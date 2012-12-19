package net.relatedwork.shared.dto;

import com.gwtplatform.dispatch.shared.Result;

/**
 * A data transfer object for one single comment of any type.
 *
 * @author Rene Pickhardt
 * @author Xinruo Sun <xiaoruoruo@gmail.com>
 */
public class Comments implements Result {

    public enum CommentType {
        Question("Question"),
        Review("Review"),
        Summary("Summary"),
        GeneralDiscussion("General Discussion"),;

        private final String title;
        private CommentType(String title) {
            this.title = title;
        }
        @Override
        public String toString() {
            return title;
        }
    }

	Author author;
	String comment;
	String date;
	Integer voting = 0;
    Comments target;
    CommentType type;
	
	public Comments() {
		// TODO Auto-generated constructor stub
	}

	public Comments(Author author, String comment){
		this.author=author;
		this.comment=comment;
	}
	
	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the voting
	 */
	public Integer getVoting() {
		return voting;
	}

	/**
	 * @param voting the voting to set
	 */
	public void setVoting(Integer voting) {
		this.voting = voting;
	}

    public Comments getTarget() {
        return target;
    }

    public void setTarget(Comments target) {
        this.target = target;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }
}
