package com.tnc.dialog;

import java.util.ArrayList;

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
import com.tnc.fragments.CloudRecoverSamePhone;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CloudRecoverSamePhoneResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

public class ReturningUserDialog extends DialogFragment implements OnClickListener 
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Activity mAct;
	private 	String title="",message="",messageSub="";
	private 	Gson gson;
	private TransparentProgressDialog progress;
	private SharedPreference saveState;

	public ReturningUserDialog newInstance(String title, Activity mAct,String message,
			String messageSub)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		ReturningUserDialog frag = new ReturningUserDialog();
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
		View view = inflater.inflate(R.layout.returning_user_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo= (Button) view.findViewById(R.id.btnNo);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		saveState=new SharedPreference();
		progress=new TransparentProgressDialog(mAct, R.drawable.customspinner);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);	
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

	/**
	 * check availability of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mAct)) {
			CloudBackupSamePhoneBean cloudRecoverSamePhone=new 
					CloudBackupSamePhoneBean("1",saveState.getDeviceId(mAct),
							"",saveState.getBackupKey(mAct));
			getBackupListRequest(cloudRecoverSamePhone);
		} else
			ShowDialog.alert(mAct, "", mAct.getResources()
					.getString(R.string.no_internet_abvailable));
	}

	/**
	 * get List of backups in case of returning user has existed cloud backup and clicks yes
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
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null)
				{
					Logs.writeLog("ReturningUserDialog", "OnSuccess",response.toString());
					getResponseBackupSamePhone(response.toString());				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("ReturningUserDialog", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/**
	 * handle response of the server with request to the backup list requested
	 * @param response
	 */
	private void getResponseBackupSamePhone(String response)
	{
		try {

			gson=new Gson();
			ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
			CloudRecoverSamePhoneResponse get_Response = gson.fromJson(response,CloudRecoverSamePhoneResponse.class);
			if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) 
			{
				GlobalCommonValues.listBackups=new ArrayList<CloudRecoverBackupListingBean>();
				GlobalCommonValues.listBackups.clear();
				CloudRecoverBackupListingBean objListingBean=null;
				for(int i=0;i<get_Response.getData().size();i++)
				{
					objListingBean=new CloudRecoverBackupListingBean();
					objListingBean.setDatetime(get_Response.getData().get(i).getDatetime());
					objListingBean.setUrl(get_Response.getData().get(i).getUrl());
					GlobalCommonValues.listBackups.add(objListingBean);
				}
				BackupListFragment objBackupListingfragment=new BackupListFragment();
				if(mAct instanceof MainBaseActivity)
				{
					objBackupListingfragment.newInstance(((MainBaseActivity)mAct));
					((MainBaseActivity)mAct).setFragment(objBackupListingfragment);
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					objBackupListingfragment.newInstance(((HomeScreenActivity)mAct));
					((HomeScreenActivity)mAct).setFragment(objBackupListingfragment);	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE)) 
			{
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorMessage.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorMessage.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1)) 
			{
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorMessage.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorMessage.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5)) 
			{
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorMessage.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorMessage.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)) 
			{
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorMessage.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorMessage.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_7)) 
			{
				if(mAct instanceof MainBaseActivity)
				{
					dialogErrorMessage.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogErrorMessage.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"",null);
					dialogErrorMessage.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_601)) 
			{
				NoBackupFoundDialog dialogNoBackup=null;
				if(mAct instanceof MainBaseActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.newInstance("",((MainBaseActivity)mAct),get_Response.getMessage(),"");
					dialogNoBackup.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"test");
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.newInstance("",((HomeScreenActivity)mAct),get_Response.getMessage(),"");
					dialogNoBackup.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"test");
				}
			}
			else
			{
				Log.d("improper_response",response);
				ShowDialog.alert(mAct, "",getResources().getString(R.string.improper_response));	
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)
		{
			dismiss();
			if(message.contains("cloud backup exists"))
			{
				saveState.setRegistered(mAct, true);
				checkInternetConnection();
			}
			else if(message.contains("user registration exists"))
			{
				CloudRecoverSamePhone cloudRecoverSamePhoneFragment=new CloudRecoverSamePhone();
				if(mAct instanceof MainBaseActivity)
				{
					cloudRecoverSamePhoneFragment.newInstance(((MainBaseActivity)mAct),null,null);
					((MainBaseActivity)mAct).setFragment(cloudRecoverSamePhoneFragment);
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					cloudRecoverSamePhoneFragment.newInstance(((HomeScreenActivity)mAct),null,null);
					((HomeScreenActivity)mAct).setFragment(cloudRecoverSamePhoneFragment);
				}
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
			saveState.setPublicKey(mAct, "");
			saveState.setRegistered(mAct, false);
			if(message.contains("cloud backup exists") || message.contains("user registration exists"))
			{
				if(mAct instanceof MainBaseActivity)
				{
					((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
					((MainBaseActivity)mAct).finish();
				}
				else if(mAct instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
					((HomeScreenActivity)mAct).finish();
				}
			}
		}
	}
}
