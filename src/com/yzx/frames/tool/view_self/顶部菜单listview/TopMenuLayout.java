package com.yzx.frames.tool.view_self.�����˵�listview;

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
			// �����y����
			downY = (int) event.getRawY();
			// ����Ŀ�ʼ��scrollY
			startScrollY = getScrollY();
			// ���ñ��bottomView��cancel�¼�����
			hasSendToListViewCancel = false;
			//
			// �ж��ǲ��Ǵ�bottomView�Ŀɻ�����ͷ��ʼ����(���bottomView��ListView��ScrollView)
			if (isUp)
				fromListViewTopScroll = !ViewCompat.canScrollVertically(bottomView, -1);
			else
				fromListViewTopScroll = false;
			return super.dispatchTouchEvent(event);
		}

		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// ��ȡ�����ļ��
			int gap = (int) (event.getRawY() - downY);
			// ����ǲ˵�����״̬
			if (isUp) {
				// ����Ǵ�bottomView�Ķ�����ʼ���»�,���һ�����һ�ξ���,�����ص�
				if (fromListViewTopScroll && gap >= ViewConfiguration.getTouchSlop()) {
					sendCancelEventToListView(event);
					scrollTo(0, startScrollY - gap);
					return true;
				} else {
					return super.dispatchTouchEvent(event);
				}
			}
			// �������View��ʾ��
			else {
				// ����������볬��һ��ֵ,�����ص�
				if (Math.abs(gap) >= ViewConfiguration.getTouchSlop()) {
					sendCancelEventToListView(event);
					scrollTo(0, startScrollY - gap);
					return true;
				} else
					return super.dispatchTouchEvent(event);
			}
		}

		// ��ָ̧��,��ʼ����
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
	 * ������ȥ or ����
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
	 * ֪ͨbottomView : �¼���������
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
	//ʹ��
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
