/**
 * Copyright (C) 2015 HongCheng System Inc.
 * This file write by ljt in 2015年6月13日
 */
package com.example.servicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

/**
 * TODO
 *
 * @author fantasyliu
 * @version V1.0
 * @date 2016年2月1日下午11:54:24
 */
public class MyReceiver extends BroadcastReceiver {

	private TextView tv = null;

	/**
	 * 构造函数
	 */
	public MyReceiver(TextView tv) {
		this.tv = tv;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("------------------", "start数字：" + intent.getStringExtra("second"));
		tv.setText(intent.getStringExtra("second"));
	}

}
