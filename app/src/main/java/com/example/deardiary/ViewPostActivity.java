package com.example.deardiary;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPostActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private ImageView iv_diary;
    private ImageView iv_comment;
    private TextView tv_viewCnt;
    private TextView tv_comment_cnt;
    private ImageView modify_post;
    private TextView tv_date;
    private ToggleButton tbn_heart;
    private EditText tv_diary;
    private EditText tv_tag;
    private PopupMenu popupMenu;
    private ToggleButton button_favorite;
    private TextView tv_likeCnt;

    private String server_ip;
    private String SERVER_URL;

    private boolean initiate = false;

    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;//애니메이션이 일어나는 동안의 회수, 속도를 조절하거나 시작과 종료시의 효과를 추가 할 수 있다

    String clicked = "";
    String userId;
    String postId;
    JSONObject requestJsonUserObject;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        BusProvider.getInstance().register(this);

        server_ip = getString(R.string.server_ip);

        iv_diary = findViewById(R.id.diary_image);
        iv_comment = findViewById(R.id.iv_comment);
        tv_viewCnt = findViewById(R.id.tv_viewCnt);
        tv_comment_cnt = findViewById(R.id.iv_comment_cnt);
        tbn_heart = findViewById(R.id.tbn_heart);
        tv_diary = findViewById(R.id.comment_text);
        tv_tag = findViewById(R.id.diary_tag);
        tv_date = findViewById(R.id.diary_date);
        tv_likeCnt = findViewById(R.id.tv_likeCnt);
        modify_post = findViewById(R.id.modify_post);
        button_favorite = findViewById(R.id.button_favorite);

        //좋아요 버튼 애니메이션 설정
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);

        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        button_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(scaleAnimation);


                    if (isChecked) { //하트를 해제할 경우
                        clicked = "1";
                    } else { //하트를 누를 경우
                        clicked = "0";
                    }
                    loadLikeInfo(userId, postId);
                    Log.d("디버그태그", String.valueOf(isChecked));
                }

        });

        button_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        Intent postDataIntent = getIntent();

        userId = postDataIntent.getStringExtra("userId");
        postId = postDataIntent.getStringExtra("postId");

        //수정 버튼은 사용자의 아이디로 자신의 포스트를 볼때만 보여준다.
        if(UserInfo.getInstance().getClickedId().equals(UserInfo.getInstance().getId())) {
            modify_post.setOnClickListener(this);
        } else {
            modify_post.setVisibility(View.INVISIBLE);
        }

        requestJsonUserObject = new JSONObject();

        try {
            requestJsonUserObject.put("userId", userId);
            requestJsonUserObject.put("postId", postId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadMyPost(requestJsonUserObject);
        loadLikeInfo(userId, postId);

    }

    private void loadLikeInfo(String userId, String postId) {

        SERVER_URL="http://"+server_ip+"/loadingdata/load_post_like.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject resJsonObject = new JSONObject(response);

                    String like_cnt = resJsonObject.getString("like_cnt");
                    String like_status = resJsonObject.getString("like_status");

                    tv_likeCnt.setText(like_cnt + " 개");
                    if(like_status.equals("1")) {
                        button_favorite.setChecked(true);
                    } else {
                        button_favorite.setChecked(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
         //BusProvider.getInstance().post(new BusEvent(true));
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
                param.put("postId", postId);
                param.put("userId", userId);

                if(clicked.equals("0") || clicked.equals("1")) {
                    param.put("clicked", clicked);
                }

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_post:
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미
                getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;

        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modify: //리스트 첫번째 클릭시 호출
                Toast.makeText(getApplication(), "수정", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete: //리스트 두번째 클릭시 호출
                Toast.makeText(getApplication(), "삭제", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Subscribe
    public void busStop(CommentEvent commentEvent) throws JSONException {//public항상 붙여줘야함
        if(commentEvent.isUpdate()) {
            Log.i("BusEvent", "ViewPostActivity Comment 업데이트 하기");
            //기존에 사용하던 JsonObjectRequest를 계속 사용하기 위해 Request로 보내준 JSonObject에 값을 하나 더 담아서 보내준다.
            //기존에 userId와 PostId만 담은 JSonObject는 포스팅 하나를 로딩하기 위한 요청
            // comment_cnt를 추가한 JSonObject는 댓글 갯수 업데이트를 위한 요청
            //(두 요청에 해한 query의 차이점은 ViewCnt(조회수) 를 올리느냐의 유무 만 다르다.)
            requestJsonUserObject.put("comment_cnt", "comment_cnt");
            loadMyPost(requestJsonUserObject);
        }
    }


    //loadMyPost는 조회수와 하트를 제외한 모든 게시물의 정보를 가져온다.
    //조회수와 하트는 따로 가져온다.
    private void loadMyPost(JSONObject requestJsonUserObject) {
        // Inflate the layout for this fragment

        //서버의 loadDBtoJson.php파일에 접속하여 (DB데이터들)결과 받기
        //Volley+ 라이브러리 사용

        //서버주소
        SERVER_URL = "http://"+server_ip+"/loadingdata/load_single_post.php";

        //결과를 JsonArray 받을 것이므로..
        //StringRequest가 아니라..
        //JsonArrayRequest를 이용할 것임
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, SERVER_URL, requestJsonUserObject, new Response.Listener<JSONObject>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();

                //파라미터로 응답받은 결과 JsonArray를 분석
                try {

                    String comment_cnt = response.getString("comment_cnt");
                    String user_id = response.getString("user_id");
                    String text_content = response.getString("text_content");
                    String imgPath = response.getString("img_src");
                    String hashtag = response.getString("hashtag");
                    String hit_view = response.getString("hit_view");
                    String hit_like = response.getString("hit_like");
                    String click_like = response.getString("click_like");
                    String created_date = response.getString("created_date");

//                    tv_viewCnt = findViewById(R.id.tv_viewCnt);
//                    tv_comment_cnt = findViewById(R.id.iv_comment_cnt);
                    //이미지 경로의 경우 서버 IP가 제외된 주소이므로(uploads/xxxx.jpg) 바로 사용 불가.
                    imgPath = "http://"+server_ip+imgPath;

                    Glide.with(ViewPostActivity.this).load(imgPath).into(iv_diary);
                    tv_diary.setText(text_content);
                    tv_tag.setText(hashtag);
                    tv_date.setText(created_date);
                    tv_viewCnt.setText(hit_view);
                    tv_comment_cnt.setText(comment_cnt);
                    iv_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentIntent = new Intent(getApplicationContext(), CommentActivity.class);
                            commentIntent.putExtra("postId", postId);
                            startActivity(commentIntent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //요청큐에 요청 객체 생성
        requestQueue.add(jsonArrayRequest);
    }

    //이벤트버스 등록해제
    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        Log.d("생명주기 :", "onDestroy()");
    }

}