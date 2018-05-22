package com.tnc.common;

import java.util.ArrayList;

import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.bean.ContactDetailsBean;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Utitlity class to hold global values
 *  @author a3logics
 */

public class GlobalCommonValues {
    public static final String SUCCESS_CODE = "200";
    public static final String FAILURE_CODE = "400";
    public static final String FAILURE_CODE_1 = "401";
    public static final String FAILURE_CODE_2 = "402";
    public static final String FAILURE_CODE_3 = "403";
    public static final String FAILURE_CODE_4 = "404";
    public static final String FAILURE_CODE_5 = "405";
    public static final String FAILURE_CODE_6 = "406";
    public static final String FAILURE_CODE_7 = "407";
    public static final String FAILURE_CODE_501 = "501";
    public static final String FAILURE_CODE_502 = "502";
    public static final String FAILURE_CODE_601 = "601";
    public static final String SENDER_ID = "135039435317";// "135039435317";//"993696410830";
    public static final String SERVER_URL = null;
    public static ArrayList<BBContactsBean> listBBContacts = new ArrayList<BBContactsBean>();
    public static ArrayList<CloudRecoverBackupListingBean> listBackups = new ArrayList<CloudRecoverBackupListingBean>();
    public static boolean isBackedupSuccessful = false;
    public static String YOUTUBE_API_KEY = "AIzaSyAVJReNTUgVjFXUsV3QWuBm8G6-fKK0hFs";
    public static String INTRO_VIDEO_YOUTUBE_ID = "HsCTQZ1dJ_M"; //"Z6M-pCDc4wM";
    public static String REGISTRATION_VIDEO_YOUTUBE_ID = "kLODsd88928"; //"f4LIrs_vrwc";

    public static final String FONT_Helvetica_Bold = "fonts/Helvetica-Bold.otf";

    // For Staging Server
    //	public static String BASE_URL = "http://tap-n-chat.com/api/services/";

    // For Demo Server
    public static String BASE_URL = "http://demotest.a3logics.com/chatstasy-org/api/services/"; // "http://10.10.16.152/chatstasy-org/api/services/";
    /*"http://demotest.a3logics.com/chatstasy-org/api/services/";*/

    //For Production Server
    //	public static String BASE_URL = "https://tap-n-chat.org/api/services/";
    //Devanshu-2-9-2016
//	public static String BASE_URL = "https://chatstasy.org/api/services/";

    // For Demo Server
    //		public static String BASE_URL = "http://demotest.a3logics.com/tapnchat-org/api/services/";

    public static String CHECK_CONTACTS_DATABASE = BASE_URL
            + "checkContactList";
    public static String CREATE_TILE = BASE_URL + "saveTiles";
    public static String GET_CLIPARTS = BASE_URL + "getClipart";
    public static String CHECK_EXISTING_EMAIL = BASE_URL + "checkExistingEmail";
    public static String USER_REGISTRATION = BASE_URL + "registration";
    public static String NOTIFICATION_LIST = BASE_URL + "getNotificationList";
    public static String UPDATE_NOTIFICATION_STATUS = BASE_URL
            + "updateNotificationStatus";
    public static String NOTIFICATION_CHECK_IMAGE_REQUEST_STATUS = BASE_URL
            + "checkImageRequest";
    public static String UPLOAD_REQUESTED_IMAGE = BASE_URL
            + "uploadRequestedImage";
    public static String IMAGE_REQUEST = BASE_URL + "imageRequest";
    public static String GET_ALL_BACKUP = BASE_URL + "getAllBackupList";
    public static String UPLOAD_BACKUP = BASE_URL + "uploadBackup";
    public static String GET_ALL_MESSAGE = BASE_URL + "getAllMessage";
    public static String SEND_MESSAGE = BASE_URL + "sendMessage";
    public static String UPDATE_MESSAGE_STATUS = BASE_URL
            + "updateMessageStatusFrom";
    public static String GET_MESSAGE = BASE_URL + "getMessage";
    public static String GET_BBID = BASE_URL + "getBbid";
    public static String SET_DEFAULT_IMAGE = BASE_URL + "setDefaultImage";
    public static String CANCEL_REGISTRATION = BASE_URL + "cancelRegistration";
    public static String VERIFY_REGISTRATION = BASE_URL + "checkIsVerified";

    public static String CHECKTNCUSER = BASE_URL + "checkTncUser";
    public static String SHARECONTACT = BASE_URL + "shareContact";
    public static String CHECKSHARECONTACT = BASE_URL + "checkShareContact";
    public static String UPDATESHARECONTACT = BASE_URL + "updateShareContact";
    public static String UPDATEUSERINFO = BASE_URL + "infoUpdate";
    public static String CHANGEMYUMBER = BASE_URL + "checkNumber";
    public static String NOTIFYMYFRIEND = BASE_URL + "notifyMyFriend";
    public static String CHECKNUMBER = BASE_URL + "checkNumber";
    public static String CONTACTUS = BASE_URL + "contactUs";
    public static String REEMAILCONFIRMATION = BASE_URL + "reEmailConfirmation";
    public static String GET_HOWTO = BASE_URL + "getHowTo";
    public static String GET_FAQ = BASE_URL + "getFaq";
    public static String RESEND_BACKUP_KEY = BASE_URL + "backupKey";
    public static String GET_PRIVACYPOLICY = BASE_URL + "privacyPolicy";
    public static String GET_TERMSOFUSAGE = BASE_URL + "termsUsages";
    public static String GET_CHILD_REGISTRATION = BASE_URL + "getChildRegistration";
    public static String PARENT_CONSENT_STATUS = BASE_URL + "parentConsentStatus";
    public static String check_returning_user = BASE_URL + "checkReturningUser";
    public static String CHECK_USER_ACTIVATION = BASE_URL + "checkUserActivation";
    public static String GET_DEFAULT_MESSAGES = BASE_URL + "getDefaultMessages";
    public static String GET_EMERGENCY_NUMBERS = BASE_URL + "getEmergencyContact";
    public static String UPDATE_PREMIUM_STATUS = BASE_URL + "updatePremiumStatus";
    public static String GET_COUNTRY_SMS_NUMBER = BASE_URL + "getCountrySmsNumber";
    public static String SEND_NOTIFICATION_TO_EMERGENCY_CONTACT = BASE_URL + "sendNotificationToEmergencyContact";

    //sets the bitmap of selected contact from contact list
    public static Bitmap _Contacimage = null;
    public static String TelephoneNumberTobeDisplayed = "";
    public static String TelephoneNumberRestoreTobeDisplayed = "";
    public static String pushNotificationString = null;
    public static Bitmap _bitmap = null;
    public static boolean isImageSelected = false;
    public static String selectedImagepath = "";
    public static String API_KEY_TWITTER="wOiBIYSpq9WW3tVLzvJvaUBde";
    public static String API_SECRET_TWITTER="wEMhC51Zs7RiX5EnvJa5YWI3i9SDsE9t1vS0cNv01ubKHhfgrB";
    public static String ACCESS_TOKEN_TWITTER="602728788-mUkWJPMjmwgmaCSZsX2WKvCnNmpLLfywWvDFJMcT";
    public static String ACCESS_TOKEN_SECRET_TWITTER="vwjfIDzwMaZExXMsCLlhwxM3YtUrVORPH7OHTEaEfUhmJ";

    public static ArrayList<String> listCountryCodes = new ArrayList<String>();
    public static ArrayList<String> listIDDCodes = new ArrayList<String>();
    public static ArrayList<ContactDetailsBean> listContacts = new
            ArrayList<ContactDetailsBean>();

    public static ArrayList<ContactDetailsBean> listContactsSendToServer = new
            ArrayList<ContactDetailsBean>();

    // GCM Data
    public static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";
    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    public static  String SUCCESS = "success";

    public static double mLatitude = 0.0;
    public static double mLongitude = 0.0;

    public static String CALL_STATE_DIALING    = "dialing";
    public static String CALL_STATE_INCOMING   = "incoming";
    public static String CALL_STATE_CONNECTED  = "connected";

    public static String PHONE_MODE_NORMAL     = "normal";
    public static String PHONE_MODE_SILENT     = "silent";
    public static String PHONE_MODE_VIBRATE    = "vibrate";
    public static String PHONE_MODE_AIRPLANE   = "airplane";

    //User Registration
    public static String USER_TYPE_EXISTING        = "existing";
    public static String USER_TYPE_NON_EXISTING    = "nonexisting";

    public static String MATCH_TYPE_PHONE_NUMBER         = "phoneno";
    public static String MATCH_TYPE_DEVICE               = "device";
    public static String MATCH_TYPE_BOTH                 = "both";
    public static String MATCH_TYPE_NONE                 = "none";

    public static final String ButtonTypeRecentCalls     = "0";
    public static final String ButtonTypeAll             = "all";
    public static final String ButtonTypeFriend          = "2";
    public static final String ButtonTypeFamily          = "3";
    public static final String ButtonTypeBusiness        = "4";

//    public static final String ButtonTypeFriend          = "1";
//    public static final String ButtonTypeFamily          = "2";
//    public static final String ButtonTypeBusiness        = "3";

    public static final String ButtonModecall     = "call";
    public static final String ButtonModeMessage  = "message";

    public static final String UPDATE_UI_STRING   = "updateUI";

    public static final String RecentCalls        = "Recent Calls";

    public static final String BUTTON_SHAPE_SQUARE= "square";
    public static final String BUTTON_SHAPE_CIRCLE= "circle";

    //1 - incoming, 2 - outgoing, 3 - missed
    public static final int CallTypeIncoming   = 1;
    public static final int CallTypeOutgoing   = 2;
    public static final int CallTypeMissed     = 3;

    //Days Constants
    public static final String DAY_TODAY        = "Today";
    public static final String DAY_YESTERDAY    = "Yesterday";


    public static String YES  = "yes";
    public static String NO   = "no";
    public static String NULL = "null";

    public static String HYPHEN   = "-";
    public static String RETRY    = "retry";
    public static String WARNING  = "warning";

    public static boolean YES_BOOLEAN = true;
    public static boolean NO_BOOLEAN  = false;


    //premium feature api
    public static String GET_PREMIUM_FEATURES = BASE_URL + "getFeatureimagewithcaption";

    public static String IS_CREATE_CHAT_BUTTON  = "create_chat_button";


    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by the
     * UI and the background service.
     *
     * @param context
     *            application's context.
     * @param message
     *            message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
