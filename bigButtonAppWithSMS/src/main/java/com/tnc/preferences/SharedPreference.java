package com.tnc.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.tnc.common.GlobalCommonValues;

public class SharedPreference
{
	/**
	 * Shared Preferences Class to save/hold the values
	 */
	public static final String APPLICATION_SHARED_PREFERENCE = "BIGBUTTON_PREFERENCE";
	String IS_FIRSTTIME = "is_firsttime";
	public static boolean isFirstTime=true;

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setFirstTime(Context context, boolean isFirstTime)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FIRSTTIME, isFirstTime);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isFirst(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FIRSTTIME,true);
	}

	String IS_FIRSTTILE="is_firsttile";

	public void setFirstTile(Context context, boolean isFirstTile)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FIRSTTILE, isFirstTile);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isFirstTile(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FIRSTTILE,true);
	}

	String PRIVATE_KEY = "private_key";

	public void setPrivateKey(Context context, String private_key)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PRIVATE_KEY, private_key);
		editor.commit();
	}

	public String getPrivateKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PRIVATE_KEY, "");
	}

	String PUBLIC_KEY = "public_key";

	public void setPublicKey(Context context, String public_key)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PUBLIC_KEY, public_key);
		editor.commit();
	}

	public String getPublicKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PUBLIC_KEY, "");
	}

	String BACKUP_KEY = "backupkey";

	public void setBackupKey(Context context, String backupkey)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(BACKUP_KEY, backupkey);
		editor.commit();
	}

	public String getBackupKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(BACKUP_KEY, "");
	}


	String DEVICE_ID = "device_id";

	public void setDeviceId(Context context, String device_id)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(DEVICE_ID, device_id);
		editor.commit();
	}

	public String getDeviceId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(DEVICE_ID, "");
	}

	String IS_REGISTERED = "is_registered";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setRegistered(Context context, boolean is_registered)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_REGISTERED, is_registered);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isRegistered(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_REGISTERED,false);
	}

	String IS_CHANGED = "is_changed";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setChanged(Context context, boolean is_changed)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_CHANGED, is_changed);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isChanged(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_CHANGED,false);
	}

	String IS_UPOLADCONTACTSREQUESTED = "is_uploadcontactsrequested";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void set_IS_UPOLADCONTACTSREQUESTED(Context context, boolean is_uploadcontactsrequested)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_UPOLADCONTACTSREQUESTED, is_uploadcontactsrequested);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean IS_UPOLADCONTACTSREQUESTED(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_UPOLADCONTACTSREQUESTED,true);
	}

	String AUTOBACKUP_TILES_DATE = "auto_backup_tiles_date";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setAUTOBACKUP_TILES_DATE(Context context, String auto_backup_tiles_date)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(AUTOBACKUP_TILES_DATE, auto_backup_tiles_date);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getAUTOBACKUP_TILES_DATE(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(AUTOBACKUP_TILES_DATE,"");
	}

	String IS_SERVICEON = "is_service_on";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_SERVICEON(Context context, boolean is_service_on)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_SERVICEON, is_service_on);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS_SERVICEON(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_SERVICEON,true);
	}

	String IS_INSERT_MESSAGE_SERVICEON = "is_insert_message_service_on";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_INSERT_MESSAGE_SERVICEON(Context context, boolean is_insert_message_service_on)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_INSERT_MESSAGE_SERVICEON, is_insert_message_service_on);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS__INSERT_MESSAGE_SERVICEON(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_INSERT_MESSAGE_SERVICEON,false);
	}

	String IS_UploadDatabseRequested = "is_uploaddatabserequested";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_UploadDatabseRequested(Context context, boolean is_service_on)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_UploadDatabseRequested, is_service_on);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS_UploadDatabseRequested(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_UploadDatabseRequested,false);
	}

	String BBID = "bbid";
	public void setBBID(Context context, String bbid)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(BBID, bbid);
		editor.commit();
	}

	public String getBBID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(BBID, "");
	}


	String BBID_USER = "bbid_user";
	public void setBBID_User(Context context, String bbid_user)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(BBID_USER, bbid_user);
		editor.commit();
	}

	public String getBBID_User(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(BBID_USER, "");
	}

	String IS_DEFAULT_IMAGE = "is_default_image";

	public void setDisplayISDEFAULTIMAGEString(Context context, String is_default_image)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(IS_DEFAULT_IMAGE, is_default_image);
		editor.commit();
	}

	public String getDisplayDEFAULTIMAGEString(Context context) {
		SharedPreferences sp = null;
		try {
			sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		} catch (Exception e) {
			e.getMessage();
		}
		return sp.getString(IS_DEFAULT_IMAGE, "");
	}

	String PASSCODE = "passcode";
	public void setPassCode(Context context, String passcode)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PASSCODE, passcode);
		editor.commit();
	}

	public String getPassCode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PASSCODE, "");
	}

	String GCM_REGISTRATION_ID = "gcm_reg_id";

	public void setGCMRegistrationId(Context context, String gcmRegistrationID)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(GCM_REGISTRATION_ID, gcmRegistrationID);
		editor.commit();
	}

	public String getGCMRegistrationId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(GCM_REGISTRATION_ID, "");
	}

	String IS_RESUMED = "is_resumed";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setResumed(Context context, boolean is_resumed)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_RESUMED, is_resumed);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isResumed(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_RESUMED,false);
	}

	String IS_TILECREATED = "is_tilecreated";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setTileCreated(Context context, boolean is_tilecreated)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_TILECREATED, is_tilecreated);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isTileCreated(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_TILECREATED,false);
	}

	String COUNTRYCODE = "countrycode";
	public void setCountryCode(Context context, String countrycode)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(COUNTRYCODE, countrycode);
		editor.commit();
	}

	public String getCountryCode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(COUNTRYCODE, "");
	}

	String COUNTRYIDD = "countryidd";
	public void setCountryidd(Context context, String countryidd)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(COUNTRYIDD, countryidd);
		editor.commit();
	}

	public String getCountryIDD(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(COUNTRYIDD, "");
	}

	String COUNTRYNAME = "countryname";
	public void setCountryname(Context context, String countryname)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(COUNTRYNAME, countryname);
		editor.commit();
	}

	public String getCountryName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(COUNTRYNAME, "");
	}

	String COUNTRYEMERGENCY = "country_emergency";
	public void setEmergency(Context context, String country_emergency)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(COUNTRYEMERGENCY, country_emergency);
		editor.commit();
	}

	public String getEmergency(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(COUNTRYEMERGENCY, "");
	}

	String USERNAME = "user_name";
	public void setUserName(Context context, String user_name)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USERNAME, user_name);
		editor.commit();
	}

	public String getUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(USERNAME, "");
	}

	String USERMAILID = "user_mail_id";
	public void setUserMailID(Context context, String user_mail_id)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USERMAILID, user_mail_id);
		editor.commit();
	}

	public String getUserMailID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(USERMAILID, "");
	}

	String USERPHONENUMBER = "user_phone_number";
	public void setUserPhoneNumber(Context context, String user_phone_number)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USERPHONENUMBER, user_phone_number);
		editor.commit();
	}

	public String getUserPhoneNumber(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(USERPHONENUMBER, "");
	}

	String IS_VERIFIED = "is_verified";
	public void setIsVerified(Context context, boolean is_verified)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_VERIFIED, is_verified);
		editor.commit();
	}

	public boolean getIsVerified(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_VERIFIED, false);
	}

	String IS_DEFUALTIMAGE = "is_defaultimage";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setDefaultImage(Context context, boolean is_defaultimage)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DEFUALTIMAGE, is_defaultimage);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isDefaultImage(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DEFUALTIMAGE,false);
	}


	String IS_UPLOADBACKUPREQUESTED = "is_uploadbackuprequested";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setUploadBackupRequested(Context context, boolean is_uploadbackuprequested)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_UPLOADBACKUPREQUESTED, is_uploadbackuprequested);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isUploadBackupRequested(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_UPLOADBACKUPREQUESTED,false);
	}

	String IS_NEWREGISTRATION = "is_newregistration";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setNewRegistration(Context context, boolean is_newregistration)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_NEWREGISTRATION, is_newregistration);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isNewRegistration(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_NEWREGISTRATION,false);
	}

	String LINKEDIN_URL = "linkedinurl";

	public void setLinkedinUrl(Context context, String linkedinurl)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(LINKEDIN_URL, linkedinurl);
		editor.commit();
	}

	public String getLinkedinUrl(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(LINKEDIN_URL, "");
	}

	String SAVED_TIME_MILLISECONDS ;

	public void setTimeMilliseconds(Context context, long saved_time_milliseconds)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(SAVED_TIME_MILLISECONDS,saved_time_milliseconds);
		editor.commit();
	}

	public long getTimeMilliseconds(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getLong(SAVED_TIME_MILLISECONDS,0L);
	}

	String SETTIMEPOPUPDISPLAY = "set_time_popup_display";
	public void setTimePopupDisplay(Context context, String set_time_popup_display)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SETTIMEPOPUPDISPLAY,set_time_popup_display);
		editor.commit();
	}

	public String getTimePopupDisplay(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(SETTIMEPOPUPDISPLAY,"");
	}


	String SETACCESSTOKENLINKEDIN = "set_token_linkedin";
	public void setAccessTokenLinkedin(Context context, String set_token_linkedin)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SETACCESSTOKENLINKEDIN,set_token_linkedin);
		editor.commit();
	}

	public String getAccessTokenLinkedin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(SETACCESSTOKENLINKEDIN,"");
	}

	String SETACCESSTOKENSECRETLINKEDIN = "set_token_secret_linkedin";
	public void setAccessTokenSecretLinkedin(Context context, String set_token_secret_linkedin)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SETACCESSTOKENSECRETLINKEDIN,set_token_secret_linkedin);
		editor.commit();
	}

	public String getAccessTokenSecretLinkedin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(SETACCESSTOKENSECRETLINKEDIN,"");
	}

	String LINKEDIN_POST_DATE = "linked_in_post_date";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setLINKEDIN_POST_DATE(Context context, String linked_in_post_date)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(LINKEDIN_POST_DATE, linked_in_post_date);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getLINKEDIN_POST_DATE(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(LINKEDIN_POST_DATE,"");
	}

	String IS_FromHome = "is_from_home";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_FROM_HOME(Context context, boolean is_from_home)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FromHome, is_from_home);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS_FROM_HOME(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FromHome,true);
	}


	String IS_FetchingContacts = "is_fetching_contacts";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_FetchingContacts(Context context, boolean is_fetching_contacts)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FetchingContacts, is_fetching_contacts);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS_FetchingContacts(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FetchingContacts,false);
	}



	String IS_RecentRegistration = "is_recent_registration";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_RecentRegistration(Context context, boolean is_recent_registration)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_RecentRegistration, is_recent_registration);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean IS_RecentRegistration(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_RecentRegistration,false);
	}

	String SETNUMBERLENGTH = "setnumberlength";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setNumber_Length(Context context, int setnumberlength)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(SETNUMBERLENGTH,setnumberlength);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public int getNumber_Length(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getInt(SETNUMBERLENGTH,0);
	}

	String IS_UNDERAGE = "is_underage";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_UNDERAGE(Context context, boolean is_underage)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_UNDERAGE, is_underage);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean getIS_UNDERAGE(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_UNDERAGE,false);
	}

	String PARENTEMAILID = "parentemailid";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setParentEmailId(Context context, String parentemailid)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PARENTEMAILID,parentemailid);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getParentEmailId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PARENTEMAILID,"");
	}

	String IS_PARENTAL_CONSENT_RECEIVED = "is_parental_consent_received";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_PARENTAL_CONSENT_RECEIVED(Context context, boolean is_parental_consent_received)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_PARENTAL_CONSENT_RECEIVED, is_parental_consent_received);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean getIS_PARENTAL_CONSENT_RECEIVED(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_PARENTAL_CONSENT_RECEIVED,false);
	}


	String PARENTCONSENTSTATUS = "parentconsentstatus";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setParentConsentStatus(Context context, String parentconsentstatus)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PARENTCONSENTSTATUS,parentconsentstatus);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getParentConsentStatus(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PARENTCONSENTSTATUS,"");
	}

	String IS_PARENTAL_OK_CLICKED = "is_parental_ok_clicked";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIS_PARENTAL_OK_CLICKED(Context context, boolean is_parental_ok_clicked)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_PARENTAL_OK_CLICKED, is_parental_ok_clicked);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public Boolean getIS_PARENTAL_OK_CLICKED(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_PARENTAL_OK_CLICKED,false);
	}


	String PRIVACYPOLICYVERSION = "privacypolicyversion";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setPrivacyPolicyversion(Context context, String privacypolicyversion)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PRIVACYPOLICYVERSION,privacypolicyversion);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getPrivacyPolicyversion(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(PRIVACYPOLICYVERSION,"0.0");
	}

	String TERMSOFUSEVERSION = "termsofuseversion";
	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setTermsOfUseVersion(Context context, String termsofuseversion)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(TERMSOFUSEVERSION,termsofuseversion);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return String
	 */
	public String getTermsOfUseVersion(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(TERMSOFUSEVERSION,"0.0");
	}

	String IS_FROM_SPLASH = "is_from_splash";
	public void setRefrehContactList(Context context, boolean is_from_splash)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FROM_SPLASH, is_from_splash);
		editor.commit();
	}

	public boolean getRefrehContactList(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FROM_SPLASH, false);
	}

	String IS_RETURNING_USER = "is_returning_user";
	public void setISRETURNINGUSER(Context context, boolean is_returning_user)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_RETURNING_USER, is_returning_user);
		editor.commit();
	}

	public boolean getISRETURNINGUSER(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_RETURNING_USER, false);
	}

	String IS_NUMBER_CHANGED = "is_number_changed";
	public void setIS_NUMBER_CHANGED(Context context, boolean is_number_changed)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_NUMBER_CHANGED, is_number_changed);
		editor.commit();
	}

	public boolean getIS_NUMBER_CHANGED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_NUMBER_CHANGED, false);
	}

	String UPDATE_EMERGENCY = "update_emergency";
	public void setUPDATE_EMERGENCY(Context context, boolean update_emergency)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(UPDATE_EMERGENCY, update_emergency);
		editor.commit();
	}

	public boolean getUPDATE_EMERGENCY(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(UPDATE_EMERGENCY, false);
	}

	String IS_DEACTIVATED = "is_deactivated";
	public void setIS_DEACTIVATED(Context context, boolean is_deactivated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DEACTIVATED, is_deactivated);
		editor.commit();
	}

	public boolean getIS_DEACTIVATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DEACTIVATED, false);
	}

	String IS_CLIPARTS_VERSION_UPDATED = "is_cliparts_version_updated";
	public void setIS_CLIPARTS_VERSION_UPDATED(Context context, boolean is_cliparts_version_updated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_CLIPARTS_VERSION_UPDATED, is_cliparts_version_updated);
		editor.commit();
	}

	public boolean getIS_CLIPARTS_VERSION_UPDATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_CLIPARTS_VERSION_UPDATED, false);
	}

	String IS_FAQ_VERSION_UPDATED = "is_faq_version_updated";
	public void setIS_FAQ_VERSION_UPDATED(Context context, boolean is_faq_version_updated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FAQ_VERSION_UPDATED, is_faq_version_updated);
		editor.commit();
	}

	public boolean getIS_FAQ_VERSION_UPDATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FAQ_VERSION_UPDATED, true);
	}

	String IS_HOWTO_VERSION_UPDATED = "is_howto_version_updated";
	public void setIS_HOWTO_VERSION_UPDATED(Context context, boolean is_howto_version_updated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_HOWTO_VERSION_UPDATED, is_howto_version_updated);
		editor.commit();
	}

	public boolean getIS_HOWTO_VERSION_UPDATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_HOWTO_VERSION_UPDATED, true);
	}

	String IS_DEFAULT_MESSAGES_VERSION_UPDATED = "is_default_messages_version_updated";
	public void setIS_DEFAULT_MESSAGES_VERSION_UPDATED(Context context, boolean is_default_messages_version_updated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DEFAULT_MESSAGES_VERSION_UPDATED, is_default_messages_version_updated);
		editor.commit();
	}

	public boolean getIS_DEFAULT_MESSAGES_VERSION_UPDATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DEFAULT_MESSAGES_VERSION_UPDATED, false);
	}

	String IS_EMERGENCY_NUMBER_VERSION_UPDATED = "is_emergency_number_version_updated";
	public void setIS_EMERGENCY_NUMBER_VERSION_UPDATED(Context context, boolean is_emergency_number_version_updated)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_EMERGENCY_NUMBER_VERSION_UPDATED, is_emergency_number_version_updated);
		editor.commit();
	}

	public boolean getIS_EMERGENCY_NUMBER_VERSION_UPDATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_EMERGENCY_NUMBER_VERSION_UPDATED, false);
	}

	String IS_INITIAL_CLIPARTS = "is_initial_cliparts";
	public void setIS_INITIAL_CLIPARTS(Context context,
									   boolean is_initial_cliparts)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_INITIAL_CLIPARTS, is_initial_cliparts);
		editor.commit();
	}

	public boolean getIS_INITIAL_CLIPARTS(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_INITIAL_CLIPARTS, true);
	}

	String CLIPARTS_VERSION = "cliparts_version";

	public void setCLIPARTS_VERSION(Context context, String cliparts_version)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(CLIPARTS_VERSION, cliparts_version);
		editor.commit();
	}

	public String getCLIPARTS_VERSION(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(CLIPARTS_VERSION, "1.0");
	}


	String IS_FIRST_TIME_AFTER_REGISTRATION = "is_first_time_after_registration";
	public void setIS_FIRST_TIME_AFTER_REGISTRATION(Context context,
													boolean is_first_time_after_registration)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_FIRST_TIME_AFTER_REGISTRATION, is_first_time_after_registration);
		editor.commit();
	}

	public boolean getIS_FIRST_TIME_AFTER_REGISTRATION(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FIRST_TIME_AFTER_REGISTRATION, true);
	}

	String SCREEN_RESOLUTION = "screen_resolution";

	public void setSCREEN_RESOLUTION(Context context, String screen_resolution)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SCREEN_RESOLUTION, screen_resolution);
		editor.commit();
	}

	public String getSCREEN_RESOLUTION(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(SCREEN_RESOLUTION, "");
	}

	String IS_PREMIUM_USER = "is_premium_user";
	public void setISPREMIUMUSER(Context context, boolean is_premium_user)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_PREMIUM_USER, is_premium_user);
		editor.commit();
	}

	public boolean getISPREMIUMUSER(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_PREMIUM_USER, false);
	}

	String IS_EMERGENCY_CONTACT_CREATED = "is_emergency_contact_created";
	public void setIS_EMERGENCY_CONTACT_CREATED(Context context,
												boolean is_emergency_contact_created)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_EMERGENCY_CONTACT_CREATED, is_emergency_contact_created);
		editor.commit();
	}

	public boolean getIS_EMERGENCY_CONTACT_CREATED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_EMERGENCY_CONTACT_CREATED, false);
	}

	String IS_REGISTRATION_POPUP_DISPLAYED = "is_registration_popup_displayed";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setIsRegistrationPopupdisplayed(Context context, boolean is_registration_popup_displayed)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_REGISTRATION_POPUP_DISPLAYED, is_registration_popup_displayed);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean getIsRegistrationPopupdisplayed(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_REGISTRATION_POPUP_DISPLAYED,true);
	}

	String USER_NAME_TEMP = "user_name_temp";

	public void setUSER_NAME_TEMP(Context context, String user_name_temp)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USER_NAME_TEMP, user_name_temp);
		editor.commit();
	}

	public String getUSER_NAME_TEMP(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(USER_NAME_TEMP, "");
	}

	String IS_NUMBER_DIALLED = "is_number_dialled";
	public void setIS_NUMBER_DIALLED(Context context, boolean is_number_dialled)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_NUMBER_DIALLED, is_number_dialled);
		editor.commit();
	}

	public boolean getIS_NUMBER_DIALLED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_NUMBER_DIALLED, false);
	}

	String IS_ELDERLY_MODE = "is_elderly_mode";
	public void setIsElderlyMode(Context context, String is_elderly_mode)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(IS_ELDERLY_MODE, is_elderly_mode);
		editor.commit();
	}

	public String getIsElderlyMode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(IS_ELDERLY_MODE, "");
	}

	String IS_DISABLE_LONG_TAP = "is_disable_long_tap";
	public void setIsDisableLongTap(Context context, boolean is_disable_long_tap)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DISABLE_LONG_TAP, is_disable_long_tap);
		editor.commit();
	}

	public boolean getIsDisableLongTap(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DISABLE_LONG_TAP, false);
	}

	String IS_AUTO_SPEAKER_MODE = "is_auto_speaker_mode";

	public void setIsAutoSpeakerMode(Context context, boolean is_auto_speaker_mode)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_AUTO_SPEAKER_MODE, is_auto_speaker_mode);
		editor.commit();
	}

	public boolean getIsAutoSpeakerMode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_AUTO_SPEAKER_MODE, false);
	}

	String IS_EMERGENCY_CONTACT_NOTIFICATION = "is_emergency_contact_notification";
	public void setIsEmergencyContactNotification(Context context, boolean is_emergency_contact_notification)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_EMERGENCY_CONTACT_NOTIFICATION, is_emergency_contact_notification);
		editor.commit();
	}

	public boolean getIsEmergencyContactNotification(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_EMERGENCY_CONTACT_NOTIFICATION, false);
	}

	String IS_EMERGENCY_NUMBER_DIALLED = "is_emergency_number_dialled";
	public void setIS_EMERGENCY_NUMBER_DIALLED(Context context, boolean is_emergency_number_dialled)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_EMERGENCY_NUMBER_DIALLED, is_emergency_number_dialled);
		editor.commit();
	}

	public boolean getIS_EMERGENCY_NUMBER_DIALLED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_EMERGENCY_NUMBER_DIALLED, false);
	}

	String SELECTED_CATEGORY = "selected_category";

	public void setSELECTED_CATEGORY(Context context, String selected_category)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SELECTED_CATEGORY, selected_category);
		editor.commit();
	}

	public String getSELECTED_CATEGORY(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(SELECTED_CATEGORY, "");
	}

	String IS_DIALER_INTERFACE_ENABLED = "is_dialer_interface_enabled";
	public void setDIALER_INTERFACE_ENABLED(Context context, boolean is_dialer_interface_enabled)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DIALER_INTERFACE_ENABLED, is_dialer_interface_enabled);
		editor.commit();
	}

	public boolean getIS_DIALER_INTERFACE_ENABLED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DIALER_INTERFACE_ENABLED, false);
	}

	String IS_VOICE_MAIL_ENABLED = "is_voice_mail_enabled";
	public void setVOICE_MAIL_ENABLED(Context context, boolean is_voice_mail_enabled){
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_VOICE_MAIL_ENABLED, is_voice_mail_enabled);
		editor.commit();
	}

	public boolean getIS_VOICE_MAIL_ENABLED(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_VOICE_MAIL_ENABLED, false);
	}

	String BUTTON_SHAPE = "button_shape";

	public void setBUTTON_SHAPE(Context context, String button_shape)
	{
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(BUTTON_SHAPE, button_shape);
		editor.commit();
	}

	public String getBUTTON_SHAPE(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(BUTTON_SHAPE, GlobalCommonValues.BUTTON_SHAPE_SQUARE);
	}

	String IS_IMAGE_CHANGE = "is_image_change";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setImageChange(Context context, boolean isImagechange)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_IMAGE_CHANGE, isImagechange);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isImageChange(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_IMAGE_CHANGE,false);
	}

	String ALARM_TIME = "alarm_time";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setAlarmTime(Context context, String alarmTime)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(ALARM_TIME, alarmTime);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public String getAlarmTime(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(ALARM_TIME,"");
	}

	String RESPONSE_TIMER_VALUE = "response_timer_value";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setResponseTimerValue(Context context, String alarmTime)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(RESPONSE_TIMER_VALUE, alarmTime);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public String getResponseTimerValue(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getString(RESPONSE_TIMER_VALUE,"");
	}

	String IS_DIALOG_DISPLAY = "is_dialog_display";

	/**
	 *
	 * @param context,boolean value
	 * @return
	 */
	public void setDialogDisplay(Context context, boolean isImagechange)
	{
		SharedPreferences sp =context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_DIALOG_DISPLAY, isImagechange);
		editor.commit();
	}
	/**
	 *
	 * @param context
	 * @return boolean
	 */
	public boolean isDialogDisplay(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return sp.getBoolean(IS_DIALOG_DISPLAY,false);
	}

}