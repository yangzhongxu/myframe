package com.yzx.frames.tool.view_self.header固定到屏幕的listview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PinnedSectionListView listview = (PinnedSectionListView) findViewById(android.R.id.list);

		// 悬浮条的高度
		listview.setFloatItemHeight(80);
		// 是否有shadow
		listview.setShadowVisible(false);

		// 填充数据
		//
		List<Lession> list = new ArrayList<Lession>();
		for (int i = 0; i < 25; i++) {
			Lession l = new Lession();
			l.setName("heheheheheh");
			l.setTitle("title" + i);
			if (i % 5 == 0) {
				l.setType("0");
			} else {
				l.setType("1");
			}
			list.add(l);
		}

		// 设置adapter
		//
		MyAdapter adapter = new MyAdapter(list, this);
		listview.setAdapter(adapter);

	}

}
