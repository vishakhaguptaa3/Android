package com.tnc.fragments;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.GCMIntentService;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.NotificationsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.NotificationBean;
import com.tnc.bean.NotificationReponseDataBean;
import com.tnc.bean.NotificationUpdateBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.swipeandpulllibrary.SwipeMenu;
import com.tnc.swipeandpulllibrary.SwipeMenuCreator;
import com.tnc.swipeandpulllibrary.SwipeMenuItem;
import com.tnc.swipeandpulllibrary.SwipeMenuListView;
import com.tnc.swipeandpulllibrary.SwipeMenuListView.OnMenuItemClickListener;
import com.tnc.swipeandpulllibrary.SwipeMenuListView.OnRefreshListener;
import com.tnc.swipeandpulllibrary.SwipeMenuListView.OnSwipeListener;
import com.tnc.utility.Logs;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.webresponse.NotificationResponse;
import com.tnc.webresponse.NotificationUpdateResponse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to diplay notifications 
 *  @author a3logics
 */

public class NotificationsFragment extends BaseFragmentTabs
{
	private FrameLayout flBackArrow;
	private Button btnBack;
	private TextView tvTitle,tvNotifications;
	//	private PullToRefreshListView lvNotifications;
	private SwipeMenuListView lvNotifications;
	private NotificationsAdapter adapterNotifications;
	private int adapterSelected_position = 0;
	private Gson gson;
	private TransparentProgressDialog progress;
	private boolean isResumed=false;
	private ArrayList<NotificationReponseDataBean> listDataNotifications=new ArrayList<NotificationReponseDataBean>();
	private int startIndex=0;
	private int notificationID=0;
	private FrameLayout flInformationButton;
	private Button btnHome;
	private INotifyGalleryDialog iNotifyObject;
	private Context mContext;
	private int notificationUpdateId=0;
	public static String displayName="",displayMessage="";
	public static Bitmap displayImage=null;
	private int notification_id,mNotificationPosition ;
	private boolean is_delete = false;


	public NotificationsFragment newInstance(Context mContext,INotifyGalleryDialog iNotifyObject)
	{
		NotificationsFragment frag = new NotificationsFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.mContext=mContext;
		this.iNotifyObject=iNotifyObject;
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.notifications,container, false);
		idInitialization(view);
		return view;
	}

	// Method to initialize views/widgets
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		lvNotifications=(SwipeMenuListView) view.findViewById(R.id.lvNotifications);
		tvNotifications=(TextView) view.findViewById(R.id.tvNotifications);
		flInformationButton=(FrameLayout) view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		flBackArrow.setVisibility(View.VISIBLE);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		//		CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/StencilStd.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvNotifications, "fonts/Roboto-Bold_1.ttf");
		displayName="";
		displayMessage="";
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
					MainBaseActivity.isFromMain=true;
					((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
					((HomeScreenActivity)mActivityTabs).finish();
				}
			}
		});

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivityTabs);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(252, 59,
						50)));// Color.rgb(0xF9,0x3F, 0x25)
				// set item width
				deleteItem.setWidth(dp2px(90));
				/*
				 * // set a icon deleteItem.setIcon(R.drawable.ic_delete);
				 */
				// set item title
				deleteItem.setTitle("Delete");
				// set item title fontsize
				deleteItem.setTitleSize(18);
				// set item title font color
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		lvNotifications.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean boolValue=false;

				//				//system.out.println(((SwipeMenuListView)v));
				/*if (!adapterNotifications.listDataNotifications.get(0).getMessage().contains("Welcome")){
					boolValue=false;
				}
				else if (adapterNotifications.listDataNotifications.get(0).getMessage().contains("Welcome")){
					boolValue=true;
				}*/
				return boolValue;
			}
		});

		// set creator
		lvNotifications.setMenuCreator(creator);

		// set SwipeListener
		lvNotifications.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
			}

			@Override
			public void onSwipeEnd(int position) {
			}
		});

		lvNotifications.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
									long arg3)
			{
				try {
					//get details of the selected notificationcountrycode + "-" + number
					int NotificationStatus=-1;
					String NotificationId="";
					adapterSelected_position = position;
					adapterNotifications.setRowColor(adapterSelected_position, true);
					NotificationStatus=listDataNotifications.get(position).getStatus();
					NotificationId=listDataNotifications.get(position).getId(); //1-request
					NotificationDetailFragment objNotificationDetail=new NotificationDetailFragment();

					String from_phone_number = "";

					int type = -1;
					try{
						type = Integer.parseInt(listDataNotifications.get(position).getType());
					}catch(Exception e){
						e.getMessage();
					}
					if(type == 15){
						if(listDataNotifications.get(position).getFrom_user_phone()!=null && !listDataNotifications.get(position).getFrom_user_phone().trim().equalsIgnoreCase("")){
							from_phone_number = listDataNotifications.get(position).getFrom_user_phone();
						}
					}

					objNotificationDetail.newInstance(listDataNotifications.get(position).message,
							listDataNotifications.get(position).getImage(),
							String.valueOf(listDataNotifications.get(position).datetime),
							NotificationStatus,
							NotificationId,
							listDataNotifications.get(position).getTo_user_id(),
							listDataNotifications.get(position).getFrom_user_id(),
							from_phone_number,
							type);
					try {
						TextView tvNotificationHeading=(TextView)view.findViewById(R.id.llNotificationHolder).findViewById(R.id.flNotificationHeading).findViewById(R.id.tvNotification);
						TextView tvNotificationDetail=(TextView)view.findViewById(R.id.llNotificationHolder).findViewById(R.id.tvNotificationDetail);
						RoundedImageViewCircular imviewNotification=(RoundedImageViewCircular)view.findViewById(R.id.llImageHolderparent).findViewById(R.id.llImageBoundary).findViewById(R.id.imViewNotificationContact);
						if((((RoundedImageViewCircular)imviewNotification).getDrawable())!=null && ((BitmapDrawable)((RoundedImageViewCircular)imviewNotification).getDrawable()).getBitmap()!=null)
							displayImage=((BitmapDrawable)((RoundedImageViewCircular)imviewNotification).getDrawable()).getBitmap();
						else
							displayImage=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
						displayName=tvNotificationHeading.getText().toString();
						displayMessage=tvNotificationDetail.getText().toString();
					} catch (Exception e) {
						e.getMessage();
					}

					if(NotificationStatus==1)
					{
						if (NetworkConnection.isNetworkAvailable(mActivityTabs))
						{
							try {
								notificationUpdateId=Integer.parseInt(listDataNotifications.get(position).id);
								NotificationUpdateBean notificationUpdateBean=new NotificationUpdateBean(Integer.parseInt(listDataNotifications.get(position).id),
										2,"no");
								updateNotification(notificationUpdateBean);
							} catch (Exception e) {
								e.getMessage();
							}
						}
					}
					((HomeScreenActivity)mActivityTabs).setFragment(objNotificationDetail);

				} catch (Exception e) {
					e.getMessage();
				}
			}
		});

		lvNotifications
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(int position,
												   SwipeMenu menu, int index) {
						switch (index) {
							case 0:
								if (!adapterNotifications.listDataNotifications.get(position).getMessage().contains("Welcome")) {
									notification_id = Integer.parseInt(adapterNotifications.listDataNotifications.get(position).id);
									mNotificationPosition = position;
									iNotifyGalleryDialog.yes();
						/*MessageDeleteConfirmation dialogMessageDelete = new MessageDeleteConfirmation();
						dialogMessageDelete
						.newInstance(
								"",
								((HomeScreenActivity) mActivityTabs),
								"Are you sure you want to delete this notification",
								"", iNotifyGalleryDialog);
						dialogMessageDelete.setCancelable(false);
						dialogMessageDelete.show(
								((HomeScreenActivity) mActivityTabs)
								.getSupportFragmentManager(),
								"test");*/
								}else if (adapterNotifications.listDataNotifications.get(position).getMessage().contains("Welcome")) {
									ImageRequestDialog mDialog = new ImageRequestDialog();
									mDialog.setCancelable(false);
									mDialog.newInstance("",getActivity(), "Welcome message cannot be deleted", "",null);
									mDialog.show(getChildFragmentManager(), "test");
								}
								break;
						}
						return false;
					}
				});

		/*lvNotifications.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list contents goes here
				// for example:
				// If this is a webservice call, it might be asynchronous so
				// you would have to call listView.onRefreshComplete(); when
				// the webservice returns the data
				//				adapter.loadData();
				//				startIndex=listDataNotifications.size()+1;
				notificationID=DBQuery.getNotificationsMaxCount(mActivityTabs);
				checkInternetConnection();

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				lvNotifications.postDelayed(new Runnable() {
					@Override
					public void run() 
					{
						lvNotifications.onRefreshComplete();
					}
				}, 2500);
			}
		});*/

		// Refresh List
		lvNotifications.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list contents goes here
				// for example:
				// If this is a webservice call, it might be asynchronous so
				// you would have to call listView.onRefreshComplete(); when
				// the webservice returns the data
				//				adapter.loadData();
				//				startIndex=listDataNotifications.size()+1;
				notificationID=DBQuery.getNotificationsMaxCount(mActivityTabs);
				checkInternetConnection();

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				lvNotifications.postDelayed(new Runnable() {
					@Override
					public void run()
					{
						lvNotifications.onRefreshComplete();
					}
				}, 2500);
			}
		});
		checkInternetConnection();
		lvNotifications.requestFocus();

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	// display list of available notifications from the database
	private void displayDataFromDatabase()
	{
		listDataNotifications=new ArrayList<NotificationReponseDataBean>();
		listDataNotifications=DBQuery.getAllNotifications(mActivityTabs);
		if(!listDataNotifications.isEmpty())
		{
			adapterNotifications=new NotificationsAdapter(mActivityTabs, listDataNotifications);
			adapterNotifications.notifyDataSetChanged();
			lvNotifications.setAdapter(adapterNotifications);
		}
	}

	/**
	 * request for updating notification list on the server
	 * @param notificationUpdateBean
	 */
	private void updateNotification(NotificationUpdateBean notificationUpdateBean) {
		try
		{
			gson=new Gson();
			String stingGson = gson.toJson(notificationUpdateBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.UPDATE_NOTIFICATION_STATUS,
					stringEntity, notificationUpdateResponseHandler,
					mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	//async task to handle response for the request being made to update notification status
	AsyncHttpResponseHandler notificationUpdateResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			//			testCopy();
			try {
				if(response!=null)
				{
					Logs.writeLog("NotificationUpdate", "OnSuccess",response.toString());
					getResponseNotificationUpdate(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("NotificationUpdate", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/**
	 * handle response for the request being made to update notification status
	 *
	 * @param response
	 */
	private void getResponseNotificationUpdate(String response)
	{
		try
		{
			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response))
			{
				gson=new Gson();
				NotificationUpdateResponse get_Response = gson.fromJson(response,NotificationUpdateResponse.class);
				if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
				{
					DBQuery.updateNotification(mActivityTabs,get_Response.getData.notification_id);
				}
				else if(get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_1) ||
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_2) ||
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_5))
				{
				}
			}
			notificationUpdateId=0;

		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed=true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapterNotifications!=null && adapterNotifications.listDataNotifications!=null && !listDataNotifications.isEmpty())
		{
			adapterNotifications.notifyDataSetChanged();
			lvNotifications.setAdapter(adapterNotifications);
		}

		HomeScreenActivity.btnBack.setEnabled(false);
		if(isResumed)
		{
			isResumed=false;
		}
		if(HomeScreenActivity.btnNotification!=null  &&
				HomeScreenActivity.btnAddTile!=null &&
				HomeScreenActivity.btnCallEmergency!=null)
		{
			HomeScreenActivity.btnAddTile.setClickable(false);
			HomeScreenActivity.btnAddTile.setEnabled(false);
			HomeScreenActivity.btnNotification.setClickable(false);
			HomeScreenActivity.btnNotification.setEnabled(false);
			HomeScreenActivity.btnCallEmergency.setClickable(false);
			HomeScreenActivity.btnCallEmergency.setEnabled(false);
			HomeScreenActivity.btnSettings.setClickable(false);
			HomeScreenActivity.btnSettings.setEnabled(false);
		}
	}

	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			try {
				//update notification status on the server in case unread notification is deleted
				//				if(listDataNotifications.get(mNotificationPosition).getStatus()==1)
				//				{
				if (NetworkConnection.isNetworkAvailable(mActivityTabs))
				{
					try {
						//notificationUpdateId=Integer.parseInt(listDataNotifications.get(mNotificationPosition).id);
						NotificationUpdateBean notificationUpdateBean=new NotificationUpdateBean(Integer.parseInt(listDataNotifications.get(mNotificationPosition).id),
								2,"yes");
						updateNotification(notificationUpdateBean);
					} catch (Exception e) {
						e.getMessage();
					}
				}
				//				}
				// delete notification in case user clicks yes upon asking confirmation

				DBQuery.deleteNotification(mActivityTabs, notification_id);
				displayDataFromDatabase();

			} catch (Exception e) {
				e.getMessage();
			}
		}

		@Override
		public void no() {
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		MainBaseActivity.isFromMain=true;
		if(HomeScreenActivity.btnBack!=null)
			HomeScreenActivity.btnBack.setEnabled(true);
		if(iNotifyObject!=null)
			iNotifyObject.yes();
		try{
			if(HomeScreenActivity.btnNotification!=null  &&
					HomeScreenActivity.btnAddTile!=null &&
					HomeScreenActivity.btnCallEmergency!=null)
			{
				HomeScreenActivity.btnAddTile.setClickable(true);
				HomeScreenActivity.btnAddTile.setEnabled(true);
				HomeScreenActivity.btnNotification.setClickable(true);
				HomeScreenActivity.btnNotification.setEnabled(true);
				HomeScreenActivity.btnCallEmergency.setClickable(true);
				HomeScreenActivity.btnCallEmergency.setEnabled(true);
				HomeScreenActivity.btnSettings.setClickable(true);
				HomeScreenActivity.btnSettings.setEnabled(true);
			}
		}catch(Exception e){
			e.getMessage();
		}
		if(HomeScreenActivity.tvUnreadNotificationCount!=null)
		{
			int count=-1;
			try {
				count=((HomeScreenActivity)mActivityTabs).getUnreadNotificationCount();
				if(count>0)
				{
					HomeScreenActivity.tvUnreadNotificationCount.setVisibility(View.VISIBLE);
					HomeScreenActivity.tvUnreadNotificationCount.setText(String.valueOf(count));
				}
				else
				{
					HomeScreenActivity.tvUnreadNotificationCount.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
		GCMIntentService.isNotificationPushDisplayDialogActivity = false;
		GCMIntentService.isMessagePushDisplayDialogActivity = false;
	}

	/**
	 * check internet availability
	 */
	private void checkInternetConnection()
	{
		if (NetworkConnection.isNetworkAvailable(mActivityTabs))
		{
			notificationID=DBQuery.getNotificationsMaxCount(mActivityTabs);
			if(notificationID==-1)
			{
				notificationID=0;
			}
			NotificationBean notificationBean=new NotificationBean(String.valueOf(notificationID));
			getNotificationList(notificationBean);
		}
		else
		{
			displayDataFromDatabase();
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
		}
	}

	/**
	 * request for getting notification list from the server
	 * @param notificationBean
	 */
	private void getNotificationList(NotificationBean notificationBean)
	{
		try
		{
			gson=new Gson();
			String stingGson = gson.toJson(notificationBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.NOTIFICATION_LIST,
					stringEntity, notificationResponseHandler,
					mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	//async task to handle response for the request being made to fetch notification from the server
	AsyncHttpResponseHandler notificationResponseHandler = new JsonHttpResponseHandler() {
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
				getResponseNotification(response.toString());
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			displayDataFromDatabase();
			// Response failed :(
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/**
	 * handle response for the request being made to get notifications
	 *
	 * @param response
	 */
	private void getResponseNotification(String response)
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
			if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2))
			{
				gson=new Gson();
				ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				String responseCode="";
				try{
					JSONObject json=new JSONObject(response);
					responseCode=json.getString("response_code");
				}catch(JSONException e){
					e.printStackTrace();
				}
				/*if (responseCode.equals(GlobalCommonValues.FAILURE_CODE) || 
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_1) ||
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_2) ||
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_3) ||
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_5) ||
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_6) ||
						responseCode.equals(GlobalCommonValues.FAILURE_CODE_601)){
					//nothing to do

				}else*/
				if(responseCode.equals(GlobalCommonValues.SUCCESS_CODE)){
					NotificationResponse get_Response = gson.fromJson(response2,NotificationResponse.class);

					listDataNotifications=get_Response.getData;

					DBQuery.insertNotification(mActivityTabs,listDataNotifications);
					listDataNotifications.clear();

//					if(!get_Response.getVersion().getClipart_version().equalsIgnoreCase(
//							saveState.getCLIPARTS_VERSION(mActivityTabs))){
//						saveState.setCLIPARTS_VERSION(mActivityTabs,get_Response.version.getFaq_version());
//					
//						//Run service to download all the clipart images in the cache
//						GlobalConfig_Methods mDownloadCliparts = new 
//								GlobalConfig_Methods();
//						mDownloadCliparts.downloadClipArts(mActivityTabs);
//					}
				}

				displayDataFromDatabase();
			}
			else{
				displayDataFromDatabase();
			}
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
