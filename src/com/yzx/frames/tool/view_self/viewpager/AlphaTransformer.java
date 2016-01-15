package com.yzx.frames.tool.view_self.viewpager;

import android.view.View;

public class AlphaTransformer implements android.support.v4.view.ViewPager.PageTransformer {

	@Override
	public void transformPage(View view, float position) {

		if (position < -1) { // [-Infinity,-1)
			view.setAlpha(0);
		} else if (position <= 0) { // [-1,0] ��ǰ �ƶ���view

			view.setAlpha(1 + position);

		} else if (position <= 1) { // (0,1] ��һ��view (ǰ��or��߶���)

			view.setAlpha(1 - position);

		} else { // (1,+Infinity]
			view.setAlpha(0);
		}
	}

}
