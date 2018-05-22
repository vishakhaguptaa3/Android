package com.tnc.service;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.SplashScreen;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.database.DBManager;
import com.tnc.database.DBQuery;
import com.tnc.fragments.VerifyingRegistrationFragment;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.GetContactListServer;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;

import cz.msebera.android.httpclient.Header;

public class GetContactService extends Service {
    private ContentResolver cr;
    private ArrayList<ContactDetailsBean> listContacts = null;
    private SharedPreference saveState;
    private Gson gson = null;
    private GetContactListServer getContactListServer = null;
    private BBContactsBean bbContactBean;
    private ArrayList<BBContactsBean> listBBContacts;
    // private Handler handler=new Handler();
    private Context mContext;
    private String mNumber = "";
    private String[] arrayNumber;
    private JSONArray mJsonArrayHomeTiles;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        saveState = new SharedPreference();
        saveState.setIS_SERVICEON(mContext, true);

        new UpdateContactAsyncTask().execute();

        return START_STICKY;
    }

    /**
     * fetch phone Contacts and request to the server to match BB Contacts
     *
     */
    class UpdateContactAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                cr = mContext.getContentResolver();
                gson = new Gson();
                // GlobalCommonValues.listContacts.clear();
                CopyDBFromAssets obj = new CopyDBFromAssets(getApplicationContext());
                if (!new File(CopyDBFromAssets.DB_PATH + CopyDBFromAssets.DATABASE_NAME).canRead()) {
                    obj.copyDatabase();
                }

                if(!saveState.getRefrehContactList(mContext) && !saveState.IS_FROM_HOME(mContext) &&
                        saveState.IS_FetchingContacts(mContext) || GlobalCommonValues.listContacts.isEmpty()){
                }

                saveState.setIS_FetchingContacts(mContext, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (NetworkConnection.isNetworkAvailable(mContext) && saveState.isRegistered(mContext)) {
                if(saveState.getRefrehContactList(mContext)){
                    contactRequest();
                }else if(saveState.IS_FROM_HOME(mContext)){
                    sendingTilesToTheServer();
                }
            } else {
            }
            saveState.set_IS_UPOLADCONTACTSREQUESTED(mContext, false);
            saveState.setIS_SERVICEON(mContext, false);
            stopSelf();
        }
    }

    /**
     * uploading phone contacts to the server to match BB Contacts
     */
    @SuppressWarnings("unused")
    private void contactRequest() {
        JSONArray jsonArray = new JSONArray();
        String arrayPhoneNumber[];
        String strPhoneNumber = "";

        try {
            JSONObject jsonObject = null;
            for (int i = 0; i < GlobalCommonValues.listContactsSendToServer.size(); i++) {
                jsonObject = new JSONObject();
                strPhoneNumber = new String();
                arrayPhoneNumber = new String[1000];
                jsonObject.put("contact_id", GlobalCommonValues.listContactsSendToServer.get(i).get_id());
                strPhoneNumber = GlobalCommonValues.listContactsSendToServer.get(i).get_phone().toString();
                JSONArray jsonArrayNumber = new JSONArray();

                if (GlobalCommonValues.listContactsSendToServer.get(i).get_phone().size() > 0) {
                    for (int j = 0; j < GlobalCommonValues.listContactsSendToServer.get(i).get_phone().size(); j++) {
                        JSONObject jsonObjectContact = new JSONObject();
                        String strPhone = GlobalCommonValues.listContactsSendToServer.get(i).get_phone().get(j);
                        String contactNumber = "";

                        try {
                            mNumber = GlobalConfig_Methods.getFormattedNumber(mContext, strPhone);
                            // arrayNumber=mNumber.split(",");
                            contactNumber = mNumber;
                            jsonObjectContact.put("number", contactNumber);
                            jsonArrayNumber.put(jsonObjectContact);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.getMessage();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }

                try {
                    arrayPhoneNumber = strPhoneNumber.split(",");
                    jsonObject.put("phonenumbers", jsonArrayNumber);// [{"number":"1-8584010143"}]
                    jsonArray.put(i, jsonObject);
                } catch (Exception e) {
                    //system.out.println("Error--" + e.getMessage() + strPhoneNumber);
                }

            }
            // //system.out.println("request bean" + jsonArray);
            String strJsonArray = jsonArray.toString();
            // String strEncode = URLEncoder.encode(strJsonArray, "UTF-8");
            cz.msebera.android.httpclient.entity.StringEntity stringEntity =
                    new cz.msebera.android.httpclient.entity.StringEntity(strJsonArray);
            // StringEntity stringEntity=new StringEntity(jsonArray.toString());
            MyHttpConnection.postWithJsonEntityHeader(getApplicationContext(),
                    GlobalCommonValues.CHECK_CONTACTS_DATABASE, stringEntity, getContactsFromServer,
                    getApplicationContext().getResources().getString(R.string.private_key), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * uploading phone contacts to the server to match BB Contacts
     */
    private void sendingTilesToTheServer() {
        mJsonArrayHomeTiles = new JSONArray();
        String arrayPhoneNumber[];
        String strPhoneNumber = "";

        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject();
            strPhoneNumber = new String();
            arrayPhoneNumber = new String[1000];
            JSONArray jsonArrayNumber = new JSONArray();

            // check for existence of tiles and
            // add these contacts/numbers
            // and send to the server.

            try {
                ArrayList<ContactTilesBean> listContactTiles = DBQuery.getAllTiles(mContext);
                if (listContactTiles != null && listContactTiles.size() > 0) {
                    for (int k = 0; k < listContactTiles.size(); k++) {
                        jsonObject = new JSONObject();
                        jsonArrayNumber = new JSONArray();
                        strPhoneNumber = new String();// listContactTiles.get(k).getPrefix()+listContactTiles.get(k).getCountryCode()+listContactTiles.get(k).getPhoneNumber();
                        strPhoneNumber = listContactTiles.get(k).getCountryCode() + "-"
                                + listContactTiles.get(k).getPhoneNumber();
                        jsonObject.put("contact_id", 10001 + k);
                        JSONObject jsonObjectContact = new JSONObject();
                        jsonObjectContact.put("number", strPhoneNumber);
                        jsonArrayNumber.put(jsonObjectContact);
                        jsonObject.put("phonenumbers", jsonArrayNumber);
                        mJsonArrayHomeTiles.put(k, jsonObject);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            //system.out.println(jsonObject);

            String strJsonArray = mJsonArrayHomeTiles.toString();
            cz.msebera.android.httpclient.entity.StringEntity stringEntity =
                    new cz.msebera.android.httpclient.entity.StringEntity(strJsonArray);
            MyHttpConnection.postWithJsonEntityHeader(getApplicationContext(),
                    GlobalCommonValues.CHECK_CONTACTS_DATABASE, stringEntity, getContactsFromServer,
                    getApplicationContext().getResources().getString(R.string.private_key), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AsyncHttpResponseHandler getContactsFromServer = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            if(GlobalCommonValues.listContactsSendToServer!=null && GlobalCommonValues.listContactsSendToServer.size()>0){
                GlobalCommonValues.listContactsSendToServer = new ArrayList<ContactDetailsBean>();
            }
            try {
                if (response != null) {
                    Logs.writeLog("GetContactService", "OnSuccess", response.toString());
                    getResponse(response.toString());
                }
            } catch (JsonSyntaxException json) {
                json.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            if(GlobalCommonValues.listContactsSendToServer!=null && GlobalCommonValues.listContactsSendToServer.size()>0){
                GlobalCommonValues.listContactsSendToServer = new ArrayList<ContactDetailsBean>();
            }
            if (response != null)
                Logs.writeLog("GetContactService", "OnFailure", response);

            if(!saveState.isFirst(mContext)){
                Intent intentMainacivity = new Intent("com.tapnchat.phonecontactshomescreen");
                sendBroadcast(intentMainacivity);
                Intent intentHomeScreen = new Intent("com.tapnchat.phonecontactsmainscreen");
                sendBroadcast(intentHomeScreen);
            }else if(saveState.isFirst(mContext)){
                Intent intent = new Intent("com.tapnchat.phonecontactsmainscreen");
                sendBroadcast(intent);
            }
            if (saveState.IS_FROM_HOME(mContext)) {
                Intent intent = new Intent("com.bigbutton.homereceiver");
                sendBroadcast(intent);
            }

        }

        @Override
        public void onFinish() {
        }
    };

    /**
     * Handling response from the Server
     *
     * @param response
     */
    private void getResponse(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>") || response.contains("php")) {
                response2 = response.substring(response.indexOf("response_code") - 2, response.length());
            } else {
                response2 = response;
            }
            SharedPreference.isFirstTime = false;
            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                getContactListServer = gson.fromJson(response2, GetContactListServer.class);
                if (getContactListServer.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) {
                    listBBContacts = new ArrayList<BBContactsBean>();
                    for (int i = 0; i < getContactListServer.getData().size(); i++) {
                        int mobID;
                        mobID = Integer.parseInt(getContactListServer.getData().get(i).getContact_id());
                        try {
                            for (int k = 0; k < getContactListServer.getData().get(i).getPhoneData().size(); k++) {
                                if (saveState.isRegistered(mContext)) {
                                    if (!saveState.getBBID(mContext).trim().equals("") &&
                                            Integer.parseInt(getContactListServer.getData().get(i).getPhoneData().get(k)
                                                    .getBbid()) != Integer.parseInt(saveState.getBBID(mContext))) {
                                        //Check existence of Tnc User already in the database
                                        boolean isBBIDExist = false;


										/*bbid = DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,
												getContactListServer.getData().get(i)
												.getPhoneData().get(k).getNumber(),getContactListServer.getData().get(i)
												.getPhoneData().get(k).getCountryCode());*/

                                        isBBIDExist = DBQuery.checkBBIDExistence(mContext, Integer.parseInt(
                                                getContactListServer.getData().get(i)
                                                        .getPhoneData().get(k).getBbid()
                                        ));

                                        DBManager dbCheckTileTable = null;

                                        if(isBBIDExist){
                                            // Check if tiles exists for this number then update their BBID and IsTncUser = true
                                            try{
                                                dbCheckTileTable = new DBManager(mContext);
                                                dbCheckTileTable.open();
                                                dbCheckTileTable.updateIsTnCUserForTile(Integer.parseInt(
                                                        getContactListServer.getData().get(i)
                                                                .getPhoneData().get(k).getBbid()), getContactListServer.getData().get(i)
                                                                .getPhoneData().get(k).getCountryCode(),
                                                        getContactListServer.getData().get(i)
                                                                .getPhoneData().get(k).getNumber());
                                            }catch(Exception e){
                                                e.getMessage();
                                            }finally {
                                                if (dbCheckTileTable != null)
                                                    dbCheckTileTable.close();
                                            }

                                        }

                                        if(!isBBIDExist){
                                            bbContactBean = new BBContactsBean();
                                            bbContactBean.setCountryCode(getContactListServer.getData().get(i)
                                                    .getPhoneData().get(k).getCountryCode());
                                            bbContactBean.setMobID(mobID);
                                            bbContactBean.setBBID(Integer.parseInt(
                                                    getContactListServer.getData().get(i).getPhoneData().get(k).getBbid()));

                                            if(saveState.IS_FROM_HOME(mContext)){//In case refresh is from the home screen
                                                String mBBContactName = "";
                                                if(mJsonArrayHomeTiles!=null){
                                                    for(int z=0;z<mJsonArrayHomeTiles.length();z++){
                                                        try {
                                                            if(mJsonArrayHomeTiles.getJSONObject(z).getString("contact_id").trim().equalsIgnoreCase(String.valueOf(mobID))){
                                                                String mOriginalNumber = String.valueOf(new JSONArray(mJsonArrayHomeTiles.getJSONObject(z).getString("phonenumbers")).getJSONObject(0).get("number"));
                                                                String[] mOriginalNumberArray = mOriginalNumber.split("-");
                                                                String mTileCountryCode = "",mTilePhoneNumber="";
                                                                //																if((mOriginalNumberArray[0]!=null) && !(mOriginalNumberArray[0].trim().equalsIgnoreCase("")))
                                                                //																	mTileCountryCode = mOriginalNumberArray[0];

                                                                if((mOriginalNumberArray[1]!=null) && !(mOriginalNumberArray[1].trim().equalsIgnoreCase("")))
                                                                    mTilePhoneNumber = mOriginalNumberArray[1];

                                                                //																if(!(mTileCountryCode.trim().equalsIgnoreCase("")) && !(mTilePhoneNumber.trim().equalsIgnoreCase("")))
                                                                if(!(mTilePhoneNumber.trim().equalsIgnoreCase(""))){
                                                                    ArrayList<ContactTilesBean> mListTilesData = new ArrayList<ContactTilesBean>();
                                                                    mListTilesData = DBQuery.getTileFromPhoneNumber(mContext,mTilePhoneNumber);
                                                                    if(mListTilesData!=null && mListTilesData.size()>0){
                                                                        mBBContactName = mListTilesData.get(0).getName();
                                                                    }
                                                                }
                                                            }

                                                        }catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    bbContactBean.setName(mBBContactName);
                                                }
                                            }else{
                                                bbContactBean.setName(
                                                        DBQuery.getContactname(getApplicationContext(), String.valueOf(mobID)));
                                            }


                                            bbContactBean.setPhoneNumber(getContactListServer.getData().get(i)
                                                    .getPhoneData().get(k).getNumber());
                                            if (getContactListServer.getData().get(i).getPhoneData().get(k)
                                                    .getImage() != null
                                                    && !getContactListServer.getData().get(i).getPhoneData().get(k)
                                                    .getImage().trim().equals("")) {
                                                bbContactBean.setImage(getContactListServer.getData().get(i).getPhoneData()
                                                        .get(k).getImage());
                                            } else {
                                                bbContactBean.setImage("");
                                            }
                                            listBBContacts.add(bbContactBean);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    //DBQuery.deleteTable("BBContacts", "", null, getApplicationContext());

                    DBQuery.insertBBContactFromService(getApplicationContext(), listBBContacts);
                    if (VerifyingRegistrationFragment.isNotify) {
                        Intent intent = new Intent("com.bigbutton.receiver");
                        sendBroadcast(intent);

                        intent = new Intent("com.bigbutton.receiver_contacts");
                        sendBroadcast(intent);
                    } else if (saveState.IS_FROM_HOME(mContext)) {
                        Intent intent = new Intent("com.bigbutton.homereceiver");
                        sendBroadcast(intent);
                    }

                    if(!saveState.isFirst(mContext)){
                        Intent intentMainacivity = new Intent("com.tapnchat.phonecontactshomescreen");
                        sendBroadcast(intentMainacivity);
                        Intent intentHomeScreen = new Intent("com.tapnchat.phonecontactsmainscreen");
                        sendBroadcast(intentHomeScreen);

                        if (saveState.IS_FROM_HOME(mContext)) {
                            Intent intent = new Intent("com.bigbutton.homereceiver");
                            sendBroadcast(intent);
                        }

                    }else if(saveState.isFirst(mContext)){
                        Intent intent = new Intent("com.tapnchat.phonecontactsmainscreen");
                        sendBroadcast(intent);
                    }
                    try {
                        GlobalCommonValues.listBBContacts = new ArrayList<BBContactsBean>();
                        GlobalCommonValues.listBBContacts = DBQuery.getAllBBContacts(mContext);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    saveState.setRefrehContactList(mContext,false);
                    saveState.setIS_FROM_HOME(mContext,false);
                } else if (getContactListServer.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
                        getContactListServer.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1)) {


                    DBQuery.deleteTable("BBContacts", "", null, getApplicationContext());
                    //if tiles exists then update tiles value and reset them as a non tnc-user
                    if(DBQuery.getAllTiles(mContext).size()>0){
                        DBQuery.updateTileResetTnCUser(mContext);
                    }

                    if(!saveState.isFirst(mContext)){
                        Intent intentMainacivity = new Intent("com.tapnchat.phonecontactshomescreen");
                        sendBroadcast(intentMainacivity);
                        Intent intentHomeScreen = new Intent("com.tapnchat.phonecontactsmainscreen");
                        sendBroadcast(intentHomeScreen);
                    }else if(saveState.isFirst(mContext)){
                        Intent intent = new Intent("com.tapnchat.phonecontactsmainscreen");
                        sendBroadcast(intent);
                    }

                    if (saveState.IS_FROM_HOME(mContext)) {
                        Intent intent = new Intent("com.bigbutton.homereceiver");
                        sendBroadcast(intent);
                    }

                    // to make progress visibility gone
                    if (VerifyingRegistrationFragment.isNotify) {
                        Intent intent = new Intent("com.bigbutton.receiver");
                        sendBroadcast(intent);

                        intent = new Intent("com.bigbutton.receiver_contacts");
                        sendBroadcast(intent);
                    } else if (saveState.IS_FROM_HOME(mContext)) {
                        Intent intent = new Intent("com.bigbutton.homereceiver");
                        sendBroadcast(intent);
                    }


                    saveState.setRefrehContactList(mContext,false);
                    saveState.setIS_FROM_HOME(mContext,false);
                }
            } else {
            }
        } catch (Exception e) {
            e.getMessage();
            saveState.setRefrehContactList(mContext,false);
            saveState.setIS_FROM_HOME(mContext,false);
        }
    }
}
