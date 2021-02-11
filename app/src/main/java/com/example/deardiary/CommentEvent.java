package com.example.deardiary;

//댓글 업로드시 이벤트 객체
public class CommentEvent {
    String postId;
    boolean update;

    public CommentEvent(String postId, boolean update) {
        this.postId = postId;
        this.update = update;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
