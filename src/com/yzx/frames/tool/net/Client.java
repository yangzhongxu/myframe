package com.yzx.frames.tool.net;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class Client {

	/** GET请求 */
	public static RequestHandle get(String url, RequestParams params, NetCallBack callback) {
		RequestHandle handle = getClient().get(url, params, getResponseHandler(callback));
		return handle;
	}

	/** POST请求 */
	public static RequestHandle post(String url, RequestParams params, NetCallBack callback) {
		RequestHandle handle = getClient().post(url, params, getResponseHandler(callback));
		return handle;
	}

	/* 获取AsyncHttpClient 实例 */
	private static AsyncHttpClient getClient() {
		AsyncHttpClient ahc = new AsyncHttpClient();
		ahc.setConnectTimeout(4000);
		ahc.setMaxConnections(1);
		return ahc;
	}

	/* 获取AsyncHttpClient回调Handler */
	private static AsyncHttpResponseHandler getResponseHandler(final NetCallBack callback) {
		return new AsyncHttpResponseHandler() {
			public void onSuccess(int code, Header[] arg1, byte[] bytes) {
				if (code < 200 || code > 399) {
					if (callback != null)
						callback.onError(false);
					return;
				}
				if (callback != null)
					try {
						if (bytes != null)
							callback.onSuccess(new String(bytes).trim());
						else
							throw new Exception();
					} catch (Exception e) {
						callback.onSuccess("");
					}
			}

			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (callback != null)
					callback.onError(arg0 == 0);
			}
		};
	}

}
