package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoMenuAdapter extends BaseAdapter 
{
	private Context mContext;
	public ArrayList<String> listInfoMenu;
	private int selectedPosition;
	private boolean isColorSet=false;

	public InfoMenuAdapter(Context mContext,ArrayList<String> listInfoMenu)
	{
		this.mContext = mContext;
		this.listInfoMenu=new ArrayList<String>();
		this.listInfoMenu=listInfoMenu;
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
		return listInfoMenu.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return listInfoMenu.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	static class ViewHolder{
		TextView tvMenuTitle,tvMenuDetail;
		ImageView imViewMenuIcon,imViewArrow;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "DefaultLocale", "CutPasteId" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=null;
		//reuse views
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.info_menu_adapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvMenuTitle=(TextView) convertView.findViewById(R.id.tvMenuTitle);
			holder.tvMenuDetail=(TextView) convertView.findViewById(R.id.tvMenuDetail);
			holder.imViewMenuIcon=(ImageView) convertView.findViewById(R.id.imViewMenuIcon);
			holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		if(isColorSet && position == selectedPosition){
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
			TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
			TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
			tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.white));	
			tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.white));
			ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
			imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
			ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			if(selectedPosition==0) // HOW TO
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.how_white));
			}
			else if(selectedPosition==1) //FAQ
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.faq_white));
			}
			else if(selectedPosition==2) // RATE THE APP
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rates_white));
			}
			else if(selectedPosition==3) // INTERNATIONAL EMERGENCY NUMBER
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.international_emergency_white));
			}
			else if(selectedPosition==4)// SOCIAL SHARING
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.share_white));
			}
			else if(selectedPosition==5) // Privacy Policy / Terms Of Use
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.privacy_white));
			}
			else if(selectedPosition==6) // CONTACT US
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.incoming_white));
			}
		}
		else{
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
			TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
			TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
			tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));	
			tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.textGreyColorChooseConatct));
			ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
			imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));
			ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			if(position==0)  // HOW TO
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.how));
			}
			else if(position==1) //FAQ
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.faq));
			}
			else if(position==2) // RATE THE APP
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rates));
				SharedPreference saveState = new SharedPreference();
				 if(saveState.getIS_UNDERAGE(mContext) && saveState.isRegistered(mContext)){
					holder.tvMenuTitle.setTextColor(Color.parseColor("#9c9c9c"));
				}else{
					holder.tvMenuTitle.setTextColor(Color.parseColor("#555555"));
				}
			}
			else if(position==3) // INTERNATIONAL EMERGENCY NUMBER
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.international_emergency));
			}
			else if(position==4)  // SOCIAL SHARING
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.share));
				SharedPreference saveState = new SharedPreference();
				 if(saveState.getIS_UNDERAGE(mContext) && saveState.isRegistered(mContext)){
					holder.tvMenuTitle.setTextColor(Color.parseColor("#9c9c9c"));
				}else{
					holder.tvMenuTitle.setTextColor(Color.parseColor("#555555"));
				}
			}
			else if(position==5) // Privacy Policy / Terms Of Use
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.privacy));
			}else if(position==6) // CONTACT US
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.incoming));
			}
		}
		holder.tvMenuTitle.setText(listInfoMenu.get(position));

		return convertView;
	}
}
