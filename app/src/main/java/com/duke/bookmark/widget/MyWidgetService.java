package com.duke.bookmark.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Matteo on 12/08/2015.
 */
public class MyWidgetService extends RemoteViewsService {

    public MyWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyViewsFactory(this.getApplicationContext(), intent);
    }
}

