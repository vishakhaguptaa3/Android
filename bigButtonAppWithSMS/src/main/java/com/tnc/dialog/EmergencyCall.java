package com.tnc.dialog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.bean.MessageSendBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EmergencyCall extends DialogFragment implements OnClickListener
{
    private TextView tvTitle,tvMessage,tvMessageSub;
    public Button btnYes,btnNo;
    private Context mAct;
    private String title="",message="",messageSub="", mBBIDS = "";
    private Gson gson;
    private SharedPreference saveState;

    public EmergencyCall newInstance(String title, Context mAct,String message,
                                     String messageSub)
    {
        this.mAct = mAct;
        this.title=title;
        this.message=message;
        this.messageSub=messageSub;
        EmergencyCall frag = new EmergencyCall();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.6f;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.backup_confirmation, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvMessage=(TextView) view.findViewById(R.id.tvMessage);
        tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
        btnYes = (Button) view.findViewById(R.id.btnYes);
        btnNo= (Button) view.findViewById(R.id.btnNo);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");

        saveState = new SharedPreference();

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        updateView();
        return view;
    }

    private void updateView()
    {
        tvMessage.setText(message);
        btnYes.setText("Call");
        tvTitle.setVisibility(View.GONE);
        tvMessageSub.setVisibility(View.GONE);
    }

    /**
     * Method to notify emergency contacts
     */
    private void notifyEmergencyContacts(){
        mBBIDS = DBQuery.getBBIDForEmergencyNotification(mAct);

        MessageSendBean mMessageSendBean = new MessageSendBean();
        mMessageSendBean.setTo_user_id(mBBIDS);
        // check if GPS Setting is ON otherwise send empty message

        if(GlobalConfig_Methods.isGPSEnabled(mAct) && (GlobalCommonValues.mLongitude!=0.0 && GlobalCommonValues.mLatitude!=0.0)){
            if(mAct!=null)
                mMessageSendBean.setMessage(mAct.getResources().getString(R.string.txtMapMessageContent) +
                        GlobalCommonValues.mLatitude + "," + GlobalCommonValues.mLongitude);
        }

        try {
            gson = new Gson();
            String stingGson = gson.toJson(mMessageSendBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mAct,
                    GlobalCommonValues.SEND_NOTIFICATION_TO_EMERGENCY_CONTACT, stringEntity,
                    sendMessageResponsehandler,
                    mAct.getString(R.string.private_key),
                    saveState.getPublicKey(mAct));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * async task to send request paramaters to the web service for sending the chat message
     */
    AsyncHttpResponseHandler sendMessageResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null)
                {
                    Logs.writeLog("EmergencyMessageGetResponse","OnSuccess",response.toString());
                    //getResponseSendMessage(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null){
                Logs.writeLog("EmergencyMessageGetResponse","onFailure",response);
            }
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.btnYes)
        {
            dismiss();

            //notify emergency contacts if the user is registered
            if(mAct!=null && saveState.isRegistered(mAct) && saveState.getIsEmergencyContactNotification(mAct))
                notifyEmergencyContacts();

            SharedPreference saveState=new SharedPreference();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+saveState.getEmergency(mAct)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            mAct.startActivity(intent);

        }
        else if(v.getId()==R.id.btnNo)
        {
            dismiss();
        }
    }
}
