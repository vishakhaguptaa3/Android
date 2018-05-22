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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TileContactsAdapterChangeButton extends BaseAdapter{


	private Context mContext;
	public ArrayList<ContactTilesBean> listTiles;
	public ArrayList<BBContactsBean> listBBContacts = null;
	private SharedPreference saveState;
	private int selectedPosition;
	private boolean isColorSet;	
	private Bitmap _bitmapDefaultImage;
	private boolean isShareMode=false;
	public ArrayList<ContactTilesBean> listTilesAdded=new ArrayList<ContactTilesBean>();
	private ArrayList<String> listIdsAdded=new ArrayList<String>();
	private ViewHolder holder;
	private boolean isAllSelected;
	private INotifyGalleryDialog iNotifyUncheckBox;
	public ArrayList<ContactTilesBean> listTilesHomeScreen=new ArrayList<ContactTilesBean>();
	private boolean isDuplicateChecked=false;
	private int mUserBBID = 0;

	public TileContactsAdapterChangeButton(Context mContext,ArrayList<ContactTilesBean> listTiles,boolean isShareMode,
			INotifyGalleryDialog iNotifyUncheckBox,boolean isDuplicateChecked)
	{
		this.mContext=mContext;
		this.listTiles=new ArrayList<ContactTilesBean>();
		this.listTiles=listTiles;
		this.isDuplicateChecked=isDuplicateChecked;
		this.iNotifyUncheckBox=iNotifyUncheckBox;
		this.isShareMode=isShareMode;
		this.listBBContacts = new ArrayList<BBContactsBean>();
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

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		mUserBBID = 0;
		holder=null;
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
		convertView.setBackgroundColor(Color.parseColor("#EFEDED"));
		holder.imViewTag.setVisibility(View.GONE);
		holder.llViewBorder.setVisibility(View.GONE);
		holder.tvImagePending.setVisibility(View.GONE);
		holder.tvDuplicate.setVisibility(View.GONE);
		holder.chkBoxSelection.setVisibility(View.GONE);
		if(isShareMode)
		{
			if(position==0){
				holder.llViewBorder.setVisibility(View.VISIBLE);
			}

			holder.imViewArrow.setVisibility(View.INVISIBLE);
			//			holder.chkBoxSelection.setVisibility(View.VISIBLE);
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
							holder.tvDuplicate.setText(mContext.getResources().getString(R.string.txtDuplicate));
							holder.tvDuplicate.setVisibility(View.VISIBLE);
							break tagLoop;
						}
						else{
							holder.tvDuplicate.setVisibility(View.GONE);
						}
					}
				}
			}
		}

		else if(!isShareMode){
			//			holder.chkBoxSelection.setVisibility(View.VISIBLE);
			holder.imViewArrow.setVisibility(View.VISIBLE);
		}

		CustomFonts.setFontOfTextView(mContext,holder.tvContactName, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(mContext,holder.tvImagePending, "fonts/Roboto-Bold_1.ttf");
		holder.tvContactName.setText(listTiles.get(position).getName());
		holder.tvContactNumber.setText(listTiles.get(position).getPrefix()+listTiles.get(position).getCountryCode()+listTiles.get(position).getPhoneNumber());
		if(listTiles.get(position).getImage()!=null && listTiles.get(position).getImage().length>0)

		{
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
				mUserBBID = 0;

				String tileCountryCode=listTiles.get(position).getCountryCode();
				String mUserPhone = listTiles.get(position).getPhoneNumber();
				if((tileCountryCode!=null) && (tileCountryCode.trim().equals("") || (tileCountryCode.trim().length()==1 && tileCountryCode.equals("0")))){
					tileCountryCode=saveState.getCountryCode(mContext);//listTiles.get(position).getPrefix();							
				}else if(tileCountryCode == null){
					tileCountryCode=saveState.getCountryCode(mContext);
				}

				if((tileCountryCode!=null && mUserPhone!=null) && (!tileCountryCode.trim().equals("") && !mUserPhone.trim().equals("")))	{

					try {
						mUserBBID = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,mUserPhone,tileCountryCode);
						if(mUserBBID>0){
							// Functionality to blue frame of TnC User
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

							/*if(!saveState.getBBID(mContext).trim().equals("") && mUserBBID!=Integer.parseInt(saveState.getBBID(mContext))){

							}else{

							}*/
						}else{
							holder.llViewBorder.setVisibility(View.GONE);
							if(isShareMode){
								if(position==0){
									holder.llViewBorder.setVisibility(View.VISIBLE);
								}
							}
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}

				//				String num=listTiles.get(position).getPrefix()+listTiles.get(position).getCountryCode()+listTiles.get(position).getPhoneNumber();
				//				String strPhone =num;// GlobalConfig_Methods.trimSpecialCharactersFromString(num);
				//				if(saveState.isRegistered(mContext))
				//				{
				//					String number=GlobalConfig_Methods.getBBNumberToCheck(mContext,strPhone);
				//					String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
				//					boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
				//					try {
				//						String []arrayUserDetails=number.split(",");
				//						countryCodeRegisteredUser=arrayUserDetails[0];
				//						numberRegisteredUser=arrayUserDetails[1];
				//						isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
				//						isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
				//						isdCodeRegisteredUser=arrayUserDetails[4];
				//						isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);
				//					} catch (Exception e) {
				//						e.getMessage();
				//					}
				//
				//					if (isTncUserRegisteredUser){
				//						//							holder.imViewTag.setVisibility(View.VISIBLE);
				//						holder.llViewBorder.setVisibility(View.VISIBLE);
				//						FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				//						int margins=(int)(mContext.getResources().getDimension(R.dimen.imageBorderWidth_tileContacts));
				//						params.topMargin=margins;
				//						params.bottomMargin=margins;
				//						params.leftMargin=margins;
				//						params.rightMargin=margins;
				//						holder.imViewUserImage.setLayoutParams(params);
				//						int val=dpToPx(2);
				//						holder.imViewUserImage.setPadding(val,val,val,val);
				//					}
				//					else {
				//						holder.llViewBorder.setVisibility(View.GONE);
				//						if(isShareMode){
				//							if(position==0){
				//								holder.llViewBorder.setVisibility(View.VISIBLE);
				//							}
				//						}
				//					}
				//				}
			}
		}

		if(holder.llViewBorder.getVisibility()==View.GONE){
			if(listTiles.get(position).isIsTncUser()){
				holder.llViewBorder.setVisibility(View.VISIBLE);
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
