package com.yzx.frames.tool.view_self.language;

import java.util.ArrayList;
import java.util.List;

import com.yzx.frames.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class LanguageChangeableTextView extends TextView {

	private static List<LanguageChangeableTextView> dataList = new ArrayList<LanguageChangeableTextView>();

	public static void clearData() {
		dataList.clear();
	}

	public static void notifyAllText() {
		if (!dataList.isEmpty())
			for (LanguageChangeableTextView tv : dataList)
				tv.resetText();
	}

	/*
	 * 
	 * 
	 * 
	 */

	private int text_id;

	public void setMyText(CharSequence str) {
		text_id = 0;
		setText(str);
	}

	public void setMyText(int id) {
		text_id = id;
		setText(text_id);
	}

	public void resetText() {
		if (text_id != 0)
			setText(text_id);
	}

	//
	//
	//

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		registSelf(true);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		registSelf(visibility == View.VISIBLE);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		registSelf(false);
	}

	public LanguageChangeableTextView(Context context) {
		super(context);
	}

	public LanguageChangeableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void registSelf(boolean add) {
		if (add) {
			resetText();
			if (!dataList.contains(this))
				dataList.add(this);
		} else
			dataList.remove(this);
	}

	private void init(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.textview_language);
		int rid = ta.getResourceId(R.styleable.textview_language_my_text, -1);
		if (rid != -1)
			setMyText(rid);
		ta.recycle();
	}

}
