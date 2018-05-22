package com.tnc.fragments;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.InitResponseMessageEditableAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.DefaultMessagesBeanDB;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.DefaultMessagesResponse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InitiationResponseMessages extends BaseFragmentTabs implements OnClickListener
{
	private LinearLayout llTopView;
	private Context mActivity;
	private FrameLayout flBackArrow,flInformationButton;
	private Button btnBack,btnHome;
	private TextView tvTitle,tvNew,tvInitiation,tvResponse,tvHeaderTitle;//tvHeadingSystemMessages,
	private ToggleButton toggleMessageType;
	private ListView lvEditableMessagesFromDB;
	private InitResponseMessageEditableAdapter adapterInitResponseEditable=null;
	private ArrayList<InitResponseMessageBean> listInitMessages=null;
	private ArrayList<InitResponseMessageBean> listResponseMessages=null;
	private ArrayList<InitResponseMessageBean> listDisplayMessages=null;
	private int type=0;
	private float x1,x2;
	private float y1, y2;
	private int adapterSelected_position;
	private TransparentProgressDialog progress;
	public enum mTypeEnum {initiation, response};

	public InitiationResponseMessages newInstance(Context mActivity)
	{
		InitiationResponseMessages frag = new InitiationResponseMessages();
		//		objInitiationResponseMessages = new InitiationResponseMessages();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.init_response_message_fragment, container, false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		progress = new TransparentProgressDialog(getActivity(), R.drawable.customspinner);
		newInstance(mActivityTabs);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvNew=(TextView) view.findViewById(R.id.tvNew);
		tvInitiation=(TextView) view.findViewById(R.id.tvInitiation);
		tvResponse=(TextView) view.findViewById(R.id.tvResponse);
		tvHeaderTitle=(TextView) view.findViewById(R.id.tvNotificationsTitle);
		tvHeaderTitle.setTextColor(Color.parseColor("#1a649f"));
		lvEditableMessagesFromDB=(ListView) view.findViewById(R.id.lvEditableMessagesFromDB);
		toggleMessageType=(ToggleButton) view.findViewById(R.id.toggleMessageType);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		llTopView=(LinearLayout) view.findViewById(R.id.llTopView);
		flBackArrow.setVisibility(View.VISIBLE);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvHeaderTitle, "fonts/Roboto-Bold_1.ttf");
		HomeScreenActivity.btnHome.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		tvNew.setOnClickListener(this);
		llTopView.setOnClickListener(this);
		toggleMessageType.setChecked(false);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

		//check if messages version is updated

		/**
		 * if(saveState.getIS_DEFAULT_MESSAGES_VERSION_UPDATED(mActivity) ||
		 ((DBQuery.getInitResponseMessages(mActivityTabs,0,0).size())==0) ||
		 ((DBQuery.getInitResponseMessages(mActivityTabs,0,1).size())==0))
		 */

		if(saveState.getIS_DEFAULT_MESSAGES_VERSION_UPDATED(mActivity) ||
				((DBQuery.getInitResponseMessages(mActivityTabs,0,1).size())==0) ||
				((DBQuery.getInitResponseMessages(mActivityTabs,1,1).size())==0)){
			checkInternetConnection();

		}else{//fetch all messages from the database
			displayInitMessages();
		}

		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		});

		lvEditableMessagesFromDB.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View childView, int position,
									long arg3) {
				if(!adapterInitResponseEditable.listMessages.get(position).getMessage().toString().equalsIgnoreCase("System Default Messages") &&
						!adapterInitResponseEditable.listMessages.get(position).getMessage().toString().equalsIgnoreCase("Custom Messages"))
				{
					adapterSelected_position=position;
					adapterInitResponseEditable.setRowColor(adapterSelected_position, true);
					ComposeMessageFragment objComposeMessage=null;
					try {
						boolean isLocked;
						if(adapterInitResponseEditable.listMessages.get(position).getLocked()==0){
							isLocked=false;
						}
						else {
							isLocked=true;
						}

						objComposeMessage=new ComposeMessageFragment();
						objComposeMessage.newInstance(mActivityTabs,adapterInitResponseEditable.listMessages.get(position).getType(),
								adapterInitResponseEditable.listMessages.get(position).getId(),
								adapterInitResponseEditable.listMessages.get(position).getMessage(),
								isLocked,iNotifyGalleryDialog);
						((HomeScreenActivity)mActivityTabs).setFragment(objComposeMessage);

					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});

		toggleMessageType.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)  //In Case of Response Messages
				{
					type=1;
					tvInitiation.setTextColor(Color.parseColor("#C5C5C5"));//#A7A5A5
					tvResponse.setTextColor(Color.parseColor("#000000"));
					displayResponseMessages();
				}
				else   //In Case of Initiation Messages
				{
					type=0;
					tvInitiation.setTextColor(Color.parseColor("#000000"));
					tvResponse.setTextColor(Color.parseColor("#C5C5C5"));//#A7A5A5
					displayInitMessages();
				}
			}
		});

		toggleMessageType.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent touchevent) {

				switch(touchevent.getAction())
				{

					// when user first touches the screen we get x and y coordinate
					case MotionEvent.ACTION_DOWN:
					{
						x1 = touchevent.getX();
						y1 = touchevent.getY();
						break;
					}

					case MotionEvent.ACTION_UP:
					{
						x2 = touchevent.getX();
						y2 = touchevent.getY();

						if (x1 == x2)
						{
							if(toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(false);
							}
							else if(!toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(true);
							}
						}
						else if (y1 == y2)
						{
							if(toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(false);
							}
							else if(!toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(true);
							}
						}

						//if left to right sweep event on screen
						else if (x1 < x2)
						{
							if(toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(false);
							}
							else if(!toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(true);
							}
						}

						// if right to left sweep event on screen
						else if (x1 > x2)
						{
							if(toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(false);
							}
							else if(!toggleMessageType.isChecked())
							{
								toggleMessageType.setChecked(true);
							}
						}

						// if UP to Down sweep event on screen
						else if (y1 < y2)
						{
						}

						//if Down to UP sweep event on screen
						else  if (y1 > y2)
						{
						}
						break;
					}
				}
				return true;
			}
		});

		tvInitiation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toggleMessageType.isChecked()) {
					toggleMessageType.setChecked(false);
				} else if (!toggleMessageType.isChecked()) {
					toggleMessageType.setChecked(true);
				}
			}
		});

		tvResponse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toggleMessageType.isChecked()) {
					toggleMessageType.setChecked(false);
				} else if (!toggleMessageType.isChecked()) {
					toggleMessageType.setChecked(true);
				}
			}
		});
	}

	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			getInitMessagesRequest();
		}
		else{
			displayInitMessages();
		}
	}

	//Method to call web service to get configured mesages from the server
	private void getInitMessagesRequest()
	{
		MyHttpConnection.getWithoutPara(mActivityTabs,GlobalCommonValues.GET_DEFAULT_MESSAGES,
				mActivity.getResources().getString(R.string.private_key),defaultMessagesResponsehandler);
	}

	// Async task to call web service to get configured mesages
	AsyncHttpResponseHandler defaultMessagesResponsehandler = new JsonHttpResponseHandler() {
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
				if(response!=null){
					Logs.writeLog("Default Mesasges", "OnSuccess",response.toString());
					getResponse(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Default Mesasges", "OnFailure",response);
			displayInitMessages();
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/*
	 * Handling Response from the Server for the request being sent to get onfigured mesages
	 */
	private void getResponse(String response) {
		try {
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				listInitMessages=new ArrayList<InitResponseMessageBean>();
				try {
					Gson gson = new Gson();
					DefaultMessagesResponse get_Response = gson.fromJson(response,DefaultMessagesResponse.class);
					if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){

						//Delete all the default messages of older version
						DBQuery.deleteConfigMessageFromDB(mActivityTabs, 1);

						ArrayList<DefaultMessagesBeanDB> mListDefaultMessages = new ArrayList<DefaultMessagesBeanDB>();
						if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
						{
							DefaultMessagesBeanDB mObjDefaultMessagesBeanDB;

							int maxID = DBQuery.getConfigMessagesMaxCount(mActivityTabs);

							if(maxID==-1 || maxID==0){
								maxID = 1;
							}else{
								maxID=maxID+1;
							}
							for(int i=0;i<get_Response.getData().size();i++){
								mObjDefaultMessagesBeanDB= new DefaultMessagesBeanDB();
								mObjDefaultMessagesBeanDB.setId(maxID+i);
								mObjDefaultMessagesBeanDB.setMessage(get_Response.getData().get(i).getMessage());
								mObjDefaultMessagesBeanDB.setIsLocked(1);

								String mType = get_Response.getData().get(i).getType();
								int mTypeMessage = (mType.equals("initiation") ? 0: 1);  // 0- initiation  1- response

								mObjDefaultMessagesBeanDB.setType(mTypeMessage);
								mListDefaultMessages.add(mObjDefaultMessagesBeanDB);
							}
							saveState.setIS_DEFAULT_MESSAGES_VERSION_UPDATED(mActivity, false);
							// Insert Emergency Number in the Database
							DBQuery.insertConfigMessageFromWebService(mActivity,mListDefaultMessages);
							displayInitMessages();
						}
					}
					else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){
						displayInitMessages();

					}
				}
				catch (Exception e){
					e.getMessage();
				}

			} else {
				displayInitMessages();
				//ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * interface to notify the list of deselecting the selected position
	 */
	INotifyGalleryDialog iNotifyContactListing = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterInitResponseEditable.setRowColor(adapterSelected_position, false);

		}

		@Override
		public void no() {
			adapterInitResponseEditable.setRowColor(adapterSelected_position, false);
		}
	};

	/**
	 * Method to display pre-configured messages
	 */
	private void displayInitMessages()
	{
		listInitMessages=new ArrayList<InitResponseMessageBean>();
		adapterInitResponseEditable=new InitResponseMessageEditableAdapter(mActivityTabs, listInitMessages);
		adapterInitResponseEditable.listMessages=new ArrayList<InitResponseMessageBean>();

		//For Initiation User Messages
		//		listInitMessages2=new ArrayList<InitResponseMessageBean>();
		listInitMessages=DBQuery.getInitResponseMessages(mActivityTabs,0,0);
		if(DBQuery.getInitResponseMessages(mActivityTabs,0,0).size()>0)
		{
			InitResponseMessageBean objCustomMessages=new InitResponseMessageBean();
			objCustomMessages.setMessage("Custom Messages");
			adapterInitResponseEditable.listMessages.add(objCustomMessages);
			for(int i=0;i<listInitMessages.size();i++)
			{
				adapterInitResponseEditable.listMessages.add(listInitMessages.get(i));
			}
		}
		lvEditableMessagesFromDB.setAdapter(adapterInitResponseEditable);
		adapterInitResponseEditable.notifyDataSetChanged();

		//For System Initiation Messages
		InitResponseMessageBean objSystemMessages=new InitResponseMessageBean();
		objSystemMessages.setMessage("System Default Messages");
		adapterInitResponseEditable.listMessages.add(objSystemMessages);
		//For Inititation System Messages
		listInitMessages=DBQuery.getInitResponseMessages(mActivityTabs,0,1);
		for(int i=0;i<listInitMessages.size();i++)
		{
			adapterInitResponseEditable.listMessages.add(listInitMessages.get(i));
		}
		adapterInitResponseEditable.notifyDataSetChanged();
		lvEditableMessagesFromDB.setAdapter(adapterInitResponseEditable);
	}

	private void displayResponseMessages()
	{
		listResponseMessages=new ArrayList<InitResponseMessageBean>();
		adapterInitResponseEditable=new InitResponseMessageEditableAdapter(mActivityTabs, listResponseMessages);
		adapterInitResponseEditable.listMessages=new ArrayList<InitResponseMessageBean>();

		//For Response User Messages
		listResponseMessages=DBQuery.getInitResponseMessages(mActivityTabs,1,0);
		if(DBQuery.getInitResponseMessages(mActivityTabs,1,0).size()>0)
		{
			InitResponseMessageBean objCustomMessages=new InitResponseMessageBean();
			objCustomMessages.setMessage("Custom Messages");
			adapterInitResponseEditable.listMessages.add(objCustomMessages);
			for(int i=0;i<listResponseMessages.size();i++)
			{
				adapterInitResponseEditable.listMessages.add(listResponseMessages.get(i));
			}
			adapterInitResponseEditable.notifyDataSetChanged();
		}
		lvEditableMessagesFromDB.setAdapter(adapterInitResponseEditable);

		//For System Response Messages
		InitResponseMessageBean objSystemMessages=new InitResponseMessageBean();
		objSystemMessages.setMessage("System Default Messages");
		adapterInitResponseEditable.listMessages.add(objSystemMessages);
		//For Response System Messages
		listResponseMessages=DBQuery.getInitResponseMessages(mActivityTabs,1,1);
		for(int i=0;i<listResponseMessages.size();i++)
		{
			adapterInitResponseEditable.listMessages.add(listResponseMessages.get(i));
		}
		adapterInitResponseEditable.notifyDataSetChanged();
		lvEditableMessagesFromDB.setAdapter(adapterInitResponseEditable);
	}

	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			displayInitMessages();
			adapterInitResponseEditable.setRowColor(adapterSelected_position, false);
		}

		@Override
		public void no() {
			displayResponseMessages();
			adapterInitResponseEditable.setRowColor(adapterSelected_position, false);
		}
	};

	@Override
	public void onClick(View v)
	{
		//		if(v.getId()==R.id.btnInformation)
		//		{
		//
		//		}
		if(v.getId()==R.id.llTopView)
		{

		}
		else if(v.getId()==R.id.btnHome)
		{
			if(mActivityTabs instanceof MainBaseActivity)
			{
				((MainBaseActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
				((MainBaseActivity)mActivityTabs).finish();
			}
			else if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
				((HomeScreenActivity)mActivityTabs).finish();
			}
		}
		else if(v.getId()==R.id.tvNew)
		{
			ComposeMessageFragment objComposeFragment=new ComposeMessageFragment();
			objComposeFragment.newInstance(mActivityTabs,type,-1,null,false,iNotifyGalleryDialog);
			((HomeScreenActivity)mActivityTabs).setFragment(objComposeFragment);
		}
	}
}
