package com.duke.bookmark.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.duke.bookmark.base.BaseEvent;
import com.duke.bookmark.constants.Event;
import com.duke.bookmark.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

public class TalkReceiver extends BroadcastReceiver {

    private static final String TAG = "TalkReceiver:";
    /*{
        "versionCode": 2,
            "updateTitle": "有新的更新",
            "updateMessage": "测试更新内容",
            "downloadUrl": "https://github.com/gj009351/Bookmark/blob/master/note.png"
    }
*/

    private NotificationManager nm;
     
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG + "JPush 用户注册成功," + intent.getAction());
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
         
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            LogUtils.d(TAG + "JPush 用户注册成功");
             
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtils.d(TAG + "接受到推送下来的自定义消息");
             
            // Push Talk messages are push down by custom message format
            processCustomMessage(context, bundle);
         
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtils.d(TAG + "接受到推送下来的通知");
     
//            receivingNotification(context,bundle);
 
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtils.d(TAG + "用户点击打开了通知");
        
//           openNotification(context,bundle);
 
        } else {
            LogUtils.d(TAG + "Unhandled intent - " + intent.getAction());
        }
    }
     
    private void processCustomMessage(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if (TextUtils.isEmpty(message)) {
            LogUtils.w(TAG + "Unexpected: empty title (friend). Give up");
            return;
        }
        EventBus.getDefault().post(new BaseEvent(Event.CODE_UPDATE_VERSION, bundle));

    }
     
}