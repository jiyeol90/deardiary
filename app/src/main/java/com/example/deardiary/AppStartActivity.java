package com.example.deardiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class AppStartActivity extends AppCompatActivity {

    private Button logout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private Context context = this;

    private String myId;
    private String server_ip;
    private String SERVER_URL;
    private String FCM_TOKEN;

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    Fragment accountFragment;
    Fragment homeFragment;
    Fragment liveFragment;
    Fragment chattingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        //채팅 알람을 통해 들어올 경우
        Intent notificationIntent = getIntent();
        if(notificationIntent.hasExtra("user_id")) {
            UserInfo.getInstance().setId(notificationIntent.getStringExtra("user_id"));
            UserInfo.getInstance().setClickedId(notificationIntent.getStringExtra("friend_id"));

            Intent directToChattingIntent = new Intent(this, MyChattingActivity.class);
            directToChattingIntent.putExtra("direct", "direct");
            directToChattingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(directToChattingIntent);
        }


        myId = UserInfo.getInstance().getId();
        //FCM 토큰값 저장 [해당 아이디]
        if(!UserInfo.getInstance().isTokenSaved()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            FCM_TOKEN = task.getResult();
                            UserInfo.getInstance().setToken(FCM_TOKEN);
                            // Log and toast
                            String msg = getString(R.string.msg_token_fmt, FCM_TOKEN);
                            Log.d("FCM", msg);
                            //토큰정보
                            //Toast.makeText(AppStartActivity.this, msg, Toast.LENGTH_SHORT).show();

                            saveFCMToken();
                            //토큰값을 얻어오기 전에 Volley 통신을 하니깐 계속 token값이 빈 문자열이다.
                            UserInfo.getInstance().setTokenSaved(true);
                        }
                    });

//            saveFCMToken();
//            //토큰값을 얻어오기 전에 Volley 통신을 하니깐 계속 token값이 빈 문자열이다.
//            UserInfo.getInstance().setTokenSaved(true);
        }
        server_ip = getString(R.string.server_ip);
        //SERVER_URL = "http://"+ server_ip +"/loginregister/user_info.php";
        //Todo 위치를 MainActivity로 옮겨준다.
        //notifyUserInfo(requestJsonUserObject, serverUrl);

        accountFragment = new MyAccountFragment();
        homeFragment = new MyHomeFragment();
        liveFragment = new MyLiveFragment();
        chattingFragment = new MyChattingFragment();

        setFragment(0); //첫 프래그먼트 화면을 지정한다.

        bottomNavigationView = findViewById(R.id.bottomNavi);


        //BottomNavigation으로 fragment화면 전환
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.btn_account:
                        setFragment(0);
                        break;

                    case R.id.btn_home:
                        setFragment(1);
                        break;

                    case R.id.btn_live:
                        setFragment(2);
                        break;

                    case R.id.btn_chatting:
                        setFragment(3);
                        break;

                }
                return true;
            }
        });


        //노티로 들어오는 화면은 모두 chattingFragment를 통해 roomId를 통해 들어가도록하자
//        Intent notificationIntent = getIntent();
//        if(notificationIntent.hasExtra("user_id")) {
//            UserInfo.getInstance().setId(notificationIntent.getStringExtra("user_id"));
//            UserInfo.getInstance().setClickedId(notificationIntent.getStringExtra("friend_id"));
//            UserInfo.getInstance().setRoomId(notificationIntent.getStringExtra("room_id"));
//
//            bottomNavigationView.setSelectedItemId(R.id.btn_chatting);
//
//            Intent directToChattingIntent = new Intent(this, MyChattingActivity.class);
//            directToChattingIntent.putExtra("direct", "direct");
//            directToChattingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            startActivity(directToChattingIntent);
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); //뒤로가기 버튼 이미지 지정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("App Start Activity");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", 0);


        //Todo 로그아웃 기능
        //logout = findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getResources().getString(R.string.prefLoginState), "loggedout");
//                editor.apply();
//                startActivity(new Intent(AppStartActivity.this, MainActivity.class));
//                finish();
//            }
//        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.account){
                    Toast.makeText(context, title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.setting){
                    Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.logout){
                    Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.prefLoginState), "loggedout");
                    editor.apply();
                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                    finish();
                }

                return true;
            }
        });
    }

    //토큰 업데이트 -> 기기마다 다른 토큰이 부여되므로 로그인 할때마다 업데이트 해준다. (같은 아이디로 다른 기기에 접속했을 때를 파악해 준다.)
    private void saveFCMToken() {

        SERVER_URL = "http://"+server_ip+"/loginregister/register_FCM_token.php";
        StringRequest request = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("token", FCM_TOKEN);
                param.put("myId", myId);
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequestQueue(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragment(int index) {

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (index) {

            case 0:
                fragmentTransaction.replace(R.id.main_frame, accountFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentTransaction.replace(R.id.main_frame, homeFragment);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentTransaction.replace(R.id.main_frame, liveFragment);
                fragmentTransaction.commit();
                break;

            case 3:
                fragmentTransaction.replace(R.id.main_frame, chattingFragment);
                fragmentTransaction.commit();
                break;

        }
    }

    @Override
    public void onBackPressed() {

    }
}