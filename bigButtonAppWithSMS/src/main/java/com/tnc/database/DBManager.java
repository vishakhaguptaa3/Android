package com.tnc.database;

import java.util.ArrayList;
import com.tnc.MainBaseActivity;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.ClipArtBean;
import com.tnc.bean.ContactDetailsUserBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.DefaultMessagesBeanDB;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.bean.NotificationReponseDataBean;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.preferences.SharedPreference;
import com.tnc.webresponse.GetMessageResponseDataBean;
import com.tnc.webresponse.HowtoReponseDataBean;
import com.tnc.webresponse.SendMessageReponseDataBean;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

/**
 * Helper class to perform CRUD operations in the database
 *
 * @author a3logics
 */

public class DBManager {

    public Context mContext;
    private static SQLiteDatabase sqliteDatabaseCheckExistence;
    public static SQLiteDatabase sqliteDatabase;

    private DatabaseHelper databasehelper;
    private DatabaseHelper databasehelperCheckExistence;
    // private boolean isTileMerged=false;

    public DBManager(Context ctx) {
        this.mContext = ctx;
        databasehelper = new DatabaseHelper(mContext);
    }

    public DBManager(Context ctx, String dbname) {
        this.mContext = ctx;
        databasehelperCheckExistence = new DatabaseHelper(mContext, dbname);
    }

    public DBManager open() throws SQLException {
        sqliteDatabase = databasehelper.getWritableDatabase();
        return this;
    }

    public DBManager openForCheckingExistence() throws SQLException {
        sqliteDatabaseCheckExistence = databasehelperCheckExistence.getWritableDatabase();
        return this;
    }

    public void close() {
        databasehelper.close();
    }

    public void closeForCheckExistence() {
        databasehelperCheckExistence.close();
    }

    PackageManager packageManager;
    private static final String DATABASE_NAME = CopyDBFromAssets.DATABASE_NAME;
    private static final int DATABASE_VERSION = 5;
    private final String BIG_BUTTON_BBCONTACTS_TABLE = "BBContacts";
    // private final String BIG_BUTTON_CONFIG_TABLE = "Config";
    // private final String BIG_BUTTON_MYBACKUP_TABLE = "MyBackup";
    private final String BIG_BUTTON_CONFIGURED_MESSAGES_TABLE = "ConfiguredMessages";
    // private final String BIG_BUTTON_MESSAGES="ConfiguredMessages";
    // private final String BIG_BUTTON_NOTIFICATIONS="Notifications";
    private final String BIG_BUTTON_TILES_TABLE = "Tiles";
    private final String BIG_BUTTON_USER_TABLE = "User";
    private final String BIG_BUTTON_NOTIFICATIONS_TABLE = "Notifications";
    private final String BIG_BUTTON_MESSAGES_TABLE = "Messages";
    private final String BIG_BUTTON_COUNTRY_CODES_TABLE = "CountryCodes";
    private final String BIG_BUTTON_HOW_TO_TABLE = "HowTo";
    private final String BIG_BUTTON_FAQ_TABLE = "Faq";
    private final String BIG_BUTTON_EMERGENCYNUMBERS_TABLE = "EmergencyNumbers";
    private final String BIG_BUTTON_CLIPARTS_TABLE = "Cliparts";
    private final String BIG_BUTTON_CATEGORY_TABLE = "Category";
    private final String BIG_BUTTON_CALL_DETAILS_TABLE = "CallDetails";


    // private final String BIG_BUTTON_USER_NAME = "Name";
    // private final String BIG_BUTTON_USER_PHNUMBER = "PhoneNumber";

	 /*
      * for bigbutton registered user column name
	  */

    // For User Table
    private final String BIG_BUTTON_REGISTERED_USER_BBID = "BBID";
    private final String BIG_BUTTON_REGISTERED_USER_NAME = "Name";
    private final String BIG_BUTTON_REGISTERED_USER_EMAIL = "Email";
    private final String BIG_BUTTON_REGISTERED_USER_NUMBER = "PhoneNumber";
    private final String BIG_BUTTON_REGISTERED_USER_IMAGE = "Image";
    private final String BIG_BUTTON_REGISTERED_USER_IS_DEAFULT_IMAGE = "IsDefaultImage";

    // For Tiles Table
    private final String BIG_BUTTON_TILE_NAME = "Name";
    private final String BIG_BUTTON_TILE_NUMBER = "PhoneNumber";
    private final String BIG_BUTTON_TILE_IMAGE = "Image";
    private final String BIG_BUTTON_TILE_EMERGENCY = "IsEmergency";
    private final String BIG_BUTTON_TILE_POSITION = "TilePosition";
    private final String BIG_BUTTON_TILE_IS_IMAGE_PENDING = "IsImagePending";
    private final String BIG_BUTTON_TILE_PREFIX = "Prefix";
    private final String BIG_BUTTON_TILE_COUNTRY_CODE = "CountryCode";
    private final String BIG_BUTTON_TILE_IS_TNC_USER = "IsTncUser";
    private final String BIG_BUTTON_TILE_iS_TNC_USER = "isTncUser";
    private final String BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE = "ButtonType";
    private final String BIG_BUTTON_TILE_BBID = "BBID";
    public final String BIG_BUTTON_TILE__ISMOBILE = "IsMobile";

    private final String BIG_BUTTON_TILE_ISMOBILE = "IsMobile";

    // private final String BIG_BUTTON_TILE_TYPE = "Type";

    // For BBContacts Table
    private final String BIG_BUTTON_BBCONTACTS_MOBILEID = "MobileContactID";
    private final String BIG_BUTTON_BBCONTACTS_BBID = "BBID";
    private final String BIG_BUTTON_BBCONTACTS_NAME = "Name";
    private final String BIG_BUTTON_BBCONTACTS_PHONE = "Phone";
    private final String BIG_BUTTON_BBCONTACTS_IMAGE = "Image";
    private final String BIG_BUTTON_BBCONTACTS_COUNTRYCODE = "countryCode";
    // For Config Details Table
    // private final String BIG_BUTTON_CONFIG_KEY = "Key";
    // private final String BIG_BUTTON_CONFIG_VALUE = "Value";

    // For ConfiguredMessages Table
    private final String BIG_BUTTON_CONFIGURED_MESSAGE_ID = "id";
    private final String BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE = "message";
    private final String BIG_BUTTON_CONFIGURED_MESSAGE_TYPE = "type";
    private final String BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED = "isLocked";

    // For Messages Table
    private final String BIG_BUTTON__MESSAGE_ID = "id";
    private final String BIG_BUTTON__MESSAGE_FROM_USER_ID = "from_user_id";
    private final String BIG_BUTTON__MESSAGE_FROM_PHONENUMBER = "from_user_phone";
    private final String BIG_BUTTON__MESSAGE_TO_USER_ID = "to_user_id";
    private final String BIG_BUTTON__MESSAGE_MESSAGE = "message";
    private final String BIG_BUTTON__MESSAGE_STATUS = "status";
    private final String BIG_BUTTON__MESSAGE_DATETIME = "datetime";
    private final String BIG_BUTTON__MESSAGE_NAME = "Name";
    // private final String BIG_BUTTON__MESSAGE_UNREAD_COUNT="unread_count";

    // For Notifications Table
    private final String BIG_BUTTON__NOTIFICATION_ID = "id";
    private final String BIG_BUTTON__NOTIFICATION_FROM_USER_ID = "from_user_id";
    private final String BIG_BUTTON__NOTIFICATION_TO_USER_ID = "to_user_id";
    private final String BIG_BUTTON_NOTIFICATION_TYPE = "type";
    private final String BIG_BUTTON_NOTIFICATION_MESSAGE = "message";
    private final String BIG_BUTTON_NOTIFICATION_NAME = "Name";
    private final String BIG_BUTTON_NOTIFICATION_FROM_PHONE_NUMBER = "FromPhoneNumber";

    // private final String BIG_BUTTON_NOTIFICATION_CREATED_ON="created_on";
    // private final String BIG_BUTTON_NOTIFICATION_MODIFIED_ON="modified_on";
    private final String BIG_BUTTON_NOTIFICATION_DATE_TIME = "datetime";
    private final String BIG_BUTTON__NOTIFICATION_STATUS = "status";
    private final String BIG_BUTTON__NOTIFICATION_IMAGE = "image";

    // For Country Codes Table
    private final String BIG_BUTTON_COUNTRY_NAME = "CountryName";
    private final String BIG_BUTTON_COUNTRY_CODE = "CountryCode";
    private final String BIG_BUTTON_COUNTRY_IDD_CODE = "IDDCode";
    private final String BIG_BUTTON_COUNTRY_EMERGENCY = "Emergency";
    private final String BIG_BUTTON_COUNTRY_FLAG = "flag";

    // For How To Table
    private final String BIG_BUTTON_HOWTO_QUESTION = "question";
    private final String BIG_BUTTON_HOWTO_ANSWER = "answer";
    private final String BIG_BUTTON_HOWTO_VERSION = "version";
    private final String BIG_BUTTON_HOWTO_ADD_DATE = "add_date";

    // For FAQ Table
    private final String BIG_BUTTON_FAQ_QUESTION = "question";
    private final String BIG_BUTTON_FAQ_ANSWER = "answer";
    private final String BIG_BUTTON_FAQ_VERSION = "version";
    private final String BIG_BUTTON_FAQ_ADD_DATE = "add_date";

    //For Cliparts Table
    private final String BIG_BUTTON_CLIPARTS_IMAGE = "image";

    //For Category Table
    private final String BIG_BUTTON_CATEGORY_ID = "CategoryID";
    private final String BIG_BUTTON_CATEGORY_NAME = "CategoryName";

    //For Call Logs Table
    private final String BIG_BUTTON_CALL_NAME           = "CallName";
    private final String BIG_BUTTON_CALL_PREFIX         = "CallingPrefix";
    private final String BIG_BUTTON_CALL_COUNTRY_CODE   = "CallingCountryCode";
    private final String BIG_BUTTON_CALL_NUMBER         = "CallingNumber";
    private final String BIG_BUTTON_CALL_TIME           = "CallTime";
    private final String BIG_BUTTON_CALL_TYPE           = "CallType";
    private final String BIG_BUTTON_CALL_STATUS         = "Status";
    private final String BIG_BUTTON_COUNT_RECORDS       = "CountRecords";
    private final String BIG_BUTTON_IS_EMERGENCY_CALL   = "IsEmergency";

    // private final String
    // BIG_BUTTON_COUNTRY_MESSAGEMULTIPLIER="MessageMultiplier";

    // -------DATABASE HELPER-------------------------------
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContextDBHelper;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContextDBHelper = context;
        }

        DatabaseHelper(Context context, String dbname) {
            super(context, dbname, null, DATABASE_VERSION);
            mContextDBHelper = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //system.out.println("");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > 1 && newVersion < 3) {
                // check if Category table exists or not
                try{
                    Cursor mCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE name ='Category' and type='table'", null);

                    if((mCursor == null) || (mCursor!=null && mCursor.getCount()<1)){
                        // Create Tile Categories Table
                        createTileCategories(db);
                    }
                }catch(Exception e){
                    e.getMessage();
                }

                try{
                    // check if CallDetails table exists or not
                    Cursor mCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE name ='CallDetails' and type='table'", null);

                    if((mCursor == null) || (mCursor!=null && mCursor.getCount()<1)){
                        // Create Call Details Table
                        createCallDetails(db);
                    }
                }catch(Exception e){
                    e.getMessage();
                }

                // Check for FromPhoneNumber Column in Database and create it, if it doesn't exists
                try{
                    Cursor mCursor = db.rawQuery("ALTER TABLE Notifications ADD COLUMN FromPhoneNumber VARCHAR;", null);
                }catch(Exception e){
                    e.getMessage();
                }

            }
            if(newVersion>=5){
                db.execSQL(DBConstant.SQL_ALTER_TILE_TABLE);
            }
        }

        // Create Tile Categories Table
        public void createTileCategories(SQLiteDatabase db){
            String query = "CREATE TABLE IF NOT EXISTS Category(CategoryID VARCHAR,CategoryName VARCHAR);";
            try {
                try{
                    db.execSQL(query);

                    ContentValues mContentValues;
                    for (int i = 0; i < 5; i++) {
                        mContentValues = new ContentValues();
                        mContentValues.put("CategoryID", String.valueOf(i));

                        if (i == 0)
                            mContentValues.put("CategoryName", "Recent Calls");
                        else if (i == 1)
                            mContentValues.put("CategoryName", "All");
                        else if (i == 2)
                            mContentValues.put("CategoryName", "Friend");
                        else if (i == 3)
                            mContentValues.put("CategoryName", "Family");
                        else if (i == 4)
                            mContentValues.put("CategoryName", "Business");

                        db.insert("Category", null, mContentValues);
                    }

                }catch(Exception e){
                    e.getMessage();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        // Create Call Details Table
        public void createCallDetails(SQLiteDatabase db){
            //1 - incoming, 2 - outgoing, 3 - missed
            String query = "CREATE TABLE IF NOT EXISTS CallDetails(CallName VARCHAR, CallingPrefix VARCHAR, " +
                    "CallingCountryCode VARCHAR, CallingNumber VARCHAR, CallTime VARCHAR, CallType INTEGER, Status INTEGER, IsEmergency BOOL);";
            try{
                db.execSQL(query);
            }catch(Exception e){
                e.getMessage();
            }
        }

    }

    public long addUser(ArrayList<ContactDetailsUserBean> userListdata) {
        if (userListdata == null)
            return 0l;
        long rowID = -1;
        return (long) rowID;
    }

    public long addBBTiles(ArrayList<ContactTilesBean> tilesListdata, boolean isBackupMode) {
        if (tilesListdata == null)
            return 0l;
        long rowID = -1;
        if (isBackupMode) {
            if (MainBaseActivity.mergeTiles) {
                for (int j = 0; j < tilesListdata.size(); j++) {
                    checkExistingTiles(tilesListdata.get(j).getPhoneNumber(), j, tilesListdata, rowID);
                }
            } else if (!MainBaseActivity.mergeTiles) {
                ContentValues initialValues = new ContentValues();
                for (int i = 0; i < tilesListdata.size(); i++) {
                    initialValues.put("Name", tilesListdata.get(i).getName());
                    initialValues.put("PhoneNumber", tilesListdata.get(i).getPhoneNumber());
                    initialValues.put("Image", tilesListdata.get(i).getImage());
                    initialValues.put("IsEmergency", tilesListdata.get(i).isIsEmergency());
                    initialValues.put("TilePosition", tilesListdata.get(i).getTilePosition());
                    initialValues.put("IsImagePending", tilesListdata.get(i).getIsImagePending());
                    initialValues.put("Prefix", tilesListdata.get(i).getPrefix());
                    initialValues.put("CountryCode", tilesListdata.get(i).getCountryCode());
                    initialValues.put("BBID", tilesListdata.get(i).getBBID());
                    initialValues.put(DBConstant.TILE_COLUMN_IMAGE_LOCK,tilesListdata.get(i).isImageLocked());
                    //system.out.println("ISMOBILE 2" + "TRUE");
                    if (tilesListdata.get(i).isIsMobile()) {
                        initialValues.put(BIG_BUTTON_TILE__ISMOBILE, 1);
                    } else {
                        initialValues.put(BIG_BUTTON_TILE__ISMOBILE, 0);
                    }
                    initialValues.put("IsTncUser", tilesListdata.get(i).isIsTncUser()); // tilesListdata.get(i).isIsTncUser()

                    //initialValues.put("IsTncUser", 0); // tilesListdata.get(i).isIsTncUser()
                    try {
                        initialValues.put("ButtonType", tilesListdata.get(i).getButtonType());
                        //initialValues.put("ButtonType", 1); // tilesListdata.get(i).getButtonType()
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    rowID = sqliteDatabase.insert(BIG_BUTTON_TILES_TABLE, null, initialValues);
                    //system.out.println("ISMOBILE 3 " + rowID);
                    addPhoneContact(rowID, initialValues);
                }
            }
        } else {
            try {
                ContentValues initialValues = new ContentValues();
                for (int i = 0; i < tilesListdata.size(); i++) {
                    initialValues.put("Name", tilesListdata.get(i).getName());
                    initialValues.put("PhoneNumber", tilesListdata.get(i).getPhoneNumber());
                    initialValues.put("Image", tilesListdata.get(i).getImage());
                    initialValues.put("IsEmergency", tilesListdata.get(i).isIsEmergency());
                    initialValues.put("TilePosition", tilesListdata.get(i).getTilePosition());
                    initialValues.put("IsImagePending", tilesListdata.get(i).getIsImagePending());
                    initialValues.put("Prefix", tilesListdata.get(i).getPrefix());
                    initialValues.put("CountryCode", tilesListdata.get(i).getCountryCode());
                    initialValues.put("IsTncUser", tilesListdata.get(i).isIsTncUser());
                    initialValues.put("BBID", tilesListdata.get(i).getBBID());
                    initialValues.put(DBConstant.TILE_COLUMN_IMAGE_LOCK,tilesListdata.get(i).isImageLocked());

                    if (tilesListdata.get(i).isIsMobile()) {
                        initialValues.put(BIG_BUTTON_TILE__ISMOBILE, 1);
                    } else {
                        initialValues.put(BIG_BUTTON_TILE__ISMOBILE, 0);
                    }
                    try {
                        initialValues.put("ButtonType", tilesListdata.get(i).getButtonType());
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    rowID = sqliteDatabase.insert(BIG_BUTTON_TILES_TABLE, null, initialValues);
                    addPhoneContact(rowID, initialValues);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return (long) rowID;
    }

    public void deleteTable(String tableName, String whereClause, String[] whereArgs) {
        try {
            sqliteDatabase.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //add Bb Contacts from the service
    public long addBBContactsFromService(Context context, ArrayList<BBContactsBean> bbContactsListdata) {
        if (bbContactsListdata == null)
            return 0l;
        long rowID = -1;
        int mUserBBIDTilesTable;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < bbContactsListdata.size(); i++) {

               /* mUserBBIDTilesTable = DBQuery.getBBIDFromPhoneNumberAndCountryCode(context,
                        bbContactsListdata.get(i).getPhoneNumber(), bbContactsListdata.get(i).getCountryCode());*/
//                if (mUserBBIDTilesTable < 1) {
                if (sqliteDatabase != null && !sqliteDatabase.isOpen()) {
                    open();
                }

                // Check if Tile for this number exists then update it's IsTncUser field to 1 i.e it a TnC User
                updateIsTnCUserForTile(bbContactsListdata.get(i).getBBID(), bbContactsListdata.get(i).getCountryCode(),
                        bbContactsListdata.get(i).getPhoneNumber());


                try{ // remove previous records if any, exists with the same number and country code
                    String WHERE = "countryCode = ? AND Phone = ?";// "countryCode = ? AND PhoneNumber=?";
                    String[] WHERE_ARGS = new String[]{bbContactsListdata.get(i).getCountryCode(), bbContactsListdata.get(i).getPhoneNumber()};
                    sqliteDatabase.delete(BIG_BUTTON_BBCONTACTS_TABLE, WHERE, WHERE_ARGS);
                }catch(Exception e){
                    e.getMessage();
                }

                initialValues.put("MobileContactID", bbContactsListdata.get(i).getMobID());
                initialValues.put("BBID", bbContactsListdata.get(i).getBBID());
                initialValues.put("Name", bbContactsListdata.get(i).getName());
                initialValues.put("Phone", bbContactsListdata.get(i).getPhoneNumber());
                initialValues.put("Image", bbContactsListdata.get(i).getImage());
                initialValues.put("countryCode", bbContactsListdata.get(i).getCountryCode());
                rowID = sqliteDatabase.insert(BIG_BUTTON_BBCONTACTS_TABLE, null, initialValues);
//                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Update tile's details if tile exists for a particular number
    public void updateIsTnCUserForTile(int BBID, String mCountryCode, String mNumber){
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM " +
                                BIG_BUTTON_TILES_TABLE +" where CountryCode=" + mCountryCode +
                                " AND PhoneNumber=" + mNumber,
                        null);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
                if(mCursor.getCount()>0){
                    // If matching record found then update the tile's IsTncUser to 1 i.e true
                    mCursor = null;
                    try{
                        //mCursor =  sqliteDatabase.rawQuery("UPDATE Tiles SET IsTncUser=1,BBID=" + BBID + " where CountryCode=" +  mCountryCode + " AND PhoneNumber=" + mNumber, null);

                        ContentValues initialValues = new ContentValues();
                        String WHERE = "CountryCode = ? AND PhoneNumber = ?";
                        String[] WHERE_ARGS = new String[]{mCountryCode, mNumber};

                        initialValues.put("IsTncUser", 1);
                        initialValues.put("BBID", BBID);

                        int mCount = sqliteDatabase.update(BIG_BUTTON_TILES_TABLE, initialValues, WHERE, WHERE_ARGS);

                        System.out.print(mCursor + "");

                    }catch(Exception e){
                        e.getMessage();
                    }

                    /*String where = "CountryCode = ? AND PhoneNumber = ?";
                    // bind VALUES here
                    String[] whereArgs = {mCountryCode, mNumber};

                    ContentValues cv = new ContentValues();
                    cv.put("IsTncUser", true);
                    cv.put("BBID", BBID);

                    int mCount = sqliteDatabase.update("Tiles", cv, where, whereArgs);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add bbContacts
    public long addBBContacts(ArrayList<BBContactsBean> bbContactsListdata) {
        if (bbContactsListdata == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < bbContactsListdata.size(); i++) {
                initialValues.put("MobileContactID", bbContactsListdata.get(i).getMobID());
                initialValues.put("BBID", bbContactsListdata.get(i).getBBID());
                initialValues.put("Name", bbContactsListdata.get(i).getName());
                initialValues.put("Phone", bbContactsListdata.get(i).getPhoneNumber());
                initialValues.put("Image", bbContactsListdata.get(i).getImage());
                initialValues.put("countryCode", bbContactsListdata.get(i).getCountryCode());
                rowID = sqliteDatabase.insert(BIG_BUTTON_BBCONTACTS_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // -------CASE_NOTIFICATION DATA ADDITION-------------------------------
    public long addNotifications(ArrayList<NotificationReponseDataBean> notificationListdata) {
        if (notificationListdata == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < notificationListdata.size(); i++) {
                if(!notificationListdata.get(i).getType().equalsIgnoreCase("16")) {
                    int size = checkNotificationExistence(Integer.parseInt(notificationListdata.get(i).getId()));
                    if (!(size > 0)) {
                        initialValues.put("id", notificationListdata.get(i).id);
                        initialValues.put("from_user_id", (notificationListdata.get(i).from_user_id));
                        initialValues.put("to_user_id", (notificationListdata.get(i).to_user_id));
                        initialValues.put("type", notificationListdata.get(i).type);// Integer.parseInt(notificationListdata.get(i).type)
                        initialValues.put("message", notificationListdata.get(i).message);
                        initialValues.put("datetime", notificationListdata.get(i).datetime);
                        initialValues.put("status", notificationListdata.get(i).status);
                        initialValues.put("image", notificationListdata.get(i).image);
                        if ((notificationListdata.get(i).getFrom_user_phone() != null) && !(notificationListdata.get(i).getFrom_user_phone().trim().equalsIgnoreCase(""))) {
                            initialValues.put("FromPhoneNumber", notificationListdata.get(i).getFrom_user_phone());
                        } else {
                            initialValues.put("FromPhoneNumber", "");
                        }
                        rowID = sqliteDatabase.insert(BIG_BUTTON_NOTIFICATIONS_TABLE, null, initialValues);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // -------CASE_NOTIFICATION DATA UPDATION-------------------------------
    public long updateNotification(String notification_id) {
        long rowID = -1;
        try { // status; 1 - unread, 2 - read
            ContentValues initialValues = new ContentValues();
            String WHERE = "id=?";
            String[] WHERE_ARGS = new String[]{notification_id};
            initialValues.put("status", 2);
            rowID = sqliteDatabase.update(BIG_BUTTON_NOTIFICATIONS_TABLE, initialValues, WHERE, WHERE_ARGS);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return (long) rowID;
    }

    /*
     * get notifications from database in sorted order according to read/unread
     */
    public ArrayList<NotificationReponseDataBean> getNotifications() {
        Cursor mCursor = getCursorAllNotifications();// ContactDetailsRegisteredBean
        ArrayList<NotificationReponseDataBean> list = new ArrayList<NotificationReponseDataBean>();
        try {
            if (mCursor != null) {
                do {
                    NotificationReponseDataBean applicationClassInfo = new NotificationReponseDataBean();
                    applicationClassInfo.setId(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__NOTIFICATION_ID)));
                    applicationClassInfo.setFrom_user_id(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__NOTIFICATION_FROM_USER_ID)));
                    applicationClassInfo.setTo_user_id(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__NOTIFICATION_TO_USER_ID)));
                    applicationClassInfo
                            .setType(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_TYPE)));
                    applicationClassInfo
                            .setMessage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_MESSAGE)));
                    applicationClassInfo
                            .setDatetime(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_DATE_TIME)));
                    // applicationClassInfo.setDatetime(mCursor.getString(mCursor
                    // .getColumnIndex(BIG_BUTTON_NOTIFICATION_MODIFIED_ON)));
                    applicationClassInfo
                            .setStatus(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON__NOTIFICATION_STATUS)));
                    applicationClassInfo
                            .setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__NOTIFICATION_IMAGE)));
                    applicationClassInfo
                            .setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_NAME)));

                    int index = mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_FROM_PHONE_NUMBER);
                    if (index == -1) {
                        // Column doesn't exist
                    } else {
                        applicationClassInfo
                                .setFrom_user_phone(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_NOTIFICATION_FROM_PHONE_NUMBER)));
                    }

                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
	 /*
	  * Get Cursor for Notification
	  */

    public Cursor getCursorAllNotifications() {
        Cursor mCursor = null;
        try {
			 /*
			  * String[] fields = new String[] { BIG_BUTTON__NOTIFICATION_ID,
			  * BIG_BUTTON__NOTIFICATION_FROM_USER_ID,
			  * BIG_BUTTON__NOTIFICATION_TO_USER_ID,
			  * BIG_BUTTON_NOTIFICATION_TYPE,BIG_BUTTON_NOTIFICATION_MESSAGE,
			  * BIG_BUTTON_NOTIFICATION_DATE_TIME,
			  * BIG_BUTTON__NOTIFICATION_STATUS, BIG_BUTTON__NOTIFICATION_IMAGE};
			  * String strOrder = "upper(status)";
			  */
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM Notifications ORDER BY status,datetime DESC", null);// sqliteDatabase.query(BIG_BUTTON_NOTIFICATIONS_TABLE,
                // fields,null,
                // null,
                // null,
                // null,
                // strOrder);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Check Existence of Notification
	  * 
	  */

    public int checkNotificationExistence(int id) {
        int size = -1;
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase
                    .rawQuery("SELECT Count(id) As Count FROM Notifications where id = " + String.valueOf(id), null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        return size;
    }

    /*
     * Get Count For Unread Notifications
     */
    public int getUnreadNotificationCount() {
        int size = -1;
        Cursor mCursor = null;
        mCursor = sqliteDatabase.rawQuery("Select Count(id) AS Count from Notifications where status=1", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        return size;
    }

	 /*
	  * Get MaxCount For for Notification
	  */

    public Cursor getCursorMaxCountNotifications() {
        Cursor mCursor = null;
        try {
            // String[] fields = new String[] { BIG_BUTTON__NOTIFICATION_ID,
            // BIG_BUTTON__NOTIFICATION_FROM_USER_ID,BIG_BUTTON__NOTIFICATION_TO_USER_ID,
            // BIG_BUTTON_NOTIFICATION_TYPE,BIG_BUTTON_NOTIFICATION_MESSAGE,
            // BIG_BUTTON_NOTIFICATION_CREATED_ON,BIG_BUTTON_NOTIFICATION_MODIFIED_ON,
            // BIG_BUTTON__NOTIFICATION_STATUS,BIG_BUTTON__NOTIFICATION_IMAGE};
            try {
                mCursor = sqliteDatabase.query(BIG_BUTTON_NOTIFICATIONS_TABLE,
                        new String[]{"MAX(" + BIG_BUTTON__NOTIFICATION_ID + ")"}, null, null, null, null, null);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    //Delete Notification
    public void deleteNotification(int notification_id) {
        String WHERE = "id = ?";
        String[] WHERE_ARGS = new String[]{String.valueOf(notification_id)};
        //		String WHERE2 = "from_user_id = ? AND to_user_id = ?";
        try {
            int i = sqliteDatabase.delete(BIG_BUTTON_NOTIFICATIONS_TABLE, WHERE, WHERE_ARGS);
            //			sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE2, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Insert All Default Messages
    public long insertAllDefaultMessages(ArrayList<DefaultMessagesBeanDB> mListDefaultMessages) {
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < mListDefaultMessages.size(); i++) {
                initialValues.put("id", mListDefaultMessages.get(i).getId());
                initialValues.put("message", (mListDefaultMessages.get(i).getMessage()));
                initialValues.put("type", mListDefaultMessages.get(i).getType());
                initialValues.put("isLocked", (mListDefaultMessages.get(i).getIsLocked()));
                rowID = sqliteDatabase.insert(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // -------CASE CONFIGURE MESSAGE DATA
    // ADDITION-------------------------------
    public long addUpdateInitResponseMessage(int id, String message, int type, int isLocked, boolean isUpdateMode,
                                             boolean isBackupMode) {
        long rowID = -1;
        if (isBackupMode) {
            if (MainBaseActivity.mergeTiles) {
                checkExistingConfigMessages(id, message, type, isLocked, isUpdateMode, rowID);
            } else if (!MainBaseActivity.mergeTiles) {
                ContentValues initialValues = new ContentValues();
                initialValues.put("id", id);
                initialValues.put("message", message);
                initialValues.put("type", type);
                initialValues.put("isLocked", isLocked);
                rowID = sqliteDatabase.insert(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, null, initialValues);
            }
            GlobalCommonValues.isBackedupSuccessful = true;
        } else {
            try {
                if (isUpdateMode) {
                    if (isLocked == 1) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("id", id);
                        initialValues.put("message", message);
                        initialValues.put("type", type);
                        initialValues.put("isLocked", 0);
                        rowID = sqliteDatabase.insert(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, null, initialValues);
                    } else {
                        String WHERE = "id = ?";
                        String[] WHERE_ARGS = new String[]{String.valueOf(id)};
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("message", message);
                        rowID = sqliteDatabase.update(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, initialValues, WHERE,
                                WHERE_ARGS);
                    }
                } else if (!isUpdateMode) {
                    ContentValues initialValues = new ContentValues();
                    initialValues.put("id", id);
                    initialValues.put("message", message);
                    initialValues.put("type", type);
                    initialValues.put("isLocked", isLocked);
                    rowID = sqliteDatabase.insert(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, null, initialValues);
                }
            } catch (Exception e) {
                e.getMessage();
                rowID = -1;
            }
        }
        return (long) rowID;
    }

    /*
     * get non existing Config Messsages to be merged from database
     */
    // String WHERE;
    // String[] WHERE_ARGS;
    public void checkExistingConfigMessages(int id, String messageDB, int type, int isLocked, boolean isUpdateMode,
                                            long rowID) {
        Cursor mCursor = null;
        try {
            String[] fields = new String[]{BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE};
            try {

                WHERE = "message=?";
                WHERE_ARGS = new String[]{messageDB};

                // new DatabaseHelper(mContext, "big_button_app");
                mCursor = sqliteDatabase.query(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, fields, WHERE, WHERE_ARGS, null,
                        null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCursor != null && mCursor.getCount() > 0) {
        } else {
            try {
                ContentValues initialValues = new ContentValues();
                initialValues.put("id", id);
                initialValues.put("message", messageDB);
                initialValues.put("type", type);
                initialValues.put("isLocked", isLocked);

                if (!sqliteDatabase.isOpen()) {
                    sqliteDatabase = databasehelper.getWritableDatabase();
                }
                rowID = sqliteDatabase.insert(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, null, initialValues);
                if (rowID != -1) {
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    // -------CASE MESSAGE DATA ADDITION-------------------------------
    public long addMessage(ArrayList<SendMessageReponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listMessageDetails.size(); i++) {
                int size = checkMessageExistence(Integer.parseInt(listMessageDetails.get(i).getMessage_id()));
                if (!(size > 0)) {
					 /*
					  * initialValues.put("id",Integer.parseInt(
					  * listMessageDetails.get(i).message_id));
					  * initialValues.put("from_user_id",Integer.parseInt(
					  * listMessageDetails.get(i).from_user_id));
					  * initialValues.put("from_user_phone",listMessageDetails.
					  * get(i).from_user_phone);
					  * initialValues.put("to_user_id",Integer.parseInt(
					  * listMessageDetails.get(i).to_user_id));
					  * initialValues.put("message",listMessageDetails.get(i).
					  * message);
					  * initialValues.put("status",listMessageDetails.get(i).
					  * status);
					  * initialValues.put("datetime",listMessageDetails.get(i).
					  * datatime);
					  */
                    initialValues.put("id", (listMessageDetails.get(i).message_id));
                    initialValues.put("from_user_id", (listMessageDetails.get(i).from_user_id));
                    initialValues.put("from_user_phone", listMessageDetails.get(i).from_user_phone);
                    initialValues.put("to_user_id", (listMessageDetails.get(i).to_user_id));
                    initialValues.put("message", listMessageDetails.get(i).message);
                    initialValues.put("status", listMessageDetails.get(i).status);
                    initialValues.put("datetime", listMessageDetails.get(i).datatime);
                    initialValues.put("Name", listMessageDetails.get(i).name);
                    rowID = sqliteDatabase.insert(BIG_BUTTON_MESSAGES_TABLE, null, initialValues);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // -------CASE GET MESSAGE DATA ADDITION-------------------------------
    public long addGetMessage(ArrayList<GetMessageResponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listMessageDetails.size(); i++) {
                int size = checkMessageExistence(Integer.parseInt(listMessageDetails.get(i).getMessage_id()));
                if (!(size > 0)) {
                    // initialValues.put("id",Integer.parseInt(listMessageDetails.get(i).message_id));
                    initialValues.put("id", listMessageDetails.get(i).message_id);
                    // initialValues.put("from_user_id",Integer.parseInt(listMessageDetails.get(i).from_user_id));
                    initialValues.put("from_user_id", listMessageDetails.get(i).from_user_id);
                    // initialValues.put("from_user_phone",Integer.parseInt(listMessageDetails.get(i).from_user_phone));
                    initialValues.put("from_user_phone", listMessageDetails.get(i).from_user_phone);
                    // initialValues.put("to_user_id",Integer.parseInt(listMessageDetails.get(i).to_user_id));
                    initialValues.put("to_user_id", listMessageDetails.get(i).to_user_id);
                    initialValues.put("message", listMessageDetails.get(i).message);
                    initialValues.put("status", listMessageDetails.get(i).status);
                    initialValues.put("datetime", listMessageDetails.get(i).datetime);
                    initialValues.put("name", listMessageDetails.get(i).getName());
                    rowID = sqliteDatabase.insert(BIG_BUTTON_MESSAGES_TABLE, null, initialValues);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    public long addGetMessage1(ArrayList<GetMessageResponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listMessageDetails.size(); i++) {
				 /*
				  * initialValues.put("id",Integer.parseInt(listMessageDetails.
				  * get(i).message_id));
				  * initialValues.put("from_user_id",Integer.parseInt(
				  * listMessageDetails.get(i).from_user_id));
				  * initialValues.put("from_user_phone",Integer.parseInt(
				  * listMessageDetails.get(i).from_user_phone));
				  * initialValues.put("to_user_id",Integer.parseInt(
				  * listMessageDetails.get(i).to_user_id));
				  * initialValues.put("message",listMessageDetails.get(i).message
				  * );
				  * initialValues.put("status",listMessageDetails.get(i).status);
				  * initialValues.put("datetime",listMessageDetails.get(i).
				  * datetime);
				  */

                initialValues.put("id", (listMessageDetails.get(i).message_id));
                initialValues.put("from_user_id", (listMessageDetails.get(i).from_user_id));
                initialValues.put("from_user_phone", (listMessageDetails.get(i).from_user_phone));
                initialValues.put("to_user_id", (listMessageDetails.get(i).to_user_id));
                initialValues.put("message", listMessageDetails.get(i).message);
                initialValues.put("status", listMessageDetails.get(i).status);
                initialValues.put("datetime", listMessageDetails.get(i).datetime);
                initialValues.put("Name", listMessageDetails.get(i).name);
                rowID = sqliteDatabase.insert(BIG_BUTTON_MESSAGES_TABLE, null, initialValues);

            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Delete Configured Message From IsLocked
    public int deleteConfigMessage(int isLocked) {
        int i = -1;
        String WHERE = "isLocked = ? ";
        String[] WHERE_ARGS = new String[]{String.valueOf(isLocked)};
        try {
            i = sqliteDatabase.delete(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, WHERE, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            i = -1;
        }
        return i;
    }

    // Delete Configured Message From Id
    public int deleteConfigMessage(String id) {
        int i = -1;
        String WHERE = "id = ? ";
        String[] WHERE_ARGS = new String[]{id};
        try {
            i = sqliteDatabase.delete(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, WHERE, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            i = -1;
        }
        return i;
    }

    // Delete Message From Id
    public int deleteMessage(String id) {
        int i = -1;
        String WHERE = "id = ? ";
        String[] WHERE_ARGS = new String[]{id};
        try {
            i = sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE, WHERE_ARGS);
            // sqliteDatabase.rawQuery("DELETE FROM " +
            // BIG_BUTTON_MESSAGES_TABLE + " where id = '" +id + "'",
            // null);//delete(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, WHERE,
            // WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            i = -1;
        }
        return i;
    }

    // Delete Message From Text
    public int deleteMessageFromText(ArrayList<String> listDeleteMessages) {
        int i = -1;
        String WHERE = "message = ? ";
        try {
            for (int k = 0; k < listDeleteMessages.size(); k++) {
                String[] WHERE_ARGS = new String[]{listDeleteMessages.get(k)};
                i = sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE, WHERE_ARGS);
            }
            // sqliteDatabase.rawQuery("DELETE FROM " +
            // BIG_BUTTON_MESSAGES_TABLE + " where id = '" +id + "'",
            // null);//delete(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE, WHERE,
            // WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            i = -1;
        }
        return i;
    }

    public void deleteUserChat(int to_user_id_delete, int from_user_id_delete) {
        String WHERE = "to_user_id = ? AND from_user_id=?";
        String[] WHERE_ARGS = new String[]{String.valueOf(to_user_id_delete), String.valueOf(from_user_id_delete)};
        String WHERE2 = "from_user_id = ? AND to_user_id = ?";
        // String[] WHERE_ARGS2 = new String[] {
        // String.valueOf(to_user_id_delete),String.valueOf(from_user_id_delete)
        // };
        try {
            sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE, WHERE_ARGS);
            sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE2, WHERE_ARGS);
            // rawQuery("DELETE FROM " + BIG_BUTTON_MESSAGES_TABLE + " where
            // to_user_id = '" +to_user_id_delete + "'" +" AND from_user_id = '"
            // +from_user_id_delete + "'" + " OR to_user_id = '" +
            // from_user_id_delete + "'" +" AND from_user_id = '" +
            // to_user_id_delete + "'"+"", null);
            // delete(BIG_BUTTON_MESSAGES_TABLE, WHERE, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
        }
        // GlobalConfig_Methods.testCopy();
    }

    /*
     * get messages from database
     */
    public ArrayList<SendMessageReponseDataBean> getMessages(int to_user_id) {
        Cursor mCursor = getCursorAllMessages(to_user_id);// ContactDetailsRegisteredBean
        ArrayList<SendMessageReponseDataBean> list = new ArrayList<SendMessageReponseDataBean>();
        try {
            if (mCursor != null) {
                do {
                    SendMessageReponseDataBean applicationClassInfo = new SendMessageReponseDataBean();
                    applicationClassInfo
                            .setMessage_id(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_ID)));
                    applicationClassInfo.setFrom_user_id(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_FROM_USER_ID)));
                    applicationClassInfo.setFrom_user_phone(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_FROM_PHONENUMBER)));
                    applicationClassInfo
                            .setTo_user_id(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_TO_USER_ID)));
                    applicationClassInfo
                            .setMessage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_MESSAGE)));
                    applicationClassInfo
                            .setStatus(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_STATUS)));
                    applicationClassInfo
                            .setDatatime(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_DATETIME)));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_NAME)));
                    // applicationClassInfo.setUnread_count(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON__MESSAGE_UNREAD_COUNT)));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
	 /*
	  * Get Cursor for Messages
	  */

    public Cursor getCursorAllMessages(int to_user_id) {
        Cursor mCursor = null;
        try {
            try {
                if (to_user_id == -164) {
                    mCursor = sqliteDatabase.rawQuery(
                            "SELECT id, from_user_id, to_user_id, message, status, datetime, from_user_phone, Name, (SELECT COUNT(id)  FROM Messages m2 WHERE  status=1 AND m1.from_user_id = m2.from_user_id) As unread_count FROM Messages m1 GROUP BY from_user_id,to_user_id ORDER BY id DESC",
                            null);
                } else {
                    mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_MESSAGES_TABLE
                                    + " where to_user_id = '" + to_user_id + "'" + " or from_user_id = '" + to_user_id + "'",
                            null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    /*
     * get initiation/response messages from database
     */
    public ArrayList<InitResponseMessageBean> getInitResponseMessages(int type, int isLocked) {
        Cursor mCursor = getCursorInitResponseMessages(type, isLocked);// ContactDetailsRegisteredBean
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try {
            if (mCursor != null) {
                do {
                    InitResponseMessageBean applicationClassInfo = new InitResponseMessageBean();
                    applicationClassInfo.setId(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ID))));
                    applicationClassInfo.setMessage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE)));
                    applicationClassInfo.setType(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_TYPE))));
                    applicationClassInfo.setLocked(Integer.parseInt(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED))));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * get initiation & response messages from database(Custom/Locked)
     */
    public ArrayList<InitResponseMessageBean> getInitResponseMessagesCustomLocked(int isLocked) {
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_CONFIGURED_MESSAGES_TABLE
                    + " where isLocked = '" + isLocked + "'" + " ORDER BY message COLLATE NOCASE ASC", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try {
            if (mCursor != null) {
                do {
                    InitResponseMessageBean applicationClassInfo = new InitResponseMessageBean();
                    applicationClassInfo.setId(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ID))));
                    applicationClassInfo.setMessage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE)));
                    applicationClassInfo.setType(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_TYPE))));
                    applicationClassInfo.setLocked(Integer.parseInt(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED))));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

	 /*
	  * Get Cursor for Init/Response Messages
	  */

    public Cursor getCursorInitResponseMessages(int type, int isLocked) {
        Cursor mCursor = null;
        // String[] WHERE_ARGS = new String[] {
        // String.valueOf(type),String.valueOf(isLocked) };
        try {
            // sqliteDatabase.query(BIG_BUTTON_CONFIGURED_MESSAGES,new String[]
            // { "type","isLocked"},"type = ? AND isLocked = ?", new String[] {
            // String.valueOf(type),String.valueOf(isLocked)},null,null,null);
            // mCursor=sqliteDatabase.rawQuery("SELECT * FROM " +
            // BIG_BUTTON_CONFIGURED_MESSAGES_TABLE + " where type = '"+ type
            // +"'" +" AND isLocked = '" + isLocked + "'", null);
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_CONFIGURED_MESSAGES_TABLE + " where type = '" + type + "'" + " AND isLocked = '" + isLocked + "'" + " ORDER BY message COLLATE NOCASE ASC", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != mCursor) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

	 /*
	  * get initiation/response messages from database
	  */

    public ArrayList<InitResponseMessageBean> getInitResponseMessages(int type) {
        Cursor mCursor = getCursorInitResponseMessages(type, false);// ContactDetailsRegisteredBean
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try {
            if (mCursor != null) {
                do {
                    InitResponseMessageBean applicationClassInfo = new InitResponseMessageBean();
                    applicationClassInfo.setId(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ID))));
                    applicationClassInfo.setMessage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE)));
                    applicationClassInfo.setType(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_TYPE))));
                    applicationClassInfo.setLocked(Integer.parseInt(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED))));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // In case of BackupMode
    public ArrayList<InitResponseMessageBean> getInitResponseMessages() {
        Cursor mCursor = getCursorInitResponseMessages(0, true);// ContactDetailsRegisteredBean
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try {
            if (mCursor != null) {
                do {
                    InitResponseMessageBean applicationClassInfo = new InitResponseMessageBean();
                    applicationClassInfo.setId(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ID))));
                    applicationClassInfo.setMessage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE)));
                    applicationClassInfo.setType(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_TYPE))));
                    applicationClassInfo.setLocked(Integer.parseInt(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED))));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
            // mCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCursor = getCursorInitResponseMessages(1, true);// ContactDetailsRegisteredBean

        try {
            if (mCursor != null) {
                do {
                    InitResponseMessageBean applicationClassInfo = new InitResponseMessageBean();
                    applicationClassInfo.setId(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ID))));
                    applicationClassInfo.setMessage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_MESSAGE)));
                    applicationClassInfo.setType(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_TYPE))));
                    applicationClassInfo.setLocked(Integer.parseInt(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIGURED_MESSAGE_ISLOCKED))));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sqliteDatabase != null && sqliteDatabase.isOpen()) {
                sqliteDatabase.close();
            }
            if (sqliteDatabaseCheckExistence != null && sqliteDatabaseCheckExistence.isOpen()) {
                sqliteDatabaseCheckExistence.close();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

	 /*
	  * Get Cursor for Init/Response Messages
	  */

    public Cursor getCursorInitResponseMessages(int type, boolean isBackupMode) {
        Cursor mCursor = null;
        if (isBackupMode) {
            try {
                mCursor = sqliteDatabaseCheckExistence.rawQuery("SELECT * FROM " + BIG_BUTTON_CONFIGURED_MESSAGES_TABLE
                        + " where type = '" + type + "'" + " ORDER BY message COLLATE NOCASE ASC", null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } else {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_CONFIGURED_MESSAGES_TABLE
                        + " where type = '" + type + "'" + " ORDER BY message COLLATE NOCASE ASC", null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        }
        return mCursor;
    }

	 /*
	  * Get MaxCount For for ConfigMessage
	  */

    public Cursor getCursorMaxCountConfigMessages() {
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT MAX(id) FROM ConfiguredMessages", null);// sqliteDatabase.query(BIG_BUTTON_CONFIGURED_MESSAGES_TABLE,new
                // String
                // []
                // {"MAX("+BIG_BUTTON_CONFIGURED_MESSAGE_ID+")"},
                // null,
                // null,
                // null,
                // null,
                // null);
                // //system.out.println(mCursor.getString(mCursor.getColumnIndex("maxValue")));
                if (mCursor != null)
                    mCursor.moveToFirst();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Check Existence of Config Message
	  * 
	  */

    public int checkConfigMessageExistence(String message) {
        int size = -1;
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT Count(message) As Count FROM ConfiguredMessages where message = '"
                    + message + "'" + " COLLATE NOCASE", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        return size;
    }

	 /*
	  * Get MaxCount For for Message
	  */

    public Cursor getCursorMaxCountMessages() {
        Cursor mCursor = null;
        try {
            try {

                sqliteDatabase.query(BIG_BUTTON_MESSAGES_TABLE, new String[]{"MAX(" + BIG_BUTTON__MESSAGE_ID + ")"},
                        null, null, null, null, null);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Check Existence of Message
	  * 
	  */

    public int checkMessageExistence(int id) {
        int size = -1;
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase
                    .rawQuery("SELECT Count(id) As Count FROM Messages where id = " + String.valueOf(id), null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));

        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return size;
    }

    public int getUnreadMessageCount() {
        int size = -1;
        Cursor mCursor = null;
        mCursor = sqliteDatabase.rawQuery("Select Count(id) AS Count from messages where status=1", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        }
        return size;
    }

    public int getUnreadMessageCountOfTnCUser(int from_id) {
        int size = -1;
        Cursor mCursor = null;
        mCursor = sqliteDatabase
                .rawQuery("Select Count(id) AS Count from messages where status=1 AND from_user_id = " + from_id, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        return size;
    }

    /**
     * update message status to read  // 1 - unread 2 - read
     * @param to_user_id_status_update
     * @param from_user_id_status_update
     */
    public void updateMessageStatus(int to_user_id_status_update, int from_user_id_status_update) {
        String WHERE = "to_user_id = ? AND from_user_id=?";
        String[] WHERE_ARGS = new String[]{String.valueOf(to_user_id_status_update),
                String.valueOf(from_user_id_status_update)};
        String WHERE2 = "from_user_id = ? AND to_user_id = ?";
        // String[] WHERE_ARGS2 = new String[] {
        // String.valueOf(to_user_id_delete),String.valueOf(from_user_id_delete)
        // };
        try {
            ContentValues initialvalues = new ContentValues();
            initialvalues.put("status", 2);
            sqliteDatabase.update(BIG_BUTTON_MESSAGES_TABLE, initialvalues, WHERE, WHERE_ARGS);
            sqliteDatabase.update(BIG_BUTTON_MESSAGES_TABLE, initialvalues, WHERE2, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /*
     * get contact details from database
     */
    public ArrayList<ContactDetailsUserBean> getUsers() {
        Cursor mCursor = getCursorAllUser();// ContactDetailsRegisteredBean
        ArrayList<ContactDetailsUserBean> list = new ArrayList<ContactDetailsUserBean>();
        try {
            if (mCursor != null) {
                do {
                    ContactDetailsUserBean applicationClassInfo = new ContactDetailsUserBean();
                    applicationClassInfo.setBBID(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_BBID))));
                    applicationClassInfo
                            .setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_NAME)));
                    applicationClassInfo
                            .setEmail(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_EMAIL)));
                    applicationClassInfo.setPhoneNumber(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_NUMBER)));
                    applicationClassInfo.setImage(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_IMAGE)).getBytes());
                    applicationClassInfo.setImage(mCursor
                            .getString(mCursor.getColumnIndex(BIG_BUTTON_REGISTERED_USER_IS_DEAFULT_IMAGE)).getBytes());

                    // list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
	 /*
	  * Get Cursor for Big Button Users
	  */

    public Cursor getCursorAllUser() {
        Cursor mCursor = null;
        try {
            String[] fields = new String[]{BIG_BUTTON_REGISTERED_USER_BBID, BIG_BUTTON_REGISTERED_USER_NAME,
                    BIG_BUTTON_REGISTERED_USER_EMAIL, BIG_BUTTON_REGISTERED_USER_NUMBER,
                    BIG_BUTTON_REGISTERED_USER_IMAGE, BIG_BUTTON_REGISTERED_USER_IS_DEAFULT_IMAGE};

            try {
                mCursor = sqliteDatabase.query(BIG_BUTTON_USER_TABLE, fields, null, null, null, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    /*
     * get non existing tile to be merged from database
     */
    String WHERE;
    String[] WHERE_ARGS;

    public void checkExistingTiles(String phoneNumber, int position, ArrayList<ContactTilesBean> tilesListdata,
                                   long rowID) {
        Cursor mCursor = null;
        try {
            try {
                // mCursor=sqliteDatabase.rawQuery("SELECT PhoneNumber,Prefix
                // FROM Tiles where PhoneNumber = '"
                // +String.valueOf(tilesListdata.get(position).getPhoneNumber())
                // + "'" +" AND Prefix = '" +
                // tilesListdata.get(position).getPrefix() + "'"+"",null);
                mCursor = sqliteDatabase.rawQuery("SELECT PhoneNumber,CountryCode FROM Tiles where PhoneNumber = '"
                        + String.valueOf(phoneNumber) + "'" + " AND CountryCode = '"
                        + tilesListdata.get(position).getCountryCode() + "'" + "", null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCursor != null && mCursor.getCount() > 0) {
        } else {
            try {

                ContentValues initialValues = new ContentValues();
                int maxEmergencyTilePosition;
                int maxTilePosition;
                if (tilesListdata.get(position).isIsEmergency()) {
                    Cursor mCursorEmergencyTile = getCursorMaxCountTilePositionEmergency();
                    if (mCursorEmergencyTile
                            .getString(mCursorEmergencyTile.getColumnIndex("MAX(TilePosition)")) == null)
                        maxEmergencyTilePosition = -1;
                    else {
                        maxEmergencyTilePosition = Integer.parseInt(mCursorEmergencyTile
                                .getString(mCursorEmergencyTile.getColumnIndex("MAX(TilePosition)")));
                    }
                    if (maxEmergencyTilePosition == -1)
                        maxEmergencyTilePosition = 0;
                    else {
                        maxEmergencyTilePosition = maxEmergencyTilePosition + 1;
                    }
                    ArrayList<ContactTilesBean> listContactDB = new ArrayList<ContactTilesBean>();
                    listContactDB = DBQuery.getAllTiles(mContext);
                    if (listContactDB.size() > 0) {
                        // Re-insert the reindexed positioned values
                        for (int countList = 0; countList < listContactDB.size(); countList++) {
                            DBQuery.updateTilePositionByOne(mContext, listContactDB.get(countList).getPhoneNumber(),
                                    Integer.parseInt(listContactDB.get(countList).getTilePosition()));
                            // listContactDB.get(countList).setTilePosition(String.valueOf(countList+1));
                        }
                    }
                    initialValues.put("Name", tilesListdata.get(position).getName());
                    initialValues.put("PhoneNumber", tilesListdata.get(position).getPhoneNumber());
                    initialValues.put("Image", tilesListdata.get(position).getImage());
                    initialValues.put("IsEmergency", tilesListdata.get(position).isIsEmergency());
                    initialValues.put("TilePosition", maxEmergencyTilePosition);
                    initialValues.put("IsImagePending", tilesListdata.get(position).getIsImagePending());
                    initialValues.put("Prefix", tilesListdata.get(position).getPrefix());
                    initialValues.put("CountryCode", tilesListdata.get(position).getCountryCode());
                    initialValues.put("IsTncUser", 0); // ,tilesListdata.get(position).isIsTncUser()
                    initialValues.put("BBID", tilesListdata.get(position).getBBID());
                    initialValues.put("IsMobile", tilesListdata.get(position).isIsMobile());
                    initialValues.put(DBConstant.TILE_COLUMN_IMAGE_LOCK, tilesListdata.get(position).isImageLocked());
                    try {
                        initialValues.put("ButtonType", 1); // tilesListdata.get(i).getButtonType()
                    } catch (Exception e) {
                        e.getMessage();
                    }

                    if (!sqliteDatabase.isOpen()) {
                        sqliteDatabase = databasehelper.getWritableDatabase();
                    }
                    rowID = sqliteDatabase.insert(BIG_BUTTON_TILES_TABLE, null, initialValues);
                    if (rowID != -1) {
                    }
                } else {
                    Cursor mCursorMaxTile = getCursorMaxCountTilePosition();
                    if (mCursorMaxTile.getString(mCursorMaxTile.getColumnIndex("MAX(TilePosition)")) == null)
                        maxTilePosition = -1;
                    else {
                        maxTilePosition = Integer
                                .parseInt(mCursorMaxTile.getString(mCursorMaxTile.getColumnIndex("MAX(TilePosition)")));
                    }
                    if (maxTilePosition == -1)
                        maxTilePosition = 0;
                    else {
                        maxTilePosition = maxTilePosition + 1;
                    }

                    initialValues.put("Name", tilesListdata.get(position).getName());
                    initialValues.put("PhoneNumber", tilesListdata.get(position).getPhoneNumber());
                    initialValues.put("Image", tilesListdata.get(position).getImage());
                    initialValues.put("IsEmergency", tilesListdata.get(position).isIsEmergency());
                    initialValues.put("TilePosition", maxTilePosition);
                    initialValues.put("IsImagePending", tilesListdata.get(position).getIsImagePending());
                    initialValues.put("Prefix", tilesListdata.get(position).getPrefix());
                    initialValues.put("CountryCode", tilesListdata.get(position).getCountryCode());
                    initialValues.put("IsTncUser", tilesListdata.get(position).isIsTncUser());
                    initialValues.put("IsMobile", tilesListdata.get(position).isIsMobile());
                    initialValues.put(DBConstant.TILE_COLUMN_IMAGE_LOCK,tilesListdata.get(position).isImageLocked());
                    if (!sqliteDatabase.isOpen()) {
                        sqliteDatabase = databasehelper.getWritableDatabase();
                    }
                    rowID = sqliteDatabase.insert(BIG_BUTTON_TILES_TABLE, null, initialValues);
                    if (rowID != -1) {
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
			 /*
			  * try { ContentValues initialValues = new ContentValues();
			  * initialValues.put("Name",tilesListdata.get(position).getName());
			  * initialValues.put("PhoneNumber",tilesListdata.get(position).
			  * getPhoneNumber());
			  * initialValues.put("Image",tilesListdata.get(position).getImage())
			  * ; initialValues.put("IsEmergency",tilesListdata.get(position).
			  * isIsEmergency());
			  * initialValues.put("TilePosition",tilesListdata.get(position).
			  * getTilePosition());
			  * initialValues.put("IsImagePending",tilesListdata.get(position).
			  * getIsImagePending()); if(!sqliteDatabase.isOpen()) {
			  * sqliteDatabase=databasehelper.getWritableDatabase(); } rowID =
			  * sqliteDatabase.insert(BIG_BUTTON_TILES_TABLE,
			  * null,initialValues); if(rowID!=-1) { } } catch (Exception e) {
			  * e.getMessage(); }
			  */
        }
    }

    /*
     * get tile details from database
     */
    public ArrayList<ContactTilesBean> getTiles(boolean isBackupMode) {
        Cursor mCursor = getCursorAllTiles(isBackupMode);// ContactDetailsRegisteredBean
        ArrayList<ContactTilesBean> list = new ArrayList<ContactTilesBean>();
        try {
            if (mCursor != null) {
                do {
                    ContactTilesBean applicationClassInfo = new ContactTilesBean();
                    String mName = "";
                    mName = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NAME));
                    if (mName.contains("&#39;"))
                        mName = mName.replaceAll("&#39;", "'");
                    applicationClassInfo.setName(mName);
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NUMBER)));
                    applicationClassInfo.setImage(mCursor.getBlob(mCursor.getColumnIndex(BIG_BUTTON_TILE_IMAGE)));

                    if ((mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_ISMOBILE)) == 1)) {
                        applicationClassInfo.setIsMobile(true);
                    } else {
                        applicationClassInfo.setIsMobile(false);
                    }

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)) == null) {
                        applicationClassInfo.setIsEmergency(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)).equals("1")) {
                            applicationClassInfo.setIsEmergency(true);
                        } else {
                            applicationClassInfo.setIsEmergency(false);
                        }
                    }

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)) == null || mCursor
                            .getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)).equalsIgnoreCase("-1")) {
                        applicationClassInfo
                                .setTilePosition(String.valueOf(DBQuery.getMaximumTilePosition(mContext) + 1));
                    } else {
                        applicationClassInfo
                                .setTilePosition(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
                    }

                    try {
                        if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_IMAGE_PENDING)) == 1) {
                            applicationClassInfo.setIsImagePending(1);
                        } else {
                            applicationClassInfo.setIsImagePending(0);
                        }
                    } catch (Exception e) {
                        applicationClassInfo.setIsImagePending(0);
                        e.getMessage();
                    }
                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

                    try {
                        if (mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID) > 0) {
                            if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID)) > 0) {
                                applicationClassInfo
                                        .setBBID(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID)));
                            } else {
                                applicationClassInfo.setBBID(0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        applicationClassInfo.setIsImagePending(0);
                        e.getMessage();
                    }

                    applicationClassInfo.setPrefix(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_PREFIX)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_COUNTRY_CODE)));
                    if (mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER) > 0) {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER)) == null) {
                            applicationClassInfo.setIsTncUser(false);
                        } else {
                            if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER)).equals("1")) {
                                applicationClassInfo.setIsTncUser(true);
                            } else {
                                applicationClassInfo.setIsTncUser(false);
                            }
                        }
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_iS_TNC_USER)) == null) {
                            applicationClassInfo.setIsTncUser(false);
                        } else {
                            if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_iS_TNC_USER)).equals("1")) {
                                applicationClassInfo.setIsTncUser(true);
                            } else {
                                applicationClassInfo.setIsTncUser(false);
                            }
                        }
                    }

                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

                    try {
                        applicationClassInfo.setButtonType(
                                mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE)));
                        /*if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE)) > 0) {
                            applicationClassInfo.setButtonType(
                                    mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE)));
                        } else {
                            applicationClassInfo.setButtonType(1);
                        }*/
                        // }
                    } catch (Exception e) {
//                        applicationClassInfo.setButtonType(1);
                        e.getMessage();
                    }

                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sqliteDatabase != null && sqliteDatabase.isOpen()) {
            sqliteDatabase.close();
        }
        if (sqliteDatabaseCheckExistence != null && sqliteDatabaseCheckExistence.isOpen()) {
            sqliteDatabaseCheckExistence.close();
        }
        return list;
    }
	 /*
	  * Get Cursor for Big Button Tiles
	  */

    public Cursor getCursorAllTiles(boolean isBackupMode) {
        Cursor mCursor = null;
        try {

            try {
                if (isBackupMode) {
                    mCursor = sqliteDatabaseCheckExistence.rawQuery("Select * From Tiles ORDER BY TilePosition ASC",
                            null);
                } else {
                    mCursor = sqliteDatabase.rawQuery("Select * From Tiles ORDER BY TilePosition ASC", null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Check Existence of Tiles
	  * 
	  */

    public int checkTileExistence(String phoneNumber, String countryCode) {
        int size = -1;
        Cursor mCursor = null;
        try {
            // mCursor=sqliteDatabase.rawQuery("SELECT Count(PhoneNumber) As
            // Count FROM Tiles where PhoneNumber = '"
            // +String.valueOf(phoneNumber) + "'",null);
            mCursor = sqliteDatabase.rawQuery("SELECT PhoneNumber,CountryCode FROM Tiles where PhoneNumber = '"
                    + String.valueOf(phoneNumber) + "'" + " AND CountryCode = '" + countryCode + "'" + "", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            // Commented as we are check tile existence with only last 10 digits
			 /*
			  * if(mCursor.getString(mCursor.getColumnIndex("PhoneNumber")).
			  * equals(phoneNumber) &&
			  * mCursor.getString(mCursor.getColumnIndex("Prefix")).equals(prefix
			  * )) { size=1; }
			  */
			 /*
			  * size=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(
			  * "Count")));
			  */// Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("PhoneNumber")));
            if (mCursor.getCount() > 0) {
                size = 1;
            } else {
                size = -1;
            }
        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        return size;
    }

    // Update Tile
    public void updateTile(ContactTilesBean objcontactTile) {

        // in cas eof de-registration
        if(objcontactTile == null){
            try {
                // Update all tiles table an reset the values as Non-Chatstasy User

                ContentValues cv = new ContentValues();
                cv.put("IsTncUser", false);
                cv.put("BBID", 0);
                int count = sqliteDatabase.update("Tiles", cv, null, null);
                Log.i("", count + "");
            }catch(Exception e){
                e.getMessage();
            }
        }else{
            try {
                String pos = objcontactTile.getTilePosition();
                // pos=getCurrentTilePosition(PhoneNumber)+1;
                ContentValues cv = new ContentValues();
                cv.put("Name", objcontactTile.getName());
                cv.put("PhoneNumber", objcontactTile.getPhoneNumber());
                cv.put("Image", objcontactTile.getImage());
                cv.put("Prefix", objcontactTile.getPrefix());
                cv.put("CountryCode", objcontactTile.getCountryCode());
                cv.put("IsEmergency", objcontactTile.isIsEmergency());
                cv.put("IsImagePending", objcontactTile.getIsImagePending());
                cv.put("IsTncUser", objcontactTile.isIsTncUser());
                cv.put("BBID", objcontactTile.getBBID());
                cv.put(DBConstant.TILE_COLUMN_IMAGE_LOCK,objcontactTile.isImageLocked());

                /**
                 * DEVANSHU NATH TRIPATHI
                 */

                if (objcontactTile.isIsMobile()) {
                    cv.put(BIG_BUTTON_TILE__ISMOBILE, 1);
                } else {
                    cv.put(BIG_BUTTON_TILE__ISMOBILE, 0);
                }

                if (objcontactTile.isIsMobile()) {
                    //system.out.println("ISMOBILE 4 " + "TRUE");
                } else {
                    //system.out.println("ISMOBILE 4 " + "FALSE");
                }

                try {
//                    if (objcontactTile.getButtonType() > 0)
                    cv.put("ButtonType", objcontactTile.getButtonType());
                } catch (Exception e) {
                    e.getMessage();
                }
                String where = "TilePosition = ?";
			 /* bind VALUES here */
                String[] whereArgs = {pos};
                sqliteDatabase.update("Tiles", cv, where, whereArgs);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    // Update Tile's PhoneNumber
    public void updateTilePhoneNumber(String phoneNumber, String tilePosition) {
        try {
            String pos = tilePosition;
            ContentValues cv = new ContentValues();
            cv.put("PhoneNumber", phoneNumber);
            // cv.put("Prefix",prefix);
            String where = "TilePosition = ?";
			 /* bind VALUES here */
            String[] whereArgs = {pos};
            sqliteDatabase.update("Tiles", cv, where, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Update Tile's PhoneNumber
    public void updateTilePhoneNumberButtonUpdate(String countryCodeRegisteredUserOld, String phoneNumber,
                                                  String tilePosition) {
        try {
            String pos = tilePosition;
            ContentValues cv = new ContentValues();
            cv.put("CountryCode", countryCodeRegisteredUserOld);
            cv.put("PhoneNumber", phoneNumber);
            String where = "TilePosition = ?";
			 /* bind VALUES here */
            String[] whereArgs = {pos};
            sqliteDatabase.update("Tiles", cv, where, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Update Tile Details on Registration
    public void updateTileDetailsOnRegistration(String countryCode, String isdCode, String phoneNumber,
                                                String tilePosition) {
        try {
            String pos = tilePosition;
            ContentValues cv = new ContentValues();
            cv.put("CountryCode", countryCode);
            cv.put("Prefix", isdCode);
            cv.put("PhoneNumber", phoneNumber);
            // cv.put("Prefix",prefix);
            String where = "TilePosition = ?";
			 /* bind VALUES here */
            String[] whereArgs = {pos};
            sqliteDatabase.update("Tiles", cv, where, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Update Tile Position By One
    public void updateTilePositionByOne(String PhoneNumber, int tilePosition) {
        try {
            int pos = -1;
            pos = getCurrentTilePosition(PhoneNumber) + 1;
            ContentValues cv = new ContentValues();
            cv.put("TilePosition", pos);
            String where = "PhoneNumber = ?";
			 /* bind VALUES here */
            String[] whereArgs = {PhoneNumber};
            sqliteDatabase.update("Tiles", cv, where, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Update Tile Position By One
    public void updateTilePositionOnRearrange(int index, String PhoneNumber) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("TilePosition", index);
            String where = "PhoneNumber = ?";
			 /* bind VALUES here */
            String[] whereArgs = {PhoneNumber};
            sqliteDatabase.update("Tiles", cv, where, whereArgs);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //get current tile position
    private int getCurrentTilePosition(String phoneNumber) {
        int count = -1;
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase
                    .rawQuery("Select TilePosition FROM Tiles where PhoneNumber = " + "'" + phoneNumber + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                count = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
            }
        } catch (Exception e) {
            e.getMessage();
            count = -1;
        }
        return count;
    }

	 /*
	  * Get MaxCount For Tile Position
	  */

    public Cursor getCursorMaxCountTilePosition() {
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT MAX(TilePosition) FROM Tiles", null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Get MaxCount For Max Tile Position Of Emergency
	  */

    public Cursor getCursorMaxCountTilePositionEmergency() {
        Cursor mCursor = null;
        try {
            try {

                mCursor = sqliteDatabase.rawQuery("SELECT MAX(TilePosition) FROM Tiles WHERE IsEmergency=1", null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    public void updateTileResetTnCUser() {
        String where = "IsTncUser = ?";

		 /* bind VALUES here */
        String[] whereArgs = {String.valueOf(1)};

        ContentValues cv = new ContentValues();
        cv.put("IsTncUser", false);

        sqliteDatabase.update("Tiles", cv, where, whereArgs);
    }

    /*
     * get BBContacts details from database
     */
    public ArrayList<BBContactsBean> getBBContacts() {
        Cursor mCursor = getCursorAllBBContacts();// ContactDetailsRegisteredBean
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            if (mCursor != null) {
                do {
                    BBContactsBean applicationClassInfo = new BBContactsBean();
                    applicationClassInfo.setMobID(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_MOBILEID))));
                    applicationClassInfo.setBBID(
                            Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_BBID))));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_PHONE)));
                    applicationClassInfo
                            .setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_IMAGE)));
                    applicationClassInfo.setCountryCode(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_COUNTRYCODE)));


                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * get BBContacts details from database
     */
    public ArrayList<BBContactsBean> getBBContacts2() {
        Cursor mCursor = getCursorAllBBContacts2();// ContactDetailsRegisteredBean
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            if (mCursor != null) {
                do {
                    BBContactsBean applicationClassInfo = new BBContactsBean();
                    applicationClassInfo.setMobID(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_MOBILEID))));
                    applicationClassInfo.setBBID(
                            Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_BBID))));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_PHONE)));
                    applicationClassInfo
                            .setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_IMAGE)));
                    applicationClassInfo.setCountryCode(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_COUNTRYCODE)));


                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /*
     * get BBContact details from PhoneNumber And Country Code From the database
     */
    public ArrayList<BBContactsBean> getBBContactsFromCountrycodeAndPhoneNumber(String countryCode,
                                                                                String phoneNumber) {
        Cursor mCursor = null;
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM BBContacts where countryCode = '" + countryCode + "'"
                    + " AND " + "Phone = '" + phoneNumber + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    BBContactsBean applicationClassInfo = new BBContactsBean();
                    applicationClassInfo.setMobID(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_MOBILEID))));
                    applicationClassInfo.setBBID(
                            Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_BBID))));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_PHONE)));
                    applicationClassInfo
                            .setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_IMAGE)));
                    applicationClassInfo.setCountryCode(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_COUNTRYCODE)));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * get Ordered BBContacts details from database
     */
    public ArrayList<BBContactsBean> getBBContactsOrdered() {
        Cursor mCursor = null;// ContactDetailsRegisteredBean
        mCursor = sqliteDatabase.rawQuery("SELECT * FROM BBContacts ORDER BY Name COLLATE NOCASE ASC", null);
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    BBContactsBean applicationClassInfo = new BBContactsBean();
                    applicationClassInfo.setMobID(Integer
                            .parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_MOBILEID))));
                    applicationClassInfo.setBBID(
                            Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_BBID))));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_PHONE)));
                    applicationClassInfo
                            .setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_IMAGE)));
                    applicationClassInfo.setCountryCode(
                            mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_COUNTRYCODE)));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//    /*
//     * Get Phone From BBID
//     */
//    public String getPhoneNumberFromBBID(int BBID) {
//        String phoneNumber = "";
//        Cursor mCursor = null;
//        try {
//            mCursor = sqliteDatabase.rawQuery("SELECT Phone As Phone FROM BBContacts where BBID = '" + BBID + "'",
//                    null);// ("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE + "
//            // where Phone = '" +phoneNumber + "'", null);
//            if (mCursor != null) {
//                mCursor.moveToFirst();
//                phoneNumber = mCursor.getString(mCursor.getColumnIndex("Phone"));
//            }
//        } catch (Exception e) {
//            e.getMessage();
//            phoneNumber = "";
//        }
//        return phoneNumber;
//    }
//
//    /*
//     * Get BBID from phone Number
//     */
//    public int getBBIDFromPhoneNumber(String phoneNumber) {
//        int BBID = 0;
//        Cursor mCursor = null;
//        try {
//            mCursor = sqliteDatabase.rawQuery("SELECT BBID As BBID FROM BBContacts where Phone = '" + phoneNumber + "'",
//                    null);// ("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE + "
//            // where Phone = '" +phoneNumber + "'", null);
//            if (mCursor != null) {
//                mCursor.moveToFirst();
//                BBID = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("BBID")));
//            }
//
//        } catch (Exception e) {
//        }
//        return BBID;
//                            applicationClassInfo.setButtonType(
//                                    mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE)));
//                        } else {
//                            applicationClassInfo.setButtonType(1);
//                        }
//                        // }
//                    } catch (Exception e) {
//                        applicationClassInfo.setButtonType(1);
//                        e.getMessage();
//                    }
//
//                    list.add(applicationClassInfo);
//                } while (mCursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (sqliteDatabase != null && sqliteDatabase.isOpen()) {
//            sqliteDatabase.close();
//        }
//        if (sqliteDatabaseCheckExistence != null && sqliteDatabaseCheckExistence.isOpen()) {
//            sqliteDatabaseCheckExistence.close();
//        }
//        return list;
//    }

    /*
     * Get Phone From BBID
     */
    public String getPhoneNumberFromBBID(int BBID) {
        String phoneNumber = "";
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT Phone As Phone FROM BBContacts where BBID = '" + BBID + "'",
                    null);// ("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE + "
            // where Phone = '" +phoneNumber + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                phoneNumber = mCursor.getString(mCursor.getColumnIndex("Phone"));
            }
        } catch (Exception e) {
            e.getMessage();
            phoneNumber = "";
        }
        return phoneNumber;
    }

    /*
     * Get BBID from phone Number
     */
    public int getBBIDFromPhoneNumber(String phoneNumber) {
        int BBID = 0;
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT BBID As BBID FROM BBContacts where Phone = '" + phoneNumber + "'",
                    null);// ("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE + "
            // where Phone = '" +phoneNumber + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                BBID = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("BBID")));
            }

        } catch (Exception e) {
            e.getMessage();
            BBID = 0;
        }
        return BBID;
    }

    // Get BBID from Phone Number & Country Code
    public int getBBIDFromPhoneNumberAndCountryCode(String phoneNumber, String CountryCode) {
        int BBID = 0;
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT MAX(BBID) As BBID FROM BBContacts where Phone = '" + phoneNumber + "'"
                    + " AND countryCode = " + "'" + CountryCode + "'", null);

            /**
             *  mCursor = sqliteDatabase.rawQuery("SELECT BBID As BBID FROM BBContacts where Phone = '" + phoneNumber + "'"
             + " AND countryCode = " + "'" + CountryCode + "'", null);
             */

            if (mCursor != null) {
                mCursor.moveToFirst();
                BBID = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("BBID")));
            }

        } catch (Exception e) {
            e.getMessage();
            BBID = 0;
        }
        return BBID;
    }

    // Get BBID from Phone Number & Country Code
    public boolean checkBBIDExistence(int BBID) {
        boolean isBBIDExist = false;
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT BBID As BBID FROM BBContacts where BBID = '" + BBID + "'"
                    , null);

            if (mCursor != null) {
                mCursor.moveToFirst();
                if(mCursor.getCount() > 0){
                    isBBIDExist = true;
                }
            }

        } catch (Exception e) {
            e.getMessage();
            isBBIDExist = false;
        }
        return isBBIDExist;
    }


    // -------UPDATE BBContacts-------------------------------
    public void updateBBContactImage(int BBID, String imagePath) {

        try {
            Cursor mCursor = null;
            mCursor = sqliteDatabase
                    .rawQuery("UPDATE BBContacts SET Image=" + "'" + imagePath + "'" + " where BBID = '" + BBID + "'", null);
            if (mCursor != null && mCursor.getCount()>0) {
                mCursor.moveToFirst();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


	 /*
	  * Get Cursor for Big Button Tiles
	  */

    public Cursor getCursorAllChatstastyTilesFromCategory(String mCategory, String mMode) {
        Cursor mCursor = null;
        try {
            try {
                if (mMode.equalsIgnoreCase("call")) { // in case of call mode
                    if (mCategory.trim().equalsIgnoreCase(GlobalCommonValues.ButtonTypeAll)) {
                        mCursor = sqliteDatabase.rawQuery("Select * From Tiles ORDER BY TilePosition ASC",
                                null);
                    } else { // fetch filtered records based on category
                       /* mCursor = sqliteDatabase.rawQuery("Select * From Tiles where " + BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE + "=" + mCategory + " ORDER BY TilePosition ASC",
                                null);*/
                        mCursor = sqliteDatabase.rawQuery("Select * From Tiles where " + BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE + " like \'%" + mCategory + "%\' ORDER BY TilePosition ASC",
                                null);
                    }
                } else if (mMode.equalsIgnoreCase("message")) { // in case of message mode
                    if (mCategory.trim().equalsIgnoreCase(GlobalCommonValues.ButtonTypeAll)) {
                        mCursor = sqliteDatabase.rawQuery("Select * From Tiles where " + BIG_BUTTON_TILE_IS_TNC_USER + "=1 ORDER BY TilePosition ASC", null);
                    } else {
//                        mCursor = sqliteDatabase.rawQuery("Select * From Tiles where " + BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE + "=" + mCategory + " AND " + BIG_BUTTON_TILE_IS_TNC_USER + "=1 ORDER BY TilePosition ASC", null);
                        mCursor = sqliteDatabase.rawQuery("Select * From Tiles where " + BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE + " like \'%" + mCategory + "%\' AND " + BIG_BUTTON_TILE_IS_TNC_USER + "=1 ORDER BY TilePosition ASC", null);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor && mCursor.getCount()>0) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    //Get tile deatils based on category - End

    // Get All country Details
    public ArrayList<CountryDetailsBean> getAllCountryDetails() {
        ArrayList<CountryDetailsBean> listCountries = new ArrayList<CountryDetailsBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM CountryCodes ORDER BY CountryName COLLATE NOCASE ASC",
                    null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    CountryDetailsBean applicationClassInfo = new CountryDetailsBean();
                    applicationClassInfo
                            .setCountryName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_NAME)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_CODE)));
                    applicationClassInfo
                            .setIDDCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_IDD_CODE)));
                    applicationClassInfo
                            .setEmergency(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_EMERGENCY)));
                    listCountries.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }

        if(sqliteDatabase!=null && sqliteDatabase.isOpen()){
            sqliteDatabase.close();
        }

        return listCountries;
    }


    public ArrayList<Integer> getBBIDforCategory(String mCategoryIds) {
        ArrayList<Integer> BBIDList = new ArrayList<>();
        Cursor mCursor = null;
        try {
            String queryStr = "SELECT BBID FROM Tiles  ";
            String whereClause1 = "ButtonType IN (" + mCategoryIds  + ") AND ";
            String whereClause2 = "IsTncUser=1";
            String finalQuery ;
            if(mCategoryIds.equalsIgnoreCase("1")){
                finalQuery =  whereClause2;
            }else{
                finalQuery =  whereClause1+whereClause2;
            }
            mCursor =  sqliteDatabase.query("Tiles",new String[]{"BBID"},finalQuery,null,null,null,null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    BBIDList.add(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("BBID"))));
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return BBIDList;
    }

    // Insert All countries emergency numbers
    public long insertAllCountryEmergencyNumbers(ArrayList<CountryDetailsBean> listEmergencyNumbers) {
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listEmergencyNumbers.size(); i++) {
                initialValues.put(BIG_BUTTON_COUNTRY_NAME, listEmergencyNumbers.get(i).CountryName);
                initialValues.put(BIG_BUTTON_COUNTRY_EMERGENCY, (listEmergencyNumbers.get(i).Emergency));
                rowID = sqliteDatabase.insert(BIG_BUTTON_EMERGENCYNUMBERS_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Get All countries emergency numbers
    public ArrayList<CountryDetailsBean> getAllCountryEmergencyNumbers() {
        ArrayList<CountryDetailsBean> listCountries = new ArrayList<CountryDetailsBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM EmergencyNumbers WHERE Emergency <> '" + "?"
                    + "' GROUP BY CountryName ORDER BY CountryName COLLATE NOCASE ASC", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    CountryDetailsBean applicationClassInfo = new CountryDetailsBean();
                    applicationClassInfo
                            .setCountryName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_NAME)));
                    applicationClassInfo
                            .setEmergency(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_EMERGENCY)));
                    listCountries.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listCountries;
    }

    // Get All countries emergency numbers----------
    public ArrayList<CountryDetailsBean> getAllCountryCodes() {
        ArrayList<CountryDetailsBean> listCountries = new ArrayList<CountryDetailsBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM CountryCodes WHERE CountryCode <> '" + "?"
                    + "' GROUP BY CountryName ORDER BY CountryName COLLATE NOCASE ASC", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    CountryDetailsBean applicationClassInfo = new CountryDetailsBean();
                    applicationClassInfo
                            .setCountryName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_NAME)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_CODE)));
                    listCountries.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listCountries;
    }

    // Get flag name from country name
    public String getCountryFlag(String countryName) {
        String flagIcon = "";
        Cursor mCursor = null;
        try {   //ZIMBABWE
            mCursor = sqliteDatabase
                    .rawQuery("SELECT flag FROM CountryCodes WHERE CountryName = '" + countryName.trim() + "'", null);
            if (mCursor != null) {
                {
                    mCursor.moveToFirst();
                    flagIcon = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_FLAG));
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return flagIcon;
    }

    // Get emergency number from country name
    public String getEmergency(String countryName) {
        String emergencyNumber = "";
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase
                    .rawQuery("SELECT Emergency FROM CountryCodes WHERE CountryName = '" + countryName + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                emergencyNumber = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_EMERGENCY));
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return emergencyNumber;
    }


    // Insert All Clipart Images
    public long insertAllClipartImages(Context mContext,
                                       ArrayList<String> listClipArts) {
        SharedPreference saveState = new SharedPreference();
        saveState.setIS_INITIAL_CLIPARTS(mContext, false);
        deleteTable("Cliparts", "", null);
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listClipArts.size(); i++) {
                initialValues.put(BIG_BUTTON_CLIPARTS_IMAGE, listClipArts.get(i));
                //				initialValues.put(BIG_BUTTON_COUNTRY_EMERGENCY, (listEmergencyNumbers.get(i).Emergency));
                rowID = sqliteDatabase.insert(BIG_BUTTON_CLIPARTS_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Get All country Details
    public ArrayList<ClipArtBean> getAllClipArts(Context mContext) {
        ArrayList<ClipArtBean> listClipArts = new ArrayList<ClipArtBean>();
        Cursor mCursor = null;
        SharedPreference saveState = new SharedPreference();
        try {
            if (saveState.getIS_INITIAL_CLIPARTS(mContext)) {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_CLIPARTS_TABLE + " ORDER BY category COLLATE NOCASE ASC", null);
            } else {
                mCursor = sqliteDatabase.rawQuery("SELECT image FROM Cliparts", null);
            }
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    ClipArtBean applicationClassInfo = new ClipArtBean();
                    applicationClassInfo
                            .setClipArtName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CLIPARTS_IMAGE)));
                    listClipArts.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        return listClipArts;
    }

    // Insert All Categories
    public long insertAllCategories(Context mContext,
                                    ArrayList<CategoryBean> listCategories) {
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < listCategories.size(); i++) {
                initialValues.put(BIG_BUTTON_CATEGORY_ID, listCategories.get(i).getCategoryID());
                initialValues.put(BIG_BUTTON_CATEGORY_NAME, listCategories.get(i).getCategoryName());
                rowID = sqliteDatabase.insert(BIG_BUTTON_CATEGORY_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Get All country Details
    public ArrayList<CategoryBean> getAllCategories(Context mContext) {
        ArrayList<CategoryBean> listCategories = new ArrayList<CategoryBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_CATEGORY_TABLE + " ORDER BY " + BIG_BUTTON_CATEGORY_ID + " COLLATE NOCASE ASC", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    CategoryBean applicationClassInfo = new CategoryBean();

                    applicationClassInfo
                            .setCategoryID(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CATEGORY_ID)));
                    applicationClassInfo
                            .setCategoryName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CATEGORY_NAME)));

                    listCategories.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        return listCategories;
    }

    /*
     * Get Tile from phone number
     */
    public ArrayList<ContactTilesBean> getTileFromPhoneNumber(String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery(
                    "SELECT * FROM " + BIG_BUTTON_TILES_TABLE + " where PhoneNumber = '" + phoneNumber + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    ContactTilesBean applicationClassInfo = new ContactTilesBean();
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NUMBER)));
                    applicationClassInfo.setImage(mCursor.getBlob(mCursor.getColumnIndex(BIG_BUTTON_TILE_IMAGE)));
                    applicationClassInfo.setPrefix(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_PREFIX)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_COUNTRY_CODE)));
                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)).equals("1")) {
                        applicationClassInfo.setIsEmergency(true);
                    } else {
                        applicationClassInfo.setIsEmergency(false);
                    }
                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

					 /*
					  * applicationClassInfo.setIsEmergency(Boolean.parseBoolean(
					  * mCursor.getString(
					  * mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY))));
					  */
                    applicationClassInfo
                            .setTilePosition(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
                    if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_IMAGE_PENDING)) == 1) {
                        applicationClassInfo.setIsImagePending(1);
                    } else {
                        applicationClassInfo.setIsImagePending(0);
                    }
                    listContactTiles.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listContactTiles;
    }

    public ArrayList<ContactTilesBean> getTileFromPhoneNumberUpdateButton(String countryCodeRegisteredUser,
                                                                          String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_TILES_TABLE + " where PhoneNumber = '"
                    + phoneNumber + "'" + " AND CountryCode = '" + countryCodeRegisteredUser + "'" + "", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    ContactTilesBean applicationClassInfo = new ContactTilesBean();
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NUMBER)));
                    applicationClassInfo.setImage(mCursor.getBlob(mCursor.getColumnIndex(BIG_BUTTON_TILE_IMAGE)));
                    applicationClassInfo.setPrefix(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_PREFIX)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_COUNTRY_CODE)));
                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)).equals("1")) {
                        applicationClassInfo.setIsEmergency(true);
                    } else {
                        applicationClassInfo.setIsEmergency(false);
                    }
                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

					 /*
					  * applicationClassInfo.setIsEmergency(Boolean.parseBoolean(
					  * mCursor.getString(
					  * mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY))));
					  */
                    applicationClassInfo
                            .setTilePosition(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
                    if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_IMAGE_PENDING)) == 1) {
                        applicationClassInfo.setIsImagePending(1);
                    } else {
                        applicationClassInfo.setIsImagePending(0);
                    }
                    listContactTiles.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listContactTiles;
    }

    /*
     * Get Tile from PhoneNumber & Prefix
     */
    public ArrayList<ContactTilesBean> getTileFromPhoneNumberPrefix(String prefix, String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT PhoneNumber,Prefix FROM Tiles where PhoneNumber = '" + phoneNumber
                    + "'" + " AND Prefix = '" + prefix + "'" + "", null);
            // sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_TILES_TABLE
            // + " where PhoneNumber = '" +phoneNumber + "'" + " AND Prefix = '"
            // +prefix + "'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    ContactTilesBean applicationClassInfo = new ContactTilesBean();
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NAME)));
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NUMBER)));
                    applicationClassInfo.setImage(mCursor.getBlob(mCursor.getColumnIndex(BIG_BUTTON_TILE_IMAGE)));
                    applicationClassInfo.setPrefix(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_PREFIX)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_COUNTRY_CODE)));
                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)).equals("1")) {
                        applicationClassInfo.setIsEmergency(true);
                    } else {
                        applicationClassInfo.setIsEmergency(false);
                    }
                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

                    applicationClassInfo
                            .setTilePosition(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
                    if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_IMAGE_PENDING)) == 1) {
                        applicationClassInfo.setIsImagePending(1);
                    } else {
                        applicationClassInfo.setIsImagePending(0);
                    }
                    listContactTiles.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listContactTiles;
    }

    // Delete Tile
    public int deleteTile(String Prefix, String PhoneNumber, int TilePosition) {
        int count = -1;
        String WHERE = "TilePosition = ?";// "Prefix = ? AND PhoneNumber=?";
        String[] WHERE_ARGS = new String[]{String.valueOf(TilePosition)};
        // String WHERE2 = "from_user_id = ? AND to_user_id = ?";
        // String[] WHERE_ARGS2 = new String[] {
        // String.valueOf(to_user_id_delete),String.valueOf(from_user_id_delete)
        // };
        try {
            count = sqliteDatabase.delete(BIG_BUTTON_TILES_TABLE, WHERE, WHERE_ARGS);
            // sqliteDatabase.delete(BIG_BUTTON_TILES_TABLE, WHERE, WHERE_ARGS);
            // sqliteDatabase.delete(BIG_BUTTON_MESSAGES_TABLE, WHERE2,
            // WHERE_ARGS);
            // rawQuery("DELETE FROM " + BIG_BUTTON_MESSAGES_TABLE + " where
            // to_user_id = '" +to_user_id_delete + "'" +" AND from_user_id = '"
            // +from_user_id_delete + "'" + " OR to_user_id = '" +
            // from_user_id_delete + "'" +" AND from_user_id = '" +
            // to_user_id_delete + "'"+"", null);
            // delete(BIG_BUTTON_MESSAGES_TABLE, WHERE, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            count = -1;
        }
        return count;
    }

      /*if(mMode.equalsIgnoreCase(GlobalCommonValues.ButtonModecall)){
                // In case of call mode
                if(mCategory.equalsIgnoreCase(GlobalCommonValues.ButtonTypeAll)){
                    mCursor = sqliteDatabase.rawQuery("Select * From Tiles ORDER BY TilePosition ASC", null);
                }else{
                    mCursor = sqliteDatabase.rawQuery("Select * From Tiles where ButtonType=" + mCategory + " ORDER BY TilePosition ASC", null);
                }

            }else if(mMode.equalsIgnoreCase(GlobalCommonValues.ButtonModeMessage)){
                // In case of message mode
                if(mCategory.equalsIgnoreCase(GlobalCommonValues.ButtonTypeAll)){
                    mCursor = sqliteDatabase.rawQuery("Select * From Tiles where IsTncUser=" + 1 + " ORDER BY TilePosition ASC", null);
                }else{
                    mCursor = sqliteDatabase.rawQuery("Select * From Tiles where IsTncUser=" + 1
                            + " AND ButtonType=" + mCategory + " ORDER BY TilePosition ASC", null);
                }
            }*/

    /**
     *
     * @param mCategory
     * @param mMode
     * @return
     */

    // Get BigButton Tiles on the basis of category
    public ArrayList<ContactTilesBean> getChatstasyTilesFromCategory(String mCategory,
                                                                     String mMode){
        ArrayList<ContactTilesBean> mListTiles = new ArrayList<ContactTilesBean>();

        Cursor mCursor = getCursorAllChatstastyTilesFromCategory(mCategory, mMode);
        try {
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    ContactTilesBean applicationClassInfo = new ContactTilesBean();
                    String mName = "";
                    mName = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NAME));
                    if (mName.contains("&#39;"))
                        mName = mName.replaceAll("&#39;", "'");
                    applicationClassInfo.setName(mName);
                    applicationClassInfo
                            .setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_NUMBER)));
                    applicationClassInfo.setImage(mCursor.getBlob(mCursor.getColumnIndex(BIG_BUTTON_TILE_IMAGE)));

                    if ((mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_ISMOBILE)) == 1)) {
                        applicationClassInfo.setIsMobile(true);
                    } else {
                        applicationClassInfo.setIsMobile(false);
                    }

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)) == null) {
                        applicationClassInfo.setIsEmergency(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_EMERGENCY)).equals("1")) {
                            applicationClassInfo.setIsEmergency(true);
                        } else {
                            applicationClassInfo.setIsEmergency(false);
                        }
                    }
                    if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)) == null) {
                        applicationClassInfo.setImageLocked(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)).equals("1")) {
                            applicationClassInfo.setImageLocked(true);
                        } else {
                            applicationClassInfo.setImageLocked(false);
                        }
                        ////system.out.println("value"+mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK)));
                    }

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)) == null || mCursor
                            .getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)).equalsIgnoreCase("-1")) {
                        applicationClassInfo
                                .setTilePosition(String.valueOf(DBQuery.getMaximumTilePosition(mContext) + 1));
                    } else {
                        applicationClassInfo
                                .setTilePosition(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_POSITION)));
                    }

                    try {
                        if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_IMAGE_PENDING)) == 1) {
                            applicationClassInfo.setIsImagePending(1);
                        } else {
                            applicationClassInfo.setIsImagePending(0);
                        }
                    } catch (Exception e) {
                        applicationClassInfo.setIsImagePending(0);
                        e.getMessage();
                    }

                    try {
                        if (mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID) > 0) {
                            if (mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID)) > 0) {
                                applicationClassInfo
                                        .setBBID(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_TILE_BBID)));
                            } else {
                                applicationClassInfo.setBBID(0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        applicationClassInfo.setIsImagePending(0);
                        e.getMessage();
                    }

                    applicationClassInfo.setPrefix(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_PREFIX)));
                    applicationClassInfo
                            .setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_COUNTRY_CODE)));
                    if (mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER) > 0) {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER)) == null) {
                            applicationClassInfo.setIsTncUser(false);
                        } else {
                            if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_USER)).equals("1")) {
                                applicationClassInfo.setIsTncUser(true);
                            } else {
                                applicationClassInfo.setIsTncUser(false);
                            }
                        }
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_iS_TNC_USER)) == null) {
                            applicationClassInfo.setIsTncUser(false);
                        } else {
                            if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_iS_TNC_USER)).equals("1")) {
                                applicationClassInfo.setIsTncUser(true);
                            } else {
                                applicationClassInfo.setIsTncUser(false);
                            }
                        }
                    }

                    try {

                        applicationClassInfo.setButtonType(
                                mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_TILE_IS_TNC_BUTTON_TYPE)));
                    } catch (Exception e) {
                        e.getMessage();
                    }

                    mListTiles.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return  mListTiles;
    }

    // -------CASE_TILE_IMAGE DATA UPDATION-------------------------------
    public long updateImage_BBTile(byte[] Image, String from_user_phoneNumber) {
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            String WHERE = "PhoneNumber=?";
            String[] WHERE_ARGS = new String[]{from_user_phoneNumber};
            initialValues.put("Image", Image);
            initialValues.put("IsImagePending", 0);
            rowID = sqliteDatabase.update(BIG_BUTTON_TILES_TABLE, initialValues, WHERE, WHERE_ARGS);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return (long) rowID;
    }
    // -------CASE_TILE_IMAGE DATA UPDATION-------------------------------
    public long updateImageNotification_BBTile(byte[] Image, String from_BBID) {
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            String WHERE = "BBID=?";
            String[] WHERE_ARGS = new String[]{from_BBID};
            initialValues.put("Image", Image);
            initialValues.put("IsImagePending", 0);
            rowID = sqliteDatabase.update(BIG_BUTTON_TILES_TABLE, initialValues, WHERE, WHERE_ARGS);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return (long) rowID;
    }

	 /*
	  * Get Cursor for Big Button Contacts
	  */

    public Cursor getCursorAllBBContacts() {
        Cursor mCursor = null;
        try {
            String[] fields = new String[]{BIG_BUTTON_BBCONTACTS_MOBILEID, BIG_BUTTON_BBCONTACTS_BBID,
                    BIG_BUTTON_BBCONTACTS_NAME, BIG_BUTTON_BBCONTACTS_PHONE, BIG_BUTTON_BBCONTACTS_IMAGE,
                    BIG_BUTTON_BBCONTACTS_COUNTRYCODE};

            try {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE
                        + " where BBID>1 ", null);
                //query(BIG_BUTTON_BBCONTACTS_TABLE, fields, null, null, null, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }
	 
	 
	 /*
	  * Get Cursor for Big Button Contacts
	  */

    public Cursor getCursorAllBBContacts2() {
        Cursor mCursor = null;
        try {
            String[] fields = new String[]{BIG_BUTTON_BBCONTACTS_MOBILEID, BIG_BUTTON_BBCONTACTS_BBID,
                    BIG_BUTTON_BBCONTACTS_NAME, BIG_BUTTON_BBCONTACTS_PHONE, BIG_BUTTON_BBCONTACTS_IMAGE,
                    BIG_BUTTON_BBCONTACTS_COUNTRYCODE};

            try {
                mCursor = sqliteDatabase.query(BIG_BUTTON_BBCONTACTS_TABLE, fields, null, null, null, null, "Name COLLATE NOCASE");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }


    // Check existence of TnC User in BBContacts Table
    public boolean checkBBContactsFromNumber(String countryCode, String phoneNumber) {
        boolean isContactFound = false;
        Cursor mCursor = getCursorCheckBBContactsExistenceFromNumber(countryCode, phoneNumber);
        try {
            if (mCursor != null) {
                if (mCursor.getCount() > 0) {
                    isContactFound = true;
                } else {
                    isContactFound = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContactFound;
    }

    // Get Cursor to Check Existence of Big Button Contacts
    public Cursor getCursorCheckBBContactsExistenceFromNumber(String countryCode, String phoneNumber) {
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE
                        + " where countryCode = '" + countryCode + "'" + " AND Phone = '" + phoneNumber + "'", null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    // Check existence of Chat User in BBContacts Table
    public ArrayList<BBContactsBean> checkBBContacts(int matching_user_id) {
        Cursor mCursor = getCursorCheckBBContactsExistence(matching_user_id);
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            if (mCursor != null) {
                do {
                    BBContactsBean applicationClassInfo = new BBContactsBean();
                    applicationClassInfo.setMobID(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_MOBILEID))));
                    applicationClassInfo.setBBID(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_BBID))));
                    applicationClassInfo.setName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_NAME)));
                    applicationClassInfo.setPhoneNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_PHONE)));
                    applicationClassInfo.setImage(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_IMAGE)));
                    applicationClassInfo.setCountryCode(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_BBCONTACTS_COUNTRYCODE)));
                    list.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get Cursor to Check Existence of Big Button Contacts
    public Cursor getCursorCheckBBContactsExistence(int matching_user_id) {
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery(
                        "SELECT * FROM " + BIG_BUTTON_BBCONTACTS_TABLE + " where BBID = '" + matching_user_id + "'",
                        null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

	 /*
	  * Extract idd Code from Number from the database
	  */

    public String getIDDCodeFromNumber(String countryCode) {
        String resultCountryCode = "";
        Cursor mCursor = getCursorIDDCodeFromNumber(countryCode);// ContactDetailsRegisteredBean
		 /*
		  * ArrayList<CountryCodeBean> list = new ArrayList<CountryCodeBean>();
		  * try { if (mCursor != null) { do { CountryCodeBean
		  * applicationClassInfo = new CountryCodeBean();
		  * applicationClassInfo.setCountryName(mCursor.getString(mCursor.
		  * getColumnIndex(BIG_BUTTON_COUNTRY_NAME)));
		  * applicationClassInfo.setCountryCode(mCursor.getString(mCursor.
		  * getColumnIndex(BIG_BUTTON_COUNTRY_CODE)));
		  * applicationClassInfo.setIDDCode(mCursor.getString(mCursor.
		  * getColumnIndex(BIG_BUTTON_COUNTRY_IDD_CODE)));
		  * applicationClassInfo.setEmergency(mCursor.getString(mCursor.
		  * getColumnIndex(BIG_BUTTON_COUNTRY_EMERGENCY)));
		  * applicationClassInfo.setMessageMultiplier(mCursor.getString(mCursor.
		  * getColumnIndex(BIG_BUTTON_COUNTRY_MESSAGEMULTIPLIER)));
		  * list.add(applicationClassInfo); } while (mCursor.moveToNext()); } }
		  */
        try {
            if (mCursor != null) {
                resultCountryCode = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_IDD_CODE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultCountryCode;
    }
    public String getIsImageLockStatusFromBBID(String BBID) {
        String resultBBID = "";
        Cursor mCursor = getIsImageLockStatus(BBID);// ContactDetailsRegisteredBean
        try {
            if (mCursor != null) {
                resultBBID = mCursor.getString(mCursor.getColumnIndex(DBConstant.TILE_COLUMN_IMAGE_LOCK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBBID;
    }

	 /*
	  * Get Cursor for Init/Response Messages
	  */

    public Cursor getCursorIDDCodeFromNumber(String countryCode) {
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT IDDCode FROM " + BIG_BUTTON_COUNTRY_CODES_TABLE
                    + " where CountryCode = '" + countryCode + "'", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != mCursor) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getIsImageLockStatus(String BBID) {
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT "+ DBConstant.TILE_COLUMN_IMAGE_LOCK+" FROM " + DBConstant.TILE_TABLE
                    + " where BBID = '" + BBID + "'", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != mCursor) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public String getCountryNameFromCountryCode(String countryCode) {
        String resultCountryCode = "";
        Cursor mCursor = null;

        try {
            mCursor = sqliteDatabase.rawQuery("SELECT CountryName FROM " + BIG_BUTTON_COUNTRY_CODES_TABLE
                    + " where CountryCode = '" + countryCode + "'", null);// ContactDetailsRegisteredBean
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        try {
            if (mCursor != null) {
                resultCountryCode = mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_COUNTRY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultCountryCode;
    }

    // -------How To... DATA ADDITION-------------------------------
    public long addHowTo(ArrayList<HowtoReponseDataBean> responseListdata) {
        if (responseListdata == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < responseListdata.size(); i++) {
                initialValues.put(BIG_BUTTON_HOWTO_QUESTION, responseListdata.get(i).question);
                initialValues.put(BIG_BUTTON_HOWTO_ANSWER, (responseListdata.get(i).answer));
                initialValues.put(BIG_BUTTON_HOWTO_VERSION, (responseListdata.get(i).version));
                initialValues.put(BIG_BUTTON_HOWTO_ADD_DATE, responseListdata.get(i).add_date);// Integer.parseInt(notificationListdata.get(i).type)
                rowID = sqliteDatabase.insert(BIG_BUTTON_HOW_TO_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Get All How To
    public ArrayList<HowtoReponseDataBean> getAllHowTo() {
        ArrayList<HowtoReponseDataBean> listHowTo = new ArrayList<HowtoReponseDataBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM HowTo", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                do {
                    HowtoReponseDataBean applicationClassInfo = new HowtoReponseDataBean();
                    applicationClassInfo
                            .setQuestion(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_HOWTO_QUESTION)));
                    applicationClassInfo.setAnswer(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_HOWTO_ANSWER)));
                    applicationClassInfo
                            .setVersion(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_HOWTO_VERSION)));
                    applicationClassInfo
                            .setAdd_date(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_HOWTO_ADD_DATE)));
                    listHowTo.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listHowTo;
    }

    // -------FAQ DATA ADDITION-------------------------------
    public long addFAQ(ArrayList<HowtoReponseDataBean> responseListdata) {
        if (responseListdata == null)
            return 0l;
        long rowID = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < responseListdata.size(); i++) {
                initialValues.put(BIG_BUTTON_FAQ_QUESTION, responseListdata.get(i).question);
                initialValues.put(BIG_BUTTON_FAQ_ANSWER, (responseListdata.get(i).answer));
                initialValues.put(BIG_BUTTON_FAQ_VERSION, (responseListdata.get(i).version));
                initialValues.put(BIG_BUTTON_FAQ_ADD_DATE, responseListdata.get(i).add_date);// Integer.parseInt(notificationListdata.get(i).type)
                rowID = sqliteDatabase.insert(BIG_BUTTON_FAQ_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }

    // Get All FAQ
    public ArrayList<HowtoReponseDataBean> getAllFAQ() {
        ArrayList<HowtoReponseDataBean> listFAQ = new ArrayList<HowtoReponseDataBean>();
        Cursor mCursor = null;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT * FROM Faq", null);
            if (mCursor != null && mCursor.getCount()>0) {
                mCursor.moveToFirst();
                do {
                    HowtoReponseDataBean applicationClassInfo = new HowtoReponseDataBean();
                    applicationClassInfo
                            .setQuestion(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_FAQ_QUESTION)));
                    applicationClassInfo.setAnswer(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_FAQ_ANSWER)));
                    applicationClassInfo.setVersion(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_FAQ_VERSION)));
                    applicationClassInfo
                            .setAdd_date(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_FAQ_ADD_DATE)));
                    listFAQ.add(applicationClassInfo);
                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return listFAQ;
    }

	 /*
	  * get Config details from database
	  */
	 /*
	  * public ArrayList<ConfigurationBean> getConfigDetails() { Cursor mCursor =
	  * getCursorAllConfigDetails();//ContactDetailsRegisteredBean
	  * ArrayList<ConfigurationBean> list = new ArrayList<ConfigurationBean>();
	  * try { if (mCursor != null) { do { ConfigurationBean applicationClassInfo
	  * = new ConfigurationBean();
	  * applicationClassInfo.setIntroWatched(Boolean.parseBoolean(mCursor.
	  * getString(
	  * mCursor.getColumnIndex(BIG_BUTTON_CONFIG_ISINTROVDEOWATCHED))));
	  * applicationClassInfo.setRegisteredUser(Boolean.parseBoolean(mCursor.
	  * getString( mCursor.getColumnIndex(BIG_BUTTON_CONFIG_ISREGISTEREDUSER))));
	  * applicationClassInfo.setRegisterForBackup(Boolean.parseBoolean(mCursor.
	  * getString(mCursor.getColumnIndex(BIG_BUTTON_CONFIG_ISREGISTERFORBACKUP)))
	  * );
	  * applicationClassInfo.setDBChanged(Boolean.parseBoolean(mCursor.getString(
	  * mCursor.getColumnIndex(BIG_BUTTON_CONFIG_ISDBCHANGED))));
	  * applicationClassInfo.setText(mCursor.getString(mCursor
	  * .getColumnIndex(BIG_BUTTON_CONFIG_DBVersion))); //
	  * list.add(applicationClassInfo); } while (mCursor.moveToNext()); } } catch
	  * (Exception e) { e.printStackTrace(); }
	  * 
	  * return list; }
	  */
	 /*
	  * Get Cursor for Big Button Config Details
	  */

	 /*
	  * public Cursor getCursorAllConfigDetails() { Cursor mCursor = null; try {
	  * String[] fields = new String[] { BIG_BUTTON_CONFIG_ISINTROVDEOWATCHED,
	  * BIG_BUTTON_CONFIG_ISREGISTEREDUSER,BIG_BUTTON_CONFIG_ISREGISTERFORBACKUP,
	  * BIG_BUTTON_CONFIG_ISDBCHANGED,BIG_BUTTON_CONFIG_DBVersion};
	  * 
	  * try { mCursor = sqliteDatabase.query(BIG_BUTTON_CONFIG_TABLE, fields,
	  * null, null, null, null, null); } catch (Exception ex) {
	  * ex.printStackTrace(); } if (null != mCursor) { mCursor.moveToFirst(); } }
	  * catch (Exception e) { e.printStackTrace(); } return mCursor; }
	  */

	 /*
	  * get MyBackup details from database
	  * 
	  * @SuppressWarnings("deprecation") public ArrayList<MyBackupBean>
	  * getMyBackupDetails() { Cursor mCursor =
	  * getCursorAllMyBackupDetails();//ContactDetailsRegisteredBean
	  * ArrayList<MyBackupBean> list = new ArrayList<MyBackupBean>(); try { if
	  * (mCursor != null) { do { MyBackupBean applicationClassInfo = new
	  * MyBackupBean(); applicationClassInfo.setDate(new
	  * Date(mCursor.getString(mCursor
	  * .getColumnIndex(BIG_BUTTON_MYBACKUP_DATE))));
	  * applicationClassInfo.setBackupID(Integer.parseInt(mCursor.getString(
	  * mCursor .getColumnIndex(BIG_BUTTON_MYBACKUP_ID)))); //
	  * list.add(applicationClassInfo); } while (mCursor.moveToNext()); } } catch
	  * (Exception e) { e.printStackTrace(); }
	  * 
	  * return list; }
	  */
	 /*
	  * Get Cursor for Big Button MyBackup Details
	  */

	 /*
	  * public Cursor getCursorAllMyBackupDetails() { Cursor mCursor = null; try
	  * { String[] fields = new String[] { BIG_BUTTON_CONFIG_ISINTROVDEOWATCHED,
	  * BIG_BUTTON_CONFIG_ISREGISTEREDUSER,BIG_BUTTON_CONFIG_ISREGISTERFORBACKUP,
	  * BIG_BUTTON_CONFIG_ISDBCHANGED,BIG_BUTTON_CONFIG_DBVersion};
	  * 
	  * try { mCursor = sqliteDatabase.query(BIG_BUTTON_MYBACKUP_TABLE, fields,
	  * null, null, null, null, null); } catch (Exception ex) {
	  * ex.printStackTrace(); } if (null != mCursor) { mCursor.moveToFirst(); } }
	  * catch (Exception e) { e.printStackTrace(); } return mCursor; }
	  */

    public void addPhoneContact(long rowID, ContentValues initialValues) {
        if (rowID != -1 && MainBaseActivity.isContactCreated) {
            //system.out.println(MainBaseActivity.isContactCreated + "1--------");
            ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();
            int rawContactID = listOperations.size();

            String strPhone = GlobalConfig_Methods
                    .trimSpecialPhoneNumberToDisplay(String.valueOf(initialValues.get("PhoneNumber")));
            String contactNumber = "";
            // phase-4 Comment

            String prefix = "", countryCode = "";

            if (MainBaseActivity.selectedPrefixCodeForTileDetails != null
                    && !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(""))
                prefix = MainBaseActivity.selectedPrefixCodeForTileDetails;
            if (MainBaseActivity.selectedCountryCodeForTileDetails != null
                    && !MainBaseActivity.selectedCountryCodeForTileDetails.trim().equals(""))
                countryCode = MainBaseActivity.selectedCountryCodeForTileDetails;
            contactNumber = prefix + countryCode + strPhone;
			 /*
			  * if(MainBaseActivity.selectedPrefixCodeForTileDetails!=null &&
			  * !MainBaseActivity.selectedPrefixCodeForTileDetails.trim().equals(
			  * "")) {
			  * contactNumber=MainBaseActivity.selectedPrefixCodeForTileDetails+
			  * strPhone; } else{ contactNumber=strPhone; }
			  */
            // Adding insert operation to operations list
            // to insert a new raw contact in the table
            // ContactsContract.RawContacts
            listOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null).build());

            // Adding insert operation to operations list
            // to insert display name in the table ContactsContract.Data
            listOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, initialValues.get("Name")).build());

            // Adding insert operation to operations list
            // to insert Mobile Number in the table ContactsContract.Data
            listOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, contactNumber).withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
            try {
                // Executing all the insert operations as a single database
                // transaction
                mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, listOperations);
                // Toast.makeText(mContext, "Contact is successfully added",
                // Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                //system.out.println(e.getMessage() + "--------");
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                //system.out.println(e.getMessage() + "--------");
                e.printStackTrace();
            }
            // commented as refresh button is added to refresh the list
			 /*
			  * if(mContext instanceof MainBaseActivity) {
			  * ((MainBaseActivity)mContext).startService(new
			  * Intent(mContext,GetContactService.class)); } else if(mContext
			  * instanceof HomeScreenActivity) {
			  * ((HomeScreenActivity)mContext).startService(new
			  * Intent(mContext,GetContactService.class)); }
			  */
            //system.out.println(MainBaseActivity.isContactCreated + "2--------");
            MainBaseActivity.isContactCreated = false;
        }
    }

    /**
     * Method to create Cliparts Table
     */
    public void createClipartsTable() {
        String query = "CREATE TABLE IF NOT EXISTS Cliparts(id double,category double,clipart_order double,name VARCHAR,image VARCHAR,version VARCHAR,add_date datetime);";
        try {
            sqliteDatabase.execSQL(query);
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * Method to create Category Table
     */
    public void createCategoryTable() {
        String query = "CREATE TABLE IF NOT EXISTS Category(CategoryID VARCHAR,CategoryName VARCHAR);";
        try {
            sqliteDatabase.execSQL(query);

            ArrayList<CategoryBean> mListCategories = new ArrayList<CategoryBean>();
            CategoryBean mCategoryBean;
            for (int i = 0; i < 4; i++) {
                // create an object of each category value
                mCategoryBean = new CategoryBean();
                mCategoryBean.setCategoryID(String.valueOf(i));
                if(i == 0)
                    mCategoryBean.setCategoryName("All");
                else if(i == 1)
                    mCategoryBean.setCategoryName("Friend");
                else if(i == 2)
                    mCategoryBean.setCategoryName("Family");
                else if(i == 3)
                    mCategoryBean.setCategoryName("Business");

                // add an object of category value in ArrayList
                mListCategories.add(mCategoryBean);
            }

            try{
                insertAllCategories(mContext, mListCategories);
            }catch(Exception e){
                e.getMessage();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // //insert Call Log
    public long insertCallDetails(Context context, CallDetailsBean mCallDetailsBean) {
        if (mCallDetailsBean == null)
            return 0l;
        long rowID = -1;

        try {
            ContentValues initialValues = new ContentValues();

            if(mCallDetailsBean!=null){

                if (sqliteDatabase != null && !sqliteDatabase.isOpen()) {
                    open();
                }

                initialValues.put(BIG_BUTTON_CALL_NAME, mCallDetailsBean.getCallName());

                initialValues.put(BIG_BUTTON_CALL_PREFIX, "");
                initialValues.put(BIG_BUTTON_CALL_COUNTRY_CODE, "");
                initialValues.put(BIG_BUTTON_CALL_NUMBER, mCallDetailsBean.getCallingNumber());

                initialValues.put(BIG_BUTTON_CALL_TIME, mCallDetailsBean.getCallTime());
                initialValues.put(BIG_BUTTON_CALL_TYPE, mCallDetailsBean.getCallType());
                initialValues.put(BIG_BUTTON_CALL_STATUS, mCallDetailsBean.getStatus());
                initialValues.put(BIG_BUTTON_IS_EMERGENCY_CALL, mCallDetailsBean.isEmergencyCall());
                rowID = sqliteDatabase.insert(BIG_BUTTON_CALL_DETAILS_TABLE, null, initialValues);
            }

        } catch (Exception e) {
            e.getMessage();
        }
        return (long) rowID;
    }


    /**
     * get BBID of TnC Users From the DB
     */
    public String getBBIDForEmergencyNotification(){

        Cursor mCursor = null;
        String mBBIDs = "";
        String query = "SELECT BBID AS BBID FROM Tiles where IsTncUser=1 AND IsEmergency=1";
        try{
            mCursor = sqliteDatabase.rawQuery(query, null);

            if(mCursor!=null){
                mCursor.moveToFirst();
                if(mCursor.getCount() == 1){
                    // if there is only one record
                    mBBIDs = mCursor.getString(mCursor.getColumnIndex("BBID"));
                }else{
                    // iterate through all the records
                    do{
                        mBBIDs+= mCursor.getString(mCursor.getColumnIndex("BBID")) + ",";
                    }while(mCursor.moveToNext());

                    if(String.valueOf(mBBIDs.charAt(mBBIDs.length()-1)).equalsIgnoreCase(",")){
                        mBBIDs = mBBIDs.substring(0, mBBIDs.length()-1);
                        //(mBBIDs.substring(0,mBBIDs.length()-1)).
                    }
                }
            }

        }catch(Exception e){
            e.getMessage();
        }
        return mBBIDs;
    }


    /*
     * Get Contact Tile Name From BBID
     */
    public String getContactTileNameFromBBID(int BBID, boolean isFromTileTable) {
        String mUserName = "";
        Cursor mCursor = null;
        try {
            if(isFromTileTable){
                // in case of tiles table
                mCursor = sqliteDatabase.rawQuery("SELECT Name As Name FROM Tiles where BBID = '" + BBID + "'",
                        null);
            }else{
                // in case of BBContacts table
                mCursor = sqliteDatabase.rawQuery("SELECT Name As Name FROM BBContacts where BBID = '" + BBID + "'",
                        null);
            }

            if (mCursor != null) {
                mCursor.moveToFirst();
                if(mCursor.getCount() > 0)
                    mUserName = mCursor.getString(mCursor.getColumnIndex("Name"));
            }
        } catch (Exception e) {
            e.getMessage();
            mUserName = "";
        }
        return mUserName;
    }


    /**
     *  get the list of emergency calls on the basis of type
     *  1 - incoming, 2 - outgoing, 3 - missed
     */

    public ArrayList<CallDetailsBean> getcallLogsFromType(int callType){

        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();

        Cursor mCursor = null;
        CallDetailsBean mCallDetailsBean = null;
        String mBBIDs = "";
        //String query = "SELECT * FROM " + BIG_BUTTON_CALL_DETAILS_TABLE + " ORDER BY Status,CallTime DESC"; // +"where CallType=" + callType;

//        String queryForEmergency    = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , CallTime , CallType , IsEmergency, Status,count(*) as CountRecords FROM CallDetails where CallType =" + callType + " AND IsEmergency =1 group by CallingNumber ORDER BY Status,CallTime DESC;";
//
//        String queryForNonEmergency = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , CallTime , CallType , IsEmergency, Status,count(*) as CountRecords FROM CallDetails where CallType =" + callType + " AND IsEmergency <>1 group by CallingNumber ORDER BY Status,CallTime DESC;";

        /*String query = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , CallTime , CallType , IsEmergency, count(*) as CountRecords,MAX(IsEmergency) as IsEmergencyCount, MIN(Status) as Status FROM CallDetails where CallType =" + callType + " group by CallingNumber ORDER BY Status,CallTime DESC;";*/

        String query = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , Max(CallTime) AS CallTime , CallType , IsEmergency, count(*) as CountRecords,MAX(IsEmergency) as IsEmergencyCount, MIN(Status) as Status FROM CallDetails where CallType =" + callType + " group by CallingNumber ORDER BY Status,CallTime DESC;";

        /*if(callType == 3){ // in case of missed call
            query = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , CallTime , CallType , IsEmergency, count(*) as CountRecords,MAX(IsEmergency) as IsEmergencyCount, MIN(Status) as Status FROM CallDetails where CallType =" + callType + " group by CallingNumber ORDER BY Status,CallTime DESC;";
        }else{
            query = "SELECT CallName , CallingPrefix , CallingCountryCode , CallingNumber , CallTime , CallType , IsEmergency, count(*) as CountRecords,MAX(IsEmergency) as IsEmergencyCount, MIN(Status) as Status FROM CallDetails where CallType =" + callType + " group by CallingNumber ORDER BY Status,CallTime DESC;";
        }*/

        try{
            /*mCursor = sqliteDatabase.rawQuery(queryForEmergency, null);

            if(mCursor!=null && mCursor.getCount() > 0){
                // Append more results here
                mCursor = sqliteDatabase.rawQuery(queryForNonEmergency, null);
            }else if(mCursor ==null || mCursor.getCount() < 1){
                // add results in cursor from here
                mCursor = sqliteDatabase.rawQuery(query, null);
            }*/

            mCursor = sqliteDatabase.rawQuery(query, null);

            if(mCursor!=null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                do{
                    mCallDetailsBean = new CallDetailsBean();
                    mCallDetailsBean.setCallName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NAME)));
                    mCallDetailsBean.setPrefix("");
                    mCallDetailsBean.setCountryCode("");
                    mCallDetailsBean.setCallingNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NUMBER)));
                    mCallDetailsBean.setCallTime(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_TIME)));
                    mCallDetailsBean.setCallType(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_TYPE)));
                    mCallDetailsBean.setStatus(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_STATUS)));
                    try{
                        mCallDetailsBean.setCallCount(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_COUNT_RECORDS)));
                    }catch(Exception e){
                        e.getMessage();
                    }

                    if (mCursor.getString(mCursor.getColumnIndex("IsEmergencyCount")) == null) {
                        mCallDetailsBean.setEmergencyCall(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex("IsEmergencyCount")).equals("1")) {
                            mCallDetailsBean.setEmergencyCall(true);
                        } else {
                            mCallDetailsBean.setEmergencyCall(false);
                        }
                    }

                    mListCallDetails.add(mCallDetailsBean);
                }while (mCursor.moveToNext()); // iterate through all the records
            }
        }catch(Exception e){
            e.getMessage();
        }
        return mListCallDetails;
    }

    /**
     * Method to get unread call log count
     * @param mPhoneNumber
     * @return
     */
    public int getUnreadCallCount(String mPhoneNumber) {
        int size = -1;
        Cursor mCursor = null;
        if(mPhoneNumber == null || mPhoneNumber.trim().equalsIgnoreCase("")){
            // In case of fetching all the unread call logs
            mCursor = sqliteDatabase
                    .rawQuery("Select Count(CallingNumber) AS Count from CallDetails where Status=1", null);

        }else {
            if(mPhoneNumber.equalsIgnoreCase("3")){
                // In case of fetching count of missed calls from the call logs
                mCursor = sqliteDatabase
                        .rawQuery("Select Count(CallingNumber) AS Count from CallDetails where Status=1 AND CallType = 3" , null);
            }else{
                // In case of fetching all the unread call logs of specific number
                mCursor = sqliteDatabase
                        .rawQuery("Select Count(CallingNumber) AS Count from CallDetails where Status=1 AND CallingNumber LIKE " + "\'" + "%" + mPhoneNumber + "%" + "\'", null);
            }
        }

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        }
        return size;
    }

    // -------UPDATE CALL LOG STATUS-------------------------------
    public void updateCallLogStatus(String mPhoneNumber) {

        mPhoneNumber = GlobalConfig_Methods.trimSpecialCharactersFromString(mPhoneNumber);

        try { // status; 1 - unread, 2 - read
            Cursor mCursor = null;
            mCursor = sqliteDatabase
                    .rawQuery("UPDATE CallDetails SET Status=2 where CallingNumber LIKE " + "\'" + "%" + mPhoneNumber + "%" + "\'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  get the list of calls on the basis of number
     *  @param: mPhoneNumber
     */

    public ArrayList<CallDetailsBean> getCallLogsFromNumber(String mPhoneNumber){

        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();

        Cursor mCursor = null;
        CallDetailsBean mCallDetailsBean = null;
        String mBBIDs = "";

        mPhoneNumber = GlobalConfig_Methods.trimSpecialCharactersFromString(mPhoneNumber);

        String query = "SELECT * FROM CallDetails where CallingNumber LIKE" + "\'" + "%" + mPhoneNumber + "%" + "\'" + " ORDER BY CallTime DESC;";

        try{
            mCursor = sqliteDatabase.rawQuery(query, null);

            if(mCursor!=null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                do{
                    mCallDetailsBean = new CallDetailsBean();
                    mCallDetailsBean.setCallName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NAME)));
                    mCallDetailsBean.setPrefix("");
                    mCallDetailsBean.setCountryCode("");
                    mCallDetailsBean.setCallingNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NUMBER)));
                    mCallDetailsBean.setCallTime(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_TIME)));
                    mCallDetailsBean.setCallType(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_TYPE)));
                    mCallDetailsBean.setStatus(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_STATUS)));

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_IS_EMERGENCY_CALL)) == null) {
                        mCallDetailsBean.setEmergencyCall(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_IS_EMERGENCY_CALL)).equals("1")) {
                            mCallDetailsBean.setEmergencyCall(true);
                        } else {
                            mCallDetailsBean.setEmergencyCall(false);
                        }
                    }
                    mListCallDetails.add(mCallDetailsBean);
                }while (mCursor.moveToNext()); // iterate through all the records
            }
        }catch(Exception e){
            e.getMessage();
        }
        return mListCallDetails;
    }

    /**
     *  get the list of calls on the basis of number
     *  @param: mPhoneNumber
     *  1 - incoming, 2 - outgoing, 3 - missed
     */

    public ArrayList<CallDetailsBean> getCallLogsIncomingOutgoing(){

        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();

        Cursor mCursor = null;
        CallDetailsBean mCallDetailsBean = null;
        //String mBBIDs = "";

        //mPhoneNumber = GlobalConfig_Methods.trimSpecialCharactersFromString(mPhoneNumber);

        String query = "SELECT * FROM CallDetails where CallType = 1 OR CallType = 2 GROUP BY CallingNumber ORDER BY CallTime DESC;";

        try{
            mCursor = sqliteDatabase.rawQuery(query, null);

            if(mCursor!=null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                do{
                    mCallDetailsBean = new CallDetailsBean();
                    mCallDetailsBean.setCallName(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NAME)));
                    mCallDetailsBean.setPrefix("");
                    mCallDetailsBean.setCountryCode("");
                    mCallDetailsBean.setCallingNumber(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_NUMBER)));
                    mCallDetailsBean.setCallTime(mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_CALL_TIME)));
                    mCallDetailsBean.setCallType(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_TYPE)));
                    mCallDetailsBean.setStatus(mCursor.getInt(mCursor.getColumnIndex(BIG_BUTTON_CALL_STATUS)));

                    if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_IS_EMERGENCY_CALL)) == null) {
                        mCallDetailsBean.setEmergencyCall(false);
                    } else {
                        if (mCursor.getString(mCursor.getColumnIndex(BIG_BUTTON_IS_EMERGENCY_CALL)).equals("1")) {
                            mCallDetailsBean.setEmergencyCall(true);
                        } else {
                            mCallDetailsBean.setEmergencyCall(false);
                        }
                    }
                    mListCallDetails.add(mCallDetailsBean);
                }while (mCursor.moveToNext()); // iterate through all the records
            }
        }catch(Exception e){
            e.getMessage();
        }
        return mListCallDetails;
    }

    /*
     * Get MaxCount For for Categories
     */

    public Cursor getCursorMaxCountCategories() {
        Cursor mCursor = null;
        try {
            try {
                mCursor = sqliteDatabase.rawQuery("SELECT MAX(CategoryID) FROM Category", null);

                if (mCursor != null && mCursor.getCount() > 0)
                    mCursor.moveToFirst();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor;
    }

    // -------CASE CONFIGURE MESSAGE DATA
    // ADDITION-------------------------------
    public long addUpdateCategories(int id, String message, boolean isUpdateMode,
                                    boolean isBackupMode) {
        long rowID = -1;
        if (isBackupMode) {

            // in case of back - up , check for existence of message and insert it, if not exists
            checkExistingCategories(id, message, isUpdateMode, rowID);

            /*ContentValues initialValues = new ContentValues();
            initialValues.put("CategoryID", id);
            initialValues.put("CategoryName", message);
            rowID = sqliteDatabase.insert(BIG_BUTTON_CATEGORY_TABLE, null, initialValues);*/

            GlobalCommonValues.isBackedupSuccessful = true;
        } else {
            try {
                if (isUpdateMode) {
                    String WHERE = "CategoryID = ?";
                    String[] WHERE_ARGS = new String[]{String.valueOf(id)};
                    ContentValues initialValues = new ContentValues();

                    initialValues.put("CategoryName", message);
                    rowID = sqliteDatabase.update(BIG_BUTTON_CATEGORY_TABLE, initialValues, WHERE,
                            WHERE_ARGS);
                } else if (!isUpdateMode) {
                    ContentValues initialValues = new ContentValues();
                    initialValues.put("CategoryID", id);
                    initialValues.put("CategoryName", message);

                    rowID = sqliteDatabase.insert(BIG_BUTTON_CATEGORY_TABLE, null, initialValues);
                }
            } catch (Exception e) {
                e.getMessage();
                rowID = -1;
            }
        }
        return (long) rowID;
    }


    /*
     * get non existing Config Messsages to be merged from database
     */
    // String WHERE;
    // String[] WHERE_ARGS;
    public void checkExistingCategories(int id, String messageDB, boolean isUpdateMode, long rowID) {
        Cursor mCursor = null;
        try {
            String[] fields = new String[]{BIG_BUTTON_CATEGORY_NAME};
            try {

                WHERE = "CategoryName=?";
                WHERE_ARGS = new String[]{messageDB};

                mCursor = sqliteDatabase.query(BIG_BUTTON_CATEGORY_TABLE, fields, WHERE, WHERE_ARGS, null,
                        null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != mCursor && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCursor != null && mCursor.getCount() > 0) {
        } else {
            try {
                ContentValues initialValues = new ContentValues();
                initialValues.put("CategoryID", id);
                initialValues.put("CategoryName", messageDB);

                if (!sqliteDatabase.isOpen()) {
                    sqliteDatabase = databasehelper.getWritableDatabase();
                }
                rowID = sqliteDatabase.insert(BIG_BUTTON_CATEGORY_TABLE, null, initialValues);
                if (rowID != -1) {
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    // Delete Chat Button Category From Id
    public int deleteChatButtonCategoryFromID(String id) {
        int i = -1;
        String WHERE = "CategoryID = ? ";
        String[] WHERE_ARGS = new String[]{id};
        try {
            i = sqliteDatabase.delete(BIG_BUTTON_CATEGORY_TABLE, WHERE, WHERE_ARGS);
        } catch (Exception e) {
            e.getMessage();
            i = -1;
        }
        return i;
    }

	 /*
	  * Check Existence of Categories
	  *
	  */

    public int checkCategoriesExistence(String message) {
        int size = -1;
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT Count(CategoryName) As Count FROM Category where CategoryName = '"
                    + message + "'" + " COLLATE NOCASE", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        return size;
    }

     /*
	  * Check Existence of Categories
	  *
	  */

    public String getCategoryIDFromName(String categoryName) {
        String categoryID = "-1";
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT CategoryID FROM Category where CategoryName = '"
                    + categoryName + "'", null);
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
            }
            categoryID = mCursor.getString(mCursor.getColumnIndex("CategoryID"));
        } catch (Exception e) {
            e.getMessage();
            categoryID = "-1";
        }
        return categoryID;
    }

    /*
	  * Get categoryName From CategoryID
	  *
	  */

    public String getCategoryNameFromID(String categoryID) {
        String categoryName = "-1";
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT CategoryName FROM Category where CategoryID = '"
                    + categoryID + "'", null);
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
            }
            categoryName = mCursor.getString(mCursor.getColumnIndex("CategoryID"));
        } catch (Exception e) {
            e.getMessage();
            categoryName = "";
        }
        return categoryName;
    }

    /*
	  * Check Existence of Categories in Tiles Table
	  *
	  */

    public int checkCategoryExistenceInTiles(int mButtonType) {
        int size = -1;
        Cursor mCursor;
        try {
            mCursor = sqliteDatabase.rawQuery("SELECT Count(ButtonType) As Count FROM Tiles where ButtonType = '"
                    + mButtonType + "'", null);
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
            }
            size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
        } catch (Exception e) {
            e.getMessage();
            size = -1;
        }
        return size;
    }

    /**
     *
     *  public int checkNotificationExistence(int id) {
     int size = -1;
     Cursor mCursor;
     try {
     mCursor = sqliteDatabase
     .rawQuery("SELECT Count(id) As Count FROM Notifications where id = " + String.valueOf(id), null);
     if (mCursor != null) {
     mCursor.moveToFirst();
     }
     size = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
     } catch (Exception e) {
     e.getMessage();
     size = -1;
     }

     *
     *
     */

}
