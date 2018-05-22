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

public class SelectCountryDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvCamera,tvGallery;
	private Context mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyAction iObject;

	public SelectCountryDialog newInstance(String title, Context mActivity,INotifyAction iObject)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObject=iObject;
		SelectCountryDialog frag = new SelectCountryDialog();
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
		tvCamera.setText("US/Canada");
		tvGallery.setText("India");
		btnCancel.setText("Other Countries");
//		tvCamera.setText("Register using US Based Number");
//		tvGallery.setText("Register using India Based Number");
		tvCamera.setOnClickListener(this);
		tvGallery.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tvCamera){
			dismiss();
			iObject.setAction("USA/Canada");
		}
		else if(v.getId()==R.id.tvGallery){
			dismiss();
			iObject.setAction("India");
		}else if(v.getId()==R.id.btnCancel){
			dismiss();
				iObject.setAction("Other Countries");
		}
	}
}
