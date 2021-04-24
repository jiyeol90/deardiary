package com.example.deardiary;
import com.bumptech.glide.Glide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<FriendListItem> listViewFriendList = new ArrayList<FriendListItem>() ;

    public FriendListViewAdapter() {}

    @Override
    public int getCount() {
        return listViewFriendList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_friend_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        CircleImageView civ_profileImage = (CircleImageView) convertView.findViewById(R.id.imageView1) ;
        TextView tv_userId = (TextView) convertView.findViewById(R.id.textView1) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        FriendListItem listViewItem = listViewFriendList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        /*  의존성(dependencies) 추가
            implementation 'com.github.bumptech.glide:glide:4.11.0'
              annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
        */
        if(!listViewItem.getProfileSrc().equals("default")) {
            Glide.with(context) //해당 환경의 Context나 객체 입력
                    .load(listViewItem.getProfileSrc()) //URL, URI 등등 이미지를 받아올 경로
                    .into(civ_profileImage); //받아온 이미지를 받을 공간(ex. ImageView)
        } else {
            Glide.with(context) //해당 환경의 Context나 객체 입력
                    .load(R.drawable.ic_profile_default) //URL, URI 등등 이미지를 받아올 경로
                    .into(civ_profileImage); //받아온 이미지를 받을 공간(ex. ImageView)
        }
        tv_userId.setText(listViewItem.getUserId());

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String profileSrc, String userId) {
        FriendListItem item = new FriendListItem();

        item.setProfileSrc(profileSrc);
        item.setUserId(userId);

        listViewFriendList.add(item);
    }
}
