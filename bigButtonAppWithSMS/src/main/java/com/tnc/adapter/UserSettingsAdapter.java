package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.fragments.ElderlyModeFragment;
import com.tnc.homescreen.HomeScreenActivity;
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

public class UserSettingsAdapter extends BaseAdapter
{
    private Context mContext;
    private Activity mAct;
    public ArrayList<String> listUserSettingsMenu;
    private int selectedPosition;
    private boolean isColorSet=false;
    private SharedPreference saveState;

    public UserSettingsAdapter(Activity mContext,ArrayList<String> listUserSettingsMenu){
        this.mContext = mContext;
        this.mAct = mContext;
        this.listUserSettingsMenu = new ArrayList<String>();
        this.listUserSettingsMenu = listUserSettingsMenu;
        saveState = new SharedPreference();
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
        return listUserSettingsMenu.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listUserSettingsMenu.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    static class ViewHolder
    {
        TextView tvMenuTitle,tvMenuDetail;
        ImageView imViewMenuIcon,imViewArrow;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({ "InflateParams", "DefaultLocale", "CutPasteId" })
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.setting_menu_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tvMenuTitle=(TextView) convertView.findViewById(R.id.tvMenuTitle);
            holder.tvMenuDetail=(TextView) convertView.findViewById(R.id.tvMenuDetail);
            holder.imViewMenuIcon=(ImageView) convertView.findViewById(R.id.imViewMenuIcon);
            holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
            convertView.setTag(holder);
        }
        else
        {
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        String mOptionSelected = listUserSettingsMenu.get(position);

        if(isColorSet && position == selectedPosition){
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
            TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
            TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
            tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.white));
            tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.white));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);

            if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserInfoCamelCase))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app_f));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserProfileSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_profile_settings_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserPrivacySettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_privacy_settings_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserPersonalSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_personal_settings_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtChatButtonCategory))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_categories_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserGroups))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_group_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtElderlyMode))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_elderly_mode_white));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtLocationSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_location_setting_white));
            }
        }
        else{
            ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
            TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
            TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
            tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));
            tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.textGreyColorChooseConatct));
            ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));
            ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
            if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserInfoCamelCase))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserProfileSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_profile_settings));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserPrivacySettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_privacy_settings));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserPersonalSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_personal_settings));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtChatButtonCategory))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_categories));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserGroups))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_group));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtElderlyMode))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_elderly_mode));
            }else if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtLocationSettings))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_location_setting));
            }
        }

        holder.tvMenuTitle.setText(listUserSettingsMenu.get(position));

        return convertView;
    }
}
