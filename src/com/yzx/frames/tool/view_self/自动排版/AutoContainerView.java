package com.yzx.frames.tool.view_self.�Զ��Ű�;

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

	// �ı�ǰ�� ��View����
	private int count;

	/**
	 * ���� ������View��λ��
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

	// ��View�븸������ߵľ���
	private final int leftSpacing = 10;
	// ��View�븸�����ұߵľ���
	private final int rightSpacing = 10;
	// ��View�븸�����ϱߵľ���
	private final int topSpacing = 8;
	// ��View�븸�����ױߵľ���
	private final int bottomSpacing = 8;
	// ������View����֮��ļ��
	private final int left_Right_Spacing = 10;
	// ������View����֮��ļ��
	private final int top_Bottom_Spacing = 10;
	// ���������ܹ�������View���������
	private final int umberRows = 3;
	private int groupHeight;
	private int scrollViewHeight;
	private int height, width;

	/**
	 * ���� �������Ŀ��
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ��ô�ViewGroup�ϼ�����Ϊ���Ƽ��Ŀ�͸ߣ��Լ�����ģʽ
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		// ��������е�childView�Ŀ�͸�
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		// ViewGroup �Ŀ�
		width = sizeWidth;
		// ViewGroup �ĸ�
		height = topSpacing + bottomSpacing;
		// ��View���ܸ���
		int childCount = this.getChildCount();
		// ��View�Ŀ�
		int childWidth = 0;
		// ��View�ĸ�
		int childHeight = 0;
		// ��View��ӵ� ��
		int currentWidth = leftSpacing + rightSpacing;
		int currentHeight = 0;
		int row = 0;// ��View������
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
			// ����View��ӵĿ���ڸ������Ŀ�ʱ �Զ����� ������currentWidth �� currentHeight
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
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * ��spֵת��Ϊpxֵ����֤���ִ�С����
	 * 
	 * @param spValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}
