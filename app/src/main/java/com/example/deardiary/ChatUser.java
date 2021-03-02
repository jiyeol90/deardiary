package com.example.deardiary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ChatUser {

    private Socket sock;
    private String userId;
    private BufferedReader br;
    private PrintWriter pw;

    //부재중 메시지 개수 표시
    private int stackMessage = 1;
    //유저가 참여한 방과 쌓인 메시지의 수를 담아둔다.
    private HashMap<ChatRoom, Integer> stackMessageInTheRoom = new HashMap<>();

    //유저가 속한 룸. -> 유저가 속한 룸은 한개 이상일 수 있나?
    private ChatRoom room;


    public ChatUser() {} // 아무런 정보가 없는 깡통 유저를 만들 때

    /**
     * 유저 생성
     * @param userId 유저아이디
     */
    public ChatUser(String userId) { // 닉네임 정보만 가지고 생성
        System.out.println(userId + " 이 생성되었습니다.");
        this.userId = userId;
    }

    /**
     * 방에 입장시킴
     * @param room  입장할 방
     */
    public void enterRoom(ChatRoom room) {
        room.enterUser(this); // 룸에 입장시킨 후
        this.room = room; //
        // 변경한다.(중요)
    }

    /**
     * 방에서 퇴장
     * @param room 퇴장할 방
     */
    public void exitRoom(ChatRoom room){
        //현재 참여한 방이 없다.
        this.room = null;
        //방에 대한 메시지를 지워준다.
        this.stackMessageInTheRoom.remove(room);
        // 퇴장처리(화면에 메세지를 준다는 등)
        // ...
    }



    public ChatRoom getRoom() {
        return room;
    }

    public void setRoom(ChatRoom room) {
        this.room = room;
    }

    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
        try {
            this.br = new BufferedReader(new InputStreamReader(sock.getInputStream(),"utf-8"));
            this.pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"utf-8"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getBr() { return br; }

    public void setBr(BufferedReader br) { this.br = br; }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public int getStackMessage() { return stackMessage; }

    public void setStackMessage(int stackMessage) { this.stackMessage = stackMessage; }


    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public HashMap<ChatRoom, Integer> getStackMessageInTheRoom() { return stackMessageInTheRoom; }

    public void setStackMessageInTheRoom(HashMap<ChatRoom, Integer> stackMessageInTheRoom) { this.stackMessageInTheRoom = stackMessageInTheRoom; }

    /*
            equals와 hashCode를 override 해줘야, 동일유저를 비교할 수 있다
            비교할 때 -> gameUser 간 equals 비교, list에서 find 등
         */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatUser chatUser = (ChatUser) o;

        return userId == chatUser.userId;
    }

    @Override
    public int hashCode() {
        //userId는 유일한 값이므로
        return userId.hashCode();
    }


}















