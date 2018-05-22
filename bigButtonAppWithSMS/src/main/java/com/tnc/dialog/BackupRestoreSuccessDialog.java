package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.UploadBackupServer;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class BackupRestoreSuccessDialog extends DialogFragment
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	private Context mAct;
	private String title="",message="",messageSub="";
	private SharedPreference saveState;

	public BackupRestoreSuccessDialog newInstance(String title, Context mAct,String message,
			String messageSub)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		BackupRestoreSuccessDialog frag = new BackupRestoreSuccessDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
	}

	@Override
	public void onStart() 
	{
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.6f;
		window.setAttributes(params);
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.backup_restore_success_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnOk = (Button) view.findViewById(R.id.btnOk);
		saveState=new SharedPreference();
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnOk, "fonts/Roboto-Bold_1.ttf");
		updateView();
		btnOk.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				dismiss();	
				if(!message.equalsIgnoreCase("An image has been sent successfully"))
				{
					if(MainBaseActivity.recoveryType.equals("archival_backup"))
					{
						UploadBackupServer uploadBackup=new UploadBackupServer(mAct, false, null);
						uploadBackup.uploadBackup();
					}
					saveState.setChanged(mAct, false);		
				}
				MainBaseActivity.recoveryType="";
//				UserRegistration.number = "";
				MainBaseActivity.mergeTiles=false;
				MainBaseActivity.isReturningUser=false;
				MainBaseActivity.isTileCreated=false;
				if(mAct instanceof MainBaseActivity)
				{
					((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
					((MainBaseActivity)mAct).finish();
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
					((HomeScreenActivity)mAct).finish();
				}
				if(!GlobalCommonValues.TelephoneNumberTobeDisplayed.trim().equals(""))
					GlobalCommonValues.TelephoneNumberTobeDisplayed="";
			}
		});
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);
		tvMessageSub.setText(messageSub);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(messageSub.trim().equals(""))
		{
			tvMessageSub.setVisibility(View.GONE);
		}
		else{
			tvMessageSub.setVisibility(View.VISIBLE);
		}
	}
}
