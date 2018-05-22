package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
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
import android.widget.TextView;

public class PrivacyTermsOfDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvCamera,tvGallery;
	private Context mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyAction iObject;

	public PrivacyTermsOfDialog newInstance(String title, Context mActivity,INotifyAction iObject)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObject=iObject;
		PrivacyTermsOfDialog frag = new PrivacyTermsOfDialog();
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
		View view = inflater.inflate(R.layout.selectcountrypopup, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvCamera=(TextView)view.findViewById(R.id.tvCamera);
		tvGallery=(TextView)view.findViewById(R.id.tvGallery);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setText("Select Country");
		if(!title.trim().equals("")){
			tvTitle.setText(title);
		}
		/*tvCamera.setText("US");
		tvGallery.setText("Canada");
		btnCancel.setText("Other");*/
		tvCamera.setText("Privacy Policy");
		tvGallery.setText("Terms Of Use");
		tvCamera.setOnClickListener(this);
		tvGallery.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tvCamera){
			dismiss();
			iObject.setAction("privacy_policy");
		}
		else if(v.getId()==R.id.tvGallery){
			dismiss();
			iObject.setAction("terms_of_use");
		}else if(v.getId()==R.id.btnCancel){
			dismiss();
			//	iObject.setAction("Other");
		}
	}
}
