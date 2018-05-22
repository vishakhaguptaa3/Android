package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.common.CustomFonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlphabetAdapter extends BaseAdapter 
{
	private ArrayList<String> listAlphabets=null;
	private Context mContext;

	public AlphabetAdapter(Context mContext,ArrayList<String> listAlphabets)
	{
		this.mContext=mContext;
		this.listAlphabets=new ArrayList<String>();
		this.listAlphabets=listAlphabets;
	}

	@Override
	public int getCount() {
		return listAlphabets.size();
	}

	@Override
	public Object getItem(int position){
		return listAlphabets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder
	{
		TextView tvAlphabet;	
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder=null;
		//reuse views
		if (convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.alphabetadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvAlphabet=(TextView) convertView.findViewById(R.id.tvAlphabet); 
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvAlphabet,"fonts/Roboto-Regular_1.ttf");
		holder.tvAlphabet.setText(listAlphabets.get(position));
		return convertView;
	}
}
