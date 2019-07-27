package com.shelly.ambar.chatup.Models;

public class PostModel {
    private String postId;
    private String postImage;
    private String description;
    private String publisher;
    private String video;
    private String isvideo;
    private String publisherName;
    private String publisherImage;

    public PostModel(String PostId, String PostImage, String Description, String Publisher,String video, String isvideo
            ,String publisherName,String publisherImage) {
        this.postId = PostId;
        this.postImage = PostImage;
        this.description = Description;
        this.publisher = Publisher;
        this.isvideo=isvideo;
        this.video=video;
        this.publisherName=publisherName;
        this.publisherImage=publisherImage;
    }

    public PostModel() {
    }

    public String getPublisherImage() {
        return publisherImage;
    }

    public void setPublisherImage(String publisherImage) {
        this.publisherImage = publisherImage;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(String isvideo) {
        this.isvideo = isvideo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
