package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.database.DBQuery;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CloudBackupAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<String> listCloudBackupMenu;
	private ArrayList<String> listCloudBackupMenuDetails;
	private int selectedPosition;
	private boolean isColorSet=false;
	private SharedPreference saveState;

	public CloudBackupAdapter(Activity mContext,ArrayList<String> listCloudBackupMenu,
			ArrayList<String> listCloudBackupMenuDetails)
	{
		this.mContext = mContext;
		this.listCloudBackupMenu=new ArrayList<String>();
		this.listCloudBackupMenuDetails=new ArrayList<String>();
		this.listCloudBackupMenu=listCloudBackupMenu;
		this.listCloudBackupMenuDetails=listCloudBackupMenuDetails;
		saveState=new SharedPreference(); 
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
		return listCloudBackupMenu.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return listCloudBackupMenu.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	static class ViewHolder
	{
		TextView tvMenuTitle,tvMenuDetail;
		ImageView imViewMenuIcon;
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
			convertView = inflater.inflate(R.layout.cloudbackup_adapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvMenuTitle=(TextView) convertView.findViewById(R.id.tvMenuTitle);
			holder.tvMenuDetail=(TextView) convertView.findViewById(R.id.tvMenuDetail);
			holder.imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		//		CustomFonts.setFontOfTextView(mContext,holder.tvMenuTitle, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(mContext,holder.tvMenuDetail, "fonts/Roboto-Regular_1.ttf");
		if(isColorSet && position == selectedPosition){
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
			TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
			TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
			tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.white));	
			tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.white));
			ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
			imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
			ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			imViewArrow.setVisibility(View.GONE);
			if(selectedPosition==0)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.manual_backup_white));
			}
			else if(selectedPosition==1)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.restore_f));
			}
			//			else if(selectedPosition==2)
			//			{
			//				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.new_phone_f));
			//			}	
		}
		else{
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
			TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
			TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
			tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));	
			tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));
			ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
			imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));
			ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			imViewArrow.setVisibility(View.GONE);
			if(position==0)
			{
				if(saveState.isChanged(mContext) && (DBQuery.getAllTiles(mContext).size()>0))
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.manual_backup));//mannul_back_up
				else 
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.manual_backup_grey));
			}
			else if(position==1)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.restore));
			}
			//			else if(position==2)
			//			{
			//				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.new_phone));
			//			}
		}
		holder.tvMenuTitle.setText(listCloudBackupMenu.get(position));
		//		holder.tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));
		//		if(position==1)
		//		{
		//			Spannable wordtoSpan = new SpannableString(listCloudBackupMenu.get(position));
		//			wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.darkGreyColor)), 0, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//			wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.textBlueColor)), 25, listCloudBackupMenu.get(position).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//			holder.tvMenuTitle.setText(wordtoSpan);
		//		}
		//		else if(position==2)
		//		{
		//			Spannable wordtoSpan = new SpannableString(listCloudBackupMenu.get(position));
		//			wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.darkGreyColor)), 0, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//			wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.textBlueColor)), 25, listCloudBackupMenu.get(position).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//			holder.tvMenuTitle.setText(wordtoSpan);
		//			holder.tvMenuTitle.setText(wordtoSpan);
		//		}
		if(!listCloudBackupMenuDetails.isEmpty())
			holder.tvMenuDetail.setText(listCloudBackupMenuDetails.get(position));
		return convertView;
	}
}
