package com.example.deardiary;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SendPictureThread extends Thread{

        private String imgPath;
        private OutputStream outputStream;

        private final String TAG = "SendPictureThread";


        public SendPictureThread(String imgPath, OutputStream outputStream) {
            this.imgPath = imgPath;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            try {
                //전송할 파일의 크기를 클라이언트 쪽에서 알 수 없으므로, 먼저 파일의 크기를 전송하고, 파일의 내용을 보낸다.

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
                out.println("/img");

                Log.e(TAG, imgPath); // imagePath => /storage/emulated/0/DCIM/Camera/20210222_164527.jpg
                File imgFile = new File(imgPath);
                DataInputStream dis = new DataInputStream(new FileInputStream(imgFile));

                DataOutputStream dos = new DataOutputStream(outputStream);

                byte[] buf = new byte[1024];

                long totalReadBytes = 0;

                int readBytes;
                while ((readBytes = dis.read(buf)) > 0) { //길이 정해주고 딱 맞게 서버로 보냅니다.
                    dos.write(buf, 0, readBytes);
                    dos.flush();
                    totalReadBytes += readBytes;
                }


                //dos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }//run method..

}
