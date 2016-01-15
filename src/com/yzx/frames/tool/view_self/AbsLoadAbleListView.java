package com.yzx.frames.tool.view_self;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public abstract class AbsLoadAbleListView extends ListView {

	private final int state_loading = 1;
	private final int state_normal = 0;
	private int state;

	private boolean isEnable = false;
	private View bottomView;
	private Runnable onBottomClickListener;

	/**
	 * 停止load功能
	 */
	public void disable() {
		if (!isEnable)
			return;
		stopLoad();
		removeFooterView(bottomView);
	}

	/**
	 * 启用load功能
	 */
	public void enable() {
		if (isEnable)
			return;
		addFooterView(bottomView, null, false);
		stopLoad();
		bottomView.setOnClickListener(onBottomClick);
	}

	/**
	 * 停止load
	 */
	public void stopLoad() {
		setStateNormal(bottomView);
		state = state_normal;
	}

	/**
	 * 设置点击触发的load事件
	 */
	public void setOnBottomClickListener(Runnable onBottomClickListener) {
		this.onBottomClickListener = onBottomClickListener;
	}

	//
	//

	/** 生成footerview */
	protected abstract View genBottomView(ListView.LayoutParams lp);

	/** loading状态的footerview的样式设置 */
	protected abstract void setStateLoading(View bottomView);

	/** normal状态的footerview的样式设置 */
	protected abstract void setStateNormal(View bottomView);

	//
	//

	public AbsLoadAbleListView(Context context) {
		super(context);
		init();
	}

	public AbsLoadAbleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		bottomView = genBottomView(new ListView.LayoutParams(-1, -2));
		addFooterView(bottomView, null, false);
		setStateNormal(bottomView);
		bottomView.setOnClickListener(onBottomClick);
	}

	private OnClickListener onBottomClick = new View.OnClickListener() {
		public void onClick(View v) {
			if (state == state_loading)
				return;
			if (onBottomClickListener != null) {
				state = state_loading;
				onBottomClickListener.run();
				setStateLoading(bottomView);
			}
		}
	};

	//
	//

	@Override
	public void addFooterView(View v, Object data, boolean isSelectable) {
		if (v == bottomView)
			isEnable = true;
		super.addFooterView(v, data, isSelectable);
	}

	@Override
	public boolean removeFooterView(View v) {
		if (v == bottomView)
			isEnable = false;
		return super.removeFooterView(v);
	}

}
