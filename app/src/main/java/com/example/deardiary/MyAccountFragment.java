package com.example.deardiary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;




public class MyAccountFragment extends Fragment {

    public static final int REQUEST_CODE = 100;
    private Button btn_post;
    private TextView tv_postCnt;
    private CircleImageView iv_profile;
    private Button btn_profile;
    private ImageView iv_post;

    GridLayoutManager layoutManager;
    GridView gridView;
    RecyclerView rcv_grid;

    ArrayList<GridListItem> items= new ArrayList<>();

    GridListAdapter adapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void loadPage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Loading My Page");
        progressDialog.show();

        String serverUrl="http://3.36.92.185/loadingdata/mypage_load.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {

                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                //파라미터로 응답받은 결과 JsonArray를 분석

                items.clear();
                adapter.notifyDataSetChanged();
                try {
                    JSONArray resJsonArray = new JSONArray(response);
                    for(int i=0; i<resJsonArray.length(); i++){
                        JSONObject jsonObject= resJsonArray.getJSONObject(i);

                        //String no= jsonObject.getString("id"); //no가 문자열이라서 바꿔야함.
                        // String name=jsonObject.getString("user_id");

                        String imgPath=jsonObject.getString("img_src");
                        String date=jsonObject.getString("created_date").substring(0,10); // 2021-02-01 14:44:01 에서 년 월 일만 띄어준다.


                        //이미지 경로의 경우 서버 IP가 제외된 주소이므로(uploads/xxxx.jpg) 바로 사용 불가.
                        imgPath = "http://3.36.92.185"+imgPath;

                        items.add(0, new GridListItem(imgPath, date)); // 첫 번째 매개변수는 몇번째에 추가 될지, 제일 위에 오도록
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemInserted(0);

                    }
                    //리사이클러뷰의 레이아웃 매니저 설정
                    layoutManager= new GridLayoutManager(getActivity(),3);
                    rcv_grid.setLayoutManager(layoutManager);

                    progressDialog.dismiss();
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

                String index = UserInfo.getInstance().getIndex();
                Log.d("index값", index);
                param.put("index", index);

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_account, container, false);
        btn_post = rootView.findViewById(R.id.btn_diary_write);
        btn_profile = rootView.findViewById(R.id.btn_profile_edit);
        tv_postCnt = rootView.findViewById(R.id.tv_diary_count);
        iv_profile = rootView.findViewById(R.id.iv_profile);


        //포스팅 버튼
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DiaryPostActivity.class);
                //startActivityForResult(intent,REQUEST_CODE);
                //결과값으로 가져올 데이터가 없으므로 새로운 인텐트를 띄어준다.
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ProfileUploadActivity.class);
                //startActivityForResult(intent,REQUEST_CODE);
                //결과값으로 가져올 데이터가 없으므로 새로운 인텐트를 띄어준다.
                startActivity(intent);
            }
        });

        return rootView;
    }
}