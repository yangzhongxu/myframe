package com.yzx.frames.tool.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yzx.frames.tool.Tool;

public class ExceptionCatcher implements UncaughtExceptionHandler {

	private static boolean hasInit = false;

	public synchronized static void init(Runnable onKillAppListener, String toSendUrl) {
		if (hasInit)
			return;
		hasInit = true;
		ExceptionCatcher ec = new ExceptionCatcher(onKillAppListener, toSendUrl);
		Thread.setDefaultUncaughtExceptionHandler(ec);
	}

	private Runnable onKillAppListener;
	private String toSendUrl;

	private ExceptionCatcher(Runnable onKillAppListener, String toSendUrl) {
		this.onKillAppListener = onKillAppListener;
		this.toSendUrl = toSendUrl;
	}

	@Override
	public void uncaughtException(Thread thread, final Throwable ex) {
		// 发送错误消息
		sendErrorInfo(ex);
		// 外部将Activity等界面kill掉
		if (onKillAppListener != null)
			onKillAppListener.run();
	}

	/**
	 * 发送错误信息
	 * 
	 */
	private void sendErrorInfo(final Throwable ex) {

		RequestParams params = new RequestParams();
		params.put("error", Tool.getErrorInfo(ex));

		new AsyncHttpClient().post(toSendUrl, params, new AsyncHttpResponseHandler() {
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			}

			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}
		});
	}
}
