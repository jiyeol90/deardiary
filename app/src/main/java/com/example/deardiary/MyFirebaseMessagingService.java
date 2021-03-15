package com.example.deardiary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
            //이벤트 버스
            BusProvider.getInstance().post(new ChatEvent(true));
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String type = remoteMessage.getData().get("contentType");

        String title = remoteMessage.getData().get("title");

        String user_id = remoteMessage.getData().get("receive");

        String message;

        if(type.equals("img")) {
            message = "사진";
        } else {
            message = remoteMessage.getData().get("message");
        }


        UserInfo.getInstance().setClickedId(title); // title은 상대방 ID 이므로 다시 설정해준다.

        //Intent intent = new Intent(this, MyChattingActivity.class);
        Intent intent = new Intent(this, AppStartActivity.class);
        intent.putExtra("user_id", user_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
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

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
