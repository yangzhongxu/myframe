package com.yzx.frames.tool.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseCommonAdapter<T> extends android.widget.BaseAdapter {

	private Context context;
	private List<T> list;
	private final int myLayoutId;

	public BaseCommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		this.context = context;
		this.list = mDatas;
		this.myLayoutId = itemLayoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = ViewHolder.get(context, convertView, myLayoutId, position);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, T item);

	//

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public T getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
