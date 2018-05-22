package com.tnc.service;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.fragments.VerifyingRegistrationFragment;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CancelRegistrationResponseBean;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import cz.msebera.android.httpclient.Header;

public class RegistrationCheckService extends Service {

	private Context mContext;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mContext = RegistrationCheckService.this;

		return START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onTaskRemoved(Intent rootIntent) {
		//system.out.println("onTaskRemoved called");
		super.onTaskRemoved(rootIntent);
		if(!VerifyingRegistrationFragment.isAppUserRegistered){

			//CANCEL THE USER'S REGISTRATION
//			cancelUserRegistrationRegistration();

			GlobalConfig_Methods.clearDataBaseValues(mContext);
			GlobalConfig_Methods.clearRegsitrationPreferences(mContext);
			VerifyingRegistrationFragment.isAppUserRegistered = false;
		}

		//do something you want
		//stop service
		this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method to cancel the registration
	 */
	private void cancelUserRegistrationRegistration(){
		CancelRegistrationRequestBean objCancelRegistration = null;
		SharedPreference saveState = new SharedPreference();
		objCancelRegistration = new CancelRegistrationRequestBean();
		objCancelRegistration.setPasscode(saveState.getPassCode(mContext));
		cancelRegistration(objCancelRegistration);
	}

	/**
	 * // Method to call web service to cancel the registration
	 * @param objCancelRegistration
	 */
	public void cancelRegistration(
			CancelRegistrationRequestBean objCancelRegistration) {
		try {
			Gson gson = new Gson();
			String stingGson = gson.toJson(objCancelRegistration);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection
					.postWithJsonEntityHeader(mContext,
							GlobalCommonValues.CANCEL_REGISTRATION,
							stringEntity, cancelRegistrationResponseHandler,
							mContext.getString(R.string.private_key), "");
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
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
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
			if (!TextUtils.isEmpty(response)
					&& GlobalConfig_Methods.isJsonString(response)) {
				Gson gson = new Gson();
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
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
}

