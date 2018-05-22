package com.tnc.widgets;

import java.lang.reflect.Method;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tnc.R;
import com.tnc.activities.CustomDialerScreen;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.interfaces.INotifyGalleryDialog;

public class HUDView extends RelativeLayout implements View.OnClickListener{

    private Context mContext;

    public LinearLayout llImageBorder;

    private FrameLayout flBackArrow;

    public TextView tvTitle, tvStatus, tvContactName, tvContactNumber;

    private Button btnBack, btnWifi, btnBluetooth, btnVolumeIncrease, btnVolumeDecrease, btnSpeaker,
            btnVoiceMail, btnAddCall, btnContactsList, btnDialpad, btnMute,
            btnHangUp, btnHold;

    public Button btnSendMessage;

    private ImageView imViewUserImage;
    public  ImageView imViewEmergency;

    public HUDView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public HUDView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HUDView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.activity_call_screen, this);

        tvTitle         = (TextView) v.findViewById(R.id.tvTitle);
        tvStatus        = (TextView) v.findViewById(R.id.tvStatus);
        tvContactName   = (TextView) findViewById(R.id.tvContactName);
        tvContactNumber = (TextView) findViewById(R.id.tvContactNumber);

        llImageBorder= (LinearLayout) findViewById(R.id.llImageBorder);

        flBackArrow=(FrameLayout)findViewById(R.id.flBackArrow);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnWifi = (Button) findViewById(R.id.btnWifi);
        btnBluetooth = (Button) findViewById(R.id.btnBluetooth);
        btnVolumeIncrease = (Button) findViewById(R.id.btnVolumeIncrease);
        btnVolumeDecrease = (Button) findViewById(R.id.btnVolumeDecrease);
        btnSpeaker = (Button) findViewById(R.id.btnSpeaker);
        btnVoiceMail = (Button) findViewById(R.id.btnVoiceMail);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        btnAddCall = (Button) findViewById(R.id.btnAddCall);
        btnContactsList = (Button) findViewById(R.id.btnContactsList);
        btnDialpad = (Button) findViewById(R.id.btnDialpad);
        btnMute = (Button) findViewById(R.id.btnMute);
        btnHangUp = (Button) findViewById(R.id.btnHangUp);
        btnHold = (Button) findViewById(R.id.btnHold);

        imViewUserImage = (ImageView) findViewById(R.id.imViewUserImage);
        imViewEmergency = (ImageView) findViewById(R.id.imViewEmergency);

        flBackArrow.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);

        CustomFonts.setFontOfTextView(mContext, tvTitle,
                "fonts/comic_sans_ms_regular.ttf");

        CustomFonts.setFontOfTextView(mContext, tvStatus,
                "fonts/Roboto-Bold_1.ttf");

        // Handle Click Listeners

        btnBack.setOnClickListener(this);
        btnVolumeIncrease.setOnClickListener(this);
        btnVolumeDecrease.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
        //btnVoiceMail.setOnClickListener(this);
//      btnSendMessage.setOnClickListener(this);
        btnAddCall.setOnClickListener(this);
        btnContactsList.setOnClickListener(this);
        btnDialpad.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        btnHangUp.setOnClickListener(this);
        btnHold.setOnClickListener(this);

        // Set  Buttons images as per their enable/disable state
//        toggleButtonState(btnVoiceMail);
//        toggleButtonState(btnSendMessage);
//        toggleButtonState(btnAddCall);
//        toggleButtonState(btnContactsList);
//        toggleButtonState(btnDialpad);

        /**
         * Call Method to set bluetooth / Wi-Fi Button State
         */
        setWifiBluetoothState();

        /**
         * Call Method to update loud - speaker Button State
         */
        updateLoudSpeakerButtonStatus();
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //system.out.println(event.getKeyCode());
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Log.d("back button pressed", "back button pressed");

            if (mContext != null) {
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                wm.removeView(this);
            }

            return true;
        }
        return super.dispatchKeyEvent(event);
    }*/

    /**
     * enable / disable button
     */
    private void toggleButtonState(Button mButton){

        switch (mButton.getId()){
//            case R.id.btnVolumeIncrease:
//                break;
//            case R.id.btnVolumeDecrease:
//                break;
//            case R.id.btnSpeaker:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_speaker_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_speaker_disbaled));
//                }
//                break;
//            case R.id.btnVoiceMail:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_voice_mail_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_voice_mail_disabled));
//                }
//                break;
//            case R.id.btnSendMessage:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_send_message_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_send_message_disabled));
//                }
//                break;
//            case R.id.btnAddCall:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_add_call_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_add_call_disabled));
//                }
//                break;
//            case R.id.btnContactsList:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_contacts_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_contacts_disabled));
//                }
//                break;
//            case R.id.btnDialpad:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_dialpad));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_dialpad_disabled));
//                }
//                break;
//            case R.id.btnHold:
//                if(mButton.isEnabled()){
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_hold_enabled));
//                }else{
//                    mButton.setBackground(getResources().getDrawable(R.drawable.ic_hold_disabled));
//                }
//                break;
        }
    }

    /**
     * Method to set bluetooth / wifi button state
     */
    private void setWifiBluetoothState(){
        if(NetworkConnection.isWifiAvailable(mContext)){
            btnWifi.setBackground(getResources().getDrawable(R.drawable.ic_wifi));
        }else{
            btnWifi.setBackground(getResources().getDrawable(R.drawable.ic_wifi_disabled));
        }

        if(GlobalConfig_Methods.isBluetoothAvailable()){
            btnBluetooth.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth));
        }else{
            btnBluetooth.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth_disabled));
        }
    }

    /**
     * update loud speaker button status
     */
    private void updateLoudSpeakerButtonStatus(){
        if(GlobalConfig_Methods.isLoudSpeakerEnabled(mContext)){
            btnSpeaker.setBackground(getResources().getDrawable(R.drawable.ic_speaker_enabled));
        }else{
            btnSpeaker.setBackground(getResources().getDrawable(R.drawable.ic_speaker_disbaled));
        }
    }

    /**
     * update hold button status
     */
    private void updateHoldButtonStatus(){
        if(GlobalConfig_Methods.isLoudSpeakerEnabled(mContext)){
            btnHold.setBackground(getResources().getDrawable(R.drawable.ic_hold_enabled));
        }else{
            btnHold.setBackground(getResources().getDrawable(R.drawable.ic_hold_disabled));
        }
    }

    /**
     * update mute button status
     */
    private void updateMuteButtonStatus(){
        if(GlobalConfig_Methods.isMicroPhoneMute(mContext)){
            btnMute.setBackground(getResources().getDrawable(R.drawable.ic_mute_enabled));
        }else{
            btnMute.setBackground(getResources().getDrawable(R.drawable.ic_mute_disabled));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                ((CustomDialerScreen)mContext).finish();
                break;
            case R.id.btnVolumeIncrease:
                break;
            case R.id.btnVolumeDecrease:
                break;
            case R.id.btnSpeaker:
                GlobalConfig_Methods.enableDisableLoudSpeakerCall(mContext);
                // Method to update loudspeaker status
                updateLoudSpeakerButtonStatus();
                break;
            case R.id.btnVoiceMail:
                // ask user to send the voice mail

                // make Alert Dialog overlaying all the other windows on the app
                final WindowManager manager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.alpha = 1.0f;
                layoutParams.packageName = mContext.getPackageName();
                layoutParams.buttonBrightness = 1f;
                layoutParams.windowAnimations = android.R.style.Animation_Dialog;

                final View view = View.inflate(mContext.getApplicationContext(),R.layout.messagedeleteconfirmation, null);

                TextView tvMessage  = (TextView) view.findViewById(R.id.tvMessage);
                Button yesButton    = (Button) view.findViewById(R.id.btnYes);
                Button noButton     = (Button) view.findViewById(R.id.btnNo);

                tvMessage.setText(mContext.getResources().getString(R.string.txtVoiceMailSendingConfirmation));

                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        iNotifyVoiceMail.yes();
                        manager.removeView(view);
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        manager.removeView(view);
                    }
                });
                manager.addView(view, layoutParams);
                break;
//            btnSendMessage
            case R.id.btnAddCall:
                break;
            case R.id.btnContactsList:
                break;
            case R.id.btnDialpad:
                break;
            case R.id.btnMute:
                GlobalConfig_Methods.enableDisableMicroPhoneMute(mContext);
                // Method to update microphone status
                updateMuteButtonStatus();
                break;
            case R.id.btnHangUp:
                GlobalConfig_Methods.disconnectCall();
                ((CustomDialerScreen)mContext).finish();
                break;
            case R.id.btnHold:
                break;
        }
    }

    /**
     * Interface to handle response in case user needs to send trhe voice mail
     */
    INotifyGalleryDialog iNotifyVoiceMail = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // In case user clicks yes to send the voice mail

            //disconnect the current call
            GlobalConfig_Methods.disconnectCall();
        }

        @Override
        public void no() {

        }
    };

}
