package com.yzx.frames.tool.view_self;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 下拉刷新容器布局,继承使用
 * 
 * @author yzx
 * 
 */
public abstract class RefreshLayoutCompat extends LinearLayout {

	{
	}
	{
	}
	{
		// 配置
	}

	/** 设置header,不用设置LayoutParams */
	protected abstract View initHeaderView();

	/** 下拉时,变成松开就能刷新时调用(多次调用,避免重操作) ---------- ↑ 松开刷新 */
	protected abstract void onReadyToRefreshSet();

	/** 下拉时,设置最开始出现时的样式(多次调用,避免重操作) ---------- ↓ 下拉刷新 */
	protected abstract void onPullToRefreshSet();

	/** 刷新中样式设置(调用一次) ---------- 加载中...... */
	protected abstract void onRefreshingSet();

	/** 刷新成功或失败的提示设置 ---------- 刷新成功 */
	protected abstract void onRefreshOverSet(boolean isSuccess);

	/** header 的宽度 */
	protected int getHeaderWidth() {
		return -1;
	}

	/** header的高度 */
	protected int getHeaderHeight() {
		return 160;
	}

	/** 下拉时,当header全部显示后,再下拉多长距离可以松开刷新 */
	protected int getReadyToRefreshDistance() {
		return -headerOriginalTopMargin >> 2;
	}

	/** 刷新成功或失败的提示时长(毫秒) */
	protected int getResultNoticeTime() {
		return 700;
	}

	/** header进行Scroll时候的间隔频率 -------- 每秒刷新次数=1000/17 */
	protected int getHz() {
		return 17;
	}

	{
	}
	{
	}
	{
		// 主要逻辑
	}

	private int downY;
	private int headerOriginalTopMargin;
	private boolean hasGetChildCanScrollView = false;
	//
	private Scroller mScroller;
	private Handler mHandler = new Handler();
	private GestureDetector mGestureDetector;
	//
	private View headerView;
	private View canScrollView;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!canRefresh || isRefreshing)
			return false;

		// 获取子类ScrollView或AbsListView
		if (!hasGetChildCanScrollView) {
			hasGetChildCanScrollView = true;
			canScrollView = getChildCanScrollView(this);
		}

		if (canScrollView == null)
			return mGestureDetector.onTouchEvent(ev);

		return ViewCompat.canScrollVertically(canScrollView, -1) ? false : mGestureDetector.onTouchEvent(ev);
	}

	private boolean isUpToRefresh = false;
	private boolean isRefreshing = false;

	/** onTouch */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果 1设置刷新功能无效 2刷新中 3执行动画中 ,什么也不做
		if (!canRefresh || isRefreshing || !mScroller.isFinished())
			return false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// 获取手指下拉的距离
			int moveDis = (int) (event.getY() - downY);
			// 如果距离为正数
			if (moveDis > 0) {
				// 当header完全显示,并且再下拉一段距离(getReadyToRefreshDistance方法)
				if (reLayoutHeader(headerOriginalTopMargin + moveDis / 3).topMargin >= getReadyToRefreshDistance()) {
					isUpToRefresh = true;
					onReadyToRefreshSet();
				} else {
					isUpToRefresh = false;
					onPullToRefreshSet();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 如果header的露出的距离为0,就什么也不做
			final RefreshLayoutCompat.LayoutParams tm = (LayoutParams) headerView.getLayoutParams();
			if (tm.topMargin == headerOriginalTopMargin)
				return isUpToRefresh = false;
			//
			runNewThread(new Runnable() {
				public void run() {
					// 根据是否是松开刷新,设置header的scroll距离
					int dx = (!isUpToRefresh) ? (headerOriginalTopMargin - tm.topMargin) : -tm.topMargin;
					mScroller.startScroll(0, tm.topMargin, 0, dx, (!isUpToRefresh) ? 400 : 150);
					startScrollHeader();
					// 如果是松开刷新,显示刷新中状态,执行刷新事件
					if (isUpToRefresh)
						mHandler.post(new Runnable() {
							public void run() {
								isUpToRefresh = false;
								isRefreshing = true;
								onRefreshingSet();
								if (onRefreshListener != null)
									onRefreshListener.run();
							}
						});
				}
			});
			break;
		}
		return true;
	}

	/** 构造方法+ 初始化 */
	public RefreshLayoutCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(1);
		// 初始化header,设置LayoutParams,并加入布局,位置为第一个
		headerView = initHeaderView();
		headerOriginalTopMargin = -getHeaderHeight();
		LayoutParams lp = new RefreshLayoutCompat.LayoutParams(getHeaderWidth(), -headerOriginalTopMargin);
		lp.topMargin = headerOriginalTopMargin;
		headerView.setLayoutParams(lp);
		addView(headerView, 0);
		// 初始化scroller
		mScroller = new Scroller(context);
		// 初始化手势监听
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			public boolean onDown(MotionEvent e) {
				downY = (int) e.getY();
				return super.onDown(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return e1.getY() <= e2.getY();
			}
		});
	}

	{
	}
	{
	}
	{
		// 私有方法 private function
	}

	private void runNewThread(Runnable run) {
		Thread t = new Thread(run);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	/* 遍历获取可以滑动的ChildView */
	private View getChildCanScrollView(ViewGroup self) {
		for (int i = 0; i < self.getChildCount(); i++) {
			View child = self.getChildAt(i);
			if (child instanceof AbsListView || child instanceof ScrollView)
				return child;
			else if (child instanceof ViewGroup) {
				View subCanScrollView = getChildCanScrollView((ViewGroup) child);
				if (subCanScrollView instanceof AbsListView || subCanScrollView instanceof ScrollView)
					return subCanScrollView;
			}
		}
		return null;
	}

	/* 重新设置header的topMargin */
	private RefreshLayoutCompat.LayoutParams reLayoutHeader(int newTopMargin) {
		RefreshLayoutCompat.LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
		lp.topMargin = newTopMargin;
		headerView.setLayoutParams(lp);
		return lp;
	}

	/* 根据Scroller设置的距离,移动header */
	private void startScrollHeader() {
		Runnable backRun = new Runnable() {
			public void run() {
				reLayoutHeader(mScroller.getCurrY());
			}
		};
		while (mScroller.computeScrollOffset()) {
			mHandler.post(backRun);
			SystemClock.sleep(getHz());
		}
	}

	{
	}
	{
	}
	{
		// 外部调用 public function
	}

	/** 设置刷新结束,成功or失败 */
	public void setRefreshComplete(final boolean success) {
		mHandler.post(new Runnable() {
			public void run() {
				onRefreshOverSet(success);
			}
		});
		runNewThread(new Runnable() {
			public void run() {
				SystemClock.sleep(getResultNoticeTime());
				mScroller.startScroll(0, 0, 0, headerOriginalTopMargin, 300);
				startScrollHeader();
				isRefreshing = false;
			}
		});
	}

	private Runnable onRefreshListener;
	private boolean canRefresh = true;

	/** 是否启用下拉刷新 */
	public void setCanRefresh(boolean canRefresh) {
		this.canRefresh = canRefresh;
	}

	/** 设置..你懂得 */
	public void setOnRefreshListener(Runnable onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

}
