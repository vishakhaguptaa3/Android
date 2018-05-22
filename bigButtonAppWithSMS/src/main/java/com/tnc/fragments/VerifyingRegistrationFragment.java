package com.tnc.fragments;

import java.util.ArrayList;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.android.util.IabHelper;
import com.tnc.android.util.IabResult;
import com.tnc.android.util.Inventory;
import com.tnc.android.util.Purchase;
import com.tnc.base.BaseFragment;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.DefaultMessagesBeanDB;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.NotifyFriendsConfirmationDialog;
import com.tnc.dialog.WelcomeBackReg_RestoreDialog;
import com.tnc.dialog.WelcomeDialogRegistration;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.GetContactService;
import com.tnc.service.RegistrationCheckService;
import com.tnc.utility.Logs;
import com.tnc.webresponse.DefaultMessagesResponse;
import com.tnc.webresponse.EmergencyNumberRespnseBean;
import com.tnc.webresponse.GetBBIDResponseBeanData;
import com.tnc.webresponse.VerifyRegistrationResponse;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to perform verification of new registration
 *  @author a3logics
 */
public class VerifyingRegistrationFragment extends BaseFragment implements
OnClickListener {

	private Context mAct;
	private TransparentProgressDialog progress;
	private Gson gson;
	private Button btnRetry, btnSkip;
	private boolean isTimeOut = false;
	private TextView tvTitle, tvVerify;
	private SharedPreference saveState;
	private boolean isNewRegistration=false;
	private Receiver receiver;
	public static boolean isNotify=false;
	private final String TAG = "IN APP";
	public static boolean isAppUserRegistered = false;

	private boolean IS_PREMIUM_USER = false;

	final String SKU_INAPPITEM = "tnc";
	String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkkemygKDASReh29hWVLYRr8HnVhnawd7UHnWLN+ft/2fX9PGAXTCy88Ot6aLEcEM1yJPEO2BtmE7WafNDvgMWtHq6nV3UR/9uQIcCl7l1pus8ugxYp9xX6hAQSAd6okstow5xPDKpsOZCNeAVN6JSGWn1m0MR2YeDkz/o61GsmxMUZf91LJxJkPYApeUrNc3qJNdmSw/a1XKWS/Hs63JFWBKvBptcxvXBhwSkbxKvGvRnJ5MMTre+ak7e/kMpSyL3ijnOp/+fNJ8fTY4OWpIszsmuuNB1jZEMbyEsP0n/j4KdEGA/RkbUFA2XP+uCKJ2x7PCzdGKktE1swPiqOQ5oQIDAQAB";

	// The helper object
	IabHelper mHelper;
	MyCountInAppPurchase counter;
	String backupKey="";
	private Intent mIntentRegistrationService;

	public VerifyingRegistrationFragment newInstance(Context mAct) {
		this.mAct = mAct;
		VerifyingRegistrationFragment frag = new VerifyingRegistrationFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.verifyingregistrationprogressbar,
				container,false);
		try {
			progress = new TransparentProgressDialog(mAct, R.drawable.customspinner);
		} catch (Exception e) {
			e.getMessage();
		}
		receiver = new Receiver();
		saveState = new SharedPreference();
		idInitialization(view);

		/*//start a service to check if the app is removed from the recent app list
		mIntentRegistrationService = new Intent(getActivity(),RegistrationCheckService.class);
		if(mIntentRegistrationService!=null)
			getActivity().startService(mIntentRegistrationService);*/

		verifyRegistration();
		return view;
	}

	// Method to initialize views/widgets
	private void idInitialization(View view) {
		btnRetry = (Button) view.findViewById(R.id.btnRetry);
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvVerify = (TextView) view.findViewById(R.id.tvVerify);
		btnRetry.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
		tvVerify.setVisibility(View.VISIBLE);
		btnRetry.setVisibility(View.GONE);
		btnSkip.setVisibility(View.GONE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");

		//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

		new CountDownTimer(60000, 1000) {
			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				isTimeOut = true;
			}
		}.start();
	}

	/**
	 * Method to call api to cancel registration
	 */

	private void verifyRegistration() {
		try {
			CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
			objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
			gson = new Gson();
			String stingGson = gson.toJson(objCancelRegistration);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mAct,
					GlobalCommonValues.VERIFY_REGISTRATION, stringEntity,
					verifyRegistrationResponseHandler,
					mAct.getString(R.string.private_key), "");

		} catch (Exception e) {
			e.getMessage();
			SharedPreference saveState = new SharedPreference();
			CancelRegistrationRequestBean objCancelRegistration = null;
			if (mAct instanceof MainBaseActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((MainBaseActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((HomeScreenActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
			clearDataBaseValues();
		}
	}

	//async task to handle call to the api to verify user registration
	AsyncHttpResponseHandler verifyRegistrationResponseHandler = new JsonHttpResponseHandler() {
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
					Logs.writeLog("VerifyRegistration", "OnSuccess", response.toString());
					getResponseVerifyRegistration(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
				SharedPreference saveState = new SharedPreference();
				CancelRegistrationRequestBean objCancelRegistration = null;
				if (mAct instanceof MainBaseActivity) {
					objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((MainBaseActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(getActivity(),
							HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((HomeScreenActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(getActivity(),
							HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}
				clearDataBaseValues();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if (progress != null && progress.isShowing())
				progress.dismiss();
			if (response != null)
				Logs.writeLog("VerifyRegistration", "OnFailure", response);
		}

		@Override
		public void onFinish() {

			// Completed the request (either success or failure)
		}
	};

	/**
	 * handle response we got from the server as a call to the api for the verify the user registration
	 * @param response
	 */
	private void getResponseVerifyRegistration(String response) {
		try {
			//						String title="",message="";
			ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
			dialogErrorMessage.setCancelable(false);

			SharedPreference saveStatePreferences = new SharedPreference();

			if (!TextUtils.isEmpty(response)
					&& GlobalConfig_Methods.isJsonString(response)) {
				gson = new Gson();
				VerifyRegistrationResponse get_Response = gson.fromJson(
						response, VerifyRegistrationResponse.class);

				//check if the user is premium user or not

				if((get_Response!=null) && (get_Response.data!=null) && (get_Response.data.getIs_premium_user()!=null)){
					try{
						if(get_Response.data.is_premium_user.equalsIgnoreCase("yes")){
							IS_PREMIUM_USER = true;
						}else{
							IS_PREMIUM_USER = false;
						}
					}catch(Exception e){
						e.getMessage();
					}
				}

				if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
					if(get_Response.data.guest_user.equalsIgnoreCase("yes")){

						//set premium user value
						saveStatePreferences.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);

						// checking app registration to prevent backdoor entry of the registration
						isAppUserRegistered = true;

						// in case of guest user registration

						if(DBQuery.getAllTiles(getActivity()).size()>0){
							saveState.setUploadBackupRequested(mActivity,true);
						}

						saveState.setRegistered(getActivity(), true);

						if (progress != null && progress.isShowing())
							progress.dismiss();
						backupKey = get_Response.getData().backup_key;
						MainBaseActivity.isReturningUser=false;

						saveState.setNewRegistration(mAct, true);
						String title="Congratulations!";
						String message="User profile created. Your backup key is "+backupKey +"."
								+ "\n We recommend you store the backup key in a safe place outside of this phone to help recover your contacts on a new phone.";

						WelcomeDialogRegistration dialogNewRegistration = new WelcomeDialogRegistration();
						dialogNewRegistration.setCancelable(false);
						if (mAct instanceof MainBaseActivity) {
							dialogNewRegistration.newInstance(title,
									((MainBaseActivity) mAct),
									message, "", null,
									iNotify);
							dialogNewRegistration.show(((MainBaseActivity) mAct)
									.getSupportFragmentManager(), "test");
						} else if (mAct instanceof HomeScreenActivity) {
							dialogNewRegistration.newInstance(title,
									((HomeScreenActivity) mAct),
									message, "", null,
									iNotify);
							dialogNewRegistration.show(((HomeScreenActivity) mAct)
									.getSupportFragmentManager(), "test");
						}

						saveState.setBackupKey(mAct,get_Response.getData().getBackup_key());
						saveState.setPublicKey(mAct,get_Response.getData().getPublic_key());
						saveState.setCountryCode(mAct,get_Response.getData().country_code);
						saveState.setIS_RecentRegistration(mAct, true);

                        //clear temp username
						GlobalConfig_Methods.clearTempUserName(getActivity());

						if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
							getBBID();
						}
						saveState.setIS_DEACTIVATED(getActivity(),false);
					}else if(get_Response.data.guest_user.equalsIgnoreCase("no")){

						// checking app registration to prevent backdoor entry of the registration
						isAppUserRegistered = false;


						if(DBQuery.getAllTiles(mAct).size()>0){
							saveState.setUploadBackupRequested(mActivity,true);
						}

						saveState.setRegistered(getActivity(), true);

						if (progress != null && progress.isShowing())
							progress.dismiss();

						saveState.setCountryCode(mAct,get_Response.getData().country_code);

						if(saveState.getPublicKey(mAct).equals(get_Response.getData().getPublic_key()))
						{   						

							// Case of New User
							// checking app registration to prevent backdoor entry of the registration
							isAppUserRegistered = false;

							isNewRegistration=true;
							if(saveState.getISRETURNINGUSER(getActivity())){
								//set premium user value
								saveStatePreferences.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);
								CheckReturningUserFragment fragmentReturningUser = new CheckReturningUserFragment();
								fragmentReturningUser.newInstance(mAct,mReturningUser);
								if (mAct instanceof MainBaseActivity) {
									((MainBaseActivity) mAct).setFragment(fragmentReturningUser);
								} else if (mAct instanceof HomeScreenActivity) {
									((HomeScreenActivity) mAct).setFragment(fragmentReturningUser);
								}
							}else{
								// Commented as In-App Purchase' Functionality is shifted somewhere else
								//registerNewUser();

								//set premium user value
								saveStatePreferences.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);

								// checking app registration to prevent backdoor entry of the registration
								isAppUserRegistered = true;

								// in case of guest user registration

								backupKey = get_Response.getData().backup_key;
								MainBaseActivity.isReturningUser=false;

								saveState.setNewRegistration(mAct, true);
								String title="Congratulations!";
								String message="User profile created. Your backup key is "+backupKey +"."
										+ "\n We recommend you store the backup key in a safe place outside of this phone to help recover your contacts on a new phone.";

								WelcomeDialogRegistration dialogNewRegistration = new WelcomeDialogRegistration();
								dialogNewRegistration.setCancelable(false);
								if (mAct instanceof MainBaseActivity) {
									dialogNewRegistration.newInstance(title,
											((MainBaseActivity) mAct),
											message, "", null,
											iNotify);
									dialogNewRegistration.show(((MainBaseActivity) mAct)
											.getSupportFragmentManager(), "test");
								} else if (mAct instanceof HomeScreenActivity) {
									dialogNewRegistration.newInstance(title,
											((HomeScreenActivity) mAct),
											message, "", null,
											iNotify);
									dialogNewRegistration.show(((HomeScreenActivity) mAct)
											.getSupportFragmentManager(), "test");
								}

								saveState.setBackupKey(mAct,get_Response.getData().getBackup_key());
								saveState.setPublicKey(mAct,get_Response.getData().getPublic_key());
								saveState.setCountryCode(mAct,get_Response.getData().country_code);
								saveState.setIS_RecentRegistration(mAct, true);

                                //clear temp username
                                GlobalConfig_Methods.clearTempUserName(getActivity());

								if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
									getBBID();
								}
								saveState.setIS_DEACTIVATED(getActivity(),false);
							}

							backupKey = get_Response.getData().getBackup_key();
							saveState.setBackupKey(mAct,get_Response.getData().getBackup_key());

						}
						else if(!saveState.getPublicKey(mAct).equals(get_Response.getData().getPublic_key()))
						{
							//set premium user value
							saveStatePreferences.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);
							// Case of Returning User
							// checking app registration to prevent backdoor entry of the registration
							isAppUserRegistered = true;

							isNewRegistration=false;
							saveState.setPublicKey(mAct,get_Response.getData().getPublic_key());
						}
						saveState.setIS_DEACTIVATED(getActivity(),false);
						saveState.setBackupKey(mAct,get_Response.getData().getBackup_key());
						if(isNewRegistration)
						{
							MainBaseActivity.isReturningUser=false;
							/*if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
								getBBID();
							}*/
						}
						else if(!isNewRegistration)
						{

                            //clear temp username
                            GlobalConfig_Methods.clearTempUserName(getActivity());

							saveState.setIS_RecentRegistration(mAct, true);
							if (progress != null && progress.isShowing())
								progress.dismiss();

							String _title="";
							String _message="Welcome Back to "+getResources().getString(R.string.app_name)+"!\nDo you want to recover your cloud backup?";
							WelcomeBackReg_RestoreDialog welcomeBackRegistrationDialog=
									new WelcomeBackReg_RestoreDialog();
							welcomeBackRegistrationDialog.setCancelable(false);
							welcomeBackRegistrationDialog.newInstance(_title, mAct,_message,"");
							if (mAct instanceof MainBaseActivity)
							{
								welcomeBackRegistrationDialog.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
							}
							else if (mAct instanceof HomeScreenActivity)
							{
								welcomeBackRegistrationDialog.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
							}

							MainBaseActivity.isReturningUser=true;
							if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
								getBBID();
							}
						}
						//					UserRegistration.number = "";

					}
				} else if (get_Response.getResponse_code().equals(
						GlobalCommonValues.FAILURE_CODE)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_1)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_5)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_2)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_3)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_4)) {
					if (!isTimeOut) {
						verifyRegistration();
					} else {
						if (progress != null && progress.isShowing())
							progress.dismiss();
						tvVerify.setVisibility(View.GONE);
						btnRetry.setVisibility(View.VISIBLE);
						btnSkip.setVisibility(View.VISIBLE);
						saveState.setRegistered(getActivity(), false);
						dialogErrorMessage = new ImageRequestDialog();
						dialogErrorMessage.setCancelable(false);
						if (mActivity instanceof MainBaseActivity) {
							dialogErrorMessage.newInstance("",
									((MainBaseActivity) mActivity),
									"Phone number verification failed", "", null);
							dialogErrorMessage.show(((MainBaseActivity) mActivity)
									.getSupportFragmentManager(), "test");
						} else if (mActivity instanceof HomeScreenActivity) {
							dialogErrorMessage.newInstance("",
									((HomeScreenActivity) mActivity),
									"Phone number verification failed", "", null);
							dialogErrorMessage.show(((HomeScreenActivity) mActivity)
									.getSupportFragmentManager(), "test");
						}
					}
				}
			}
		} catch (Exception e) {
			e.getMessage();
			SharedPreference saveState = new SharedPreference();
			CancelRegistrationRequestBean objCancelRegistration = null;
			if (mAct instanceof MainBaseActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((MainBaseActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((HomeScreenActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
			clearDataBaseValues();
		}
	}

	/**
	 * interface to handle call to //Notify friends about our availabitity on Tnc App if registered for the fIrst time
	 */
	INotifyGalleryDialog iNotify = new INotifyGalleryDialog() {
		@Override
		public void yes() {
		}

		@Override
		public void no() {
			//Notify friends about our availabitity on Tnc App if registered for the first time
			if(saveState.isNewRegistration(mAct))
			{
				NotifyFriendsConfirmationDialog dialog=new NotifyFriendsConfirmationDialog();
				dialog.newInstance("",mAct,"Do you like to notify all \n"+getResources().getString(R.string.app_name)+" users from your contact list?","",iNotifyTncContacts);
				dialog.setCancelable(false);
				dialog.show(getChildFragmentManager(), "test");
			}
		}
	};

	/**
	 * interface to handle call to returning user case ,if user clicked the returning user
	 */
	private INotifyAction mReturningUser = new INotifyAction() {

		@Override
		public void setAction(String action) {
			if(action.contains("200")){
				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = true;

				//set premium user value
				//saveState.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);
				saveState.setISRETURNINGUSER(mAct, false);
				saveState.setIS_RecentRegistration(mAct, true);
				saveState.setIS_NUMBER_CHANGED(mAct, true);
				saveState.setUPDATE_EMERGENCY(mAct, true);

                //clear temp username
                GlobalConfig_Methods.clearTempUserName(getActivity());

				if (progress != null && progress.isShowing())
					progress.dismiss();

				String _title="";
				String _message="Welcome Back to "+getResources().getString(R.string.app_name)+"!\nDo you want to recover your cloud backup?";
				WelcomeBackReg_RestoreDialog welcomeBackRegistrationDialog=
						new WelcomeBackReg_RestoreDialog();
				welcomeBackRegistrationDialog.setCancelable(false);
				welcomeBackRegistrationDialog.newInstance(_title, mAct,_message,"");
				if (mAct instanceof MainBaseActivity){
					welcomeBackRegistrationDialog.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if (mAct instanceof HomeScreenActivity){
					welcomeBackRegistrationDialog.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}

				MainBaseActivity.isReturningUser=true;
				if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
					getBBID();
				}
			}else if(action.contains("new_user")){
				//				registerNewUser();
			}
		}
	};

	/**
	 * Method to be called in case of new registration
	 */
	private void registerNewUser(){
		/*isNewRegistration=true;

		if(counter == null){
			counter = new MyCountInAppPurchase(2500, 1000);
			counter.start();
		}
		mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);
		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");
				//				Toast.makeText(getActivity(), "registerNewUser()-onIabSetupFinished"+result.toString()+"--"+result.getResponse()+"--"+result.getMessage(), Toast.LENGTH_LONG).show();
				if (!result.isSuccess()) {
					// checking app registration to prevent backdoor entry of the registration 
					isAppUserRegistered = false;

					// there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// IAB is fully set up. Now, let's get an inventory of
				// stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
		MainBaseActivity.isReturningUser=false;*/
	}

	//Method to call bbid web service to get user details

	private void callBBIDService(){
		if (saveState.isRegistered(mAct) && !saveState.getPublicKey(mAct).trim().equals("")) {
			getBBID();
		}
	}

	/**
	 * Method to update tiles
	 */
	private void updateTiles(){
		ArrayList<ContactTilesBean> listTiles = DBQuery.getAllTiles(mAct);
		for(int i=0;i<listTiles.size();i++)
		{
			String number=listTiles.get(i).getPhoneNumber();
			String mCountryCode = listTiles.get(i).getCountryCode();
			if(mCountryCode == null){
				mCountryCode = "";
			}
			String tilePosition=listTiles.get(i).getTilePosition();
			String numberSplitted=GlobalConfig_Methods.getBBNumberToCheck(mAct, mCountryCode + number);
			String[] numberArray=numberSplitted.split(",");
			String countryCode=numberArray[0];
			String phoneNumber=numberArray[1];
			String isdCode=listTiles.get(i).getPrefix()+numberArray[4];
			DBQuery.updateTileDetailsOnRegistration(mAct,countryCode,isdCode,phoneNumber, tilePosition);
		}
	}

	//Interface to handle ,whether user want to notify users for the availability of him on TnC/not
	INotifyGalleryDialog iNotifyTncContacts = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			if(progress!=null){
				progress.show();
			}
			saveState.setRefrehContactList(getActivity(),true);
			saveState.setIS_FROM_HOME(getActivity(),false);

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					new StartServiceClass().execute();
				}
			},100);
		}

		@Override
		public void no() {
			// In case user selected no for not being to notified to friends for the number changed
			//then go to home screen
			if (mAct instanceof MainBaseActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
		}
	};

	/**
	 * Method to go to home screen
	 */
	private void gotoHomeScreen(){
		//go to home screen
		if (mAct instanceof MainBaseActivity) {
			startActivity(new Intent(getActivity(),HomeScreenActivity.class));
			((MainBaseActivity) mAct).finish();
		} else if (mAct instanceof HomeScreenActivity) {
			startActivity(new Intent(getActivity(),HomeScreenActivity.class));
			((HomeScreenActivity) mAct).finish();
		}
	}

	//async task to call service to send the contacts to the server
	class StartServiceClass extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			saveState.setRefrehContactList(getActivity(),true);
			saveState.setIS_FROM_HOME(getActivity(),false);
			isNotify=true;

			if(GlobalCommonValues.listContacts!=null &&
					GlobalCommonValues.listContacts.size()>0){
				GlobalCommonValues.listContactsSendToServer =new
						ArrayList<ContactDetailsBean>();
				for(int i=0;i<GlobalCommonValues.listContacts.size();i++)
					GlobalCommonValues.listContactsSendToServer.add(GlobalCommonValues.listContacts.get(i));
			}

			Intent mainIntent = new Intent(getActivity(),GetContactService.class);
			getActivity().startService(mainIntent);
		}
	}
	//get bbid after registration
	private void getBBID() {
		// call method to stop the service for registration complete check
		stopRegistrationService();

		try {
			gson = new Gson();
			MyHttpConnection.postHeaderWithoutJsonEntity(mAct,
					GlobalCommonValues.GET_BBID, getBBIDResponseHandler,
					mAct.getString(R.string.private_key),
					saveState.getPublicKey(mAct));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to call web service to get the registered user details
	AsyncHttpResponseHandler getBBIDResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("getBBIDResponseHandler", "OnSuccess",
							response.toString());
					getResponseBBID(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("getBBIDResponseHandler", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/*
	 * method to handle the response we got as a user details
	 * @param response
	 */
	private void getResponseBBID(String response) {
		try {
			String response2="";
			if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
				response2=response.substring(response.indexOf("user_id")-2,response.length());
			}
			else{
				response2=response;
			}

			if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
				gson = new Gson();
				GetBBIDResponseBeanData get_Response = gson.fromJson(response2,GetBBIDResponseBeanData.class);
				if (get_Response.is_activate == 0) {
					// checking app registration to prevent backdoor entry of the registration
					isAppUserRegistered = false;
				}
				else if (get_Response.is_activate == 1) {
					saveState.setRegistered(getActivity(), true);
					saveState.setCountryCode(mAct,get_Response.country_code);
					saveState.setBBID(mAct, get_Response.user_id);
					if(saveState.getEmergency(mAct).trim().equals(""))
						saveState.setEmergency(mAct,get_Response.country_emergency);
					saveState.setUserName(mAct, Uri.decode(get_Response.name));
					saveState.setUserMailID(mAct, Uri.decode(get_Response.email));
					//String number=get_Response.number.substring(get_Response.number.length()-10,get_Response.number.length());
					saveState.setUserPhoneNumber(mAct,get_Response.number);
					String iddCode=DBQuery.getIDDCodeDB(mAct, saveState.getCountryCode(mAct));
					saveState.setCountryidd(mAct,iddCode);

					if(get_Response.is_email_verified==1)
						saveState.setIsVerified(mActivity,true);
					else{
						saveState.setIsVerified(mAct,false);
					}
					if(get_Response.email!=null && !get_Response.email.trim().equals("") &&
							!get_Response.email.trim().equalsIgnoreCase("null")	&& saveState.getUserMailID(getActivity()).trim().equals("")){
						saveState.setUserMailID(getActivity(),Uri.decode(get_Response.email));
					}

					//Update Tile Details
					if(DBQuery.getAllTiles(mAct).size()>0){
						updateTiles();
					}

					if(get_Response.image!=null && !get_Response.image.trim().equals("") && !get_Response.image.trim().equalsIgnoreCase("null"))
					{
					}

					if (get_Response != null
							&& get_Response.is_default_image
							.equalsIgnoreCase("no")
							&& !get_Response.image.trim().equals("")
							&& !get_Response.image.trim().equalsIgnoreCase(
									"NULL")) {
						saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
						saveState.setDefaultImage(mAct, false);
					}
					else if (get_Response != null
							&& get_Response.is_default_image
							.equalsIgnoreCase("yes")
							&& !get_Response.image.trim().equals("")
							&& !get_Response.image.trim().equalsIgnoreCase(
									"NULL")) {
						saveState.setDefaultImage(mAct, true);
					}

					//call the web service to fetch latest emergency numbers from the webservice
					checkInternetConnectionEmergencyNumber();

					//call the web service to fetch latest default messages from the webservice
					checkInternetConnectionDefaultMessages();
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter("com.bigbutton.receiver");
		getActivity().registerReceiver(receiver,filter);
	}

	@Override
	public void onDestroy() {
		isTimeOut = false;

		if(!isAppUserRegistered){

		}
		super.onDestroy();
	}

	/**
	 * check availabitiy of internet connection for emergency number
	 */
	public void checkInternetConnectionEmergencyNumber() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			getEmergencyNumbersRequest();
		}
	}

	// method to call web service to get update emergency numbers from the web service
	private void getEmergencyNumbersRequest()
	{
		MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_EMERGENCY_NUMBERS,
				mActivity.getResources().getString(R.string.private_key),emergencyNumbersResponsehandler);
	}

	// Async task to call web service to get Emergency Numbers
	AsyncHttpResponseHandler emergencyNumbersResponsehandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			//			if ((!progress.isShowing()))
			//				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null){
					Logs.writeLog("Emergency Numbers", "OnSuccess",response.toString());
					getResponseEmergencyNumbers(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Emergency Numbers", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			//			if (progress.isShowing())
			//				progress.dismiss();
		}
	};

	/*
	 * Handling Response from the Server for the request being sent to get Emergency Numbers
	 */
	private void getResponseEmergencyNumbers(String response) {
		try {
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				ArrayList<CountryDetailsBean> listCountries=new ArrayList<CountryDetailsBean>();
				try {
					gson = new Gson();
					EmergencyNumberRespnseBean get_Response = gson.fromJson(response,EmergencyNumberRespnseBean.class);
					if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
						if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
						{
							CountryDetailsBean mObjCountryDetailBean;
							for(int i=0;i<get_Response.getData().size();i++){
								mObjCountryDetailBean= new CountryDetailsBean();
								mObjCountryDetailBean.setCountryName(get_Response.getData().get(i).getCountry());
								mObjCountryDetailBean.setEmergency(get_Response.getData().get(i).getEmergency());
								listCountries.add(mObjCountryDetailBean);
							}
							saveState.setIS_EMERGENCY_NUMBER_VERSION_UPDATED(mActivity, false);

							//Delete the existing Emergency Number table from the database
							if(listCountries!=null && listCountries.size()>0)
								DBQuery.deleteTable("EmergencyNumbers", "", null, getActivity().getApplicationContext());
							// Insert Emergency Number in the Database
							DBQuery.insertAllCountryEmergencyNumbers(mActivity,listCountries);
						}
					}
					else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){


					}
				}
				catch (Exception e){
					e.getMessage();
				}

			} else {
				//ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}


	/**
	 * check availability of internet connection for default messages
	 */
	public void checkInternetConnectionDefaultMessages() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			getInitMessagesRequest();
		}
	}

	//Method to call web service to get configured mesages from the server
	private void getInitMessagesRequest()
	{
		MyHttpConnection.getWithoutPara(getActivity(),GlobalCommonValues.GET_DEFAULT_MESSAGES,
				mActivity.getResources().getString(R.string.private_key),defaultMessagesResponsehandler);
	}

	// Async task to call web service to get configured mesages
	AsyncHttpResponseHandler defaultMessagesResponsehandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			//			if ((!progress.isShowing()))
			//				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null){
					Logs.writeLog("Default Mesasges", "OnSuccess",response.toString());
					getResponseDefaultMessages(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Default Mesasges", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			//			if (progress.isShowing())
			//				progress.dismiss();
		}
	};

	/*
	 * Handling Response from the Server for the request being sent to get configured mesages
	 */
	private void getResponseDefaultMessages(String response) {
		try {
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				ArrayList<InitResponseMessageBean> listInitMessages=new ArrayList<InitResponseMessageBean>();
				try {
					Gson gson = new Gson();
					DefaultMessagesResponse get_Response = gson.fromJson(response,DefaultMessagesResponse.class);
					if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){

						ArrayList<DefaultMessagesBeanDB> mListDefaultMessages = new ArrayList<DefaultMessagesBeanDB>();
						if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
						{
							DefaultMessagesBeanDB mObjDefaultMessagesBeanDB;

							int maxID = DBQuery.getConfigMessagesMaxCount(getActivity());

							if(maxID==-1 || maxID==0){
								maxID = 1;
							}else{
								maxID=maxID+1;
							}
							for(int i=0;i<get_Response.getData().size();i++){
								mObjDefaultMessagesBeanDB= new DefaultMessagesBeanDB();
								mObjDefaultMessagesBeanDB.setId(maxID+i);
								mObjDefaultMessagesBeanDB.setMessage(get_Response.getData().get(i).getMessage());
								mObjDefaultMessagesBeanDB.setIsLocked(1);

								String mType = get_Response.getData().get(i).getType();
								int mTypeMessage = (mType.equals("initiation") ? 0: 1);  // 0- initiation  1- response

								mObjDefaultMessagesBeanDB.setType(mTypeMessage);
								mListDefaultMessages.add(mObjDefaultMessagesBeanDB);
							}
							saveState.setIS_DEFAULT_MESSAGES_VERSION_UPDATED(mActivity, false);

							//Delete all the default messages of older version
							if(mListDefaultMessages!=null && mListDefaultMessages.size()>0)
								DBQuery.deleteConfigMessageFromDB(getActivity(), 1);

							// Insert Emergency Number in the Database
							DBQuery.insertConfigMessageFromWebService(mActivity,mListDefaultMessages);
						}
					}
					else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){

					}
				}
				catch (Exception e){
					e.getMessage();
				}

			} else {
				//ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * BroadcastReceiver gets fired when we got a response from the server after sending the contacts
	 * @author a3logics
	 *
	 */
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if(!(getUserVisibleHint()) || !(VerifyingRegistrationFragment.this.isVisible())){
				return;
			}

			isNotify=false;
			if(progress!=null && progress.isShowing())
			{
				progress.dismiss();
			}
			ArrayList<BBContactsBean> listBBContacts=DBQuery.getAllBBContactsOrdered(mAct);
			if(listBBContacts.isEmpty())
			{
				// go to home screen
				if (mAct instanceof MainBaseActivity) {
					startActivity(new Intent(getActivity(),HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					startActivity(new Intent(getActivity(),HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}
			}
			else if(listBBContacts.size()>0)
			{
				// In case user selected yes to notify friends for the your availability on TnC
				TnCUsers_NotifyFragmentRegistration objFragment = new TnCUsers_NotifyFragmentRegistration();
				objFragment.newInstance("new registration");
				if(mAct instanceof MainBaseActivity)
					((MainBaseActivity)mAct).setFragment(objFragment);
				if(mAct instanceof HomeScreenActivity)
					((HomeScreenActivity)mAct).setFragment(objFragment);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRetry:
			if(mAct instanceof MainBaseActivity)
			{
				((MainBaseActivity)mAct).fragmentManager.popBackStack();
			}
			else if(mAct instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mAct).fragmentManager.popBackStack();
			}
			break;

		case R.id.btnSkip:
			// checking app registration to prevent backdoor entry of the registration
			isAppUserRegistered = false;

			SharedPreference saveState = new SharedPreference();
			CancelRegistrationRequestBean objCancelRegistration = null;
			if (mAct instanceof MainBaseActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((MainBaseActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				objCancelRegistration = new CancelRegistrationRequestBean();
				objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
				((HomeScreenActivity) mAct)
				.cancelRegistration(objCancelRegistration);
				startActivity(new Intent(getActivity(),
						HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
			clearDataBaseValues();
			// dismiss();
			//			UserRegistration.number = "";
			break;

		default:
			break;
		}
	}

	//In App Purchase data

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");
			//			Toast.makeText(getActivity(), "mGotInventoryListener"+result.toString()+"--"+result.getResponse()+"--"+result.getMessage(), Toast.LENGTH_LONG).show();

			if (result.isFailure()) {
				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = false;
				clearDataBaseValues();
				complain("Failed to query inventory: " + result);
				return;
			}
			Log.d(TAG, "Query inventory was successful.");
			/* Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
	  verifyDeveloperPayload().*/


			// Check for in app purchase
			Purchase removeAdsPurchase = inventory.getPurchase(SKU_INAPPITEM);
			if (removeAdsPurchase != null && verifyDeveloperPayload(removeAdsPurchase)) {
				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = true;

				Log.d(TAG, "User has already purchased this item . Write the Logic for making him continue using the app.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_INAPPITEM),
						mConsumeFinishedListener);
				return;
			}

			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}

	};

	public void handleInAppresponse(int requestCode, int resultCode, Intent data){
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			//super.onActivityResult(requestCode, resultCode, data);
			//			Toast.makeText(getActivity(), "Handled",1000).show();
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
			//Toast.makeText(getActivity(), "onActivityResult handled by IABUtil.",1000).show();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	};

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (progress != null && progress.isShowing())
				progress.dismiss();
			saveState.setIS_DEACTIVATED(getActivity(),false);

			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);
			if (result.isFailure()) {//IabResult: Success (response: 0:OK)
				complain("Error purchasing: " + result);
				// Functionality in case purchasing fails

				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = false;


				saveState.setRegistered(getActivity(), false);
				saveState.setPublicKey(getActivity(), "");
				saveState.setCountryCode(mAct,"");
				saveState.setCountryname(mAct,"");
				saveState.setBBID(mAct, "");
				saveState.setUserName(mAct, "");
				saveState.setUserMailID(mAct, "");
				saveState.setUserPhoneNumber(mAct,"");
				saveState.setCountryidd(mAct,"");
				saveState.setIsVerified(mAct,false);
				saveState.setIS_DEACTIVATED(mAct,false);

				if (mAct instanceof MainBaseActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((MainBaseActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((MainBaseActivity) mAct),
							HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((HomeScreenActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((HomeScreenActivity) mAct),
							HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}

				saveState.setPassCode(getActivity(), "");
				/**
				 * CALL METHOD TO CLEAR DATABSE
				 */

				clearDataBaseValues();

				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				// Functionality in case purchasing fails

				saveState.setRegistered(getActivity(), false);
				saveState.setPublicKey(getActivity(), "");
				saveState.setCountryCode(mAct,"");
				saveState.setCountryname(mAct,"");
				saveState.setBBID(mAct, "");
				saveState.setUserName(mAct, "");
				saveState.setUserMailID(mAct, "");
				saveState.setUserPhoneNumber(mAct,"");
				saveState.setCountryidd(mAct,"");
				saveState.setIsVerified(mAct,false);
				saveState.setIS_DEACTIVATED(mAct,false);

				if (mAct instanceof MainBaseActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((MainBaseActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((MainBaseActivity) mAct),
							HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((HomeScreenActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((HomeScreenActivity) mAct),
							HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}

				saveState.setPassCode(getActivity(), "");

				/**
				 * CALL METHOD TO CLEAR DATABSE
				 */

				clearDataBaseValues();

				return;
			}

			Log.d(TAG, "Purchase successful.");
			// Functionality after purchasing the app
			//Toast.makeText(getActivity(),"Purchase successful.",Toast.LENGTH_LONG).show();
			if (purchase.getSku().equals(SKU_INAPPITEM)) {
				//purchased the app
				Log.d(TAG,
						"App Purchase was succesful.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);
			//			Toast.makeText(getActivity(), "mConsumeFinishedListener"+result.toString()+"--"+result.getResponse()+"--"+result.getMessage(), Toast.LENGTH_LONG).show();
			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {

				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = true;

				saveState.setIS_RecentRegistration(mAct, true);
				// successfully consumed, so we apply the effects of the item in
				// our
				// app purchasing's logic

				Log.d(TAG, "Consumption successful. Provisioning.");
				alert("You have purchased tapnchat app.");

				saveState.setNewRegistration(mAct, true);
				String title="Congratulations!";
				String message="User profile created. Your backup key is "+backupKey +"."
						+ "\n We recommend you store the backup key in a safe place outside of this phone to help recover your contacts on a new phone.";

				//Commented as a part of In-App purcghase functionality
				WelcomeDialogRegistration dialogNewRegistration = new WelcomeDialogRegistration();
				dialogNewRegistration.setCancelable(false);
				if (mAct instanceof MainBaseActivity) {
					dialogNewRegistration.newInstance(title,
							((MainBaseActivity) mAct),
							message, "", null,
							iNotify);
					dialogNewRegistration.show(((MainBaseActivity) mAct)
							.getSupportFragmentManager(), "test");
				} else if (mAct instanceof HomeScreenActivity) {
					dialogNewRegistration.newInstance(title,
							((HomeScreenActivity) mAct),
							message, "", null,
							iNotify);
					dialogNewRegistration.show(((HomeScreenActivity) mAct)
							.getSupportFragmentManager(), "test");
				}

				// call bbid web service to get user details
				callBBIDService();

			} else {

				// checking app registration to prevent backdoor entry of the registration
				isAppUserRegistered = false;

				complain("Error while consuming: " + result);

				saveState.setRegistered(getActivity(), false);
				saveState.setPublicKey(getActivity(), "");
				saveState.setCountryCode(mAct,"");
				saveState.setCountryname(mAct,"");
				saveState.setBBID(mAct, "");
				saveState.setUserName(mAct, "");
				saveState.setUserMailID(mAct, "");
				saveState.setUserPhoneNumber(mAct,"");
				saveState.setCountryidd(mAct,"");
				saveState.setIsVerified(mAct,false);
				saveState.setIS_DEACTIVATED(mAct,false);

				if (mAct instanceof MainBaseActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((MainBaseActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((MainBaseActivity) mAct),
							HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					SharedPreference saveState = new SharedPreference();
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((HomeScreenActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(((HomeScreenActivity) mAct),
							HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}

				saveState.setPassCode(getActivity(), "");

				/**
				 * CALL METHOD TO CLEAR DATABSE
				 */

				clearDataBaseValues();
			}
			Log.d(TAG, "End consumption flow.");
		}
	};

	//  1- Purchase Successful 2- OnActivityresult-IAB 3- result.isSuccess()
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		@SuppressWarnings("unused")
		String payload = p.getDeveloperPayload();

		/* TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.*/

		return true;
	}

	void complain(String message) {
		//		Toast.makeText(getActivity(), "complain"+message, Toast.LENGTH_LONG).show();
		Log.e(TAG, "**** IN APP Purchase Error: " + message);//Error purchasing: IabResult: User canceled. (response: -1005:User cancelled)
		alert(message);
	}

	void alert(String message) {
		//		Toast.makeText(getActivity(), "alert"+message, Toast.LENGTH_LONG).show();
		Log.d(TAG, "Showing alert dialog: " + message);
		//alert("Result : " +message);
	}

	// Timer to perform In App Purchase
	class MyCountInAppPurchase extends CountDownTimer{

		public MyCountInAppPurchase(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			Log.d(TAG, "In Ap purchase Event");

			/* TODO: for security, generate your payload here for verification. See
			 * the comments on verifyDeveloperPayload() for more info. on a production app you
			 * should carefully generate this.*/

			String payload = "";

			try {
				mHelper.launchPurchaseFlow(getActivity(), SKU_INAPPITEM, 10000,
						mPurchaseFinishedListener, payload);
			} catch (Exception e) {
				e.getMessage();

				SharedPreference saveState = new SharedPreference();
				CancelRegistrationRequestBean objCancelRegistration = null;
				if (mAct instanceof MainBaseActivity) {
					objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((MainBaseActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(getActivity(),
							HomeScreenActivity.class));
					((MainBaseActivity) mAct).finish();
				} else if (mAct instanceof HomeScreenActivity) {
					objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
					((HomeScreenActivity) mAct)
					.cancelRegistration(objCancelRegistration);
					startActivity(new Intent(getActivity(),
							HomeScreenActivity.class));
					((HomeScreenActivity) mAct).finish();
				}
				clearDataBaseValues();
			}
			if(counter!=null){
				counter.cancel();
				counter=null;
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
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
		GlobalConfig_Methods.clearRegsitrationPreferences(mAct);
	}

	/**
	 * Method to stop the registration service
	 */
	private void stopRegistrationService(){
		boolean isServiceRunning = GlobalConfig_Methods.isServiceRunning(mActivity, "com.tnc.service.RegistrationCheckService");
		if(mIntentRegistrationService!=null && isServiceRunning)
			getActivity().stopService(mIntentRegistrationService);
	}
}
