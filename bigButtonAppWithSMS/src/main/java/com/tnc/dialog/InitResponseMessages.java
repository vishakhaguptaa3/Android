package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.interfaces.INotifyGalleryDialog;

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

public class InitResponseMessages extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvCamera,tvGallery;
	private Context mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyGalleryDialog iObject;

	public InitResponseMessages newInstance(String title, Context mActivity,INotifyGalleryDialog iObject)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObject=iObject;
		InitResponseMessages frag = new InitResponseMessages();
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
		View view = inflater.inflate(R.layout.selectimagepopup, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvCamera=(TextView)view.findViewById(R.id.tvCamera);
		tvGallery=(TextView)view.findViewById(R.id.tvGallery);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvCamera, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvGallery, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setText(title);
		tvCamera.setText("Initiation Messages");
		tvGallery.setText("Response Messages");
		tvCamera.setOnClickListener(this);
		tvGallery.setOnClickListener(this);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				dismiss();	
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tvCamera)
		{
			dismiss();
			iObject.yes();
		}
		else if(v.getId()==R.id.tvGallery)
		{
			dismiss();
			iObject.no();
		}
	}
}
