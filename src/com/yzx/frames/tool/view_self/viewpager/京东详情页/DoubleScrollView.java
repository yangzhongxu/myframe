package com.yzx.frames.tool.view_self.viewpager.¾©¶«ÏêÇéÒ³;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DoubleScrollView extends VerticalViewPager {

	public DoubleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	private int downY;
	private boolean hasDealMove = false;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downY = (int) ev.getRawY();
			hasDealMove = false;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE)
			if (!hasDealMove) {
				hasDealMove = true;
				int gap = (int) (ev.getRawY() - downY);

				View child = getChildAt(getCurrentItem());
				if (child != null) {
					if (gap > 0) {

						if (isScrollViewAtTop(child)) {
							return true;
						} else if (isScrollViewAtBottom(child)) {
							return false;
						} else {
							return false;
						}

					} else {

						if (isScrollViewAtTop(child)) {
							return false;
						} else if (isScrollViewAtBottom(child)) {
							return true;
						} else {
							return false;
						}

					}
				}
			}

		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * 
	 * 
	 * 
	 */

	private boolean isScrollViewAtTop(View view) {
		return !ViewCompat.canScrollVertically(view, -1);
	}

	private boolean isScrollViewAtBottom(View view) {
		return !ViewCompat.canScrollVertically(view, 1);
	}

}
