package com.example.deardiary;

public class GridListItem {

    String date;
    String imgPath;

    public GridListItem(String imgPath, String date) {

        this.date = date;
        this.imgPath = imgPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
