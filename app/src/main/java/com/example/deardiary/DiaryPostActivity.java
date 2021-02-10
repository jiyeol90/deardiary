package com.example.deardiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.VectorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DiaryPostActivity extends AppCompatActivity {

    final int REQUEST_WIDTH = 512;
    final int REQUEST_HEIGHT = 512;
    private static final String ROOT_URL = "http://3.36.92.185/uploads/file_upload.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;

    //사진을 업로드 하기위한 비트맵
    Bitmap bitmap;
    //이미지 뷰는 이미지 리소스를 가져올 수 없으므로 Bitmap으로 비교하기 위해
    String filePath;
    String textContent = "";
    String tag = "";
    String userId; // user아이디가


    ImageView diaryImage;
    MaterialEditText diaryText;
    MaterialEditText diaryTag;
    Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_post);
        //이벤트 버스를 등록해 준다.
        BusProvider.getInstance().register(this);

        diaryImage = findViewById(R.id.diary_image);
        diaryText = findViewById(R.id.comment_text);
        diaryTag = findViewById(R.id.diary_tag);
        postButton = findViewById(R.id.post_btn);

        //사진 이미지를 선택시
        diaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(DiaryPostActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(DiaryPostActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(DiaryPostActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }


            }
        });

        //포스팅 작성 버튼
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textContent = diaryText.getText().toString();
                tag = diaryTag.getText().toString();
                userId = UserInfo.getInstance().getId();

                Log.i("정보태그", "textContent : " + textContent + ", tag : " + tag);

                //등록한 이미지가 VectorDrawable이다. 사진을 등록하면 BitmapDrawable 이므로.
                if(diaryImage.getDrawable() instanceof VectorDrawable) {
                    Toast.makeText(getApplicationContext(), "이미지를 등록하세요.",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textContent)) {
                    Toast.makeText(getApplicationContext(), "일기를 등록하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    uploadBitmap(bitmap, textContent, tag, userId);
                }
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //갤러리앱에서 관리하는 DB정보가 있는데, 그것이 나온다 [실제 파일 경로가 아님!!]
            //얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
            //업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
            //Uri -->절대경로(String)로 변환
            Uri picUri = data.getData();
            //ex) content://media/external/images/media/2874
            filePath = getRealPathFromUri(picUri);
            Log.i("filePath: " , filePath);
            //ex) /storage/emulated/0/DCIM/Camera/20210122_011008.jpg
            ExifInterface exif = null;
            if (filePath != null) {
                try {
                    exif = new ExifInterface(filePath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);

                    //사진 리사이징
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // inJustDecodeBounds = true일때 BitmapFactory.decodeResource는 리턴하지 않는다.
                    //즉 bitmap은 반환하지않고, options 변수에만 값이 대입된다.

                    // = 디코딩 시 inJustDecodeBounds 속성을 true로 설정하면 메모리 할당이 방지된다.
                    // 비트맵 객체에 null이 반환되지만 outWidth, outHeight, outMimeType 은 설정되어 이 기법을 사용하면
                    // 비트맵을 생성(메모리 할당 포함)하기 전에 이미지 데이터의 크기와 유형을 읽을 수 있다.
                    // 즉 bitmap은 반환하지않고, options 변수에만 값이 대입된다.
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, options);

                    // 이미지 사이즈를 필요한 사이즈로 적당히 줄이기위해 계산한 값을
                    // options.inSampleSize 에 2의 배수의 값으로 넣어준다.
                    options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);

                    // options.inJustDecodeBounds 에 false 로 다시 설정해서 BitmapFactory.decodeResource의 Bitmap을 리턴받을 수 있게한다.
                    options.inJustDecodeBounds = false;
                    //사진 회전
                    bitmap = rotate(BitmapFactory.decodeFile(filePath, options), exifDegree);

                    //textView.setText("File Selected");

                    Log.d("filePath", String.valueOf(filePath));
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);

                    //bitmap = rotate(BitmapFactory.decodeFile(filePath),exifDegree);//경로를 통해 비트맵으로 전환
                    //bitmap = rotate(MediaStore.Images.Media.getBitmap(getContentResolver(), picUri),exifDegree);

                    //uploadBitmap(bitmap);
                    diaryImage.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(
                        DiaryPostActivity.this, "no image selected",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public String getRealPathFromUri(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//        return path;
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);

        Log.i("getPathResult", "" + result);
        cursor.close();
        return result;
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /*
    //포스팅 사진 업로드
    private void uploadBitmap(final Bitmap bitmap, final String content, final String tag, final String index) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> param = new HashMap<>();
                long imagename = System.currentTimeMillis();
                param.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return param;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("content", content);
                params.put("tag", tag);
                params.put("index", index);
                Log.i("정보태그", "textContent : " + content + ", tag : " + tag);
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
*/


    private void uploadBitmap(final Bitmap bitmap, final String content, final String tag, final String userId) {
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, ROOT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //new AlertDialog.Builder(DiaryPostActivity.this).setMessage("응답:"+response).create().show();
                //데이를 보낼 곳에 Boolean 값을 준다.
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
        smpr.addStringParam("content", content);
        smpr.addStringParam("tag", tag);
        smpr.addStringParam("userId", userId);
        //이미지 파일 추가
        smpr.addFile("image", filePath);

        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(smpr);

    }

    //사진의 회전값 가져오기 - 회전값을 처리하지 않으면 사진을 찍은 방향대로 이미지뷰에 처리되지 않는다.
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //사진을 정방향대로 회전하기
    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    // 이미지 Resize 함수
    private int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight) {
        // 이미지 사이즈를 체크할 원본 이미지 가로/세로 사이즈를 임시 변수에 대입.
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // 원본 이미지 비율인 1로 초기화
        int size = 1;

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
        while (requestWidth < originalWidth || requestHeight < originalHeight) {
            originalWidth = originalWidth / 2;
            originalHeight = originalHeight / 2;

            size = size * 2;
        }
        return size;
    }

}