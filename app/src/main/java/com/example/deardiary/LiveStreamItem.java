package com.example.deardiary;

public class LiveStreamItem {

    String preview;
    String author; //broadcasting 하는 user ID
    String title; // //스트리밍 시작 시간을 넣어준다.
    String streamingTime; //스트리밍 시작 시간 -> UNIX TimeStamp (변환을 해줘야 한다.)
    String resourceUri;
    String length; //스트리밍 방송시간
    String type; // 라이브중 : live , 방송종료 : archived

    public LiveStreamItem(String preview, String author, String title, String streamingTime, String resourceUri, String length, String type) {
        this.preview = preview;
        this.author = author;
        this.title = title;
        this.streamingTime = streamingTime;
        this.resourceUri = resourceUri;
        this.length = length;
        this.type = type;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreamingTime() {
        return streamingTime;
    }

    public void setStreamingTime(String streamingTime) {
        this.streamingTime = streamingTime;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LiveStreamItem{" +
                "preview='" + preview + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", streamingTime='" + streamingTime + '\'' +
                ", resourceUri='" + resourceUri + '\'' +
                ", length='" + length + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
