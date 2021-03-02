package com.example.deardiary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class SendThread extends Thread{

        private String message;
        private PrintWriter out;
        private int roomId;


        public SendThread(String message, PrintWriter out) {
            this.message = message;
            this.out = out;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //서버로 보낼 메세지 EditText로 부터 얻어오기

            Log.d("msg", message);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                String msg= et_text.getText().toString();
//                                // TODO Auto-generated method stub
//                                m_Adapter.add(et_text.getText().toString() + msg, 1);
//                                m_Adapter.notifyDataSetChanged();
//                                Log.d("=============", msg);
//                                et_text.getText().clear();
//
//                            }
//                        });


            out.println(message);
            out.flush();

        }//run method..

}
