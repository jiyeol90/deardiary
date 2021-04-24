package com.example.deardiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class CommentActivity extends AppCompatActivity {

    CircleImageView user_profile;
    EditText comment_text;
    TextView comment_date;
    RecyclerView rcv_comment;
    Button btn_submit;
    private String server_ip;
    private String SERVER_URL;

    ArrayList<CommentItem> items= new ArrayList<>();
    CommentAdapter adapter;

    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        server_ip = getString(R.string.server_ip);
        adapter= new CommentAdapter(getApplicationContext(), items);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        user_profile = findViewById(R.id.userProfile);
        comment_text = findViewById(R.id.comment_text);
        rcv_comment = findViewById(R.id.rcv_comment);
        rcv_comment.addItemDecoration(new DividerItemDecoration(this, 1)); //구분선 설정

        comment_date = findViewById(R.id.comment_date);
        btn_submit = findViewById(R.id.submit);

        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                btn_submit.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                //공백문자를 제거한 후의 문자열을 저장하도록 한다.
                if(arg0.toString().trim().length() == 0)
                    btn_submit.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }

        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               submitComment();
               comment_text.getText().clear();
            }
        });

        loadComment();

    }

    private void submitComment() {

        String comment = comment_text.getText().toString();
        String userId = UserInfo.getInstance().getId();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Upload Comment");
        progressDialog.show();

        SERVER_URL="http://"+server_ip+"/loadingdata/upload_comment.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                //댓글 입력 성공시
                //1.해당 포스트의 댓글을 다시 로딩한다.
                //2.포스트뷰 액티비티를 다시 로딩한다. (댓글 갯수 하나증가해준다.)
                Toast.makeText(getApplicationContext(), "response",Toast.LENGTH_SHORT).show();
                loadComment();
                CommentEvent commentEvent = new CommentEvent(postId, true);
                BusProvider.getInstance().post(commentEvent);
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
                param.put("comment", comment);
                param.put("userId", userId);

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }


    private void loadComment() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Loading Comment");
        progressDialog.show();

        SERVER_URL = "http://"+server_ip+"/loadingdata/load_comment.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
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

                    for(int i=0; i<resJsonArray.length(); i++) {
                        JSONObject post = resJsonArray.getJSONObject(i);
                        String imgPath= post.getString("user_profile");
                        String userId = post.getString("user_id");
                        String content = post.getString("content");
                        String date = post.getString("created_date");

                        /*
                         * 기본이미지일 경우의 response 값 ("default")
                         * 프로필 문구만 입력되어있는 경우 response값 (null)
                         * DB조회를 할때 ueser 테이블에서 프로필 이미지의 url (user_profile) 과 프로필 문구 (user_text) 컬럼을 select 하기 때문에 처리해 줘야 한다.
                         */
                        if(imgPath.equals("default") || imgPath.equals("null")) {

                            //서버에서 저장된 이미지 url
                            imgPath = "default";
                        } else {
                            imgPath = "http://"+server_ip+imgPath;
                        }

                        items.add(0, new CommentItem(imgPath, userId, content, date));
                        adapter.notifyItemInserted(0);

                        //아이템 클릭 이벤트 처리
                        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                String postId = items.get(position).getId();
                                Toast.makeText(getApplicationContext(), position+" 번째 아이템", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    rcv_comment.setAdapter(adapter);
                    //리사이클러뷰의 레이아웃 매니저 설정
                    LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                    rcv_comment.setLayoutManager(layoutManager);

                } catch (JSONException e) {e.printStackTrace();}

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

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }
}