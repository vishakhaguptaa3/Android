package com.tnc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tnc.R;
import com.tnc.bean.CallDetailsBean;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a3logics on 16/12/16.
 */

public class CallLogDetailsAdapter extends BaseAdapter {

    private Context mContext;
    public List<CallDetailsBean> mListUserCallLogDataBean;

    public CallLogDetailsAdapter(Activity mContext, List<CallDetailsBean> mListUserCallLogDataBean){
        this.mContext = mContext;
        this.mListUserCallLogDataBean = new ArrayList<CallDetailsBean>();
        this.mListUserCallLogDataBean = mListUserCallLogDataBean;
    }

    @Override
    public int getCount() {
        return mListUserCallLogDataBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mListUserCallLogDataBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {

        TextView tvCallingDateTime;
        ImageView imViewCallIcon, imViewEmergencyCallIcon;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder            = null;

        long mDateTimeInMilliSeconds = 0l;

        String displayDate = "";

        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.call_log_detail_adapter, null);

            // configure view holder
            holder = new ViewHolder();

            holder.tvCallingDateTime           = (TextView) convertView.findViewById(R.id.tvCallingDateTime);

            holder.imViewCallIcon              = (ImageView) convertView.findViewById(R.id.imViewCallIcon);
            holder.imViewEmergencyCallIcon     = (ImageView) convertView.findViewById(R.id.imViewEmergencyCallIcon);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imViewEmergencyCallIcon.setVisibility(View.GONE);

        //Display call icon on the basis of type
        //1 - incoming, 2 - outgoing, 3 - missed

        if(mListUserCallLogDataBean.get(position).getCallType()  == 1){
            holder.imViewCallIcon.setBackgroundResource(R.drawable.ic_incoming_call);
        }else if(mListUserCallLogDataBean.get(position).getCallType()  == 2){
            holder.imViewCallIcon.setBackgroundResource(R.drawable.ic_outgoing_call);
        }else if(mListUserCallLogDataBean.get(position).getCallType()  == 3){
            holder.imViewCallIcon.setBackgroundResource(R.drawable.ic_missed_call);
        }

        try{
            // Display date/Time of Call
            mDateTimeInMilliSeconds = GlobalConfig_Methods.getFormattedDateTimeInMilliSeconds(mListUserCallLogDataBean.get(position).getCallTime());

            if(mDateTimeInMilliSeconds > 0){
                displayDate = GlobalConfig_Methods.getFormattedDateFromMilliSeconds(mContext, mDateTimeInMilliSeconds);
            }

            // Display Call Log Datet - Time
            if(displayDate!=null)
                holder.tvCallingDateTime.setText(displayDate);

        }catch(Exception e){
            e.getMessage();
        }

        // Display icon if isEmergency Call Is exists in the database
        if(mListUserCallLogDataBean.get(position).isEmergencyCall()){
            holder.imViewEmergencyCallIcon.setVisibility(View.VISIBLE);
        }else{
            holder.imViewEmergencyCallIcon.setVisibility(View.GONE);
        }

        return convertView;
    }
}
