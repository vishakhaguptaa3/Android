package com.tnc.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by a3logics on 19/4/17.
 */

public class VoiceMailSettingsFragment extends BaseFragmentTabs implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener{

    private FrameLayout  flBackArrow, flInformationButton;

    private LinearLayout llVolumeBarLevelContainer;

    private TextView     tvTitle, tvVoiceMailHeading, tvRecordingAudioTimer, tvRecordCurrentDurationLabel,
            tvRecordTotalDurationLabel;

    private Button       btnBack, btnHome, btnVolumeIncrease, btnVolumeDecrease,
            btnLoudSpeaker, btnPlayRecording, btnRecord, btnStopRecording;

    private CheckBox     chkBoxVoiceMailEnable;

    private Context      mContext;

    private int          MAX_VOLUME_LEVEL = 0, CURRENT_VOLUME_LEVEL = 0;

    private AudioManager audioManager;

    private  View        volumeLevelViews = null;

    private boolean      isRecordingInProgress = false, isLoudSpeakerEnabled = false;

    private CountDownTimer Count = null;

    private MediaPlayer  mediaPlayer = null;

    private SeekBar      seekBarRecording = null;

    private Handler       mHandler = null;

    private SettingsContentObserver mSettingsContentObserver = null;

    public VoiceMailSettingsFragment newInstance(Context mContext){
        VoiceMailSettingsFragment frag = new VoiceMailSettingsFragment();
        this.mContext                  = mContext;
        Bundle args                    = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view                      = inflater.inflate(R.layout.voice_mail_settings_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState                     = new SharedPreference();

        mHandler                      = new Handler();

        tvTitle                       = (TextView) view.findViewById(R.id.tvTitle);
        tvVoiceMailHeading            = (TextView) view.findViewById(R.id.tvVoiceMailHeading);
        tvRecordingAudioTimer         = (TextView) view.findViewById(R.id.tvRecordingAudioTimer);
        tvRecordCurrentDurationLabel  = (TextView) view.findViewById(R.id.tvRecordCurrentDurationLabel);
        tvRecordTotalDurationLabel    = (TextView) view.findViewById(R.id.tvRecordTotalDurationLabel);

        flBackArrow                   = (FrameLayout) view.findViewById(R.id.flBackArrow);
        flInformationButton           = (FrameLayout)view.findViewById(R.id.flInformationButton);

        llVolumeBarLevelContainer     = (LinearLayout) view.findViewById(R.id.llVolumeBarLevelContainer);

        btnHome                       = (Button) view.findViewById(R.id.btnHome);
        btnBack                       = (Button) view.findViewById(R.id.btnBack);
        btnVolumeIncrease             = (Button) view.findViewById(R.id.btnVolumeIncrease);
        btnVolumeDecrease             = (Button) view.findViewById(R.id.btnVolumeDecrease);
        btnLoudSpeaker                = (Button) view.findViewById(R.id.btnLoudSpeaker);
        btnPlayRecording              = (Button) view.findViewById(R.id.btnPlayRecording);
        btnRecord                     = (Button) view.findViewById(R.id.btnRecord);
        btnStopRecording              = (Button) view.findViewById(R.id.btnStopRecording);

        chkBoxVoiceMailEnable         = (CheckBox) view.findViewById(R.id.chkBoxVoiceMailEnable);

        seekBarRecording              = (SeekBar) view.findViewById(R.id.seekBarRecording);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);

        // Set font style of text views
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvVoiceMailHeading, "fonts/Roboto-Bold_1.ttf");

        btnLoudSpeaker.setVisibility(View.GONE);

        //Apply click Listeners
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnVolumeIncrease.setOnClickListener(this);
        btnVolumeDecrease.setOnClickListener(this);
        btnLoudSpeaker.setOnClickListener(this);
        btnPlayRecording.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnStopRecording.setOnClickListener(this);

        chkBoxVoiceMailEnable.setOnClickListener(this);

        // set voice mail settings enabled if they were enabled earlier
        if(saveState.getIS_VOICE_MAIL_ENABLED(getActivity())){
            chkBoxVoiceMailEnable.setChecked(true);
        }else{
            chkBoxVoiceMailEnable.setChecked(false);
        }

        // Get Maximum Volume Level
        MAX_VOLUME_LEVEL    = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Get Current Volume Level
        CURRENT_VOLUME_LEVEL = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Call method to display volume bar level
        displayVolumeBarLevel();

        seekBarRecording.setOnSeekBarChangeListener(this);

        seekBarRecording.setProgress(0);

        // initialize media player in case greetings message exists
        try{
            if(GlobalConfig_Methods.isGreetingMessageExist(getActivity())) {
                Uri mp4 = Uri.parse(GlobalConfig_Methods.getGreetingMessageFilePath(getActivity()).getAbsolutePath());
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(getActivity(), mp4);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)

                // set maximum time for media player
                String mGreetingMessageTime =
                        GlobalConfig_Methods.milliSecondsToTimer(mediaPlayer.getDuration());

                tvRecordTotalDurationLabel.setText(mGreetingMessageTime);
            }
        }catch(Exception e){
            e.getMessage();
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

    /**
     * Method to start timer for recorder
     */
    private void startRecordingCountDown(){
        Count = new CountDownTimer(31000, 1000) {
            public void onTick(long millisUntilFinished) {

                // update stop button icon
                btnStopRecording.setBackgroundResource(R.drawable.ic_stop_recording_enabled);

                isRecordingInProgress = true;
                tvRecordingAudioTimer.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                // update stop button icon
                btnStopRecording.setBackgroundResource(R.drawable.ic_stop_recording_disabled);

                GlobalConfig_Methods.stopRecording();
                isRecordingInProgress = false;
                tvRecordingAudioTimer.setText("Finished");

                // reset media player status
                resetMediaPlayerStatus();
            }
        };
        Count.start();
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
        // stop recording if in progress
        if(isRecordingInProgress){
            GlobalConfig_Methods.stopRecording();
        }
        // reset media player status
        resetMediaPlayerStatus();

        super.onDestroy();
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
     * Method to update loudspeaker icons in case of enable / disable loudspeaker
     * @param : isMediaPlayerPlaying
     */
    private void updateLoudSpeakerIcons(boolean isLoudSpeakerEnabled){
        if(isLoudSpeakerEnabled){
            btnLoudSpeaker.setBackgroundResource(R.drawable.ic_speaker_enabled);
        }else{
            btnLoudSpeaker.setBackgroundResource(R.drawable.ic_speaker_disbaled);
        }
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            if (mActivityTabs instanceof MainBaseActivity) {
                ((MainBaseActivity) mActivityTabs).fragmentManager.popBackStack();
            } else if (mActivityTabs instanceof HomeScreenActivity) {
                ((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();
            }
        }else if(v.getId() == R.id.btnHome){
            getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
            getActivity().finish();
        }else if(v.getId() == R.id.chkBoxVoiceMailEnable){

            CheckBox mCheckBox = (CheckBox)v;

            // check greeting message existency
            if(GlobalConfig_Methods.isGreetingMessageExist(getActivity())){

                if(mCheckBox.isChecked()){
                    saveState.setVOICE_MAIL_ENABLED(getActivity(), true);
                }else{
                    saveState.setVOICE_MAIL_ENABLED(getActivity(), false);
                }
            }else{
                // display alert for non - existence of greeting message
                mCheckBox.setChecked(!mCheckBox.isChecked());

                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.newInstance("", getActivity(),
                        getResources().getString(R.string.txtVoiceMessageEnableAlert), "", null);
                mDialog.setCancelable(false);
                mDialog.show(getFragmentManager(), "Test");
            }

        }else if(v.getId() == R.id.btnVolumeIncrease){

            // check if current volume level is less than maximum sound level

            if(CURRENT_VOLUME_LEVEL < MAX_VOLUME_LEVEL){

                // increase volume level
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ++CURRENT_VOLUME_LEVEL, 0);

                // Update Current Volume Value
                CURRENT_VOLUME_LEVEL = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                // Update Volume Bar Level
                displayVolumeBarLevel();
            }

        }else if(v.getId() == R.id.btnVolumeDecrease){

            // check if current volume level is more than minimum level i.e. 0

            if(CURRENT_VOLUME_LEVEL >= 0){

                // increase volume level
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, --CURRENT_VOLUME_LEVEL, 0);

                // Update Current Volume Value
                CURRENT_VOLUME_LEVEL = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                // Update Volume Bar Level
                displayVolumeBarLevel();
            }
        }else if(v.getId() == R.id.btnLoudSpeaker){

//            if(!isLoudSpeakerEnabled){
//                isLoudSpeakerEnabled = true;
//            }else if(isLoudSpeakerEnabled){
//                isLoudSpeakerEnabled = false;
//            }

            //call method to update loud speaker button icons
//            updateLoudSpeakerIcons(isLoudSpeakerEnabled);

            // call method to enable / disable loud speaker
//            GlobalConfig_Methods.enableDisableLoudSpeakerNormal(getActivity());

        }else if(v.getId() == R.id.btnPlayRecording){

            // play recording only if there is no recording in progress
            if(!isRecordingInProgress){

                // Call method to play recorded greetings
                playRecordedGreeting();
            }

        }else if(v.getId() == R.id.btnRecord){
            if(!isRecordingInProgress){

                // start recording along with count down timer
                startRecordingCountDown();

                // start recording
                GlobalConfig_Methods.startRecording(getActivity());
            }
        }else if(v.getId() == R.id.btnStopRecording){
            if(isRecordingInProgress){
                // stop recording and timer
                if(Count!=null){

                    // update stop button icon
                    btnStopRecording.setBackgroundResource(R.drawable.ic_stop_recording_disabled);

                    Count.onFinish();
                    Count.cancel();
                    tvRecordingAudioTimer.setText("Finished");
                }
            }
        }else if(v.getId() == R.id.btnHome){
            getActivity().startActivity(new Intent(getActivity(), HomeScreenActivity.class));
            getActivity().finish();
        }
    }
}
