package com.yzx.frames.tool.view_self.较多页面viewpager的title;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzx.frames.R;

public class MyViewPagerTitle extends FrameLayout implements View.OnClickListener {

	// 传进来的数据
	private List<String> titles;
	// items的集合
	private final List<View> items = new ArrayList<View>();
	// 选中的文字颜色
	protected int color_selected = Color.RED;
	// 没选中的文字颜色
	private int color_normal = Color.rgb(0x99, 0x99, 0x99);
	// 空间的总体高度
	private int mHeight;
	// 控件的总体宽度
	private int totalWidth;
	// 每一个title的宽度
	private final int itemWidth;
	// 未选中的字体大小
	private float normalTextSize = 14;
	// 选中的字体大小
	private float selectedTextSize = 17;

	private HorizontalScrollView hscrollview;
	private LinearLayout container;

	public MyViewPagerTitle setTitles(List<String> titles) {
		this.titles = titles;
		return this;
	}

	public MyViewPagerTitle setSelectedColor(int color) {
		this.color_selected = color;
		return this;
	}

	public MyViewPagerTitle setOnTitleClickListener(OnItemClickListener listener) {
		this.listener = listener;
		return this;
	}

	private OnItemClickListener listener;

	/* Init */
	public void init() {
		post(new Runnable() {
			public void run() {
				items.clear();
				container.removeAllViews();
				android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(itemWidth, mHeight);
				for (int i = 0; i < titles.size(); i++) {
					View itemView = View.inflate(getContext(), R.layout.layout_my_viewpager_title_item, null);
					items.add(itemView);
					container.addView(itemView, lp);
					TextView tv = getTv(itemView);
					tv.setText(titles.get(i));
					tv.setGravity(Gravity.CENTER);
					itemView.setTag(i);
					itemView.setOnClickListener(MyViewPagerTitle.this);
				}
				setItemSelection(0);
			}
		});
	}

	/* 设置选中第几个item */
	public void setItemSelection(int p) {
		for (int i = 0; i < titles.size(); i++) {
			TextView tv = getTv(items.get(i));
			View line = getLine(items.get(i));
			tv.setTextColor(color_normal);
			line.setBackgroundColor(Color.TRANSPARENT);
			tv.setTextSize(normalTextSize);
		}
		TextView tv = getTv(items.get(p));
		View line = getLine(items.get(p));
		tv.setTextColor(color_selected);
		line.setBackgroundColor(color_selected);
		tv.setTextSize(selectedTextSize);

		checkLastVisibleItem(p);
	}

	@Override
	public void onClick(View v) {
		if (listener != null)
			listener.onClick((Integer) v.getTag());
		setItemSelection((Integer) v.getTag());
	}

	public MyViewPagerTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_my_viewpager_title, this, true);
		hscrollview = (HorizontalScrollView) findViewById(R.id.hscrollview);
		container = (LinearLayout) findViewById(R.id.container);
		itemWidth = dip2px(80, context);

		post(new Runnable() {
			public void run() {
				mHeight = getMeasuredHeight();
				totalWidth = getMeasuredWidth();
			}
		});
	}

	private void checkLastVisibleItem(int p) {
		int marginLeft = itemWidth * p;
		int scrollLeft = hscrollview.getScrollX();
		if (marginLeft < scrollLeft) {
			hscrollview.smoothScrollBy(-(scrollLeft - marginLeft), 0);
			return;
		}
		int marginRight = itemWidth * (p + 1);
		int contentRight = scrollLeft + totalWidth;
		if (marginRight > contentRight)
			hscrollview.smoothScrollBy(-(contentRight - marginRight), 0);
	}

	private TextView getTv(View item) {
		return (TextView) item.findViewById(R.id.textview);
	}

	private View getLine(View item) {
		return item.findViewById(R.id.line);
	}

	public interface OnItemClickListener {
		void onClick(int p);
	}

	private int dip2px(float dpValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
