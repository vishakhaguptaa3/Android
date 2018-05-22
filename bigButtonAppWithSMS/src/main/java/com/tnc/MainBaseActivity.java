package com.tnc;

import static com.tnc.common.GlobalCommonValues.DISPLAY_MESSAGE_ACTION;
import static com.tnc.common.GlobalCommonValues.EXTRA_MESSAGE;
import static com.tnc.common.GlobalCommonValues.SENDER_ID;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.splunk.mint.Mint;
import com.tnc.SocialNetwork.Google.GoogleClass;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.TileDetailBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.SMSChargeDialog;
import com.tnc.fragments.ContactListFragment;
import com.tnc.fragments.ContactListTabFragment;
import com.tnc.fragments.CreateContactFragment;
import com.tnc.fragments.HomeTabFragment;
import com.tnc.fragments.UserRegistrationFragment;
import com.tnc.fragments.VerifyingRegistrationFragment;
import com.tnc.fragments.VideoFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.RegistrationCheckService;
import com.tnc.service.SendLocationService;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CancelRegistrationResponseBean;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import cz.msebera.android.httpclient.Header;

/**
 * Parent Class of the App  holds common values
 * @author a3logics
 *
 */
public class MainBaseActivity extends FragmentActivity {

    public static final int CONTACT_LOADER_ID = 78; // From docs: A unique identifier for this loader. Can be whatever you want.
    private Cursor mResultantCursor = null;

    public FragmentManager fragmentManager = null;
    public FragmentTransaction fragmentTransaction = null;
    Context mContext;
    public SharedPreference saveState;
    public Fragment currentFragment;
    private SMSChargeDialog dialogExit;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    public String strImageBase64 = "";
    public static String selectedImagepath="";
    //sets the bitmap of selected contact
    public static Bitmap _bitmap=null;
    public static Bitmap _bitmapContact=null;
    public static boolean isImageSelected=false;
    public static String selectedBBID="";
    public static boolean isImageRequested=false;
    public Fragment objFragment=null;
    public static boolean isContactCreated=false;
    public static boolean isHomeScreenExit=false;
    public static boolean isCameraCanceled=false;
    public INotifyGalleryDialog iNotifyContactListing;
    public INotifyGalleryDialog iNotifyCloudBackup;
    private Gson gson;
    private TransparentProgressDialog progress;
    public static String recoveryType="";
    public static boolean mergeTiles=false;
    public static boolean isBackButtonToDisplay=false;
    public static boolean isFromMain=true;
    public static boolean isNotificationPushSend=false;
    public static int selectedConatctId=-1;
    public static boolean isTileCreated=false;
    public static boolean isReturningUser=false;
    //	public static boolean isAlreadyDisplaying=false;
    public static boolean isSmsSent=false;
    public static boolean isFromHomeScreen=true;
    public ImageLoader imageLoaderNostra = ImageLoader.getInstance();

    //	Phase-4
    public static String contactNameForTile="";
    public static String contactNumberForTile="";
    public static String selectedCountryCodeForTileDetails="";
    public static String selectedPrefixCodeForTileDetails="";
    public static boolean isMobileChecked=false;
    public static boolean isIsdCodeFlagChecked=false;
    public static TileDetailBean objTileDetailBeanStatic=null;
    public static ContactTilesBean objTileEdit=null;
    private Handler handler = new Handler();
    private Receiver receiver;
    private Bundle mBundleData;
    private CallbackManager callbackManager;

    //	public static boolean isAppExit = false;
    //	public static VerifyingRegistrationFragment verifyRegistrationFragment=null;
    /**
     *
     * @param iNotifyCloudBackup
     * interface for making list selected option to be shown as deselected
     */
    public void setINotifyCloudBackup(INotifyGalleryDialog iNotifyCloudBackup){
        this.iNotifyCloudBackup = iNotifyCloudBackup;
    }

    /**
     *
     * @param :INotifyContactListing
     * interface for making list selected option to be shown as deselected
     */
    public void setINotifyContactListing(INotifyGalleryDialog iNotifyContactListing){
        this.iNotifyContactListing = iNotifyContactListing;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //FaceBook callback
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google login callback to start further process on receiving data on successful login
        GoogleClass.onActivityResult(requestCode,resultCode,data);

        super.onActivityResult(requestCode, resultCode, data);//10000,-1,0(IN CASE OF SUCCESS)  10000,0,2(IN CASE OF ERROR-error while retrieving information from the server)
        if(getVisibleFragment()!=null){
            Fragment objFragment=getVisibleFragment();
            if(objFragment  instanceof VerifyingRegistrationFragment){
                ((VerifyingRegistrationFragment)objFragment).handleInAppresponse(requestCode, resultCode, data);
            }
        }

    }

    /**
     * get current fragment
     *
     * @return
     */
    public Fragment getVisibleFragment() {
        try {
            FragmentManager fragmentManager = MainBaseActivity.this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     *
     * @param AlertCallAction
     * interface for making the dialog dismiss & can also also perform
     *  any action after dismiss if required any
     */
    protected AlertCallAction alertBack = new AlertCallAction() {
        @Override
        public void isAlert(boolean isOkClicked) {
            dialogExit.dismiss();
            if (isOkClicked) {
                finish();
            }
        }
    };

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.videoscreen);
        mContext = MainBaseActivity.this;
        saveState = new SharedPreference();
        // Mint Library Initialization
        Mint.initAndStartSession(MainBaseActivity.this, "387688fa");
        //findViewById(R.id.llImageBoundary).setVisibility(View.VISIBLE);

        progress=new TransparentProgressDialog(mContext, R.drawable.customspinner);
        saveState.setDeviceId(mContext,
                Secure.getString(getContentResolver(), Secure.ANDROID_ID));
        saveState.setPrivateKey(mContext,
                mContext.getResources().getString(R.string.private_key));

        //		saveState.getPublicKey(mContext);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        // check if device's gps is enabled or not
        /*if(GlobalConfig_Methods.isGPSEnabled(MainBaseActivity.this)){
            if(!GlobalConfig_Methods.isServiceRunning(MainBaseActivity.this, "com.tnc.service.SendLocationService"))
                startService(new Intent(MainBaseActivity.this,SendLocationService.class));
        }*/

        callbackManager = CallbackManager.Factory.create();

        checkInternetConnection();
        if (!saveState.isRegistered(mContext) && !GlobalConfig_Methods.isValidString(saveState.getPublicKey(mContext)))//(saveState.isFirst(mContext))
        {
            saveState.setChanged(mContext, false);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();//2014/11/12
            saveState.setAUTOBACKUP_TILES_DATE(mContext,dateFormat.format(date));

            //open registration form if user is not registered
            setFragment(new UserRegistrationFragment());
            //setFragment(new VideoFragment());

            // Initialize the loader with a special ID and the defined callbacks from above
            getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), contactsLoader);

            //			saveState.setEmergency(mContext, "911");
        }
        else
        {
            // set boolean value = true to know that the app is from MainBAseActivity
            //since the app has two parent class
            // 1- MainBAseActivity& 2- HomeScreenActivity
            MainBaseActivity.isFromMain=true;
            if(GCMIntentService.isNotificationPushDisplayDialogActivity)
                GCMIntentService.isNotificationPushDisplayDialogActivity=false;
            if(GCMIntentService.isMessagePushDisplayDialogActivity)
                GCMIntentService.isMessagePushDisplayDialogActivity=false;
            if(GlobalCommonValues.pushNotificationString==null)
                GlobalCommonValues.pushNotificationString=null;
            Intent intent=new Intent(mContext,HomeScreenActivity.class);
            MainBaseActivity.isFromMain=true;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
        }
    }

    /**
     * Check Availability of Internet Connection
     */
    public void checkInternetConnection() {
        if (NetworkConnection.isNetworkAvailable(mContext)) {
            registerGCM();
        }
        else{
        }
    }

    /**
     * Register the device to GCM Server
     */
    String regId;
    private void registerGCM()
    {
        checkNotNull(SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        GCMRegistrar.register(this, SENDER_ID);
        regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, SENDER_ID);

        } else {
        }
        Log.d("reg id- ", regId);
        regId = GCMRegistrar.getRegistrationId(this);
        saveState.setGCMRegistrationId(mContext, regId);
    }

    /**
     * setGCM Registration ID of the device
     */
    public void setGCMRegID()
    {
        GCMRegistrar.register(this, SENDER_ID);
        saveState.setGCMRegistrationId(mContext, GCMRegistrar.getRegistrationId(mContext));
    }

    /**
     * @param :instance of Dialog Fragment
     */
    public void callDialog(DialogFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        fragment.show(transaction, "dialog");
    }

    /**
     * @param :instance of Fragment
     */
    public void setFragment(Fragment currentFragment) {
        try {
            this.currentFragment = currentFragment;
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(android.R.id.content, currentFragment);
            fragmentTransaction.addToBackStack(currentFragment.toString());
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume(){
        super.onResume();

        GlobalConfig_Methods.removeBadgeCount(MainBaseActivity.this);

        ArrayList<CountryDetailsBean> listCountryDetails=DBQuery.getAllCountryDetails(MainBaseActivity.this);
        GlobalCommonValues.listCountryCodes.clear();
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter("com.tapnchat.phonecontactsmainscreen");
        registerReceiver(receiver,filter);
        for(CountryDetailsBean mCountryDetailsBean :listCountryDetails){
            GlobalCommonValues.listCountryCodes.add(mCountryDetailsBean.CountryCode);
            GlobalCommonValues.listIDDCodes.add(mCountryDetailsBean.IDDCode);
        }
        if(fragmentManager==null && MainBaseActivity.isHomeScreenExit){
            resetGlobalValues();
            finish();
        }
        else if(MainBaseActivity.isHomeScreenExit){
            resetGlobalValues();
            finish();
        }

        if(!saveState.IS_FetchingContacts(mContext)){
            if(fragmentManager==null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveState.setRefrehContactList(MainBaseActivity.this,false);
                        saveState.setIS_FROM_HOME(MainBaseActivity.this,false);
                        saveState.setIS_FetchingContacts(getApplicationContext(), true);
                        //						new fetchContactsClass().execute();
                    }
                }, 100);
            }
            else if(fragmentManager!=null){
                // fetch Phone contacts
                int num=fragmentManager.getBackStackEntryCount();
                BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(num-1);
                if(!backStackEntry.getName().contains("ContactListFragment") &&
                        !backStackEntry.getName().contains("ContactListTabFragment")){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveState.setRefrehContactList(MainBaseActivity.this,false);
                            saveState.setIS_FROM_HOME(MainBaseActivity.this,false);
                            saveState.setIS_FetchingContacts(getApplicationContext(), true);
                            //							new fetchContactsClass().execute();
                        }
                    }, 100);
                }
            }
        }
    }

    // Method to reset the static values in the app
    private void resetGlobalValues()
    {
        if(GlobalCommonValues.listBBContacts!=null && !GlobalCommonValues.listBBContacts.isEmpty())
        {
            GlobalCommonValues.listBBContacts.clear();
            GlobalCommonValues.listBBContacts=null;
        }
        HomeScreenActivity.flBackArrow=null;
        HomeScreenActivity.flSettingButton=null;
        HomeScreenActivity.flInformationButton=null;
        HomeScreenActivity.btnBack=null;
        HomeScreenActivity.btnSettings=null;
        HomeScreenActivity.btnInformation=null;
        GlobalCommonValues._Contacimage=null;
        //		HomeScreenActivity.manager=null;
        GlobalCommonValues.isBackedupSuccessful=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);

       /* if(GlobalConfig_Methods.isServiceRunning(MainBaseActivity.this, "com.tnc.service.SendLocationService"))
            stopService(new Intent(MainBaseActivity.this,SendLocationService.class));*/

        //		if((getVisibleFragment()!=null) && ((getVisibleFragment() instanceof CheckReturningUserFragment) ||
        //				(getVisibleFragment() instanceof VerifyingRegistrationFragment))){
        //			Toast.makeText(getApplicationContext(), "MainBaseActivity",1000).show();
        //			if(!saveState.getPassCode(MainBaseActivity.this).trim().equals("")){
        //				CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
        //				objCancelRegistration.setPasscode(saveState.getPassCode(MainBaseActivity.this));
        //				cancelRegistration(objCancelRegistration);
        //			}
        //			GlobalConfig_Methods.clearRegsitrationPreferences(MainBaseActivity.this);
        //		}


        MainBaseActivity._bitmap=null;
        MainBaseActivity.selectedImagepath=null;
        MainBaseActivity.isImageSelected=false;
        //phase-4 Comment
        //		MainBaseActivity.selectedContactName=null;
        //		MainBaseActivity.selectedISDCode=null;
        MainBaseActivity.selectedBBID=null;
        saveState.setBBID_User(MainBaseActivity.this,"");
        //phase-4 Comment
        //		MainBaseActivity.selectedContactNumber=null;
        GlobalCommonValues._Contacimage=null;
        MainBaseActivity.isImageRequested=false;
        MainBaseActivity.isContactCreated=false;
        MainBaseActivity.isHomeScreenExit=false;
        MainBaseActivity.isCameraCanceled=false;
        //		GlobalCommonValues.pushNotificationString=null;
        //		GlobalCommonValues.NotificationStatus=-1;
        //		GlobalCommonValues.NotificationId=null;
        HomeScreenActivity.isFirsTimeHomeTabOpen=true;
        HomeScreenActivity.manager=null;
        GlobalCommonValues._bitmap=null;
        GlobalCommonValues.isImageSelected=false;
        GlobalCommonValues.selectedImagepath="";
        //		GlobalCommonValues.selectedClipArtUrl="";
        MainBaseActivity.isBackButtonToDisplay=false;
        recoveryType="";
        mergeTiles=false;
        GCMIntentService.isNotificationPushDisplayDialogActivity = false;
        GCMIntentService.isMessagePushDisplayDialogActivity = false;
        MainBaseActivity.isFromMain=true;
        MainBaseActivity.isNotificationPushSend=false;
        MainBaseActivity.selectedConatctId=-1;
        MainBaseActivity.isTileCreated=false;
        MainBaseActivity.isReturningUser=false;
        MainBaseActivity.isSmsSent=false;
        if(saveState==null)
            saveState=new SharedPreference();
        saveState.setResumed(MainBaseActivity.this, true);
        MainBaseActivity.isMobileChecked=false;
        MainBaseActivity.selectedCountryCodeForTileDetails=null;
        MainBaseActivity.selectedPrefixCodeForTileDetails=null;
        MainBaseActivity.isIsdCodeFlagChecked=false;
        MainBaseActivity.contactNumberForTile=null;
        MainBaseActivity.contactNameForTile=null;
        objTileDetailBeanStatic=null;
        //		ContactDetailsFragment.isdCodeNumber=null;
        CreateContactFragment.isdCodeNumberCreateContact=null;
        MainBaseActivity.objTileEdit=null;
        HomeTabFragment.X_Edit=HomeTabFragment.Y_Edit=HomeTabFragment.Top=HomeTabFragment.Right=
                HomeTabFragment.Left=HomeTabFragment.Bottom=HomeTabFragment.X_Delete=HomeTabFragment.Y_Delete
                        =HomeTabFragment.TopDelete=HomeTabFragment.RightDelete=HomeTabFragment.LeftDelete=HomeTabFragment.BottomDelete=-1;
        //		GlobalCommonValues.selectedOriginalNumber=null;
    }

    /**
     *
     * @param reference
     * @param name
     * Used to check if the GCM ID returned is not null and throws exception if null
     */
    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(getString(R.string.error_config,
                    name));
        }
    }

    /**
     * BroadcastReciever to handle GCM Registration
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Logs.writeLog("MainBaseActivity", "BroadCastOnReceive", intent.getExtras().getString(EXTRA_MESSAGE));
            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    /**
     * // Method to call web service to cancel the registration
     * @param objCancelRegistration
     */
    public void cancelRegistration(
            CancelRegistrationRequestBean objCancelRegistration) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(objCancelRegistration);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection
                    .postWithJsonEntityHeader(MainBaseActivity.this,
                            GlobalCommonValues.CANCEL_REGISTRATION,
                            stringEntity, cancelRegistrationResponseHandler,
                            MainBaseActivity.this
                                    .getString(R.string.private_key), "");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //Async task to cancel the registration
    AsyncHttpResponseHandler cancelRegistrationResponseHandler = new JsonHttpResponseHandler() {
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
                if (response != null) {
                    Logs.writeLog("CancelRegistration", "OnSuccess", response.toString());
                    getResponseCancelRegistration(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("CancelRegistration", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /**
     * Method to handle response we got from the server to cancel the user registration
     * @param response
     */
    private void getResponseCancelRegistration(String response) {
        try {
            ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
            dialogErrorMessage.setCancelable(false);
            saveState.setRegistered(MainBaseActivity.this,false);
            saveState.setPublicKey(MainBaseActivity.this,"");
            saveState.setPassCode(MainBaseActivity.this,"");
            saveState.setCountryCode(MainBaseActivity.this,"");
            saveState.setCountryname(MainBaseActivity.this,"");
            saveState.setCountryidd(MainBaseActivity.this,"");
            saveState.setCountryidd(MainBaseActivity.this,"");
            saveState.setUserName(MainBaseActivity.this, "");
            saveState.setUserPhoneNumber(MainBaseActivity.this, "");

            boolean isServiceRunning = GlobalConfig_Methods.isServiceRunning(MainBaseActivity.this, "com.tnc.service.RegistrationCheckService");
            if(isServiceRunning){
                stopService(new Intent(MainBaseActivity.this,RegistrationCheckService.class));
            }

            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                CancelRegistrationResponseBean get_Response = gson.fromJson(response,
                        CancelRegistrationResponseBean.class);
                if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE))
                {
                    //					saveState.setRegistered(MainBaseActivity.this,false);
                    //					saveState.setPublicKey(MainBaseActivity.this,"");
                    //					startActivity(new Intent(MainBaseActivity.this,HomeScreenActivity.class));
                    //					finish();
                }
                else if (get_Response.getResponse_code().equals(
                        GlobalCommonValues.FAILURE_CODE_1) || get_Response.getResponse_code().equals(
                        GlobalCommonValues.FAILURE_CODE_5) || get_Response.getResponse_code().equals(
                        GlobalCommonValues.FAILURE_CODE_2) || get_Response.getResponse_code().equals(
                        GlobalCommonValues.FAILURE_CODE_3)) {
                    dialogErrorMessage.newInstance("",
                            MainBaseActivity.this,
                            get_Response.getResponse_message(), "", null);
                    dialogErrorMessage.show(getSupportFragmentManager(), "test");
                }
            }
        }
        catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            try {
                int num=fragmentManager.getBackStackEntryCount();
                BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(num-1);

                String fragment=fragmentManager.getFragments().get(1).toString();
                //				if(fragment.contains("SettingsFragment"))

                if(backStackEntry.getName().contains("RegistrationFeatures") && fragment.contains("SettingsFragment"))
                {
                    fragmentManager.popBackStack();
                    //					startActivity(new Intent(MainBaseActivity.this,HomeScreenActivity.class));
                    //					finish();
                }
                else if(backStackEntry.getName().contains("RegistrationFeatures") && !fragment.contains("SettingsFragment"))
                {
                    startActivity(new Intent(MainBaseActivity.this,HomeScreenActivity.class));
                    finish();
                }
                else{
                    if(
                            backStackEntry.getName().contains("SendSMSFullScreenDialogFragment") ||
                                    backStackEntry.getName().contains("TnCUsers_NotifyFragmentRegistration") ||
                                    backStackEntry.getName().contains("VerifyingRegistrationFragment") ||
                                    backStackEntry.getName().contains("CheckReturningUserFragment") ||
                                    backStackEntry.getName().contains("PremiumFeaturesFragment"))
                    {
                    }
                    else if(backStackEntry.getName().contains("BackupListFragment") &&
                            !MainBaseActivity.isBackButtonToDisplay)
                    {

                    }
                    else if (!saveState.isFirst(mContext) && (fragmentManager.getBackStackEntryCount() == 2)) {
                        dialogExit=new SMSChargeDialog();
                        dialogExit.setCancelable(false);
                        dialogExit.newInstance("", MainBaseActivity.this,getResources().getString(R.string.txtExitMessage),"",alertBack);
                        dialogExit.show(getSupportFragmentManager(), "test");
                    }
                    else if (saveState.isFirst(mContext) && (fragmentManager.getBackStackEntryCount() == 1)) {
                        dialogExit=new SMSChargeDialog();
                        dialogExit.newInstance("", MainBaseActivity.this,getResources().getString(R.string.txtExitMessage),"",alertBack);
                        dialogExit.show(getSupportFragmentManager(), "test");
                    }
                    else if (!saveState.isFirst(mContext) && (fragmentManager.getBackStackEntryCount() == 1)) {
                        dialogExit=new SMSChargeDialog();
                        dialogExit.newInstance("", MainBaseActivity.this,getResources().getString(R.string.txtExitMessage),"",alertBack);
                        dialogExit.show(getSupportFragmentManager(), "test");
                    } else {
                        MainBaseActivity.isImageSelected=false;
                        fragmentManager.popBackStack();
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return false;
        }
		/*else if ((keyCode == KeyEvent.KEYCODE_HOME)) {
			if(getVisibleFragment() instanceof CheckReturningUserFragment){
				Toast.makeText(getApplicationContext(), "MainBaseActivity",1000).show();
				if(!saveState.getPassCode(MainBaseActivity.this).trim().equals("")){
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(saveState.getPassCode(MainBaseActivity.this));
					cancelRegistration(objCancelRegistration);
				}
				GlobalConfig_Methods.clearRegsitrationPreferences(MainBaseActivity.this);
			}

			Toast.makeText(getApplicationContext(), "HomeScreenActivity",1000).show();
		}*/
        return false;
    }


    /**
     * Broadcast receiver that will be fired when we get a response from GetContactService Class
     * for the TnC User
     *
     * This class is used to refresh contact list if the current display screen is of contact listing
     * @author a3logics
     *
     */
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {
                if(fragmentManager!=null){
                    int num=fragmentManager.getBackStackEntryCount();
                    BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(num-1);
                    if(backStackEntry.getName().contains("ContactListFragment") ||
                            backStackEntry.getName().contains("ContactListTabFragment")){
                        if(backStackEntry.getName().contains("ContactListFragment")){
                            Fragment fragment = fragmentManager.getFragments().get(num-1);
                            if(fragment instanceof ContactListFragment){
                                ((ContactListFragment)fragment).refreshContactList("contactlist");
                                saveState.setIS_FetchingContacts(getApplicationContext(), false);
                            }
                        }else if(backStackEntry.getName().contains("ContactListTabFragment")){
                            Fragment fragment = fragmentManager.getFragments().get(num-1);
                            if(fragment instanceof ContactListTabFragment){
                                ((ContactListTabFragment)fragment).refreshContactList("contactlist");
                                saveState.setIS_FetchingContacts(getApplicationContext(), false);
                            }
                        }
                    }
                }else{
                    if(getVisibleFragment() instanceof HomeTabFragment){
                        if(((HomeTabFragment)getVisibleFragment()).dialogProgress!=null && ((HomeTabFragment)getVisibleFragment()).dialogProgress.isShowing()){
                            ((HomeTabFragment)getVisibleFragment()).dialogProgress.dismiss();
                        }
                    } else if(getVisibleFragment() instanceof ContactListFragment){
                        if(((ContactListFragment)getVisibleFragment()).dialogProgress!=null && ((ContactListFragment)getVisibleFragment()).dialogProgress.isShowing()){
                            ((ContactListFragment)getVisibleFragment()).dialogProgress.dismiss();
                        }
                    }else if(getVisibleFragment() instanceof ContactListTabFragment){
                        if(((ContactListTabFragment)getVisibleFragment()).dialogProgress!=null && ((ContactListTabFragment)getVisibleFragment()).dialogProgress.isShowing()){
                            ((ContactListFragment)getVisibleFragment()).dialogProgress.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                if(getVisibleFragment() instanceof HomeTabFragment){
                    if(((HomeTabFragment)getVisibleFragment()).dialogProgress!=null && ((HomeTabFragment)getVisibleFragment()).dialogProgress.isShowing()){
                        ((HomeTabFragment)getVisibleFragment()).dialogProgress.dismiss();
                    }
                } else if(getVisibleFragment() instanceof ContactListFragment){
                    if(((ContactListFragment)getVisibleFragment()).dialogProgress!=null && ((ContactListFragment)getVisibleFragment()).dialogProgress.isShowing()){
                        ((ContactListFragment)getVisibleFragment()).dialogProgress.dismiss();
                    }
                }else if(getVisibleFragment() instanceof ContactListTabFragment){
                    if(((ContactListTabFragment)getVisibleFragment()).dialogProgress!=null && ((ContactListTabFragment)getVisibleFragment()).dialogProgress.isShowing()){
                        ((ContactListFragment)getVisibleFragment()).dialogProgress.dismiss();
                    }
                }
                e.getMessage();
            }}
    }

    //Loader To Fetch Contacts

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                // Create and return the actual cursor loader for the contacts data
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                    String select = "((" + Contacts.DISPLAY_NAME
                            + " NOTNULL) AND ("
                            + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                            + Contacts.DISPLAY_NAME + " != '' ) AND ("
                            +ContactsContract.CommonDataKinds.Email.DATA+"))";

                    // Define the columns to retrieve      -- [_id, display_name, data1, data1, photo_uri]
                    String[] projectionFields =  new String[] { ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts.PHOTO_URI };
                    // Construct the loader
                    CursorLoader cursorLoader = new CursorLoader(MainBaseActivity.this,
                            Phone.CONTENT_URI, // URI
                            null,  // projection fields
                            select, // the selection criteria
                            null, // the selection args
                            Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC" // the sort order
                    );
                    // Return the loader for use
                    return cursorLoader;
                }

                // When the system finishes retrieving the Cursor through the CursorLoader,
                // a call to the onLoadFinished() method takes place.
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    // The swapCursor() method assigns the new Cursor to the adapter
                    //			adapter.swapCursor(cursor);

                    //			if (saveState.isFirst(mContext)){
                    mResultantCursor = cursor;

                    // Iterate through all the record in a cursor
                    if(mResultantCursor!=null && mResultantCursor.getCount()>0){
                        mResultantCursor.moveToFirst();
                        GlobalCommonValues.listContacts = new ArrayList<ContactDetailsBean>();

                        new SetContactsToDisplay().execute("");
                        //				}
				/*handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						new SetContactsToDisplay().execute("");
					}
				}, 100);*/
                    }
                }

                // This method is triggered when the loader is being reset
                // and the loader data is no longer available. Called if the data
                // in the provider changes and the Cursor becomes stale.
                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    // Clear the Cursor we were using with another call to the swapCursor()
                }
            };

    /**
     * async task to set the display contact list being fetched from the server
     */
    class SetContactsToDisplay extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //			if (saveState.isFirst(mContext)){
            // Set the contact list to display
            if(mResultantCursor!=null && !mResultantCursor.isClosed())
                GlobalConfig_Methods.setContactListToDislpay(mResultantCursor);
            //			}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}