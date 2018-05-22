package com.tnc.fragments;

import java.util.Timer;
import java.util.TimerTask;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * class to Display Dialpad to make a call/tile(tile/chat button on the home screen)  
 *  @author a3logics
 */

public class DialPad extends BaseFragmentTabs implements OnClickListener{

    private TextView tvTitle,tvDialPad,tvNumber,tvAlphabet,
            tvNumberSecond,tvNumberThird,tvNumberFourth,tvNumberFifth,tvNumberSixth,tvNumberSeventh,tvNumberEighth,tvNumberNinth;
    private ToggleButton toggleNumberMessage;
    private Button btnDisable,btnFirst,btnSecond,btnThird,btnFourth,btnFifth,btnSixth,btnSeventh,btnEighth,
            btnNinth,btnStar,btnZero,btnHash,btnCall,btnClear,btnCreateContact, btnCallDetails,
            btnClearAllNumbers;
    private EditText etNumber;
    private FrameLayout flBackArrow;
    private Button btnBack;
    private String gridType="number";
    private Context mContext;
    int lastId=-1;
    private LinearLayout llParentLayout;
    private INotifyGalleryDialog iNotifySelectTab;
    private LinearLayout llClear;
    private float x1, x2;
    private float y1, y2;
    private boolean isfirstTime=true;
    private boolean isLongClicked=false;
    private Timer timer;
    private TimerTask timerTask;
    private final int refresh = 1;
    private final int stoprefresh = 2;
    private final int donothing = 3;
    private boolean deletenumber_oncall;
    private MediaPlayer mp;
    private SoundPool soundPool;
    private  AudioManager audioManager;
    private ToneGenerator mToneGenerator;

    public DialPad newInstance(Context mContext,INotifyGalleryDialog iNotifySelectTab) {
        DialPad frag = new DialPad();
        this.mContext=mContext;
        this.iNotifySelectTab=iNotifySelectTab;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialpad, container, false);
        idInitialization(view);
        return view;
    }

    //define Ids of widgets/views
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        llParentLayout=(LinearLayout) view.findViewById(R.id.llParentLayout);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvDialPad=(TextView) view.findViewById(R.id.tvDialPad);
        tvNumber=(TextView) view.findViewById(R.id.tvNumber);
        tvAlphabet=(TextView) view.findViewById(R.id.tvAlphabet);
        btnDisable=(Button) view.findViewById(R.id.btnDisable);
        etNumber=(EditText) view.findViewById(R.id.etNumber);
        btnFirst=(Button) view.findViewById(R.id.btnFirst);
        btnSecond=(Button) view.findViewById(R.id.btnSecond);
        btnThird=(Button) view.findViewById(R.id.btnThird);
        btnFourth=(Button) view.findViewById(R.id.btnFourth);
        btnFifth=(Button) view.findViewById(R.id.btnFifth);
        btnSixth=(Button) view.findViewById(R.id.btnSixth);
        btnSeventh=(Button) view.findViewById(R.id.btnSeventh);
        btnEighth=(Button) view.findViewById(R.id.btnEighth);
        btnNinth=(Button) view.findViewById(R.id.btnNinth);
        btnZero=(Button) view.findViewById(R.id.btnZero);
        btnStar=(Button) view.findViewById(R.id.btnStar);
        btnHash=(Button) view.findViewById(R.id.btnHash);
        btnCall=(Button) view.findViewById(R.id.btnCall);
        btnClear=(Button) view.findViewById(R.id.btnClear);
        llClear=(LinearLayout)view.findViewById(R.id.llClear);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        btnCreateContact=(Button) view.findViewById(R.id.btnCreateContact);
        btnCallDetails=(Button) view.findViewById(R.id.btnCallDetails);
        btnClearAllNumbers=(Button) view.findViewById(R.id.btnClearAllNumbers);
        tvNumberSecond=(TextView)view.findViewById(R.id.tvNumberSecond);
        tvNumberThird=(TextView)view.findViewById(R.id.tvNumberThird);
        tvNumberFourth=(TextView)view.findViewById(R.id.tvNumberFourth);
        tvNumberFifth=(TextView)view.findViewById(R.id.tvNumberFifth);
        tvNumberSixth=(TextView)view.findViewById(R.id.tvNumberSixth);
        tvNumberSeventh=(TextView)view.findViewById(R.id.tvNumberSeventh);
        tvNumberEighth=(TextView)view.findViewById(R.id.tvNumberEighth);
        tvNumberNinth=(TextView)view.findViewById(R.id.tvNumberNinth);
        tvNumberSecond.setVisibility(View.GONE);
        tvNumberThird.setVisibility(View.GONE);
        tvNumberFourth.setVisibility(View.GONE);
        tvNumberFifth.setVisibility(View.GONE);
        tvNumberSixth.setVisibility(View.GONE);
        tvNumberSeventh.setVisibility(View.GONE);
        tvNumberEighth.setVisibility(View.GONE);
        tvNumberNinth.setVisibility(View.GONE);
        btnFirst.setSoundEffectsEnabled(false);
        btnSecond.setSoundEffectsEnabled(false);
        btnThird.setSoundEffectsEnabled(false);
        btnFourth.setSoundEffectsEnabled(false);
        btnFifth.setSoundEffectsEnabled(false);
        btnSixth.setSoundEffectsEnabled(false);
        btnSeventh.setSoundEffectsEnabled(false);
        btnEighth.setSoundEffectsEnabled(false);
        btnNinth.setSoundEffectsEnabled(false);
        btnZero.setSoundEffectsEnabled(false);
        btnStar.setSoundEffectsEnabled(false);
        btnHash.setSoundEffectsEnabled(false);
        btnClear.setSoundEffectsEnabled(false);
        flBackArrow.setVisibility(View.VISIBLE);
        toggleNumberMessage=(ToggleButton)view.findViewById(R.id.toggleNumberMessage);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvDialPad, "fonts/Roboto-Bold_1.ttf");
//        CustomFonts.setFontOfTextView(getActivity(),tvDialPad, "fonts/Roboto-Bold_1.ttf");
//        CustomFonts.setFontOfTextView(getActivity(),tvDialPad, "fonts/Roboto-Bold_1.ttf");
        btnDisable.setVisibility(View.GONE);

        // Display Enable Call Log Detail Screen only for the registered user
        /*if(saveState.isRegistered(getActivity())){
            btnCallDetails.setVisibility(View.VISIBLE);
        }else{
            btnCallDetails.setVisibility(View.GONE);
        }*/

        btnCallDetails.setVisibility(View.GONE);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        audioManager = (AudioManager) mActivityTabs.getSystemService(Context.AUDIO_SERVICE);
        int volume_level= audioManager.getStreamVolume(AudioManager.STREAM_RING); // Highest Ring volume level is 7, lowest is 0
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 14); // Raising volume to 100% (For eg. 7 * 14 ~ 100)


        llParentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnFirst.setOnClickListener(this);
        btnSecond.setOnClickListener(this);
        btnThird.setOnClickListener(this);
        btnFourth.setOnClickListener(this);
        btnFifth.setOnClickListener(this);
        btnSixth.setOnClickListener(this);
        btnSeventh.setOnClickListener(this);
        btnEighth.setOnClickListener(this);
        btnNinth.setOnClickListener(this);
        btnZero.setOnClickListener(this);
        btnStar.setOnClickListener(this);
        btnHash.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnCreateContact.setOnClickListener(this);
        btnCallDetails.setOnClickListener(this);
        btnClearAllNumbers.setOnClickListener(this);

        //btnFirst.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));

        btnClear.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                playSound();
                isLongClicked=true;
                enableRefreshing();
                return true;
            }
        });


        btnClear.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isLongClicked=false;
                        disableRefreshing();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        btnStar.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                playSound();
                setNumber(",");
                return true;
            }
        });

        btnZero.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setNumber("+");
                return true;
            }
        });

        if(isfirstTime)
        {
            setNumberGridAdapter(gridType);
        }
        toggleNumberMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lastId=-1;
                if(isChecked)  //On ABC Side
                {
                    tvAlphabet.setTextColor(Color.parseColor("#1a649f"));
                    tvNumber.setTextColor(Color.parseColor("#929598"));
                    tvNumberSecond.setVisibility(View.VISIBLE);
                    tvNumberThird.setVisibility(View.VISIBLE);
                    tvNumberFourth.setVisibility(View.VISIBLE);
                    tvNumberFifth.setVisibility(View.VISIBLE);
                    tvNumberSixth.setVisibility(View.VISIBLE);
                    tvNumberSeventh.setVisibility(View.VISIBLE);
                    tvNumberEighth.setVisibility(View.VISIBLE);
                    tvNumberNinth.setVisibility(View.VISIBLE);
                    gridType="alphabet";
                }
                else           //On 123 Side
                {
                    tvNumber.setTextColor(Color.parseColor("#1a649f"));
                    tvAlphabet.setTextColor(Color.parseColor("#929598"));
                    gridType="number";
                    tvNumberSecond.setVisibility(View.GONE);
                    tvNumberThird.setVisibility(View.GONE);
                    tvNumberFourth.setVisibility(View.GONE);
                    tvNumberFifth.setVisibility(View.GONE);
                    tvNumberSixth.setVisibility(View.GONE);
                    tvNumberSeventh.setVisibility(View.GONE);
                    tvNumberEighth.setVisibility(View.GONE);
                    tvNumberNinth.setVisibility(View.GONE);

                }
                setNumberGridAdapter(gridType);
            }
        });

        tvAlphabet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (toggleNumberMessage.isChecked()) {
                    toggleNumberMessage.setChecked(false);
                } else if (!toggleNumberMessage.isChecked()) {
                    toggleNumberMessage.setChecked(true);
                }
            }
        });

        tvNumber.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (toggleNumberMessage.isChecked()) {
                    toggleNumberMessage.setChecked(false);
                } else if (!toggleNumberMessage.isChecked()) {
                    toggleNumberMessage.setChecked(true);
                }
            }
        });

        toggleNumberMessage.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent touchevent) {
                switch (touchevent.getAction()) {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN: {
                        x1 = touchevent.getX();
                        y1 = touchevent.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = touchevent.getX();
                        y2 = touchevent.getY();

                        if (x1 == x2) {
                            if (toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(false);
                            } else if (!toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(true);
                            }
                        } else if (y1 == y2) {
                            if (toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(false);
                            } else if (!toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(true);
                            }
                        }

                        // if left to right sweep event on screen
                        else if (x1 < x2) {
                            if (toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(false);
                            } else if (!toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(true);
                            }
                        }

                        // if right to left sweep event on screen
                        else if (x1 > x2) {
                            if (toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(false);
                            } else if (!toggleNumberMessage.isChecked()) {
                                toggleNumberMessage.setChecked(true);
                            }
                        }

                        // if UP to Down sweep event on screen
                        else if (y1 < y2) {
                        }

                        // if Down to UP sweep event on screen
                        else if (y1 > y2) {
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void deleteNumber()
    {
        if(!etNumber.getText().toString().trim().equals("") && isLongClicked)
        {
            String strNumber=etNumber.getText().toString();
            String displayNumber=strNumber.substring(0,strNumber.length()-1);
            etNumber.setText(displayNumber);
        }
        else if(etNumber.getText().toString().trim().equals(""))
        {
            disableRefreshing();
        }
    }

    private void enableRefreshing() {
        try {

            if (timer != null) {
                if (timerTask != null) {
                    timer.schedule(timerTask, 0, 500);
                } else {
                    timerTask.cancel();
                    timerTask = null;
                    timer.cancel();
                    timer = null;
                    initTimerTask();
                    timer.schedule(timerTask, 0, 500);
                }

            } else {
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                initTimerTask();
                timer.schedule(timerTask, 0, 500);
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void disableRefreshing() {
        try {

            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void initTimerTask() {
        try {

            if (timer == null)
                timer = new Timer();
            if (timerTask == null)
                timerTask = new TimerTask()
                {
                    @Override
                    public void run() {
                        try {

                            if (!deletenumber_oncall)
                            {
                                mActivityTabs.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(isLongClicked)
                                        {
                                            final Message msg = Message.obtain(
                                                    messagerefresh_handler, refresh, null);
                                            messagerefresh_handler.dispatchMessage(msg);
                                        }
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler messagerefresh_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case refresh:
                        deleteNumber();
                        break;
                    case stoprefresh:
                        break;
                    case donothing:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        isfirstTime=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(iNotifySelectTab!=null)
            iNotifySelectTab.yes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFirst:
                playSound();
                setNumber(btnFirst.getText().toString());
                break;

            case R.id.btnSecond:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnSecond.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("2");
                }
                break;

            case R.id.btnThird:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnThird.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("3");
                }
                break;

            case R.id.btnFourth:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnFourth.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("4");
                }
                break;

            case R.id.btnFifth:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnFifth.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("5");
                }
                break;

            case R.id.btnSixth:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnSixth.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("6");
                }
                break;

            case R.id.btnSeventh:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnSeventh.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("7");
                }
                break;

            case R.id.btnEighth:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnEighth.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("8");
                }
                break;

            case R.id.btnNinth:
                playSound();
                if(gridType.equals("number"))
                {
                    setNumber(btnNinth.getText().toString());
                }
                else if(gridType.equals("alphabet")){
                    setNumber("9");
                }
                break;

            case R.id.btnZero:
                playSound();
                setNumber(btnZero.getText().toString());
                break;

            case R.id.btnStar:
                playSound();
                setNumber(btnStar.getText().toString());
                break;

            case R.id.btnHash:
                playSound();
                setNumber(btnHash.getText().toString());
                break;
            case R.id.btnCall:
                if(gridType.equals("number") || gridType.equals("alphabet"))
                {
                    if(!etNumber.getText().toString().trim().equals(""))
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+etNumber.getText().toString()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                        //update boolean value in preference as number is dialled from the app
                        saveState.setIS_NUMBER_DIALLED(getActivity(), true);
                        mActivityTabs.startActivity(intent);
                    }
                    else if(etNumber.getText().toString().trim().equals(""))
                    {
                        ImageRequestDialog dialogError=new ImageRequestDialog();
                        dialogError.newInstance("",mActivityTabs,"Please enter the  number to dial","",null);
                        dialogError.setCancelable(false);
                        dialogError.show(getChildFragmentManager(), "test");
                    }
                }
                break;

            case R.id.btnClear:
                playSound();
                if(!etNumber.getText().toString().trim().equals(""))
                {
                    String strNumber=etNumber.getText().toString();
                    String displayNumber=strNumber.substring(0,strNumber.length()-1);
                    etNumber.setText(displayNumber);
                }
                break;

            case R.id.btnBack:
                if(mActivityTabs instanceof HomeScreenActivity)
                    ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
                break;

            case R.id.btnCreateContact:
                TileContactDetailsFragment objTileContactDetailsFragment=null;
                String contact_Number=etNumber.getText().toString();
                objTileContactDetailsFragment=new TileContactDetailsFragment();
                if(saveState.isRegistered(mActivityTabs))
                {
                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,GlobalConfig_Methods.getBBNumberToCheck(mActivityTabs,contact_Number), null,"",
                            contact_Number,null,true);
                }
                else {
                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,null,contact_Number,"",contact_Number,null,true);
                }
                ((HomeScreenActivity)mActivityTabs).setFragment(objTileContactDetailsFragment);
                //			}

                break;

            case R.id.btnCallDetails:

                // Go to Call Details Screen
                CallDetailsFragment mCallDetailsFragment = new CallDetailsFragment();
                mCallDetailsFragment.newInstance(getActivity(), null);
                if(mActivityTabs instanceof MainBaseActivity){
                    ((MainBaseActivity)mActivityTabs).setFragment(mCallDetailsFragment);
                }else if(mActivityTabs instanceof HomeScreenActivity){
                    ((HomeScreenActivity)mActivityTabs).setFragment(mCallDetailsFragment);
                }

                break;

            case R.id.btnClearAllNumbers:

                // Clear all Numbers from the textbox
                etNumber.setText("");
                break;

            default:
                break;
        }
        lastId=v.getId();
    }

    /**
     * Method to play sound on button click
     */
    private void playSound()
    {

        mToneGenerator.stopTone();
        mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 100); // play sound for 100ms

        /*if(soundPool == null)
            soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 0);
        *//** soundId for Later handling of sound pool **//*
        int soundId = soundPool.load(mActivityTabs, R.raw.telephone_speakerphone_dial_button_single, 1); // in 2nd param u have to pass your desire ringtone

        soundPool.play(soundId, 1, 1, 0, 0, 1);

        MediaPlayer mPlayer = MediaPlayer.create(mActivityTabs, R.raw.telephone_speakerphone_dial_button_single); // in 2nd param u have to pass your desire ringtone
        mPlayer.start();*/
    }

    /**
     * Set dislpay number in edittext for dialing
     * @param number
     */
    private void setNumber(String number)
    {
        etNumber.setText(etNumber.getText().toString()+number);
        etNumber.setCursorVisible(true);
    }

    /**
     * Set View of dial-pad grid
     * @param gridType
     */
    private void setNumberGridAdapter(String gridType)
    {
        if(gridType.equals("number"))
        {
            btnSecond.setText("2");
            btnThird.setText("3");
            btnFourth.setText("4");
            btnFifth.setText("5");
            btnSixth.setText("6");
            btnSeventh.setText("7");
            btnEighth.setText("8");
            btnNinth.setText("9");
//            btnFirst.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            /*btnSecond.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnThird.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnFourth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnFifth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnSixth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnSeventh.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnEighth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnNinth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnZero.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));*/
        }
        else if(gridType.equals("alphabet"))
        {
            btnSecond.setText("ABC");
            btnThird.setText("DEF");
            btnFourth.setText("GHI");
            btnFifth.setText("JKL");
            btnSixth.setText("MNO");
            btnSeventh.setText("PQRS");
            btnEighth.setText("TUV");
            btnNinth.setText("WXYZ");
            //			btnFirst.setTextSize(dpToPx(10));
//            btnFirst.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
           /* btnSecond.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnThird.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnFourth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnFifth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnSixth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnSeventh.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnEighth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));
            btnNinth.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.txtSizeSmall));*/
            //			btnFirst.setTextSize(dpToPx(10));
        }
    }

    public int dpToPx(int dp) {
        int px = dp;
        if(getActivity()!=null){
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
        return px;
    }
}
