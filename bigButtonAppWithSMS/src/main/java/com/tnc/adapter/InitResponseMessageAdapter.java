package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.InitResponseMessageBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class InitResponseMessageAdapter extends BaseAdapter
{
	private Context mContext;
	public ArrayList<InitResponseMessageBean> listMessages=null;

	public InitResponseMessageAdapter(Context mContext,ArrayList<InitResponseMessageBean> listMessages)
	{
		this.listMessages=new ArrayList<InitResponseMessageBean>();
		this.listMessages=listMessages;
		this.mContext=mContext;
	}

	@Override
	public int getCount() {
		return listMessages.size();
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
		TextView tvMessage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=null;
		//reuse views
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.init_response_message_adapter, null);
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
		holder.tvMessage.setText(listMessages.get(position).getMessage());
		return convertView;
	}
}
