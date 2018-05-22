package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;
import com.tnc.fragments.TilePreviewFragment;
import com.tnc.homescreen.HomeScreenActivity;
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

public class ImageRequestPushDialog extends DialogFragment implements OnClickListener 
{
	public TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	//	Activity mAct;
	private String title="",message="",messageSub="";
	private AlertCallAction alertBack;
	private Context mContext;
	private INotifyGalleryDialog iNotifyGalleryDialog;

	//	public ImageRequestDialog newInstance(String title, Activity mAct,String message,
	//			String messageSub,AlertCallAction alertBack,INotifyGalleryDialog iNotifyGalleryDialog)
	//	{
	//		this.mAct = mAct;
	//		this.title=title;
	//		this.message=message;
	//		this.messageSub=messageSub;
	//		this.alertBack=alertBack;
	//		ImageRequestDialog frag = new ImageRequestDialog();
	//		Bundle args = new Bundle();
	//		frag.setArguments(args);
	//		this.iNotifyGalleryDialog=iNotifyGalleryDialog;
	//		return frag;
	//	}

	public ImageRequestDialog newInstance(String title, Context mContext,String message,
			String messageSub,AlertCallAction alertBack,INotifyGalleryDialog iNotifyGalleryDialog)
	{
		this.mContext = mContext;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.alertBack=alertBack;
		ImageRequestDialog frag = new ImageRequestDialog();
		this.iNotifyGalleryDialog=iNotifyGalleryDialog;
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
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.imagerequestpushdialog, container);
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
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnOk)
		{
			this.dismiss();
			iNotifyGalleryDialog.yes();
			TilePreviewFragment objTilePreview;
			if(mContext instanceof MainBaseActivity)		
			{
				objTilePreview=new TilePreviewFragment();
				objTilePreview.newInstance("",((MainBaseActivity)mContext));
				((MainBaseActivity)mContext).setFragment(objTilePreview);
			}
			else if(mContext instanceof HomeScreenActivity)	
			{
				objTilePreview=new TilePreviewFragment();
				objTilePreview.newInstance("",((HomeScreenActivity)mContext));
				((HomeScreenActivity)mContext).setFragment(objTilePreview);
			}
		}
	}
}
