package com.tnc.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.BackupListAdapter;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.database.DBQuery;
import com.tnc.dialog.BackupConfirmationDialog;
import com.tnc.dialog.BackupRestoreSuccessDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MergeReplaceOptionDialog;
import com.tnc.fragments.VerifyingRegistrationFragment.StartServiceClass;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.GetContactService;
import com.tnc.utility.Logs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * class to display the list of available backups from the cloud backups 
 *  @author a3logics
 */


public class BackupListFragment extends BaseFragment
{
    private FrameLayout flBackArrow,flInformationButton;
    private TextView tvTitle,tvHeading,tvInformation;
    private Button btnBack,btnHome;
    private Context mActivity;
    private ListView lvBackUp;
    private BackupListAdapter adapterBackupList=null;
    private int adapterSelected_position;
    private TransparentProgressDialog progress;
    private File SDCardRoot;
    private File file;
    private String fileUrl="";
    private ArrayList<ContactTilesBean> listBackedup=null;
    private boolean isErrorOccuredWhileDownloadingFile=false;
    private ArrayList<InitResponseMessageBean> listBackedupComposedMessages=new ArrayList<InitResponseMessageBean>();
    private SharedPreference saveState;

    public BackupListFragment newInstance(Context mActivity)
    {
        BackupListFragment frag = new BackupListFragment();
        this.mActivity=mActivity;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof MainBaseActivity){
            ((MainBaseActivity) activity).setINotifyCloudBackup(iNotifyBackupListing);
        }else if(activity instanceof HomeScreenActivity){
            ((HomeScreenActivity) activity).setINotifyCloudBackup(iNotifyBackupListing);
        }
    }

    /**
     * interface to notify the list for deselecting the selected position
     */
    INotifyGalleryDialog iNotifyBackupListing = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            adapterBackupList.setRowColor(adapterSelected_position, false);
        }

        @Override
        public void no() {
            adapterBackupList.setRowColor(adapterSelected_position, false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.backuplistfragment, container, false);
        idInitialization(view);
        return view;
    }

    private void idInitialization(View view){
        progress = new TransparentProgressDialog(mActivity, R.drawable.customspinner);
        MainBaseActivity.isTileCreated=false;
        saveState=new SharedPreference();
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvHeading=(TextView) view.findViewById(R.id.tvHeading);
        tvInformation=(TextView) view.findViewById(R.id.tvInformation);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
                
//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
        
        if(saveState.isRegistered(mActivity))
        {
            flBackArrow.setVisibility(View.GONE);
        }
        else
        {
            flBackArrow.setVisibility(View.VISIBLE);
        }
        if(MainBaseActivity.isBackButtonToDisplay)
        {
            flBackArrow.setVisibility(View.VISIBLE);
        }
        else if(!MainBaseActivity.isBackButtonToDisplay)
        {
            flBackArrow.setVisibility(View.GONE);
        }
        lvBackUp=(ListView) view.findViewById(R.id.lvBackUp);
        tvHeading.setText("BACKUPS");
        tvInformation.setText("Please select one of the available cloud backups");
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvHeading, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvInformation, "fonts/Roboto-Light_1.ttf");

        adapterBackupList=new BackupListAdapter(mActivity,GlobalCommonValues.listBackups);
        lvBackUp.setAdapter(adapterBackupList);
        if(iNotifyBackupListing!=null)
            iNotifyBackupListing.no();
        lvBackUp.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View rowView, int position,
                                    long arg3){
                // In case user selected the specific backup option
                MainBaseActivity.isTileCreated=false;
                adapterSelected_position=position;
                adapterBackupList.setRowColor(adapterSelected_position, true);

                /*if(saveState.getISPREMIUMUSER(getActivity())){*/
                    // In case of user is premium user
                    BackupConfirmationDialog dialogBackupConfirmationDialog=new BackupConfirmationDialog();
                    MergeReplaceOptionDialog mergeReplaceDialog=null;
                    dialogBackupConfirmationDialog.setCancelable(false);
                    if(mActivity instanceof MainBaseActivity){
                        if(MainBaseActivity.recoveryType.equals("current_backup")){
                            if(MainBaseActivity.isReturningUser){
                                if(DBQuery.getAllTiles(mActivity).size()>0){
                                    mergeReplaceDialog=new MergeReplaceOptionDialog();
                                    mergeReplaceDialog.setCancelable(false);
                                    mergeReplaceDialog.newInstance("",GlobalCommonValues.listBackups.get(position).getUrl(),((MainBaseActivity)mActivity), iNotifyBackupListing);
                                    mergeReplaceDialog.show(((MainBaseActivity)mActivity).getSupportFragmentManager(), "test");
                                }else{
                                    //in case of no tile exists already
                                    fileUrl = GlobalCommonValues.listBackups.get(position).getUrl();
                                    getBackupFromServer();
                                }
                            }
                            else{
                                dialogBackupConfirmationDialog.newInstance("Recover Current Backup",((MainBaseActivity)mActivity), "Your unsaved changes will be lost.  Please confirm","",GlobalCommonValues.listBackups.get(position).getUrl(),iNotifyBackupListing);
                                dialogBackupConfirmationDialog.show(((MainBaseActivity)mActivity).getSupportFragmentManager(), "test");
                            }
                        }
                        else if(MainBaseActivity.recoveryType.equals("archival_backup")){
                            //in case of no tile(s) exists already
                            if(DBQuery.getAllTiles(mActivity).size()>0){
                                mergeReplaceDialog=new MergeReplaceOptionDialog();
                                mergeReplaceDialog.setCancelable(false);
                                mergeReplaceDialog.newInstance("",GlobalCommonValues.listBackups.get(position).getUrl(),((MainBaseActivity)mActivity), iNotifyBackupListing);
                                mergeReplaceDialog.show(((MainBaseActivity)mActivity).getSupportFragmentManager(), "test");
                            }else{
                                //in case of no tile exists already
                                fileUrl = GlobalCommonValues.listBackups.get(position).getUrl();
                                getBackupFromServer();
                            }
                        }
                    }
                    else if(mActivity instanceof HomeScreenActivity)
                    {
                        if(MainBaseActivity.recoveryType.equals("current_backup"))
                        {
                            if(MainBaseActivity.isReturningUser)
                            {
                                if(DBQuery.getAllTiles(mActivity).size()>0){
                                    mergeReplaceDialog=new MergeReplaceOptionDialog();
                                    mergeReplaceDialog.setCancelable(false);
                                    mergeReplaceDialog.newInstance("",GlobalCommonValues.listBackups.get(position).getUrl(),((HomeScreenActivity)mActivity), iNotifyBackupListing);
                                    mergeReplaceDialog.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(), "test");
                                }else{
                                    //in case of no tile exists already
                                    fileUrl = GlobalCommonValues.listBackups.get(position).getUrl();
                                    getBackupFromServer();
                                }
                            }
                            else{
                                dialogBackupConfirmationDialog.newInstance("Recover Current Backup", ((HomeScreenActivity)mActivity), "Your unsaved changes will be lost.  Please confirm","",GlobalCommonValues.listBackups.get(position).getUrl(),iNotifyBackupListing);
                                dialogBackupConfirmationDialog.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(), "test");
                            }
                        }
                        else if(MainBaseActivity.recoveryType.equals("archival_backup")){
                            //in case of tile(s) exists already
                            if(DBQuery.getAllTiles(mActivity).size()>0){
                                mergeReplaceDialog=new MergeReplaceOptionDialog();
                                mergeReplaceDialog.setCancelable(false);
                                mergeReplaceDialog.newInstance("",GlobalCommonValues.listBackups.get(position).getUrl(),((HomeScreenActivity)mActivity), iNotifyBackupListing);
                                mergeReplaceDialog.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(), "test");
                            }else{
                                //in case of no tile exists already
                                fileUrl = GlobalCommonValues.listBackups.get(position).getUrl();
                                getBackupFromServer();
                            }
                        }
                    }
                /*}else{
                    PremiumFeaturesFragment mObjectPremiumFeaturesFragment = new PremiumFeaturesFragment();
                    mObjectPremiumFeaturesFragment.newInstance("","",null);
                    // In case of user is not the premium user
                    if(mActivity instanceof MainBaseActivity){
                        ((MainBaseActivity)mActivity).setFragment(mObjectPremiumFeaturesFragment);

                    }
                    else if(mActivity instanceof HomeScreenActivity){
                        ((HomeScreenActivity)mActivity).setFragment(mObjectPremiumFeaturesFragment);
                    }
                    iNotifyBackupListing.yes();
                }*/
            }
        });

        btnHome.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainBaseActivity.isTileCreated=false;
                if(mActivity instanceof MainBaseActivity)
                {
                    ((MainBaseActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                    ((MainBaseActivity)mActivity).finish();

                }
                else if(mActivity instanceof HomeScreenActivity)
                {
                    ((HomeScreenActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                    ((HomeScreenActivity)mActivity).finish();
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mActivity instanceof MainBaseActivity)
                {
                    ((MainBaseActivity)mActivity).fragmentManager.popBackStack();
                }
                else if(mActivity instanceof HomeScreenActivity)
                {
                    ((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
                }
            }
        });
    }

    /**
     * Method to get the backup from the server
     */
    private void getBackupFromServer()
    {
        try {
            new DownloadFileAsyncTask().execute();
        } catch (Exception e) {
            isErrorOccuredWhileDownloadingFile=true;
            Logs.writeLog("MergeReplaceConfirmationDialog","getBackupFromServer",e.getMessage());
        }
    }

    //async task to download cloud backup file from the server
    class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new TransparentProgressDialog(mActivity, R.drawable.customspinner);
            progress.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            //call method to download cloud backup file from the server
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
                adapterBackupList.setRowColor(adapterSelected_position,false);
                ImageRequestDialog dialogErrorMessage=new ImageRequestDialog();
                dialogErrorMessage.setCancelable(false);
                if (mActivity instanceof MainBaseActivity) {
                    dialogErrorMessage.newInstance("",
                            ((MainBaseActivity) mActivity),
                            "Failed to restore.\n Please try after sometime", "", null);
                    dialogErrorMessage.show(((MainBaseActivity) mActivity)
                            .getSupportFragmentManager(), "test");
                } else if (mActivity instanceof HomeScreenActivity) {
                    dialogErrorMessage.newInstance("",
                            ((HomeScreenActivity) mActivity),
                            "Failed to restore.\n Please try after sometime", "", null);
                    dialogErrorMessage.show(
                            ((HomeScreenActivity) mActivity)
                                    .getSupportFragmentManager(), "test");
                }
            }
            else if(!isErrorOccuredWhileDownloadingFile){
                GlobalConfig_Methods.testCopy();
                fetchBackupTileFromDataBase();
                fetchComposedMessagesFromDataBase();
                insertDatabse();

                if(GlobalCommonValues.isBackedupSuccessful)
                {
                    saveState=new SharedPreference();
                    saveState.setFirstTile(mActivity,false);
                    saveState.setChanged(mActivity, true);
                    BackupRestoreSuccessDialog dialogSuccess=new BackupRestoreSuccessDialog();
                    dialogSuccess.setCancelable(false);
                    dialogSuccess.newInstance("CONGRATULATION!",mActivity,Html.
                            fromHtml("Your backup has been<br>recovered successfully").toString(),"");
                    //dismiss();
                    if(mActivity instanceof HomeScreenActivity)
                    {
                        dialogSuccess.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(),"Test");
                        if(MainBaseActivity._bitmap!=null)
                        {
                            MainBaseActivity._bitmap=null;
                        }
                        UserRegistration1.isOnUserRegistration=false;
                    }
                    else if(mActivity instanceof MainBaseActivity)
                    {
                        dialogSuccess.show(((MainBaseActivity)mActivity).getSupportFragmentManager(),"Test");
                        if(MainBaseActivity._bitmap!=null)
                        {
                            MainBaseActivity._bitmap=null;
                        }
                        UserRegistration1.isOnUserRegistration=false;
                    }
                }}
        }
    }

    /**
     * insert backup tiles in the database table
     */
    private void insertDatabse()
    {
        if(!MainBaseActivity.mergeTiles)
        {
            DBQuery.deleteTable("Tiles","",null,mActivity);
            DBQuery.deleteTable("ConfiguredMessages","",null,mActivity);
        }
        DBQuery.insertTile(mActivity, listBackedup,true);
        if(listBackedupComposedMessages.size()>0){
            for(int i=0;i<listBackedupComposedMessages.size();i++)
            {
                DBQuery.insertConfigMessage(mActivity,listBackedupComposedMessages.get(i).getId(),
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
            listBackedup=DBQuery.getAllTiles(mActivity, file.getName());
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
            listBackedupComposedMessages=DBQuery.getAllConfiguredMessages(mActivity, file.getName());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     *method to download cloud backup file from the server
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

    @Override
    public void onResume()
    {
        super.onResume();

        saveState.setRefrehContactList(getActivity(),true);
        saveState.setIS_FROM_HOME(getActivity(),false);

        //call the service to send the contacts to the server
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new StartServiceClass().execute();
            }
        },100);

        if(HomeScreenActivity.btnNotification!=null && HomeScreenActivity.btnAddTile!=null){
            HomeScreenActivity.btnNotification.setEnabled(false);
            HomeScreenActivity.btnNotification.setClickable(false);
            HomeScreenActivity.btnAddTile.setEnabled(false);
            HomeScreenActivity.btnAddTile.setClickable(false);
        }
        if(iNotifyBackupListing!=null)
            iNotifyBackupListing.no();
    }

    //async task to call service to send the contacts to the server
    class StartServiceClass extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            saveState.setRefrehContactList(getActivity(),true);
            saveState.setIS_FROM_HOME(getActivity(),false);

            if(GlobalCommonValues.listContacts!=null &&
                    GlobalCommonValues.listContacts.size()>0){
                GlobalCommonValues.listContactsSendToServer =new
                        ArrayList<ContactDetailsBean>();
                for(int i=0;i<GlobalCommonValues.listContacts.size();i++)
                    GlobalCommonValues.listContactsSendToServer.add(GlobalCommonValues.listContacts.get(i));
            }

            Intent mainIntent = new Intent(getActivity(),GetContactService.class);
            getActivity().startService(mainIntent);
        }
    }
}
