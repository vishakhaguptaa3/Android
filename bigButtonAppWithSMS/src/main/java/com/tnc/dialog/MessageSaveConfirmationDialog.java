package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.app.Activity;
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

public class MessageSaveConfirmationDialog extends DialogFragment implements OnClickListener
{
	public TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	private Activity mAct;
	private String title="",message="";
	private INotifyGalleryDialog iNotifyGalleryDialog;
	private int type;

	public MessageSaveConfirmationDialog newInstance(String title, Activity mAct,String message,
			INotifyGalleryDialog iNotifyGalleryDialog,int type)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.type=type;
		MessageSaveConfirmationDialog frag = new MessageSaveConfirmationDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.iNotifyGalleryDialog=iNotifyGalleryDialog;
		return frag;
	}

	/*public MessageSaveConfirmationDialog newInstance(String title, Context mContext,String message,
			String messageSub,AlertCallAction alertBack)
	{
		this.mContext = mContext;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.alertBack=alertBack;
		MessageSaveConfirmationDialog frag = new MessageSaveConfirmationDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}*/

	/*@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
		if(iNotifyGalleryDialog!=null)
			iNotifyGalleryDialog.no();
	}*/

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
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.registrationdetaildialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnOk = (Button) view.findViewById(R.id.btnOk);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnOk, "fonts/Roboto-Bold_1.ttf");
		btnOk.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		tvMessageSub.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnOk)
		{
			dismiss();
			((HomeScreenActivity)mAct).fragmentManager.popBackStack();
			if(type==0)
			{
				iNotifyGalleryDialog.yes();
			}
			else if(type==1)
			{
				iNotifyGalleryDialog.no();
			}
		}
	}
}