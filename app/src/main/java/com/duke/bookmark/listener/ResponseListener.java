package com.duke.bookmark.listener;

/**
 * 请求接口的回调
 */
public interface ResponseListener {

	void onSuccess(String url, String result, Object... extra);

	void onFailure(String url, String errorCode, String error, String result, Object... extra);

}
