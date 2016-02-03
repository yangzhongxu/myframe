package com.yzx.frames.tool.view_self.刮奖view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yzx.frames.R;

public class GuaView extends View {

	public GuaView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setBackgroundResource(R.drawable.ic_launcher);

		//
		// 创建遮盖的bitmap和其对应的canvas
		layerBmp = Bitmap.createBitmap(500, 500, Config.ARGB_8888);
		layerCanvas = new Canvas(layerBmp);
		layerCanvas.drawColor(Color.GRAY);

		//
		// 创建画笔
		layerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		layerPaint.setStyle(Style.STROKE);
		layerPaint.setStrokeCap(Paint.Cap.ROUND);
		layerPaint.setStrokeJoin(Paint.Join.ROUND);
		layerPaint.setStrokeWidth(28);
		layerPaint.setColor(Color.BLACK);
		layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
	}

	private Bitmap layerBmp;
	private Canvas layerCanvas;
	private Paint layerPaint;

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制图层bitmap
		canvas.drawBitmap(layerBmp, 0, 0, null);
		// 使用bitmap的canvas对象绘制path
		// 注意 这里用的是Bitmap的canvas,而不是View的
		layerCanvas.drawPath(mPath, layerPaint);
	}

	/*
	 * 
	 * 
	 * 
	 * 以下是计算Path的路径的
	 */

	private Path mPath = new Path();
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mPath.reset();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			break;
		}
		invalidate();
		return true;
	}
}
