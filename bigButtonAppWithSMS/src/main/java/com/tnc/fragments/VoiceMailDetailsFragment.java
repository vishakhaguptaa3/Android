package com.tnc.fragments;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CallLogDetailsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CallDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by a3logics on 26/4/17..
 */

public class VoiceMailDetailsFragment extends BaseFragmentTabs implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener{

    private FrameLayout  flBackArrow;

    private LinearLayout llParentLayout, llImageBorder, llVolumeBarLevelContainer;

    private TextView     tvTitle, tvCallDetail, tvContactName, tvContactNumber, tvContactTime,
            tvRecordCurrentDurationLabel, tvRecordTotalDurationLabel;

    private ImageView    imViewUserImage;

    private Button       btnBack, btnCallUserNumber, btnChatUserNumber, btnVolumeIncrease, btnVolumeDecrease,
            btnShareVoiceMail, btnPlayRecording, btnStopRecording, btnDeleteVoiceMail;

    private ListView     lvVoiceMessageDetails;

    private ScrollView   svMediaPlayer;

    private SeekBar      seekBarRecording;

    private CallLogDetailsAdapter adapterCallLogDetails;

    private ArrayList<CallDetailsBean> mListUserCallLogDataBean;

    private String mNumber = "";

    private Context mContext;

    private  View        volumeLevelViews = null;

    private int          MAX_VOLUME_LEVEL = 0, CURRENT_VOLUME_LEVEL = 0;

    private AudioManager audioManager;

    private Handler      mHandler;

    private MediaPlayer  mediaPlayer = null;

    private SettingsContentObserver mSettingsContentObserver = null;

    public VoiceMailDetailsFragment newInstance(Context mContext, String mNumber){
        VoiceMailDetailsFragment frag = new VoiceMailDetailsFragment();
        this.mContext = mContext;
        this.mNumber  = mNumber;
        Bundle args   = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_mail_details_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState                    = new SharedPreference();

        mHandler                      = new Handler();

        tvTitle                      = (TextView) view.findViewById(R.id.tvTitle);
        tvCallDetail                 = (TextView) view.findViewById(R.id.tvCallDetail);
        tvContactName                = (TextView) view.findViewById(R.id.tvContactName);
        tvContactNumber              = (TextView) view.findViewById(R.id.tvContactNumber);
        tvContactTime                = (TextView) view.findViewById(R.id.tvContactTime);
        tvRecordCurrentDurationLabel = (TextView) view.findViewById(R.id.tvRecordCurrentDurationLabel);
        tvRecordTotalDurationLabel   = (TextView) view.findViewById(R.id.tvRecordTotalDurationLabel);

        flBackArrow                  = (FrameLayout) view.findViewById(R.id.flBackArrow);
        llParentLayout               = (LinearLayout) view.findViewById(R.id.llParentLayout);
        llImageBorder                = (LinearLayout) view.findViewById(R.id.llImageBorder);
        llVolumeBarLevelContainer    = (LinearLayout) view.findViewById(R.id.llVolumeBarLevelContainer);

        imViewUserImage              = (ImageView) view.findViewById(R.id.imViewUserImage);

        svMediaPlayer                = (ScrollView) view.findViewById(R.id.svMediaPlayer);

        btnBack                      = (Button) view.findViewById(R.id.btnBack);
        btnCallUserNumber            = (Button) view.findViewById(R.id.btnCallUserNumber);
        btnChatUserNumber            = (Button) view.findViewById(R.id.btnChatUserNumber);
        btnVolumeIncrease            = (Button) view.findViewById(R.id.btnVolumeIncrease);
        btnVolumeDecrease            = (Button) view.findViewById(R.id.btnVolumeDecrease);
        btnShareVoiceMail            = (Button) view.findViewById(R.id.btnShareVoiceMail);
        btnPlayRecording             = (Button) view.findViewById(R.id.btnPlayRecording);
        btnStopRecording             = (Button) view.findViewById(R.id.btnStopRecording);
        btnDeleteVoiceMail           = (Button) view.findViewById(R.id.btnDeleteVoiceMail);

        lvVoiceMessageDetails        = (ListView) view.findViewById(R.id.lvCallDetails);
        seekBarRecording             = (SeekBar) view.findViewById(R.id.seekBarRecording);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        flBackArrow.setVisibility(View.VISIBLE);

        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvCallDetail, "fonts/Roboto-Bold_1.ttf");

        llImageBorder.setVisibility(View.VISIBLE);
        btnChatUserNumber.setBackgroundResource(R.drawable.ic_chat_mode_enabled);

        btnBack.setOnClickListener(this);
        btnCallUserNumber.setOnClickListener(this);
        btnChatUserNumber.setOnClickListener(this);
        llParentLayout.setOnClickListener(this);
        btnVolumeIncrease.setOnClickListener(this);
        btnVolumeDecrease.setOnClickListener(this);

        // Get Maximum Volume Level
        MAX_VOLUME_LEVEL    = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Get Current Volume Level
        CURRENT_VOLUME_LEVEL = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Call method to display volume bar level
        displayVolumeBarLevel();

        seekBarRecording.setOnSeekBarChangeListener(this);

        seekBarRecording.setProgress(0);

        // initialize media player and play audio from url
        /*try{
            Uri mp4 = Uri.parse(GlobalConfig_Methods.getGreetingMessageFilePath(getActivity()).getAbsolutePath());
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getActivity(), mp4);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)

            // set maximum time for media player
            String mGreetingMessageTime =
                    GlobalConfig_Methods.milliSecondsToTimer(mediaPlayer.getDuration());

            tvRecordTotalDurationLabel.setText(mGreetingMessageTime);
        }catch(Exception e){
            e.getMessage();
        }*/
    }

    @Override
    public void onPause() {
        // Un - Register class to handle volume up and volume down operations
        getActivity().getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);
        super.onPause();
    }

    @Override
    public void onResume() {
        // Register class to handle volume up and volume down operations
        if(mSettingsContentObserver == null)
            mSettingsContentObserver = new SettingsContentObserver( new Handler() );
        getActivity().getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // reset media player status

        resetMediaPlayerStatus();

        super.onDestroy();
    }

    /**
     * Method to update media player status
     */
    private void resetMediaPlayerStatus(){
        // stop the media player and reset
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            //call method to update loud speaker button icons
//            updateLoudSpeakerIcons(false);
        }
    }

    /**
     * Method to update media player icons in case of start / stop media player
     * @param isMediaPlayerPlaying
     */
    private void updatePlayerIcons(boolean isMediaPlayerPlaying){
        if(isMediaPlayerPlaying){
            btnPlayRecording.setBackgroundResource(R.drawable.ic_play_recording_enabled);
        }else{
            btnPlayRecording.setBackgroundResource(R.drawable.ic_play_recording_disabled);
            //Call Method to update media player status
            resetMediaPlayerStatus();
        }
    }

    /**
     * Method to play recorded greeting
     */
    private void playRecordedGreeting(){
        try {

            if(GlobalConfig_Methods.isGreetingMessageExist(getActivity())){
//                Uri mp4 = Uri.parse(GlobalConfig_Methods.getGreetingMessageFilePath(getActivity()).getAbsolutePath());
//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(getActivity(), mp4);
//                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();

                // Updating progress / seek bar
                updateProgressBar();

                // update media player icon when player audio starts
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Call Method to update media player icons in case of start / stop media player
                        updatePlayerIcons(true);
                    }
                });

                // update media player icon when player audio stops
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Call Method to update media player icons in case of start / stop media player
                        updatePlayerIcons(false);
                    }
                });
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * class to handle volume up and volume down operations
     */
    class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v(LOG_TAG, "Settings change detected");

            // Update Current Volume Value
            CURRENT_VOLUME_LEVEL = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            // Update Volume Bar Level
            displayVolumeBarLevel();
        }
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            tvRecordTotalDurationLabel.setText(""+GlobalConfig_Methods.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tvRecordCurrentDurationLabel.setText(""+GlobalConfig_Methods.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(GlobalConfig_Methods.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBarRecording.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     * Method to display volume bar level
     */
    private void displayVolumeBarLevel(){

        // Clear previous views, if any
        if(llVolumeBarLevelContainer!=null){
            llVolumeBarLevelContainer.removeAllViews();
        }

        for (int mSoundLevel = 0; mSoundLevel < MAX_VOLUME_LEVEL; mSoundLevel++){

            volumeLevelViews        = getActivity().getLayoutInflater().inflate(R.layout.volume_bar_level, null);

            ImageView imViewVolumeLevel  = (ImageView) volumeLevelViews.findViewById(R.id.imViewVolumeLevel);

            // Set background of volume level as per the current volume level
            if((CURRENT_VOLUME_LEVEL!= 0) && (mSoundLevel < CURRENT_VOLUME_LEVEL)){
                imViewVolumeLevel.setBackgroundColor(getResources().getColor(R.color.textColorGreen));
            }else{
                imViewVolumeLevel.setBackgroundColor(getResources().getColor(R.color.txtColorClickOnImge));
            }

            llVolumeBarLevelContainer.addView(volumeLevelViews);

        }

        // call method to reverse the direction of volume bar layout (container layout)
        reverseLayoutDirection();

        // Disable volume increase / decrease buttons if volume reaches to the maximum / minimum level

        if(CURRENT_VOLUME_LEVEL == MAX_VOLUME_LEVEL){

            // If volume reaches Maximum
            btnVolumeIncrease.setBackgroundResource(R.drawable.ic_volume_increase_disable);
            btnVolumeDecrease.setBackgroundResource(R.drawable.ic_volume_decrease_enable);

        }else if(CURRENT_VOLUME_LEVEL == 0){

            // If volume reached minimum
            btnVolumeIncrease.setBackgroundResource(R.drawable.ic_volume_increase_enable);
            btnVolumeDecrease.setBackgroundResource(R.drawable.ic_volume_decrease_disable);

        }else if(CURRENT_VOLUME_LEVEL > 0 && CURRENT_VOLUME_LEVEL < MAX_VOLUME_LEVEL){

            // If volume is in between maximum and minimum
            btnVolumeIncrease.setBackgroundResource(R.drawable.ic_volume_increase_enable);
            btnVolumeDecrease.setBackgroundResource(R.drawable.ic_volume_decrease_enable);

        }
    }

    /**
     * Method to reverse the direction of linear layout to display volume bar levels
     */
    private void reverseLayoutDirection(){

        ArrayList<View> views = new ArrayList<View>();

        // firstly add views in arraylist
        for(int x = 0; x < llVolumeBarLevelContainer.getChildCount(); x++) {
            views.add(llVolumeBarLevelContainer.getChildAt(x));
        }

        // remove the contants of container layout
        llVolumeBarLevelContainer.removeAllViews();

        // reverse the direction of views in container and then add again from the arraylist
        for(int x = views.size() - 1; x >= 0; x--) {
            llVolumeBarLevelContainer.addView(views.get(x));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnBack){

            if (mActivityTabs instanceof MainBaseActivity) {
                ((MainBaseActivity) mActivityTabs).fragmentManager.popBackStack();
            } else if (mActivityTabs instanceof HomeScreenActivity) {
                ((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();

            }
        }else if(view.getId() == R.id.btnCallUserNumber){
            GlobalConfig_Methods.makePhoneCall(getActivity(), mNumber);
        }else if(view.getId() == R.id.btnChatUserNumber){
            /*if(saveState.isRegistered(getActivity()) && isTncUserRegisteredUser){
                // go to chat window
                goToChatWindow();
            }*/
        }else if(view.getId() == R.id.btnVolumeIncrease){

        }else if(view.getId() == R.id.btnVolumeDecrease){

        }else if(view.getId() == R.id.btnShareVoiceMail){

        }else if(view.getId() == R.id.btnPlayRecording){

        }else if(view.getId() == R.id.btnStopRecording){

        }else if(view.getId() == R.id.btnDeleteVoiceMail){

        }else if(view.getId() == R.id.llParentLayout){
            //disable background click
        }
    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = GlobalConfig_Methods.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        btnStopRecording.performClick();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
}
