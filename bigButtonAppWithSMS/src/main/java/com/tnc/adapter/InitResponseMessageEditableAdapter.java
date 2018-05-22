package com.tnc.adapter;

import java.util.ArrayList;
import com.tnc.R;
import com.tnc.bean.InitResponseMessageBean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class InitResponseMessageEditableAdapter extends BaseAdapter
{
	private Context mContext;
	public ArrayList<InitResponseMessageBean> listMessages=new ArrayList<InitResponseMessageBean>();
	private ViewHolder holder=null;
	private int positionListItem;
	private int selectedPosition;
	private boolean isColorSet;
	//	public SwipeMenuListView lvEditableMessagesFromDB;

	public InitResponseMessageEditableAdapter(Context mContext,ArrayList<InitResponseMessageBean> listMessages)
	{
		this.listMessages=new ArrayList<InitResponseMessageBean>();
		//		this.lvEditableMessagesFromDB=lvEditableMessagesFromDB;
		this.listMessages=listMessages;
		this.mContext=mContext;
	}

	public void setRowColor(int position, boolean isColorSet) {
		selectedPosition = position;
		this.isColorSet = isColorSet;
		this.notifyDataSetInvalidated();
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
		LinearLayout llInitResponseMessageHolder;
		ImageView imViewArrow;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//reuse views
		if ( convertView== null)
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.init_response_message_adapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.llInitResponseMessageHolder=(LinearLayout) convertView.findViewById(R.id.llInitResponseMessageHolder);
			holder.tvMessage=(TextView) convertView.findViewById(R.id.tvMessage);
			holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvMessage.setTag(R.id.tvMessage,position);
		if(listMessages.get(position).getMessage().equalsIgnoreCase("System Default Messages") ||
				listMessages.get(position).getMessage().equalsIgnoreCase("Custom Messages"))
		{
			holder.tvMessage.setTextColor(Color.parseColor("#000000"));
			convertView.setBackgroundColor(Color.parseColor("#EFEDED"));
			holder.imViewArrow.setVisibility(View.GONE);
		}
		else{

			holder.imViewArrow.setVisibility(View.VISIBLE);
			if (isColorSet && position == selectedPosition){
				((LinearLayout) convertView).setBackgroundColor(mContext
						.getResources().getColor(R.color.stripDarkBlueColor));
				holder.tvMessage.setTextColor(mContext.getResources().getColor(
						R.color.white));
				holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.arrow_white));
			}
			else{
				((LinearLayout) convertView).setBackgroundColor(mContext
						.getResources().getColor(R.color.white));
				holder.tvMessage.setTextColor(Color.parseColor("#000000"));
				holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.arrow_blue));
			}
			//			holder.tvMessage.setTextColor(Color.parseColor("#000000"));
			//			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		holder.tvMessage.setTypeface(null,Typeface.BOLD);

		/*convertView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean boolValue=false;
				Object pos = ((LinearLayout)v).findViewById(R.id.tvMessage).getTag(R.id.tvMessage);
				positionListItem=Integer.parseInt(String.valueOf(pos));
				if(listMessages.get(positionListItem).getLocked()==1){
					boolValue=true;
					if(lvEditableMessagesFromDB.mTouchView!=null && lvEditableMessagesFromDB.mTouchView.isOpen())
						lvEditableMessagesFromDB.mTouchView.smoothCloseMenu();
				}
				else if(listMessages.get(positionListItem).getMessage().equalsIgnoreCase("System Default Messages") ||
						listMessages.get(positionListItem).getMessage().equalsIgnoreCase("Custom Messages"))
				{
					boolValue=true;
					if(lvEditableMessagesFromDB.mTouchView!=null && lvEditableMessagesFromDB.mTouchView.isOpen())
						lvEditableMessagesFromDB.mTouchView.smoothCloseMenu();
				}
				else {
					boolValue=false;
				}
				return boolValue;
			}
		});*/
		holder.tvMessage.setText(listMessages.get(position).getMessage());
		return convertView;
	}
}
