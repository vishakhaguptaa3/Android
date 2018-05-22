package com.tnc.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.activities.PrivacyPolicyActivity;
import com.tnc.activities.UserLocationDisplayActivity;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactNotificationBean;
import com.tnc.bean.ContactShareStatusUpdate;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.NotificationBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ContactShareAcceptConfirmationDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ShowDialog;
import com.tnc.dialog.UpdateTileConfirmationDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.GoogleMapUtils;
import com.tnc.utility.Logs;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.webresponse.NotificationImageStatusResponse;
import com.tnc.webresponse.NotificationResponseContactShareStatus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to diplay details of the selected notification 
 *  @author a3logics
 */
public class NotificationDetailFragment extends BaseFragmentTabs implements OnClickListener
{
    private RoundedImageViewCircular imViewNotificationContact;
    private TextView tvTitle,tvTime,tvTitleNotification,tvNotificationDetail;
    private Button btnBack,btnHome,btnReply,btnAccept,btnUpdateNumber, btnCreateChatButton;
    private FrameLayout flBackArrow,flInformationButton;
    private String message;
    private String imagePath;
    private String time;
    private ImageLoadTask imageLoadTask=null;
    private int NotificationStatus;
    private String NotificationId="";
    private Gson gson;
    private TransparentProgressDialog progress;
    private String to_user_id="",from_user_id="";
    private ArrayList<BBContactsBean> listBBContacts=null;
    private int BBID=0;
    private ImageLoadTask imageLoader=null;
    private String imageUrl="",notificationType="image";
    //	String tileImage="";
    //	String from_user_id_resposne="";
    private byte[] arrayImage=null;
    private LinearLayout llImageBoundary;
    private String status="",dbLink="";
    private boolean isErrorOccuredWhileDownloadingFile=false;
    private File fileDB,fileSaved;
    private ArrayList<ContactTilesBean> listSharedContactTiles;
    private String notificationStatus="";
    private String db_path;
    private int downloadcount=1;
    private String displayName="";
    private Bitmap displayImage=null;
    private String displayMessage="";
    private boolean isFirstTime=true;
    private INotifyGalleryDialog iNotifyUpdateTile;
    private String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
    private boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
    private ArrayList<ContactTilesBean> listContactTiles=new ArrayList<ContactTilesBean>();
    private String oldNumber="",newNumber="";
    private String countryCodeRegisteredUserOld="",numberRegisteredUserOld="";
    private String countryCodeRegisteredUserNew="",numberRegisteredUserNew="";
    private String countryCodeRegisteredUserDB="",numberRegisteredUserDB="";
    private String from_phone_number = "";
    private int type = -1;
    private String mCountryCodeTileCreate = "", mPhoneNumberTileCreate = "";

    public NotificationDetailFragment newInstance(String message,String imagePath,
                                                  String time,int NotificationStatus,String NotificationId,
                                                  String to_user_id,String from_user_id, String from_phone_number, int type)
    {
        NotificationDetailFragment frag = new NotificationDetailFragment();
        this.message=message;
        this.imagePath=imagePath;
        this.time=time;
        this.NotificationStatus=NotificationStatus;
        this.NotificationId=NotificationId;
        this.to_user_id=to_user_id;
        this.from_user_id=from_user_id;
        if(from_phone_number!=null && !from_phone_number.trim().equalsIgnoreCase("")){
            this.from_phone_number = from_phone_number;
        }
        this.type = type;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notificationdetail, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    @SuppressWarnings("unused")
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        try {
            progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
        } catch (Exception e) {
            e.getMessage();
        }
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvTitleNotification=(TextView) view.findViewById(R.id.tvTitleNotification);
        tvNotificationDetail=(TextView) view.findViewById(R.id.tvNotificationDetail);
        tvTime=(TextView) view.findViewById(R.id.tvTime);
        llImageBoundary=(LinearLayout) view.findViewById(R.id.llImageBoundary);
        imViewNotificationContact=(RoundedImageViewCircular) view.findViewById(R.id.imViewNotificationContact);
        btnAccept=(Button)view.findViewById(R.id.btnAccept);
        btnReply=(Button)view.findViewById(R.id.btnReply);
        btnUpdateNumber=(Button)view.findViewById(R.id.btnUpdateNumber);
        btnCreateChatButton=(Button)view.findViewById(R.id.btnCreateChatButton);
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        flBackArrow.setVisibility(View.VISIBLE);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(), tvTitleNotification, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvNotificationDetail, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvTime, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnReply, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnAccept, "fonts/Roboto-Regular_1.ttf");
        btnBack.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnReply.setOnClickListener(this);
        btnHome.setOnClickListener(this);
//        tvNotificationDetail.setOnClickListener(this);
        btnReply.setText("");
        btnReply.setClickable(false);
        btnReply.setVisibility(View.GONE);
        btnAccept.setVisibility(View.GONE);
        btnCreateChatButton.setOnClickListener(this);
        tvTitleNotification.setVisibility(View.GONE);
        llImageBoundary.setVisibility(View.GONE);

        btnUpdateNumber.setVisibility(View.GONE);

        tvNotificationDetail.setMovementMethod(LinkMovementMethod.getInstance());

        if(message.contains(getActivity().getResources().getString(R.string.txtMapMessageContent))){

            int i1 = message.indexOf("http");
            int i2 = message.length()-1;

            tvNotificationDetail.setText(message, TextView.BufferType.SPANNABLE);
            Spannable mySpannable = (Spannable)tvNotificationDetail.getText();
            ClickableSpan myClickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View widget) {

                    String messageContent = tvNotificationDetail.getText().toString();
                    String mLatitude  = "";
                    String mLongitude = "";

                    if(progress!=null)
                        progress.show();

                    String[] mArrayMessage = new String[300];
                    if(message.contains("=")){
                        mArrayMessage = message.split("=");

                        String mLatLong = mArrayMessage[1];

                        if(mLatLong.contains(",")){
                            String[] mLatLongArray = mLatLong.split(",");

                            mLatitude      = mLatLongArray[0];
                            mLongitude     = mLatLongArray[1];

                            if(!(mLatitude.trim().equalsIgnoreCase("")) &&
                                    !(mLongitude.trim().equalsIgnoreCase(""))){
                                String address = GoogleMapUtils.getAddressString(getActivity(),
                                        Double.parseDouble(mLatitude),Double.parseDouble(mLongitude));

                                if(progress!=null)
                                    progress.dismiss();

                                Intent mLocationScreenIntent = new Intent(getActivity(), UserLocationDisplayActivity.class);
                                mLocationScreenIntent.putExtra("latitude", Double.parseDouble(mLatitude));
                                mLocationScreenIntent.putExtra("longitude", Double.parseDouble(mLongitude));
                                getActivity().startActivity(mLocationScreenIntent);

                               /* GlobalConfig_Methods.showMap(getActivity(),
                                        Double.parseDouble(mLatitude),Double.parseDouble(mLongitude),
                                        address);*/
                            }
                        }
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(Color.parseColor("#3692DB"));//set text color
                    ds.setUnderlineText(true); // set to false to remove underline
                }
            };
            mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //Set Display data on the basis of contents
        try {
            if(message.toLowerCase(Locale.ENGLISH).contains("welcome"))
            {
                llImageBoundary.setVisibility(View.VISIBLE);
                String decodedMessage="";
                int splitPosition=-1;
                Spanned displayMessage;
                if(message.contains("Since")) // In Case of Returning User
                {
                    decodedMessage=Uri.decode(message);
                    splitPosition=decodedMessage.indexOf("Since")-8;
                    String key=decodedMessage.substring(splitPosition,decodedMessage.indexOf("Since")-2);
                    displayMessage=Html.fromHtml("Welcome to "+getResources().getString(R.string.app_name)+". Thank you very much for your purchase and registering your app. Your cloud backup service has now been activated and your key is " +
                            "<b>"+ key + "</b>"	+ "." +"<br><br>Since you are a returning user, please ignore your previous backup key(s).");
                    tvNotificationDetail.setText(displayMessage);
                }
                else{
                    decodedMessage=Uri.decode(message);
                    splitPosition=decodedMessage.length();
                    String messageNormal=decodedMessage.substring(0,splitPosition-7);
                    String messageBold=decodedMessage.substring(splitPosition-7,decodedMessage.length());
                    displayMessage=Html.fromHtml(messageNormal+"<b>" + messageBold + "</b>"+".");
                    tvNotificationDetail.setText(displayMessage);  // In Case Of New User
                }
                try {
                    btnReply.setVisibility(View.GONE);
                    tvTitleNotification.setVisibility(View.VISIBLE);
                    tvTitleNotification.setText("Welcome");
                    int matching_user_id=Integer.parseInt(to_user_id);
                    listBBContacts=DBQuery.checkBBContactExistence(mActivityTabs,matching_user_id);
                    if(listBBContacts.size()>0)
                    {
                        BBID=listBBContacts.get(0).getBBID();
                    }
                    else
                    {
                        matching_user_id=Integer.parseInt(from_user_id);
                        listBBContacts=DBQuery.checkBBContactExistence(mActivityTabs,matching_user_id);
                        if(listBBContacts.size()>0){
                            BBID=listBBContacts.get(0).getBBID();
                        }
                    }
                    if(BBID!=0){
                        if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().trim().equalsIgnoreCase("NULL")){
                            imageLoader=new ImageLoadTask(mActivityTabs,listBBContacts.get(0).getImage(),imViewNotificationContact,320);
                            imageLoader.execute();
                        }
                        else{
                            imViewNotificationContact.setImageResource(R.drawable.appicon);//play_icon_round_non_border
                        }
                    }
                    else if(BBID==0){
                        imViewNotificationContact.setImageResource(R.drawable.appicon); //play_icon_round_non_border
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            else {
                // In case of message is not a welcome message for the registered user
                if(message.toLowerCase(Locale.ENGLISH).contains("image request") ||
                        message.toLowerCase(Locale.ENGLISH).contains("need") ||
                        message.toLowerCase(Locale.ENGLISH).contains("respond") ||
                        message.toLowerCase(Locale.ENGLISH).contains("response"))
                {
                    // 	In Case of request / response
                    llImageBoundary.setVisibility(View.VISIBLE);
                    tvNotificationDetail.setText(NotificationsFragment.displayMessage);//(Uri.decode(message));
                    try {
                        btnReply.setVisibility(View.GONE);
                        btnReply.setText("");
                        btnReply.setClickable(false);
                        tvTitleNotification.setVisibility(View.VISIBLE);
                        try {
                            tvTitleNotification.setText(NotificationsFragment.displayName);
                            imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        checkInternetConnection(notificationType);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                else if(message.toLowerCase(Locale.ENGLISH).contains("shared his/her contacts"))
                {
                    // In case of shared/accept shared contact
                    llImageBoundary.setVisibility(View.VISIBLE);
                    tvTitleNotification.setText(NotificationsFragment.displayName);
                    tvNotificationDetail.setText(NotificationsFragment.displayMessage);
                    imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                    try {
                        btnReply.setVisibility(View.GONE);
                        btnReply.setText("");
                        btnReply.setClickable(false);
                        tvTitleNotification.setVisibility(View.VISIBLE);

                        checkInternetConnection("contact");
                    }catch(Exception e){
                        e.getMessage();
                    }
                }
                else if(message.toLowerCase(Locale.ENGLISH).contains("shared contact")){
                    //In case of user shared a contact notification
                    llImageBoundary.setVisibility(View.VISIBLE);
                    tvTitleNotification.setText(NotificationsFragment.displayName);
                    tvNotificationDetail.setText(NotificationsFragment.displayMessage);
                    imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                    try {
                        btnReply.setVisibility(View.GONE);
                        btnReply.setText("");
                        btnReply.setClickable(false);
                        tvTitleNotification.setVisibility(View.VISIBLE);

                    }catch(Exception e){
                        e.getMessage();
                    }
                }

                else if(message.toLowerCase(Locale.ENGLISH).contains("joined tap-n-chat") ||
                        message.toLowerCase(Locale.ENGLISH).contains("joined chatstasy")){
                    //In case of user joined tap-n-chat notification
                    llImageBoundary.setVisibility(View.VISIBLE);
                    tvTitleNotification.setText(NotificationsFragment.displayName);
                    tvNotificationDetail.setText(NotificationsFragment.displayMessage);
                    imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                    try {
                        btnReply.setVisibility(View.GONE);
                        btnReply.setText("");
                        btnReply.setClickable(false);
                        tvTitleNotification.setVisibility(View.VISIBLE);


                        // Create tile for the current number

                        if(from_phone_number!=null && !from_phone_number.trim().equalsIgnoreCase("")) {
                            String[] mNumberArray = from_phone_number.split("-");
                            mCountryCodeTileCreate = mNumberArray[0];
                            mPhoneNumberTileCreate = mNumberArray[1];

                            ArrayList<ContactTilesBean> mListTilesExists = new ArrayList<ContactTilesBean>();

                            mListTilesExists = DBQuery.getTileFromPhoneNumber(getActivity(), mPhoneNumberTileCreate);

                            // check if tile already exists from the same number
                            if (mListTilesExists != null && mListTilesExists.size() > 0) {
                                // display button to create chat button for the number who joined chatstasy
                                btnCreateChatButton.setVisibility(View.GONE);
                            }else{
                                btnCreateChatButton.setVisibility(View.VISIBLE);
                            }
                        }
                        /*String numberExtracted=notificationMessage.substring(notificationMessage.indexOf("from")+4,notificationMessage.length());
                        String[] arrayNumbers=numberExtracted.split("to");

                        oldNumber=arrayNumbers[0].trim();
                        newNumber=arrayNumbers[1].trim();

                        String formattedNumberOld = GlobalConfig_Methods.getFormattedNumber(mActivityTabs, oldNumber);
                        String formattedNumberNew = GlobalConfig_Methods.getFormattedNumber(mActivityTabs, newNumber);

                        countryCodeRegisteredUserOld = formattedNumberOld.split("-")[0];
                        numberRegisteredUserOld = formattedNumberOld.split("-")[1];
                        countryCodeRegisteredUserNew=formattedNumberNew.split("-")[0];
                        numberRegisteredUserNew=formattedNumberNew.split("-")[1];

                        listContactTiles=new ArrayList<ContactTilesBean>();
                        listContactTiles=DBQuery.getTileFromPhoneNumberUpdateButton(mActivityTabs,countryCodeRegisteredUserOld,numberRegisteredUserOld);

                        String prefixTile="",countryCodeTile="",numberTile,contactNumberTile;

                        //check for the existence of tiles in case tile of the same number exists
                        //then make the update button visible
                        //Set visibility of number update button on the basis of tile existence a user
                        if(listContactTiles.size()>0){
                            btnCreateChatButton.setVisibility(View.VISIBLE);
                        }else{
                            btnCreateChatButton.setVisibility(View.GONE);
                        }*/

                        // display button to create chat button for the number who joined chatstasy
                        // btnCreateChatButton.setVisibility(View.VISIBLE);
                    }
                    catch(Exception e)
                    {
                        e.getMessage();
                    }
                }//Aa has changed his/her number from 6731234591 to 6731234592
                else if(message.toLowerCase(Locale.ENGLISH).contains("number from"))
                {
//                    if(saveState.getISPREMIUMUSER(getActivity())){
                        // Check if the user has purchased the premium version

                        //					String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
                        //					boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;

                        String notificationMessage=message.toLowerCase(Locale.ENGLISH);

                        String numberExtracted=notificationMessage.substring(notificationMessage.indexOf("from")+4,notificationMessage.length());
                        String[] arrayNumbers=numberExtracted.split("to");

                        oldNumber=arrayNumbers[0].trim();
                        newNumber=arrayNumbers[1].trim();

                        String formattedNumberOld = GlobalConfig_Methods.getFormattedNumber(mActivityTabs, oldNumber);
                        String formattedNumberNew = GlobalConfig_Methods.getFormattedNumber(mActivityTabs, newNumber);

                        countryCodeRegisteredUserOld = formattedNumberOld.split("-")[0];
                        numberRegisteredUserOld = formattedNumberOld.split("-")[1];
                        countryCodeRegisteredUserNew=formattedNumberNew.split("-")[0];
                        numberRegisteredUserNew=formattedNumberNew.split("-")[1];

                        listContactTiles=new ArrayList<ContactTilesBean>();
                        listContactTiles=DBQuery.getTileFromPhoneNumberUpdateButton(mActivityTabs,countryCodeRegisteredUserOld,numberRegisteredUserOld);

                        String prefixTile="",countryCodeTile="",numberTile,contactNumberTile;

                        //check for the existence of tiles in case tile of the same number exists
                        //then make the update button visible
                        //Set visibility of number update button on the basis of tile existence a user
                        if(listContactTiles.size()>0){
                            btnUpdateNumber.setVisibility(View.VISIBLE);
                        }else{
                            btnUpdateNumber.setVisibility(View.GONE);
                        }

                        //In case of number updated notification
                        llImageBoundary.setVisibility(View.VISIBLE);
                        tvTitleNotification.setText(NotificationsFragment.displayName);
                        tvNotificationDetail.setText(NotificationsFragment.displayMessage);
                        imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                        try {
                            btnReply.setVisibility(View.GONE);
                            btnReply.setText("");
                            btnReply.setClickable(false);
                            tvTitleNotification.setVisibility(View.VISIBLE);
                        }
                        catch(Exception e){
                            e.getMessage();
                        }

                        iNotifyUpdateTile = new INotifyGalleryDialog() {

                            @Override
                            public void yes() {
                                //In case user clicked "Yes" to update tile/Button
                                //find out number ,of which tile is to be updated
                                //notification message format-'username' has changed his number from 919888888888 to 919777777777

                                //check for the existence of tiles in case tile of the same number exists so needs to be updated
                                if(listContactTiles.size()>0)
                                {
                                    String prefixTile="",countryCodeTile="",numberTile,contactNumberTile;
                                    ImageRequestDialog dialog=null;
                                    for(int i = 0;i<listContactTiles.size();i++)
                                    {
                                        String tilePosition="";
                                        prefixTile=listContactTiles.get(i).getPrefix();
                                        countryCodeTile=listContactTiles.get(i).getCountryCode();
                                        numberTile=listContactTiles.get(i).getPhoneNumber();
                                        contactNumberTile=prefixTile+countryCodeTile+numberTile;
                                        tilePosition=listContactTiles.get(i).getTilePosition();

                                        //Check for identification Of Number
                                        //String number=GlobalConfig_Methods.getBBNumberToCheck(mActivityTabs,contactNumberTile);

                                        if((countryCodeTile+numberTile).equals(GlobalConfig_Methods.getPlaneNumber(oldNumber))){
                                            DBQuery.updateTilePhoneNumberButtonUpdate(mActivityTabs,countryCodeRegisteredUserNew,numberRegisteredUserNew,tilePosition);
                                        }
                                        if(dialog==null){
                                            dialog= new ImageRequestDialog();//"The contact button number for "+listContactTiles.get(i).getName()+" has been changed to "+countryCodeRegisteredUserNew+"-"+numberRegisteredUserNew
                                            dialog.setCancelable(false);
                                            dialog.newInstance("",mActivityTabs,"Chat button updated successfully","",null);
                                            dialog.show(getChildFragmentManager(), "test");
                                        }
                                    }
                                }else{
                                    ImageRequestDialog dialog= new ImageRequestDialog();
                                    dialog.setCancelable(false);
                                    dialog.newInstance("",mActivityTabs,"Chat button not found","",null);
                                    dialog.show(getChildFragmentManager(), "test");
                                }
                            }

                            @Override
                            public void no() {
                            }
                        };

                        btnUpdateNumber.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UpdateTileConfirmationDialog dialog = new
                                        UpdateTileConfirmationDialog();
                                dialog.newInstance("",mActivityTabs,"Do you want to update chat button? Please be aware that your currently saved phone number will be updated with the this number. ","",iNotifyUpdateTile);
                                dialog.show(getChildFragmentManager(), "test");
                            }
                        });
                    /*}else{
                        gotoBackScreen();
                    }*/

                }
                else{
                    tvTitleNotification.setVisibility(View.VISIBLE);
                    tvTitleNotification.setText(NotificationsFragment.displayName);
                    if(!message.contains(getActivity().getResources().getString(R.string.txtMapMessageContent))){
                        tvNotificationDetail.setText(NotificationsFragment.displayMessage);
                    }
                    imViewNotificationContact.setImageBitmap(NotificationsFragment.displayImage);
                    llImageBoundary.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

        //Change The App name in Notification Details
        String mTitleName = tvNotificationDetail.getText().toString();
        String mTitleNameDisplay = "";
        if(mTitleName.contains("Tap-n-Chat")){
            mTitleNameDisplay = mTitleName.replace("Tap-n-Chat","Chatstasy");
        }else if(mTitleName.contains("tap-n-Chat")){
            mTitleNameDisplay = mTitleName.replace("tap-n-chat","Chatstasy");
        }else{
            mTitleNameDisplay = mTitleName;
        }
        if(!message.contains(getActivity().getResources().getString(R.string.txtMapMessageContent))) {
            tvNotificationDetail.setText(mTitleNameDisplay);

//            tvNotificationDetail.setText(message, TextView.BufferType.SPANNABLE);
        }
//        tvNotificationDetail.setText(mTitleNameDisplay);

        //        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    //Check internet connection availability
    private void checkInternetConnection(String notificationType)
    {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs))
        {
            if(notificationType.equals("image"))
            {
                NotificationBean notificationBean=new NotificationBean(NotificationId);
                getNotificationStatus(notificationBean);
            }
            else if(notificationType.equals("contact"))
            {
                ContactNotificationBean contactNotificationBean=new ContactNotificationBean(NotificationId);
                sendContactSharingStatus(contactNotificationBean);
            }
            else if(notificationType.equals("updateContactStatus"))
            {  //Update Notification status  i.e. rejected
                ContactShareStatusUpdate contactShareStatusUpdate=new ContactShareStatusUpdate(NotificationId, status);
                updateContactSharingStatus(contactShareStatusUpdate);
            }
        }
        else
        {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    //send query to server with data for updating contact sharing status(Pending/Accepted/Declined/Replied)
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

    //Async task to handle update contact sharing status
    AsyncHttpResponseHandler updateContactShareStatusResponseHandler = new JsonHttpResponseHandler() {
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
                isFirstTime=false;
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
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    //send query to server with data for updating conatct sharing status(Accept/Decline)
    private void sendContactSharingStatus(ContactNotificationBean contactNotificationBean)
    {
        try
        {
            gson=new Gson();
            String stingGson = gson.toJson(contactNotificationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.CHECKSHARECONTACT,
                    stringEntity, contactShareStatusResponseHandler,
                    mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    //async task to handle response for the contact being shared and do the status change
    AsyncHttpResponseHandler contactShareStatusResponseHandler = new JsonHttpResponseHandler() {
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
                if(response!=null)
                {
                    Logs.writeLog("contactShareStatusResponseHandlerStatus", "OnSuccess",response.toString());
                    getResponseContactShareStatus(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("contactShareStatusResponseHandlerStatus", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * //Method to handle response for the contact being shared and do the status change
     * @param response
     */
    private void getResponseContactShareStatus(String response){
        btnUpdateNumber.setVisibility(View.GONE);
        try {
            String response2="";//Set visibility of number update button on the basis of tile existence a user
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
                    notificationStatus=get_Response.getData.status;
                    if(get_Response.getData.status.equalsIgnoreCase("pending"))
                    {
                        btnReply.setVisibility(View.VISIBLE);
                        btnReply.setBackground(getActivity().getResources().getDrawable(R.drawable.button_bg_delete));
                        btnAccept.setVisibility(View.VISIBLE);
                        btnReply.setText("Decline");
                        btnReply.setClickable(true);
                    }
                    else if(get_Response.getData.status.equalsIgnoreCase("declined") || get_Response.getData.status.equalsIgnoreCase("decline")){
                        btnReply.setVisibility(View.VISIBLE);
                        btnAccept.setVisibility(View.GONE);
                        btnReply.setText("Declined");
                        btnReply.setClickable(false);

                        if(!isFirstTime)
                            if(mActivityTabs instanceof HomeScreenActivity)
                            {
                                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
                            }

                    }
                    else if(get_Response.getData.status.equalsIgnoreCase("accepted") ||get_Response.getData.status.equalsIgnoreCase("accept")){
                        btnReply.setVisibility(View.VISIBLE);
                        btnAccept.setVisibility(View.GONE);
                        btnReply.setText("Accepted");
                        btnReply.setClickable(false);
                    }
                    dbLink=get_Response.getData.link;
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
            else {
                Log.d("improper_response",response);
                ShowDialog.alert(mActivityTabs,"",getResources().getString(R.string.improper_response_network));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * request for getting image request status from the server
     * @param notificationBean
     */
    private void getNotificationStatus(NotificationBean notificationBean)
    {
        try
        {
            gson=new Gson();
            String stingGson = gson.toJson(notificationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.NOTIFICATION_CHECK_IMAGE_REQUEST_STATUS,
                    stringEntity, notificationStatusResponseHandler,
                    mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    //async task to handle response for the request made to check image request
    AsyncHttpResponseHandler notificationStatusResponseHandler = new JsonHttpResponseHandler() {
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
                if(response!=null)
                {
                    Logs.writeLog("NotificationImageRequestStatus", "OnSuccess",response.toString());
                    getResponseNotificationImageRequestStatus(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("NotificationImageRequestStatus", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * handle response for the request being made to fetch user image
     *
     * @param response
     */
    private void getResponseNotificationImageRequestStatus(String response)
    {
        try
        {
            imageUrl="";
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
                ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
                dialogErrorMessage.setCancelable(false);
                NotificationImageStatusResponse get_Response = gson.fromJson(response2,NotificationImageStatusResponse.class);
                if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE))
                {
                    String strBtnText="";
                    //				btnReply.setVisibility(View.VISIBLE);
                    if(get_Response.getData.type.equals("1"))  //Image Request
                    {
                        btnReply.setVisibility(View.VISIBLE);
                        if(get_Response.getData.status.equals("1"))
                        {
                            strBtnText="Reply";
                        }
                        else if(get_Response.getData.status.equals("2"))
                        {
                            strBtnText="Replied";
                        }
                        else if(get_Response.getData.status.equals("3"))
                        {
                            strBtnText="Auto-Replied";
                        }
                        else{
                            btnReply.setVisibility(View.GONE);
                        }
                        btnReply.setText(strBtnText);
                        if(get_Response.getData.status!=null)
                        {
                            if(get_Response.getData.status.equalsIgnoreCase("1"))
                            {
                                btnReply.setClickable(true);
                            }
                            else
                            {
                                btnReply.setClickable(false);
                            }
                        }
                    }
                    else if(get_Response.getData.type.equals("2"))  //Image Response
                    {
                        imageUrl="";
                        new DownloadImageServiceClass().execute();
                        imageUrl=get_Response.getData.getImage();
                        btnReply.setVisibility(View.GONE);
                        //					Bitmap bitmapImage=GlobalConfig_Methods.getBitmapFromURL(get_Response.getData.getImage());
                        //					String Image=GlobalConfig_Methods.encodeTobase64(bitmapImage);
                    }
                }

                else if (get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_1) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_2) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_3) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_5) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_6) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_601))
                {
//                    dialogErrorMessage.newInstance("",((HomeScreenActivity)mActivityTabs),get_Response.response_message,"",null);
//                    dialogErrorMessage.show(((HomeScreenActivity)mActivityTabs).getSupportFragmentManager(),"test");
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    // async task to download image from the url
    Bitmap bitmapImage;
    class DownloadImageServiceClass extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                bitmapImage=GlobalConfig_Methods.loadBitmap(imageUrl);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmapImage.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, blob);
                arrayImage = blob.toByteArray();
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            int matching_user_id=-1;
            if(Integer.parseInt(saveState.getBBID(mActivityTabs)) == Integer.parseInt(to_user_id))
            {
                matching_user_id=Integer.parseInt(from_user_id);
            }
            else if(Integer.parseInt(saveState.getBBID(mActivityTabs)) == Integer.parseInt(from_user_id))
            {
                matching_user_id=Integer.parseInt(to_user_id);
            }
            listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs,matching_user_id);
            if(arrayImage!=null && listBBContacts!=null && !listBBContacts.isEmpty())
                DBQuery.updateBBImageFromResponse(mActivityTabs,arrayImage,listBBContacts.get(0).getPhoneNumber());
        }
    }

    //Set the time in seconds/mins/hours according the time passed after the notification is received
    private void setTime()
    {
        int timeHours=0;
        int timeDays=0;
        String formattedDate=GlobalConfig_Methods.isUnlockTimeExpire(time);
        int timeSeconds=GlobalConfig_Methods.getTimeDifference_Seconds_Local(formattedDate);
        //		int timeMinutes=GlobalConfig_Methods.getTimeDifference_Minutes_Local(time);
        if(timeSeconds==0)
        {
            tvTime.setText("Just Now"); //"Just Now"
        }
        else if(timeSeconds>0 && timeSeconds<60)
        {
            tvTime.setText(timeSeconds+" seconds ago"); //"seconds ago"
        }
        else if(timeSeconds>=60 && timeSeconds<120)
        {
            tvTime.setText("1 min ago"); //"1 min ago"
        }
        else
        {
            int timeMinutes=GlobalConfig_Methods.getTimeDifference_Minutes_Local(formattedDate);
            timeHours=timeMinutes/60;
            if(timeMinutes>1 && timeMinutes<=1440)
            {
                if(timeMinutes>1 && timeMinutes<60)
                {
                    tvTime.setText(timeMinutes+" minutes ago");
                }
                else if(timeHours>=1 && timeHours<2)
                {
                    tvTime.setText(timeHours+" hour ago");
                }
                else if(timeHours>=1 && timeHours<24)
                {
                    tvTime.setText(timeHours+" hours ago");
                }
            }
            else if(timeHours>=24)
            {
                timeDays=timeHours/24;
                if((timeDays<=1) ||(timeDays>=1 && timeDays<2))
                {
                    tvTime.setText("1 day ago");
                }
                else if(timeDays>=2)
                {
                    tvTime.setText(timeDays+" days ago");
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Set the notification updated time
        setTime();
    }

    /**
     * interface to handle status update in case user accepted/rejected the request
     */
    INotifyGalleryDialog iNotify=new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // in case user clicked yes to decline
            status="3";
            checkInternetConnection("updateContactStatus");
        }

        @Override
        public void no() {
        }
    };

    /**
     * fetch database file from the server
     */

    private void getBackupFromServer()
    {
        try {
            new DownloadFileAsyncTask().execute();
        } catch (Exception e) {
            isErrorOccuredWhileDownloadingFile=true;
            Logs.writeLog("BackupConfirmationDialog","getBackupFromServer",e.getMessage());
        }
    }

    //async task to download file
    class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(downloadcount==1){
                progress=new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
                progress.show();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(downloadcount==1)
                DownloadFromUrl(dbLink,"tnc_tiles");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(downloadcount==1)
            {
                if(progress!=null)
                    progress.dismiss();
                downloadcount=2;
                if(isErrorOccuredWhileDownloadingFile)
                {
                    ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
                    dialogErrorMessage.setCancelable(false);
                    dialogErrorMessage.newInstance("",
                            ((HomeScreenActivity) mActivityTabs),
                            "Failed to get Contacts.\n Please try after sometime", "", null);
                    dialogErrorMessage.show(
                            ((HomeScreenActivity) mActivityTabs)
                                    .getSupportFragmentManager(), "test");
                }
                else if(!isErrorOccuredWhileDownloadingFile)
                {
                    fetchSharedContactsFromDataBase();
                }
            }
        }
    }

    //Fetch Contacts from the database which was been fetched from the server
    private void fetchSharedContactsFromDataBase(){
        listSharedContactTiles=new ArrayList<ContactTilesBean>();
        listSharedContactTiles=DBQuery.getSharedContactTiles(mActivityTabs,fileSaved.getName());
        TileContacts tileContacts=new TileContacts();
        tileContacts.newInstance("SharedContactsMerged",notificationStatus,NotificationId,listSharedContactTiles);
        ((HomeScreenActivity)mActivityTabs).setFragment(tileContacts);
    }

    /**
     * download file from the server
     */
    public void DownloadFromUrl(String DownloadUrl, String fileName) {
        try {
            db_path="/data/data/" + this.mActivityTabs.getApplication().getPackageName()+ "/databases/";
            fileDB = new File(db_path);
            if(fileDB.exists()==false) {
                fileDB.mkdirs();
            }
            else if(fileDB.exists()){
                fileDB.delete();
            }
            fileDB.createNewFile();
            URL url = new URL(DownloadUrl); //you can write here any link
            fileSaved = new File(fileDB,fileName);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadManager", "download begining");
            Log.d("DownloadManager", "download url:" + url);
            Log.d("DownloadManager", "downloaded file name:" + fileName);

			/* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();
			/*
			 * Define InputStreams to read from the URLConnection.
			 */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

			/* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(fileSaved);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        } catch (IOException e) {
            isErrorOccuredWhileDownloadingFile=true;
            Log.d("DownloadManager", "Error: " + e);
        }
    }

    /**
     * Method to go to back screen
     */
    private void gotoBackScreen(){
        ImageRequestDialog mDialog = new ImageRequestDialog();
        mDialog.setCancelable(false);
        mDialog.newInstance("",getActivity(),getResources().getString(R.string.txtPremiumFeaturePopupMessage),"",mActionGoToBackScreen);
        mDialog.show(getChildFragmentManager(),"Test");
		/*PremiumFeaturesFragment mObjectPremiumFeaturesFragment = new PremiumFeaturesFragment();
        mObjectPremiumFeaturesFragment.newInstance("","",null);
        // In case of user is not the premium user
        if(mActivityTabs instanceof MainBaseActivity){
            ((MainBaseActivity)mActivityTabs).setFragment(mObjectPremiumFeaturesFragment);

        }
        else if(mActivityTabs instanceof HomeScreenActivity){
            ((HomeScreenActivity)mActivityTabs).setFragment(mObjectPremiumFeaturesFragment);
        }*/
    }

    AlertCallAction mActionGoToBackScreen = new AlertCallAction() {
        @Override
        public void isAlert(boolean isOkClicked)
        {
            // go to google play store to download Premium Version of the app
            GlobalConfig_Methods.gotoPremiumVersionPlayStore(getActivity());

            if(mActivityTabs instanceof MainBaseActivity){
                ((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
            }else if(mActivityTabs instanceof HomeScreenActivity){
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
            }
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnBack:
                if(mActivityTabs instanceof HomeScreenActivity)
                {
                    ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
                    NotificationsFragment.displayName="";
                    NotificationsFragment.displayMessage="";
                    NotificationsFragment.displayImage=null;
                }
                break;
            case R.id.btnAccept:
                downloadcount=1;
                //In case user accepted contact sharing then Download Database file from the specified Url
                status="2";
                //Download Database file from the specified Url
                if(dbLink!=null && !dbLink.trim().equals("") && !dbLink.toLowerCase().contains("null"))
                {
                    if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
                        if(downloadcount==1)
                            getBackupFromServer();
                    } else
                    {
                        ImageRequestDialog dialog=new ImageRequestDialog();;
                        dialog.setCancelable(false);
                        dialog.newInstance("",mActivityTabs,"Invalid Url","",null);
                        dialog.show(getChildFragmentManager(), "test");
                    }
                }
                //			iNotify.yes();
                break;

            case R.id.btnReply:
                if(btnReply.getText().toString().equalsIgnoreCase("Reply"))
                {
                    ChooseImageResponseFragment objChooseImageFragment=new ChooseImageResponseFragment();
                    objChooseImageFragment.newInstance(mActivityTabs,NotificationId,btnReply.getText().toString());
                    ((HomeScreenActivity)mActivityTabs).setFragment(objChooseImageFragment);
                }
                else if(btnReply.getText().toString().equalsIgnoreCase("Decline")){
                    ContactShareAcceptConfirmationDialog dialog=new ContactShareAcceptConfirmationDialog();
                    dialog.newInstance("",mActivityTabs,"Do you like to decline?","",iNotify);
                    dialog.setCancelable(false);
                    dialog.show(getChildFragmentManager(), "test");
                }
                break;

            case R.id.btnHome:
                if(mActivityTabs instanceof HomeScreenActivity)
                {
                    MainBaseActivity.isFromMain=true;
                    ((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
                    ((HomeScreenActivity)mActivityTabs).finish();
                    NotificationsFragment.displayName="";
                    NotificationsFragment.displayMessage="";
                    NotificationsFragment.displayImage=null;
                }
                break;

            case R.id.btnCreateChatButton:

                // Create tile for the current number

                if((mCountryCodeTileCreate!=null && !mCountryCodeTileCreate.trim().equalsIgnoreCase("")) &&
                        (mPhoneNumberTileCreate!=null && !mPhoneNumberTileCreate.trim().equalsIgnoreCase(""))) {

                    // create tile if a tile does not exist for this number
                    TileContactDetailsFragment objTileContactDetailsFragment=null;

                    objTileContactDetailsFragment=new TileContactDetailsFragment();
//                    objTileContactDetailsFragment.newInstance(getActivity(),false,null,strSelectedNumber,tvTitleNotification.getText().toString().trim(),
//                            strSelectedNumber,null,true);

                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity)getActivity()),false,
                            GlobalConfig_Methods.getBBNumberToCheck(getActivity(),mCountryCodeTileCreate + mPhoneNumberTileCreate),
                            null, tvTitleNotification.getText().toString().trim(),
                            mCountryCodeTileCreate + mPhoneNumberTileCreate,null,true);

                    ((HomeScreenActivity) getActivity()).setFragment(objTileContactDetailsFragment);

                }

                /*

                if(from_phone_number!=null && !from_phone_number.trim().equalsIgnoreCase("")){
                    String[] mNumberArray = from_phone_number.split("-");
                    mCountryCode = mNumberArray[0];
                    mPhoneNumber = mNumberArray[1];

                    ArrayList<ContactTilesBean> mListTilesExists = new  ArrayList<ContactTilesBean>();

                    mListTilesExists = DBQuery.getTileFromPhoneNumber(getActivity(), mPhoneNumber);

                    if(mListTilesExists!=null && mListTilesExists.size() > 0){
                        // check if tile already exists from the same number

                        ImageRequestDialog dialog= new ImageRequestDialog();
                        dialog.setCancelable(false);
                        dialog.newInstance("",mActivityTabs,getResources().getString(R.string.txtChattButtonExistsValidationMessage),"",null);
                        dialog.show(getChildFragmentManager(), "test");

                    }else{
                        // create tile if a tile does not exist for this number
                        TileContactDetailsFragment objTileContactDetailsFragment=null;

                        objTileContactDetailsFragment=new TileContactDetailsFragment();
//                    objTileContactDetailsFragment.newInstance(getActivity(),false,null,strSelectedNumber,tvTitleNotification.getText().toString().trim(),
//                            strSelectedNumber,null,true);

                        objTileContactDetailsFragment.newInstance(((HomeScreenActivity)getActivity()),false,
                                GlobalConfig_Methods.getBBNumberToCheck(getActivity(),mCountryCode + mPhoneNumber),
                                null, tvTitleNotification.getText().toString().trim(),
                                mCountryCode + mPhoneNumber,null,true);

                        ((HomeScreenActivity) getActivity()).setFragment(objTileContactDetailsFragment);
                    }
                }*/

                break;

            /*case R.id.tvNotificationDetail:
                String messageContent = tvNotificationDetail.getText().toString();
                String mLatitude  = "";
                String mLongitude = "";

                if(messageContent.contains(
                        getActivity().getResources().getString(R.string.txtMapMessageContent))){

                    if(progress!=null)
                        progress.show();

                    String[] mArrayMessage = new String[150];
                    if(messageContent.contains("=")){
                        mArrayMessage = messageContent.split("=");

                        String mLatLong = mArrayMessage[1];

                        if(mLatLong.contains(",")){
                            String[] mLatLongArray = mLatLong.split(",");

                            mLatitude      = mLatLongArray[0];
                            mLongitude     = mLatLongArray[1];

                            if(!(mLatitude.trim().equalsIgnoreCase("")) &&
                                    !(mLongitude.trim().equalsIgnoreCase(""))){
                                String address = GoogleMapUtils.getAddressString(getActivity(),
                                        Double.parseDouble(mLatitude),Double.parseDouble(mLongitude));

                                if(progress!=null)
                                    progress.dismiss();

                                GlobalConfig_Methods.showMap(getActivity(),
                                        Double.parseDouble(mLatitude),Double.parseDouble(mLongitude),
                                        address);

                            }
                        }
                    }
                }*/
            default:
                break;
        }
    }
}
