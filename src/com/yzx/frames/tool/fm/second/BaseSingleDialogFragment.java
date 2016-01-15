package com.yzx.frames.tool.fm.second;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yzx.frames.tool.fm.Fid;
import com.yzx.frames.tool.fm.ViewChangeAbleHolder;

/** 单一,弹窗显示的fragment */
public abstract class BaseSingleDialogFragment extends DialogFragment {

	//
	// ========set=========================================================
	//

	/** 是否右滑删除 */
	protected boolean canRightScrollDismiss() {
		return true;
	}

	/** 是否开启注解findView功能 */
	protected boolean isAnnotationUseing() {
		return false;
	}

	/** 进入and消失的动画效果style */
	protected int getWindowAnimationRes() {
		return android.R.style.Animation_Dialog;
	}

	/** 界面的theme */
	protected int getWindowTheme() {
		return android.R.style.Theme_Translucent_NoTitleBar;
	}

	/** 是否调用ondismissListener(因为很多情况下不确定什么时候,所以动态设置) */
	protected void setCanCallDismissListener(boolean canCallDismissListener) {
		this.canCallDismissListener = canCallDismissListener;
	}

	/** 返回按钮的id */
	protected int getBackBtnId() {
		// TODO
		return 0;
	}

	/** 返回按钮点击事件 */
	protected void onBackBtnPress() {
		dismiss();
	}

	/** 第一次resume执行 */
	protected void onFirstResume() {
		// empty
	}

	/** 窗口的宽 */
	protected int getWidth() {
		return -1;
	}

	/** 窗口的高 */
	protected int getHeight() {
		return -1;
	}

	/** 设置窗口动画 */
	protected void setWindowAnim(int animRes) {
		Dialog mDialog = getDialog();
		if (mDialog != null) {
			Window mWindow = mDialog.getWindow();
			if (mWindow != null)
				mWindow.setWindowAnimations(animRes);
		}
	}

	//
	// =========protect=====================================================
	//

	protected FragmentActivity activity;
	protected final Handler mHandler = new Handler();
	protected final ViewChangeAbleHolder changeHolder = new ViewChangeAbleHolder();

	/** 主线程执行 */
	protected void runOnMain(Runnable run) {
		activity.runOnUiThread(run);
	}

	/** id找控件 */
	protected View findView(int id) {
		return rootView.findViewById(id);
	}

	/** tag找控件 */
	protected View findView(String tag) {
		return rootView.findViewWithTag(tag);
	}

	/** 初始化,再onCreate中执行 */
	protected void init() {
	};

	/** 获取根布局资源 */
	protected abstract int getRootViewRes();

	/** 主要操作 */
	protected abstract void doMain();

	/** 主要操作2 (如果用到) */
	protected void doMain2() {
	}

	/** 主要操作3 (如果用到) */
	protected void doMain3() {
	}

	/** 销毁后执行,资源释放 */
	protected abstract void release();

	//
	// =======public========================================================
	//

	/** 设置消失Listener */
	public void setOnDismissListener(Runnable onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	/** 获取根布局 */
	public View getRootView() {
		return rootView;
	}

	/** 获取滑动删除的viewpager(如果滑动删除不可用,返回null) */
	public ViewPager getScrollPager() {
		return viewpager;
	}

	//
	// =======life circle==========================================================
	//

	// 消失后回调listener
	private Runnable onDismissListener;
	// 消失后是否执行onDismissListener
	private boolean canCallDismissListener = true;
	// 记录是否是第一次执行onResume
	private boolean isFirstResume = true;
	// 根布局
	private View rootView;
	// 滑动删除的viewpager
	private ViewPager viewpager;

	@Override
	public void show(FragmentManager manager, String tag) {
		// 检测tag是否是SingleManager的
		if (!SingleManager.TAG.equals(tag))
			throw new IllegalStateException("show() must be called by SingleManager");
		super.show(manager, null);
	}

	@Override
	public int show(FragmentTransaction transaction, String tag) {
		throw new IllegalStateException("show() must be called by SingleManager");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 赋值Activity
		this.activity = (FragmentActivity) activity;
		// 注册自己到SingleManager
		SingleManager.add(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置theme
		setStyle(STYLE_NO_TITLE, getWindowTheme());
		// 初始化
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 如果不用右滑删除 ,直接返回根布局
		if (!canRightScrollDismiss())
			return rootView = inflater.inflate(getRootViewRes(), null);
		// 创建一个viewpager
		viewpager = new ViewPager(activity);
		viewpager.setBackgroundColor(Color.TRANSPARENT);
		viewpager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
		// 引入根布局
		rootView = inflater.inflate(getRootViewRes(), null);
		// 设置adapter,布局导入viewpager
		viewpager.setAdapter(new PAdapter(rootView));
		// 移动倒第二页
		viewpager.setCurrentItem(1, false);
		// 设置滑动事件
		viewpager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageScrollStateChanged(int s) {
				if (s == ViewPager.SCROLL_STATE_IDLE && viewpager.getCurrentItem() == 0)
					dismiss();
			}
		});
		return viewpager;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// 设置窗口大小(默认全屏)
		view.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getHeight()));
		// 初始化返回按钮事件
		View backView = findView(getBackBtnId());
		if (backView != null)
			backView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onBackBtnPress();
				}
			});
		// 初始化注解View
		if (isAnnotationUseing())
			initAnnotationView();
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		// 执行主要逻辑操作
		doMain();
		doMain2();
		doMain3();
	}

	@Override
	public void onResume() {
		if (isFirstResume) {
			setWindowAnim(getWindowAnimationRes());
			isFirstResume = false;
			// 设置背景透明
			if (getDialog() != null && getDialog().getWindow() != null)
				getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			onFirstResume();
		} else
			mHandler.postDelayed(new Runnable() {
				public void run() {
					setWindowAnim(getWindowAnimationRes());
				}
			}, 200);
		super.onResume();
	}

	@Override
	public void onPause() {
		setWindowAnim(0);
		super.onPause();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// 移除注册SingleManager
		SingleManager.remove(getClass());
		// 执行消失Listener(如果可行)
		if (onDismissListener != null && canCallDismissListener)
			onDismissListener.run();
		super.onDismiss(dialog);
	}

	@Override
	public void onDestroy() {
		// 释放资源
		release();
		super.onDestroy();
	}

	//
	// ===============================priavte=============================
	//

	/* 初始化注解view */
	private void initAnnotationView() {
		Field[] fs = getClass().getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			Fid fid = field.getAnnotation(Fid.class);
			if (fid != null) {
				int id = fid.id();
				if (id != Fid.EMPTY_ID)
					try {
						field.set(this, findView(id));
					} catch (Exception e) {
					}
			}
		}
	}

	/* viewpager 的 adapter */
	private class PAdapter extends PagerAdapter {

		private View outView;
		private View emptyView;

		public PAdapter(View view) {
			outView = view;
			emptyView = new View(activity);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(position == 0 ? emptyView : outView);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(position == 0 ? emptyView : outView);
			return position == 0 ? emptyView : outView;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

}
