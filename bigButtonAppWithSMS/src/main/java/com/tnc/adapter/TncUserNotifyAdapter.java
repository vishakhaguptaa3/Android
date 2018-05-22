package com.tnc.adapter;

import java.util.ArrayList;
import java.util.Locale;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.Name;
import com.tnc.database.DBQuery;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TncUserNotifyAdapter extends BaseAdapter
{
	private Context mContext;
	public ArrayList<BBContactsBean> listContacts;
	private ArrayList<BBContactsBean> listContactsFiltered;
	private ArrayList<BBContactsBean> listFirstName = null;
	private ArrayList<BBContactsBean> listLastName = null;
	private ArrayList<BBContactsBean> listContainsName = null;
	private int selectedPosition;
	private boolean isColorSet;
	private ImageLoadTask imageLoader=null;
	private Bitmap _bmp=null;
	private SharedPreference pref=null;
	private int BBID=0;
	private Bitmap displayImage=null;
	private String displayName="";
	private String phoneNumber="";
	private ArrayList<ContactTilesBean> listContactTiles=null;
	private ArrayList<BBContactsBean> listBBContacts=null;
	private INotifyGalleryDialog iNotifyUncheckBox;
	private boolean isAllSelected;
	public ArrayList<BBContactsBean> listTncContactsAdded=new ArrayList<BBContactsBean>();

	public TncUserNotifyAdapter(Context mContext,
			ArrayList<BBContactsBean> listContacts,INotifyGalleryDialog iNotifyUncheckBox)
	{
		this.mContext=mContext;
		this.listContacts=new ArrayList<BBContactsBean>();
		this.listContacts=listContacts;
		this.listContactsFiltered = new ArrayList<BBContactsBean>();
		this.listContactsFiltered.addAll(listContacts);
		this.iNotifyUncheckBox=iNotifyUncheckBox;
		_bmp=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
		pref=new SharedPreference();
	}

	/**
	 * 
	 * @param position
	 * @param isColorSet
	 * set selected List item row color and content color
	 */
	public void setRowColor(int position, boolean isColorSet) {
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
		return listContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return listContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		LinearLayout llContactNameHolder;
		TextView tvContactName;
		ImageView imViewTag;
		RoundedImageViewCircular imViewContactImage;
		CheckBox chkBoxSelection;
	}

	ViewHolder holder;

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = null;
		BBID=0;
		displayImage=null;

		// reuse views
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.tncusersnotifyadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvContactName = (TextView) convertView
					.findViewById(R.id.tvContactName);
			holder.imViewTag = (ImageView) convertView
					.findViewById(R.id.imViewTag);

			holder.imViewContactImage=(RoundedImageViewCircular)convertView.findViewById(R.id.imViewContactImage);
			holder.llContactNameHolder=(LinearLayout) convertView.findViewById(R.id.llContactNameHolder);
			holder.chkBoxSelection=(CheckBox) convertView.findViewById(R.id.chkBoxSelection);
			convertView.setTag(holder);
		} else {
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}

		holder.chkBoxSelection.setVisibility(View.VISIBLE);
		holder.chkBoxSelection.setTag(position);

		if(isAllSelected)
		{
			holder.chkBoxSelection.setChecked(true);
			listTncContactsAdded.add(listContacts.get(position));
		}
		else{
			holder.chkBoxSelection.setChecked(false);
			if(listTncContactsAdded!=null)
				listTncContactsAdded.clear();
		}

		holder.chkBoxSelection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				if(cb.isChecked())
				{
					listTncContactsAdded.add(listContacts.get(position));
				}
				else{
					BBContactsBean objContactDetails=new BBContactsBean();
					objContactDetails=listContacts.get((int)cb.getTag());
					listTncContactsAdded.remove(objContactDetails);
				}
				if(iNotifyUncheckBox!=null)	
				{
					iNotifyUncheckBox.yes();
				}
			}
		});

		listBBContacts=new ArrayList<BBContactsBean>();
		try {
			if (isColorSet && position == selectedPosition) {
				holder.tvContactName.setTextColor(mContext.getResources().getColor(
						R.color.white));
				((RelativeLayout) convertView).setBackgroundColor(mContext
						.getResources().getColor(R.color.stripDarkBlueColor));
			} else {
				holder.tvContactName.setTextColor(mContext.getResources().getColor(
						R.color.textGreyColorChooseConatct));
				((RelativeLayout) convertView).setBackgroundColor(mContext
						.getResources().getColor(R.color.white));
			}
			CustomFonts.setFontOfTextView(mContext, holder.tvContactName,"fonts/Roboto-Regular_1.ttf");
			holder.imViewContactImage.setVisibility(View.VISIBLE);
			holder.llContactNameHolder.setVisibility(View.VISIBLE);
			holder.imViewTag.setVisibility(View.GONE);
			holder.llContactNameHolder.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notificationimageborder));
			holder.tvContactName.setText(listContacts.get(position).getName());
			phoneNumber=listContacts.get(position).getPhoneNumber();
			listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,GlobalConfig_Methods.trimSpecialCharactersFromString(phoneNumber));
			displayName="";
		} catch (Exception e) {
			e.getMessage();
		}
		if(listContactTiles.size()>0)  // In case of Tile of the number exists
		{
			displayName=listContactTiles.get(0).getName();
			if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0)
			{
				byte arrayImage[]=listContactTiles.get(0).getImage();
				if(arrayImage!=null && arrayImage.length>0)
				{
					holder.imViewContactImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
				}
			}
			else {
				listBBContacts=DBQuery.checkBBContactExistence(mContext,listContacts.get(position).getBBID());
				if(listBBContacts.size()>0)
				{
					BBID=listBBContacts.get(0).getBBID();
				}
				if(BBID!=0)
				{
					if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
					{
						imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContactImage,320);
						imageLoader.execute();
					}
					else{
						displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
						if(displayImage!=null)
						{
							holder.imViewContactImage.setImageBitmap(displayImage);
						}
						else{
							holder.imViewContactImage.setImageBitmap(_bmp);
						}
					}
				}
				else{
					displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
					if(displayImage!=null)
					{
						holder.imViewContactImage.setImageBitmap(displayImage);
					}
					else{
						holder.imViewContactImage.setImageBitmap(_bmp);
					}
				}
			}
		}
		else { 
			// In case of tile of that number doesn't exists
			listBBContacts=DBQuery.checkBBContactExistence(mContext,listContacts.get(position).getBBID());
			if(listBBContacts.size()>0)
			{
				BBID=listBBContacts.get(0).getBBID();
			}
			if(BBID!=0)
			{
				displayName=listBBContacts.get(0).getName();
				if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
				{
					imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContactImage,320);
					imageLoader.execute();
				}
				else{
					displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
					if(displayImage!=null)
					{
						holder.imViewContactImage.setImageBitmap(displayImage);
					}
					else{
						holder.imViewContactImage.setImageBitmap(_bmp);
					}
				}
			}
			else{
				if(!phoneNumber.equals(""))
				{
					displayName=GlobalConfig_Methods.getContactName(mContext, phoneNumber);
					displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
				}
				if(displayImage!=null)
				{
					holder.imViewContactImage.setImageBitmap(displayImage);
				}
				else{
					holder.imViewContactImage.setImageBitmap(_bmp);
				}
			}
		}
		if(!displayName.trim().equalsIgnoreCase(""))
			holder.tvContactName.setText(displayName);
		else if(listContacts.get(position).getName().equalsIgnoreCase("No Contact Found") || listContacts.get(position).getName().equalsIgnoreCase("No matching record found"))
		{
			holder.tvContactName.setText(listContacts.get(position).getName());
			holder.chkBoxSelection.setVisibility(View.GONE);
		}
		if(listContacts.get(position).getName().equalsIgnoreCase("No Contact Found") || listContacts.get(position).getName().equalsIgnoreCase("No matching record found"))
		{
			holder.imViewContactImage.setVisibility(View.GONE);
			holder.llContactNameHolder.setVisibility(View.GONE);
			holder.imViewTag.setVisibility(View.GONE);
			holder.llContactNameHolder.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * @param :instance of text to be filtered in list
	 *filters the contents of the list as per input given in the searchview
	 */
	public void filterData(String charText) 
	{
		charText = charText.toLowerCase(Locale.getDefault());
		if(listContacts!=null && !listContacts.isEmpty())
		{
			listContacts.clear();
			if (charText.trim().length() == 0) 
			{
				listContacts.addAll(listContactsFiltered);
			} 
			else 
			{
				listFirstName = new ArrayList<BBContactsBean>();
				listLastName = new ArrayList<BBContactsBean>();
				listContainsName = new ArrayList<BBContactsBean>();
				Name name;
				for (BBContactsBean contactDetailBean : listContactsFiltered) {
					name = checkName(
							contactDetailBean.getName().toLowerCase(
									Locale.getDefault()), charText);
					if (name != null) {
						if (name.isFirst) {
							listFirstName.add(contactDetailBean);
						} else if (name.isLast) {
							listLastName.add(contactDetailBean);
						} else if (name.isContainer) {
							listContainsName.add(contactDetailBean);
						}
					}
				}
				showList();
			}
		}
	}

	/*
	 * Check Existence of Name
	 */
	private Name checkName(String contactName, String searchString) {
		String nameArray[] = contactName.split(" ");
		if (nameArray != null && nameArray.length > 0) {
			Name name;
			int nameLength = nameArray.length;
			if (nameArray[0].startsWith(searchString)) {
				name = new Name();
				name.isFirst = true;
				return name;
			} else if (nameArray[nameLength - 1].startsWith(searchString)) {
				name = new Name();
				name.isLast = true;
				return name;
			} else if (contactName.contains(searchString)) {
				name = new Name();
				name.isContainer = true;
				return name;
			}
		}
		return null;
	}

	//display filtered contact List
	private void showList() {
		listContacts.clear();

		if (listFirstName != null && listFirstName.size() > 0) {
			listContacts.addAll(listFirstName);
		}

		if (listLastName != null && listLastName.size() > 0) {
			listContacts.addAll(listLastName);
		}

		if (listContainsName != null && listContainsName.size() > 0) {
			listContacts.addAll(listContainsName);
		}
		this.notifyDataSetInvalidated();
	}
}
