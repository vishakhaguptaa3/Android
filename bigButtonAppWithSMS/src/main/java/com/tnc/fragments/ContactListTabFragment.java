package com.tnc.fragments;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.ContactListActivityAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.contact.GetContactDetails;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


/**
 * class to display contact list from the phone from the bottom tab on home sacreen 
 *  @author a3logics
 */

public class ContactListTabFragment extends BaseFragmentTabs implements OnClickListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener 
{
	private TextView tvTitle,tvStep,tvFavoritesList;
	private Button btnBack,btnHome;
	private FrameLayout flBackArrow,flInformationButton;
	private SearchView searchViewContacts;
	private ListView lvContacts,lvAlphabets;
	private ContactListActivityAdapter adapterContactList;
	private ArrayList<ContactDetailsBean> listContactDetail;
	private LinearLayout llSearchView;
	private String title;
	private GetContactDetails getFavoriteContacts=null;
	public TransparentProgressDialog dialogProgress=null;
	private ContentResolver contentResolver=null;
	private ContactDetailsBean selectedContactBean=null;
	private int adapterSelected_position;
	private ArrayList<String> listEmptyMessage;
	private boolean isSearchResultFirstTime=false;
	private ArrayList<String> listAlphabets=null;
	private AlphabetAdapter adapterAlphabet;
	private SharedPreference saveState;
	public FragmentManager fragmentManager = null;
	public FragmentTransaction fragmentTransaction = null;
	public Fragment currentFragment;
	public INotifyGalleryDialog iNotifyRefreshSelectedTab;
	int mFirstVisiblePositionList = -1,mLastVisiblePositionList = -1;
	private String mSearchtext = "";

	public ContactListTabFragment newInstance(INotifyGalleryDialog iNotifyRefreshSelectedTab) {
		ContactListTabFragment frag = new ContactListTabFragment();
		this.iNotifyRefreshSelectedTab = iNotifyRefreshSelectedTab;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contactlistfragment, container,
				false);
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
		llSearchView.setVisibility(View.VISIBLE);
		adapterContactList=new ContactListActivityAdapter(getActivity(),listContactDetail,null,false);
		lvContacts.setAdapter(adapterContactList);
		adapterContactList.notifyDataSetChanged();
		lvContacts.setTextFilterEnabled(true);
		lvContacts.setEnabled(true);
		lvContacts.setClickable(true);
		lvAlphabets.setEnabled(true);
		lvAlphabets.setClickable(true);

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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
	}

	/*
	 * Initialization of widgets/views
	 * */
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView)view.findViewById(R.id.tvTitle);
		tvStep=(TextView)view.findViewById(R.id.tvStep);
		tvFavoritesList=(TextView)view.findViewById(R.id.tvFavoritesList);
		flBackArrow=(FrameLayout)view.findViewById(R.id.flBackArrow);
		llSearchView=(LinearLayout)view.findViewById(R.id.llSearchView);
		searchViewContacts=(SearchView)view.findViewById(R.id.searchViewContacts);
		lvContacts=(ListView)view.findViewById(R.id.lvContacts);
		lvAlphabets=(ListView)view.findViewById(R.id.lvAlphabets);
		btnBack=(Button)view.findViewById(R.id.btnBack);
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnHome=(Button)view.findViewById(R.id.btnHome);
		if(saveState.isRegistered(getActivity())){
			flInformationButton.setVisibility(View.VISIBLE);
			btnHome.setVisibility(View.VISIBLE);
			btnHome.setBackgroundResource(R.drawable.refresh);
		}
		if(!saveState.isRegistered(getActivity())){
			btnHome.setVisibility(View.GONE);	
		}
		tvStep.setTextColor(Color.parseColor("#1a649f"));
		tvStep.setText("Contact List");
		tvFavoritesList.setVisibility(View.GONE);
		new GetContacts().execute();
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(mActivityTabs,tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(mActivityTabs,tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(mActivityTabs,tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(mActivityTabs,tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
		try {
			SearchManager searchManager = (SearchManager) mActivityTabs.getSystemService(Context.SEARCH_SERVICE);
			searchViewContacts.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
			searchViewContacts.setQueryHint(getResources().getString(R.string.txtSearchCountryCountry));
		} catch (Exception e) {
			e.getMessage();
		}
		searchViewContacts.setOnQueryTextListener(this);
		searchViewContacts.setOnCloseListener(this);
		btnBack.setOnClickListener(this);

		lvContacts.setSelector(R.drawable.list_selector_flatcolor);
		listAlphabets=new ArrayList<String>();
		for(int i=65;i<=90;i++)
		{
			listAlphabets.add(String.valueOf((char)(i)));
		}
		adapterAlphabet=new AlphabetAdapter(getActivity(),listAlphabets);
		adapterAlphabet.notifyDataSetChanged();
		lvAlphabets.setAdapter(adapterAlphabet);

		// Customizing searchview
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
		btnHome.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(listContactDetail!=null && listContactDetail.size()>0 && saveState.isRegistered(mActivityTabs)){

					mFirstVisiblePositionList = lvContacts.getFirstVisiblePosition();
					mLastVisiblePositionList = lvContacts.getLastVisiblePosition();

					for(int i = mFirstVisiblePositionList; i<=mLastVisiblePositionList;i++){
						GlobalCommonValues.listContactsSendToServer.add(listContactDetail.get(i));
					}

					if(mActivityTabs instanceof HomeScreenActivity)
					{
						if(dialogProgress == null){
							dialogProgress=new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
							dialogProgress.show();
						}else if(dialogProgress!=null && !dialogProgress.isShowing()){
							dialogProgress.show();
						}
						//Refresh List Here
						if(saveState.isRegistered(getActivity())){
							saveState.setRefrehContactList(getActivity(),true);
							saveState.setIS_FROM_HOME(getActivity(),false);
							((HomeScreenActivity)getActivity()).handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if(mFirstVisiblePositionList >-1 && mLastVisiblePositionList>-1){
										((HomeScreenActivity)getActivity()).sendContactsToServer();
										mFirstVisiblePositionList = -1;
										mLastVisiblePositionList = -1;
									}
								}
							}, 100);
						}
					}
				}
			}
		});

		lvAlphabets.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressLint("DefaultLocale")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3)
			{
				for(int i=0;i<listContactDetail.size();i++)
				{
					if(listContactDetail.get(i).get_name().toLowerCase().startsWith(listAlphabets.get(position).toLowerCase()))
					{
						lvContacts.setSelection(i);
						break;
					}
				}
			}
		});

		lvContacts.setOnItemClickListener(new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) 
			{
				if(adapterContactList.listContactsShow!=null && !adapterContactList.listContactsShow.get(position).get_name().equalsIgnoreCase("No matching record found")  &&
						!adapterContactList.listContactsShow.get(position).get_name().equalsIgnoreCase("No Contact Found") && (listEmptyMessage==null || listEmptyMessage.isEmpty()))
				{
					//fetch contact details of the selected contact
					adapterSelected_position=position;
					adapterContactList.setRowColor(adapterSelected_position,true);
					selectedContactBean=new ContactDetailsBean();
					//					if(adapterContactList.listContactsShow.get(position).get_imgpeople()!=null)
					//						selectedContactBean.set_imgpeople(adapterContactList.listContactsShow.get(position).get_imgpeople());
					ImageView imviewContact=(ImageView)arg1.findViewById(R.id.llContactNameParent).findViewById(R.id.llImageHolderparent).findViewById(R.id.llImageHolderparent).findViewById(R.id.llContactNameHolder).findViewById(R.id.imViewContactImage);
					//selectedContactBean.set_imgpeople(((BitmapDrawable)(imviewContact).getDrawable()).getBitmap());
                                        Bitmap mBitmapuserImage = ((BitmapDrawable)(imviewContact).getDrawable()).getBitmap();					
                                        if(mBitmapuserImage!=null)
                                           selectedContactBean.set_imgpeople(mBitmapuserImage);
                                        else
                                        selectedContactBean.set_imgpeople(null);					
                                        
                                        selectedContactBean.set_id(adapterContactList.listContactsShow.get(position).get_id());
					selectedContactBean.set_name(adapterContactList.listContactsShow.get(position).get_name());
					selectedContactBean.set_phone(adapterContactList.listContactsShow.get(position).get_phone());
					selectedContactBean.setsType(adapterContactList.listContactsShow.get(position).getsType());
					if(adapterContactList.listContactsShow.get(position).get_emailid()!=null && !adapterContactList.listContactsShow.get(position).get_emailid().trim().equals(""))
					{
						selectedContactBean.set_emailid(adapterContactList.listContactsShow.get(position).get_emailid());
					}
					if(mActivityTabs instanceof HomeScreenActivity)
					{
						ContactDetailsScreenFragment objContactDetailsScreenFragment = new ContactDetailsScreenFragment();
						objContactDetailsScreenFragment.newInstance(selectedContactBean);
						((HomeScreenActivity)mActivityTabs).setFragment(objContactDetailsScreenFragment);
					}
					searchViewContacts.setQuery("",false);
					adapterContactList.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 * interface to notify the list of deselecting the selected position
	 */
	INotifyGalleryDialog iNotifyContactListing = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterContactList.setRowColor(adapterSelected_position, false); 			
		}
		@Override
		public void no() {
			adapterContactList.setRowColor(adapterSelected_position, false); 			
		}
	};

	@Override
	public void onClick(View v) 
	{
		if(v.getId()==R.id.btnBack)
		{
			if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		}
	}

	//On seachview close
	@Override
	public boolean onClose() 
	{
		adapterContactList.filterData("");
		searchViewContacts.setQuery("",false);
		if(adapterContactList!=null && adapterContactList.listMenu!=null && !adapterContactList.listMenu.isEmpty() && adapterContactList.listMenu.contains("No Contact Found"))
		{
			listEmptyMessage=new ArrayList<String>();
			listEmptyMessage.add("No Contact Found");
			adapterContactList=new ContactListActivityAdapter(mActivityTabs,null,listEmptyMessage,false);
			lvContacts.setAdapter(adapterContactList);
		}
		if(adapterContactList!=null && adapterContactList.listMenu!=null && !adapterContactList.listMenu.isEmpty() && adapterContactList.listMenu.contains("No matching record found"))
		{
			listEmptyMessage=new ArrayList<String>();
			listEmptyMessage.add("No matching record found");
			adapterContactList=new ContactListActivityAdapter(mActivityTabs,null,listEmptyMessage,false);
			lvContacts.setAdapter(adapterContactList);
		}
		return false;
	}

	/**
	 * @param //text typed in the searchview as an input
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		mSearchtext = newText;
		if(adapterContactList!=null)
		{
			adapterContactList.filterData(newText);
			if(adapterContactList.listContactsShow!=null && adapterContactList.listContactsShow!=null && !adapterContactList.listContactsShow.isEmpty())
			{
				if(adapterContactList.listContactsShow.size()<=0)
				{
					ContactDetailsBean objContactDetail= new ContactDetailsBean();
					objContactDetail.set_name("No matching record found");
					adapterContactList.listContactsShow.add(objContactDetail);
				}
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
			else if(adapterContactList!=null && adapterContactList.listContactsShow!=null && adapterContactList.listContactsShow.isEmpty())
			{
				ContactDetailsBean objContactDetail= new ContactDetailsBean();
				objContactDetail.set_name("No matching record found");
				adapterContactList.listContactsShow=new ArrayList<ContactDetailsBean>();
				adapterContactList.listContactsShow.add(objContactDetail);
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
			else if(adapterContactList.listContactsShow==null)
			{
				ContactDetailsBean objContactDetail= new ContactDetailsBean();
				objContactDetail.set_name("No matching record found");
				adapterContactList.listContactsShow=new ArrayList<ContactDetailsBean>();
				adapterContactList.listContactsShow.add(objContactDetail);
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
		}
		return false;
	}

	/**
	 * @param //text submitted to the searchview
	 */
	@Override
	public boolean onQueryTextSubmit(String query) 
	{
		if(adapterContactList!=null)
		{
			adapterContactList.filterData(query);
			if(adapterContactList.listContactsShow!=null && adapterContactList.listContactsShow!=null && !adapterContactList.listContactsShow.isEmpty())
			{
				if(adapterContactList.listContactsShow.size()<=0)
				{
					ContactDetailsBean objContactDetail= new ContactDetailsBean();
					objContactDetail.set_name("No matching record found");
					adapterContactList.listContactsShow.add(objContactDetail);
				}
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
			else if(adapterContactList!=null && adapterContactList.listContactsShow!=null && adapterContactList.listContactsShow.isEmpty())
			{
				ContactDetailsBean objContactDetail= new ContactDetailsBean();
				objContactDetail.set_name("No matching record found");
				adapterContactList.listContactsShow=new ArrayList<ContactDetailsBean>();
				adapterContactList.listContactsShow.add(objContactDetail);
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
			else if(adapterContactList.listContactsShow==null)
			{
				ContactDetailsBean objContactDetail= new ContactDetailsBean();
				objContactDetail.set_name("No matching record found");
				adapterContactList.listContactsShow=new ArrayList<ContactDetailsBean>();
				adapterContactList.listContactsShow.add(objContactDetail);
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
			}
			InputMethodManager imm = (InputMethodManager)mActivityTabs.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchViewContacts.getWindowToken(), 0);
		}
		return false;
	}

	/**
	 * 
	 * @params fetch contacts from the phone
	 *
	 */
	class GetContacts extends AsyncTask<Void, Void, Void> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			if(GlobalCommonValues.listContacts!=null && GlobalCommonValues.listContacts.isEmpty()){
				dialogProgress=new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
				dialogProgress.show();
				listContactDetail=new ArrayList<ContactDetailsBean>();
			}
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			try {
				//				if(!tvFavoritesList.getText().toString().startsWith("Favorites")){
				listContactDetail = new ArrayList<ContactDetailsBean>();
				listContactDetail.addAll(GlobalCommonValues.listContacts);
				//				}
			} catch (Exception e) {
				e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			try {
				//fill list to display data of the fetched contacts
				if(listContactDetail!=null && !listContactDetail.isEmpty()){
					adapterContactList=new ContactListActivityAdapter(getActivity(),listContactDetail,null,false);
					lvContacts.setAdapter(adapterContactList);
					if(!tvFavoritesList.getText().toString().startsWith("Favorites"))
						lvContacts.setTextFilterEnabled(true);
					else if(tvFavoritesList.getText().toString().startsWith("Favorites"))
						lvContacts.setTextFilterEnabled(false);
				}
				else{
					listEmptyMessage=new ArrayList<String>();
					listEmptyMessage.add("No Contact Found");
					adapterContactList=new ContactListActivityAdapter(getActivity(),null,listEmptyMessage,false);
					lvContacts.setAdapter(adapterContactList);
				}
				if(dialogProgress!=null && dialogProgress.isShowing())
					dialogProgress.dismiss();
				if(isSearchResultFirstTime==false)
					isSearchResultFirstTime=true;
			} catch (Exception e) {
				e.getMessage();
				if(dialogProgress!=null)
					dialogProgress.dismiss();
			}
		}
	}

	/**
	 * Method to refresh contact list after we get5 a response after clicking the refresh button
	 * @param type
	 */
	public void refreshContactList(String type){
		if(dialogProgress!=null && dialogProgress.isShowing())
			dialogProgress.dismiss();
		int pos  = lvContacts.getFirstVisiblePosition();
		if(!GlobalCommonValues.listContacts.isEmpty()){
			listEmptyMessage=new ArrayList<String>();
			listContactDetail.clear();
			listContactDetail.addAll(GlobalCommonValues.listContacts);
			if(listContactDetail!=null && !listContactDetail.isEmpty()){
				if(!mSearchtext.trim().equals("")){
					
					adapterContactList=new ContactListActivityAdapter(getActivity(),listContactDetail,null,false);
					lvContacts.setAdapter(adapterContactList);
					adapterContactList.notifyDataSetChanged();
					adapterContactList.filterData(mSearchtext);
					adapterContactList.notifyDataSetChanged();
					/*ArrayList<ContactDetailsBean> mListDataToDisplay = new ArrayList<ContactDetailsBean>();
					if(adapterContactList.listFirstName!=null && adapterContactList.listFirstName.size()>0){
						mListDataToDisplay.addAll(adapterContactList.listFirstName);
					}
					if(adapterContactList.listLastName!=null && adapterContactList.listLastName.size()>0){
						mListDataToDisplay.addAll(adapterContactList.listLastName);
					}
					if(adapterContactList.listContainsName!=null && adapterContactList.listContainsName.size()>0){
						mListDataToDisplay.addAll(adapterContactList.listContainsName);
					}
					adapterContactList=new ContactListAdapter(getActivity(),mListDataToDisplay,null,false);
					lvContacts.setAdapter(adapterContactList);*/
				}else{
					adapterContactList=new ContactListActivityAdapter(getActivity(),listContactDetail,null,false);
					lvContacts.setAdapter(adapterContactList);
					adapterContactList.notifyDataSetChanged();
				}
				lvContacts.setTextFilterEnabled(true);
				if(pos>0)
					lvContacts.setSelection(pos);
				
			/*	adapterContactList=new ContactListActivityAdapter(getActivity(),listContactDetail,null,false);
				lvContacts.setAdapter(adapterContactList);
				adapterContactList.notifyDataSetChanged();
				lvContacts.setTextFilterEnabled(true);
				if(pos>0)
					lvContacts.setSelection(pos);*/
			}
		}else if(GlobalCommonValues.listContacts.isEmpty()){
			listEmptyMessage=new ArrayList<String>();
			listEmptyMessage.add("No Contact Found");
			adapterContactList=new ContactListActivityAdapter(getActivity(),null,listEmptyMessage,false);
			lvContacts.setAdapter(adapterContactList);
		}
	}
}
