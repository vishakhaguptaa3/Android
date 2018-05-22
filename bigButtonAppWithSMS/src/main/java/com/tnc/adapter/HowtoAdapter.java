package com.tnc.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.webresponse.HowtoReponseDataBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HowtoAdapter  extends BaseAdapter 
{
	public ArrayList<HowtoReponseDataBean> listHowToQuestions=new ArrayList<HowtoReponseDataBean>();
	private Context mContext;
	private int selectedPosition;
	private boolean isColorSet;
	private ViewHolder holder;
	private ImageLoadTask imageLoader=null;
	//	ImageLoader imageLoaderSecondary=null;
	//	Bitmap _bmp=null,bitmapAppIcon=null;
	//	ArrayList<BBContactsBean> listBBContacts;
	private Calendar cal;
	private TimeZone tz;
	//	int matching_user_id;
	//	int BBID=0;
	//	public  Bitmap displayImage=null;
	//	String phoneNumber="";
	//	public String displayName="";
	private SharedPreference pref;
	//	ArrayList<ContactTilesBean> listContactTiles=null;
	public String messageToDisplay="";
	public View rowView;

	public HowtoAdapter(Context mContext,
			ArrayList<HowtoReponseDataBean> listHowToQuestions)
	{
		this.listHowToQuestions=new ArrayList<HowtoReponseDataBean>();
		this.listHowToQuestions=listHowToQuestions;
		this.mContext=mContext;
		//		this.imageLoaderSecondary = new ImageLoader(this.mContext);
		//		_bmp=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
		//		bitmapAppIcon=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.appicon_96))).getBitmap();
		cal = Calendar.getInstance();
		tz = cal.getTimeZone();
		pref=new SharedPreference();
		//		this.notificationUpdate=notificationUpdate;
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
		return listHowToQuestions.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	public class ViewHolder
	{
		public LinearLayout llImageBoundary;
		public TextView tvQuestion,tvAnswer,tvTime;
		public RoundedImageViewCircular imViewNotificationContact;
		public ImageView imViewArrow;
		//		Button btnReply;
	}

	@SuppressWarnings({ "deprecation", "unused" })
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder=null;
		//		listBBContacts=new ArrayList<BBContactsBean>();
		int BBID=0;
		boolean isExist=false;
		boolean setAppIcon=false;
		//		displayImage=null;
		messageToDisplay="";
		rowView=convertView;
		//reuse views
		if (rowView== null){
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.howtoadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.imViewNotificationContact=(RoundedImageViewCircular) rowView.findViewById(R.id.imViewNotificationContact);
			holder.tvQuestion=(TextView) rowView.findViewById(R.id.tvQuestion);
			holder.tvAnswer=(TextView) rowView.findViewById(R.id.tvAnswer);
			holder.tvTime=(TextView) rowView.findViewById(R.id.tvTime);
			holder.imViewArrow=(ImageView)rowView.findViewById(R.id.imViewArrow);

			holder.llImageBoundary=(LinearLayout) rowView.findViewById(R.id.llImageBoundary);
			rowView.setTag(holder);
		}
		else{
			//			// fill data
			holder = (ViewHolder)rowView.getTag();
		}

		rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		/*if(position%2==0){
			rowView.setBackgroundColor(Color.parseColor("#EFEDED"));
		}
		else{
			rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}*/
		if(isColorSet && position==selectedPosition){
			((LinearLayout) rowView).setBackgroundColor(mContext
					.getResources().getColor(R.color.stripDarkBlueColor));
			holder.tvQuestion.setTextColor(mContext.getResources().getColor(
					R.color.white));
			holder.tvAnswer.setTextColor(mContext.getResources().getColor(
					R.color.white));
			holder.tvTime.setTextColor(mContext.getResources().getColor(
					R.color.white));
			holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.arrow_blue));
		}
		else{
			holder.tvQuestion.setTextColor(mContext.getResources().getColor(
					R.color.textBlueColor));
			holder.tvAnswer.setTextColor(mContext.getResources().getColor(
					android.R.color.black));

			/*try {
				if(listDataNotifications.get(position).status==1)//Unread
				{
					holder.tvNotificationDetail.setTypeface(null,Typeface.BOLD);
					holder.tvNotificationDetail.setTextColor(Color.parseColor("#000000"));
					holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notificationimageborder));
				}
				else if(listDataNotifications.get(position).status==2)  //Read
				{
					holder.tvNotificationDetail.setTypeface(null,Typeface.NORMAL);
					holder.tvNotificationDetail.setTextColor(Color.parseColor("#555555"));
					holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notification_image_border_grey));
				}
			} catch (NoSuchMethodError e) {
				e.getMessage();
			}*/
			holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.arrow_blue));
		}

		CustomFonts.setFontOfTextView(mContext,holder.tvQuestion, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(mContext,holder.tvTime, "fonts/Roboto-Regular_1.ttf");
		holder.llImageBoundary.setVisibility(View.GONE);
		holder.imViewNotificationContact.setVisibility(View.GONE);
		holder.tvQuestion.setTypeface(null,Typeface.BOLD);
		holder.tvQuestion.setText(String.valueOf(position+1)+". "+Html.fromHtml(listHowToQuestions.get(position).question).toString());
		holder.tvAnswer.setText(Html.fromHtml(listHowToQuestions.get(position).answer).toString());
		holder.tvAnswer.setVisibility(View.GONE);
		String time=String.valueOf(listHowToQuestions.get(position).add_date);
		String formattedDate=GlobalConfig_Methods.isUnlockTimeExpire(time);
		int timeSeconds=GlobalConfig_Methods.getTimeDifference_Seconds_Local(formattedDate);
		int timeHours=0;
		int timeDays=0;
		holder.tvTime.setVisibility(View.GONE);
		if(timeSeconds==0)
		{
			holder.tvTime.setText(mContext.getResources().getString(R.string.txtJustNow)); //"Just Now"
		}
		else if(timeSeconds>0 && timeSeconds<60)
		{
			holder.tvTime.setText(timeSeconds+" "+ (mContext.getResources().getString(R.string.txtSecondsAgo))); //"seconds ago"
		}
		else if(timeSeconds>=60 && timeSeconds<120)
		{
			holder.tvTime.setText((mContext.getResources().getString(R.string.txtOneMinuteAgo))); //"1 min ago"
		}
		else
		{
			int timeMinutes=GlobalConfig_Methods.getTimeDifference_Minutes_Local(formattedDate);
			timeHours=timeMinutes/60;
			if(timeMinutes>1 && timeMinutes<=1440)
			{
				if(timeMinutes>1 && timeMinutes<60)
				{
					holder.tvTime.setText(timeMinutes+" " + (mContext.getResources().getString(R.string.txtMinutesAgo)));
				}
				else if(timeHours>=1 && timeHours<2)
				{
					holder.tvTime.setText(timeHours+" " + (mContext.getResources().getString(R.string.txtHourAgo)));
				}
				else if(timeHours>=1 && timeHours<24)
				{
					holder.tvTime.setText(timeHours+" " + (mContext.getResources().getString(R.string.txtHoursAgo)));
				}
			}
			else if(timeHours>=24)
			{
				timeDays=timeHours/24;
				if((timeDays<=1) ||(timeDays>=1 && timeDays<2))
				{
					holder.tvTime.setText((mContext.getResources().getString(R.string.txtOneDayAgo)));
				}
				else if(timeDays>=2)
				{
					holder.tvTime.setText(timeDays+" " + (mContext.getResources().getString(R.string.txtDaysAgo)));
				}
			}
		}

		return rowView;
	}
}
