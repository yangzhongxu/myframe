package com.yzx.frames.tool.view_self;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/** 选择ABCD#等字母的竖条的控件 */
public class LetterSelectView extends View {

	private final String[] ss = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "#" };

	private Paint paint = new Paint();
	// 每个字母的所占平均高度
	private int per_height;

	public LetterSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTextSize(30);
		paint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 设置背景,可以自定义个圆角背啥的
		canvas.drawColor(Color.argb(66, 66, 66, 66));
		// 挨个绘制字母 ↓ (注 : 文字的绘制坐标以左下角为准)
		int marginLeft = getWidth() / 3;
		per_height = getHeight() / ss.length;
		for (int i = 0; i < ss.length; i++) {
			int txtSize = (int) paint.measureText(ss[i]);
			int cha = (per_height - txtSize) >> 1;
			canvas.drawText(ss[i], marginLeft, per_height * i + txtSize + cha, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			onTouchCallback.onUp();
		} else {
			int touchY = (int) event.getY();
			int poi = touchY / per_height;
			if (poi < ss.length && poi >= 0)
				onTouchCallback.onTouch(ss[poi]);
		}
		return true;
	}

	private OnTouchCallback onTouchCallback;

	public void setOnTouchCallback(OnTouchCallback onTouchCallback) {
		this.onTouchCallback = onTouchCallback;
	}

	public static interface OnTouchCallback {

		public void onTouch(String ch);

		public void onUp();
	}

}
