package com.tnc.common;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.FileUploadBean;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ImageResponse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import cz.msebera.android.httpclient.Header;

/**
 * Utility class to upload the backup on the server
 * @author a3logics
 *
 */

public class UploadBackupServer {

	File fileDatabase;
	Gson gson;
	TransparentProgressDialog  progress;
	SharedPreference saveState;
	File fileBackup;
	File fileServerBackup;
	String path="";
	Context mActivityTabs;
	public boolean isDialogShow=false;
	INotifyGalleryDialog iNotifyCloudBackup;

	//	public static int count = 1;

	/**
	 *
	 * @param mContext
	 * @param isDialogShow
	 * @param iNotifyCloudBackup(interface reference to make list show deselected when popup dismisses)
	 */
	public UploadBackupServer(Context mContext,boolean isDialogShow,INotifyGalleryDialog iNotifyCloudBackup)
	{
		this.mActivityTabs=mContext;
		this.isDialogShow=isDialogShow;
		this.iNotifyCloudBackup=iNotifyCloudBackup;
		saveState=new SharedPreference();
	}

	/**
	 * upload backup to the server
	 */
	@SuppressWarnings("static-access")
	@SuppressLint("SdCardPath")
	public void uploadBackup()
	{
		//this.mActivityTabs.getPackageName()
		boolean isUpload = saveState.IS_UploadDatabseRequested(mActivityTabs);

		if(!isUpload){
			saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivityTabs, true);
			//			//system.out.println("count :- "+ count++);
			CopyDBFromAssets obj=new CopyDBFromAssets(mActivityTabs);
			fileDatabase=new File(obj.DB_PATH+CopyDBFromAssets.DATABASE_NAME);
			try
			{
				progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
				gson=new Gson();
				FileUploadBean fileUploadBean=new FileUploadBean(fileDatabase);
				try
				{
					path = android.os.Environment.getExternalStorageDirectory()
							+ File.separator
							+ "TNCDatabase";
					File fileBackupDir = new File(path);
					//				if(fileBackupDir.exists())
					//					fileBackupDir.delete();

					if (!fileBackupDir.exists()) {
						fileBackupDir.mkdirs();
					}
					if (fileDatabase.exists()) {
						fileBackup = new File(fileBackupDir, CopyDBFromAssets.DATABASE_NAME);
						try {
							fileBackup.createNewFile();
							FileUtils.copyFile(fileDatabase, fileBackup);
						}
						catch (Exception exception)
						{
							exception.getMessage();
						}
					}
				} catch (Exception e) {
					e.getMessage();
				}
				fileServerBackup=fileBackup;//new File(path,CopyDBFromAssets.DATABASE_NAME);
				String stingGson = gson.toJson(fileUploadBean);
				cz.msebera.android.httpclient.entity.StringEntity stringEntity;
				stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
				MyHttpConnection.postFileWithJsonEntityHeader(mActivityTabs,
						GlobalCommonValues.UPLOAD_BACKUP,fileServerBackup,
						stringEntity, uploadBackupResponseHandler,
						saveState.getPrivateKey(mActivityTabs),saveState.getPublicKey(mActivityTabs));
			}
			catch (Exception e)
			{
				e.getMessage();
			}
		}

	}

	AsyncHttpResponseHandler uploadBackupResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if(isDialogShow)
			{
				if ((!progress.isShowing()))
					if(mActivityTabs!=null)
						progress.setTitle(mActivityTabs.getResources().getString(R.string.txtAutoBackupMessage));
				progress.show();
			}
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response

			try {
				Logs.writeLog("Cloud Backup", "OnSuccess",response.toString());/*saveState.setChanged(mActivityTabs, false);*/
				if(response!=null)
				{
					getResponseUploadBackup(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
			saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivityTabs, false);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
			{
				Logs.writeLog("Cloud Backup", "OnFailure",response);
			}
			saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivityTabs, false);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if(isDialogShow)
			{
				if (progress.isShowing())
					progress.dismiss();
			}
			saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivityTabs, false);
		}
	};

	/**
	 *
	 * @param response as a String
	 * To handle the response we get from the server
	 */
	@SuppressLint("SimpleDateFormat")
	private void getResponseUploadBackup(String response)
	{
		try {
			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response))
			{
				try {
					gson=new Gson();
					ImageResponse get_Response = gson.fromJson(response, ImageResponse.class);
					if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
					{
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date date = new Date();
						saveState.setAUTOBACKUP_TILES_DATE(mActivityTabs,dateFormat.format(date));
						saveState.setChanged(mActivityTabs, false);
						if(isDialogShow)
						{
							ImageRequestDialog dialogResult=new ImageRequestDialog();
							dialogResult.setCancelable(false);
							if(mActivityTabs instanceof HomeScreenActivity)
							{
								dialogResult.newInstance("",((HomeScreenActivity)mActivityTabs),"Successfully backed your contact data to the cloud","",null,iNotifyCloudBackup);
								dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
							}
							else if(mActivityTabs instanceof MainBaseActivity)
							{
								dialogResult.newInstance("",((MainBaseActivity)mActivityTabs),"Successfully backeddata to the cloud","",null,iNotifyCloudBackup);
								dialogResult.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(), "test");
							}
						}
						saveState.setUploadBackupRequested(mActivityTabs, false);
					}
					else if (get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE) ||
							get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_1) ||
							get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_2)  ||
							get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_4))
					{
						if(isDialogShow)
						{
							ImageRequestDialog dialogResult=new ImageRequestDialog();
							dialogResult.setCancelable(false);
							dialogResult.newInstance("",mActivityTabs,get_Response.getMessage(),"",null);
							if(mActivityTabs instanceof HomeScreenActivity)
							{
								dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
							}
							else if(mActivityTabs instanceof MainBaseActivity)
							{
								dialogResult.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(), "test");
							}
						}
						//				ShowDialog.alert(mActivityTabs, "", get_Response.getMessage());
					}

				} catch (Exception e) {
					e.getMessage();
				}

			}
			else
			{
				Log.d("improper_response",response);
				ShowDialog.alert(mActivityTabs, "",mActivityTabs.getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
}

