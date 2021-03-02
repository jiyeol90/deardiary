package com.example.deardiary;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoom {

    private int id; // 룸 ID
    private List <ChatUser>userList; // 방 안에 참여한 유저들의 리스트
    private ChatUser roomOwner; // 방장
    private String roomName; // 방 이름


    public ChatRoom(int roomId) { // 아무도 없는 방을 생성할 때
        this.id = roomId;
        userList = new ArrayList<ChatUser>();
    }

    public ChatRoom(ChatUser user) { // 유저가 방을 만들때
        userList = new ArrayList<ChatUser>();
        user.enterRoom(this);
        userList.add(user); // 유저를 추가시킨 후
        this.roomOwner = user; // 방장을 유저로 만든다.
    }

    public ChatRoom(List<ChatUser> users) { // 유저 리스트가 방을 생성할
        this.userList = users; // 유저리스트 복사

        // 룸 입장
        for(ChatUser user : users){
            user.enterRoom(this);
        }

        //Todo 친구 리스트로 초대할 때 리스트를 만든 해당 유저가 방장으로 설정 되게 수정
        this.roomOwner = userList.get(0); // 첫번째 유저를 방장으로 설정
    }

    public void enterUser(ChatUser user) {
        //user.enterRoom(this);//상호 호출을 하므로 stackoverflow가 발생
        userList.add(user);
    }

    public void enterUser(List<ChatUser> users) {
        for(ChatUser chatUser : users){
            chatUser.enterRoom(this);
        }
        userList.addAll(users);
    }

    /**
     * 해당 유저를 방에서 내보냄
     * @param user 내보낼 유저
     */
    public void exitUser(ChatUser user) {
        user.exitRoom(this);
        userList.remove(user); // 해당 유저를 방에서 내보냄

        if (userList.size() < 1) { // 모든 인원이 다 방을 나갔다면
            RoomManager.removeRoom(this); // 이 방을 제거한다.
            return;
        }

        if(this.roomName.equals(user.getUserId())) { //나간 유저가 방장일 경우
            this.roomOwner = userList.get(0); // 리스트의 첫번째 유저가 방장이 된다.
        }

        if (userList.size() < 2) { // 방에 남은 인원이 1명 이하라면
            this.roomOwner = userList.get(0); // 리스트의 첫번째 유저가 방장이 된다.
            return;
        }
    }

    /**
     * 해당 룸의 유저를 다 퇴장시키고 삭제함
     */
    public void close() {
        for (ChatUser user : userList) {
            user.exitRoom(this);
        }
        this.userList.clear();
        this.userList = null;
    }

    // 게임 로직

    /**
     * 해당 byte 배열을 방의 모든 유저에게 전송
     * @param data 보낼 data
     */
    public void broadcast(String data) {
        for (ChatUser user : userList) { // 방에 속한 유저의 수만큼 반복
            // 각 유저에게 데이터를 전송하는 메서드 호출~
            // ex) user.SendData(data);

            //user.getSock().getOutputStream().write(data); // 이런식으로 바이트배열을 보낸다.
            //현재 내가 방에 입장해 있지 않은경우 참여했던 방에 부재중 메시지로 표시한다.
  //          HashMap<ChatRoom, Integer> stackMessageInTheRoom = user.getStackMessageInTheRoom();
            //유저가 현재 참여하고 있는 방일 경우 쌓여있던 메시지가 없어진다.
            //if(user.getRoom().equals(this)) {
            if(this.equals(user.getRoom())) {
                user.getPw().println(data);
                user.getPw().flush();

//                if(user.getStackMessageInTheRoom().size() > 0) { //get으로 얻어올때 NPE가 발생할 수 있다.
//                    if (user.getStackMessageInTheRoom().get(this) != null) {
//                        stackMessageInTheRoom = user.getStackMessageInTheRoom();
//                        stackMessageInTheRoom.replace(this, user.getStackMessage());
//                        user.setStackMessageInTheRoom(stackMessageInTheRoom);
//                    }
//                } else { //ChatUser에 stackMessageInTheRoom을 추가해준다.
//                    stackMessageInTheRoom.put(this, user.getStackMessage());
//                    user.setStackMessageInTheRoom(stackMessageInTheRoom);
//                }
            }
//            else if(user.getRoom() != null){//유저가 참여했던 방일 경우 (exit으로 방을 나오지 않고 다른방에 참여중이다.)
//                //유저가 참여하는 방이 없고 exit으로 나가있는 상태라면 보내지 않는다.
//                //유저에 현재의 방에 대한 정보가 없으면 데이터를 넣어준다.
//                int stackMessage = user.getStackMessage();
//                //해쉬맵에 현재 룸에 대한 정보가 없는경우
//                if(user.getStackMessageInTheRoom().get(this) == null) {
//                    //기본 stackMessage값을 현재 룸과 함께 넣는다.
//                    user.getStackMessageInTheRoom().put(this, stackMessage);
//                } else {
//                    //해쉬맵에 현재 룸에 대한 정보가 있는경우
//                    stackMessage = user.getStackMessageInTheRoom().get(this);
//                }
//                stackMessageInTheRoom = user.getStackMessageInTheRoom();
//                stackMessageInTheRoom.replace(this, stackMessage + 1);
//                user.setStackMessageInTheRoom(stackMessageInTheRoom);
//                user.getPw().println("[["+ stackMessage +" 개의 메시지가 왔습니다.]]" + data);
//                user.getPw().flush();
                //user.setStackMessage(stackMessage + 1);
//          }
        }
    }

    public void setOwner(ChatUser chatUser) {
        this.roomOwner = chatUser; // 특정 사용자를 방장으로 변경한다.
    }

    public void setRoomName(String name) { // 방 이름을 설정
        this.roomName = name;
    }

    public ChatUser getUserByNickName(String userId) { // 닉네임을 통해서 방에 속한 유저를 리턴함

        for (ChatUser user : userList) {
            if (user.getUserId().equals(userId)) {
                return user; // 유저를 찾았다면
            }
        }
        return null; // 찾는 유저가 없다면
    }

    public ChatUser getUser(ChatRoom gameUser) { // GameUser 객체로 get

        int idx = userList.indexOf(gameUser);

        // 유저가 존재한다면(gameUser의 equals로 비교)
        if(idx > 0){
            return userList.get(idx);
        }
        else{
            // 유저가 없다면
            return null;
        }
    }

    public String getRoomName() { // 방 이름을 가져옴
        return roomName;
    }

    public int getUserSize() { // 유저의 수를 리턴
        return userList.size();
    }

    public ChatUser getOwner() { // 방장을 리턴
        return roomOwner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ChatUser> getUserList() {
        return userList;
    }

    public void setUserList(List<ChatUser> userList) {
        this.userList = userList;
    }

    public ChatUser getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(ChatUser roomOwner) {
        this.roomOwner = roomOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoom chatRoom = (ChatRoom) o;

        return id == chatRoom.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}