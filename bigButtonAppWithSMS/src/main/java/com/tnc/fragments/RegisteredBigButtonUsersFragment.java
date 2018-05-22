package com.tnc.fragments;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.RegisteredBigButtonUsersAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.MessageStatusUpdateBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.contact.GetContactDetails;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display list of registered BB users for chatting 
 *  @author a3logics
 */

public class RegisteredBigButtonUsersFragment extends BaseFragmentTabs implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener
{
	private TextView tvTitle,tvStep,tvFavoritesList;
	private Button btnBack;
	private FrameLayout flBackArrow;
	private SearchView searchViewContacts;
	private ListView lvContacts,lvAlphabets;
	private LinearLayout llSearchView;
	private String title;
	private GetContactDetails getFavoriteContacts=null;
	private TransparentProgressDialog dialogProgress=null;
	//	ContentResolver contentResolver=null;
	private ContactDetailsBean selectedContactBean=null;
	private Context mActivity;
	private RegisteredBigButtonUsersAdapter adapterContactList;
	private int adapterSelected_position;
	private boolean isSearchResultFirstTime=false;
	private ArrayList<BBContactsBean> listContacts;
	private ArrayList<String> listAlphabets=null;
	private AlphabetAdapter adapterAlphabet;
	private int from_user_id_delete=-1,to_user_id_delete=-1;
	private Gson gson;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.contactlistfragment, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		isSearchResultFirstTime=false;
		lvContacts.setEnabled(false);
		lvContacts.setClickable(false);
		lvAlphabets.setEnabled(false);
		lvAlphabets.setClickable(false);
		searchViewContacts.clearFocus();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		searchViewContacts.setQuery("",false);
		if(adapterContactList!=null)
		{
			adapterContactList.notifyDataSetChanged();
			lvContacts.setAdapter(adapterContactList);
		}
		lvContacts.setTextFilterEnabled(true);
		lvContacts.setEnabled(true);
		lvContacts.setClickable(true);
		lvAlphabets.setEnabled(true);
		lvAlphabets.setClickable(true);
	}

	/*
	 * Initialization of widgets/views
	 * */
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvFavoritesList=(TextView) view.findViewById(R.id.tvFavoritesList);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		llSearchView=(LinearLayout) view.findViewById(R.id.llSearchView);
		searchViewContacts=(SearchView) view.findViewById(R.id.searchViewContacts);
		lvContacts=(ListView) view.findViewById(R.id.lvContacts);
		lvAlphabets=(ListView) view.findViewById(R.id.lvAlphabets);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		tvFavoritesList.setText(getActivity().getResources().getString(R.string.app_name) + " Contacts");
		tvFavoritesList.setAllCaps(true);
		flBackArrow.setVisibility(View.VISIBLE);
		tvStep.setVisibility(View.GONE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
		SearchManager searchManager = (SearchManager) mActivityTabs.getSystemService(Context.SEARCH_SERVICE);
		searchViewContacts.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		searchViewContacts.setQueryHint("Search from "+ getActivity().getResources().getString(R.string.app_name) +" Users...");
		searchViewContacts.setOnQueryTextListener(this);
		searchViewContacts.setOnCloseListener(this);
		listContacts=new ArrayList<BBContactsBean>();
		listContacts=DBQuery.getAllBBContactsOrdered(mActivityTabs);
		if(listContacts.isEmpty())
		{
			BBContactsBean objContactBean=new BBContactsBean();
			objContactBean.setName("No Contact Found");
			listContacts.add(objContactBean);
			lvAlphabets.setClickable(false);
			searchViewContacts.setVisibility(View.GONE);
		}
		else
		{
			lvAlphabets.setClickable(true);
			searchViewContacts.setVisibility(View.VISIBLE);
		}
		setListAdapter();
		listAlphabets=new ArrayList<String>();
		for(int i=65;i<=90;i++)
		{
			listAlphabets.add(String.valueOf((char)(i)));
		}
		adapterAlphabet=new AlphabetAdapter(mActivityTabs,listAlphabets);
		adapterAlphabet.notifyDataSetChanged();
		lvAlphabets.setAdapter(adapterAlphabet);
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		});
		lvContacts.setSelector(R.drawable.list_selector_flatcolor);

		int searchPlateId = searchViewContacts.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlateView = searchViewContacts.findViewById(searchPlateId);
		if (searchPlateView != null) {
			searchPlateView.setBackgroundColor(Color.WHITE);
		}

		try{
			int id = searchViewContacts.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchViewContacts.findViewById(id);
			textView.setTextColor(Color.BLACK);

		}catch(Exception ex){
			ex.printStackTrace();
		}

		lvContacts.setOnItemClickListener(new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
			{
				if(!listContacts.get(position).getName().equalsIgnoreCase("No matching record found") && !listContacts.get(position).getName().equalsIgnoreCase("No Contact Found"))
				{
					adapterSelected_position=position;
					adapterContactList.setRowColor(adapterSelected_position,true);
					BBContactsBean objContactDetail= new BBContactsBean();
					objContactDetail.setName(listContacts.get(position).getName());
					objContactDetail.setPhoneNumber(listContacts.get(position).getPhoneNumber());
					objContactDetail.setBBID(listContacts.get(position).getBBID());
					objContactDetail.setImage(listContacts.get(position).getImage());
					objContactDetail.setCountryCode(listContacts.get(position).getCountryCode());
					//Update Notification
					try{
						DBQuery.updateMessageStatus(mActivityTabs,listContacts.get(position).getBBID(),
								Integer.parseInt(saveState.getBBID(mActivityTabs)));
					} catch(Exception e){
						e.printStackTrace();
					}

					MessageStatusUpdateBean updateStatusBean=new MessageStatusUpdateBean();
					updateStatusBean.setBbid(String.valueOf(listContacts.get(position).getBBID()));
					updateMessageStatus(updateStatusBean);
					((HomeScreenActivity)mActivityTabs).setUnreadMessageCount();
					//Open Chat Window
					MessagePredefinedComposeFragment messagePredefined=new MessagePredefinedComposeFragment();
					try{
						messagePredefined.newInstance(objContactDetail,null,
								Integer.parseInt(saveState.getBBID(mActivityTabs)),listContacts.get(position).getBBID(), null);
					} catch(Exception e){
						e.printStackTrace();
					}
					((HomeScreenActivity)mActivityTabs).setFragment(messagePredefined);
					searchViewContacts.setQuery("",false);
					adapterContactList.notifyDataSetChanged();
				}
			}
		});

		lvAlphabets.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressLint("DefaultLocale")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3)
			{
				//				int selectedPosition=0;
				for(int i=0;i<listContacts.size();i++)
				{
					if(listContacts.get(i).getName().toLowerCase().startsWith(listAlphabets.get(position).toLowerCase()))
					{
						//						selectedPosition=i;
						lvContacts.setSelection(i);
						break;
					}
				}
			}
		});

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	//Call a WebService to update message status from unread to read
	private void updateMessageStatus(MessageStatusUpdateBean updateStatusBean)
	{
		try
		{
			gson=new Gson();
			String stingGson = gson.toJson(updateStatusBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
					GlobalCommonValues.UPDATE_MESSAGE_STATUS,
					stringEntity, updateMessageResponseHandler,
					mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	//async task to handle Call to WebService to update message status from unread to read
	AsyncHttpResponseHandler updateMessageResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			if(response!=null)
				Logs.writeLog("Update Message Status", "OnSuccess",response.toString());
			try {
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Update Message Status", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	/**
	 * Method to set the list that is to be displayed
	 */
	private void setListAdapter()
	{
		adapterContactList=new RegisteredBigButtonUsersAdapter(mActivityTabs, listContacts);
		adapterContactList.notifyDataSetChanged();
		lvContacts.setAdapter(adapterContactList);
	}

	//async task to handle Call to WebService to get TnC user details
	class GetBbUserDetails extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			adapterContactList=new RegisteredBigButtonUsersAdapter(mActivityTabs, listContacts);
			adapterContactList.notifyDataSetChanged();
			lvContacts.setAdapter(adapterContactList);
		}
	}

	@Override
	public boolean onClose()
	{
		adapterContactList.filterData("");
		searchViewContacts.setQuery("",false);
		if(adapterContactList!=null && adapterContactList.listContacts.isEmpty())
		{
			BBContactsBean objContactBean=new BBContactsBean();
			objContactBean.setName("No matching record found");
			listContacts.add(objContactBean);
			adapterContactList=new RegisteredBigButtonUsersAdapter(mActivityTabs, listContacts);
			lvContacts.setAdapter(adapterContactList);
		}
		return false;
	}

	/**
	 * @param :text typed in the searchview as an input
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		if(adapterContactList!=null)
		{
			adapterContactList.filterData(newText);
			if(adapterContactList.listContacts!=null && !adapterContactList.listContacts.isEmpty())
			{
				try
				{
					if(adapterContactList.listContacts.size()<=0)
					{
						BBContactsBean objContactDetail= new BBContactsBean();
						objContactDetail.setName("No matching record found");
						adapterContactList.listContacts.add(objContactDetail);
					}
					adapterContactList.notifyDataSetChanged();
					lvContacts.setAdapter(adapterContactList);
				}
				catch (Exception e)
				{
					e.getMessage();
				}
			}
			else if(adapterContactList!=null && adapterContactList.isEmpty())
			{
				if(adapterContactList.listContacts.size()<=0)
				{
					BBContactsBean objContactDetail= new BBContactsBean();
					objContactDetail.setName("No matching record found");
					adapterContactList.listContacts.add(objContactDetail);
					adapterContactList.notifyDataSetChanged();
					lvContacts.setAdapter(adapterContactList);
				}
			}
		}
		return false;
	}

	/**
	 * @param :text submitted to the searchview
	 */
	@Override
	public boolean onQueryTextSubmit(String query)
	{
		if(adapterContactList!=null)
		{
			adapterContactList.filterData(query);
			if(adapterContactList.listContacts!=null && !adapterContactList.listContacts.isEmpty())
			{
				try
				{
					if(adapterContactList.listContacts.size()<=0)
					{
						BBContactsBean objContactDetail= new BBContactsBean();
						objContactDetail.setName("No matching record found");
						adapterContactList.listContacts.add(objContactDetail);
					}
					adapterContactList.notifyDataSetChanged();
					lvContacts.setAdapter(adapterContactList);
				}
				catch (Exception e)
				{
					e.getMessage();
				}
			}
			else if(adapterContactList!=null && adapterContactList.isEmpty())
			{
				if(adapterContactList.listContacts.size()<=0)
				{
					BBContactsBean objContactDetail= new BBContactsBean();
					objContactDetail.setName("No matching record found");
					adapterContactList.listContacts.add(objContactDetail);
					adapterContactList.notifyDataSetChanged();
					lvContacts.setAdapter(adapterContactList);
				}
			}
		}
		return false;
	}
}
