package com.example.deardiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppStartActivity extends AppCompatActivity {

    Button logout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    Fragment accountFragment;
    Fragment homeFragment;
    Fragment noteFragment;
    Fragment chattingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        accountFragment = new MyAccountFragment();
        homeFragment = new MyHomeFragment();
        noteFragment = new MyNoteFragment();
        chattingFragment = new MyChattingFragment();

        setFragment(0); //첫 프래그먼트 화면을 지정한다.

        bottomNavigationView = findViewById(R.id.bottomNavi);
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

                    case R.id.btn_note:
                        setFragment(2);
                        break;

                    case R.id.btn_chatting:
                        setFragment(3);
                        break;

                }
                return true;
            }
        });

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
                fragmentTransaction.replace(R.id.main_frame, noteFragment);
                fragmentTransaction.commit();
                break;

            case 3:
                fragmentTransaction.replace(R.id.main_frame, chattingFragment);
                fragmentTransaction.commit();
                break;

        }
    }


}