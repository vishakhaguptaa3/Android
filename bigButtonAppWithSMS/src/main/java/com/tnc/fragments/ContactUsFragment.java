/*package com.tnc.fragments;

import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ContactUsFragment extends BaseFragmentTabs{
	TextView tvTitle,tvContactUs,tvVersion,tvMessage,tvAttachment;
	Button btnAddAttachment,btnBack,btnSendMessage,btnDisableOverlay,btnCancel;
	FrameLayout flBackArrow;
	EditText etMessage;
	int requestCodeGallery=200;

	public ContactUsFragment newInstance(Context mActivity)
	{
		ContactUsFragment frag = new ContactUsFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contact_us_fragment,container, false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view){
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvContactUs=(TextView) view.findViewById(R.id.tvContactUs);
		tvVersion=(TextView) view.findViewById(R.id.tvVersion);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvAttachment=(TextView) view.findViewById(R.id.tvAttachment);
		btnAddAttachment=(Button) view.findViewById(R.id.btnAddAttachment);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		etMessage=(EditText) view.findViewById(R.id.etMessage);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnSendMessage=(Button) view.findViewById(R.id.btnSendMessage);
		btnDisableOverlay=(Button) view.findViewById(R.id.btnDisableOverlay);
		btnCancel=(Button) view.findViewById(R.id.btnCancel);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvContactUs, "fonts/Roboto-Bold_1.ttf");
		String version="";
		try {
			version=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0).versionName;
		} catch (Exception e) {
			e.getMessage();
		}
		tvVersion.setText("Version "+version);

		btnAddAttachment.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {

				Intent intentImages=new Intent();
				intentImages.setAction(android.content.Intent.ACTION_GET_CONTENT);  
				intentImages.setType("image/*");
				getActivity().setResult(getActivity().RESULT_OK, intentImages);
				intentImages.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivityForResult(intentImages,requestCodeGallery);

			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		});

		btnSendMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		});

		etMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()==0)
				{
					btnSendMessage.setEnabled(false);
					btnDisableOverlay.setVisibility(View.VISIBLE);
				}
				else if(s.length()>0)
				{
					btnSendMessage.setEnabled(true);
					btnDisableOverlay.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==requestCodeGallery)
		{
			if(resultCode==getActivity().RESULT_OK)
			{
				Toast.makeText(getActivity(), "Success",1000).show();
			}
		}
	}
}
*/