package com.duke.bookmark.receiver;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.duke.bookmark.utils.LogUtils;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.service.JPushMessageReceiver;

public class JpushReceiver extends JPushMessageReceiver {

    private String TAG = "JpushReceiver:";

    @Override
    public Notification getNotification(Context var1, NotificationMessage var2) {
        LogUtils.d(TAG + "getNotification:" + var2);
        return null;
    }

    @Override
    public void onMessage(Context var1, CustomMessage var2) {
        ActionHelper.getInstance().onMessage(var1, var2);
        LogUtils.d(TAG + "onMessage:" + var2);
    }

    @Override
    public void onNotifyMessageOpened(Context var1, NotificationMessage var2) {
        ActionHelper.getInstance().onNotifyMessageOpened(var1, var2);
        LogUtils.d(TAG + "onNotifyMessageOpened:" + var2);
    }

    @Override
    public void onNotifyMessageArrived(Context var1, NotificationMessage var2) {
        ActionHelper.getInstance().onNotifyMessageArrived(var1, var2);
        LogUtils.d(TAG + "onNotifyMessageArrived:" + var2);
    }

    @Override
    public void onNotifyMessageUnShow(Context var1, NotificationMessage var2) {
        LogUtils.d(TAG + "onNotifyMessageUnShow:" + var2);
    }

    @Override
    public void onNotifyMessageDismiss(Context var1, NotificationMessage var2) {
        LogUtils.d(TAG + "onNotifyMessageDismiss:" + var2);
    }

    @Override
    public void onRegister(Context var1, String var2) {
        LogUtils.d(TAG + "onRegister:" + var2);
    }

    @Override
    public void onConnected(Context var1, boolean var2) {
        LogUtils.d(TAG + "onConnected:" + var2);
    }

    @Override
    public void onCommandResult(Context var1, CmdMessage var2) {
        LogUtils.d(TAG + "onCommandResult:" + var2);
    }

    @Override
    public void onMultiActionClicked(Context var1, Intent var2) {
        ActionHelper.getInstance().onMultiAction(var1, var2);
        LogUtils.d(TAG + "onMultiActionClicked:" + var2);
    }

    @Override
    public void onTagOperatorResult(Context var1, JPushMessage var2) {
        LogUtils.d(TAG + "onTagOperatorResult:" + var2);
    }

    @Override
    public void onCheckTagOperatorResult(Context var1, JPushMessage var2) {
        LogUtils.d(TAG + "onCheckTagOperatorResult:" + var2);
    }

    @Override
    public void onAliasOperatorResult(Context var1, JPushMessage var2) {
        LogUtils.d(TAG + "onCheckTagOperatorResult:" + var2);
    }

    @Override
    public void onMobileNumberOperatorResult(Context var1, JPushMessage var2) {
        LogUtils.d(TAG + "onCheckTagOperatorResult:" + var2);
    }

    @Override
    public void onNotificationSettingsCheck(Context var1, boolean var2, int var3) {
        LogUtils.d(TAG + "onCheckTagOperatorResult:" + var2 + ", " + var3);
    }

    @Override
    public boolean isNeedShowNotification(Context var1, NotificationMessage var2, String var3) {
        LogUtils.d(TAG + "isNeedShowNotification:" + var2 + ", " + var3);
        return false;
    }
}
