package com.tnc.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ParseException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class BackupListAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<CloudRecoverBackupListingBean> listBackups;
	private int selectedPosition;
	private boolean isColorSet;

	public BackupListAdapter(Context mContext,ArrayList<CloudRecoverBackupListingBean> listBackups)	
	{
		this.mContext=mContext;	
		this.listBackups=new ArrayList<CloudRecoverBackupListingBean>();
		this.listBackups=listBackups;
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
	public int getCount() {
		// TODO Auto-generated method stub
		return listBackups.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder
	{
		public TextView tvBackupTitle,tvBackupDate;
		public ImageView imViewClockIcon;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "DefaultLocale" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		//reuse views
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.backuplistadapter, null);
			// configure view holder
			holder = new ViewHolder();
			//			holder.imViewGallery=(ImageView) convertView.findViewById(R.id.imViewGallery);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		if(isColorSet && position==selectedPosition)
		{
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
			holder.tvBackupTitle=(TextView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.tvBackupTitle);
			holder.tvBackupTitle.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.tvBackupDate=(TextView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.llImageIconHolder).findViewById(R.id.tvBackupDate);
			holder.tvBackupDate.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.imViewClockIcon=(ImageView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.llImageIconHolder).findViewById(R.id.imViewIcon);
			holder.imViewClockIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.clock_f));
		}
		else
		{
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
			TextView tvBackupTilte=(TextView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.tvBackupTitle);
			tvBackupTilte.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));
			TextView tvBackupDate=(TextView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.llImageIconHolder).findViewById(R.id.tvBackupDate);
			tvBackupDate.setTextColor(mContext.getResources().getColor(R.color.textBlueColor));
			ImageView imViewClockIcon=(ImageView)convertView.findViewById(R.id.llBackupNameHolderChild).findViewById(R.id.llImageIconHolder).findViewById(R.id.imViewIcon);
			imViewClockIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.clock));	
		}
		int pos=position;
		holder.tvBackupTitle=(TextView) convertView.findViewById(R.id.tvBackupTitle);
		holder.tvBackupDate=(TextView) convertView.findViewById(R.id.tvBackupDate);
		CustomFonts.setFontOfTextView(mContext,holder.tvBackupTitle,"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(mContext,holder.tvBackupDate,"fonts/Roboto-Light_1.ttf");
		
		if(MainBaseActivity.recoveryType.equals("current_backup"))
		{
			if(pos==0)
			{
				holder.tvBackupTitle.setText(mContext.getResources().getString(R.string.txtCurrentbackUp));
			}
			else
			{
				holder.tvBackupTitle.setText(mContext.getResources().getString(R.string.txtBackUp) + " "+pos);
			}	
			
		}
		else if(MainBaseActivity.recoveryType.equals("archival_backup")){
			holder.tvBackupTitle.setText(mContext.getResources().getString(R.string.txtBackUp) + " "+String.valueOf(pos+1));
		}
		
		String time=GlobalConfig_Methods.isUnlockTimeExpire(listBackups.get(position).getDatetime());
		String formattedDate=Convert24to12(time);
		holder.tvBackupDate.setText(formattedDate);
		return convertView;
	}

	/**
	 * 
	 * @param time as String
	 * @return converted time as String
	 * converts the date and time in 24 hour mode to 12 hour mode
	 */
	public static String Convert24to12(String time)
	{
		String convertedTime ="";
		String strTimeMode="";
		String finalTime="";
		Date date=new Date();
		Date displayDate=null;
		DateFormat writeFormat=null;
		String str[]=new String[35];
		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat readDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			writeFormat = new SimpleDateFormat("dd MMM yyyy");
			try 
			{
				str=time.split(" ");
				date = parseFormat.parse(str[1]);
				displayDate=readDateFormat.parse(str[0]);
			}
			catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
			convertedTime=displayFormat.format(date);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		
		if(convertedTime.contains("am") || convertedTime.contains("AM") || convertedTime.contains("Am"))
		{
			if(convertedTime.contains("am")) 
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("a"),convertedTime.indexOf("a")+2);
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("a"))+strTimeMode.toUpperCase();
			}
			else if(convertedTime.contains("AM"))
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("A"),convertedTime.indexOf("A")+2);			
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("A"))+strTimeMode.toUpperCase();
			}
			else if(convertedTime.contains("Am"))
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("A"),convertedTime.indexOf("A")+2);
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("A"))+strTimeMode.toUpperCase();
			}
		}
		else if(convertedTime.contains("pm") || convertedTime.contains("PM") || convertedTime.contains("Pm"))
		{
			if(convertedTime.contains("pm"))
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("p"),convertedTime.indexOf("p")+2);
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("p"))+strTimeMode.toUpperCase();
			}
			else if(convertedTime.contains("PM"))
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("P"),convertedTime.indexOf("P")+2);		
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("P"))+strTimeMode.toUpperCase();
			}
			else if(convertedTime.contains("Pm"))
			{
				strTimeMode=convertedTime.substring(convertedTime.indexOf("P"),convertedTime.indexOf("P")+2);
				finalTime=writeFormat.format(displayDate)+","+convertedTime.substring(0,convertedTime.indexOf("P"))+strTimeMode.toUpperCase();
			}
		}
		return  finalTime;
		//Output will be 10:23 PM
	}
}
