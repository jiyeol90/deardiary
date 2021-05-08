package com.example.deardiary;

/*
* 사용자의 정보가 필요할때마다 http통신을 통해 DB값을 가져오는 것보다
* Singleton 패턴의 User 클래스를 만들어두어 필요할 때마다 객체에 접근해
* 정보를 가져오기 위한 클래스
* */

public class UserInfo {
    private static UserInfo userInfo;

    private String index;
    private String id;
    private String roomId = "";
    private String userName;
    private String clickedId = "";
    private String scrollPosition = "-1"; //포스팅 보기에서 돌아올때의 위치

    //사용자가 나중에 입력해줄 값들은 초기화 해준다.
    private String userText = "";
    private String userProfile = "default"; //초기값으로 default를 설정해 준다.
    private boolean isTokenSaved = false;
    private String token;


    private UserInfo() {

    }

    public static UserInfo getInstance() {
        if(userInfo == null) {
            userInfo = new UserInfo();
        }
        return userInfo;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserText() { return userText; }

    public void setUserText(String userText) { this.userText = userText; }

    public String getUserProfile() { return userProfile; }

    public void setUserProfile(String userProfile) { this.userProfile = userProfile; }

    public String getClickedId() { return clickedId; }

    public void setClickedId(String clickedId) { this.clickedId = clickedId; }

    public boolean isTokenSaved() { return isTokenSaved; }

    public void setTokenSaved(boolean tokenSaved) { isTokenSaved = tokenSaved; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(String scrollPosition) {
        this.scrollPosition = scrollPosition;
    }
}
