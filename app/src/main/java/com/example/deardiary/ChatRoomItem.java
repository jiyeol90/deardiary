package com.example.deardiary;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

// 예제들 중 ChatVO 혹은 ChatItem 이라는 클래스와 같은 멤버 변수들이 담겨있다.
// 채팅 화면에서 필요한 모든 데이터를 다루는 클래스
public class ChatRoomItem {
    // 아이템 타입을 구분하기 위한 type 변수.
    private int type ;

    private String roomId;
    private String contentType;
    private String content;
    private String clickedId;
    private String imageURI;
    private HashMap<String, String> friendsMap;
    private String notiCnt; //부재중 메시지가 올때 채팅방 리스트에 띄어줄 메시지

    private Drawable iconDrawable ;
    private String date;
    //iconDrawable, friendMap, contentType, content, date
    public ChatRoomItem(Drawable iconDrawable, HashMap<String, String> friendsMap, String contentType, String content, String date, String roomId, String notiCnt) {
        this.iconDrawable = iconDrawable;
        this.friendsMap = friendsMap;
        this.contentType = contentType;
        this.content = content;
        this.date = date;
        this.roomId = roomId;
        this.notiCnt = notiCnt;
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

    public HashMap<String, String> getFriendsMap() {
        return friendsMap;
    }

    public void setFriendsMap(HashMap<String, String> friendsMap) {
        this.friendsMap = friendsMap;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getNotiCnt() { return notiCnt; }

    public void setNotiCnt(String notiCnt) { this.notiCnt = notiCnt; }
}