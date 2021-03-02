package com.example.deardiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyHomeFragment extends Fragment {

    RecyclerView recyclerView;

    PostItem item;
    ArrayList<PostItem> items= new ArrayList<>();

    PostAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recycler);
        adapter= new PostAdapter(getActivity(), items);

        // Inflate the layout for this fragment

        //서버의 loadDBtoJson.php파일에 접속하여 (DB데이터들)결과 받기
        //Volley+ 라이브러리 사용

        //서버주소
        String serverUrl="http://3.36.92.185/loadingdata/load_post.php";

        //결과를 JsonArray 받을 것이므로..
        //StringRequest가 아니라..
        //JsonArrayRequest를 이용할 것임
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.POST, serverUrl, null, new Response.Listener<JSONArray>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(JSONArray response) {
               // Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();


                //파라미터로 응답받은 결과 JsonArray를 분석

                items.clear();
                adapter.notifyDataSetChanged();
                try {

                    for(int i=0;i<response.length();i++){
                        JSONObject jsonObject= response.getJSONObject(i);

                        String userProfile =jsonObject.getString("user_profile");
                        String no= jsonObject.getString("id"); //diaraypage.id -> 다이어리 아이디
                        String name=jsonObject.getString("user_id"); // diarypage.user_id -> 다이어리 작성 아이디
                        String imgPath=jsonObject.getString("img_src"); // diarypage.img_src -> 다이어리 이미지 src
                        String date=jsonObject.getString("created_date"); // diarypage.created_date -> 다이어리 생성일

                        if(userProfile.equals("null")) {
                            userProfile = "default";
                        } else {
                            userProfile = "http://3.36.92.185"+userProfile;
                        }
                        //이미지 경로의 경우 서버 IP가 제외된 주소이므로(uploads/xxxx.jpg) 바로 사용 불가.
                        imgPath = "http://3.36.92.185"+imgPath;

                        items.add(0, new PostItem(no, name, userProfile, imgPath, date)); // 첫 번째 매개변수는 몇번째에 추가 될지, 제일 위에 오도록
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemInserted(0);
                        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {

                            @Override
                            public void onItemClick(View v, int pos) {
                                Toast.makeText(getActivity(), pos+" 번째 아이템", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    recyclerView.setAdapter(adapter);

                    //리사이클러뷰의 레이아웃 매니저 설정
                    LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(layoutManager);

                } catch (JSONException e) {e.printStackTrace();}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        //요청큐에 요청 객체 생성
        requestQueue.add(jsonArrayRequest);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_home, container, false);


        return rootView;
    }
}