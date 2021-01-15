package com.example.deardiary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class MyAccountFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gridView = view.findViewById(R.id.gv_diary_list);
        GridListAdapter adapter = new GridListAdapter();

        //테스트 아이템
        adapter.addItem(new ListItem("일기제목", "21/01/14"));
        adapter.addItem(new ListItem("일기제목", "21/01/15"));
        adapter.addItem(new ListItem("일기제목", "21/01/16"));
        adapter.addItem(new ListItem("일기제목", "21/01/17"));
        adapter.addItem(new ListItem("일기제목", "21/01/18"));
        adapter.addItem(new ListItem("일기제목", "21/01/19"));
        adapter.addItem(new ListItem("일기제목", "21/01/20"));
        adapter.addItem(new ListItem("일기제목", "21/01/21"));
        adapter.addItem(new ListItem("일기제목", "21/01/22"));
        adapter.addItem(new ListItem("일기제목", "21/01/23"));
        adapter.addItem(new ListItem("일기제목", "21/01/24"));
        adapter.addItem(new ListItem("일기제목", "21/01/25"));
        adapter.addItem(new ListItem("일기제목", "21/01/26"));
        adapter.addItem(new ListItem("일기제목", "21/01/27"));
        adapter.addItem(new ListItem("일기제목", "21/01/28"));
        adapter.addItem(new ListItem("일기제목", "21/01/29"));
        adapter.addItem(new ListItem("일기제목", "21/01/30"));
        adapter.addItem(new ListItem("일기제목", "21/01/31"));
        gridView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }
}