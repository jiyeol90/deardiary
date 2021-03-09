package com.example.deardiary;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

// 예제들 중 ChatVO 혹은 ChatItem 이라는 클래스와 같은 멤버 변수들이 담겨있다.
// 채팅 화면에서 필요한 모든 데이터를 다루는 클래스
public class ChatRoomItem {
    // 아이템 타입을 구분하기 위한 type 변수.
    private int type ;

    private String contentType;
    private String content;
    private String clickedId;
    private String imageURI;
    private HashMap<String, String> friendMap;

    private Drawable iconDrawable ;
    private String date;
    //iconDrawable, friendMap, contentType, content, date
    public ChatRoomItem(Drawable iconDrawable, HashMap<String, String> friendMap, String contentType, String content, String date) {
        this.iconDrawable = iconDrawable;
        this.friendMap = friendMap;
        this.contentType = contentType;
        this.content = content;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getClickedId() {
        return clickedId;
    }

    public void setClickedId(String clickedId) {
        this.clickedId = clickedId;
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

    public HashMap<String, String> getFriendMap() {
        return friendMap;
    }

    public void setFriendMap(HashMap<String, String> friendMap) {
        this.friendMap = friendMap;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}