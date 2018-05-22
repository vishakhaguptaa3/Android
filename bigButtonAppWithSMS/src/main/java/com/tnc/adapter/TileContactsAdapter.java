package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TileContactsAdapter extends BaseAdapter{


    private Context mContext;
    public ArrayList<ContactTilesBean> listTiles;
    public ArrayList<BBContactsBean> listBBContacts = null;
    private SharedPreference saveState;
    private int selectedPosition;
    private boolean isColorSet;
    private Bitmap _bitmapDefaultImage;
    private boolean isShareMode=false;
    public ArrayList<ContactTilesBean> listTilesAdded=new ArrayList<ContactTilesBean>();
    public ArrayList<String> listIdsAdded=new ArrayList<String>();
    private ViewHolder holder;
    private boolean isAllSelected;
    private INotifyGalleryDialog iNotifyUncheckBox;
    public ArrayList<ContactTilesBean> listTilesHomeScreen=new ArrayList<ContactTilesBean>();
    private boolean isDuplicateChecked=false;
    private ArrayList<TextView> mListTextViewCategory = new ArrayList<TextView>();
    private ArrayList<CheckBox> mListImageViewCheckBoxes = new ArrayList<CheckBox>();

    public TileContactsAdapter(Context mContext,ArrayList<ContactTilesBean> listTiles,
                               ArrayList<String> listTilesId,boolean isShareMode,
                               INotifyGalleryDialog iNotifyUncheckBox,boolean isDuplicateChecked)
    {
        this.mContext=mContext;
        this.listTiles=new ArrayList<ContactTilesBean>();
        this.listTiles=listTiles;
        this.isDuplicateChecked=isDuplicateChecked;
        this.iNotifyUncheckBox=iNotifyUncheckBox;
        this.isShareMode=isShareMode;
        this.listBBContacts = new ArrayList<BBContactsBean>();
        mListTextViewCategory    = new ArrayList<TextView>();
        mListImageViewCheckBoxes = new ArrayList<CheckBox>();
        saveState=new SharedPreference();
        if(GlobalCommonValues.listBBContacts!=null && GlobalCommonValues.listBBContacts.isEmpty())
        {
            listBBContacts = DBQuery.getAllBBContacts(mContext);
            GlobalCommonValues.listBBContacts = listBBContacts;
        }
        else if(GlobalCommonValues.listBBContacts!=null){
            this.listBBContacts=GlobalCommonValues.listBBContacts;
        }
        _bitmapDefaultImage=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
        if(isShareMode)
            listTilesHomeScreen=DBQuery.getAllTiles(mContext);

        //for retaining checkbox state
        if(listTilesId!=null && listTilesId.size()>0){
            this.listIdsAdded.addAll(listTilesId);
        }
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

    /**
     *
     * @param isAllSelected
     * set selectedstate of checkbox
     */
    public void setAllSelected(boolean isAllSelected)
    {
        listIdsAdded.clear();
        listTilesAdded.clear();
        this.isAllSelected=isAllSelected;
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return listTiles.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {
        LinearLayout llViewBorder;
        FrameLayout flImageHolder;//,flFrameOverlay;
        TextView tvContactName,tvImagePending,tvContactNumber,tvDuplicate;
        ImageView imViewUserImage,imViewTag,imViewArrow,imViewEmergency;
        CheckBox chkBoxSelection;
    }

    int count = 1;

    @SuppressWarnings({ "deprecation", "unused" })
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder=null;
        String mName = "";
        if ( convertView== null) {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tilelistadapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.flImageHolder=(FrameLayout)convertView.findViewById(R.id.flImageHolder);
            holder.tvContactName=(TextView)convertView.findViewById(R.id.tvContactName);
            holder.tvContactNumber=(TextView) convertView.findViewById(R.id.tvContactNumber);
            holder.tvDuplicate=(TextView) convertView.findViewById(R.id.tvDuplicate);
            holder.imViewUserImage=(ImageView)convertView.findViewById(R.id.imViewUserImage);
            holder.imViewTag=(ImageView) convertView.findViewById(R.id.imViewTag);
            holder.tvImagePending=(TextView) convertView.findViewById(R.id.tvImagePending);
            holder.llViewBorder=(LinearLayout) convertView.findViewById(R.id.llViewBorder);
            holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
            holder.imViewEmergency=(ImageView) convertView.findViewById(R.id.imViewEmergency);
            holder.chkBoxSelection=(CheckBox) convertView.findViewById(R.id.chkBoxSelection);
            convertView.setTag(holder);
        }
        else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        mListTextViewCategory.add(holder.tvContactName);
        mListImageViewCheckBoxes.add(holder.chkBoxSelection);

        convertView.setBackgroundColor(Color.parseColor("#EFEDED"));
        holder.imViewTag.setVisibility(View.GONE);
        holder.llViewBorder.setVisibility(View.GONE);
        holder.tvImagePending.setVisibility(View.GONE);
        holder.tvDuplicate.setVisibility(View.GONE);

        if(isShareMode){
            // in case of sharing / sending contacts to other user
            if(position==0){
                holder.llViewBorder.setVisibility(View.VISIBLE);
            }

            holder.imViewArrow.setVisibility(View.INVISIBLE);
            holder.chkBoxSelection.setVisibility(View.VISIBLE);
            holder.chkBoxSelection.setTag(position);

            //Check for duplicates if tiles of the same number already exists
            if(isDuplicateChecked)
            {
                if(listTilesHomeScreen!=null && listTilesHomeScreen.size()>0)
                {
                    tagLoop:for(int i=0;i<listTilesHomeScreen.size();i++)
                    {
                        if(listTilesHomeScreen.get(i).getPhoneNumber().equals(listTiles.get(position).getPhoneNumber()))
                        {
                            holder.tvDuplicate.setText("Duplicate");
                            holder.tvDuplicate.setVisibility(View.VISIBLE);
                            break tagLoop;
                        }
                        else{
                            holder.tvDuplicate.setVisibility(View.GONE);
                        }
                    }
                }
            }
            try {
                if(isAllSelected){
                    holder.chkBoxSelection.setChecked(true);

                    if(!listTilesAdded.contains(listTiles.get(position))){
                        listTilesAdded.add(listTiles.get(position));
                    }

//                    if(position == 0)
//                        listTilesAdded.addAll(listTiles);

                    String mID = String.valueOf((int)holder.chkBoxSelection.getTag());

                    if(!listIdsAdded.contains(mID))
                        listIdsAdded.add(mID);
                }
                else{
                    /*holder.chkBoxSelection.setChecked(false);
                    if(listTilesAdded!=null && (listIdsAdded==null || listIdsAdded.size()==0))
                        listTilesAdded.clear();*/

                    ContactTilesBean mObjectBBContactsBean = listTiles.get(position);

                    // even if all is not selected then also check whether that list item's checked state is true
                    if(mObjectBBContactsBean.isContactChecked()){
                        holder.chkBoxSelection.setChecked(true);
                        // add item only if it down not exist in the list of added contacts
                        if(!listTilesAdded.contains(mObjectBBContactsBean))
                            listTilesAdded.add(mObjectBBContactsBean);

                        String id = String.valueOf((int)holder.chkBoxSelection.getTag());
                        if(listIdsAdded!=null && !listIdsAdded.contains(id))
                            listIdsAdded.add(id);

                    }else if(!mObjectBBContactsBean.isContactChecked()){
                        holder.chkBoxSelection.setChecked(false);
                        if(listTilesAdded.contains(mObjectBBContactsBean))
                            listTilesAdded.remove(mObjectBBContactsBean);

                        String id = String.valueOf((int)holder.chkBoxSelection.getTag());
                        if(listIdsAdded!=null && listIdsAdded.contains(id))
                            listIdsAdded.remove(id);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }

            String mCheckBoxId = String.valueOf((int)holder.chkBoxSelection.getTag());

            if(listIdsAdded!=null && listIdsAdded.size()>0){
                tagloop: for(int i = 0; i<listIdsAdded.size();i++){
                    if(Integer.parseInt(mCheckBoxId)==Integer.parseInt(listIdsAdded.get(i))){
                        holder.chkBoxSelection.setChecked(true);
                        if(!listTilesAdded.contains(listTiles.get(position)))
                            listTilesAdded.add(listTiles.get(position));
                        break tagloop;
                    }
                }
            }

            holder.tvContactName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        int mPosition = Integer.parseInt(String.valueOf(view.getTag()));
                        if(mPosition > -1){
                            isAllSelected = false;
                            mListImageViewCheckBoxes.get(mPosition).performClick();
                        }
                    }catch(Exception e){
                        e.getMessage();
                    }
                }
            });

            holder.chkBoxSelection.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAllSelected = false;
                    CheckBox cb = (CheckBox)v;
                    String id = String.valueOf((int)cb.getTag());
                    if(cb.isChecked()){
                        if(listIdsAdded!=null && !listIdsAdded.contains(id)){
                            listIdsAdded.add(id);
                            listTilesAdded.add(listTiles.get(position));

                            //Set Checked State
                            listTiles.get((int)cb.getTag()).setContactChecked(true);

                        }
                    }else {
                        if(listIdsAdded.contains(id)){
                            listIdsAdded.remove(id);
                            ContactTilesBean objContactDetails=new ContactTilesBean();
                            objContactDetails=listTiles.get((int)cb.getTag());
                            if(listTilesAdded.contains(objContactDetails))
                                listTilesAdded.remove(objContactDetails);

                            //Set Checked State
                            listTiles.get((int)cb.getTag()).setContactChecked(false);
                        }
                    }
					/*if(listTilesAdded.size()<=10)
					{*/
					/*if(cb.isChecked()){
						listTilesAdded.add(listTiles.get(position));
					}
					else{
						ContactTilesBean objContactDetails=new ContactTilesBean();
						objContactDetails=listTiles.get((int)cb.getTag());
						listTilesAdded.remove(objContactDetails);
					}*/
                    if(iNotifyUncheckBox!=null)
                    {
                        iNotifyUncheckBox.yes();
                    }
                    //					}
					/*else if(listTilesAdded.size()>10)
					{
						ImageRequestDialog dialog =new ImageRequestDialog();
						dialog.setCancelable(false);
						dialog.newInstance("", mContext,"Cannot send more than 10 contacts","",null);
						dialog.show(((HomeScreenActivity)mContext).getSupportFragmentManager(), "test");
					}*/
                }
            });
        }else if(!isShareMode){ // in case of importing shared contacts
            holder.chkBoxSelection.setVisibility(View.VISIBLE);
            holder.imViewArrow.setVisibility(View.VISIBLE);
            //////////////////////////////////////////////////
            try {
                if(isAllSelected)
                {
                    holder.chkBoxSelection.setChecked(true);

                    // add item only if it down not exist in the list of added contacts
                    if(!listTilesAdded.contains(listTiles.get(position)))
                        listTilesAdded.add(listTiles.get(position));
                }else{
                    ContactTilesBean mObjectBBContactsBean = listTiles.get(position);

                    // even if all is not selected then also check whether that list item's checked state is true
                    if(mObjectBBContactsBean.isContactChecked()){
                        holder.chkBoxSelection.setChecked(true);
                        // add item only if it down not exist in the list of added contacts
                        if(!listTilesAdded.contains(mObjectBBContactsBean))
                            listTilesAdded.add(mObjectBBContactsBean);

                    }else if(!mObjectBBContactsBean.isContactChecked()){
                        holder.chkBoxSelection.setChecked(false);
                        if(listTilesAdded.contains(mObjectBBContactsBean))
                            listTilesAdded.remove(mObjectBBContactsBean);
                    }
                }

                holder.tvContactName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            int mPosition = Integer.parseInt(String.valueOf(view.getTag()));
                            if(mPosition > -1){
                                isAllSelected = false;
                                mListImageViewCheckBoxes.get(mPosition).performClick();
                            }
                        }catch(Exception e){
                            e.getMessage();
                        }
                    }
                });

                holder.chkBoxSelection.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllSelected = false;
                        CheckBox cb = (CheckBox)v;
                        String id = String.valueOf((int)cb.getTag());
                        if(cb.isChecked()){
                            if(listIdsAdded!=null && !listIdsAdded.contains(id)){
                                listIdsAdded.add(id);
                                listTilesAdded.add(listTiles.get(position));

                                //Set Checked State
                                listTiles.get((int)cb.getTag()).setContactChecked(true);
                            }
                        }else {
                            if(listIdsAdded.contains(id)){
                                listIdsAdded.remove(id);
                                ContactTilesBean objContactDetails=new ContactTilesBean();
                                objContactDetails=listTiles.get((int)cb.getTag());
                                if(listTilesAdded.contains(objContactDetails))
                                    listTilesAdded.remove(objContactDetails);

                                //Set Checked State
                                listTiles.get((int)cb.getTag()).setContactChecked(false);
                            }
                        }
                        if(iNotifyUncheckBox!=null)
                        {
                            iNotifyUncheckBox.yes();
                        }
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
        }

        CustomFonts.setFontOfTextView(mContext,holder.tvContactName, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(mContext,holder.tvImagePending, "fonts/Roboto-Bold_1.ttf");
        mName = listTiles.get(position).getName();//Ram&#39;s iPhone
        if(mName.contains("&#39;"))
            mName = mName.replaceAll("&#39;","'");
        holder.tvContactName.setText(Uri.decode(mName));
        holder.tvContactNumber.setText(listTiles.get(position).getPrefix()+listTiles.get(position).getCountryCode()+listTiles.get(position).getPhoneNumber());
        if(listTiles.get(position).getImage()!=null && listTiles.get(position).getImage().length>0){
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth_tileContacts));
            params.topMargin=margins;
            params.bottomMargin=margins;
            params.leftMargin=margins;
            params.rightMargin=margins;
            holder.imViewUserImage.setLayoutParams(params);
            holder.imViewUserImage.setPadding(1,1,1,1);
            byte arrayImage[]=listTiles.get(position).getImage();
            holder.imViewUserImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
        }
        else{
            holder.imViewUserImage.setImageBitmap(_bitmapDefaultImage);
        }
        if(listTiles.get(position).isIsEmergency())
            holder.imViewEmergency.setVisibility(View.VISIBLE);
        else
            holder.imViewEmergency.setVisibility(View.GONE);

        if(listTiles.get(position).getIsImagePending()==1)
        {
            holder.tvImagePending.setVisibility(View.VISIBLE);
            holder.tvImagePending.setText(mContext.getResources().getString(R.string.txtImagePending));
            holder.tvImagePending.setTypeface(null,Typeface.NORMAL);
            holder.tvImagePending.setTextColor(Color.parseColor("#A7A5A5"));
        }
        else{
            holder.tvImagePending.setVisibility(View.GONE);
        }

        if(isColorSet && position==selectedPosition)
        {
            ((RelativeLayout) convertView).setBackgroundColor(mContext
                    .getResources().getColor(R.color.stripDarkBlueColor));
            holder.tvContactName.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.tvContactNumber.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_white));
        }
        else{
            ((RelativeLayout) convertView).setBackgroundColor(mContext
                    .getResources().getColor(R.color.white));
            holder.tvContactName.setTextColor(mContext.getResources().getColor(
                    R.color.textBlueColor));
            holder.tvContactNumber.setTextColor(mContext.getResources().getColor(
                    android.R.color.black));
            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_blue));

            if(position%2==0)
            {
                convertView.setBackgroundColor(Color.parseColor("#EFEDED"));
            }
            else
            {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

        //Identify Tnc User

        if(listTiles.get(position).getImage()==null || listTiles.get(position).getImage().equals("") ||
                listTiles.get(position).getImage().length<=0)
        {
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth_tileContacts));
            params.topMargin=margins;
            params.bottomMargin=margins;
            params.leftMargin=margins;
            params.rightMargin=margins;
            holder.imViewUserImage.setLayoutParams(params);
            int val=dpToPx(2);
            holder.imViewUserImage.setPadding(val,val,val,val);
        }
        else{
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            int margins=1;//(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth_tileContacts));
            params.topMargin=margins;
            params.bottomMargin=margins;
            params.leftMargin=margins;
            params.rightMargin=margins;
            holder.imViewUserImage.setLayoutParams(params);
            int val=dpToPx(0);
            holder.imViewUserImage.setPadding(val,val,val,val);
        }
        if (listBBContacts!=null && !listBBContacts.isEmpty())
        {
            if(!(listTiles.get(position).getPrefix()+listTiles.get(position).getCountryCode()+listTiles.get(position).getPhoneNumber()).trim().equals(""))
            {
                String num=listTiles.get(position).getPrefix()+listTiles.get(position).getCountryCode()+listTiles.get(position).getPhoneNumber();
                String strPhone =num;// GlobalConfig_Methods.trimSpecialCharactersFromString(num);
                if(saveState.isRegistered(mContext))
                {
                    String number=GlobalConfig_Methods.getBBNumberToCheck(mContext,strPhone);
                    String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
                    boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
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

                    if (isTncUserRegisteredUser){
                        //							holder.imViewTag.setVisibility(View.VISIBLE);
                        holder.llViewBorder.setVisibility(View.VISIBLE);
                        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                        int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth_tileContacts));
                        params.topMargin=margins;
                        params.bottomMargin=margins;
                        params.leftMargin=margins;
                        params.rightMargin=margins;
                        holder.imViewUserImage.setLayoutParams(params);
                        int val=dpToPx(2);
                        holder.imViewUserImage.setPadding(val,val,val,val);
                    }
                    else {
                        // to display our own contact as TnC User at the very first position in contact sharing mode.
                        holder.llViewBorder.setVisibility(View.GONE);
                        if(isShareMode){
                            if(position==0){
                                holder.llViewBorder.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        }

        if(holder.llViewBorder.getVisibility()==View.GONE){
            if(listTiles.get(position).isIsTncUser()){
                holder.llViewBorder.setVisibility(View.VISIBLE);
            }
        }

        /*if(position == listTiles.size()-1){
            Log.i("count :-" + count +"  -- SIZE -- :- ", "" + listTilesAdded.size() + " : " + listIdsAdded.size());
        }*/

        return convertView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
