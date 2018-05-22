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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectUserNumberDialog extends DialogFragment{

	private Context mActivity;
	private LinearLayout llContainer;
	private TextView tvContactNumber,tvTitle;
	public ArrayList<String> listNumbers=new ArrayList<String>();
	private View contactNumberViews;
	private Button btnCancel;
	private INotifyAction iNotifyselectedNumber;

	public SelectUserNumberDialog newInstance(Context mActivity,ArrayList<String> listNumbers,
			INotifyAction iNotifyselectedNumber)
	{
		this.mActivity = mActivity;
		this.listNumbers=listNumbers;
		this.iNotifyselectedNumber=iNotifyselectedNumber;
		SelectUserNumberDialog frag = new SelectUserNumberDialog();
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
		View view = inflater.inflate(R.layout.select_user_number_popup, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		llContainer=(LinearLayout) view.findViewById(R.id.llContainer);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		btnCancel=(Button) view.findViewById(R.id.btnCancel);
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
					if(iNotifyselectedNumber!=null)
						iNotifyselectedNumber.setAction(phoneNumber);
				}
			});
			llContainer.addView(contactNumberViews);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
		return view;
	}
}
