package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserPhoneNumbersAdapter extends BaseAdapter{

	private Context mContext;
	public ArrayList<String> listNumbers=new ArrayList<String>();
	private SharedPreference saveState;
	public ArrayList<BBContactsBean> listBBContacts = null;
	private int count=0,nonTileContactCount=0;
	private INotifyAction iAction;
	private INotifyGalleryDialog iNotifyInvite;
	private ContactDetailsBean selectedContactBean;

	public UserPhoneNumbersAdapter(Context mContext,ContactDetailsBean selectedContactBean,
			ArrayList<String> listNumbers,INotifyAction iAction,
			INotifyGalleryDialog iNotifyInvite)
	{
		this.mContext=mContext;	
		this.selectedContactBean=selectedContactBean;
		this.listNumbers=listNumbers;
		count=0;
		nonTileContactCount=0;
		this.iAction=iAction;
		this.iNotifyInvite=iNotifyInvite;
		this.listBBContacts=new ArrayList<BBContactsBean>();
		saveState=new SharedPreference();
		if(saveState.isRegistered(mContext))
		{
			if (GlobalCommonValues.listBBContacts != null
					&& GlobalCommonValues.listBBContacts.isEmpty()) {
				listBBContacts = DBQuery.getAllBBContacts(mContext);
				GlobalCommonValues.listBBContacts = listBBContacts;
			} else if (GlobalCommonValues.listBBContacts != null) {
				this.listBBContacts = GlobalCommonValues.listBBContacts;
			}
		}
		else if(!saveState.isRegistered(mContext))
		{
			GlobalCommonValues.listBBContacts= new ArrayList<BBContactsBean>();
			this.listBBContacts = new ArrayList<BBContactsBean>();	
		}
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

	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//		ViewHolder holder=null;
		String numberFilter="";
		String prefix="";
		String phoneNumber="";
		LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.users_phone_number_adapter, null);
		LinearLayout llViewBorder=(LinearLayout) convertView.findViewById(R.id.llViewBorder);
		TextView tvPhoneNumber=(TextView) convertView.findViewById(R.id.tvPhoneNumber);
		TextView tvNumberTypeTag=(TextView) convertView.findViewById(R.id.tvNumberTypeTag);
		Button btnDisableOverlay=(Button) convertView.findViewById(R.id.btnDisableOverlay);

		llViewBorder.setVisibility(View.GONE);
		btnDisableOverlay.setVisibility(View.GONE);
		phoneNumber=listNumbers.get(position);//GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(listNumbers.get(position));
		tvPhoneNumber.setText(phoneNumber);

		tvNumberTypeTag.setText(selectedContactBean.getsType().get(position));
		
		if(phoneNumber.length()>10)
		{
			numberFilter=phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length());
			prefix=phoneNumber.substring(0,phoneNumber.length()-10);
		}
		else{
			numberFilter=phoneNumber.substring(0, phoneNumber.length());;
			prefix="";
		}

		if(DBQuery.checkTileExistence(mContext,numberFilter,"")>0)  
		{
			btnDisableOverlay.setVisibility(View.VISIBLE);
			count++;
			if(count==listNumbers.size() && nonTileContactCount==0)
			{
				if(iAction!=null)
					iAction.setAction("disable");
			}
		}

		else if(DBQuery.checkTileExistence(mContext,phoneNumber,"")<=0){
			nonTileContactCount++;
			btnDisableOverlay.setVisibility(View.GONE);
		}
		// check if the user number is registered as TnC user,then display blue frame along it
		if(saveState.isRegistered(mContext))
		{
			if (!listBBContacts.isEmpty()) {
				if (listNumbers != null) {
					String contactNumber="";
					String strPhone = listNumbers.get(position);
					try {
						contactNumber=	strPhone;//GlobalConfig_Methods.trimSpecialCharactersFromString(strPhone);
					} catch (Exception e) {
						e.getMessage();
					}

					String strNumber=GlobalConfig_Methods.getBBNumberToCheck(mContext, contactNumber);
					String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
					boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
					String []arrayUserDetails=strNumber.split(",");

					countryCodeRegisteredUser=arrayUserDetails[0];
					numberRegisteredUser=arrayUserDetails[1];
					isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
					isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
					isdCodeRegisteredUser=arrayUserDetails[4];
					isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);

					if(isTncUserRegisteredUser)
					{
						llViewBorder.setVisibility(View.VISIBLE);
					}
					else{
						llViewBorder.setVisibility(View.GONE);
					}
				}
			}
		}

		btnDisableOverlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		return convertView;
	}
}
