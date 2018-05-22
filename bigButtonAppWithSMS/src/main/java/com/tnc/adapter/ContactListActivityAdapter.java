package com.tnc.adapter;

import java.util.ArrayList;
import java.util.Locale;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.Name;
import com.tnc.database.DBQuery;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class ContactListActivityAdapter extends BaseAdapter {
	Context mContext;
	// Activity mAct;
	public ArrayList<ContactDetailsBean> listContacts;
	public ArrayList<ContactDetailsBean> listContactsShow;
	ArrayList<ContactDetailsBean> listContactsFiltered;
	//	ArrayList<BBContactsBean> listBBContacts = null;
	private ArrayList<ContactDetailsBean> listFirstName = null;
	private ArrayList<ContactDetailsBean> listLastName = null;
	private ArrayList<ContactDetailsBean> listContainsName = null;
	public ArrayList<String> listMenu = null;
	private boolean isMenuOption = false;
	private int selectedPosition;
	private boolean isColorSet;
	private int BBID = 0;
	private ArrayList<BBContactsBean> listBBContactsMatch;
	private int matching_user_id;
	private ImageLoadTask imageLoader = null;
	private SharedPreference saveState;
	private ContentResolver cr;

	/**
	 * 
	 * @param position
	 * @param isColorSet
	 * set selected List item row color and content color
	 */
	public void setRowColor(int position, boolean isColorSet) {
		selectedPosition = position;
		this.isColorSet = isColorSet;
		this.notifyDataSetInvalidated();
	}

	public ContactListActivityAdapter(Context mContext,
			ArrayList<ContactDetailsBean> listContacts,
			ArrayList<String> listMenu, boolean isMenuOption) {
		cr = mContext.getContentResolver();
		try {
			this.mContext = mContext;
			this.listContactsFiltered = new ArrayList<ContactDetailsBean>();
			this.listContactsShow = listContacts;
			this.listContacts = listContacts;
			this.listMenu = listMenu;
			this.isMenuOption = isMenuOption;
			saveState=new SharedPreference();
			if (!isMenuOption) {
				if (listContacts != null)
					this.listContactsFiltered.addAll(listContacts);

				if(saveState.isRegistered(mContext)){
					if(GlobalCommonValues.listBBContacts!=null){
						GlobalCommonValues.listBBContacts = DBQuery.getAllBBContacts(mContext);
					}
				}

				//				this.listBBContacts = new ArrayList<BBContactsBean>();
				/*if(saveState.isRegistered(mContext))
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
				}*/
			}
			this.notifyDataSetChanged();

		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public int getCount() {
		int count = 0;
		if (!isMenuOption) {
			if (listContactsShow != null)
				count = listContactsShow.size();
			else if (listMenu != null)
				count = listMenu.size();
		} else {
			count = listMenu.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		Object obj = null;
		if (!isMenuOption) {
			if (listContactsShow != null)
				obj = listContactsShow.get(position);
		} else {
			obj = listMenu.get(position);
		}
		return obj;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		FrameLayout llImageHolderparent;
		LinearLayout llContactNameHolder;
		TextView tvContactName;
		ImageView imViewTag, imViewArrow, imViewContactImage;
	}

	ViewHolder holder;

	@SuppressWarnings({ "deprecation", "unused" })
	@SuppressLint({ "InflateParams", "DefaultLocale" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean isExist = false;
		holder = null;
		// reuse views
		if (convertView == null) {
			LayoutInflater inflater=null;
			try {
				inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.contactlistadapter, null);
			} catch (Exception e) {
				//				Toast.makeText(mContext, e.getMessage(),1000).show();
			}

			// configure view holder
			holder = new ViewHolder();
			holder.tvContactName = (TextView) convertView
					.findViewById(R.id.tvContactName);
			holder.imViewTag = (ImageView) convertView
					.findViewById(R.id.imViewTag);
			holder.imViewArrow = (ImageView) convertView
					.findViewById(R.id.imViewArrow);
			holder.imViewContactImage = (ImageView) convertView
					.findViewById(R.id.imViewContactImage);
			holder.llImageHolderparent=(FrameLayout) convertView.findViewById(R.id.llImageHolderparent);
			holder.llContactNameHolder=(LinearLayout) convertView.findViewById(R.id.llContactNameHolder);
			convertView.setTag(holder);
		} else {
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}

		if (isColorSet && position == selectedPosition) {
			((RelativeLayout) convertView).setBackgroundColor(mContext
					.getResources().getColor(R.color.stripDarkBlueColor));
			holder.tvContactName.setTextColor(mContext.getResources().getColor(
					R.color.white));
			holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.arrow_white));

		} else {
			((RelativeLayout) convertView).setBackgroundColor(mContext
					.getResources().getColor(R.color.white));

			holder.tvContactName.setTextColor(mContext.getResources().getColor(
					R.color.textGreyColorChooseConatct));
			holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.arrow_blue));
		}
		CustomFonts.setFontOfTextView(mContext, holder.tvContactName,
				"fonts/Roboto-Regular_1.ttf");
		holder.imViewArrow.setVisibility(View.GONE);
		holder.imViewContactImage.setVisibility(View.VISIBLE);
		if (isMenuOption) {
			holder.tvContactName.setText(listMenu.get(position));

			if(MainBaseActivity.objTileDetailBeanStatic!=null && !MainBaseActivity.objTileDetailBeanStatic.Is_Mobile && listMenu.get(position).equalsIgnoreCase("Request from Contact"))
				holder.tvContactName.setTextColor(Color.parseColor("#9c9c9c"));
			else{
				holder.tvContactName.setTextColor(Color.parseColor("#5d5c5c"));
			}
			holder.imViewTag.setVisibility(View.GONE);
			holder.imViewContactImage.setVisibility(View.GONE);
			holder.imViewArrow.setVisibility(View.VISIBLE);
			holder.llImageHolderparent.setVisibility(View.GONE);
			holder.llContactNameHolder.setVisibility(View.GONE);
		} else {
			if (!isMenuOption
					&& (listContactsShow == null || listContactsShow.isEmpty())
					&& (listMenu != null && !listMenu.isEmpty())) {
				holder.tvContactName.setText(listMenu.get(position));
				holder.imViewTag.setVisibility(View.GONE);
				holder.imViewContactImage.setVisibility(View.GONE);
				holder.imViewArrow.setVisibility(View.GONE);
				holder.llImageHolderparent.setVisibility(View.GONE);
				holder.llContactNameHolder.setVisibility(View.GONE);
			} else {
				if (listContactsShow != null) {
					holder.tvContactName.setText(listContactsShow.get(position)
							.get_name());
					holder.imViewTag.setVisibility(View.GONE);
					holder.imViewContactImage.setVisibility(View.VISIBLE);
					holder.llImageHolderparent.setVisibility(View.VISIBLE);
					holder.llContactNameHolder.setVisibility(View.VISIBLE);
					holder.imViewArrow.setVisibility(View.GONE);
					if (listContactsShow.get(position).get_name()
							.contains("No matching record found")
							|| listContactsShow.get(position).get_name()
							.contains("No Contact Found")) {
						holder.imViewTag.setVisibility(View.GONE);
						holder.imViewContactImage.setVisibility(View.GONE);
						holder.imViewArrow.setVisibility(View.GONE);
						holder.llContactNameHolder.setVisibility(View.GONE);
						holder.llImageHolderparent.setVisibility(View.GONE);
					}
					if(saveState.isRegistered(mContext))
					{
						try {
							contactImageLoopTag: for (int i = 0; i < listContactsShow.get(position).get_phone().size(); i++) {
								BBID = 0;
								listBBContactsMatch = new ArrayList<BBContactsBean>();

								String phoneNumberArray = GlobalConfig_Methods.getBBNumberToCheck(mContext, listContactsShow.get(position).get_phone().get(i));
								String countryCode = "",mobileNumber="";
								countryCode = phoneNumberArray.split(",")[0];
								mobileNumber = phoneNumberArray.split(",")[1];
								if(countryCode!=null && !countryCode.trim().equals("") && mobileNumber!=null && !mobileNumber.trim().equals("")){
									matching_user_id = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,mobileNumber,countryCode);
									listBBContactsMatch = DBQuery.checkBBContactExistence(mContext,matching_user_id);
									if (listBBContactsMatch.size() > 0) {
										isExist = true;
										BBID = listBBContactsMatch.get(0).getBBID();
										break contactImageLoopTag;
									}
								}
								/*matching_user_id = DBQuery.getBBIDFromPhoneNumber(mContext,GlobalConfig_Methods
										.trimSpecialCharactersFromString(listContactsShow.get(position).get_phone().get(i)));// Integer.parseInt(listContactsShow.get(position).get_id());
								//								GlobalConfig_Methods.trimSpecialCharactersFromString(listContactsShow.get(position).get_phone().get(i));
								listBBContactsMatch = DBQuery.checkBBContactExistence(mContext,matching_user_id);
								if (listBBContactsMatch.size() > 0) {
									isExist = true;
									BBID = listBBContactsMatch.get(0).getBBID();
									break contactImageLoopTag;
								}*/
							}

						/*if(listContactsShow.get(position).get_name().equalsIgnoreCase("Ayush Verma"))
						{
							//system.out.println("success");
						}*/

						if (isExist
								&& BBID != 0
								&& listBBContactsMatch.get(0).getImage() != null
								&& !listBBContactsMatch.get(0).getImage()
								.trim().equals("")
								&& !listBBContactsMatch.get(0).getImage()
								.trim().equalsIgnoreCase("NULL")) {
							imageLoader = new ImageLoadTask(mContext,
									listBBContactsMatch.get(0).getImage(),
									holder.imViewContactImage, 320);
							imageLoader.execute();
						}

						else if(listContactsShow.get(position).getImageUri()!=null && !listContactsShow.get(position).getImageUri().trim().equals("")){
							try {
								holder.imViewContactImage.setImageBitmap(MediaStore.Images.Media.getBitmap(cr, Uri.parse(listContactsShow.get(position).getImageUri())));
							} catch (Exception e) {
								e.getMessage();
							}
						}else{
							holder.imViewContactImage.setImageResource(R.drawable.no_image);
						}

						/*else if (listContactsShow.get(position).get_imgpeople() != null) {
							holder.imViewContactImage
							.setImageBitmap(listContactsShow.get(
									position).get_imgpeople());
						} else {

							holder.imViewContactImage
							.setImageResource(R.drawable.no_image);
						}*/
						} catch (Exception e) {
							e.getMessage();
						}
					}
					else{
						try {

							if(listContactsShow.get(position).getImageUri()!=null && !listContactsShow.get(position).getImageUri().trim().equals("")){
								try {
									holder.imViewContactImage.setImageBitmap(MediaStore.Images.Media.getBitmap(cr, Uri.parse(listContactsShow.get(position).getImageUri())));
								} catch (Exception e) {
									e.getMessage();
								}
							}else{
								holder.imViewContactImage.setImageResource(R.drawable.no_image);
							}

							/*if (listContactsShow.get(position).get_imgpeople() != null) {
								holder.imViewContactImage
								.setImageBitmap(listContactsShow.get(
										position).get_imgpeople());
							} else {

								holder.imViewContactImage
								.setImageResource(R.drawable.no_image);
							}*/			
						} catch (Exception e) {
							e.getMessage();
						}
					}

					if(saveState.isRegistered(mContext));
					{
						if (!GlobalCommonValues.listBBContacts.isEmpty()) {
							if (listContactsShow != null) {
								tagloop: for (int j = 0; j < listContactsShow.get(position).get_phone().size(); j++) {
									String strPhone = listContactsShow.get(position).get_phone().get(j);
									String contactNumber = "";
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
										try {
											holder.llContactNameHolder.setBackground(mContext.getResources().getDrawable(R.drawable.notificationimageborder));					
										} catch (NoSuchMethodError e) {
											e.getMessage();
											break tagloop;
										}		
										break tagloop;
									}
									else{
										try {
											holder.llContactNameHolder.setBackground(mContext.getResources().getDrawable(R.drawable.messagelist_border_grey));					
										} catch (NoSuchMethodError e) {
											e.getMessage();
											break tagloop;
										}	
									}

								}
							}
						}
					}
				}
			}
		}
		return convertView;
	}

	/**
	 * @param instance
	 *            of text to be filtered in list filters the contents of the
	 *            list as per input given in the searchview
	 */
	public void filterData(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		if (listContactsShow != null && !listContactsShow.isEmpty()) {
			listContactsShow.clear();
			if (charText.trim().length() == 0) {
				listContactsShow.addAll(listContactsFiltered);
			} else {
				listFirstName = new ArrayList<ContactDetailsBean>();
				listLastName = new ArrayList<ContactDetailsBean>();
				listContainsName = new ArrayList<ContactDetailsBean>();
				Name name;
				for (ContactDetailsBean contactDetailBean : listContactsFiltered) {
					name = checkName(
							contactDetailBean.get_name().toLowerCase(
									Locale.getDefault()), charText);
					if (name != null) {
						if (name.isFirst) {
							listFirstName.add(contactDetailBean);
						} else if (name.isLast) {
							listLastName.add(contactDetailBean);
						} else if (name.isContainer) {
							listContainsName.add(contactDetailBean);
						}
					}
				}
				showList();
			}
		}
	}

	/*
	 * Check Existence of Name
	 */

	private Name checkName(String contactName, String searchString) {
		String nameArray[] = contactName.split(" ");
		if (nameArray != null && nameArray.length > 0) {
			Name name;
			int nameLength = nameArray.length;
			if (nameArray[0].startsWith(searchString)) {
				name = new Name();
				name.isFirst = true;
				return name;
			} else if (nameArray[nameLength - 1].startsWith(searchString)) {
				name = new Name();
				name.isLast = true;
				return name;
			} else if (contactName.contains(searchString)) {
				name = new Name();
				name.isContainer = true;
				return name;
			}

		}
		return null;
	}

	// display Contact List on filtering
	private void showList() {
		listContactsShow.clear();

		if (listFirstName != null && listFirstName.size() > 0) {
			listContactsShow.addAll(listFirstName);
		}

		if (listLastName != null && listLastName.size() > 0) {
			listContactsShow.addAll(listLastName);
		}

		if (listContainsName != null && listContainsName.size() > 0) {
			listContactsShow.addAll(listContainsName);
		}
		this.notifyDataSetInvalidated();
	}
}