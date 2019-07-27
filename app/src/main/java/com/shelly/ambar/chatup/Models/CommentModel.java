package com.shelly.ambar.chatup.Models;

public class CommentModel {
    private String comment;
    private String publisher;
    private String commentId;


    public CommentModel(String comment, String publisher, String commentId) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentId=commentId;
    }

    public CommentModel() {
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
