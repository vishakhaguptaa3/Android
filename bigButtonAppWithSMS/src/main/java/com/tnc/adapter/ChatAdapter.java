package com.tnc.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.auth.FirebaseAuth;
import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.model.Chat;
import com.tnc.model.Message;
import com.tnc.quickaction.ActionItem;
import com.tnc.quickaction.QuickAction;
import com.tnc.quickaction.QuickAction.OnActionItemClickListener;
import com.tnc.webresponse.SendMessageReponseDataBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.media.CamcorderProfile.get;

@SuppressLint("DefaultLocale")
public class ChatAdapter extends BaseAdapter
{
	private Context mContext;
	public ArrayList<SendMessageReponseDataBean> listChatMessages;
	private int userid;
	private List<Chat> mChats;
	private ActionItem itemCopy,itemDelete;
	private QuickAction quickAction;
	private int position;
	private String strTextCopied="";
	private String strIdCopied="";
	private int positionListItem;
	private SimpleDateFormat writeFormat;


	public ChatAdapter(Context mContext,ArrayList<SendMessageReponseDataBean> listChatMessages,
			int userid)
	{
		itemCopy= new ActionItem(0,mContext.getResources().getString(R.string.txCopy),null);
		itemDelete	= new ActionItem(1, mContext.getResources().getString(R.string.txtDelete),null);
		//		itemForward= new ActionItem(2,"Forward",null);
		//use setSticky(true) to disable QuickAction dialog being dismisse
		// d after an item is clicked
		itemCopy.setSticky(true);
		itemDelete.setSticky(true);

		//create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
		//orientation
		quickAction = new QuickAction(mContext,QuickAction.HORIZONTAL);

		//add action items into QuickAction
		quickAction.addActionItem(itemCopy);
		quickAction.addActionItem(itemDelete);
		this.mContext=mContext;
		this.listChatMessages=new ArrayList<SendMessageReponseDataBean>();
		this.listChatMessages=listChatMessages;
		this.userid=userid;
		writeFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
	}

	@Override
	public int getCount() {
		return listChatMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return listChatMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void update(Chat chat) {
		int inextUPDATED = Collections.binarySearch(mChats, chat);
		if (inextUPDATED >= 0) {
			((Chat) mChats.get(inextUPDATED)).setPhotoURL_a(chat.getPhotoURL_a());
			notifyItemChanged(inextUPDATED);
		}

	}




	public ChatAdapter(Activity mContext, List<Chat> chats) {
		mChats = chats;
		this.mContext = mContext;
	}

	private void notifyItemChanged(int inextUPDATED) {

	}

	public void add(Chat chat) {
		mChats.add(chat);
		notifyItemInserted(mChats.size() - 1);

	}

	private void notifyItemInserted(int i) {
		}

	public int getItemCount() {
		if (mChats != null) {
			return mChats.size();
		}
		return 0;
	}


	static class ViewHolder
	{
		LinearLayout llSendmessage_row_left,llSendmessage_row_right;
		TextView tvMsg_sendmessage_left,tvDate_sendmessage_left,
		tvMsg_sendmessage_right,tvDate_sendmessage_right;
		ImageView imageright,imageleft;
	}

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	@Override


	public View getView(int location, View convertView, ViewGroup parent)
	{
		position=location;
		View row=convertView;
		ViewHolder holder;
		String dateArray[];

		//		String currentDate=null;

		//reuse views
		if ( row== null)
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.chat_adapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.llSendmessage_row_left=(LinearLayout) convertView.findViewById(R.id.llSendmessage_row_left);
			holder.llSendmessage_row_right=(LinearLayout) convertView.findViewById(R.id.llSendmessage_row_right);
			holder.tvMsg_sendmessage_left=(TextView) convertView.findViewById(R.id.tvMsg_sendmessage_left);
			holder.tvDate_sendmessage_left=(TextView) convertView.findViewById(R.id.tvDate_sendmessage_left);
			holder.tvMsg_sendmessage_right=(TextView) convertView.findViewById(R.id.tvMsg_sendmessage_right);
			holder.tvDate_sendmessage_right=(TextView) convertView.findViewById(R.id.tvDate_sendmessage_right);
			row.setTag(holder);
//			holder.imageleft = (ImageView)convertView.findViewById(R.id.imageleft);
//			holder.imageright = (ImageView)convertView.findViewById(R.id.imageright);

		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		holder.llSendmessage_row_left.setVisibility(View.GONE);
		holder.llSendmessage_row_right.setVisibility(View.GONE);

		holder.tvMsg_sendmessage_left.setMovementMethod(LinkMovementMethod.getInstance());
		holder.tvMsg_sendmessage_right.setMovementMethod(LinkMovementMethod.getInstance());


		int time_format = 0;
		String time=GlobalConfig_Methods.getDateTimeLocal(listChatMessages.get(position).getDatatime());
		try
		{
			time_format = Settings.System.getInt(mContext.getContentResolver(), Settings.System.TIME_12_24);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		if(listChatMessages.get(position).getTo_user_id()==null || listChatMessages.get(position).getTo_user_id().trim().equals(""))
		{
			holder.llSendmessage_row_left.setVisibility(View.GONE);
			holder.llSendmessage_row_right.setVisibility(View.VISIBLE);
			holder.tvMsg_sendmessage_right.setTextColor(Color.parseColor("#ffffff"));
			holder.tvMsg_sendmessage_right.setText(Uri.decode(listChatMessages.get(position).getMessage()));
			if(listChatMessages.get(position).getDatatime()!=null)
			{
				dateArray=time.split(" ");
				if(time_format==12)
				{
					try
					{
						if(listChatMessages.get(position).getDatatime().contains("-"))
						{
							holder.tvDate_sendmessage_right.setText(time.split(" ")[1].split(":")[0]+":"+time.split(" ")[1].split(":")[1]+" "+time.split(" ")[2]);
						}
						else {
							holder.tvDate_sendmessage_right.setText(time);
						}
					}
					catch (Exception e)
					{
						e.getMessage();
					}
				}
				else if(time_format==24)
				{
					try {
						if(dateArray.length==1)
						{
							holder.tvDate_sendmessage_right.setText(dateArray[0]);
						}
						else if(dateArray.length==2)
						{
							holder.tvDate_sendmessage_right.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);//displayFormat.format(date)--------parseFormat.format(date) + " = " +
						}

					} catch (Exception e) {
						e.getMessage();
					}
				}
				else if(time_format==0)
				{
					if(dateArray.length==1)
						holder.tvDate_sendmessage_right.setText(dateArray[0]);
					else if(dateArray.length==2)
						holder.tvDate_sendmessage_right.setText(dateArray[1]);
				}
			}
		}
		else if(Integer.parseInt(listChatMessages.get(position).getTo_user_id())!=userid) //For The person who is doing chat on his own mobile phone
		{

			holder.llSendmessage_row_left.setVisibility(View.VISIBLE);
			holder.llSendmessage_row_right.setVisibility(View.GONE);
			holder.tvMsg_sendmessage_left.setText(Uri.decode(Uri.decode(listChatMessages.get(position).getMessage())));
			if(listChatMessages.get(position).getDatatime()!=null)
			{
				dateArray=time.split(" ");
				if(time_format==12)
				{
					holder.tvDate_sendmessage_left.setText(GlobalConfig_Methods.Convert24to12WithDate(time));
				}
				else if(time_format==24)
				{
					holder.tvDate_sendmessage_left.setText(GlobalConfig_Methods.ConvertDate12WithDate(time));
					/*if(dateArray.length==1)
					{
						holder.tvDate_sendmessage_left.setText(dateArray[0]);
					}
					else if(dateArray.length==2)
						holder.tvDate_sendmessage_left.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);*/
				}
				else if(time_format==0)
				{
					holder.tvDate_sendmessage_left.setText(GlobalConfig_Methods.ConvertDate12WithDate(time));
					//					holder.tvDate_sendmessage_left.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);
				}
			}
		}
		else if(Integer.parseInt(listChatMessages.get(position).getTo_user_id())==userid) // For the person who is responding to the chat

		{
			holder.llSendmessage_row_left.setVisibility(View.GONE);
			holder.llSendmessage_row_right.setVisibility(View.VISIBLE);
			holder.tvMsg_sendmessage_right.setTextColor(Color.parseColor("#ffffff"));
			holder.tvMsg_sendmessage_right.setText(Uri.decode(listChatMessages.get(position).getMessage()));
			if(listChatMessages.get(position).getDatatime()!=null)
			{
				dateArray=time.split(" ");
				if(time_format==12)
				{
					holder.tvDate_sendmessage_right.setText(GlobalConfig_Methods.Convert24to12WithDate(time));
				}
				else if(time_format==24)
				{
					holder.tvDate_sendmessage_right.setText(GlobalConfig_Methods.ConvertDate12WithDate(time));
					//					holder.tvDate_sendmessage_right.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);
				}
				else if(time_format==0)
				{
					holder.tvDate_sendmessage_right.setText(GlobalConfig_Methods.ConvertDate12WithDate(time));
					//					holder.tvDate_sendmessage_right.setText(dateArray[1].split(":")[0]+":"+dateArray[1].split(":")[1]);
				}
			}
		}
		if(holder.llSendmessage_row_left.getVisibility()==View.VISIBLE)
		{
			holder.llSendmessage_row_left.setTag(R.id.llSendmessage_row_left,position);
		}
		else if(holder.llSendmessage_row_right.getVisibility()==View.VISIBLE)
		{
			holder.llSendmessage_row_right.setTag(R.id.llSendmessage_row_right,position);
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvMsg_sendmessage_left,"fonts/Roboto-Regular_1.ttf.ttf");
		CustomFonts.setFontOfTextView(mContext, holder.tvMsg_sendmessage_left,"fonts/Roboto-Regular_1.ttf.ttf");
		holder.llSendmessage_row_left.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				quickAction.show(v);
				Object pos = v.getTag(R.id.llSendmessage_row_left);
				positionListItem=Integer.parseInt(String.valueOf(pos));
				strTextCopied=listChatMessages.get(Integer.parseInt(String.valueOf(pos))).message;
				strIdCopied=listChatMessages.get(Integer.parseInt(String.valueOf(pos))).message_id;
				return true;
			}
		});
		holder.llSendmessage_row_right.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				quickAction.show(v);
				Object pos = v.getTag(R.id.llSendmessage_row_right);
				positionListItem=Integer.parseInt(String.valueOf(pos));
				strTextCopied=listChatMessages.get(Integer.parseInt(String.valueOf(pos))).message;
				strIdCopied=listChatMessages.get(Integer.parseInt(String.valueOf(pos))).message_id;
				return true;
			}
		});

		quickAction.setOnActionItemClickListener(new OnActionItemClickListener()
		{
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId)
			{
				if(pos==0)
				{
					ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("text_content",Uri.decode(strTextCopied));
					clipboard.setPrimaryClip(clip);
					quickAction.dismiss();
				}
				else if(pos==1)
				{
					quickAction.dismiss();
					if(strIdCopied!=null && !strIdCopied.trim().equals(""))
					{
						((HomeScreenActivity)mContext).runOnUiThread(new Runnable()
						{
							@Override
							public void run() {
								int i=DBQuery.deleteMessage(mContext, strIdCopied);
								if(!(i<0))
								{
									listChatMessages.remove(positionListItem);
									notifyDataSetChanged();
								}
							}
						});
					}
					/*else if(strTextCopied!=null && !strTextCopied.trim().equals(""))
					{
						listDeleteMessages.add(strTextCopied);
						listChatMessages.remove(positionListItem);
						notifyDataSetChanged();
					}*/
				}
			}
		});
		return row;

	}


}