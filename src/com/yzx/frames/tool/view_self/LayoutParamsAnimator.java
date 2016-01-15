package com.yzx.frames.tool.view_self;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class LayoutParamsAnimator {

	public static final int TIME_FRAME = 17;
	public static final int TIME_DURATION_DEFAULT = 200;

	/**
	 * 改变view的LayoutParams的height
	 * 
	 * @param targetView
	 *            目标view
	 * @param targetHeight
	 *            目标高度
	 * @param duration
	 *            时间
	 * @param restoreLayoutOnOver
	 *            完成后是否还原
	 * @param overListener
	 *            完成监听事件
	 */
	public static void actionHeight(final View targetView, final int targetHeight, Integer duration, final boolean restoreLayoutOnOver,
			final Runnable overListener) {
		duration = duration == null ? TIME_DURATION_DEFAULT : duration;
		final int oldHeight = getUsefullHeight(targetView);
		scrollValueCallback(targetView.getContext(), 0, oldHeight, 0, targetHeight, duration, new XYCallback() {
			public void onRun(int x, int y) {
				y = y == 0 ? 1 : y;
				targetView.getLayoutParams().height = y;
				targetView.setLayoutParams(targetView.getLayoutParams());
			}

			public void onFinish() {
				if (overListener != null)
					overListener.run();
				if (restoreLayoutOnOver) {
					targetView.getLayoutParams().height = oldHeight;
					targetView.setLayoutParams(targetView.getLayoutParams());
				}
			}
		});
	}

	/**
	 * 改变view的LayoutParams的width
	 * 
	 * @param targetView
	 *            目标view
	 * @param targetWidth
	 *            目标宽度
	 * @param duration
	 *            时间
	 * @param restoreLayoutOnOver
	 *            完成后是否还原
	 * @param overListener
	 *            完成监听事件
	 */
	public static void actionWidth(final View targetView, int targetWidth, Integer duration, final boolean restoreLayoutOnOver,
			final Runnable overListener) {
		duration = duration == null ? TIME_DURATION_DEFAULT : duration;
		final int oldWidth = getUsefullWidth(targetView);
		scrollValueCallback(targetView.getContext(), oldWidth, 0, targetWidth, 0, duration, new XYCallback() {
			public void onRun(int x, int yyy) {
				x = x == 0 ? 1 : x;
				targetView.getLayoutParams().width = x;
				targetView.setLayoutParams(targetView.getLayoutParams());
			}

			public void onFinish() {
				if (overListener != null)
					overListener.run();
				if (restoreLayoutOnOver) {
					targetView.getLayoutParams().width = oldWidth;
					targetView.setLayoutParams(targetView.getLayoutParams());
				}
			}
		});
	}

	/**
	 * 
	 * @param context
	 * @param startx
	 *            起始x
	 * @param starty
	 *            起始y
	 * @param targetx
	 *            目标x
	 * @param targety
	 *            目标y
	 * @param duration
	 *            时间
	 * @param cb
	 *            回调
	 */
	public static void scrollValueCallback(Context context, int startx, int starty, int targetx, int targety, int duration,
			final XYCallback cb) {
		if (cb == null)
			return;
		final Handler handler = new Handler(context.getMainLooper());
		final Scroller scroller = new Scroller(context, new LinearInterpolator());
		scroller.startScroll(startx, starty, targetx - startx, targety - starty, duration);
		final Runnable postRun = new Runnable() {
			public void run() {
				cb.onRun(scroller.getCurrX(), scroller.getCurrY());
			}
		};
		new Thread(new Runnable() {
			public void run() {
				while (scroller.computeScrollOffset()) {
					handler.post(postRun);
					SystemClock.sleep(TIME_FRAME);
				}
				handler.post(new Runnable() {
					public void run() {
						cb.onFinish();
					}
				});
			}
		}).start();
	}

	/**
	 * 
	 *
	 */
	public static interface XYCallback {
		void onRun(int x, int y);

		void onFinish();
	}

	/*
	 * 获取可用的view的高度
	 */
	private static int getUsefullHeight(View targetView) {
		LayoutParams lp = targetView.getLayoutParams();
		if (lp == null)
			throw new IllegalStateException("no LayoutParams with target view");
		return lp.height > 1 ? lp.height : targetView.getMeasuredHeight();
	}

	/*
	 * 获取可用的view的宽度
	 */
	private static int getUsefullWidth(View targetView) {
		LayoutParams lp = targetView.getLayoutParams();
		if (lp == null)
			throw new IllegalStateException("no LayoutParams with target view");
		return lp.width > 1 ? lp.width : targetView.getMeasuredWidth();
	}

}
