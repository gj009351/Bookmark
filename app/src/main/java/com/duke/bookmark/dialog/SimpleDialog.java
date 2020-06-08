package com.duke.bookmark.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
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
        super(context);
        init(title, content, listener);
    }

    private void init(String titleText, String contentText, OnClickListener listener) {
        setContentView(R.layout.dialog_update_version);
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
