package com.tnc.fragments;

import java.io.File;
import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.UserPhoneNumbersAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.PhoneNumberSMSSelectDialog;
import com.tnc.dialog.PhoneNumbersDialog;
import com.tnc.dialog.SMSSendConfirmationDialog_ContactDetailsScreen;
import com.tnc.dialog.SelectInviteOptionDialog;
import com.tnc.dialog.SelectUserNumberDialog;
import com.tnc.dialog.SendMessagePopupDialog;
import com.tnc.dialog.SendSMSFullScreenDialog_ContactDetails;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * class to display selected contact details /create new contact 
 *  @author a3logics
 */
public class ContactDetailsScreenFragment extends BaseFragmentTabs implements
        OnClickListener {

    private LinearLayout llParent;
    private ScrollView svContents;
    private FrameLayout flBackArrow,flInformationButton;
    private Button btnBack,btnHome,btnPlaceCall,btnCreateButton,btnInvite;
    private ContactDetailsBean selectedContactBean;
    private TextView tvTitle,tvContactName,tvContactEmail;
    private EditText etContactEmail;
    private ImageView imViewUserImage;
    private ListView lvNumbers;
    private UserPhoneNumbersAdapter adapterUserPhoneNumber;
    private String logoPath="";
    private String logoName="";

    public ContactDetailsScreenFragment newInstance(ContactDetailsBean selectedContactBean)
    {
        ContactDetailsScreenFragment frag = new ContactDetailsScreenFragment();
        this.selectedContactBean=selectedContactBean;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contactdetailscreenfragment, container,false);
        idInitialization(view);
        return view;
    }

    /*
     * Initialization of widgets/views
     * */
    private void idInitialization(View view) {
        saveState=new SharedPreference();
        llParent=(LinearLayout) view.findViewById(R.id.llParent);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        btnPlaceCall=(Button) view.findViewById(R.id.btnPlaceCall);
        btnCreateButton=(Button) view.findViewById(R.id.btnCreateButton);
        btnInvite=(Button) view.findViewById(R.id.btnInvite);
        lvNumbers=(ListView)view.findViewById(R.id.lvNumbers);
        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
        svContents=(ScrollView) view.findViewById(R.id.svContents);
        tvContactEmail=(TextView)view.findViewById(R.id.tvContactEmail);
        tvContactName=(TextView) view.findViewById(R.id.tvContactName);
        etContactEmail=(EditText) view.findViewById(R.id.etContactEmail);
        imViewUserImage=(ImageView) view.findViewById(R.id.imViewUserImage);
        CustomFonts.setFontOfTextView(mActivityTabs,tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(mActivityTabs,tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(mActivityTabs,tvContactName, "fonts/Roboto-Bold_1.ttf");

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnPlaceCall.setOnClickListener(this);
        btnCreateButton.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        btnInvite.setEnabled(false);
        btnInvite.setAlpha(0.5f);
        setValues();
        etContactEmail.setEnabled(false);
        lvNumbers.setOnTouchListener(new OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvNumbers.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View viewChild, int position,
                                    long arg3) {

                if(selectedContactBean.get_phone()!=null && selectedContactBean.get_phone().size()>0){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String num = selectedContactBean.get_phone().get(position).trim();
                    callIntent.setData(Uri.parse("tel:" + num.trim()));
                    //update boolean value in preference as number is dialled from the app
                    if(saveState == null){
                        saveState = new SharedPreference();
                    }
                    saveState.setIS_NUMBER_DIALLED(getActivity(), true);
                    startActivity(callIntent);
                }

            }
        });

        llParent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * set the values for the contents
     */
    private void setValues()
    {
        if(selectedContactBean.get_imgpeople()!=null)
        {
            imViewUserImage.setImageBitmap(selectedContactBean.get_imgpeople());
        }
        if(selectedContactBean.get_name()!=null)
        {
            tvContactName.setText(selectedContactBean.get_name());
        }
        if(selectedContactBean.get_emailid()!=null)
        {
            etContactEmail.setText(selectedContactBean.get_emailid());
        }
        if(selectedContactBean.get_phone()!=null && selectedContactBean.get_phone().size()>0)
        {
            adapterUserPhoneNumber=new UserPhoneNumbersAdapter(mActivityTabs,selectedContactBean,selectedContactBean.get_phone(),iAction,iNotifyInvite);
            lvNumbers.setAdapter(adapterUserPhoneNumber);
        }
    }

    @SuppressWarnings("unused")
    public void onResume() {
        super.onResume();
        boolean isTncContactsExists=false;
        //functionality to make invite button enable/disable
        if(saveState.isRegistered(mActivityTabs))
        {
            for(int j=0;j< adapterUserPhoneNumber.listNumbers.size();j++)
            {
                if (!adapterUserPhoneNumber.listBBContacts.isEmpty()) {
                    if (adapterUserPhoneNumber.listNumbers != null) {
                        String contactNumber="";
                        String strPhone = adapterUserPhoneNumber.listNumbers.get(j);
                        try {
                            contactNumber=	strPhone;
                        } catch (Exception e) {
                            e.getMessage();
                        }

                        String strNumber=GlobalConfig_Methods.getBBNumberToCheck(mActivityTabs, contactNumber);
                        String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
                        boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
                        String []arrayUserDetails=strNumber.split(",");
                        countryCodeRegisteredUser=arrayUserDetails[0];
                        numberRegisteredUser=arrayUserDetails[1];
                        isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
                        isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
                        isdCodeRegisteredUser=arrayUserDetails[4];
                        isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);

                        if(isTncUserRegisteredUser)
                        {
                            isTncContactsExists=true;
                        }
                        else{
                        }
                    }
                }
            }
            if(isTncContactsExists)
            {
                // In case of disable invite button if tnc user exists
                if(iNotifyInvite!=null)
                    iNotifyInvite.yes();
            }
            else{
                // In case of enable invite button if no tnc user exists
                if(iNotifyInvite!=null)
                    iNotifyInvite.no();
            }
        } // In case of enable invite button if user is not registered
        else if(!saveState.isRegistered(mActivityTabs))
        {
            if(iNotifyInvite!=null)
                iNotifyInvite.no();
        }
    }

    /**
     *interface to enable/disable invite button option ,in case ,tile button exists/not exists
     *for the user
     */
    INotifyAction iAction=new INotifyAction() {
        @Override
        public void setAction(String action) {
            if(action.equalsIgnoreCase("disable"))
            {
                btnCreateButton.setEnabled(false);
                btnCreateButton.setAlpha(0.5f);
            }
            else{
                btnCreateButton.setEnabled(true);
                btnCreateButton.setAlpha(1.0f);
            }
        }
    };

    /**
     *interface to enable/disable invite button option ,in case ,tnc user exists/not exists
     */
    INotifyGalleryDialog iNotifyInvite=new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // In case of disable invite button if tnc user exists
            btnInvite.setEnabled(false);
            btnInvite.setAlpha(0.5f);
        }

        @Override
        public void no() {
            // In case of enable invite button if no tnc user exists
            btnInvite.setEnabled(true);
            btnInvite.setAlpha(1.0f);
        }
    };

    /**
     *interface to invite user(via email/sms),in case of he/she is non tnc user
     */
    INotifyGalleryDialog iNotifyInvitationOption=new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // In case of user selected to send Email
            Uri URI=null;
            String message="",
                    email=etContactEmail.getText().toString().trim();
            URI=Uri.fromFile(new File(logoPath+logoName+".jpg"));
//			String privacyStatements="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
            message=String.valueOf(Html.fromHtml(selectedContactBean.get_name()+","+"<br><br>I would like to connect with you on " + getResources().getString(R.string.app_name) +". You can view more information about"+
                    " this App and download it on your smartphone at chatstasy.com.<br>" + "<br><br>"+Uri.decode(saveState.getUserName(mActivityTabs))));

            final Intent emailIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");//plain/text
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] { email });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    getResources().getString(R.string.app_name));
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent
                    .putExtra(android.content.Intent.EXTRA_TEXT, message);
            mActivityTabs.startActivity(Intent.createChooser(emailIntent,
                    "Select email provider..."));
        }

        @Override
        public void no() {
            // In case of user selected to send SMS
            if(adapterUserPhoneNumber.listNumbers.size()==1)
            {
                sendSMS(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(adapterUserPhoneNumber.listNumbers.get(0)));
            }
            else if(adapterUserPhoneNumber.listNumbers.size()>1)
            {
                //Open Phonenumbers dialog to select from a list of available numbers
                PhoneNumberSMSSelectDialog dialogPhoneNumbersDisplay=new PhoneNumberSMSSelectDialog();
                dialogPhoneNumbersDisplay.newInstance(mActivityTabs, selectedContactBean.get_phone(),iActionNumberSelectForSMS);
                dialogPhoneNumbersDisplay.show(getChildFragmentManager(), "test");
            }
        }
    };

    /**
     *send SMS to the specified Number
     */
    private void sendSMS(String phoneNumber) {
//		String message = getResources().getString(R.string.app_name) +
//				"\nwould like to use your text messaging service.You may incur text messaging charges from your carrier.";
//		String messageSub = "Would you like to Continue?";
//		String number=phoneNumber;
//		SMSSendConfirmationDialog_ContactDetailsScreen imageGalleryDialog = new SMSSendConfirmationDialog_ContactDetailsScreen();
//		imageGalleryDialog.newInstance("", mActivityTabs, message, messageSub,
//				number,selectedContactBean.get_name());
//		imageGalleryDialog.setCancelable(false);
//		imageGalleryDialog.show(getChildFragmentManager(), "test");
        boolean isAirplaneModeON = false;

        try{
            isAirplaneModeON = GlobalConfig_Methods.isAirplaneModeOn(mActivityTabs);
        }catch(Exception e){
            e.getMessage();
        }

        if(isAirplaneModeON){
            GlobalConfig_Methods.showSimErrorDialog(mActivityTabs,
                    "The device is in Airplane mode or has no sim card or outside of cellular service range");
        }else{
            String mSimState = GlobalConfig_Methods.checkSimState(mActivityTabs);
            if(mSimState.equalsIgnoreCase(GlobalCommonValues.SUCCESS)){
                String _message=String.valueOf(Html.fromHtml(selectedContactBean.get_name()+",<br>I would like to connect with you on Chatstasy. You can view more information "
                        +"about this App at chatstasy.com."));

                SendSMSFullScreenDialog_ContactDetails dialog=null;
                if(mActivityTabs instanceof HomeScreenActivity)
                {
                    dialog = new SendSMSFullScreenDialog_ContactDetails();
                    dialog.newInstance((HomeScreenActivity)mActivityTabs, _message,phoneNumber);
                    ((HomeScreenActivity)mActivityTabs).setFragment(dialog);
                }
            }else{
                GlobalConfig_Methods.showSimErrorDialog(mActivityTabs, mSimState);
            }
        }
    }

    /**
     *interface to send sms to the selected number,in case multiple number exists for the user
     */
    INotifyAction iActionNumberSelectForSMS=new INotifyAction() {
        @Override
        public void setAction(String action) {
            sendSMS(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(action));
        }
    };

    /**
     *interface to send contact details to the create contact screen to create contact tile
     */
    INotifyAction iNotifyselectedNumber=new INotifyAction() {
        @Override
        public void setAction(String action) {
            String contact_Number=GlobalConfig_Methods.trimSpecialCharactersFromString(action);
            String name=selectedContactBean.get_name();
            Bitmap userImage=selectedContactBean.get_imgpeople();
            TileContactDetailsFragment objTileContactDetailsFragment=null;

            if(userImage!=null)
                MainBaseActivity._bitmapContact=userImage;

            if(contact_Number.length()<=2)
            {
                ImageRequestDialog dialog = new ImageRequestDialog();
                dialog.setCancelable(false);
                dialog.newInstance("", mActivityTabs,"Invalid Phone Number","",null);
                dialog.show(getChildFragmentManager(), "test");
            }
            else if(contact_Number.length()>=3 && contact_Number.length()<=9)
            {
                objTileContactDetailsFragment=new TileContactDetailsFragment();
                objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,null,contact_Number,name,
                        contact_Number,null,true);
                ((HomeScreenActivity) mActivityTabs).setFragment(objTileContactDetailsFragment);
            }
            else if(contact_Number.trim().length()>=10)
            {
                objTileContactDetailsFragment=new TileContactDetailsFragment();
                if(saveState.isRegistered(mActivityTabs))
                {
                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,GlobalConfig_Methods.getBBNumberToCheck(mActivityTabs,contact_Number), null,name,
                            contact_Number,null,true);
                }
                else {
                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,null,contact_Number,name,contact_Number,null,true);
                }
                ((HomeScreenActivity)mActivityTabs).setFragment(objTileContactDetailsFragment);
            }
        }
    };

    @SuppressWarnings("unused")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack)
        {
            if(mActivityTabs instanceof HomeScreenActivity)
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
        }
        else if(v.getId()==R.id.btnHome)
        {
            if(mActivityTabs instanceof HomeScreenActivity)
            {
                ((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
                ((HomeScreenActivity)mActivityTabs).finish();
            }
        }  //Make a call to the  number
        else if(v.getId()==R.id.btnPlaceCall)
        {
            if(adapterUserPhoneNumber.listNumbers.size()>1)
            {
                // In case of user has more than one phone number
                PhoneNumbersDialog dialogPhoneNumbersDisplay=new PhoneNumbersDialog();
                dialogPhoneNumbersDisplay.newInstance(mActivityTabs, selectedContactBean.get_phone());
                dialogPhoneNumbersDisplay.show(getChildFragmentManager(), "test");
            }
            else if(adapterUserPhoneNumber.listNumbers.size()==1){
                // In case of user has only one phone number
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+adapterUserPhoneNumber.listNumbers.get(0)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                //update boolean value in preference as number is dialled from the app
                if(saveState == null){
                    saveState = new SharedPreference();
                }
                saveState.setIS_NUMBER_DIALLED(getActivity(), true);
                mActivityTabs.startActivity(intent);
            }
        }   //Create contact tile of the number
        else if(v.getId()==R.id.btnCreateButton)
        {
            ArrayList<String> listNumbers = new ArrayList<String>();
            String numberFilter="";
            String prefix="";
            String phoneNumber="";
            // In case of user has only one phone number
            if(adapterUserPhoneNumber.listNumbers.size()==1)
            {
                boolean isExist = false;
                phoneNumber= adapterUserPhoneNumber.listNumbers.get(0);
                isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), phoneNumber);

                if(isExist)
                {
                    ImageRequestDialog dialog=new ImageRequestDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivityTabs,"Chat button for this contact number already exists","",null);
                    dialog.show(getChildFragmentManager(), "test");
                    //if one number and That too if it's tile exists
                }
                else if(!isExist){
                    if(((BitmapDrawable)(imViewUserImage).getDrawable())!=null && ((BitmapDrawable)(imViewUserImage).getDrawable()).getBitmap()!=null)
                        GlobalCommonValues._Contacimage=((BitmapDrawable)(imViewUserImage).getDrawable()).getBitmap();
                    listNumbers.add(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(adapterUserPhoneNumber.listNumbers.get(0)));
                    iNotifyselectedNumber.setAction(listNumbers.get(0));
                }
            }
            // In case of user has more than one phone number
            else if(adapterUserPhoneNumber.listNumbers.size()>1){
                for(int i=0;i<adapterUserPhoneNumber.listNumbers.size();i++)
                {
                    boolean isExist = false;
                    phoneNumber= adapterUserPhoneNumber.listNumbers.get(i);
                    isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), phoneNumber);
                    if(isExist){
                        ImageRequestDialog dialog=new ImageRequestDialog();
                        dialog.setCancelable(false);
                        dialog.newInstance("", mActivityTabs,"Chat button for this contact number already exists","",null);
                        dialog.show(getChildFragmentManager(), "test");

                    }else if(!isExist){
                        if(((BitmapDrawable)(imViewUserImage).getDrawable())!=null && ((BitmapDrawable)(imViewUserImage).getDrawable()).getBitmap()!=null)
                            GlobalCommonValues._Contacimage=((BitmapDrawable)(imViewUserImage).getDrawable()).getBitmap();
                        listNumbers.add(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(adapterUserPhoneNumber.listNumbers.get(i)));
                    }
                }
                if(listNumbers.size()>0)
                {
                    SelectUserNumberDialog dialogSelectUserNumber=new SelectUserNumberDialog();
                    dialogSelectUserNumber.newInstance(mActivityTabs,listNumbers,iNotifyselectedNumber);
                    dialogSelectUserNumber.show(getChildFragmentManager(), "test");
                }
            }
        }
        else if(v.getId()==R.id.btnInvite)
        {
            //Invite user via Email/SMS,in case ,user is non Tnc User
            try {
                SelectInviteOptionDialog dialogInvitation=new SelectInviteOptionDialog();
                dialogInvitation.setCancelable(false);
                dialogInvitation.newInstance("",mActivityTabs,iNotifyInvitationOption);
                dialogInvitation.show(getChildFragmentManager(), "test");
                logoPath=Environment.getExternalStorageDirectory().toString()+"/"+"TNC/";
                logoName="appicon";
            } catch (Exception e) {
                e.getMessage();
            }
            if(!new File(logoPath+logoName+".jpg").getAbsoluteFile().exists())
            {     //Save App logo to the sd card
                Bitmap _bmp=((BitmapDrawable)(mActivityTabs.getResources().getDrawable(R.drawable.appicon))).getBitmap();
                GlobalConfig_Methods.savebitmap(_bmp,logoPath,logoName);
            }
        }
    }

    @Override
    public void onDestroy() {
        GlobalCommonValues._Contacimage=null;
        super.onDestroy();
    }

}