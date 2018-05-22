package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.common.CustomFonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessagePredefinedAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<InitResponseMessageBean> listMessages;
	private int selectedPosition;
	private boolean isColorSet=false;
	public String listType="";

	public MessagePredefinedAdapter(Context mContext,
			ArrayList<InitResponseMessageBean> listMessages, String listType)
	{
		this.mContext=mContext;
		this.listMessages=new ArrayList<InitResponseMessageBean>();
		this.listMessages=listMessages;
		this.listType=listType;
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
		return listMessages.size();
	}
	@Override
	public Object getItem(int position)
	{
		return listMessages.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	static class ViewHolder
	{
		TextView tvMessage;
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=null;
		//reuse views
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.messagelistadapter_predefined, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvMessage=(TextView) convertView.findViewById(R.id.tvMessage);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvMessage,"fonts/Roboto-Regular_1.ttf");
		holder.tvMessage.setText(listMessages.get(position).getMessage());
		if(isColorSet && position==selectedPosition)
		{
			LinearLayout ll=(LinearLayout)convertView.findViewById(R.id.llMessageListParent);
			ll.setBackground(mContext.getResources().getDrawable(R.drawable.list_bg_blue));
			holder.tvMessage=(TextView)convertView.findViewById(R.id.llMessageListParent).findViewById(R.id.tvMessage);
			holder.tvMessage.setTextColor(Color.parseColor("#ffffff"));
		}
		else
		{
			LinearLayout ll=(LinearLayout)convertView.findViewById(R.id.llMessageListParent);
			ll.setBackground(mContext.getResources().getDrawable(R.drawable.list_bg_white));
			holder.tvMessage=(TextView)convertView.findViewById(R.id.llMessageListParent).findViewById(R.id.tvMessage);
			holder.tvMessage.setTextColor(Color.parseColor("#000000"));
		}
		return convertView;
	}
}
