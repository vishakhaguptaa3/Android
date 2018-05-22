package com.tnc.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.database.DBQuery;
import com.tnc.fragments.UserRegistration1;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class BackupConfirmationDialog extends DialogFragment implements OnClickListener 
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private File fileDatabase;
	private Gson gson;
	private TransparentProgressDialog  progress;
	private SharedPreference saveState;
	//	File fileBackup;
	//	File fileServerBackup;
	//	String path="";
	private File SDCardRoot;
	private File file;
	private String fileUrl="";
	private ArrayList<ContactTilesBean> listBackedup=null;
	private ArrayList<InitResponseMessageBean> listBackedupComposedMessages=null;
	private INotifyGalleryDialog iNotifyBackupListing;
	private boolean isErrorOccuredWhileDownloadingFile=false;

	public BackupConfirmationDialog newInstance(String title, Context mAct,String message,
			String messageSub,String fileUrl,INotifyGalleryDialog iNotifyBackupListing)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.fileUrl=fileUrl;
		BackupConfirmationDialog frag = new BackupConfirmationDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.iNotifyBackupListing=iNotifyBackupListing;
		return frag;
	}
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		iNotifyBackupListing.no();
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
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);	
		tvMessage.setText(message);
		tvMessageSub.setText(messageSub);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(messageSub.trim().equals(""))
		{
			tvMessageSub.setVisibility(View.GONE);
		}
		else{
			tvMessageSub.setVisibility(View.VISIBLE);
		}
	}

	private void getBackupFromServer()
	{
		try {
			new DownloadFileAsyncTask().execute();
			//			downloadFileFromServer();
		} catch (Exception e) {
			isErrorOccuredWhileDownloadingFile=true;
			Logs.writeLog("BackupConfirmationDialog","getBackupFromServer",e.getMessage());
		}
	}

	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mAct)) {
			getBackupFromServer();
		} else
		{
			GlobalConfig_Methods.displayNoNetworkAlert(mAct);
		}
	}

	class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress=new TransparentProgressDialog(mAct, R.drawable.customspinner);
			progress.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			downloadFileFromServer();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(progress!=null)
				progress.dismiss();
			if(isErrorOccuredWhileDownloadingFile)
			{
				ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				if (mAct instanceof MainBaseActivity) {
					dialogErrorMessage.newInstance("",
							((MainBaseActivity) mAct),
							"Failed to restore.\n Please try after sometime", "", null);
					dialogErrorMessage.show(((MainBaseActivity) mAct)
							.getSupportFragmentManager(), "test");
				} else if (mAct instanceof HomeScreenActivity) {
					dialogErrorMessage.newInstance("",
							((HomeScreenActivity) mAct),
							"Failed to restore.\n Please try after sometime", "", null);
					dialogErrorMessage.show(
							((HomeScreenActivity) mAct)
							.getSupportFragmentManager(), "test");
				}
			}
			else if(!isErrorOccuredWhileDownloadingFile)
			{
				fetchBackupTileFromDataBase();
				fetchComposedMessagesFromDataBase();
				insertDatabse();				

				if(GlobalCommonValues.isBackedupSuccessful)
				{
					saveState=new SharedPreference();
					saveState.setFirstTile(mAct,false);
					saveState.setChanged(mAct, true);
					BackupRestoreSuccessDialog dialogSuccess=new BackupRestoreSuccessDialog();
					dialogSuccess.setCancelable(false);
					dialogSuccess.newInstance("CONGRATULATION!",mAct,Html.
							fromHtml("Your backup has been<br>recovered successfully").toString(),"");
					dismiss();
					if(mAct instanceof HomeScreenActivity)
					{
						dialogSuccess.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"Test");
						if(MainBaseActivity._bitmap!=null)
						{
							MainBaseActivity._bitmap=null;
						}
						UserRegistration1.isOnUserRegistration=false;
					}
					else if(mAct instanceof MainBaseActivity)
					{
						dialogSuccess.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"Test");
						if(MainBaseActivity._bitmap!=null)
						{
							MainBaseActivity._bitmap=null;
						}
						UserRegistration1.isOnUserRegistration=false;
					}
					//				if(mAct instanceof HomeScreenActivity)
					//					dialogSuccess.show(((HomeScreenActivity)mAct).getSupportFragmentManager(),"Test");
					//				else if(mAct instanceof MainBaseActivity)
					//					dialogSuccess.show(((MainBaseActivity)mAct).getSupportFragmentManager(),"Test");
				}
			}

		}
	}

	/**
	 * insert backup tiles in the database table
	 */
	private void insertDatabse()
	{
		if(!MainBaseActivity.mergeTiles)
		{
			DBQuery.deleteTable("Tiles","",null,mAct);
			DBQuery.deleteTable("ConfiguredMessages","",null,mAct);
		}
		DBQuery.insertTile(mAct, listBackedup,true);
		if(listBackedupComposedMessages.size()>0){
		for(int i=0;i<listBackedupComposedMessages.size();i++)
		{
			DBQuery.insertConfigMessage(mAct,listBackedupComposedMessages.get(i).getId(), 
					listBackedupComposedMessages.get(i).getMessage(), listBackedupComposedMessages.get(i).getType(),
					listBackedupComposedMessages.get(i).getLocked(), false,true);
		}
		}else{
			GlobalCommonValues.isBackedupSuccessful = true;
		}
	}

	/**
	 * fetch tiles from the Restored Database Table
	 */
	private void fetchBackupTileFromDataBase()
	{
		listBackedup= new ArrayList<ContactTilesBean>();
		try {
			listBackedup=DBQuery.getAllTiles(mAct, file.getName());
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * fetch Configured Messages from the Restored Database Table
	 */
	private void fetchComposedMessagesFromDataBase()
	{
		listBackedupComposedMessages= new ArrayList<InitResponseMessageBean>();
		try {
			listBackedupComposedMessages=DBQuery.getAllConfiguredMessages(mAct, file.getName());
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * download file from the server
	 */
	private void downloadFileFromServer()
	{
		try {
			//set the download URL, a url that points to a file on the internet
			//this is the file to be downloaded
			URL url = new URL(fileUrl);
			//create the new connection
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			//set up some things on the connection
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			//and connect!
			urlConnection.connect();
			//set the path where we want to save the file
			//in this case, going to save it on the root directory of the
			//sd card.
			SDCardRoot = new File(CopyDBFromAssets.DB_PATH);
			//create a new file, specifying the path, and the filename
			//which we want to save the file as.
			file = new File(SDCardRoot,"tnc_app");
			if(file.exists())
				file.delete();
			file.createNewFile();
			//this will be used to write the downloaded data into the file we created
			FileOutputStream fileOutput = new FileOutputStream(file);

			//this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			//this is the total size of the file
			//			int totalSize = urlConnection.getContentLength();
			//variable to store total downloaded bytes
			int downloadedSize = 0;

			//create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; //used to store a temporary size of the buffer

			//now, read through the input buffer and write the contents to the file
			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
				//add the data in the buffer to the file in the file output stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				//add up the size so we know how much is downloaded
				downloadedSize += bufferLength;
				//system.out.println(downloadedSize);
				//this is where you would do something to report the prgress, like this maybe
				//	                updateProgress(downloadedSize, totalSize);

			}
			//close the output stream when done
			fileOutput.close();

			//catch some possible errors...
		} catch (Exception e) {
			isErrorOccuredWhileDownloadingFile=true;
			e.printStackTrace();
		} 
	}

	/*INotifyGalleryDialog iNotifyMergeReplaceTiles=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			//In case of Merging Tiles	
			MainBaseActivity.mergeTiles=true;
			checkInternetConnection();
		}

		@Override
		public void no() {
			//In case of Replacing Old Tiles
			MainBaseActivity.mergeTiles=false;
			//All your exisitng tiles will be lost
			//			displayDataLostDialog();
			checkInternetConnection();
		}
	};*/

	INotifyGalleryDialog iNotifyRecovery=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			checkInternetConnection();
		}

		@Override
		public void no() {
		}
	};

	@SuppressWarnings("unused")
	private void displayDataLostDialog()
	{
		RecoveryConfirmationAlertDialog dialog=new RecoveryConfirmationAlertDialog();
		dialog.newInstance("", mAct,"Your current changes will be lost.  Please confirm","",iNotifyRecovery);
		if(mAct instanceof MainBaseActivity)
		{
			dialog.show(((MainBaseActivity)mAct).getSupportFragmentManager(), "test");
		}
		else if(mAct instanceof HomeScreenActivity)
		{
			dialog.show(((HomeScreenActivity)mAct).getSupportFragmentManager(), "test");
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnYes)  //Replace
		{
			dismiss();
			if(title.equalsIgnoreCase("Recover Archival Backup"))
			{
				if(iNotifyBackupListing!=null)
					iNotifyBackupListing.yes();
			}
			else{
				MainBaseActivity.mergeTiles=false;
				checkInternetConnection();
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			dismiss();
			if(iNotifyBackupListing!=null)
				iNotifyBackupListing.no();
		}
	}
}
