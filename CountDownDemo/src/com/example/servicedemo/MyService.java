/**
 * Copyright (C) 2015 HongCheng System Inc.
 * This file write by ljt in 2015年6月13日
 */
package com.example.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * service
 *
 * @author fantasyliu
 * @version V1.0
 * @date 2016年2月1日下午10:35:22
 */
public class MyService extends Service {
	private static final String TAG = "MyService" ;
	public static final String ACTION = "com.example.servicedemo.MyService";
	
	private int second = 60;
	private boolean start = false;
	
	private Intent intent = new Intent("com.example.servicedemo.MyReceiver");  
	private MyBinder myBinder = new MyBinder();
	
	class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "MyService onBind");
		start = true;
		startCountDown();
		return myBinder;
	}
	
	@Override
	public void onCreate() {
		Log.v(TAG, "MyService onCreate");
		super.onCreate();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "MyService onStart");
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "MyService onStartCommand");
		start = true;
		startCountDown();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startCountDown(){
		new Thread() {
			public void run() {
				if (start) {					
					while (second >= 0) {
						Log.e(TAG, second+"");
						 //发送Action为com.example.communication.RECEIVER的广播  
						 intent.putExtra("second", second+"");  
						 sendBroadcast(intent);  
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (second > 0) {							
							second--;
						} else {
							second = 60;
							start = false;
							break;
						}
					} 
				}
			};
		}.start();
	}
	
	public int getCurrentSecond() {
		return this.second;
	}
}
