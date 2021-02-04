package com.example.deardiary;

public class GridListItem {

    String id;
    String date;
    String imgPath;

    public GridListItem(String id, String imgPath, String date) {
        this.id = id;
        this.date = date;
        this.imgPath = imgPath;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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
