package com.example.deardiary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatRoomItem> chatRoomitemList;
    private Context context;

    public ChattingRoomAdapter(Context context, ArrayList<ChatRoomItem> chatRoomitemList) {
        this.chatRoomitemList = chatRoomitemList;
        this.context = context;
    }

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


        ((RoomViewHolder)holder).profile.setImageDrawable(chatRoomitem.getIconDrawable());
        ((RoomViewHolder)holder).userId.setText(chatRoomitem.getUserId());
        ((RoomViewHolder)holder).lastMessage.setText(chatRoomitem.getMessage());
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

    public void addItem(Drawable iconDrawable, String userId, String message, String date) {
        ChatRoomItem item = new ChatRoomItem() ;

        //상대방 프로필 사진, 아이디, 메시지 내용, 날짜가 표시된다.
        item.setIconDrawable(iconDrawable); ;
        item.setUserId(userId);
        item.setMessage(message);
        item.setDate(date); ;

        chatRoomitemList.add(item) ;
    }

    // 상대방의 이미지 메시지
    static class RoomViewHolder extends RecyclerView.ViewHolder{

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
        }
    }

}
