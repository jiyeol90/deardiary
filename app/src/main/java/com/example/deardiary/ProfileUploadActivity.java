package com.example.deardiary;
import android.text.TextUtils;
import android.widget.EditText;

import com.bumptech.glide.Glide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUploadActivity extends AppCompatActivity implements Button.OnClickListener {

    private static final String ROOT_URL = "http://3.36.92.185/uploads/profile_upload.php";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;


    private Uri mImageCaptureUri;
    private CircleImageView iv_UserPhoto;
    private Button btn_upload;
    private Button btn_save;
    private EditText et_profileText;

    private String profileSrc;
    private String profileText;
    private String absoultePath;

    private File photoFile;
    private String imageFilePath;
    private Uri photoUri;
    String userId;
    String userText;

    @Override

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_upload);

        //권한 설정
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setDeniedMessage(("[설정] > [권한] 에서 권한을 허용할 수 있습니다."))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        iv_UserPhoto = (CircleImageView) this.findViewById(R.id.user_image);
        btn_upload = (Button) this.findViewById(R.id.btn_UploadPicture);
        btn_save = (Button) this.findViewById(R.id.btn_savePicture);
        et_profileText = (EditText) this.findViewById(R.id.et_profileText);

        profileSrc = UserInfo.getInstance().getUserProfile();
        profileText = UserInfo.getInstance().getUserText();
        if(!profileSrc.equals("")) {
            //이미지 url이 있으면 호출한다.
            Glide.with(this).load("http://3.36.92.185"+profileSrc).into(iv_UserPhoto);
        }
        if(!profileSrc.equals("")) {
            et_profileText.setText(profileText);
        }

        btn_upload.setOnClickListener(this);
        btn_save.setOnClickListener(this);

    }

    /**

     * 카메라에서 사진 촬영

     */

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기

    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if(photoFile != null) {
            mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.deardiary.fileprovider", photoFile);

            Log.i("mImageCaptureUri :", mImageCaptureUri.toString());
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "tmp_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory()+ "/deardiary/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }


    /**

     * 앨범에서 이미지 가져오기

     */

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기

    {
        // 앨범 호출

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);


        if(resultCode != RESULT_OK)

            return;


        switch(requestCode)

        {

            case PICK_FROM_ALBUM:

            {

                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.

                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();

                Log.d("SmartWheel",mImageCaptureUri.getPath().toString());

            }


            case PICK_FROM_CAMERA:

            {

                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.

                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");

                intent.setDataAndType(mImageCaptureUri, "image/*");


                // CROP할 이미지를 200*200 크기로 저장

                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기

                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기

                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율

                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율

                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동

                break;

            }

            case CROP_FROM_iMAGE:

            {

                // 크롭이 된 이후의 이미지를 넘겨 받습니다.

                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에

                // 임시 파일을 삭제합니다.

                if(resultCode != RESULT_OK) {

                    return;

                }


                final Bundle extras = data.getExtras();


                // CROP된 이미지를 저장하기 위한 FILE 경로

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+

                        "/SmartWheel/"+System.currentTimeMillis()+".jpg";


                if(extras != null)

                {

                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP

                    iv_UserPhoto.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌


                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.

                    absoultePath = filePath;
                    Log.i("경로 in OnActivityResult", filePath);
                    btn_save.setVisibility(View.VISIBLE);
                    break;


                }

                // 임시 파일 삭제

                File f = new File(mImageCaptureUri.getPath());

                if(f.exists())

                {

                    f.delete();

                }
            }
        }
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_UploadPicture) {

            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {

                    doTakePhotoAction();

                }

            };

            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();

                }

            };


            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }

            };


            new AlertDialog.Builder(this)

                    .setTitle("업로드할 이미지 선택")

                    //Todo 사진촬영 기능 구현할 것
                    //.setPositiveButton("사진촬영", cameraListener)

                    .setNeutralButton("앨범선택", albumListener)

                    .setNegativeButton("취소", cancelListener)

                    .show();

        }else if (v.getId() == R.id.btn_savePicture) {
            if(iv_UserPhoto.getDrawable() instanceof VectorDrawable) {
                Toast.makeText(getApplicationContext(), "이미지를 등록하세요.", Toast.LENGTH_SHORT).show();
            } else {
                userId = UserInfo.getInstance().getId();
                userText = et_profileText.getText().toString();

                Log.i("data : ", userText);
                savePicture(userId, userText);
            }
        }

    }

    private void savePicture(String userId, String userText) {

            SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, ROOT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //new AlertDialog.Builder(DiaryPostActivity.this).setMessage("응답:"+response).create().show();
                    //이벤트 버스를 송신한다.
                    BusProvider.getInstance().post(new BusEvent(true));
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            //요청 객체에 보낼 데이터를 추가

            smpr.addStringParam("userId", userId);
            smpr.addStringParam("userText", userText);
            //이미지 파일 추가
            //기존 이미지를 저장하기 누른다면 따로 저장해줄 필요가 없다.
            if(!TextUtils.isEmpty(absoultePath)) {
                smpr.addFile("image", absoultePath);
            }

            //요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(smpr);


    }

    /*

     * Bitmap을 저장하는 부분

     */

    private void storeCropImage(Bitmap bitmap, String filePath) {

        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";

        File directory_SmartWheel = new File(dirPath);


        if(!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)

            directory_SmartWheel.mkdir();


        File copyFile = new File(filePath);

        BufferedOutputStream out = null;


        try {

            copyFile.createNewFile();

            out = new BufferedOutputStream(new FileOutputStream(copyFile));

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,

                    Uri.fromFile(copyFile)));

            //ex) content://media/external/images/media/2874
            filePath = (Uri.fromFile(copyFile).toString()).substring(7);

            //Log.i("크롭된 이미지 절대 경로", Uri.fromFile(copyFile).toString());
            Log.i("경로 in storeCropImage", filePath);

            out.flush();

            out.close();



        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}