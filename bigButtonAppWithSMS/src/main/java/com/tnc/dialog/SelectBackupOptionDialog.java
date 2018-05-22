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

public class SelectBackupOptionDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvCamera,tvGallery;
	private Context mActivity;
	private String title="";
	private 	Button btnCancel;
	private INotifyGalleryDialog iObject,iNotifyCloudBackup;

	public SelectBackupOptionDialog newInstance(String title, Context mActivity,
			INotifyGalleryDialog iObject,INotifyGalleryDialog iNotifyCloudBackup)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObject=iObject;
		this.iNotifyCloudBackup=iNotifyCloudBackup;
		SelectBackupOptionDialog frag = new SelectBackupOptionDialog();
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
		View view = inflater.inflate(R.layout.select_backup_option_popup, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvCamera=(TextView)view.findViewById(R.id.tvCamera);
		tvGallery=(TextView)view.findViewById(R.id.tvGallery);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvCamera, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvGallery, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setText("Recover Cloud Backup");
		tvCamera.setText("Recover Current Backup");
		tvGallery.setText("Recover Archival Backup");
		tvCamera.setOnClickListener(this);
		tvGallery.setOnClickListener(this);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				dismiss();	
				if(iNotifyCloudBackup!=null)
					iNotifyCloudBackup.yes();
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
