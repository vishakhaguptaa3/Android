package com.tnc.contact;

import java.io.InputStream;
import java.util.ArrayList;

import com.tnc.bean.ContactDetailsBean;
import com.tnc.utility.Logs;
import com.tnc.utility.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.widget.ImageView;


/**
 * UTILITY CLASS TO FETCH ALL THE CONTACTS FROM THE DATABASE
 *  @author a3logics
 */


public class GetContactDetails
{
	ArrayList<ContactDetailsBean> contacts;
	ImageView imgview;
	Cursor cursor;
	String listType="";
	Context mContext;
	String SORT_ORDER;

	public GetContactDetails()
	{
	}
	public GetContactDetails(String listType,Context cxt) 
	{
		this.listType=listType;
		mContext=cxt;
	}
	public ArrayList<ContactDetailsBean> fetchContacts(ContentResolver cr,Context cxt)
	{
		if (cr == null || cxt == null) 
		{
			return new ArrayList<ContactDetailsBean>();
		}

		mContext=cxt;
		ArrayList<String> phoneNumber = null;
		String email = null;
		String Orgination = null;
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		//		String time = ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		Uri contacturi = ContactsContract.Data.CONTENT_URI;
		String orgnization = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
		/*
		 * Uri PhoneCONTENT_URIs =
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI; String
		 * EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		 * String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		 */

		/*
		 * String PhotoCONTACT_ID =
		 * ContactsContract.CommonDataKinds.Photo.PHOTO_ID; Uri contactPhotoUri
		 * = ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI,
		 * Long.parseLong(PhotoCONTACT_ID));
		 */
		StringBuffer output = new StringBuffer();
		// ContentResolver contentResolver = getContentResolver();
		try {
			SORT_ORDER = ContactsContract.Contacts.DISPLAY_NAME
					+ " COLLATE LOCALIZED ASC";//String.valueOf(Settings.System.getInt(mContext.getContentResolver (),"android.contacts.SORT_ORDER"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listType.startsWith("favorites"))
		{
			String SELECTION =(Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) + "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1" + " AND " + Contacts.STARRED + "=1";
			cursor = cr.query(CONTENT_URI, null,SELECTION, null, SORT_ORDER);
		}
		else
		{
			cursor = cr.query(CONTENT_URI, null,null, null, SORT_ORDER);
		}

		/*
		 *  Loop for every contact in the phone
		 */
		if (cursor.getCount() > 0) 
		{ 
			contacts = new ArrayList<ContactDetailsBean>();
			while (cursor.moveToNext()) 
			{
				email="";
				ContactDetailsBean contactDetails = new ContactDetailsBean();
				Bitmap bitmap = null;
				String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

				if (name != null && !name.isEmpty() && !name.contains("@") && hasPhoneNumber>0){
					contactDetails.set_id(contact_id);
					contactDetails.set_name(name);
					//Get Contact images
					Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contact_id));
					InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactPhotoUri);
					if (inputStream != null){
						bitmap = BitmapFactory.decodeStream(inputStream);
						contactDetails.set_imgpeople(bitmap);
					} 
					else{
						if(Logs.isShow)
							Logs.writeLog("GetContactDetails", "fetchContacts",contact_id + ": No Contact photo available");
					}

					if (hasPhoneNumber > 0){
						output.append("\n First Name:" + name);
						
						// Query and loop for every phone number of the contact
						Cursor phoneCursor = cr.query(PhoneCONTENT_URI, null,Phone_CONTACT_ID + " = ?",new String[] { contact_id }, null);
						phoneNumber=new ArrayList<String>();
						//						int count=0;
						ArrayList<String> mListType = new ArrayList<String>();
						while (phoneCursor.moveToNext()) 
						{
							phoneNumber.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
							output.append("\n Phone number:" + phoneNumber);
							
							int type = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));  
							
							String sType = "";
						    switch (type) {
						    case Phone.TYPE_HOME:
						        sType = "Home";
						        break;
						    case Phone.TYPE_MOBILE:
						        sType = "Mobile";
						        break;
						    case Phone.TYPE_WORK:
						        sType = "Work";
						        break;
						    case Phone.TYPE_FAX_HOME:
						        sType = "Home Fax";
						        break;
						    case Phone.TYPE_FAX_WORK:
						        sType = "Work Fax";
						        break;
						    case Phone.TYPE_MAIN:
						        sType = "Main";
						        break;
						    case Phone.TYPE_OTHER:
						        sType = "Other";
						        break;
						    case Phone.TYPE_CUSTOM:
						        sType = "Custom";
						        break;
						    case Phone.TYPE_PAGER:
						        sType = "Pager";
						        break;
						    case Phone.TYPE_ASSISTANT:
						        sType = "Assistant";
						        break;
						    case Phone.TYPE_CALLBACK:
						        sType = "Callback";
						        break;
						    case Phone.TYPE_CAR:
						        sType = "Car";
						        break;
						    case Phone.TYPE_COMPANY_MAIN:
						        sType = "Company Main";
						        break;
						    case Phone.TYPE_ISDN:
						        sType = "ISDN";
						        break;
						    case Phone.TYPE_MMS:
						        sType = "MMS";
						        break;
						    case Phone.TYPE_OTHER_FAX:
						        sType = "Other Fax";
						        break;
						    case Phone.TYPE_RADIO:
						        sType = "Radio";
						        break;
						    case Phone.TYPE_TELEX:
						        sType = "Telex";
						        break;
						    case Phone.TYPE_TTY_TDD:
						        sType = "TTY TDD";
						        break;
						    case Phone.TYPE_WORK_MOBILE:
						        sType = "Work Mobile";
						        break;
						    case Phone.TYPE_WORK_PAGER:
						        sType = "Work Pager";
						        break;
						    }
						    mListType.add(sType);
//						    contactDetails.set_type(sType);
							/*if(count==0)
							{
								phoneNumber.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
								count++;
							}
							for(int z=0;z<phoneCursor.getCount();z++)
							{
								if(!phoneNumber.get(z).equals(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER))))
								{
									phoneNumber.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
									output.append("\n Phone number:" + phoneNumber);
								}
							}*/
						}
						
						contactDetails.setsType(mListType);
						contactDetails.set_phone(phoneNumber);
						String orgWhere = ContactsContract.Data.CONTACT_ID
								+ " = ? AND " + ContactsContract.Data.MIMETYPE
								+ " = ?";
						String[] orgWhereParams = new String[] { contact_id,orgnization };

						Cursor orgCur = cr.query(contacturi, null, orgWhere,orgWhereParams, null);
						if (orgCur.moveToFirst()) 
						{
							Orgination = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
							//						String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
						}
						contactDetails.setOrgination(Orgination);
						orgCur.close();

						/*// Query and loop for every email of the contact
						Cursor emailCursor = cr.query(EmailCONTENT_URI, null,
								EmailCONTACT_ID + " = ?",
								new String[] { contact_id }, null);

						while (emailCursor.moveToNext())
						{
							email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
							output.append("\nEmail:" + email);
						}
						contactDetails.set_emailid(email);
						emailCursor.close();*/

						phoneCursor.close();
					}
					output.append("\n");

					// Query and loop for every email of the contact
					Cursor emailCursor = cr.query(EmailCONTENT_URI, null,
							EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (emailCursor.moveToNext())
					{
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						output.append("\nEmail:" + email);
					}
					contactDetails.set_emailid(email);
					emailCursor.close();

					contacts.add(contactDetails);
					//				else 
					//				{
					//					contactDetails.set_name("Unknown");
					//				}

					/*String orgWhere = ContactsContract.Data.CONTACT_ID
							+ " = ? AND " + ContactsContract.Data.MIMETYPE
							+ " = ?";
					String[] orgWhereParams = new String[] { contact_id,
							orgnization };

					Cursor orgCur = cr.query(contacturi, null, orgWhere,orgWhereParams, null);
					if (orgCur.moveToFirst()) 
					{
						Orgination = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
						//						String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
					}
					contactDetails.setOrgination(Orgination);
					orgCur.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = cr.query(EmailCONTENT_URI, null,
							EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (emailCursor.moveToNext())
					{
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						output.append("\nEmail:" + email);
					}
					contactDetails.set_emailid(email);
					emailCursor.close();*/
				}
				// removed contactlist.add(onjContactDetailsBean)  from here & out.append();
			}
			/*
			 * insert fetched contacts into the database
			 */
			//DBQuery.insertAllUserCallLogFromPhone(cxt, contacts);
		}
		return contacts;
	}
}
