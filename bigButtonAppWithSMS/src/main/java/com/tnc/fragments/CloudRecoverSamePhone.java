package com.tnc.fragments;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CloudBackupSamePhoneBean;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.NoBackupFoundDialog;
import com.tnc.dialog.RecoveryConfirmationAlertDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CloudRecoverSamePhoneResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

public class CloudRecoverSamePhone extends BaseFragmentTabs implements OnClickListener
{
	private FrameLayout flBackArrow,flInformationButton;
	private TextView tvTitle,tvRecover,tvSamePhone,tvSubMessage,tvInformation;
	private EditText etBackupKey;
	private Button btnBack,btnSubmit,btnHome;
	private Context mActivity;
	private Gson gson;
	private TransparentProgressDialog progress;
	private ListView lvCloudBackupMenu;
	private INotifyGalleryDialog iNotifyCloudBackup;

	public CloudRecoverSamePhone newInstance(Context mActivity,ListView lvCloudBackupMenu,
			INotifyGalleryDialog iNotifyCloudBackup)
	{
		CloudRecoverSamePhone frag = new CloudRecoverSamePhone();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.lvCloudBackupMenu=lvCloudBackupMenu;
		if(this.lvCloudBackupMenu!=null)
		{
			this.lvCloudBackupMenu.setEnabled(false);
			this.lvCloudBackupMenu.setClickable(false);
		}
		this.iNotifyCloudBackup=iNotifyCloudBackup;
		return frag;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(iNotifyCloudBackup!=null)
			iNotifyCloudBackup.no();
		if(this.lvCloudBackupMenu!=null)
		{
			this.lvCloudBackupMenu.setEnabled(true);
			this.lvCloudBackupMenu.setClickable(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.cloudrecoversamephone, container, false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		progress=new TransparentProgressDialog(mActivity, R.drawable.customspinner);
		saveState=new SharedPreference();
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);	
		tvRecover=(TextView) view.findViewById(R.id.tvRecover);
		tvSamePhone=(TextView) view.findViewById(R.id.tvSamePhone);
		tvSubMessage=(TextView) view.findViewById(R.id.tvSubMessage);
		tvInformation=(TextView) view.findViewById(R.id.tvInformation);
		etBackupKey=(EditText) view.findViewById(R.id.etBackupKey);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnSubmit=(Button) view.findViewById(R.id.btnSubmit);
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		flBackArrow.setVisibility(View.VISIBLE);
		tvRecover.setText("Recover Cloud Backup on");
		tvSamePhone.setText("Same Phone");
		tvSubMessage.setText("Set up perspectives");
		tvSubMessage.setVisibility(View.INVISIBLE);
		tvInformation.setText("Please provide backup key");
		etBackupKey.setHint("Enter BackupKey");
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		etBackupKey.setHintTextColor(getResources().getColor(android.R.color.black));
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle,"fonts/StencilStd.otf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvRecover,"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvSamePhone,"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvSubMessage,"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvInformation,"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etBackupKey,"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnSubmit, "fonts/Roboto-Regular_1.ttf");
		btnBack.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
	}

	/**
	 * check internet availability
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) 
		{
			CloudBackupSamePhoneBean cloudRecoverSamePhone=new 
					CloudBackupSamePhoneBean("1",saveState.getDeviceId(mActivity),
							"",etBackupKey.getText().toString().trim());
			getBackupListRequest(cloudRecoverSamePhone);
		}
		else
		{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
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
			MyHttpConnection.postWithJsonEntityHeader(mActivity, 
					GlobalCommonValues.GET_ALL_BACKUP,
					stringEntity, cloudRecoverSamePhoneResponseHandle,
					mActivity.getString(R.string.private_key),"");
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
					Logs.writeLog("CloudRecoverSamePhone", "OnSuccess",response.toString());
					getResponseBackupSamePhone(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("CloudRecoverSamePhone", "OnFailure",response);
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
			gson=new Gson();
			ImageRequestDialog dialogErrorResponse=new ImageRequestDialog();
			dialogErrorResponse.setCancelable(false);
			CloudRecoverSamePhoneResponse get_Response = gson.fromJson(response,CloudRecoverSamePhoneResponse.class);
			if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) 
			{
				/*if(!GlobalCommonValues.Public_Key.trim().equals(""))
				{
//					saveState.setPublicKey(getActivity(),GlobalCommonValues.Public_Key);
//					GlobalCommonValues.Public_Key="";
				}*/
				GlobalCommonValues.listBackups.clear();
				CloudRecoverBackupListingBean objListingBean;
				/*saveState.setRegistered(mActivity, true);*/
				if(get_Response.getData().size()>0)
				{
					saveState.setPublicKey(mActivity,get_Response.getData().get(0).getPublic_key());
				}
				//			int listSize=0;
				//			if(get_Response.getData().size()>3)
				//				listSize=3;
				//			else 
				//				listSize=get_Response.getData().size();
				for(int i=0;i<get_Response.getData().size();i++)
				{
					objListingBean=new CloudRecoverBackupListingBean();
					objListingBean.setDatetime(get_Response.getData().get(i).getDatetime());
					objListingBean.setUrl(get_Response.getData().get(i).getUrl());
					GlobalCommonValues.listBackups.add(objListingBean);
				}
				RecoveryConfirmationAlertDialog dialog=new RecoveryConfirmationAlertDialog();
				dialog.newInstance("", mActivityTabs,"All your exisitng tiles will be lost","",null);
				dialog.show(getChildFragmentManager(), "test");
				/*BackupListFragment backupListFragment=new BackupListFragment();
				if(mActivityTabs instanceof MainBaseActivity)
				{
					backupListFragment.newInstance(((MainBaseActivity)mActivityTabs));
					((MainBaseActivity)mActivityTabs).setFragment(backupListFragment);
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					backupListFragment.newInstance(((HomeScreenActivity)mActivityTabs));
					((HomeScreenActivity)mActivityTabs).setFragment(backupListFragment);
				}*/
				/*KeyMatchBackupDialog dialogKeyMatch=new KeyMatchBackupDialog();
				dialogKeyMatch.setCancelable(false);
				if(mActivity instanceof MainBaseActivity)
				{
					dialogKeyMatch.newInstance("",((MainBaseActivity)mActivity),
							Html.fromHtml("The key provided has<br>matched successfully").toString(),
							"");
					dialogKeyMatch.show(((MainBaseActivity)mActivity).getSupportFragmentManager(), "test");
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					dialogKeyMatch.newInstance("",((HomeScreenActivity)mActivity),
							Html.fromHtml("The key provided has<br>matched successfully").toString(),
							"");
					dialogKeyMatch.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(), "test");
				}*/
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE)) 
			{
				if(mActivityTabs instanceof MainBaseActivity)			
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1)) 
			{
				if(mActivityTabs instanceof MainBaseActivity)			
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5)) 
			{
				if(mActivityTabs instanceof MainBaseActivity)			
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)) 
			{
				if(mActivityTabs instanceof MainBaseActivity)			
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_7)) 
			{
				if(mActivityTabs instanceof MainBaseActivity)			
				{
					dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"",null);
					dialogErrorResponse.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");	
				}
			}
			else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_601)) 
			{
				NoBackupFoundDialog dialogNoBackup=null;
				if(mActivityTabs instanceof MainBaseActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",((MainBaseActivity)mActivityTabs),get_Response.getMessage(),"");
					dialogNoBackup.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					dialogNoBackup=new NoBackupFoundDialog();
					dialogNoBackup.setCancelable(false);
					dialogNoBackup.newInstance("",((HomeScreenActivity)mActivityTabs),get_Response.getMessage(),"");
					dialogNoBackup.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");
				}
			}
			else
			{
				//ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));	
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onPause() {
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etBackupKey);
		super.onPause();
		//		InputMethodManager imm = (InputMethodManager)mActivity.getSystemService( Context.INPUT_METHOD_SERVICE);
		//		imm.hideSoftInputFromWindow(etBackupKey.getWindowToken(), 0);
		//		etBackupKey.setText("");
	}

	/*INotifyGalleryDialog iNotifyRecovery=new INotifyGalleryDialog()
	{
		@Override
		public void yes() 
		{
			checkInternetConnection();
		}

		@Override
		public void no() {
		}
	};*/

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnBack)
		{
			if(mActivityTabs instanceof MainBaseActivity)
			{
				((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
			}
			else if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
				((HomeScreenActivity)mActivityTabs).performINotifyCloudBackup();
			}
		}
		else if(v.getId()==R.id.btnSubmit)
		{
			if(!etBackupKey.getText().toString().trim().equals(""))
			{
				checkInternetConnection();
				/*if(saveState.isChanged(mActivityTabs))
				{
					RecoveryConfirmationAlertDialog dialog=new RecoveryConfirmationAlertDialog();
					dialog.newInstance("", mActivityTabs,"All your exisitng tiles will be lost","",iNotifyRecovery);
					dialog.show(getChildFragmentManager(), "test");
				}
				else{
					checkInternetConnection();
				}*/
			}
			else if(etBackupKey.getText().toString().trim().equals("")) 
			{
				ImageRequestDialog dialogImageRequest =new ImageRequestDialog();
				dialogImageRequest.setCancelable(false);
				dialogImageRequest.newInstance("", mActivity, "Please provide backup key","",null);
				dialogImageRequest.show(getChildFragmentManager(), "test");
			}
		}
		else if(v.getId()==R.id.btnHome)
		{
			if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
				((HomeScreenActivity)mActivityTabs).finish();
			}	
		}
	}
}
