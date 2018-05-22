package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.webresponse.SendMessageReponseDataBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class MessageListAdapter extends BaseAdapter 
{
	private Context mContext;
	public ArrayList<SendMessageReponseDataBean> listMessageContacts;
	private int matching_user_id;
	private int BBID=0;
	private ArrayList<BBContactsBean> listBBContacts=null;
	private ImageLoadTask imageLoader=null;
	private SharedPreference saveState;
	private Bitmap _bmp=null;
	private SharedPreference pref=null;
	private ArrayList<ContactTilesBean> listContactTiles=null;
	private Bitmap displayImage=null;
	private String phoneNumber="";
	private String displayName="";

	public MessageListAdapter(Context mContext,ArrayList<SendMessageReponseDataBean> listMessageContacts)
	{
		this.mContext=mContext;
		this.listMessageContacts=listMessageContacts;
		saveState=new SharedPreference();
		_bmp=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
		pref=new SharedPreference();
	}

	@Override
	public int getCount() {
		return listMessageContacts.size();
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
		LinearLayout llImageBoundary;
		RoundedImageViewCircular imViewContact;
		//		ImageView imViewStatus;
		TextView tvContactName,tvMessageDetail,tvTime;
	}

	ViewHolder holder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		boolean isExist=false;
		holder=null;
		String dateArray[];
		BBID=0;
		displayImage=null;

		//reuse views
		if (convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.messagelistadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.llImageBoundary=(LinearLayout) convertView.findViewById(R.id.llImageBoundary);
			holder.imViewContact=(RoundedImageViewCircular) convertView.findViewById(R.id.imViewContact);
			//			holder.imViewStatus=(ImageView) convertView.findViewById(R.id.imViewStatus);
			holder.llImageBoundary=(LinearLayout) convertView.findViewById(R.id.llImageBoundary);
			holder.tvContactName=(TextView) convertView.findViewById(R.id.tvContactName); 
			holder.tvMessageDetail=(TextView) convertView.findViewById(R.id.tvMessageDetail);
			holder.tvTime=(TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.tvContactName.setTypeface(null,Typeface.NORMAL);
		//		CustomFonts.setFontOfTextView(mContext, holder.tvContactName,"fonts/Roboto-Regular_1.ttf");
		//CustomFonts.setFontOfTextView(mContext, holder.tvMessageDetail,"fonts/Roboto-Light_1.ttf");
		listBBContacts=new ArrayList<BBContactsBean>();
		listContactTiles=new ArrayList<ContactTilesBean>();
		holder.imViewContact.setVisibility(View.GONE);
		holder.llImageBoundary.setVisibility(View.GONE);
		//		holder.imViewStatus.setVisibility(View.GONE);
		holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notification_image_border_grey));
		holder.tvMessageDetail.setVisibility(View.VISIBLE);
		holder.tvTime.setVisibility(View.VISIBLE);
		displayName="";
		if(listMessageContacts.get(position).getTo_user_id()==null && listMessageContacts.get(position).getFrom_user_id()==null)
		{
			//			holder.imViewContact.setImageBitmap(_bmp);
			holder.imViewContact.setVisibility(View.GONE);
			holder.llImageBoundary.setVisibility(View.GONE);
			holder.tvMessageDetail.setVisibility(View.GONE);
			holder.tvTime.setVisibility(View.GONE);
			holder.tvContactName.setText(Uri.decode(listMessageContacts.get(position).getMessage()));
		}
		else if(listMessageContacts.get(position).getTo_user_id()!=null && listMessageContacts.get(position).getFrom_user_id()!=null)
		{
			holder.llImageBoundary.setVisibility(View.VISIBLE);
			holder.imViewContact.setVisibility(View.VISIBLE);
			holder.tvMessageDetail.setVisibility(View.VISIBLE);
			holder.tvTime.setVisibility(View.VISIBLE);
			holder.tvMessageDetail.setText(Uri.decode(listMessageContacts.get(position).getMessage()));
			int time_format = 0;
			try 
			{
				time_format = Settings.System.getInt(mContext.getContentResolver(), Settings.System.TIME_12_24);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			if(pref.getBBID(mContext).equals(listMessageContacts.get(position).getFrom_user_id()))
			{
				matching_user_id=Integer.parseInt(listMessageContacts.get(position).getTo_user_id());
				phoneNumber=DBQuery.getPhoneNumberFromBBID(mContext,Integer.parseInt(listMessageContacts.get(position).getTo_user_id()));
			}
			else if(pref.getBBID(mContext).equals(listMessageContacts.get(position).getTo_user_id())) {
				matching_user_id=Integer.parseInt(listMessageContacts.get(position).getFrom_user_id());	
				phoneNumber=DBQuery.getPhoneNumberFromBBID(mContext,Integer.parseInt(listMessageContacts.get(position).getFrom_user_id()));
			}

			if(phoneNumber.trim().equals(""))
			{
				holder.tvContactName.setText(mContext.getResources().getString(R.string.txtUnknown));
				holder.imViewContact.setImageBitmap(_bmp);
			}
			else
			{
				//				//system.out.println(phoneNumber);
				listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,GlobalConfig_Methods.trimSpecialCharactersFromString(phoneNumber));
				if(listContactTiles.size()>0)  // In case of Tile of the number exists
				{
					displayName=listContactTiles.get(0).getName();
					if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0)
					{
						byte arrayImage[]=listContactTiles.get(0).getImage();
						if(arrayImage!=null && arrayImage.length>0)
						{
							holder.imViewContact.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
						}
					}
					else {
						//					matching_user_id=Integer.parseInt(listMessageContacts.get(position).getTo_user_id());	
						listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
						if(listBBContacts.size()>0)
						{
							isExist=true;
							BBID=listBBContacts.get(0).getBBID();
						}
						if(BBID!=0)
						{
							if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
							{
								imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContact,320);
								imageLoader.execute();
							}
							else{
								displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
								if(displayImage!=null)
								{
									holder.imViewContact.setImageBitmap(displayImage);
								}
								else{
									holder.imViewContact.setImageBitmap(_bmp);
								}
							}
						}
						else{
							displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
							if(displayImage!=null)
							{
								holder.imViewContact.setImageBitmap(displayImage);
							}
							else{
								holder.imViewContact.setImageBitmap(_bmp);
							}
						}
					}
				}
				else {  // In case of tile of that number doesn't exists
					//				matching_user_id=Integer.parseInt(listMessageContacts.get(position).getTo_user_id());	
					listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
					if(listBBContacts.size()>0)
					{
						isExist=true;
						BBID=listBBContacts.get(0).getBBID();
					}
					if(BBID!=0)
					{     //Changed in Phase -4
						displayName=listBBContacts.get(0).getName();//GlobalConfig_Methods.getContactName(mContext, phoneNumber);
						if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
						{
							imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContact,320);
							imageLoader.execute();
						}
						else{
							//						displayName=GlobalConfig_Methods.getContactName(mContext, phoneNumber);
							displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
							if(displayImage!=null)
							{
								holder.imViewContact.setImageBitmap(displayImage);
							}
							else{
								holder.imViewContact.setImageBitmap(_bmp);
							}
						}
					}
					else{
						displayName=GlobalConfig_Methods.getContactName(mContext, phoneNumber);
						displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
						if(displayImage!=null)
						{
							holder.imViewContact.setImageBitmap(displayImage);
						}
						else{
							holder.imViewContact.setImageBitmap(_bmp);
						}
					}
				}

				listBBContacts=new ArrayList<BBContactsBean>();
				listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
				if(listBBContacts.size()>0)
				{
					isExist=true;
					BBID=listBBContacts.get(0).getBBID();
				}

				if(isExist)
				{
					if(!displayName.trim().equals(""))
					{
						holder.tvContactName.setText(Uri.decode(displayName));
					}
					else
					{
						holder.tvContactName.setText(mContext.getResources().getString(R.string.txtUnknown));
					}

					//				holder.tvContactName.setText(listBBContacts.get(0).getName());
				}
				else
				{
					if(!displayName.trim().equals(""))
					{
						holder.tvContactName.setText(Uri.decode(displayName));
					}
					else
					{
						holder.tvContactName.setText(mContext.getResources().getString(R.string.txtUnknown));
					}
					//				holder.tvContactName.setText(listMessageContacts.get(position).getFrom_user_phone());
				}
			}

			//Display name from DB if user is Unknown

			if(holder.tvContactName.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.txtUnknown))){
				holder.tvContactName.setText(Uri.decode(listMessageContacts.get(position).getName()));
			}		

			if(listMessageContacts.get(position).getDatatime()!=null)
			{
				String time=GlobalConfig_Methods.isUnlockTimeExpire(listMessageContacts.get(position).getDatatime());
				dateArray=time.split(" ");
				if(time_format==12)
				{
					try 
					{
						holder.tvTime.setText(GlobalConfig_Methods.Convert24to12(time).split(",")[1]);
					}
					catch (Exception e) 
					{
						e.getMessage();
					}
				}
				else if(time_format==24)
				{
					if(dateArray.length==1)
					{
						holder.tvTime.setText(dateArray[0]);
					}
					else if(dateArray.length==2)
					{
						holder.tvTime.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);
					}
				}
			}

			if(listMessageContacts.get(position).getStatus().equalsIgnoreCase("1"))
			{
				holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notificationimageborder));
				convertView.setBackgroundColor(Color.parseColor("#80e6e6e6"));


				convertView.setBackgroundColor(Color.parseColor("#63b8ff"));
				holder.tvContactName.setTextColor(Color.parseColor("#000000"));
				holder.tvMessageDetail.setTextColor(Color.parseColor("#000000"));
				holder.tvTime.setTextColor(Color.parseColor("#000000"));
				/*holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.arrow_blue));	*/


				//				holder.imViewStatus.setVisibility(View.VISIBLE);
			}
			else 
			{
				holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.messagelist_border_grey));
				convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
				//				holder.imViewStatus.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

}