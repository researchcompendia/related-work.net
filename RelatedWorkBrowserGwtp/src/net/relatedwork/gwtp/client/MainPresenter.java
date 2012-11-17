package net.relatedwork.gwtp.client;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import net.relatedwork.gwtp.client.place.NameTokens;
import net.relatedwork.gwtp.client.staticpresenter.MyTestEvent;
import net.relatedwork.gwtp.client.staticpresenter.MyTestEvent.MyTestHandler;

import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class MainPresenter extends
		Presenter<MainPresenter.MyView, MainPresenter.MyProxy> {

	@ContentSlot
	public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();
	
	private final MyTestHandler myHandler = new MyTestHandler(){
		@Override
		public void onMyTest(MyTestEvent event) {
			getView().getRwHeader().clear();
			Label l = new Label(event.getEventString());
			getView().getRwHeader().add(l);
		} };
	
	public interface MyView extends View {
		public HTMLPanel getRwHeader() ;
		public void setRwHeader(HTMLPanel rwHeader);
		public HTMLPanel getRwContent() ;
		public void setRwContent(HTMLPanel rwContent) ;
		public HTMLPanel getRwSidebar() ;
		public void setRwSidebar(HTMLPanel rwSidebar);
		public HTMLPanel getRwFooter();
		public void setRwFooter(HTMLPanel rwFooter);
		
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.main)
	public interface MyProxy extends ProxyPlace<MainPresenter> {
	}

	@Inject
	public MainPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		registerHandler(getEventBus().addHandler(MyTestEvent.getType(), myHandler));
	}
}
