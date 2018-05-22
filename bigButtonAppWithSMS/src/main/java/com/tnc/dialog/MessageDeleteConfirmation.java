package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.content.Context;
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

public class MessageDeleteConfirmation extends DialogFragment implements OnClickListener
{

	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private INotifyGalleryDialog iNotifyGalleryDialog;

	public MessageDeleteConfirmation newInstance(String title, Context mAct,String message,
												 String messageSub,INotifyGalleryDialog iNotifyGalleryDialog)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		if(iNotifyGalleryDialog!=null)
			this.iNotifyGalleryDialog=iNotifyGalleryDialog;
		MessageDeleteConfirmation frag = new MessageDeleteConfirmation();
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
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.messagedeleteconfirmation, container);
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
		tvMessage.setText(message);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		}
		if(messageSub.trim().equals(""))
		{
			tvMessageSub.setVisibility(View.GONE);
		}
		else{
			tvMessageSub.setVisibility(View.VISIBLE);
		}
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			dismiss();
			if(message.equalsIgnoreCase("Are you sure you want to delete this conversation"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}  // In case of deleting Configured Messages
			else if(message.equalsIgnoreCase("Are you sure you want to delete this notification"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
			else if(message.equalsIgnoreCase("Do you want to delete this message?"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
			else if(message.equalsIgnoreCase("Are you sure you want to delete this chat button"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
			else if(message.equalsIgnoreCase("Are you sure you want to share these contacts?"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
			else if(message.equalsIgnoreCase("Are you sure you want to add these contacts?"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
			else if(message.equalsIgnoreCase("You are about to share one or more contact with another user. Please confirm"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}else if(message.contains("Please be aware that if the user at ")){
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}else if(message.contains("Either the Backup Key or the Phone Number is incorrect. Do you like to try again?")){
				//				if(iNotifyGalleryDialog!=null)
				//					iNotifyGalleryDialog.yes();	
			}else if(message.equalsIgnoreCase("Do you want to delete this category?"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}else if(message.equalsIgnoreCase(getResources().getString(R.string.txtCreateContactTilesConfirmation))){
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.yes();
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
			if(message.equalsIgnoreCase("Are you sure you want to delete this chat button"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.no();
			}
			else if(message.equalsIgnoreCase("You are sharing contact information with another user.  Please confirm"))
			{
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.no();
			}else if(message.contains("Please be aware that if the user at")){
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.no();
			}else if(message.contains("Either the Backup Key or the Phone Number is incorrect. Do you like to try again?")){
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.no();
			}else if(message.equalsIgnoreCase(getResources().getString(R.string.txtCreateContactTilesConfirmation))){
				if(iNotifyGalleryDialog!=null)
					iNotifyGalleryDialog.no();
			}
		}
	}
}
