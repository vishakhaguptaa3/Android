package com.tnc.dialog;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.CloudBackupSamePhoneBean;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.fragments.BackupListFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CloudRecoverSamePhoneResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

public class WelcomeBackReg_RestoreDialog  extends DialogFragment implements OnClickListener {
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private Gson gson;
	private TransparentProgressDialog progress;
	private SharedPreference saveState;
	public WelcomeBackReg_RestoreDialog newInstance(String title, Context mAct,String message,
			String messageSub)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		WelcomeBackReg_RestoreDialog frag = new WelcomeBackReg_RestoreDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
	}
	@Override
	public void onStart()
	{
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.6f;
		window.setAttributes(params);
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		progress=new TransparentProgressDialog(mAct, R.drawable.customspinner);
		saveState=new SharedPreference();
		View view = inflater.inflate(R.layout.welcomeback, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo= (Button) view.findViewById(R.id.btnNo);
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		updateView();
		return view;
	}
	private void updateView()
	{
		tvTitle.setText(title);
		tvMessage.setText(message);
		if(tvTitle.getText().toString().equals(""))
			tvTitle.setVisibility(View.GONE);
		tvMessage.setText(message);
		tvMessageSub.setText(messageSub);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(messageSub.trim().equals(""))
		{
			tvMessageSub.setVisibility(View.GONE);
		}
		else{
			tvMessageSub.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			checkInternetConnectionCurrentBackUp();
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
			if (mAct instanceof MainBaseActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
			MainBaseActivity.isReturningUser=false;
		}
	}

	/**
	 * check internet availability
	 */
	public void checkInternetConnectionCurrentBackUp() {
		if (NetworkConnection.isNetworkAvailable(mAct))
		{
			CloudBackupSamePhoneBean cloudRecoverSamePhone=new
					CloudBackupSamePhoneBean("1",saveState.getDeviceId(mAct),
							"",saveState.getBackupKey(mAct));
			getBackupListRequest(cloudRecoverSamePhone);
		}
		else
		{
			ShowDialog.alert(mAct, "", mAct.getResources()
					.getString(R.string.no_internet_abvailable));
		}
	}

	/**
	 * request to the server
	 * @param cloudRecoverSamePhone
	 */
	private void getBackupListRequest(CloudBackupSamePhoneBean cloudRecoverSamePhone)
	{
		try
		{
			gson=new Gson();
			String stingGson = gson.toJson(cloudRecoverSamePhone);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mAct,
					GlobalCommonValues.GET_ALL_BACKUP,
					stringEntity, cloudRecoverSamePhoneResponseHandle,
					mAct.getString(R.string.private_key),"");
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	AsyncHttpResponseHandler cloudRecoverSamePhoneResponseHandle = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
//			if (!(progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null){
					Logs.writeLog("WelcomeBackReg_RestoreDialog", "OnSuccess",response.toString());
					getResponseBackupSamePhone(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("WelcomeBackReg_RestoreDialog", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/**
	 * handling response from the server for the request being made  
	 * @param response
	 */
	private void getResponseBackupSamePhone(String response)
	{
		try
		{
			String response2="";
			if(response.contains("</div>") || response.contains("<h4>") || response.contains("php"))
			{
				response2=response.substring(response.indexOf("response_code")-2,response.length());
			}
			else{
				response2=response;
			}
			gson=new Gson();
			ImageRequestDialog dialogErrorResponse=new ImageRequestDialog();
			dialogErrorResponse.setCancelable(false);
			CloudRecoverSamePhoneResponse get_Response = gson.fromJson(response2,CloudRecoverSamePhoneResponse.class);
			if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE))
			{
				GlobalCommonValues.listBackups.clear();
				CloudRecoverBackupListingBean objListingBean;

				for(int i=0;i<get_Response.getData().size();i++)
				{
					objListingBean=new CloudRecoverBackupListingBean();
					objListingBean.setDatetime(get_Response.getData().get(i).getDatetime());
					objListingBean.setUrl(get_Response.getData().get(i).getUrl());
					GlobalCommonValues.listBackups.add(objListingBean);
				}//All your exisitng tiles will be lost
				MainBaseActivity.recoveryType="current_backup";
				dismiss();
				BackupListFragment backupListFragment=new BackupListFragment();
				if(mAct instanceof MainBaseActivity)
				{
					backupListFragment.newInstance(((MainBaseActivity)mAct));
					((MainBaseActivity)mAct).setFragment(backupListFragment);
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					backupListFragment.newInstance(((HomeScreenActivity)mAct));
					((HomeScreenActivity)mAct).setFragment(backupListFragment);
				}
				MainBaseActivity.isBackButtonToDisplay=false;
				MainBaseActivity.isReturningUser=true;
				dismiss();
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
					get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
					get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
					get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6) ||
					get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_7))
			{
				dismiss();
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}

			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_601))
			{
				dismiss();
				NoBackupFoundDialog dialogNoBackup=null;
				if(mAct instanceof MainBaseActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"");
					dialogNoBackup.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"");
					dialogNoBackup.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else
			{
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}
}
