package com.example.deardiary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatRoomItem> chatRoomitemList;
    Context context;

    public ChattingRoomAdapter(Context context, ArrayList<ChatRoomItem> chatRoomitemList) {
        this.chatRoomitemList = chatRoomitemList;
        this.context = context;
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) { this.mListener = listener; }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.recycler_room_item, parent, false);

        return new RoomViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatRoomItem chatRoomitem = chatRoomitemList.get(position);

        /*
         CircleImageView profile;
        TextView userId;
        TextView lastMessage;
        TextView date;
         */

        String clickedId = chatRoomitem.getClickedId();
        if(clickedId.contains("/")) {
            clickedId = clickedId.replace("/", ", ");
        }
        ((RoomViewHolder)holder).profile.setImageDrawable(chatRoomitem.getIconDrawable());
        ((RoomViewHolder)holder).userId.setText(clickedId);
        ((RoomViewHolder)holder).lastMessage.setText(chatRoomitem.getContent());
        ((RoomViewHolder)holder).date.setText(chatRoomitem.getDate());

    }

//    @Override
//    public int getItemViewType() {
//        return ITEM_VIEW_TYPE_MAX ;
//    }

    // position 위치의 아이템 타입 리턴.
    @Override
    public int getItemViewType(int position) {
        return chatRoomitemList.get(position).getType();
    }


    //지정한 위치에 있는 데이터와 관계된 아이템의 id를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chatRoomitemList.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴
//    @Override
//    public ListViewChatItem getItem(int position) {
//        return listViewChatItemList.get(position);
//    }

    public void addItem(Drawable iconDrawable, HashMap<String, String> friendsMap, String contentType, String content, String date, String clickedId, String roomId) {
        ChatRoomItem item = new ChatRoomItem(iconDrawable, friendsMap, contentType, content, date, roomId) ;
        item.setClickedId(clickedId);

//        String friend = "";
//        for(String key : friendMap.keySet()){ //key : value => 상대방 아이디 : 프로필URI
//
//            friend += key + "/";
//            /*
//            친구 프로필 처리 작성 -> 1명일때 , 그 이상일 경우 처리할 것.
//             */
//        }
//        friend = friend.substring(0, friend.length()-1);


        if(contentType.equals("img")) {
            item.setContent("사진");
        } else {
            item.setContent(content);
        }

        //상대방 프로필 사진, 아이디, 메시지 내용, 날짜가 표시된다.
        item.setIconDrawable(iconDrawable);
        item.setClickedId(clickedId);
        item.setDate(date);

        chatRoomitemList.add(item) ;
    }

    // 상대방의 이미지 메시지
    public class RoomViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView userId;
        TextView lastMessage;
        TextView date;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.chatroom_iv_profile);
            userId = itemView.findViewById(R.id.chatroom_tv_userId);
            lastMessage = itemView.findViewById(R.id.chatroom_tv_message);
            date = itemView.findViewById(R.id.chatroom_tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //이벤트가 발생한 아이템의 위치를 알아내기 위한 메소드
                    int pos = getAdapterPosition();

                    /*
                     * notifyDataSetChanged()에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서
                     * 뷰홀더가 참조하는 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는
                     * NO_POSITION을 리턴하기 때문에 리턴값이 NO_POSTION인지 검사해줘야 한다.
                     * */
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }

                }
            });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition() ;
//                    if (pos != RecyclerView.NO_POSITION) {
//                        Toast.makeText(context.getApplicationContext(), (pos + 1) + "번째 채팅룸",Toast.LENGTH_SHORT).show();
//                        HashMap<String, String> friendMap = chatRoomitemList.get(pos).getFriendMap();
//                        String friend = "";
//                        if(friendMap.size() > 1) {
//                            for (String key : friendMap.keySet()) { //key : value => 상대방 아이디 : 프로필URI
//                                friend += key;
//                            }
//                        } else {
//                            for (String key : friendMap.keySet()) { //key : value => 상대방 아이디 : 프로필URI
//                                friend += key;
//                            }
//                        }
//
//                        UserInfo.getInstance().setClickedId(friend);
//                        Intent chatIntent = new Intent(context.getApplicationContext(), MyChattingActivity.class);
//                        context.getApplicationContext().startActivity(chatIntent);
//                    }
//                }
//            });



        }
    }

}
