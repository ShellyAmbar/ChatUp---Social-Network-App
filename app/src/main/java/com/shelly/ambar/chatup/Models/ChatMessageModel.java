package com.shelly.ambar.chatup.Models;

public class ChatMessageModel {
    private String User_Message;
    private String from;
    private String User_Time;
    private String type;
    private String userPhoto;
    private String userExactTime;
    private String toUser;





    public ChatMessageModel(String user_Message, String from, String User_Time,String type,String userPhoto,String userExactTime,String toUser) {
        this.User_Message = user_Message;
        this.from = from;
        this.User_Time=User_Time ;
        this.type=type;
        this.userPhoto=userPhoto;
        this.userExactTime=userExactTime;
        this.toUser=toUser;

    }

    public ChatMessageModel() {
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getUserExactTime() {
        return userExactTime;
    }

    public void setUserExactTime(String userExactTime) {
        this.userExactTime = userExactTime;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getUser_Message() {
        return User_Message;
    }

    public void setUser_Message(String user_Message) {
        User_Message = user_Message;
    }



    public String getUser_Time() {
        return User_Time;
    }

    public void setUser_Time(String user_Time) {
        User_Time = user_Time;
    }
}
