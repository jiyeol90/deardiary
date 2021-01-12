package com.example.deardiary;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.lang.annotation.Documented;

public class MySingleton {

    //Volley는 하나의 Queue에서 모든 요청을 처리하므로 Queue는 하나만 만들어 쓰기 위해 Singleton패턴을 사용한다.

    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    public MySingleton(Context mCtx) {
        this.mCtx = mCtx;
        mRequestQueue = getmRequestQueue();
    }

    // RequestQueue를 생성하여 custom action을 하기위해서는
    // 1.cache -> DiskBasedCache
    // 2.network -> BasicNetwork
    // 가 필요하다.

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            // 캐시 초기화하기
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 1024*1024); // 1MB cap
            // HttpURLConnection을 HTTP 클라이언트로 사용하기 위한 초기화
            Network network = new BasicNetwork(new HurlStack());
            // 초기화된 캐시와 네트워크로 요청 큐 초기화하기
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            //Volley의 편의 메소드를 이용한 RequestQueue 생성 방법
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        }
        return mRequestQueue;
    }

    public static synchronized MySingleton getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }

        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        mRequestQueue.add(request);
    }

}