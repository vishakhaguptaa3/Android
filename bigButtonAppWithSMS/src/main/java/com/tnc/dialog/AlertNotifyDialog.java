package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;

import android.app.Activity;
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

public class AlertNotifyDialog extends DialogFragment implements OnClickListener
{
	TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	Activity mAct;
	String title="",message="",messageSub="";
	//	AlertCallAction alertBack;
	//	public boolean isFirstTime=false;

	public AlertNotifyDialog newInstance(String title, Activity mAct,String message,
			String messageSub)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		AlertNotifyDialog frag = new AlertNotifyDialog();
		Bundle args = new Bundle();
		//		args.putString("title", title);
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
		View view = inflater.inflate(R.layout.registrationdetaildialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnOk = (Button) view.findViewById(R.id.btnOk);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnOk, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setVisibility(View.GONE);
		tvMessageSub.setVisibility(View.GONE);
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
		}
	}
}