package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;

import android.app.Activity;
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

public class TileDeleteSuccessDialog extends DialogFragment implements OnClickListener
{
	public TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	private Activity mAct;
	private String title="",message="",messageSub="";
	private Context mContext;
	boolean shouldNavigateToHomeScreen=false;

	public TileDeleteSuccessDialog newInstance(String title, Activity mAct,String message,
			String messageSub,boolean shouldNavigateToHomeScreen)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		TileDeleteSuccessDialog frag = new TileDeleteSuccessDialog();
		Bundle args = new Bundle();
		this.shouldNavigateToHomeScreen=shouldNavigateToHomeScreen;
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
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.registrationdetaildialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnOk = (Button) view.findViewById(R.id.btnOk);
		CustomFonts.setFontOfButton(getActivity(),btnOk, "fonts/Roboto-Bold_1.ttf");
		btnOk.setOnClickListener(this);
		updateView();
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

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnOk)
		{
			dismiss();

			if(shouldNavigateToHomeScreen)
			{
				if(mAct instanceof MainBaseActivity)
				{
					Intent myIntent=new Intent(mAct,HomeScreenActivity.class);
					startActivity(myIntent);
					((MainBaseActivity)mAct).finish();
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					Intent myIntent=new Intent(mAct,HomeScreenActivity.class);
					startActivity(myIntent);
					((HomeScreenActivity)mAct).finish();
				}
			}
		}
	}
}
