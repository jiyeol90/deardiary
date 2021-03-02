package com.example.deardiary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {

    private static List<ChatRoom> roomList; // 방의 리스트
    private static AtomicInteger atomicInteger;


    static {
        roomList = new ArrayList<ChatRoom>();
        atomicInteger = new AtomicInteger();
    }

    public RoomManager() {

    }

    /**
     * 빈 룸을 생성
     * @return GameRoom
     */
    public static ChatRoom createRoom() { // 룸을 새로 생성(빈 방)
        int roomId = atomicInteger.incrementAndGet();// room id 채번
        ChatRoom room = new ChatRoom(roomId);
        roomList.add(room);
        System.out.println("Room Created!");
        return room;
    }

    /**
     * 방을 생성함과 동시에 방장을 만들어줌
     * @param owner 방장
     * @return GameRoom
     */
    public static ChatRoom createRoom(ChatUser owner) { // 유저가 방을 생성할 때 사용(유저가 방장으로 들어감)
        int roomId = atomicInteger.incrementAndGet();// room id 채번

        ChatRoom room = new ChatRoom(roomId);
        room.enterUser(owner);
        room.setOwner(owner);

        roomList.add(room);
        System.out.println("Room Created!");
        return room;
    }

    /**
     * 유저 리스트로 방을 생성
     * @param users 입장시킬 유저 리스트
     * @return GameRoom
     */
    public static ChatRoom createRoom(List users) {
        int roomId = atomicInteger.incrementAndGet();// room id 채번

        ChatRoom room = new ChatRoom(roomId);
        room.enterUser(users);

        roomList.add(room);
        System.out.println("Room Created!");
        return room;
    }

    public static ChatRoom getRoom(ChatRoom chatRoom){

        int idx = roomList.indexOf(chatRoom);

        if(idx >= 0){ //기존에 idx > 0 라는 코드는 잘못된 것 같다.
            return  roomList.get(idx);
        }
        else{
            return null;
        }
    }

    public static ChatRoom getRoomByIndex(int index){

        ChatRoom room = roomList.get(index);

        if(room != null){
            return  room;
        }
        else{
            return null;
        }
    }

    public static String getRoomList() {
        String list = "";

        if(roomList.size() > 0) {
            for(int i = 0; i < roomList.size(); i++) {
                list += "["+(i+1)+" 번째방 이름] :" + roomList.get(i).getRoomName() +
                        " ["+(i+1)+" 번째방 id] : " + roomList.get(i).getId() + "\r\n";
            }
        } else {
            list = "현재 참여 가능한 방이 없습니다.\r\n";
        }
        return list;
    }

    /**
     * 전달받은 룸을 제거
     * @param room 제거할 룸
     */
    public static void removeRoom(ChatRoom room) {
        room.close();
        roomList.remove(room); // 전달받은 룸을 제거한다.
        System.out.println("Room Deleted!");
    }

    /**
     * 방의 현재 크기를 리턴
     * @return 현재 size
     */
    public static int roomCount() {
        return roomList.size();
    }
}