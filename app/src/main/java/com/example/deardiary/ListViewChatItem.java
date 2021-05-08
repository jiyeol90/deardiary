package com.example.deardiary;

import android.graphics.drawable.Drawable;

// 예제들 중 ChatVO 혹은 ChatItem 이라는 클래스와 같은 멤버 변수들이 담겨있다.
// 채팅 화면에서 필요한 모든 데이터를 다루는 클래스
public class ListViewChatItem {
    // 아이템 타입을 구분하기 위한 type 변수.
    private int type ;

    private String message = "";
    private String userId;
    private String imageURI;

    private Drawable iconDrawable ;
    private String userProfileURI;
    private String date;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserProfileURI() { return userProfileURI; }

    public void setUserProfileURI(String userProfileURI) { this.userProfileURI = userProfileURI; }
}