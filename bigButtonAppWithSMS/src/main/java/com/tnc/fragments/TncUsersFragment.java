package com.tnc.fragments;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.RegisteredBigButtonUsersAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactShareBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.contact.GetContactDetails;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ImageResponse;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * class to display list of TnC Users
 *  @author a3logics
 */
public class TncUsersFragment extends BaseFragmentTabs implements 
SearchView.OnQueryTextListener, SearchView.OnCloseListener 
{
	private TextView tvTitle,tvStep,tvFavoritesList;
	private Button btnBack,btnHome;
	private FrameLayout flBackArrow,flInformation;
	private SearchView searchViewContacts;
	private ListView lvContacts,lvAlphabets;
	private LinearLayout llSearchView;
	private String title;
	private GetContactDetails getFavoriteContacts=null;
	private TransparentProgressDialog dialogProgress=null;
	private 	ContactDetailsBean selectedContactBean=null;
	private 	Context mActivity;
	private RegisteredBigButtonUsersAdapter adapterContactList;
	private int adapterSelected_position;
	private boolean isSearchResultFirstTime=false;
	private ArrayList<BBContactsBean> listContacts;
	private ArrayList<String> listAlphabets=null;
	private AlphabetAdapter adapterAlphabet;
	private int from_user_id_delete=-1,to_user_id_delete=-1;
	private TransparentProgressDialog progress;
	private Gson gson;
	private ArrayList<ContactTilesBean> listTilesAdded;
	private SharedPreference saveState;
	private SQLiteDatabase db;
	private String _BBID="";
	private String name="";
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.contactlistfragment, container, false);
		idInitialization(view);
		return view;
	}

	public TncUsersFragment newInstance(ArrayList<ContactTilesBean> listContactsToShare)
	{
		TncUsersFragment frag = new TncUsersFragment();
		this.listTilesAdded=listContactsToShare;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	/*
	 * Initialization of widgets/views
	 * */
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		progress=new TransparentProgressDialog(getActivity(), R.drawable.customspinner);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvFavoritesList=(TextView) view.findViewById(R.id.tvFavoritesList);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		llSearchView=(LinearLayout) view.findViewById(R.id.llSearchView);
		searchViewContacts=(SearchView) view.findViewById(R.id.searchViewContacts);
		lvContacts=(ListView) view.findViewById(R.id.lvContacts);
		lvAlphabets=(ListView) view.findViewById(R.id.lvAlphabets);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flInformation=(FrameLayout) view.findViewById(R.id.flInformationButton);
		flInformation.setVisibility(View.VISIBLE);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		btnHome.setVisibility(View.VISIBLE);
		tvStep.setText("CONTACT SHARING");
		tvFavoritesList.setText("Select a Recipient User	");
		flBackArrow.setVisibility(View.VISIBLE);
		tvStep.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/StencilStd.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
		SearchManager searchManager = (SearchManager) mActivityTabs.getSystemService(Context.SEARCH_SERVICE);
		searchViewContacts.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		searchViewContacts.setQueryHint("Search from "+getResources().getString(R.string.app_name)+" Users...");
		searchViewContacts.setOnQueryTextListener(this);
		searchViewContacts.setOnCloseListener(this);
		listContacts=new ArrayList<BBContactsBean>();
		
		//fetch Tnc Users from the Database
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
					_BBID=String.valueOf(listContacts.get(position).getBBID());
					name=listContacts.get(position).getName();
					//You are sharing contact information with another user.  Please confirm
					MessageDeleteConfirmation dialog=new MessageDeleteConfirmation();
					dialog.setCancelable(false);
					dialog.newInstance("CONTACT SHARING", mActivityTabs, "You are about to share one or more contact with another user. Please confirm","",iNotifyConfirmation);
					dialog.show(getChildFragmentManager(), "test");		
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
				for(int i=0;i<listContacts.size();i++)
				{
					if(listContacts.get(i).getName().toLowerCase().startsWith(listAlphabets.get(position).toLowerCase()))
					{
						lvContacts.setSelection(i);
						break;
					}
				}
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mActivityTabs instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)getActivity()).startActivity(new Intent(getActivity(),HomeScreenActivity.class));
					((HomeScreenActivity)getActivity()).finish();
				}
			}
		});
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	/**
	 * interface to handle confirmation to send contacts to the tnc user
	 */
	INotifyGalleryDialog iNotifyConfirmation=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterContactList.setRowColor(adapterSelected_position,false);
			adapterContactList.notifyDataSetChanged();
			// In case of user clicked yes to send the contacts to the user
			sendContactDatabaseToServer();
		}

		@Override
		public void no() {
			// In case of user clicked No For not sending the contacts to the user
			adapterContactList.setRowColor(adapterSelected_position,false);
			adapterContactList.notifyDataSetChanged();
		}
	};



	/**
	 * Method to set list to be displayed
	 */
	private void setListAdapter()
	{
		adapterContactList=new RegisteredBigButtonUsersAdapter(mActivityTabs, listContacts);
		adapterContactList.notifyDataSetChanged();
		lvContacts.setAdapter(adapterContactList);
	}

	// In case of search View is Closed
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
	 * @param text typed in the searchview as an input
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
	 * @param text submitted to the searchview
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

	// Send the database file containing contacts information to the server
	private void sendContactDatabaseToServer()
	{
		db=getActivity().openOrCreateDatabase("tiles_test9_database.sqlite", Context.MODE_PRIVATE, null);
		File fileTilesDB=new File(db.getPath());
		progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
		gson=new Gson();
		ContactShareBean objContacShareBean=new ContactShareBean(_BBID);
		try {
			gson = new Gson();
			MyHttpConnection.postFileWithJsonEntityHeaderShareContact(
					mActivityTabs, GlobalCommonValues.SHARECONTACT,
					fileTilesDB,objContacShareBean.getTo_id(),
					uploadTilesDBResponseHandler,
					saveState.getPrivateKey(mActivityTabs),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to handle sending the database file containing contacts information to the server
	AsyncHttpResponseHandler uploadTilesDBResponseHandler = new JsonHttpResponseHandler() {
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
				Logs.writeLog("TileContacts", "OnSuccess",response.toString());/*saveState.setChanged(mActivityTabs, false);*/
				if(response!=null)
				{
					getResponseUploadContacts(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
			{
				Logs.writeLog("TileContacts", "OnFailure",response);
			}
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/**
	 * handle response for the request being to upload button contacts database
	 * 
	 * @param response
	 */
	private void getResponseUploadContacts(String response)
	{
		try {
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
				ImageResponse get_Response = gson.fromJson(response2, ImageResponse.class);
				if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE)) 
				{
					ImageRequestDialog dialogResult=new ImageRequestDialog();
					dialogResult.setCancelable(false);
					String msg="";
					if(listTilesAdded.size()==1)
					{
						msg="Contacts shared successfully!";
					}
					else if(listTilesAdded.size()>1)
					{
						msg="Contacts shared successfully!";
					}
					if(mActivityTabs instanceof HomeScreenActivity)
					{
						dialogResult.newInstance("",((HomeScreenActivity)mActivityTabs),msg,"",null,iNotifyBackupSuccessful);
						dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
					}
					else if(mActivityTabs instanceof MainBaseActivity)
					{
						dialogResult.newInstance("",((MainBaseActivity)mActivityTabs),msg,"",null,iNotifyBackupSuccessful);
						dialogResult.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(), "test");
					}
					dialogResult.show(getChildFragmentManager(), "test");
				}
				else if (get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE) ||
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_1) ||
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_2)  ||
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_4) || 
						get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_4))
				{
					ImageRequestDialog dialogResult=new ImageRequestDialog();
					dialogResult.setCancelable(false);
					dialogResult.newInstance("",mActivityTabs,get_Response.getMessage(),"",null);
					if(mActivityTabs instanceof HomeScreenActivity)
					{
						dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
					}
					else if(mActivityTabs instanceof MainBaseActivity)
					{
						dialogResult.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(), "test");
					}
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//interface to handle ,in case of backup successful 
	INotifyGalleryDialog iNotifyBackupSuccessful=new INotifyGalleryDialog() {

		@Override
		public void yes() {
		}

		@Override
		public void no() {
			//In case of send contact successful
			((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
			((HomeScreenActivity)mActivityTabs).finish();
		}
	};
}
