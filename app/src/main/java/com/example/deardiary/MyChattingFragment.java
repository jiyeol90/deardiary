package com.example.deardiary;
import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyChattingFragment extends Fragment {

    private String MY_ID = UserInfo.getInstance().getId();

    RecyclerView recyclerView;
    ChatRoomItem item;
    ArrayList<ChatRoomItem> items = new ArrayList<>();
    ChattingRoomAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recycler_room);
        adapter= new ChattingRoomAdapter(getActivity(), items);



        loadingRoomList();
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");
//        adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default), "james", "마지막 메시지", "2021-03-02 11 : 07");

    }

    @Subscribe
    public void busStop(ChatEvent chatEvent) {//public항상 붙여줘야함
        if(chatEvent.isFlag()) {
            Log.i("BusEvent", "MyAccount 페이지 로딩하기");
            loadingRoomList();
        }
    }


    //1. 내가 참여한 채팅방의 목록을 모두 표시한다.
    //2. 채팅방에는 상대방의 프로필과 (한명일때, 여러명일때)

    private void loadingRoomList() {
        {

            String SERVER_URL = "http://3.36.92.185/chattingdata/load_room_list.php";


            // 나와 상대가 대화중인 방이 있는지 roomId를 얻어온 후
            // 그동안 대화한 내용을 뿌려준다.


            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
                @Override
                public void onResponse(String response) {
/*
{
    "room_id": "1",
    "user_id": "james",
    "content_type": "txt",
    "content": "hello",
    "created_date": "2021-03-08 06:30:14",
    "friend": [
            {
            "user_id": "jiyeol90",
            "user_profile": "default"
            }
        ]
    },
 */
                    items.clear();
                    adapter.notifyDataSetChanged();
                    try {
                        JSONArray resJsonArray = new JSONArray(response);
                        Log.i("MYCHATTING", String.valueOf(resJsonArray.length()));
                        for(int i = 0; i < resJsonArray.length(); i++) {
                            HashMap<String, String> friendMap = new HashMap<String, String>();
                            JSONObject roomObject = resJsonArray.getJSONObject(i);
                            String roomId = roomObject.getString("room_id");
                            String userId = roomObject.getString("user_id");
                            String contentType = roomObject.getString("content_type");
                            String content = roomObject.getString("content");
                            String createdDate = roomObject.getString("created_date");
                            JSONArray friendArray = roomObject.getJSONArray("friend");

                            for(int j = 0; j < friendArray.length(); j++) {
                                JSONObject friendObject = friendArray.getJSONObject(j);
                                String friendId = friendObject.getString("user_id");
                                String friendProfile = friendObject.getString("user_profile");
                                friendMap.put(friendId, friendProfile);
                            }
                            System.out.println("\n[ " + i + " ] " + "번째 ROOM INFO");
                            System.out.println("roomId : " + roomId);
                            System.out.println("userId : " + userId);
                            System.out.println("contentType : " + contentType);
                            System.out.println("createdDate : " + createdDate);

                            String clickedId = "";
                            for(String key : friendMap.keySet()){
                                clickedId = key;
                                String value = friendMap.get(key);

                                System.out.println(key+" : "+value);
                            }
                            adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default),friendMap, contentType, content, createdDate, clickedId);
                            //Variable 'clickedId' is accessed from within inner class, needs to be final or effectively fina

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.setOnItemClickListener(new ChattingRoomAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(View v, int pos) {
                            Toast.makeText(getActivity(), pos+" 번째 아이템", Toast.LENGTH_SHORT).show();
                            UserInfo.getInstance().setClickedId(items.get(pos).getClickedId());
                            Intent chatIntent = new Intent(getActivity(), MyChattingActivity.class);
                            startActivity(chatIntent);
                        }
                    });
                    recyclerView.setAdapter(adapter);

                    //리사이클러뷰의 레이아웃 매니저 설정
                    LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(layoutManager);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    HashMap<String, String> param = new HashMap<>();


                    //String postId = UserInfo.getInstance().getId();
                    param.put("myId", MY_ID); //todo roomId 를 String? int?
                    return param;
                }
            };

            //실제 요청 작업을 수행해주는 요청큐 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

            //요청큐에 요청 객체 생성
            requestQueue.add(stringRequest);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_chatting, container, false);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}