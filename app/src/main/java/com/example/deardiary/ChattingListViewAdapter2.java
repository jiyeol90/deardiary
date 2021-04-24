package com.example.deardiary;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingListViewAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TEXT_TYPE_OTHER = 0 ;
    private static final int ITEM_VIEW_TEXT_TYPE_MINE = 1 ;
    private static final int ITEM_VIEW_IMAGE_TYPE_OTHER = 2 ;
    private static final int ITEM_VIEW_IMAGE_TYPE_MINE = 3 ;
    private static final int ITEM_VIEW_TYPE_INOUT = 4 ;

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnMyItemClickListener myListener = null;

    private ArrayList<ListViewChatItem> listViewChatItemList = new ArrayList<ListViewChatItem>() ;
    private Context context;

    public ChattingListViewAdapter2(Context context) {
        this.context = context;
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(ChattingListViewAdapter2.LeftImageViewHolder holder, View v, int position) ;
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnMyItemClickListener {
        void onItemClick(ChattingListViewAdapter2.RightImageViewHolder holder, View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnMyItemClickListener listener) {
        this.myListener = listener ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch(viewType) {
            case ITEM_VIEW_TEXT_TYPE_OTHER :
                view = inflater.inflate(R.layout.other_chat_message, parent, false);
                return new LeftTextViewHolder(view);

            case ITEM_VIEW_TEXT_TYPE_MINE:
                view = inflater.inflate(R.layout.my_chat_message, parent, false);
                return new RightTextViewHolder(view);

            case ITEM_VIEW_IMAGE_TYPE_OTHER:
                view = inflater.inflate(R.layout.other_image_chat_message, parent, false);
                return new LeftImageViewHolder(view);

            case ITEM_VIEW_IMAGE_TYPE_MINE:
                view = inflater.inflate(R.layout.my_image_chat_message, parent, false);
                return new RightImageViewHolder(view);

            default: // ITEM_VIEW_TYPE_INOUT
                view = inflater.inflate(R.layout.activity_chatitem, parent, false);
                return new MiddleViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ListViewChatItem listViewItem = listViewChatItemList.get(position);
        /*
         CircleImageView profileImage = convertView.findViewById(R.id.chatmessage_iv_profile);
                    TextView userIdTextView = convertView.findViewById(R.id.chatmessage_tv_nickname);
                    TextView messageTextView = convertView.findViewById(R.id.chatmessage_tv_message);
                    TextView dateTextView = convertView.findViewById(R.id.chatmessage_tv_date);

                    profileImage.setImageDrawable(listViewItem.getIconDrawable());
                    userIdTextView.setText(listViewItem.getUserId());

                    messageTextView.setBackgroundResource(R.drawable.inbox2);
                    messageTextView.setText(listViewItem.getMessage());
                    dateTextView.setText(listViewItem.getDate());
         */
        if(holder instanceof LeftTextViewHolder) {
            //((LeftTextViewHolder)holder).profile.setImageDrawable(listViewItem.getIconDrawable());
            Glide.with(holder.itemView.getContext()) //해당 환경의 Context나 객체 입력
                    .load(listViewItem.getUserProfileURI()) //URL, URI 등등 이미지를 받아올 경로
                    .override(90,90)
                    .into(((LeftTextViewHolder) holder).profile); //받아온 이미지를 받을 공간(ex. ImageView)




            ((LeftTextViewHolder)holder).userId.setText(listViewItem.getUserId());
            ((LeftTextViewHolder)holder).message.setText(listViewItem.getMessage());
            //((LeftTextViewHolder)holder).message.setBackgroundResource(R.drawable.inbox2);
            ((LeftTextViewHolder)holder).date.setText(listViewItem.getDate());
        } else if(holder instanceof RightTextViewHolder) {
            /*
              convertView = inflater.inflate(R.layout.my_chat_message, parent, false);

                    TextView myIdTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_message);
                    TextView myDateTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_date);

                    myIdTextView.setBackgroundResource(R.drawable.outbox2);
                    myIdTextView.setText(listViewItem.getMessage());
                    myDateTextView.setText(listViewItem.getDate());
             */

            ((RightTextViewHolder)holder).message.setText(listViewItem.getMessage());
            //((RightTextViewHolder)holder).message.setBackgroundResource(R.drawable.outbox2);
            ((RightTextViewHolder)holder).date.setText(listViewItem.getDate());
        } else if(holder instanceof LeftImageViewHolder) {

            //((LeftImageViewHolder) holder).profile.setImageDrawable(listViewItem.getIconDrawable());
            Glide.with(holder.itemView.getContext()) //해당 환경의 Context나 객체 입력
                    .load(listViewItem.getUserProfileURI()) //URL, URI 등등 이미지를 받아올 경로
                    .override(90,90)
                    .into(((LeftImageViewHolder) holder).profile); //받아온 이미지를 받을 공간(ex. ImageView)

            ((LeftImageViewHolder) holder).userId.setText(listViewItem.getUserId());
            ((LeftImageViewHolder) holder).image.setImageResource(R.drawable.ic_chatting);

            Glide.with(holder.itemView.getContext()) //해당 환경의 Context나 객체 입력
                    .load(listViewItem.getImageURI()) //URL, URI 등등 이미지를 받아올 경로
                    .override(1000,600)
                    .into(((LeftImageViewHolder) holder).image); //받아온 이미지를 받을 공간(ex. ImageView)

            //((LeftImageViewHolder) holder).image.setBackgroundResource(R.drawable.inbox2);
            ((LeftImageViewHolder) holder).date.setText(listViewItem.getDate());
        } else if (holder instanceof RightImageViewHolder){

            //((RightImageViewHolder) holder).image.setBackgroundResource(R.drawable.outbox2);
            Glide.with(holder.itemView.getContext()) //해당 환경의 Context나 객체 입력
                    .load(listViewItem.getImageURI()) //URL, URI 등등 이미지를 받아올 경로
                    .override(1000,600)
                    .into(((RightImageViewHolder) holder).image); //받아온 이미지를 받을 공간(ex. ImageView)
            ((RightImageViewHolder) holder).date.setText(listViewItem.getDate());
        } else {

            ((MiddleViewHolder) holder).message.setText(listViewItem.getMessage());
        }

    }

//    @Override
//    public int getItemViewType() {
//        return ITEM_VIEW_TYPE_MAX ;
//    }

    public ListViewChatItem getItem(int position) {
        return listViewChatItemList.get(position);
    }
    // position 위치의 아이템 타입 리턴.
    @Override
    public int getItemViewType(int position) {
        return listViewChatItemList.get(position).getType();
    }

    //*****ListView 전체를 만들기 위한 코드가 아니라, 항목 하나를 구성하는 코드가 들어가는 부분이다. *********
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final Context context = parent.getContext();
//        int viewType = getItemViewType(position) ;
//
//
//        //if (convertView == null) { //convertView를 무조건 생성한다. => 비용이 많이 든다.
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
//
//            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
//            ListViewChatItem listViewItem = listViewChatItemList.get(position);
//
//            switch (viewType) {
//                case ITEM_VIEW_TEXT_TYPE_OTHER:
//                    convertView = inflater.inflate(R.layout.other_chat_message, parent, false);
//                    /*
//                    item.setIconDrawable(iconDrawable); ;
//                    item.setUserId(userId);
//                    item.setMessage(message);
//                    item.setDate(date); ;
//                     */
//                    CircleImageView profileImage = convertView.findViewById(R.id.chatmessage_iv_profile);
//                    TextView userIdTextView = convertView.findViewById(R.id.chatmessage_tv_nickname);
//                    TextView messageTextView = convertView.findViewById(R.id.chatmessage_tv_message);
//                    TextView dateTextView = convertView.findViewById(R.id.chatmessage_tv_date);
//
//                    profileImage.setImageDrawable(listViewItem.getIconDrawable());
//                    userIdTextView.setText(listViewItem.getUserId());
//
//                    messageTextView.setBackgroundResource(R.drawable.inbox2);
//                    messageTextView.setText(listViewItem.getMessage());
//                    dateTextView.setText(listViewItem.getDate());
//                    break;
//                case ITEM_VIEW_TEXT_TYPE_MINE:
//                    convertView = inflater.inflate(R.layout.my_chat_message, parent, false);
//
//                    TextView myIdTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_message);
//                    TextView myDateTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_date);
//
//                    myIdTextView.setBackgroundResource(R.drawable.outbox2);
//                    myIdTextView.setText(listViewItem.getMessage());
//                    myDateTextView.setText(listViewItem.getDate());
//                    break;
//                case ITEM_VIEW_TYPE_INOUT:
//                    convertView = inflater.inflate(R.layout.activity_chatitem, parent, false);
//
//                    TextView userState = (TextView) convertView.findViewById(R.id.text);
//
//                    userState.setText(listViewItem.getMessage());
//                    break;
//            }
//      //  }
//
//        return convertView;
//    }



    //지정한 위치에 있는 데이터와 관계된 아이템의 id를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listViewChatItemList.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴
//    @Override
//    public ListViewChatItem getItem(int position) {
//        return listViewChatItemList.get(position);
//    }


    public void addItem(String userProfile, String userId, String message, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //상대방 프로필 사진, 아이디, 메시지 내용, 날짜가 표시된다.
        item.setType(ITEM_VIEW_TEXT_TYPE_OTHER) ;
        //item.setIconDrawable(iconDrawable);
        item.setUserProfileURI(userProfile);

        item.setUserId(userId);
        item.setMessage(message);
        item.setDate(date);

        listViewChatItemList.add(item) ;
    }

    // 두 번째 아이템 추가를 위한 함수.
    public void addItem(String message, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //나의 메시지 내용, 날짜만 표시된다.
        item.setType(ITEM_VIEW_TEXT_TYPE_MINE) ;
        item.setMessage(message);
        item.setDate(date);

        listViewChatItemList.add(item);
    }

    public void addImageItem(String userProfile, String userId, String imageURI, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //상대방 프로필 사진, 아이디, 메시지 내용, 날짜가 표시된다.
        item.setType(ITEM_VIEW_IMAGE_TYPE_OTHER) ;
        //item.setIconDrawable(iconDrawable);
        item.setUserProfileURI(userProfile);
        item.setUserId(userId);
        item.setImageURI(imageURI);
        item.setDate(date);

        listViewChatItemList.add(item) ;
    }

    public void addImageItem(String imageURI, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //나의 메시지 내용, 날짜만 표시된다.
        item.setType(ITEM_VIEW_IMAGE_TYPE_MINE) ;
        item.setImageURI(imageURI);
        item.setDate(date);

        listViewChatItemList.add(item);
    }

/*
ITEM_VIEW_TEXT_TYPE_OTHER
ITEM_VIEW_TEXT_TYPE_MINE =
ITEM_VIEW_IMAGE_TYPE_OTHER
ITEM_VIEW_IMAGE_TYPE_MINE
ITEM_VIEW_TYPE_INOUT = 4 ;
 */

    //Todo 나중에 추가할것. 유저가 들어올때 나갈때 띄어줄 메시지
    public void addItem(String message) {
        ListViewChatItem item = new ListViewChatItem() ;

        //나의 메시지 내용, 날짜만 표시된다.
        item.setType(ITEM_VIEW_TYPE_INOUT) ;
        item.setMessage(message);

        listViewChatItemList.add(item);
    }

    // 상대방의 텍스트 메시지
    public class LeftTextViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView userId;
        TextView message;
        TextView date;

        public LeftTextViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.chatmessage_iv_profile);
            userId = itemView.findViewById(R.id.chatmessage_tv_nickname);
            message = itemView.findViewById(R.id.chatmessage_tv_message);
            date = itemView.findViewById(R.id.chatmessage_tv_date);
        }
    }

    // 내가 보낸 텍스트 메시지
    public class RightTextViewHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView date;

        public RightTextViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.my_chatmessage_tv_message);
            date = itemView.findViewById(R.id.my_chatmessage_tv_date);
        }
    }

    // 상대방의 이미지 메시지
    public class LeftImageViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView userId;
        ImageView image;
        TextView date;

        public LeftImageViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.imgmessage_iv_profile);
            userId = itemView.findViewById(R.id.imgmessage_tv_nickname);
            image = itemView.findViewById(R.id.imgmessage_iv_message);
            date = itemView.findViewById(R.id.imgmessage_tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(LeftImageViewHolder.this, v, pos) ;
                        }
                    }
                }
            });
        }
    }

    // 나의 이미지 메시지
    public class RightImageViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView date;

        public RightImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.my_imgmessage_iv_message);
            date = itemView.findViewById(R.id.my_imgmessage_iv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (myListener != null) {
                            myListener.onItemClick(RightImageViewHolder.this, v, pos) ;
                        }
                    }
                }
            });
        }
    }

    // 유저 입장, 퇴장 표시
    public class MiddleViewHolder extends RecyclerView.ViewHolder{

        TextView message;

        public MiddleViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.text);
        }
    }

}
