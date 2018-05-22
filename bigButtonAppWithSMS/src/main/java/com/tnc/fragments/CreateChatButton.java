package com.tnc.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.bean.AddTileBean;
import com.tnc.bean.TileDetailBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.SelectImagePopup;
import com.tnc.dialog.TileDeleteSuccessDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import static com.tnc.fragments.UserRegistration1.REQUEST_CODE_CROP_IMAGE;
import static com.tnc.fragments.UserRegistration1.REQUEST_CODE_TAKE_PICTURE;

/**
 * Created by a3logics on 25/7/17.
 */

public class CreateChatButton  extends BaseFragment implements View.OnClickListener {

    private LinearLayout llImageHolder;
    private ImageView imViewUserImage;
    private TextView tvTitle,tvStep,tvContact,
            tvContactPhone,tvCountryCode,tvIsMobile,tvIsdCodeFlag;
    private Button btnBack,btnHome,btnNext,btnDelete;
    private FrameLayout flBackArrow,flInformationButton;
    private EditText etContactName,etContactNumber,etCountryCode,etISDCode;
    private CheckBox chkBoxIsMobile,chkBoxIsdCodeFlag;
    private AddTileBean addTileBean=null;
    private String TAG="TileContactDetailsFragment";
    private ImageRequestDialog dialogAlert=null;
    private Context mActivity;
    private String tileDetailsRegisteredUser="";
    private String tileDetailsNonRegisteredUser="";
    private String contactName="";
    private SharedPreference saveState;
    private int BBID=-1;
    private boolean isButtonClicked=false;
    private LinearLayout llParent;

    //countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser;
    String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
    boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
    String originalNumber="";
    INotifyAction iActionIsFromHomeScreen;
    private boolean isCountryCodeShow = false;
    boolean isMobiles= false;
    boolean isFirstTime=true;
    int count=1;

    private Uri fileUri;
    private final int PICK_IMAGE_REQUEST = 210;
    private String picturePath = "";

    public CreateChatButton()
    {
    }

    public CreateChatButton newInstance(Context mActivity,boolean ismobile,String tileDetailsRegisteredUser,
                                        String tileDetailsNonRegisteredUser,String contactName,String originalNumber,INotifyAction iActionIsFromHomeScreen,
                                        boolean isCountryCodeShow)
    {
        CreateChatButton frag = new CreateChatButton();
        this.mActivity=mActivity;
        this.originalNumber=originalNumber;
        this.tileDetailsRegisteredUser=tileDetailsRegisteredUser;
        this.tileDetailsNonRegisteredUser=tileDetailsNonRegisteredUser;
        this.contactName=contactName;
        this.iActionIsFromHomeScreen=iActionIsFromHomeScreen;
        this.isCountryCodeShow = isCountryCodeShow;
        this.isMobiles=ismobile;
        if(tileDetailsRegisteredUser!=null && !tileDetailsRegisteredUser.trim().equals(""))
            if(!isMobiles && Boolean.parseBoolean(tileDetailsRegisteredUser.split(",")[2])){
                isMobiles = true;
            }

        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_chat_button_fragment, container,
                false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
//        tvCreateContact=(TextView) view.findViewById(R.id.tvCreateContact);
        tvStep=(TextView) view.findViewById(R.id.tvStep);

        tvContactPhone=(TextView) view.findViewById(R.id.tvContactPhone);
        tvCountryCode=(TextView) view.findViewById(R.id.tvCountryCode);
        tvIsMobile=(TextView) view.findViewById(R.id.tvIsMobile);
        tvIsdCodeFlag=(TextView) view.findViewById(R.id.tvIsdCodeFlag);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);

        imViewUserImage = (ImageView) view.findViewById(R.id.imViewUserImage);
        llImageHolder = view.findViewById(R.id.llImageHolder);
        llImageHolder.setVisibility(View.GONE);

        btnBack=(Button) view.findViewById(R.id.btnBack);
        etContactName=(EditText) view.findViewById(R.id.etContactName);
        etContactNumber=(EditText) view.findViewById(R.id.etContactNumber);
        etCountryCode=(EditText) view.findViewById(R.id.etCountryCode);
        etISDCode=(EditText) view.findViewById(R.id.etISDCode);
        chkBoxIsMobile=(CheckBox) view.findViewById(R.id.chkBoxIsMobile);
        chkBoxIsdCodeFlag=(CheckBox) view.findViewById(R.id.chkBoxIsdCodeFlag);
        btnNext=(Button) view.findViewById(R.id.btnNext);
        btnDelete=(Button) view.findViewById(R.id.btnDelete);
        btnDelete.setText(getActivity().getResources().getString(R.string.txtDeleteCaps));
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        llParent=(LinearLayout) view.findViewById(R.id.llParent);

        llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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

        if(MainBaseActivity.objTileEdit!=null)
        {
            btnDelete.setVisibility(View.VISIBLE);
        }
        else{
            btnDelete.setVisibility(View.GONE);
        }
        tvIsMobile.setText(getActivity().getResources().getString(R.string.txtMobileNumber));
        tvIsdCodeFlag.setText(getActivity().getResources().getString(R.string.txtDialingPrefix));

        if(MainBaseActivity.objTileEdit!=null)
        {
            tvStep.setVisibility(View.VISIBLE);
            tvStep.setText("Change Chat Button");  //getActivity().getResources().getString(R.string.txtChangeButton)
            tvStep.setAllCaps(true);
//            tvCreateContact.setVisibility(View.GONE);
        }
        else if(MainBaseActivity.objTileEdit==null){
            tvStep.setText(getActivity().getResources().getString(R.string.txtStep2));
//            tvCreateContact.setVisibility(View.VISIBLE);
//            tvCreateContact.setAllCaps(true);
            tvStep.setVisibility(View.GONE);
        }
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
//        CustomFonts.setFontOfTextView(getActivity(),tvCreateContact, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(),etContactName, "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(),etContactNumber, "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(),etISDCode, "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(),etCountryCode, "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnNext, "fonts/Roboto-Regular_1.ttf");
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        imViewUserImage.setOnClickListener(this);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    /**
     * Method to set the values to be displayed on the screen
     */
    @SuppressWarnings("unused")
    private void setValues()
    {
        // In case of User is Registered
        if(tileDetailsRegisteredUser!=null && !tileDetailsRegisteredUser.trim().equals(""))
        {
            //Toast.makeText(getActivity(), "In case of User is Registered",1000).show();
            //countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser;
            if(isFirstTime)
            {
                String []arrayUserDetails=tileDetailsRegisteredUser.split(",");
                countryCodeRegisteredUser=arrayUserDetails[0];
                numberRegisteredUser=arrayUserDetails[1];
                isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
                isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
                isdCodeRegisteredUser=arrayUserDetails[4];
                isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);

                MainBaseActivity.contactNumberForTile=numberRegisteredUser;

                if(isMobileRegisteredUser)

                    MainBaseActivity.isMobileChecked=isMobiles;
                else
                    MainBaseActivity.isMobileChecked=isMobiles;

                if(isdCodeFlagRegisteredUser)
                    MainBaseActivity.isIsdCodeFlagChecked=true;
                else
                    MainBaseActivity.isIsdCodeFlagChecked=false;

                if(originalNumber.startsWith("+")){
                    MainBaseActivity.selectedPrefixCodeForTileDetails="+";
                }
                else {
                    tagloop:for(int i = 0; i< GlobalCommonValues.listIDDCodes.size(); i++)
                    {
                        try {
                            if(originalNumber.startsWith(GlobalCommonValues.listIDDCodes.get(i)))
                            {
                                MainBaseActivity.selectedPrefixCodeForTileDetails=GlobalCommonValues.listIDDCodes.get(i);
                                break tagloop;
                            }
                        }catch(Exception e)
                        {
                            e.getMessage();
                        }
                    }
                }

                if(MainBaseActivity.selectedPrefixCodeForTileDetails!=null && !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(""))
                {
                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);
                    chkBoxIsdCodeFlag.setChecked(true);
                }
                if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
                {
                    SpannableString sp = new SpannableString(MainBaseActivity.selectedCountryCodeForTileDetails);
                    etContactNumber.setText(sp);
                }
                //MainBaseActivity.selectedPrefixCodeForTileDetails=isdCodeRegisteredUser;
                MainBaseActivity.selectedCountryCodeForTileDetails=countryCodeRegisteredUser;//--isdCodeRegisteredUser;
                MainBaseActivity.contactNumberForTile=numberRegisteredUser;//-----originalNumber;
                String strNumberToCheck=originalNumber;//GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(originalNumber);

            }

            if(MainBaseActivity.selectedCountryCodeForTileDetails.contains("+"))
            {
                MainBaseActivity.selectedCountryCodeForTileDetails=MainBaseActivity.selectedCountryCodeForTileDetails.substring(1,MainBaseActivity.selectedCountryCodeForTileDetails.length());
            }
            else {
                tagloop:for(int i=0;i<GlobalCommonValues.listCountryCodes.size();i++)
                {
                    try {
                        if(MainBaseActivity.selectedCountryCodeForTileDetails.startsWith(GlobalCommonValues.listCountryCodes.get(i)))
                        {
                            MainBaseActivity.selectedCountryCodeForTileDetails=GlobalCommonValues.listCountryCodes.get(i);
                            break tagloop;
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }          // In case of User is Not Registered
        else if(tileDetailsNonRegisteredUser!=null && !tileDetailsNonRegisteredUser.trim().equals(""))
        {
            if(MainBaseActivity.objTileEdit!=null){
                String mName = "",mNumber = "",mCountryCode = "",mPrefix = "";
                boolean mIsMobile=false;

                if(MainBaseActivity.objTileEdit.getPrefix()!=null)
                    mPrefix = MainBaseActivity.objTileEdit.getPrefix();

                if(MainBaseActivity.objTileEdit.getCountryCode()!=null)
                    mCountryCode = MainBaseActivity.objTileEdit.getCountryCode();

                if(MainBaseActivity.objTileEdit.getPhoneNumber()!=null)
                    mNumber = MainBaseActivity.objTileEdit.getPhoneNumber();

                if(MainBaseActivity.objTileEdit.getName()!=null)
                    mName = MainBaseActivity.objTileEdit.getName();

                try {
                    mIsMobile = MainBaseActivity.objTileEdit.isIsMobile();
                } catch (Exception e) {
                    e.getMessage();
                }

                if(!mName.trim().equalsIgnoreCase(""))
                    etContactName.setText(mName);

                if(!mCountryCode.trim().equalsIgnoreCase(""))
                    etCountryCode.setText(mCountryCode);

                if(!mNumber.trim().equalsIgnoreCase(""))
                    etContactNumber.setText(mNumber);

                if(mIsMobile){
                    chkBoxIsMobile.setChecked(true);
                }else {
                    chkBoxIsMobile.setChecked(false);
                }

                if(!mPrefix.trim().equalsIgnoreCase("")){
                    etISDCode.setText(mPrefix);
                    chkBoxIsdCodeFlag.setChecked(true);
                    etISDCode.setEnabled(true);
                    etISDCode.setAlpha(1.0f);
                }else if(mPrefix.trim().equalsIgnoreCase("")){
                    chkBoxIsdCodeFlag.setChecked(false);
                    etISDCode.setEnabled(false);
                    etISDCode.setAlpha(0.5f);
                }

            }else{

                //				Toast.makeText(getActivity(), "In case of User is Not Registered",1000).show();
                if(saveState.isRegistered(mActivity))
                {
                    MainBaseActivity.contactNumberForTile=originalNumber;
                    //				try{
                    //					MainBaseActivity.contactNumberForTile=GlobalConfig_Methods.getBBNumberToCheck(mActivity, originalNumber).split(",")[1];
                    //				}catch(Exception e){
                    //					MainBaseActivity.contactNumberForTile=originalNumber;
                    //				}
                    String strNumberToCheck=originalNumber;
                    //GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay();  commented on 19-9-2015

                    if(strNumberToCheck.trim().length()<10)
                    {
                        if(isFirstTime)
                        {
                            MainBaseActivity.contactNumberForTile=strNumberToCheck;
                            MainBaseActivity.selectedCountryCodeForTileDetails="";
                            MainBaseActivity.selectedPrefixCodeForTileDetails="";
                            MainBaseActivity.isMobileChecked=isMobiles;
                            MainBaseActivity.isIsdCodeFlagChecked=false;
                        }
                    }
                    else if(strNumberToCheck.trim().length()==10)
                    {
                        if(isFirstTime)
                        {
                            String mPrefix = "",mCountryCode = "",mNumber = strNumberToCheck;
                            if(MainBaseActivity.objTileEdit!=null){

                                if(MainBaseActivity.objTileEdit.getPrefix()!=null && !MainBaseActivity.objTileEdit.getPrefix().trim().equals("")){
                                    mPrefix = MainBaseActivity.objTileEdit.getPrefix();
                                }

                                if(MainBaseActivity.objTileEdit.getCountryCode()!=null && !MainBaseActivity.objTileEdit.getCountryCode().trim().equals("")){
                                    mCountryCode = MainBaseActivity.objTileEdit.getCountryCode();
                                }

                                if(MainBaseActivity.objTileEdit.getPhoneNumber()!=null && !MainBaseActivity.objTileEdit.getPhoneNumber().trim().equals("")){
                                    mNumber = MainBaseActivity.objTileEdit.getPhoneNumber();
                                }
                            }

                            MainBaseActivity.isMobileChecked=isMobiles;
                            MainBaseActivity.isIsdCodeFlagChecked=false;
                            MainBaseActivity.contactNumberForTile=mNumber;
                            MainBaseActivity.selectedPrefixCodeForTileDetails=mPrefix;
                            MainBaseActivity.selectedCountryCodeForTileDetails=mCountryCode;
                        }
                    }
                    else if(strNumberToCheck.trim().length()>10)
                    {
                        if(isFirstTime)
                        {
                            String mNumberFormatted = GlobalConfig_Methods.getBBNumberToCheck(mActivity, tileDetailsNonRegisteredUser);
                            String[] numberArray = mNumberFormatted.split(",");
                            isMobiles = Boolean.parseBoolean(numberArray[2]);
                            MainBaseActivity.isMobileChecked=isMobiles;
                            MainBaseActivity.isIsdCodeFlagChecked=true;
                            //Made Changes below in next 6 lines
                            isTncUserRegisteredUser = Boolean.parseBoolean(numberArray[5]);
                            if(numberArray[0]!=null && !numberArray[0].trim().equals("")){
                                MainBaseActivity.selectedCountryCodeForTileDetails=numberArray[0];
                            }

                            if(numberArray[1]!=null && !numberArray[1].trim().equals("")){
                                MainBaseActivity.contactNumberForTile=numberArray[1];
                            }

                            if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))// && !CreateContactFragment.isdCodeNumberCreateContact.equals(""))
                            {
                                MainBaseActivity.isIsdCodeFlagChecked=true;
                            }


                            if(originalNumber.startsWith("+")){
                                {
                                    MainBaseActivity.selectedPrefixCodeForTileDetails="+";
                                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);
                                    chkBoxIsdCodeFlag.setChecked(true);
                                }
                            }
                            else {
                                boolean isFound=false;
                                tagloop:for(int i=0;i<GlobalCommonValues.listIDDCodes.size();i++)
                                {
                                    try {
                                        if(GlobalCommonValues.listIDDCodes.get(i)!=null)
                                        {
                                            if(originalNumber.startsWith(GlobalCommonValues.listIDDCodes.get(i))){
                                                MainBaseActivity.selectedPrefixCodeForTileDetails=GlobalCommonValues.listIDDCodes.get(i);
                                                isFound=true;
                                                break tagloop;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }

                                if(isFound){
                                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);
                                    chkBoxIsdCodeFlag.setChecked(true);
                                }
                            }

                            etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);
                            if(!etISDCode.getText().toString().trim().equals("")){
                                chkBoxIsdCodeFlag.setChecked(true);
                            }
                            else if(etISDCode.getText().toString().trim().equals("")){
                                chkBoxIsdCodeFlag.setChecked(false);
                            }
                            if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && MainBaseActivity.selectedCountryCodeForTileDetails.contains("+"))
                            {
                                MainBaseActivity.selectedCountryCodeForTileDetails=MainBaseActivity.selectedCountryCodeForTileDetails.substring(1,MainBaseActivity.selectedCountryCodeForTileDetails.length());
                                etCountryCode.setText(MainBaseActivity.selectedCountryCodeForTileDetails);
                            }
                            else {
                                if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals("")){
                                    boolean isFound=false;
                                    try {
                                        tagloop:for(int i=0;i<GlobalCommonValues.listCountryCodes.size();i++)
                                        {
                                            if(GlobalCommonValues.listCountryCodes.get(i)!=null){
                                                if(MainBaseActivity.selectedCountryCodeForTileDetails.startsWith(GlobalCommonValues.listCountryCodes.get(i)))
                                                {
                                                    MainBaseActivity.selectedCountryCodeForTileDetails=GlobalCommonValues.listCountryCodes.get(i);
                                                    isFound=true;
                                                    break tagloop;
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                    if(isFound)
                                    {
                                        etCountryCode.setText(MainBaseActivity.selectedCountryCodeForTileDetails);
                                    }
                                }
                            }
                        }
                    }

                }
                else if(!saveState.isRegistered(mActivity))
                {
                    MainBaseActivity.isMobileChecked=isMobiles;
                    MainBaseActivity.isIsdCodeFlagChecked=false;
                    MainBaseActivity.contactNumberForTile=originalNumber;
                    MainBaseActivity.selectedPrefixCodeForTileDetails="";
                    MainBaseActivity.selectedCountryCodeForTileDetails="";
                }
            }
        }

        //Check For TnCUser && IsMobile
        if(saveState.isRegistered(mActivity))
        {
            if(!isTncUserRegisteredUser)
            {
                String _prefix="",_countryCode="",displayNumber="";

                if(MainBaseActivity.selectedPrefixCodeForTileDetails!=null && !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(""))
                    _prefix=MainBaseActivity.selectedPrefixCodeForTileDetails;

                if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
                    _countryCode=MainBaseActivity.selectedCountryCodeForTileDetails;

                if(MainBaseActivity.contactNumberForTile!=null && !MainBaseActivity.contactNumberForTile.trim().equals(""))
                    displayNumber=MainBaseActivity.contactNumberForTile;

                String mNumberWithIsdCode=_prefix +_countryCode + displayNumber;//GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.selectedPrefixCodeForTileDetails+MainBaseActivity.contactNumberForTile);
                BBID=-1;

                if(mNumberWithIsdCode.length()>0 && mNumberWithIsdCode.length()>7){
                    String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";//,
                    //iddCode="";
                    boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,_isTncUserRegisteredUser=false;
                    if(_prefix.equals(DBQuery.getIDDCodeDB(mActivity, saveState.getCountryCode(mActivity)))){
                        String[] arrayNumberDetails=GlobalConfig_Methods.getBBNumberToCheck(mActivity, mNumberWithIsdCode).split(","); // originalNumber
                        countryCodeRegisteredUser=arrayNumberDetails[0];
                        numberRegisteredUser=arrayNumberDetails[1];
                        isMobileRegisteredUser=Boolean.parseBoolean(arrayNumberDetails[2]);
                        isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayNumberDetails[3]);
                        isdCodeRegisteredUser=arrayNumberDetails[4];
                        _isTncUserRegisteredUser=Boolean.parseBoolean(arrayNumberDetails[5]);
                        MainBaseActivity.selectedPrefixCodeForTileDetails = isdCodeRegisteredUser;
                        MainBaseActivity.selectedCountryCodeForTileDetails = countryCodeRegisteredUser;
                        MainBaseActivity.contactNumberForTile = numberRegisteredUser;
                        BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mActivity,numberRegisteredUser,countryCodeRegisteredUser);

                        if(_isTncUserRegisteredUser || BBID>0)
                        {
                            isTncUserRegisteredUser=true;
                            MainBaseActivity.isMobileChecked=isMobiles;
                        }
                    }
                }else if(mNumberWithIsdCode.length()>0 && mNumberWithIsdCode.length()<=7){
                    MainBaseActivity.contactNumberForTile = mNumberWithIsdCode;
                }

                if(BBID>0)
                {
                    MainBaseActivity.isMobileChecked=isMobiles;
                }
            }
        }

        if(saveState.isRegistered(mActivity)){
            if(MainBaseActivity.selectedCountryCodeForTileDetails!=null && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
            {
                if(MainBaseActivity.selectedCountryCodeForTileDetails.startsWith("+"))
                {
                    SpannableString sp = new SpannableString(MainBaseActivity.selectedCountryCodeForTileDetails.substring(1,MainBaseActivity.selectedCountryCodeForTileDetails.length()));
                    etCountryCode.setText(sp);
                }
                else{
                    boolean isFound=false;
                    tagloop:for(int i=0;i<GlobalCommonValues.listCountryCodes.size();i++)
                    {
                        try {
                            if(MainBaseActivity.selectedCountryCodeForTileDetails.startsWith(GlobalCommonValues.listCountryCodes.get(i)))
                            {
                                MainBaseActivity.selectedCountryCodeForTileDetails=GlobalCommonValues.listCountryCodes.get(i);
                                isFound=true;
                                break tagloop;
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    if(isFound)
                    {
                        SpannableString sp = new SpannableString(MainBaseActivity.selectedCountryCodeForTileDetails);
                        etCountryCode.setText(sp);
                    }
                }
            }
        }

        if(etCountryCode.getText().toString().trim().equals(""))
        {
            if(saveState.isRegistered(mActivity))
            {
                etCountryCode.setText(saveState.getCountryCode(mActivity));
            }
        }
        if(tileDetailsRegisteredUser==null && tileDetailsNonRegisteredUser==null)
        {
        }
        else{
            if(MainBaseActivity.objTileEdit==null){
                //				Toast.makeText(getActivity(), "Last else",1000).show();
                etContactName.setText(contactName);
                etContactNumber.setText(GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.contactNumberForTile));
                if(!etCountryCode.getText().toString().trim().equals("")){
                    MainBaseActivity.isMobileChecked=isMobiles;
                }
                if(MainBaseActivity.isMobileChecked)
                    chkBoxIsMobile.setChecked(true);
                else
                    chkBoxIsMobile.setChecked(false);

                if(MainBaseActivity.isIsdCodeFlagChecked)
                {
                    chkBoxIsdCodeFlag.setChecked(true);
                    etISDCode.setEnabled(true);
                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);//(GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.selectedISDCodeForTileDetails));
                }
                else
                {
                    chkBoxIsdCodeFlag.setChecked(false);
                    etISDCode.setEnabled(false);
                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);//(GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.selectedISDCodeForTileDetails));
                    etISDCode.setAlpha(.5f);
                }
            }
        }
        if(etISDCode.getText().toString().trim().equals(""))
        {
            chkBoxIsdCodeFlag.setChecked(false);
            etISDCode.setEnabled(false);
            etISDCode.setAlpha(.5f);
        }
        else if(!etISDCode.getText().toString().trim().equals("")){
            chkBoxIsdCodeFlag.setChecked(true);
            etISDCode.setEnabled(true);
            etISDCode.setAlpha(1.0f);
        }

        //		etISDCode.setText(IsdCode.trim());
        chkBoxIsMobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    MainBaseActivity.isMobileChecked=true;
                }
                else{
                    MainBaseActivity.isMobileChecked=false;
                }
            }
        });

        chkBoxIsdCodeFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    chkBoxIsdCodeFlag.setChecked(true);
                    etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);//(GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.selectedISDCodeForTileDetails));
                    etISDCode.setEnabled(true);
                    etISDCode.setAlpha(1.0f);
                    MainBaseActivity.isIsdCodeFlagChecked=true;
                }
                else
                {
                    chkBoxIsdCodeFlag.setChecked(false);
                    etISDCode.setEnabled(false);
                    etISDCode.setAlpha(.5f);
                    MainBaseActivity.isIsdCodeFlagChecked=false;
                    //					etISDCode.setText("");
                }
            }
        });
        if(!isCountryCodeShow){
            etCountryCode.setText("");
        }
    }

    @Override
    public void onPause() {
        GlobalConfig_Methods.hideKeyBoard(mActivity, etContactName);
        GlobalConfig_Methods.hideKeyBoard(mActivity, etContactNumber);
        GlobalConfig_Methods.hideKeyBoard(mActivity, etISDCode);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //		ContactDetailsFragment.isdCodeNumber="";
        //		CreateContactFragment.isdCodeNumberCreateContact="";
        MainBaseActivity._bitmap = null;
        MainBaseActivity.isContactCreated=false;
        if(iActionIsFromHomeScreen!=null && !isButtonClicked)
        {
            iActionIsFromHomeScreen.setAction("refresh");
        }
        isButtonClicked=true;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setValues();
        isButtonClicked=false;
        if(!isFirstTime)
        {
            SpannableString sp = new SpannableString(MainBaseActivity.contactNumberForTile);
            etContactNumber.setText(sp);
            etISDCode.setText(MainBaseActivity.selectedPrefixCodeForTileDetails);
            if(MainBaseActivity.isMobileChecked)
            {
                chkBoxIsMobile.setChecked(true);
            }
            else{
                chkBoxIsMobile.setChecked(false);
            }
            if(MainBaseActivity.isIsdCodeFlagChecked)
            {
                chkBoxIsdCodeFlag.setChecked(true);
                etISDCode.setAlpha(1.0f);
            }
            else{
                chkBoxIsdCodeFlag.setChecked(false);
                etISDCode.setAlpha(0.5f);
            }
        }


        if(MainBaseActivity.objTileEdit!=null && MainBaseActivity.objTileEdit.getCountryCode()!=null &&
                !MainBaseActivity.objTileEdit.getCountryCode().trim().equalsIgnoreCase("")){
            SpannableString sp = new SpannableString(MainBaseActivity.objTileEdit.getCountryCode());
            etCountryCode.setText(sp);
        }


        // Set Image on ImageView
        if (MainBaseActivity.isCameraCanceled) {
            MainBaseActivity._bitmap = null;
            MainBaseActivity.isImageSelected = false;
            MainBaseActivity.isImageRequested = false;
            if (mActivity instanceof MainBaseActivity)
                ((MainBaseActivity) mActivity).objFragment = null;
            else if (mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity) mActivity).objFragment = null;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri =GlobalConfig_Methods.getOutputImageUri();
            if (fileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            MainBaseActivity.isCameraCanceled = false;
        }
        if (MainBaseActivity._bitmap != null) {
            MainBaseActivity._bitmap = GlobalConfig_Methods.getResizedBitmap(
                    MainBaseActivity._bitmap, 250, 250);
            imViewUserImage.setImageBitmap(MainBaseActivity._bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                try {
                    Uri selectedImage = fileUri;
                    picturePath = selectedImage.getPath();
                    MainBaseActivity.selectedImagepath = picturePath;
                    cropImage(true);
                } catch (Exception e) {
                    e.getMessage();
                }
            }else if(requestCode==PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                @SuppressWarnings("unused")
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                MainBaseActivity.selectedImagepath=picturePath;
                Intent cropIntent=new Intent(getActivity(),CropImage.class);
                cropIntent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
                cropIntent.putExtra(CropImage.SCALE, true);
                cropIntent.putExtra(CropImage.ASPECT_X, 1);
                cropIntent.putExtra(CropImage.ASPECT_Y, 1);
                getActivity().startActivity(cropIntent);
            }
        }
    }

    /**
     * interface to handle call to delete the tile
     */
    INotifyGalleryDialog iNotifyDelete=new INotifyGalleryDialog() {

        @Override
        public void yes() {
            isButtonClicked=true;
            // In case of User is Tile Deletion Successful
            int count=-1;
            count=DBQuery.deleteTile(mActivity,MainBaseActivity.objTileEdit.getPrefix(),MainBaseActivity.objTileEdit.getPhoneNumber(),Integer.parseInt(MainBaseActivity.objTileEdit.getTilePosition()));
            if(count>0)
            {
                TileDeleteSuccessDialog dialogTiledeleteSuccess=new TileDeleteSuccessDialog();
                if(mActivity instanceof MainBaseActivity)
                {
                    dialogTiledeleteSuccess.newInstance("",((MainBaseActivity)mActivity),"Contact Button Deleted Successfully","",true);
                }
                else if(mActivity instanceof HomeScreenActivity)
                {
                    dialogTiledeleteSuccess.newInstance("",((HomeScreenActivity)mActivity),"Contact Button Deleted Successfully","",true);
                }
                dialogTiledeleteSuccess.setCancelable(false);
                dialogTiledeleteSuccess.show(getFragmentManager(), "test");
            }
            else{  // In case of User is Tile Deletion Error Occurence
                ImageRequestDialog dialogTileDelete=new ImageRequestDialog();
                dialogTileDelete.newInstance("",mActivity,"Chat Button delete error occured","",null);
                dialogTileDelete.setCancelable(false);
                dialogTileDelete.show(getChildFragmentManager(), "test");
            }
        }

        @Override
        public void no() {

        }
    };

    /**
     * Method to create new contact
     */
    private void createContact()
    {

        isFirstTime=false;
        ImageRequestDialog dialogAlert;
        //In case of length 0-2
        if((chkBoxIsdCodeFlag.isChecked()) && ((etContactNumber.getText().toString().trim().length()+etISDCode.getText().toString().trim().length())<=2))
        {
            dialogAlert=new ImageRequestDialog();
            dialogAlert.newInstance("",mActivity,"Invalid Phone Number","",null);
            dialogAlert.show(getChildFragmentManager(), "test");
        }
        else if((!chkBoxIsdCodeFlag.isChecked()) && (etContactNumber.getText().toString().trim().length()<=2))
        {
            dialogAlert=new ImageRequestDialog();
            dialogAlert.newInstance("",mActivity,"Invalid Phone Number","",null);
            dialogAlert.show(getChildFragmentManager(), "test");
        }
        else{
            TileDetailBean objTileDetailBean=new TileDetailBean();
            MainBaseActivity.contactNameForTile=etContactName.getText().toString();
            MainBaseActivity.contactNumberForTile=etContactNumber.getText().toString();//GlobalConfig_Methods.trimSpecialCharactersFromString(etContactNumber.getText().toString());
            MainBaseActivity.selectedCountryCodeForTileDetails=etCountryCode.getText().toString();
            MainBaseActivity.selectedPrefixCodeForTileDetails=etISDCode.getText().toString();

            objTileDetailBean.Name=etContactName.getText().toString();
            objTileDetailBean.Phone=etContactNumber.getText().toString();

            //For Tile Edit Object Values to be make updated
            if(MainBaseActivity.objTileEdit!=null){
                MainBaseActivity.objTileEdit.setName(etContactName.getText().toString());
                MainBaseActivity.objTileEdit.setPrefix(etISDCode.getText().toString());
                MainBaseActivity.objTileEdit.setCountryCode(etCountryCode.getText().toString());
                MainBaseActivity.objTileEdit.setPhoneNumber(etContactNumber.getText().toString());
            }

            if(chkBoxIsMobile.isChecked())
            {
                objTileDetailBean.Is_Mobile=true;
                MainBaseActivity.isMobileChecked=true;
                isCountryCodeShow = true;
                //For Tile Edit Object Values to be make updated
                if(MainBaseActivity.objTileEdit!=null){
                    MainBaseActivity.objTileEdit.setIsMobile(true);
                }
            }
            else
            {
                objTileDetailBean.Is_Mobile=false;
                MainBaseActivity.isMobileChecked=false;
                isCountryCodeShow = false;
                //For Tile Edit Object Values to be make updated
                if(MainBaseActivity.objTileEdit!=null){
                    MainBaseActivity.objTileEdit.setIsMobile(true);
                }
            }

            if(chkBoxIsdCodeFlag.isChecked())
            {
                objTileDetailBean.International_Access_Code_Flag=true;
                objTileDetailBean.International_Access_Code=etISDCode.getText().toString();
                // Single line change done
                objTileDetailBean.Phone=etCountryCode.getText().toString()+etContactNumber.getText().toString();

                MainBaseActivity.isIsdCodeFlagChecked=true;
                originalNumber=etISDCode.getText().toString()+etCountryCode.getText().toString()+etContactNumber.getText().toString();
            }else{
                objTileDetailBean.International_Access_Code_Flag=false;
                objTileDetailBean.International_Access_Code="";
                MainBaseActivity.isIsdCodeFlagChecked=false;
                originalNumber = etContactNumber.getText().toString();
                //commented as a part of solving feedback sheet issue
                //originalNumber=etCountryCode.getText().toString()+etContactNumber.getText().toString();
            }
            MainBaseActivity.objTileDetailBeanStatic=objTileDetailBean;

            ChooseImageFragment objChooseImagefragment=new ChooseImageFragment();
            if(mActivity instanceof MainBaseActivity)
            {
                objChooseImagefragment.newInstance(((MainBaseActivity)mActivity));
                ((MainBaseActivity) mActivity).setFragment(objChooseImagefragment);
            }
            else if(mActivity instanceof HomeScreenActivity)
            {
                objChooseImagefragment.newInstance(((HomeScreenActivity)mActivity));
                ((HomeScreenActivity) mActivity).setFragment(objChooseImagefragment);
            }
        }
    }

    /**
     * Interface to handle events of image selection
     */
    INotifyGalleryDialog iObject = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // In case of Camera option is selected
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = GlobalConfig_Methods.getOutputImageUri();
            if (fileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }

        @Override
        public void no() {
            // In case of Gallery option is selected
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(
                    Intent.createChooser(intent, "Complete action using"),
                    PICK_IMAGE_REQUEST);
        }
    };


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnBack)
        {
            if(mActivity instanceof MainBaseActivity)
                ((MainBaseActivity)mActivity).fragmentManager.popBackStack();
            else if(mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
        }
        else if(v.getId()==R.id.btnNext)
        {
            isButtonClicked=true;
            if(!chkBoxIsdCodeFlag.isChecked())
            {
                if(etContactName.getText().toString().trim().equals("") && etContactNumber.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following fields:\ncontact name,contact number","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else if(etContactName.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact name","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else if(etContactNumber.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact number","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else{
                    createContact();
                }
            }
            else if(chkBoxIsdCodeFlag.isChecked())
            {
                if(etContactName.getText().toString().trim().equals("") && etContactNumber.getText().toString().trim().equals("") && etISDCode.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following fields:\ncontact name,contact number,Dialing Prefix","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else if(etContactName.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact name","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else if(etContactNumber.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact number","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else if(etISDCode.getText().toString().trim().equals(""))
                {
                    ImageRequestDialog dialogAlert=new ImageRequestDialog();
                    dialogAlert.setCancelable(false);
                    dialogAlert.newInstance("",mActivity,"Please enter following field:\nDialing Prefix","",null);
                    dialogAlert.show(getChildFragmentManager(), "test");
                }
                else{
                    createContact();
                }
            }
        }
        else if(v.getId()==R.id.btnDelete)
        {
            MessageDeleteConfirmation dialogConfirmation=new MessageDeleteConfirmation();
            dialogConfirmation.newInstance("",mActivity,"Are you sure you want to delete this chat button","",iNotifyDelete);
            dialogConfirmation.setCancelable(false);
            dialogConfirmation.show(getChildFragmentManager(), "test");
        }
        else if(v.getId()==R.id.btnHome)
        {
            isButtonClicked=true;
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
        }else if(v.getId() == R.id.imViewUserImage){
            // open image select popup

            SelectImagePopup dialogSelectimage = new SelectImagePopup();
//            dialogSelectimage.setCancelable(false);
            if (mActivity instanceof MainBaseActivity) {
                dialogSelectimage.newInstance("Choose Image",
                        ((MainBaseActivity) mActivity), iObject,this);
//                dialogSelectimage.show(((MainBaseActivity) mActivity)
//                        .getSupportFragmentManager(), "test");
            } else if (mActivity instanceof HomeScreenActivity) {
                dialogSelectimage.newInstance("Choose Image",
                        ((HomeScreenActivity) mActivity), iObject,this);
//                dialogSelectimage.show(((HomeScreenActivity) mActivity)
//                        .getSupportFragmentManager(), "test");
            }
        }
    }

    /**
     * Method to perform image cropping
     */

    private void cropImage(boolean isCamera) {
        if (!MainBaseActivity.selectedImagepath.equals("")) {
            MainBaseActivity._bitmap = null;
            Intent intent = new Intent(getActivity(), CropImage.class);
            if (isCamera)
                intent.putExtra(CropImage.IMAGE_PATH, picturePath);
            else
                intent.putExtra(CropImage.IMAGE_PATH,
                        MainBaseActivity.selectedImagepath);
            intent.putExtra(CropImage.SCALE, true);
            intent.putExtra(CropImage.ASPECT_X, 1);
            intent.putExtra(CropImage.ASPECT_Y, 1);
            getActivity().startActivityForResult(intent,
                    REQUEST_CODE_CROP_IMAGE);
        }
    }
}
