package com.duke.bookmark.listener;

import android.text.TextUtils;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.duke.bookmark.base.BaseModel;
import com.duke.bookmark.utils.HttpUtils;
import com.duke.bookmark.utils.JsonUtils;

/**
 *  统一处理接口返回数据，并回调给activity或fragment
 */
public class HttpResponseHandler implements StringRequestListener {
    private ResponseListener listener;
    private String url;
    private Object[] extra;

    public HttpResponseHandler(String url, ResponseListener listener, Object... extra) {
        this.listener = listener;
        this.url = url;
        this.extra = extra;
    }

    @Override
    public void onResponse(String response) {
        BaseModel model = new BaseModel();
        try {
            model = JsonUtils.fromJson(response, BaseModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            model = new BaseModel();
            model.setCode(String.valueOf(HttpUtils.STATE_JSON_ERROR));
        }
        if (model != null) {
            if (String.valueOf(HttpUtils.STATE_SUCCESS).equals(model.getCode())) {
                success(response);
            } else {
                fail(model.getCode(), model.getMsg(), response);
            }
        } else {
            fail(String.valueOf(HttpUtils.STATE_JSON_ERROR), "", response);
        }
    }

    @Override
    public void onError(ANError anError) {
        if (anError != null) {
            fail(String.valueOf(anError.getErrorCode()), anError.getErrorDetail(), null);
        } else {
            fail(String.valueOf(HttpUtils.STATE_FAILED), null, null);
        }

    }

    private void success(String result) {
        if (listener != null) {
            listener.onSuccess(url, result, extra);
        }
    }

    private void fail(String errorCode, String error, String result) {
        if (listener != null && !TextUtils.isEmpty(url)) {
            listener.onFailure(url, errorCode, error, result, extra);
        }
    }


}
