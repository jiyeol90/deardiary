package com.example.deardiary;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyChattingActivity extends AppCompatActivity implements View.OnClickListener{

    private Handler mHandler;
    Socket socket;
    private String ip = "3.36.92.185"; // IP 주소
    private static final String ROOT_URL = "http://3.36.92.185/uploads/chatting_img_upload.php";
    private int port = 10001; // PORT번호
    private PrintWriter out;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader input;

    private int CHATTING_ROOM_ID;
    private String initMessage;

    private SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");

    private final int GET_GALLERY_IMAGE = 200;
    private final String TAG = "MyChattingActivity";

    //ListView m_ListView;
    RecyclerView m_RecyclerView;
   // ChattingListViewAdapter m_Adapter;
    ChattingListViewAdapter2 m_Adapter;
    EditText et_text;
    Button btn_send;
    Button btn_start;
    Button btn_img;
    TextView textView;

    DataOutputStream dos;
    private String MY_ID = UserInfo.getInstance().getId();
    private String FRIEND_ID =  UserInfo.getInstance().getClickedId();
    private String read = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_chatting_acitivity);

        et_text = findViewById(R.id.textMessage);
        btn_send = findViewById(R.id.sendButton);
        //btn_start = findViewById(R.id.tcpStart);
        btn_img = findViewById(R.id.imgButton);
        textView = findViewById(R.id.text);

//      btn_start.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_img.setOnClickListener(this);


        // 커스텀 어댑터 생성
        m_Adapter = new ChattingListViewAdapter2(getApplicationContext());

        // Xml에서 추가한 ListView 연결
        m_RecyclerView = (RecyclerView) findViewById(R.id.recycler_chatting);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        //역순으로 출력하기위해 설정한다.
//        manager.setReverseLayout(true);
//        manager.setStackFromEnd(true);
        m_RecyclerView.setLayoutManager(manager); // LayoutManager 등록
        // ListView에 어댑터 연결
        m_RecyclerView.setAdapter(m_Adapter);

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

        //기존에 만들어 놓은 방이 있는 DB 탐색
        // 없다면 makeRoom = false;
        // 있다면 makeRoom = true;

        //상대방과 대화한 방이 있는지 검색한 후 ConnectThread를 실행한다.
        loadChatRoomData();
//        ConnectThread th =new ConnectThread();
//        th.start();

    }

    private void loadChatRoomData() {
        {
//            String myId = UserInfo.getInstance().getId();
//            String friendId = UserInfo.getInstance().getClickedId();

            String SERVER_URL = "http://3.36.92.185/chattingdata/load_room_data.php";


            // 나와 상대가 대화중인 방이 있는지 roomId를 얻어온 후
            // 그동안 대화한 내용을 뿌려준다.


            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
                @Override
                public void onResponse(String response) {
                    //조회한 방ID를 가져온다.                    roomId@[roomId]
                    //테이블에 방이 없으면 "noRoom"을 리턴한다.   noRoom@
                    //조회한 방이 없으면 마지막 방 ID를 리턴한다.  lastRoomId@[lastRoomId]
                    String[] roomInfo = response.split("@");

                    if(roomInfo[0].equals("roomId")) {
                        initMessage = roomInfo[1];
                        CHATTING_ROOM_ID = Integer.parseInt(initMessage);
                        //roomId를 가지고 넘어올때만 채팅룸의 내용을 보여준다.
                        loadChatMessage(initMessage);
                    }else if(roomInfo[0].equals("lastRoomId")) {
                        initMessage = "lastRoomId/"+ roomInfo[1];
                        //위치에 대해 고민해봐야 한다.
                        ConnectThread th =new ConnectThread();
                        th.start();
                    }else {

                        //noRoom일때는 테이블에 데이터가 없는 경우이므로 첫 room data를 생성한다.
                        initMessage = "lastRoomId/"+ 0;
                        ConnectThread th =new ConnectThread();
                        th.start();
                    }
                    //BusProvider.getInstance().post(new BusEvent(true));



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    HashMap<String, String> param = new HashMap<>();


                    //String postId = UserInfo.getInstance().getId();
                    param.put("myId", MY_ID); //todo roomId 를 String? int?
                    param.put("friendId", FRIEND_ID);

                /*
                CHATTING_ROOM_ID = Integer.parseInt(roomId);
        String senderUserId = filter[1];
        String contentType = filter[2];
        String content = filter[3];

         String myId = UserInfo.getInstance().getId();
            String friendId = UserInfo.getInstance().getClickedId();
                 */

                    return param;
                }
            };

            //실제 요청 작업을 수행해주는 요청큐 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

            //요청큐에 요청 객체 생성
            requestQueue.add(stringRequest);

        }
    }

    void loadChatMessage(String roomId) {
        {
            String SERVER_URL = "http://3.36.92.185/chattingdata/load_message_data.php";

            // 나와 상대가 대화중인 방이 있는지 roomId를 얻어온 후
            // 그동안 대화한 내용을 뿌려준다.

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
                @Override
                public void onResponse(String response) {

                    JSONArray resJsonArray = null;
                    try {
                        //msg.user_id, user.user_profile, msg.content_type, msg.content, msg.created_date
                        resJsonArray = new JSONArray(response);
                        for(int i = 0; i < resJsonArray.length(); i++) {
                            JSONObject message = resJsonArray.getJSONObject(i);
                            String user_id = message.getString("user_id");
                            String user_profile = message.getString("user_profile");
                            String content_type = message.getString("content_type");
                            String content = message.getString("content");
                            String created_date = message.getString("created_date");

                            //user_id : 내아이디 / 상대방 아이디 (내아디가 아닌 모든 사람)
                            //content_type : txt / img
                            //content : 텍스트 메시지 / img URI
                            //create_date : 메시지 생성 시간

                            //내가 보낸 메시지
                            if (user_id.equals(MY_ID)) {
                                //텍스트 메시지
                                if(content_type.equals("txt")) {
                                    m_Adapter.addItem(content, created_date); ///uploads/post_images/161426010420200731_185544.jpg
                                } else {
                                //이미지 메시지
                                    m_Adapter.addImageItem("http://3.36.92.185"+content, created_date);
                                }
                            } else {
                                //상대방이 보낸 메시지
                                if(content_type.equals("txt")) {
                                    m_Adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile_default), user_id, content, created_date); ///uploads/post_images/161426010420200731_185544.jpg
                                } else {
                                    //이미지 메시지
                                    m_Adapter.addImageItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile_default), user_id, "http://3.36.92.185"+content, created_date);
                                }
                            }

                            m_Adapter.notifyDataSetChanged();
                            //ArrayList의 크기의 index 위치로 포지셔닝.
                            m_RecyclerView.scrollToPosition(m_Adapter.getItemCount()-1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //위치에 대해 고민해봐야 한다.
                    ConnectThread th =new ConnectThread();
                    th.start();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    HashMap<String, String> param = new HashMap<>();


                    //String postId = UserInfo.getInstance().getId();
                    param.put("roomId", roomId); //todo roomId 를 String? int?

                    return param;
                }
            };

            //실제 요청 작업을 수행해주는 요청큐 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

            //요청큐에 요청 객체 생성
            requestQueue.add(stringRequest);

        }
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
                sndMsg = UserInfo.getInstance().getId()+"@"+UserInfo.getInstance().getClickedId(); // 내 Id + @ + 상대방 Id
                Log.d(TAG, "메시지 내용 : " + sndMsg);

                //[방번호]@[내아이디]@[상대방아이디]
                out.println(initMessage + "@" + sndMsg);

                out.flush();



                while ((read = input.readLine()) != null) {

                    String[] filter = read.split("@");

                    String roomId = filter[0];
                    CHATTING_ROOM_ID = Integer.parseInt(roomId);
                    String senderUserId = filter[1];
                    String contentType = filter[2];
                    String content = filter[3];


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //현재 시간 구하기

                            Calendar time = Calendar.getInstance();
                            String format_time = format.format(time.getTime());
                            //1. 처음접속할때 뿌려줄 메시지 "[id] 님이 접속하였습니다."
                            //2. 내가 쓴 메시지 "imgj : [내 id] : 메시지 내용"
                            //3. 상태방이 쓴 메시지 "[상대방 id] : 메시지 내용"

                            if(contentType.equals("initConnectAndMakeRoom")) {
                                //DB에 생성된 방을 insert한다
                                m_Adapter.addItem(content);
                            }
                            else if(contentType.equals("initConnect")) {
                               // m_Adapter.add(message, 2);
                                m_Adapter.addItem(content);
                            } else if (contentType.equals("img")) {
                                //이미지를 보냈을때
                                    if(senderUserId.equals(MY_ID)) {
                                        m_Adapter.addImageItem("http://3.36.92.185"+content, format_time); ///uploads/post_images/161426010420200731_185544.jpg
                                    } else {
                                        m_Adapter.addImageItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile_default), senderUserId, "http://3.36.92.185"+ content, format_time);
                                    }

                            } else if (senderUserId.equals(MY_ID)) {
                                m_Adapter.addItem(content, format_time);
                            } else if (contentType.equals("disconnect")) {
                                m_Adapter.addItem(content);
                            } else {
                                m_Adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile_default), senderUserId, content, format_time);//todo  protocol 수정할것
                            }
                            m_Adapter.notifyDataSetChanged();
                            //ArrayList의 크기의 index 위치로 포지셔닝.
                            m_RecyclerView.scrollToPosition(m_Adapter.getItemCount()-1);
                            Log.d("=============", read);
                            et_text.getText().clear();
                            //메시지에 따라 DB저장하는 방법을 다르게 한다.
                            //* initMessageAndMakeRoom  : 방을 insert 한다.
                            //* initConnect / disconnect: 입장했다는 메시지를 표시하기 위한 것이므로 DB에 저장하지 않는다.
                            //* 그 외의 contentType 은 txt와 img에 따라 구분하면서 저장한다.
                        }
                    });
                    
                    //runOnUiThread에서 실행하지말것 -> UI 업데이트관련 작업만 가능
                        uploadChatMessage(read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadChatMessage(String message) {

        String SERVER_URL = "";


        //메시지에 따라 DB저장하는 방법을 다르게 한다.
        //* initMessageAndMakeRoom  : 방을 insert 하고 -> participate_in 에 insert (트랜잭션)
        //* initConnect / disconnect: 입장했다는 메시지를 표시하기 위한 것이므로 DB에 저장하지 않는다. -> participate_in 에 insert or update
        //* 그 외의 contentType 은 txt와 img에 따라 구분하면서 저장한다. -> insert message

        //*** response메시지는 "[contentType] 이 성공/실패 하였습니다."

        String[] filter = message.split("@");
        String roomId = filter[0];
        CHATTING_ROOM_ID = Integer.parseInt(roomId);
        String senderUserId = filter[1];
        String contentType = filter[2];
        String content = filter[3];
        //상대방 아이디도 넣어준다.
        String friendId = UserInfo.getInstance().getClickedId();


        if(contentType.equals("initConnectAndMakeRoom")) { //1.처음 방을 만들고 입장할 때
            SERVER_URL = "http://3.36.92.185/chattingdata/chatroom_upload.php";
        } else if(contentType.equals("initConnect")) { // 2.만들어진 방에 처음 입장할때
            SERVER_URL = "http://3.36.92.185/chattingdata/participate_in_upload.php";
        } else if (contentType.equals("txt") || contentType.equals("img")){
            if(senderUserId.equals(MY_ID)) {
                SERVER_URL = "http://3.36.92.185/chattingdata/message_upload.php";
            } else {
                return;
            }
        } else {
            //방을 나간 경우 저장할 필요가 없다.
            return;
        }



        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), response,Toast.LENGTH_SHORT).show();

                //BusProvider.getInstance().post(new BusEvent(true));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();


                //String postId = UserInfo.getInstance().getId();
                param.put("roomId", roomId); //todo roomId 를 String? int?
                param.put("myId", senderUserId);
                param.put("friendId", friendId);
                param.put("contentType", contentType);
                param.put("content", content);

                /*
                CHATTING_ROOM_ID = Integer.parseInt(roomId);
                String senderUserId = filter[1];
                String contentType = filter[2];
                String content = filter[3];
                 */

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }

    // 받은 메시지 출력
//    class msgUpdate implements Runnable {
//        private String msg;
//        public msgUpdate(String str) {
//            this.msg = str;
//        }
//        public void run() {
//            m_Adapter.add(et_text.getText().toString() + msg, 1);
//            m_Adapter.notifyDataSetChanged();
//        }
//    };


    //채팅방을 나가는 경우 는 2가지
    //1. 뒤로가기로 빠져나가거나
    //2. 앱을 꺼버려 onDestroy()가 호출될때
    // 생명주기 에 따라 뒤로가기가 호출되면 그다음에 onPause -> onStop -> onDestroy 가 호출된다.
    // 그렇기 때문에 뒤로가기를 누른 경우와 그냥 나간 경우를 구분해서 호출해줘야 한다.
    @Override
    public void onBackPressed() {
        super.onBackPressed();

            if (out == null) return;
            String message = CHATTING_ROOM_ID + "@" + MY_ID + "@" + "disconnect" + "@" + "/&quit";
            SendThread st = new SendThread(message, out);
            st.start();

            //user_status 1로 업데이트 한다.
            userStatusUpdate();

    }


    private void userStatusUpdate() {

        String SERVER_URL = "http://3.36.92.185/chattingdata/user_status_upload.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), response,Toast.LENGTH_SHORT).show();
                //BusProvider.getInstance().post(new BusEvent(true));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();


                //String postId = UserInfo.getInstance().getId();
                param.put("roomId", String.valueOf(CHATTING_ROOM_ID));
                param.put("myId", MY_ID);

                /*
                CHATTING_ROOM_ID = Integer.parseInt(roomId);
                String senderUserId = filter[1];
                String contentType = filter[2];
                String content = filter[3];
                 */

                return param;
            }
        };

        //실제 요청 작업을 수행해주는 요청큐 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        //요청큐에 요청 객체 생성
        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View v) {
        String message;

        switch (v.getId()) {
            case R.id.sendButton:
                if(out == null) return;

                message= et_text.getText().toString();
                //개행문자 처리
                message = message.replaceAll(System.getProperty("line.separator"), " ");
                message = CHATTING_ROOM_ID + "@" + MY_ID + "@" + "txt" + "@" + message;
                SendThread st = new SendThread(message, out);
                st.start();

                break;
            case R.id.imgButton:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            if(resultCode == RESULT_OK) {
                String filePath = getRealPathFromURI(data.getData());
                //sendPicture를 스레드로 생성하면 While문이 있어 끝나지 않는다.
//           SendPictureThread sendPicThread = new SendPictureThread(imagePath, outputStream);
//           sendPicThread.start();

                Log.d("Volley 정보", filePath + " , " + MY_ID);
                sendImageFile(filePath, MY_ID);
            }
           //sendPicture(filePath);
        }

    }

    private void sendImageFile(final String filePath, final String userId) {
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, ROOT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //new AlertDialog.Builder(DiaryPostActivity.this).setMessage("응답:"+response).create().show();
                //데이를 보낼 곳에 Boolean 값을 준다.
                try {
                    JSONObject obj = new JSONObject(response);
                    String image_path = obj.getString("file_path");
                    String image_width = obj.getString("file_width");
                    String image_height = obj.getString("file_height");

                    Log.d("image width, height", image_width + " , " + image_height);

                    String message =  CHATTING_ROOM_ID + "@" + userId + "@" + "img" +"@"+ image_path;
                    SendThread st = new SendThread(message, out);
                    st.start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("userId", userId);
        //이미지 파일 추가
        smpr.addFile("image", filePath);

        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(smpr);

    }

    private void sendPicture(String imgUri) {
        try {
            //String imagePath = getRealPathFromURI(imgUri); //  imgUri =>  content://media/external/images/media/1578
            Log.e(TAG, imgUri); // imagePath => /storage/emulated/0/DCIM/Camera/20210222_164527.jpg

            out.println("/img");

            File imgFile = new File(imgUri);
            DataInputStream dis = new DataInputStream(new FileInputStream(imgFile));

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            byte[] buf = new byte[1024];

            long totalReadBytes = 0;

            int readBytes;
            while ((readBytes = dis.read(buf)) > 0) { //길이 정해주고 딱 맞게 서버로 보냅니다.
                dos.write(buf, 0, readBytes);

                totalReadBytes += readBytes;
            }

            Log.d("사진", Long.toString(totalReadBytes) + "만큼 전송되었다.");

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