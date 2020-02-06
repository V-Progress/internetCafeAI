package com.yunbiao.internetcafe_ai;

import android.app.Application;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initHttp();
    }

    private void initHttp(){
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(60 * 3, TimeUnit.SECONDS)
                .writeTimeout(60 * 3, TimeUnit.SECONDS)
                .readTimeout(60 * 3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        OkHttpUtils.initClient(build);
    }
}
