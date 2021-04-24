package com.example.deardiary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String server_ip;
    private String SERVER_URL;
    private HashMap<String, String>friendsMap;

    private String type;
    private String title;
    private String user_id;
    private String room_id;
    private String roomType;
    private String friendList;
    private String message;

    private Intent intent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
            //이벤트 버스
            BusProvider.getInstance().post(new ChatEvent(true));
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        type = remoteMessage.getData().get("contentType");

        title = remoteMessage.getData().get("title");

        user_id = remoteMessage.getData().get("receive");

        room_id = remoteMessage.getData().get("roomId");

        roomType = remoteMessage.getData().get("type");

        friendList = "";

        message = "";

        assert type != null;
        if(type.equals("img")) {
            message = "사진";
        } else {
            message = remoteMessage.getData().get("message");
        }


        UserInfo.getInstance().setClickedId(title); // title은 상대방 ID 이므로 다시 설정해준다.

        //Intent intent = new Intent(this, MyChattingActivity.class);
        intent = new Intent(this, AppStartActivity.class);


        if(roomType.equals("2")) {
            //단체방에서는 friendList를 받는다.
            friendList = remoteMessage.getData().get("friendList");
            friendList = friendList.replace(user_id, title);

            intent.putExtra("friend_id", friendList);

            //friendList에 있는 id와 프로필 의 hashmap을 만들어야 한다.
        } else {
            //1:1 방에서는 friendId를 받는다.
            friendList = title;
            intent.putExtra("friend_id", title);
        }

        intent.putExtra("user_id", user_id);
        intent.putExtra("room_id", room_id);

        //findFriendMap(friendList); -> 이유모를 Fragment오류때문에 되질 않는다.

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        final String CHANNEL_ID = "ChannelID";
        NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_NAME = "ChannerName";
            final String CHANNEL_DESCRIPTION = "ChannerDescription";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            // add in API level 26
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setColorized(true);
        builder.setColor(0xff123456);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.ic_group_chat);//R.mipmap.ic_launcher
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setContentTitle(title);
            builder.setVibrate(new long[]{500, 500});
        }
        mManager.notify(0, builder.build());

    }

    private void findFriendMap(String friends) {
        {

//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setCancelable(false);
//            progressDialog.setIndeterminate(false);
//            progressDialog.setTitle("Upload Comment");
//            progressDialog.show();

            server_ip = getString(R.string.server_ip);
            SERVER_URL = "http://"+server_ip+"/chattingdata/noti_friends_map.php";
            friendsMap = new HashMap<>();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용
                @Override
                public void onResponse(String response) {
//                    progressDialog.dismiss();

                    try {
                        JSONArray resJsonArray = new JSONArray(response);

                        for(int i = 0; i < resJsonArray.length(); i++) {
                            JSONObject jsonObject= resJsonArray.getJSONObject(i);

                            String friendId = jsonObject.getString("user_id");
                            String profileSrc = jsonObject.getString("user_profile");

                            Log.d("friendList", "friendId : " + friendId + ", profileSrc : " + profileSrc);

                            friendsMap.put(friendId, profileSrc);

                        }

                        intent.putExtra("user_id", user_id);
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("friendsMap", friendsMap);


                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        final String CHANNEL_ID = "ChannelID";
                        NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            final String CHANNEL_NAME = "ChannerName";
                            final String CHANNEL_DESCRIPTION = "ChannerDescription";
                            final int importance = NotificationManager.IMPORTANCE_HIGH;

                            // add in API level 26
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                            mChannel.setDescription(CHANNEL_DESCRIPTION);
                            mChannel.enableLights(true);
                            mChannel.enableVibration(true);
                            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                            mManager.createNotificationChannel(mChannel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                        builder.setColorized(true);
                        builder.setColor(0xff123456);
                        builder.setAutoCancel(true);
                        builder.setDefaults(Notification.DEFAULT_ALL);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setSmallIcon(R.drawable.ic_group_chat);//R.mipmap.ic_launcher
                        builder.setContentTitle(title);
                        builder.setContentText(message);
                        builder.setContentIntent(pendingIntent);

                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            builder.setContentTitle(title);
                            builder.setVibrate(new long[]{500, 500});
                        }
                        mManager.notify(0, builder.build());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   // Toast.makeText(getApplicationContext(), "response",Toast.LENGTH_SHORT).show();


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
                    param.put("friends", friends);

                    return param;
                }
            };

            //실제 요청 작업을 수행해주는 요청큐 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

            //요청큐에 요청 객체 생성
            requestQueue.add(stringRequest);

        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
