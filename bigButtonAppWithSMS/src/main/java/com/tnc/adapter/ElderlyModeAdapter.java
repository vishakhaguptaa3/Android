package com.tnc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.tnc.R;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

/**
 * Created by a3logics on 30/11/16.
 */

public class ElderlyModeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mListElderlyModeMenu = new ArrayList<String>();
    private SharedPreference mSaveState;

    public ElderlyModeAdapter(Context mContext, ArrayList<String> mListElderlyModeMenu){
        this.mContext = mContext;
        this.mListElderlyModeMenu = mListElderlyModeMenu;
        mSaveState = new SharedPreference();
    }

    @Override
    public int getCount() {
        return mListElderlyModeMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return mListElderlyModeMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {
        TextView tvElderModeOption;
        CheckBox mChkBoxElderlyModeOption;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;
        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elderly_mode_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tvElderModeOption = (TextView) convertView.findViewById(R.id.tvElderModeOption);
            holder.mChkBoxElderlyModeOption = (CheckBox) convertView.findViewById(R.id.chkBoxElderlyModeOption);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mChkBoxElderlyModeOption.setTag(position);

        holder.tvElderModeOption.setText(mListElderlyModeMenu.get(position));


        if(mListElderlyModeMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtHomeAppMode))){
            holder.mChkBoxElderlyModeOption.setVisibility(View.GONE);
        }

        if(mListElderlyModeMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtDisableLongTop))){
            enableDisableOption(holder.tvElderModeOption,
                    holder.mChkBoxElderlyModeOption, mSaveState.getIsDisableLongTap(mContext));
        }else if(mListElderlyModeMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtAutoSpeakerMode))){
            enableDisableOption(holder.tvElderModeOption,
                    holder.mChkBoxElderlyModeOption, mSaveState.getIsAutoSpeakerMode(mContext));
        }else if(mListElderlyModeMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtEmergencyContactNotification))){
            enableDisableOption(holder.tvElderModeOption,
                    holder.mChkBoxElderlyModeOption, mSaveState.getIsEmergencyContactNotification(mContext));
        }

        holder.mChkBoxElderlyModeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isChecked = ((CompoundButton)view).isChecked();

                int mPositionSelected = -1;
                try{
                    mPositionSelected = Integer.parseInt(String.valueOf(view.getTag()));
                }catch (Exception e){
                    e.getMessage();
                }
                if(mPositionSelected > -1){
                    if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtDisableLongTop))){
                        mSaveState.setIsDisableLongTap(mContext, isChecked);
                    }else if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtAutoSpeakerMode))){
                        mSaveState.setIsAutoSpeakerMode(mContext, isChecked);
                    }else if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtEmergencyContactNotification))){
                        mSaveState.setIsEmergencyContactNotification(mContext, isChecked);
                    }
                    notifyDataSetChanged();
                }
            }
        });

       /* holder.mToggleOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int mPositionSelected = -1;
                try{
                    mPositionSelected = Integer.parseInt(String.valueOf(compoundButton.getTag()));
                }catch (Exception e){
                    e.getMessage();
                }
                if(mPositionSelected > -1){
                    if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtDisableLongTop))){
                        mSaveState.setIsDisableLongTap(mContext, isChecked);
                    }else if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtAutoSpeakerMode))){
                        mSaveState.setIsAutoSpeakerMode(mContext, isChecked);
                    }else if(mListElderlyModeMenu.get(mPositionSelected).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtEmergencyContactNotification))){
                        mSaveState.setIsEmergencyContactNotification(mContext, isChecked);
                    }
                    notifyDataSetChanged();
                }
            }
        });*/

        return convertView;
    }

    private void enableDisableOption(TextView mTextViewForOption,
                                     CheckBox mToggleButton, boolean isEnabled){
        /*if(isEnabled){
            mTextViewForOption.setTextColor(Color.parseColor("#000000"));
        }else{
            mTextViewForOption.setTextColor(Color.parseColor("#C0C0C0"));
        }*/
        mToggleButton.setChecked(isEnabled);
    }
}
