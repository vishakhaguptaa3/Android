/*package com.tnc.fragments;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
import com.tnc.service.GetContactService;
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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

public class CopyOfMessageListFragment extends BaseFragmentTabs implements
OnClickListener {
	TextView tvTitle, tvInbox, tvUpdateStatus, tvUnreadCount, tvCompose;
	Button btnBack;
	FrameLayout flBackArrow;
	SwipeMenuListView lvMessageContacts;
	MessageListAdapter adapterMessagesList;
	ArrayList<SendMessageReponseDataBean> listMessageContacts;// Saves an
	// displays list
	// of webservice
	Gson gson;
	TransparentProgressDialog progress;
	ArrayList<BBContactsBean> listBBContacts;
	ArrayList<SendMessageReponseDataBean> listChatMessage;
	int to_user_id_delete = -1, from_user_id_delete = -1;
	int to_user_id_status_update = -1, from_user_id_status_update = -1;
	boolean isFirstTime = true;
	Handler handler = new Handler();
	INotifyGalleryDialog iNotifyRefreshSelectedTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.messagelistfragment, container,
				false);
		idInitialization(view);
		if (isFirstTime) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new StartServiceClass().execute();
				}
			}, 100);
		}
		return view;
	}

	public CopyOfMessageListFragment newInstance(
			INotifyGalleryDialog iNotifyRefreshSelectedTab) {
		CopyOfMessageListFragment frag = new CopyOfMessageListFragment();
		this.iNotifyRefreshSelectedTab = iNotifyRefreshSelectedTab;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	class StartServiceClass extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Intent mainIntent = new Intent(mActivityTabs,
					GetContactService.class);
			mActivityTabs.startService(mainIntent);
		}
	}

	private void idInitialization(View view) {
		// ((HomeScreenActivity)mActivityTabs).mCurrentTab_pos=0;
		// ((HomeScreenActivity)mActivityTabs).setSelectedTab();
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
//		CustomFonts.setFontOfTextView(getActivity(), tvTitle,
//				"fonts/StencilStd.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvInbox,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvUpdateStatus,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvUnreadCount,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvCompose,
				"fonts/Roboto-Regular_1.ttf");
		tvUpdateStatus.setText("Updated Just Now");
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
				
				 * // set a icon deleteItem.setIcon(R.drawable.ic_delete);
				 
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
						to_user_id_status_update = Integer.parseInt(listChatMessage
								.get(position).getTo_user_id());
						from_user_id_status_update = Integer
								.parseInt(listChatMessage.get(position)
										.getFrom_user_id());
						to_user_id_delete = Integer.parseInt(listChatMessage.get(
								position).getTo_user_id());
						from_user_id_delete = Integer.parseInt(listChatMessage.get(
								position).getFrom_user_id());
						DBQuery.updateMessageStatus(mActivityTabs,
								to_user_id_status_update,
								from_user_id_status_update);
						MessageStatusUpdateBean updateStatusBean = new MessageStatusUpdateBean();
							if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == Integer
									.parseInt(listChatMessage.get(position)
											.getFrom_user_id())) {
								updateStatusBean.setBbid(listChatMessage.get(position)
										.getTo_user_id());
							} else {
								updateStatusBean.setBbid(listChatMessage.get(position)
										.getFrom_user_id());
							}
							updateMessageStatus(updateStatusBean);
						int matching_user_id = Integer.parseInt(listChatMessage
								.get(position).getTo_user_id());
						listBBContacts = DBQuery.checkBBContactExistence(
								mActivityTabs, matching_user_id);
						if (listBBContacts.size() > 0) {
							BBID = listBBContacts.get(0).getBBID();
						} else {
							matching_user_id = Integer.parseInt(listChatMessage
									.get(position).getFrom_user_id());
							listBBContacts = DBQuery.checkBBContactExistence(
									mActivityTabs, matching_user_id);
							if (listBBContacts.size() > 0) {
								BBID = listBBContacts.get(0).getBBID();
							}
						}
						MessagePredefinedComposeFragment messagePredefined = new MessagePredefinedComposeFragment();
						if (BBID != 0) {
							objContactDetail = new BBContactsBean();
							objContactDetail.setName(listBBContacts.get(0)
									.getName());
							objContactDetail.setPhoneNumber(listBBContacts.get(0)
									.getPhoneNumber());
							objContactDetail.setBBID(listBBContacts.get(0)
									.getBBID());
							objContactDetail.setImage(listBBContacts.get(0)
									.getImage());
							messagePredefined.newInstance(objContactDetail, null,
									from_user_id_delete, to_user_id_delete, null);
						} else if (BBID == 0) {
							objUserDetailBean = new ChatUserDetailBean();
							objUserDetailBean.setNumber(listChatMessage.get(
									position).getFrom_user_phone());
							objUserDetailBean.setTo_user_id(listChatMessage.get(
									position).getTo_user_id());
							objUserDetailBean.setFrom_user_id(listChatMessage.get(
									position).getFrom_user_id());
							messagePredefined.newInstance(null, objUserDetailBean,
									from_user_id_delete, to_user_id_delete, null);
						}
						((HomeScreenActivity) mActivityTabs)
						.setFragment(messagePredefined);
					}
				} catch (Exception e) {
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
		
		 * if(isFirstTime) { checkInternetConnection(); } else if(!isFirstTime)
		 * { ((HomeScreenActivity)mActivityTabs).setUnreadMessageCount();
		 * displayChatFromDatabse(); // GlobalConfig_Methods.testCopy(); }
		 
		tvInbox.setOnClickListener(this);
		tvCompose.setOnClickListener(this);
		// lvMessageContacts.setFocusable(true);
		// lvMessageContacts.setFocusableInTouchMode(true);
		// lvMessageContacts.requestFocus();
		// lvMessageContacts.setSelected(true);
	}

	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			try {

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
				&& HomeScreenActivity.btnCall != null) {
			HomeScreenActivity.btnNotification.setClickable(true);
			HomeScreenActivity.btnNotification.setEnabled(true);
			HomeScreenActivity.btnAddTile.setClickable(true);
			HomeScreenActivity.btnAddTile.setEnabled(true);
			HomeScreenActivity.btnCall.setClickable(true);
			HomeScreenActivity.btnCall.setEnabled(true);
		}
		if(mActivityTabs instanceof HomeScreenActivity)
		{
			((HomeScreenActivity)mActivityTabs).setUnreadMessageCount();
		}
		GCMIntentService.isNotificationPushDisplayDialogActivity = false;
		GCMIntentService.isMessagePushDisplayDialogActivity = false;
		GlobalCommonValues.pushNotificationString=null;
	}

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
					//listChatMessage1.add(listChatMessage.get(0));

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

				listChatMessage = listChatMessage1;

				if(listChatMessage!= null && listChatMessage.size()>0){
						listChatMessage1.add(listChatMessage.get(0));

						SendMessageReponseDataBean obj,obj1;

						boolean isMatched = false;

						for(int i=1;i<listChatMessage.size();++i){

							obj= listChatMessage.get(i);
							isMatched = false;

							for(int j =0;j<listChatMessage1.size();++j){
								obj1= listChatMessage1.get(j);
								if(obj1.from_user_id.equals(obj.to_user_id) && obj1.to_user_id.equals(obj.from_user_id)){
									isMatched = true;
									break;
								}
							}

							if(!isMatched){
								listChatMessage1.add(obj);
							}
						}
					}

				

					  while(true){
					isLoopcomplete = true;
					outerLoop: for (SendMessageReponseDataBean objToMatchFirst : listChatMessage) {
						if (objToMatchFirst.from_user_id.equals(saveState
								.getBBID(mActivityTabs))) {
							String otherBBID = objToMatchFirst.to_user_id;
							innerLoop: for (SendMessageReponseDataBean objToMatchSecond : listChatMessage) {
								try {
									if (objToMatchSecond.from_user_id
											.equals(otherBBID)) {
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss");
										Date objDateFirst = sdf
												.parse(objToMatchFirst.datatime);// GetDateFromString([obj1
										// objectForKey:@"datetime"]);
										Date objDateSecond = sdf
												.parse(objToMatchSecond.datatime);// GetDateFromString([obj2
										// objectForKey:@"datetime"]);
										if (objDateFirst.compareTo(objDateSecond) > 0) {
											objMatched = objToMatchSecond;
										} else // if(objDateFirst.after(objDateSecond))
										{
											objMatched = objToMatchFirst;
										}
										isLoopcomplete = false;
										break innerLoop;
									}
								} catch (Exception e) {
									e.getMessage();
								}
							}
						//}
						if (objMatched != null) {
							listChatMessage.remove(objMatched);
							break outerLoop;
						}
					}

					if(isLoopcomplete){
						break;
					}
				}


			} catch (Exception e) {
				e.getMessage();
			}
		}
		adapterMessagesList = new MessageListAdapter(mActivityTabs,
				listChatMessage);
		adapterMessagesList.notifyDataSetChanged();
		lvMessageContacts.setAdapter(adapterMessagesList);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (HomeScreenActivity.btnNotification != null
				&& HomeScreenActivity.btnAddTile != null
				&& HomeScreenActivity.btnCall != null) {
			HomeScreenActivity.btnNotification.setClickable(false);
			HomeScreenActivity.btnNotification.setEnabled(false);
			HomeScreenActivity.btnAddTile.setClickable(false);
			HomeScreenActivity.btnAddTile.setEnabled(false);
			HomeScreenActivity.btnCall.setClickable(false);
			HomeScreenActivity.btnCall.setEnabled(false);
		}
		// displayChatFromDatabse();
		displayUnreadMessageCount();
	}

	private void displayUnreadMessageCount() {
		int count = DBQuery.getUnreadMessageCount(mActivityTabs);
		if (count > 0) {
			tvUnreadCount.setText(String.valueOf(count) + " Unread");
		} else {
			tvUnreadCount.setText("");
		}
	}

	*//**
	 * check internet availability
	 *//*
	private void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			// MessageListBean messageListBean=new MessageListBean("");
			getMessageList();
		} else {
			// If no internet available then also display list from the database
			displayChatFromDatabse();
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
		}
	}

	private void getMessageList() {
		try {
			gson = new Gson();
			// String stingGson = gson.toJson(messageListBean);
			// StringEntity stringEntity;
			// stringEntity=new StringEntity(stingGson);
			MyHttpConnection.postHeaderWithoutJsonEntity(mActivityTabs,
					GlobalCommonValues.GET_ALL_MESSAGE,
					messagesAllResponseHandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	AsyncHttpResponseHandler messagesAllResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(String response) {
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("MessageListFragment", "OnSuccess", response);
					getResponseAllMessages(response);
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(Throwable e, String response) {
			// Response failed :(
			if (response != null) {
				Logs.writeLog("MessageListFragment", "OnFailure", response);
			}
			displayChatFromDatabse();
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

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
					// displayChatFromDatabse();
				}
				displayChatFromDatabse();
				displayUnreadMessageCount();
			} else {
				displayChatFromDatabse();
				//ShowDialog.alert(mActivityTabs, "",
				//	getResources().getString(R.string.improper_response));
			}

		} catch (Exception e) {
			e.getMessage();
		}}

	// Call a WebService to update message status from unread to read
	private void updateMessageStatus(MessageStatusUpdateBean updateStatusBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(updateStatusBean);
			StringEntity stringEntity;
			stringEntity = new StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.UPDATE_MESSAGE_STATUS, stringEntity,
					updateMessageResponseHandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	AsyncHttpResponseHandler updateMessageResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			
			 * if ((!progress.isShowing())) progress.show();
			 
		}

		@Override
		public void onSuccess(String response) {
			// Successfully got a response
			if (response != null)
				Logs.writeLog("Update Message Status", "OnSuccess", response);
			try {
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(Throwable e, String response) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("Update Message Status", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			
			 * if (progress.isShowing()) progress.dismiss();
			 
		}
	};

	@Override
	public void onClick(View v) {
		
		 * if(v.getId()==R.id.btnBack) { if(mActivityTabs instanceof
		 * HomeScreenActivity) {
		 * ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack(); }
		 * } else
		 
		try{
			if (v.getId() == R.id.tvInbox) {

			} else if (v.getId() == R.id.tvCompose) {
				((HomeScreenActivity) mActivityTabs)
				.setFragment(new RegisteredBigButtonUsersFragment());
				// ((HomeScreenActivity)mActivityTabs).setFragment(new
				// MessagePredefinedComposeFragment());
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
*/