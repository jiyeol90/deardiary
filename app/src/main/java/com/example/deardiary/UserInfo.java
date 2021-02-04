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
    private String userName;
    //사용자가 나중에 입력해줄 값들은 초기화 해준다.
    private String userText = "";
    private String userProfile = "";


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
}
