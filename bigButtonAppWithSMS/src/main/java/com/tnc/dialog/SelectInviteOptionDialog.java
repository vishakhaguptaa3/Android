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

public class SelectInviteOptionDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvSendEmail,tvSendText;
	private Context mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyGalleryDialog iObject;

	public SelectInviteOptionDialog newInstance(String title, Context mActivity,INotifyGalleryDialog iObject)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObject=iObject;
		SelectInviteOptionDialog frag = new SelectInviteOptionDialog();
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
		View view = inflater.inflate(R.layout.select_invite_option_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvSendEmail=(TextView)view.findViewById(R.id.tvSendEmail);
		tvSendText=(TextView)view.findViewById(R.id.tvSendText);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setText("Choose Option");
		tvSendEmail.setText("Send an Email");
		tvSendText.setText("Send a Text");
		tvSendEmail.setOnClickListener(this);
		tvSendText.setOnClickListener(this);
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
		if(v.getId()==R.id.tvSendEmail)
		{
			// In case of suser selected to send Email
			dismiss();
			iObject.yes();
		}
		else if(v.getId()==R.id.tvSendText)
		{
			// In case of suser selected to send SMS
			dismiss();
			iObject.no();
		}
	}
}
