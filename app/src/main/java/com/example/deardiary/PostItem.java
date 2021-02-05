package com.example.deardiary;

//포스팅을 연속적으로 보여줄때의 데이터값들
public class PostItem {

    String no;
    String userId;
   //String msg;
    String imgPath;
    String date;

    public PostItem() {
    }

    public PostItem(String no, String userId, String imgPath, String date) {
        this.no = no;
        this.userId = userId;
        //this.msg = msg;
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

//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }

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
