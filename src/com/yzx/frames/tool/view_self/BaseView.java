package com.yzx.frames.tool.view_self;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public abstract class BaseView extends View {

	protected abstract void myDraw(Canvas canvas);

	protected abstract void reDo();

	private Thread thread;
	private boolean running = true;

	@Override
	protected final void onDraw(Canvas canvas) {
		if (thread == null) {
			thread = new Thread() {
				public void run() {
					while (running) {
						reDo();
						postInvalidate();
						SystemClock.sleep(17);// 1000/60
					}
				}
			};
			thread.start();
		} else
			myDraw(canvas);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		running = false;
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseView(Context context) {
		super(context);
	}

}
