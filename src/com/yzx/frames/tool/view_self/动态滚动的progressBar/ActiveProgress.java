package com.yzx.frames.tool.view_self.动态滚动的progressBar;

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
				// 将ImageView设置repeat的背景图片
				BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.pro));
				drawable.setTileModeX(TileMode.REPEAT);
				drawable.setAntiAlias(true);
				image.setScaleType(ScaleType.FIT_XY);
				image.setImageDrawable(drawable);

				//
				// 将宽度设置成10000px(或屏幕宽度的整倍数啥的)
				LayoutParams lp = new LayoutParams(-1, -1);
				lp.width = 10000;
				lp.height = -1;
				image.setLayoutParams(lp);

				//
				// 使用动画,将image左边平移,当image的尾部平移到右侧时候,重复动画,就这样不断重复
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
