package com.yzx.frames.tool.func;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ListView;

public class ListViewUtil {

	public static boolean atTop(ListView lv) {
		return !ViewCompat.canScrollVertically(lv, -1);
	}

	public static boolean atBottom(ListView lv) {
		return !ViewCompat.canScrollVertically(lv, 1);
	}

	public static View positionToView(ListView lv, int position) {
		if (lv.getChildCount() == 0)
			return null;
		return lv.getChildAt(position - lv.getFirstVisiblePosition() - lv.getHeaderViewsCount());
	}

}
