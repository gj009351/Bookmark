package com.duke.bookmark.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.duke.bookmark.R;

public class SimpleDialog extends Dialog {

    private TextView title;
    private TextView content;
    private TextView update;
    private TextView cancel;

    public SimpleDialog(@NonNull Context context) {
        this(context, "", "", null);
    }

    public SimpleDialog(Context context, String title, String content, OnClickListener listener) {
        super(context, R.style.dialog_style);
        initView(context, title, content, listener);
    }

    private void initView(Context context, String titleText, String contentText, OnClickListener listener) {
        setContentView(R.layout.dialog_update_version);

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


        title = findViewById(R.id.title);
        title.setText(titleText);
        content = findViewById(R.id.content);
        content.setText(contentText);
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public SimpleDialog showCancel(boolean show) {
        if (cancel != null) {
            cancel.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public SimpleDialog setConfirmText(String confirmText) {
        if (update != null) {
            update.setText(confirmText);
        }
        return this;
    }

    public static SimpleDialog init(Context context, String title, String content, OnClickListener listener) {
        SimpleDialog dialog = new SimpleDialog(context, title, content, listener);
        dialog.show();
        return dialog;
    }

    public static SimpleDialog show(Context context, String title, String content, OnClickListener listener) {
        SimpleDialog dialog = new SimpleDialog(context, title, content, listener);
        dialog.show();
        return dialog;
    }

    public interface OnClickListener{
        void onConfirm();
    }
}
