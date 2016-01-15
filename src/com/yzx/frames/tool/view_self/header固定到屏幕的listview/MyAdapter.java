package com.yzx.frames.tool.view_self.header固定到屏幕的listview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

	private List<Lession> list;
	private Context context;

	private int layout_header = android.R.layout.simple_list_item_1;
	private int layout_content = android.R.layout.simple_list_item_2;

	public MyAdapter(List<Lession> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder1 h1;
		Holder2 h2;
		int type = getItemViewType(position);

		// type 是 header
		if (type == 0) {
			if (convertView == null) {
				h1 = new Holder1();
				convertView = View.inflate(context, layout_header, null);
				h1.tv = (TextView) convertView.findViewById(android.R.id.list);
				convertView.setTag(h1);
			} else
				h1 = (Holder1) convertView.getTag();

			h1.tv.setText(list.get(position).getTitle());

			// type 是 content
		} else {
			if (convertView == null) {
				h2 = new Holder2();
				convertView = View.inflate(context, layout_content, null);
				h2.tv = (TextView) convertView.findViewById(android.R.id.list);
				convertView.setTag(h2);
			} else
				h2 = (Holder2) convertView.getTag();

			h2.tv.setText(list.get(position).getName());
		}

		return convertView;
	}

	private class Holder1 {
		TextView tv;
	}

	private class Holder2 {
		ImageView iv;
		TextView tv;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == 0;
	}

	@Override
	public int getItemViewType(int position) {
		Lession l = list.get(position);
		if ("0".equals(l.getType()))
			return 0;
		else
			return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
