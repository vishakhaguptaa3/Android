package com.tnc.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.MessageStatusUpdateBean;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.fragments.MessagePredefinedComposeFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.widgets.HUDView;
import android.content.BroadcastReceiver;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by a3logics on 23/3/17.
 */

public class CustomDialerScreen extends FragmentActivity{

    private Context mActivity;
    private HUDView mView;
    private Receiver receiver;
    int BBID = -1;
    String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
    boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
    private SharedPreference saveState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = new HUDView(this);

        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        mActivity=CustomDialerScreen.this;

        saveState = new SharedPreference();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT | Gravity.TOP;
//        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        try{
            wm.addView(mView, params);

            if(mView!=null && mView.tvStatus!=null && mView.tvContactNumber!=null){

                String mDisplayHeading = "";

                if(getIntent()!=null && getIntent().getExtras()!=null && getIntent().getExtras().containsKey("call_state")){
                    String mCallState = getIntent().getExtras().getString("call_state");

                    if(mCallState.equalsIgnoreCase(GlobalCommonValues.CALL_STATE_DIALING)){
                        mDisplayHeading = getResources().getString(R.string.txtCallStatusDialing);
                    }else if(mCallState.equalsIgnoreCase(GlobalCommonValues.CALL_STATE_INCOMING)){
                        mDisplayHeading = getResources().getString(R.string.txtCallStatusIncoming);
                    }
                }
                mView.tvStatus.setText(mDisplayHeading);

                BBID = -1;
                if(getIntent()!=null && getIntent().getExtras()!=null && getIntent().getExtras().containsKey("phone_number")){
                    String mPhoneNumber = getIntent().getExtras().getString("phone_number");

                    String number = GlobalConfig_Methods.getBBNumberToCheck(CustomDialerScreen.this, mPhoneNumber);

                    countryCodeRegisteredUser=""; numberRegisteredUser=""; isdCodeRegisteredUser="";
                    isMobileRegisteredUser=false; isdCodeFlagRegisteredUser=false; isTncUserRegisteredUser=false;
                    String []arrayUserDetails=number.split(",");

                    countryCodeRegisteredUser=arrayUserDetails[0];
                    numberRegisteredUser=arrayUserDetails[1];
                    isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
                    isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
                    isdCodeRegisteredUser=arrayUserDetails[4];
                    isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);

                    /*if((countryCodeRegisteredUser!=null && !(countryCodeRegisteredUser.trim().equals(""))) &&
                            (numberRegisteredUser!=null && !(numberRegisteredUser.trim().equals("")))){

                    }else if(countryCodeRegisteredUser == null || countryCodeRegisteredUser.equals("")){
                        mView.tvContactNumber.setText(mPhoneNumber);
                    }*/

                    mView.tvContactNumber.setText(mPhoneNumber);

                    // Set User name with whom we are making/receiving a call
                    if((numberRegisteredUser == null || numberRegisteredUser.trim().equals(""))){
                        mView.tvContactName.setText(GlobalConfig_Methods.getUserNameFromAlgorithm(mActivity,
                                "", mPhoneNumber, null));
                    }else{
                        mView.tvContactName.setText(GlobalConfig_Methods.getUserNameFromAlgorithm(mActivity,
                                "", numberRegisteredUser, null));
                    }

                    // Enable click of send message button
                    boolean isSendMessageButtonClickEnable = false;

                    // Display Emergency Icon
                    ArrayList<ContactTilesBean> mListTiles =
                            DBQuery.getTileFromPhoneNumber(CustomDialerScreen.this,
                                    numberRegisteredUser);

                    boolean isEmergency = mListTiles.get(0).isIsEmergency();

                    if(isEmergency){
                        mView.imViewEmergency.setVisibility(View.VISIBLE);
                    }else{
                        mView.imViewEmergency.setVisibility(View.GONE);
                    }

                    if(isTncUserRegisteredUser){
                        isSendMessageButtonClickEnable = true;
                        mView.llImageBorder.setVisibility(View.VISIBLE);

                        // Get and Set BBID
                        ArrayList<BBContactsBean> mListBBContacts =
                                DBQuery.getBBContactsfromCountryCodeAndPhoneNumber(CustomDialerScreen.this,
                                        countryCodeRegisteredUser, numberRegisteredUser);
                        BBID = mListBBContacts.get(0).getBBID();

                    }else{
                        isSendMessageButtonClickEnable = false;
                        mView.llImageBorder.setVisibility(View.GONE);
                        if((countryCodeRegisteredUser!=null && !countryCodeRegisteredUser.trim().equals("")) &&
                                (numberRegisteredUser!=null && !numberRegisteredUser.trim().equals(""))){

                            // Get and Set BBID
                            ArrayList<BBContactsBean> mListBBContacts =
                                    DBQuery.getBBContactsfromCountryCodeAndPhoneNumber(CustomDialerScreen.this,
                                            countryCodeRegisteredUser, numberRegisteredUser);
                            BBID = mListBBContacts.get(0).getBBID();

                            if(mListBBContacts!=null && mListBBContacts.size() > 0){
                                mView.llImageBorder.setVisibility(View.VISIBLE);
                                isSendMessageButtonClickEnable = true;
                            }
                        }
                    }

                    // check if send message button is enable and accordingly perform click event for the same
                    if(isSendMessageButtonClickEnable){
                        // set background image for send message button on the basis of isTncUser
                        mView.btnSendMessage. setBackground(getResources().getDrawable(R.drawable.ic_send_message_enabled));

                        // Handle click event for navigating to the chat screen
                        mView.btnSendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * Call method to go to chat window
                                 */
                                gotoChatWindow(countryCodeRegisteredUser, numberRegisteredUser, BBID);
                            }
                        });
                    }else{
                        // set background image for send message button on the basis of isTncUser
                        mView.btnSendMessage. setBackground(getResources().getDrawable(R.drawable.ic_send_message_disabled));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    /**
     * Method to go to the chat window screen
     */
    private void gotoChatWindow(String countryCoderegisteredUser, String numberRegisteredUser, int mBBID){
        try {
            String phoneNumber = "";
            BBContactsBean objContactDetail = null;
            ArrayList<BBContactsBean> listBBContacts = null;
            phoneNumber = numberRegisteredUser;

            BBID = mBBID;
            if(!(BBID>0)){
                BBID = DBQuery.getBBIDFromPhoneNumber(mActivity, phoneNumber);
            }
            if (BBID != 0) {
                if (!saveState.getBBID(mActivity).trim()
                        .equals("")) {
                    // Update Notification
                    DBQuery.updateMessageStatus(mActivity,
                            BBID, Integer.parseInt(saveState
                                    .getBBID(mActivity)));
                    MessageStatusUpdateBean updateStatusBean = new MessageStatusUpdateBean();
                    updateStatusBean.setBbid(String
                            .valueOf(BBID));
                    updateMessageStatus(updateStatusBean);
                    ((HomeScreenActivity) mActivity)
                            .setUnreadMessageCount();

                    // Open Chat Window
                    listBBContacts = new ArrayList<BBContactsBean>();
                    listBBContacts = DBQuery
                            .checkBBContactExistence(
                                    mActivity, BBID);
                    objContactDetail = new BBContactsBean();
                    objContactDetail.setName(listBBContacts
                            .get(0).getName());
                    objContactDetail
                            .setPhoneNumber(listBBContacts.get(
                                    0).getPhoneNumber());
                    objContactDetail.setCountryCode(listBBContacts.get(
                            0).getCountryCode());
                    objContactDetail.setBBID(listBBContacts
                            .get(0).getBBID());
                    objContactDetail.setImage(listBBContacts
                            .get(0).getImage());
                    MessagePredefinedComposeFragment objMesssagePredefined = new MessagePredefinedComposeFragment();
                    objMesssagePredefined.newInstance(
                            objContactDetail, null,
                            Integer.parseInt(saveState
                                    .getBBID(mActivity)),
                            BBID, null);
                    if (mActivity instanceof MainBaseActivity) {
                        ((MainBaseActivity) mActivity)
                                .setFragment(objMesssagePredefined);
                    } else if (mActivity instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivity)
                                .setFragment(objMesssagePredefined);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    // Call a WebService to update message status from unread to read
    public void updateMessageStatus(MessageStatusUpdateBean updateStatusBean) {
        try {
            Gson gson = new Gson();
            String stingGson = gson.toJson(updateStatusBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivity,
                    GlobalCommonValues.UPDATE_MESSAGE_STATUS, stringEntity,
                    updateMessageResponseHandler,
                    mActivity.getString(R.string.private_key),
                    saveState.getPublicKey(mActivity));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    AsyncHttpResponseHandler updateMessageResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            if (response != null)
                Logs.writeLog("Update Message Status", "OnSuccess", response.toString());
            try {
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("Update Message Status", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
			/*
			 * if (progress.isShowing()) progress.dismiss();
			 */
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter("com.chatstasy.endcallscreen");
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Broadcast receiver that will be fired when a call is ended
     * @author a3logics
     *
     */
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase("com.chatstasy.endcallscreen")){
                try{
                    finish();
                }catch(Exception e){
                    e.getMessage();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
