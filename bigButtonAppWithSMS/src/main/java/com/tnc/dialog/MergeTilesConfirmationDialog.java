package com.tnc.dialog;
/*package com.bigbutton.dialog;

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

import com.bigbutton.R;
import com.bigbutton.common.CustomFonts;
import com.bigbutton.interfaces.INotifyGalleryDialog;

public class MergeTilesConfirmationDialog extends DialogFragment implements OnClickListener {

	TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnMerge,btnReplace,btnCancelMerge;
	Context mAct;
	String title="",message="",messageSub="";
	INotifyGalleryDialog iNotifyMergeReplaceTiles;

	public MergeTilesConfirmationDialog newInstance(String title, Context mAct,String message,
			String messageSub,INotifyGalleryDialog iNotifyMergeReplaceTiles)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		MergeTilesConfirmationDialog frag = new MergeTilesConfirmationDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.iNotifyMergeReplaceTiles=iNotifyMergeReplaceTiles;
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
		View view = inflater.inflate(R.layout.merge_tiles_confirmation, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnMerge = (Button) view.findViewById(R.id.btnMerge);
		btnReplace= (Button) view.findViewById(R.id.btnReplace);
		btnCancelMerge=(Button) view.findViewById(R.id.btnCancelMerge);
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnMerge, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnReplace, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnCancelMerge, "fonts/Roboto-Bold_1.ttf");
		btnMerge.setOnClickListener(this);
		btnReplace.setOnClickListener(this);
		btnCancelMerge.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);	
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
		if(v.getId()==R.id.btnMerge)
		{
			dismiss();
			if(iNotifyMergeReplaceTiles!=null)//In case of Merging Tiles
				iNotifyMergeReplaceTiles.yes();
		}
		else if(v.getId()==R.id.btnReplace)
		{
			dismiss();
			if(iNotifyMergeReplaceTiles!=null)//In case of Replacing Old Tiles
				iNotifyMergeReplaceTiles.no();
		}
		else if(v.getId()==R.id.btnCancelMerge)
		{
			dismiss();
		}
	}

}
*/