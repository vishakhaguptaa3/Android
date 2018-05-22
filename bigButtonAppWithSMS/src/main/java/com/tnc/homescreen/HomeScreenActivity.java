package com.tnc.homescreen;

import static com.tnc.common.GlobalCommonValues.SENDER_ID;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tnc.GCMIntentService;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.SocialNetwork.Google.GoogleClass;
import com.tnc.activities.InfoActivity;
import com.tnc.base.AppTabsConstants;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.NotificationBean;
import com.tnc.bean.NotificationResponseTimer;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.common.UploadBackupServer;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessagePushDisplayDialogActivity;
import com.tnc.dialog.NotificationPushDisplayDialogActivity;
import com.tnc.dialog.PremiumFeaturesDialog;
import com.tnc.dialog.RegistrationCheckDialog;
import com.tnc.dialog.SMSChargeDialog;
import com.tnc.fragments.CallDetailsFragment;
import com.tnc.fragments.ChatFragment;
import com.tnc.fragments.CloudBackupFragment;
import com.tnc.fragments.ContactListFragment;
import com.tnc.fragments.ContactListTabFragment;
import com.tnc.fragments.CreateCallButtonFragment;
import com.tnc.fragments.HomeTabFragment;
import com.tnc.fragments.NotificationsFragment;
import com.tnc.fragments.PremiumFeaturesFragment;
import com.tnc.fragments.SettingsFragment;
import com.tnc.fragments.UserRegistration1;
import com.tnc.fragments.UserRegistrationFragment;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.GetContactService;
import com.tnc.service.RegistrationCheckService;
import com.tnc.service.SendLocationService;
import com.tnc.utility.CalculateDays;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CancelRegistrationResponseBean;
import com.tnc.webresponse.GetAllMessageResponseBean;
import com.tnc.webresponse.SendMessageReponseDataBean;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * Parent class of the App holds common values
 * @author a3logics
 *
 */

public class HomeScreenActivity extends FragmentActivity implements OnClickListener {
    public static final int CONTACT_LOADER_ID = 78; // From docs: A unique identifier for this loader. Can be whatever you want.
    private Cursor mResultantCursor = null;
    private CallbackManager callbackManager;
    private  int RC_SIGN_IN = 23;
    public static FrameLayout flBackArrow, flSettingButton,
            flInformationButton;
    public static Button btnBack, btnSettings, btnInformation, btnHome;
    ImageView tabImageView;
    public TextView tvTitle, tabTvName, tvUnreadMessageCount, tvUnreadCallCount;
    private TabHost mTabHost;
    public String mCurrentTab = AppTabsConstants.HOME;
    public int mCurrentTab_pos = 0;
    public FragmentManager fragmentManager = null;
    public FragmentTransaction fragmentTransaction = null;
    public Fragment currentFragment;
    public HomeTabFragment currentHomeFragment;
    private  SMSChargeDialog dialogExit;
    public Fragment objFragment = null;
    public static FragmentManager manager = null;
    public static boolean isFirsTimeHomeTabOpen = true;
    public FragmentTransaction ft = null;
    public SharedPreference save_State;
    public static Button btnCall_, btnNotification, btnAddTile,btnEdit,btnDelete,btnDisable,btnContactSharing,btnDailPad,btnProfileMode,btnVoiceMail;
    public static ImageView btnCallEmergency;
    public Handler handler = new Handler();
    Timer timer = null;
    TimerTask timerTask = null;
    final int refresh = 1;
    final int stoprefresh = 2;
    final int donothing = 3;
    private int notificationID = 0;
    boolean webservices_oncall;
    private Gson gson;
    private TransparentProgressDialog progress;
    public static TextView tvUnreadNotificationCount;
    public ImageLoader imageLoaderNostra = ImageLoader.getInstance();
    public INotifyGalleryDialog iNotifySettings;
    public INotifyGalleryDialog iNotifyNotifications;
    public INotifyGalleryDialog iNotifyCloudBackup;
    public INotifyGalleryDialog iNotifyBackupListing;
    public INotifyGalleryDialog iNotifyContactListing;
    private Receiver receiver;
    Fragment mFragmentInstance;
    private  Bundle mBundleData = null;
    private Intent mIntent = null;
    private boolean mDisplayRegistrationScreen = false;
    private boolean isCreateChatButton = false;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    public void setINotifySettings(INotifyGalleryDialog iNotifyGalleryDialog) {
        this.iNotifySettings = iNotifyGalleryDialog;
    }

    public void setINotifyNotifications(
            INotifyGalleryDialog iNotifyGalleryDialog) {
        this.iNotifyNotifications = iNotifyGalleryDialog;
    }

    public void setINotifyCloudBackup(INotifyGalleryDialog iNotifyCloudBackup) {
        this.iNotifyCloudBackup = iNotifyCloudBackup;
    }

    public void setINotifyBackupListing(
            INotifyGalleryDialog iNotifyBackupListing) {
        this.iNotifyBackupListing = iNotifyBackupListing;
    }

    public void setINotifyContactListing(
            INotifyGalleryDialog iNotifyContactListing) {
        this.iNotifyContactListing = iNotifyContactListing;
    }

    public void performINotifyCloudBackup() {
        if (this.iNotifyCloudBackup != null
                && !UserRegistration1.isOnUserRegistration)
            this.iNotifyCloudBackup.yes();
    }

    /**
     * interface to update the selectedTab
     */
    public INotifyGalleryDialog iNotifySelectTab = new INotifyGalleryDialog() {
        @Override
        public void yes() {

            GlobalConfig_Methods.setHomeGridViewColumns(getApplicationContext());

            enableRefreshing();
            setUnreadMessageCount();
            setSelectedTab();
            mTabHost.setCurrentTab(0);
            setUnreadMessageCount();
            if(getVisibleFragment()!=null && getVisibleFragment() instanceof HomeTabFragment){

                // Change done on 11-1-2017

                if(((HomeTabFragment)getVisibleFragment()).adapterTiles!=null &&
                        ((HomeTabFragment)getVisibleFragment()).adapterTiles.getCount()>0){

                    if(((HomeTabFragment)getVisibleFragment()).spinnerCategory!=null &&
                            ((HomeTabFragment)getVisibleFragment()).spinnerCategory.getText().toString().equalsIgnoreCase(GlobalCommonValues.RecentCalls)){
                        // recreate tile adapter to display recent calls category updated in case of updated call log history
                        ((HomeTabFragment)getVisibleFragment()).setTilesAdapter();
                    }else{
                        // refresh tile adapter
                        ((HomeTabFragment)getVisibleFragment()).adapterTiles.notifyDataSetChanged();
                    }

                }

                //((HomeTabFragment)getVisibleFragment()).refreshTileAdapter();
                // recreate tile adapter to display recent calls category updated in case of updated call log history
                /*if(((HomeTabFragment)getVisibleFragment()).adapterTiles!=null &&
                        ((HomeTabFragment)getVisibleFragment()).adapterTiles.getCount()>0){
                    //((HomeTabFragment)getVisibleFragment()).adapterTiles.notifyDataSetChanged();
                    ((HomeTabFragment)getVisibleFragment()).setTilesAdapter();
                }*/
            }
        }

        @Override
        public void no() {
        }
    };



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        save_State.setResumed(HomeScreenActivity.this, true);
        //		Toast.makeText(HomeScreenActivity.this, save_State.isResumed(HomeScreenActivity.this)+"On save_State",1000).show();
    }

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        try{
            // Check if gps is
            if(GlobalConfig_Methods.isGPSEnabled(HomeScreenActivity.this)){

                // start service for continuously getting updated location if gps is enable
                if(!GlobalConfig_Methods.isServiceRunning(HomeScreenActivity.this, "com.tnc.service.SendLocationService")){
                    startService(new Intent(HomeScreenActivity.this, SendLocationService.class));
                }

            }else{
                // stop service for continuously getting updated location if gps is disable

                if(GlobalConfig_Methods.isServiceRunning(HomeScreenActivity.this, "com.tnc.service.SendLocationService")){
                    stopService(new Intent(HomeScreenActivity.this, SendLocationService.class));
                }
            }

            if(save_State == null){
                save_State = new SharedPreference();
            }

            GlobalConfig_Methods.removeBadgeCount(HomeScreenActivity.this);

            // display registration reminder alert pop-up if not registered
            /*if(!save_State.getIsRegistrationPopupdisplayed(HomeScreenActivity.this)){

                save_State.setIsRegistrationPopupdisplayed(HomeScreenActivity.this, true);


                if(!isDestroyed()){
                    RegistrationReminderPopup mDialog = new RegistrationReminderPopup();
                    mDialog.setCancelable(false);
                    mDialog.newInstance("", HomeScreenActivity.this, getResources().getString(R.string.txtRegistrationReminderMessage)
                            , "", new INotifyGalleryDialog() {
                                @Override
                                public void yes() {
                                    // In case user clicks yes to register now
                                    setFragment(new UserRegistration());
                                }

                                @Override
                                public void no() {
                                    // In case user wants to register later

                                }
                            });
                    mDialog.show(getSupportFragmentManager(), "Test");
                }
            }*/

            mIntent = this.getIntent();
            mBundleData = mIntent.getExtras();

            if((mIntent!=null) && (mIntent.getExtras()!=null) && (mBundleData!=null)){
                //check if we need to display registration screen

                if(mIntent.hasExtra("display_registration_screen")){
                    mDisplayRegistrationScreen = mIntent.getExtras().getBoolean("display_registration_screen");
                }

                if(mDisplayRegistrationScreen){
                    setFragment(new UserRegistrationFragment());
                }
            }

            receiver = new Receiver();
            IntentFilter filter = new IntentFilter("com.tapnchat.phonecontactshomescreen");
            IntentFilter filterdisplayPopup = new IntentFilter("com.chatstasy.displaypopup");
            IntentFilter refreshCallLogCount = new IntentFilter("com.tapnchat.refreshcalllogcount");

            registerReceiver(receiver,filter);
            registerReceiver(receiver, filterdisplayPopup);
            registerReceiver(receiver, refreshCallLogCount);

            MainBaseActivity.selectedConatctId = -1;
            MainBaseActivity.isReturningUser = false;
            MainBaseActivity.isSmsSent = false;
            MainBaseActivity.isContactCreated=false;
            MainBaseActivity.selectedBBID=null;
            if(!save_State.IS_FetchingContacts(HomeScreenActivity.this)){
                if(fragmentManager==null){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            save_State.setRefrehContactList(HomeScreenActivity.this,false);
                            save_State.setIS_FROM_HOME(HomeScreenActivity.this,false);
                            save_State.setIS_FetchingContacts(getApplicationContext(), true);
                        }
                    }, 100);
                }
                else if(fragmentManager!=null){
                    int num=fragmentManager.getBackStackEntryCount();
                    BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(num-1);
                    if(!backStackEntry.getName().contains("ContactListFragment") &&
                            !backStackEntry.getName().contains("ContactListTabFragment")){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                save_State.setRefrehContactList(HomeScreenActivity.this,false);
                                save_State.setIS_FROM_HOME(HomeScreenActivity.this,false);
                                save_State.setIS_FetchingContacts(getApplicationContext(), true);
                            }
                        }, 100);
                    }
                }
            }

            if(timerTask== null && timer == null)
                iNotifySelectTab.yes();
            save_State.setBBID_User(HomeScreenActivity.this,"");
            GCMIntentService.clearPushNotifications(HomeScreenActivity.this);
            try {
                ArrayList<CountryDetailsBean> listCountryDetails=DBQuery.getAllCountryDetails(HomeScreenActivity.this);
                GlobalCommonValues.listCountryCodes.clear();
                for(CountryDetailsBean mCountryDetailsBean :listCountryDetails)
                {
                    GlobalCommonValues.listCountryCodes.add(mCountryDetailsBean.CountryCode);
                    GlobalCommonValues.listIDDCodes.add(mCountryDetailsBean.IDDCode);
                }
                MainBaseActivity.mergeTiles = false;
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                if (iNotifySettings != null) {
                    iNotifySettings.no();
                } else if (iNotifyCloudBackup != null) {
                    iNotifyCloudBackup.no();
                } else if (iNotifyBackupListing != null) {
                    iNotifyBackupListing.no();
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                Date currentDate = new Date(dateFormat.format(date));
                if (CalculateDays.daysBetween(new Date(
                                save_State
                                        .getAUTOBACKUP_TILES_DATE(HomeScreenActivity.this)),
                        currentDate) > 0
                        && save_State.isChanged(HomeScreenActivity.this)
                        && save_State.isRegistered(HomeScreenActivity.this)) {
                    UploadBackupServer objUploadBackup = null;
                    objUploadBackup = new UploadBackupServer(
                            HomeScreenActivity.this, false, null);
                    objUploadBackup.uploadBackup();
                    // Set Current date as backedup date in shared preference
                }
                else if(save_State.isUploadBackupRequested(HomeScreenActivity.this))
                {
                    UploadBackupServer objUploadBackup = null;
                    objUploadBackup = new UploadBackupServer(
                            HomeScreenActivity.this, false, null);
                    objUploadBackup.uploadBackup();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }catch(Exception e){
            e.getMessage();
        }

        // display premium feature dialog
        displayPremiumFeatureDialog();
        showPremiumFeatureDialog();
    }

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
                    .postWithJsonEntityHeader(HomeScreenActivity.this,
                            GlobalCommonValues.CANCEL_REGISTRATION,
                            stringEntity, cancelRegistrationResponseHandler,
                            HomeScreenActivity.this
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
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("CancelRegistration", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
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
           /* save_State.setRegistered(HomeScreenActivity.this, false);
            save_State.setPublicKey(HomeScreenActivity.this, "");
            save_State.setPassCode(HomeScreenActivity.this,"");
            save_State.setCountryCode(HomeScreenActivity.this,"");
            save_State.setCountryname(HomeScreenActivity.this,"");
            save_State.setCountryidd(HomeScreenActivity.this,"");
            save_State.setUserName(HomeScreenActivity.this, "");
            save_State.setUserPhoneNumber(HomeScreenActivity.this, "");*/

            GlobalConfig_Methods.clearDataBaseValues(HomeScreenActivity.this);
            GlobalConfig_Methods.clearRegsitrationPreferences(HomeScreenActivity.this);
            GlobalConfig_Methods.clearAllPreferences(HomeScreenActivity.this);

            boolean isServiceRunning = GlobalConfig_Methods.isServiceRunning(HomeScreenActivity.this, "com.tnc.service.RegistrationCheckService");
            if(isServiceRunning){
                stopService(new Intent(HomeScreenActivity.this,RegistrationCheckService.class));
            }

            if (!TextUtils.isEmpty(response)
                    && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                CancelRegistrationResponseBean get_Response = gson.fromJson(
                        response, CancelRegistrationResponseBean.class);
                if (get_Response.getResponse_code().equals(
                        GlobalCommonValues.SUCCESS_CODE)) {
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
                    dialogErrorMessage.newInstance("", HomeScreenActivity.this,
                            get_Response.getResponse_message(), "", null);
                    dialogErrorMessage
                            .show(getSupportFragmentManager(), "test");
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to run timer to call web service to get unread message/notification count
     */
    private void enableRefreshing() {
        try {

            if (timer != null) {
                if (timerTask != null) {
                    timer.schedule(timerTask, 0, 6000);
                } else {
                    timerTask.cancel();
                    timerTask = null;
                    timer.cancel();
                    timer = null;
                    initTimerTask();
                    timer.schedule(timerTask, 0, 6000);
                }

            } else {
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                initTimerTask();
                timer.schedule(timerTask, 0, 6000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to disable timer to call web service to get unread message/notification count
     */
    private void disableRefreshing() {
        try {

            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    /**
     * Method to initialize timer to call web service to get unread message/notification count
     */
    public void initTimerTask() {
        try {
            if (timer == null)
                timer = new Timer();
            if (timerTask == null)
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!webservices_oncall) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Message msg = Message.obtain(
                                            messagerefresh_handler, refresh,
                                            null);
                                    messagerefresh_handler.dispatchMessage(msg);
                                }
                            });

                        }
                    }
                };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handler for the timer to call web service to get unread message/notification count
     */
    @SuppressLint("HandlerLeak")
    final Handler messagerefresh_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case refresh:
                        checkInternetConnectionMessageList();
                        break;
                    case stoprefresh:
                        break;
                    case donothing:
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * check internet availability
     */
    private void checkInternetConnectionMessageList() {
        if (NetworkConnection.isNetworkAvailable(HomeScreenActivity.this)) {
            getMessageList();
        } else {
        }
    }

    /**
     * method to send parameters to get unread message count
     */
    private void getMessageList() {
        try {
            SharedPreference save_State = new SharedPreference();
            gson = new Gson();
            // String stingGson = gson.toJson(messageListBean);
            // StringEntity stringEntity;
            // stringEntity=new StringEntity(stingGson);
            MyHttpConnection.postHeaderWithoutJsonEntity(
                    HomeScreenActivity.this,
                    GlobalCommonValues.GET_ALL_MESSAGE,
                    messagesAllResponseHandler,
                                                HomeScreenActivity.this.getString(R.string.private_key),
                    save_State.getPublicKey(HomeScreenActivity.this));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //Async task to get all messages sent from the sender
    AsyncHttpResponseHandler messagesAllResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("HomeTabFragment_MessageList", "OnSuccess",
                            response.toString());
                    getResponseAllMessages(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("HomeTabFragment_MessageList", "OnFailure",
                        response);
        }

        @Override
        public void onFinish() {
        }
    };

    /**
     * Method to handle response we got from the server for the unread messages received by us
     * @param response
     */
    private void getResponseAllMessages(String response) {
        try {
            if (!TextUtils.isEmpty(response)
                    && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                GetAllMessageResponseBean get_Response = gson.fromJson(
                        response, GetAllMessageResponseBean.class);
                if (get_Response.response_code
                        .equals(GlobalCommonValues.SUCCESS_CODE)) {
                    ArrayList<SendMessageReponseDataBean> data = get_Response
                            .getData();
                    ArrayList<SendMessageReponseDataBean> listMessageContacts = new ArrayList<SendMessageReponseDataBean>();

                    if ((data != null && data.isEmpty()) || data == null) {
                        SendMessageReponseDataBean dataBean = new SendMessageReponseDataBean();
                        dataBean.setMessage("No Message Found");
                        listMessageContacts.add(dataBean);
                    } else if (data != null && data.size() > 0) {
                        listMessageContacts = data;
                        ArrayList<SendMessageReponseDataBean> listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
                        SendMessageReponseDataBean objResponseDataBean = null;
                        for (int i = 0; i < listMessageContacts.size(); i++) {
                            objResponseDataBean = new SendMessageReponseDataBean();
                            objResponseDataBean.setMessage_id(data.get(i)
                                    .getMessage_id());
                            objResponseDataBean.setFrom_user_id(data.get(i)
                                    .getFrom_user_id());
                            objResponseDataBean.setFrom_user_phone(data.get(i)
                                    .getFrom_user_phone());
                            objResponseDataBean.setTo_user_id(data.get(i)
                                    .getTo_user_id());
                            objResponseDataBean.setMessage(data.get(i)
                                    .getMessage());
                            objResponseDataBean.setStatus(data.get(i)
                                    .getStatus());
                            objResponseDataBean.setDatatime(data.get(i)
                                    .getDatatime());
                            objResponseDataBean.setName(data.get(i).getName());
                            listMessageDetails.add(objResponseDataBean);
                        }
                        DBQuery.insertMessage(HomeScreenActivity.this,
                                listMessageDetails);
                        setUnreadMessageCount();
                    }
                } else if ((get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE))
                        || (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_1))
                        || (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_2))) {
                }
            } else {
                // ShowDialog.alert(HomeScreenActivity.this,
                // "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homescreen);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(GlobalCommonValues.IS_CREATE_CHAT_BUTTON)) {
            isCreateChatButton = getIntent().getExtras().getBoolean(GlobalCommonValues.IS_CREATE_CHAT_BUTTON);
        }

        if(save_State == null){
            save_State = new SharedPreference();
        }

        // Navigate to create call button fragment screen in case new user comes for first time on the home screen
        if(isCreateChatButton){
            setFragment(new CreateCallButtonFragment());
        }
        // open registration screen in case of unregistered users
        if(!save_State.isRegistered(HomeScreenActivity.this)){
            setFragment(new UserRegistrationFragment());
        }

        // Initialize facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(HomeScreenActivity.this);
        FacebookSdk.setAutoLogAppEventsEnabled(true);

        // Initialize the loader with a special ID and the defined callbacks from above
        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), contactsLoader);

        GlobalConfig_Methods.setHomeGridViewColumns(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        idInitialization();

        // check if device's gps is enabled or not
       /* if(GlobalConfig_Methods.isGPSEnabled(HomeScreenActivity.this)){
            if(!GlobalConfig_Methods.isServiceRunning(HomeScreenActivity.this, "com.tnc.service.SendLocationService"))
                startService(new Intent(HomeScreenActivity.this,SendLocationService.class));
        }*/

        if (!MainBaseActivity.isFromMain && !save_State.isResumed(HomeScreenActivity.this)) {

            //			Toast.makeText(HomeScreenActivity.this, save_State.isResumed(HomeScreenActivity.this)+"Oncreate To Navigate",1000).show();
            if (GCMIntentService.isNotificationPushDisplayDialogActivity) {
                NotificationsFragment objNotificationsFrgament = new NotificationsFragment();
                objNotificationsFrgament.newInstance(
                        HomeScreenActivity.this, null);
                setFragment(objNotificationsFrgament);
                NotificationPushDisplayDialogActivity.isNotificationPushDisplayDialogActivity = false;
                // GlobalCommonValues.pushNotificationString=null;
                MainBaseActivity.isFromMain = true;
            }
            if (GCMIntentService.isMessagePushDisplayDialogActivity) {
                ChatFragment objMessageFrgament = new ChatFragment();
                objMessageFrgament.newInstance(iNotifySelectTab);
                setFragment(objMessageFrgament);
                MessagePushDisplayDialogActivity.isMessagePushDisplayDialogActivity = false;
                MainBaseActivity.isFromMain = true;
            }
            save_State.setResumed(HomeScreenActivity.this, true);
        }
    }

    // display premium feature dialog
    private void displayPremiumFeatureDialog(){
        ////System.out.println("calendar new time"+calendar.getTime().toString());
        Date storedDate = GlobalConfig_Methods.getDateFromStringDate(save_State.getAlarmTime(this));
        Date currentDate = GlobalConfig_Methods.getDateFromStringDate(Calendar.getInstance().getTime().toString());
        //System.out.println("stored Date"+  storedDate+ "current Date "+ currentDate);
        if(storedDate !=null && currentDate!=null && !save_State.isDialogDisplay(this) && currentDate.compareTo(storedDate)>0) {
            showPremiumFeatureDialog();
        }
    }

    /**
     * Method to send contacts to the server
     */
    public void sendContactsToServer(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new StartServiceClass().execute();
            }
        }, 100);
    }


    /**
     * async task to call the service to send the contacts to the server
     */
    class StartServiceClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent mainIntent = new Intent(HomeScreenActivity.this,GetContactService.class);
            startService(mainIntent);
        }
    }


    /**
     * async task to set the display contact list being fetched from the server
     */
    class SetContactsToDisplay extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // Set the contact list to display
            if(mResultantCursor!=null && !mResultantCursor.isClosed())
                GlobalConfig_Methods.setContactListToDislpay(mResultantCursor);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void setGCMRegID() {
        try {
            GCMRegistrar.register(this, SENDER_ID);
            save_State.setGCMRegistrationId(HomeScreenActivity.this,
                    GCMRegistrar.getRegistrationId(HomeScreenActivity.this));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * initialization of views
     */
    private void idInitialization() {
        save_State = new SharedPreference();
        progress = new TransparentProgressDialog(HomeScreenActivity.this,
                R.drawable.customspinner);
        HomeScreenActivity.flBackArrow = (FrameLayout) findViewById(R.id.flBackArrow);
        HomeScreenActivity.flSettingButton = (FrameLayout) findViewById(R.id.flSettingButton);
        HomeScreenActivity.flInformationButton = (FrameLayout) findViewById(R.id.flInformationButton);
        HomeScreenActivity.btnBack = (Button) findViewById(R.id.btnBack);
        HomeScreenActivity.btnSettings = (Button) findViewById(R.id.btnSettings);
        HomeScreenActivity.btnInformation = (Button) findViewById(R.id.btnInformation);
        HomeScreenActivity.btnHome = (Button) findViewById(R.id.btnHome);
        HomeScreenActivity.flBackArrow.setVisibility(View.GONE);
        //Phase-4
        HomeScreenActivity.flSettingButton.setVisibility(View.GONE);
        HomeScreenActivity.flInformationButton.setVisibility(View.GONE);

        HomeScreenActivity.btnSettings.setVisibility(View.GONE);
        HomeScreenActivity.btnInformation.setVisibility(View.GONE);

        tvTitle = (TextView) findViewById(R.id.tvTitle);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        CustomFonts.setFontOfTextView(HomeScreenActivity.this, tvTitle,
                "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(HomeScreenActivity.this, tvTitle,
//				"fonts/Helvetica-Bold.otf");

        // CustomFonts.setFontOfTextView(HomeScreenActivity.this, tvTitle,
        // "fonts/StencilStd.otf");
        HomeScreenActivity.btnBack.setOnClickListener(this);
        HomeScreenActivity.btnSettings.setOnClickListener(this);
        HomeScreenActivity.btnInformation.setOnClickListener(this);
        HomeScreenActivity.btnHome.setOnClickListener(this);
        mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();

        //Home Tab
        TabHost.TabSpec spec = mTabHost.newTabSpec(AppTabsConstants.HOME);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.home,HomeScreenActivity.this.getResources().getString(R.string.tabnameHome)));
        mTabHost.addTab(spec);
        mTabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);

        //Settings Tab
        spec = mTabHost.newTabSpec(AppTabsConstants.SETTINGS);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.settings,HomeScreenActivity.this.getResources().getString(R.string.tabnameSettings)));
        mTabHost.addTab(spec);

        //Contact Tab
        spec = mTabHost.newTabSpec(AppTabsConstants.CONTACT);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.contact,HomeScreenActivity.this.getResources().getString(R.string.tabnameContacts)));
        mTabHost.addTab(spec);

        //DialPad Tab
        spec = mTabHost.newTabSpec(AppTabsConstants.DIALPAD);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.bottom_menu_call_icon,HomeScreenActivity.this.getResources().getString(R.string.tabnameCalls)));
        mTabHost.addTab(spec);

        //Message Tab
        spec = mTabHost.newTabSpec(AppTabsConstants.MESSAGE);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.message,HomeScreenActivity.this.getResources().getString(R.string.tabnameMessages)));
        mTabHost.addTab(spec);

        //Info Tab
        spec = mTabHost.newTabSpec(AppTabsConstants.INFO);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.info,HomeScreenActivity.this.getResources().getString(R.string.tabnameInfo)));
        mTabHost.addTab(spec);

        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setCurrentTab(0);
        setSelectedTab();
        // }
    }

    @SuppressLint("InflateParams")
    private View createTabView(final int id, String tabName) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab, null);
        tabImageView = (ImageView) view.findViewById(R.id.tab_image);
        tabTvName = (TextView) view.findViewById(R.id.tvTabName);
        //		tvUnreadMessageCount = (TextView) view
        //				.findViewById(R.id.tvUnreadMessageCount);
        // llTabSelector=(LinearLayout)view.findViewById(R.id.llTabSelector);
        // llTabSelector.setVisibility(View.INVISIBLE);
        CustomFonts.setFontOfTextView(HomeScreenActivity.this, tabTvName,
                "fonts/Roboto-Regular_1.ttf");
        tabImageView.setImageDrawable(getResources().getDrawable(id));
        tabTvName.setText(tabName);
        if (tabName.equals(HomeScreenActivity.this.getResources().getString(R.string.tabnameMessages))) {
            tvUnreadMessageCount = (TextView) view
                    .findViewById(R.id.tvUnreadMessageCount);
            tvUnreadMessageCount.setVisibility(View.GONE);
            //			save_State = new SharedPreference();
            setUnreadMessageCount();
        }
        else{
            if(tvUnreadMessageCount!=null)
                tvUnreadMessageCount.setVisibility(View.GONE);
        }
        if (tabName.equals(HomeScreenActivity.this.getResources().getString(R.string.tabnameCalls))) {
            tvUnreadCallCount = (TextView) view
                    .findViewById(R.id.tvUnreadMessageCount);
            tvUnreadCallCount.setVisibility(View.GONE);
            //			save_State = new SharedPreference();
            setUnreadMessageCount();
        }
        else{
            if(tvUnreadCallCount!=null)
                tvUnreadCallCount.setVisibility(View.GONE);
        }


        return view;
    }
    /**
     * Method to get unread notifications count
     * @return
     */
    public int getUnreadNotificationCount() {
        int count = -1;
        try {
            count = DBQuery.getUnreadNotificationCount(HomeScreenActivity.this);
        } catch (Exception e) {
            e.getMessage();
            count = -1;
        }
        return count;
    }
    /**
     * Method to get unread messages count
     * @return
     */
    public void setUnreadMessageCount() {
        int count = -1;
        try {
            // get unread message count
            count = DBQuery.getUnreadMessageCount(HomeScreenActivity.this);
        } catch (Exception e) {
            e.getMessage();
        }
        if (tvUnreadMessageCount != null) {
            if (count > 0) {
                tvUnreadMessageCount.setVisibility(View.VISIBLE);
                tvUnreadMessageCount.setText(String.valueOf(count));

                if(currentHomeFragment!=null && currentHomeFragment instanceof HomeTabFragment){

                    // Change done on 11-1-2017

                    //currentHomeFragment.refreshTileAdapter();

                    if(((HomeTabFragment)currentHomeFragment).adapterTiles!=null &&
                            ((HomeTabFragment)currentHomeFragment).adapterTiles.getCount()>0){
                        ((HomeTabFragment)currentHomeFragment).adapterTiles.notifyDataSetChanged();
                    }
                }
            } else {
                tvUnreadMessageCount.setVisibility(View.GONE);
            }
        }

        // get unread call log count
        count = -1;
        try {
            // get unread message count
            count = DBQuery.getUnreadCallCount(HomeScreenActivity.this, "");
        } catch (Exception e) {
            e.getMessage();
        }
        if (tvUnreadCallCount != null) {
            if (count > 0) {
                tvUnreadCallCount.setVisibility(View.VISIBLE);
                tvUnreadCallCount.setText(String.valueOf(count));
            } else {
                tvUnreadCallCount.setVisibility(View.GONE);
            }
        }

        //update unread call log count on tiles of HomeScreen
        if(getVisibleFragment()!=null && getVisibleFragment() instanceof HomeTabFragment){
            if(((HomeTabFragment)getVisibleFragment()).adapterTiles!=null &&
                    ((HomeTabFragment)getVisibleFragment()).adapterTiles.getCount()>0){
                ((HomeTabFragment)getVisibleFragment()).adapterTiles.notifyDataSetChanged();
            }
        }

    }
    /**
     * set the current tab
     *
     * @param val
     */
    public void setCurrentTab(int val) {
        mTabHost.setCurrentTab(val);
    }

    boolean isAlreadyHomeSelected = false;

    /* Comes here when user switch tab, or we do programmatically */
    FragmentTabHost.OnTabChangeListener listener = new FragmentTabHost.OnTabChangeListener() {

        public void onTabChanged(String tabId) {

            HomeTabFragment objHomeFragment;

            MainBaseActivity.objTileEdit=null;
			/* Set current tab.. */
            mCurrentTab = tabId;
            if (tabId.equals(AppTabsConstants.HOME)) {
                mCurrentTab_pos = 0;
                mCurrentTab = AppTabsConstants.HOME;
                objHomeFragment = new HomeTabFragment();
                objHomeFragment.newInstance(HomeScreenActivity.this);
                if (!isAlreadyHomeSelected) {
                    isAlreadyHomeSelected = true;
                    pushFragments(tabId, objHomeFragment);
                }
            } else if (tabId.equals(AppTabsConstants.MESSAGE)) {
                MainBaseActivity.isTileCreated = false;
                mCurrentTab_pos = 3;
                mCurrentTab = AppTabsConstants.MESSAGE;
                mTabHost.setCurrentTab(0);

                if (save_State.isRegistered(HomeScreenActivity.this)) {
                    ChatFragment objChatFragment = new ChatFragment();
                    objChatFragment.newInstance(iNotifySelectTab);
                    setFragment(objChatFragment);
                    disableRefreshing();
                }

//                if (save_State.isRegistered(HomeScreenActivity.this)) {
//                    MessageListFragment objChatFragment = new MessageListFragment();
//                    objChatFragment.newInstance(iNotifySelectTab);
//                    setFragment(objChatFragment);
//                    disableRefreshing();
//                }
                else if (!save_State.isRegistered(HomeScreenActivity.this)) {
                    RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance(
                            "",
                            HomeScreenActivity.this,
                            Html.fromHtml(
                                    "Please create profile <br>"
                                            + "to use this feature").toString(),
                            "", null, iNotifySelectTab);
                    dialog.show(getSupportFragmentManager(), "test");
                    setSelectedTab();
                }
            }
            else if(tabId.equals(AppTabsConstants.SETTINGS))
            {
                SettingsFragment objSettingFragment = new SettingsFragment();
                objSettingFragment.newInstance(iNotifySelectTab);
                setFragment(objSettingFragment);
                disableRefreshing();
                MainBaseActivity.isTileCreated = false;
            }
            else if(tabId.equals(AppTabsConstants.INFO))
            {
                startActivity(new Intent(HomeScreenActivity.this,InfoActivity.class));
                disableRefreshing();
                MainBaseActivity.isTileCreated = false;
            }
            else if(tabId.equals(AppTabsConstants.DIALPAD))
            {
                // Go to Call Details Screen
                CallDetailsFragment mCallDetailsFragment = new CallDetailsFragment();
                mCallDetailsFragment.newInstance(HomeScreenActivity.this,iNotifySelectTab);
                setFragment(mCallDetailsFragment);

                /*DialPad objDialPad=new DialPad();
                objDialPad.newInstance(HomeScreenActivity.this,iNotifySelectTab);
                ((HomeScreenActivity)HomeScreenActivity.this).setFragment(objDialPad);*/
            }
            else if(tabId.equals(AppTabsConstants.CONTACT))
            {
                ContactListTabFragment objContactFragment = new ContactListTabFragment();
                objContactFragment.newInstance(iNotifySelectTab);
                setFragment(objContactFragment);
                disableRefreshing();
                //				startActivity(new Intent(HomeScreenActivity.this,ContactListActivity.class));
            }
        }
    };
    /**
     * set the selected tab
     */
    public void setSelectedTab() {
        for (int i = 0; i < mTabHost.getTabWidget().getTabCount(); i++) {
            if (i == 0) {
                int height = (int) HomeScreenActivity.this.getResources()
                        .getDimension(R.dimen.heightTopBar);// heightSelectedTab
                mTabHost.getTabWidget().getChildAt(i)
                        .findViewById(R.id.mainview_tab).getLayoutParams().height = height;
                mTabHost.getTabWidget().getChildAt(i)
                        .findViewById(R.id.mainview_tab)
                        .setBackgroundResource(R.drawable.tab_selected_bg);
                // llTabSelector.setVisibility(View.VISIBLE);
            } else {
                int height = (int) HomeScreenActivity.this.getResources()
                        .getDimension(R.dimen.heightTopBar);
                mTabHost.getTabWidget().getChildAt(i)
                        .findViewById(R.id.mainview_tab).getLayoutParams().height = height;
                mTabHost.getTabWidget().getChildAt(i)
                        .findViewById(R.id.mainview_tab)
                        .setBackgroundResource(R.drawable.tab_unselected_bg);
            }
        }
        mTabHost.getTabWidget().getLayoutParams().height = (int) HomeScreenActivity.this
                .getResources().getDimension(R.dimen.heightTopBar);// heightSelectedTab
    }

    /**
     * set fragment on a tab screen
     *
     * @param tag
     * @param fragment
     */
    public void pushFragments(String tag, HomeTabFragment fragment) {
        //this.currentHomeFragment = fragment;
        this.currentHomeFragment = (HomeTabFragment)fragment;
        manager = getSupportFragmentManager();
        ft = manager.beginTransaction();
        ft.replace(R.id.realtabcontent, fragment);
        ft.addToBackStack(fragment.toString().substring(0,
                fragment.toString().indexOf("ent") + 3));
        ft.commit();
    }

    /**
     * set fragment on home screen,not on the tab screen
     *
     * @param currentFragment
     */
    public void setFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, currentFragment);
        fragmentTransaction.addToBackStack(currentFragment.toString());//.substring(0, currentFragment.toString().indexOf("ent") + 3));
        fragmentTransaction.commit();
    }

    /**
     * top bar back button functionality
     *
     * @param fragmentName
     */
    public static void setBackButtonFunctionality(String fragmentName) {
        if (fragmentName.contains("cloudbackup")) {
            manager.popBackStack();
        } else if (fragmentName.contains("settingfragment")) {
            manager.popBackStack();
            HomeScreenActivity.flBackArrow.setVisibility(View.GONE);
            HomeScreenActivity.flSettingButton.setVisibility(View.VISIBLE);
        }

        else if (fragmentName.contains("chatfragment")) {
            manager.popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * interface to notify the dismiss of dialog and handle events if required
     * any
     */
    protected AlertCallAction alertBack = new AlertCallAction() {
        @Override
        public void isAlert(boolean isOkClicked) {
            dialogExit.dismiss();
            if (isOkClicked) {
                if (GlobalCommonValues.listBBContacts != null
                        && !GlobalCommonValues.listBBContacts.isEmpty()) {
                    GlobalCommonValues.listBBContacts.clear();
                    GlobalCommonValues.listBBContacts = null;
                }
                HomeScreenActivity.flBackArrow = null;
                HomeScreenActivity.flSettingButton = null;
                HomeScreenActivity.flInformationButton = null;
                HomeScreenActivity.btnBack = null;
                HomeScreenActivity.btnSettings = null;
                HomeScreenActivity.btnInformation = null;
                HomeScreenActivity.btnHome = null;
                HomeScreenActivity.manager = null;
                GlobalCommonValues._Contacimage = null;
                GlobalCommonValues.isBackedupSuccessful = false;
                MainBaseActivity.isHomeScreenExit = true;
                finish();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            try {
                if (fragmentManager != null
                        && fragmentManager.getBackStackEntryCount() > 1) {
                    int num = fragmentManager.getBackStackEntryCount();
                    BackStackEntry backStackEntry = fragmentManager
                            .getBackStackEntryAt(num - 1);
                    String fragmentSettings=fragmentManager.getFragments().get(1).toString();

                    if(backStackEntry.getName().contains("RegistrationFeatures") && fragmentSettings.contains("SettingsFragment")){
                        fragmentManager.popBackStack();
                    }
                    else if(backStackEntry.getName().contains("RegistrationFeatures") && !fragmentSettings.contains("SettingsFragment")){
                        startActivity(new Intent(HomeScreenActivity.this,HomeScreenActivity.class));
                        finish();
                    }
                    else
                    {
                        if (backStackEntry.getName().contains("TilePreview")) {
                            MainBaseActivity._bitmap = null;
                        }
                        if (backStackEntry.getName().contains("HomeTabFragment")) {
                            enableRefreshing();
                        }

                        if (backStackEntry.getName().contains("SendSMSFullScreenDialogFragment")||
                                backStackEntry.getName().contains("TnCUsers_NotifyFragmentRegistration") ||
                                backStackEntry.getName().contains("VerifyingRegistrationFragment") ||
                                backStackEntry.getName().contains("CheckReturningUserFragment") ||
                                backStackEntry.getName().contains("PremiumFeaturesFragment")) {
                        }

                        else if (backStackEntry.getName().contains(
                                "BackupListFragment")
                                && !MainBaseActivity.isBackButtonToDisplay) {

                        } else {
                            fragmentManager.popBackStack();
                            Fragment fragment = getVisibleFragment();

                            if (fragment instanceof SettingsFragment) {
                                iNotifySettings.yes();
                            }

                            else if (fragment instanceof CloudBackupFragment) {
                                performINotifyCloudBackup();
                            }
                        }
                    }
                } else if (manager != null
                        && manager.getBackStackEntryCount() > 1) {
                    manager.popBackStack();

                } else {
                    dialogExit = new SMSChargeDialog();
                    dialogExit.newInstance("", HomeScreenActivity.this,
                            "Are you sure you want to exit?", "", alertBack);
                    dialogExit.setCancelable(false);
                    dialogExit.show(getSupportFragmentManager(), "test");
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return false;
        }

		/*else if ((keyCode == KeyEvent.KEYCODE_HOME)) {
			if(getVisibleFragment() instanceof CheckReturningUserFragment){
				Toast.makeText(getApplicationContext(), "HomeScreenActivity",1000).show();
				if(!save_State.getPassCode(HomeScreenActivity.this).trim().equals("")){
					CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
					objCancelRegistration.setPasscode(save_State.getPassCode(HomeScreenActivity.this));
					cancelRegistration(objCancelRegistration);
				}
				GlobalConfig_Methods.clearRegsitrationPreferences(HomeScreenActivity.this);
			}
			Toast.makeText(getApplicationContext(), "HomeScreenActivity",1000).show();
		}*/
        return false;
    }

    /**
     * get current fragment
     *
     * @return
     */
    public Fragment getVisibleFragment() {
        try {
            FragmentManager fragmentManager = HomeScreenActivity.this.getSupportFragmentManager();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //FaceBook callback
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google login callback to start further process on receiving data on successful login
        GoogleClass.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);//10000,-1,0(IN CASE OF SUCCESS) & 10000,0,2(IN CASE OF ERROR-error while retrieving information from the server)
        if(fragmentManager!=null){
            try {
            int num=fragmentManager.getBackStackEntryCount();
            Fragment fragmentVerification = fragmentManager.getFragments().get(num-1);
            if(fragmentVerification  instanceof PremiumFeaturesFragment){
                ((PremiumFeaturesFragment)fragmentVerification).handleInAppresponse(requestCode, resultCode, data);
            }
            }catch (Exception e){
                e.getMessage();
            }
        }

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
        public void onReceive(Context arg0, Intent intent) {

            if(intent.getAction().equalsIgnoreCase("com.chatstasy.displaypopup")){
                JSONObject mObject;
                /**
                 * Display Dialog with Contents
                 * @param message
                 */
                try{
                    String mUserName = "";
                    String from_user_id = "";
                    if(intent.getExtras().containsKey("message")){

                        String messageToDisplay = intent.getExtras().getString("message");

                        /*if(intent.getExtras().containsKey("from_user_id")){
                            from_user_id = intent.getExtras().getString("from_user_id");
                            if(from_user_id!=null && !from_user_id.trim().equalsIgnoreCase("")){
                                // check in Tiles table for existence of contacts
                                mUserName = DBQuery.getContactTileNameFromBBID(HomeScreenActivity.this,
                                        Integer.parseInt(from_user_id), true);

                                if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                                    // check in BBContacts table for existence of contacts
                                    mUserName = DBQuery.getContactTileNameFromBBID(HomeScreenActivity.this,
                                            Integer.parseInt(from_user_id), false);
                                }
                            }
                        }*/


                        if(messageToDisplay.toLowerCase().contains("emergency call from")){
                            // In case of emergency call notification

                            /*String phoneNumber = "";

                            if(intent.getExtras().containsKey("from_user_phone")){
                                phoneNumber = intent.getExtras().getString("from_user_phone");

                                if((phoneNumber!=null) && !(phoneNumber.trim().equalsIgnoreCase("")) &&
                                        (mUserName == null || mUserName.trim().equalsIgnoreCase(""))){
                                    mUserName = GlobalConfig_Methods.getContactName(HomeScreenActivity.this, phoneNumber);
                                }
                                if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                                    messageToDisplay = messageToDisplay.substring(messageToDisplay.indexOf("from")+3, messageToDisplay.length());
                                    //(messageToDisplay.indexOf("from")+3,8));
                                }
                            }

                            if(mUserName!=null && !mUserName.trim().equalsIgnoreCase("")){
                                messageToDisplay = messageToDisplay.substring(0, messageToDisplay.indexOf("from")+3) + " " +
                                        mUserName + ".";
                            }*/

                        }else{
                            // In case of emergency contact notification

                            if(intent.getExtras().containsKey("from_user_id")){
                                from_user_id = intent.getExtras().getString("from_user_id");
                                if(from_user_id!=null && !from_user_id.trim().equalsIgnoreCase("")){
                                    // check in Tiles table for existence of contacts
                                    mUserName = DBQuery.getContactTileNameFromBBID(HomeScreenActivity.this,
                                            Integer.parseInt(from_user_id), true);

                                    if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                                        // check in BBContacts table for existence of contacts
                                        mUserName = DBQuery.getContactTileNameFromBBID(HomeScreenActivity.this,
                                                Integer.parseInt(from_user_id), false);
                                    }
                                }
                            }

                            if(mUserName!=null && !mUserName.trim().equalsIgnoreCase("")){
                                messageToDisplay = messageToDisplay.substring(0, messageToDisplay.indexOf("AWARE THAT")+10) + " " +
                                        mUserName + " " + messageToDisplay.substring(messageToDisplay.indexOf("HAD REQUESTED"), messageToDisplay.length());
                            }
                        }

                        if(messageToDisplay!=null && !messageToDisplay.trim().equalsIgnoreCase("")){
                            ImageRequestDialog mDialogEmergencyNotification = new ImageRequestDialog();
                            mDialogEmergencyNotification.newInstance("", HomeScreenActivity.this, messageToDisplay, "", null);
                            mDialogEmergencyNotification.setCancelable(false);
                            mDialogEmergencyNotification.show(getSupportFragmentManager(), "Test");
                        }
                    }
                }catch(Exception e){
                    e.getMessage();
                }
            }else if(intent.getAction().equalsIgnoreCase("com.tapnchat.refreshcalllogcount")){
                //update unread call log count
                /*if(getVisibleFragment()!=null && getVisibleFragment() instanceof HomeTabFragment){
                    if(((HomeTabFragment)getVisibleFragment()).adapterTiles!=null &&
                            ((HomeTabFragment)getVisibleFragment()).adapterTiles.getCount()>0){
                        ((HomeTabFragment)getVisibleFragment()).adapterTiles.notifyDataSetChanged();
                    }
                }*/
                try {
                    setUnreadMessageCount();
                }catch (Exception e){
                    e.getMessage();
                }
            }else{
                try {
                    if(fragmentManager!=null){
                        int num=fragmentManager.getBackStackEntryCount();
                        BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(num-1);
                        if(backStackEntry.getName().contains("ContactListFragment") ||
                                backStackEntry.getName().contains("ContactListTabFragment")){
                            if(backStackEntry.getName().contains("ContactListFragment")){
                                Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size()-1); //get(num-1);
                                if(fragment instanceof ContactListFragment){
                                    ((ContactListFragment)fragment).refreshContactList("contactlist");
                                    save_State.setIS_FetchingContacts(getApplicationContext(), false);
                                }
                            }else if(backStackEntry.getName().contains("ContactListTabFragment")){
                                Fragment fragment = fragmentManager.getFragments().get(num-1);
                                if(fragment instanceof ContactListTabFragment){
                                    ((ContactListTabFragment)fragment).refreshContactList("contactlist");
                                    save_State.setIS_FetchingContacts(getApplicationContext(), false);
                                }
                            }
                        }
                    }else{
                        if(getVisibleFragment() instanceof HomeTabFragment){
                            if(((HomeTabFragment)getVisibleFragment()).dialogProgress!=null && ((HomeTabFragment)getVisibleFragment()).dialogProgress.isShowing()){
                                ((HomeTabFragment)getVisibleFragment()).dialogProgress.dismiss();
                            }

                            // refresh tiles adapter
                            if(((HomeTabFragment)getVisibleFragment()).adapterTiles!=null && ((HomeTabFragment)getVisibleFragment()).adapterTiles.getCount() > 0){
                                ((HomeTabFragment)getVisibleFragment()).adapterTiles.notifyDataSetChanged();
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
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
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
                    CursorLoader cursorLoader = new CursorLoader(HomeScreenActivity.this,
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
                    mResultantCursor = cursor;

                    // Iterate through all the record in a cursor
                    if(mResultantCursor!=null && mResultantCursor.getCount()>0){
                        mResultantCursor.moveToFirst();
                        GlobalCommonValues.listContacts = new ArrayList<ContactDetailsBean>();

                        if(mResultantCursor!=null && !mResultantCursor.isClosed())
                            new SetContactsToDisplay().execute("");
                        //				getLoaderManager().getLoader(CONTACT_LOADER_ID).stopLoading();
				/*handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						new SetContactsToDisplay().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
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

    protected void onDestroy() {
        super.onDestroy();

        AppEventsLogger.deactivateApp(HomeScreenActivity.this);

        /*if(GlobalConfig_Methods.isServiceRunning(HomeScreenActivity.this, "com.tnc.service.SendLocationService"))
            stopService(new Intent(HomeScreenActivity.this,SendLocationService.class));*/

    }

    //
    //	protected void onDestroy() {
    //		super.onDestroy();
    //		if((getVisibleFragment()!=null) && ((getVisibleFragment() instanceof CheckReturningUserFragment) ||
    //				(getVisibleFragment() instanceof VerifyingRegistrationFragment))){
    //			Toast.makeText(getApplicationContext(), "HomeScreenActivity",1000).show();
    //			if(!save_State.getPassCode(HomeScreenActivity.this).trim().equals("")){
    //				CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
    //				objCancelRegistration.setPasscode(save_State.getPassCode(HomeScreenActivity.this));
    //				cancelRegistration(objCancelRegistration);
    //			}
    //			GlobalConfig_Methods.clearRegsitrationPreferences(HomeScreenActivity.this);
    //		}
    //
    //	};

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }

    public void getFragmentInstance(Fragment fragment){
        mFragmentInstance = fragment;
    }


    private void getNotificationList(NotificationBean notificationBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(notificationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(this,
                    GlobalCommonValues.NOTIFICATION_LIST, stringEntity,
                    notificationResponseHandler,
                    getString(R.string.private_key),
                    save_State.getPublicKey(this));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to handle request made for getting the  notifications from the server if any
    AsyncHttpResponseHandler notificationResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("HomeTabFragment_Notification", "OnSuccess",
                            response.toString());
                    getResponseNotification(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("HomeTabFragment_Notification", "OnFailure",
                        response);
        }

        @Override
        public void onFinish() {
        }
    };

    private void getResponseNotification(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>")
                    || response.contains("php")) {
                response2 = response.substring(
                        response.indexOf("response_code") - 2,
                        response.length());
            } else {
                response2 = response;
            }
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                setAlarmTime(response);

            }
            }catch (Exception e) {
                e.printStackTrace();

            }
        }

    /**
     * Method to set alarm time to display list of premium feature popup
     * @param response
     */
    public void setAlarmTime(String response){
        try {
            NotificationResponseTimer notificationResponseTimer = gson.fromJson(response,NotificationResponseTimer.class);
            //System.out.println("save notification timer value"+ notificationResponseTimer.getTimer().getTimer());
            //System.out.println("save state alarm time"+ save_State.getAlarmTime(this));
            //System.out.println("save alarm value"+save_State.getResponseTimerValue(this));
            //System.out.println("save is dialog display"+save_State.isDialogDisplay(this));

            if(save_State.isDialogDisplay(this) && null != notificationResponseTimer.getTimer() && ! notificationResponseTimer.getTimer().getTimer().equalsIgnoreCase((save_State.getResponseTimerValue(this)))) {
                Calendar calendar = Calendar.getInstance();
                //System.out.println("calendar old time"+calendar.getTime().toString());
                calendar.add(Calendar.HOUR,Integer.parseInt(notificationResponseTimer.getTimer().getTimer()));
                //System.out.println("calendar new time"+calendar.getTime().toString());
                save_State.setAlarmTime(this,GlobalConfig_Methods.getDateFromStringDate(calendar.getTime().toString()).toString() );
                save_State.setResponseTimerValue(this,notificationResponseTimer.getTimer().getTimer());
                save_State.setDialogDisplay(this,false);
            }else{
                if(null != notificationResponseTimer.getTimer() && save_State.getAlarmTime(this).equalsIgnoreCase("")){
                    save_State.setDialogDisplay(this,true);
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    ////System.out.println("calendar old time"+calendar.getTime().toString());
                    calendar.add(Calendar.HOUR,Integer.parseInt(notificationResponseTimer.getTimer().getTimer()));
                    ////System.out.println("calendar new time"+calendar.getTime().toString());
                    save_State.setAlarmTime(this,GlobalConfig_Methods.getDateFromStringDate(calendar.getTime().toString()).toString() );
                    save_State.setResponseTimerValue(this,notificationResponseTimer.getTimer().getTimer());
                    showPremiumFeatureDialog();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void showPremiumFeatureDialog(){
            PremiumFeaturesDialog dialog = new PremiumFeaturesDialog();
            dialog.setCancelable(false);
            dialog.newInstance(this);
//            dialog.show(getSupportFragmentManager(), "test");
            save_State.setDialogDisplay(this,true);
            checkInternetConnectionNotification();
    }

    /**
     * check internet availability
     */
    private void checkInternetConnectionNotification() {
        if (NetworkConnection.isNetworkAvailable(this)) {
            notificationID = DBQuery.getNotificationsMaxCount(this);
            if (notificationID == -1) {
                notificationID = 0;
            }
            NotificationBean notificationBean = new NotificationBean(String.valueOf(notificationID));
            getNotificationList(notificationBean);
        } else {
        }
    }
}


