package com.tnc.fragments;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ParentConsentStatusBean;
import com.tnc.bean.ParentalConsentRegistrationBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ChildRegistrationResponse;
import com.tnc.webresponse.ParentConsentStatusResponse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to Enter Parental Consent Details in incase Of underage
 *  @author a3logics
 */

public class ParentalConsentFragment extends BaseFragmentTabs /*implements OnClickListener*/{

	private FrameLayout flBackArrow;
	private LinearLayout llCheckBoxContainer;
	private TextView tvParentalConsent,tvParentalInformation,
	tvParentGuardianLabel,tvParentalConsentStatus;
	private CheckBox chkBoxUnderAge;
	private EditText etEmailId;
	private Button btnBack,btnCancel,btnOk;
	private SharedPreference saveState;
	private Gson gson;
	private TransparentProgressDialog progress;
	boolean isResend = false;
	private TextView tvTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.parental_consent, container,
				false);
//		idInitialization(view);
		return view;
	}

//	// Method to initialize views/widgets
//	private void idInitialization(View view) {
//		saveState = new SharedPreference();
//		progress = new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
//
//		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
//		llCheckBoxContainer = (LinearLayout) view.findViewById(R.id.llCheckBoxContainer);
//
//		tvParentalConsent = (TextView) view.findViewById(R.id.tvParentalConsent);
//		tvParentalInformation = (TextView) view.findViewById(R.id.tvParentalInformation);
//		tvParentGuardianLabel = (TextView) view.findViewById(R.id.tvParentGuardianLabel);
//		tvParentalConsentStatus = (TextView) view.findViewById(R.id.tvParentalConsentStatus);
//
//		chkBoxUnderAge = (CheckBox) view.findViewById(R.id.chkBoxUnderAge);
//		etEmailId = (EditText) view.findViewById(R.id.etGuardianEmailId);
//
//		btnCancel = (Button) view.findViewById(R.id.btnCancel);
//		btnBack = (Button) view.findViewById(R.id.btnBack);
//		btnOk = (Button) view.findViewById(R.id.btnOk);
//
//		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//
//		btnBack.setOnClickListener(this);
//		btnCancel.setOnClickListener(this);
//		btnOk.setOnClickListener(this);
//
//		flBackArrow.setVisibility(View.VISIBLE);
//
//		llCheckBoxContainer.setVisibility(View.GONE);
//		tvParentalConsentStatus.setVisibility(View.GONE);
//
//		CustomFonts.setFontOfTextView(getActivity(), tvParentalConsent,
//				"fonts/Roboto-Bold_1.ttf");
//
////		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		updateUI();
//	}
//
//	// Method to update ui & set the values as per the response we got from the server
//	private void updateUI(){
//		if(!saveState.getParentEmailId(mActivityTabs).trim().equals("")){
//			etEmailId.setText(saveState.getParentEmailId(mActivityTabs));
//		}
//
//		if(saveState.getIS_PARENTAL_OK_CLICKED(mActivityTabs)){
//			if(NetworkConnection.isNetworkAvailable(getActivity())){
//				checkInternetConnectionParentConsentStatus();
//			}else{
//				if(saveState.getIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs)){
//					llCheckBoxContainer.setVisibility(View.VISIBLE);
//					tvParentalConsentStatus.setVisibility(View.GONE);
//					tvParentalConsentStatus.setText("Parental Consent Received");
//					btnCancel.setText("Cancel");
//					if(saveState.getIS_UNDERAGE(mActivityTabs))
//						chkBoxUnderAge.setChecked(true);
//				}else{
//					if(saveState.getParentConsentStatus(mActivityTabs).contains("agree")){
//						saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, true);
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//						tvParentalConsentStatus.setVisibility(View.VISIBLE);
//						if(saveState.getIS_UNDERAGE(mActivityTabs))
//							chkBoxUnderAge.setChecked(true);
//						else
//							chkBoxUnderAge.setChecked(false);
//						tvParentalConsentStatus.setText("Parental Consent Received");
//						btnCancel.setText("Cancel");
//					}else if(saveState.getParentConsentStatus(mActivityTabs).contains("pending")){
//						saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, false);
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//						tvParentalConsentStatus.setVisibility(View.GONE);
//						if(saveState.getIS_UNDERAGE(mActivityTabs))
//							chkBoxUnderAge.setChecked(true);
//						else
//							chkBoxUnderAge.setChecked(false);
//						tvParentalConsentStatus.setText("Parental Consent Not Received");
//						if(!etEmailId.getText().toString().trim().equals(""))
//						{
//							if(saveState.getIS_UNDERAGE(mActivityTabs))
//								chkBoxUnderAge.setChecked(true);
//							else
//								chkBoxUnderAge.setChecked(false);
//							btnCancel.setText("RESEND");
//						}
//					}else if(saveState.getParentConsentStatus(mActivityTabs).contains("disagree")){
//						saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, false);
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//						tvParentalConsentStatus.setVisibility(View.VISIBLE);
//						if(saveState.getIS_UNDERAGE(mActivityTabs))
//							chkBoxUnderAge.setChecked(true);
//						else
//							chkBoxUnderAge.setChecked(false);
//						tvParentalConsentStatus.setText("Parental Consent Declined");
//						if(!etEmailId.getText().toString().trim().equals(""))
//						{
//							if(saveState.getIS_UNDERAGE(mActivityTabs))
//								chkBoxUnderAge.setChecked(true);
//							else
//								chkBoxUnderAge.setChecked(false);
//							btnCancel.setText("RESEND");
//						}
//					}else{
//						if(!saveState.getIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs)){
//							llCheckBoxContainer.setVisibility(View.VISIBLE);
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//					}
//				}
//			}
//		}else{
//			llCheckBoxContainer.setVisibility(View.VISIBLE);
//			tvParentalConsentStatus.setVisibility(View.GONE);
//		}
//		updateTextColor();
//	}
//	/**
//	 * Method to go to the Registration Screen
//	 */
//	private void gotoRegistrationFragment(){
//		if(mActivityTabs instanceof MainBaseActivity){
//			((MainBaseActivity)mActivityTabs).setFragment(new UserRegistration());
//		}else if(mActivityTabs instanceof HomeScreenActivity){
//			((HomeScreenActivity)mActivityTabs).setFragment(new UserRegistration());
//		}
//		if(!chkBoxUnderAge.isChecked()){
//			saveState.setIS_UNDERAGE(getActivity(), false);
//		}
//		else if(chkBoxUnderAge.isChecked()){
//			saveState.setIS_UNDERAGE(getActivity(), true);
//		}
//		saveState.setParentEmailId(mActivityTabs, etEmailId.getText().toString());
//		saveState.setIS_PARENTAL_OK_CLICKED(mActivityTabs, true);
//	}
//	/**
//	 * Method to save the data in the preferences
//	 */
//	private void saveData(){
//		if(chkBoxUnderAge.isChecked() && saveState.getIS_UNDERAGE(getActivity())){
//			chkBoxUnderAge.setChecked(true);
//
//			//addded on 8-8-2016
//			// In case of under 13 years of age
//			if(etEmailId.getText().toString().trim().equals("")){
//				ImageRequestDialog dialog = new ImageRequestDialog();
//				dialog.setCancelable(false);
//				dialog.newInstance("",getActivity(),"Please enter valid email ID","",null);
//				dialog.show(getChildFragmentManager(), "test");
//			}else{
//				saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//				saveState.setIS_UNDERAGE(getActivity(), true);
//				// call web service here to send a parental consent	mail to the email provided
//				// then go to the main menu
//				isResend = false;
//				checkInternetConnection();
//			}
//		}
//
//		else if(tvParentalConsentStatus.getText().toString().contains("Parental Consent Declined")){
//			isResend = false;
//			checkInternetConnection();
//		}else{
//			if(chkBoxUnderAge.isChecked()){
//
//				// In case of under 13 years of age
//				if(etEmailId.getText().toString().trim().equals("")){
//					ImageRequestDialog dialog = new ImageRequestDialog();
//					dialog.setCancelable(false);
//					dialog.newInstance("",getActivity(),"Please enter valid email ID","",null);
//					dialog.show(getChildFragmentManager(), "test");
//				}else{
//					saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//					saveState.setIS_UNDERAGE(getActivity(), true);
//					// call web service here to send a parental consent	mail to the email provided
//					// then go to the main menu
//					isResend = false;
//					checkInternetConnection();
//				}
//			}else{
//				if(!tvParentalConsentStatus.getText().toString().contains("Parental Consent Not Received") &&
//						!tvParentalConsentStatus.getText().toString().contains("Parental Consent Declined") ||
//						(tvParentalConsentStatus.getVisibility()==View.GONE &&
//						tvParentalConsentStatus.getVisibility()==View.GONE)){
//					// in case of above 13 years of age
//					//					saveState.setParentEmailId(getActivity(),"");
//					saveState.setIS_UNDERAGE(getActivity(), false);
//					saveState.setIS_PARENTAL_OK_CLICKED(mActivityTabs, true);
//					//					etEmailId.setText("");
//					if(mActivityTabs instanceof MainBaseActivity){
//						((MainBaseActivity)mActivityTabs).setFragment(new UserRegistration());
//					}else if(mActivityTabs instanceof HomeScreenActivity) {
//						((HomeScreenActivity)mActivityTabs).setFragment(new UserRegistration());
//					}
//				}else{
//					if(mActivityTabs instanceof MainBaseActivity){
//						((MainBaseActivity)mActivityTabs).setFragment(new UserRegistration());
//					}else if(mActivityTabs instanceof HomeScreenActivity) {
//						((HomeScreenActivity)mActivityTabs).setFragment(new UserRegistration());
//					}
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * check internet availability
//	 */
//	public void checkInternetConnectionParentConsentStatus() {
//		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
//			ParentConsentStatusBean mObjParentalConsent = new ParentConsentStatusBean();
//			mObjParentalConsent.setDevice("android");
//			mObjParentalConsent.setDevice_id(saveState.getDeviceId(mActivityTabs));
//			parentalConsentStatus(mObjParentalConsent);
//		} else {
//			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
//		}
//	}
//
//	/**
//	 * check internet availability
//	 */
//	public void checkInternetConnection() {
//		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
//			ParentalConsentRegistrationBean mObjParentalConsent = new ParentalConsentRegistrationBean();
//			mObjParentalConsent.setEmail(etEmailId.getText().toString());
//			mObjParentalConsent.setDevice("android");
//			mObjParentalConsent.setDevice_id(saveState.getDeviceId(mActivityTabs));
//			childRegistration(mObjParentalConsent);
//
//		} else {
//			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
//		}
//	}
//
//	/**
//	 * request for Parent consent status bean
//	 *
//	 * @param :ParentConsentStatusBean
//	 */
//	private void parentalConsentStatus(ParentConsentStatusBean mObjParentalConsent) {
//		try {
//			gson = new Gson();
//			String stingGson = gson.toJson(mObjParentalConsent);
//			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
//			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
//			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
//					GlobalCommonValues.PARENT_CONSENT_STATUS, stringEntity,
//					parentConsentStatusResponseHandler,
//					mActivityTabs.getString(R.string.private_key), "");
//		} catch (Exception e) {
//			e.getMessage();
//		}
//	}
//
//	// async task to check parental consent status
//	AsyncHttpResponseHandler parentConsentStatusResponseHandler = new JsonHttpResponseHandler() {
//		@Override
//		public void onStart() {
//			// Initiated the request
//			if ((!progress.isShowing()))
//				progress.show();
//		}
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//			super.onSuccess(statusCode, headers, response);
//			// Successfully got a response
//			try {
//				if (response != null) {
//					Logs.writeLog("parentConsentStatus", "OnSuccess", response.toString());
//					getResponseParentConsentStatus(response.toString());
//				}
//			} catch (JsonSyntaxException jsone) {
//				jsone.toString();
//				if (progress!=null && progress.isShowing())
//					progress.dismiss();
//			}
//		}
//
//		@Override
//		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
//			// Response failed :(
//			if (response != null)
//				Logs.writeLog("parentConsentStatus", "OnFailure", response);
//			if (progress!=null && progress.isShowing())
//				progress.dismiss();
//		}
//
//		@Override
//		public void onFinish() {
//			// Completed the request (either success or failure)
//			if (progress.isShowing())
//				progress.dismiss();
//		}
//	};
//
//	/**
//	 * handle response for the request being made to check parent consent status
//	 *
//	 * @param response
//	 */
//	private void getResponseParentConsentStatus(String response) {
//		try {
//			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
//				gson = new Gson();
//				ParentConsentStatusResponse get_Response = gson.fromJson(response,ParentConsentStatusResponse.class);
//				if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE))
//				{
//					saveState.setParentConsentStatus(mActivityTabs,get_Response.getData().get(get_Response.getData().size()-1).is_consent);
//					if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("agree")){
//						saveState.setParentEmailId(mActivityTabs, get_Response.getData().get(get_Response.getData().size()-1).email);
//						saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, true);
//
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//						if(saveState.getIS_UNDERAGE(mActivityTabs)){
//							tvParentalConsentStatus.setVisibility(View.VISIBLE);
//
//							String mDate = GlobalConfig_Methods.getDateTimeLocal(get_Response.getData().get(get_Response.getData().size()-1).created_on).split(" ")[0];
//
//							tvParentalConsentStatus.setText("Parental Consent Received on "+GlobalConfig_Methods.changeDateFormatToddMMyyyy(mDate));
//							chkBoxUnderAge.setChecked(true);
//
//						}else{
//							tvParentalConsentStatus.setVisibility(View.GONE);
//							chkBoxUnderAge.setChecked(false);
//						}
//						btnCancel.setText("Cancel");
//
//						updateTextColor();
//						if(etEmailId.getText().toString().trim().equals("")){
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//						//						gotoRegistrationFeaturesFragment();
//					}else if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("pending")){
//						saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, false);
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//
//						etEmailId.setText(saveState.getParentEmailId(mActivityTabs));
//
//						if(saveState.getIS_UNDERAGE(mActivityTabs)){
//							tvParentalConsentStatus.setVisibility(View.VISIBLE);
//							tvParentalConsentStatus.setText("Parental Consent Not Received");
//						}else{
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//
//						if(!etEmailId.getText().toString().trim().equals(""))
//						{
//							if(saveState.getIS_UNDERAGE(mActivityTabs))
//								chkBoxUnderAge.setChecked(true);
//							else
//								chkBoxUnderAge.setChecked(false);
//							btnCancel.setText("RESEND");
//						}else{
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//						updateTextColor();
//					}else if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("disagree")){
//						llCheckBoxContainer.setVisibility(View.VISIBLE);
//
//						if(saveState.getIS_UNDERAGE(mActivityTabs)){
//							tvParentalConsentStatus.setVisibility(View.VISIBLE);
//
//							String mDate = GlobalConfig_Methods.getDateTimeLocal(get_Response.getData().get(get_Response.getData().size()-1).created_on).split(" ")[0];
//
//							tvParentalConsentStatus.setText("Parental Consent Declined on "+
//									GlobalConfig_Methods.changeDateFormatToddMMyyyy(mDate));
//						}else{
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//
//						if(!etEmailId.getText().toString().trim().equals(""))
//						{
//							if(saveState.getIS_UNDERAGE(mActivityTabs))
//								chkBoxUnderAge.setChecked(true);
//							else
//								chkBoxUnderAge.setChecked(false);
//							btnCancel.setText("RESEND");
//						}
//						updateTextColor();
//						if(etEmailId.getText().toString().trim().equals("")){
//							tvParentalConsentStatus.setVisibility(View.GONE);
//						}
//					}
//					updateTextColor();
//				}else if (get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE) ||
//						get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_1)){
//					llCheckBoxContainer.setVisibility(View.VISIBLE);
//					tvParentalConsentStatus.setVisibility(View.GONE);
//
//					if (progress!=null && progress.isShowing())
//						progress.dismiss();
//				}
//			} else {
//				Log.d("improper_response",response);
//				ShowDialog.alert(
//						mActivityTabs,
//						"",
//						getResources().getString(
//								R.string.improper_response_network));
//			}
//		}catch(Exception e){
//			e.getMessage();
//		}
//	}
//
//	/**
//	 * request for child registration
//	 *
//	 * @param :ParentalConsentRegistrationBean
//	 */
//	private void childRegistration(ParentalConsentRegistrationBean mObjParentalConsent) {
//		try {
//			gson = new Gson();
//			String stingGson = gson.toJson(mObjParentalConsent);
//			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
//			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
//			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
//					GlobalCommonValues.GET_CHILD_REGISTRATION, stringEntity,
//					childRegistrationResponseHandler,
//					mActivityTabs.getString(R.string.private_key), "");
//		} catch (Exception e) {
//			e.getMessage();
//		}
//	}
//
//	// async task for child registration
//	AsyncHttpResponseHandler childRegistrationResponseHandler = new JsonHttpResponseHandler() {
//		@Override
//		public void onStart() {
//			// Initiated the request
//			if ((!progress.isShowing()))
//				progress.show();
//		}
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//			super.onSuccess(statusCode, headers, response);
//			// Successfully got a response
//			try {
//				if (response != null) {
//					Logs.writeLog("GETCHILDRegistration", "OnSuccess", response.toString());
//					getResponseChildRegistration(response.toString());
//				}
//			} catch (JsonSyntaxException jsone) {
//				jsone.toString();
//			}
//		}
//
//		@Override
//		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
//			// Response failed :(
//			if (response != null)
//				Logs.writeLog("GETCHILDRegistration", "OnFailure", response);
//		}
//
//		@Override
//		public void onFinish() {
//			// Completed the request (either success or failure)
//			if (progress.isShowing())
//				progress.dismiss();
//		}
//	};
//
//	public void onStop() {
//		super.onStop();
//	}
//
//
//	/**
//	 * handle response for the request being made for the registration
//	 *
//	 * @param response
//	 */
//	private void getResponseChildRegistration(String response) {
//		try {
//			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
//				gson = new Gson();
//				ChildRegistrationResponse get_Response = gson.fromJson(response,ChildRegistrationResponse.class);
//				if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE))
//				{
//					if(!isResend){
//						ImageRequestDialog dialog = new ImageRequestDialog();
//						dialog.setCancelable(false);
//						dialog.newInstance("",mActivityTabs, "Parent consent email has been sent. Please ask your parent/guardian to look for this email","",null, mNotify);
//						dialog.show(getChildFragmentManager(), "test");
//					}
//					saveState.setIS_PARENTAL_OK_CLICKED(mActivityTabs, true);
//					saveState.setIS_PARENTAL_CONSENT_RECEIVED(mActivityTabs, false);
//
//				}
//			} else {
//				Log.d("improper_response",response);
//				ShowDialog.alert(
//						mActivityTabs,
//						"",
//						getResources().getString(
//								R.string.improper_response_network));
//			}
//		}catch(Exception e){
//			e.getMessage();
//		}
//	}
//
//	/**
//	 * interface to handle call to send to the home screen
//	 */
//	INotifyGalleryDialog mNotify = new INotifyGalleryDialog() {
//
//		@Override
//		public void yes() {
//		}
//
//		@Override
//		public void no() {
//			saveState.setParentEmailId(mActivityTabs, etEmailId.getText().toString());
//			getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
//			getActivity().finish();
//		}
//	};
//
//	/**
//	 * Method to go back to the screen
//	 */
//	private void goBack(){
//		if(mActivityTabs instanceof MainBaseActivity){
//			((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
//		}else if(mActivityTabs instanceof HomeScreenActivity){
//			((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
//		}
//	}
//
//	/**
//	 * Method to update the text color
//	 */
//	private void updateTextColor(){
//		if(tvParentalConsentStatus.getVisibility()==View.VISIBLE){
//			if(tvParentalConsentStatus.getText().toString().contains("Parental Consent Received")){
//				tvParentalConsentStatus.setTextColor(Color.parseColor("#559c5d"));
//			}
//			else if(tvParentalConsentStatus.getText().toString().contains("Parental Consent Not Received")){
//				tvParentalConsentStatus.setTextColor(Color.parseColor("#1a649f"));//#3692DB
//			}
//			else if(tvParentalConsentStatus.getText().toString().contains("Parental Consent Declined")){
//				tvParentalConsentStatus.setTextColor(Color.parseColor("#FF3A2D"));
//			}
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnBack:
//			goBack();
//			break;
//
//		case R.id.btnCancel:
//			if(btnCancel.getText().toString().contains("RESEND")){
//
//				if(chkBoxUnderAge.isChecked() ){
//
//					if(NetworkConnection.isNetworkAvailable(mActivityTabs)){
//						// In case of under 13 years of age
//						if(etEmailId.getText().toString().trim().equals("")){
//							ImageRequestDialog dialog = new ImageRequestDialog();
//							dialog.setCancelable(false);
//							dialog.newInstance("",getActivity(),"Please enter valid email ID","",null);
//							dialog.show(getChildFragmentManager(), "test");
//						}else{
//
//							ImageRequestDialog  dialog = new ImageRequestDialog();
//							dialog.setCancelable(false);
//							dialog.newInstance("",mActivityTabs, "Parent consent email has been sent. Please ask parent/guardian to check", "",null, mNotify);
//							dialog.show(getChildFragmentManager(), "test");
//
//							saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//							saveState.setIS_UNDERAGE(getActivity(), true);
//							// call web service here to send a parental consent	mail to the email provided
//							// then go to the main menu
//							isResend = true;
//							checkInternetConnection();
//						}
//					}else{
//						GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
//					}
//				}else{
//					ImageRequestDialog  dialog = new ImageRequestDialog();
//					dialog.setCancelable(false);
//					dialog.newInstance("",mActivityTabs, "Parent consent email has been sent. Please ask parent/guardian to check", "",null, mNotify);
//					dialog.show(getChildFragmentManager(), "test");
//
//					saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//					saveState.setIS_UNDERAGE(getActivity(), true);
//					// call web service here to send a parental consent	mail to the email provided
//					// then go to the main menu
//					isResend = true;
//					checkInternetConnection();
//				}
//			}else{
//				getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
//				getActivity().finish();
//			}
//			break;
//
//		case R.id.btnOk:
//			if(tvParentalConsentStatus.getText().toString().contains("Parental Consent Received")){
//				if(etEmailId.getText().toString().equals(saveState.getParentEmailId(mActivityTabs))){
//					gotoRegistrationFragment();
//				}else{
//					if(chkBoxUnderAge.isChecked()){
//
//						// In case of under 13 years of age
//						if(etEmailId.getText().toString().trim().equals("")){
//							ImageRequestDialog dialog = new ImageRequestDialog();
//							dialog.setCancelable(false);
//							dialog.newInstance("",getActivity(),"Please enter valid email ID","",null);
//							dialog.show(getChildFragmentManager(), "test");
//						}else{
//							saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//							saveState.setIS_UNDERAGE(getActivity(), true);
//							// call web service here to send a parental consent	mail to the email provided
//							// then go to the main menu
//							isResend = false;
//							checkInternetConnection();
//						}
//					}else{
//						gotoRegistrationFragment();
//					}
//				}
//			}
//			else if(tvParentalConsentStatus.getVisibility()==View.VISIBLE && tvParentalConsentStatus.getText().toString().contains("Parental Consent Not Received")){
//				if(chkBoxUnderAge.isChecked()){
//					mNotify.no();
//				}else if(!chkBoxUnderAge.isChecked()){
//					gotoRegistrationFragment();
//				}
//			}else if(tvParentalConsentStatus.getVisibility()==View.VISIBLE && tvParentalConsentStatus.getText().toString().contains("Parental Consent Declined")){
//				if(etEmailId.getText().toString().equals(saveState.getParentEmailId(mActivityTabs))){
//
//					if(chkBoxUnderAge.isChecked()){
//						getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
//						getActivity().finish();
//					}else{
//						gotoRegistrationFragment();
//					}
//
//				}else{
//					if(chkBoxUnderAge.isChecked()){
//
//						// In case of under 13 years of age
//						if(etEmailId.getText().toString().trim().equals("")){
//							ImageRequestDialog dialog = new ImageRequestDialog();
//							dialog.setCancelable(false);
//							dialog.newInstance("",getActivity(),"Please enter valid email ID","",null);
//							dialog.show(getChildFragmentManager(), "test");
//						}else{
//							saveState.setParentEmailId(getActivity(),etEmailId.getText().toString());
//							saveState.setIS_UNDERAGE(getActivity(), true);
//							// call web service here to send a parental consent	mail to the email provided
//							// then go to the main menu
//							isResend = false;
//							checkInternetConnection();
//						}
//					}else{
//						gotoRegistrationFragment();
//					}
//				}
//			}else{
//				saveData();
//			}
//			break;
//		}
//	}
}
