package com.tnc.common;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.DefaultMessagesBeanDB;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.database.DBQuery;
import com.tnc.fragments.VerifyingRegistrationFragment;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.DefaultMessagesResponse;
import com.tnc.webresponse.EmergencyNumberRespnseBean;
import com.tnc.webresponse.GetBBIDResponseBeanData;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

/**
 * Created by a3logics on 23/1/18.
 */

public class GetBBID {

    private String TAG = GetBBID.class.getName();
    private Context mContext;
    private Gson gson;
    private SharedPreference saveState;
    private boolean isFromRegistration = false;

    private final String REQUEST_UPDATE_TILES               = "updateTiles";
    private final String REQUEST_UPDATE_EMERGENCY_NUMBERS   = "emergencyNumbers";
    private final String REQUEST_UPDATE_DEFAULT_MESSAGES    = "defaultMessages";

    private String requestType = REQUEST_UPDATE_TILES;

    ArrayList<CountryDetailsBean>    listCountries = new ArrayList<CountryDetailsBean>();
    ArrayList<DefaultMessagesBeanDB> mListDefaultMessages = new ArrayList<DefaultMessagesBeanDB>();

    public GetBBID(Context mContext, boolean isFromRegistration) {
        this.mContext = mContext;
        this.isFromRegistration = isFromRegistration;
        saveState = new SharedPreference();
    }

    public void getBBID() {

        try {
            gson = new Gson();
            MyHttpConnection.postHeaderWithoutJsonEntity(mContext,
                    GlobalCommonValues.GET_BBID, getBBIDResponseHandler,
                    mContext.getString(R.string.private_key),
                    saveState.getPublicKey(mContext));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to call web service to get the registered user details
    AsyncHttpResponseHandler getBBIDResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog(TAG, "OnSuccess",
                            response.toString());
                    getResponseBBID(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog(TAG, "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /*
	 * method to handle the response we got as a user details
	 * @param response
	 */
    private void getResponseBBID(String response) {
        try {
            String response2="";
            if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
                response2=response.substring(response.indexOf("user_id")-2,response.length());
            }
            else{
                response2=response;
            }

            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                GetBBIDResponseBeanData get_Response = gson.fromJson(response2,GetBBIDResponseBeanData.class);

                if (get_Response.is_activate == 0) {
                    // checking app registration to prevent backdoor entry of the registration
                    VerifyingRegistrationFragment.isAppUserRegistered = false;
                }
                else if (get_Response.is_activate == 1) {
                    saveState.setRegistered(mContext, true);
                    saveState.setCountryCode(mContext,get_Response.country_code);
                    saveState.setBBID(mContext, get_Response.user_id);
                    if(saveState.getEmergency(mContext).trim().equals(""))
                        saveState.setEmergency(mContext,get_Response.country_emergency);
                    saveState.setUserName(mContext, Uri.decode(get_Response.name));
                    saveState.setUserMailID(mContext, Uri.decode(get_Response.email));
                    //String number=get_Response.number.substring(get_Response.number.length()-10,get_Response.number.length());
                    saveState.setUserPhoneNumber(mContext,get_Response.number);
                    String iddCode= DBQuery.getIDDCodeDB(mContext, saveState.getCountryCode(mContext));
                    saveState.setCountryidd(mContext,iddCode);

                    if(get_Response.is_email_verified==1)
                        saveState.setIsVerified(mContext,true);
                    else{
                        saveState.setIsVerified(mContext,false);
                    }
                    if(get_Response.email!=null && !get_Response.email.trim().equals("") &&
                            !get_Response.email.trim().equalsIgnoreCase("null")	&& saveState.getUserMailID(mContext).trim().equals("")){
                        saveState.setUserMailID(mContext,Uri.decode(get_Response.email));
                    }

                    if(isFromRegistration){
                        //Update Tile Details in case of call is from registration process
                        if(DBQuery.getAllTiles(mContext).size()>0){
                            requestType = REQUEST_UPDATE_TILES;
                            updateData();
                        }
                    }


                    if(get_Response.image!=null && !get_Response.image.trim().equals("") && !get_Response.image.trim().equalsIgnoreCase("null"))
                    {
                    }

                    if (get_Response != null
                            && get_Response.is_default_image
                            .equalsIgnoreCase("no")
                            && !get_Response.image.trim().equals("")
                            && !get_Response.image.trim().equalsIgnoreCase(
                            "NULL")) {
                        saveState.setDisplayISDEFAULTIMAGEString(mContext, "true");
                        saveState.setDefaultImage(mContext, false);
                    }
                    else if (get_Response != null
                            && get_Response.is_default_image
                            .equalsIgnoreCase("yes")
                            && !get_Response.image.trim().equals("")
                            && !get_Response.image.trim().equalsIgnoreCase(
                            "NULL")) {
                        saveState.setDefaultImage(mContext, true);
                    }

                    //call the web service to fetch latest emergency numbers from the webservice
                    checkInternetConnectionEmergencyNumber();

                    //call the web service to fetch latest default messages from the webservice
                    checkInternetConnectionDefaultMessages();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to update tiles
     */
    private void updateData(){
        new UpdateTiles().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class UpdateTiles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if(requestType.equals(REQUEST_UPDATE_TILES)){
                ArrayList<ContactTilesBean> listTiles = DBQuery.getAllTiles(mContext);
                for(int i=0;i<listTiles.size();i++)
                {
                    String number=listTiles.get(i).getPhoneNumber();
                    String mCountryCode = listTiles.get(i).getCountryCode();
                    if(mCountryCode == null){
                        mCountryCode = "";
                    }
                    String tilePosition=listTiles.get(i).getTilePosition();
                    String numberSplitted=GlobalConfig_Methods.getBBNumberToCheck(mContext, mCountryCode + number);
                    String[] numberArray=numberSplitted.split(",");
                    String countryCode=numberArray[0];
                    String phoneNumber=numberArray[1];
                    String isdCode=listTiles.get(i).getPrefix()+numberArray[4];
                    DBQuery.updateTileDetailsOnRegistration(mContext,countryCode,isdCode,phoneNumber, tilePosition);
                }
            }else if(requestType.equals(REQUEST_UPDATE_EMERGENCY_NUMBERS)){
                //Delete the existing Emergency Number table from the database
                if(listCountries!=null && listCountries.size()>0){
                    DBQuery.deleteTable("EmergencyNumbers", "", null, mContext.getApplicationContext());
                    // Insert Emergency Number in the Database
                    DBQuery.insertAllCountryEmergencyNumbers(mContext,listCountries);
                }

            }else if(requestType.equals(REQUEST_UPDATE_DEFAULT_MESSAGES)){
                //Delete all the default messages of older version
                if(mListDefaultMessages!=null && mListDefaultMessages.size()>0){
                    DBQuery.deleteConfigMessageFromDB(mContext, 1);

                    // Insert Emergency Number in the Database
                    DBQuery.insertConfigMessageFromWebService(mContext,mListDefaultMessages);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
        }
    }

    /**
     * check availabitiy of internet connection for emergency number
     */
    private void checkInternetConnectionEmergencyNumber() {
        if (NetworkConnection.isNetworkAvailable(mContext)) {
            getEmergencyNumbersRequest();
        }
    }

    // method to call web service to get update emergency numbers from the web service
    private void getEmergencyNumbersRequest()
    {
        MyHttpConnection.getWithoutPara(mContext,GlobalCommonValues.GET_EMERGENCY_NUMBERS,
                mContext.getResources().getString(R.string.private_key),emergencyNumbersResponsehandler);
    }

    // Async task to call web service to get Emergency Numbers
    AsyncHttpResponseHandler emergencyNumbersResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            //			if ((!progress.isShowing()))
            //				progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null){
                    Logs.writeLog("Emergency Numbers", "OnSuccess",response.toString());
                    getResponseEmergencyNumbers(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("Emergency Numbers", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /*
     * Handling Response from the Server for the request being sent to get Emergency Numbers
     */
    private void getResponseEmergencyNumbers(String response) {
        try {
            if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
                try {
                    gson = new Gson();
                    EmergencyNumberRespnseBean get_Response = gson.fromJson(response,EmergencyNumberRespnseBean.class);
                    if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
                        if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
                        {
                            CountryDetailsBean mObjCountryDetailBean;
                            for(int i=0;i<get_Response.getData().size();i++){
                                mObjCountryDetailBean= new CountryDetailsBean();
                                mObjCountryDetailBean.setCountryName(get_Response.getData().get(i).getCountry());
                                mObjCountryDetailBean.setEmergency(get_Response.getData().get(i).getEmergency());
                                listCountries.add(mObjCountryDetailBean);
                            }
                            saveState.setIS_EMERGENCY_NUMBER_VERSION_UPDATED(mContext, false);

                            requestType = REQUEST_UPDATE_EMERGENCY_NUMBERS;

                            // call method to insert emergency numbers in a table
                            updateData();
                        }
                    }
                    else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){
                    }
                }
                catch (Exception e){
                    e.getMessage();
                }

            } else {
                //ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * check availability of internet connection for default messages
     */
    private void checkInternetConnectionDefaultMessages() {
        if (NetworkConnection.isNetworkAvailable(mContext)) {
            getInitMessagesRequest();
        }
    }

    //Method to call web service to get configured mesages from the server
    private void getInitMessagesRequest()
    {
        MyHttpConnection.getWithoutPara(mContext,GlobalCommonValues.GET_DEFAULT_MESSAGES,
                mContext.getResources().getString(R.string.private_key),defaultMessagesResponsehandler);
    }

    // Async task to call web service to get configured mesages
    AsyncHttpResponseHandler defaultMessagesResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            //			if ((!progress.isShowing()))
            //				progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null){
                    Logs.writeLog("Default Mesasges", "OnSuccess",response.toString());
                    getResponseDefaultMessages(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("Default Mesasges", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /*
     * Handling Response from the Server for the request being sent to get configured mesages
     */
    private void getResponseDefaultMessages(String response) {
        try {
            if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
                ArrayList<InitResponseMessageBean> listInitMessages=new ArrayList<InitResponseMessageBean>();
                try {
                    Gson gson = new Gson();
                    DefaultMessagesResponse get_Response = gson.fromJson(response,DefaultMessagesResponse.class);
                    if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){

                        if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
                        {
                            DefaultMessagesBeanDB mObjDefaultMessagesBeanDB;

                            int maxID = DBQuery.getConfigMessagesMaxCount(mContext);

                            if(maxID==-1 || maxID==0){
                                maxID = 1;
                            }else{
                                maxID=maxID+1;
                            }
                            for(int i=0;i<get_Response.getData().size();i++){
                                mObjDefaultMessagesBeanDB= new DefaultMessagesBeanDB();
                                mObjDefaultMessagesBeanDB.setId(maxID+i);
                                mObjDefaultMessagesBeanDB.setMessage(get_Response.getData().get(i).getMessage());
                                mObjDefaultMessagesBeanDB.setIsLocked(1);

                                String mType = get_Response.getData().get(i).getType();
                                int mTypeMessage = (mType.equals("initiation") ? 0: 1);  // 0- initiation  1- response

                                mObjDefaultMessagesBeanDB.setType(mTypeMessage);
                                mListDefaultMessages.add(mObjDefaultMessagesBeanDB);
                            }
                            saveState.setIS_DEFAULT_MESSAGES_VERSION_UPDATED(mContext, false);

                            requestType = REQUEST_UPDATE_DEFAULT_MESSAGES;

                            // Insert Default messages in database
                            updateData();
                        }
                    }
                    else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){

                    }
                }
                catch (Exception e){
                    e.getMessage();
                }

            } else {
                //ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}