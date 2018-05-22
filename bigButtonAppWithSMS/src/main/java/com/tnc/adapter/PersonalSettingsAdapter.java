package com.tnc.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tnc.R;
import com.tnc.common.GlobalCommonValues;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;

/**
 * Created by a3logics on 21/3/17.
 */

public class PersonalSettingsAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mAct;
    public ArrayList<String> listPersonalSettingsMenu;
    private int selectedPosition;
    private boolean isColorSet=false;
    private SharedPreference saveState;

    public PersonalSettingsAdapter(Activity mContext, ArrayList<String> listPersonalSettingsMenu){
        this.mContext = mContext;
        this.mAct = mContext;
        this.listPersonalSettingsMenu = new ArrayList<String>();
        this.listPersonalSettingsMenu = listPersonalSettingsMenu;
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
    public int getCount() {
        return listPersonalSettingsMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView tvMenuTitle;
        ImageView imViewMenuIcon,imViewArrow;
        CheckBox chkBoxDialerInterface;
        RadioGroup rbGroup;
        RadioButton radioBtnSquare, radioBtnCircle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //reuse views
        if (convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.personal_setting_menu_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tvMenuTitle=(TextView) convertView.findViewById(R.id.tvMenuTitle);

            holder.imViewMenuIcon=(ImageView) convertView.findViewById(R.id.imViewMenuIcon);
            holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
            holder.chkBoxDialerInterface = (CheckBox) convertView.findViewById(R.id.chkBoxDialerInterface);
            holder.rbGroup = (RadioGroup) convertView.findViewById(R.id.rbGroup);
            holder.radioBtnSquare = (RadioButton) convertView.findViewById(R.id.radioBtnSquare);
            holder.radioBtnCircle = (RadioButton) convertView.findViewById(R.id. radioBtnCircle);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the visibility of checkbox / arrow as per item position
        if(position == 0){ // Dialer interface
            holder.chkBoxDialerInterface.setVisibility(View.VISIBLE);
            holder.imViewArrow.setVisibility(View.GONE);
        }else if(position == 1 || position == 2){  // Voice mail settings
            holder.chkBoxDialerInterface.setVisibility(View.GONE);
            holder.imViewArrow.setVisibility(View.VISIBLE);
        }

        String mOptionSelected = listPersonalSettingsMenu.get(position);

        // Make RadioButton Group Visible only in case of Chat Button Shape Option
        if(position == 1){
            holder.rbGroup.setVisibility(View.VISIBLE);

            if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_SQUARE)){
                holder.radioBtnSquare.setChecked(true);
            }else if(saveState.getBUTTON_SHAPE(mContext).equals(GlobalCommonValues.BUTTON_SHAPE_CIRCLE)){
                holder.radioBtnCircle.setChecked(true);
            }
        }else{
            holder.rbGroup.setVisibility(View.GONE);
        }

        if(isColorSet && position == selectedPosition && selectedPosition == 2) {
            ((RelativeLayout) convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
            TextView tvMenuTitle = (TextView) convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);

            tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.white));

            ImageView imViewArrow = (ImageView) convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
            imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));

            ImageView imViewMenuIcon = (ImageView) convertView.findViewById(R.id.imViewMenuIcon);

//            if (mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserInfoCamelCase))) {
            imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_voice_mail_white));
//            }
        }else{

            if(position == 0){
                ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_custom_dialer));
            }else if(position == 1){
                ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_chat_button_shape));
            }else if(position == 2){
                ((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);

                tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.darkGreyColor));

                ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
                imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_blue));

                ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);

//                if(mOptionSelected.trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserInfoCamelCase))){
                imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_voice_mail));
//                }
            }
        }

        // set checkbox state as per the value in preference
        holder.chkBoxDialerInterface.setChecked(saveState.getIS_DIALER_INTERFACE_ENABLED(mContext));


        // handle checkbox state change
        holder.chkBoxDialerInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChecked = false;

                isChecked = ((CheckBox)v).isChecked();
                saveState.setDIALER_INTERFACE_ENABLED(mContext,isChecked);

//                Toast.makeText(mContext, isChecked + "", Toast.LENGTH_LONG).show();
            }
        });

        holder.tvMenuTitle.setText(listPersonalSettingsMenu.get(position));

//      holder.rbGroup.getCheckedRadioButtonId()



        holder.rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.radioBtnSquare){
                    saveState.setBUTTON_SHAPE(mContext, GlobalCommonValues.BUTTON_SHAPE_SQUARE);
                }else if(checkedId == R.id.radioBtnCircle){
                    saveState.setBUTTON_SHAPE(mContext, GlobalCommonValues.BUTTON_SHAPE_CIRCLE);
                }
            }
        });

        return convertView;
    }
}
