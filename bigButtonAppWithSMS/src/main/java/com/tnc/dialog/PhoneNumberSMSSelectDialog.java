package com.tnc.dialog;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyAction;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneNumberSMSSelectDialog extends DialogFragment{

	private Context mActivity;
	private LinearLayout llContainer;
	private TextView tvContactNumber;
	public ArrayList<String> listNumbers=new ArrayList<String>();
	private View contactNumberViews;
	private TextView tvTitle;
	private INotifyAction iActionNumberSelectForSMS;

	public PhoneNumberSMSSelectDialog newInstance(Context mActivity,ArrayList<String> listNumbers,
			INotifyAction iActionNumberSelectForSMS)
	{
		this.mActivity = mActivity;
		this.listNumbers=listNumbers;
		this.iActionNumberSelectForSMS=iActionNumberSelectForSMS;
		PhoneNumberSMSSelectDialog frag = new PhoneNumberSMSSelectDialog();
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
		window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.user_phonenumber_popup_layout, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		llContainer=(LinearLayout) view.findViewById(R.id.llContainer);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setText("Select number");	
		for(int i=0;i<listNumbers.size();i++)
		{
			contactNumberViews = ((HomeScreenActivity)mActivity).getLayoutInflater().inflate(R.layout.user_number_count, null);
			tvContactNumber=(TextView) contactNumberViews.findViewById(R.id.tvContactNumber);
			tvContactNumber.setId(i);
			tvContactNumber.setText(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(listNumbers.get(i)));
			tvContactNumber.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					String phoneNumber=listNumbers.get(v.getId());
					if(iActionNumberSelectForSMS!=null)
						iActionNumberSelectForSMS.setAction(phoneNumber);
				}
			});
			llContainer.addView(contactNumberViews);
		}
		return view;
	}
}
