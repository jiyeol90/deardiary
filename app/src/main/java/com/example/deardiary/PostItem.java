package com.example.deardiary;

public class PostItem {

    String no;
    String name;
    String msg;
    String imgPath;
    String date;

    public PostItem() {
    }

    public PostItem(String no, String name, String msg, String imgPath, String date) {
        this.no = no;
        this.name = name;
        this.msg = msg;
        this.imgPath = imgPath;
        this.date = date;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

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
