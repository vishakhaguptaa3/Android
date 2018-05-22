package com.tnc.dialog;

import com.google.gson.Gson;
import com.tnc.GCMIntentService;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import com.tnc.webresponse.NotificationPushReponseBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class NotificationPushDisplayDialogActivity extends Activity implements OnClickListener
{
	public static boolean isNotificationPushDisplayDialogActivity = false;

	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	//	String title="",message="",messageSub="";
	private Gson gson;
	private TransparentProgressDialog  progress;
	private SharedPreference saveState;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		setTheme(android.R.style.Theme_Dialog);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | 
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
		setContentView(R.layout.pushdisplaylayout);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvMessage=(TextView)findViewById(R.id.tvMessage);
		tvMessageSub=(TextView)findViewById(R.id.tvMessageSub);
		btnYes = (Button)findViewById(R.id.btnYes);
		btnNo= (Button)findViewById(R.id.btnNo);
		//		CustomFonts.setFontOfTextView(NotificationPushDisplayDialogActivity.this,tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(NotificationPushDisplayDialogActivity.this,tvMessage, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(NotificationPushDisplayDialogActivity.this,tvMessageSub, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(NotificationPushDisplayDialogActivity.this,btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(NotificationPushDisplayDialogActivity.this,btnNo, "fonts/Roboto-Bold_1.ttf");
		btnYes.setOnClickListener(this);
		btnYes.setText("Send");
		btnNo.setOnClickListener(this);
		updateView();
//		MainBaseActivity.isAlreadyDisplaying=true;
	}

	/*@Override
	public void onStart() 
	{
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.6f;
		window.setAttributes(params);
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}*/

	private void updateView() 
	{
		if (!TextUtils.isEmpty(GlobalCommonValues.pushNotificationString) && GlobalConfig_Methods.isJsonString(GlobalCommonValues.pushNotificationString)) 
		{
			gson=new Gson();
			NotificationPushReponseBean get_Response = gson.fromJson(GlobalCommonValues.pushNotificationString,NotificationPushReponseBean.class);
			tvMessage.setText(get_Response.getMessage());
		}
		tvTitle.setVisibility(View.GONE);
		tvTitle.setText("");
		tvMessageSub.setText("");
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			isNotificationPushDisplayDialogActivity = true;
			Intent intent =new Intent(NotificationPushDisplayDialogActivity.this,HomeScreenActivity.class);
			//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			//					| Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
			//					Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_EXCLUDE_STOPPED_PACKAGES | Intent.FLAG_ACTIVITY_CLEAR_TASK );
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
			intent.putExtra("type","notification");
			startActivity(intent);
			NotificationPushDisplayDialogActivity.this.finish();
			//			MainBaseActivity.isAppExit=false;
			GCMIntentService.isNotificationPushDisplayDialogActivity=true;
			GCMIntentService.isMessagePushDisplayDialogActivity=false;
			MainBaseActivity.isFromMain=false;
		}
		else if(v.getId()==R.id.btnNo)
		{
			MainBaseActivity.isFromMain=true;
			GCMIntentService.isNotificationPushDisplayDialogActivity=false;
			GCMIntentService.isMessagePushDisplayDialogActivity=false;
			NotificationPushDisplayDialogActivity.this.finish();
			GlobalCommonValues.pushNotificationString=null;
			//			MainBaseActivity.isAppExit=true;
		}
	}
}
