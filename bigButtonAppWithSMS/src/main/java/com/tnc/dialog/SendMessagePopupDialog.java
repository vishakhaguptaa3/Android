package com.tnc.dialog;

import java.io.File;

import com.google.gson.Gson;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendMessagePopupDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle;
	private EditText etTo,etMessage;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private File fileDatabase;
	private Gson gson;
	private String messageText="";
	private AlertCallAction alertBack;

	public SendMessagePopupDialog newInstance(String title, Context mAct,String message,
			String messageSub,AlertCallAction alertBack)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		SendMessagePopupDialog frag = new SendMessagePopupDialog();
		Bundle args = new Bundle();
		this.alertBack=alertBack;
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
		View view = inflater.inflate(R.layout.smsdialogpopup, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		etTo=(EditText) view.findViewById(R.id.etTo);
		etTo.setEnabled(false);
		etMessage=(EditText) view.findViewById(R.id.etMessage);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo= (Button) view.findViewById(R.id.btnNo);
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),etTo, "fonts/Roboto-Light_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),etMessage, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Regular_1.ttf");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		messageText="I am using the " + getResources().getString(R.string.app_name) + " App and I need your image for my chat button.  Can you please respond with a close-up image of you? Thanks.";
		updateView();
		return view;
	}
	private void updateView() 
	{
		tvTitle.setText("Send SMS");
		//Phase-4
		String prefix="",countryCode="";

		if(MainBaseActivity.selectedPrefixCodeForTileDetails!=null && !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(""))
			prefix=MainBaseActivity.selectedPrefixCodeForTileDetails;
		if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
			countryCode=MainBaseActivity.selectedCountryCodeForTileDetails;
		etTo.setText(prefix+countryCode+MainBaseActivity.contactNumberForTile);
		/*if(MainBaseActivity.selectedPrefixCodeForTileDetails!=null && !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(""))
		{
			etTo.setText(MainBaseActivity.selectedPrefixCodeForTileDetails + MainBaseActivity.contactNumberForTile);
		}
		else{
			etTo.setText(MainBaseActivity.contactNumberForTile);
		}*/
		etMessage.setText(messageText);
	}

	//---sends a SMS message to another device---
	private void sendSMS(String phoneNumber, String message)
	{      

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);      
		BaseFragment.dialogRegistration=null;
		BaseFragment.dialogRegistration=new RegistrationDetailDialog();
		String messageDisplay="Your image request has been sent via SMS text message. Please look for the response,store the image in your Gallery and transfer it to the contact button";
		BaseFragment.dialogRegistration.setCancelable(false);
		MainBaseActivity.isImageRequested=true;
		if(mAct instanceof MainBaseActivity)
		{
			BaseFragment.dialogRegistration.newInstance("", ((MainBaseActivity)mAct),messageDisplay,"",alertBack,false,null);
			BaseFragment.dialogRegistration.show(((MainBaseActivity)mAct).getSupportFragmentManager(), "test");
		}
		else if(mAct instanceof HomeScreenActivity)
		{
			BaseFragment.dialogRegistration.newInstance("", ((HomeScreenActivity)mAct),messageDisplay,"",alertBack,false,null);
			BaseFragment.dialogRegistration.show(((HomeScreenActivity)mAct).getSupportFragmentManager(), "test");
		}
	} 

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			dismiss();
			sendSMS(MainBaseActivity.contactNumberForTile,messageText); //phase-4
			MainBaseActivity.isSmsSent=true;
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
		}
	}
}


