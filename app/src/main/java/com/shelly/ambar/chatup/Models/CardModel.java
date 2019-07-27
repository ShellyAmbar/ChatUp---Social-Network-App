package com.shelly.ambar.chatup.Models;

public class CardModel {
    private String title;
    private String description;
    private int image;

    public CardModel(String title,String description, int image) {
        this.title = title;
        this.description=description;
        this.image=image;
    }

    public CardModel() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
