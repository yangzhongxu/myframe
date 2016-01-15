/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

public abstract class VerticalPagerAdapter {

	private DataSetObservable mObservable = new DataSetObservable();

	public static final int POSITION_UNCHANGED = -1;
	public static final int POSITION_NONE = -2;

	public abstract int getCount();

	public void startUpdate(ViewGroup container) {
		startUpdate((View) container);
	}

	public Object instantiateItem(ViewGroup container, int position) {
		return instantiateItem((View) container, position);
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		destroyItem((View) container, position, object);
	}

	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		setPrimaryItem((View) container, position, object);
	}

	public void finishUpdate(ViewGroup container) {
		finishUpdate((View) container);
	}

	public void startUpdate(View container) {
	}

	public Object instantiateItem(View container, int position) {
		throw new UnsupportedOperationException("Required method instantiateItem was not overridden");
	}

	public void destroyItem(View container, int position, Object object) {
		throw new UnsupportedOperationException("Required method destroyItem was not overridden");
	}

	public void setPrimaryItem(View container, int position, Object object) {
	}

	public void finishUpdate(View container) {
	}

	public abstract boolean isViewFromObject(View view, Object object);

	public Parcelable saveState() {
		return null;
	}

	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	public int getItemPosition(Object object) {
		return POSITION_UNCHANGED;
	}

	public void notifyDataSetChanged() {
		mObservable.notifyChanged();
	}

	void registerDataSetObserver(DataSetObserver observer) {
		mObservable.registerObserver(observer);
	}

	void unregisterDataSetObserver(DataSetObserver observer) {
		mObservable.unregisterObserver(observer);
	}

	public CharSequence getPageTitle(int position) {
		return null;
	}
}
