package com.yzx.frames.tool.view_self.自动排版;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class AutoContainerView extends ViewGroup {

	public AutoContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoContainerView(Context context) {
		super(context);
	}

	// 改变前的 子View数量
	private int count;

	/**
	 * 计算 各个子View的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int i = getChildCount();
		if (count == i)
			return;
		count = i;
		int width = getMeasuredWidth();

		int childWidth = 0;
		int childHeight = 0;

		int totalWidth = leftSpacing + rightSpacing;
		int currentTopPosition = topSpacing;
		int currentLeftPosition = leftSpacing;

		int maxHeight = 0;
		for (int j = 0; j < i; j++) {
			View v = getChildAt(j);
			childWidth = v.getMeasuredWidth();
			childHeight = v.getMeasuredHeight();
			if (j == 0) {
				totalWidth += childWidth;
				maxHeight = childHeight;
			} else {
				totalWidth += (childWidth + left_Right_Spacing);
				if (totalWidth > width) {
					currentTopPosition += (maxHeight + top_Bottom_Spacing);
					currentLeftPosition = leftSpacing;
					maxHeight = childHeight;
					totalWidth = leftSpacing + rightSpacing + childWidth;
				} else {
					if (childHeight > maxHeight) {
						maxHeight = childHeight;
					}
					View upChild = getChildAt(j - 1);
					int upChildWidth = upChild.getMeasuredWidth();
					currentLeftPosition += (upChildWidth + left_Right_Spacing);
				}
			}
			l = currentLeftPosition;
			t = currentTopPosition;
			r = childWidth + currentLeftPosition;
			b = currentTopPosition + childHeight;
			v.layout(l, t, r, b);
		}

	}

	// 子View离父容器左边的距离
	private final int leftSpacing = 10;
	// 子View离父容器右边的距离
	private final int rightSpacing = 10;
	// 子View离父容器上边的距离
	private final int topSpacing = 8;
	// 子View离父容器底边的距离
	private final int bottomSpacing = 8;
	// 两个子View左右之间的间隔
	private final int left_Right_Spacing = 10;
	// 两个子View上下之间的间隔
	private final int top_Bottom_Spacing = 10;
	// 父容器中能够窜下子View的最大行数
	private final int umberRows = 3;
	private int groupHeight;
	private int scrollViewHeight;
	private int height, width;

	/**
	 * 计算 父容器的宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		// 计算出所有的childView的宽和高
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		// ViewGroup 的宽
		width = sizeWidth;
		// ViewGroup 的高
		height = topSpacing + bottomSpacing;
		// 子View的总个数
		int childCount = this.getChildCount();
		// 子View的宽
		int childWidth = 0;
		// 子View的高
		int childHeight = 0;
		// 子View相加的 宽
		int currentWidth = leftSpacing + rightSpacing;
		int currentHeight = 0;
		int row = 0;// 子View的行数
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			childWidth = childView.getMeasuredWidth();
			childHeight = childView.getMeasuredHeight();
			if (i == 0) {
				row++;
				currentWidth += childWidth;
				currentHeight = childHeight;
			} else {
				currentWidth += (childWidth + left_Right_Spacing);
			}
			// 当子View相加的宽大于父容器的宽时 自动换行 并重置currentWidth 和 currentHeight
			if (currentWidth > width) {
				row++;
				currentWidth = leftSpacing + rightSpacing + childWidth;
				height += (currentHeight + top_Bottom_Spacing);
				currentHeight = childHeight;
			} else {
				if (childHeight > currentHeight) {
					currentHeight = childHeight;
				}
			}
			if (row <= umberRows) {
				scrollViewHeight = height + currentHeight;
			}
		}
		height += currentHeight;
		groupHeight = height;
		Log.e("", "scrollViewHeight == " + scrollViewHeight);
		setMeasuredDimension(width, height);
	}

	public int getGroupViewheight() {
		return groupHeight;
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		new MarginLayoutParams(getContext(), attrs);
		return super.generateLayoutParams(attrs);
	}

}

class DpToPx {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}
