package com.tnc.service;
/*package com.bigbutton.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.bigbutton.widgets.MessagePushDisplayView;

public class ChatHeadService extends Service {

	private WindowManager windowManager;
	private MessagePushDisplayView chatHead;
	Handler handler=new Handler();

	@Override public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override public void onCreate() {
		super.onCreate();
		handler.post(new Runnable() {

			@Override public void run() { //
				Toast.makeText(getApplicationContext(),
						"Success",Toast.LENGTH_LONG).show(); //
				//system.out.println("Succes 1-----------------------------");
			} });
		chatHead = new MessagePushDisplayView(this);
		//		chatHead.setImageResource(R.drawable.msg_toggle);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.RIGHT | Gravity.TOP;
	
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = this.getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
//		params.setTitle("Load Average");
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(chatHead, layoutParams);
		
		chatHead.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				handler.post(new Runnable() {

					@Override public void run() { //
						Toast.makeText(getApplicationContext(),
								"Success",Toast.LENGTH_LONG).show(); //
						//system.out.println("Succes 1-----------------------------");
					} });
				return true;
			}
		});
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.msg_toggle);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(chatHead, params);
	}

	//	@Override
	//	public int onStartCommand(Intent intent, int flags, int startId) {
	//		handler.post(new Runnable() {
	//
	//			@Override public void run() { //
	//				//system.out.println("Succes 2-----------------------------");
	//				Toast.makeText(getApplicationContext(),
	//						"Success2",Toast.LENGTH_LONG).show(); //
	//			} });
	//		return START_STICKY;
	//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
	}
}

*/