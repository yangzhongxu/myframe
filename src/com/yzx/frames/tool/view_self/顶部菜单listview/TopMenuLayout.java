package com.yzx.frames.tool.view_self.顶部菜单listview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class TopMenuLayout extends LinearLayout {

	private int height_Top;
	private int downY;
	private int startScrollY;

	private boolean isUp = false;
	private boolean fromListViewTopScroll = false;
	private boolean hasSendToListViewCancel = false;

	private ValueAnimator animator;
	private View bottomView;

	public TopMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		bottomView = getChildAt(1);
		bottomView.post(new Runnable() {
			public void run() {
				getChildAt(0).post(new Runnable() {
					public void run() {
						height_Top = getChildAt(0).getMeasuredHeight();
						LinearLayout.LayoutParams lp = (LayoutParams) bottomView.getLayoutParams();
						lp.bottomMargin -= height_Top;
						bottomView.setLayoutParams(lp);
					}
				});
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (animator != null || height_Top == 0)
			return true;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 点击的y坐标
			downY = (int) event.getRawY();
			// 点击的开始的scrollY
			startScrollY = getScrollY();
			// 重置标记bottomView的cancel事件变量
			hasSendToListViewCancel = false;
			//
			// 判断是不是从bottomView的可滑动的头开始滑动(如果bottomView是ListView或ScrollView)
			if (isUp)
				fromListViewTopScroll = !ViewCompat.canScrollVertically(bottomView, -1);
			else
				fromListViewTopScroll = false;
			return super.dispatchTouchEvent(event);
		}

		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// 获取滑动的间距
			int gap = (int) (event.getRawY() - downY);
			// 如果是菜单提起状态
			if (isUp) {
				// 如果是从bottomView的顶部开始向下滑,并且滑动了一段距离,就拦截掉
				if (fromListViewTopScroll && gap >= ViewConfiguration.getTouchSlop()) {
					sendCancelEventToListView(event);
					scrollTo(0, startScrollY - gap);
					return true;
				} else {
					return super.dispatchTouchEvent(event);
				}
			}
			// 如果顶部View显示中
			else {
				// 如果滑动距离超过一定值,就拦截掉
				if (Math.abs(gap) >= ViewConfiguration.getTouchSlop()) {
					sendCancelEventToListView(event);
					scrollTo(0, startScrollY - gap);
					return true;
				} else
					return super.dispatchTouchEvent(event);
			}
		}

		// 手指抬起,开始动画
		else {
			int currectScrollY = getScrollY();
			if (isUp) {
				if (currectScrollY < height_Top * 0.7) {
					animationUpOrDown(currectScrollY, false);
				} else {
					animationUpOrDown(currectScrollY, true);
				}
			} else {
				if (currectScrollY > height_Top * 0.3) {
					animationUpOrDown(currectScrollY, true);
				} else {
					animationUpOrDown(currectScrollY, false);
				}
			}
			return super.dispatchTouchEvent(event);
		}
	}

	/*
	 * 滑动上去 or 下来
	 */
	private void animationUpOrDown(int currectScrollY, final boolean isUp) {
		this.isUp = isUp;
		animator = ValueAnimator.ofInt(currectScrollY, isUp ? height_Top : 0);
		animator.setDuration(200);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				int s = (Integer) animation.getAnimatedValue();
				if (isUp && s == height_Top)
					animator = null;
				else if (!isUp && s == 0)
					animator = null;
				scrollTo(0, s);
			}
		});
		animator.start();
	}

	/*
	 * 通知bottomView : 事件被拦截了
	 */
	private void sendCancelEventToListView(MotionEvent ev) {
		if (hasSendToListViewCancel)
			return;
		hasSendToListViewCancel = true;
		ev.setAction(MotionEvent.ACTION_CANCEL);
		bottomView.dispatchTouchEvent(ev);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (y < 0)
			y = 0;
		if (y > height_Top)
			y = height_Top;
		super.scrollTo(x, y);
	}

	//
	//使用
	//
	
	
//	<?xml version="1.0" encoding="utf-8"?>
//	<com.example.touch_test.TopMenuLayout xmlns:android="http://schemas.android.com/apk/res/android"
//	    android:layout_width="match_parent"
//	    android:layout_height="match_parent"
//	    android:orientation="vertical" >
//
//	    <View
//	        android:layout_width="match_parent"
//	        android:layout_height="200dp"
//	        android:background="#f00"
//	        android:clickable="true" />
//
//	    <View
//	        android:id="@+id/listview"
//	        android:layout_width="match_parent"
//	        android:layout_height="match_parent"
//	        android:background="#fff"
//	        android:clickable="true" />
//
//	</com.example.touch_test.TopMenuLayout>
	
	
}
