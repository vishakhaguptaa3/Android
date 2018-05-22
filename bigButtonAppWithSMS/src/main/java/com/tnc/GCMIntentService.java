package com.tnc;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.MessagePushReponseBean;
import com.tnc.webresponse.NotificationPushReponseBean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class GCMIntentService extends GCMBaseIntentService {
    static int pendingNotificationCode;
    public static boolean isNotificationPushDisplayDialogActivity = false;
    public static boolean isMessagePushDisplayDialogActivity = false;

    public GCMIntentService() {
        super(GlobalCommonValues.SENDER_ID);
    }

    private static final String TAG = "===GCMIntentService===";

    @Override
    protected void onRegistered(Context arg0, String registrationId) {
        try {
            Log.i(TAG, "Device registered: regId = " + registrationId);
        } catch (Exception e) {
            e.getMessage();
        }
        // Toast.makeText(getApplicationContext(), "register",
        // Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        try {
            Log.i(TAG, "unregistered = " + arg1);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        try {
            SharedPreference save_State=new SharedPreference();
            save_State.setResumed(context, false);
            GlobalCommonValues.pushNotificationString = null;
            //			MainBaseActivity.isAlreadyDisplaying=false;
            Log.i(TAG, "new message= ");
            String message = intent.getExtras().getString("message");
            Log.i(TAG, "new message= " + message);

            //Is some version of the contents updated like
            //cliparts,howto,faq,default messages,emergency numbers version
            // so to detect their update we maintain boolean values in the preferenece
            if(Uri.decode(message).toLowerCase().contains("cliparts") ||
                    Uri.decode(message).toLowerCase().contains("cliparts:") ||
                    Uri.decode(message).toLowerCase().contains("clipart") ||
                    Uri.decode(message).toLowerCase().contains("clipart:")){
                SharedPreference saveState = new SharedPreference();
                saveState.setIS_CLIPARTS_VERSION_UPDATED(context, true);
                GlobalConfig_Methods mObjCLiparts =
                        new GlobalConfig_Methods();

                mObjCLiparts.downloadClipArts(getApplicationContext());
                //				fetchClipartsImages();
            }else if(Uri.decode(message).toLowerCase().contains("faq:")){
                SharedPreference saveState = new SharedPreference();
                saveState.setIS_FAQ_VERSION_UPDATED(context, true);

            }else if(Uri.decode(message).toLowerCase().contains("how to:")){
                SharedPreference saveState = new SharedPreference();
                saveState.setIS_HOWTO_VERSION_UPDATED(context, true);

            }else if(Uri.decode(message).toLowerCase().contains("emergencycontact") ||
                    Uri.decode(message).toLowerCase().contains("emergency numbers:")){
                SharedPreference saveState = new SharedPreference();
                saveState.setIS_EMERGENCY_NUMBER_VERSION_UPDATED(context, true);

            }else if(Uri.decode(message).toLowerCase().contains("default messages:") ||
                    Uri.decode(message).toLowerCase().contains("defaultmessage")){
                SharedPreference saveState = new SharedPreference();
                saveState.setIS_DEFAULT_MESSAGES_VERSION_UPDATED(context, true);
            }
            /*//Make entry in call log database if it's an emergency call by another using by double tapping our tile on the homescreen.
            if(Uri.decode(message).toLowerCase().contains("message") &&
                    Uri.decode(message).toLowerCase().contains("emergency call from")){

                String mNumberCallLog = "";
                try{
                    mNumberCallLog = new JSONObject(message).getString("from_user_phone");
                }catch(Exception e){
                    e.getMessage();
                }
                if(mNumberCallLog!=null && !mNumberCallLog.trim().equalsIgnoreCase("")){
                    save_State.setIS_EMERGENCY_NUMBER_DIALLED(context, true);
                    GlobalConfig_Methods.insertCallLog(context, mNumberCallLog, GlobalCommonValues.CallTypeMissed);
                    save_State.setIS_EMERGENCY_NUMBER_DIALLED(context, false);
                    *//*Intent intentRefreshUnreadCallsCountOnHomeScreen = new Intent("com.tapnchat.phonecontactshomescreen");
                    sendBroadcast(intent);*//*
                }
            }*/

            // generateNotification(context, message);
            // check if the app is in background,if the app is in background
            //then do not dislay the notification
            if (GlobalConfig_Methods.isForeground(context, "com.tnc")) {
                Logs.writeLog("GCMIntentService", "OnMessage", "app in forground");

                // Display dialog in case of emergency notification received
                if(Uri.decode(message).contains("HAD REQUESTED EMERGENCY SERVICES")){

                    JSONObject mObject;

                    try{
                        mObject = new JSONObject(Uri.decode(message)); //.get("message")
                        if(mObject.has("message")){
                            String messageToDisplay = mObject.get("message").toString();
                            String bbidForName = mObject.get("from_user_id").toString();
                            Intent intentDiaplayPopup = new Intent("com.chatstasy.displaypopup");
                            intentDiaplayPopup.putExtra("message", messageToDisplay);
                            intentDiaplayPopup.putExtra("from_user_id", bbidForName);
                            sendBroadcast(intentDiaplayPopup);
                        }
                    }catch(Exception e){
                        e.getMessage();
                    }
                }else if(Uri.decode(message).toLowerCase().contains("message") &&
                        Uri.decode(message).toLowerCase().contains("emergency call from")){

                        /*String mNumberCallLog = "";
                        try{
                            mNumberCallLog = new JSONObject(message).getString("from_user_phone");
                        }catch(Exception e){
                            e.getMessage();
                        }
                        if(mNumberCallLog!=null && !mNumberCallLog.trim().equalsIgnoreCase("")){
                            save_State.setIS_EMERGENCY_NUMBER_DIALLED(context, true);
                            GlobalConfig_Methods.insertCallLog(context, mNumberCallLog, GlobalCommonValues.CallTypeMissed);
                            save_State.setIS_EMERGENCY_NUMBER_DIALLED(context, false);
                        }*/

                    JSONObject mObject;
                    try{
                        mObject = new JSONObject(Uri.decode(message));

                        if(mObject.has("message")){
                            String messageToDisplay = mObject.get("message").toString();
                            String bbidForName      = mObject.get("from_user_id").toString();
                            String fromPhoneNumber  = mObject.get("from_user_phone").toString();
                            Intent intentDiaplayPopup = new Intent("com.chatstasy.displaypopup");
                            intentDiaplayPopup.putExtra("message", messageToDisplay);
                            intentDiaplayPopup.putExtra("from_user_id", bbidForName);
                            intentDiaplayPopup.putExtra("from_user_phone",fromPhoneNumber);
                            sendBroadcast(intentDiaplayPopup);
                        }
                    }catch(Exception e){
                        e.getMessage();
                    }
                }else{
                    GlobalCommonValues.displayMessage(context, Uri.decode(message));
                }
            } else {
                //if the app is in the background

                //Display BadgeCount
                if(intent!=null && intent.getExtras()!=null &&
                        intent.getExtras().containsKey("badge") &&
                        intent.getExtras().getString("badge")!=null){
                    String mBadgeCount = intent.getExtras().getString("badge");
                    GlobalConfig_Methods.displayBadgeCount(context,mBadgeCount);
                }

                //display the notification
                Logs.writeLog("GCMIntentService", "OnMessage", "app in background");
                SharedPreference saveState = new SharedPreference();
                if (saveState.isRegistered(context)
                        && !saveState.getPublicKey(context).trim().equals("")   //&& !saveState.getBBID(context).trim().equals("")
                        ) {
                    GlobalCommonValues.pushNotificationString = Uri.decode(message);
                    generateNotification(context, GlobalCommonValues.pushNotificationString);
                    //					context. startService(new Intent(context,ChatHeadService.class));
					/* KeyguardManager kgm = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
					    boolean isKeyguardUp = kgm.inKeyguardRestrictedInputMode();
					    KeyguardLock kgl = kgm.newKeyguardLock("Your Activity/Service name");

					    if(isKeyguardUp){
					    kgl.disableKeyguard();
					    isKeyguardUp = false;
					    }
					    PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
					    WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
					    wakeLock.acquire();*/
					/*Intent notificationIntent = null;
					if (GlobalCommonValues.pushNotificationString
							.contains("notification_id")) {
						//						generateNotification(context, GlobalCommonValues.pushNotificationString);
						notificationIntent = new Intent(context,
								NotificationPushDisplayDialogActivity.class);
						notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );


					 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK 
								| Intent.FLAG_ACTIVITY_TASK_ON_HOME);

						// Intent.FLAG_ACTIVITY_CLEAR_TASK
						MainBaseActivity.isFromMain=false;
						isNotificationPushDisplayDialogActivity = true;
						isMessagePushDisplayDialogActivity=false;
					} else if (GlobalCommonValues.pushNotificationString
							.contains("message_id")) {
						//						generateNotification(context, message);
						notificationIntent = new Intent(context,
								MessagePushDisplayDialogActivity.class);
						notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
						MainBaseActivity.isFromMain=false;
						isNotificationPushDisplayDialogActivity = false;
						isMessagePushDisplayDialogActivity=true;
					}
					context.startActivity(notificationIntent);*/
                }
                // generateNotification(context, message);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onError(Context arg0, String errorId) {
        try {
            Log.i(TAG, "Received error: " + errorId);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings("deprecation")
    private void generateNotification(Context context, String message) {
        GCMIntentService.isNotificationPushDisplayDialogActivity = false;
        GCMIntentService.isMessagePushDisplayDialogActivity = false;
        int icon = R.drawable.appicon_96;
        pendingNotificationCode++;
        String displayMessage="";
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String title = context.getString(R.string.app_name);
        Intent notificationIntent = null;
        try {
            //			MainBaseActivity.isAlreadyDisplaying=true;
            if(message.contains("notification_id"))
            {
                if (!TextUtils.isEmpty(GlobalCommonValues.pushNotificationString) && GlobalConfig_Methods.isJsonString(GlobalCommonValues.pushNotificationString))
                {
                    Gson gson=new Gson();
                    NotificationPushReponseBean get_Response = gson.fromJson(GlobalCommonValues.pushNotificationString,NotificationPushReponseBean.class);
                    displayMessage=get_Response.getMessage();
                }
                notificationIntent =new Intent(context,HomeScreenActivity.class);
                MainBaseActivity.isFromMain=false;
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                );
                notificationIntent.putExtra("type","notification");

                isNotificationPushDisplayDialogActivity = true;
                isMessagePushDisplayDialogActivity=false;
            }
            else if(message.contains("message_id"))
            {
                if (!TextUtils.isEmpty(GlobalCommonValues.pushNotificationString) && GlobalConfig_Methods.isJsonString(GlobalCommonValues.pushNotificationString))
                {
                    Gson gson=new Gson();
                    MessagePushReponseBean get_Response = gson.fromJson(GlobalCommonValues.pushNotificationString,MessagePushReponseBean.class);
                    displayMessage=Uri.decode(get_Response.getMessage());
                }
                notificationIntent =new Intent(context,HomeScreenActivity.class);
                MainBaseActivity.isFromMain=false;
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                );
                notificationIntent.putExtra("type","message");
                //				startActivity(notificationIntent);
                isNotificationPushDisplayDialogActivity = false;
                isMessagePushDisplayDialogActivity=true;
            }
        } catch (Exception e) {
            e.getMessage();
        }

        //Is some version of the contents updated like
        //cliparts,howto,faq,default messages,emergency numbers version
        // remove prefixed string
        if(Uri.decode(displayMessage).contains("Cliparts:")){
            displayMessage = displayMessage.replace("Cliparts:","");//.substring(displayMessage.indexOf(":")+1,displayMessage.length());
        }else if(Uri.decode(displayMessage).contains("Faq:")){
            displayMessage = displayMessage.replace("Faq:", "");//substring(displayMessage.indexOf(":")+1,displayMessage.length());
        }else if(Uri.decode(displayMessage).contains("How To:")){
            displayMessage = displayMessage.replace("How To:","");//substring(displayMessage.indexOf(":")+1,displayMessage.length());
        }else if(Uri.decode(displayMessage).contains("Emergency Numbers:")){
            displayMessage = displayMessage.replace("Emergency Numbers:", "");//substring(displayMessage.indexOf(":")+1,displayMessage.length());
        }else if(Uri.decode(displayMessage).contains("Default Messages:")){
            displayMessage = displayMessage.replace("Default Messages:", "");//substring(displayMessage.indexOf(":")+1,displayMessage.length());
        }

        // set intent so it does not start a new activity
		/*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		Notification notification = new Notification(icon, Uri.decode(displayMessage), when);

		PendingIntent intent = PendingIntent.getActivity(context,
				pendingNotificationCode, notificationIntent,
				pendingNotificationCode);

		notification.setLatestEventInfo(context, title, Uri.decode(displayMessage), intent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;*/



        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //Notification notification = new Notification(icon, Uri.decode(displayMessage), when);

        //PendingIntent intent = PendingIntent.getActivity(context,pendingNotificationCode, notificationIntent,pendingNotificationCode);

        //notification.setLatestEventInfo(context, title, Uri.decode(displayMessage), intent);

        //notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //////////////////////  -- For Api level 23 and above
        PendingIntent intent = PendingIntent.getActivity(context, pendingNotificationCode, notificationIntent, pendingNotificationCode);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification notification = builder.setContentIntent(intent)
                .setSmallIcon(icon)
                .setTicker(title)
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText( Uri.decode(displayMessage)))
                .setContentText(Uri.decode(displayMessage)).build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        //Is some version of the contents updated like cliparts,howto,faq,default messages,emergency numbers version
        if(Uri.decode(message).toLowerCase().contains("cliparts:") ||
                Uri.decode(message).toLowerCase().contains("clipart")){

        }else if(Uri.decode(message).toLowerCase().contains("faq:")){

        }else if(Uri.decode(message).toLowerCase().contains("how to:")){

        }else if(Uri.decode(message).toLowerCase().contains("emergencycontact") ||
                Uri.decode(message).toLowerCase().contains("emergency numbers:")){

        }else if(Uri.decode(message).toLowerCase().contains("default messages:") ||
                Uri.decode(message).toLowerCase().contains("defaultmessage")){

        }else if(Uri.decode(message).toLowerCase().contains("default messages:") ||
                Uri.decode(message).toLowerCase().contains("defaultmessage")){

        }
        else if(Uri.decode(message).toLowerCase().contains("privacy policy:") ||
                Uri.decode(message).toLowerCase().contains("defaultmessage")){

        }else if(Uri.decode(message).toLowerCase().contains("terms usage:") ||
                Uri.decode(message).toLowerCase().contains("defaultmessage")){

        }else{
            //make a notfication sound or vibrate only in the case of
            //non dynamic contents' notification
            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;
            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        // notification.sound = Uri.parse("android.resource://" +
        // context.getPackageName() + "your_sound_file_name.mp3");

        notificationManager.notify(pendingNotificationCode, notification);
    }

    public static void clearPushNotifications(Context mContext)
    {
        ((NotificationManager)mContext.getSystemService("notification")).cancel(pendingNotificationCode);
    }

    private void fetchClipartsImages(){
        GlobalConfig_Methods mObjCLiparts =
                new GlobalConfig_Methods();

        mObjCLiparts.downloadClipArts(getApplicationContext());
    }
}
