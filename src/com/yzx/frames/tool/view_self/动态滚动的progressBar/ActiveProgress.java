package com.yzx.frames.tool.view_self.��̬������progressBar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.yzx.frames.R;

public class ActiveProgress extends FrameLayout {

	private ImageView image;
	private ObjectAnimator anim;

	public ActiveProgress(Context context, AttributeSet attrs) {
		super(context, attrs);

		post(new Runnable() {
			public void run() {
				image = new ImageView(getContext());
				addView(image);

				//
				// ��ImageView����repeat�ı���ͼƬ
				BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.pro));
				drawable.setTileModeX(TileMode.REPEAT);
				drawable.setAntiAlias(true);
				image.setScaleType(ScaleType.FIT_XY);
				image.setImageDrawable(drawable);

				//
				// ��������ó�10000px(����Ļ��ȵ�������ɶ��)
				LayoutParams lp = new LayoutParams(-1, -1);
				lp.width = 10000;
				lp.height = -1;
				image.setLayoutParams(lp);

				//
				// ʹ�ö���,��image���ƽ��,��image��β��ƽ�Ƶ��Ҳ�ʱ��,�ظ�����,�����������ظ�
				int end = lp.width - getResources().getDisplayMetrics().widthPixels;
				int perSecondPx = 400;
				anim = ObjectAnimator.ofFloat(image, "translationX", 0f, -end).setDuration(end / perSecondPx * 1000);
				anim.setInterpolator(new LinearInterpolator());
				anim.setRepeatCount(9999999);
				if (!anim.isRunning())
					anim.start();
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (anim != null)
			if (!anim.isRunning())
				anim.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		anim.cancel();
	}

}
