package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.common.CustomFonts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialPadAdapter extends BaseAdapter{

	public ArrayList<String> listNumbers=new ArrayList<String>();
	private Context mContext;

	public DialPadAdapter(Context mContext,ArrayList<String> listNumbers)
	{
		this.mContext=mContext;
		this.listNumbers=listNumbers;
	}

	@Override
	public int getCount() {
		return listNumbers.size();
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
		TextView tvNumber;	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder=null;
		//reuse views
		if (convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.dialpadadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvNumber=(TextView) convertView.findViewById(R.id.tvNumber); 
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvNumber,"fonts/Roboto-Regular_1.ttf");
		holder.tvNumber.setText(listNumbers.get(position));
		return convertView;
	}
}
