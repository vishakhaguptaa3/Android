/*package com.tnc.fragments;

import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CloudBackupNewPhoneBean;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.NoBackupFoundDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CloudRecoverSamePhoneResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CloudRecoverNewPhoneFragment extends BaseFragmentTabs implements
OnClickListener {

	FrameLayout flBackArrow, flInformationButton;
	TextView tvTitle, tvInformation, tvInformationSub, tvCountryCode,tvTelephoneNumber,
	tvTelephoneNumberSub, tvRecover;// tvRecover,tvSamePhone,tvSubMessage
	EditText etBackupKey, etCountryCode,etTelephoneNumber;
	Button btnBack, btnSubmit, btnHome;
	Context mContext;
	TransparentProgressDialog progress;
	Gson gson;
	ListView lvCloudBackupMenu;
	INotifyGalleryDialog iNotifyCloudBackup;
	public boolean isFirstTime = true;

	public CloudRecoverNewPhoneFragment newInstance(Context mContext,
			ListView lvCloudBackupMenu, INotifyGalleryDialog iNotifyCloudBackup) {
		CloudRecoverNewPhoneFragment frag = new CloudRecoverNewPhoneFragment();
		this.mContext = mContext;
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.lvCloudBackupMenu = lvCloudBackupMenu;
		if (this.lvCloudBackupMenu != null) {
			this.lvCloudBackupMenu.setEnabled(false);
			this.lvCloudBackupMenu.setClickable(false);
		}
		this.iNotifyCloudBackup = iNotifyCloudBackup;
		return frag;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFirstTime) {
			SpannableString sp = new SpannableString(
					GlobalCommonValues.TelephoneNumberRestoreTobeDisplayed);
			etTelephoneNumber.setText(sp);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!GlobalCommonValues.TelephoneNumberRestoreTobeDisplayed.trim()
				.equals(""))
			GlobalCommonValues.TelephoneNumberRestoreTobeDisplayed = "";
		if (iNotifyCloudBackup != null)
			iNotifyCloudBackup.no();
		if (this.lvCloudBackupMenu != null) {
			this.lvCloudBackupMenu.setEnabled(true);
			this.lvCloudBackupMenu.setClickable(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cloudrecovernewphone, container,
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
		// tvSamePhone=(TextView) view.findViewById(R.id.tvSamePhone);
		// tvSubMessage=(TextView) view.findViewById(R.id.tvSubMessage);
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
		flBackArrow.setVisibility(View.VISIBLE);
		tvRecover.setText("Recover Archival Backup");
		
		 * tvRecover.setText("Option for Same or New phone");
		 * tvRecover.setTextColor
		 * (getActivity().getResources().getColor(R.color.textBlueColor));
		 * tvRecover.setTypeface(null,Typeface.BOLD);
		 
		// tvRecover.setText("Recover Cloud Backup on");
		// tvSamePhone.setText("New Phone");
		
		 * tvSubMessage.setText("Set up perspectives");
		 * tvSubMessage.setVisibility(View.GONE);
		 
		tvInformation.setText("Please provide backup key");
		tvInformationSub.setText("Backup Key of your Archival Backup");
		tvCountryCode.setText("Country Code");
		tvTelephoneNumber.setText("Telephone Number");
		tvTelephoneNumberSub.setText("Telephone Number of Old Device");
		//		etBackupKey.setHint("Enter BackupKey");
		//		etTelephoneNumber.setHint("(xxx) xxx-xxxx");

		CustomFonts.setFontOfTextView(getActivity(), tvTitle,
				"fonts/Helvetica-Bold.otf");
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
		btnBack.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		// Attach TextWatcher to EditText
		TextWatcher watcher = new GlobalConfig_Methods()
		.convertPhoneToUSFormat(etTelephoneNumber);
		etTelephoneNumber.addTextChangedListener(watcher);
	}

	*//**
	 * check internet connection
	 *//*
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
				mCountryCode =saveState.getCountryCode(mContext) ;
			}

			GlobalCommonValues.TelephoneNumberRestoreTobeDisplayed = strPhone;
			CloudBackupNewPhoneBean cloudRecoverSamePhone = new CloudBackupNewPhoneBean(
					"2", saveState.getDeviceId(mActivityTabs),
					mCountryCode+contactNumber, etBackupKey.getText().toString()
					.trim().toUpperCase());
			getBackupListRequest(cloudRecoverSamePhone);// contactNumber.trim()
			//			}
		} else{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);

		}
	}

	*//**
	 * request to the server
	 * 
	 * @param cloudRecoverSamePhone
	 *//*
	private void getBackupListRequest(
			CloudBackupNewPhoneBean cloudRecoverSamePhone) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(cloudRecoverSamePhone);
			StringEntity stringEntity;
			stringEntity = new StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.GET_ALL_BACKUP, stringEntity,
					cloudRecoverNewPhoneResponseHandle,
					mActivityTabs.getString(R.string.private_key), "");
		} catch (Exception e) {
			e.getMessage();
		}
	}

	AsyncHttpResponseHandler cloudRecoverNewPhoneResponseHandle = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(String response) {
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("CloudRecoverNewPhone", "OnSuccess", response);
					getResponseBackupSamePhone(response);
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(Throwable e, String response) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("CloudRecoverNewPhone", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	*//**
	 * handling response from the server for the requrest being sent
	 * 
	 * @param response
	 *//*
	private void getResponseBackupSamePhone(String response) {
		try {
			gson = new Gson();
			ImageRequestDialog dialogErrorResponse = new ImageRequestDialog();
			CloudRecoverSamePhoneResponse get_Response = gson.fromJson(
					response, CloudRecoverSamePhoneResponse.class);
			if (get_Response.getResponse_code().equals(
					GlobalCommonValues.SUCCESS_CODE)) {
				GlobalCommonValues.listBackups.clear();
				 saveState.setRegistered(mActivityTabs, true); 
				CloudRecoverBackupListingBean objListingBean;
				// int listSize=0;
				// if(get_Response.getData().size()>3)
				// listSize=3;
				// else
				// listSize=get_Response.getData().size();
				for (int i = 0; i < get_Response.getData().size(); i++) {
					objListingBean = new CloudRecoverBackupListingBean();
					objListingBean.setDatetime(get_Response.getData().get(i)
							.getDatetime());
					objListingBean.setUrl(get_Response.getData().get(i)
							.getUrl());
					GlobalCommonValues.listBackups.add(objListingBean);
				}

				BackupListFragment backupListFragment = new BackupListFragment();
				
				 * if(MainBaseActivity.verifyRegistrationFragment!=null) {
				 * MainBaseActivity.verifyRegistrationFragment.dismiss();
				 * MainBaseActivity.verifyRegistrationFragment=null; }
				 
				MainBaseActivity.recoveryType = "archival_backup";
				if (mActivityTabs instanceof MainBaseActivity) {
					backupListFragment
					.newInstance(((MainBaseActivity) mActivityTabs));
					((MainBaseActivity) mActivityTabs)
					.setFragment(backupListFragment);
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					backupListFragment
					.newInstance(((HomeScreenActivity) mActivityTabs));
					((HomeScreenActivity) mActivityTabs)
					.setFragment(backupListFragment);
				}
				MainBaseActivity.isBackButtonToDisplay=true;
				// if(get_Response.getData().size()>0)
				// {
				// saveState.setPublicKey(mActivityTabs,get_Response.getData().get(0).getPublic_key());
				// }
				
				 * KeyMatchBackupDialog dialogKeyMatch=new
				 * KeyMatchBackupDialog(); dialogKeyMatch.setCancelable(false);
				 * if(mActivityTabs instanceof MainBaseActivity) {
				 * dialogKeyMatch
				 * .newInstance("",((MainBaseActivity)mActivityTabs),
				 * Html.fromHtml
				 * ("The provided information has<br>matched successfully"
				 * ).toString(), "");
				 * dialogKeyMatch.show(((MainBaseActivity)mActivityTabs
				 * ).getSupportFragmentManager(), "test"); } else
				 * if(mActivityTabs instanceof HomeScreenActivity) {
				 * dialogKeyMatch
				 * .newInstance("",((HomeScreenActivity)mActivityTabs),
				 * Html.fromHtml
				 * ("The provided information has<br>matched successfully"
				 * ).toString(), "");
				 * dialogKeyMatch.show(((HomeScreenActivity)mActivityTabs
				 * ).getSupportFragmentManager(), "test"); }
				 
				isFirstTime = false;
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE)) {
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogErrorResponse.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogErrorResponse.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(
							((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_1)) {
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogErrorResponse.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogErrorResponse.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(
							((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_5)) {
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogErrorResponse.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogErrorResponse.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(
							((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_6)) {
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogErrorResponse.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogErrorResponse.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(
							((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_7)) {
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogErrorResponse.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogErrorResponse.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "", null);
					dialogErrorResponse.setCancelable(false);
					dialogErrorResponse.show(
							((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else if (get_Response.getResponse_code().equals(
					GlobalCommonValues.FAILURE_CODE_601)) {
				NoBackupFoundDialog dialogNoBackup = null;
				if (mActivityTabs instanceof MainBaseActivity) {
					dialogNoBackup = new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",
							((MainBaseActivity) mActivityTabs),
							get_Response.getMessage(), "");
					dialogNoBackup.show(((MainBaseActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				} else if (mActivityTabs instanceof HomeScreenActivity) {
					dialogNoBackup = new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.getMessage(), "");
					dialogNoBackup.show(((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
				}
			} else {
				// ShowDialog.alert(mActivityTabs,
				// "",getResources().getString(R.string.improper_response));
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
		// InputMethodManager imm =
		// (InputMethodManager)mActivityTabs.getSystemService(
		// Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(etBackupKey.getWindowToken(), 0);
		// imm.hideSoftInputFromWindow(etTelephoneNumber.getWindowToken(), 0);
		// etBackupKey.setText("");
		// etTelephoneNumber.setText("");
	}

	
	 * INotifyGalleryDialog iNotifyRecovery=new INotifyGalleryDialog() {
	 * 
	 * @Override public void yes() { checkInternetConnection(); }
	 * 
	 * @Override public void no() { } };
	 

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			if (mActivityTabs instanceof MainBaseActivity) {
				((MainBaseActivity) mActivityTabs).fragmentManager
				.popBackStack();
			} else if (mActivityTabs instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivityTabs).fragmentManager
				.popBackStack();
				((HomeScreenActivity) mActivityTabs)
				.performINotifyCloudBackup();
			}
		} else if (v.getId() == R.id.btnSubmit) {
			if (!etTelephoneNumber.getText().toString().trim().equals("")
					&& etTelephoneNumber.getText().toString().trim().length() <=15
					&& !etBackupKey.getText().toString().trim().equals("")) {
				checkInternetConnection();
			} else {
				if (etBackupKey.getText().toString().trim().equals("")
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

				} else {
					if (etTelephoneNumber.getText().toString().trim().length() < 7) {
						ImageRequestDialog dialogImageRequest = new ImageRequestDialog();
						dialogImageRequest.setCancelable(false);
						dialogImageRequest.newInstance("", mActivityTabs,
								"Contact No length cannot be less than 7 digits", "",
								null);
						dialogImageRequest.show(getChildFragmentManager(), "test");
					}
					else{
						checkInternetConnection();
					}
				}
			}
		}

		else if (v.getId() == R.id.btnHome) {
			if (mActivityTabs instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivityTabs).startActivity(new Intent(
						mActivityTabs, HomeScreenActivity.class));
				((HomeScreenActivity) mActivityTabs).finish();
			}
		}

	}
}
*/