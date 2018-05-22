package com.tnc.service;
/*package com.bigbutton.service;

import com.bigbutton.preferences.SharedPreference;
import com.google.gson.Gson;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class InsertGetMessageService extends Service 
{
	public String response="";
	Context mContext;
	SharedPreference saveState;
	Gson gson=null;

	@Override
	public IBinder onBind(Intent intent) 
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Override
	public void onCreate() 
	{
		super.onCreate();
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		mContext = this;	
		new InsertMessageAsyncTask().execute();
		return START_NOT_STICKY;
	}


	*//**
	 *insert response messages in Database
	 *
	 *//*
	class InsertMessageAsyncTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			saveState=new SharedPreference();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			insertMessageInDataBase();
			saveState.setIS_INSERT_MESSAGE_SERVICEON(mContext,true);
		}
	}

	private void insertMessageInDataBase()
	{

	}
}
*/