package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.database.DBQuery;
import com.tnc.interfaces.INotifyAction;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TileAdapterHomeScreen extends BaseAdapter
{
    private Context mContext;
    public ArrayList<ContactTilesBean> listTiles=new ArrayList<ContactTilesBean>();
    private int view_item_width;
    private int positionListItem;
    private String tilesType;
    private boolean isAnimationSet;
    private Bitmap _bmp;
    private SharedPreference saveState;
    private int mUserBBID = 0;
    private INotifyAction mActionUpdatePageArrowAndNumber;

    public TileAdapterHomeScreen(Context mContext, ArrayList<ContactTilesBean> listTiles,
                                 int view_item_width, String tilesType,
                                 INotifyAction mActionUpdatePageArrowAndNumber)
    {
        this.mContext=mContext;
        this.listTiles=listTiles;
        this.view_item_width=view_item_width;
        this.tilesType=tilesType;
        this.mActionUpdatePageArrowAndNumber = mActionUpdatePageArrowAndNumber;
        saveState=new SharedPreference();
        if(saveState.isRegistered(mContext)){
            if(GlobalCommonValues.listBBContacts!=null){
                GlobalCommonValues.listBBContacts = DBQuery.getAllBBContacts(mContext);
            }
        }
		/*else if(!saveState.isRegistered(mContext))
		{
			GlobalCommonValues.listBBContacts= new ArrayList<BBContactsBean>();
			this.listBBContacts = new ArrayList<BBContactsBean>();	
		}*/
        _bmp=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
    }

    @Override
    public int getCount()
    {
        return listTiles.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listTiles.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    static class ViewHolder
    {
        LinearLayout llViewBorder;
        FrameLayout flImageHolder;//,flFrameOverlay;
        TextView tvContactName,tvImagePending,tvUnreadMessageCount;
        ImageView imViewUserImage,imViewTag,imViewEmergency;
        RoundedImageViewCircular imViewContactImageRounded;
    }

    @SuppressWarnings("unused")
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tileviewadapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.flImageHolder=(FrameLayout)convertView.findViewById(R.id.flImageHolder);
            holder.tvContactName=(TextView)convertView.findViewById(R.id.tvContactName);
            holder.imViewUserImage=(ImageView)convertView.findViewById(R.id.imViewUserImage);
            holder.imViewContactImageRounded = (RoundedImageViewCircular) convertView.findViewById(R.id.imViewContactImage);
            holder.imViewTag=(ImageView) convertView.findViewById(R.id.imViewTag);
            holder.imViewEmergency=(ImageView)convertView.findViewById(R.id.imViewEmergency);
            holder.tvImagePending=(TextView) convertView.findViewById(R.id.tvImagePending);
            holder.llViewBorder=(LinearLayout) convertView.findViewById(R.id.llViewBorder);
            holder.tvUnreadMessageCount = (TextView) convertView.findViewById(R.id.tvUnreadMessageCount);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        // set shape of image border on the basis of selection in settings
        if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
            holder.flImageHolder.setBackgroundResource(R.drawable.img_back);
            holder.llViewBorder.setBackgroundResource(R.drawable.rectangular_boundary_blue);
            holder.imViewUserImage.setVisibility(View.VISIBLE);
            holder.imViewContactImageRounded.setVisibility(View.GONE);
        }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
            holder.flImageHolder.setBackgroundResource(R.drawable.img_back_circle);
            holder.llViewBorder.setBackgroundResource(R.drawable.rounded_boundary_blue);
            holder.imViewUserImage.setVisibility(View.GONE);
            holder.imViewContactImageRounded.setVisibility(View.VISIBLE);
        }

        mUserBBID = 0;

        holder.imViewTag.setVisibility(View.GONE);
        holder.llViewBorder.setVisibility(View.GONE);
        CustomFonts.setFontOfTextView(mContext,holder.tvContactName, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(mContext,holder.tvImagePending, "fonts/Roboto-Bold_1.ttf");
        if(tilesType.equals("call"))
        {
            holder.llViewBorder.setVisibility(View.GONE);
        }
        else if(tilesType.equals("message"))
        {
            holder.llViewBorder.setVisibility(View.VISIBLE);
        }
        holder.tvUnreadMessageCount.setVisibility(View.GONE);
        try {
            holder.tvContactName.setText(listTiles.get(position).getName());

            // set tag of image on the basis of selection in settings
            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                holder.imViewUserImage.setTag(position);
            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                holder.imViewContactImageRounded.setTag(position);
            }

            holder.tvContactName.setTag(position);
        } catch (Exception e) {
            //system.out.println("----"+e.getMessage());
        }

        if(listTiles.get(position).getImage()!=null && listTiles.get(position).getImage().length>0)
        {
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth));
            params.topMargin=margins;
            params.bottomMargin=margins;
            params.leftMargin=margins;
            params.rightMargin=margins;

            // get image from the object
            byte arrayImage[]=listTiles.get(position).getImage();

            // set image on the basis of selection in settings
            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                holder.imViewUserImage.setLayoutParams(params);
                holder.imViewUserImage.setPadding(1,1,1,1);
                holder.imViewUserImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                holder.imViewContactImageRounded.setLayoutParams(params);
                holder.imViewContactImageRounded.setPadding(1,1,1,1);
                holder.imViewContactImageRounded.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
            }
        }
        else if(listTiles.get(position).getImage()==null || listTiles.get(position).getImage().equals("") ||
                listTiles.get(position).getImage().length<=0){

            // set image on the basis of selection in settings
            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                holder.imViewUserImage.setImageBitmap(_bmp);
            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                holder.imViewContactImageRounded.setImageBitmap(_bmp);
            }
        }

        //		Check if Tile is Emergency

        if(listTiles.get(position).isIsEmergency()){
            holder.imViewEmergency.setVisibility(View.VISIBLE);
        }
        else{
            holder.imViewEmergency.setVisibility(View.GONE);
        }

        if(listTiles.get(position).getIsImagePending()==1){
            holder.tvImagePending.setVisibility(View.VISIBLE);
            holder.tvImagePending.setText(mContext.getResources().getString(R.string.txtImagePending));
            holder.tvImagePending.setTypeface(null,Typeface.BOLD);
            holder.tvImagePending.setTextColor(Color.parseColor("#A7A5A5"));
        }
        else{
            holder.tvImagePending.setVisibility(View.GONE);
        }
        //Check existence of tiles in BBContacts Table to verify if they are BB users
        if(tilesType.equals(mContext.getResources().getString(R.string.txtCall_Small)))
        {
            holder.tvUnreadMessageCount.setVisibility(View.GONE);
            mUserBBID = 0;
            if(listTiles.get(position).getImage()==null || listTiles.get(position).getImage().equals("") ||
                    listTiles.get(position).getImage().length<=0){
                FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth));
                params.topMargin=margins;
                params.bottomMargin=margins;
                params.leftMargin=margins;
                params.rightMargin=margins;

                // set image on the basis of selection in settings
                if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                    holder.imViewUserImage.setLayoutParams(params);
                    int val=dpToPx(5);
                    holder.imViewUserImage.setPadding(val,val,val,val);
                }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                    holder.imViewContactImageRounded.setLayoutParams(params);
                    int val=dpToPx(5);
                    holder.imViewContactImageRounded.setPadding(val,val,val,val);
                }
            }
            else{
                FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth));
                params.topMargin=margins;
                params.bottomMargin=margins;
                params.leftMargin=margins;
                params.rightMargin=margins;

                // set image on the basis of selection in settings
                if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                    holder.imViewUserImage.setLayoutParams(params);
                    int val=dpToPx(0);
                    holder.imViewUserImage.setPadding(val,val,val,val);
                }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                    holder.imViewContactImageRounded.setLayoutParams(params);
                    int val=dpToPx(0);
                    holder.imViewContactImageRounded.setPadding(val,val,val,val);
                }
            }

            // Display Unread Call From a Log and to display the Indicator Logic
            int missedCallCount = 0;

            try{
                missedCallCount = DBQuery.getUnreadCallCount(mContext, listTiles.get(position).getPhoneNumber());
            }catch(Exception e){
                e.getMessage();
            }

            if(missedCallCount > 0){
                holder.tvUnreadMessageCount.setVisibility(View.VISIBLE);
                holder.tvUnreadMessageCount.setBackground(mContext.getResources().getDrawable(R.drawable.indicator_navy_blue));
                holder.tvUnreadMessageCount.setText(String.valueOf(missedCallCount));
            }else{
                holder.tvUnreadMessageCount.setVisibility(View.GONE);
            }

            if (GlobalCommonValues.listBBContacts!=null && !GlobalCommonValues.listBBContacts.isEmpty()){

                String tileCountryCode=listTiles.get(position).getCountryCode();
                String mUserPhone = listTiles.get(position).getPhoneNumber();
                if((tileCountryCode!=null) && (tileCountryCode.trim().equals("") || (tileCountryCode.trim().length()==1 && tileCountryCode.equals("0")))){
                    tileCountryCode=saveState.getCountryCode(mContext);//listTiles.get(position).getPrefix();
                }else if(tileCountryCode == null){
                    tileCountryCode=saveState.getCountryCode(mContext);
                }

                if((tileCountryCode!=null && mUserPhone!=null) && (!tileCountryCode.trim().equals("") && !mUserPhone.trim().equals("")))	{
                    try {
                        //Commented as part of new Functionality
                        //mUserBBID = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,mUserPhone,tileCountryCode);
                        //if(mUserBBID>0){
                        if(listTiles.get(position).isIsTncUser() || listTiles.get(position).getBBID() > 0){
                            holder.llViewBorder.setVisibility(View.VISIBLE);
                            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                            int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth));
                            params.topMargin=margins;
                            params.bottomMargin=margins;
                            params.leftMargin=margins;
                            params.rightMargin=margins;


                            // set image on the basis of selection in settings
                            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                                holder.imViewUserImage.setLayoutParams(params);
                                int val=dpToPx(5);
                                holder.imViewUserImage.setPadding(val,val,val,val);
                            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                                holder.imViewContactImageRounded.setLayoutParams(params);
                                int val=dpToPx(5);
                                holder.imViewContactImageRounded.setPadding(val,val,val,val);
                            }

                            if(!(missedCallCount > 0))
                                holder.tvUnreadMessageCount.setVisibility(View.GONE);

                            // update BBID if there is no bbid exists for a tile in the Tiles table
                            if(listTiles.get(position).getBBID()<=0){
                                ContactTilesBean mContactTileBean = new ContactTilesBean();
                                try{
                                    int mBBIDFromBBContactsTable = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext, mUserPhone, tileCountryCode);

                                    if(mBBIDFromBBContactsTable > 0){

                                        mContactTileBean.setBBID(mBBIDFromBBContactsTable);
                                        mContactTileBean.setButtonType(listTiles.get(position).getButtonType());
                                        mContactTileBean.setCountryCode(listTiles.get(position).getCountryCode());
                                        mContactTileBean.setImage(listTiles.get(position).getImage());
                                        mContactTileBean.setIsEmergency(listTiles.get(position).isIsEmergency());
                                        mContactTileBean.setIsImagePending(listTiles.get(position).getIsImagePending());
                                        mContactTileBean.setIsMobile(listTiles.get(position).isIsMobile());
                                        mContactTileBean.setIsTncUser(listTiles.get(position).isIsTncUser());
                                        mContactTileBean.setName(listTiles.get(position).getName());
                                        mContactTileBean.setPhoneNumber(listTiles.get(position).getPhoneNumber());
                                        mContactTileBean.setPrefix(listTiles.get(position).getPrefix());
                                        mContactTileBean.setTilePosition(listTiles.get(position).getTilePosition());

                                        DBQuery.updateTile(mContext, mContactTileBean);
                                    }

                                }catch(Exception  e){
                                    e.getMessage();
                                }

                            }

                        }else{
                            holder.llViewBorder.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }
        else if(tilesType.equals(mContext.getResources().getString(R.string.txtMessage_Small)))
        {
            mUserBBID = 0;
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth));
            params.topMargin=margins;
            params.bottomMargin=margins;
            params.leftMargin=margins;
            params.rightMargin=margins;

            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                holder.imViewUserImage.setLayoutParams(params);
                int val=dpToPx(5);
                holder.imViewUserImage.setPadding(val,val,val,val);
            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                holder.imViewContactImageRounded.setLayoutParams(params);
                int val=dpToPx(5);
                holder.imViewContactImageRounded.setPadding(val,val,val,val);
            }

            if (GlobalCommonValues.listBBContacts!=null && !GlobalCommonValues.listBBContacts.isEmpty()){

                String tileCountryCode=listTiles.get(position).getCountryCode();
                String mUserPhone = listTiles.get(position).getPhoneNumber();
                if((tileCountryCode!=null) && (tileCountryCode.trim().equals("") || (tileCountryCode.trim().length()==1 && tileCountryCode.equals("0")))){
                    tileCountryCode=saveState.getCountryCode(mContext);//listTiles.get(position).getPrefix();
                }else if(tileCountryCode == null){
                    tileCountryCode=saveState.getCountryCode(mContext);
                }

                if((tileCountryCode!=null && mUserPhone!=null) && (!tileCountryCode.trim().equals("") && !mUserPhone.trim().equals(""))){
                    try {
                        //Commented as part of new Functionality
                        mUserBBID = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,mUserPhone,tileCountryCode);
//						if(mUserBBID>0){
                        if(listTiles.get(position).isIsTncUser()){
                            // Functionality to display unread message count
                            if(!saveState.getBBID(mContext).trim().equals("") && mUserBBID!=Integer.parseInt(saveState.getBBID(mContext))){
                                int mCountMesssage = DBQuery.getUnreadMessageCountOfTnCUser(mContext,mUserBBID);
                                if(mCountMesssage>0){
                                    holder.tvUnreadMessageCount.setVisibility(View.VISIBLE);
                                    holder.tvUnreadMessageCount.setText(String.valueOf(mCountMesssage));
                                }else{
                                    holder.tvUnreadMessageCount.setVisibility(View.GONE);
                                }
                            }else{
                                holder.tvUnreadMessageCount.setVisibility(View.GONE);
                            }

                            // update BBID if there is no bbid exists for a tile in the Tiles table
                            if(listTiles.get(position).getBBID()<=0){
                                ContactTilesBean mContactTileBean = new ContactTilesBean();
                                try{
                                    int mBBIDFromBBContactsTable = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext, mUserPhone, tileCountryCode);

                                    if(mBBIDFromBBContactsTable > 0){

                                        mContactTileBean.setBBID(mBBIDFromBBContactsTable);
                                        mContactTileBean.setButtonType(listTiles.get(position).getButtonType());
                                        mContactTileBean.setCountryCode(listTiles.get(position).getCountryCode());
                                        mContactTileBean.setImage(listTiles.get(position).getImage());
                                        mContactTileBean.setIsEmergency(listTiles.get(position).isIsEmergency());
                                        mContactTileBean.setIsImagePending(listTiles.get(position).getIsImagePending());
                                        mContactTileBean.setIsMobile(listTiles.get(position).isIsMobile());
                                        mContactTileBean.setIsTncUser(listTiles.get(position).isIsTncUser());
                                        mContactTileBean.setName(listTiles.get(position).getName());
                                        mContactTileBean.setPhoneNumber(listTiles.get(position).getPhoneNumber());
                                        mContactTileBean.setPrefix(listTiles.get(position).getPrefix());
                                        mContactTileBean.setTilePosition(listTiles.get(position).getTilePosition());

                                        DBQuery.updateTile(mContext, mContactTileBean);
                                    }
                                }catch(Exception  e){
                                    e.getMessage();
                                }
                            }
                        }else{
                            holder.llViewBorder.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }

        // Check if a chat button user is Chatstasy user, if yes; then display blue frame along tile
        if(holder.llViewBorder.getVisibility()==View.GONE){
            if(listTiles.get(position).isIsTncUser() || listTiles.get(position).getBBID() > 0){
                holder.llViewBorder.setVisibility(View.VISIBLE);
            }
        }

        //update page swipe arrow visibility and page number on UI
        if(position == listTiles.size()-1){
            if(mActionUpdatePageArrowAndNumber != null){
                mActionUpdatePageArrowAndNumber.setAction(GlobalCommonValues.UPDATE_UI_STRING);
            }
        }

        return convertView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
