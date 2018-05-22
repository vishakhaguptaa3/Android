package com.tnc.database;

import java.util.ArrayList;

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
import com.tnc.webresponse.GetMessageResponseDataBean;
import com.tnc.webresponse.HowtoReponseDataBean;
import com.tnc.webresponse.SendMessageReponseDataBean;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds;


/**
 * Class containing methods to call Database' CRUD opeartion Methods
 * @author a3logics
 *
 */

@SuppressWarnings("deprecation")
public class DBQuery
{
    //BigButton User
    public static ArrayList<ContactDetailsUserBean> getAllUsers(Context context) {
        ArrayList<ContactDetailsUserBean> list = new ArrayList<ContactDetailsUserBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getUsers();
            } catch (Exception e) {
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static void insertUserDetails(Context context,ArrayList<ContactDetailsUserBean> userListdata) {
        if (userListdata == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addUser(userListdata);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ContactTilesBean> getAllTiles(Context context) {
        ArrayList<ContactTilesBean> list = new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getTiles(false);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // BigButton Tiles
    public static ArrayList<ContactTilesBean> getAllTiles(Context context,String dbName) {
        ArrayList<ContactTilesBean> list = new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context,dbName);
            db.openForCheckingExistence();
            try {	list = db.getTiles(true);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.closeForCheckExistence();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // BigButton Tiles
    public static ArrayList<ContactTilesBean> getSharedContactTiles(Context context,String dbName) {
        ArrayList<ContactTilesBean> list = new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context,dbName);
            db.openForCheckingExistence();
            try {
                list = db.getTiles(true);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.closeForCheckExistence();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //Get tile details from phoneNumber
    public static ArrayList<ContactTilesBean> getTileFromPhoneNumber(Context context,
                                                                     String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles=new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listContactTiles = db.getTileFromPhoneNumber(phoneNumber);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listContactTiles;
    }


    //Get tile details from phoneNumber
    public static ArrayList<ContactTilesBean> getTileFromPhoneNumberUpdateButton(Context context,String countryCodeRegisteredUser,
                                                                                 String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles=new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listContactTiles = db.getTileFromPhoneNumberUpdateButton(countryCodeRegisteredUser,phoneNumber);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listContactTiles;
    }

    // get Tile Details from PhoneNumber & Prefix
    public static ArrayList<ContactTilesBean> getTileFromPhoneNumberPrefix1(Context context,String prefix,String phoneNumber) {
        ArrayList<ContactTilesBean> listContactTiles=new ArrayList<ContactTilesBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listContactTiles = db.getTileFromPhoneNumberPrefix(prefix,phoneNumber);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listContactTiles;
    }

    //Delete Tile
    public static int deleteTile(Context context,String Prefix,String PhoneNumber,int TilePosition) {
        int count=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                //i=db.deleteMessage(id);
                count=db.deleteTile(Prefix,PhoneNumber,TilePosition);
            } catch (Exception e) {
                e.printStackTrace();
                count=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count=-1;
        }
        return count;
    }

    // Get BigButton Tiles on the basis of category
    public static ArrayList<ContactTilesBean> getChatstasyTilesFromCategory(Context context,String mCategory,
                                                                            String mMode) {
        ArrayList<ContactTilesBean> list = new ArrayList<ContactTilesBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                list = db.getChatstasyTilesFromCategory(mCategory, mMode);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Categories for Tiles
    public static ArrayList<CategoryBean> getCategoriesForTiles(Context context) {
        ArrayList<CategoryBean> list = new ArrayList<CategoryBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                list = db.getAllCategories(context);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Configured Messages
    public static ArrayList<InitResponseMessageBean> getAllConfiguredMessages(Context context,String dbName) {
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try {
            DBManager db = new DBManager(context,dbName);
            db.openForCheckingExistence();
            try {	list = db.getInitResponseMessages();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.closeForCheckExistence();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //Check  BigButton Tiles Existence
    public static int checkTileExistence(Context context,String phoneNumber,String countryCode) {
        int size=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                size = db.checkTileExistence(phoneNumber,countryCode);
            } catch (Exception e) {
                e.getMessage();
                size=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            size=-1;
        }
        return size;
    }

    public static void insertTile(Context context,ArrayList<ContactTilesBean> tilesListdata,boolean isBackupMode) {
        if (tilesListdata == null)
            return;
        if(isBackupMode)
        {
            try {
                DBManager dbMain = new DBManager(context);
                //				DBManager dbBackup = new DBManager(context,"big_button_app");
                dbMain.open();
                //				dbBackup.openForCheckingExistence();
                try {
                    dbMain.addBBTiles(tilesListdata,isBackupMode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (dbMain != null)
                    {
                        dbMain.close();
                    }
					/*if (dbBackup != null)
					{
						dbBackup.closeForCheckExistence();
					}*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                DBManager db = new DBManager(context);
                db.open();
                try {
                    db.addBBTiles(tilesListdata,isBackupMode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (db != null)
                        db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Update tile
    public static void updateTile(Context context,ContactTilesBean objContactTile)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTile(objContactTile);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Update Tile's PhoneNumber
    public static void updateTilePhoneNumber(Context context,String phoneNumber,String tilePosition)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTilePhoneNumber(phoneNumber,tilePosition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Update Tile's PhoneNumber
    public static void updateTilePhoneNumberButtonUpdate(Context context,String countryCodeRegisteredUserOld,
                                                         String phoneNumber,String tilePosition)
    {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTilePhoneNumberButtonUpdate(countryCodeRegisteredUserOld,phoneNumber,tilePosition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Update Tile Details on Registration
    public static void updateTileDetailsOnRegistration(Context context,
                                                       String countryCode,String isdCode,
                                                       String phoneNumber,String tilePosition)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTileDetailsOnRegistration(countryCode,isdCode,phoneNumber,tilePosition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    //Update tile Position By One
    public static void updateTilePositionByOne(Context context,String PhoneNumber,int tilePosition)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTilePositionByOne(PhoneNumber,tilePosition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Update tile Position On Rearrange
    public static void updateTilePositionOnRearrange(Context context,int index,String phoneNumber)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTilePositionOnRearrange(index,phoneNumber);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //Get Maximum Tile Position
    public static int getMaximumTilePosition(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                Cursor mCursor=db.getCursorMaxCountTilePosition();
                if(mCursor.getString(mCursor.getColumnIndex("MAX(TilePosition)"))==null)
                    max=-1;
                else{
                    max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(TilePosition)")));
                }
                //				max=mCursor.getColumnIndex("Count");//Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return max;
    }

    //Get Maximum Tile Position Emergency
    public static int getMaximumTilePositionEmetegncy(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                Cursor mCursor=db.getCursorMaxCountTilePositionEmergency();
                if(mCursor.getString(mCursor.getColumnIndex("MAX(TilePosition)"))==null)
                    max=-1;
                else{
                    max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(TilePosition)")));
                }
                //					max=mCursor.getColumnIndex("Count");//Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("Count")));
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return max;
    }


    //Update tiles reset tnc user to non tnc user
    public static void updateTileResetTnCUser(Context context)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateTileResetTnCUser();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //BigButtonContacts
    public static ArrayList<BBContactsBean> getAllBBContacts(Context context) {
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getBBContacts();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //BigButtonContacts2
    public static ArrayList<BBContactsBean> getAllBBContacts2(Context context) {
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getBBContacts2();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //BigButtonContacts From Country Code & PhoneNumber
    public static ArrayList<BBContactsBean> getBBContactsfromCountryCodeAndPhoneNumber(Context context,String countryCode,String phoneNumber) {
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getBBContactsFromCountrycodeAndPhoneNumber(countryCode, phoneNumber);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }


    //BigButtonContacts Ordered
    public static ArrayList<BBContactsBean> getAllBBContactsOrdered(Context context) {
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getBBContactsOrdered();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //check existence of BBContact
    public static ArrayList<BBContactsBean> checkBBContactExistence(Context context,int matching_user_id) {
        ArrayList<BBContactsBean> list = new ArrayList<BBContactsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                list = db.checkBBContacts(matching_user_id);
            } catch (Exception e) {
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //check existence of BBContact from countrycode & phoneNumber
    public static boolean checkBBContactExistenceFromNumber(Context context,String countryCode,String phoneNumber) {
        boolean isValidTnCUser=false;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                isValidTnCUser = db.checkBBContactsFromNumber(countryCode,phoneNumber);
            } catch (Exception e) {
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isValidTnCUser;
    }

    //insert BBContact From Service
    public static void insertBBContactFromService(Context context,ArrayList<BBContactsBean> bbContactsList) {
        if (bbContactsList == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addBBContactsFromService(context,bbContactsList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert BBContact
    public static void insertBBContact(Context context,ArrayList<BBContactsBean> bbContactsList) {
        if (bbContactsList == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addBBContacts(bbContactsList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPhoneNumberFromBBID(Context context,int BBID) {
        String phoneNumber="";
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                phoneNumber = db.getPhoneNumberFromBBID(BBID);
            }
            catch (Exception e) {
                e.getMessage();
                BBID=0;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            phoneNumber="";
        }
        return phoneNumber;
    }

    // Get BBID From PhoneNumber
    public static int getBBIDFromPhoneNumber(Context context,String phoneNumber) {
        int BBID=0;
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                BBID = db.getBBIDFromPhoneNumber(phoneNumber);
            }
            catch (Exception e) {
                e.getMessage();
                BBID=0;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            BBID=0;
        }
        return BBID;
    }

    //Get BBID From Specific Phone Number & Country Code
    public static int getBBIDFromPhoneNumberAndCountryCode(Context context,String phoneNumber,String CountryCode) {
        int BBID=0;
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                BBID = db.getBBIDFromPhoneNumberAndCountryCode(phoneNumber,CountryCode);
            }
            catch (Exception e) {
                e.getMessage();
                BBID=0;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            BBID=0;
        }
        return BBID;
    }


    //Check existence of BBID
    public static boolean checkBBIDExistence(Context context,int BBID) {
        boolean isBBIDExist = false;
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                isBBIDExist = db.checkBBIDExistence(BBID);
            }
            catch (Exception e) {
                e.getMessage();
                isBBIDExist = false;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            isBBIDExist = false;
        }
        return isBBIDExist;
    }


    /**
     * Update BBContacts Table
     * @param mContext
     * @param : BBID
     */
    public static void updateBBContactsImage(Context mContext,int BBID, String imagePath)
    {
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                db.updateBBContactImage(BBID, imagePath);
            }catch (Exception e){
                e.getMessage();
            }finally{
                if (db != null)
                    db.close();
            }
        }catch (Exception ex){
            ex.getMessage();
        }
    }


    //Get all the countries details
    public static ArrayList<CountryDetailsBean> getAllCountryDetails(Context context) {
        ArrayList<CountryDetailsBean> listCountries=new ArrayList<CountryDetailsBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listCountries= db.getAllCountryDetails();
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listCountries;
    }

    //Insert emergency Numbers of all the countries
    public static void insertAllCountryEmergencyNumbers(Context context, ArrayList<CountryDetailsBean> listCountries) {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.insertAllCountryEmergencyNumbers(listCountries);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Get emergency Numbers of all the countries
    public static ArrayList<CountryDetailsBean> getAllCountryEmergencyNumbers(Context context) {
        ArrayList<CountryDetailsBean> listCountries=new ArrayList<CountryDetailsBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listCountries= db.getAllCountryEmergencyNumbers();
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listCountries;
    }

    //Get Country Codes of all the countries
    public static ArrayList<CountryDetailsBean> getAllCountryCode(Context context) {
        ArrayList<CountryDetailsBean> listCountries=new ArrayList<CountryDetailsBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listCountries= db.getAllCountryCodes();
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listCountries;
    }

    //Get flag icon of the country on th basis of countryname
    public static String getCountryFlag(Context context,String countryName) {
        String flagIcon="";
        try{
            DBManager db = new DBManager(context);
            db.open();
            try{
                flagIcon= db.getCountryFlag(countryName);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flagIcon;
    }

    //Get emergency number of the country on the basis of countryname
    public static String getEmergency(Context context,String countryName) {
        String emergencyNumber="";
        try{
            DBManager db = new DBManager(context);
            db.open();
            try{
                emergencyNumber= db.getEmergency(countryName);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return emergencyNumber;
    }

    //Insert Clipart Images
    public static void insertAllClipartImages(Context context,
                                              ArrayList<String> listClipArts) {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try{
                db.insertAllClipartImages(context,listClipArts);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Get All ClipArts
    public static ArrayList<ClipArtBean> getAllClipArts(Context context) {
        ArrayList<ClipArtBean> list =
                new ArrayList<ClipArtBean>();
        try {
            DBManager db = new DBManager(context);
            try {
                db.close();
            } catch (Exception e) {
                e.getMessage();
            }
            db.open();
            try {
                list = db.getAllClipArts(context);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static void updateBBImageFromResponse(Context context,byte[] Image,
                                                 String from_user_phoneNumber)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateImage_BBTile(Image,from_user_phoneNumber);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void updateBBImageFromNotification(Context context,byte[] Image,String from_user_phoneNumber)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateImageNotification_BBTile(Image,from_user_phoneNumber);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	/*//check existence of Notification
	public static boolean checkNotificationExistence(Context context,int id) 
	{
		boolean IsExist=false;
		try 
		{
			DBManager db = new DBManager(context);
			db.open();
			try 
			{
				IsExist = db.checkNotifiationExistence(id);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				IsExist=false;
			}
			finally 
			{
				if (db != null)
					db.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			IsExist=false;
		}
		return IsExist;
	}*/

    //Notifications
    public static void insertNotification(Context context,ArrayList<NotificationReponseDataBean> notificationList) {
        if (notificationList == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addNotifications(notificationList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateNotification(Context context,String notification_id)
    {
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                db.updateNotification(notification_id);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<NotificationReponseDataBean> getAllNotifications(Context context) {
        ArrayList<NotificationReponseDataBean> list = new ArrayList<NotificationReponseDataBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getNotifications();
            } catch (Exception e) {
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static int getUnreadNotificationCount(Context mContext)
    {
        int size=-1;
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                size=db.getUnreadNotificationCount();
            }
            catch (Exception e)
            {
                e.getMessage();
                size=-1;
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.getMessage();
            size=-1;
        }
        return size;
    }

    public static int getNotificationsMaxCount(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                Cursor mCursor=db.getCursorMaxCountNotifications();
                max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(id)")));
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return max;
    }

    //Delete Notification
    public static void deleteNotification(Context context,int notification_id)
    {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.deleteNotification(notification_id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Message
    public static void insertMessage(Context context,ArrayList<SendMessageReponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addMessage(listMessageDetails);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Insert ConfiguredMessages from a list
    public static int insertConfigMessageFromWebService(Context context,ArrayList<DefaultMessagesBeanDB> mListDefaultMessages)
    {
        int saveId=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                saveId=(int)db.insertAllDefaultMessages(mListDefaultMessages);
            } catch (Exception e) {
                e.printStackTrace();
                saveId=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveId=-1;
        }
        return saveId;
    }

    //ConfiguredMessages
    public static int insertConfigMessage(Context context,int id,String message,int type,int isLocked,
                                          boolean isUpdateMode,boolean isBackupMode)
    {
        int saveId=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                saveId=(int)db.addUpdateInitResponseMessage(id,message,type,isLocked,isUpdateMode,isBackupMode);
            } catch (Exception e) {
                e.printStackTrace();
                saveId=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveId=-1;
        }
        return saveId;
    }

    //Check  Config Message Existence
    public static int checkConfigMessageExistence(Context context,String message) {
        int size=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                size = db.checkConfigMessageExistence(message);
            } catch (Exception e) {
                e.getMessage();
                size=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            size=-1;
        }
        return size;
    }

    //Message
    public static int deleteConfigMessageFromDB(Context context,int isLocked) {
        int i=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                //i=db.deleteMessage(id);
                i=db.deleteConfigMessage(isLocked);
            } catch (Exception e) {
                e.printStackTrace();
                i=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            i=-1;
        }
        return i;
    }

    //Message
    public static int deleteConfigMessage(Context context,String id) {
        int i=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                //i=db.deleteMessage(id);
                i=db.deleteConfigMessage(id);
            } catch (Exception e) {
                e.printStackTrace();
                i=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            i=-1;
        }
        return i;
    }

    //Message
    public static void insertGetMessage(Context context,ArrayList<GetMessageResponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addGetMessage(listMessageDetails);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void insertGetMessage1(Context context,ArrayList<GetMessageResponseDataBean> listMessageDetails) {
        if (listMessageDetails == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addGetMessage1(listMessageDetails);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Message
    public static int deleteMessage(Context context,String position) {
        int i=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                i=db.deleteMessage(position);
            } catch (Exception e) {
                e.printStackTrace();
                i=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            i=-1;
        }
        return i;
    }

    //Delete Message
    public static int deleteMessageFromText(Context context,ArrayList<String> listDeleteMessages) {
        int i=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                i=db.deleteMessageFromText(listDeleteMessages);
            } catch (Exception e) {
                e.printStackTrace();
                i=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            i=-1;
        }
        return i;
    }

    public static void deleteUserChat(Context context,int to_user_id_delete,int from_user_id_delete)
    {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.deleteUserChat(to_user_id_delete,from_user_id_delete);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SendMessageReponseDataBean> getAllMessages(Context context,int to_user_id) {
        ArrayList<SendMessageReponseDataBean> list = new ArrayList<SendMessageReponseDataBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                list = db.getMessages(to_user_id);
            } catch (Exception e) {
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /*
     * Fetch Init response/Messages on the basis of type (Init/response) and user/custom
     * */
    public static ArrayList<InitResponseMessageBean> getInitResponseMessages(Context context,int type,int isLocked)
    {
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                list = db.getInitResponseMessages(type,isLocked);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
    }

    /*
     * Fetch Init &  response Messages Custom/User Made
     * */
    public static ArrayList<InitResponseMessageBean> getInitResponseMessagesCustomLocked(Context context,int isLocked)
    {
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                list = db.getInitResponseMessagesCustomLocked(isLocked);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
    }

    /*
     * Fetch Init/response Messages on the basis of type (Init/response)
     * */
    public static ArrayList<InitResponseMessageBean> getInitResponseMessages(Context context,int type)
    {
        ArrayList<InitResponseMessageBean> list = new ArrayList<InitResponseMessageBean>();
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                list = db.getInitResponseMessages(type);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
    }

	/*//get All Messages to display chat View

	public static ArrayList<SendMessageReponseDataBean> getAllMessagesChat(Context context,int to_user_id) {
		ArrayList<SendMessageReponseDataBean> list = new ArrayList<SendMessageReponseDataBean>();
		try {
			DBManager db = new DBManager(context);
			db.open();
			try {
				// list = db.getAllInstalledApplication();
				list = db.getMessages(to_user_id);
			} catch (Exception e) {
			} finally {
				if (db != null)
					db.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}*/

    public static int getConfigMessagesMaxCount(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                Cursor mCursor=db.getCursorMaxCountConfigMessages();
                max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(id)")));
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return max;
    }

    public static int getMessagesMaxCount(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                // list = db.getAllInstalledApplication();
                Cursor mCursor=db.getCursorMaxCountMessages();
                max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(id)")));
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return max;
    }

    /**
     * get unread message count
     * @param mContext
     * @return
     */
    public static int getUnreadMessageCount(Context mContext)
    {
        int size=-1;
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                size=db.getUnreadMessageCount();
            }catch (Exception e){
                e.getMessage();
                size=-1;
            }finally{
                if (db != null)
                    db.close();
            }
        }catch (Exception ex){
            ex.getMessage();
            size=-1;
        }
        return size;
    }

    /**
     * get unread message count
     * @param mContext
     * @param from_id
     * @return
     */
    public static int getUnreadMessageCountOfTnCUser(Context mContext,int from_id){
        int size=-1;
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                size=db.getUnreadMessageCountOfTnCUser(from_id);
            }catch (Exception e){
                e.getMessage();
                size=-1;
            }finally{
                if (db != null)
                    db.close();
            }
        }catch (Exception ex){
            ex.getMessage();
            size=-1;
        }
        return size;
    }

    /**
     * update message status  as read    // 1 - unread 2 - read
     * @param mContext
     * @param to_user_id_status_update
     * @param from_user_id_status_update
     */
    public static void updateMessageStatus(Context mContext,int to_user_id_status_update,int from_user_id_status_update)
    {
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                db.updateMessageStatus(to_user_id_status_update,from_user_id_status_update);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }
    }

	/*public static ArrayList<ConfigurationBean> getAllConfigDetails(Context context) {
		ArrayList<ConfigurationBean> list = new ArrayList<ConfigurationBean>();
		try {
			DBManager db = new DBManager(context);
			db.open();
			try {
				// list = db.getAllInstalledApplication();
				list = db.getConfigDetails();
			} catch (Exception e) {
			} finally {
				if (db != null)
					db.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}*/

	/*public static void insertConfigDetails(Context context,ArrayList<ConfigurationBean> configDetailsList) {
		if (configDetailsList == null)
			return;
		try {
			DBManager db = new DBManager(context);
			db.open();
			try {
				db.addConfigDetails(configDetailsList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*public static ArrayList<MyBackupBean> getMyBackup(Context context) {
		ArrayList<MyBackupBean> list = new ArrayList<MyBackupBean>();
		try {
			DBManager db = new DBManager(context);
			db.open();
			try {
				// list = db.getAllInstalledApplication();
				list = db.getMyBackupDetails();
			} catch (Exception e) {
			} finally {
				if (db != null)
					db.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static void insertMyBackupDetails(Context context,ArrayList<MyBackupBean> myBackupDetailsList) {
		if (myBackupDetailsList == null)
			return;
		try {
			DBManager db = new DBManager(context);
			db.open();
			try {
				db.addMyBackupDetails(myBackupDetailsList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

    public static void deleteTable(String tableName, String whereClause, String []whereArgs,
                                   Context context) {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.deleteTable(tableName, whereClause, whereArgs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param :context
     *            ,list of contacts data
     * @return
     */
	/*
	 * public static void insertAllUserCallLogFromPhone(Context context,
	 * ArrayList<ContactDetailsRegisteredBean> contactListdata) { if
	 * (contactListdata == null) return; try { DBManager db = new
	 * DBManager(context); db.open(); try {
	 * db.addUserDatafromPhone(contactListdata); } catch (Exception e) {
	 * e.printStackTrace(); } finally { if (db != null) db.close(); } } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */
    public static String getContactname(Context mContext,String contactID)
    {
//		if(contactID.trim().equalsIgnoreCase("10002")){
//			//system.out.println("");
//		}

        String name="";
        Cursor cursor =mContext.getContentResolver().query(
                CommonDataKinds.Phone.CONTENT_URI,
                null,
                CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{contactID}, null);

        while (cursor.moveToNext())
        {
            name=cursor.getString(cursor.getColumnIndex(People.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }

    public static String getIDDCodeDB(Context mContext,String countryCode)
    {
        String iddCode="";
        try
        {
            DBManager db = new DBManager(mContext);
            db.open();
            try
            {
                iddCode = db.getIDDCodeFromNumber(countryCode);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return iddCode;
    }

    public static String getIsImageLockFromBBID(Context mContext,String BBID)
    {
        String isImageLockStatus="";
        try
        {
            DBManager db = new DBManager(mContext);
            db.open();
            try
            {
                isImageLockStatus = db.getIsImageLockStatusFromBBID(BBID);
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return isImageLockStatus;
    }

    //insert How To
    public static void insertHowTo(Context context,ArrayList<HowtoReponseDataBean> howtoList) {
        if (howtoList == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addHowTo(howtoList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get all how to questions
    public static ArrayList<HowtoReponseDataBean> getAllHowTo(Context context) {
        ArrayList<HowtoReponseDataBean> list = new ArrayList<HowtoReponseDataBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                list = db.getAllHowTo();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //insert FAQ
    public static void insertFAQ(Context context,ArrayList<HowtoReponseDataBean> howtoList) {
        if (howtoList == null)
            return;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.addFAQ(howtoList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get all FAQ questions
    public static ArrayList<HowtoReponseDataBean> getAllFAQ(Context context) {
        ArrayList<HowtoReponseDataBean> list = new ArrayList<HowtoReponseDataBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                list = db.getAllFAQ();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //create Cliparts Table
    public static void createClipartsTable(Context context) {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.createClipartsTable();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //create Category Table
    public static void createCategoryTable(Context context) {
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                db.createCategoryTable();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //insert Call Log
    public static long insertCallDetails(Context context,CallDetailsBean mCallDetailsBean) {
        long rowID = -1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                rowID = db.insertCallDetails(context,mCallDetailsBean);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowID;
    }

    // get the list of emergency calls on the basis of type
    //1 - incoming, 2 - outgoing, 3 - missed
    public static ArrayList<CallDetailsBean> getcallLogsFromType(Context context, int callType){
        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                mListCallDetails = db.getcallLogsFromType(callType);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  mListCallDetails;
    }

    // get BBids for sending emergency contact notification
    public static String getBBIDForEmergencyNotification(Context context){
        String mBBIDS = "";
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                mBBIDS = db.getBBIDForEmergencyNotification();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  mBBIDS;
    }

    /*
     * Get Contact Tile Name From BBID
     */
    public static String getContactTileNameFromBBID(Context context, int BBID, boolean isFromTileTable) {
        String mUserName="";
        try
        {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                mUserName = db.getContactTileNameFromBBID(BBID, isFromTileTable);
            }
            catch (Exception e) {
                e.getMessage();
                BBID=0;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mUserName="";
        }
        return mUserName;
    }

    /**
     * get unread call log count
     * @param mContext
     * @param mPhoneNumber
     * @return
     */
    public static int getUnreadCallCount(Context mContext,String mPhoneNumber){
        int size=-1;
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                size = db.getUnreadCallCount(mPhoneNumber);
            }catch (Exception e){
                e.getMessage();
                size=-1;
            }finally{
                if (db != null)
                    db.close();
            }
        }catch (Exception ex){
            ex.getMessage();
            size=-1;
        }
        return size;
    }

    /**
     * Update Call log Status as read  // 1 - unread 2 - read
     * @param mContext
     * @param mPhoneNumber
     */
    public static void updateCallLogStatus(Context mContext,String mPhoneNumber)
    {
        try {
            DBManager db = new DBManager(mContext);
            db.open();
            try {
                db.updateCallLogStatus(mPhoneNumber);
            }catch (Exception e){
                e.getMessage();
            }finally{
                if (db != null)
                    db.close();
            }
        }catch (Exception ex){
            ex.getMessage();
        }
    }

    /**
     *  get the list of calls on the basis of number
     *  @param: context, mPhoneNumber
     */
    public static ArrayList<CallDetailsBean> getCallLogsFromNumber(Context context, String mPhoneNumber){
        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                mListCallDetails = db.getCallLogsFromNumber(mPhoneNumber);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  mListCallDetails;
    }

    /**
     *  get the list of calls For Incoming and outgoing
     *  @param: context, mPhoneNumber
     */
    public static ArrayList<CallDetailsBean> getCallLogsIncomingOutgoing(Context context){
        ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                mListCallDetails = db.getCallLogsIncomingOutgoing();
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  mListCallDetails;
    }

    /**
     * Get Max count of Categories
     * @param context
     * @return
     */
    public static int getCategoriesMaxCount(Context context) {
        int max=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                Cursor mCursor=db.getCursorMaxCountCategories();
                max=Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("MAX(CategoryID)")));
            }catch (Exception e){
                e.getMessage();
            }finally{
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return max;
    }

    public static int insertUpdateCategories(Context context,int id,String message,
                                             boolean isUpdateMode,boolean isBackupMode)
    {
        int saveId=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {             //(int)db.addUpdateInitResponseMessage
                saveId = (int)db.addUpdateCategories(id,message,isUpdateMode,isBackupMode);
            } catch (Exception e) {
                e.printStackTrace();
                saveId=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveId=-1;
        }
        return saveId;
    }

    /**
     * Method to delete chat button category
     * @param context
     * @param id
     * @return
     */
    public static int deleteChatButtonCategory(Context context,String id) {
        int i=-1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                i=db.deleteChatButtonCategoryFromID(id);
            } catch (Exception e) {
                e.printStackTrace();
                i=-1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            i=-1;
        }
        return i;
    }

    //Get CategoryID From CategoryName
    public static String getCategoryIDFromName(Context context,String categoryName) {
        String categoryID = "-1";
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                categoryID = db.getCategoryIDFromName(categoryName);
            } catch (Exception e) {
                e.getMessage();
                categoryID = "-1";
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            categoryID = "-1";
        }
        return categoryID;
    }

    //Get CategoryName From CategoryID
    public static String getCategoryNameFromID(Context context,String categoryID) {
        String categoryName = "";
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                categoryName = db.getCategoryNameFromID(categoryName);
            } catch (Exception e) {
                e.getMessage();
                categoryName = "";
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            categoryName = "";
        }
        return categoryName;
    }

    //Check existence of category in tiles table
    public static int checkCategoryExistenceInTiles(Context context,int mButtonType) {
        int categoryID = -1;
        try {
            DBManager db = new DBManager(context);
            db.open();
            try {
                categoryID = db.checkCategoryExistenceInTiles(mButtonType);
            } catch (Exception e) {
                e.getMessage();
                categoryID = -1;
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            categoryID = -1;
        }
        return categoryID;
    }

	/*public static String getCountryName(Context mContext,String countryCode)
	{
		String iddCode="";
		try 
		{
			DBManager db = new DBManager(mContext);
			db.open();
			try 
			{
				iddCode = db.getCountryNameFromCountryCode(countryCode);
			}
			catch (Exception e) 
			{
				e.getMessage();
			}
			finally 
			{
				if (db != null)
					db.close();
			}
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return iddCode;
	}*/

    public static ArrayList<Integer> getBBIDforCategory(Context context,
                                                                     String categoryIds) {
        ArrayList<Integer> listBBID = new ArrayList<Integer>();
        try {
            DBManager db = new DBManager(context);
            db.open();
            try
            {
                listBBID = db.getBBIDforCategory(categoryIds);
            }
            catch (Exception e) {
                e.getMessage();
            } finally {
                if (db != null)
                    db.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listBBID;
    }
    public DBQuery() {
    }
}
