/**
 * Copyright (C) 2015 HongCheng System Inc.
 * This file write by ljt in 2015年6月13日
 */
package com.example.servicedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

/**
 *TODO
 *
 *@author		fantasyliu	
 *@version		V1.0
 *@date			2016年2月2日上午12:09:38
 */
public class TwoActivity extends Activity {

	private MyReceiver mReceiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_two);
		
		TextView tv = (TextView) findViewById(R.id.tv);
		mReceiver = new MyReceiver(tv);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.servicedemo.MyReceiver");
		registerReceiver(mReceiver, filter);
		
	}
}
