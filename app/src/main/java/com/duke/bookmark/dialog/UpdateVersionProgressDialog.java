package com.duke.bookmark.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.duke.bookmark.R;
import com.duke.bookmark.listener.ProgressResponseListener;
import com.duke.bookmark.utils.AppUtils;
import com.duke.bookmark.utils.FileUtils;
import com.duke.bookmark.utils.HttpUtils;
import com.duke.bookmark.utils.LogUtils;
import com.duke.bookmark.utils.MD5Utils;
import com.duke.bookmark.view.NumberProgressBar;

public class UpdateVersionProgressDialog extends Dialog {

    private NumberProgressBar progressBar;

    public UpdateVersionProgressDialog(@NonNull Context context) {
        super(context, R.style.dialog_style);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.dialog_update_version_progress);

        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            Window window = getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                if (params != null) {
                    params.gravity = Gravity.CENTER;
                    window.setAttributes(params);
                }
            }
        }
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        progressBar = findViewById(R.id.number_progress_bar);
        progressBar.setMax(100);

    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public static UpdateVersionProgressDialog show(Context context) {
        UpdateVersionProgressDialog dialog = new UpdateVersionProgressDialog(context);
        dialog.show();
        return dialog;
    }
}
