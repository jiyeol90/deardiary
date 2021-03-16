package com.example.deardiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherAccountFragment extends Fragment {

    public static final int REQUEST_CODE = 100;
    private Button btn_post;
    private TextView tv_postCnt;
    private TextView tv_profileText;
    private CircleImageView iv_profile;
    private Button btn_friend;
    private Button btn_chatting;
    private Button btn_profile;
    private ImageView iv_post;

    GridLayoutManager layoutManager;
    GridView gridView;
    RecyclerView rcv_grid;

    ArrayList<GridListItem> items= new ArrayList<>();

    GridListAdapter adapter;


    //이벤트 버스 등록
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("생명주기 :", "onCreate()");
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("생명주기 :", "onViewCreated()");
        rcv_grid = view.findViewById(R.id.grid_view);


        //userindex값을 받아서 Volley의 JsonArrayRequest로 보낸다.
        String userIndex = UserInfo.getInstance().getIndex();

        JSONObject requestJsonUserObject = new JSONObject();
        try {
            requestJsonUserObject.put("index", userIndex);

        } catch(JSONException e) {
            e.printStackTrace();
        }

        adapter = new GridListAdapter(getActivity(), items);
        rcv_grid.setAdapter(adapter);



        //결과를 JsonArray 받을 것이므로..
        //StringRequest가 아니라..
        //JsonArrayRequest를 이용할 것임

        loadPage();

    }
    //데이터를 받을 메서드
    @Subscribe
    public void busStop(BusEvent busEvent) {//public항상 붙여줘야함
        if(busEvent.isFlag()) {
            Log.i("BusEvent", "MyAccount 페이지 로딩하기");
            loadPage();
        }
    }

    private void loadPage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Loading My Page");
        progressDialog.show();

        String serverUrl="http://3.36.92.185/loadingdata/otherpage_load.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                //파라미터로 응답받은 결과 JsonArray를 분석

                items.clear();
                adapter.notifyDataSetChanged();
                try {
                    JSONArray resJsonArray = new JSONArray(response);
                    Log.i("resJsonArray", Integer.toString(resJsonArray.length()));

                    JSONObject upperMypage = resJsonArray.getJSONObject(0);
                    String imageSrc = upperMypage.getString("user_profile");
                    String profileText = upperMypage.getString("user_text");
                    String cnt = upperMypage.getString("postCnt");
                    String friendOrNot = upperMypage.getString("friendOrNot");
                    Log.i("포스팅 개수", cnt);

                    /*
                     * 기본이미지일 경우의 response 값 ("default")
                     * 프로필 문구만 입력되어있는 경우 response값 (null)
                     * DB조회를 할때 ueser 테이블에서 프로필 이미지의 url (user_profile) 과 프로필 문구 (user_text) 컬럼을 select 하기 때문에 처리해 줘야 한다.
                     */
                    if(!imageSrc.equals("default") && !imageSrc.equals("null")) {

                        //UserInfo 객체에 이미지 경로 필드에 저장한다.
                        UserInfo.getInstance().setUserProfile(imageSrc);
                        //서버에서 저장된 이미지 url
                        imageSrc = "http://3.36.92.185"+imageSrc;
                        Glide.with(OtherAccountFragment.this).load(imageSrc).into(iv_profile);
                    }
                    if(!profileText.equals("default") && !profileText.equals("null")) {

                        UserInfo.getInstance().setUserText(profileText);
                        //상태메시지 입력
                        tv_profileText.setText(profileText);
                    }

                    tv_postCnt.setText(cnt);

                    //친구상태일 경우 체크를 해준다.
                    if(friendOrNot.equals("1")) {
                        btn_friend.setBackgroundColor(Color.GRAY);
                        btn_friend.setText("친구끊기");
//                        btn_friend.setClickable(false);
                        btn_chatting.setVisibility(View.VISIBLE);
                    }


                    JSONArray postArray = resJsonArray.getJSONArray(1);
                    Log.i("postArray 수", Integer.toString(postArray.length()));
                    for(int i=0; i<postArray.length(); i++) {
                        JSONObject post = postArray.getJSONObject(i);
                        String postId = post.getString("id");
                        String imgPath= post.getString("img_src");
                        String userId = post.getString("user_id");
                        String date= post.getString("created_date").substring(0,10);

                        imgPath = "http://3.36.92.185"+imgPath;
                        items.add(0, new GridListItem(postId, imgPath, date));
                        adapter.notifyItemInserted(0);

                        //아이템 클릭 이벤트 처리
                        adapter.setOnItemClickListener(new GridListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                String postId = items.get(position).getId();
                                Toast.makeText(getActivity(), position+" 번째 아이템", Toast.LENGTH_SHORT).show();
                                Intent viewPostIntent = new Intent(getContext(), ViewPostActivity.class);
                                viewPostIntent.putExtra("userId", userId);
                                viewPostIntent.putExtra("postId", postId);
                                startActivity(viewPostIntent);

                            }
                        });

                    }

                    //리사이클러뷰의 레이아웃 매니저 설정
                    layoutManager= new GridLayoutManager(getActivity(),3);
                    rcv_grid.setLayoutManager(layoutManager);


                } catch (JSONException e) {e.printStackTrace();}

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

                String userId = UserInfo.getInstance().getClickedId();
                String myId = UserInfo.getInstance().getId();
                param.put("userId", userId);
                param.put("myId", myId);

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            String sendText = data.getExtras().getString("text");
            Log.i("결과값", sendText);
        }
    }

    //이벤트버스 등록해제
    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        Log.d("생명주기 :", "onDestroy()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("생명주기 :", "onCreateView()");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_other_account, container, false);
        tv_postCnt = rootView.findViewById(R.id.tv_diary_count);
        iv_profile = rootView.findViewById(R.id.iv_profile);
        tv_profileText = rootView.findViewById(R.id.profile_text);
        btn_friend = rootView.findViewById(R.id.btn_friend);
        btn_chatting = rootView.findViewById(R.id.diary);

        //친구맺기 버튼
        btn_friend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(btn_friend.getText().equals("친구하기")) {
                    btn_friend.setBackgroundColor(Color.GRAY);
//                btn_friend.setClickable(false);
                    btn_friend.setText("친구끊기");
                    btn_chatting.setVisibility(View.GONE);

                    //Todo 여기서 부터 시작 (02/26) 친구 리스트 만들기
                    String friendId = UserInfo.getInstance().getClickedId();
                    makeFriend(friendId);
                } else {
                    showDialog();
                }

            }
        });

        //채팅버튼 누를시
        btn_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent chatIntent = new Intent(getContext(), ChattingActivity.class);
//                startActivity(chatIntent);

                Intent chatIntent = new Intent(getContext(), MyChattingActivity.class);
                startActivity(chatIntent);
                //채팅화면 띄어주기
            }
        });
        // 프로필 사진 업로드

        return rootView;
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("알림")
                .setMessage("친구를 끊으시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        makeFriend("cancel");
                        Toast.makeText(getActivity(), "친구를 끊었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    } });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }


    private void makeFriend(String friendId) {

        String serverUrl = "";

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Update My Friend");
        progressDialog.show();

        if(friendId.equals("cancel")) {
            serverUrl = "http://3.36.92.185/uploads/friend_cancel_upload.php";
        } else {
            serverUrl = "http://3.36.92.185/uploads/friend_upload.php";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                //파라미터로 응답받은 결과 JsonArray를 분석
                if(response.equals("success")) {
                    btn_friend.setBackgroundColor(Color.GRAY);
                    btn_friend.setText("친구끊기");
                    //btn_friend.setClickable(false);
                    btn_chatting.setVisibility(View.VISIBLE);
                } else if(response.equals("cancel")) {
                    btn_friend.setBackgroundColor(Color.parseColor("#6200EE"));//@color/mtrl_btn_ripple_color
                    btn_friend.setText("친구하기");
                    //btn_friend.setClickable(false);
                    btn_chatting.setVisibility(View.GONE);
                }

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

                String userId = UserInfo.getInstance().getId();
                param.put("userId", userId);
                param.put("friendId", friendId);

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }

}