package com.example.deardiary;

public class CommentItem {
    String imgPath;
    String id;
    String comment;
    String date;

    public CommentItem(String imgPath, String id, String comment, String date) {
        this.imgPath = imgPath;
        this.id = id;
        this.comment = comment;
        this.date = date;
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

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}
