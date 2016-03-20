package com.example.androidtest;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import stretchable.StretchableListener;

public class MainActivity extends Activity {

	private Handler handler = new MyHandler(this);
	private RelativeLayout layout;
	private TextView helloT;
	private Timer timer;
	

	private static class MyHandler extends Handler {
		final private WeakReference<MainActivity> ma;

		private MyHandler(MainActivity a) {
			ma = new WeakReference<MainActivity>(a);
		}

		@Override
		public void handleMessage(Message msg) {
			if (ma == null) return;
			final MainActivity a = ma.get();
			if (msg.what == 0x123) {
				if(a.layout.getWidth()!=0) { 
					a.timer.cancel();
					a.helloT.setOnTouchListener(new StretchableListener(a, 100, 100, a.layout.getWidth()-100, a.layout.getHeight()-100) {
						@Override
						public void onClick(View v, MotionEvent event) {
							Toast.makeText(a, "just fo test", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (RelativeLayout) findViewById(R.id.layout);
		helloT = (TextView) findViewById(R.id.hello);
		timer = new Timer();
		TimerTask task = new TimerTask(){
			public void run() { 
				handler.sendEmptyMessage(0x123);
			} 
		}; 
		timer.schedule(task,10,1000);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
