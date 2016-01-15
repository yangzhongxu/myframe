package com.yzx.frames.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yzx.frames.R;

public class MainActivity extends FragmentActivity {

	public static Activity a;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		a = this;
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onDestroy() {
		a = null;
		super.onDestroy();
	}

}
