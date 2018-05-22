package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONObject;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.adapter.ChatAdapter;
import com.tnc.adapter.MessagePredefinedAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.BBIDUserDetailsBean;
import com.tnc.bean.ChatUserDetailBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.bean.MessageRequestBean;
import com.tnc.bean.MessageSendBean;
import com.tnc.chat.ChatContract;
import com.tnc.chat.ChatPresenter;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.DeleteChatConfirmationDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.InitResponseMessages;
import com.tnc.dialog.InitResponseMessagesImage;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.model.Chat;
import com.tnc.model.ChatInfo;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Constants;
import com.tnc.utility.Logs;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.webresponse.GetBBIDUserDetailsBean;
import com.tnc.webresponse.GetMessageResponseBean;
import com.tnc.webresponse.GetMessageResponseDataBean;
import com.tnc.webresponse.SendMessageReponseDataBean;
import com.tnc.webresponse.SendMessageResponse;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;


/**
 * class to display chat with specific user 
 *  @author a3logics
 */

public class MessagePredefinedComposeFragment extends BaseFragmentTabs
implements ChatContract.View, TextView.OnEditorActionListener,OnClickListener {
	private FrameLayout flInformationButton;
	private TextView tvTitle, tvContactName, tvContactNumber, tvDeleteChat;
	private Button btnBack, btnSendMessage, btnPredefinedMessages, btnHome, btnPredefinedMessagesImage;
	private ListView lvPredefinedMessages, lvChatMessages;
	private RoundedImageViewCircular imViewNotificationContact;
	private EditText etMessage;
	private FrameLayout flBackArrow;
	private ChatAdapter adapterChat;
	private BBContactsBean objContactDetail;
	private ChatUserDetailBean objUserDetailBean;
	private MessagePredefinedAdapter adapterPredefinedMessages;
	private ArrayList<InitResponseMessageBean> listInitMessages = null;
	private ArrayList<InitResponseMessageBean> listResponseMessages = null;
	private TransparentProgressDialog progress;
	private Gson gson;
	StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("ggs://chatstasy-f0999.appspot.com");
	private ArrayList<SendMessageReponseDataBean> listMessageDetails = null;
	private ArrayList<SendMessageReponseDataBean> listMessageDetailsHolder = null;
	private ArrayList<GetMessageResponseDataBean> listGetMessageDetails = null;
	private Timer timer = null;
	private TimerTask timerTask = null;
	private final int refresh = 1;
	private final int stoprefresh = 2;
	private final int donothing = 3;
	private boolean webservices_oncall;
	private int to_user_id_delete = -1, from_user_id_delete = -1;
	private LinearLayout ll_mssges;
	//	ArrayList<String> listDeleteMessages;
	private INotifyGalleryDialog INotifyCount;
	DatabaseReference databaseReference;
	private RelativeLayout rlChat;
	private String displayName = "";
	private Bitmap displayImage = null;
	private SharedPreference pref = null;
	ArrayList<ContactTilesBean> listContactTiles = null;
	private int matching_user_id;
	private String phoneNumber = "";
	private int BBID = 0;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private ChatPresenter mChatPresenter;
	static final int FILE_SELECT_CODE = 2;
	private ArrayList<BBContactsBean> listBBContacts;
	private ImageLoadTask imageLoader = null;
	private Bitmap _bmp;
	String mDisplayCountryCode = "";


	public static MessagePredefinedComposeFragment newInstance(String email,
															   String receiverUid,
															   String firebaseToken) {
		Bundle args = new Bundle();
		args.putString(Constants.ARG_EMAIL, email);
		args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
		args.putString(Constants.ARG_FIREBASE_UID, firebaseToken);
		MessagePredefinedComposeFragment fragment = new MessagePredefinedComposeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public static MessagePredefinedComposeFragment newInstanceForGroup(String groupName,
																	   String firebaseToken) {
		Bundle args = new Bundle();
		args.putString(Constants.ARG_GROUPS_NAME, groupName);
		args.putString(Constants.ARG_FIREBASE_UID, firebaseToken);
		MessagePredefinedComposeFragment fragment = new MessagePredefinedComposeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public MessagePredefinedComposeFragment newInstance(BBContactsBean objContactDetail,
														ChatUserDetailBean objUserDetailBean,
														int from_user_id_delete, int to_user_id_delete, INotifyGalleryDialog INotifyCount) {
		this.objContactDetail = objContactDetail;
		this.objUserDetailBean = objUserDetailBean;
		MessagePredefinedComposeFragment frag = new MessagePredefinedComposeFragment();
		Bundle args = new Bundle();
		this.from_user_id_delete = from_user_id_delete;
		this.to_user_id_delete = to_user_id_delete;
		this.INotifyCount = INotifyCount;
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_predefine_compose_list, container, false);
		idInitialization(view);
		databaseReference = FirebaseDatabase.getInstance().getReference();
		setChatType();

		return view;
	}

	void setChatType() {

		final String room_type_1 = FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + getArguments().getString(Constants.ARG_RECEIVER_UID);
		final String room_type_2 = getArguments().getString(Constants.ARG_RECEIVER_UID) + "_" + FirebaseAuth.getInstance().getCurrentUser().getUid();

		final Map<String, Object> map = new HashMap<>();
		map.put("chatType", "one-to-one");


		databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.hasChild(room_type_1)) {

//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);

					databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map);
					//                   Log.e(TAG, "databaseReference 11....... " + databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().getKey());

				} else if (dataSnapshot.hasChild(room_type_2)) {

//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
					databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).updateChildren(map);
//                    Log.e(TAG, "databaseReference 11f....... " + databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).updateChildren(map));


				} else {
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);

					databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map);
					//                   Log.e(TAG, "databaseReference 11f.dd...... " +  databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map));


				}

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.d("canceled failure", databaseError.getMessage());
			}
		});

	}


	@SuppressLint("ClickableViewAccessibility")
	private void idInitialization(View view) {
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
		gson = new Gson();
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvContactName = (TextView) view.findViewById(R.id.tvContactName);
		tvContactNumber = (TextView) view.findViewById(R.id.tvContactNumber);
		tvDeleteChat = (TextView) view.findViewById(R.id.tvDeleteChat);
		imViewNotificationContact = (RoundedImageViewCircular) view.findViewById(R.id.imViewNotificationContact);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		etMessage = (EditText) view.findViewById(R.id.etMessage);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		flInformationButton = (FrameLayout) view.findViewById(R.id.flInformationButton);
		btnHome = (Button) view.findViewById(R.id.btnHome);
		btnPredefinedMessages = (Button) view.findViewById(R.id.btnPredefinedMessages);
		// new feature image send
//		btnPredefinedMessagesImage = (Button)view.findViewById(R.id.btnPredefinedMessagesImage);

		btnSendMessage = (Button) view.findViewById(R.id.btnSendMessage);
		lvPredefinedMessages = (ListView) view.findViewById(R.id.lvPredefinedMessages);
		ll_mssges = (LinearLayout) view.findViewById(R.id.ll_mssges);
		lvChatMessages = (ListView) view.findViewById(R.id.lvChatMessages);
		rlChat = (RelativeLayout) view.findViewById(R.id.rlChat);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		listContactTiles = new ArrayList<ContactTilesBean>();
		listBBContacts = new ArrayList<BBContactsBean>();
		CustomFonts.setFontOfTextView(getActivity(), tvContactNumber, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvDeleteChat, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(), etMessage, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnSendMessage, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvContactName, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvContactNumber, "fonts/Roboto-Bold_1.ttf");
		btnSendMessage.setVisibility(View.GONE);
		btnPredefinedMessages.setVisibility(View.VISIBLE);
//		btnPredefinedMessagesImage.setVisibility(View.VISIBLE);
		tvDeleteChat.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		btnSendMessage.setOnClickListener(this);
		btnPredefinedMessages.setOnClickListener(this);
//		btnPredefinedMessagesImage.setOnClickListener(this);
		rlChat.setOnClickListener(this);

		mChatPresenter = new ChatPresenter(this);

		if (getArguments().getString(Constants.ARG_RECEIVER_UID) != null) {

			mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
					getArguments().getString(Constants.ARG_RECEIVER_UID));

		} else {

			mChatPresenter.getMessageForGroup(getArguments().getString(Constants.ARG_GROUPS_NAME));
		}


		_bmp = ((BitmapDrawable) (mActivityTabs.getResources().getDrawable(R.drawable.no_image))).getBitmap();
		if (objContactDetail != null) {
			if ((objContactDetail.getCountryCode() != null) && !(objContactDetail.getCountryCode().trim().equalsIgnoreCase(""))) {
				mDisplayCountryCode = objContactDetail.getCountryCode();
			}

			phoneNumber = objContactDetail.getPhoneNumber();//FromBBID(mActivityTabs,objContactDetail.getBBID());
			if (phoneNumber.trim().equals("")) {
				displayName = "Unknown";
				imViewNotificationContact.setImageBitmap(_bmp);
				checkInternetConnection_UserDetails();
			} else {
				listContactTiles = DBQuery.getTileFromPhoneNumber(mActivityTabs, GlobalConfig_Methods.trimSpecialCharactersFromString(phoneNumber));
				if (listContactTiles.size() > 0) {
					displayName = listContactTiles.get(0).getName();
					if (listContactTiles.get(0).getImage() != null && listContactTiles.get(0).getImage().length > 0) {
						byte arrayImage[] = listContactTiles.get(0).getImage();
						if (arrayImage != null && arrayImage.length > 0) {
							imViewNotificationContact.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length));
						}
					} else {
						listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs, objContactDetail.getBBID());
						if (listBBContacts.size() > 0) {
							//						isExist=true;
							BBID = listBBContacts.get(0).getBBID();
						}

						if (BBID != 0) {
							if (listBBContacts.get(0).getImage() != null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL")) {
								imageLoader = new ImageLoadTask(mActivityTabs, listBBContacts.get(0).getImage(), imViewNotificationContact, 320);
								imageLoader.execute();
							} else {
								displayImage = GlobalConfig_Methods.getContactBitmap(mActivityTabs, phoneNumber);
								if (displayImage != null) {
									imViewNotificationContact.setImageBitmap(displayImage);
								} else {
									imViewNotificationContact.setImageBitmap(_bmp);
								}
							}
						} else {
							displayImage = GlobalConfig_Methods.getContactBitmap(mActivityTabs, phoneNumber);
							if (displayImage != null) {
								imViewNotificationContact.setImageBitmap(displayImage);
							} else {
								imViewNotificationContact.setImageBitmap(_bmp);
							}
						}
					}
				} else {
					// In case of tile of that number doesn't exists
					listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs, objContactDetail.getBBID());
					if (listBBContacts.size() > 0) {
						//					isExist=true;
						BBID = listBBContacts.get(0).getBBID();
					}

					if (BBID != 0) {
						displayName = GlobalConfig_Methods.getContactNameFromPhone(mActivityTabs, mDisplayCountryCode + phoneNumber);//1-9-2016
						if (displayName.trim().equalsIgnoreCase("")) {
							displayName = GlobalConfig_Methods.getContactName(mActivityTabs, phoneNumber);//1-9-2016
						}

						if (listBBContacts.get(0).getImage() != null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL")) {
							imageLoader = new ImageLoadTask(mActivityTabs, listBBContacts.get(0).getImage(), imViewNotificationContact, 320);
							imageLoader.execute();
						} else {
							displayImage = GlobalConfig_Methods.getContactBitmap(mActivityTabs, phoneNumber);
							if (displayImage != null) {
								imViewNotificationContact.setImageBitmap(displayImage);
							} else {
								imViewNotificationContact.setImageBitmap(_bmp);
							}
						}
					} else {
						displayName = GlobalConfig_Methods.getContactNameFromPhone(mActivityTabs, mDisplayCountryCode + phoneNumber);//1-9-2016
						if (displayName.trim().equalsIgnoreCase("")) {
							displayName = GlobalConfig_Methods.getContactName(mActivityTabs, phoneNumber);//1-9-2016
						}
						displayImage = GlobalConfig_Methods.getContactBitmap(mActivityTabs, phoneNumber);
						if (displayImage != null) {
							imViewNotificationContact.setImageBitmap(displayImage);
						} else {
							imViewNotificationContact.setImageBitmap(_bmp);
						}
					}
				}
			}

			if (!displayName.trim().equals("")) {
				tvContactName.setText(displayName);
			}
			if (!phoneNumber.trim().equals("")) {
				if (!(mDisplayCountryCode.trim().equalsIgnoreCase(""))) {
					tvContactNumber.setText(mDisplayCountryCode + " " + phoneNumber);
				} else {
					tvContactNumber.setText(phoneNumber);
				}
			}
			if (displayName.trim().equals("")) {
				if (!objContactDetail.getName().trim().equals(""))
					tvContactName.setText(objContactDetail.getName());
				else {
					tvContactName.setText("Unknown");
					checkInternetConnection_UserDetails();
				}
			}
		}

		if (objUserDetailBean != null) {
			tvContactName.setText("Unknown");
			checkInternetConnection_UserDetails();
			imViewNotificationContact.setBackgroundResource(R.drawable.no_image);
			//			tvContactNumber.setText(objUserDetailBean.getNumber());
		}
		listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
		listMessageDetailsHolder = new ArrayList<SendMessageReponseDataBean>();
		if (objContactDetail != null) {
			listMessageDetails = DBQuery.getAllMessages(mActivityTabs, objContactDetail.getBBID());
			adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, objContactDetail.getBBID());
		} else if (objUserDetailBean != null) {
			try {
				listMessageDetails = DBQuery.getAllMessages(mActivityTabs, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
				adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
			} catch (Exception e) {
				e.getMessage();
			}
		}
		listMessageDetailsHolder = listMessageDetails;
		adapterChat.notifyDataSetChanged();
		lvChatMessages.setAdapter(adapterChat);
		if (objContactDetail != null) {
			if (!DBQuery.getAllMessages(mActivityTabs, objContactDetail.getBBID()).isEmpty()) {
				lvChatMessages.setSelection(DBQuery.getAllMessages(mActivityTabs, objContactDetail.getBBID()).size());
			}
		} else if (objUserDetailBean != null) {
			try {
				if (!DBQuery.getAllMessages(mActivityTabs, Integer.parseInt(objUserDetailBean.getFrom_user_id())).isEmpty()) {
					lvChatMessages.setSelection(DBQuery.getAllMessages(mActivityTabs, Integer.parseInt(objUserDetailBean.getFrom_user_id())).size());
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
		etMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (ll_mssges.getVisibility() == View.VISIBLE) {
					ll_mssges.setVisibility(View.GONE);
				}
				if (etMessage.getText().toString().trim().equals("")) {
					btnSendMessage.setVisibility(View.GONE);
					btnPredefinedMessages.setVisibility(View.VISIBLE);
//					btnPredefinedMessagesImage.setVisibility(View.VISIBLE);
					//					llPredefinedMessages.setVisibility(View.VISIBLE);
				} else {
					btnSendMessage.setVisibility(View.VISIBLE);
					btnPredefinedMessages.setVisibility(View.GONE);
//					btnPredefinedMessagesImage.setVisibility(View.GONE);
					//					llPredefinedMessages.setVisibility(View.GONE);
				}
			}
		});

		lvPredefinedMessages.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etMessage);
				return false;
			}
		});

		lvPredefinedMessages.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
									int position, long arg3) {
				adapterPredefinedMessages.setRowColor(position, true);
				if (adapterPredefinedMessages.listType.equals("init")) {
					etMessage.setText(listInitMessages.get(position).getMessage());
				} else if (adapterPredefinedMessages.listType.equals("response")) {
					etMessage.setText(listResponseMessages.get(position).getMessage());
				}
			}
		});

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	@Override
	public void onPause() {

		try {
			disableRefreshing();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etMessage);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			enableRefreshing();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (INotifyCount != null) {
			INotifyCount.yes();
		}
	}

	/**
	 * Method to initialize timer to call web service to get message
	 */
	public void initTimerTask() {
		try {

			if (timer == null)
				timer = new Timer();
			if (timerTask == null)
				timerTask = new TimerTask() {
					@Override
					public void run() {
						try {

							if (!webservices_oncall) {
								mActivityTabs.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final Message msg = Message.obtain(
												messagerefresh_handler, refresh, null);
										messagerefresh_handler.dispatchMessage(msg);
									}
								});
							}

						} catch (Exception e) {
							e.getMessage();
						}
					}
				};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to run timer to call web service to get message
	 */
	private void enableRefreshing() {
		try {

			if (timer != null) {
				if (timerTask != null) {
					timer.schedule(timerTask, 0, 3000);
				} else {
					timerTask.cancel();
					timerTask = null;
					timer.cancel();
					timer = null;
					initTimerTask();
					timer.schedule(timerTask, 0, 3000);
				}

			} else {
				if (timerTask != null) {
					timerTask.cancel();
					timerTask = null;
				}
				initTimerTask();
				timer.schedule(timerTask, 0, 3000);
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * Method to stop timer to call web service to get message
	 */
	private void disableRefreshing() {
		try {

			if (timerTask != null) {
				timerTask.cancel();
				timerTask = null;
			}
			if (timer != null) {
				timer.cancel();
				timer = null;
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * handler for the timer to call web service to get message
	 */
	@SuppressLint("HandlerLeak")
	final Handler messagerefresh_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
					case refresh:
						MessageRequestBean objMessageRequestBean = null;
						if (objContactDetail != null) {
							objMessageRequestBean = new MessageRequestBean(String.valueOf(objContactDetail.getBBID()));
						} else if (objUserDetailBean != null) {
							objMessageRequestBean = new MessageRequestBean(String.valueOf(objUserDetailBean.getFrom_user_id()));
						}
						getMessageRequestBean(objMessageRequestBean);
						break;
					case stoprefresh:
						break;
					case donothing:
						break;
					default:
						break;
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
	};

	/**
	 * Method to send request paramaters to the web service for receiving the chat message
	 *
	 * @param objMessageRequestBean
	 */
	private void getMessageRequestBean(MessageRequestBean objMessageRequestBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(objMessageRequestBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.GET_MESSAGE, stringEntity,
					getMessageResponsehandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}


	/**
	 * async task to send request paramaters to the web service for receiving the chat message
	 */
	AsyncHttpResponseHandler getMessageResponsehandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("MessageGetResponse", "OnSuccess", response.toString());
					getResponseGetMessage(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("MessageGetResponse", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/**
	 * mathod to handle response we got from the server as we received any chat message sent to us
	 *
	 * @param response
	 */
	private void getResponseGetMessage(String response) {
		try {
			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
				gson = new Gson();
				ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				GetMessageResponseBean get_Response = null;
				try {
					get_Response = gson.fromJson(response, GetMessageResponseBean.class);
				} catch (Exception e) {
					e.getMessage();
				}
				if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) {
					// Enter Messages In Datatbase
					listGetMessageDetails = new ArrayList<GetMessageResponseDataBean>();
					listGetMessageDetails = get_Response.getData;
					//add data in list to update List with updated selected position
					SendMessageReponseDataBean objGetMessage = new SendMessageReponseDataBean();
					objGetMessage.setMessage_id(listGetMessageDetails.get(0).getMessage_id());
					objGetMessage.setFrom_user_id(listGetMessageDetails.get(0).getFrom_user_id());
					objGetMessage.setFrom_user_phone(listGetMessageDetails.get(0).getFrom_user_phone());
					objGetMessage.setTo_user_id(listGetMessageDetails.get(0).getTo_user_id());
					objGetMessage.setMessage(Uri.decode(Uri.decode(listGetMessageDetails.get(0).getMessage())));
					objGetMessage.setStatus(listGetMessageDetails.get(0).getStatus());
					objGetMessage.setDatatime(listGetMessageDetails.get(0).getDatetime());
					objGetMessage.setName(listGetMessageDetails.get(0).getName());
					listMessageDetailsHolder.add(objGetMessage);
					DBQuery.insertGetMessage(mActivityTabs, listGetMessageDetails);
					listGetMessageDetails = new ArrayList<GetMessageResponseDataBean>();
					if (objContactDetail != null) {
						listMessageDetails = DBQuery.getAllMessages(mActivityTabs, objContactDetail.getBBID());
						adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, objContactDetail.getBBID());
					} else if (objUserDetailBean != null) {
						try {
							listMessageDetails = DBQuery.getAllMessages(mActivityTabs, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
							adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
						} catch (Exception e) {
							e.getMessage();
						}
					}
					refreshChatView();
				}
			} else {
				Log.d("improper_response", response);
				ShowDialog.alert(mActivityTabs, "",
						getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/*
	 * Interface to refresh the list according to messages category type
	 * */
	INotifyGalleryDialog iObject = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			// Initiation Messages
			listInitMessages = new ArrayList<InitResponseMessageBean>();
			ArrayList<InitResponseMessageBean> listInit = new ArrayList<InitResponseMessageBean>();

			listInitMessages = DBQuery.getInitResponseMessages(mActivityTabs, 0, 0);//Init Unlocked
			if (DBQuery.getInitResponseMessages(mActivityTabs, 0, 1).size() > 0) {
				listInit = DBQuery.getInitResponseMessages(mActivityTabs, 0, 1); // Init Locked
				for (int i = 0; i < listInit.size(); i++) {
					listInitMessages.add(listInit.get(i));
				}
			}
			adapterPredefinedMessages = new MessagePredefinedAdapter(mActivityTabs, listInitMessages, "init");
			adapterPredefinedMessages.notifyDataSetChanged();
			lvPredefinedMessages.setAdapter(adapterPredefinedMessages);
			ll_mssges.setVisibility(View.VISIBLE);

		}

		@Override
		public void no() {
			// Response Messages
			listResponseMessages = new ArrayList<InitResponseMessageBean>();
			ArrayList<InitResponseMessageBean> listResp = new ArrayList<InitResponseMessageBean>();
			listResponseMessages = DBQuery.getInitResponseMessages(mActivityTabs, 1, 0);//Resp Unlocked
			if (DBQuery.getInitResponseMessages(mActivityTabs, 1, 1).size() > 0) {
				listResp = DBQuery.getInitResponseMessages(mActivityTabs, 1, 1);// Resp Locked
				for (int i = 0; i < listResp.size(); i++) {
					listResponseMessages.add(listResp.get(i));
				}
			}
			adapterPredefinedMessages = new MessagePredefinedAdapter(mActivityTabs, listResponseMessages, "response");
			adapterPredefinedMessages.notifyDataSetChanged();
			lvPredefinedMessages.setAdapter(adapterPredefinedMessages);
			ll_mssges.setVisibility(View.VISIBLE);
		}
	};

	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection_UserDetails() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			BBIDUserDetailsBean bbidUserDetailBean = null;
			try {//objUserDetailBean.getFrom_user_id()

				if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == this.from_user_id_delete) {
					bbidUserDetailBean = new BBIDUserDetailsBean(String.valueOf(this.to_user_id_delete));//String.valueOf(objUserDetailBean.getTo_user_id())
				} else if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == this.to_user_id_delete)//objUserDetailBean.getTo_user_id()
				{
					bbidUserDetailBean = new BBIDUserDetailsBean(String.valueOf(this.from_user_id_delete));//objUserDetailBean.getFrom_user_id()
				}

			} catch (Exception e) {
				e.getMessage();
			}

			//			}
			sendBBIDUserDetailsRequest(bbidUserDetailBean);
		} else {
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
		}
	}

	//method to send parameters to handle request made to get the user details
	private void sendBBIDUserDetailsRequest(BBIDUserDetailsBean bbidUserDetailBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(bbidUserDetailBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.GET_BBID,
					stringEntity, getBBIDUserDetailsResponseHandler,
					mActivityTabs.getString(R.string.private_key), saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to handle request made to get the user details
	AsyncHttpResponseHandler getBBIDUserDetailsResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("getBBIDUserDetailsResponseHandler", "OnSuccess", response.toString());
					getResponseUserDetailsBBID(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("getBBIDUserDetailsResponseHandler", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/**
	 * handle response for the request being made for getting User Details via BBID
	 *
	 * @param response
	 */
	private void getResponseUserDetailsBBID(String response) {
		try {
			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
				gson = new Gson();
				GetBBIDUserDetailsBean get_Response = gson.fromJson(response, GetBBIDUserDetailsBean.class);
				tvContactName.setText(Uri.decode(get_Response.getName()));
				if ((get_Response.country_code != null) && !(get_Response.country_code.trim().equalsIgnoreCase(""))) {
					tvContactNumber.setText(get_Response.country_code + " " + get_Response.number);
				} else {
					tvContactNumber.setText(get_Response.number);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private void refreshChatView() {
		adapterChat.notifyDataSetChanged();
		lvChatMessages.setAdapter(adapterChat);
		if (!adapterChat.listChatMessages.isEmpty()) {
			lvChatMessages.setSelection(adapterChat.listChatMessages.size() - 1);//DBQuery.getAllMessages(mActivityTabs,objContactDetail.getBBID()).size()
		}
	}

	/**
	 * interface for the confirmation to delete chat
	 */

	INotifyGalleryDialog iNotifyDeleteChat = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			DBQuery.deleteUserChat(mActivityTabs, to_user_id_delete, from_user_id_delete);
			adapterChat.listChatMessages = new ArrayList<SendMessageReponseDataBean>();
			refreshChatView();
		}

		@Override
		public void no() {
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnBack:
				((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();
				refreshChatView();
				break;
			case R.id.tvDeleteChat:
				if (adapterChat.listChatMessages.isEmpty()) {
					ImageRequestDialog dialog = new ImageRequestDialog();
					dialog.newInstance("", mActivityTabs, "There is no conversation to be deleted", "", null);
					dialog.show(getChildFragmentManager(), "test");
				} else {
					DeleteChatConfirmationDialog dialog = new DeleteChatConfirmationDialog();
					dialog.newInstance("", mActivityTabs, "Are you sure you want to delete this conversation", "", iNotifyDeleteChat);
					dialog.setCancelable(false);
					dialog.show(getChildFragmentManager(), "test");
				}
				break;
			case R.id.btnSendMessage:
				checkInternetConnection();
				break;
			case R.id.btnPredefinedMessages:
				InitResponseMessages initRepsonseMessages = new InitResponseMessages();
				initRepsonseMessages.newInstance("Preconfigured Message Type",
						mActivityTabs, iObject);
				initRepsonseMessages.setCancelable(false);
				initRepsonseMessages.show(getChildFragmentManager(), "test");
				break;
			// Select image option
//            case R.id.btnPredefinedMessagesImage:
//               InitResponseMessagesImage initResponseMessagesImage = new InitResponseMessagesImage();
//				initResponseMessagesImage.newInstance("Selected Images",
//                        mActivityTabs, iObject);
//				initResponseMessagesImage.setCancelable(false);
//				initResponseMessagesImage.show(getChildFragmentManager(), "test");
//                break;

			case R.id.btnHome:
				if (mActivityTabs instanceof HomeScreenActivity) {
					((HomeScreenActivity) mActivityTabs).startActivity(new Intent(mActivityTabs, HomeScreenActivity.class));
					((HomeScreenActivity) mActivityTabs).finish();
				}
			case R.id.rlChat:
				break;
			default:
				break;
		}
	}

	// Save send message

	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
			MessageSendBean messageSendBean = null;
			if (objContactDetail != null) {
				messageSendBean = new MessageSendBean(String.valueOf(objContactDetail.getBBID()), Uri.encode(etMessage.getText().toString()));
			} else if (objUserDetailBean != null) {
				messageSendBean = new MessageSendBean(String.valueOf(objUserDetailBean.getFrom_user_id()), Uri.encode(etMessage.getText().toString()));
			}
			sendMessageRequest(messageSendBean);
			etMessage.setText("");
		} else {
			GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
		}
	}

	/**
	 * Method to send request paramaters to the web service for sending the chat message
	 *
	 * @param :objMessageRequestBean
	 */
	private void sendMessageRequest(MessageSendBean messageSendBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(messageSendBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.SEND_MESSAGE, stringEntity,
					sendMessageResponsehandler,
					mActivityTabs.getString(R.string.private_key),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}


	/**
	 * async task to send request paramaters to the web service for sending the chat message
	 */
	AsyncHttpResponseHandler sendMessageResponsehandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("MessageGetResponse", "OnSuccess", response.toString());
					getResponseSendMessage(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null) {
				Logs.writeLog("MessageGetResponse", "onFailure", response);
			}
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/**
	 * mathod to handle response we got from the server as we sent any chat message to the user
	 *
	 * @param response
	 */
	private void getResponseSendMessage(String response) {
		try {
			String response2 = "";
			if (response.contains("</div>") || response.contains("<h4>") || response.contains("php")) {
				response2 = response.substring(response.indexOf("response_code") - 2, response.length());
			} else {
				response2 = response;
			}

			if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
				gson = new Gson();
				ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				SendMessageResponse get_Response = gson.fromJson(response2, SendMessageResponse.class);
				if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) {
					// Enter Messages In Datatbase
					listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
					//				addChatView();
					SendMessageReponseDataBean objSendMessage = new SendMessageReponseDataBean();
					objSendMessage.setMessage_id(get_Response.getData.message_id);
					objSendMessage.setFrom_user_id(get_Response.getData.from_user_id);
					objSendMessage.setFrom_user_phone(get_Response.getData.from_user_phone);
					objSendMessage.setTo_user_id(get_Response.getData.to_user_id);
					objSendMessage.setMessage(Uri.decode(get_Response.getData.message));
					objSendMessage.setStatus(get_Response.getData.status);
					objSendMessage.setDatatime(get_Response.getData.datatime);
					objSendMessage.setName(get_Response.getData.name);
					listMessageDetails.add(objSendMessage);
					DBQuery.insertMessage(mActivityTabs, listMessageDetails);

					if (objContactDetail != null) {
						listMessageDetails = DBQuery.getAllMessages(mActivityTabs, objContactDetail.getBBID());
						adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, objContactDetail.getBBID());
					} else if (objUserDetailBean != null) {
						try {
							listMessageDetails = DBQuery.getAllMessages(mActivityTabs, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
							adapterChat = new ChatAdapter(mActivityTabs, listMessageDetails, Integer.parseInt(objUserDetailBean.getFrom_user_id()));
						} catch (Exception e) {
							e.getMessage();
						}
					}

					refreshChatView();
				} else if (get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_1)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_2)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_5)) {
					dialogErrorMessage.newInstance("", ((HomeScreenActivity) mActivityTabs),
							get_Response.response_message, "", null);
					dialogErrorMessage.show(((HomeScreenActivity) mActivityTabs).
							getSupportFragmentManager(), "test");
				}
			} else {
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onSendMessageSuccess() {
		etMessage.setText("");
		Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpdateMessage(Chat chat) {
		adapterChat.update(chat);
	}

	@Override
	public void onSendMessageFailure(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGetMessagesSuccess(Chat chat) {
		if (adapterChat == null) {
			adapterChat = new ChatAdapter(getActivity(), new ArrayList<Chat>());
			lvChatMessages.setAdapter(adapterChat);
		}
		adapterChat.add(chat);
		lvChatMessages.smoothScrollToPosition(adapterChat.getItemCount() - 1);
	}

	@Override
	public void onGetMessagesFailure(String message) {
		try {
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND) {
			if (getArguments().getString(Constants.ARG_GROUPS_NAME) == null) {
				sendMessage();
			} else {
				sendMessageforGroup();
			}
			return true;
		}
		return false;
	}

	private void sendMessageforGroup() {

		String message = etMessage.getText().toString();
		String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);

		Chat chat = new Chat(senderUid,message);
		mChatPresenter.sendMessageforGroup(getActivity().getApplicationContext(),chat,receiverFirebaseToken,getArguments().getString(Constants.ARG_GROUPS_NAME));
	}


	@SuppressLint("LongLogTag")
	private void sendMessage() {
		String displayName = FirebaseAuth.getInstance().getCurrentUser().getEmail().
				substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().lastIndexOf("@"));
		String message = etMessage.getText().toString();
		String receiver = getArguments().getString(Constants.ARG_EMAIL);
		String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
		String email = receiver;
		String Firebaseuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);
		String deviceToken = FirebaseInstanceId.getInstance().getToken();
		String fuid = databaseReference.child(Constants.ARG_CHAT_ROOMS).child(Constants.ARG_SOLO_MESSAGES).push().getKey();
		String createdOn = String.valueOf(System.currentTimeMillis());

		Log.e(TAG, "dataSnapshot   timestamp 11....... " + "\n"+
				"displayName......" + displayName +"\n"+
				"message......." + message  +"\n" +
				"email..........." + email +"\n"+
				"receiver.........." + receiver +"\n"+
				"receiverUid........" + receiverUid +"\n"+
				"Firebaseuid........." + Firebaseuid +"\n"+
				"receiverFirebaseToken..........." + receiverFirebaseToken +"\n"+
				"deviceToken.........." + deviceToken +"\n"+
				"uid.........." + fuid
		);

		ChatInfo chatInfo = new ChatInfo("Solo");
		Chat chat = new Chat(displayName,
				email,
				Firebaseuid,
				receiverUid,
				message,
				String.valueOf(System.currentTimeMillis()),
				deviceToken,
				fuid
		);

		mChatPresenter.sendMessage(getActivity().getApplicationContext(),
				chat,chatInfo,
				receiverFirebaseToken);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		Log.e("2", "2");
		try {
			Log.e("intent", "intent" + intent.getType());
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);

		} catch (android.content.ActivityNotFoundException ex) {

		}

	   }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

			Log.d("onActivityResult", "......");
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			UploadPostTask uploadPostTask = new UploadPostTask();
			uploadPostTask.execute(imageBitmap);


		}if (resultCode == RESULT_OK) {
			if (requestCode == FILE_SELECT_CODE) {
				Uri selectedImageUri = data.getData();

				// OI FILE Manager
				String filemanagerstring = selectedImageUri.getPath();

				// MEDIA GALLERY
				String selectedImagePath = getPath(selectedImageUri);
				if (selectedImagePath != null) {



                    /*Intent intent = new Intent(HomeActivity.this,
                            VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
				}
			}
		}
		}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	private class UploadPostTask
		 extends AsyncTask<Bitmap, Void, Void> {

			@Override
			protected Void doInBackground(Bitmap... params) {
				Bitmap bitmap = params[0];
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
				storageRef.child(UUID.randomUUID().toString() + "jpg").putBytes(
						byteArrayOutputStream.toByteArray()).addOnSuccessListener(
						new OnSuccessListener<UploadTask.TaskSnapshot>() {
							@Override
							public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
								if (taskSnapshot.getDownloadUrl() != null) {
									final String imageUrl = taskSnapshot.getDownloadUrl().toString();
									String storageImageUrl = taskSnapshot.getStorage().toString()!=null?taskSnapshot.getStorage().toString():null;
									Log.e("imageURL", imageUrl);
									String message = "";
									String receiver = getArguments().getString(Constants.ARG_EMAIL);
									String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
									String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
									String Firebaseuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
									String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);
									String imageA = imageUrl;
									String imageI = storageImageUrl;
									ChatInfo chatInfo = new ChatInfo("Solo");
									Chat chat = new Chat(email,
											email,
											Firebaseuid,
											receiverUid,
											message,
											String.valueOf(System.currentTimeMillis()),imageA, imageI);

									mChatPresenter.sendMessageWithImage(getActivity().getApplicationContext(),
											chat,chatInfo,
											receiverFirebaseToken);
                                /*Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                String chkdate = simpleDateFormat.format(timestamp);
                                newMessage.convertdate = chkdate;
                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);

                                databaseReference.push().setValue(message);*/


								}else {
									Log.d("sdsd", "sfd");
								}
							}
						});

				return null;
			}
	}
}
