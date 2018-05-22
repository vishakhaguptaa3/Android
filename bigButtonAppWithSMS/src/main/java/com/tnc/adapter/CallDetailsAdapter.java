package com.tnc.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.UserCallLogDataBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.fragments.CallLogDetailsScreenFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a3logics on 2/12/16.
 */

public class CallDetailsAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mAct;
    public List<CallDetailsBean> mListUserCallLogDataBean;
    private int selectedPosition;
    private boolean isColorSet=false;
    private SharedPreference saveState;

    public CallDetailsAdapter(Activity mContext, List<CallDetailsBean> mListUserCallLogDataBean){
        this.mContext = mContext;
        this.mAct = mContext;
        this.mListUserCallLogDataBean = new ArrayList<CallDetailsBean>();
        this.mListUserCallLogDataBean = mListUserCallLogDataBean;
        saveState = new SharedPreference();
    }

    /**
     *
     * @param position
     * @param isColorSet
     * set selected List item row color and content color
     */
    public void setRowColor(int position, boolean isColorSet){
        selectedPosition = position;
        this.isColorSet = isColorSet;
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount()
    {
        return mListUserCallLogDataBean.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListUserCallLogDataBean.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    static class ViewHolder
    {
        LinearLayout llContactNameHolder, llArrowHolder;
        TextView tvCallName, tvCallingNumber, tvCallCount, tvCallingDateTime, tvEmptyList;
        ImageView imViewMenuIcon, imViewUnreadCallLog, imViewArrow;
        RoundedImageViewCircular imViewContactImage;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({ "InflateParams", "DefaultLocale", "CutPasteId" })
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;

        ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();
        ArrayList<BBContactsBean> listBBContacts     = new ArrayList<BBContactsBean>();

        String mUserName                             = "", mDisplayImageFromBBID = "";
        Bitmap displayImageFromTile                          = null, displayImageFromPhoneContact = null;

        int BBID                                     =  -1;
        boolean isTncUser                            = false;
        View    rowView;

        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.call_detail_menu_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.llContactNameHolder    = (LinearLayout) convertView.findViewById(R.id.llContactNameHolder);
            holder.llArrowHolder          = (LinearLayout) convertView.findViewById(R.id.llArrowHolder);
            holder.tvCallName             = (TextView) convertView.findViewById(R.id.tvCallName);
            holder.tvCallCount            = (TextView) convertView.findViewById(R.id.tvCallCount);
            holder.tvCallingNumber        = (TextView) convertView.findViewById(R.id.tvCallingNumber);
            holder.tvCallingDateTime      = (TextView) convertView.findViewById(R.id.tvCallingDateTime);
            holder.tvEmptyList            = (TextView) convertView.findViewById(R.id.tvEmptyList);
            holder.imViewMenuIcon         = (ImageView) convertView.findViewById(R.id.imViewMenuIcon);
            holder.imViewUnreadCallLog    = (ImageView) convertView.findViewById(R.id.imViewUnreadCallLog);
            holder.imViewArrow            = (ImageView) convertView.findViewById(R.id.imViewArrow);
            holder.imViewContactImage     = (RoundedImageViewCircular) convertView.findViewById(R.id.imViewContactImage);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        rowView = convertView;

        holder.imViewUnreadCallLog.setVisibility(View.GONE);

        holder.imViewMenuIcon.setVisibility(View.GONE);

        holder.tvEmptyList.setVisibility(View.GONE);

        String mOptionSelected = mListUserCallLogDataBean.get(position).getCallName();

        if(isColorSet && position == selectedPosition){
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
            TextView tvCallName=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallName);
            TextView tvCallCount=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallCount);
            TextView tvCallingNumber = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingNumber);
            TextView tvCallingDateTime = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingDateTime);
            tvCallName.setTextColor(mContext.getResources().getColor(R.color.white));
            tvCallCount.setTextColor(mContext.getResources().getColor(R.color.white));
            tvCallingNumber.setTextColor(mContext.getResources().getColor(R.color.white));
            tvCallingDateTime.setTextColor(mContext.getResources().getColor(R.color.white));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);

            /*if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserProfileSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_profile_settings_white));
            }*/
        }
        else{
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
            TextView tvCallName=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallName);
            TextView tvCallCount=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallCount);
            TextView tvCallingNumber = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingNumber);
            TextView tvCallingDateTime = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingDateTime);
            tvCallName.setTextColor(mContext.getResources().getColor(R.color.textBlueColor));
            tvCallCount.setTextColor(mContext.getResources().getColor(android.R.color.black));
            tvCallingNumber.setTextColor(mContext.getResources().getColor(android.R.color.black));
            tvCallingDateTime.setTextColor(mContext.getResources().getColor(android.R.color.black));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));
            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
            /*if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserProfileSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_profile_settings));
            }*/
        }

        holder.tvCallingDateTime.setVisibility(View.GONE);

        holder.llArrowHolder.setTag(position);
        holder.imViewArrow.setTag(position);

        // in case of empty list
        if(mListUserCallLogDataBean.get(0).getCallName().equalsIgnoreCase(
                mContext.getResources().getString(R.string.txtEmptyCallLogmessage))){

            holder.tvEmptyList.setVisibility(View.VISIBLE);
            holder.imViewArrow.setVisibility(View.GONE);
            holder.llContactNameHolder.setVisibility(View.GONE);
            holder.imViewContactImage.setVisibility(View.GONE);

        }else{
            // if call name is not blank then set number field otherwise if name is blank/empty/null then set number in case of name
            //  Display name

            // split the number as per splitting rule algorithm
            String strNumber = "", countryCodeRegisteredUser="", numberRegisteredUser = "";
            String []arrayUserDetails = new String[80];

            try {
                strNumber = GlobalConfig_Methods.getBBNumberToCheck(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());
                arrayUserDetails = strNumber.split(",");

                countryCodeRegisteredUser=arrayUserDetails[0];
                numberRegisteredUser=arrayUserDetails[1];
                if(arrayUserDetails[5]!=null && !arrayUserDetails[5].trim().equalsIgnoreCase(""))
                    if(Boolean.parseBoolean(arrayUserDetails[5]) == true)
                        isTncUser = true;
            } catch (Exception e) {
                e.getMessage();
            }

            // fetch details from tile table using phonenumber
            try{
                if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase("")){
                    // in case number successfully splitted from the algorithm
                    // check in Tiles table for existence of contacts
                    // fetch details from tile table using phonenumber
                    listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,
                            GlobalConfig_Methods.trimSpecialCharactersFromString(numberRegisteredUser));
                }else{
                    // in case unable to find number from splitting algorithm
                    // check in Tiles table for existence of contacts
                    // fetch details from tile table using phonenumber
                    listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,
                            GlobalConfig_Methods.trimSpecialCharactersFromString(mListUserCallLogDataBean.get(position).getCallingNumber()));
                }
            }catch(Exception e){
                e.getMessage();
            }

            // In case of Tile Exists for the user number
            if(listContactTiles!=null && listContactTiles.size()>0){
                mUserName = listContactTiles.get(0).getName();

                //set image array if tile table has an image array

                if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0) {
                    byte arrayImage[] = listContactTiles.get(0).getImage();
                    if (arrayImage != null && arrayImage.length > 0) {
                        displayImageFromTile = BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length);
                    }
                }

                // Check if it is a TnC user
                if(listContactTiles.get(0).isIsTncUser()){
                    isTncUser = true;
                }else{
                    // check for is TncUserFromBBContacts
                    // get BBID from phone number and country Code
                    BBID = DBQuery.getBBIDFromPhoneNumber(mContext, listContactTiles.get(0).getPhoneNumber());

                    // fetch name from BBContacts Table on the basis of BBID 2293
                    if(BBID > 0){
                        // Set it as a TnC user
                        isTncUser = true;
                    }
                }

            }else if((mUserName == null || mUserName.trim().equalsIgnoreCase(""))){

                if((saveState.isRegistered(mContext))){
                    // get BBID from phone number and country Code
                    BBID = DBQuery.getBBIDFromPhoneNumber(mContext, numberRegisteredUser);

                    // fetch name from BBContacts Table on the basis of BBID 2293
                    if(BBID > 0){

                        // Set it as a TnC user
                        isTncUser = true;

                        // fetch image from bbContacts table if doesn't exists in tile table
                        listBBContacts=DBQuery.checkBBContactExistence(mContext,BBID);
                        if(mUserName!= null && listBBContacts.size() > 0){
                            if(listBBContacts.get(0).getName()!=null &&
                                    !listBBContacts.get(0).getName().trim().equalsIgnoreCase("")){
                                mUserName = listBBContacts.get(0).getName();
                            }

                            if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equalsIgnoreCase("")){
                                mDisplayImageFromBBID = listBBContacts.get(0).getImage();
                            }
                        }
                        // check in BBContacts table for existence of contacts
                        /*mUserName = DBQuery.getContactTileNameFromBBID(mContext,
                                BBID, false);*/
                    }else{
                        // fetch contact name from phone
                        mUserName = GlobalConfig_Methods.getContactName(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());
                        if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                            mUserName = "Unknown";
                        }
                        // fetch User Image from Phone Contact
                        displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());
                    }
                }else{
                    // fetch the contact name from the phone contact list
                    // fetch contact name from phone
                    mUserName = GlobalConfig_Methods.getContactName(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());
                    if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                        mUserName = "Unknown";
                    }

                    // fetch User Image from Phone Contact
                    displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());
                }
            }

            if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                // fetch contact name from phone
                mUserName = GlobalConfig_Methods.getContactName(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());

                // fetch User Image from Phone Contact
                displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mListUserCallLogDataBean.get(position).getCallingNumber());

            }else {
                if(mUserName == null || mUserName.trim().equalsIgnoreCase("Unknown"))
                    mUserName = "Unknown";
            }

            if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                mUserName = "Unknown";
            }

            String prefix = "", mNumberToDisplay = "";

            if(mUserName == null ||
                    mUserName.equalsIgnoreCase("") ||
                    mUserName.equalsIgnoreCase("Unknown")){

                if(mListUserCallLogDataBean.get(position).getCallingNumber().startsWith("+")){
                    prefix = mListUserCallLogDataBean.get(position).getCallingNumber().substring(0, 1);
                }
                if(prefix!=null && !prefix.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=prefix;

                if(countryCodeRegisteredUser!=null && !countryCodeRegisteredUser.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=countryCodeRegisteredUser + " ";

                if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=numberRegisteredUser;

                if(numberRegisteredUser == null || numberRegisteredUser.trim().equalsIgnoreCase("")){
                    mNumberToDisplay = mListUserCallLogDataBean.get(position).getCallingNumber();
                }

                if(mNumberToDisplay!=null && !mNumberToDisplay.trim().equalsIgnoreCase(""))
                    holder.tvCallName.setText(mNumberToDisplay);

                /*if(mNumberToDisplay == null || mNumberToDisplay.trim().equalsIgnoreCase("")){
                    holder.tvCallName.setText(mListUserCallLogDataBean.get(position).getCallingNumber());
                }*/

//                holder.tvCallName.setText(mNumberToDisplay);
                holder.tvCallingNumber.setText("");
            }else{
                holder.tvCallName.setText(mUserName);

                if(mListUserCallLogDataBean.get(position).getCallingNumber().startsWith("+")){
                    prefix = mListUserCallLogDataBean.get(position).getCallingNumber().substring(0, 1);
                }
                if(prefix!=null && !prefix.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=prefix;

                if(countryCodeRegisteredUser!=null && !countryCodeRegisteredUser.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=countryCodeRegisteredUser + " ";

                if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase(""))
                    mNumberToDisplay+=numberRegisteredUser;

                if(numberRegisteredUser == null || numberRegisteredUser.trim().equalsIgnoreCase("")){
                    mNumberToDisplay = mListUserCallLogDataBean.get(position).getCallingNumber();
                }

                if(mNumberToDisplay!=null && !mNumberToDisplay.trim().equalsIgnoreCase(""))
                    holder.tvCallingNumber.setText(mNumberToDisplay);

                if(mNumberToDisplay == null || mNumberToDisplay.trim().equalsIgnoreCase("")){
                    holder.tvCallingNumber.setText(mListUserCallLogDataBean.get(position).getCallingNumber());
                }

                //holder.tvCallingNumber.setText(mListUserCallLogDataBean.get(position).getCallingNumber());
            }

            // Set User Image Border
            if(isTncUser){
                holder.llContactNameHolder.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notificationimageborder));
                mListUserCallLogDataBean.get(position).setTncUser(true);
            }else{
                holder.llContactNameHolder.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.messagelist_border_grey));
                mListUserCallLogDataBean.get(position).setTncUser(false);
            }

            //Set Contact Display Image
            if(displayImageFromTile!=null){
                // Set Tile Image
                holder.imViewContactImage.setImageBitmap(displayImageFromTile);
            }else if(mDisplayImageFromBBID!=null && mDisplayImageFromBBID.length() > 0 && !mDisplayImageFromBBID.trim().equalsIgnoreCase("null")){
                // Set Image from BBContacts Table
                Picasso.with(mContext).load(mDisplayImageFromBBID).into(holder.imViewContactImage);
                //holder.imViewContactImage.setImageBitmap(displayImageFromTile);
            }else if(displayImageFromPhoneContact!=null){
                // Set Phone Contact Image
                holder.imViewContactImage.setImageBitmap(displayImageFromPhoneContact);
            }else{
                // Set Default Image
                holder.imViewContactImage.setImageBitmap(((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap());
                //holder.imViewContactImage.setImageResource(R.drawable.no_image);
            }

            holder.tvCallCount.setVisibility(View.GONE);

            // Display Call Count from specific number
            /*if(mListUserCallLogDataBean.get(position).getCallCount()>0){
                holder.tvCallCount.setText("(" + mListUserCallLogDataBean.get(position).getCallCount() + ")");
            }else{
                holder.tvCallCount.setText("");
            }*/

            // Display Unread CallLog icon
            if(mListUserCallLogDataBean.get(position).getStatus() == 1){
                // in case of un-read call log
                rowView.setBackgroundColor(Color.parseColor("#63b8ff"));

                holder.tvCallName.setTypeface(null, Typeface.BOLD);
                holder.tvCallName.setTextColor(Color.parseColor("#000000"));

                /*if((holder.tvCallCount!=null) && (holder.tvCallCount.getVisibility() == View.VISIBLE) && !(holder.tvCallCount.getText().toString().equalsIgnoreCase("")))
                    holder.tvCallCount.setTextColor(mContext.getResources().getColor(R.color.white));*/

                if((holder.tvCallingNumber!=null) && (holder.tvCallingNumber.getVisibility() == View.VISIBLE) && !(holder.tvCallingNumber.getText().toString().equalsIgnoreCase(""))){
                    holder.tvCallingNumber.setTypeface(null, Typeface.BOLD);
                    holder.tvCallingNumber.setTextColor(Color.parseColor("#000000"));
                }

                holder.tvCallingDateTime.setTextColor(Color.parseColor("#000000"));
                holder.imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));

            }else{
                // in case of read call log
                rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                holder.tvCallName.setTypeface(null, Typeface.NORMAL);
                holder.tvCallName.setTextColor(mContext.getResources().getColor(R.color.textBlueColor));

               /* if((holder.tvCallCount!=null) && (holder.tvCallCount.getVisibility() == View.VISIBLE) && !(holder.tvCallCount.getText().toString().equalsIgnoreCase("")))
                    holder.tvCallCount.setTextColor(mContext.getResources().getColor(android.R.color.black));*/

                if((holder.tvCallingNumber!=null) && (holder.tvCallingNumber.getVisibility() == View.VISIBLE) && !(holder.tvCallingNumber.getText().toString().equalsIgnoreCase("")))
                    holder.tvCallingNumber.setTextColor(Color.parseColor("#555555"));
                holder.tvCallingDateTime.setTextColor(Color.parseColor("#555555"));
                holder.imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));

            }

//            CustomFonts.setFontOfTextView(mContext,holder.tvCallName, "fonts/Roboto-Regular_1.ttf");
//            CustomFonts.setFontOfTextView(mContext,holder.tvCallingNumber, "fonts/Roboto-Regular_1.ttf");
//            CustomFonts.setFontOfTextView(mContext,holder.tvCallingDateTime, "fonts/Roboto-Regular_1.ttf");

            //holder.imViewUnreadCallLog.setVisibility(View.VISIBLE);

            //Handle click event of right arrow to display call log details screen
            /*holder.llArrowHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        int mPosition = Integer.parseInt(String.valueOf(view.getTag()));

                        // update call log status as read if it is unread
                        if(!(mListUserCallLogDataBean.get(mPosition).getStatus() == 2))
                            DBQuery.updateCallLogStatus(mContext,
                                    mListUserCallLogDataBean.get(mPosition).getCallingNumber());
                    }catch(Exception e){
                        e.getMessage();
                    }

                    try{
                        int mItemPosition = Integer.parseInt(String.valueOf(((ImageView)view).getTag()));
                        if(mItemPosition >= 0){
                            CallLogDetailsScreenFragment mCallLogDetailsScreenFragment = new CallLogDetailsScreenFragment();
                            mCallLogDetailsScreenFragment.newInstance(mContext,
                                    mListUserCallLogDataBean.get(mItemPosition).getCallingNumber(),
                                    mListUserCallLogDataBean.get(mItemPosition).isTncUser());
                            if (mContext instanceof MainBaseActivity) {
                                ((MainBaseActivity) mContext)
                                        .setFragment(mCallLogDetailsScreenFragment);
                            } else if (mContext instanceof HomeScreenActivity) {
                                ((HomeScreenActivity) mContext)
                                        .setFragment(mCallLogDetailsScreenFragment);
                            }
                        }
                    }catch(Exception e){
                        e.getMessage();
                    }

                }
            });

            //Handle click event of right arrow to display call log details screen
            holder.imViewArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        int mPosition = Integer.parseInt(String.valueOf(view.getTag()));

                        // update call log status as read if it is unread
                        if(!(mListUserCallLogDataBean.get(mPosition).getStatus() == 2))
                            DBQuery.updateCallLogStatus(mContext,
                                    mListUserCallLogDataBean.get(mPosition).getCallingNumber());
                    }catch(Exception e){
                        e.getMessage();
                    }

                    try{
                        int mItemPosition = Integer.parseInt(String.valueOf(((ImageView)view).getTag()));
                        if(mItemPosition >= 0){
                            CallLogDetailsScreenFragment mCallLogDetailsScreenFragment = new CallLogDetailsScreenFragment();
                            mCallLogDetailsScreenFragment.newInstance(mContext,
                                    mListUserCallLogDataBean.get(mItemPosition).getCallingNumber(),
                                    mListUserCallLogDataBean.get(mItemPosition).isTncUser());
                            if (mContext instanceof MainBaseActivity) {
                                ((MainBaseActivity) mContext)
                                        .setFragment(mCallLogDetailsScreenFragment);
                            } else if (mContext instanceof HomeScreenActivity) {
                                ((HomeScreenActivity) mContext)
                                        .setFragment(mCallLogDetailsScreenFragment);
                            }
                        }
                    }catch(Exception e){
                        e.getMessage();
                    }
                }
            });*/

            // Display icon if isEmergency Call Is exists in the database
            if(mListUserCallLogDataBean.get(position).isEmergencyCall()){
                holder.imViewUnreadCallLog.setVisibility(View.VISIBLE);
            }else{
                holder.imViewUnreadCallLog.setVisibility(View.GONE);
            }

            /*try {
                // Convert and Set Date Time
                if(mListUserCallLogDataBean.get(position).getCallTime()!=null &&
                        !mListUserCallLogDataBean.get(position).getCallTime().trim().equalsIgnoreCase("")){
                    String mDisplayDate = GlobalConfig_Methods.changeDateFormatToMMMddyyyy(mContext,
                            mListUserCallLogDataBean.get(position).getCallTime());

                    holder.tvCallingDateTime.setText(mDisplayDate);
                }
            }catch(Exception e){
                e.getMessage();
            }*/

        }
        return convertView;
    }

}
