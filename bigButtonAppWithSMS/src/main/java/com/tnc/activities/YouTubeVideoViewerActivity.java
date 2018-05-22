package com.tnc.activities;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tnc.R;
import com.tnc.common.GlobalCommonValues;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by a3logics on 9/8/16.
 */
public class YouTubeVideoViewerActivity extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener{

	private static final int RECOVERY_DIALOG_REQUEST = 1;

	// YouTube player view
	private YouTubePlayerView youTubeView;
	private String mDisplayScreen = "";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.youtube_video_player_screen);

		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

		if(getIntent()!=null && getIntent().getExtras()!=null &&
				getIntent().getExtras().containsKey("video_type")){
			try{

				if((getIntent().getExtras().get("video_type")!=null) && 
						!(getIntent().getExtras().get("video_type").toString().trim().equalsIgnoreCase(""))){
					mDisplayScreen = String.valueOf(getIntent().getExtras().get("video_type"));

					// Initializing video player with developer key
					youTubeView.initialize(GlobalCommonValues.YOUTUBE_API_KEY, YouTubeVideoViewerActivity.this);
				}
			}catch(Exception e){
				e.getMessage();
			}
		}
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {

		/** add listeners to YouTubePlayer instance **/
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.setPlaybackEventListener(playbackEventListener);

		if (!wasRestored) {

			// loadVideo() will auto play video
			// Use cueVideo() method, if you don't want to play it automatically
			if(!mDisplayScreen.isEmpty() && mDisplayScreen.equalsIgnoreCase("intro_video")){
				player.loadVideo(GlobalCommonValues.INTRO_VIDEO_YOUTUBE_ID);
			}else if(!mDisplayScreen.isEmpty() && mDisplayScreen.equalsIgnoreCase("registration_video")){
				player.loadVideo(GlobalCommonValues.REGISTRATION_VIDEO_YOUTUBE_ID);
			}

			player.setManageAudioFocus(true);
			player.setFullscreen(true);

			// Hiding player controls
			player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(
					getString(R.string.error_player), errorReason.toString());
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(!(mDisplayScreen.isEmpty()) && !(mDisplayScreen.equalsIgnoreCase("registration_video"))){
				Bundle b = new Bundle();
				b.putString("status", "done");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK,intent);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECOVERY_DIALOG_REQUEST) {
			// Retry initialization if user performed a recovery action
			getYouTubePlayerProvider().initialize(GlobalCommonValues.YOUTUBE_API_KEY, YouTubeVideoViewerActivity.this);
		}
	}

	private YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

		@Override
		public void onBuffering(boolean arg0) {
		}

		@Override
		public void onPaused() {
		}

		@Override
		public void onPlaying() {
		}

		@Override
		public void onSeekTo(int arg0) {
		}

		@Override
		public void onStopped() {
		}

	};

	private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

		@Override
		public void onAdStarted() {
		}

		@Override
		public void onError(YouTubePlayer.ErrorReason arg0) {
		}

		@Override
		public void onLoaded(String arg0) {
		}

		@Override
		public void onLoading() {
		}

		@Override
		public void onVideoEnded() {
			Bundle b = new Bundle();
			b.putString("status", "done");
			Intent intent = new Intent();
			intent.putExtras(b);
			setResult(RESULT_OK,intent);
			finish();
		}

		@Override
		public void onVideoStarted() {
		}
	};
}
