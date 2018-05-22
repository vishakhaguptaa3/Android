package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CategoriesSelectAdapter;
import com.tnc.base.BaseFragment;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.ImageRequestBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ImageRequestedResponse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display tile preview with the name and image which will be displayed on the main menu or the home screen 
 *  @author a3logics
 */
public class TilePreviewFragment extends BaseFragment implements OnClickListener{
    private TextView tvTitle,tvMessageDisplay,tvImageRequested,tvContactName; //, tvCategory;
    private FrameLayout flBackArrow,flInformationButton;
//    private LinearLayout llCategoryView;
    private Button btnBack,btnHome,btnFinish;//btnGoBack
    private String title;
    private ImageView imViewContact;
    //    private CustomSpinner spinnerCategory;
    private ContactTilesBean tilesListdata;
    private ArrayList<ContactTilesBean> listContactTiles;
    private Gson gson;
    private TransparentProgressDialog progress;
    private LinearLayout llViewBorder;
    private Context mContext;
    private CheckBox chkBoxEmergencyContact,chkBoxIsMobile,chkBoxImageLock;
    private LinearLayout llParent;
    private String tileName="",tileNumber="",tilePrefix="",tileCountryCode="";
    private String strPhoneWithIsdCode="";
    private boolean isImagePendingStatus=true,isTncUserRegisteredUser=false;
    private String mBBContactName = "",mBBContactBBID = "",mBBContactMobID = "",
            mBBContactCountryCode = "",mBBContactPhoneNumber = "",mBBContactImage = "";
    //  private RadioGroup rgButtonType;
//  private RadioButton rbFamily,rbFriend,rbBusiness;
    private String mButtonType = ""; //2-Friend, 3-Family, 4-Business
    private boolean isTnCUser = false;
    private int mUserBBIDTilesTable = 0;
    private boolean isMobile = false;
    private int is_image_requested = 0, is_emergency = 0;
//    private CategoriesSelectFragment mCategoriesSelectFragment;
    private boolean isFirstTime = true;
    private ArrayList<CategoryBean> mListCategoryFromDB;
    CategoriesSelectAdapter mAdapterCategoryList;
    ImageView hintEmergencyButton,hintLockButton;


    public TilePreviewFragment newInstance(String title,Context mContext)
    {
        this.title=title;
        TilePreviewFragment frag = new TilePreviewFragment();
        this.mContext=mContext;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tilepreviewfragment, container, false);
        idInitialization(view);
        return view;
    }

    @SuppressWarnings("unused")
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        progress=new TransparentProgressDialog(mActivity, R.drawable.customspinner);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvMessageDisplay=(TextView) view.findViewById(R.id.tvMessageDisplay);
        tvImageRequested=(TextView) view.findViewById(R.id.tvImageRequested);
        tvContactName=(TextView) view.findViewById(R.id.tvContactName);
//        tvCategory= (TextView) view.findViewById(R.id.tvCategory);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
//        llCategoryView= (LinearLayout) view.findViewById(R.id.llCategoryView);
        llViewBorder=(LinearLayout) view.findViewById(R.id.llViewBorder);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        btnFinish=(Button) view.findViewById(R.id.btnFinish);
        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        llParent=(LinearLayout) view.findViewById(R.id.llParent);
        chkBoxImageLock =(CheckBox) view.findViewById(R.id.chkBoxImageLock);
        hintEmergencyButton = (ImageView) view.findViewById(R.id.hintEmergencyButton);
        hintLockButton      = (ImageView) view.findViewById(R.id.hintLockButton);




















       /* ListView lvCategories = (ListView) view.findViewById(R.id.listCategories);
        ArrayList<String> mListButtonType = new ArrayList<String>();


        // check if it is in edit mode, then set button type
        if(MainBaseActivity.objTileEdit!=null){
            try{
                if(isFirstTime){
                    isFirstTime = false;
                    mButtonType = MainBaseActivity.objTileEdit.getButtonType();
                }
            }catch(Exception e){
                mButtonType = "";
                e.getMessage();
            }
        }

        mListCategoryFromDB = DBQuery.getCategoriesForTiles(getActivity());
       *//*if(MainBaseActivity.objTileEdit!=null)
       mButtonType = MainBaseActivity.objTileEdit.getButtonType();*//*


        // set button types in a list
        if(mButtonType!=null && !mButtonType.isEmpty()){
            if(mButtonType.contains(",")){
                // in case of multiple value for the buttontype

                String mButtonTypeArray[] = mButtonType.split(",");

                // iterate through array for each value to be added in the list
                for(int i=0;i<mButtonTypeArray.length;i++){
                    mListButtonType.add(mButtonTypeArray[i]);
                }
            }else{
                // in case of single value for the buttontype
                mListButtonType.add(mButtonType);
            }
        }

        if(mListCategoryFromDB!=null && mListCategoryFromDB.size()>0){
            mListCategoryFromDB.remove(0);
            mListCategoryFromDB.remove(0);

            //set List Adapter
            mAdapterCategoryList = new CategoriesSelectAdapter(getActivity(), mListCategoryFromDB, mListButtonType);
            lvCategories.setAdapter(mAdapterCategoryList);
            mAdapterCategoryList.notifyDataSetChanged();
        }*/




























        setCategory(view);
//        rgButtonType = (RadioGroup) view.findViewById(R.id.rgButtonType);
//        rbFamily = (RadioButton) view.findViewById(R.id.rbFamily);
//        rbFriend = (RadioButton) view.findViewById(R.id.rbFriend);
//        rbBusiness = (RadioButton) view.findViewById(R.id.rbBusiness);

//        spinnerCategory = (CustomSpinner) view.findViewById(R.id.spinnerCategory);

        //chkBoxIsMobile=(CheckBox) view.findViewById(R.id.chkBoxIsMobile);
//        rgButtonType.setOnCheckedChangeListener(this);
        //chkBoxIsMobile.setOnClickListener(this);
//        rbFriend.setChecked(true);

        //		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

//        rgButtonType.setVisibility(View.VISIBLE);

        // Display Categories in a drop-down
        //fetch category from database to display it in the dropdown
//        ArrayList<CategoryBean> mListCategoryFromDB = new ArrayList<CategoryBean>();
//
//        ArrayList<String> mListCategory = new ArrayList<String>();

//        mListCategoryFromDB = DBQuery.getCategoriesForTiles(getActivity());

       /* if(mListCategoryFromDB.size() > 0){
            mListCategoryFromDB.remove(0);
            mListCategoryFromDB.remove(0);

            for(CategoryBean mCategoryBean : mListCategoryFromDB){
                mListCategory.add(mCategoryBean.getCategoryName());
            }

            //set dropdown adapter
            spinnerCategory.setListItems(mListCategory);

            //Set interface to handle click event of Custom Spinner
            spinnerCategory.setInterface(mINotifyCategorySelected);
        }

        if(mListCategory!=null && mListCategory.size()>0)
            spinnerCategory.setText(mListCategory.get(0));*/


        if(mActivity instanceof MainBaseActivity)
        {
            String fragment=((MainBaseActivity)mActivity).fragmentManager.getFragments().get(0).toString();
            if(fragment.contains("VideoFragment"))
            {
                flInformationButton.setVisibility(View.GONE);
                btnHome.setVisibility(View.GONE);
            }
            else{
                flInformationButton.setVisibility(View.VISIBLE);
                btnHome.setVisibility(View.VISIBLE);
            }
        }
        else if(mActivity instanceof HomeScreenActivity){
            flInformationButton.setVisibility(View.VISIBLE);
            btnHome.setVisibility(View.VISIBLE);
        }
        imViewContact=(ImageView) view.findViewById(R.id.imViewContact);
        chkBoxEmergencyContact=(CheckBox) view.findViewById(R.id.chkBoxEmergencyContact);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvMessageDisplay, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvImageRequested, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvContactName, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnFinish, "fonts/Roboto-Regular_1.ttf");
        tvMessageDisplay.setText(getActivity().getResources().getString(R.string.txtImageSentSuccessfullytothecontact));
        /*if(MainBaseActivity.isImageRequested)
            tvMessageDisplay.setVisibility(View.VISIBLE);
        else
            tvMessageDisplay.setVisibility(View.INVISIBLE);*/
        tvContactName.setText(MainBaseActivity.objTileDetailBeanStatic.Name);
        String strPhone=MainBaseActivity.objTileDetailBeanStatic.Phone;//MainBaseActivity.selectedContactNumber;
        strPhoneWithIsdCode=MainBaseActivity.objTileDetailBeanStatic.International_Access_Code+MainBaseActivity.objTileDetailBeanStatic.Phone;

        tileName=MainBaseActivity.objTileDetailBeanStatic.Name;
        //Made change in below line as an issue resolve
        tileNumber= MainBaseActivity.contactNumberForTile; //MainBaseActivity.objTileDetailBeanStatic.Phone;

        tilePrefix=MainBaseActivity.objTileDetailBeanStatic.International_Access_Code;
        if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
            tileCountryCode=MainBaseActivity.selectedCountryCodeForTileDetails;
        if(MainBaseActivity.objTileEdit!=null &&  MainBaseActivity.objTileEdit.isIsEmergency()){
            chkBoxEmergencyContact.setChecked(true);
        }else{
            chkBoxEmergencyContact.setChecked(false);
        }
        // setting chkboxImageLock true on the basis of database value
        if(MainBaseActivity.objTileEdit!=null &&  MainBaseActivity.objTileEdit.isImageLocked()){
            chkBoxImageLock.setChecked(true);
        }else{
            chkBoxImageLock.setChecked(false);
        }


        /*// check if it is in edit mode, then set button type
        if(MainBaseActivity.objTileEdit!=null){
            try{
               if(isFirstTime){
                   isFirstTime = false;
                   mButtonType = MainBaseActivity.objTileEdit.getButtonType();
               }
            }catch(Exception e){
                mButtonType = "";
                e.getMessage();
            }
        }*/

        if(saveState.isRegistered(mActivity))
        {
            String number=GlobalConfig_Methods.getBBNumberToCheck(mActivity,tilePrefix+tileCountryCode+tileNumber);
            String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
            boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false;
            try {
                String []arrayUserDetails=number.split(",");
                countryCodeRegisteredUser=arrayUserDetails[0];
                numberRegisteredUser=arrayUserDetails[1];
                isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
                isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
                isdCodeRegisteredUser=arrayUserDetails[4];
                isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                if(isTncUserRegisteredUser || MainBaseActivity.isNotificationPushSend)
                {
                    llViewBorder.setBackgroundResource(R.drawable.rectangular_boundary_blue);
                }
                else{
                    llViewBorder.setBackgroundResource(R.drawable.rectangular_boundary_transparent);
                }
            } catch (NoSuchMethodError e) {
                e.getMessage();
            }

        }//Scope Of Is Registered Check Ends Here

        if(MainBaseActivity._bitmap!=null)
        {
            tvImageRequested.setVisibility(View.GONE);
            imViewContact.setImageBitmap(MainBaseActivity._bitmap);

            // Phase -4 Change for image pending status
            if(MainBaseActivity.objTileEdit!=null)
            {
                if(!MainBaseActivity.isImageRequested)
                {
                    tvImageRequested.setVisibility(View.GONE);
                    MainBaseActivity.objTileEdit.setIsImagePending(0);
                    isImagePendingStatus=false;
                }
                else{
                    tvImageRequested.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            if(GlobalCommonValues._Contacimage!=null)
            {
                imViewContact.setImageBitmap(GlobalCommonValues._Contacimage);
            }
            else if(GlobalCommonValues._Contacimage==null)
            {
                imViewContact.setImageBitmap(((BitmapDrawable)mActivity.getResources().getDrawable(R.drawable.no_image)).getBitmap());
            }
            if(MainBaseActivity.isImageRequested)
            {
                tvImageRequested.setVisibility(View.VISIBLE);
            }
            else
            {
                tvImageRequested.setVisibility(View.GONE);
            }
            if(MainBaseActivity.objTileEdit!=null)
            {
                byte arrayImage[]=MainBaseActivity.objTileEdit.getImage();
                if(arrayImage!=null && arrayImage.length>0){
                    imViewContact.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
                }

                /*
                Commented as a part of new requirements to display options on new screen

                mButtonType = MainBaseActivity.objTileEdit.getButtonType();

                spinnerCategory.setText(mListCategory.get(mButtonType-1));*/

                // In case of tile edit set category in drop-down to be displayed

                /*if(mButtonType == 1){
                    rbFriend.setChecked(true);
                    rbFamily.setChecked(false);
                    rbBusiness.setChecked(false);
                }else if(mButtonType == 2){
                    rbFriend.setChecked(false);
                    rbFamily.setChecked(true);
                    rbBusiness.setChecked(false);
                }else if(mButtonType == 3){
                    rbFriend.setChecked(false);
                    rbFamily.setChecked(false);
                    rbBusiness.setChecked(true);
                }*/
            }
            // Phase -4 Change for image pending status
            if(MainBaseActivity.objTileEdit!=null && MainBaseActivity.objTileEdit.getImage()!=null && MainBaseActivity.objTileEdit.getImage().length>0)
            {    if(!MainBaseActivity.isImageRequested)
            {
                tvImageRequested.setVisibility(View.GONE);
                MainBaseActivity.objTileEdit.setIsImagePending(0);
                isImagePendingStatus=false;
            }
            else if(MainBaseActivity.isImageRequested)
            {
                tvImageRequested.setVisibility(View.VISIBLE);
                MainBaseActivity.objTileEdit.setIsImagePending(1);
                isImagePendingStatus=true;
            }
            }
        }

        tvMessageDisplay.setVisibility(View.GONE);
        btnBack.setOnClickListener(this);
        //		btnGoBack.setOnClickListener(this);
        //		btnGoBack.setVisibility(View.GONE);
        btnFinish.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        tvMessageDisplay.setOnClickListener(this);
//        llCategoryView.setOnClickListener(this);
        llParent.setOnClickListener(this);
        hintEmergencyButton.setOnClickListener(this);
        hintLockButton.setOnClickListener(this);
        if(MainBaseActivity.isImageRequested && saveState.isRegistered(mActivity))
        {
        }
        copyDatabase();
    }

    /**
     * Method to copy the database in case db don't exists
     */
    private void copyDatabase(){
        CopyDBFromAssets obj = new CopyDBFromAssets(getActivity());
        obj.copyDatabase();

        //		if (!new File(CopyDBFromAssets.DB_PATH + CopyDBFromAssets.DATABASE_NAME).canRead()) {
        //			obj.copyDatabase();
        //		}
    }

    public void onDestroy() {
        super.onDestroy();
        //		GalleryFragment.ifisfirstTime=false;
        MainBaseActivity._bitmap=null;
        MainBaseActivity.isImageSelected=false;
        MainBaseActivity.isImageRequested=false;
        MainBaseActivity.selectedImagepath=null;
        MainBaseActivity.isCameraCanceled=false;
        MainBaseActivity.isNotificationPushSend=false;
        mBBContactCountryCode = "";
        mBBContactPhoneNumber = "";
    };

    /**
     * Method to check internet connection availability
     */
    public void checkInternetConnection()
    {
        is_image_requested = 1;
        if(chkBoxEmergencyContact.isChecked() && saveState.getIsEmergencyContactNotification(getActivity())){
            is_emergency = 1;
        }else{
            is_emergency = 0;
        }

        if (NetworkConnection.isNetworkAvailable(mActivity)){
            ImageRequestBean imageRequestBean=new ImageRequestBean(saveState.getBBID_User(mActivity),
                    is_image_requested, is_emergency,0);
            imageRequest(imageRequestBean);
        }else{
            GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
        }
    }

    /**
     * Method to send image request
     */
    private void imageRequest(ImageRequestBean imageRequestBean)
    {
        try
        {
            gson=new Gson();
            String stingGson = gson.toJson(imageRequestBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivity,
                    GlobalCommonValues.IMAGE_REQUEST,
                    stringEntity, sendImageRequestHandle,
                    mActivity.getString(R.string.private_key),saveState.getPublicKey(mActivity));
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    //asyc task for the image request made to the TnC user
    AsyncHttpResponseHandler sendImageRequestHandle = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null)
                {
                    Logs.writeLog("ChooseImageFragment","OnSuccess",response.toString());
                    getResponseImageRequest(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("ChooseImageFragment","OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /**
     * method to handle the response that we get as the request is made for image to the TnC user
     * @param response
     */
    private void getResponseImageRequest(String response)
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
                ImageRequestedResponse get_Response = gson.fromJson(response2,ImageRequestedResponse.class);
                if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE))
                {
                }
                else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
                        get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2))
                {
                    ImageRequestDialog dialogErrorResponse=new ImageRequestDialog();
                    if(mActivity instanceof MainBaseActivity)
                    {
                        dialogErrorResponse.newInstance("", ((MainBaseActivity)mActivity),get_Response.getResponse_message(),"",null);
                        dialogErrorResponse.setCancelable(false);
                        dialogErrorResponse.show(((MainBaseActivity)mActivity).getSupportFragmentManager(),"test");
                    }
                    else if(mActivity instanceof HomeScreenActivity)
                    {
                        dialogErrorResponse.newInstance("", ((HomeScreenActivity)mActivity),get_Response.getResponse_message(),"",null);
                        dialogErrorResponse.setCancelable(false);
                        dialogErrorResponse.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(),"test");
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * interface to handle selection of categories
     */
    INotifyAction mActionSelectedcategories = new INotifyAction() {
        @Override
        public void setAction(String selectedcategories) {
            mButtonType = selectedcategories;
        }
    };

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.btnBack)
        {
            MainBaseActivity.isImageSelected=false;
            if(mActivity instanceof MainBaseActivity)
                ((MainBaseActivity)mActivity).fragmentManager.popBackStack();
            else if(mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
        } else if(v.getId()==R.id.hintEmergencyButton){
            GlobalConfig_Methods.displayAlertDialog(getActivity().getResources().getString(R.string.txtHintEmergencyContact),getActivity());
        } else if(v.getId()==R.id.hintLockButton){
            GlobalConfig_Methods.displayAlertDialog(getActivity().getResources().getString(R.string.txtHintLockImage),getActivity());
        }



        /*else if(v.getId() == R.id.llCategoryView){
            mCategoriesSelectFragment = new CategoriesSelectFragment();
            mCategoriesSelectFragment.newInstance(getActivity(), mActionSelectedcategories, mButtonType);

            // Go to category selection screen
            if(mActivity instanceof MainBaseActivity)
                ((MainBaseActivity)mActivity).setFragment(mCategoriesSelectFragment);
            else if(mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity)mActivity).setFragment(mCategoriesSelectFragment);

        }*/else if(v.getId()==R.id.btnFinish){
            selectCategories();
            if(mButtonType == null || mButtonType.isEmpty()){
                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.setCancelable(false);
                mDialog.newInstance("",getActivity(), getResources().getString(R.string.txtSelectCategoryMessage), "", null);
                mDialog.show(getChildFragmentManager(), "Test");
                return;
            }

            try {
                if(MainBaseActivity.isNotificationPushSend){
                    checkInternetConnection();
                }
                //				String contactNumber="";
                ArrayList<ContactTilesBean> listContactDB=new ArrayList<ContactTilesBean>();
                boolean isContactFound=false;
//                listContactDB=DBQuery.getAllTiles(mActivity);
                listContactDB=DBQuery.getAllTiles(mActivity);
                if(MainBaseActivity.objTileEdit==null)
                {
                    String mPrefix="",mCountrycode="",mNumber="";

                    if(tilePrefix!=null && !tilePrefix.trim().equals(""))
                        mPrefix=tilePrefix;
                    if(tileCountryCode!=null && !tileCountryCode.trim().equals(""))
                        mCountrycode=tileCountryCode;
                    if(tileNumber!=null && !tileNumber.trim().equals(""))
                        mNumber=tileNumber;
                    if((mPrefix+mCountrycode+mNumber).length()>7)
                    {
                        isContactFound=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(),mPrefix+mCountrycode+mNumber);
                    }
                }

                if(isContactFound)  // && MainBaseActivity.objTileEdit==null
                {
                    ImageRequestDialog dialog=new ImageRequestDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivity,"Chat button for this contact number already exists","",null);
                    dialog.show(getChildFragmentManager(), "test");
                }
                else
                {
                    listContactTiles=new ArrayList<ContactTilesBean>();
                    tilesListdata=new ContactTilesBean();
                    tilesListdata.setName(MainBaseActivity.objTileDetailBeanStatic.Name);
                    /*if(chkBoxImageLock.isChecked()){
                        tilesListdata.setImageLocked(true);
                    }else{
                        tilesListdata.setImageLocked(false);
                    }*/
                    tilesListdata.setImageLocked(chkBoxImageLock.isChecked());
                    tilesListdata.setIsMobile(MainBaseActivity.isMobileChecked);
                    if(MainBaseActivity.isMobileChecked){
                        //system.out.println("ISMOBILE "+ "TRUE");
                    }else{
                        //system.out.println("ISMOBILE "+"FALSE");
                    }
                    tilesListdata.setPhoneNumber(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(tileNumber));
                    if(tilePrefix!=null && !tilePrefix.trim().equals(""))
                        tilesListdata.setPrefix(tilePrefix);
                    else if(tilePrefix.trim().equals(""))
                        tilesListdata.setPrefix("");

                    if(tileCountryCode!=null && !tileCountryCode.trim().equals(""))
                        tilesListdata.setCountryCode(tileCountryCode);

                    if(MainBaseActivity._bitmap!=null || GlobalCommonValues._Contacimage!=null || (MainBaseActivity.objTileEdit!=null && MainBaseActivity.objTileEdit.getImage()!=null && MainBaseActivity.objTileEdit.getImage().length>0))
                    {
                        Bitmap _bitmap=((BitmapDrawable)((ImageView)imViewContact).getDrawable()).getBitmap();
                        ByteArrayOutputStream blob = new ByteArrayOutputStream();
                        _bitmap.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, blob);
                        byte[] arrayImage = blob.toByteArray();
                        tilesListdata.setImage(arrayImage);
                    }
                    else if(MainBaseActivity._bitmap==null && GlobalCommonValues._Contacimage==null)
                    {
                        tilesListdata.setImage(null);
                    }

                    if(tvImageRequested.getVisibility()==View.VISIBLE)  //MainBaseActivity.isImageRequested
                    {
                        tilesListdata.setIsImagePending(1);
                    }
                    else if(tvImageRequested.getVisibility()==View.GONE || tvImageRequested.getVisibility()==View.INVISIBLE)
                    {
                        tilesListdata.setIsImagePending(0);
                    }
                    try {
                        ContactTilesBean objContactTileBean;
                        if(chkBoxEmergencyContact.isChecked()){
                            //In case of emergency is checked

                            tilesListdata.setIsEmergency(true);
                            if(MainBaseActivity.objTileEdit!=null)
                            {
                                objContactTileBean=new ContactTilesBean();
                                objContactTileBean.setName(tilesListdata.getName());
                                objContactTileBean.setPrefix(tilesListdata.getPrefix());
                                objContactTileBean.setCountryCode(tilesListdata.getCountryCode());
                                objContactTileBean.setPhoneNumber(tilesListdata.getPhoneNumber());
                                objContactTileBean.setIsMobile(tilesListdata.isIsMobile());
                                objContactTileBean.setButtonType(mButtonType);
                                objContactTileBean.setImageLocked(chkBoxImageLock.isChecked());


                                /**
                                 * DEVANSHU NATH TRIPATHI
                                 * set the ismobile is true or not
                                 */
                                objContactTileBean.setIsMobile(tilesListdata.isIsMobile());

                                if(tilesListdata.isIsMobile()){
                                    //system.out.println("ISMOBILE 1"+ "TRUE");
                                }else{
                                    //system.out.println("ISMOBILE 1 "+"FALSE");
                                }

                                if(MainBaseActivity.isNotificationPushSend){
                                    mBBContactBBID = saveState.getBBID_User(mActivity);
                                    mBBContactName = tilesListdata.getName();
                                    mBBContactCountryCode = tilesListdata.getCountryCode();
                                    mBBContactPhoneNumber = tilesListdata.getPhoneNumber();
                                    try {
                                        mBBContactImage = tilesListdata.getImage().toString();
                                    } catch (Exception e) {
                                        mBBContactImage = "";
                                    }
                                    mBBContactMobID = String.valueOf(listContactDB.size()+12000);
                                }
                                isTnCUser = isTncUser();
                                objContactTileBean.setIsTncUser(isTnCUser);
                                if(isTnCUser && mUserBBIDTilesTable>0){
                                    objContactTileBean.setBBID(mUserBBIDTilesTable);
                                }else{
                                    objContactTileBean.setBBID(0);
                                }

                                objContactTileBean.setImage(tilesListdata.getImage());
                                objContactTileBean.setIsImagePending(MainBaseActivity.objTileEdit.getIsImagePending());
                                objContactTileBean.setTilePosition(MainBaseActivity.objTileEdit.getTilePosition());
                                objContactTileBean.setIsEmergency(chkBoxEmergencyContact.isChecked());

                                if(MainBaseActivity.objTileEdit!=null && !isImagePendingStatus){
                                    objContactTileBean.setIsImagePending(0);
                                }
                                DBQuery.updateTile(mActivity,objContactTileBean);
                            }
                            else if(MainBaseActivity.objTileEdit==null){

                                boolean isNonEmergencyTileExist=false;
                                boolean isAddedOnce = false;
                                for(int i=0;i<listContactDB.size();i++)  // in case of emergency tile creation to shift the current non-emergency tiles to one position 'up'
                                {
                                    if(listContactDB.get(i).isIsEmergency()==false){
                                        if(!isAddedOnce)
                                        {
                                            tilesListdata.setTilePosition(String.valueOf(listContactDB.get(i).getTilePosition()));
//                                            listContactTiles.add(tilesListdata);
                                            isAddedOnce = true;
                                        }
                                        isNonEmergencyTileExist=true;
                                        DBQuery.updateTilePositionByOne(mActivity,listContactDB.get(i).getPhoneNumber(),Integer.parseInt(listContactDB.get(i).getTilePosition()));
                                    }
                                }
                                if(MainBaseActivity.isNotificationPushSend){
                                    mBBContactBBID = saveState.getBBID_User(mActivity);
                                    mBBContactName = tilesListdata.getName();
                                    mBBContactCountryCode = tilesListdata.getCountryCode();
                                    mBBContactPhoneNumber = tilesListdata.getPhoneNumber();
                                    try {
                                        mBBContactImage = tilesListdata.getImage().toString();
                                    } catch (Exception e) {
                                        mBBContactImage = "";
                                    }
                                    mBBContactMobID = String.valueOf(listContactDB.size()+12000);
                                }

                                int emergencyTilePosition = 0;

                                if(!isNonEmergencyTileExist){
                                    // implemented as a part of feedback sheet point 343  fixation
                                    // in case when alltils exists are emergency tiles only and no non-emergency tiles are there
                                    emergencyTilePosition=DBQuery.getMaximumTilePositionEmetegncy(mActivity);
                                    if(emergencyTilePosition==-1)
                                        emergencyTilePosition=0;
                                    else
                                        emergencyTilePosition=emergencyTilePosition+1;

                                    // implemented as a part of feedback sheet point 343  fixation

                                    tilesListdata.setTilePosition(String.valueOf(emergencyTilePosition));


                                    /*isTnCUser = isTncUser(); ///
                                    if(isTnCUser && mUserBBIDTilesTable>0){
                                        tilesListdata.setBBID(mUserBBIDTilesTable);
                                    }else{
                                        tilesListdata.setBBID(0);
                                    }
                                    tilesListdata.setIsTncUser(isTnCUser);
                                    tilesListdata.setButtonType(mButtonType);
                                    tilesListdata.setTilePosition(String.valueOf(emergencyTilePosition));
                                    listContactTiles.add(tilesListdata);*/
                                }

                                isTnCUser = isTncUser();
                                if(isTnCUser && mUserBBIDTilesTable>0){
                                    tilesListdata.setBBID(mUserBBIDTilesTable);
                                }else{
                                    tilesListdata.setBBID(0);
                                }
                                tilesListdata.setIsTncUser(isTnCUser);
                                tilesListdata.setButtonType(mButtonType);

                                listContactTiles.add(tilesListdata);

                                // Save Selected Catgory
                                saveSelectedCategory(String.valueOf(tilesListdata.getButtonType()));

                                DBQuery.insertTile(getActivity(),listContactTiles,false);
                                saveState.setTileCreated(mActivity, true);
                            }
                        }
                        else
                        {
                            //In case of emergency is not checked
                            tilesListdata.setIsEmergency(false);
                            if(MainBaseActivity.objTileEdit!=null)
                            {
                                objContactTileBean=new ContactTilesBean();
                                objContactTileBean.setName(tilesListdata.getName());
                                objContactTileBean.setPrefix(tilesListdata.getPrefix());
                                objContactTileBean.setCountryCode(tilesListdata.getCountryCode());
                                objContactTileBean.setPhoneNumber(tilesListdata.getPhoneNumber());
                                objContactTileBean.setIsMobile(tilesListdata.isIsMobile());
                                objContactTileBean.setButtonType(mButtonType);

                                if(MainBaseActivity.isNotificationPushSend){
                                    mBBContactBBID = saveState.getBBID_User(mActivity);
                                    mBBContactName = tilesListdata.getName();
                                    mBBContactCountryCode = tilesListdata.getCountryCode();
                                    mBBContactPhoneNumber = tilesListdata.getPhoneNumber();
                                    try {
                                        mBBContactImage = tilesListdata.getImage().toString();
                                    } catch (Exception e) {
                                        mBBContactImage = "";
                                    }
                                    mBBContactMobID = String.valueOf(listContactDB.size()+12000);
                                }

                                isTnCUser = isTncUser();

                                if(isTnCUser && mUserBBIDTilesTable>0){
                                    objContactTileBean.setBBID(mUserBBIDTilesTable);
                                }else{
                                    objContactTileBean.setBBID(0);
                                }

                                objContactTileBean.setIsTncUser(isTnCUser);

                                objContactTileBean.setImage(tilesListdata.getImage());
                                objContactTileBean.setIsImagePending(MainBaseActivity.objTileEdit.getIsImagePending());
                                objContactTileBean.setTilePosition(MainBaseActivity.objTileEdit.getTilePosition());
                                objContactTileBean.setIsEmergency(chkBoxEmergencyContact.isChecked());
                                objContactTileBean.setImageLocked(chkBoxImageLock.isChecked());

                                // Phase -4 Change for image pending status
                                if(MainBaseActivity.objTileEdit!=null && !isImagePendingStatus)
                                {
                                    objContactTileBean.setIsImagePending(0);
                                }
                                //asdfkjasdkfj
                                DBQuery.updateTile(mActivity,objContactTileBean);
                                MainBaseActivity.isIsdCodeFlagChecked=false;
                            }
                            else if(MainBaseActivity.objTileEdit==null)
                            {
                                String position=String.valueOf(DBQuery.getMaximumTilePosition(mActivity));
                                if(position.equalsIgnoreCase("-1"))
                                    tilesListdata.setTilePosition("0");
                                else{
                                    //								int maxTilePosition = DBQuery.getMaximumTilePosition(mActivity);
                                    tilesListdata.setTilePosition(String.valueOf(Integer.parseInt(position)+1));
                                }

                                if(MainBaseActivity.isNotificationPushSend){
                                    mBBContactBBID = saveState.getBBID_User(mActivity);
                                    mBBContactName = tilesListdata.getName();
                                    mBBContactCountryCode = tilesListdata.getCountryCode();
                                    mBBContactPhoneNumber = tilesListdata.getPhoneNumber();
                                    try {
                                        mBBContactImage = tilesListdata.getImage().toString();
                                    } catch (Exception e) {
                                        mBBContactImage = "";
                                    }
                                    mBBContactMobID = String.valueOf(listContactDB.size()+12000);
                                }

                                tilesListdata.setButtonType(mButtonType);

                                isTnCUser = isTncUser();

                                if(isTnCUser && mUserBBIDTilesTable>0){
                                    tilesListdata.setBBID(mUserBBIDTilesTable);
                                }else{
                                    tilesListdata.setBBID(0);
                                }

                                tilesListdata.setIsTncUser(isTnCUser);

                                listContactTiles.add(tilesListdata);

                                // Save Selected Catgory
                                saveSelectedCategory(String.valueOf(tilesListdata.getButtonType()));

                                DBQuery.insertTile(getActivity(),listContactTiles,false);
                                saveState.setTileCreated(mActivity, true);
                                MainBaseActivity.isTileCreated=true;
                                MainBaseActivity.isIsdCodeFlagChecked=false;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.getMessage();
                        saveState.setTileCreated(mActivity,false);
                    }
                    saveState.setChanged(getActivity(),true);

                    Intent myIntent = new Intent(getActivity(), HomeScreenActivity.class);

                    if(saveState.isFirstTile(getActivity())==true)
                    {
//						BaseFragment.dialogRegistration=null;
//						BaseFragment.dialogRegistration=new RegistrationDetailDialog();
//						BaseFragment.dialogRegistration.newInstance("CONGRATULATION!",mActivity,
//								"You have created your \nfirst chat button","",alertBack,true,saveState);
//						BaseFragment.dialogRegistration.setCancelable(false);
//						BaseFragment.dialogRegistration.show(getChildFragmentManager(), "test");
                        if(saveState.isRegistered(mActivity)){
                            saveState.setUploadBackupRequested(mActivity,true);
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("display_registration_screen",true);
                            myIntent.putExtras(bundle);
                        }
                    }
					/*else
					{
						getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
						getActivity().finish();
					}*/

                    getActivity().startActivity(myIntent);
                    getActivity().finish();

                    saveState.setIS_EMERGENCY_CONTACT_CREATED(getActivity(), chkBoxEmergencyContact.isChecked());
                    saveState.setFirstTile(getActivity(),false);
                    MainBaseActivity.isImageSelected=false;
                    //phase-4 Comment
                    MainBaseActivity.selectedPrefixCodeForTileDetails="";
                    MainBaseActivity.selectedCountryCodeForTileDetails="";
                    GlobalCommonValues._Contacimage=null;
                }
            } catch (Exception e) {
                e.getMessage();
            }
            MainBaseActivity._bitmap=null;
            MainBaseActivity._bitmapContact=null;
        }
        if(v.getId()==R.id.btnHome)
        {
            if(mActivity instanceof MainBaseActivity)
            {
                ((MainBaseActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                ((MainBaseActivity)mActivity).finish();
            }
            else if(mActivity instanceof HomeScreenActivity)
            {
                ((HomeScreenActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                ((HomeScreenActivity)mActivity).finish();
            }
        }
        if(v.getId()==R.id.tvMessageDisplay){

        }
        if(v.getId()==R.id.llParent){

        }
        if(v.getId()==R.id.chkBoxIsMobile){
            if (((CheckBox) v).isChecked()) {
                isMobile=true;
            }else{
                isMobile=false;
            }
        }
    }

    private void saveSelectedCategory(String SelectedCategory){
        saveState.setSELECTED_CATEGORY(getActivity(), SelectedCategory);
    }

    //check if the user is a TnC userf
    private boolean isTncUser(){
        boolean isTncUser = false;
        if(isTncUserRegisteredUser || MainBaseActivity.isNotificationPushSend){
            isTncUser = true;
        }else{
            isTncUser = false;
        }

        //Check for existence of BBContant in BBContacts table and insert record in database if it doesn't exists
        if(isTncUser){


            mUserBBIDTilesTable = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mActivity,
                    MainBaseActivity.contactNumberForTile, MainBaseActivity.selectedCountryCodeForTileDetails);

            if(mBBContactCountryCode.trim().equals("") && mBBContactPhoneNumber.trim().equals(""))
                mBBContactCountryCode = MainBaseActivity.selectedCountryCodeForTileDetails;
            mBBContactPhoneNumber = MainBaseActivity.contactNumberForTile;
            ArrayList<BBContactsBean> mListBbContact = new ArrayList<BBContactsBean>();
            mListBbContact = DBQuery.getBBContactsfromCountryCodeAndPhoneNumber(getActivity(), mBBContactCountryCode,
                    mBBContactPhoneNumber);
            if(mListBbContact==null || mListBbContact.isEmpty()){
                BBContactsBean mObjBbContact = new BBContactsBean();
                mObjBbContact.setBBID(Integer.valueOf(mBBContactBBID));
                mObjBbContact.setCountryCode(mBBContactCountryCode);
                mObjBbContact.setImage(mBBContactImage);
                mObjBbContact.setMobID(Integer.valueOf(mBBContactMobID));
                if(mBBContactName.trim().equals("")){
                    mBBContactName = tvContactName.getText().toString();
                }
                mObjBbContact.setName(mBBContactName);
                mObjBbContact.setPhoneNumber(mBBContactPhoneNumber);
                ArrayList<BBContactsBean> mListAddBBContact = new ArrayList<BBContactsBean>();
                mListAddBBContact.add(mObjBbContact);
                DBQuery.insertBBContact(getActivity(), mListAddBBContact);

                mUserBBIDTilesTable = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mActivity,
                        mBBContactCountryCode, mBBContactPhoneNumber);

                GlobalCommonValues.listBBContacts.clear();
                GlobalCommonValues.listBBContacts = DBQuery.getAllBBContacts(getActivity());
            }
        }

        return isTncUser;
    }

    /**
     * Interface to handle click action of category dropdown
     */
    /*INotifyAction mINotifyCategorySelected = new INotifyAction() {
        @Override
        public void setAction(String category) {

            String mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(getActivity(), category);

            if(mCategoryID!=null && !mCategoryID.trim().equalsIgnoreCase("")){
                mButtonType = Integer.parseInt(mCategoryID);
            }
        }
    };*/

   /* @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rbFriend){
            mButtonType = 1;
            rbFriend.setChecked(true);
            rbFamily.setChecked(false);
            rbBusiness.setChecked(false);
        }else if(checkedId == R.id.rbFamily){
            mButtonType = 2;
            rbFriend.setChecked(false);
            rbFamily.setChecked(true);
            rbBusiness.setChecked(false);
        }else if(checkedId == R.id.rbBusiness){
            mButtonType = 3;
            rbFriend.setChecked(false);
            rbFamily.setChecked(false);
            rbBusiness.setChecked(true);
        }
    }*/
   
   void setCategory(View view){
       ListView lvCategories = (ListView) view.findViewById(R.id.listCategories);
       ArrayList<String> mListButtonType = new ArrayList<String>();


       // check if it is in edit mode, then set button type
       if(MainBaseActivity.objTileEdit!=null){
           try{
               if(isFirstTime){
                   isFirstTime = false;
                   mButtonType = MainBaseActivity.objTileEdit.getButtonType();
               }
           }catch(Exception e){
               mButtonType = "";
               e.getMessage();
           }
       }

       mListCategoryFromDB = DBQuery.getCategoriesForTiles(getActivity());
       if(MainBaseActivity.objTileEdit!=null)
       mButtonType = MainBaseActivity.objTileEdit.getButtonType();


       // set button types in a list
       if(mButtonType!=null && !mButtonType.isEmpty()){
           if(mButtonType.contains(",")){
               // in case of multiple value for the buttontype

               String mButtonTypeArray[] = mButtonType.split(",");

               // iterate through array for each value to be added in the list
               for(int i=0;i<mButtonTypeArray.length;i++){
                   mListButtonType.add(mButtonTypeArray[i]);
               }
           }else{
               // in case of single value for the buttontype
               mListButtonType.add(mButtonType);
           }
       }

       if(mListCategoryFromDB!=null && mListCategoryFromDB.size()>0){
           mListCategoryFromDB.remove(0);
           mListCategoryFromDB.remove(0);

           //set List Adapter
           mAdapterCategoryList = new CategoriesSelectAdapter(getActivity(), mListCategoryFromDB, mListButtonType);
           lvCategories.setAdapter(mAdapterCategoryList);
       }
   }

    /**
     * Following function is used to select category 
     */
    private void selectCategories(){
        // send selected categories id to the back screen
        if(mActionSelectedcategories!=null){
            if(mAdapterCategoryList == null || mAdapterCategoryList.mListselectedCategory == null || mAdapterCategoryList.mListselectedCategory.size() == 0){
                mActionSelectedcategories.setAction("");
            }else if(mAdapterCategoryList!=null && mAdapterCategoryList.mListselectedCategory!=null && mAdapterCategoryList.mListselectedCategory.size() > 0){

                // if only one category selected then no  comma separation values required
                if(mAdapterCategoryList.mListselectedCategory.size() == 1){
                    mActionSelectedcategories.setAction(mAdapterCategoryList.mListselectedCategory.get(0));
                }else{
                    // if more than one category selected then comma separation values required by iterating through each value in the loop

                    String selectedCategories = "";

                    for(int i=0; i< mAdapterCategoryList.mListselectedCategory.size(); i++){
                        selectedCategories += mAdapterCategoryList.mListselectedCategory.get(i);

                        if(!(i == mAdapterCategoryList.mListselectedCategory.size()-1)){
                            selectedCategories += ",";
                        }
                    }
                    mActionSelectedcategories.setAction(selectedCategories);
                }
            }
        }
    }

}
