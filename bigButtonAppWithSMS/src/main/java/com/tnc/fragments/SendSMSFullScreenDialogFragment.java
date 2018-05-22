package com.tnc.fragments;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.GetCountrySMSRequestBean;
import com.tnc.bean.GetCountrySMSResponseBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cz.msebera.android.httpclient.Header;

/**
 * class to Dislpay SMS Popup Screen 
 *  @author a3logics
 *
 *  In flow from registration
 */

public class SendSMSFullScreenDialogFragment extends BaseFragmentTabs implements
        OnClickListener {

    private Context mAct;
    private EditText etTo, etMessage;
    private Button btnSendMessage, btnCancelMessage;
    private String message, phoneNumber;
    private SharedPreference saveState;
    private String number_type="",country_name="";
    private TransparentProgressDialog progress;
    private Gson gson;
    private boolean isFromRegistration = false;
    private String mDisplayMessage = "";

    public SendSMSFullScreenDialogFragment newInstance(Context mAct,
                                                       String message, String phoneNumber,String number_type,String country_name,
                                                       boolean isFromRegistration) {
        this.mAct = mAct;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.number_type = number_type;
        this.country_name = country_name;
        this.isFromRegistration = isFromRegistration;
        SendSMSFullScreenDialogFragment frag = new SendSMSFullScreenDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    //TO-DO : W.S. Implementation
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_sms_popup_window, container,
                false);
        // getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveState = new SharedPreference();
        progress=new TransparentProgressDialog(mAct, R.drawable.customspinner);
        etTo = (EditText) view.findViewById(R.id.etTo);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btnSendMessage = (Button) view.findViewById(R.id.btnSendMessage);
        btnCancelMessage = (Button) view.findViewById(R.id.btnCancelMessage);
		/*
		 * 16-9-2016
		 * etTo.setText(phoneNumber);
		if(phoneNumber.contains("8582606130")){
			etMessage.setText(saveState.getPassCode(mAct));
		}else{
			etMessage.setText("TAPNCHAT "
					+ saveState.getPassCode(mAct));
		}
		etTo.setSelection(phoneNumber.length());
		etMessage.setSelection(etMessage.getText().toString().length());*/

        //call web service to get SMS Number Details as per the country Selected by the user
        checkInternetConnection();

        btnSendMessage.setOnClickListener(this);
        btnCancelMessage.setOnClickListener(this);
        return view;
    }

    /**
     * check internet availability
     */
    public void checkInternetConnection() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            GetCountrySMSRequestBean mGetCountrySMSRequestBean = new GetCountrySMSRequestBean();
            mGetCountrySMSRequestBean.setCountry_name(country_name);
            mGetCountrySMSRequestBean.setNumber_type(number_type);
            getCountrySmsNumber(mGetCountrySMSRequestBean);
        } else {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    /**
     * Method to call web service to change the user number
     */

    private void getCountrySmsNumber(GetCountrySMSRequestBean mGetCountrySMSRequestBean)
    {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(mGetCountrySMSRequestBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.GET_COUNTRY_SMS_NUMBER, stringEntity,
                    getCountrySmsNumberResponseHandler,
                    mActivityTabs.getString(R.string.private_key), "");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * async task to handle call to a web service to change the user number
     */
    AsyncHttpResponseHandler getCountrySmsNumberResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            if(progress!=null && !progress.isShowing())
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("getCountrySmsNumberResponseHandler", "OnSuccess",
                            response.toString());
                    getCountrySmsNumberResponse(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(progress!=null && progress.isShowing())
                progress.dismiss();
            if (response != null)
                Logs.writeLog("getCountrySmsNumberResponseHandler", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            if(progress!=null )
                progress.dismiss();
            // Completed the request (either success or failure)
        }
    };

    /**
     * handle response for the request being made to change the number
     * @param response
     */

    private void getCountrySmsNumberResponse(String response)
    {
        if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
            gson = new Gson();
            GetCountrySMSResponseBean get_Response = gson.fromJson(response,GetCountrySMSResponseBean.class);
            if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE))
            {
                if(get_Response.getData()!=null)
                {
                    mDisplayMessage = "";

                    // set number on which sms is needed to be sent
                    etTo.setText(get_Response.getData().getNumber());

                    //Please press SEND to complete registration

                    if((get_Response.getData().getType() == null) ||
                            (get_Response.getData().getType().trim().equals(""))){
                        //etMessage.setText(saveState.getPassCode(mAct));
                        if(isFromRegistration){
                            mDisplayMessage = saveState.getPassCode(mAct);
                        }else{
                            // in case of update number
                            mDisplayMessage = "UPDATE " + saveState.getPassCode(mAct);
                        }
                    }else{
//						etMessage.setText(get_Response.getData().getType() + " "
//								+ saveState.getPassCode(mAct));
                        if(isFromRegistration){
                            mDisplayMessage = get_Response.getData().getType() + " "
                                    + saveState.getPassCode(mAct);
                        }else{
                            // in case of update number
                            mDisplayMessage = get_Response.getData().getType() + " UPDATE  "
                                    + saveState.getPassCode(mAct);
                        }
                    }

                    if(isFromRegistration){
                        etMessage.setText(mDisplayMessage + ". " +  mAct.getResources().getString(R.string.txtRegistrationTapMessage));
                    }else{
                        etMessage.setText(mDisplayMessage);
                    }

                    etTo.setSelection(etTo.getText().toString().length());
                    etMessage.setSelection(etMessage.getText().toString().length());
                }
            }
            else {
                if(mAct instanceof MainBaseActivity){
                    ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
                    dialogErrorMessage.newInstance("",
                            ((MainBaseActivity)mAct),
                            get_Response.getResponse_message(), "", alertBack);
                    dialogErrorMessage.show(
                            ((MainBaseActivity)mAct)
                                    .getSupportFragmentManager(), "test");
                }else if(mAct instanceof HomeScreenActivity){
                    ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
                    dialogErrorMessage.newInstance("",
                            ((HomeScreenActivity)mAct),
                            get_Response.getResponse_message(), "", alertBack);
                    dialogErrorMessage.show(
                            ((HomeScreenActivity)mAct)
                                    .getSupportFragmentManager(), "test");
                }
            }
        }
    }


    /**
     * Interface to handle action in case of invalid country sms number information
     */
    AlertCallAction alertBack = new AlertCallAction() {

        @Override
        public void isAlert(boolean isOkClikced) {

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

            Intent mIntent = new Intent(mActivityTabs,HomeScreenActivity.class);
            startActivity(mIntent);
            mActivityTabs.finish();
        }
    };

    /**
     * Method to send sms via Api calls
     */
    private void send_SMS() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(etTo.getText().toString().trim(), null, mDisplayMessage, null, null);

            VerifyingRegistrationFragment verifyRegistrationFragment = null;
            if (mAct instanceof MainBaseActivity) {
                verifyRegistrationFragment = new VerifyingRegistrationFragment();
                verifyRegistrationFragment
                        .newInstance(((MainBaseActivity) mAct));
                ((MainBaseActivity) mAct)
                        .setFragment(verifyRegistrationFragment);
            } else if (mAct instanceof HomeScreenActivity) {
                verifyRegistrationFragment = new VerifyingRegistrationFragment();
                verifyRegistrationFragment
                        .newInstance(((HomeScreenActivity) mAct));
                ((HomeScreenActivity) mAct)
                        .setFragment(verifyRegistrationFragment);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMessage:
                // dismiss();
                send_SMS();
                break;

            case R.id.btnCancelMessage:
                // dismiss();
                if(isFromRegistration){
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
                }else{
                    // in case of update number
                    if (mAct instanceof MainBaseActivity) {
                        ((MainBaseActivity)mAct).fragmentManager.popBackStack();
                    } else if (mAct instanceof HomeScreenActivity) {
                        ((HomeScreenActivity)mAct).fragmentManager.popBackStack();
                    }
                }
                break;
            default:
                break;
        }

    }
}
