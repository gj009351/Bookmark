package com.duke.bookmark.listener;


import com.androidnetworking.error.ANError;

/**
 * 带进度的回调，可用于上传下载
 */
public interface ProgressResponseListener {

    void onDownloadComplete(String file);

    void onError(ANError anError);

    void onProgress(long bytesDownloaded, long totalBytes);
}
