package com.tnc.receivers;

import android.content.BroadcastReceiver;

/**
 * Created by a3logics on 25/11/16.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.tnc.activities.CustomDialerScreen;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.preferences.SharedPreference;

import java.util.Date;

public class CallBroadCastReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhonecallStartEndDetector listener;
    String outgoingSavedNumber;
    protected Context savedContext;
    SharedPreference saveState;
    String mCallState = "";
    String mPhoneNumber = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        savedContext = context;
        if(listener == null){
            listener = new PhonecallStartEndDetector();
        }

        saveState = new SharedPreference();

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));

            mPhoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

            // Set Custom Dialer Screen in Case of Outgoing Call and settings are enabled for the same
            if(saveState.getIS_NUMBER_DIALLED(savedContext) &&
                    saveState.getIS_DIALER_INTERFACE_ENABLED(savedContext)){
                mCallState = GlobalCommonValues.CALL_STATE_DIALING;
                callActionHandler.postDelayed(runRingingActivity, 1000);
            }


            /*if(saveState == null)
                saveState = new SharedPreference();
            // To enable loudspeaker of the phone for outgoing calls if the number dialled is from chatstasy app
            if(saveState.getIS_NUMBER_DIALLED(savedContext)) {
                saveState.setIS_NUMBER_DIALLED(savedContext, false);
                if (saveState.getIsAutoSpeakerMode(savedContext)) {

                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                    // check if the autospeaker mode is ON
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                }
            }*/
            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Derived classes should override these to respond to specific events of interest
    /*protected abstract void onIncomingCallStarted(String number, Date start);
    protected abstract void onOutgoingCallStarted(String number, Date start);
    protected abstract void onIncomingCallEnded(String number, Date start, Date end);
    protected abstract void onOutgoingCallEnded(String number, Date start, Date end);
    protected abstract void onMissedCall(String number, Date start);*/

    //Deals with actual events
    public class PhonecallStartEndDetector extends PhoneStateListener {
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhonecallStartEndDetector() {}

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number){
            savedNumber  = number;
            mPhoneNumber = number;
        }

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            SharedPreference saveState = new SharedPreference();

            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber  = incomingNumber;
                    mPhoneNumber = incomingNumber;
                    // Set Custom Dialer Screen in Case of Outgoing Call and settings are enabled for the same
                    if(saveState.getIS_NUMBER_DIALLED(savedContext) &&
                            saveState.getIS_DIALER_INTERFACE_ENABLED(savedContext)){
                        mCallState = GlobalCommonValues.CALL_STATE_INCOMING;
//                        callActionHandler.postDelayed(runRingingActivity, 1000);
                    }

                    //onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing down on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        //onOutgoingCallStarted(savedNumber, callStartTime);

//                        mCallState = GlobalCommonValues.CALL_STATE_CONNECTED;
//                        Toast.makeText(savedContext, "Connected", Toast.LENGTH_LONG).show();

                        if(saveState == null)
                            saveState = new SharedPreference();
                        // To enable loudspeaker of the phone for outgoing calls if the number dialled is from chatstasy app
                        if(saveState.getIS_NUMBER_DIALLED(savedContext)) {
                            saveState.setIS_NUMBER_DIALLED(savedContext, false);
                        }
                        if (saveState.getIsAutoSpeakerMode(savedContext)) {
                            GlobalConfig_Methods.enableDisableLoudSpeakerCall(savedContext);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)

                    long mCount = 0;

                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        //onMissedCall(savedNumber, callStartTime);

                        // Make Call Entry into the  App local DB
                        if(savedNumber!=null && !savedNumber.trim().equalsIgnoreCase(""))
                            mCount = GlobalConfig_Methods.insertCallLog(savedContext, savedNumber, GlobalCommonValues.CallTypeMissed);

                        // Make Emergency Dialled Number  - true
                        saveState.setIS_EMERGENCY_NUMBER_DIALLED(savedContext, false);

                       /* Toast.makeText(savedContext,
                                "Missed call from : " + mCount + "  - : -  " + savedNumber,
                                Toast.LENGTH_LONG).show();*/
                    }
                    else if(isIncoming){
                        //onIncomingCallEnded(savedNumber, callStartTime, new Date());

                        // Make Call Entry into the  App local DB
                        if(savedNumber!=null && !savedNumber.trim().equalsIgnoreCase(""))
                            mCount = GlobalConfig_Methods.insertCallLog(savedContext, savedNumber, GlobalCommonValues.CallTypeIncoming);

                        // Make Emergency Dialled Number  - true
                        saveState.setIS_EMERGENCY_NUMBER_DIALLED(savedContext, false);

                        /*Toast.makeText(savedContext,
                                "onIncomingCallEnded from : " + mCount + "  - : -  " + savedNumber,
                                Toast.LENGTH_LONG).show();*/
                    }
                    else{
                        //onOutgoingCallEnded(savedNumber, callStartTime, new Date());

                        // Make Call Entry into the  App local DB
                        if(savedNumber!=null && !savedNumber.trim().equalsIgnoreCase(""))
                            mCount = GlobalConfig_Methods.insertCallLog(savedContext, savedNumber, GlobalCommonValues.CallTypeOutgoing);

                        // Make Emergency Dialled Number  - true
                        saveState.setIS_EMERGENCY_NUMBER_DIALLED(savedContext, false);

                        try{
                            // update call log status as read if it is unread
                            DBQuery.updateCallLogStatus(savedContext,GlobalConfig_Methods.trimSpecialCharactersFromString(savedNumber));
                        }catch(Exception e){
                            e.getMessage();
                        }


                        /*Toast.makeText(savedContext,
                                "onOutgoingCallEnded : " + mCount + "  - : -  " + savedNumber,
                                Toast.LENGTH_LONG).show();*/
                    }

                    // update call log count on home screen tiles and bottom tab indicator on Homescreen
                    Intent intentMainacivity = new Intent("com.tapnchat.refreshcalllogcount");
                    savedContext.sendBroadcast(intentMainacivity);

                    // finish custom dialer screen activity
                    Intent intentCustomDialer = new Intent("com.chatstasy.endcallscreen");
                    savedContext.sendBroadcast(intentCustomDialer);

                    break;
            }
            lastState = state;
        }
    }

    Handler callActionHandler = new Handler();
    Runnable runRingingActivity = new Runnable(){
        @Override
        public void run(){

            Intent intentPhoneCall = new Intent(savedContext, CustomDialerScreen.class);
//            intentPhoneCall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentPhoneCall.putExtra("call_state", mCallState);
            intentPhoneCall.putExtra("phone_number", mPhoneNumber);
            savedContext.startActivity(intentPhoneCall);
        }
    };
}

// previous work

        /*mcontext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setResultData(null);
        dialphonenumber = getResultData();
        if (dialphonenumber == null) {
            dialphonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
        setResultData(dialphonenumber);

        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            if(saveState == null)
                saveState = new SharedPreference();
            // To enable loudspeaker of the phone for outgoing calls if the number dialled is from chatstasy app
            if(saveState.getIS_NUMBER_DIALLED(mcontext)){
                saveState.setIS_NUMBER_DIALLED(mcontext, false);
                if(saveState.getIsAutoSpeakerMode(mcontext)){
                    // check if the autospeaker mode is ON
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                }

                // Make Call Entry into the  App local DB
                if(dialphonenumber!=null && !dialphonenumber.trim().equalsIgnoreCase("")){

                    *//*Toast.makeText(mcontext,
                            "outgoing call to : " + dialphonenumber,
                            Toast.LENGTH_LONG).show();*//*

                    GlobalConfig_Methods.insertCallLog(mcontext, dialphonenumber, GlobalCommonValues.CallTypeOutgoing);
                }
            }
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {

            case TelephonyManager.CALL_STATE_RINGING:

                ring = true;
                // CALL_STATE_RINGING

                incomingFlag = true;

                incoming_number = intent.getStringExtra("incoming_number");

                Log.i(TAG, "RINGING :" + incoming_number);

                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:

                // CALL_STATE_OFFHOOK;
                callReceived = true;

                if (incomingFlag) {
                    Log.i(TAG, "incoming ACCEPT :" + incoming_number);
                    // Make Call Entry into the  App local DB

                   *//* Toast.makeText(mcontext,
                            "incoming call from : " + incoming_number,
                            Toast.LENGTH_LONG).show();*//*

                    if(incoming_number!=null && !incoming_number.trim().equalsIgnoreCase("")){
                        GlobalConfig_Methods.insertCallLog(mcontext, incoming_number, GlobalCommonValues.CallTypeIncoming);
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                // on call disconnect
                if (ring == true && callReceived == false) {
                    *//*Toast.makeText(mcontext,
                            "Missed call from : " + incoming_number,
                            Toast.LENGTH_LONG).show();*//*
                    // Make Call Entry into the  App local DB
                    if(incoming_number!=null && !incoming_number.trim().equalsIgnoreCase("")){
                        GlobalConfig_Methods.insertCallLog(mcontext, incoming_number, GlobalCommonValues.CallTypeMissed);
                    }
                }

                //end of call
                if (incomingFlag) {
                    Log.i(TAG, "incoming IDLE");
                }
                Toast.makeText(mcontext,
                        "Call Ended - 1",
                        Toast.LENGTH_LONG).show();
                if(callReceived){
                    callReceived = false;
                    Toast.makeText(mcontext,
                            "Call Ended - 2",
                            Toast.LENGTH_LONG).show();
                }

                break;
        }*/
//    }   End of onReceive
//}       End of Class
