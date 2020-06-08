package com.duke.bookmark.base;

import android.os.Bundle;

public class BaseEvent {
    private int code;
    private Bundle bundle;

    public BaseEvent(int code) {
        this.code = code;
    }

    public BaseEvent(int code, Bundle bundle) {
        this.code = code;
        this.bundle = bundle;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
