package com.tnc.fragments;

import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.EmergencyConfigureConfirmationDialog;
import com.tnc.dialog.EmergencyNumberSavedConfirmationDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * class used to configure/set emergency number on the home screen 
 *  @author a3logics
 */

public class EmergencyNumberConfigureFragment extends BaseFragmentTabs{

	private Button btnBack,btnSave;
	private Context mActivity;
	private EditText etEmergency;
	private SharedPreference saveState;
	private TextView tvTitle,tvStep,tvCreateContact,tvEmergencyNumber;
	private FrameLayout flBackArrow;
	private LinearLayout llDisableOverlay;
	private String strEmergency="";

	public EmergencyNumberConfigureFragment newInstance(Context mActivity)
	{
		EmergencyNumberConfigureFragment frag = new EmergencyNumberConfigureFragment();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.emergencynumberconfigure, container, false);
		idInitialization(view);
		return view;
	}

	// Method to initialize views/widgets

	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvCreateContact=(TextView) view.findViewById(R.id.tvCreateContact);
		etEmergency=(EditText)view.findViewById(R.id.etEmergency);
		btnSave=(Button)view.findViewById(R.id.btnNext);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		llDisableOverlay=(LinearLayout) view.findViewById(R.id.llDisableOverlay);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvCreateContact, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etEmergency, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnSave, "fonts/Roboto-Regular_1.ttf");
		tvStep.setText("CONFIGURE EMERGENCY");
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		tvCreateContact.setVisibility(View.GONE);
		strEmergency=saveState.getEmergency(mActivityTabs);
		etEmergency.setText(strEmergency);
		llDisableOverlay.setVisibility(View.VISIBLE);
		btnSave.setEnabled(false);
		tvStep.setTextColor(Color.parseColor("#1a649f"));
		btnSave.setText("SAVE");
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mActivityTabs instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
				}
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(saveState.getEmergency(mActivityTabs).trim().equals(""))
				{
					iNotifyEditEmergency.yes();

				}
				else if(!saveState.getEmergency(mActivityTabs).trim().equals(etEmergency.getText().toString().trim()))
				{
					EmergencyConfigureConfirmationDialog dialogConfigureEmergency=new EmergencyConfigureConfirmationDialog();
					dialogConfigureEmergency.setCancelable(false);
					dialogConfigureEmergency.newInstance("",mActivityTabs,"Please be aware that you are changing the emergency number. Are you sure you would like to continue?","",iNotifyEditEmergency);
					dialogConfigureEmergency.show(getChildFragmentManager(), "test");
				}
				else if(saveState.getEmergency(mActivityTabs).trim().equals(etEmergency.getText().toString().trim()))
				{
					ImageRequestDialog dialogError=new ImageRequestDialog();
					dialogError.newInstance("", mActivityTabs,"Same number already exists","",null);
					dialogError.setCancelable(false);
					dialogError.show(getChildFragmentManager(), "test");
				}
			}
		});
		etEmergency.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if(!s.toString().trim().equals(""))
				{
					llDisableOverlay.setVisibility(View.GONE);
					btnSave.setEnabled(true);
				}
				else if(s.toString().trim().equals(""))
				{
					llDisableOverlay.setVisibility(View.VISIBLE);
					btnSave.setEnabled(false);
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

	/**
	 * interface to handle update of emergency button
	 */
	INotifyGalleryDialog iNotifyEditEmergency=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			//In case of User selected yes to edit
			saveState.setEmergency(mActivityTabs, etEmergency.getText().toString().trim());	
			EmergencyNumberSavedConfirmationDialog dialogMessageSaved=new EmergencyNumberSavedConfirmationDialog();
			dialogMessageSaved.setCancelable(false);
			dialogMessageSaved.newInstance("",mActivityTabs,"Emergency number updated successfully",iNotifyDialog);
			dialogMessageSaved.show(getChildFragmentManager(), "test");
		}

		@Override
		public void no() {
			//In case of User selected no to edit
		}
	};

	INotifyGalleryDialog iNotifyDialog=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			//In case of emergency number saved successfully pop to backscreen
			if(mActivityTabs instanceof HomeScreenActivity)
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
		}

		@Override
		public void no() {

		}
	};

	public void onPause() {
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etEmergency);
		super.onPause();
	}
}
