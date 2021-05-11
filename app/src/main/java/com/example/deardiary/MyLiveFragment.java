package com.example.deardiary;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyLiveFragment extends Fragment {

    RecyclerView recyclerView;
    LiveStreamItem item;
    ArrayList<LiveStreamItem> items= new ArrayList<>();
    LiveStreamAdapter adapter;
    LinearLayoutManager layoutManager;

    private static final String APPLICATION_ID = "ZaZrnb7KSnYrJZkUDVunJA";
    private static final String API_KEY = "FdqhqjQv8DNFKrWrEArGRt";
    OkHttpClient mOkHttpClient;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("lifeCycle", "onAttach 호출");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("lifeCycle", "onCreate 호출");
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("lifeCycle", "onViewCreated 호출");
        recyclerView= view.findViewById(R.id.live_recycler);
        adapter= new LiveStreamAdapter(getActivity(), items);
        layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //스트리밍 데이터 로딩
        loadStreamInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("lifeCycle", "onActivityCreated 호출");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("lifeCycle", "onStart 호출");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("lifeCycle", "onPause 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("lifeCycle", "onStop 호출");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("lifeCycle", "onDestroyView 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("lifeCycle", "onDetach 호출");
    }

    @Subscribe
    public void busStop(LiveEvent liveEvent) {//public항상 붙여줘야함
        if(liveEvent.isFlag()) {
            Log.i("BusEvent", "MyAccount 페이지 로딩하기");
            loadStreamInfo();
        }
    }

    void loadStreamInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Live Streamming Loading");
        progressDialog.show();


        mOkHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.bambuser.com/broadcasts?order=live")
                .addHeader("Accept", "application/vnd.bambuser.v1+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .get()
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
//                runOnUiThread(new Runnable() { @Override public void run() {
//                    if (mPlayerStatusTextView != null)
//                        mPlayerStatusTextView.setText("Http exception: " + e);
//                }});
            }
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {

                //데이터를 얻어올때마다 초기화 시켜준다.
                items.clear();
                //adapter.notifyDataSetChanged();

                String body = response.body().string();
                Log.d("디버그태그", body);
                String resourceUri = null;
                try {
                    JSONObject json = new JSONObject(body);
                    JSONArray results = json.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject broadcast =  results.optJSONObject(i);
                        String preview = broadcast.optString("preview");
                        String author = broadcast.optString("author");
                        String title = broadcast.optString("title");
                        resourceUri = broadcast.optString("resourceUri");
                        String length = broadcast.optString("length");
                        String type = broadcast.optString("type");
                        String streamTime = broadcast.optString("created");

                        Log.d("okhttp", "preview :" + preview);
                        Log.d("okhttp", "author :" + author);
                        Log.d("okhttp", "title :" + title);
                        Log.d("okhttp", "resourceUri :" + resourceUri);
                        Log.d("okhttp", "length :" + length);
                        Log.d("okhttp", "type :" + type);
                        Log.d("okhttp", "streamTime :" + streamTime);

                        items.add( new LiveStreamItem(preview, author, title, streamTime,resourceUri, length, type));

//                      adapter.notifyItemInserted(0);
                    }
                    /*
                    String preview;
                    String author; //broadcasting 하는 user ID
                    String title; // //스트리밍 시작 시간을 넣어준다.
                    String streamingTime; //스트리밍 시작 시간 -> UNIX TimeStamp (변환을 해줘야 한다.)
                    String resourceUri;
                    String length; //스트리밍 방송시간
                    String type; // 라이브중 : live , 방송종료 : archived
                     */
                    Log.d("okhttp", items.toString());


                    adapter.setOnItemClickListener(new LiveStreamAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int pos) {
                            String resourceUri = items.get(pos).resourceUri;
                            Toast.makeText(getActivity(), "resourceUri :" + resourceUri,Toast.LENGTH_SHORT).show();
                            Intent mediaIntent = new Intent(getContext(), LiveMediaActivity.class);
                            mediaIntent.putExtra("resourceUri", resourceUri);
                            startActivity(mediaIntent);
                        }
                    });

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
//                    recyclerView.setAdapter(adapter);
//                    //LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
//                    recyclerView.setLayoutManager(layoutManager);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_live, container, false);
        Log.d("lifeCycle", "onCreateView 호출");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lifeCycle", "onResume 호출");
        //Toast.makeText(getActivity(), "onResume()호출",Toast.LENGTH_SHORT).show();
        //loadStreamInfo(); -> 계속 죽는다.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lifeCycle", "onDestroy 호출");
        mOkHttpClient.dispatcher().cancelAll();
        BusProvider.getInstance().unregister(this);
    }
}