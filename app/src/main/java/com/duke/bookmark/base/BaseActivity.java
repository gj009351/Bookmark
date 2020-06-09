package com.duke.bookmark.base;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.duke.bookmark.BuildConfig;
import com.duke.bookmark.R;
import com.duke.bookmark.activity.MainActivity;
import com.duke.bookmark.bean.VersionResponse;
import com.duke.bookmark.constants.Event;
import com.duke.bookmark.dialog.SimpleDialog;
import com.duke.bookmark.dialog.UpdateVersionProgressDialog;
import com.duke.bookmark.utils.AppUtils;
import com.duke.bookmark.utils.FileUtils;
import com.duke.bookmark.utils.JsonUtils;
import com.duke.bookmark.utils.LogUtils;
import com.duke.bookmark.utils.MD5Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int RC_PERMISSIONS = 1002;
    private VersionResponse mVersionResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requiresPermissions();
    }

    private void requiresPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            ActivityCompat.requestPermissions(this, perms, RC_PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtils.d("onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
//            EasyPermissions.requestPermissions(this, getString(R.string.apply_for_permission_content), RC_PERMISSIONS, perms.toArray(new String[perms.size()]));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(BaseEvent event) {
        Bundle bundle = event.getBundle();
        if (event.getCode() == Event.CODE_UPDATE_VERSION) {
            if (bundle != null) {
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                if (!TextUtils.isEmpty(message)) {
                    VersionResponse response = JsonUtils.fromJson(message, VersionResponse.class);
                    if (response.getVersionCode() > BuildConfig.VERSION_CODE) {
                        showUpdateDialog(response);
                    }
                }
            }
        }
    }

    private void showUpdateDialog(VersionResponse response) {
        mVersionResponse = response;
//        String downloadUrl = response.getDownloadUrl();
//        String fileName = MD5Utils.getMD5(downloadUrl) + FileUtils.getFileTypeName(downloadUrl);
//        File file = new File(FileUtils.getRootFilePath(this), fileName);
//        if (file.exists()) {
//
//        }
        SimpleDialog.init(this, response.getUpdateTitle(), response.getUpdateMessage()
                , new SimpleDialog.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //是否有安装位置来源的权限
                            boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                            if (haveInstallPermission) {
                                LogUtils.i("8.0手机已经拥有安装未知来源应用的权限，直接安装！");
                                UpdateVersionProgressDialog.show(BaseActivity.this, response.getDownloadUrl());
                            } else {
                                SimpleDialog.init(BaseActivity.this, "", getString(R.string.install_app_tips), new SimpleDialog.OnClickListener() {
                                    @Override
                                    public void onConfirm() {
                                        Uri packageUri = Uri.parse("package:"+ getPackageName());
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageUri);
                                        startActivityForResult(intent,10086);
                                    }
                                })
                                        .setConfirmText(getString(R.string.sure))
                                        .show();
                            }
                        } else {
                            UpdateVersionProgressDialog.show(BaseActivity.this, response.getDownloadUrl());
                        }
                    }
                })
                .showCancel("1".equals(response.getForceUpdate()))
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            LogUtils.i("设置了安装未知应用后的回调。。。");
            if (mVersionResponse != null) {
                String downloadUrl = mVersionResponse.getDownloadUrl();
                String fileName = MD5Utils.getMD5(downloadUrl) + FileUtils.getFileTypeName(downloadUrl);
                File file = new File(FileUtils.getRootFilePath(this), fileName);
                if (file.exists()) {
                    AppUtils.installApk(this, file.getAbsolutePath());
                } else {
                    showUpdateDialog(mVersionResponse);
                }
            }
        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            requiresPermissions();
        }
    }

}
