package com.example.deardiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원가입 액티비티 - (index로 주석을 찾을수 있다.)
 * 1. 아이디 중복을 체크
 * 2. 모든 입력 필드가 유효하면 정상적으로 회원 가입 진행
 *
 */
public class RegisterActivity extends AppCompatActivity {

    MaterialEditText et_userId, et_password, et_email, et_userName;
    RadioGroup radioGroup;
    Button btn_register, btn_checkId;

    boolean isCheckedId = false;
    String server_ip;

    //private final String server_ip = getString(R.string.server_ip);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        server_ip = getString(R.string.server_ip);

        et_userId = findViewById(R.id.user_id);
        et_email = findViewById(R.id.email);
        et_userName = findViewById(R.id.user_name);
        et_password = findViewById(R.id.password);
        radioGroup = findViewById(R.id.radiogp);
        btn_checkId = findViewById(R.id.check_id);
        btn_register = findViewById(R.id.register);


        btn_checkId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtUserId = et_userId.getText().toString();

                if(TextUtils.isEmpty(txtUserId)) {
                    Toast.makeText(RegisterActivity.this, "Please input your ID", Toast.LENGTH_SHORT).show();
                } else {
                    checkDuplicateID(txtUserId);
                }
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String txtUserId = et_userId.getText().toString();
                String txtPassword = et_password.getText().toString();
                String txtUserName = et_userName.getText().toString();
                String txtEmail = et_email.getText().toString();


                if (TextUtils.isEmpty(txtUserId) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtUserName)
                        ||TextUtils.isEmpty(txtEmail)) {
                    Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else {
                    int genderId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selected_Gender = radioGroup.findViewById(genderId);
                    if (selected_Gender == null) {
                        Toast.makeText(RegisterActivity.this, "Select gender Please", Toast.LENGTH_SHORT).show();
                    } else {
                        String selectGender = selected_Gender.getText().toString();

                        if(isCheckedId) {
                            registerNewAccount(txtUserId, txtPassword, txtUserName, txtEmail, selectGender);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Please Check the ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void checkDuplicateID(final String userId) {

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Check The ID");
        progressDialog.show();

        String uRl = "http://"+server_ip+"/loginregister/check_id.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("This ID is Available")) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                    isCheckedId = true;
                    et_userId.setEnabled(false);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", userId);
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(RegisterActivity.this).addToRequestQueue(request);

    }


    //2. 모든 입력 필드가 유효하면 정상적으로 회원 가입 진행
    private void registerNewAccount(final String userId, final String password, final String userName,final String email,
                                    final String gender) {

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering New Account");
        progressDialog.show();

        String uRl = "http://"+server_ip+"/loginregister/register.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("Successfully Registered")) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", userId);
                param.put("psw", password);
                param.put("userName", userName);
                param.put("email", email);
                param.put("gender", gender);

                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(RegisterActivity.this).addToRequestQueue(request);

    }

}