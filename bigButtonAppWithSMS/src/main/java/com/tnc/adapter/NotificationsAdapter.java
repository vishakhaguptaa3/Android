package com.tnc.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.tnc.R;
import com.tnc.Loader.ImageLoader;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.NotificationReponseDataBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class NotificationsAdapter extends BaseAdapter
{
    public ArrayList<NotificationReponseDataBean> listDataNotifications=new ArrayList<NotificationReponseDataBean>();
    private Context mContext;
    private int selectedPosition;
    private boolean isColorSet;
    private ViewHolder holder;
    //	NotificationUpdateAction notificationUpdate;
    private ImageLoadTask imageLoader=null;
    private ImageLoader imageLoaderSecondary=null;
    private Bitmap _bmp=null,bitmapAppIcon=null;
    private ArrayList<BBContactsBean> listBBContacts;
    private Calendar cal;
    private TimeZone tz;
    private int matching_user_id;
    private int BBID=0;
    public  Bitmap displayImage=null;
    private String phoneNumber="";
    public String displayName="";
    private SharedPreference pref;
    private ArrayList<ContactTilesBean> listContactTiles=null;
    public String messageToDisplay="";
    public View rowView;

    public NotificationsAdapter(Context mContext,
                                ArrayList<NotificationReponseDataBean> listDataNotifications)
    {
        this.listDataNotifications=new ArrayList<NotificationReponseDataBean>();
        this.listDataNotifications=listDataNotifications;
        this.mContext=mContext;
        this.imageLoaderSecondary = new ImageLoader(this.mContext);
        _bmp=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
        bitmapAppIcon=((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.appicon_96))).getBitmap();
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
        return listDataNotifications.size();
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
        public TextView tvNotification,tvNotificationDetail,tvTime;
        public RoundedImageViewCircular imViewNotificationContact;
        public ImageView imViewArrow;
        //		Button btnReply;
    }

    @SuppressWarnings({ "deprecation", "unused" })
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder=null;
        listBBContacts=new ArrayList<BBContactsBean>();
        int BBID=0;
        boolean isExist=false;
        boolean setAppIcon=false;
        displayImage=null;
        messageToDisplay="";
        rowView=convertView;
        //reuse views
        if (rowView== null){
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.notificationadapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.imViewNotificationContact=(RoundedImageViewCircular) rowView.findViewById(R.id.imViewNotificationContact);
            holder.tvNotification=(TextView) rowView.findViewById(R.id.tvNotification);
            holder.tvNotificationDetail=(TextView) rowView.findViewById(R.id.tvNotificationDetail);
            holder.tvTime=(TextView) rowView.findViewById(R.id.tvTime);
            holder.imViewArrow=(ImageView)rowView.findViewById(R.id.imViewArrow);

            holder.llImageBoundary=(LinearLayout) rowView.findViewById(R.id.llImageBoundary);
            //			holder.btnReply=(Button) convertView.findViewById(R.id.btnReply);
            rowView.setTag(holder);
        }
        else{
            //			// fill data
            holder = (ViewHolder)rowView.getTag();
        }

        rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));

		/*rowView.setBackgroundColor(Color.parseColor("#EFEDED"));
		if(position%2==0){
			rowView.setBackgroundColor(Color.parseColor("#EFEDED"));
		}
		else{
			rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}*/
        if(isColorSet && position==selectedPosition){
            ((LinearLayout) rowView).setBackgroundColor(mContext
                    .getResources().getColor(R.color.stripDarkBlueColor));
            holder.tvNotification.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.tvNotificationDetail.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.tvTime.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_blue));
        }
        else{
            holder.tvNotification.setTextColor(mContext.getResources().getColor(
                    R.color.textBlueColor));
            holder.tvNotificationDetail.setTextColor(mContext.getResources().getColor(
                    R.color.darkGreyColor));

            try {
                if(listDataNotifications.get(position).status==1)//Unread
                {
                    holder.tvNotificationDetail.setTypeface(null,Typeface.BOLD);
                    holder.tvNotificationDetail.setTextColor(Color.parseColor("#000000"));
                    holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notificationimageborder));
                    rowView.setBackgroundColor(Color.parseColor("#80e6e6e6"));

                    //row background color
                    ((LinearLayout) rowView).setBackgroundColor(Color.parseColor("#63b8ff"));
                    holder.tvNotification.setTextColor(Color.parseColor("#000000"));
                    holder.tvNotificationDetail.setTextColor(Color.parseColor("#000000"));
                    holder.tvTime.setTextColor(Color.parseColor("#000000"));
                    holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                            .getDrawable(R.drawable.arrow_blue));

                }
                else if(listDataNotifications.get(position).status==2)  //Read
                {
                    holder.tvNotificationDetail.setTypeface(null,Typeface.NORMAL);
                    holder.tvNotificationDetail.setTextColor(Color.parseColor("#555555"));
                    holder.llImageBoundary.setBackground(mContext.getResources().getDrawable(R.drawable.notification_image_border_grey));
                    rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            } catch (NoSuchMethodError e) {
                e.getMessage();
            }
            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_blue));
        }

        CustomFonts.setFontOfTextView(mContext,holder.tvNotification, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(mContext,holder.tvTime, "fonts/Roboto-Regular_1.ttf");

        if(listDataNotifications.get(position).getMessage().toLowerCase().contains("welcome"))
        {
            displayName = "Welcome";
            holder.tvNotification.setText("Welcome");
            holder.tvNotificationDetail.setText(listDataNotifications.get(position).message);
            matching_user_id=Integer.parseInt(listDataNotifications.get(position).getTo_user_id());
            listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
            if(listBBContacts.size()>0){
                BBID=listBBContacts.get(0).getBBID();
            }
            else
            {
                matching_user_id=Integer.parseInt(listDataNotifications.get(position).getFrom_user_id());
                listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
                if(listBBContacts.size()>0)
                {
                    BBID=listBBContacts.get(0).getBBID();
                }
            }
            if(BBID!=0)
            {
                if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().trim().equalsIgnoreCase("NULL"))
                {
                    //					setDisplayImage=false;
                    imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewNotificationContact,320);
                    imageLoader.execute();
                }
                else{
                    //					setDisplayImage=false;
                    holder.imViewNotificationContact.setImageResource(R.drawable.appicon);//play_icon_round_non_border
                }
            }
            else if(BBID==0)
            {
                //				setDisplayImage=false;
                holder.imViewNotificationContact.setImageResource(R.drawable.appicon);//play_icon_round_non_border
            }
        }
        else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("requesting"))
        {
            messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("is requesting")-1,listDataNotifications.get(position).message.length());
        }
        else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("responded"))
        {
            messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("responded")-1,listDataNotifications.get(position).message.length());
        }
        if(!listDataNotifications.get(position).getMessage().contains("Welcome"))
        {
            if(listDataNotifications.get(position).getMessage().contains("declined")){
                //system.out.println("");
            }
            listContactTiles=new ArrayList<ContactTilesBean>();

            if(pref.getBBID(mContext).equals(listDataNotifications.get(position).getFrom_user_id()))
            {
                matching_user_id=Integer.parseInt(listDataNotifications.get(position).getTo_user_id());
                phoneNumber=DBQuery.getPhoneNumberFromBBID(mContext,Integer.parseInt(listDataNotifications.get(position).getTo_user_id()));
            }
            else if(pref.getBBID(mContext).equals(listDataNotifications.get(position).getTo_user_id())) {
                matching_user_id=Integer.parseInt(listDataNotifications.get(position).getFrom_user_id());
                phoneNumber=DBQuery.getPhoneNumberFromBBID(mContext,Integer.parseInt(listDataNotifications.get(position).getFrom_user_id()));
            }

            if((phoneNumber==null)|| (phoneNumber!=null && phoneNumber.trim().equals(""))){
                displayName="Unknown";
                holder.imViewNotificationContact.setImageBitmap(_bmp);
                //				setDisplayImage=false;
            }
            else{  // fetch details from tile table using phonenumber
                listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,GlobalConfig_Methods.trimSpecialCharactersFromString(phoneNumber));
                if(listContactTiles.size()>0)  // In case of Tile of the number exists
                {
                    displayName=listContactTiles.get(0).getName();
                    if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0)
                    {
                        byte arrayImage[]=listContactTiles.get(0).getImage();
                        if(arrayImage!=null && arrayImage.length>0)
                        {
                            displayImage=BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length);
                            holder.imViewNotificationContact.setImageBitmap(displayImage);
                        }
                    }
                    else {
                        // fetch image from bbContacts table if doesn't exists in tile table
                        listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
                        if(listBBContacts.size()>0)
                        {
                            isExist=true;
                            BBID=listBBContacts.get(0).getBBID();
                        }
                        if(BBID!=0)  // In case of contact is found as BB Contact then fetch image from bbContacts table if doesn't existes in tile table
                        {
                            if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
                            {
                                imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewNotificationContact,320);
                                imageLoader.execute();
                            }
                            else{
                                displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                                if(displayImage!=null)
                                {
                                    holder.imViewNotificationContact.setImageBitmap(displayImage);
                                    //									setDisplayImage=false;
                                }
                                else{
                                    holder.imViewNotificationContact.setImageBitmap(_bmp);
                                    //									setDisplayImage=false;
                                    if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy") ||
                                            listDataNotifications.get(position).getMessage().toLowerCase().contains("number from")){
                                        setAppIcon=true;
                                    }
                                }
                            }
                        }
                        else{      // In case of contact is not found as BB Contact,then fetch image from Phone Contacts table if doesn't exists in tile table
                            displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                            if(displayImage!=null)
                            {
                                holder.imViewNotificationContact.setImageBitmap(displayImage);
                                //								setDisplayImage=false;
                            }
                            else{
                                holder.imViewNotificationContact.setImageBitmap(_bmp);
                                if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy") ||
                                        listDataNotifications.get(position).getMessage().toLowerCase().contains("number from")){
                                    setAppIcon=true;
                                }
                                //								setDisplayImage=false;
                            }
                        }
                    }
                }  //End of  case if tile exists
                else{
                    // In case of tile of that number doesn't exists then fetch details of BBContact user
                    listBBContacts=DBQuery.checkBBContactExistence(mContext,matching_user_id);
                    if(listBBContacts.size()>0)
                    {
                        isExist=true;
                        BBID=listBBContacts.get(0).getBBID();
                    }
                    if(BBID!=0)  // In case of contact is found as BB Contact then fetch image from bbContacts table
                    {
                        displayName=listBBContacts.get(0).getName();//GlobalConfig_Methods.getContactName(mContext, phoneNumber);
                        if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
                        {
                            //							setDisplayImage=false;
                            imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewNotificationContact,320);
                            imageLoader.execute();
                        }
                        else{
                            // In case of contact is not found as BB Contact then fetch image from Phone Contacts
                            displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                            if(displayImage!=null)
                            {
                                //								setDisplayImage=false;
                                holder.imViewNotificationContact.setImageBitmap(displayImage);
                            }
                            else{
                                //								setDisplayImage=false;
                                holder.imViewNotificationContact.setImageBitmap(_bmp);
                                if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy") ||
                                        listDataNotifications.get(position).getMessage().toLowerCase().contains("number from")){
                                    setAppIcon=true;
                                }
                            }
                        }
                    }
                    else{  // In case of contact is not found as BB Contact then fetch Details from Phone Contacts
                        if(!phoneNumber.trim().equals(""))
                        {
                            displayName=GlobalConfig_Methods.getContactName(mContext, phoneNumber);
                            displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                        }
                        if((displayName==null) || (displayName!=null && displayName.equals("")))
                        {
                            displayName="Unknown";
                        }
                        if(displayImage!=null)
                        {
                            //							setDisplayImage=false;
                            holder.imViewNotificationContact.setImageBitmap(displayImage);
                        }
                        else{
                            //							setDisplayImage=false;
                            holder.imViewNotificationContact.setImageBitmap(_bmp);
                            if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy") ||
                                    listDataNotifications.get(position).getMessage().toLowerCase().contains("number from")){
                                setAppIcon=true;
                            }
                        }
                    }
                }//End of  case if tile don't exists
            }
            //set display contents here
            if(listDataNotifications.get(position).getMessage().toLowerCase().contains("requesting"))
            {
                messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("is requesting")-1,listDataNotifications.get(position).message.length());
                if(displayName.equalsIgnoreCase("Unknown"))
                {
                    displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("is requesting")-1);
                }
            }
            else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("responded"))
            {
                messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("responded")-1,listDataNotifications.get(position).message.length());
                if(displayName.equalsIgnoreCase("Unknown"))
                {
                    displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("responded")-1);
                }
            }
            else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("shared his/her contacts"))
            {
                messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("has shared his/her contacts")-1,listDataNotifications.get(position).message.length());
                if(displayName.trim().equals("") || displayName.equalsIgnoreCase("Unknown"))
                {
                    displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("has shared his/her contacts")-1);
                }
            }
            else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("accept") ||
                    listDataNotifications.get(position).getMessage().toLowerCase().contains("decline") ||
                    listDataNotifications.get(position).getMessage().toLowerCase().contains("accepted") ||
                    listDataNotifications.get(position).getMessage().toLowerCase().contains("declined"))
            {
                if(listDataNotifications.get(position).getMessage().toLowerCase().contains("accept"))
                {
                    messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("accept")-1,listDataNotifications.get(position).message.length());
                    if(displayName.trim().equals("") || displayName.equalsIgnoreCase("unknown")){
                        displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("accept")-1);
                    }
                }
                else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("accepted"))
                {
                    messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("accepted")-1,listDataNotifications.get(position).message.length());
                    if(displayName.trim().equals("") || displayName.equalsIgnoreCase("unknown")){
                        displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("accepted")-1);
                    }
                }
                else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("decline"))
                {
                    messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("decline")-1,listDataNotifications.get(position).message.length());
                    if(displayName.trim().equals("") || displayName.equalsIgnoreCase("unknown")){
                        displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("decline")-1);
                    }
                }
                else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("declined"))
                {
                    messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("declined")-1,listDataNotifications.get(position).message.length());
                    if(displayName.trim().equals("") || displayName.equalsIgnoreCase("unknown")){
                        displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("declined")-1);
                    }
                }

				/*messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("has")-1,listDataNotifications.get(position).message.length());
				if(displayName.equalsIgnoreCase("Unknown"))
				{
					displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("has")-1);
				}*/
            }

            else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy") ||
                    listDataNotifications.get(position).getMessage().toLowerCase().contains("joined chatstasy"))/*if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined tap-n-chat") ||
                    listDataNotifications.get(position).getMessage().toLowerCase().contains("joined tap-n-chat"))*/
            {
                if(displayName.equalsIgnoreCase("Unknown"))
                {
                    displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("joined chatstasy")-4);
                }
                messageToDisplay="has joined \nChaststasy. You can now chat with " +Uri.decode(displayName) + "  via text."; //listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.toLowerCase().indexOf("joined the tap-n-chat")-1,listDataNotifications.get(position).message.length());
            }
            else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("number from"))
            {
                String strMessage=listDataNotifications.get(position).message;
                String oldNumber="",newNumber="";

                try{
                    //Extract oldnumber from the notification
                    oldNumber=strMessage.substring(strMessage.indexOf("from")+4,strMessage.indexOf("to")).trim();
                    String oldCountryCode=oldNumber.substring(0,oldNumber.length()-10).trim();
                    String oldMobileNumber=oldNumber.substring(oldNumber.length()-10,oldNumber.length()).trim();

                    //Extract newnumber from the notification
                    newNumber=strMessage.substring(strMessage.indexOf("to")+2,strMessage.length()).trim();
                    String newCountryCode=newNumber.substring(0,newNumber.length()-10).trim();
                    String newMobileNumber=newNumber.substring(newNumber.length()-10,newNumber.length()).trim();

                    messageToDisplay="has changed his/her number from "+GlobalConfig_Methods.getFormattedNumber(mContext,oldNumber)+
                            " to " + GlobalConfig_Methods.getFormattedNumber(mContext,newNumber);

					/*messageToDisplay="has changed his/her number from "+oldCountryCode+"-"+oldMobileNumber+
							" to " + newCountryCode + "-" + newMobileNumber;*/
                }catch(Exception e){
                    e.getMessage();
                }

                if(displayName.equalsIgnoreCase("Unknown")){
                    displayName=listDataNotifications.get(position).message.substring(0,listDataNotifications.get(position).message.toLowerCase().indexOf("has changed his")-1);
                }
            }
            else{
                if(displayName.trim().equalsIgnoreCase(""))
                    displayName="";
                messageToDisplay=listDataNotifications.get(position).message;
            }
            holder.tvNotification.setText(Uri.decode(displayName));
        }
        if(displayName.trim().equals(""))
        {
            holder.tvNotification.setText(mContext.getResources().getString(R.string.app_name));
            setAppIcon=true;
        }
        if(listDataNotifications.get(position).getMessage().toLowerCase().contains("your email id has been successfully verified") ||
                listDataNotifications.get(position).getMessage().toLowerCase().contains("your email id has been verified successfully")){
            setAppIcon=true;

            if(holder.tvNotification.getText().toString().equalsIgnoreCase("Unknown")){
                holder.tvNotification.setText(mContext.getResources().getString(R.string.app_name));
            }


        }
		/*if(listDataNotifications.get(position).getMessage().toLowerCase().contains("shared his/her contacts"))
		{
			messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.indexOf("shared his/her contacts")-1,listDataNotifications.get(position).message.length());
			holder.tvNotification.setText("Contact Sharing");
		}
		else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("shared contact"))
		{
			messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.indexOf("shared contact")-1,listDataNotifications.get(position).message.length());
			holder.tvNotification.setText("Contact Sharing");
		}

		else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("joined the tap-n-chat"))
		{
			messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.indexOf("joined the tap-n-chat")-1,listDataNotifications.get(position).message.length());
			holder.tvNotification.setText("Tap-n-Chat");
		}

		else if(listDataNotifications.get(position).getMessage().toLowerCase().contains("number from"))
		{
			messageToDisplay=listDataNotifications.get(position).message.substring(listDataNotifications.get(position).message.indexOf("number from")-1,listDataNotifications.get(position).message.length());
			holder.tvNotification.setText("Tap-n-Chat");
		}*/


        //Display App Icon into the notification in case Notification is for Notifying number about or availability of any user on tap-n-Chat
        if(setAppIcon){
            holder.imViewNotificationContact.setImageBitmap(bitmapAppIcon);
        }

        if(messageToDisplay.contains("\n")){
            messageToDisplay = messageToDisplay.replaceAll("\n", " ");
        }

        //Display name from DB if user is Unknown
        if(holder.tvNotification.getText().toString().equalsIgnoreCase("Unknown")){
            if(GlobalConfig_Methods.isValidString(listDataNotifications.get(position).getName())) // added in case of notification title is coming null beacause of "Unknown" value 7 getName() is also null
            {
                holder.tvNotification.setText(Uri.decode(listDataNotifications.get(position).getName()));
            }else{
                holder.tvNotification.setText(mContext.getResources().getString(R.string.app_name));
            }
        }

        if(holder.tvNotificationDetail.getText().toString().startsWith("Unknown")){
            holder.tvNotification.getText().toString().replaceFirst("Unknown",Uri.decode(listDataNotifications.get(position).getName()));
        }

        if(holder.tvNotificationDetail.getText().toString().startsWith("unknown")){
            holder.tvNotificationDetail.getText().toString().replaceFirst("unknown",Uri.decode(listDataNotifications.get(position).getName()));
        }

        //Is some version of the contents updated like cliparts,howto,faq,default messages,emergency numbers version
        if(Uri.decode(messageToDisplay).contains("Cliparts:")){
            messageToDisplay = messageToDisplay.replace("Cliparts:","");
        }else if(Uri.decode(messageToDisplay).contains("Faq:")){
            messageToDisplay = messageToDisplay.replace("Faq:", "");
        }else if(Uri.decode(messageToDisplay).contains("How To:")){
            messageToDisplay = messageToDisplay.replace("How To:","");
        }else if(Uri.decode(messageToDisplay).contains("Emergency Numbers:")){
            messageToDisplay = messageToDisplay.replace("Emergency Numbers:", "");
        }else if(Uri.decode(messageToDisplay).contains("Default Messages:")){
            messageToDisplay = messageToDisplay.replace("Default Messages:", "");
        }

        //trim name from notification over here
        if(!listDataNotifications.get(position).message.toLowerCase().contains("welcome")){
            String mNotificationDetails = "";

            if(listDataNotifications.get(position).message.toLowerCase().startsWith("please be aware that")){
                // in case of emergency contact notification
                mNotificationDetails = (Uri.decode(messageToDisplay)).trim();
            }else{
                if(!displayName.trim().toLowerCase().equals("unknown")) // added in case of courtesy notification was coming preappended with Unknown & to remove that
                {
                    mNotificationDetails = (Uri.decode(displayName)+" "+Uri.decode(messageToDisplay)).trim();
                }
                else{
                    mNotificationDetails = ""+Uri.decode(messageToDisplay).toString().trim();
                }
            }

            String mNotificationDetailsDisplay = "";
            if(mNotificationDetails.contains("Tap-n-Chat")){
                mNotificationDetailsDisplay = mNotificationDetails.replace("Tap-n-Chat",mContext.getResources().getString(R.string.app_name));
            }else if(mNotificationDetails.contains("tap-n-Chat")){
                mNotificationDetailsDisplay = mNotificationDetails.replace("tap-n-chat",mContext.getResources().getString(R.string.app_name));
            }else{
                mNotificationDetailsDisplay = mNotificationDetails;
            }

            // to add name in emergency aware notification (apended with image request)
            if(mNotificationDetailsDisplay.contains("aware that")){
                if(mNotificationDetailsDisplay.contains("has tagged")){
                    mNotificationDetailsDisplay =
                            mNotificationDetailsDisplay.substring(0,mNotificationDetailsDisplay.indexOf("aware that") + 10) + " " + displayName + " " +
                                    mNotificationDetailsDisplay.substring(mNotificationDetailsDisplay.indexOf("has tagged"), mNotificationDetailsDisplay.length());
                }

            }

            // to add name in emergency aware notification (apended with image request)
            if(mNotificationDetailsDisplay.startsWith("PLEASE BE AWARE THAT") && !displayName.equalsIgnoreCase("Unknown")){
                mNotificationDetailsDisplay =
                        mNotificationDetailsDisplay.substring(0,mNotificationDetailsDisplay.indexOf("AWARE THAT") + 10) + " " + displayName + " " +
                                mNotificationDetailsDisplay.substring(mNotificationDetailsDisplay.indexOf("HAD REQUESTED"), mNotificationDetailsDisplay.length());
            }
            if((mNotificationDetailsDisplay.startsWith("PLEASE BE AWARE THAT") && (displayName.equalsIgnoreCase("Unknown") || displayName.equalsIgnoreCase("")))){
                holder.tvNotification.setText( Uri.decode(  mNotificationDetailsDisplay.substring(mNotificationDetailsDisplay.indexOf("AWARE THAT") + 10,mNotificationDetailsDisplay.indexOf("HAD REQUESTED"))  ) .trim());
            }
            /**
             * mNotificationDetailsDisplay.substring(mNotificationDetailsDisplay.indexOf("AWARE THAT") + 10), 25)
             */

            holder.tvNotificationDetail.setText(mNotificationDetailsDisplay);
        }else{
            // For App name Change in the title
            String mTitleName = holder.tvNotificationDetail.getText().toString();
            String mTitleNameDisplay = "";
            if(mTitleName.contains("Tap-n-Chat")){
                mTitleNameDisplay = mTitleName.replace("Tap-n-Chat",mContext.getResources().getString(R.string.app_name));
            }else if(mTitleName.contains("tap-n-Chat")){
                mTitleNameDisplay = mTitleName.replace("tap-n-chat",mContext.getResources().getString(R.string.app_name));
            }else{
                mTitleNameDisplay = mTitleName;
            }
            holder.tvNotificationDetail.setText(mTitleNameDisplay);
        }

        String mNotificationMessage = holder.tvNotificationDetail.getText().toString();

        // for registration email verified successfully message
        if(mNotificationMessage.contains("Unknown")){
            mNotificationMessage = mNotificationMessage.replace("Unknown", "").trim();
            holder.tvNotificationDetail.setText(mNotificationMessage);
        }else if(mNotificationMessage.contains("unknown")){
            mNotificationMessage = mNotificationMessage.replace("unknown", "").trim();
            holder.tvNotificationDetail.setText(mNotificationMessage);
        }

        String time=String.valueOf(listDataNotifications.get(position).datetime);
        String formattedDate=GlobalConfig_Methods.isUnlockTimeExpire(time);
        int timeSeconds=GlobalConfig_Methods.getTimeDifference_Seconds_Local(formattedDate);
        int timeHours=0;
        int timeDays=0;
        if(timeSeconds==0)
        {
            holder.tvTime.setText(mContext.getResources().getString(R.string.txtJustNow)); //"Just Now"
        }
        else if(timeSeconds>0 && timeSeconds<60)
        {
            holder.tvTime.setText(timeSeconds+" " + mContext.getResources().getString(R.string.txtSecondsAgo)); //"seconds ago"
        }
        else if(timeSeconds>=60 && timeSeconds<120)
        {
            holder.tvTime.setText(mContext.getResources().getString(R.string.txtOneMinuteAgo)); //"1 min ago"
        }
        else
        {
            int timeMinutes=GlobalConfig_Methods.getTimeDifference_Minutes_Local(formattedDate);
            timeHours=timeMinutes/60;
            if(timeMinutes>1 && timeMinutes<=1440)
            {
                if(timeMinutes>1 && timeMinutes<60)
                {
                    holder.tvTime.setText(timeMinutes+" " + mContext.getResources().getString(R.string.txtMinutesAgo));
                }
                else if(timeHours>=1 && timeHours<2)
                {
                    holder.tvTime.setText(timeHours+" " + mContext.getResources().getString(R.string.txtHourAgo));
                }
                else if(timeHours>=1 && timeHours<24)
                {
                    holder.tvTime.setText(timeHours+" " + mContext.getResources().getString(R.string.txtHoursAgo));
                }
            }
            else if(timeHours>=24)
            {
                timeDays=timeHours/24;
                if((timeDays<=1) ||(timeDays>=1 && timeDays<2))
                {
                    holder.tvTime.setText(mContext.getResources().getString(R.string.txtOneDayAgo));
                }
                else if(timeDays>=2)
                {
                    holder.tvTime.setText(timeDays+" " + mContext.getResources().getString(R.string.txtDaysAgo));
                }
            }
        }

        return rowView;
    }
}