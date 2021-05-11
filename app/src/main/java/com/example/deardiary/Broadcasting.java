package com.example.deardiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bambuser.broadcaster.BroadcastStatus;
import com.bambuser.broadcaster.Broadcaster;
import com.bambuser.broadcaster.CameraError;
import com.bambuser.broadcaster.ConnectionError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Broadcasting extends AppCompatActivity {

    //private static final String FRONT_CAMERA = "3"; //전면 카메라 ID => 기기별 카메라 갯수가 다르므로 옳은 방식이 될 수 없다.
    private String FRONT_CAMERA;
    private static final String REAR_CAMERA = "0"; //후면 카메라 ID
    private String author;
    private String title;

    SurfaceView mPreviewSurface;
    Broadcaster mBroadcaster;
    Button mBroadcastButton;
    Button mSwitchButton;
    Button mZoomIn;
    Button mZoomOut;


    private List<Broadcaster.Camera> cameraList;
    private int zoomInfo = 100;
    private static final String APPLICATION_ID = "ZaZrnb7KSnYrJZkUDVunJA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcasting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 켜진 상태로 유지

        mPreviewSurface = findViewById(R.id.PreviewSurfaceView);

        mBroadcaster = new Broadcaster(this, APPLICATION_ID, mBroadcasterObserver);
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());

        author = UserInfo.getInstance().getId();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd a hh:mm");
        String getTime = simpleDate.format(date);
        title = getTime;
        //Toast.makeText(getApplicationContext(),  mBroadcaster.getZoom(),Toast.LENGTH_SHORT).show();

        int cameraNum = mBroadcaster.getCameraCount();

        //FRONT_CAMERA = String.valueOf(cameraNum - 2); // 기기별 카메라 갯수가 다르므로 이와 같이 처리한다.
        //FRONT_CAMERA = "1";
                Toast.makeText(getApplicationContext(), "카메라 개수 : "+String.valueOf(cameraNum),Toast.LENGTH_SHORT).show();
        List<Broadcaster.Camera> cameraList = mBroadcaster.getSupportedCameras();
        for(int i = 0; i < cameraList.size(); i++)
        {
            String id = cameraList.get(i).id;
            Log.d("카메라정보", "id : " + id);
            String checkFront = cameraList.get(i).facing;
            Log.d("카메라정보", "FRONT : " + checkFront);
            if(checkFront.equals("front")) {
                FRONT_CAMERA = String.valueOf(i);
            }
        }

        Log.d("카메라정보", mBroadcaster.getCameraId());
        //카메라 전환 버튼
        mSwitchButton = findViewById(R.id.CameraSwitch);
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //줌 정보를 초기화 한다.
                zoomInfo = 100;

                String cameraId = mBroadcaster.getCameraId();

                mBroadcaster.setCameraId(cameraId.equals(REAR_CAMERA) ? FRONT_CAMERA : REAR_CAMERA);
            }
        });

        //방송버튼
        mBroadcastButton = findViewById(R.id.BroadcastButton);
        mBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBroadcaster.canStartBroadcasting()) {
                    mBroadcaster.startBroadcast();
                    mBroadcaster.setTitle(title);
                    mBroadcaster.setAuthor(author);
                }
                else
                    mBroadcaster.stopBroadcast();
            }
        });

        mZoomIn = findViewById(R.id.ZoomIn);
        mZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //줌인은 4배까지 가능하다.
                if(zoomInfo <= 300) {
                    zoomInfo += 100;
                    mBroadcaster.setZoom(zoomInfo);
                }
            }
        });

        mZoomOut = findViewById(R.id.ZoomOut);
        mZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zoomInfo >= 200) {
                    zoomInfo -= 100;
                    mBroadcaster.setZoom(zoomInfo);
                }
            }
        });

    }


    private Broadcaster.Observer mBroadcasterObserver = new Broadcaster.Observer() {
        @Override
        public void onConnectionStatusChange(BroadcastStatus broadcastStatus) {
            Log.i("Mybroadcastingapp", "Received status change: " + broadcastStatus);

            if (broadcastStatus == BroadcastStatus.STARTING)
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (broadcastStatus == BroadcastStatus.IDLE)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //Update the button label depending on broadcast state:
            mBroadcastButton.setText(broadcastStatus == BroadcastStatus.IDLE ? "Broadcast" : "Disconnect");
        }
        @Override
        public void onStreamHealthUpdate(int i) {
        }
        @Override
        public void onConnectionError(ConnectionError connectionError, String s) {
            Log.w("Mybroadcastingapp", "Received connection error: " + connectionError + ", " + s);
        }
        @Override
        public void onCameraError(CameraError cameraError) {
        }
        @Override
        public void onChatMessage(String s) {
            Log.d("chat", s);

        }
        @Override
        public void onResolutionsScanned() {
        }
        @Override
        public void onCameraPreviewStateChanged() {
        }
        @Override
        public void onBroadcastInfoAvailable(String s, String s1) {
        }
        @Override
        public void onBroadcastIdAvailable(String s) {
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        if (!hasPermission(Manifest.permission.CAMERA)
                && !hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.CAMERA))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);

        mBroadcaster.setCameraSurface(mPreviewSurface);
        mBroadcaster.onActivityResume();
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());
    }

    //퍼미션을 거부할 경우에는 onRequestPermissionsResult()를 구현해야 한다.

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBroadcaster.onActivityDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        mBroadcaster.onActivityPause();
    }

}