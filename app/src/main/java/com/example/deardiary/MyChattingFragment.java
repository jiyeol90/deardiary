package com.example.deardiary;
import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;


public class MyChattingFragment extends Fragment {

    RecyclerView recyclerView;

    ChatRoomItem item;
    ArrayList<ChatRoomItem> items = new ArrayList<>();

    ChattingRoomAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recycler_room);
        adapter= new ChattingRoomAdapter(getActivity(), items);




        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");


        recyclerView.setAdapter(adapter);

        //리사이클러뷰의 레이아웃 매니저 설정
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);






        //서버주소
        String serverUrl="http://3.36.92.185/loadingdata/load_post.php";

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_chatting, container, false);

        return rootView;
    }



}