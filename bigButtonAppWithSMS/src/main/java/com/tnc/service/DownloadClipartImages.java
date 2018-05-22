package com.tnc.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.tnc.R;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import cz.msebera.android.httpclient.Header;

public class DownloadClipartImages extends Service {

	SharedPreference saveState;
	Gson gson = null;
	private Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		mContext = this;

		// call the web service to download the images
		checkInternetConnection();
		return START_STICKY;
	}


	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mContext)) {
			clipArtsRequest();
		}
	}

	//Method to call web service to get clipart images
	private void clipArtsRequest()
	{
		MyHttpConnection.getWithoutPara(mContext,GlobalCommonValues.GET_CLIPARTS,
				mContext.getResources().getString(R.string.private_key),clipArtsResponsehandler);
	}

	//async task to handle request made to get the clipart images
	AsyncHttpResponseHandler clipArtsResponsehandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {

		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null)
				{
					Logs.writeLog("ClipArtsImage", "OnSuccess",response.toString());
					getResponse(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("ClipArtsImage", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/*
	 * Handling Response from the Server for the request being sent to get clip art images
	 */
	private void getResponse(String response) {
		try {
			saveState = new SharedPreference();
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				String strResponseCode="",strResponseMessage="";
				ArrayList<String> listClipArtsString=new ArrayList<String>();
				try{
					JSONObject jobj=new JSONObject(response);
					saveState.setCLIPARTS_VERSION(mContext, jobj.get("version").toString());
					strResponseCode=String.valueOf(jobj.get("response_code"));
					strResponseMessage=String.valueOf(jobj.get("response_message"));
					if(strResponseCode.equals(GlobalCommonValues.SUCCESS_CODE))
					{
						JSONArray arrCat = jobj.getJSONArray("data");
						for (int i = 0; i < arrCat.length(); i++) {
							listClipArtsString.add(String.valueOf(arrCat.get(i)));
							Picasso.with(mContext).load(String.valueOf(arrCat.get(i))).fetch();
						}

						DBQuery.insertAllClipartImages(mContext, listClipArtsString);
						saveState.setIS_INITIAL_CLIPARTS(mContext, false);
						stopSelf();
					}
					else if(strResponseCode.equals(GlobalCommonValues.FAILURE_CODE_1)){
						//						ShowDialog.alert(mActivity, "", strResponseMessage);
						stopSelf();
					}
				}
				catch (Exception e) 
				{
					stopSelf();
					e.getMessage();
				}

			} else {
				stopSelf();
			}
		} catch (Exception e) {
			stopSelf();
			e.getMessage();
		}
	}
}
