package com.example.deardiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MaterialEditText userId, password;
    Button login, register;
    CheckBox loginState;
    SharedPreferences sharedPreferences;

    //AWS EC2에 Elastic IP를 설정하지 않았기 때문에(유료) 서버를 재부팅 할때마다 IP주소가 바뀌어서 리소스 외부화를 해주었다.
    String server_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server_ip = getString(R.string.server_ip);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        userId = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        loginState = findViewById(R.id.checkbox);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String txtUserId = userId.getText().toString();
                String txtPassword = password.getText().toString();
                if (TextUtils.isEmpty(txtUserId) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(MainActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else {
                    login(txtUserId, txtPassword);
                }
            }
        });

        //todo 로그인 유지 보류
        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefLoginState), "");
//        if (loginStatus.equals("loggedin")) {
//            startActivity(new Intent(MainActivity.this, AppStartActivity.class));
//        }

    }

    private void login(final String userId, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering New Account");
        progressDialog.show();

        String uRl = "http://"+ server_ip +"/loginregister/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("Login Success")) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (loginState.isChecked()) {
                        editor.putString(getResources().getString(R.string.prefLoginState), "loggedin");
                    }
                    else {
                        editor.putString(getResources().getString(R.string.prefLoginState), "loggedout");
                    }
                    editor.apply();

                    Intent loginIntent = new Intent(MainActivity.this, AppStartActivity.class);
                    loginIntent.putExtra("userId", userId);
                    loginIntent.putExtra("password", password);
                    startActivity(loginIntent);
                }

                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", userId);
                param.put("psw", password);

                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(MainActivity.this).addToRequestQueue(request);

    }

}