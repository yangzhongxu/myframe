package com.yzx.frames.tool.net.ok;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class BaseClient {

	private final int TIME_OUT_SECOND = 5;
	private final Handler mHandler = new Handler();
	private final OkHttpClient client = new OkHttpClient();

	public BaseClient() {
		client.setWriteTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS);
		client.setReadTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS);
	}

	/**
	 * 结束请求
	 * 
	 * @param url
	 */
	public void cancle(String url) {
		if (url != null)
			client.cancel(url);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 *            url
	 * @param params
	 *            参数键值对
	 * @param callback
	 *            回调
	 */
	public void get(String url, Map<String, String> params, final OKCallBack callback) {
		String realUrl = url.concat(getParamsString(params));
		Request request = new Request.Builder().url(realUrl).tag(url).build();
		startCall(request, callback);
	}

	/**
	 * get请求2
	 * 
	 * @param url
	 *            url
	 * @param params
	 *            参数键值对
	 * @param callback
	 *            回调
	 */
	public void get(String url, Map<String, String> params, String arrayKey, List<String> arrayValues, final OKCallBack callback) {
		String realUrl = url.concat(getParamsString(params));
		String arrayParamsString = getArrayParamsString(arrayKey, arrayValues);
		if (arrayParamsString.length() > 0)
			realUrl = realUrl.concat((realUrl.length() == url.length()) ? "?" : "&").concat(arrayParamsString);
		Request request = new Request.Builder().url(realUrl).tag(url).build();
		startCall(request, callback);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @param callback
	 */
	public void post(String url, Map<String, String> params, final OKCallBack callback) {
		FormEncodingBuilder form = new FormEncodingBuilder();
		if (params != null)
			for (Map.Entry<String, String> en : params.entrySet())
				form.add(en.getKey(), en.getValue());
		Request request = new Request.Builder().url(url).tag(url).post(form.build()).build();
		startCall(request, callback);
	}

	/**
	 * post请求2
	 * 
	 * @param url
	 * @param params
	 * @param callback
	 */
	public void post(String url, Map<String, String> params, String arrayKey, List<String> arrayValues, final OKCallBack callback) {
		FormEncodingBuilder form = new FormEncodingBuilder();
		if (params != null)
			for (Map.Entry<String, String> en : params.entrySet())
				form.add(en.getKey(), en.getValue());
		if (arrayValues != null && arrayKey != null)
			for (String str : arrayValues)
				form.add(arrayKey, str);
		Request request = new Request.Builder().url(url).tag(url).post(form.build()).build();
		startCall(request, callback);
	}

	private final String UPLOAD_FILE_KEY = "upload";
	private final MediaType TYPE_FILE = MediaType.parse("application/octet-stream");

	/**
	 * 发送文件
	 * 
	 * @param url
	 * @param params
	 * @param flist
	 * @param callback
	 */
	public void postFile(String url, Map<String, String> params, List<File> flist, final OKCallBack callback) {
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if (params != null)
			for (Map.Entry<String, String> en : params.entrySet())
				builder.addFormDataPart(en.getKey(), en.getValue());
		if (flist != null)
			for (File f : flist)
				builder.addFormDataPart(UPLOAD_FILE_KEY, null, RequestBody.create(TYPE_FILE, f));
		Request request = new Request.Builder().url(url).tag(url).post(builder.build()).build();
		startCall(request, callback);
	}

	/**
	 * 发送单个文件
	 * 
	 * @param url
	 * @param params
	 * @param file
	 * @param callback
	 */
	public void postFile(String url, Map<String, String> params, File file, final OKCallBack callback) {
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if (params != null)
			for (Map.Entry<String, String> en : params.entrySet())
				builder.addFormDataPart(en.getKey(), en.getValue());
		if (file != null)
			builder.addFormDataPart(UPLOAD_FILE_KEY, null, RequestBody.create(TYPE_FILE, file));
		Request request = new Request.Builder().url(url).tag(url).post(builder.build()).build();
		startCall(request, callback);
	}

	//
	//
	//
	//
	//

	/* 开始请求,处理回调 */
	private void startCall(Request request, final OKCallBack callback) {
		client.newCall(request).enqueue(new Callback() {
			public void onResponse(Response res) throws IOException {
				if (!res.isSuccessful()) {
					if (callback != null)
						mHandler.post(new Runnable() {
							public void run() {
								callback.onFailed(true);
							}
						});
					return;
				}
				final String result = res.body().string();
				if (callback != null)
					mHandler.post(new Runnable() {
						public void run() {
							callback.onSuccess(result);
						}
					});
			}

			public void onFailure(Request req, IOException e) {
				if (callback != null)
					mHandler.post(new Runnable() {
						public void run() {
							callback.onFailed(false);
						}
					});
			}
		});
	}

	/* 根据map,返回get请求的参数String */
	private static String getParamsString(Map<String, String> params) {
		if (params == null || params.isEmpty())
			return "";
		StringBuffer result = new StringBuffer("?");
		for (Map.Entry<String, String> en : params.entrySet())
			result.append(en.getKey()).append("=").append(en.getValue()).append("&");
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	/* 返回数组参数String,无 "?"和"&"开头 */
	private static String getArrayParamsString(String key, List<String> list) {
		if (key == null || key.trim().length() == 0 || list == null || list.isEmpty())
			return "";
		StringBuffer result = new StringBuffer();
		for (String value : list)
			result.append(key).append("=").append(value).append("&");
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

}
