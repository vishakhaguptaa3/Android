package com.tnc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tnc.R;
import com.tnc.bean.CallDetailsBean;
import com.tnc.utility.RoundedImageViewCircular;

import java.util.ArrayList;

/**
 * Created by a3logics on 25/4/17.
 */

public class VoiceMailListAdapter extends BaseAdapter {

    private Context mContext;
    private int selectedPosition;
    private boolean isColorSet=false;
    private ArrayList<CallDetailsBean> mListVoiceMessages;

    public VoiceMailListAdapter(Activity mContext, ArrayList<CallDetailsBean> mListVoiceMessages){
        this.mContext           = mContext;
        this.mListVoiceMessages = new ArrayList<CallDetailsBean>();
        this.mListVoiceMessages = mListVoiceMessages;
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


    public int getCount(){
        return mListVoiceMessages.size();
    }

    @Override
    public Object getItem(int position){
        return mListVoiceMessages.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    static class ViewHolder
    {
        LinearLayout llContactNameHolder, llArrowHolder;
        TextView tvUserName, tvCallingDateTime, tvUnreadMessageCount, tvEmptyList;
        ImageView imViewArrow;
        RoundedImageViewCircular imViewContactImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;
        View    rowView;

        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.voice_message_list_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.llContactNameHolder     = (LinearLayout) convertView.findViewById(R.id.llContactNameHolder);
            holder.llArrowHolder           = (LinearLayout) convertView.findViewById(R.id.llArrowHolder);
            holder.tvUserName              = (TextView) convertView.findViewById(R.id.tvCallName);
            holder.tvUnreadMessageCount    = (TextView) convertView.findViewById(R.id.tvCallCount);
            holder.tvCallingDateTime       = (TextView) convertView.findViewById(R.id.tvCallingNumber);
            holder.tvEmptyList             = (TextView) convertView.findViewById(R.id.tvEmptyList);
            holder.imViewArrow             = (ImageView) convertView.findViewById(R.id.imViewArrow);
            holder.imViewContactImage      = (RoundedImageViewCircular) convertView.findViewById(R.id.imViewContactImage);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        rowView = convertView;

        holder.tvUnreadMessageCount.setVisibility(View.GONE);

        holder.tvEmptyList.setVisibility(View.GONE);

        if(isColorSet && position == selectedPosition){
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
            TextView tvUserName=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallName);
            TextView tvCallCount=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallCount);
            TextView tvCallingDateTime = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingDateTime);
            tvUserName.setTextColor(mContext.getResources().getColor(R.color.white));
            tvCallCount.setTextColor(mContext.getResources().getColor(R.color.white));
            tvCallingDateTime.setTextColor(mContext.getResources().getColor(R.color.white));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
//            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
        }
        else{
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
            TextView tvUserName=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallName);
            TextView tvCallCount=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.llCallNameHolder).findViewById(R.id.tvCallCount);
            TextView tvCallingDateTime = (TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvCallingDateTime);
            tvUserName.setTextColor(mContext.getResources().getColor(R.color.textBlueColor));
            tvCallCount.setTextColor(mContext.getResources().getColor(android.R.color.black));
            tvCallingDateTime.setTextColor(mContext.getResources().getColor(android.R.color.black));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));
//            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
        }

        // in case of empty list
        if(mListVoiceMessages.get(0).getCallName().equalsIgnoreCase(
                mContext.getResources().getString(R.string.txtEmptyVoiceLogMessage))){

            holder.tvEmptyList.setVisibility(View.VISIBLE);
            holder.imViewArrow.setVisibility(View.GONE);
            holder.llContactNameHolder.setVisibility(View.GONE);
            holder.imViewContactImage.setVisibility(View.GONE);

        }else{

            holder.llContactNameHolder.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notificationimageborder));

        }
        return rowView;
    }
}
