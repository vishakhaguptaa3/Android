package com.tnc.fragments;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.TncUserNotifyAdapter;
import com.tnc.adapter.TncUserNotifyRegistrationAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.NotifyContactsBean;
import com.tnc.checkinternet.NetworkConnection;
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
import com.tnc.webresponse.NotifyFriendResponse;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display list of tnc users to be notified for the number change / availability on TnC App
 *  @author a3logics
 */
public class TnCUsers_NotifyFragmentRegistration extends BaseFragmentTabs implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener
{
    private TextView tvTitle,tvStep,tvFavoritesList;
    private Button btnBack,btnHome,btnSendContacts,btnCancel;
    private FrameLayout flBackArrow,flInformation;
    private SearchView searchViewContacts;
    private ListView lvContacts,lvAlphabets;
    private LinearLayout llSearchView;
    private String title;
    private GetContactDetails getFavoriteContacts=null;
    private TransparentProgressDialog dialogProgress=null;
    private ContactDetailsBean selectedContactBean=null;
    private Context mActivity;
    private TncUserNotifyRegistrationAdapter adapterContactList;
    private int adapterSelected_position;
    private ArrayList<BBContactsBean> listContacts = new ArrayList<BBContactsBean>();
    private ArrayList<String> listAlphabets=null;
    private AlphabetAdapter adapterAlphabet;
    private int from_user_id_delete=-1,to_user_id_delete=-1;
    private TransparentProgressDialog progress;
    private Gson gson;
    private ArrayList<ContactTilesBean> listTilesAdded;
    private SharedPreference saveState;
    private SQLiteDatabase db;
    private boolean isCheckBoxClickDisable=false;
    private CheckBox chkBoxSelectAll;
    private String message="";
    private Handler handler = new Handler();
    private LinearLayout llParent;
    private boolean isFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tncusersnotifylistfragment, container, false);
        idInitialization(view);
        return view;
    }

    public TnCUsers_NotifyFragmentRegistration newInstance(String message)
    {
        TnCUsers_NotifyFragmentRegistration frag = new TnCUsers_NotifyFragmentRegistration();
        this.message=message;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        lvContacts.setEnabled(false);
        lvContacts.setClickable(false);
        lvAlphabets.setEnabled(false);
        lvAlphabets.setClickable(false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
        progress=new TransparentProgressDialog(getActivity(), R.drawable.customspinner);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvStep=(TextView) view.findViewById(R.id.tvStep);
        llParent=(LinearLayout) view.findViewById(R.id.llParent);
        tvFavoritesList=(TextView) view.findViewById(R.id.tvFavoritesList);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        llSearchView=(LinearLayout) view.findViewById(R.id.llSearchView);
        llSearchView.setVisibility(View.GONE);
        searchViewContacts=(SearchView) view.findViewById(R.id.searchViewContacts);
        lvContacts=(ListView) view.findViewById(R.id.lvContacts);
        lvAlphabets=(ListView) view.findViewById(R.id.lvAlphabets);
        chkBoxSelectAll=(CheckBox) view.findViewById(R.id.chkBoxSelectAll);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        flInformation=(FrameLayout) view.findViewById(R.id.flInformationButton);
        flInformation.setVisibility(View.VISIBLE);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        btnHome.setVisibility(View.VISIBLE);
        btnSendContacts=(Button) view.findViewById(R.id.btnSendContacts);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        setTitleAndButtonText(isFirstTime);

       /* String mHeading = getResources().getString(R.string.txtCreate);

        mHeading = mHeading.substring(0,1).toUpperCase() + mHeading.substring(1,mHeading.length());

        btnSendContacts.setText(mHeading);*/

//        tvFavoritesList.setText(getResources().getString(R.string.txtNotifyOtherUsersTitle)); //(getResources().getString(R.string.app_name)+" Contacts");
        flBackArrow.setVisibility(View.GONE);
        tvStep.setVisibility(View.GONE);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
        SearchManager searchManager = (SearchManager) mActivityTabs.getSystemService(Context.SEARCH_SERVICE);
        searchViewContacts.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchViewContacts.setQueryHint("Search from "+getResources().getString(R.string.app_name)+" Users...");
        searchViewContacts.setOnQueryTextListener(this);
        searchViewContacts.setOnCloseListener(this);
        listContacts=new ArrayList<BBContactsBean>();
        searchViewContacts.setVisibility(View.GONE);
        lvAlphabets.setVisibility(View.GONE);

        //fetch Tnc Users From the Database
        listContacts=DBQuery.getAllBBContactsOrdered(mActivityTabs);

        if(listContacts.isEmpty())
        {
            BBContactsBean objContactBean=new BBContactsBean();
            objContactBean.setName("No Contact Found");
            listContacts.add(objContactBean);
            lvAlphabets.setClickable(false);
            searchViewContacts.setVisibility(View.GONE);
            btnSendContacts.setAlpha(0.5f);
            btnSendContacts.setEnabled(false);
            btnCancel.setAlpha(0.5f);
            btnCancel.setEnabled(false);
            chkBoxSelectAll.setClickable(false);
        }
        else
        {
            lvAlphabets.setClickable(true);
            searchViewContacts.setVisibility(View.GONE);
            btnSendContacts.setAlpha(1.0f);
            btnSendContacts.setEnabled(true);
            btnCancel.setAlpha(1.0f);
            btnCancel.setEnabled(true);
            chkBoxSelectAll.setClickable(true);
        }

        if(btnSendContacts.getText().toString().equalsIgnoreCase(getResources().getString(R.string.txtNotify))){
            setListAdapter(true);// send notify mode as true
        }else{
            setListAdapter(false);// send notify mode as false
        }

        llParent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        chkBoxSelectAll.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // To manage checkbox state to handle event only in case of checkbox click manuallty by the user
                isCheckBoxClickDisable=false;
                return false;
            }
        });

        chkBoxSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // To manage checkbox state to handle event only in case of checkbox click manuallty by the user
                if(!isCheckBoxClickDisable)
                {
                    if(adapterContactList!=null && !adapterContactList.isEmpty()){
                        adapterContactList.listTncContactsAdded = new ArrayList<BBContactsBean>();

                        // if selected then make checkbox state to true for all the checkboxes in a list
                        if(isChecked){
                            if((adapterContactList.listContacts!=null) && !(adapterContactList.listContacts.isEmpty())){
                                for (BBContactsBean mBBContactsBean : adapterContactList.listContacts){
                                    mBBContactsBean.setContactChecked(true);
                                }
                            }
                        }else if(!isChecked){
                            // call method to reset checkboxes
                            resetCheckBoxes();
                        }
                        adapterContactList.setAllSelected(isChecked);
                    }
                }
            }
        });

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

        btnSendContacts.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(adapterContactList.listTncContactsAdded.size()>0){

                    if(btnSendContacts.getText().toString().equalsIgnoreCase(getResources().getString(R.string.txtNotify))){
                        // Call Web Service to notify the contacts
                        checkInternetConnection();
                    }else{
                        // Add Selected contacts in the Tile Table
                        addContactTilesToDB();
                    }
                }else{
                    ImageRequestDialog dialogResult=new ImageRequestDialog();
                    dialogResult.setCancelable(false);

                    if(btnSendContacts.getText().toString().equalsIgnoreCase(getResources().getString(R.string.txtNotify))){
                        dialogResult.newInstance("",mActivityTabs,getResources().getString(R.string.txtChooseContactToNotifyMessage),"",null);
                    }else{
                        dialogResult.newInstance("",mActivityTabs,getResources().getString(R.string.txtChooseContactToCreateTileMessage),"",null);
                    }

                    if(mActivityTabs instanceof MainBaseActivity)
                    {
                        dialogResult.show(((MainBaseActivity)mActivityTabs).getSupportFragmentManager(), "test");
                    }
                    else if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
                    }
                }
            }
        });

        btnHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mActivityTabs instanceof MainBaseActivity)
                {
                    ((MainBaseActivity)getActivity()).startActivity(new Intent(getActivity(),HomeScreenActivity.class));
                    ((MainBaseActivity)getActivity()).finish();
                }
                else if(mActivityTabs instanceof HomeScreenActivity)
                {
                    ((HomeScreenActivity)getActivity()).startActivity(new Intent(getActivity(),HomeScreenActivity.class));
                    ((HomeScreenActivity)getActivity()).finish();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // reset all values on going to the new screen
                if(adapterContactList!=null && !adapterContactList.isEmpty()){
                    adapterContactList.listTncContactsAdded = new ArrayList<BBContactsBean>();
                }

                // call method to reset checkboxes
                resetCheckBoxes();

                String mTitleString = tvFavoritesList.getText().toString();

                if(mTitleString.toLowerCase().equalsIgnoreCase(getResources().getString(R.string.txtNotifyOtherUsersTitle).toLowerCase())){
                    // if notify contacts screen is visible then navigate to create chat button contacts otherwise go to home screen
                    iNotifyCreateContacts.yes();
                }else if(mTitleString.toLowerCase().equalsIgnoreCase(getResources().getString(R.string.txtCreateCall).toLowerCase())){
                    // go to home screen
                    iNotifyCreateContacts.no();
                }
            }
        });

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    /**
     * Method to reset checkboxes
     */
    private void resetCheckBoxes(){
        // if unselected then make checkbox state to false for all the checkboxes in a list
        if((adapterContactList.listContacts!=null) && !(adapterContactList.listContacts.isEmpty())){
            for (BBContactsBean mBBContactsBean : adapterContactList.listContacts){
                mBBContactsBean.setContactChecked(false);
            }
        }
    }

    /**
     * Method to add contacts tiles to DB
     */
    private void addContactTilesToDB(){

        ContactTilesBean mContactTileBean;

        if(adapterContactList.listTncContactsAdded!=null && adapterContactList.listTncContactsAdded.size() > 0){

            for(int i = 0; i< adapterContactList.listTncContactsAdded.size(); i++){

                mContactTileBean = new ContactTilesBean();

                ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();

                try{
                    String mTilePosition = String.valueOf(DBQuery.getMaximumTilePosition(getActivity()));
                    if(mTilePosition.equalsIgnoreCase("-1"))
                        mContactTileBean.setTilePosition("0");
                    else{
                        //								int maxTilePosition = DBQuery.getMaximumTilePosition(mActivity);
                        mContactTileBean.setTilePosition(String.valueOf(Integer.parseInt(mTilePosition)+1));
                    }

                    mContactTileBean.setName(adapterContactList.listTncContactsAdded.get(i).getName());
                    mContactTileBean.setPrefix("");
                    mContactTileBean.setCountryCode(adapterContactList.listTncContactsAdded.get(i).getCountryCode());
                    mContactTileBean.setPhoneNumber(adapterContactList.listTncContactsAdded.get(i).getPhoneNumber());
                    mContactTileBean.setBBID(adapterContactList.listTncContactsAdded.get(i).getBBID());

                    if(adapterContactList.listTncContactsAdded.get(i).getmImageArray()!=null &&
                            adapterContactList.listTncContactsAdded.get(i).getmImageArray().length > 0){
                        mContactTileBean.setImage(adapterContactList.listTncContactsAdded.get(i).getmImageArray());
                    }

                    mContactTileBean.setIsEmergency(false);
                    mContactTileBean.setIsImagePending(1);
                    mContactTileBean.setIsTncUser(true);
                    mContactTileBean.setIsMobile(true);
                    mContactTileBean.setImageLocked(false);
                    try{
                        mContactTileBean.setButtonType(adapterContactList.listTncContactsAdded.get(i).getCategory());
                    }catch(Exception e){
                        e.getMessage();
                    }

                    listContactTiles.add(mContactTileBean);

                    // insert data in tile table
                    if(listContactTiles!=null && !listContactTiles.isEmpty()){
                        DBQuery.insertTile(getActivity(),listContactTiles,false);
                    }
                    // go to home screen after creating contacts
                    gotoHomeScreen();

                }catch(Exception e){
                    e.getMessage();
                }
            }
        }
    }

    /**
     * Method to go to home screen
     */
    private void gotoHomeScreen(){
        Intent intent = new Intent(mActivityTabs,HomeScreenActivity.class);
        intent.putExtra(GlobalCommonValues.IS_CREATE_CHAT_BUTTON, true);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    /**
     * Method to set button text
     */
    private void setTitleAndButtonText(boolean isFirstTimeDisplay){
        if(isFirstTimeDisplay){
            isFirstTime = false;
            String mHeading = getResources().getString(R.string.txtNotify);

            btnSendContacts.setText(mHeading);
            tvFavoritesList.setText(getResources().getString(R.string.txtNotifyOtherUsersTitle));
        }else{
            String mHeading = getResources().getString(R.string.txtCreate);

            mHeading = mHeading.substring(0,1).toUpperCase() + mHeading.substring(1,mHeading.length());

            btnSendContacts.setText(mHeading);
            tvFavoritesList.setText(getResources().getString(R.string.txtCreateCall));

            // reset all values on going to the new screen
            if(adapterContactList!=null && !adapterContactList.isEmpty()){
                if(adapterContactList.listTncContactsAdded!=null &&
                        adapterContactList.listTncContactsAdded.size() > 0){
                    adapterContactList.listTncContactsAdded.clear();
                }
            }

            // call method to reset checkboxes
            resetCheckBoxes();
        }
    }

    /**
     * Method to set list items to be displayed
     */
    private void setListAdapter(boolean isNotifyMode)
    {
        adapterContactList=new TncUserNotifyRegistrationAdapter(mActivityTabs, listContacts,iNotifyUncheckBox,
                isNotifyMode);
        adapterContactList.notifyDataSetChanged();
        lvContacts.setAdapter(adapterContactList);
    }


    /**
     * interface to handle call to select/unselect of contacts to be notified
     */

    INotifyGalleryDialog iNotifyUncheckBox=new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // In case unchecking the box when even a single item from the list is deselected
            isCheckBoxClickDisable=true;
            chkBoxSelectAll.setChecked(false);
        }

        @Override
        public void no() {
        }
    };

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
            if(btnSendContacts.getText().toString().equalsIgnoreCase(getResources().getString(R.string.txtNotify))){
                adapterContactList=new TncUserNotifyRegistrationAdapter(mActivityTabs, listContacts,iNotifyUncheckBox,true);
            }else{
                adapterContactList=new TncUserNotifyRegistrationAdapter(mActivityTabs, listContacts,iNotifyUncheckBox,false);
            }
            lvContacts.setAdapter(adapterContactList);
        }
        return false;
    }

    /**
     * @param :text typed in the searchview as an input
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if(adapterContactList!=null){
            adapterContactList.filterData(newText);
            if(adapterContactList.listContacts!=null && !adapterContactList.listContacts.isEmpty())
            {
                try{
                    if(adapterContactList.listContacts.size()<=0){
                        BBContactsBean objContactDetail= new BBContactsBean();
                        objContactDetail.setName("No matching record found");
                        adapterContactList.listContacts.add(objContactDetail);
                    }
                    adapterContactList.notifyDataSetChanged();
                    lvContacts.setAdapter(adapterContactList);
                }
                catch (Exception e){
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

    /**
     * check internet availability
     */
    private void checkInternetConnection()
    {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs))
        {
            int size=adapterContactList.listTncContactsAdded.size();
            StringBuffer ids=new StringBuffer();
            for(int i=0;i<size;i++)
            {
                ids=ids.append(adapterContactList.listTncContactsAdded.get(i).getBBID());
                if(i<size-1)
                    ids=ids.append(",");
            }
            String tncids=String.valueOf(ids);
            // type - 1 for new joined chatstasy user and 2 for changed his number from
            NotifyContactsBean objNotifyContacts=new NotifyContactsBean("1",tncids);
            sendNotificationToContacts(objNotifyContacts);
        }
        else
        {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    // Send notification to contacts
    private void sendNotificationToContacts(NotifyContactsBean objNotifyContacts)
    {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(objNotifyContacts);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(
                    mActivity,
                    GlobalCommonValues.NOTIFYMYFRIEND, stringEntity,
                    notifyContactsResponseHandler,
                    saveState.getPrivateKey(mActivityTabs),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // async task to handle call to the Send notification to contacts
    AsyncHttpResponseHandler notifyContactsResponseHandler = new JsonHttpResponseHandler() {
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
                Logs.writeLog("notifyContactsResponseHandler", "OnSuccess",response.toString());/*saveState.setChanged(mActivityTabs, false);*/
                if(response!=null)
                {
                    getResponseNotifyContacts(response.toString());
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
                Logs.writeLog("notifyContactsResponseHandler", "OnFailure",response);
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
    private void getResponseNotifyContacts(String response)
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
                NotifyFriendResponse get_Response = gson.fromJson(response2, NotifyFriendResponse.class);
                if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
                {
                    ImageRequestDialog dialogResult=new ImageRequestDialog();
                    dialogResult.setCancelable(false);
                    String msg="";
                    msg="Contacts were notified about your availability on \n"+getResources().getString(R.string.app_name);
                    if(mActivityTabs instanceof MainBaseActivity)
                    {
                        dialogResult.newInstance("",((MainBaseActivity)mActivityTabs),msg,"",null,iNotifyBackupSuccessful);
                    }
                    else if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        dialogResult.newInstance("",((HomeScreenActivity)mActivityTabs),msg,"",null,iNotifyBackupSuccessful);
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


    /**
     * interface to handle call to navigate to the home screen
     */

    INotifyGalleryDialog iNotifyBackupSuccessful=new INotifyGalleryDialog() {
        @Override
        public void yes() {
        }

        @Override
        public void no() {
            // Ask user to create tiles of these contacts
            MessageDeleteConfirmation dialogResult=new MessageDeleteConfirmation();
            dialogResult.setCancelable(false);
            String msg="";
            msg = getResources().getString(R.string.txtCreateContactTilesConfirmation);
            if(mActivityTabs instanceof MainBaseActivity)
            {
                dialogResult.newInstance("",((MainBaseActivity)mActivityTabs),msg,"",iNotifyCreateContacts);
            }
            else if(mActivityTabs instanceof HomeScreenActivity)
            {
                dialogResult.newInstance("",((HomeScreenActivity)mActivityTabs),msg,"",iNotifyCreateContacts);
            }
            dialogResult.show(getChildFragmentManager(), "test");
        }
    };

    INotifyGalleryDialog iNotifyCreateContacts = new INotifyGalleryDialog() {

        public void yes() {
            //In case user want to create tiles of the contacts, then he will be displayed with the list of Chatstasy contacts to create tiles of them.

            // UPDATE THE BUTTON TEXT
            setTitleAndButtonText(false);

            // Refresh the list of chatstasy users to select the users to create the tile
            adapterContactList.listTncContactsAdded = new ArrayList<BBContactsBean>();
            setListAdapter(false); // send notify mode as false
        }

        @Override
        public void no() {
            //In case user don't want to create tile , then it will be navigated to the home screen.
            gotoHomeScreen();
        }
    };
}
