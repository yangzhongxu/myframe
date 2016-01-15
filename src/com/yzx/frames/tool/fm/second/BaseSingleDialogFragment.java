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

/** ��һ,������ʾ��fragment */
public abstract class BaseSingleDialogFragment extends DialogFragment {

	//
	// ========set=========================================================
	//

	/** �Ƿ��һ�ɾ�� */
	protected boolean canRightScrollDismiss() {
		return true;
	}

	/** �Ƿ���ע��findView���� */
	protected boolean isAnnotationUseing() {
		return false;
	}

	/** ����and��ʧ�Ķ���Ч��style */
	protected int getWindowAnimationRes() {
		return android.R.style.Animation_Dialog;
	}

	/** �����theme */
	protected int getWindowTheme() {
		return android.R.style.Theme_Translucent_NoTitleBar;
	}

	/** �Ƿ����ondismissListener(��Ϊ�ܶ�����²�ȷ��ʲôʱ��,���Զ�̬����) */
	protected void setCanCallDismissListener(boolean canCallDismissListener) {
		this.canCallDismissListener = canCallDismissListener;
	}

	/** ���ذ�ť��id */
	protected int getBackBtnId() {
		// TODO
		return 0;
	}

	/** ���ذ�ť����¼� */
	protected void onBackBtnPress() {
		dismiss();
	}

	/** ��һ��resumeִ�� */
	protected void onFirstResume() {
		// empty
	}

	/** ���ڵĿ� */
	protected int getWidth() {
		return -1;
	}

	/** ���ڵĸ� */
	protected int getHeight() {
		return -1;
	}

	/** ���ô��ڶ��� */
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

	/** ���߳�ִ�� */
	protected void runOnMain(Runnable run) {
		activity.runOnUiThread(run);
	}

	/** id�ҿؼ� */
	protected View findView(int id) {
		return rootView.findViewById(id);
	}

	/** tag�ҿؼ� */
	protected View findView(String tag) {
		return rootView.findViewWithTag(tag);
	}

	/** ��ʼ��,��onCreate��ִ�� */
	protected void init() {
	};

	/** ��ȡ��������Դ */
	protected abstract int getRootViewRes();

	/** ��Ҫ���� */
	protected abstract void doMain();

	/** ��Ҫ����2 (����õ�) */
	protected void doMain2() {
	}

	/** ��Ҫ����3 (����õ�) */
	protected void doMain3() {
	}

	/** ���ٺ�ִ��,��Դ�ͷ� */
	protected abstract void release();

	//
	// =======public========================================================
	//

	/** ������ʧListener */
	public void setOnDismissListener(Runnable onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	/** ��ȡ������ */
	public View getRootView() {
		return rootView;
	}

	/** ��ȡ����ɾ����viewpager(�������ɾ��������,����null) */
	public ViewPager getScrollPager() {
		return viewpager;
	}

	//
	// =======life circle==========================================================
	//

	// ��ʧ��ص�listener
	private Runnable onDismissListener;
	// ��ʧ���Ƿ�ִ��onDismissListener
	private boolean canCallDismissListener = true;
	// ��¼�Ƿ��ǵ�һ��ִ��onResume
	private boolean isFirstResume = true;
	// ������
	private View rootView;
	// ����ɾ����viewpager
	private ViewPager viewpager;

	@Override
	public void show(FragmentManager manager, String tag) {
		// ���tag�Ƿ���SingleManager��
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
		// ��ֵActivity
		this.activity = (FragmentActivity) activity;
		// ע���Լ���SingleManager
		SingleManager.add(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����theme
		setStyle(STYLE_NO_TITLE, getWindowTheme());
		// ��ʼ��
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// ��������һ�ɾ�� ,ֱ�ӷ��ظ�����
		if (!canRightScrollDismiss())
			return rootView = inflater.inflate(getRootViewRes(), null);
		// ����һ��viewpager
		viewpager = new ViewPager(activity);
		viewpager.setBackgroundColor(Color.TRANSPARENT);
		viewpager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
		// ���������
		rootView = inflater.inflate(getRootViewRes(), null);
		// ����adapter,���ֵ���viewpager
		viewpager.setAdapter(new PAdapter(rootView));
		// �ƶ����ڶ�ҳ
		viewpager.setCurrentItem(1, false);
		// ���û����¼�
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
		// ���ô��ڴ�С(Ĭ��ȫ��)
		view.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getHeight()));
		// ��ʼ�����ذ�ť�¼�
		View backView = findView(getBackBtnId());
		if (backView != null)
			backView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onBackBtnPress();
				}
			});
		// ��ʼ��ע��View
		if (isAnnotationUseing())
			initAnnotationView();
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		// ִ����Ҫ�߼�����
		doMain();
		doMain2();
		doMain3();
	}

	@Override
	public void onResume() {
		if (isFirstResume) {
			setWindowAnim(getWindowAnimationRes());
			isFirstResume = false;
			// ���ñ���͸��
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
		// �Ƴ�ע��SingleManager
		SingleManager.remove(getClass());
		// ִ����ʧListener(�������)
		if (onDismissListener != null && canCallDismissListener)
			onDismissListener.run();
		super.onDismiss(dialog);
	}

	@Override
	public void onDestroy() {
		// �ͷ���Դ
		release();
		super.onDestroy();
	}

	//
	// ===============================priavte=============================
	//

	/* ��ʼ��ע��view */
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

	/* viewpager �� adapter */
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
