package com.duke.bookmark.bean;

import com.duke.bookmark.base.BaseModel;

public class VersionResponse extends BaseModel {

    private int versionCode;
    private String updateTitle;
    private String updateMessage;
    private long totalBytes;
    private String downloadUrl;
    private String forceUpdate;//1强制更新，不显示cancel

    /*{
        "versionCode": 2,
        "totalBytes": 2,
        "forceUpdate": 1,
            "updateTitle": "有新的更新",
            "updateMessage": "测试更新内容",
            "downloadUrl": "https://github.com/gj009351/YinYangLi/blob/master/app/release/app-release.apk"
    }*/
    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
