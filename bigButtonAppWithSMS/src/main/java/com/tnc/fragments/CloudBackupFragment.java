package com.tnc.fragments;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CloudBackupAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CloudBackupSamePhoneBean;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.common.UploadBackupServer;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.NoBackupFoundDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CloudRecoverSamePhoneResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display cloud backup option for the registered user 
 *  @author a3logics
 */


public class CloudBackupFragment extends BaseFragmentTabs 
{
	private FrameLayout flBackArrow,flInformationButton;
	private Button btnBack,btnHome;
	private TextView tvTitle,tvCloudBackupMenu;
	private ListView lvCloudBackupMenu;
	private ArrayList<String> listCloudBackupMenu;
	private ArrayList<String> listCloudBackupMenuDetails;
	private CloudBackupAdapter adapterCloudBackup;
	private File fileDatabase;
	private Gson gson;
	private TransparentProgressDialog  progress;
	private SharedPreference saveState;
	private File fileBackup;
	private File fileServerBackup;
	private String path="";
	private int adapterSelected_position;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((HomeScreenActivity) activity).setINotifyCloudBackup(iNotifyCloudBackup);
	}

	/**
	 * interface to notify the list of deselecting the selected position
	 */
	INotifyGalleryDialog iNotifyCloudBackup = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterCloudBackup.setRowColor(adapterSelected_position, false); 			

		}

		@Override
		public void no() {
			adapterCloudBackup.setRowColor(adapterSelected_position, false); 			
		}
	};

	public CloudBackupFragment newInstance()
	{
		CloudBackupFragment frag = new CloudBackupFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.cloudbackup, container, false);
		idInitialization(view);
		return view;
	}

	INotifyGalleryDialog iObject =new INotifyGalleryDialog() {

		@Override
		public void yes() {
			//Recover Backup from Current Registered User
			adapterCloudBackup.setRowColor(adapterSelected_position, false); 	
			MainBaseActivity.recoveryType = "current_backup";
			checkInternetConnectionCurrentBackUp();
		}

		@Override
		public void no() {
			//			Recover Backup from Old Archive Data
			/*MainBaseActivity.recoveryType = "archival_backup";
			adapterCloudBackup.setRowColor(adapterSelected_position, false); 	
			CloudRecoverNewPhoneFragment cloudRecoverNewPhone=new CloudRecoverNewPhoneFragment();
			if(mActivityTabs instanceof MainBaseActivity)
			{
				cloudRecoverNewPhone.newInstance(((MainBaseActivity)mActivityTabs),lvCloudBackupMenu,iNotifyCloudBackup);
				((MainBaseActivity)mActivityTabs).setFragment(cloudRecoverNewPhone);
			}
			else if(mActivityTabs instanceof HomeScreenActivity)
			{
				cloudRecoverNewPhone.newInstance(((HomeScreenActivity)mActivityTabs),lvCloudBackupMenu,iNotifyCloudBackup);
				((HomeScreenActivity)mActivityTabs).setFragment(cloudRecoverNewPhone);
			}
			MainBaseActivity.isReturningUser=false;*/
		}
	};

	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvCloudBackupMenu=(TextView) view.findViewById(R.id.tvCloudBackupMenu);
		lvCloudBackupMenu=(ListView) view.findViewById(R.id.lvCloudBackupMenu);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		flInformationButton=(FrameLayout) view.findViewById(R.id.flInformationButton);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/StencilStd.otf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvCloudBackupMenu, "fonts/Roboto-Bold_1.ttf");
		listCloudBackupMenu=new ArrayList<String>();
		listCloudBackupMenuDetails=new ArrayList<String>();
		listCloudBackupMenu.add("Perform Manual Backup");
		listCloudBackupMenu.add("Recover Cloud Backup");
		//		listCloudBackupMenu.add("Recover Cloud Backup on \nNew Phone");
		flBackArrow.setVisibility(View.VISIBLE);	
		flInformationButton.setVisibility(View.VISIBLE);		
		btnHome.setVisibility(View.VISIBLE);	
		adapterCloudBackup=new CloudBackupAdapter(mActivityTabs, listCloudBackupMenu,listCloudBackupMenuDetails);
		lvCloudBackupMenu.setAdapter(adapterCloudBackup);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(mActivityTabs instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();	
				}
			}
		});

		btnHome.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(mActivityTabs instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
					((HomeScreenActivity)mActivityTabs).finish();
				}	
			}
		});
		lvCloudBackupMenu.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View rowView, int position,
					long arg3) 
			{
				if(position==0)  // in case of uploading backup to the server
				{
					adapterSelected_position = position;
					adapterCloudBackup.setRowColor(adapterSelected_position, true);
					if(DBQuery.getAllTiles(mActivityTabs).size()==0)
					{
						if(mActivityTabs instanceof MainBaseActivity)
						{
							ImageRequestDialog dialogNoTile=new ImageRequestDialog();
							dialogNoTile.setCancelable(false);
							dialogNoTile.newInstance("",((MainBaseActivity)mActivityTabs),
									"Please create at least one tile to take backup","",null,iNotifyCloudBackup);
							dialogNoTile.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(),"test");
						}
						else if(mActivityTabs instanceof HomeScreenActivity)
						{
							ImageRequestDialog dialogNoTile=new ImageRequestDialog();
							dialogNoTile.setCancelable(false);
							dialogNoTile.newInstance("",((HomeScreenActivity)mActivityTabs),
									"Please create at least one tile to take backup","",null,iNotifyCloudBackup);
							dialogNoTile.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");
						}
					}

					else if(!saveState.isChanged(mActivityTabs))
					{
						ImageRequestDialog dialog=new ImageRequestDialog();
						dialog.setCancelable(false);
						dialog.newInstance("", mActivityTabs,"A current cloud backup already exists for your account","",null,iNotifyCloudBackup);
						dialog.show(getChildFragmentManager(),"test");
					}

					else if(saveState.isChanged(mActivityTabs))
					{
						checkInternetConnectionPerformBackUp();
						//						uploadBackup();
					}
				}
				else if(position==1) // in case of recovering backup
				{
					iNotifyCloudBackup.yes();
					iObject.yes();
				}
			}
		});

		HomeScreenActivity.btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				HomeScreenActivity.setBackButtonFunctionality("cloudbackup");
			}
		});
	}
	/**
	 * check availability of the internet connection
	 */
	public void checkInternetConnectionPerformBackUp() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			UploadBackupServer objUploadBackup=null;
			if(mActivityTabs instanceof MainBaseActivity)
			{
				objUploadBackup=new UploadBackupServer(((MainBaseActivity)mActivityTabs),true,iNotifyCloudBackup);
			}
			else if(mActivityTabs instanceof HomeScreenActivity)
			{
				objUploadBackup=new UploadBackupServer(((HomeScreenActivity)mActivityTabs),true,iNotifyCloudBackup);
			}
			objUploadBackup.uploadBackup();
		} 
		else
		{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
		}
	}

	/**
	 * check internet availability
	 */
	public void checkInternetConnectionCurrentBackUp() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) 
		{
			CloudBackupSamePhoneBean cloudRecoverSamePhone=new 
					CloudBackupSamePhoneBean("1",saveState.getDeviceId(mActivityTabs),
							"",saveState.getBackupKey(mActivityTabs));
			getBackupListRequest(cloudRecoverSamePhone);
		}
		else
		{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
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
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs, 
					GlobalCommonValues.GET_ALL_BACKUP,
					stringEntity, cloudRecoverSamePhoneResponseHandle,
					mActivityTabs.getString(R.string.private_key),"");
		}
		catch (Exception e) 
		{
			e.getMessage();
		}
	}

	//async task  for handling the request made to recover cloud back
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
			String response2="";
			if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
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
				}
				BackupListFragment backupListFragment=new BackupListFragment();
			
				if(mActivityTabs instanceof MainBaseActivity)
				{
					backupListFragment.newInstance(((MainBaseActivity)mActivityTabs));
					((MainBaseActivity)mActivityTabs).setFragment(backupListFragment);
				}
				else if(mActivityTabs instanceof HomeScreenActivity)
				{
					backupListFragment.newInstance(((HomeScreenActivity)mActivityTabs));
					((HomeScreenActivity)mActivityTabs).setFragment(backupListFragment);
				}
				MainBaseActivity.isBackButtonToDisplay=true;
				MainBaseActivity.isReturningUser=false;
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
		super.onPause();
		lvCloudBackupMenu.setEnabled(false);
		lvCloudBackupMenu.setClickable(false);
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		lvCloudBackupMenu.setEnabled(true);
		lvCloudBackupMenu.setClickable(true);
		if(HomeScreenActivity.btnNotification!=null && HomeScreenActivity.btnAddTile!=null)
		{
			HomeScreenActivity.btnNotification.setEnabled(false);
			HomeScreenActivity.btnNotification.setClickable(false);
			HomeScreenActivity.btnAddTile.setEnabled(false);
			HomeScreenActivity.btnAddTile.setClickable(false);
		}
		adapterCloudBackup.notifyDataSetChanged();
		lvCloudBackupMenu.setAdapter(adapterCloudBackup);
	}
}
