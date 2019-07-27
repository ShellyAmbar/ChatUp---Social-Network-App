package com.shelly.ambar.chatup.Models;

public class NotificationModel {
    private String userId;
    private String postId;
    private String text;
    private String isPost;
    private String isvideo;

    public NotificationModel(String userId, String postId, String text, String isPost,String isvideo) {
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.isPost = isPost;
        this.isvideo=isvideo;
    }

    public NotificationModel() {
    }

    public String getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(String isvideo) {
        this.isvideo = isvideo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }
}
