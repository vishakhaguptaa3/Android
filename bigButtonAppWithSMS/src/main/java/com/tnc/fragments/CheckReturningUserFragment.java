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
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.CheckReturningUserBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.GetContactService;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CheckReturningUserResponse;
import com.tnc.webresponse.NotifyFriendResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

/**
 * class to check returning user at the time of new registration 
 *  @author a3logics
 */

public class CheckReturningUserFragment extends BaseFragmentTabs implements
OnClickListener {

	private FrameLayout flBackArrow, flInformationButton;
	private TextView tvTitle, tvInformation, tvInformationSub, tvCountryCode,tvTelephoneNumber,
	tvTelephoneNumberSub, tvRecover;// tvRecover,tvSamePhone,tvSubMessage
	private EditText etBackupKey, etCountryCode,etTelephoneNumber;
	private Button btnBack, btnSubmit, btnHome;
	private Context mContext;
	private TransparentProgressDialog progress;
	private Gson gson;
	private INotifyAction mReturningUser;

	public CheckReturningUserFragment newInstance(Context mContext,INotifyAction mReturningUser) {
		CheckReturningUserFragment frag = new CheckReturningUserFragment();
		this.mContext = mContext;
		this.mReturningUser = mReturningUser;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.check_returning_user, container,
				false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view) {
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivityTabs,
				R.drawable.customspinner);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvRecover = (TextView) view.findViewById(R.id.tvRecover);
		tvInformation = (TextView) view.findViewById(R.id.tvInformation);
		tvInformationSub = (TextView) view.findViewById(R.id.tvInformationSub);
		tvCountryCode = (TextView) view
				.findViewById(R.id.tvCountryCode);
		tvTelephoneNumber = (TextView) view
				.findViewById(R.id.tvTelephoneNumber);
		tvTelephoneNumberSub = (TextView) view
				.findViewById(R.id.tvTelephoneNumberSub);
		etBackupKey = (EditText) view.findViewById(R.id.etBackupKey);
		etCountryCode = (EditText) view
				.findViewById(R.id.etCountryCode);
		etTelephoneNumber = (EditText) view
				.findViewById(R.id.etTelephoneNumber);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
		flInformationButton = (FrameLayout) view
				.findViewById(R.id.flInformationButton);
		btnHome = (Button) view.findViewById(R.id.btnHome);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		flBackArrow.setVisibility(View.GONE);
		tvRecover.setText("Recover Archival Backup");
		tvInformation.setText("Please provide backup key");
		tvInformationSub.setText("Backup Key of your Archival Backup");
		tvCountryCode.setText("Country Code");
		tvTelephoneNumber.setText("Telephone Number");
		tvTelephoneNumberSub.setText("Telephone Number of Old Device");

		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(), tvTitle,
//				"fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvInformation,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvInformationSub,
				"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvCountryCode,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvTelephoneNumber,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvTelephoneNumberSub,
				"fonts/Roboto-Light_1.ttf");

		CustomFonts.setFontOfButton(getActivity(), btnSubmit,
				"fonts/Roboto-Regular_1.ttf");

		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnHome.setOnClickListener(this);
	}

	/**
	 * check internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			String strPhone = etTelephoneNumber.getText().toString();
			String contactNumber = "";
			if (strPhone.contains("(")) {
				strPhone = strPhone.replace("(", "");
			}
			if (strPhone.contains(")")) {
				strPhone = strPhone.replace(")", "");
			}
			if (strPhone.contains("-")) {
				strPhone = strPhone.replace("-", "");
			}
			if (strPhone.contains(" ")) {
				strPhone = strPhone.replaceAll(" ", "");
			}
			contactNumber = strPhone;
			String mCountryCode = "";
			mCountryCode = etCountryCode.getText().toString();

			if(mCountryCode == null || mCountryCode.trim().equals("")){
				mCountryCode = saveState.getCountryCode(mContext) ;
			}

			GlobalCommonValues.TelephoneNumberRestoreTobeDisplayed = strPhone;
			CheckReturningUserBean cloudRecoverSamePhone = new CheckReturningUserBean(etBackupKey.getText().toString()
					.trim().toUpperCase(),mCountryCode,contactNumber);
			checkReturningUser(cloudRecoverSamePhone);// contactNumber.trim()
			//			}
		} else{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);

		}
	}

	/**
	 * request to the server
	 * 
	 * @param cloudRecoverSamePhone
	 */
	private void checkReturningUser(CheckReturningUserBean cloudRecoverSamePhone) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(cloudRecoverSamePhone);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.check_returning_user, stringEntity,
					checkReturningUserResponseHandle,
					mActivityTabs.getString(R.string.private_key), "");
		} catch (Exception e) {
			e.getMessage();
		}
	}

	// async task to check returning user details
	AsyncHttpResponseHandler checkReturningUserResponseHandle = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("CheckReturningUserResponseHandle", "OnSuccess", response.toString());
					getResponseBackupSamePhone(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("CheckReturningUserResponseHandle", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/**
	 * handling response from the server for the request being sent to check the returning user
	 * 
	 * @param response
	 */
	private void getResponseBackupSamePhone(String response) {
		try {
			String response2="";
			if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
				response2=response.substring(response.indexOf("response_code")-2,response.length());
			}
			else{
				response2=response;
			}
			
			gson = new Gson();
			CheckReturningUserResponse get_Response = gson.fromJson(
					response2,CheckReturningUserResponse.class);
			
			// In case of success/failure in the response send the response to the
			// back screen
			
			if (get_Response.getResponse_code().equals(
					GlobalCommonValues.SUCCESS_CODE)) {
				
				//set premium user value
				if(get_Response.getData().is_premium_user.trim().equalsIgnoreCase("yes")){
					saveState.setISPREMIUMUSER(mActivityTabs, true);
				}else if(get_Response.getData().is_premium_user.trim().equalsIgnoreCase("no")){
					saveState.setISPREMIUMUSER(mActivityTabs, false);
				}

				//Toast.makeText(getActivity(), String.valueOf(saveState.getISPREMIUMUSER(getActivity())),1000).show();
			
				
				if(mReturningUser!=null){
					mReturningUser.setAction(response2);
				}
			}else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE)) {
				displayConfirmationPopup("retry");
			}else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_1)) {
				displayConfirmationPopup("retry");
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_6)) {
				displayConfirmationPopup("retry");
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onPause() {
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etBackupKey);
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etCountryCode);
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etTelephoneNumber);
		super.onPause();
	}

	/**
	 * Method to display confirmation popup
	 * @param message
	 */
	private void displayConfirmationPopup(String message){
		MessageDeleteConfirmation dialog  = new MessageDeleteConfirmation();
		if(message.equals("warning")){
			dialog.newInstance("",mActivityTabs,"Please be aware that if the user at "+ 
					etCountryCode.getText().toString() + " " +etTelephoneNumber.getText().toString() +" starts using the App, your account will be deactivated without notice. Do you like to proceed","", iConfirmDeactivate);
		}else if(message.equals("retry")){
			dialog.newInstance("",mActivityTabs,"Either the Backup Key or the Phone Number is incorrect. Do you like to try again?","", iConfirmDeactivate);
		}
		dialog.setCancelable(false);
		dialog.show(getChildFragmentManager(), "test");
	}

	/**
	 * Interface to send the request to the server for checking returning user case
	 */
	private INotifyGalleryDialog iConfirmDeactivate = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			checkInternetConnection();
		}

		@Override
		public void no() {
			if(mReturningUser!=null){
				mReturningUser.setAction("new_user");
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			/*if (mActivityTabs instanceof MainBaseActivity) {
				((MainBaseActivity) mActivityTabs).fragmentManager
				.popBackStack();
			} else if (mActivityTabs instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivityTabs).fragmentManager
				.popBackStack();
			}*/
		} else if (v.getId() == R.id.btnSubmit) {
			if (!etCountryCode.getText().toString().trim().equals("") 
					&& !etTelephoneNumber.getText().toString().trim().equals("")
					&& etTelephoneNumber.getText().toString().trim().length() <=15
					&& !etBackupKey.getText().toString().trim().equals("")) {
				displayConfirmationPopup("warning");
			} else {
				if (etBackupKey.getText().toString().trim().equals("")
						&& etCountryCode.getText().toString().trim().equals("") 
						&& etTelephoneNumber.getText().toString().trim()
						.equals("")) {
					ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
					dialogImageRequest.setCancelable(false);
					dialogImageRequest.newInstance("", mActivityTabs, Html
							.fromHtml(
									"Please provide backup key, country code "
											+ "& Telephone Number").toString(),
							"", null);
					dialogImageRequest.show(getChildFragmentManager(), "test");
				}
				else if (etBackupKey.getText().toString().trim().equals("")
						&& etCountryCode.getText().toString().trim()
						.equals("")) {
					ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
					dialogImageRequest.setCancelable(false);
					dialogImageRequest.newInstance("", mActivityTabs, Html
							.fromHtml(
									"Please provide backup key<br>"
											+ "& country code").toString(),
							"", null);
					dialogImageRequest.show(getChildFragmentManager(), "test");
				} 
				else if (etBackupKey.getText().toString().trim().equals("")
						&& etTelephoneNumber.getText().toString().trim()
						.equals("")) {
					ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
					dialogImageRequest.setCancelable(false);
					dialogImageRequest.newInstance("", mActivityTabs, Html
							.fromHtml(
									"Please provide backup key<br>"
											+ "& Telephone Number").toString(),
							"", null);
					dialogImageRequest.show(getChildFragmentManager(), "test");
				} else if (etBackupKey.getText().toString().trim().equals("")
						&& !etTelephoneNumber.getText().toString().trim()
						.equals("")) {
					ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
					dialogImageRequest.setCancelable(false);
					dialogImageRequest.newInstance("", mActivityTabs,
							"Please provide backup key", "", null);
					dialogImageRequest.show(getChildFragmentManager(),
							"test");

				} else if (!etBackupKey.getText().toString().trim().equals("")
						&& etTelephoneNumber.getText().toString().trim()
						.equals("")) {
					ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
					dialogImageRequest.setCancelable(false);
					dialogImageRequest.newInstance("", mActivityTabs,
							"Please enter Telephone Number", "", null);
					dialogImageRequest.show(getChildFragmentManager(), "test");

				} 
				else {
					if (etTelephoneNumber.getText().toString().trim().length() < 7) {
						ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
						dialogImageRequest.setCancelable(false);
						dialogImageRequest.newInstance("", mActivityTabs,
								"Contact No length cannot be less than 7 digits", "",
								null);
						dialogImageRequest.show(getChildFragmentManager(), "test");
					}
					else{
						displayConfirmationPopup("warning");
					}
				}
			}
		}

		else if (v.getId() == R.id.btnHome) {
			
			saveState.setRegistered(getActivity(), false);
			saveState.setPublicKey(getActivity(), "");
			saveState.setCountryCode(mActivityTabs,"");
			saveState.setCountryname(mActivityTabs,"");
			saveState.setBBID(mActivityTabs, "");
			saveState.setUserName(mActivityTabs, "");
			saveState.setUserMailID(mActivityTabs, "");
			saveState.setUserPhoneNumber(mActivityTabs,"");
			saveState.setCountryidd(mActivityTabs,"");
			saveState.setIsVerified(mActivityTabs,false);
			saveState.setIS_DEACTIVATED(mActivityTabs,false);
			
			CancelRegistrationRequestBean objCancelRegistration = null;
			if (mActivityTabs instanceof MainBaseActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mActivityTabs));
				((MainBaseActivity) mActivityTabs)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((MainBaseActivity) mActivityTabs).finish();
			} else if (mActivityTabs instanceof HomeScreenActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mActivityTabs));
				((HomeScreenActivity) mActivityTabs)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((HomeScreenActivity) mActivityTabs).finish();
			}
			
			saveState.setPassCode(mActivityTabs,"");
			
			/**
			 * CALL METHOD TO CLEAR DATABSE
			 */
			
			clearDataBaseValues();
		}
	}
	
	/**
	 * METHOD TO CLEAR DATABSE
	 */
	
	private void clearDataBaseValues(){
		DBQuery.deleteTable("BBContacts", "", null, getActivity());
		DBQuery.deleteTable("Messages", "", null, getActivity());
		DBQuery.deleteTable("Notifications", "", null,
				getActivity());
	}
}
