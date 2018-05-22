package com.tnc.fragments;

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
import com.tnc.bean.PremiumUserRequestBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageGalleryDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.PremiumUserWebResponse;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display registration fetaures/benefits of the app
 *  @author a3logics
 */

public class PremiumFeaturesFragment extends BaseFragment implements OnClickListener
{
	private TextView tvTitle,tvRegistrationMessage,tvRegBenefitFirst,tvRegBenefitSecond,tvRegBenefitThird,
			tvRegBenefitFourth,tvRegBenefitFifth,tvRegisterConfirmation, tvUpgradeNow;
	private EditText mTextGroupCode;

	private Button btnYes,btnNo;
	private String title = "",mFromScreen = "";
	private final String TAG = "IN APP";

	private final String SKU_INAPPITEM = "tnc";
	private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkkemygKDASReh29hWVLYRr8HnVhnawd7UHnWLN+ft/2fX9PGAXTCy88Ot6aLEcEM1yJPEO2BtmE7WafNDvgMWtHq6nV3UR/9uQIcCl7l1pus8ugxYp9xX6hAQSAd6okstow5xPDKpsOZCNeAVN6JSGWn1m0MR2YeDkz/o61GsmxMUZf91LJxJkPYApeUrNc3qJNdmSw/a1XKWS/Hs63JFWBKvBptcxvXBhwSkbxKvGvRnJ5MMTre+ak7e/kMpSyL3ijnOp/+fNJ8fTY4OWpIszsmuuNB1jZEMbyEsP0n/j4KdEGA/RkbUFA2XP+uCKJ2x7PCzdGKktE1swPiqOQ5oQIDAQAB";
	private String mPromoCode = "";

	// The helper object
	private IabHelper mHelper;
	private MyCountInAppPurchase counter;
	private TransparentProgressDialog progress;

	private Intent mIntentRegistrationService;

	private SharedPreference saveState;

	private Gson gson;

	private INotifyGalleryDialog iNotifyGalleryDialog;

	public void setINotificationDialog(ImageGalleryDialog imageGalleryDialog){
		this.imageGalleryDialog = imageGalleryDialog;
	}

	public PremiumFeaturesFragment newInstance(String title, String mFromScreen, INotifyGalleryDialog iNotifyGalleryDialog)
	{
		this.title=title;
		this.mFromScreen=mFromScreen;
		this.iNotifyGalleryDialog = iNotifyGalleryDialog;
		PremiumFeaturesFragment frag = new PremiumFeaturesFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.premium_feature, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(HomeScreenActivity.btnBack!=null)
		{
			HomeScreenActivity.btnBack.setEnabled(false);
			HomeScreenActivity.btnBack.setClickable(false);
		}
		if(HomeScreenActivity.btnDisable!=null)
			HomeScreenActivity.btnDisable.setEnabled(false);
		MainBaseActivity._bitmap=null;
		MainBaseActivity._bitmapContact=null;
	}

	@Override
	public void onPause() {
		super.onPause();
		if(HomeScreenActivity.btnBack!=null)
		{
			HomeScreenActivity.btnBack.setEnabled(true);
			HomeScreenActivity.btnBack.setClickable(true);
		}
		if(mActivity instanceof MainBaseActivity)
		{
			if(((MainBaseActivity)mActivity)!=null)
			{
				if (NetworkConnection.isNetworkAvailable(mActivity))
				{
					if(saveState.getGCMRegistrationId(getActivity()).equals(""))
					{
						((MainBaseActivity)mActivity).setGCMRegID();
					}
				}
			}
		}
	}

	// Method to initialize views/widgets
	private void idInitialization(View view)
	{
		progress = new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvRegistrationMessage=(TextView) view.findViewById(R.id.tvRegistrationMessage);
		tvRegBenefitFirst=(TextView)view.findViewById(R.id.tvRegBenefitFirst);
		tvRegBenefitSecond=(TextView)view.findViewById(R.id.tvRegBenefitSecond);
		tvRegBenefitThird=(TextView)view.findViewById(R.id.tvRegBenefitThird);
		tvRegBenefitFourth=(TextView)view.findViewById(R.id.tvRegBenefitFourth);
		tvRegBenefitFifth=(TextView)view.findViewById(R.id.tvRegBenefitFifth);
		tvRegisterConfirmation=(TextView) view.findViewById(R.id.tvRegisterConfirmation);
		tvUpgradeNow= (TextView) view.findViewById(R.id.tvUpgradeNow);
		mTextGroupCode=(EditText) view.findViewById(R.id.etGroupCode);
		btnYes=(Button)view.findViewById(R.id.btnYes);
		btnNo=(Button)view.findViewById(R.id.btnNo);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegistrationMessage, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFirst, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitSecond, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitThird, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFourth, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFifth, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvRegisterConfirmation, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvUpgradeNow, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
		tvRegistrationMessage.setText(getActivity().getResources().getString(R.string.txtPremiumFeatureTitle));
		tvRegisterConfirmation.setText(getActivity().getResources().getString(R.string.txtWouldYouLikeToRegister));
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);

		//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			//Check if user has entered a promo code

			mPromoCode = mTextGroupCode.getText().toString();

			if(!(mPromoCode.equalsIgnoreCase(""))){
				callWebServiceToMakePremiumUser(mPromoCode);
			}else{
				//start a service to check if the app is removed from the recent app list
				/* mIntentRegistrationService = new Intent(getActivity(),RegistrationCheckService.class);
                 if(mIntentRegistrationService!=null)
                     getActivity().startService(mIntentRegistrationService);*/
				inAppPurchaseApplication();
				//                 if(mActivity instanceof MainBaseActivity)
				//                 {
				//                     ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragment());
				//                 }
				//                 else if(mActivity instanceof HomeScreenActivity)
				//                 {
				//                     ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragment());
				//                 }
			}

		}
		else if(v.getId()==R.id.btnNo)
		{
			if(mActivity instanceof MainBaseActivity)
			{
				((MainBaseActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
				getActivity().finish();
			}
			else if(mActivity instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
				getActivity().finish();
			}
		}
	}

	@Override
	public void onDestroy() {
		if(iNotifyGalleryDialog!=null)
			iNotifyGalleryDialog.yes();
		super.onDestroy();
	}

	/*
        Method to perform InApp Purchase of an app
	 */
	private void inAppPurchaseApplication(){
		registerNewUser();
	}

	/**
	 * Method to be called in case of new registration
	 */
	private void registerNewUser(){

		if(counter == null){
			counter = new MyCountInAppPurchase(2500, 1000);
			counter.start();
		}
		if((getActivity()!=null) && !(getActivity().isFinishing())){
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
						//isAppUserRegistered = false;

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
		}
		MainBaseActivity.isReturningUser=false;

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
				//isAppUserRegistered = false;
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
				//isAppUserRegistered = true;

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
				//isAppUserRegistered = false;


				/* saveState.setRegistered(getActivity(), false);
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
                }*/

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

				/* saveState.setRegistered(getActivity(), false);
                saveState.setPublicKey(getActivity(), "");
                saveState.setCountryCode(mAct,"");
                saveState.setCountryname(mAct,"");
                saveState.setBBID(mAct, "");
                saveState.setUserName(mAct, "");
                saveState.setUserMailID(mAct, "");
                saveState.setUserPhoneNumber(mAct,"");
                saveState.setCountryidd(mAct,"");
                saveState.setIsVerified(mAct,false);
                saveState.setIS_DEACTIVATED(getActivity(),false);

                if (getActivity() instanceof MainBaseActivity) {
                    SharedPreference saveState = new SharedPreference();
                    CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(getActivity()));
                    ((MainBaseActivity) getActivity())
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(((MainBaseActivity) getActivity()),
                            HomeScreenActivity.class));
                    ((MainBaseActivity) getActivity()).finish();
                } else if (getActivity() instanceof HomeScreenActivity) {
                    SharedPreference saveState = new SharedPreference();
                    CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(getActivity()));
                    ((HomeScreenActivity) getActivity())
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(((HomeScreenActivity) getActivity()),
                            HomeScreenActivity.class));
                    ((HomeScreenActivity) getActivity()).finish();
                }

                saveState.setPassCode(getActivity(), "");*/

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
				mPromoCode = ""; //Commented below line 14-9-2016
				//				callWebServiceToMakePremiumUser("");
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
				//isAppUserRegistered = true;

				/* saveState.setIS_RecentRegistration(mAct, true);*/
				// successfully consumed, so we apply the effects of the item in
				// our
				// app purchasing's logic

				Log.d(TAG, "Consumption successful. Provisioning.");
				alert("You have purchased Chatstasy app.");
				mPromoCode = "";
				callWebServiceToMakePremiumUser("");
				/*saveState.setNewRegistration(mAct, true);
                String title="Congratulations!";
                String message="Your registration is complete. Your backup key is "+backupKey +"."
                        + "\n We recommend you store the backup key in a safe place outside of this phone to help recover your contacts on a new phone."+"\nIf you provided a valid email ID, an email will be sent to you. This might be in "
                        +"your Spam folder. It is important that you open the email and click the \'verify\' "
                        +"link to verify your email ID.";

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
                callBBIDService();*/

			} else {

				// checking app registration to prevent backdoor entry of the registration
				//isAppUserRegistered = false;

				complain("Error while consuming: " + result);

				/* saveState.setRegistered(getActivity(), false);
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
				 */
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
	class MyCountInAppPurchase extends CountDownTimer {

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

				/* SharedPreference saveState = new SharedPreference();
                CancelRegistrationRequestBean objCancelRegistration = null;
                if (getActivity() instanceof MainBaseActivity) {
                    objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(getActivity()));
                    ((MainBaseActivity) getActivity())
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(getActivity(),
                            HomeScreenActivity.class));
                    ((MainBaseActivity) getActivity()).finish();
                } else if (getActivity() instanceof HomeScreenActivity) {
                    objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(mAct));
                    ((HomeScreenActivity) getActivity())
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(getActivity(),
                            HomeScreenActivity.class));
                    ((HomeScreenActivity) getActivity()).finish();
                }
                clearDataBaseValues();*/
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
	 * Method to call the web service to set the user as a premium user on the web server
	 */
	private void callWebServiceToMakePremiumUser(String promo_code){
		checkInternetConnection(promo_code);
	}


	/**
	 * check internet availability
	 */
	public void checkInternetConnection(String promo_code) {
		if (NetworkConnection.isNetworkAvailable(getActivity())) {
			gson = new Gson();
			try {

				if(saveState == null){
					saveState = new SharedPreference();
				}
				PremiumUserRequestBean objPremiumuser = new PremiumUserRequestBean();
				objPremiumuser.setBackup_key(saveState.getBackupKey(getActivity()));
				objPremiumuser.setPromo_code(promo_code);
				gson = new Gson();
				String stingGson = gson.toJson(objPremiumuser);
				cz.msebera.android.httpclient.entity.StringEntity stringEntity;
				stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
				MyHttpConnection.postWithJsonEntityHeader(getActivity(),
						GlobalCommonValues.UPDATE_PREMIUM_STATUS, stringEntity,
						verifyPremiumUserResponseHandler,
						getResources().getString(R.string.private_key), "");

			} catch (Exception e) {
				saveState.setISPREMIUMUSER(getActivity(),false);
			}
		} else {
			GlobalConfig_Methods.displayNoNetworkAlert(getActivity());
		}
	}

	//async task to handle call to the api to verify premium user
	AsyncHttpResponseHandler verifyPremiumUserResponseHandler = new JsonHttpResponseHandler() {
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
					Logs.writeLog("VerifyPremiumUser", "OnSuccess", response.toString());
					getResponseVerifyPremiumUserRegistration(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
				saveState.setISPREMIUMUSER(getActivity(),false);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			saveState.setISPREMIUMUSER(getActivity(),false);
			if (response != null)
				Logs.writeLog("VerifyPremiumUser", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			if (progress != null && progress.isShowing())
				progress.dismiss();
			// Completed the request (either success or failure)
		}
	};

	/**
	 * handle response we got from the server as a call to the api for the verifying premium user
	 * @param response
	 */
	private void getResponseVerifyPremiumUserRegistration(String response) {
		try {

			ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
			dialogErrorMessage.setCancelable(false);
			if (!TextUtils.isEmpty(response)
					&& GlobalConfig_Methods.isJsonString(response)) {
				gson = new Gson();
				PremiumUserWebResponse get_Response = gson.fromJson(
						response, PremiumUserWebResponse.class);
				if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
					if(saveState == null){
						saveState = new SharedPreference();
					}
					saveState.setISPREMIUMUSER(mActivity,true);
					if(mActivity instanceof MainBaseActivity){
						if(((MainBaseActivity)mActivity).fragmentManager!=null)
							((MainBaseActivity)mActivity).fragmentManager.popBackStack();
					}else if(mActivity instanceof HomeScreenActivity){
						if(((HomeScreenActivity)mActivity).fragmentManager!=null)
							((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
					}

				}else{
					if(saveState == null){
						saveState = new SharedPreference();
					}
					saveState.setISPREMIUMUSER(mActivity,false);

					if(!mPromoCode.trim().equalsIgnoreCase("")){
						ImageRequestDialog mDialog = new ImageRequestDialog();
						mDialog.setCancelable(false);
						mDialog.newInstance("",getActivity(),getResources().getString(R.string.txtPromoCodeError),"",mActionClearPromoCode);
						mDialog.show(getChildFragmentManager(),"Test");
					}

				}
			}
		}catch(Exception e){
			if(saveState == null){
				saveState = new SharedPreference();
			}
			saveState.setISPREMIUMUSER(mActivity,false);
			e.getMessage();
		}
	}
	/**
	 * METHOD TO CLEAR DATABSE
	 */

	private void clearDataBaseValues(){
		/* DBQuery.deleteTable("BBContacts", "", null, getActivity());
        DBQuery.deleteTable("Messages", "", null, getActivity());
        DBQuery.deleteTable("Notifications", "", null,
                getActivity());
        GlobalConfig_Methods.clearRegsitrationPreferences(getActivity());*/
	}

	AlertCallAction mActionClearPromoCode = new AlertCallAction() {
		@Override
		public void isAlert(boolean isOkClicked)
		{
			mTextGroupCode.setText("");
		}
	};

	/**
	 * Method to clear the promocode field
	 */
	private void clearPromoCodeField(){
		mTextGroupCode.setText("");
		mPromoCode = "";
	}

}
