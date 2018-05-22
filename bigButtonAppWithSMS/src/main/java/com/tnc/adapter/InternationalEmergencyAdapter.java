package com.tnc.adapter;

import java.util.ArrayList;
import java.util.Locale;

import com.tnc.R;
import com.tnc.activities.InternationalEmergencyNumbersActivity;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.InternationalEmerDialConfirmationDialog;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class InternationalEmergencyAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<CountryDetailsBean> listCountries;
	public ArrayList<CountryDetailsBean> listCountriesFiltered;
	private String emergencyNumber="";

	public InternationalEmergencyAdapter(Context mContext,ArrayList<CountryDetailsBean> listCountries)
	{
		this.mContext=mContext;
		this.listCountries=new ArrayList<CountryDetailsBean>();
		this.listCountries=listCountries;
		this.listCountriesFiltered = new ArrayList<CountryDetailsBean>();
		this.listCountriesFiltered.addAll(listCountries);
	}

	@Override
	public int getCount() {
		return listCountries.size();
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
		TextView tvCountryName,tvEmergencyNumber;	
		Button btnDialNow;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		//reuse views
		if (convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.international_number_dialing_adapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.tvCountryName=(TextView) convertView.findViewById(R.id.tvCountryName); 
			holder.tvEmergencyNumber=(TextView) convertView.findViewById(R.id.tvEmergencyNumber); 
			holder.btnDialNow=(Button) convertView.findViewById(R.id.btnDialNow);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvCountryName,"fonts/Roboto-Bold_1.ttf");

		if(!listCountries.get(position).getEmergency().equals("?"))
		{
			holder.tvCountryName.setVisibility(View.VISIBLE);
			holder.tvEmergencyNumber.setVisibility(View.VISIBLE);
			holder.btnDialNow.setVisibility(View.VISIBLE);
			holder.btnDialNow.setTag(position);
			holder.tvCountryName.setText(listCountries.get(position).getCountryName());
			holder.tvEmergencyNumber.setText(listCountries.get(position).getEmergency());
		}
		else if(listCountries.get(position).getEmergency().equals("?"))
		{
			holder.tvCountryName.setVisibility(View.GONE);
			holder.tvEmergencyNumber.setVisibility(View.GONE);
			holder.btnDialNow.setVisibility(View.GONE);
		}
		holder.btnDialNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btnDialoNow = (Button)v;	
				emergencyNumber=listCountries.get(Integer.parseInt(String.valueOf(btnDialoNow.getTag()))).getEmergency();
				if(emergencyNumber.trim().equals("?"))
				{
					ImageRequestDialog dialog =new ImageRequestDialog();
					dialog.setCancelable(false);
					dialog.newInstance("", mContext,mContext.getResources().getString(R.string.txtNoEmergencyFound),"",null);
					dialog.show(((InternationalEmergencyNumbersActivity)mContext).getSupportFragmentManager(), "test");
				}
				else{
					InternationalEmerDialConfirmationDialog dialog=new InternationalEmerDialConfirmationDialog();
					dialog.newInstance("", mContext,mContext.getResources().getString(R.string.txtEmergencyNumberWarning)  
							,"", iNotifyDialNow);
					dialog.show(((InternationalEmergencyNumbersActivity)mContext).getSupportFragmentManager(), "test");
				}
			}
		});
		return convertView;
	}

	INotifyGalleryDialog iNotifyDialNow=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			//In case of making an emergency call
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+emergencyNumber.trim()));
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
			((InternationalEmergencyNumbersActivity)mContext).startActivity(callIntent);
		}

		@Override
		public void no() {

		}
	};

	/**
	 * @param instance of text to be filtered in list
	 *filters the contents of the list as per input given in the searchview
	 */
	//	boolean noMatchFound=true;
	public void filterData(String charText) 
	{
		charText = charText.toLowerCase(Locale.getDefault());
		if(listCountries!=null && !listCountries.isEmpty())
		{
			listCountries.clear();
			if (charText.trim().length() == 0) 
			{
				listCountries.addAll(listCountriesFiltered);
			} 
			else{
				boolean isRecordFound = false;
				for (CountryDetailsBean countryDetailsBean : listCountriesFiltered) {
					if(countryDetailsBean.getCountryName().toLowerCase().startsWith(charText.toLowerCase()))
					{
						isRecordFound = true;
						listCountries.add(countryDetailsBean);
					}
				}
				if(!isRecordFound){
//					listCountries.addAll(listCountriesFiltered);
				}
				this.notifyDataSetInvalidated();				
			}
		}else{
			if (charText.trim().length() == 0) 
			{
				listCountries.addAll(listCountriesFiltered);
			} else{
				boolean isRecordFound = false;
				for (CountryDetailsBean countryDetailsBean : listCountriesFiltered) {
					if(countryDetailsBean.getCountryName().toLowerCase().startsWith(charText.toLowerCase()))
					{
						isRecordFound = true;
						listCountries.add(countryDetailsBean);
					}
				}
				if(!isRecordFound){
//					listCountries.addAll(listCountriesFiltered);
				}
				this.notifyDataSetInvalidated();	
			}
//			if(listCountriesFiltered!=null && !listCountriesFiltered.isEmpty())
//				listCountries.addAll(listCountriesFiltered);
		}
	}
}
