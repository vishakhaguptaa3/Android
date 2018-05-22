package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.SplashScreen;
import com.tnc.activities.PrivacyPolicyActivity;
import com.tnc.common.CustomFonts;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.DialogInterface;
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

public class TermsOfUseDialog extends DialogFragment implements OnClickListener{

	private Context mAct;
	private SharedPreference saveState;
	private TextView tvTitle,tvThankYou,tvTermsOfUse,tvPrivacyPolicy;
	private Button btnAgeConfirmation,btnQuestion,btnCancel,btnOk;
	private boolean isChecked = false;
	private INotifyGalleryDialog iNotify;
	private boolean isOkClicked=false;

	public TermsOfUseDialog newInstance1(Context mAct,INotifyGalleryDialog iNotify) {
		this.mAct = mAct;
		this.iNotify=iNotify;
		TermsOfUseDialog frag = new TermsOfUseDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(iNotify!=null && !isOkClicked){
			isOkClicked=false;
			iNotify.yes();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.terms_of_use, container,false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		saveState = new SharedPreference();
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvThankYou = (TextView) view.findViewById(R.id.tvThankYou);
		tvTermsOfUse = (TextView) view.findViewById(R.id.tvTermsOfUse);
		tvPrivacyPolicy = (TextView) view.findViewById(R.id.tvPrivacyPolicy);
		btnAgeConfirmation = (Button) view.findViewById(R.id.btnAgeConfirmation);
		btnQuestion = (Button) view.findViewById(R.id.btnQuestion);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnOk = (Button) view.findViewById(R.id.btnOk);
		tvTermsOfUse.setOnClickListener(this);
		tvPrivacyPolicy.setOnClickListener(this);
		btnQuestion.setOnClickListener(this);
		btnAgeConfirmation.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnAgeConfirmation.setBackgroundResource(R.drawable.uncheck);
		btnOk.setBackgroundResource(R.drawable.button_bg_greyed_out);
		btnOk.setEnabled(false);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
//		if(saveState.isFirst(mAct))
//			saveState.setFirstTime(mAct, false);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tvTermsOfUse){
			Intent myIntent = new Intent(mAct,PrivacyPolicyActivity.class);
			myIntent.putExtra("title","Terms Of Use");
			startActivity(myIntent);
		}else if(v.getId()==R.id.tvPrivacyPolicy){
			Intent myIntent = new Intent(mAct,PrivacyPolicyActivity.class);
			myIntent.putExtra("title","Privacy Policy");
			startActivity(myIntent);
		}else if(v.getId()==R.id.btnQuestion){
			ImageRequestDialog dialog = new ImageRequestDialog();
			dialog.setCancelable(false);
			dialog.newInstance("Chatstasy",mAct,"You need to be 16 years or older to accept this terms of use agreement","",null);
			dialog.show((((SplashScreen)mAct).getSupportFragmentManager()), "test");

		}else if(v.getId()==R.id.btnOk){
			isOkClicked=true;
			dismiss();
			Intent myIntent=new Intent(((SplashScreen)mAct),MainBaseActivity.class);
			startActivity(myIntent);
			((SplashScreen)mAct).finish();

		}else if(v.getId()==R.id.btnCancel){
			ImageRequestDialog dialog = new ImageRequestDialog();
			dialog.setCancelable(false);
			dialog.newInstance("Chatstasy",mAct,"Please run the App when you are ready.","",null);
			dialog.show((((SplashScreen)mAct).getSupportFragmentManager()), "test");
		}
		else if(v.getId()==R.id.btnAgeConfirmation){
			if(isChecked){
				isChecked=false;
				btnAgeConfirmation.setBackgroundResource(R.drawable.uncheck);
				btnOk.setBackgroundResource(R.drawable.button_bg_greyed_out);
				btnOk.setEnabled(false);

			}else{
				isChecked=true;
				btnAgeConfirmation.setBackgroundResource(R.drawable.check);
				btnOk.setBackgroundResource(R.drawable.button_bg_skip);
				btnOk.setEnabled(true);
			}
		}
	}
}
