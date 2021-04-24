package com.example.deardiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener{

    private Handler mHandler;
    Socket socket;
    private String ip = "3.36.92.185"; // IP 주소
    private int port = 10001; // PORT번호
    private boolean initConnect = true;
    private boolean isConnected = true;
    private PrintWriter out;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader input;

    private final int GET_GALLERY_IMAGE = 200;
    private final String TAG = "ChattingActivity";

    ListView m_ListView;
    ChatAdapter m_Adapter;
    EditText et_text;
    Button btn_send;
    Button btn_start;
    Button btn_img;
    TextView textView;

    DataOutputStream dos;
    private String userId = UserInfo.getInstance().getId();
    private String read = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_acitivity);

        mHandler = new Handler();

        et_text = findViewById(R.id.textMessage);
        btn_send = findViewById(R.id.sendButton);
        btn_img = findViewById(R.id.imgButton);
        textView = findViewById(R.id.text);

//        btn_start.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_img.setOnClickListener(this);


        // 커스텀 어댑터 생성
        m_Adapter = new ChatAdapter();

        // Xml에서 추가한 ListView 연결
        m_ListView = (ListView) findViewById(R.id.listView);

        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);

//        m_Adapter.add("이건 뭐지", 1);
//        m_Adapter.add("쿨쿨", 1);
//        m_Adapter.add("쿨쿨쿨쿨", 0);
//        m_Adapter.add("재미있게", 1);
//        m_Adapter.add("놀자라구나힐힐 감사합니다. 동해물과 백두산이 마르고 닳도록 놀자 놀자 우리 놀자", 1);
//        m_Adapter.add("재미있게", 1);
//        m_Adapter.add("재미있게", 0);
//        m_Adapter.add("2015/11/20", 2);
//        m_Adapter.add("재미있게", 1);
//        m_Adapter.add("재미있게", 1);

        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                btn_send.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                //공백문자를 제거한 후의 문자열을 저장하도록 한다.
                if (arg0.toString().trim().length() == 0)
                    btn_send.setEnabled(false);
            }
        });

       // btn_start.performClick();

        ConnectThread th =new ConnectThread();
        th.start();

    }
    

    class ConnectThread extends Thread {

        String sndMsg;

        public void run() {
            try {
                //소켓 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);
                //입력 메시지
                //처음 연결할때만 아이디를 보내준다.

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
                input = new BufferedReader(new InputStreamReader(inputStream));


                /*
                 OutputStream out = socket.getOutputStream(); //socket에 기능중 Stream을 불러와 out에 담는다.
            DataOutputStream dos = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);

                 */
                sndMsg = UserInfo.getInstance().getId();

                //화면 출력
                out.println(sndMsg);
                out.flush();

//                String read;
                while ((read = input.readLine()) != null) {
                    //                        read = input.readLine();
//                    mHandler.post(new msgUpdate(read));
//                    Log.d("=============", read);
//                    et_text.getText().clear();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //1. 처음접속할때 뿌려줄 메시지 "[id] 님이 접속하였습니다."
                            //2. 내가 쓴 메시지 "imgj : [내 id] : 메시지 내용"
                            //3. 상태방이 쓴 메시지 "[상대방 id] : 메시지 내용"
                            String protocol = read.split(":")[0].trim();
                            String message = read.split(":")[1].trim();
                            if(protocol.equals("initConnect")) {
                                //m_Adapter.add(message, 2);
                            } else if (protocol.equals("img")) {
                                //이미지를 보냈을때

                            } else if (protocol.equals(userId)) {
                                m_Adapter.add(message, 1);
                            } else if (protocol.equals("disconnect")) {
                               // m_Adapter.add(message, 2); //Todo 시현할때는 생략한다.
                            } else {
                                m_Adapter.add(message, 0);
                            }
                            m_Adapter.notifyDataSetChanged();
                            Log.d("=============", read);
                            et_text.getText().clear();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 받은 메시지 출력
    class msgUpdate implements Runnable {
        private String msg;
        public msgUpdate(String str) {
            this.msg = str;
        }
        public void run() {
            m_Adapter.add(et_text.getText().toString() + msg, 1);
            m_Adapter.notifyDataSetChanged();
        }
    };



    @Override
    public void onClick(View v) {
        String message;

        switch (v.getId()) {
            case R.id.sendButton:
                if(out == null) return;
//                ConnectThread th =new ConnectThread();
//                th.start();

//                message = et_text.getText().toString();
//                mHandler.post(new msgUpdate(message));
//                out.println(message);
//                out.flush();
//
//                et_text.getText().clear();
                message= et_text.getText().toString();

                SendThread st = new SendThread(message, out);
                st.start();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        //서버로 보낼 메세지 EditText로 부터 얻어오기
//                        String msg= et_text.getText().toString();
//
//                        Log.d("msg", msg);
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
//
//
//                        out.println(msg);
//                        out.flush();
//                    }//run method..
//
//                }).start(); //Thread 실행..

//                message = et_text.getText().toString();

                //중복되서 메시지가 출력된다.
//                et_text.getText().clear();
//                m_Adapter.add(message, 1);
//                m_Adapter.notifyDataSetChanged();
                break;
            case R.id.imgButton:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
                break;
        }
    }

    //뒤로가기로 화면을 벗어날때 나가기로 처리되면 안된다.
    //방을 명시적으로 나가기 전까지는 계속 소켓을 유지하고 있어야 한다.
    @Override
    protected void onStop() {
        super.onStop();

        //Todo 사진을 가져올때 인텐트를 벗어나므로 stop상태가 될때 소켓을 끊어주면 안된다.
//        try {
//            if(out == null) return;
//            SendThread st = new SendThread("/quit", out);
//            st.start();
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == GET_GALLERY_IMAGE) {

           String imagePath = getRealPathFromURI(data.getData());
           SendPictureThread sendPicThread = new SendPictureThread(imagePath, outputStream);
           sendPicThread.start();

        }
    }

    private void sendPicture(Uri imgUri) {
        try {
            String imagePath = getRealPathFromURI(imgUri); //  imgUri =>  content://media/external/images/media/1578
            Log.e(TAG, imagePath); // imagePath => /storage/emulated/0/DCIM/Camera/20210222_164527.jpg

            DataInputStream dis = new DataInputStream(new FileInputStream(new File(imagePath)));

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            byte[] buf = new byte[1024];

            long totalReadBytes = 0;

            int readBytes;
            while ((readBytes = dis.read(buf)) > 0) { //길이 정해주고 딱 맞게 서버로 보냅니다.
                dos.write(buf, 0, readBytes);

                totalReadBytes += readBytes;
            }


            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }

}