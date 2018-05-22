package com.tnc.adapter;

import java.util.ArrayList;
import com.tnc.R;
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

public class SettingMenuAdapter extends BaseAdapter
{
	private Context mContext;
	private Activity mAct;
	public ArrayList<String> listSettingMenu;
	public ArrayList<String> listSettingMenuDetail;
	private int selectedPosition;
	private boolean isColorSet=false;
	private SharedPreference saveState;

	public SettingMenuAdapter(Activity mContext,ArrayList<String> listSettingMenu,
							  ArrayList<String> listSettingMenuDetail,SharedPreference saveState)
	{
		this.mContext = mContext;
		this.mAct = mContext;
		this.listSettingMenu=new ArrayList<String>();
		this.listSettingMenuDetail=new ArrayList<String>();
		this.listSettingMenu=listSettingMenu;
		this.listSettingMenuDetail=listSettingMenuDetail;
		saveState=new SharedPreference();
		this.saveState=saveState;
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
		return listSettingMenu.size();
	}

	@Override
	public Object getItem(int position)
	{
		return listSettingMenu.get(position);
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
		//		holder.imViewArrow.setVisibility(View.GONE);
		if(isColorSet && position == selectedPosition){
			((RelativeLayout)convertView).setBackgroundColor(mContext.getResources().getColor(R.color.stripDarkBlueColor));
			TextView tvMenuTitle=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuTitle);
			TextView tvMenuDetail=(TextView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llContactNameHolder).findViewById(R.id.llContactNameHolderChild).findViewById(R.id.tvMenuDetail);
			tvMenuTitle.setTextColor(mContext.getResources().getColor(R.color.white));
			tvMenuDetail.setTextColor(mContext.getResources().getColor(R.color.white));
			ImageView imViewArrow=(ImageView)convertView.findViewById(R.id.llContactNameParent).findViewById(R.id.llArrowHolder).findViewById(R.id.imViewArrow);
			imViewArrow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white));
			ImageView imViewMenuIcon=(ImageView)convertView.findViewById(R.id.imViewMenuIcon);
			//			holder.imViewArrow.setVisibility(View.GONE);
			/*if(selectedPosition==0)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notification_f));
			}
			else*/ if(selectedPosition==0)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.edit_tiltle_f));
			}
			else if(selectedPosition==1)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_f));
			}
			else if(selectedPosition==2)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.msg_toggle_f));
			}
			else if(selectedPosition==3)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.cloud_f));
			}
			/*else if(selectedPosition==4)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app_f));
			}*/
			else if(position==4)
			{
				if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtPremiumUser))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_premium_user_white));
				}else if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserSettings))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_settings_white));
				}else if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtAppRegistration))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app_f));
				}
			}else if(position==5){
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_settings_white));
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
			//			holder.imViewArrow.setVisibility(View.GONE);
			/*if(position==0)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notification));
			}
			else*/ if(position==0)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.edit_tiltle));
			}
			else if(position==1)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user));
			}
			else if(position==2)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.msg_toggle));
			}
			else if(position==3)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.cloud));
			}
			/*else if(position==4)
			{
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app));
			}*/
			else if(position==4)
			{
				if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtPremiumUser))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_premium_user));
				}else if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUserSettings))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_settings));
				}else if(listSettingMenu.get(position).trim().equalsIgnoreCase(mContext.getResources().getString(R.string.txtAppRegistration))){
					imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app));
				}
			}else if(position == 5){
				imViewMenuIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_settings));
			}
		}

		/*if(position==4)
		{
			if(saveState.isRegistered(mContext))
				holder.tvMenuTitle.setText(mContext.getResources().getString(R.string.txtUserInfoCamelCase));
			else{
				holder.tvMenuTitle.setText(mContext.getResources().getString(R.string.txtAppRegistrationSettingsMenu));
			}
		}
		else
		{
			holder.tvMenuTitle.setText(listSettingMenu.get(position));
		}*/

		if(position==4){
			if(!saveState.isRegistered(mContext)){
				holder.tvMenuTitle.setText(mContext.getResources().getString(R.string.txtAppRegistration));
			}else{
				holder.tvMenuTitle.setText(listSettingMenu.get(position));
			}
		}else{
			holder.tvMenuTitle.setText(listSettingMenu.get(position));
		}


		if(!listSettingMenuDetail.isEmpty())
			holder.tvMenuDetail.setText(listSettingMenuDetail.get(position));
		return convertView;
	}
}
