package com.yzx.frames.tool.net;

public interface NetCallBack {

	void onSuccess(String result);

	void onError(boolean noNetWork);

}
