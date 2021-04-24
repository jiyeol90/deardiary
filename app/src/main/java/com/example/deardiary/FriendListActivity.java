package com.example.deardiary;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity {

    private Button inviteButton;
    private Button selectAllButton;

    private ListView listview;
    private FriendListViewAdapter adapter;

    private String server_ip;
    private String SERVER_URL;
    private HashMap<String, String> friendsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        inviteButton = findViewById(R.id.invite);
        selectAllButton = findViewById(R.id.selectAll);

        server_ip = getString(R.string.server_ip);
        // Adapter 생성
        adapter = new FriendListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);


        // 첫 번째 아이템 추가.
        //http://3.36.92.185/uploads/profile_images/16124226401612422631487.jpg
        ///uploads/profile_images/16124226401612422631487.jpg
//        adapter.addItem("http://3.36.92.185/uploads/profile_images/16124226401612422631487.jpg",
//                "james") ;
//        // 두 번째 아이템 추가.
//        adapter.addItem("http://3.36.92.185/uploads/profile_images/16124226401612422631487.jpg",
//                "jiyeol90") ;
//        // 세 번째 아이템 추가.
//        adapter.addItem("http://3.36.92.185/uploads/profile_images/16124226401612422631487.jpg",
//                "elias") ;

        inviteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount() ;

                ArrayList<String> friends = new ArrayList<>();

                //StringBuilder clickedId = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (checkedItems.get(i)) {
                        //clickedId.append(((FriendListItem) adapter.getItem(i)).getUserId());
                        friends.add(((FriendListItem) adapter.getItem(i)).getUserId());
                        friendsMap.put(((FriendListItem) adapter.getItem(i)).getUserId(), ((FriendListItem) adapter.getItem(i)).getProfileSrc());
                    }
                }
                if (friends.size() == 0) {
                    Toast.makeText(getApplicationContext(),"초대할 친구를 선택하세요.",Toast.LENGTH_SHORT).show();
                } else if(friends.size() == 1) {
                    //클릭한 친구가 1명이면 기존의 1:1 채팅방으로
                    UserInfo.getInstance().setClickedId(friends.get(0));
                    Intent chatIntent = new Intent(getApplicationContext(), MyChattingActivity.class);
                    chatIntent.putExtra("friendsMap", friendsMap);
                    startActivity(chatIntent);
                    finish();
                } else if(friends.size() > 1) {
                    //2명 이상이면 단체 메시지방으로
                    Intent chatIntent = new Intent(getApplicationContext(), MyChattingActivity.class);
                    chatIntent.putExtra("friends", friends);
                    chatIntent.putExtra("friendsMap", friendsMap);
                    startActivity(chatIntent);
                    finish();
                    //Toast.makeText(getApplicationContext(),"단체 채팅방으로 이동~~.",Toast.LENGTH_SHORT).show();
                }


                Toast.makeText(getApplicationContext(),friends.toString(),Toast.LENGTH_SHORT).show();
                Log.d("FriendList", "클릭한 아이디 : " + friends.toString());
            }
        });

        selectAllButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(selectAllButton.getText().equals("Select All")) {
                    int count = 0 ;
                    count = adapter.getCount() ;

                    for (int i=0; i<count; i++) {
                        listview.setItemChecked(i, true) ;
                    }

                    selectAllButton.setText("Cancel All");
                } else {
                    int count = 0 ;
                    count = adapter.getCount() ;

                    for (int i=0; i<count; i++) {
                        listview.setItemChecked(i, false) ;
                    }
                    selectAllButton.setText("Select All");
                }

            }
        });

        loadFriendList();

    }

    private void loadFriendList() {
        {

            String userId = UserInfo.getInstance().getId();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setTitle("Upload Comment");
            progressDialog.show();

            SERVER_URL = "http://"+server_ip+"/loadingdata/load_friend_list.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    try {
                        JSONArray resJsonArray = new JSONArray(response);

                        for(int i = 0; i < resJsonArray.length(); i++) {
                            JSONObject jsonObject= resJsonArray.getJSONObject(i);

                            String friendId = jsonObject.getString("friend_id");
                            String profileSrc = jsonObject.getString("user_profile");

                            Log.d("friendList", "friendId : " + friendId + ", profileSrc : " + profileSrc);

                            if(!profileSrc.equals("null")) {
                                adapter.addItem("http://" + server_ip + profileSrc, friendId);
                            } else {
                                adapter.addItem("default", friendId);
                            }

                        }

                        adapter.notifyDataSetChanged();
                        /*
                        JSONObject jsonObject= response.getJSONObject(i);

                        String userProfile =jsonObject.getString("user_profile");
                        String no= jsonObject.getString("id"); //diaraypage.id -> 다이어리 아이디
                         */

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "response",Toast.LENGTH_SHORT).show();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    HashMap<String, String> param = new HashMap<>();

                    //String postId = UserInfo.getInstance().getId();
                    param.put("userId", userId);

                    return param;
                }
            };

            //실제 요청 작업을 수행해주는 요청큐 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

            //요청큐에 요청 객체 생성
            requestQueue.add(stringRequest);

        }
    }


}