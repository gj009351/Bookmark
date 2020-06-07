package com.duke.bookmark;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Matteo on 23/07/2015.
 */
public class EasyBookmarksApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        String firebaseUrl = getResources().getString(R.string.firebase_url);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        new Firebase(firebaseUrl + "/publicBookmarks").keepSynced(false);
        new Firebase(firebaseUrl + "/publicTags").keepSynced(false);
        new Firebase(firebaseUrl + "/publicTagsToBookmarks").keepSynced(false);
//        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }
}
