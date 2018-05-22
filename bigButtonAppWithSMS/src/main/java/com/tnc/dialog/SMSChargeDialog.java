package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;

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
import android.widget.TextView;

public class SMSChargeDialog extends DialogFragment implements OnClickListener 
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private 	Context mAct;
	private String title="",message="",messageSub="";
	private AlertCallAction alertBack;
	//	public boolean isOkClicked=false;

	public SMSChargeDialog newInstance(String title, Context mAct,String message,
			String messageSub,AlertCallAction alertBack)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.alertBack=alertBack;
		SMSChargeDialog frag = new SMSChargeDialog();
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
		View view = inflater.inflate(R.layout.smschargedialog, container);
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
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);	
		if(tvTitle.getText().toString().equals(""))
			tvTitle.setVisibility(View.GONE);
		//		if(tvMessageSub.getText().toString().equals(""))
		//			tvMessageSub.setVisibility(View.GONE);
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
		/*tvMessage.setText(Html.fromHtml("A SMS charge would be\ndeducted as per the<br>"+
				"carrier plan"));
		tvMessageSub.setText("Would you like to Continue?");*/
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			//			isOkClicked=true;
			alertBack.isAlert(true);
			if(message.contains("messaging service"))
			{
				SmsManager smsManager = SmsManager.getDefault();
				String smsMessage="I am using the Chatstasy App and I need your image for my contact button.Can you please respond with a close-up image of you?Thanks.";
				smsManager.sendTextMessage(MainBaseActivity.contactNumberForTile, null, smsMessage, null, null);
				BaseFragment.dialogRegistration=new RegistrationDetailDialog();
				String message="Your image request has been sent via SMS text message. Please look for the response,store the image in your Gallery and transfer it to the contact button";
				BaseFragment.dialogRegistration.newInstance("", mAct,message,"",alertBack,false,null);
				BaseFragment.dialogRegistration.setCancelable(false);
				BaseFragment.dialogRegistration.show(getFragmentManager(), "test");
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			alertBack.isAlert(false);
		}
	}
}
