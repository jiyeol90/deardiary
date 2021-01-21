package com.example.deardiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;


public class MyAccountFragment extends Fragment {

    public static final int REQUEST_CODE = 100;
    private Button btn;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        btn = rootView.findViewById(R.id.btn_diary_write);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DataInputActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                //startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}