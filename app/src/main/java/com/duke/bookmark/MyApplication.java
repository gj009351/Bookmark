package com.duke.bookmark;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.GzipRequestInterceptor;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
    }
}
