package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.content.Context;
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

public class RecoveryConfirmationAlertDialog extends DialogFragment implements OnClickListener 
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private INotifyGalleryDialog iNotifyRecovery;

	public RecoveryConfirmationAlertDialog newInstance(String title, Context mAct,String message,
			String messageSub,INotifyGalleryDialog iNotifyRecovery)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		RecoveryConfirmationAlertDialog frag = new RecoveryConfirmationAlertDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.iNotifyRecovery=iNotifyRecovery;
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
		View view = inflater.inflate(R.layout.backup_confirmation, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo= (Button) view.findViewById(R.id.btnNo);
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
//		btnYes.setText("Yes");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);	
		//		if(tvTitle.getText().toString().equals(""))
		//			tvTitle.setVisibility(View.GONE);
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

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			/*BackupListFragment backupListFragment=new BackupListFragment();
			if(MainBaseActivity.verifyRegistrationFragment!=null)
			{
				MainBaseActivity.verifyRegistrationFragment.dismiss();
				MainBaseActivity.verifyRegistrationFragment=null;
			}
			if(mAct instanceof MainBaseActivity)
			{
				backupListFragment.newInstance(((MainBaseActivity)mAct));
				((MainBaseActivity)mAct).setFragment(backupListFragment);
			}
			else if(mAct instanceof HomeScreenActivity)
			{
				backupListFragment.newInstance(((HomeScreenActivity)mAct));
				((HomeScreenActivity)mAct).setFragment(backupListFragment);
			}*/
			dismiss();
			if(iNotifyRecovery!=null)
			{
				iNotifyRecovery.yes();
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
		}
	}
}
