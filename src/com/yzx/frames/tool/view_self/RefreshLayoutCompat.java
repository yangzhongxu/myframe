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
 * ����ˢ����������,�̳�ʹ��
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
		// ����
	}

	/** ����header,��������LayoutParams */
	protected abstract View initHeaderView();

	/** ����ʱ,����ɿ�����ˢ��ʱ����(��ε���,�����ز���) ---------- �� �ɿ�ˢ�� */
	protected abstract void onReadyToRefreshSet();

	/** ����ʱ,�����ʼ����ʱ����ʽ(��ε���,�����ز���) ---------- �� ����ˢ�� */
	protected abstract void onPullToRefreshSet();

	/** ˢ������ʽ����(����һ��) ---------- ������...... */
	protected abstract void onRefreshingSet();

	/** ˢ�³ɹ���ʧ�ܵ���ʾ���� ---------- ˢ�³ɹ� */
	protected abstract void onRefreshOverSet(boolean isSuccess);

	/** header �Ŀ�� */
	protected int getHeaderWidth() {
		return -1;
	}

	/** header�ĸ߶� */
	protected int getHeaderHeight() {
		return 160;
	}

	/** ����ʱ,��headerȫ����ʾ��,�������೤��������ɿ�ˢ�� */
	protected int getReadyToRefreshDistance() {
		return -headerOriginalTopMargin >> 2;
	}

	/** ˢ�³ɹ���ʧ�ܵ���ʾʱ��(����) */
	protected int getResultNoticeTime() {
		return 700;
	}

	/** header����Scrollʱ��ļ��Ƶ�� -------- ÿ��ˢ�´���=1000/17 */
	protected int getHz() {
		return 17;
	}

	{
	}
	{
	}
	{
		// ��Ҫ�߼�
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

		// ��ȡ����ScrollView��AbsListView
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
		// ��� 1����ˢ�¹�����Ч 2ˢ���� 3ִ�ж����� ,ʲôҲ����
		if (!canRefresh || isRefreshing || !mScroller.isFinished())
			return false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// ��ȡ��ָ�����ľ���
			int moveDis = (int) (event.getY() - downY);
			// �������Ϊ����
			if (moveDis > 0) {
				// ��header��ȫ��ʾ,����������һ�ξ���(getReadyToRefreshDistance����)
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
			// ���header��¶���ľ���Ϊ0,��ʲôҲ����
			final RefreshLayoutCompat.LayoutParams tm = (LayoutParams) headerView.getLayoutParams();
			if (tm.topMargin == headerOriginalTopMargin)
				return isUpToRefresh = false;
			//
			runNewThread(new Runnable() {
				public void run() {
					// �����Ƿ����ɿ�ˢ��,����header��scroll����
					int dx = (!isUpToRefresh) ? (headerOriginalTopMargin - tm.topMargin) : -tm.topMargin;
					mScroller.startScroll(0, tm.topMargin, 0, dx, (!isUpToRefresh) ? 400 : 150);
					startScrollHeader();
					// ������ɿ�ˢ��,��ʾˢ����״̬,ִ��ˢ���¼�
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

	/** ���췽��+ ��ʼ�� */
	public RefreshLayoutCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(1);
		// ��ʼ��header,����LayoutParams,�����벼��,λ��Ϊ��һ��
		headerView = initHeaderView();
		headerOriginalTopMargin = -getHeaderHeight();
		LayoutParams lp = new RefreshLayoutCompat.LayoutParams(getHeaderWidth(), -headerOriginalTopMargin);
		lp.topMargin = headerOriginalTopMargin;
		headerView.setLayoutParams(lp);
		addView(headerView, 0);
		// ��ʼ��scroller
		mScroller = new Scroller(context);
		// ��ʼ�����Ƽ���
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
		// ˽�з��� private function
	}

	private void runNewThread(Runnable run) {
		Thread t = new Thread(run);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	/* ������ȡ���Ի�����ChildView */
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

	/* ��������header��topMargin */
	private RefreshLayoutCompat.LayoutParams reLayoutHeader(int newTopMargin) {
		RefreshLayoutCompat.LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
		lp.topMargin = newTopMargin;
		headerView.setLayoutParams(lp);
		return lp;
	}

	/* ����Scroller���õľ���,�ƶ�header */
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
		// �ⲿ���� public function
	}

	/** ����ˢ�½���,�ɹ�orʧ�� */
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

	/** �Ƿ���������ˢ�� */
	public void setCanRefresh(boolean canRefresh) {
		this.canRefresh = canRefresh;
	}

	/** ����..�㶮�� */
	public void setOnRefreshListener(Runnable onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

}
