package com.example.deardiary;

public class UserInfo {
    private static UserInfo userInfo;

    private String index;
    private String id;
    private String userName;


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

}
