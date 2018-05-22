package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TilesAdapter extends BaseAdapter
{
	private ArrayList<ContactTilesBean> listTiles;
	private ArrayList<ContactTilesBean> listTilesDisplay;
	private ArrayList<BBContactsBean> listBBContacts = null;
	private int view_item_width=0;
	private Context mContext;

	public TilesAdapter(Context mContext,ArrayList<ContactTilesBean> listTiles,int view_item_width)
	{
		this.mContext=mContext;
		this.listTiles=new ArrayList<ContactTilesBean>();
		this.listTilesDisplay=new ArrayList<ContactTilesBean>();
		this.listBBContacts = new ArrayList<BBContactsBean>();
		if(GlobalCommonValues.listBBContacts!=null && GlobalCommonValues.listBBContacts.isEmpty())
		{
			listBBContacts = DBQuery.getAllBBContacts(mContext);
			GlobalCommonValues.listBBContacts = listBBContacts;
		}
		else if(GlobalCommonValues.listBBContacts!=null){
			this.listBBContacts=GlobalCommonValues.listBBContacts;
		}
		/*this.listBBContacts = DBQuery.getAllBBContacts(mContext);	
		GlobalCommonValues.listBBContacts=listBBContacts;*/
		this.listTiles=listTiles;
		this.view_item_width=view_item_width;

		//Check existence of tiles in BBContacts Table to verify if they are BB users
		if (listBBContacts!=null && !listBBContacts.isEmpty()) 
		{
			for(int countListTiles=0;countListTiles<listTiles.size();countListTiles++)
			{
				tagloop:for (int k = 0; k < listBBContacts.size(); k++) 
				{
					String strPhone = listTiles.get(countListTiles).getPhoneNumber();
					String contactNumber = "";
					GlobalConfig_Methods.trimSpecialCharactersFromString(strPhone);
					contactNumber = strPhone;
					try 
					{
						if (contactNumber.equals(listBBContacts.get(k).getPhoneNumber())) 
						{
							listTilesDisplay.add(listTiles.get(countListTiles));
							break tagloop;
						}
					}
					catch (Exception e) 
					{
						e.getMessage();
					}
				}
			}
		}
	}
	@Override
	public int getCount() 
	{
		return listTilesDisplay.size();
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
		FrameLayout flImageHolder;
		TextView tvContactName;
		ImageView imViewUserImage,imViewTag;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.tileviewadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.flImageHolder=(FrameLayout)convertView.findViewById(R.id.flImageHolder);
			holder.tvContactName=(TextView)convertView.findViewById(R.id.tvContactName);
			holder.imViewUserImage=(ImageView)convertView.findViewById(R.id.imViewUserImage);
			holder.imViewTag=(ImageView) convertView.findViewById(R.id.imViewTag);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvContactName.setSelected(true);
		CustomFonts.setFontOfTextView(mContext,holder.tvContactName, "fonts/Roboto-Regular_1.ttf");
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.height=view_item_width-20;
		params.width=view_item_width-25;
		holder.flImageHolder.setLayoutParams(params);
		holder.imViewTag.setVisibility(View.VISIBLE);
		holder.tvContactName.setText(listTilesDisplay.get(position).getName());
		byte arrayImage[]=listTilesDisplay.get(position).getImage();
		if(arrayImage!=null && arrayImage.length>0)
		{
			holder.imViewUserImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
		}
		/*if (listBBContacts!=null && !listBBContacts.isEmpty()) 
		{
			tagloop:for (int k = 0; k < listBBContacts.size(); k++) 
			{
				String strPhone = listTiles.get(position).getPhoneNumber();
				String contactNumber = "";
				GlobalConfig_Methods.trimSpecialCharactersFromString(strPhone);
				contactNumber = strPhone;
				try 
				{
					if (contactNumber.equals(listBBContacts.get(k).getPhoneNumber())) 
					{
						holder.imViewTag.setVisibility(View.VISIBLE);
						holder.tvContactName.setText(listTiles.get(position).getName());
						byte arrayImage[]=listTiles.get(position).getImage();
						if(arrayImage!=null && arrayImage.length>0)
						{
							holder.imViewUserImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
						}
						break tagloop;
					}
					else 
					{
						holder.imViewTag.setVisibility(View.GONE);
					}
				}
				catch (Exception e) 
				{
					e.getMessage();
				}
			}
		}*/
		return convertView;
	}
}
