/*
 * Copyright (C) 2012 The Android Open Source Project And Jay Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yzx.frames.tool.view_self.viewpager.京东详情页;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class VerticalViewPager extends ViewGroup {

	private static final String TAG = "VerticalViewPager";

	private static final boolean DEBUG = false;

	private static final boolean USE_CACHE = false;

	private static final int DEFAULT_OFFSCREEN_PAGES = 1;

	private static final int MAX_SETTLE_DURATION = 600;

	private static final int MIN_DISTANCE_FOR_FLING = 25;

	private static final int[] LAYOUT_ATTRS = new int[] { android.R.attr.layout_gravity };

	static class ItemInfo {
		Object object;
		int position;
		boolean scrolling;
	}

	private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
		public int compare(ItemInfo lhs, ItemInfo rhs) {
			return lhs.position - rhs.position;
		}
	};

	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	private final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();

	private VerticalPagerAdapter mAdapter;

	private int mCurItem;

	private int mRestoredCurItem = -1;

	private Parcelable mRestoredAdapterState = null;

	private ClassLoader mRestoredClassLoader = null;

	private Scroller mScroller;

	private PagerObserver mObserver;

	private int mPageMargin;

	private Drawable mMarginDrawable;

	private int mLeftPageBounds;

	private int mRightPageBounds;

	private int mChildWidthMeasureSpec;

	private int mChildHeightMeasureSpec;

	private boolean mInLayout;

	private boolean mScrollingCacheEnabled;

	private boolean mPopulatePending;

	private boolean mScrolling;

	private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;

	private boolean mIsBeingDragged;

	private boolean mIsUnableToDrag;

	private int mTouchSlop;

	private float mInitialMotionY;

	private float mLastMotionX;
	private float mLastMotionY;

	private int mActivePointerId = INVALID_POINTER;
	private static final int INVALID_POINTER = -1;

	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;

	private boolean mFakeDragging;
	private long mFakeDragBeginTime;

	private EdgeEffectCompat mTopEdge;
	private EdgeEffectCompat mBottomEdge;

	private boolean mFirstLayout = true;
	private boolean mCalledSuper;
	private int mDecorChildCount;

	private OnPageChangeListener mOnPageChangeListener;
	private OnPageChangeListener mInternalPageChangeListener;
	private OnAdapterChangeListener mAdapterChangeListener;

	public static final int SCROLL_STATE_IDLE = 0;

	public static final int SCROLL_STATE_DRAGGING = 1;

	public static final int SCROLL_STATE_SETTLING = 2;

	private int mScrollState = SCROLL_STATE_IDLE;

	public interface OnPageChangeListener {

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	public static class SimpleOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	interface OnAdapterChangeListener {
		public void onAdapterChanged(VerticalPagerAdapter oldAdapter, VerticalPagerAdapter newAdapter);
	}

	interface Decor {
	}

	public VerticalViewPager(Context context) {
		super(context);
		initViewPager();
	}

	public VerticalViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViewPager();
	}

	void initViewPager() {
		setWillNotDraw(false);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setFocusable(true);
		final Context context = getContext();
		mScroller = new Scroller(context, sInterpolator);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mTopEdge = new EdgeEffectCompat(context);
		mBottomEdge = new EdgeEffectCompat(context);
		final float density = context.getResources().getDisplayMetrics().density;
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
	}

	private void setScrollState(int newState) {
		if (mScrollState == newState) {
			return;
		}

		mScrollState = newState;
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(newState);
		}
	}

	public void setAdapter(VerticalPagerAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mObserver);
			mAdapter.startUpdate(this);
			for (int i = 0; i < mItems.size(); i++) {
				final ItemInfo ii = mItems.get(i);
				mAdapter.destroyItem(this, ii.position, ii.object);
			}
			mAdapter.finishUpdate(this);
			mItems.clear();
			removeNonDecorViews();
			mCurItem = 0;
			scrollTo(0, 0);
		}

		final VerticalPagerAdapter oldAdapter = mAdapter;
		mAdapter = adapter;

		if (mAdapter != null) {
			if (mObserver == null) {
				mObserver = new PagerObserver();
			}
			mAdapter.registerDataSetObserver(mObserver);
			mPopulatePending = false;
			if (mRestoredCurItem >= 0) {
				mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
				setCurrentItemInternal(mRestoredCurItem, false, true);
				mRestoredCurItem = -1;
				mRestoredAdapterState = null;
				mRestoredClassLoader = null;
			} else {
				populate();
			}
		}

		if (mAdapterChangeListener != null && oldAdapter != adapter) {
			mAdapterChangeListener.onAdapterChanged(oldAdapter, adapter);
		}
	}

	private void removeNonDecorViews() {
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			if (!lp.isDecor) {
				removeViewAt(i);
				i--;
			}
		}
	}

	public VerticalPagerAdapter getAdapter() {
		return mAdapter;
	}

	void setOnAdapterChangeListener(OnAdapterChangeListener listener) {
		mAdapterChangeListener = listener;
	}

	public void setCurrentItem(int item) {
		mPopulatePending = false;
		setCurrentItemInternal(item, !mFirstLayout, false);
	}

	public void setCurrentItem(int item, boolean smoothScroll) {
		mPopulatePending = false;
		setCurrentItemInternal(item, smoothScroll, false);
	}

	public int getCurrentItem() {
		return mCurItem;
	}

	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
		setCurrentItemInternal(item, smoothScroll, always, 0);
	}

	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
		if (mAdapter == null || mAdapter.getCount() <= 0) {
			setScrollingCacheEnabled(false);
			return;
		}
		if (!always && mCurItem == item && mItems.size() != 0) {
			setScrollingCacheEnabled(false);
			return;
		}
		if (item < 0) {
			item = 0;
		} else if (item >= mAdapter.getCount()) {
			item = mAdapter.getCount() - 1;
		}
		final int pageLimit = mOffscreenPageLimit;
		if (item > (mCurItem + pageLimit) || item < (mCurItem - pageLimit)) {
			for (int i = 0; i < mItems.size(); i++) {
				mItems.get(i).scrolling = true;
			}
		}
		final boolean dispatchSelected = mCurItem != item;
		mCurItem = item;
		populate();
		final int destY = (getHeight() + mPageMargin) * item;
		if (smoothScroll) {
			smoothScrollTo(0, destY, velocity);
			if (dispatchSelected && mOnPageChangeListener != null) {
				mOnPageChangeListener.onPageSelected(item);
			}
			if (dispatchSelected && mInternalPageChangeListener != null) {
				mInternalPageChangeListener.onPageSelected(item);
			}
		} else {
			if (dispatchSelected && mOnPageChangeListener != null) {
				mOnPageChangeListener.onPageSelected(item);
			}
			if (dispatchSelected && mInternalPageChangeListener != null) {
				mInternalPageChangeListener.onPageSelected(item);
			}
			completeScroll();
			scrollTo(0, destY);
		}
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener listener) {
		OnPageChangeListener oldListener = mInternalPageChangeListener;
		mInternalPageChangeListener = listener;
		return oldListener;
	}

	public int getOffscreenPageLimit() {
		return mOffscreenPageLimit;
	}

	public void setOffscreenPageLimit(int limit) {
		if (limit < DEFAULT_OFFSCREEN_PAGES) {
			Log.w(TAG, "Requested offscreen page limit " + limit + " too small; defaulting to " + DEFAULT_OFFSCREEN_PAGES);
			limit = DEFAULT_OFFSCREEN_PAGES;
		}
		if (limit != mOffscreenPageLimit) {
			mOffscreenPageLimit = limit;
			populate();
		}
	}

	public void setPageMargin(int marginPixels) {
		final int oldMargin = mPageMargin;
		mPageMargin = marginPixels;

		final int height = getHeight();
		recomputeScrollPosition(height, height, marginPixels, oldMargin);

		requestLayout();
	}

	public int getPageMargin() {
		return mPageMargin;
	}

	public void setPageMarginDrawable(Drawable d) {
		mMarginDrawable = d;
		if (d != null)
			refreshDrawableState();
		setWillNotDraw(d == null);
		invalidate();
	}

	public void setPageMarginDrawable(int resId) {
		setPageMarginDrawable(getContext().getResources().getDrawable(resId));
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mMarginDrawable;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		final Drawable d = mMarginDrawable;
		if (d != null && d.isStateful()) {
			d.setState(getDrawableState());
		}
	}

	float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f;
		f *= 0.3f * Math.PI / 2.0f;
		return (float) Math.sin(f);
	}

	void smoothScrollTo(int x, int y) {
		smoothScrollTo(x, y, 0);
	}

	void smoothScrollTo(int x, int y, int velocity) {
		if (getChildCount() == 0) {
			setScrollingCacheEnabled(false);
			return;
		}
		int sx = getScrollX();
		int sy = getScrollY();
		int dx = x - sx;
		int dy = y - sy;
		if (dx == 0 && dy == 0) {
			completeScroll();
			setScrollState(SCROLL_STATE_IDLE);
			return;
		}

		setScrollingCacheEnabled(true);
		mScrolling = true;
		setScrollState(SCROLL_STATE_SETTLING);

		final int height = getHeight();
		final int halfHeight = height / 2;
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dy) / height);
		final float distance = halfHeight + halfHeight * distanceInfluenceForSnapDuration(distanceRatio);

		int duration = 0;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageDelta = (float) Math.abs(dy) / (height + mPageMargin);
			duration = (int) ((pageDelta + 1) * 100);
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);

		mScroller.startScroll(sx, sy, dx, dy, duration);
		invalidate();
	}

	void addNewItem(int position, int index) {
		ItemInfo ii = new ItemInfo();
		ii.position = position;
		ii.object = mAdapter.instantiateItem(this, position);
		if (index < 0) {
			mItems.add(ii);
		} else {
			mItems.add(index, ii);
		}
	}

	void dataSetChanged() {
		boolean needPopulate = mItems.size() < 3 && mItems.size() < mAdapter.getCount();
		int newCurrItem = -1;

		boolean isUpdating = false;
		for (int i = 0; i < mItems.size(); i++) {
			final ItemInfo ii = mItems.get(i);
			final int newPos = mAdapter.getItemPosition(ii.object);

			if (newPos == VerticalPagerAdapter.POSITION_UNCHANGED) {
				continue;
			}

			if (newPos == VerticalPagerAdapter.POSITION_NONE) {
				mItems.remove(i);
				i--;

				if (!isUpdating) {
					mAdapter.startUpdate(this);
					isUpdating = true;
				}

				mAdapter.destroyItem(this, ii.position, ii.object);
				needPopulate = true;

				if (mCurItem == ii.position) {
					newCurrItem = Math.max(0, Math.min(mCurItem, mAdapter.getCount() - 1));
				}
				continue;
			}

			if (ii.position != newPos) {
				if (ii.position == mCurItem) {
					newCurrItem = newPos;
				}

				ii.position = newPos;
				needPopulate = true;
			}
		}

		if (isUpdating) {
			mAdapter.finishUpdate(this);
		}

		Collections.sort(mItems, COMPARATOR);

		if (newCurrItem >= 0) {
			setCurrentItemInternal(newCurrItem, false, true);
			needPopulate = true;
		}
		if (needPopulate) {
			populate();
			requestLayout();
		}
	}

	void populate() {
		if (mAdapter == null) {
			return;
		}

		if (mPopulatePending) {
			if (DEBUG)
				Log.i(TAG, "populate is pending, skipping for now...");
			return;
		}

		if (getWindowToken() == null) {
			return;
		}

		mAdapter.startUpdate(this);

		final int pageLimit = mOffscreenPageLimit;
		final int startPos = Math.max(0, mCurItem - pageLimit);
		final int N = mAdapter.getCount();
		final int endPos = Math.min(N - 1, mCurItem + pageLimit);

		if (DEBUG)
			Log.v(TAG, "populating: startPos=" + startPos + " endPos=" + endPos);

		int lastPos = -1;
		for (int i = 0; i < mItems.size(); i++) {
			ItemInfo ii = mItems.get(i);
			if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
				if (DEBUG)
					Log.i(TAG, "removing: " + ii.position + " @ " + i);
				mItems.remove(i);
				i--;
				mAdapter.destroyItem(this, ii.position, ii.object);
			} else if (lastPos < endPos && ii.position > startPos) {
				lastPos++;
				if (lastPos < startPos) {
					lastPos = startPos;
				}
				while (lastPos <= endPos && lastPos < ii.position) {
					if (DEBUG)
						Log.i(TAG, "inserting: " + lastPos + " @ " + i);
					addNewItem(lastPos, i);
					lastPos++;
					i++;
				}
			}
			lastPos = ii.position;
		}

		lastPos = mItems.size() > 0 ? mItems.get(mItems.size() - 1).position : -1;
		if (lastPos < endPos) {
			lastPos++;
			lastPos = lastPos > startPos ? lastPos : startPos;
			while (lastPos <= endPos) {
				if (DEBUG)
					Log.i(TAG, "appending: " + lastPos);
				addNewItem(lastPos, -1);
				lastPos++;
			}
		}

		if (DEBUG) {
			Log.i(TAG, "Current page list:");
			for (int i = 0; i < mItems.size(); i++) {
				Log.i(TAG, "#" + i + ": page " + mItems.get(i).position);
			}
		}

		ItemInfo curItem = null;
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).position == mCurItem) {
				curItem = mItems.get(i);
				break;
			}
		}
		mAdapter.setPrimaryItem(this, mCurItem, curItem != null ? curItem.object : null);

		mAdapter.finishUpdate(this);

		if (hasFocus()) {
			View currentFocused = findFocus();
			ItemInfo ii = currentFocused != null ? infoForAnyChild(currentFocused) : null;
			if (ii == null || ii.position != mCurItem) {
				for (int i = 0; i < getChildCount(); i++) {
					View child = getChildAt(i);
					ii = infoForChild(child);
					if (ii != null && ii.position == mCurItem) {
						if (child.requestFocus(FOCUS_FORWARD)) {
							break;
						}
					}
				}
			}
		}
	}

	public static class SavedState extends BaseSavedState {
		int position;
		Parcelable adapterState;
		ClassLoader loader;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(position);
			out.writeParcelable(adapterState, flags);
		}

		@Override
		public String toString() {
			return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + position + "}";
		}

		public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat
				.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
					@Override
					public SavedState createFromParcel(Parcel in, ClassLoader loader) {
						return new SavedState(in, loader);
					}

					@Override
					public SavedState[] newArray(int size) {
						return new SavedState[size];
					}
				});

		SavedState(Parcel in, ClassLoader loader) {
			super(in);
			if (loader == null) {
				loader = getClass().getClassLoader();
			}
			position = in.readInt();
			adapterState = in.readParcelable(loader);
			this.loader = loader;
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.position = mCurItem;
		if (mAdapter != null) {
			ss.adapterState = mAdapter.saveState();
		}
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		if (mAdapter != null) {
			mAdapter.restoreState(ss.adapterState, ss.loader);
			setCurrentItemInternal(ss.position, false, true);
		} else {
			mRestoredCurItem = ss.position;
			mRestoredAdapterState = ss.adapterState;
			mRestoredClassLoader = ss.loader;
		}
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (!checkLayoutParams(params)) {
			params = generateLayoutParams(params);
		}
		final LayoutParams lp = (LayoutParams) params;
		lp.isDecor |= child instanceof Decor;
		if (mInLayout) {
			if (lp != null && lp.isDecor) {
				throw new IllegalStateException("Cannot add pager decor view during layout");
			}
			addViewInLayout(child, index, params);
			child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
		} else {
			super.addView(child, index, params);
		}

		if (USE_CACHE) {
			if (child.getVisibility() != GONE) {
				child.setDrawingCacheEnabled(mScrollingCacheEnabled);
			} else {
				child.setDrawingCacheEnabled(false);
			}
		}
	}

	ItemInfo infoForChild(View child) {
		for (int i = 0; i < mItems.size(); i++) {
			ItemInfo ii = mItems.get(i);
			if (mAdapter.isViewFromObject(child, ii.object)) {
				return ii;
			}
		}
		return null;
	}

	ItemInfo infoForAnyChild(View child) {
		ViewParent parent;
		while ((parent = child.getParent()) != this) {
			if (parent == null || !(parent instanceof View)) {
				return null;
			}
			child = (View) parent;
		}
		return infoForChild(child);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mFirstLayout = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		int childWidthSize = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int childHeightSize = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

		int size = getChildCount();
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp != null && lp.isDecor) {
					final int hgrav = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
					final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
					Log.d(TAG, "gravity: " + lp.gravity + " hgrav: " + hgrav + " vgrav: " + vgrav);
					int widthMode = MeasureSpec.AT_MOST;
					int heightMode = MeasureSpec.AT_MOST;
					boolean consumeVertical = vgrav == Gravity.TOP || vgrav == Gravity.BOTTOM;
					boolean consumeHorizontal = hgrav == Gravity.LEFT || hgrav == Gravity.RIGHT;

					if (consumeVertical) {
						widthMode = MeasureSpec.EXACTLY;
					} else if (consumeHorizontal) {
						heightMode = MeasureSpec.EXACTLY;
					}

					final int widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, widthMode);
					final int heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, heightMode);
					child.measure(widthSpec, heightSpec);

					if (consumeVertical) {
						childHeightSize -= child.getMeasuredHeight();
					} else if (consumeHorizontal) {
						childWidthSize -= child.getMeasuredWidth();
					}
				}
			}
		}

		mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);

		mInLayout = true;
		populate();
		mInLayout = false;

		size = getChildCount();
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				if (DEBUG)
					Log.v(TAG, "Measuring #" + i + " " + child + ": " + mChildWidthMeasureSpec);

				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp == null || !lp.isDecor) {
					child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
				}
			}
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (h != oldh) {
			recomputeScrollPosition(h, oldh, mPageMargin, mPageMargin);
		}
	}

	private void recomputeScrollPosition(int height, int oldHeight, int margin, int oldMargin) {
		final int heightWithMargin = height + margin;
		if (oldHeight > 0) {
			final int oldScrollPos = getScrollY();
			final int oldwwm = oldHeight + oldMargin;
			final int oldScrollItem = oldScrollPos / oldwwm;
			final float scrollOffset = (float) (oldScrollPos % oldwwm) / oldwwm;
			final int scrollPos = (int) ((oldScrollItem + scrollOffset) * heightWithMargin);
			scrollTo(getScrollX(), scrollPos);
			if (!mScroller.isFinished()) {
				final int newDuration = mScroller.getDuration() - mScroller.timePassed();
				mScroller.startScroll(0, scrollPos, mCurItem * heightWithMargin, 0, newDuration);
			}
		} else {
			int scrollPos = mCurItem * heightWithMargin;
			if (scrollPos != getScrollY()) {
				completeScroll();
				scrollTo(getScrollX(), scrollPos);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mInLayout = true;
		populate();
		mInLayout = false;

		final int count = getChildCount();
		int width = r - l;
		int height = b - t;
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();
		final int scrollY = getScrollY();

		int decorCount = 0;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				ItemInfo ii;
				int childLeft = 0;
				int childTop = 0;
				if (lp.isDecor) {
					final int hgrav = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
					final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
					switch (hgrav) {
					default:
						childLeft = paddingLeft;
						break;
					case Gravity.LEFT:
						childLeft = paddingLeft;
						paddingLeft += child.getMeasuredWidth();
						break;
					case Gravity.CENTER_HORIZONTAL:
						childLeft = Math.max((width - child.getMeasuredWidth()) / 2, paddingLeft);
						break;
					case Gravity.RIGHT:
						childLeft = width - paddingRight - child.getMeasuredWidth();
						paddingRight += child.getMeasuredWidth();
						break;
					}
					switch (vgrav) {
					default:
						childTop = paddingTop;
						break;
					case Gravity.TOP:
						childTop = paddingTop;
						paddingTop += child.getMeasuredHeight();
						break;
					case Gravity.CENTER_VERTICAL:
						childTop = Math.max((height - child.getMeasuredHeight()) / 2, paddingTop);
						break;
					case Gravity.BOTTOM:
						childTop = height - paddingBottom - child.getMeasuredHeight();
						paddingBottom += child.getMeasuredHeight();
						break;
					}

					childTop += scrollY;
					decorCount++;
					child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
				} else if ((ii = infoForChild(child)) != null) {
					int toff = (height + mPageMargin) * ii.position;
					childLeft = paddingLeft;
					childTop = paddingTop + toff;

					if (DEBUG)
						Log.v(TAG,
								"Positioning #" + i + " " + child + " f=" + ii.object + ":" + childLeft + "," + childTop + " "
										+ child.getMeasuredWidth() + "x" + child.getMeasuredHeight());
					child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
				}
			}
		}

		mLeftPageBounds = paddingLeft;
		mRightPageBounds = width - paddingRight;
		mDecorChildCount = decorCount;
		mFirstLayout = false;
	}

	@Override
	public void computeScroll() {
		if (DEBUG)
			Log.i(TAG, "computeScroll: finished=" + mScroller.isFinished());
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				if (DEBUG)
					Log.i(TAG, "computeScroll: still scrolling");
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();

				if (oldX != x || oldY != y) {
					scrollTo(x, y);
					pageScrolled(y);
				}

				invalidate();
				return;
			}
		}

		completeScroll();
	}

	private void pageScrolled(int ypos) {
		final int heightWithMargin = getHeight() + mPageMargin;
		final int position = ypos / heightWithMargin;
		final int offsetPixels = ypos % heightWithMargin;
		final float offset = (float) offsetPixels / heightWithMargin;

		mCalledSuper = false;
		onPageScrolled(position, offset, offsetPixels);
		if (!mCalledSuper) {
			throw new IllegalStateException("onPageScrolled did not call superclass implementation");
		}
	}

	protected void onPageScrolled(int position, float offset, int offsetPixels) {
		if (mDecorChildCount > 0) {
			final int scrollY = getScrollY();
			int paddingTop = getPaddingTop();
			int paddingBottom = getPaddingBottom();
			final int height = getHeight();
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = getChildAt(i);
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (!lp.isDecor)
					continue;

				final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
				int childTop = 0;
				switch (vgrav) {
				default:
					childTop = paddingTop;
					break;
				case Gravity.TOP:
					childTop = paddingTop;
					paddingTop += child.getHeight();
					break;
				case Gravity.CENTER_HORIZONTAL:
					childTop = Math.max((height - child.getMeasuredHeight()) / 2, paddingTop);
					break;
				case Gravity.BOTTOM:
					childTop = height - paddingBottom - child.getMeasuredHeight();
					paddingBottom += child.getMeasuredHeight();
					break;
				}
				childTop += scrollY;

				final int childOffset = childTop - child.getTop();
				if (childOffset != 0) {
					child.offsetTopAndBottom(childOffset);
				}
			}
		}

		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
		if (mInternalPageChangeListener != null) {
			mInternalPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
		mCalledSuper = true;
	}

	private void completeScroll() {
		boolean needPopulate = mScrolling;
		if (needPopulate) {
			setScrollingCacheEnabled(false);
			mScroller.abortAnimation();
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (oldX != x || oldY != y) {
				scrollTo(x, y);
			}
			setScrollState(SCROLL_STATE_IDLE);
		}
		mPopulatePending = false;
		mScrolling = false;
		for (int i = 0; i < mItems.size(); i++) {
			ItemInfo ii = mItems.get(i);
			if (ii.scrolling) {
				needPopulate = true;
				ii.scrolling = false;
			}
		}
		if (needPopulate) {
			populate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			if (DEBUG)
				Log.v(TAG, "Intercept done!");
			mIsBeingDragged = false;
			mIsUnableToDrag = false;
			mActivePointerId = INVALID_POINTER;
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN) {
			if (mIsBeingDragged) {
				if (DEBUG)
					Log.v(TAG, "Intercept returning true!");
				return true;
			}
			if (mIsUnableToDrag) {
				if (DEBUG)
					Log.v(TAG, "Intercept returning false!");
				return false;
			}
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				break;
			}

			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
			final float x = MotionEventCompat.getX(ev, pointerIndex);
			final float xDiff = Math.abs(x - mLastMotionX);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float dy = y - mLastMotionY;
			final float yDiff = Math.abs(dy);

			if (DEBUG)
				Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);

			if (canScroll(this, false, (int) dy, (int) x, (int) y)) {
				mInitialMotionY = mLastMotionY = y;
				mLastMotionX = x;
				return false;
			}
			if (yDiff > mTouchSlop && yDiff > xDiff) {
				if (DEBUG)
					Log.v(TAG, "Starting drag!");
				mIsBeingDragged = true;
				setScrollState(SCROLL_STATE_DRAGGING);
				mLastMotionY = y;
				setScrollingCacheEnabled(true);
			} else {
				if (xDiff > mTouchSlop) {
					if (DEBUG)
						Log.v(TAG, "Starting unable to drag!");
					mIsUnableToDrag = true;
				}
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			mLastMotionX = ev.getX();
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

			if (mScrollState == SCROLL_STATE_SETTLING) {
				mIsBeingDragged = true;
				mIsUnableToDrag = false;
				setScrollState(SCROLL_STATE_DRAGGING);
			} else {
				completeScroll();
				mIsBeingDragged = false;
				mIsUnableToDrag = false;
			}

			if (DEBUG)
				Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY + " mIsBeingDragged=" + mIsBeingDragged + "mIsUnableToDrag="
						+ mIsUnableToDrag);
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}

		if (!mIsBeingDragged) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);
		}

		return mIsBeingDragged;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mFakeDragging) {
			return true;
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
			return false;
		}

		if (mAdapter == null || mAdapter.getCount() == 0) {
			return false;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		boolean needsInvalidate = false;

		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			completeScroll();
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			break;
		}
		case MotionEvent.ACTION_MOVE:
			if (!mIsBeingDragged) {
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float xDiff = Math.abs(x - mLastMotionX);
				final float y = MotionEventCompat.getY(ev, pointerIndex);
				final float yDiff = Math.abs(y - mLastMotionY);
				if (DEBUG)
					Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
				if (yDiff > mTouchSlop && yDiff > xDiff) {
					if (DEBUG)
						Log.v(TAG, "Starting drag!");
					mIsBeingDragged = true;
					mLastMotionY = y;
					setScrollState(SCROLL_STATE_DRAGGING);
					setScrollingCacheEnabled(true);
				}
			}
			if (mIsBeingDragged) {
				final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float y = MotionEventCompat.getY(ev, activePointerIndex);
				final float deltaY = mLastMotionY - y;
				mLastMotionY = y;
				float oldScrollY = getScrollY();
				float scrollY = oldScrollY + deltaY;
				final int height = getHeight();
				final int heightWithMargin = height + mPageMargin;

				final int lastItemIndex = mAdapter.getCount() - 1;
				final float topBound = Math.max(0, (mCurItem - 1) * heightWithMargin);
				final float bottomBound = Math.min(mCurItem + 1, lastItemIndex) * heightWithMargin;
				if (scrollY < topBound) {
					if (topBound == 0) {
						float over = -scrollY;
						needsInvalidate = mTopEdge.onPull(over / height);
					}
					scrollY = topBound;
				} else if (scrollY > bottomBound) {
					if (bottomBound == lastItemIndex * heightWithMargin) {
						float over = scrollY - bottomBound;
						needsInvalidate = mBottomEdge.onPull(over / height);
					}
					scrollY = bottomBound;
				}
				mLastMotionY += scrollY - (int) scrollY;
				scrollTo(getScrollX(), (int) scrollY);
				pageScrolled((int) scrollY);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker, mActivePointerId);
				mPopulatePending = true;
				final int heightWithMargin = getHeight() + mPageMargin;
				final int scrollY = getScrollY();
				final int currentPage = scrollY / heightWithMargin;
				final float pageOffset = (float) (scrollY % heightWithMargin) / heightWithMargin;
				final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float y = MotionEventCompat.getY(ev, activePointerIndex);
				final int totalDelta = (int) (y - mInitialMotionY);
				int nextPage = determineTargetPage(currentPage, pageOffset, initialVelocity, totalDelta);
				setCurrentItemInternal(nextPage, true, true, initialVelocity);

				mActivePointerId = INVALID_POINTER;
				endDrag();
				needsInvalidate = mTopEdge.onRelease() | mBottomEdge.onRelease();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged) {
				setCurrentItemInternal(mCurItem, true, true);
				mActivePointerId = INVALID_POINTER;
				endDrag();
				needsInvalidate = mTopEdge.onRelease() | mBottomEdge.onRelease();
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			final float y = MotionEventCompat.getY(ev, index);
			mLastMotionY = y;
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			mLastMotionY = MotionEventCompat.getY(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
			break;
		}
		if (needsInvalidate) {
			invalidate();
		}
		return true;
	}

	private int determineTargetPage(int currentPage, float pageOffset, int velocity, int deltaY) {
		int targetPage;
		if (Math.abs(deltaY) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
			targetPage = velocity > 0 ? currentPage : currentPage + 1;
		} else {
			targetPage = (int) (currentPage + pageOffset + 0.5f);
		}

		return targetPage;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		boolean needsInvalidate = false;

		final int overScrollMode = ViewCompat.getOverScrollMode(this);
		if (overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS
				|| (overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && mAdapter != null && mAdapter.getCount() > 1)) {
			if (!mTopEdge.isFinished()) {
				final int restoreCount = canvas.save();
				final int width = getWidth() - getPaddingLeft() - getPaddingRight();

				canvas.rotate(270);
				canvas.translate(-width + getPaddingLeft(), 0);
				mTopEdge.setSize(width, getHeight());
				needsInvalidate |= mTopEdge.draw(canvas);
				canvas.restoreToCount(restoreCount);
			}
			if (!mBottomEdge.isFinished()) {
				final int restoreCount = canvas.save();
				final int width = getWidth() - getPaddingLeft() - getPaddingRight();
				final int height = getHeight();
				final int itemCount = mAdapter != null ? mAdapter.getCount() : 1;

				canvas.rotate(180);
				canvas.translate(-width + getPaddingLeft(), -itemCount * (height + mPageMargin) + mPageMargin);
				mBottomEdge.setSize(width, height);
				needsInvalidate |= mBottomEdge.draw(canvas);
				canvas.restoreToCount(restoreCount);
			}
		} else {
			mTopEdge.finish();
			mBottomEdge.finish();
		}

		if (needsInvalidate) {
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mPageMargin > 0 && mMarginDrawable != null) {
			final int scrollY = getScrollY();
			final int height = getHeight();
			final int offset = scrollY % (height + mPageMargin);
			if (offset != 0) {
				final int top = scrollY - offset + height;
				mMarginDrawable.setBounds(mLeftPageBounds, top, mRightPageBounds, top + mPageMargin);
				mMarginDrawable.draw(canvas);
			}
		}
	}

	public boolean beginFakeDrag() {
		if (mIsBeingDragged) {
			return false;
		}
		mFakeDragging = true;
		setScrollState(SCROLL_STATE_DRAGGING);
		mInitialMotionY = mLastMotionY = 0;
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
		final long time = SystemClock.uptimeMillis();
		final MotionEvent ev = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, 0, 0, 0);
		mVelocityTracker.addMovement(ev);
		ev.recycle();
		mFakeDragBeginTime = time;
		return true;
	}

	public void endFakeDrag() {
		if (!mFakeDragging) {
			throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
		}

		final VelocityTracker velocityTracker = mVelocityTracker;
		velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
		int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);
		mPopulatePending = true;
		final int totalDelta = (int) (mLastMotionY - mInitialMotionY);
		final int scrollY = getScrollY();
		final int heightWithMargin = getHeight() + mPageMargin;
		final int currentPage = scrollY / heightWithMargin;
		final float pageOffset = (float) (scrollY % heightWithMargin) / heightWithMargin;
		int nextPage = determineTargetPage(currentPage, pageOffset, initialVelocity, totalDelta);
		setCurrentItemInternal(nextPage, true, true, initialVelocity);
		endDrag();

		mFakeDragging = false;
	}

	public void fakeDragBy(float yOffset) {
		if (!mFakeDragging) {
			throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
		}

		mLastMotionY += yOffset;
		float scrollY = getScrollY() - yOffset;
		final int height = getHeight();
		final int heightWithMargin = height + mPageMargin;

		final float topBound = Math.max(0, (mCurItem - 1) * heightWithMargin);
		final float bottomBound = Math.min(mCurItem + 1, mAdapter.getCount() - 1) * heightWithMargin;
		if (scrollY < topBound) {
			scrollY = topBound;
		} else if (scrollY > bottomBound) {
			scrollY = bottomBound;
		}
		mLastMotionY += scrollY - (int) scrollY;
		scrollTo(getScrollX(), (int) scrollY);
		pageScrolled((int) scrollY);

		final long time = SystemClock.uptimeMillis();
		final MotionEvent ev = MotionEvent.obtain(mFakeDragBeginTime, time, MotionEvent.ACTION_MOVE, 0, mLastMotionY, 0);
		mVelocityTracker.addMovement(ev);
		ev.recycle();
	}

	public boolean isFakeDragging() {
		return mFakeDragging;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	private void endDrag() {
		mIsBeingDragged = false;
		mIsUnableToDrag = false;

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void setScrollingCacheEnabled(boolean enabled) {
		if (mScrollingCacheEnabled != enabled) {
			mScrollingCacheEnabled = enabled;
			if (USE_CACHE) {
				final int size = getChildCount();
				for (int i = 0; i < size; ++i) {
					final View child = getChildAt(i);
					if (child.getVisibility() != GONE) {
						child.setDrawingCacheEnabled(enabled);
					}
				}
			}
		}
	}

	protected boolean canScroll(View v, boolean checkV, int dy, int x, int y) {
		if (v instanceof ViewGroup) {
			final ViewGroup group = (ViewGroup) v;
			final int scrollX = v.getScrollX();
			final int scrollY = v.getScrollY();
			final int count = group.getChildCount();
			for (int i = count - 1; i >= 0; i--) {
				final View child = group.getChildAt(i);
				if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() && y + scrollY >= child.getTop()
						&& y + scrollY < child.getBottom()
						&& canScroll(child, true, dy, x + scrollX - child.getLeft(), y + scrollY - child.getTop())) {
					return true;
				}
			}
		}

		return checkV && ViewCompat.canScrollVertically(v, -dy);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event) || executeKeyEvent(event);
	}

	public boolean executeKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				handled = arrowScroll(FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				handled = arrowScroll(FOCUS_DOWN);
				break;
			case KeyEvent.KEYCODE_TAB:
				if (Build.VERSION.SDK_INT >= 11) {
					if (KeyEventCompat.hasNoModifiers(event)) {
						handled = arrowScroll(FOCUS_FORWARD);
					} else if (KeyEventCompat.hasModifiers(event, KeyEvent.META_SHIFT_ON)) {
						handled = arrowScroll(FOCUS_BACKWARD);
					}
				}
				break;
			}
		}
		return handled;
	}

	public boolean arrowScroll(int direction) {
		View currentFocused = findFocus();
		if (currentFocused == this)
			currentFocused = null;

		boolean handled = false;

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
		if (nextFocused != null && nextFocused != currentFocused) {
			if (direction == View.FOCUS_UP) {
				if (currentFocused != null && nextFocused.getTop() >= currentFocused.getTop()) {
					handled = pageUp();
				} else {
					handled = nextFocused.requestFocus();
				}
			} else if (direction == View.FOCUS_DOWN) {
				if (currentFocused != null && nextFocused.getTop() <= currentFocused.getTop()) {
					handled = pageDown();
				} else {
					handled = nextFocused.requestFocus();
				}
			}
		} else if (direction == FOCUS_UP || direction == FOCUS_BACKWARD) {
			handled = pageUp();
		} else if (direction == FOCUS_DOWN || direction == FOCUS_FORWARD) {
			handled = pageDown();
		}
		if (handled) {
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		}
		return handled;
	}

	boolean pageUp() {
		if (mCurItem > 0) {
			setCurrentItem(mCurItem - 1, true);
			return true;
		}
		return false;
	}

	boolean pageDown() {
		if (mAdapter != null && mCurItem < (mAdapter.getCount() - 1)) {
			setCurrentItem(mCurItem + 1, true);
			return true;
		}
		return false;
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
		final int focusableCount = views.size();

		final int descendantFocusability = getDescendantFocusability();

		if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
			for (int i = 0; i < getChildCount(); i++) {
				final View child = getChildAt(i);
				if (child.getVisibility() == VISIBLE) {
					ItemInfo ii = infoForChild(child);
					if (ii != null && ii.position == mCurItem) {
						child.addFocusables(views, direction, focusableMode);
					}
				}
			}
		}

		if (descendantFocusability != FOCUS_AFTER_DESCENDANTS || (focusableCount == views.size())) {
			if (!isFocusable()) {
				return;
			}
			if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
				return;
			}
			if (views != null) {
				views.add(this);
			}
		}
	}

	@Override
	public void addTouchables(ArrayList<View> views) {
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == VISIBLE) {
				ItemInfo ii = infoForChild(child);
				if (ii != null && ii.position == mCurItem)
					child.addTouchables(views);
			}
		}
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		int index;
		int increment;
		int end;
		int count = getChildCount();
		if ((direction & FOCUS_FORWARD) != 0) {
			index = 0;
			increment = 1;
			end = count;
		} else {
			index = count - 1;
			increment = -1;
			end = -1;
		}
		for (int i = index; i != end; i += increment) {
			View child = getChildAt(i);
			if (child.getVisibility() == VISIBLE) {
				ItemInfo ii = infoForChild(child);
				if (ii != null && ii.position == mCurItem) {
					if (child.requestFocus(direction, previouslyFocusedRect)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == VISIBLE) {
				final ItemInfo ii = infoForChild(child);
				if (ii != null && ii.position == mCurItem && child.dispatchPopulateAccessibilityEvent(event)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams();
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return generateDefaultLayoutParams();
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams && super.checkLayoutParams(p);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	private class PagerObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			dataSetChanged();
		}

		@Override
		public void onInvalidated() {
			dataSetChanged();
		}
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {
		public boolean isDecor;

		public int gravity;

		public LayoutParams() {
			super(MATCH_PARENT, MATCH_PARENT);
		}

		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);
			final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
			gravity = a.getInteger(0, Gravity.NO_GRAVITY);
			a.recycle();
		}
	}
}
