package com.example.deardiary;

//포스팅을 연속적으로 보여줄때의 데이터값들
public class PostItem {

    String no;
    String userId;
    String userProfile;
    String imgPath;
    String date;

    public PostItem() {
    }

    public PostItem(String no, String userId, String userProfile, String imgPath, String date) {
        this.no = no;
        this.userId = userId;
        this.userProfile = userProfile;
        this.imgPath = imgPath;
        this.date = date;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfile() { return userProfile; }

    public void setUserProfile(String userProfile) { this.userProfile = userProfile; }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
