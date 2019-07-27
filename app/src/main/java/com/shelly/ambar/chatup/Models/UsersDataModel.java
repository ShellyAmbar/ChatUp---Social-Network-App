package com.shelly.ambar.chatup.Models;

public class UsersDataModel {

    private String Image;
    private String UserName;
    private String UserStatus;
    private String Thumb_Image;
    private String Id;
    private String Email;
    private String BirthDay;
    private String TotalFriends;
    private String UserAge;
    private String UserCity;
    private String MatchCity;
    private String UserGender;
    private String InterestedIn;
    private String FromAge;
    private String ToAge;
    private String MyEducation;
    private String MatchEducation;




    UsersDataModel(){

    }

    public UsersDataModel(String image, String userName, String userStatus
            , String ThumbImage, String id, String Birthday
            , String Email,String totalFriends,String UserAge,
                          String UserCity,  String UserGender,String InterestedIn ,String FromAge
            ,String ToAge,String MyEducation,String MatchEducation, String MatchCity) {
        this.Image = image;
        this.UserName = userName;
        this.UserStatus = userStatus;
        this.Thumb_Image=ThumbImage;
        this.Id=id;
        this.Email=Email;
        this.BirthDay=Birthday;
        this.TotalFriends=totalFriends;
        this.UserAge=UserAge;
        this.UserCity=UserCity;
        this.UserGender=UserGender;
        this.InterestedIn=InterestedIn;
        this.FromAge=FromAge;
        this.ToAge=ToAge;
        this.MyEducation=MyEducation;
        this.MatchEducation=MatchEducation;
        this.MatchCity=MatchCity;
    }

    public String getMatchCity() {
        return MatchCity;
    }

    public void setMatchCity(String matchCity) {
        MatchCity = matchCity;
    }

    public String getUserAge() {
        return UserAge;
    }

    public void setUserAge(String userAge) {
        UserAge = userAge;
    }

    public String getFromAge() {
        return FromAge;
    }

    public void setFromAge(String fromAge) {
        FromAge = fromAge;
    }

    public String getToAge() {
        return ToAge;
    }

    public void setToAge(String toAge) {
        ToAge = toAge;
    }

    public String getMyEducation() {
        return MyEducation;
    }

    public void setMyEducation(String myEducation) {
        MyEducation = myEducation;
    }

    public String getMatchEducation() {
        return MatchEducation;
    }

    public void setMatchEducation(String matchEducation) {
        MatchEducation = matchEducation;
    }



    public String getInterestedIn() {
        return InterestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        InterestedIn = interestedIn;
    }


    public String getUserCity() {
        return UserCity;
    }

    public void setUserCity(String userCity) {
        UserCity = userCity;
    }

    public String getUserGender() {
        return UserGender;
    }

    public void setUserGender(String userGender) {
        UserGender = userGender;
    }

    public String getTotalFriends() {
        return TotalFriends;
    }

    public void setTotalFriends(String totalFriends) {
        TotalFriends = totalFriends;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(String birthDay) {
        BirthDay = birthDay;
    }




    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }



    public String getThumb_Image() {
        return Thumb_Image;
    }

    public void setThumb_Image(String thumb_Image) {
        Thumb_Image = thumb_Image;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }





}
