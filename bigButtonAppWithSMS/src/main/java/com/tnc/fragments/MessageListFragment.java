package com.tnc.fragments;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.GCMIntentService;
import com.tnc.R;
import com.tnc.adapter.MessageListAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ChatUserDetailBean;
import com.tnc.bean.MessageStatusUpdateBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.MessageDeleteConfirmation;
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
import com.tnc.webresponse.GetAllMessageResponseBean;
import com.tnc.webresponse.SendMessageReponseDataBean;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
 * class to display all chat list 
 *  @author a3logics
 */

public class MessageListFragment extends BaseFragmentTabs implements
OnClickListener {
	private TextView tvTitle, tvInbox, tvUpdateStatus, tvUnreadCount, tvCompose;
	private Button btnBack;
	private FrameLayout flBackArrow;
	private SwipeMenuListView lvMessageContacts;
	private MessageListAdapter adapterMessagesList;
	private ArrayList<SendMessageReponseDataBean> listMessageContacts;// Saves an
	private Gson gson;
	private TransparentProgressDialog progress;
	private ArrayList<BBContactsBean> listBBContacts;
	private ArrayList<SendMessageReponseDataBean> listChatMessage;
	private int to_user_id_delete = -1, from_user_id_delete = -1;
	private int to_user_id_status_update = -1, from_user_id_status_update = -1;
	private boolean isFirstTime = true;
	private Handler handler = new Handler();
	private INotifyGalleryDialog iNotifyRefreshSelectedTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.messagelistfragment, container,
				false);
		idInitialization(view);
		if (isFirstTime) {
			/*handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new StartServiceClass().execute();
				}
			}, 100);*/
		}
		return view;
	}
	public MessageListFragment newInstance(
			INotifyGalleryDialog iNotifyRefreshSelectedTab) {
		MessageListFragment frag = new MessageListFragment();
		this.iNotifyRefreshSelectedTab = iNotifyRefreshSelectedTab;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}
	// Method to initialize views/widgets
	private void idInitialization(View view) {
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivityTabs,
				R.drawable.customspinner);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvInbox = (TextView) view.findViewById(R.id.tvInbox);
		tvUpdateStatus = (TextView) view.findViewById(R.id.tvUpdateStatus);
		tvUnreadCount = (TextView) view.findViewById(R.id.tvUnreadCount);
		tvCompose = (TextView) view.findViewById(R.id.tvCompose);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		lvMessageContacts = (SwipeMenuListView) view
				.findViewById(R.id.lvMessageContacts);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvInbox,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvUpdateStatus,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvUnreadCount,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvCompose,
				"fonts/Roboto-Regular_1.ttf");
		tvUpdateStatus.setText("Updated Just Now");
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivityTabs instanceof HomeScreenActivity) {
					((HomeScreenActivity) mActivityTabs).fragmentManager
					.popBackStack();
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

		lvMessageContacts.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean boolValue=false;
				if (!adapterMessagesList.listMessageContacts.get(0).getMessage().equalsIgnoreCase("No Message Found")){
					boolValue=false;
				}
				else if (adapterMessagesList.listMessageContacts.get(0).getMessage().equalsIgnoreCase("No Message Found")){
					boolValue=true;
				}
				return boolValue;
			}
		});

		// set creator
		lvMessageContacts.setMenuCreator(creator);

		// set SwipeListener
		lvMessageContacts.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
			}

			@Override
			public void onSwipeEnd(int position) {
			}
		});

		lvMessageContacts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				try {
					if (!adapterMessagesList.listMessageContacts.get(0).getMessage().equalsIgnoreCase("No Message Found")) {
						int BBID = 0;
						BBContactsBean objContactDetail = null;
						ChatUserDetailBean objUserDetailBean = null;
						to_user_id_status_update = -1;
						from_user_id_status_update = -1;
						to_user_id_delete = -1;
						from_user_id_delete = -1;
						to_user_id_status_update = Integer.parseInt(listChatMessage.get(position).getTo_user_id());
						from_user_id_status_update = Integer.parseInt(listChatMessage.get(position).getFrom_user_id());
						to_user_id_delete = Integer.parseInt(listChatMessage.get(position).getTo_user_id());
						from_user_id_delete = Integer.parseInt(listChatMessage.get(position).getFrom_user_id());
						DBQuery.updateMessageStatus(mActivityTabs,to_user_id_status_update,from_user_id_status_update);

						int matching_user_id = Integer.parseInt(listChatMessage.get(position).getTo_user_id());
						listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs, matching_user_id);
						if (listBBContacts.size() > 0) {

							BBID = listBBContacts.get(0).getBBID();
						}
						else 
						{
							matching_user_id = Integer.parseInt(listChatMessage.get(position).getFrom_user_id());
							listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs, matching_user_id);
							if (listBBContacts.size() > 0) 
							{
								BBID = listBBContacts.get(0).getBBID();
							}
						}
						MessagePredefinedComposeFragment messagePredefined = new MessagePredefinedComposeFragment();
						if (BBID != 0) 
						{
							objContactDetail = new BBContactsBean();
							objContactDetail.setName(listBBContacts.get(0).getName());
							objContactDetail.setPhoneNumber(listBBContacts.get(0).getPhoneNumber());
							objContactDetail.setCountryCode(listBBContacts.get(0).getCountryCode());
							objContactDetail.setMobID(listBBContacts.get(0).getMobID());
							objContactDetail.setBBID(listBBContacts.get(0).getBBID());
							objContactDetail.setImage(listBBContacts.get(0).getImage());
							messagePredefined.newInstance(objContactDetail, null,from_user_id_delete, to_user_id_delete, null);
						}
						else if (BBID == 0) 
						{
							objUserDetailBean = new ChatUserDetailBean();
							objUserDetailBean.setNumber(listChatMessage.get(position).getFrom_user_phone());
							objUserDetailBean.setTo_user_id(listChatMessage.get(position).getTo_user_id());
							objUserDetailBean.setFrom_user_id(listChatMessage.get(position).getFrom_user_id());
							messagePredefined.newInstance(null, objUserDetailBean,from_user_id_delete, to_user_id_delete, null);
						}
						((HomeScreenActivity) mActivityTabs).setFragment(messagePredefined);
					}
				}
				catch (Exception e) 
				{
					e.getMessage();
				}
			}
		});

		lvMessageContacts
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position,
					SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					if (!adapterMessagesList.listMessageContacts.get(0).getMessage().equalsIgnoreCase("No Message Found")) {
						to_user_id_delete = -1;
						from_user_id_delete = -1;
						to_user_id_status_update = -1;
						from_user_id_status_update = -1;
						from_user_id_delete = Integer
								.parseInt(listChatMessage.get(position)
										.getFrom_user_id());
						to_user_id_delete = Integer
								.parseInt(listChatMessage.get(position)
										.getTo_user_id());
						from_user_id_status_update = Integer
								.parseInt(listChatMessage.get(position)
										.getFrom_user_id());
						to_user_id_status_update = Integer
								.parseInt(listChatMessage.get(position)
										.getTo_user_id());
						MessageDeleteConfirmation dialogMessageDelete = new MessageDeleteConfirmation();
						dialogMessageDelete
						.newInstance(
								"",
								((HomeScreenActivity) mActivityTabs),
								"Are you sure you want to delete this conversation",
								"", iNotifyGalleryDialog);
						dialogMessageDelete.setCancelable(false);
						dialogMessageDelete.show(
								((HomeScreenActivity) mActivityTabs)
								.getSupportFragmentManager(),
								"test");
					}
					break;
				}
				return false;
			}
		});
		// Refresh List
		lvMessageContacts.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				checkInternetConnection();
				lvMessageContacts.postDelayed(new Runnable() {
					@Override
					public void run() {
						lvMessageContacts.onRefreshComplete();
					}
				}, 2500);
			}

		});
		checkInternetConnection();

		tvInbox.setOnClickListener(this);
		tvCompose.setOnClickListener(this);
	}

	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			try {
				// delete user chat in case user clicks yes upon asking confirmation
				DBQuery.deleteUserChat(mActivityTabs, to_user_id_delete,
						from_user_id_delete);
				displayChatFromDatabse();
				displayUnreadMessageCount();
				MessageStatusUpdateBean updateStatusBean = new MessageStatusUpdateBean();
				if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == from_user_id_status_update) {
					updateStatusBean.setBbid(String
							.valueOf(to_user_id_status_update));
				} else {
					updateStatusBean.setBbid(String
							.valueOf(from_user_id_status_update));
				}
				updateMessageStatus(updateStatusBean);

			} catch (Exception e) {
				e.getMessage();
			}
		}

		@Override
		public void no() {
		}
	};

	public void onPause() {
		super.onPause();
		isFirstTime = false;
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		isFirstTime = true;
		HomeScreenActivity.isFirsTimeHomeTabOpen = false;
		if (iNotifyRefreshSelectedTab != null)
			iNotifyRefreshSelectedTab.yes();
		if (HomeScreenActivity.btnNotification != null
				&& HomeScreenActivity.btnAddTile != null
				&& HomeScreenActivity.btnCallEmergency != null) {
			HomeScreenActivity.btnNotification.setClickable(true);
			HomeScreenActivity.btnNotification.setEnabled(true);
			HomeScreenActivity.btnAddTile.setClickable(true);
			HomeScreenActivity.btnAddTile.setEnabled(true);
			HomeScreenActivity.btnCallEmergency.setClickable(true);
			HomeScreenActivity.btnCallEmergency.setEnabled(true);
		}
		if(mActivityTabs instanceof HomeScreenActivity)
		{
			((HomeScreenActivity)mActivityTabs).setUnreadMessageCount();
		}
		GCMIntentService.isNotificationPushDisplayDialogActivity = false;
		GCMIntentService.isMessagePushDisplayDialogActivity = false;
	}

	/**
	 * Method to display all the chats from the DB
	 */
	@SuppressLint("SimpleDateFormat")
	private void displayChatFromDatabse() {
		@SuppressWarnings("unused")
		SendMessageReponseDataBean objMatched = null;
		listChatMessage = new ArrayList<SendMessageReponseDataBean>();
		listChatMessage = DBQuery.getAllMessages(mActivityTabs, -164);
		if (listChatMessage.isEmpty()) {
			SendMessageReponseDataBean dataBean = new SendMessageReponseDataBean();
			dataBean.setMessage("No Message Found");
			listChatMessage.add(dataBean);
		} else {
			try {
				Logs.writeLog("MessageList", "displayMessageFromChat",
						listChatMessage.toString());
				@SuppressWarnings("unused")
				boolean isLoopcomplete = true;

				ArrayList<SendMessageReponseDataBean> listChatMessage1 = new ArrayList<SendMessageReponseDataBean>();

				if(listChatMessage!= null && listChatMessage.size()>0){

					SendMessageReponseDataBean obj,obj1;

					boolean isMatched = false;

					for(int i=0;i<listChatMessage.size();++i){

						obj= listChatMessage.get(i);
						isMatched = false;

						for(int j =0;j<listChatMessage1.size();++j){
							obj1= listChatMessage1.get(j);
							if(obj1.from_user_id.equals(obj.to_user_id) && obj1.to_user_id.equals(obj.from_user_id)){
								isMatched = true;
								break;
							}
						}
						if(!obj.to_user_id.equals(obj.from_user_id)){
							if(!isMatched){
								listChatMessage1.add(obj);
							}
						}
					}
				}
				if(listChatMessage!=null && listChatMessage1!=null && listChatMessage1.size()>0)			{
					listChatMessage = listChatMessage1;
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
		adapterMessagesList = new MessageListAdapter(mActivityTabs,listChatMessage);
		adapterMessagesList.notifyDataSetChanged();
		lvMessageContacts.setAdapter(adapterMessagesList);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (HomeScreenActivity.btnNotification != null
				&& HomeScreenActivity.btnAddTile != null
				&& HomeScreenActivity.btnCallEmergency != null) {
			HomeScreenActivity.btnNotification.setClickable(false);
			HomeScreenActivity.btnNotification.setEnabled(false);
			HomeScreenActivity.btnAddTile.setClickable(false);
			HomeScreenActivity.btnAddTile.setEnabled(false);
			HomeScreenActivity.btnCallEmergency.setClickable(false);
			HomeScreenActivity.btnCallEmergency.setEnabled(false);
		}
		// displayChatFromDatabse();
		displayUnreadMessageCount();
	}

	/**
	 * Method top display unread message count
	 */
	private void displayUnreadMessageCount() {
		int count = DBQuery.getUnreadMessageCount(mActivityTabs);
		if (count > 0) {
			tvUnreadCount.setText(String.valueOf(count) + " Unread");
		} else {
			tvUnreadCount.setText("");
		}
	}

	/**
	 * check internet availability
	 */
	private void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			getMessageList();
		} else {
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
			displayChatFromDatabse();
			displayUnreadMessageCount();
		}
	}

	/**
	 * Method to send request to get messages sent to us
	 */
	private void getMessageList() {
		try {
			gson = new Gson();
			MyHttpConnection.postHeaderWithoutJsonEntity(mActivityTabs,
					GlobalCommonValues.GET_ALL_MESSAGE,
					messagesAllResponseHandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to handle response for getting all the messages received by us
	AsyncHttpResponseHandler messagesAllResponseHandler = new JsonHttpResponseHandler() {
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
				if (response != null) {
					Logs.writeLog("MessageListFragment", "OnSuccess", response.toString());
					getResponseAllMessages(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null) {
				Logs.writeLog("MessageListFragment", "OnFailure", response);
			}
			displayChatFromDatabse();
			displayUnreadMessageCount();
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			try {
				if (progress.isShowing())
					progress.dismiss();
			} catch (Exception e) {
				e.getMessage();
			}
		}
	};

	/**
	 * Handle response for the request made to get all the messages received by us
	 * @param response
	 */
	private void getResponseAllMessages(String response) {
		try 
		{
			if (!TextUtils.isEmpty(response)
					&& GlobalConfig_Methods.isJsonString(response)) {
				gson = new Gson();
				GetAllMessageResponseBean get_Response = gson.fromJson(response,
						GetAllMessageResponseBean.class);
				if (get_Response.response_code
						.equals(GlobalCommonValues.SUCCESS_CODE)) {
					ArrayList<SendMessageReponseDataBean> data = get_Response
							.getData();
					listMessageContacts = new ArrayList<SendMessageReponseDataBean>();
					if ((data != null && data.isEmpty()) || data == null) {
						SendMessageReponseDataBean dataBean = new SendMessageReponseDataBean();
						dataBean.setMessage("No Message Found");
						listMessageContacts.add(dataBean);
					} else if (data != null && data.size() > 0) {
						listMessageContacts = data;
						ArrayList<SendMessageReponseDataBean> listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
						SendMessageReponseDataBean objResponseDataBean = null;
						for (int i = 0; i < listMessageContacts.size(); i++) {
							objResponseDataBean = new SendMessageReponseDataBean();
							objResponseDataBean.setMessage_id(data.get(i)
									.getMessage_id());
							objResponseDataBean.setFrom_user_id(data.get(i)
									.getFrom_user_id());
							objResponseDataBean.setFrom_user_phone(data.get(i)
									.getFrom_user_phone());
							objResponseDataBean.setTo_user_id(data.get(i)
									.getTo_user_id());
							objResponseDataBean
							.setMessage(data.get(i).getMessage());
							objResponseDataBean.setStatus(data.get(i).getStatus());
							objResponseDataBean.setDatatime(data.get(i)
									.getDatatime());
							objResponseDataBean.setName(data.get(i).getName());
							listMessageDetails.add(objResponseDataBean);
						}
						DBQuery.insertMessage(mActivityTabs, listMessageDetails);
					}
				} else if ((get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE))
						|| (get_Response.response_code
								.equals(GlobalCommonValues.FAILURE_CODE_1))
						|| (get_Response.response_code
								.equals(GlobalCommonValues.FAILURE_CODE_2))) {
				}
				displayChatFromDatabse();
				displayUnreadMessageCount();
			} else {
				displayChatFromDatabse();
				displayUnreadMessageCount();
			}

		} catch (Exception e) {
			e.getMessage();
		}}

	// Call a WebService to update message status from unread to read
	private void updateMessageStatus(MessageStatusUpdateBean updateStatusBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(updateStatusBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.UPDATE_MESSAGE_STATUS, stringEntity,
					updateMessageResponseHandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	AsyncHttpResponseHandler updateMessageResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			if (response != null)
				Logs.writeLog("Update Message Status", "OnSuccess", response.toString());
			try {
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("Update Message Status", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	@Override
	public void onClick(View v) {
		try{
			if (v.getId() == R.id.tvInbox) {

			} else if (v.getId() == R.id.tvCompose) {
				((HomeScreenActivity) mActivityTabs)
				.setFragment(new RegisteredBigButtonUsersFragment());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
