package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.TileContactsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactShareBean;
import com.tnc.bean.ContactShareStatusUpdate;
import com.tnc.bean.ContactTilesBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBConstant;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MergeISDNumberConfirmationDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.SharedContactSavedSuccessful;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ImageResponse;
import com.tnc.webresponse.NotificationResponseContactShareStatus;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cz.msebera.android.httpclient.Header;

import static com.tnc.database.DBConstant.TILE_COLUMN_IMAGE_LOCK;
//import static com.tnc.database.DBConstant.TILE_COLUMN_IMAGE_LOCK;

/**
 * class to display the list Tiles on the home screen 
 *  @author a3logics
 */
public class TileContacts extends BaseFragmentTabs{

	/*
	 *In case of select contacts to share & accept shared contacts
	 * */

    private TextView tvTitle,tvStep,tvFavoritesList;
    private Button btnBack,btnSendContacts,btnHome;
    private FrameLayout flBackArrow,flInformation;
    private ListView lvButtonContacts;
    private int adapterSelected_position;
    private TileContactsAdapter adapterTileContacts;
    private ArrayList<ContactTilesBean> listTiles;
    private SharedPreference saveState;
    private ArrayList<ContactTilesBean> listTilesAdded;
    private SQLiteDatabase db;
    private LinearLayout llSendButton;
    private Gson gson;
    private TransparentProgressDialog progress;
    private String screenType="",status="",notification_id="";
    private LinearLayout llSelectAllCheck;
    private CheckBox chkBoxSelectAll;
    private boolean isCheckBoxClickDisable=false;
    private ArrayList<ContactTilesBean>  listSharedContactTiles;
    private boolean isDuplicateChecked=false;
    private LinearLayout llParent,llTopHeader;
    private String filePath="";
    private String fileName="";
    private ArrayList<String> listTilesId = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tilecontactfragment_sharing, container, false);
        idInitialization(view);
        return view;
    }

    @Override
    public void onDestroy() {
        //system.out.println();
        listTilesAdded=null;
        super.onDestroy();

    }

    public TileContacts newInstance(String screenType,String status,
                                    String notification_id,ArrayList<ContactTilesBean>  listSharedContactTiles) {
        TileContacts frag = new TileContacts();
        this.screenType=screenType;
        this.status=status;
        this.notification_id=notification_id;;
        this.listSharedContactTiles=listSharedContactTiles;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    //method for initialization of views/widgets
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvFavoritesList=(TextView) view.findViewById(R.id.tvFavoritesList);
        tvStep=(TextView) view.findViewById(R.id.tvStep);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        flInformation=(FrameLayout) view.findViewById(R.id.flInformationButton);
        flInformation.setVisibility(View.VISIBLE);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        llParent=(LinearLayout) view.findViewById(R.id.llParent);
        llTopHeader=(LinearLayout) view.findViewById(R.id.llTopHeader);
        btnHome.setVisibility(View.VISIBLE);
        btnSendContacts=(Button) view.findViewById(R.id.btnSendContacts);
        lvButtonContacts=(ListView) view.findViewById(R.id.lvButtonContacts);
        llSendButton=(LinearLayout) view.findViewById(R.id.llSendButton);
        llSelectAllCheck=(LinearLayout) view.findViewById(R.id.llSelectAllCheck);
        chkBoxSelectAll=(CheckBox) view.findViewById(R.id.chkBoxSelectAll);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
        tvStep.setText("CONTACT SHARING");
        tvStep.setTextColor(Color.parseColor("#1a649f"));

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        llParent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        llTopHeader.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        //Check if the view is to select the contacts to be shared or the
        // view is to display contacts shared by a person being fetched from tiles db.
        if(screenType.equalsIgnoreCase("ShareContactsSelection"))
        {
            btnSendContacts.setText("Next");
            llSelectAllCheck.setVisibility(View.VISIBLE);

            //For My Button Button
            ContactTilesBean objContactTileMyButton =new ContactTilesBean();
            objContactTileMyButton.setName(Uri.decode(saveState.getUserName(mActivityTabs)));
            objContactTileMyButton.setCountryCode(saveState.getCountryCode(getActivity()));
            objContactTileMyButton.setPhoneNumber(saveState.getUserPhoneNumber(getActivity()));
            objContactTileMyButton.setBBID(Integer.parseInt(saveState.getBBID(mActivityTabs)));

            objContactTileMyButton.setIsTncUser(true);
            filePath=String.valueOf(Environment.getExternalStorageDirectory()+File.separator+"TNC/images/");
            fileName="userimage.jpg";
            Bitmap _bitmap = null;
            if(GlobalConfig_Methods.getBitmapFromFile(filePath,fileName)!=null){
                _bitmap=GlobalConfig_Methods.getBitmapFromFile(filePath,fileName);
            }else{
                _bitmap=((BitmapDrawable)(getActivity().getResources().getDrawable(R.drawable.no_image))).getBitmap();
            }
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            _bitmap.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, blob);
            byte[] arrayImage = blob.toByteArray();
            objContactTileMyButton.setImage(arrayImage);

            listTiles=new ArrayList<ContactTilesBean>();
            listTiles.add(objContactTileMyButton);
            listTiles=DBQuery.getAllTiles((HomeScreenActivity)mActivityTabs);
            listTiles.add(0,objContactTileMyButton);
            isDuplicateChecked=false;
            tvFavoritesList.setText("Select Chat Button(s) to Share");
        }
        else if(screenType.equalsIgnoreCase("SharedContactsMerged"))
        {
            btnSendContacts.setText("Add");
            llSelectAllCheck.setVisibility(View.VISIBLE);
            listTiles=new ArrayList<ContactTilesBean>();
            listTiles=listSharedContactTiles;
            isDuplicateChecked=true;
            tvFavoritesList.setVisibility(View.VISIBLE);
            tvFavoritesList.setText("Select contacts to import");
        }

        //Set Adapter For The List
        adapterTileContacts=new TileContactsAdapter(mActivityTabs,listTiles,listTilesId,true,iNotifyUncheckBox,isDuplicateChecked);
        adapterTileContacts.notifyDataSetChanged();
        lvButtonContacts.setAdapter(adapterTileContacts);

        chkBoxSelectAll.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isCheckBoxClickDisable=false;
                return false;
            }
        });

        chkBoxSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isCheckBoxClickDisable)
                {
                    if(adapterTileContacts!=null && !adapterTileContacts.isEmpty()){

                        // if selected then make checkbox state to true for all the checkboxes in a list
                        if(isChecked){
                            if((adapterTileContacts.listTiles!=null) && !(adapterTileContacts.listTiles.isEmpty())){
                                for (ContactTilesBean mBBContactsBean : adapterTileContacts.listTiles){
                                    mBBContactsBean.setContactChecked(true);
                                }
                            }
                        }else if(!isChecked){
                            // call method to reset checkboxes
                            resetCheckBoxes();
                        }
                        adapterTileContacts.setAllSelected(isChecked);
                    }
                }
            }
        });

        btnSendContacts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(screenType.equalsIgnoreCase("ShareContactsSelection"))
                {
                    // IN CASE OF SENDING CONTACTS
                    if(adapterTileContacts.listTilesAdded.size()>0){
                        listTilesAdded=new ArrayList<ContactTilesBean>();
                        if(DBQuery.getAllBBContacts(mActivityTabs).size()>0){
                            //Creates the database to be sent to the server
                            iNotifyConfirmation.yes();
                        }else{
                            ImageRequestDialog dialog = new ImageRequestDialog();
                            dialog.newInstance("",mActivityTabs,"No "+getResources().getString(R.string.app_name)+" user in your contact list to share","",null,null);
                            dialog.setCancelable(false);
                            dialog.show(getChildFragmentManager(), "test");
                        }
                    }
                    else{
                        ImageRequestDialog dialogResult=new ImageRequestDialog();
                        dialogResult.setCancelable(false);
                        dialogResult.newInstance("",mActivityTabs,"Please select at least one contact to share","",null);
                        dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
                    }
                }
                else if(screenType.equalsIgnoreCase("SharedContactsMerged"))
                {
                    // IN CASE OF IMPORTING CONTACTS
                    if(adapterTileContacts.listTilesAdded.size()>0)
                    {
                        MessageDeleteConfirmation dialog=new MessageDeleteConfirmation();
                        dialog.setCancelable(false);
                        dialog.newInstance("", mActivityTabs, "Are you sure you want to add these contacts?","",iNotifyConfirmation);
                        dialog.show(getChildFragmentManager(), "test");
                    }
                    else{
                        ImageRequestDialog dialogResult=new ImageRequestDialog();
                        dialogResult.setCancelable(false);
                        dialogResult.newInstance("",mActivityTabs,"Please select at least one contact to add","",null);
                        dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
                    }
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
            }
        });

        llSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((HomeScreenActivity)getActivity()).startActivity(new Intent(getActivity(),HomeScreenActivity.class));
                ((HomeScreenActivity)getActivity()).finish();
            }
        });
    }

    /**
     * Method to reset checkboxes
     */
    private void resetCheckBoxes(){
        // if unselected then make checkbox state to false for all the checkboxes in a list
        if((adapterTileContacts.listTiles!=null) && !(adapterTileContacts.listTiles.isEmpty())){
            for (ContactTilesBean mBBContactsBean : adapterTileContacts.listTilesAdded){
                mBBContactsBean.setContactChecked(false);
            }
        }
    }

    /**
     *
     * @param :ArrayList<ContactTilesBean>
     * @param
     * :create database from the list of contacts
     */
    private void createSendDatabase(ArrayList<ContactTilesBean> listTilesAdded){

        ArrayList<ContactTilesBean> listTilesAddedTemp=new ArrayList<ContactTilesBean>();
        db=getActivity().openOrCreateDatabase("tiles_test9_database.sqlite", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS Tiles");
        db.execSQL("CREATE TABLE Tiles(Name VARCHAR,PhoneNumber VARCHAR,Image BLOB,IsEmergency BOOL,TilePosition INTEGER DEFAULT (null),IsImagePending INTEGER DEFAULT (null),Prefix  VARCHAR DEFAULT (null),CountryCode  VARCHAR DEFAULT (null),IsTncUser BOOL, ButtonType VARCHAR,BBID INTEGER,IsMobile BOOL," + TILE_COLUMN_IMAGE_LOCK + " BOOL);");
        ContentValues newValues=new ContentValues();
        for(int i=0;i<listTilesAdded.size();i++)
        {
			/*db.execSQL("INSERT INTO Tiles VALUES('"+listTilesAdded.get(i).getName()+"','"+listTilesAdded.get(i).getPhoneNumber()+
					"','"+listTilesAdded.get(i).getImage()+"','"+false+"','"+-1+"','"+0+"','"+listTilesAdded.get(i).getPrefix()+"');");*/
            newValues.put("Name", listTilesAdded.get(i).getName());
            newValues.put("PhoneNumber", listTilesAdded.get(i).getPhoneNumber());
            newValues.put("Image", listTilesAdded.get(i).getImage());
            newValues.put("IsEmergency", false);
            newValues.put("TilePosition", -1);
            newValues.put("IsImagePending", 0);
            newValues.put("Prefix", ""); // listTilesAdded.get(i).getPrefix()
            newValues.put("CountryCode", listTilesAdded.get(i).getCountryCode());
            newValues.put("IsTncUser", listTilesAdded.get(i).isIsTncUser());
            newValues.put("ButtonType","2");  // 2 - friend, 3 - family, 4- business
            newValues.put(TILE_COLUMN_IMAGE_LOCK, 0);
            newValues.put("BBID",listTilesAdded.get(i).getBBID());


            if(listTilesAdded.get(i).isIsMobile()){
                newValues.put("IsMobile",1);
            }else{
                newValues.put("IsMobile",0);
            }


            db.insert("Tiles", null, newValues);
        }
        try {
            Cursor c=db.rawQuery("SELECT * FROM Tiles", null);
            if(c!=null)
            {
                c.moveToFirst();
                do{
                    ContactTilesBean objTemp=new ContactTilesBean();
                    objTemp.setImage(c.getBlob(c.getColumnIndex("Image")));
                    listTilesAddedTemp.add(objTemp);
                } while(c.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        if(adapterTileContacts.listIdsAdded.size()>0){
            listTilesId.addAll(adapterTileContacts.listIdsAdded);
        }
        TncUsersFragment tncUserFragment=new TncUsersFragment();
        tncUserFragment.newInstance(listTilesAdded);
        ((HomeScreenActivity)mActivityTabs).setFragment(tncUserFragment);
    }

    //interface to handle in case user want to merge international Number
    INotifyGalleryDialog iNotifyMergeISDNumber=new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // In case of user selects yes ,to import international contacts on confirmation
            addSelectedContactsToDB();
        }

        @Override
        public void no() {
        }
    };

    //interface to handle create & Send Database/merge of shared contacts (being responded by user on our request)
    INotifyGalleryDialog iNotifyConfirmation=new INotifyGalleryDialog() {

        @Override
        public void yes() {
            if(screenType.equalsIgnoreCase("ShareContactsSelection")){
                //In case of Send Contact
                if(chkBoxSelectAll.isChecked()){
                    listTilesAdded=adapterTileContacts.listTiles;
                }else{
                    listTilesAdded=adapterTileContacts.listTilesAdded;
                }
                createSendDatabase(listTilesAdded);
            }
            else if(screenType.equalsIgnoreCase("SharedContactsMerged"))
            {
                // Identifying existence of international number in shared contact
                boolean isISDNumber=false;
                listTilesAdded=new ArrayList<ContactTilesBean>();
                for(int i=0;i<adapterTileContacts.listTilesAdded.size();i++)
                {
                    listTilesAdded.add(adapterTileContacts.listTilesAdded.get(i));
                    if(adapterTileContacts.listTilesAdded.get(i).getPrefix()!=null && adapterTileContacts.listTilesAdded.get(i).getPrefix().startsWith("0"))  //.length()>0)
                    {
                        isISDNumber=true;
                    }
                }

                if(isISDNumber)
                {
                    MergeISDNumberConfirmationDialog dialog =new MergeISDNumberConfirmationDialog();
                    dialog.newInstance("",mActivityTabs,
                            "One or more numbers might be an international number. Please make sure it has appropriate dialing prefix for your country","", iNotifyMergeISDNumber);
                    dialog.show(getChildFragmentManager(), "test");
                }
                else{
                    addSelectedContactsToDB();
                }
            }
        }

        @Override
        public void no() {
        }
    };

    /**
     * Method to add the imported contacts into our database
     */
    private void addSelectedContactsToDB()
    {
        //Add Selected Shared Contact to our DB and update contact share status
        listTilesAdded=new ArrayList<ContactTilesBean>();
        ArrayList<ContactTilesBean> listContactDB = new ArrayList<ContactTilesBean>();
        ContactTilesBean objContactTileBean=new ContactTilesBean();
        for(int i=0;i<adapterTileContacts.listTilesAdded.size();i++)
        {
            listTilesAdded.add(adapterTileContacts.listTilesAdded.get(i));
        }

        for(int j=0;j<listTilesAdded.size();j++)
        {
            String position=String.valueOf(DBQuery.getMaximumTilePosition(mActivityTabs));
            if(position.equalsIgnoreCase("-1")) // in case of no record
            {
                listContactDB=new ArrayList<ContactTilesBean>();
                objContactTileBean=new ContactTilesBean();
                listTilesAdded.get(j).setTilePosition(String.valueOf(j));
                objContactTileBean.setTilePosition(String.valueOf(j));
                objContactTileBean.setName(listTilesAdded.get(j).getName());
                if(listTilesAdded.get(j).getImage()!=null && listTilesAdded.get(j).getImage().length>0)
                    objContactTileBean.setImage(listTilesAdded.get(j).getImage());
                objContactTileBean.setPhoneNumber(listTilesAdded.get(j).getPhoneNumber());
                objContactTileBean.setPrefix(""); /*listTilesAdded.get(j).getPrefix());*/
                objContactTileBean.setCountryCode(listTilesAdded.get(j).getCountryCode());
                objContactTileBean.setIsTncUser(listTilesAdded.get(j).isIsTncUser());
                objContactTileBean.setBBID(listTilesAdded.get(j).getBBID());
                objContactTileBean.setButtonType("2"); // 2-friend, 3- family, 4 - business
                objContactTileBean.setImageLocked(false);
                listContactDB.add(objContactTileBean);
                //Insert Tiles in Database
                DBQuery.insertTile(getActivity(),listContactDB,false);

                //check existence of BBContact in our table

                if(listTilesAdded.get(j).getCountryCode()!=null && listTilesAdded.get(j).getPhoneNumber()!=null){

                    ArrayList<BBContactsBean> mListBbContact = new ArrayList<BBContactsBean>();
                    mListBbContact = DBQuery.getBBContactsfromCountryCodeAndPhoneNumber(getActivity(),
                            listTilesAdded.get(j).getCountryCode(),
                            listTilesAdded.get(j).getPhoneNumber());
                    if(mListBbContact==null || mListBbContact.isEmpty()){
                        BBContactsBean mObjBbContact = new BBContactsBean();
                        mObjBbContact.setBBID(Integer.valueOf(listTilesAdded.get(j).getBBID()));
                        mObjBbContact.setCountryCode(listTilesAdded.get(j).getCountryCode());
                        mObjBbContact.setImage(null);
                        mObjBbContact.setMobID(Integer.valueOf(20000+j));
                        mObjBbContact.setName(listTilesAdded.get(j).getName());
                        mObjBbContact.setPhoneNumber(listTilesAdded.get(j).getPhoneNumber());
                        ArrayList<BBContactsBean> mListAddBBContact = new ArrayList<BBContactsBean>();
                        mListAddBBContact.add(mObjBbContact);
                        DBQuery.insertBBContact(getActivity(), mListAddBBContact);
                    }
                }
            }
            else{  // in case of any record
                listContactDB=new ArrayList<ContactTilesBean>();
                objContactTileBean=new ContactTilesBean();
                listTilesAdded.get(j).setTilePosition(String.valueOf(Integer.parseInt(position)+1));
                objContactTileBean.setTilePosition(String.valueOf(Integer.parseInt(position)+1));
                objContactTileBean.setName(listTilesAdded.get(j).getName());
                objContactTileBean.setPhoneNumber(listTilesAdded.get(j).getPhoneNumber());
                if(listTilesAdded.get(j).getImage()!=null && listTilesAdded.get(j).getImage().length>0)
                    objContactTileBean.setImage(listTilesAdded.get(j).getImage());
                objContactTileBean.setPrefix(""); //listTilesAdded.get(j).getPrefix());
                objContactTileBean.setCountryCode(listTilesAdded.get(j).getCountryCode());
                objContactTileBean.setIsTncUser(listTilesAdded.get(j).isIsTncUser());
//                objContactTileBean.setButtonType(1);
                objContactTileBean.setButtonType("2"); // 2-friend, 3- family, 4 - business
                objContactTileBean.setImageLocked(false);
                objContactTileBean.setBBID(listTilesAdded.get(j).getBBID());
                listContactDB.add(objContactTileBean);
                //Insert Tiles in Database
                DBQuery.insertTile(getActivity(),listContactDB,false);

                //check existence of BBContact in our table

                if(listTilesAdded.get(j).getCountryCode()!=null && listTilesAdded.get(j).getPhoneNumber()!=null){

                    ArrayList<BBContactsBean> mListBbContact = new ArrayList<BBContactsBean>();
                    mListBbContact = DBQuery.getBBContactsfromCountryCodeAndPhoneNumber(getActivity(),
                            listTilesAdded.get(j).getCountryCode(),
                            listTilesAdded.get(j).getPhoneNumber());
                    if(mListBbContact==null || mListBbContact.isEmpty()){
                        BBContactsBean mObjBbContact = new BBContactsBean();
                        mObjBbContact.setBBID(Integer.valueOf(listTilesAdded.get(j).getBBID()));
                        mObjBbContact.setCountryCode(listTilesAdded.get(j).getCountryCode());
                        mObjBbContact.setImage(null);
                        mObjBbContact.setMobID(Integer.valueOf(20000+j));
                        mObjBbContact.setName("");
                        mObjBbContact.setName(listTilesAdded.get(j).getName());
                        mObjBbContact.setPhoneNumber(listTilesAdded.get(j).getPhoneNumber());
                        ArrayList<BBContactsBean> mListAddBBContact = new ArrayList<BBContactsBean>();
                        mListAddBBContact.add(mObjBbContact);
                        DBQuery.insertBBContact(getActivity(), mListAddBBContact);
                    }
                }

            }
        }
        //Dialog to display Successful import of the Contacts
        SharedContactSavedSuccessful dialog=new SharedContactSavedSuccessful();
        dialog.newInstance("",mActivityTabs,"Contacts saved successfully","");
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "test");
        //Update Notification status  i.e. accepted
        checkInternetConnection();
    }

    //Check interenet connection availability
    private void checkInternetConnection()
    {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs))
        {
            ContactShareStatusUpdate contactShareStatusUpdate=new ContactShareStatusUpdate(notification_id,"2");
            updateContactSharingStatus(contactShareStatusUpdate);
        }
        else
        {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    //Update status of contact sharing(Accepted/Rejected)
    private void updateContactSharingStatus(ContactShareStatusUpdate contactShareStatusUpdate)
    {
        try
        {
            gson=new Gson();
            String stingGson = gson.toJson(contactShareStatusUpdate);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.UPDATESHARECONTACT,
                    stringEntity, updateContactShareStatusResponseHandler,
                    mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    //Async Task to update the status
    AsyncHttpResponseHandler updateContactShareStatusResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            //			if ((!progress.isShowing()))
            //				progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null)
                {
                    Logs.writeLog("updateContactShareStatusResponseHandler", "OnSuccess",response.toString());
                    getResponseContactShareStatus(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("updateContactShareStatusResponseHandler", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /**
     * handle response for the update status request
     *
     * @param response
     */
    private void getResponseContactShareStatus(String response)
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
                gson = new Gson();
                NotificationResponseContactShareStatus get_Response = gson.fromJson(response2,NotificationResponseContactShareStatus.class);
                if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE))
                {
                }
                else if (get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE)
                        || get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE_1)
                        || get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE_5)
                        || get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE_2)
                        || get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE_3)
                        || get_Response.response_code.equals(
                        GlobalCommonValues.FAILURE_CODE_4)) {
                    ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
                    dialogErrorMessage.setCancelable(false);
                    dialogErrorMessage.newInstance("",
                            ((HomeScreenActivity) mActivityTabs),
                            get_Response.response_message, "", null);
                    dialogErrorMessage.show(
                            ((HomeScreenActivity) mActivityTabs)
                                    .getSupportFragmentManager(), "test");
                }
            }
            else{
                Log.d("improper_response",response);
                ShowDialog.alert(mActivityTabs,"",getResources().getString(R.string.improper_response_network));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //interface to handle selection of select All Checkbox
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


    /**
     * interface to display popup in case the cloud back up is successful
     */

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

    // Send the database file containing contacts information to the server
    @SuppressWarnings("unused")
    private void sendContactDatabaseToServer()
    {
        File fileTilesDB=new File(db.getPath());
        progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
        gson=new Gson();
        ContactShareBean objContacShareBean=new ContactShareBean(String.valueOf("Enter User's BBID"));
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

    //async task to handle sending of database file of shared contacts to the server
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
                        msg="Successfully sent contact to ";
                    }
                    else if(listTilesAdded.size()>1)
                    {
                        msg="Successfully sent contacts to ";
                    }
                    if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        dialogResult.newInstance("",((HomeScreenActivity)mActivityTabs),msg+"Enter user's Name Here","",null,iNotifyBackupSuccessful);
                        dialogResult.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(), "test");
                    }
                    else if(mActivityTabs instanceof MainBaseActivity)
                    {
                        dialogResult.newInstance("",((MainBaseActivity)mActivityTabs),msg+"Enter user's Name Here","",null,iNotifyBackupSuccessful);
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
}
