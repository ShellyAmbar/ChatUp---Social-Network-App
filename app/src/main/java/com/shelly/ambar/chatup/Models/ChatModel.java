package com.shelly.ambar.chatup.Models;

public class ChatModel {
    private String userName;
    private String lastMassage;
    private String userPhoto;
    private String userId;
    private String chatId;
    private String enterTime;

    public ChatModel(String userName, String lastMassage, String userPhoto,String userId,String chatId,String enterTime) {
        this.userName = userName;
        this.lastMassage = lastMassage;
        this.userPhoto = userPhoto;
        this.userId=userId;
        this.chatId=chatId;
        this.enterTime=enterTime;
    }

    public ChatModel() {
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMassage() {
        return lastMassage;
    }

    public void setLastMassage(String lastMassage) {
        this.lastMassage = lastMassage;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
