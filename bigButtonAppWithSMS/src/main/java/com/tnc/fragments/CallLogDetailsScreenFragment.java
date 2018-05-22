package com.tnc.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CallLogDetailsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.MessageStatusUpdateBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifySetImageBitmapUrl;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

/**
 * class : UserSettings
 * description : This class is used to display call log details from a specific number
 * Created by a3logics on 16/12/16.
 */


public class CallLogDetailsScreenFragment extends BaseFragmentTabs implements View.OnClickListener{

    private FrameLayout flBackArrow;
    private LinearLayout llParentLayout, llImageBorder;
    private TextView tvTitle, tvCallDetail, tvContactName, tvContactNumber;
    private ImageView imViewUserImage;

    private Button btnBack, btnCallUserNumber, btnChatUserNumber;
    private ListView lvCallDetails;

    private CallLogDetailsAdapter adapterCallLogDetails;
    private ArrayList<CallDetailsBean> mListUserCallLogDataBean;
    private String mNumber = "";
    private Context mContext;
    private boolean IsTncUser = false;

    private  String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
    private boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;

    public CallLogDetailsScreenFragment newInstance(Context mContext, String mNumber, boolean IsTncUser){
        CallLogDetailsScreenFragment frag = new CallLogDetailsScreenFragment();
        this.mContext = mContext;
        this.mNumber  = mNumber;
        this.IsTncUser = IsTncUser;
        Bundle args   = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_log_details_screen_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvCallDetail = (TextView) view.findViewById(R.id.tvCallDetail);
        tvContactName = (TextView) view.findViewById(R.id.tvContactName);
        tvContactNumber = (TextView) view.findViewById(R.id.tvContactNumber);

        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        llParentLayout = (LinearLayout) view.findViewById(R.id.llParentLayout);
        llImageBorder = (LinearLayout) view.findViewById(R.id.llImageBorder);
        imViewUserImage = (ImageView) view.findViewById(R.id.imViewUserImage);

        flBackArrow.setVisibility(View.VISIBLE);
        btnBack = (Button) view.findViewById(R.id.btnBack);

        btnCallUserNumber = (Button) view.findViewById(R.id.btnCallUserNumber);
        btnChatUserNumber = (Button) view.findViewById(R.id.btnChatUserNumber);

        lvCallDetails = (ListView) view.findViewById(R.id.lvCallDetails);
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvCallDetail, "fonts/Roboto-Bold_1.ttf");

        // Set Display Values
        //Set/display user name
        String mDisplayName = GlobalConfig_Methods.getUserNameFromAlgorithm(getActivity(), "", mNumber,
                mINotifySetImageBitmapUrl);

        if(mDisplayName!=null){
            tvContactName.setText(mDisplayName);
        }else{
            tvContactName.setText("");
        }

        //set user phone number
        tvContactNumber.setText(mNumber);

        // set blue border line for registered user number
        if(saveState.isRegistered(getActivity())){

            String prefix = "", mPhonenumber = "";

            if(mNumber.startsWith("+")){
                prefix = mNumber.substring(0, 1);
                mPhonenumber = mNumber.substring(1, mNumber.length());
            }else{
                mPhonenumber = mNumber;
            }

            // in case of registered user detect if the number is of TnC User
            String strNumber= GlobalConfig_Methods.getBBNumberToCheck(mContext, prefix + mPhonenumber);

            String []arrayUserDetails=strNumber.split(",");

            countryCodeRegisteredUser=arrayUserDetails[0];
            numberRegisteredUser=arrayUserDetails[1];
            isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
            isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
            isdCodeRegisteredUser=arrayUserDetails[4];
            isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);


            String mNumberToDisplay = "";

            if(prefix!=null && !prefix.trim().equalsIgnoreCase(""))
                mNumberToDisplay+=prefix;

            if(countryCodeRegisteredUser!=null && !countryCodeRegisteredUser.trim().equalsIgnoreCase(""))
                mNumberToDisplay+=countryCodeRegisteredUser + " ";

            if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase(""))
                mNumberToDisplay+=numberRegisteredUser;

            if(mNumberToDisplay!=null && !mNumberToDisplay.trim().equalsIgnoreCase(""))
                tvContactNumber.setText(mNumberToDisplay);

        }

        if(isTncUserRegisteredUser)
        {
            llImageBorder.setVisibility(View.VISIBLE);
            btnChatUserNumber.setBackgroundResource(R.drawable.ic_chat_mode_enabled);
        }
        else{
            llImageBorder.setVisibility(View.GONE);
            btnChatUserNumber.setBackgroundResource(R.drawable.ic_chat_mode_disabled);
        }

        /*if(IsTncUser)
        {
            llImageBorder.setVisibility(View.VISIBLE);
            btnChatUserNumber.setBackgroundResource(R.drawable.ic_chat_mode_enabled);
        }
        else{
            llImageBorder.setVisibility(View.GONE);
            btnChatUserNumber.setBackgroundResource(R.drawable.ic_chat_mode_disabled);
        }*/

        btnBack.setOnClickListener(this);
        btnCallUserNumber.setOnClickListener(this);
        btnChatUserNumber.setOnClickListener(this);
        llParentLayout.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        //Set Call List Details
        mListUserCallLogDataBean =  new ArrayList<CallDetailsBean>();

        // get call log of specific user from the database
        mListUserCallLogDataBean = DBQuery.getCallLogsFromNumber(getActivity(), mNumber);

        if(mListUserCallLogDataBean!=null && !mListUserCallLogDataBean.isEmpty()){
            //SetAdapter for call Time History
            adapterCallLogDetails = new CallLogDetailsAdapter(getActivity(), mListUserCallLogDataBean);
            lvCallDetails.setAdapter(adapterCallLogDetails);
        }
    }

    /**
     * interface to handle event to set image on imageview
     */
    INotifySetImageBitmapUrl mINotifySetImageBitmapUrl = new INotifySetImageBitmapUrl() {
        @Override
        public void setBitmap(Bitmap bitmap) {
            if(bitmap!=null){
                // display image from the bitmap
                imViewUserImage.setImageBitmap(bitmap);
            }
        }

        @Override
        public void setUrl(String url) {
            if(url!=null && !url.trim().equalsIgnoreCase("")){
                // load and display image from url
                if(url.trim().equalsIgnoreCase("null")){
                    imViewUserImage.setImageBitmap(((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap());
                }else{
                    Picasso.with(mContext).load(url).into(imViewUserImage);
                }

            }
        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnBack){

            if (mActivityTabs instanceof MainBaseActivity) {
                ((MainBaseActivity) mActivityTabs).fragmentManager.popBackStack();
            } else if (mActivityTabs instanceof HomeScreenActivity) {
                ((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();

            }
        }else if(view.getId() == R.id.btnCallUserNumber){
            GlobalConfig_Methods.makePhoneCall(getActivity(), mNumber);
        }else if(view.getId() == R.id.btnChatUserNumber){
            if(saveState.isRegistered(getActivity()) && isTncUserRegisteredUser){
                // go to chat window
                goToChatWindow();
            }
        }
        else if(view.getId() == R.id.llParentLayout){
            //disable background click
        }
    }

    /**
     * Method to go to the chat window screen
     */
    private void goToChatWindow(){

        int BBID = 0;

        BBContactsBean objContactDetail = null;
        ArrayList<BBContactsBean> listBBContacts = null;
        try{
            if((countryCodeRegisteredUser!=null && !countryCodeRegisteredUser.trim().equalsIgnoreCase("")) &&
                    numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase("")){
                BBID = DBQuery.getBBIDFromPhoneNumberAndCountryCode(getActivity(), numberRegisteredUser, countryCodeRegisteredUser);

                if (BBID > 0) {
                    if (!saveState.getBBID(mActivityTabs).trim()
                            .equals("")){
                        // Update Notification
                        DBQuery.updateMessageStatus(mActivityTabs,
                                BBID, Integer.parseInt(saveState
                                        .getBBID(mActivityTabs)));
                        MessageStatusUpdateBean updateStatusBean = new MessageStatusUpdateBean();
                        updateStatusBean.setBbid(String
                                .valueOf(BBID));

                        if((getActivity() instanceof HomeScreenActivity) && (((HomeScreenActivity)getActivity()).getVisibleFragment() instanceof HomeTabFragment)){
                            // update message status on server
                            ((HomeTabFragment)((HomeScreenActivity)getActivity()).getVisibleFragment()).updateMessageStatus(updateStatusBean);
                            ((HomeScreenActivity) mActivityTabs)
                                    .setUnreadMessageCount();
                        }

                        // Open Chat Window
                        listBBContacts = new ArrayList<BBContactsBean>();
                        listBBContacts = DBQuery
                                .checkBBContactExistence(
                                        mActivityTabs, BBID);
                        objContactDetail = new BBContactsBean();
                        objContactDetail.setName(listBBContacts
                                .get(0).getName());
                        objContactDetail
                                .setPhoneNumber(listBBContacts.get(
                                        0).getPhoneNumber());
                        objContactDetail.setCountryCode(listBBContacts.get(
                                0).getCountryCode());
                        objContactDetail.setBBID(listBBContacts
                                .get(0).getBBID());
                        objContactDetail.setImage(listBBContacts
                                .get(0).getImage());
                        MessagePredefinedComposeFragment objMesssagePredefined = new MessagePredefinedComposeFragment();
                        objMesssagePredefined.newInstance(
                                objContactDetail, null,
                                Integer.parseInt(saveState
                                        .getBBID(mActivityTabs)),
                                BBID, null);
                        if (mActivityTabs instanceof MainBaseActivity) {
                            ((MainBaseActivity) mActivityTabs)
                                    .setFragment(objMesssagePredefined);
                        } else if (mActivityTabs instanceof HomeScreenActivity) {
                            ((HomeScreenActivity) mActivityTabs)
                                    .setFragment(objMesssagePredefined);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.getMessage();
        }
    }
}
