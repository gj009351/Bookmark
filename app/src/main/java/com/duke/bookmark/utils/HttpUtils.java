package com.duke.bookmark.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.duke.bookmark.R;
import com.duke.bookmark.listener.HttpResponseHandler;
import com.duke.bookmark.listener.ProgressResponseListener;
import com.duke.bookmark.listener.ResponseListener;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求帮助类，封装本地库AndroidNetwork
 */
public class HttpUtils {

    private final static String TAG = HttpUtils.class.getSimpleName();

    public static final int CONNECT_TIME_OUT = 30;

    public static int STATE_SUCCESS = 0;
    public static int STATE_FAILED = -100;
    public static int STATE_JSON_ERROR = -200;
    public static int STATE_NET_ERROR = -300;

    public static String CONTENT_TYPE_JSON = "application/json;charset=utf-8";


    public static ANRequest post(Context context, String url, JSONObject bodyParameter
            , ResponseListener responseListener, Object... extra) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onFailure(url, String.valueOf(STATE_NET_ERROR)
                        , context.getString(R.string.error_internet), null);
            }
            return null;
        }
        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(url)
                .setTag(context)
                .setPriority(Priority.MEDIUM)
                .setContentType(CONTENT_TYPE_JSON)
                .addJSONObjectBody(bodyParameter);
        ANRequest request = builder.build();
        request.setAnalyticsListener(new RequestAnalyticsListener());
        request.getAsString(new HttpResponseHandler(url, responseListener, extra));

        return request;
    }

    public static ANRequest get(Context context, String url, Map<String, String> queryParameterMap, ResponseListener responseListener, Object... extra) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onFailure(url, String.valueOf(STATE_NET_ERROR), context.getString(R.string.error_internet), null);
            }
            return null;
        }
        if (LogUtils.ALLOW_LOG) {
            LogUtils.d(url, queryParameterMap);
        }
        ANRequest.GetRequestBuilder builder = AndroidNetworking.get(url)
                .setTag(context)
                .setPriority(Priority.LOW)
                .addQueryParameter(getRequestParams(queryParameterMap));
        ANRequest request = builder.build()
                .setAnalyticsListener(new RequestAnalyticsListener());
        request.getAsString(new HttpResponseHandler(url, responseListener, extra));

        return request;
    }

    public static ANRequest postObj(Context context, String url, Object object, ResponseListener responseListener, Object... extra) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onFailure(url, String.valueOf(STATE_NET_ERROR), context.getString(R.string.error_internet), null);
            }
            return null;
        }
//        if (LogUtils.ALLOW_LOG) {
//            LogUtils.d(makeUrl(url, queryParameterMap));
//        }
        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(url)
                .setTag(context)
                .setPriority(Priority.MEDIUM)
                .setContentType(CONTENT_TYPE_JSON)
                .addApplicationJsonBody(object)
                .addQueryParameter(getRequestParams(null));

        ANRequest request = builder.build();
        request.setAnalyticsListener(new RequestAnalyticsListener());
        request.getAsString(new HttpResponseHandler(url, responseListener, extra));

        return request;
    }

    public static ANRequest postJsonObj(Context context, String url, JSONObject object, ResponseListener responseListener, Object... extra) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onFailure(url, String.valueOf(STATE_NET_ERROR), context.getString(R.string.error_internet), null);
            }
            return null;
        }
        if (LogUtils.ALLOW_LOG) {
            LogUtils.d(url, object);
        }
        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(url)
                .setTag(context)
                .setPriority(Priority.MEDIUM)
                .setContentType(CONTENT_TYPE_JSON)
                .addJSONObjectBody(object)
                .addQueryParameter(getRequestParams(null));

        ANRequest request = builder.build();
        request.setAnalyticsListener(new RequestAnalyticsListener());
        request.getAsString(new HttpResponseHandler(url, responseListener, extra));

        return request;
    }

    public static ANRequest upload(Context context, String url, Map<String, File> files, Map<String, String> params, ResponseListener responseListener, Object... extra) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onFailure(url, String.valueOf(STATE_NET_ERROR), context.getString(R.string.error_internet), null);
            }
            return null;
        }

        ANRequest.MultiPartBuilder builder = AndroidNetworking.upload(url)
                .setTag(context)
                .setPriority(Priority.HIGH)
                .addHeaders("Connection", "close")
                .addMultipartFile(files)
                .addMultipartParameter(params)
                .addQueryParameter(getRequestParams(null));

        ANRequest request = builder.build();
        request.setAnalyticsListener(new RequestAnalyticsListener());
        request.getAsString(new HttpResponseHandler(url, responseListener, extra));

        return request;
    }

    public static ANRequest download(Context context, String url, String dirPath, String fileName
            , ProgressResponseListener responseListener) {
        if (!NetWorkUtils.isNetworkAvailable(context)) {
            if (responseListener != null) {
                responseListener.onError(new ANError(Integer.toString(STATE_NET_ERROR)));
            }
            return null;
        }

        ANRequest request = AndroidNetworking.download(url, dirPath, fileName)
                .setTag("download")
                .setPriority(Priority.MEDIUM)
                .build();
        request.setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        if (responseListener != null) {
                            responseListener.onProgress(bytesDownloaded, totalBytes);
                        }
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        if (responseListener != null) {
                            responseListener.onDownloadComplete((dirPath + "/" + fileName));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if (responseListener != null) {
                            responseListener.onError(error);
                        }
                    }
                });
        return request;
    }

    public static class RequestAnalyticsListener implements AnalyticsListener {
        @Override
        public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
            Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
            Log.d(TAG, " bytesSent : " + bytesSent);
            Log.d(TAG, " bytesReceived : " + bytesReceived);
            Log.d(TAG, " isFromCache : " + isFromCache);
        }
    }

    public static Map<String, String> getRequestParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        params.put("clientVersion", "1");

        return params;
    }

}
