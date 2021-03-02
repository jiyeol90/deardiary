package com.example.deardiary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_OTHER = 0 ;
    private static final int ITEM_VIEW_TYPE_MINE = 1 ;
    private static final int ITEM_VIEW_TYPE_INOUT = 2 ;
    private static final int ITEM_VIEW_TYPE_MAX = 3 ;

    private ArrayList<ListViewChatItem> listViewChatItemList = new ArrayList<ListViewChatItem>() ;

    public ChattingListViewAdapter() {

    }

    //전체 항목의 개수를 판단하기 위해 호출된다.
    @Override
    public int getCount() {
        return listViewChatItemList.size();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX ;
    }

    // position 위치의 아이템 타입 리턴.
    @Override
    public int getItemViewType(int position) {
        return listViewChatItemList.get(position).getType();
    }

    //*****ListView 전체를 만들기 위한 코드가 아니라, 항목 하나를 구성하는 코드가 들어가는 부분이다. *********
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(position) ;


        //if (convertView == null) { //convertView를 무조건 생성한다. => 비용이 많이 든다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ListViewChatItem listViewItem = listViewChatItemList.get(position);

            switch (viewType) {
                case ITEM_VIEW_TYPE_OTHER:
                    convertView = inflater.inflate(R.layout.other_chat_message, parent, false);
                    /*
                    item.setIconDrawable(iconDrawable); ;
                    item.setUserId(userId);
                    item.setMessage(message);
                    item.setDate(date); ;
                     */
                    CircleImageView profileImage = convertView.findViewById(R.id.chatmessage_iv_profile);
                    TextView userIdTextView = convertView.findViewById(R.id.chatmessage_tv_nickname);
                    TextView messageTextView = convertView.findViewById(R.id.chatmessage_tv_message);
                    TextView dateTextView = convertView.findViewById(R.id.chatmessage_tv_date);

                    profileImage.setImageDrawable(listViewItem.getIconDrawable());
                    userIdTextView.setText(listViewItem.getUserId());

                    messageTextView.setBackgroundResource(R.drawable.inbox2);
                    messageTextView.setText(listViewItem.getMessage());
                    dateTextView.setText(listViewItem.getDate());
                    break;
                case ITEM_VIEW_TYPE_MINE:
                    convertView = inflater.inflate(R.layout.my_chat_message, parent, false);

                    TextView myIdTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_message);
                    TextView myDateTextView = (TextView) convertView.findViewById(R.id.my_chatmessage_tv_date);

                    myIdTextView.setBackgroundResource(R.drawable.outbox2);
                    myIdTextView.setText(listViewItem.getMessage());
                    myDateTextView.setText(listViewItem.getDate());
                    break;
                case ITEM_VIEW_TYPE_INOUT:
                    convertView = inflater.inflate(R.layout.activity_chatitem, parent, false);

                    TextView userState = (TextView) convertView.findViewById(R.id.text);

                    userState.setText(listViewItem.getMessage());
                    break;
            }
      //  }

        return convertView;
    }

    //지정한 위치에 있는 데이터와 관계된 아이템의 id를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public ListViewChatItem getItem(int position) {
        return listViewChatItemList.get(position);
    }


    public void addItem(Drawable iconDrawable, String userId, String message, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //상대방 프로필 사진, 아이디, 메시지 내용, 날짜가 표시된다.
        item.setType(ITEM_VIEW_TYPE_OTHER) ;
        item.setIconDrawable(iconDrawable); ;
        item.setUserId(userId);
        item.setMessage(message);
        item.setDate(date); ;

        listViewChatItemList.add(item) ;
    }

    // 두 번째 아이템 추가를 위한 함수.
    public void addItem(String message, String date) {
        ListViewChatItem item = new ListViewChatItem() ;

        //나의 메시지 내용, 날짜만 표시된다.
        item.setType(ITEM_VIEW_TYPE_MINE) ;
        item.setMessage(message);
        item.setDate(date);

        listViewChatItemList.add(item);
    }

    //Todo 나중에 추가할것. 유저가 들어올때 나갈때 띄어줄 메시지
    public void addItem(String message) {
        ListViewChatItem item = new ListViewChatItem() ;

        //나의 메시지 내용, 날짜만 표시된다.
        item.setType(ITEM_VIEW_TYPE_INOUT) ;
        item.setMessage(message);

        listViewChatItemList.add(item);
    }

}
