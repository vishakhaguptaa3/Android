package com.tnc.dialog;

import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SendSMSFullScreenDialog_ContactDetails extends BaseFragmentTabs implements
OnClickListener {

	private Context mAct;
	private EditText etTo, etMessage;
	private Button btnSendMessage, btnCancelMessage;
	private String message, phoneNumber;
	private SharedPreference saveState;

	public SendSMSFullScreenDialog_ContactDetails newInstance(Context mAct,
			String message, String phoneNumber) {
		this.mAct = mAct;
		this.message = message;
		this.phoneNumber = phoneNumber;
		SendSMSFullScreenDialog_ContactDetails frag = new SendSMSFullScreenDialog_ContactDetails();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.send_sms_popup_window, container,
				false);
		// getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		saveState = new SharedPreference();
		etTo = (EditText) view.findViewById(R.id.etTo);
		etMessage = (EditText) view.findViewById(R.id.etMessage);
		btnSendMessage = (Button) view.findViewById(R.id.btnSendMessage);
		btnCancelMessage = (Button) view.findViewById(R.id.btnCancelMessage);
		etTo.setText(phoneNumber);
		etMessage.setText(message);
		etTo.setSelection(phoneNumber.length());
		etMessage.setSelection(etMessage.getText().toString().length());
		btnSendMessage.setOnClickListener(this);
		btnCancelMessage.setOnClickListener(this);
		return view;
	}

	private void send_SMS() {

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(etTo.getText().toString().trim(), null, etMessage.getText()
					.toString().trim(), null, null);
			if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSendMessage:
			send_SMS();
			break;

		case R.id.btnCancelMessage:
			if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
			break;
		default:
			break;
		}

	}
}
