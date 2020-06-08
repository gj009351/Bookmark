package com.duke.bookmark.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.duke.bookmark.R;
import com.duke.bookmark.listener.ProgressResponseListener;
import com.duke.bookmark.utils.AppUtils;
import com.duke.bookmark.utils.FileUtils;
import com.duke.bookmark.utils.HttpUtils;
import com.duke.bookmark.utils.MD5Utils;
import com.duke.bookmark.view.NumberProgressBar;

public class UpdateVersionProgressDialog extends Dialog {

    private NumberProgressBar progressBar;

    public UpdateVersionProgressDialog(@NonNull Context context, String downloadUrl) {
        super(context);
        init(context, downloadUrl);
    }

    private void init(Context context, String downloadUrl) {
        setContentView(R.layout.dialog_update_version_progress);
        progressBar = findViewById(R.id.number_progress_bar);
        progressBar.setMax(100);

        String fileName = MD5Utils.getMD5(downloadUrl) + FileUtils.getFileTypeName(downloadUrl);
        HttpUtils.download(context
                , downloadUrl
                , FileUtils.getRootFilePath(context)
                , fileName, new ProgressResponseListener() {
                    @Override
                    public void onDownloadComplete(String file) {
                        dismiss();
                        //install
                        AppUtils.installApk(context, file);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }

                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        progressBar.setProgress((int) (bytesDownloaded * 100 / totalBytes));
                    }
                });
    }

    public static void show(Context context, String downloadUrl) {
        UpdateVersionProgressDialog dialog = new UpdateVersionProgressDialog(context, downloadUrl);
        dialog.show();
    }
}
