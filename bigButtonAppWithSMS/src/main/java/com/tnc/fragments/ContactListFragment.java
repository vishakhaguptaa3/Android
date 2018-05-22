package com.tnc.fragments;

import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.ContactListAdapter;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.contact.GetContactDetails;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.GetContactService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
 * class to display contact list from the phone 
 *  @author a3logics
 */

public class ContactListFragment extends BaseFragment implements
        OnClickListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener
{
    private TextView tvTitle,tvStep,tvFavoritesList,textViewSearcBar;
    private Button btnBack,btnHome;//btnHome
    private FrameLayout flBackArrow,flInformationButton;
    private SearchView searchViewContacts;
    private ListView lvContacts,lvAlphabets;
    private ContactListAdapter adapterContactList;
    private ArrayList<ContactDetailsBean> listContactDetail;
    //	private ArrayList<ContactDetailsBean> listContactDetailCurrentDisplay = new ArrayList<ContactDetailsBean>();
    private LinearLayout llSearchView;
    private String title;
    private GetContactDetails getFavoriteContacts=null;
    public TransparentProgressDialog dialogProgress=null;
    private ContentResolver contentResolver=null;
    private ContactDetailsBean selectedContactBean=null;
    private Context mActivity;
    //	int SERVICE_TIME_OUT=10000;
    private int adapterSelected_position;
    private ArrayList<String> listEmptyMessage;
    private boolean isSearchResultFirstTime=false;
    private ArrayList<String> listAlphabets=null;
    private AlphabetAdapter adapterAlphabet;
    private int mFirstVisiblePositionList = -1,mLastVisiblePositionList = -1;
    private Handler handler = new Handler();
    private String mSearchtext = "";

    public ContactListFragment newInstance(String title,Context mActivity)
    {
        this.title=title;
        ContactListFragment frag = new ContactListFragment();
        this.mActivity=mActivity;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof MainBaseActivity)
            ((MainBaseActivity) activity).setINotifyContactListing(iNotifyContactListing);
        else if(activity instanceof HomeScreenActivity)
            ((HomeScreenActivity) activity).setINotifyContactListing(iNotifyContactListing);
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
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contactlistfragment, container, false);
        try {
            idInitialization(view);
        } catch (Exception e) {
            e.getMessage();
        }
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
        try {
            if(mActivity instanceof MainBaseActivity)
            {
                if(((MainBaseActivity)mActivity)!=null)
                {
                    if (NetworkConnection.isNetworkAvailable(mActivity))
                    {
                        if(saveState.getGCMRegistrationId(getActivity()).equals(""))
                        {
                            ((MainBaseActivity)mActivity).setGCMRegID();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        searchViewContacts.setQuery("",false);

        if(!tvFavoritesList.getText().toString().startsWith("Favorites"))
        {
            llSearchView.setVisibility(View.VISIBLE);
            adapterContactList=new ContactListAdapter(mActivity,GlobalCommonValues.listContacts,null,false);
            lvContacts.setAdapter(adapterContactList);
            adapterContactList.notifyDataSetChanged();
            lvContacts.setTextFilterEnabled(true);
        }
        else if(tvFavoritesList.getText().toString().startsWith("Favorites"))
        {
            llSearchView.setVisibility(View.GONE);
            adapterContactList=new ContactListAdapter(mActivity,listContactDetail,null,false);
            lvContacts.setAdapter(adapterContactList);
            adapterContactList.notifyDataSetChanged();
            lvContacts.setTextFilterEnabled(true);
        }
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
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        if(saveState.isRegistered(getActivity())){
            flInformationButton.setVisibility(View.VISIBLE);
            btnHome.setVisibility(View.VISIBLE);
            btnHome.setBackgroundResource(R.drawable.refresh);
            btnHome.setEnabled(true);
            btnHome.setClickable(true);
        }
        if(!saveState.isRegistered(getActivity())){
            btnHome.setVisibility(View.GONE);
        }

        tvFavoritesList.setText(title);
        tvFavoritesList.setAllCaps(true);
        listContactDetail=new ArrayList<ContactDetailsBean>();

        new GetContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        flBackArrow.setVisibility(View.VISIBLE);
        tvStep.setText("STEP 2");
        tvStep.setVisibility(View.GONE);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/StencilStd.otf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
        try {
            SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);
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
        adapterAlphabet=new AlphabetAdapter(mActivity,listAlphabets);
        adapterAlphabet.notifyDataSetChanged();
        lvAlphabets.setAdapter(adapterAlphabet);
        int searchPlateId = searchViewContacts.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchViewContacts.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.WHITE);
        }
        try{
            int id = searchViewContacts.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            textViewSearcBar = (TextView) searchViewContacts.findViewById(id);
            textViewSearcBar.setTextColor(Color.BLACK);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        btnHome.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(listContactDetail!=null && listContactDetail.size()>0 && saveState.isRegistered(mActivity)){

                    mFirstVisiblePositionList = lvContacts.getFirstVisiblePosition();
                    mLastVisiblePositionList = lvContacts.getLastVisiblePosition();

                    for(int i = mFirstVisiblePositionList; i<=mLastVisiblePositionList;i++){
                        GlobalCommonValues.listContactsSendToServer.add(listContactDetail.get(i));
                    }
					
					/*listContactDetailCurrentDisplay = new ArrayList<ContactDetailsBean>();
					
					listContactDetailCurrentDisplay.addAll(GlobalCommonValues.listContactsSendToServer);*/

                    if(dialogProgress == null){
                        dialogProgress=new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
                        dialogProgress.show();
                    }else if(dialogProgress!=null && !dialogProgress.isShowing()){
                        dialogProgress.show();
                    }
                    if(mActivity instanceof MainBaseActivity){
                        //Refresh List Here
                        if(saveState.isRegistered(getActivity())){
                            saveState.setRefrehContactList(getActivity(),true);
                            saveState.setIS_FROM_HOME(getActivity(),false);

                            if(mFirstVisiblePositionList >-1 && mLastVisiblePositionList>-1){
                                sendContactsToServer();
                                mFirstVisiblePositionList = -1;
                                mLastVisiblePositionList = -1;
                            }

							/*handler.postDelayed(new Runnable() {
								@Override
								public void run() {

								}
							}, 100);*/
                        }
                    }
                    else if(mActivity instanceof HomeScreenActivity)
                    {
                        //Refresh List Here
                        if(saveState.isRegistered(getActivity())){
                            saveState.setRefrehContactList(getActivity(),true);
                            saveState.setIS_FROM_HOME(getActivity(),false);

                            if(mFirstVisiblePositionList >-1 && mLastVisiblePositionList>-1){
                                sendContactsToServer();
                                mFirstVisiblePositionList = -1;
                                mLastVisiblePositionList = -1;
                            }

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
                //				int selectedPosition=0;
                for(int i=0;i<GlobalCommonValues.listContacts.size();i++)
                {
                    if(GlobalCommonValues.listContacts.get(i).get_name().toLowerCase().startsWith(listAlphabets.get(position).toLowerCase()))
                    {
                        //						selectedPosition=i;
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
                    adapterSelected_position=position;
                    adapterContactList.setRowColor(adapterSelected_position,true);
                    selectedContactBean=new ContactDetailsBean();
                    if (adapterContactList.listContactsShow.size() > 0)//(listContactDetail.size() > 0)
                    {
                        if(GlobalCommonValues._Contacimage!=null)
                        {
                            GlobalCommonValues._Contacimage=null;
                        }

                        ImageView imviewContact=(ImageView)arg1.findViewById(R.id.llContactNameParent).findViewById(R.id.llImageHolderparent).findViewById(R.id.llImageHolderparent).findViewById(R.id.llContactNameHolder).findViewById(R.id.imViewContactImage);
                        if(((BitmapDrawable)(imviewContact).getDrawable())!=null && ((BitmapDrawable)(imviewContact).getDrawable()).getBitmap()!=null)
                            GlobalCommonValues._Contacimage=((BitmapDrawable)(imviewContact).getDrawable()).getBitmap();
                        selectedContactBean.set_id(adapterContactList.listContactsShow.get(position).get_id());//(listContactDetail.get(position).get_id());
                        selectedContactBean.set_name(adapterContactList.listContactsShow.get(position).get_name());//(listContactDetail.get(position).get_name());
                        selectedContactBean.set_phone(adapterContactList.listContactsShow.get(position).get_phone());//(listContactDetail.get(position).get_phone());
                        selectedContactBean.setsType(adapterContactList.listContactsShow.get(position).getsType());

                        if(adapterContactList.listContactsShow.get(position).get_emailid()!=null)//(listContactDetail.get(position).get_emailid()!=null)
                            selectedContactBean.set_emailid(adapterContactList.listContactsShow.get(position).get_emailid());
                    }
                    MainBaseActivity.selectedConatctId=-1;
                    ContactDetailsFragment detailFragment=new ContactDetailsFragment(selectedContactBean);
                    if(mActivity instanceof MainBaseActivity)
                        ((MainBaseActivity)mActivity).setFragment(detailFragment);
                    else if(mActivity instanceof HomeScreenActivity)
                        ((HomeScreenActivity)mActivity).setFragment(detailFragment);
                    searchViewContacts.setQuery("",false);
                    adapterContactList.notifyDataSetChanged();
                }
            }
        });
    }

    public void sendContactsToServer(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                new StartServiceClass().execute();
            }
        },100);
    }

    class StartServiceClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent mainIntent = new Intent(getActivity(),GetContactService.class);
            getActivity().startService(mainIntent);
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.btnBack)
        {
            if(mActivity instanceof MainBaseActivity)
                ((MainBaseActivity)mActivity).fragmentManager.popBackStack();
            else if(mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
        }
    }

    @Override
    public boolean onClose()
    {
        adapterContactList.filterData("");
        searchViewContacts.setQuery("",false);
        if(adapterContactList!=null && adapterContactList.listMenu!=null && !adapterContactList.listMenu.isEmpty() && adapterContactList.listMenu.contains("No Contact Found"))
        {
            listEmptyMessage=new ArrayList<String>();
            listEmptyMessage.add("No Contact Found");
            adapterContactList=new ContactListAdapter(mActivity,null,listEmptyMessage,false);
            lvContacts.setAdapter(adapterContactList);
        }
        if(adapterContactList!=null && adapterContactList.listMenu!=null && !adapterContactList.listMenu.isEmpty() && adapterContactList.listMenu.contains("No matching record found"))
        {
            listEmptyMessage=new ArrayList<String>();
            listEmptyMessage.add("No matching record found");
            adapterContactList=new ContactListAdapter(mActivity,null,listEmptyMessage,false);
            lvContacts.setAdapter(adapterContactList);
        }
        return false;
    }

    /**
     * @param :text typed in the searchview as an input
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
     * @param :text submitted to the searchview
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
            InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchViewContacts.getWindowToken(), 0);
        }
        return false;
    }

    /**
     *
     * @params fetch contacts from the phone
     *
     */
    class GetContacts extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //if(GlobalCommonValues.listContacts!=null && GlobalCommonValues.listContacts.isEmpty()){
            if(tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                dialogProgress=new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
                dialogProgress.show();
                listContactDetail=new ArrayList<ContactDetailsBean>();
            }
        }

        @Override
        protected Void doInBackground(String... params){
            try {
                if(!tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                    listContactDetail.addAll(GlobalCommonValues.listContacts);
                }
                else if(tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                    GetContactDetails getFavoriteContacts=new GetContactDetails("favorites",mActivity);
                    contentResolver=getActivity().getContentResolver();
                    if(getFavoriteContacts.fetchContacts(contentResolver,getActivity())!=null)
                        listContactDetail=getFavoriteContacts.fetchContacts(contentResolver,getActivity());
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            try {
                if(!tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                    if(listContactDetail!=null && !listContactDetail.isEmpty()){
                        adapterContactList=new ContactListAdapter(mActivity,listContactDetail,null,false);
                        lvContacts.setAdapter(adapterContactList);
                        lvContacts.setTextFilterEnabled(true);

                    }
                    else{
                        listEmptyMessage=new ArrayList<String>();
                        listEmptyMessage.add("No Contact Found");
                        adapterContactList=new ContactListAdapter(mActivity,null,listEmptyMessage,false);
                        lvContacts.setAdapter(adapterContactList);
                        mFirstVisiblePositionList = -1;
                        mLastVisiblePositionList = -1;
                    }
                    if(dialogProgress!=null && dialogProgress.isShowing())
                        dialogProgress.dismiss();
                    if(isSearchResultFirstTime==false)
                        isSearchResultFirstTime=true;
                }
                else if(tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                    if(listContactDetail!=null && !listContactDetail.isEmpty()){
                        adapterContactList=new ContactListAdapter(mActivity,listContactDetail,null,false);
                        lvContacts.setAdapter(adapterContactList);
                        lvContacts.setTextFilterEnabled(false);

                    }
                    else{
                        listEmptyMessage=new ArrayList<String>();
                        listEmptyMessage.add("No Contact Found");
                        adapterContactList=new ContactListAdapter(mActivity,null,listEmptyMessage,false);
                        lvContacts.setAdapter(adapterContactList);
                        mFirstVisiblePositionList = -1;
                        mLastVisiblePositionList = -1;
                    }
                    if(dialogProgress!=null && dialogProgress.isShowing())
                        dialogProgress.dismiss();
                    if(isSearchResultFirstTime==false)
                        isSearchResultFirstTime=true;
                }
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
        if(!tvFavoritesList.getText().toString().startsWith("FAVORITES") ||
                tvFavoritesList.getText().toString().startsWith("FAVORITES")){
            int pos  = lvContacts.getFirstVisiblePosition();
            if(tvFavoritesList.getText().toString().startsWith("FAVORITES")){
                new GetContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            }else{
                if(!GlobalCommonValues.listContacts.isEmpty()){
                    listEmptyMessage=new ArrayList<String>();
                    listContactDetail.clear();
                    listContactDetail.addAll(GlobalCommonValues.listContacts);
                    if(listContactDetail!=null && !listContactDetail.isEmpty()){
                        //						adapterContactList=new ContactListAdapter(getActivity(),listContactDetail,null,false);
                        //						lvContacts.setAdapter(adapterContactList);
                        if(!mSearchtext.trim().equals("")){

                            adapterContactList=new ContactListAdapter(getActivity(),listContactDetail,null,false);
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
                            adapterContactList=new ContactListAdapter(getActivity(),listContactDetail,null,false);
                            lvContacts.setAdapter(adapterContactList);
                            adapterContactList.notifyDataSetChanged();
                        }
                        //						adapterContactList.notifyDataSetChanged();
                        lvContacts.setTextFilterEnabled(true);
                        if(pos>0)
                            lvContacts.setSelection(pos);
                    }
                }else if(GlobalCommonValues.listContacts.isEmpty()){
                    listEmptyMessage=new ArrayList<String>();
                    listEmptyMessage.add("No Contact Found");
                    adapterContactList=new ContactListAdapter(getActivity(),null,listEmptyMessage,false);
                    lvContacts.setAdapter(adapterContactList);
                }
            }
        }
    }
}
