package com.tnc.dialog;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class PhoneNumbersDialog extends DialogFragment{

	private Context mActivity;
	private LinearLayout llContainer;
	private TextView tvContactNumber;
	public ArrayList<String> listNumbers=new ArrayList<String>();
	private View contactNumberViews;
	private TextView tvTitle;
	private SharedPreference saveState;

	public PhoneNumbersDialog newInstance(Context mActivity,ArrayList<String> listNumbers)
	{
		this.mActivity = mActivity;
		this.listNumbers=listNumbers;
		PhoneNumbersDialog frag = new PhoneNumbersDialog();
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

		saveState = new SharedPreference();

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
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:"+phoneNumber));
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
					//update boolean value in preference as number is dialled from the app
					saveState.setIS_NUMBER_DIALLED(getActivity(), true);
					((HomeScreenActivity)mActivity).startActivity(intent);
				}
			});
			llContainer.addView(contactNumberViews);
		}
		return view;
	}
}
