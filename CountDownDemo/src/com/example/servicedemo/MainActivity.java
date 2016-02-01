package com.example.servicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.servicedemo.MyService.MyBinder;

public class MainActivity extends Activity {
	private static final String TAG = "MyServiceActivity";

	private TextView tv;
	Button bindBtn;
	Button startBtn;
	Button gotoBtn;
	private MyService mService = null;
	private MyReceiver mReceiver = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv = (TextView) findViewById(R.id.textView1);
		
		bindBtn = (Button) findViewById(R.id.button1);
		bindBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				bindService(new Intent(MyService.ACTION), conn, BIND_AUTO_CREATE);
				
			}
		});
		
		
		mReceiver = new MyReceiver(tv);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.servicedemo.MyReceiver");
		registerReceiver(mReceiver, filter);
		
		startBtn = (Button) findViewById(R.id.button2);
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startService(new Intent(MyService.ACTION)); 
			}
		});
		
		gotoBtn = (Button) findViewById(R.id.button3);
		gotoBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent(MainActivity.this, TwoActivity.class)); 
				
			}
		});
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "onServiceConnected");
			mService = ((MyBinder) service).getService();
			mHandler.post(mRunnable);
		}

		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "onServiceDisconnected");
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv.setText(msg.what+"");
			if (msg.what != 0) {				
				mHandler.post(mRunnable);
			} else {
				tv.setText("获取验证码");
			}
		};
	};
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.e("------------------", "数字：" + mService.getCurrentSecond());
			Message msg = new Message();
			msg.what = mService.getCurrentSecond();
			mHandler.sendMessage(msg);
		}
	};
	
	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy unbindService");
		unbindService(conn);
		unregisterReceiver(mReceiver);
		super.onDestroy();
	};
	
}