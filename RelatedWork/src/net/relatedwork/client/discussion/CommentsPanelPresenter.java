package net.relatedwork.client.discussion;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Proxy;
import net.relatedwork.shared.dto.Comments;

import java.util.List;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.transform;
import static net.relatedwork.shared.dto.Comments.CommentType;

/**
 * The Presenter for {@link CommentsPanelView}. It holds a list of comments under one paper/author.
 * Handles DiscussionsReloadedEvent.
 *
 * @author Xinruo Sun <xiaoruoruo@gmail.com>
 */
public class CommentsPanelPresenter
        extends Presenter<CommentsPanelPresenter.MyView, CommentsPanelPresenter.MyProxy> {

    private static final List<String> COMMENT_TAB_TITLES =
            transform(ImmutableList.copyOf(CommentType.values()), toStringFunction());
    private static final int FALLBACK_COMMENT_TAB = CommentType.GeneralDiscussion.ordinal();

    public interface MyView extends View {
        void setSelectionHandler(SelectionHandler<Integer> handler);
        void addPost(int tab, Widget widget);
        void addReply(int tab, Widget widget);
        void initTabs(List<String> tabTitles);
        void setPostCount(int tab, int count);
        void switchTab(int tab);
        void resetReply(int tab);
    }

    @ProxyCodeSplit
    public interface MyProxy extends Proxy<CommentsPanelPresenter> {
    }

    private Provider<CommentBoxPresenter> commentBoxPresenterProvider;

    /** Current selected comment box presenter in each tab */
    private CommentBoxPresenter[] selectedCommentInTabs;

    /** How many posts are in each tab */
    private int[] postCounts;

    /** All comments related to the current target (paper/author) */
    private List<Comments> comments;

    private String targetUri;

    @Inject
    public CommentsPanelPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                  Provider<CommentBoxPresenter> commentBoxPresenterProvider) {
        super(eventBus, view, proxy);
        this.commentBoxPresenterProvider = commentBoxPresenterProvider;
    }

    @Override
    protected void onBind() {
        registerHandler(getEventBus().addHandler(DiscussionsReloadedEvent.getType(), new DiscussionsReloadedEvent.DiscussionsReloadedHandler() {
            @Override
            public void onDiscussionsReloaded(DiscussionsReloadedEvent event) {
                setComments(event.getComments(), event.getTargetUri());
            }
        }));

        super.onBind();
    }

    public void setComments(List<Comments> comments, String targetUri) {
        selectedCommentInTabs = new CommentBoxPresenter[CommentType.values().length];
        postCounts = new int[CommentType.values().length];

        this.comments = Lists.newArrayList(comments);
        this.targetUri = targetUri;

        getView().initTabs(COMMENT_TAB_TITLES);
        for (int i = 0; i < COMMENT_TAB_TITLES.size(); i++){
            closeReplyList(i);
        }

        putExistingPosts(filterCommentsByTarget(targetUri));

        putNewPostBoxes();

        getView().switchTab(0);
    }

    private void putExistingPosts(Iterable<Comments> comments) {
        for (Comments c : comments) {
            CommentBoxPresenter commentBoxPresenter = commentBoxPresenterProvider.get();
            commentBoxPresenter.setExistingComment(c);

            // Put it in the correct tab.
            int tab = FALLBACK_COMMENT_TAB;
            if (c.getType() != null) {
                tab = c.getType().ordinal();
            }
            getView().addPost(tab, commentBoxPresenter.getWidget());
            postCounts[tab] ++;

            commentBoxPresenter.setExpandHandler(
                    new ExpandCommentHandler(commentBoxPresenter, tab));
        }

        for (int i = 0; i < COMMENT_TAB_TITLES.size(); i++) {
            getView().setPostCount(i, postCounts[i]);
        }
    }

    private void putNewPostBoxes() {
        for (int i = 0; i < COMMENT_TAB_TITLES.size(); i++){
            putNewPostBox(i);
        }
    }

    private void putNewPostBox(final int tab) {
        CommentBoxPresenter postPresenter = commentBoxPresenterProvider.get();
        postPresenter.setNewComment(false, CommentType.values()[tab], targetUri);
        postPresenter.setSubmittedEventHandler(new CommentSubmittedEventHandler() {
            @Override
            public void success(Comments newComment) {
                putExistingPosts(ImmutableList.of(newComment));
                comments.add(newComment);
            }
        });
        getView().addPost(tab, postPresenter.getWidget());
    }

    private void showRepliesOf(Comments comment, int tab) {
        getView().resetReply(tab);
        for (Comments reply: filterCommentsByTarget(comment.getUri())) {
            putExistingReply(tab, reply);
        }
        putNewReplyBox(tab, comment);
    }

    private void putExistingReply(int tab, Comments reply) {
        CommentBoxPresenter replyPresenter = commentBoxPresenterProvider.get();
        replyPresenter.setExistingComment(reply);
        getView().addReply(tab, replyPresenter.getWidget());
    }

    private void putNewReplyBox(final int tab, final Comments post) {
        CommentBoxPresenter newReplyPresenter = commentBoxPresenterProvider.get();
        newReplyPresenter.setNewComment(true, null, post.getUri());
        newReplyPresenter.setSubmittedEventHandler(new CommentSubmittedEventHandler() {
            @Override
            public void success(Comments newComment) {
                putExistingReply(tab, newComment);
                comments.add(newComment);
            }
        });
        getView().addReply(tab, newReplyPresenter.getWidget());
    }

    private void closeReplyList(int tab) {
        getView().resetReply(tab);
        getView().addReply(tab, new Label("Click on one of the comment on the left to view its replies."));
    }

    private final class ExpandCommentHandler implements ClickHandler {
        private CommentBoxPresenter post;
        private int tab;

        public ExpandCommentHandler(CommentBoxPresenter post, int tab) {
            this.post = post;
            this.tab = tab;
        }

        @Override
        public void onClick(ClickEvent event) {
            CommentBoxPresenter newSelected = null;
            if (post != selectedCommentInTabs[tab]) {
                newSelected = post;
                post.markExpanded(true);
                showRepliesOf(post.getComment(), tab);
            }
            if (selectedCommentInTabs[tab] != null) {
                selectedCommentInTabs[tab].markExpanded(false);
                if (post == selectedCommentInTabs[tab]) {
                    closeReplyList(tab);
                }
            }
            selectedCommentInTabs[tab] = newSelected;
        }
    }

    private Iterable<Comments> filterCommentsByTarget(final String targetUri) {
        return filter(comments, new Predicate<Comments>() {
            @Override
            public boolean apply(Comments c) {
                return c.getTargetUri().equals(targetUri);
            }
        });
    }

    @Override
    protected void revealInParent() {
        throw new UnsupportedOperationException();
    }
}
