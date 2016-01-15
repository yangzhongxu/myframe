package com.yzx.frames.tool.fm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;

public class ViewChangeAbleHolder {

	private static final int SHOW_ERROR = 1;
	private static final int SHOW_EMPTY = 2;
	private static final int SHOW_LOADING = 3;
	private static final int SHOW_DATA = 4;

	private LayoutParams matchParentLayoutParams = new LayoutParams(-1, -1);
	//
	private int loadingRes, emptyRes, errorRes;
	private View rootView, loadingView, emptyView, errorView;
	//
	private int currectViewInt;
	private Context context;

	//
	// ======================
	//

	private void removeFrontView() {
		((ViewGroup) (rootView)).removeAllViews();
	}

	private View getLoadingView() {
		if (this.loadingView == null)
			this.loadingView = View.inflate(context, loadingRes, null);
		return loadingView;
	}

	private View getEmptyView() {
		if (emptyView == null)
			this.emptyView = View.inflate(context, emptyRes, null);
		return emptyView;
	}

	private View getErrorView() {
		if (errorView == null)
			this.errorView = View.inflate(context, errorRes, null);
		return errorView;
	}

	private void animDataView(View view) {
		AlphaAnimation aa = new AlphaAnimation(0f, 1f);
		aa.setDuration(300);
		view.startAnimation(aa);
	}

	/*
	 * =================================================================================
	 * 
	 * public
	 * 
	 * =================================================================================
	 */

	public void init(Context context, View containerView, int loadingLayout, int emptyLayout, int errorLayout) {
		this.context = context;
		this.rootView = containerView;
		this.loadingRes = loadingLayout;
		this.emptyRes = emptyLayout;
		this.errorRes = errorLayout;
	}

	private OnViewChangeListener listener;

	public void setOnViewChangeListener(OnViewChangeListener listener) {
		this.listener = listener;
	}

	// =======================
	// =======================
	// =======================

	public void showLoadingView() {
		if (isShowLoadingView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getLoadingView(), matchParentLayoutParams);
		currectViewInt = SHOW_LOADING;

		if (listener != null)
			listener.onLoadingShow();
	}

	public void showDataView(View data) {
		if (isShowDataView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(data);
		currectViewInt = SHOW_DATA;
		animDataView(data);

		if (listener != null)
			listener.onDataShow();
	}

	public void showEmptyView() {
		if (isShowEmptyView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getEmptyView(), matchParentLayoutParams);
		currectViewInt = SHOW_EMPTY;

		if (listener != null)
			listener.onEmptyShow(getEmptyView());
	}

	public void showErrorView() {
		if (isShowErrorView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getErrorView(), matchParentLayoutParams);
		currectViewInt = SHOW_ERROR;

		if (listener != null)
			listener.onErrorShow(getErrorView());
	}

	// =======================
	// =======================
	// =======================

	public void showLoadingView(ViewGroup.LayoutParams lp) {
		if (isShowLoadingView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getLoadingView(), lp);
		currectViewInt = SHOW_LOADING;

		if (listener != null)
			listener.onLoadingShow();
	}

	public void showDataView(View data, ViewGroup.LayoutParams lp) {
		if (isShowDataView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(data, lp);
		currectViewInt = SHOW_DATA;
		animDataView(data);

		if (listener != null)
			listener.onDataShow();
	}

	public void showEmptyView(ViewGroup.LayoutParams lp) {
		if (isShowEmptyView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getEmptyView(), lp);
		currectViewInt = SHOW_EMPTY;

		if (listener != null)
			listener.onEmptyShow(getEmptyView());
	}

	public void showErrorView(ViewGroup.LayoutParams lp) {
		if (isShowErrorView())
			return;
		removeFrontView();
		((ViewGroup) (rootView)).addView(getErrorView(), lp);
		currectViewInt = SHOW_ERROR;

		if (listener != null)
			listener.onErrorShow(getErrorView());
	}

	// =======================
	// =======================
	// =======================

	public boolean isShowDataView() {
		return currectViewInt == SHOW_DATA;
	}

	public boolean isShowLoadingView() {
		return currectViewInt == SHOW_LOADING;
	}

	public boolean isShowEmptyView() {
		return currectViewInt == SHOW_EMPTY;
	}

	public boolean isShowErrorView() {
		return currectViewInt == SHOW_ERROR;
	}

	// =======================
	// =======================
	// =======================

	public View getLoadingViewOrNull() {
		return loadingView;
	}

	public View getEmptyViewOrNull() {
		return emptyView;
	}

	public View getErrorViewOrNull() {
		return errorView;
	}

	//
	//

	public static interface OnViewChangeListener {
		void onEmptyShow(View emptyView);

		void onErrorShow(View errorView);

		void onDataShow();

		void onLoadingShow();
	}

	public static class SimpleOnViewChangeListener implements OnViewChangeListener {

		@Override
		public void onEmptyShow(View emptyView) {
		}

		@Override
		public void onErrorShow(View errorView) {
		}

		@Override
		public void onDataShow() {
		}

		@Override
		public void onLoadingShow() {
		}
	}

}
