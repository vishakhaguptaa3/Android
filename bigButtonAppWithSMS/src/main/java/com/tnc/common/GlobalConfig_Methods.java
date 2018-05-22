package com.tnc.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.UserCallLogDataBean;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ShowDialog;
import com.tnc.dialog.TileUpdateSuccessDialog;
import com.tnc.dialog.WelcomeBackReg_RestoreDialog;
import com.tnc.draggablegridviewpager.DraggableGridViewPager;
import com.tnc.fragments.SendSMSFullScreenDialogFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifySetImageBitmapUrl;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.DownloadClipartImages;
import com.tnc.utility.CalculateDays;
import com.tnc.utility.DateUtil;
import com.tnc.utility.Logs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.ParseException;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import me.leolin.shortcutbadger.ShortcutBadger;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Class containing commmon methods as utility
 * @author a3logics
 *
 */

@SuppressLint("DefaultLocale")
public class GlobalConfig_Methods {

    static Pattern NUMERIC_PATTERN=Pattern.compile("^\\d+$");

    public static boolean isJsonString(String response) {
        try {
            new JSONObject(response);
            return true;
        } catch (JSONException ex) {
            return false;
        }
    }

    /**
     *
     * @param :image
     *            file path of bitmap
     * @return Base64EncodedImage
     */
    public static String getBase64EncodedImage(String bitmappath) {
        String encodedImage = "";
        Bitmap bitmap = BitmapFactory.decodeFile(bitmappath);
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
            // bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImage = Base64
                    .encodeToString(byteArrayImage, Base64.DEFAULT);
        }
        return encodedImage;
    }

    @SuppressLint("SimpleDateFormat")
    public static int getTimeDifference_Minutes_Local(String dateText) {
        int Mins = -1;
        if (dateText != null && TextUtils.isEmpty(dateText) == false
                && dateText.equalsIgnoreCase("null") == false
                && dateText.equals("0000-00-00 00:00:00") == false) {

            java.util.Date date = null;
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = form.parse(dateText);
                if (date != null) {
                    long millis = System.currentTimeMillis() - date.getTime();
                    long seconds = millis / 1000;
                    Mins = (int) (seconds / 60);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Mins;
    }

    @SuppressLint("SimpleDateFormat")
    public static int getTimeDifference_Seconds_Local(String dateText) {
        int Seconds = -1;
        if (dateText != null && TextUtils.isEmpty(dateText) == false
                && dateText.equalsIgnoreCase("null") == false
                && dateText.equals("0000-00-00 00:00:00") == false) {

            java.util.Date date = null;
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = form.parse(dateText);// dateText
                if (date != null) {
                    long millis = System.currentTimeMillis() - date.getTime();
                    long seconds = millis / 1000;
                    Seconds = (int) (seconds);
                    // Mins = (int) (seconds / 60);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Seconds;
    }

    public static String encodeTobase64(Bitmap image) {
        String imageEncoded="";
        try {
            Bitmap immagex = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("image Encoded--", imageEncoded);
        } catch (OutOfMemoryError e) {
            e.getMessage();
        }
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Loads a bitmap from the specified url. This can take a while, so it
     * should not be called from the UI thread.
     *
     * @param url
     *            The location of the bitmap asset
     *
     * @return The bitmap, or null if it could not be loaded
     */

    private static final String TAG = "GlobalConfigMethods";

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(),
                    IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (IOException e) {
            Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
            closeStream(in);
            closeStream(out);
        }
        return bitmap;
    }

    /**
     * Closes the specified stream.
     *
     * @param stream
     *            The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not close stream", e);
            }
        }
    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     *
     * @param in
     *            The input stream to copy from.
     * @param out
     *            The output stream to copy to.
     * @throws IOException
     *             If any error occurs during the copy.
     */
    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     *
     * @param strPhone(String) to be trimmed
     * @return trimmed string with specific special character and spaces removed
     */
    public static String trimSpecialPhoneNumberToDisplay(String strPhone) {

        try {
            if (strPhone.contains("(")) {
                strPhone = strPhone.replace("(", "");
            }
            if (strPhone.contains(")")) {
                strPhone = strPhone.replace(")", "");
            }
            if (strPhone.contains("-")) {
                strPhone = strPhone.replaceAll("-", "");
            }
            if (strPhone.contains(" ")) {
                strPhone = strPhone.replaceAll(" ", "");
            }
            if (strPhone.contains("~")) {
                strPhone = strPhone.replaceAll("~", "");
            }
            if (strPhone.contains(";")) {
                strPhone = strPhone.replaceAll(";", "");
            }
            if (strPhone.contains(",")) {
                strPhone = strPhone.replaceAll(",", "");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return strPhone;
    }


    /**
     *
     * @param strPhone
     *            (String) to be trimmed
     * @return trimmed string with special character and spaces removed
     */
    public static String trimSpecialCharactersFromString(String strPhone) {

        String contactNumber = "";
        StringBuffer strBufferedNumber=new StringBuffer();

        for (int i=0;i<strPhone.length();i++)
        {
            if(NUMERIC_PATTERN.matcher(String.valueOf(strPhone.charAt(i))).matches())
            {
                strBufferedNumber.append(strPhone.charAt(i));
            }
        }
        contactNumber = String.valueOf(strBufferedNumber).trim();
        return contactNumber;
    }

	/*public static String getBBNumberToCheck(Context mContext,String strPhone)
	{
		String countrycode="",number="";
		String strNumber="";
		boolean isTnCUser=false,isMobile=false;
		boolean isdCodeflag=false;
		String isdCode="";
		int BBID=-1;
		String iddCode="";

		String contactNumber = "";
		StringBuffer strBufferedNumber=new StringBuffer();

		// keep only numbers in a phone number String and discard all rest of the String characters
		for (int i=0;i<strPhone.length();i++)
		{
			if(NUMERIC_PATTERN.matcher(String.valueOf(strPhone.charAt(i))).matches())
			{
				strBufferedNumber.append(strPhone.charAt(i));
			}
		}
		contactNumber = String.valueOf(strBufferedNumber).trim();

		SharedPreference saveState=new SharedPreference();
		countrycode=saveState.getCountryCode(mContext);
		//		countryName=DBQuery.getCountryName(mContext,countrycode);
		if(contactNumber.trim().length()==10)
		{
			number=contactNumber;	
			BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,countrycode);
			if(BBID>0)
			{
				isMobile=true;
				isdCodeflag=false;
				isdCode="";
				isTnCUser=true;
				MainBaseActivity.selectedBBID=String.valueOf(BBID);
				saveState.setBBID_User(mContext,String.valueOf(BBID));
			}
			else if(BBID<=0){
				isMobile=false;
				isdCodeflag=false;
				isdCode="";
				isTnCUser=false;
				//				MainBaseActivity.selectedBBID="";
			}
		}

		else if(contactNumber.trim().length()==11)
		{
			//			int  iddCode=-1;
			number=contactNumber.substring(1,contactNumber.length());	
			countrycode=contactNumber.substring(0,1);
			//			iddCode=Integer.parseInt(DBQuery.getIDDCodeDB(mContext, countrycode));
			if(!DBQuery.getIDDCodeDB(mContext, countrycode).trim().equals(""))
			{   // In case of Valid Country Code
				iddCode=DBQuery.getIDDCodeDB(mContext, countrycode);
				//Datatbase Query to check Existence of BBContact Code
				BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,countrycode);
				if(BBID>0)
				{
					isMobile=true;
					isdCodeflag=false;
					isdCode="";
					isTnCUser=true;
					MainBaseActivity.selectedBBID=String.valueOf(BBID);
					saveState.setBBID_User(mContext,String.valueOf(BBID));
				}
				else if(BBID<=0){
					isMobile=false;
					isdCodeflag=false;
					isdCode="";
					isTnCUser=false;
					//				MainBaseActivity.selectedBBID="";
				}
			}
			else {
				number=contactNumber.substring(1,contactNumber.length());	
				if(!DBQuery.getIDDCodeDB(mContext, saveState.getCountryCode(mContext)).trim().equals(""))
				{
					iddCode=DBQuery.getIDDCodeDB(mContext, saveState.getCountryCode(mContext));
					countrycode=saveState.getCountryCode(mContext);
					BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,countrycode);
					if(BBID>0)
					{
						isMobile=true;
						isdCodeflag=false;
						isdCode="";
						isTnCUser=true;
						MainBaseActivity.selectedBBID=String.valueOf(BBID);
						saveState.setBBID_User(mContext,String.valueOf(BBID));
					}
					else if(BBID<=0){
						isMobile=false;
						isdCodeflag=false;
						isdCode="";
						isTnCUser=false;
					}
				}
				else{
					isTnCUser=false;
				}
			}
		}

		else if(contactNumber.trim().length()==12)
		{
			//			int  iddCode=-1;
			number=contactNumber.substring(2,contactNumber.length());	
			countrycode=contactNumber.substring(0,2);
			//			iddCode=Integer.parseInt(DBQuery.getIDDCodeDB(mContext, countrycode));
			if(!DBQuery.getIDDCodeDB(mContext, countrycode).trim().equals(""))
			{  // In case of Valid Country Code
				iddCode=DBQuery.getIDDCodeDB(mContext, countrycode);
				BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,countrycode);
				if(BBID>0)
				{
					isMobile=true;
					isdCodeflag=false;
					isdCode="";
					isTnCUser=true;
					MainBaseActivity.selectedBBID=String.valueOf(BBID);
					saveState.setBBID_User(mContext,String.valueOf(BBID));
				}
				else if(BBID<=0){
					isMobile=false;
					isdCodeflag=false;
					isdCode="";
					isTnCUser=false;
				}
			}
			else {
				// In case Of InValid Country Code
				isTnCUser=false;
			}
		}
		else if(contactNumber.trim().length()>12){
			String subCountryCode="";
			//int iddCode=-1;
			number=contactNumber.substring(contactNumber.length()-10,contactNumber.length());
			countrycode=contactNumber.substring(0,contactNumber.length()-10);
			// We are interested in only the 3 rightmost digits
			subCountryCode=countrycode.substring(countrycode.length()-3,countrycode.length());
			//			iddCode=Integer.parseInt();
			if(!DBQuery.getIDDCodeDB(mContext, subCountryCode).trim().equals(""))
			{   // In case Of Valid Country Code
				iddCode=DBQuery.getIDDCodeDB(mContext, subCountryCode);
				BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,subCountryCode);
				if(BBID>0)
				{
					strNumber=number;
					isMobile=true;
					isdCodeflag=true;
					isdCode=contactNumber.substring(0,contactNumber.length()-10);
					isTnCUser=true;

				}
				else{
					strNumber=number;
					isMobile=false;
					isdCodeflag=true;
					isdCode=contactNumber.substring(0,contactNumber.length()-10);
					isTnCUser=false;
				}
			}
			else{  
				// In case Of InValid Country Code
				// Now try with 2 rightmost digits
				subCountryCode=countrycode.substring(countrycode.length()-2,countrycode.length());
				//				iddCode=Integer.parseInt(DBQuery.getIDDCodeDB(mContext, subCountryCode));
				if(!DBQuery.getIDDCodeDB(mContext, subCountryCode).trim().equals(""))
				{      // In case Of Valid Country Code
					iddCode=DBQuery.getIDDCodeDB(mContext, subCountryCode);
					BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,subCountryCode);
					if(BBID>0)
					{
						strNumber=number;
						isMobile=true;
						isdCodeflag=true;
						isdCode=contactNumber.substring(0,contactNumber.length()-10);
						isTnCUser=true;
					}
					else{
						strNumber=number;
						isMobile=false;
						isdCodeflag=true;
						isdCode=contactNumber.substring(0,contactNumber.length()-10);
						isTnCUser=false;
					}
				}
				else{  // In case Of InValid Country Code
					// Now try with rightmost 1 digit
					subCountryCode=countrycode.substring(countrycode.length()-1,countrycode.length());
					//iddCode=Integer.parseInt(DBQuery.getIDDCodeDB(mContext, subCountryCode));
					if(!DBQuery.getIDDCodeDB(mContext, subCountryCode).trim().equals(""))
					{      // In case Of Valid Country Code
						iddCode=DBQuery.getIDDCodeDB(mContext, subCountryCode);
						BBID=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mContext,number,subCountryCode);
						if(BBID>0)
						{
							strNumber=number;
							isMobile=true;
							isdCodeflag=true;
							isdCode=contactNumber.substring(0,contactNumber.length()-10);
							isTnCUser=true;
						}
						else{
							strNumber=number;
							isMobile=false;
							isdCodeflag=true;
							isdCode=contactNumber.substring(0,contactNumber.length()-10);
							isTnCUser=false;
						}
					}
				}
			}
			countrycode=subCountryCode;
		}
		strNumber=countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser + "," + iddCode;
		return strNumber;
	}*/

    EditText etPhoneNumber;
    public TextWatcher convertPhoneToUSFormat(EditText etPhoneNumber) {
        this.etPhoneNumber=etPhoneNumber;
        UsPhoneNumberFormatter addLineNumberFormatter = new UsPhoneNumberFormatter(
                new WeakReference<EditText>(etPhoneNumber));
        etPhoneNumber.addTextChangedListener(addLineNumberFormatter);
		/*if(etPhoneNumber.getText().toString().trim().length()==12)
			etPhoneNumber.setTextColor(Color.parseColor("#000"));
		else 
			etPhoneNumber.setTextColor(Color.parseColor("#929598"));*/
        return addLineNumberFormatter;
    }

    public class UsPhoneNumberFormatter implements TextWatcher {
        // This TextWatcher sub-class formats entered numbers as 1 (123)
        // 456-7890
        public boolean mFormatting; // this is a flag which prevents the
        // stack(onTextChanged)
        // private boolean clearFlag;
        public int mLastStartLocation;
        public String mLastBeforeText;
        public WeakReference<EditText> mWeakEditText;

        public UsPhoneNumberFormatter(WeakReference<EditText> weakEditText) {
            this.mWeakEditText = weakEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // if (after == 0 && s.toString().equals("1 ")) {
            // clearFlag = true;
            // }
            // if (after == 0 ) {
            // clearFlag = true;
            // }
            mLastStartLocation = start;
            mLastBeforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Make sure to ignore calls to afterTextChanged caused by the work
            // done below
            if (!mFormatting) {
                mFormatting = true;
                int curPos = mLastStartLocation;
                String beforeValue = mLastBeforeText;
                String currentValue = s.toString();
                String formattedValue = formatUsNumber(s);
                if (currentValue.length() > beforeValue.length()) {
                    int setCusorPos = formattedValue.length()
                            - (beforeValue.length() - curPos);
                    mWeakEditText.get().setSelection(
                            setCusorPos < 0 ? 0 : setCusorPos);
                } else {
                    int setCusorPos = formattedValue.length()
                            - (currentValue.length() - curPos);
                    if (setCusorPos > 0
                            && !Character.isDigit(formattedValue
                            .charAt(setCusorPos - 1))) {
                        setCusorPos--;
                    }
                    mWeakEditText.get().setSelection(
                            setCusorPos < 0 ? 0 : setCusorPos);
                }
                mFormatting = false;
				/*if(etPhoneNumber!=null)
				{
					if(etPhoneNumber.getText().toString().trim().length()==14)
						etPhoneNumber.setTextColor(Color.parseColor("#000000"));
					else 
						etPhoneNumber.setTextColor(Color.parseColor("#929598"));
				}*/
            }
        }

        public String formatUsNumber(Editable text) {
            StringBuilder formattedString = new StringBuilder();
            // Remove everything except digits
            int p = 0;
            while (p < text.length()) {
                char ch = text.charAt(p);
                if (!Character.isDigit(ch)) {
                    text.delete(p, p + 1);
                } else {
                    p++;
                }
            }
            // Now only digits are remaining
            String allDigitString = text.toString();
            int totalDigitCount = allDigitString.length();
            int alreadyPlacedDigitCount = 0;
            // The first 3 numbers must be enclosed in brackets "()"
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append("("
                        + allDigitString.substring(alreadyPlacedDigitCount,
                        alreadyPlacedDigitCount + 3) + ") ");
                alreadyPlacedDigitCount += 3;
            }
            // There must be a '-' inserted after the next 3 numbers
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append(allDigitString.substring(
                        alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3)
                        + "-");
                alreadyPlacedDigitCount += 3;
            }
            if (totalDigitCount > alreadyPlacedDigitCount) {
                formattedString.append(allDigitString
                        .substring(alreadyPlacedDigitCount));
            }
            text.clear();
            text.append(formattedString.toString());
            return formattedString.toString();
        }
    }

    @SuppressLint("SdCardPath")
    public static void testCopy() {

        String tncTest= String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNCTest/");

        //GlobalConfig_Methods.deleteFiles(tncTest);

        File f1 = new File("/data/data/com.tnc/databases/"
                + "tnc.sqlite");
        String path = android.os.Environment.getExternalStorageDirectory()
                + File.separator + "TNCTest";
        File fileBackupDir = new File(path);
        if (!fileBackupDir.exists()) {
            fileBackupDir.mkdirs();
        }
        try {
            if (f1.exists()) {
                File fileBackup = new File(fileBackupDir,
                        "tnc_test_db");
                try {
                    fileBackup.createNewFile();
                    FileUtils.copyFile(f1, fileBackup);
                } catch (Exception exception) {
                    exception.getMessage();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static Uri getOutputImageUri() {
        if (Environment.getExternalStorageState() == null) {
            return null;
        }

        File mediaStorage = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "TNC_IMAGES");
        if (!mediaStorage.exists() && !mediaStorage.mkdirs()) {
            Logs.writeLog("ChooseImageFragment", "getOutPutVideoUri",
                    "failed to create directory:");
            return null;
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File mediaFile = new File(mediaStorage, "IMG_" + timeStamp + ".jpg");
        return Uri.fromFile(mediaFile);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.getMessage();
            // Log exception
            return null;
        }
    }

    /**
     *
     * @param time
     *            as String
     * @return converted time as String converts the date and time in 24 hour
     *         mode to 12 hour mode
     */
    @SuppressLint("SimpleDateFormat")
    public static String Convert24to12(String time) {
        String convertedTime = "";
        String strTimeMode = "";
        String finalTime = "";
        Date date = new Date();
        Date displayDate = null;
        DateFormat writeFormat = null;
        String str[] = new String[35];
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat readDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            writeFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                str = time.split(" ");
                date = parseFormat.parse(str[1]);
                displayDate = readDateFormat.parse(str[0]);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            convertedTime = displayFormat.format(date);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        if (convertedTime.contains("am") || convertedTime.contains("AM")
                || convertedTime.contains("Am")) {
            if (convertedTime.contains("am")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("a"),
                        convertedTime.indexOf("a") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("a"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("AM")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("A"),
                        convertedTime.indexOf("A") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("A"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("Am")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("A"),
                        convertedTime.indexOf("A") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("A"))
                        + strTimeMode.toUpperCase();
            }
        } else if (convertedTime.contains("pm") || convertedTime.contains("PM")
                || convertedTime.contains("Pm")) {
            if (convertedTime.contains("pm")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("p"),
                        convertedTime.indexOf("p") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("p"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("PM")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("P"),
                        convertedTime.indexOf("P") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("P"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("Pm")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("P"),
                        convertedTime.indexOf("P") + 2);
                finalTime = writeFormat.format(displayDate)
                        + ","
                        + convertedTime
                        .substring(0, convertedTime.indexOf("P"))
                        + strTimeMode.toUpperCase();
            }
        }
        return finalTime;
        // Output will be 10:23 PM
    }




    /**
     *
     * @param time
     *            as String
     * @return converted time as String converts the date and time in 24 hour
     *         mode to 12 hour mode
     */
    @SuppressLint("SimpleDateFormat")
    public static String Convert24to12WithDate(String time) {
        String convertedTime = "";
        String strTimeMode = "";
        String finalTime = "";
        Date date = new Date();
        Date displayDate = null;
        DateFormat writeFormat = null;
        String str[] = new String[35];
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat readDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            writeFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                str = time.split(" ");
                date = parseFormat.parse(str[1]);
                displayDate = readDateFormat.parse(str[0]);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            convertedTime = displayFormat.format(date);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        if (convertedTime.contains("am") || convertedTime.contains("AM")
                || convertedTime.contains("Am")) {
            if (convertedTime.contains("am")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("a"),
                        convertedTime.indexOf("a") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("a"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("AM")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("A"),
                        convertedTime.indexOf("A") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("A"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("Am")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("A"),
                        convertedTime.indexOf("A") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("A"))
                        + strTimeMode.toUpperCase();
            }
        } else if (convertedTime.contains("pm") || convertedTime.contains("PM")
                || convertedTime.contains("Pm")) {
            if (convertedTime.contains("pm")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("p"),
                        convertedTime.indexOf("p") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("p"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("PM")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("P"),
                        convertedTime.indexOf("P") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("P"))
                        + strTimeMode.toUpperCase();
            } else if (convertedTime.contains("Pm")) {
                strTimeMode = convertedTime.substring(
                        convertedTime.indexOf("P"),
                        convertedTime.indexOf("P") + 2);
                finalTime = writeFormat.format(displayDate)
                        + " "
                        + convertedTime
                        .substring(0, convertedTime.indexOf("P"))
                        + strTimeMode.toUpperCase();
            }
        }
        return finalTime;
        // Output will be 14 Jul 2015 10:23 PM
    }

    /**
     *
     * @param time
     *            as String
     * @return converted time as String converts the date and time in 24 hour
     *         mode to 12 hour mode
     */
    @SuppressLint("SimpleDateFormat")
    public static String ConvertDate12WithDate(String time) {
        String convertedTime = "";
        String finalTime = "";
        Date date = new Date();
        Date displayDate = null;
        DateFormat writeFormat = null;
        String str[] = new String[35];
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat readDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            writeFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                str = time.split(" ");
                date = parseFormat.parse(str[1]);
                displayDate = readDateFormat.parse(str[0]);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            convertedTime = displayFormat.format(date);
            finalTime=writeFormat.format(displayDate)+" "+convertedTime;
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return finalTime;
        // Output will be 14 Jul 2015 10:23 PM
    }

    public static String changeDateFormatToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static boolean isForeground(Context mContext, String myPackage) {
        ActivityManager manager = (ActivityManager) mContext
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
                .getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(myPackage))
            return true;
        else
            return false;
    }

    public static void hideKeyBoard(Context mContext, EditText etTextBox) {
        InputMethodManager inputManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive())
            inputManager.hideSoftInputFromWindow(etTextBox.getWindowToken(), 0);
    }

    public static File savebitmap(Bitmap bitmapUserImage,
                                  String extStorageDirectory,String fileName) {
        ByteArrayOutputStream bytes=null;
        File file=null;
        FileOutputStream fileOutputStream = null;
        try {
            bytes = new ByteArrayOutputStream();
            bitmapUserImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            file = new File(extStorageDirectory + File.separator + fileName+".jpg");
            fileOutputStream = null;
            File directory=new File(extStorageDirectory);

            if(!directory.exists())
                directory.mkdirs();

            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("file", "" + file);
        return file;
    }

    public static Bitmap getBitmapFromFile(String filePath,String filName)
    {
        Bitmap bitmap=null;
        //		BitmapFactory.Options options = new BitmapFactory.Options();
        //		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(filePath+filName);
        return bitmap;
    }

    public boolean saveImageToInternalStorage(Bitmap image, Context context) {
        try {
            @SuppressWarnings("deprecation")
            FileOutputStream fos = context.openFileOutput("photo.jpg",
                    Context.MODE_WORLD_READABLE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 100 means
            // no
            // compression,
            // the lower
            // you go,
            // the
            // stronger
            // the
            // compression
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
        }
        return false;
    }

    /**
     * Method to save bitmap into external storage
     *
     * @param image
     * @return true if save success otherwise false
     */
    public static void saveImageToExternalStorage(Bitmap image) {
        // image=scaleCenterCrop(image,200,200);
        String fullPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/";
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, "photo1.jpg");

            if (file.exists())
                file.delete();

            file.createNewFile();
            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the
            // compression
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
    }

    public static String isUnlockTimeExpire(String dateServer) {
        String formattedDate = "";
        //system.out.println(" date server" + dateServer);
        @SuppressWarnings("unused")
        boolean isTimeExpire = false;
        Calendar cal = Calendar.getInstance();
        String gmtTime = dateServer;// "2015-02-23 12:41:42";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date date = null;
        try {
            try {
                date = df.parse(gmtTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
            //system.out.println("time zone outputText-" + formattedDate);
            String formattedDateCurrent = df.format(cal.getTime());
            //system.out.println(" time current -" + formattedDateCurrent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if current time greater than valid upto time than expiration time
        // expire
        if (cal.getTimeInMillis() > date.getTime()) {
            isTimeExpire = true;
            //system.out.println("time greate");
        } else {
            isTimeExpire = false;

            //system.out.println("time less");
        }
        return formattedDate;// isTimeExpire;
    }

    public static String getDateTimeLocal(String dateServer) {
        String formattedDate = "";
        //system.out.println(" date server" + dateServer);
        @SuppressWarnings("unused")
        boolean isTimeExpire = false;
        Calendar cal = Calendar.getInstance();
        String gmtTime = dateServer;// "2015-02-23 12:41:42";
        //yyyy-MM-dd
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date date = null;
        try {
            try {
                date = df.parse(gmtTime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = dfOutput.format(date);
            //system.out.println("time zone outputText-" + formattedDate);
            String formattedDateCurrent = df.format(cal.getTime());
            //system.out.println(" time current -" + formattedDateCurrent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if current time greater than valid upto time than expiration time
        // expire
        if (cal.getTimeInMillis() > date.getTime()) {
            isTimeExpire = true;
            //system.out.println("time greate");
        } else {
            isTimeExpire = false;

            //system.out.println("time less");
        }
        return formattedDate;// isTimeExpire;
    }

    public static  Bitmap getContactsDetails(Context mContext,String address) {

        Bitmap bp = null;/*BitmapFactory.decodeResource(context.getResources(),
	                     R.drawable.default_contact_photo);*/
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + address + "'";
        Cursor phones = mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection,
                null, null);
        while (phones.moveToNext()) {
            String image_uri = phones .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));


            if (image_uri != null) {
                try {
                    bp = MediaStore.Images.Media
                            .getBitmap(mContext.getContentResolver(),
                                    Uri.parse(image_uri));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bp;
    }

    public static String getContactName(Context mContext, String mobileNumber){
        String name="";
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER +" LIKE ?",new String[]{"%"+mobileNumber}, null);
        while (phones.moveToNext())
        {
            name	=	phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        }
        if(name==null || name.trim().equals(""))
        {
            Cursor phonesName = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER +" LIKE ?",new String[]{"%"+mobileNumber}, null);
            while (phonesName.moveToNext()){
                name	=	phonesName.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }
        }
        return name;
    }

    public static Bitmap getContactBitmap(Context mContext, String mobileNumber){
        String photoUri="";
        Bitmap bp=null;
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER +" LIKE ?",new String[]{"%"+mobileNumber}, null);
        while (phones.moveToNext())
        {
            photoUri	=	phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
        }
        if(photoUri==null || photoUri.trim().equals(""))
        {
            Cursor phonesPhotoURI = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER +" LIKE ?",new String[]{"%"+mobileNumber}, null);
            while (phonesPhotoURI.moveToNext()){
                photoUri	=	phonesPhotoURI.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            }
        }
        if (photoUri != null) {
            try {
                bp = MediaStore.Images.Media
                        .getBitmap(mContext.getContentResolver(),
                                Uri.parse(photoUri));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bp;
    }

    public static String getContactNameFromPhone(Context context, String mobileNumber) {

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobileNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public static String getHashKey(Context mContext,String packageName)
    {
        String hashKey="";
        try {                                                             //packageName
            PackageInfo info = mContext.getPackageManager().getPackageInfo(packageName,PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());  //sMIez0AU6MTw0G+E/3ZoPaBAjkY=
                hashKey=Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:",hashKey);
            }
        }
        catch (Exception e)
        {
            hashKey="";
            e.getMessage();
        }
        return hashKey;
    }

    public static long getCurrentMilliSeconds()
    {
        return  System.currentTimeMillis();
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static String getDifferenceMinutes(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long elapsedHours;
        long elapsedMinutes;
        long elapsedDays;
        //system.out.println("startDate : " + startDate);
        //system.out.println("endDate : "+ endDate);
        //system.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
        return String.valueOf(elapsedHours)+","+String.valueOf(elapsedMinutes);
    }

    @SuppressWarnings("deprecation")
    public static void displayNoNetworkAlert(Context mActivity)
    {
        SharedPreference saveState = new SharedPreference();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        if(mActivity!=null){

            if(saveState == null)
                saveState = new SharedPreference();

            if(saveState.getTimePopupDisplay(mActivity).trim().equals(""))
            {
                saveState.setTimePopupDisplay(mActivity,dateFormat.format(date));  //2015/07/21 18:03:46
                ShowDialog.alert(mActivity,"", mActivity.getResources()
                        .getString(R.string.no_internet_abvailable));
            }
            else if(!saveState.getTimePopupDisplay(mActivity).trim().equals("")){
                try {
                    Date firstDate = new Date(saveState.getTimePopupDisplay(mActivity));
                    Date secondDate = new Date(dateFormat.format(date));
                    int diff = DateUtil.minutesDiff(firstDate, secondDate);
                    if(diff>5)
                    {
                        ShowDialog.alert(mActivity,"", mActivity.getResources()
                                .getString(R.string.no_internet_abvailable));
                        saveState.setTimePopupDisplay(mActivity,dateFormat.format(date));
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    //add phone Number to local contacts
    public static void addPhoneContact(Context mContext,ContentValues initialValues)
    {
        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();
        int rawContactID = listOperations.size();
        String strPhone=GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(String.valueOf(initialValues.get("PhoneNumber")));
        String contactNumber="";
        contactNumber=strPhone;
        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        listOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        listOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, initialValues.get("Name"))
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        listOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, contactNumber)
                .withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try{
            // Executing all the insert operations as a single database transaction
            mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, listOperations);
            //				Toast.makeText(mContext, "Contact is successfully added", Toast.LENGTH_SHORT).show();
        }catch (RemoteException e) {
            e.printStackTrace();
        }catch (OperationApplicationException e) {
            //system.out.println(e.getMessage()+"--------");
            e.printStackTrace();
        }
    }

	/*public static String checkIsValidTnCNumber(String phoneNumber)
	{

		return "";
	}*/

    static String caseMatchedString1="";

    //Method To Check if a number is valid tnc number or not
    @SuppressWarnings("unused")
    public static String getBBNumberToCheck(Context mContext,String strPhone)
    {
        //		caseMatchedString="";
        String countrycode="",number="";
        String strNumber="";
        boolean isTnCUser=false,isMobile=false;
        boolean isdCodeflag=false;
        String isdCode="";
        int BBID=-1;
        String iddCode="";
        int numLength=-1;
        String contactNumber = "";
        StringBuffer strBufferedNumber=new StringBuffer();
        SharedPreference saveState=new SharedPreference();
        saveState.setNumber_Length(mContext, 0);
        try {
            String[] arrayCodeWithNumber=new String[50];
            String _number=getFormattedNumber(mContext, strPhone);
            arrayCodeWithNumber=_number.split("-");
            //system.out.println(arrayCodeWithNumber[0]+"-"+arrayCodeWithNumber[1]);
            countrycode=arrayCodeWithNumber[0];
            number=arrayCodeWithNumber[1];

            numLength=saveState.getNumber_Length(mContext);//formatPlaneNumber(strPhone).length();
            if(numLength>=3 && numLength<=6)
            {
                number=number;
                isMobile=true;
                countrycode="";
                isdCodeflag=false;
                isdCode="";
            }
            else if(numLength>12)
            {
                number=number;
                isMobile=false;
                if(!countrycode.trim().equals("")){
                    countrycode=countrycode;
                }else{
                    countrycode="";
                }
                isdCodeflag=false;
                isdCode="";
                if(strPhone.startsWith(DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext)))){
                    isdCodeflag=true;
                    isdCode=DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
                }
                else if(strPhone.startsWith("+")){
                    isdCodeflag=true;
                    isdCode=DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
                }
                isTnCUser=isValidTnCUser(mContext, countrycode, number);
                if(isTnCUser){
                    isMobile=true;
                }
            }
            else{
                isTnCUser=isValidTnCUser(mContext, countrycode, number);
                if(isTnCUser)
                {
                    isMobile=true;
                    if(numLength>=7 && numLength<=10)
                    {
                        isMobile=true;
                        countrycode=countrycode;//saveState.getCountryCode(mContext);
                        isdCodeflag=false;
                        isdCode="";
                    }
                    else if(numLength>=11 && numLength<=12){
                        number=number;
                        isMobile=true;
                        countrycode=countrycode;
                        isdCodeflag=true;
                        isdCode="";
                        if(strPhone.startsWith(DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext))))
                        {
                            isdCode=DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
                        }
                        else if(strPhone.startsWith("+")){
                            isdCode=DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
                        }
                    }
                }
                else{
                    if(numLength>=7 && numLength<=10){
                        number=number;
                        isMobile=false;
                        if(!countrycode.trim().equals(""))
                            countrycode=countrycode;
                        else
                            countrycode="";
                        isdCodeflag=false;
                        isdCode="";
                    }
                    else if(numLength>=11 && numLength<=12){
                        number=number;
                        isMobile=false;
                        countrycode=countrycode;//"";
                        isdCodeflag=false;
                        isdCode="";
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        try{
            if(isdCode == null)
                isdCode = "";

            if(strPhone == null)
                strPhone = "";

            if(isdCode.trim().equals("") && strPhone.startsWith(DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext)))){
                isdCodeflag=true;
                isdCode = DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
            }
        }catch(Exception e){
            if(isdCode == null)
                isdCode = "";
            if(strPhone == null)
                strPhone = "";
            e.getMessage();
        }
        strNumber=countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser + "," + iddCode;
        return strNumber;
    }

    //Method To return formatted number when a number is passed to it
    @SuppressWarnings("unused")

    public static String getFormattedNumber(Context mContext,String strPhone)
    {
        String countrycode="",number="";
        String strNumber="";
        boolean isTnCUser=false,isMobile=false;
        boolean isdCodeflag=false;
        String isdCode="";
        int BBID=-1;
        String iddCode="";
        int numLength=-1;
        String contactNumber = "";
        StringBuffer strBufferedNumber=new StringBuffer();
        SharedPreference saveState=new SharedPreference();

        contactNumber=deleteNonNumericCharacters(strPhone);
        iddCode=DBQuery.getIDDCodeDB(mContext,saveState.getCountryCode(mContext));
        if(iddCode.trim().equals(""))
        {
            return "";
        }

        numLength=countStringLength(contactNumber);
        saveState.setNumber_Length(mContext,numLength);
        if(numLength>=3 && contactNumber.charAt(0)=='+')
        {
            String numberwithCountryCode=contactNumber.substring(1,contactNumber.length()); //remove +
            saveState.setNumber_Length(mContext, numberwithCountryCode.length());
            int firstDigit=Integer.parseInt(numberwithCountryCode.substring(0,1));
            if(firstDigit==1 || firstDigit==7)
            {
                countrycode=String.valueOf(firstDigit);
                number=numberwithCountryCode.substring(1,numberwithCountryCode.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(numberwithCountryCode.substring(0,2)))
            {
                countrycode=numberwithCountryCode.substring(0,2);
                number=numberwithCountryCode.substring(2,numberwithCountryCode.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(numberwithCountryCode.substring(0,3)))
            {
                countrycode=numberwithCountryCode.substring(0,3);
                number=numberwithCountryCode.substring(3,numberwithCountryCode.length());
            }
            strNumber=countrycode + "-" + number;
        }
        else if(contactNumber.startsWith(iddCode)){
            String numberwithCountryCode=contactNumber.substring(iddCode.length(),contactNumber.length());
            //caseMatchedString=String.valueOf(numberwithCountryCode.length());
            saveState.setNumber_Length(mContext,numberwithCountryCode.length());
            int firstDigit=Integer.parseInt(numberwithCountryCode.substring(0,1));
            if(firstDigit==1 || firstDigit==7)
            {
                countrycode=String.valueOf(firstDigit);
                number=numberwithCountryCode.substring(1,numberwithCountryCode.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(numberwithCountryCode.substring(0,2)))
            {
                countrycode=numberwithCountryCode.substring(0,2);
                number=numberwithCountryCode.substring(2,numberwithCountryCode.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(numberwithCountryCode.substring(0,3)))
            {
                countrycode=numberwithCountryCode.substring(0,3);
                number=numberwithCountryCode.substring(3,numberwithCountryCode.length());
            }
            strNumber=countrycode + "-" + number;
        }else{
            String _number=splitNumberandCountryCode(mContext,contactNumber);
            strNumber=_number.split("-")[0]+"-"+_number.split("-")[1];
        }
        return strNumber;
    }

    public static String splitNumberandCountryCode(Context mContext,String number)
    {
        String countryCode="";
        String strNumber="";
        String phoneNumber=formatPlaneNumber(number);
        SharedPreference saveState = new SharedPreference();
        if(phoneNumber.length()<=10)
        {
            countryCode=saveState.getCountryCode(mContext);
            number=phoneNumber;
            //			caseMatchedString=String.valueOf(number.length());
            saveState.setNumber_Length(mContext,number.length());
        }else{

            int firstDigit=Integer.parseInt(phoneNumber.substring(0,1));
            if(firstDigit==1 || firstDigit==7)
            {
                countryCode=String.valueOf(firstDigit);
                number=phoneNumber.substring(1,phoneNumber.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(phoneNumber.substring(0,2)))
            {
                countryCode=phoneNumber.substring(0,2);
                number=phoneNumber.substring(2,phoneNumber.length());
            }
            else if(GlobalCommonValues.listCountryCodes.contains(phoneNumber.substring(0,3)))
            {
                countryCode=phoneNumber.substring(0,3);
                number=phoneNumber.substring(3,phoneNumber.length());
            }
            else{
                countryCode=saveState.getCountryCode(mContext);
            }
        }
        strNumber=countryCode + "-" + number;
        return strNumber;
    }

    public static String formatPlaneNumber(String number){
        String formattedNumber="";
        StringBuffer strBufferedNumber=new StringBuffer();

        // keep only numbers in a phone number String and discard all rest of the String characters
        for (int i=0;i<number.length();i++)
        {
            if(NUMERIC_PATTERN.matcher(String.valueOf(number.charAt(i))).matches())
            {
                strBufferedNumber.append(number.charAt(i));
            }
        }
        formattedNumber = String.valueOf(strBufferedNumber).trim();
        return formattedNumber;
    }

    /**
     * @param phoneNumber
     */
    public static String deleteNonNumericCharacters(String phoneNumber)
    {
        String resultNumber="";
        String contactNumber="";
        String strPhone=phoneNumber.substring(1,phoneNumber.length());
        StringBuffer strBufferedNumber=new StringBuffer();

        // keep only numbers in a phone number String and discard all rest of the String characters
        for (int i=0;i<strPhone.length();i++)
        {
            if(NUMERIC_PATTERN.matcher(String.valueOf(strPhone.charAt(i))).matches())
            {
                strBufferedNumber.append(strPhone.charAt(i));
            }
        }
        contactNumber = String.valueOf(strBufferedNumber).trim();
        resultNumber=phoneNumber.substring(0,1)+contactNumber;
        return resultNumber;  // returns a string containing only numbers except +
    }

    /**
     * @param phoneNumber
     */
    public static String getPlaneNumber(String phoneNumber)
    {
        //		String resultNumber="";
        String contactNumber="";
        String strPhone=phoneNumber.substring(1,phoneNumber.length());
        StringBuffer strBufferedNumber=new StringBuffer();

        // keep only numbers in a phone number String and discard all rest of the String characters
        for (int i=0;i<strPhone.length();i++)
        {
            if(NUMERIC_PATTERN.matcher(String.valueOf(strPhone.charAt(i))).matches())
            {
                strBufferedNumber.append(strPhone.charAt(i));
            }
        }
        contactNumber = String.valueOf(strBufferedNumber).trim();
        //resultNumber=phoneNumber.substring(0,1)+contactNumber;
        return contactNumber;  // returns a string containing only numbers except +
    }


    /**
     * @param phoneNumber
     */
    public static String getPlaneNumberForContacts(String phoneNumber)
    {
        String contactNumber="";
        String strPhone = "";
        strPhone=phoneNumber;
		/*if(phoneNumber.trim().startsWith("+")){
			strPhone=phoneNumber.substring(1,phoneNumber.length());
		}else{
			strPhone=phoneNumber.substring(0,phoneNumber.length());
		}*/
        StringBuffer strBufferedNumber=new StringBuffer();

        // keep only numbers in a phone number String and discard all rest of the String characters
        for (int i=0;i<strPhone.length();i++)
        {
            if(NUMERIC_PATTERN.matcher(String.valueOf(strPhone.charAt(i))).matches())
            {
                strBufferedNumber.append(strPhone.charAt(i));
            }
        }
        contactNumber = String.valueOf(strBufferedNumber).trim();
        //resultNumber=phoneNumber.substring(0,1)+contactNumber;
        return contactNumber;  // returns a string containing only numbers except +
    }


    /**
     * @param phoneNumber
     */
    public static String rightString(String phoneNumber)
    {
        return phoneNumber;//returns right numChars of a string
    }

    /**
     * @param phoneNumber
     */
    public static int countStringLength(String phoneNumber)
    {
        return phoneNumber.length();
        // returns the length of a string
    }

    /**
     * @param countryCode
     */
    public boolean isValidCountryCode(String countryCode)
    {
        // returns TRUE if CountryCode is in the Array of valid codes
        // for single digit country codes leading 0 is ok
        return true;
    }

    /**
     * @param :countrycode & phoneNumber
     */
    public static boolean isValidTnCUser(Context mContext,String countryCode, String phoneNumber)
    {
        boolean isValidTnCUser=false;
        isValidTnCUser=DBQuery.checkBBContactExistenceFromNumber(mContext, countryCode,phoneNumber);
        // returns TRUE if the input data is in the Database
        return isValidTnCUser;
    }

    /**
     * @param :phoneNumberwithcountrycode
     */
    public static String getCountryCodeAndPhoneNumber(String phoneNumberWithCountryCode)
    {
        // Splits the number into Country code and Phone number
        return ""+","+"";
    }

    public static boolean CheckTileDuplicacy(Context mContext,String strPhone){
        boolean isExist=false;
        String number="",countryCode="",phoneNumber="";
        int size=-1;
        SharedPreference saveState = new SharedPreference();

        if(strPhone.replaceAll(" ", "").length()<7){
            phoneNumber=strPhone.replaceAll(" ", "");
            size = DBQuery.checkTileExistence(mContext, phoneNumber, "");

        }else {
            number = getFormattedNumber(mContext, strPhone);
            String[] arrayPhoneNumber = number.split("-");
            try {
                countryCode=arrayPhoneNumber[0];
                phoneNumber=arrayPhoneNumber[1];
                if(countryCode.length()==0){
                    size = DBQuery.checkTileExistence(mContext, phoneNumber, countryCode);
                    if(size<1){
                        size = DBQuery.checkTileExistence(mContext, phoneNumber, saveState.getCountryCode(mContext));
                    }
                }else{
                    size = DBQuery.checkTileExistence(mContext, phoneNumber, countryCode);
                    if(size<1){
                        if(countryCode.equals(saveState.getCountryCode(mContext)))
                        {
                            size = DBQuery.checkTileExistence(mContext, phoneNumber,"");
                        }
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        if(size>=1){
            isExist=true;
        }else{
            isExist=false;
        }
        return isExist;
    }

    public static Bitmap getBitmap_via_Gallery(Activity mActivity,Intent data, int resultCode) {
        Bitmap image_bitmap = null;
        if (data != null) {
            Uri uri = data.getData();
            if (resultCode == Activity.RESULT_OK) {
                if (uri != null) {
                    // User had pick an image.
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = mActivity.getContentResolver().query(
                            uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String temp_imageFilePath = cursor.getString(columnIndex);

                    File file = new File(temp_imageFilePath);
                    if (file.length() > 10000000) {
						/*UserNotifySupport.show_NotificationDialog(
								getActivity(),
								"Image size should not be greater than 10MB");*/
                    } else {
                        try {
                            image_bitmap = getResizedImage(temp_imageFilePath);
                            if (image_bitmap != null) {
                                image_bitmap = adjustImageOrientation(
                                        image_bitmap, temp_imageFilePath);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                } else {
                    //system.out.println("No Image is selected.");
                }
            }
        }
        return image_bitmap;
    }

    private static Bitmap getResizedImage(String filePath) {
        final int IMAGE_MAX_SIZE = 630;
        File file = null;
        FileInputStream fis;
        BitmapFactory.Options opts;
        int resizeScale;
        Bitmap bmp = null;
        file = new File(filePath);
        // This bit determines only the width/height of the bitmap without
        // loading the contents
        opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try {
            fis = new FileInputStream(filePath);
            BitmapFactory.decodeStream(fis, null, opts);
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Find the correct scale value. It should be a power of 2
        resizeScale = 1;
        if (opts.outHeight > IMAGE_MAX_SIZE || opts.outWidth > IMAGE_MAX_SIZE) {
            resizeScale = (int) Math.pow(
                    2,
                    (int) Math.round(Math.log(IMAGE_MAX_SIZE
                            / (double) Math.max(opts.outHeight, opts.outWidth))
                            / Math.log(0.5)));
        }
        // Load pre-scaled bitmap
        opts = new BitmapFactory.Options();
        opts.inSampleSize = resizeScale;
        try {
            fis = new FileInputStream(file);
            bmp = BitmapFactory.decodeStream(fis, null, opts);
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp;
    }

    private static Bitmap adjustImageOrientation(Bitmap image, String picturePath) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(picturePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = image.getWidth();
                int h = image.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

            }
        } catch (IOException e) {
            return null;
        }
        return image.copy(Bitmap.Config.ARGB_8888, true);
    }

    public static String getNumberType(int type){
        String sType = "";
        switch (type) {
            case Phone.TYPE_HOME:
                sType = "Home";
                break;
            case Phone.TYPE_MOBILE:
                sType = "Mobile";
                break;
            case Phone.TYPE_WORK:
                sType = "Work";
                break;
            case Phone.TYPE_FAX_HOME:
                sType = "Home Fax";
                break;
            case Phone.TYPE_FAX_WORK:
                sType = "Work Fax";
                break;
            case Phone.TYPE_MAIN:
                sType = "Main";
                break;
            case Phone.TYPE_OTHER:
                sType = "Other";
                break;
            case Phone.TYPE_CUSTOM:
                sType = "Custom";
                break;
            case Phone.TYPE_PAGER:
                sType = "Pager";
                break;
            case Phone.TYPE_ASSISTANT:
                sType = "Assistant";
                break;
            case Phone.TYPE_CALLBACK:
                sType = "Callback";
                break;
            case Phone.TYPE_CAR:
                sType = "Car";
                break;
            case Phone.TYPE_COMPANY_MAIN:
                sType = "Company Main";
                break;
            case Phone.TYPE_ISDN:
                sType = "ISDN";
                break;
            case Phone.TYPE_MMS:
                sType = "MMS";
                break;
            case Phone.TYPE_OTHER_FAX:
                sType = "Other Fax";
                break;
            case Phone.TYPE_RADIO:
                sType = "Radio";
                break;
            case Phone.TYPE_TELEX:
                sType = "Telex";
                break;
            case Phone.TYPE_TTY_TDD:
                sType = "TTY TDD";
                break;
            case Phone.TYPE_WORK_MOBILE:
                sType = "Work Mobile";
                break;
            case Phone.TYPE_WORK_PAGER:
                sType = "Work Pager";
                break;
        }
        return sType;
    }


    /**
     * Method used to set the display contactlist from the native phone
     * @param mResultantCursor
     */
    public static void setContactListToDislpay(Cursor mResultantCursor){
        if(mResultantCursor!=null && !mResultantCursor.isClosed()){
            GlobalCommonValues.listContacts = new
                    ArrayList<ContactDetailsBean>();
            ArrayList<String> mListNumbers = new ArrayList<String>();
            ArrayList<String> mListEmails = new ArrayList<String>();
            ArrayList<String> mListType = new ArrayList<String>();

            ContactDetailsBean mContactModel = null;
            do{
                mContactModel = new ContactDetailsBean();
                mListNumbers = new ArrayList<String>();
                mListEmails = new ArrayList<String>();
                mListType = new ArrayList<String>();

                if(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))!=null &&
                        !(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).trim().equals("")){
                    try{
                        mContactModel.set_id(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                        mContactModel.set_name(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                        mListNumbers.add(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ",""));
                        mContactModel.set_phone(mListNumbers);


                        if(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI ))!=null &&
                                !(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI ))).trim().equals("")){
                            mContactModel.setImageUri(mResultantCursor.getString(mResultantCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI )));
                        }

                    }catch(Exception e){
                        e.getMessage();
                    }

                    int type = mResultantCursor.getInt(mResultantCursor.getColumnIndex(Phone.TYPE));
                    String sType = GlobalConfig_Methods.getNumberType(type);
                    mListType.add(sType);

                    mContactModel.setsType(mListType);

                    boolean isFound = false;
                    contactImageLoopTag: 	for (ContactDetailsBean mContactDetail : GlobalCommonValues.listContacts){

                        //if(mContactDetail.get_phone().toString().replaceAll("\\D+","").equals(mContactModel.get_phone().toString().replaceAll("\\D+","")) && mContactDetail.get_name().equals(mContactModel.get_name())){
                        if(mContactDetail.get_phone().equals(mContactModel.get_phone()) && mContactDetail.get_name().equals(mContactModel.get_name())){
                            isFound = true;
                            break contactImageLoopTag;
                        }
                    }

                    if(!isFound){
                        GlobalCommonValues.listContacts.add(mContactModel);
                    }
                }

            }while(mResultantCursor.moveToNext());
        }
    }


    /**
     * METHOD TO CLEAR DataBase Values
     */

    public static void clearDataBaseValues(Context mAct){
        DBQuery.deleteTable("BBContacts", "", null, mAct);
        DBQuery.deleteTable("Messages", "", null, mAct);
        DBQuery.deleteTable("Notifications", "", null,mAct);
    }

    /**
     * METHOD TO CLEAR PREFERENCES
     */

    public static void clearRegsitrationPreferences(Context mAct){
        try{
            SharedPreference saveState = new SharedPreference();
            saveState.setPassCode(mAct, "");
            saveState.setBBID(mAct, "");
            saveState.setCountryCode(mAct, "");
            saveState.setCountryidd(mAct, "");
            saveState.setCountryname(mAct, "");
            saveState.setIS_RecentRegistration(mAct, false);
            saveState.setISRETURNINGUSER(mAct, false);
            saveState.setPublicKey(mAct, "");
            saveState.setRegistered(mAct, false);
            saveState.setUserName(mAct, "");
            saveState.setUserPhoneNumber(mAct, "");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to clear all preferences
     * @param mAct
     */
    public static void clearAllPreferences(Context mAct){
        SharedPreferences preferences = mAct.getSharedPreferences(SharedPreference.APPLICATION_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    // com.RastrackCRT.routingapp.SendLocationService
    // com.tnc.service.RegistrationCheckService
    public static boolean isServiceRunning(Context mAct, String serviceName) {
        ActivityManager manager = (ActivityManager) mAct.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //	static Context mContext;
    public void downloadClipArts(Context mContext){

        DBQuery.createClipartsTable(mContext);
        /**
         *  String query = "CREATE TABLE IF NOT EXISTS Cliparts(id double,category double,clipart_order double,name VARCHAR,image VARCHAR,version VARCHAR,add_date datetime);";
         db.execSQL(query);
         */
        Intent mainIntent = new Intent(mContext,DownloadClipartImages.class);
        mContext.startService(mainIntent);
		/*GlobalConfig_Methods.mContext = mContext;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				new StartServiceClass().execute();
			}
		}, 100);*/
    }

	/*public class StartServiceClass extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);			
			Intent mainIntent = new Intent(GlobalConfig_Methods.mContext,DownloadClipartImages.class);
			GlobalConfig_Methods.mContext.startService(mainIntent);
		}
	}*/



    public static String getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width + "," + height;
    }

    public static void setHomeGridViewColumns(Context mContext){

        SharedPreference save_State = new SharedPreference();

        String mScreenResolution = save_State.getSCREEN_RESOLUTION(mContext);

        int mWidth = -1, mHeight = -1;
        if(!mScreenResolution.trim().equalsIgnoreCase("")){
            try {
                mWidth = Integer.parseInt(mScreenResolution.split(",")[0]);

                mHeight = Integer.parseInt(mScreenResolution.split(",")[1]);

                if((mHeight>1 && mWidth>1)&& (mHeight<=854 && mWidth<=480)){
                    DraggableGridViewPager.DEFAULT_COL_COUNT = 2;
                    DraggableGridViewPager.DEFAULT_ROW_COUNT = 1;
                }else{
                    DraggableGridViewPager.DEFAULT_COL_COUNT = 3;
                    DraggableGridViewPager.DEFAULT_ROW_COUNT = 2;
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    /**
     * Method to display the badge count on the App Icon
     * @param mContext
     * @param mbadgeCount
     */
    public static void displayBadgeCount(Context mContext, String mbadgeCount){
        int badgeCount = 0;
        try {
            badgeCount = Integer.parseInt(mbadgeCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(badgeCount>0){
            try {
                ShortcutBadger.applyCount(mContext, badgeCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to remove the badge count from the App Icon
     * @param mContext
     */
    public static void removeBadgeCount(Context mContext){
        try{
            ShortcutBadger.removeCount(mContext);
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * Method to Delete Files from App Directories
     * @param pathURL
     */
    public static void deleteFiles(String pathURL) {

        File file = new File(pathURL);

        if (file.exists()) {
            String deleteCmd = "rm -r " + pathURL;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }

    /**
     * Method to clear app data directories
     */
    public static void cleanAppDataFromSdCard(){


        String userImageFile = String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNC");

		/*String userImageDirectory= String.valueOf(
				Environment.getExternalStorageDirectory()+File.separator+"TNC");

		String tncImages= String.valueOf(
				Environment.getExternalStorageDirectory()+File.separator+"TNCImages");

		String tncTest= String.valueOf(
				Environment.getExternalStorageDirectory()+File.separator+"TNCTest");

		String userDatabase= String.valueOf(
				Environment.getExternalStorageDirectory()+File.separator+"TNCDatabase");

		//Delete User Images
		File mFileUserImage = new File(userImageDirectory);

		if(mFileUserImage.exists()){

			if (mFileUserImage.isDirectory()) 
			{
			    String[] children = mFileUserImage.list();
			    for (int i = 0; i < children.length; i++)
			    {
			       new File(mFileUserImage, children[i]).delete();
			    }
			}

			for (File c : mFileUserImage.listFiles())
				c.delete();
			//			mFileUserImage.delete();
		}

		//Delete TNC App Images
		File mAppImages = new File(tncImages);

		if(mAppImages.exists()){

			if (mAppImages.isDirectory()) 
			{
			    String[] children = mAppImages.list();
			    for (int i = 0; i < children.length; i++)
			    {
			       new File(mAppImages, children[i]).delete();
			    }
			}

			for (File c : mAppImages.listFiles())
				c.delete();
			//			mAppImages.delete();
		}

		//Delete TNC Test Directory
		File mTnCTestDirctory = new File(tncTest);

		if(mTnCTestDirctory.exists()){

			if (mTnCTestDirctory.isDirectory()) 
			{
			    String[] children = mTnCTestDirctory.list();
			    for (int i = 0; i < children.length; i++)
			    {
			       new File(mTnCTestDirctory, children[i]).delete();
			    }
			}

			for (File c : mTnCTestDirctory.listFiles())
				c.delete();
			//			mTnCTestDirctory.delete();
		}

		//Delete User Database
		File mFileDatabase = new File(userDatabase);

		if(mFileDatabase.exists()){

			if (mTnCTestDirctory.isDirectory()) 
			{
			    String[] children = mFileDatabase.list();
			    for (int i = 0; i < children.length; i++)
			    {
			       new File(mFileDatabase, children[i]).delete();
			    }
			}
			for (File c : mTnCTestDirctory.listFiles())
				c.delete();
			//			mFileDatabase.delete();
		}*/
    }

    /**
     * Method to check if the device is in airplane mode
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isAirplaneModeOn(Context context) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    /**
     * get phone profile ringer mode
     * @param mContext
     */
    public static String  getProfileRingerMode(Context mContext){
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        String mAudioMode = "";

        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                mAudioMode = GlobalCommonValues.PHONE_MODE_NORMAL;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                mAudioMode = GlobalCommonValues.PHONE_MODE_SILENT;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mAudioMode = GlobalCommonValues.PHONE_MODE_VIBRATE;
                break;
        }
        return mAudioMode;
    }

    /**
     * Method to display message
     * @param msg
     */
    public static void showSimErrorDialog(Context mAct, String msg){
        ImageRequestDialog dialog2=new ImageRequestDialog();
        dialog2.setCancelable(false);
        if(mAct instanceof MainBaseActivity)
        {
            dialog2.newInstance("", (MainBaseActivity)mAct,msg ,"",
                    null, null);
            dialog2.show(((MainBaseActivity)mAct).getSupportFragmentManager(), "test");
        }
        else if(mAct instanceof HomeScreenActivity)
        {
            dialog2.newInstance("", (HomeScreenActivity)mAct,msg ,"",
                    null, null);
            dialog2.show(((HomeScreenActivity)mAct).getSupportFragmentManager(), "test");
        }
    }

    /**
     * Method to check sim state in device
     * @return :sim state
     */
    public static String checkSimState(Context mAct){

        String mResponse = "";

        TelephonyManager telMgr = (TelephonyManager)mAct.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                mResponse = "No sim present";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // do something
                mResponse = "Sim network is lock";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // do something
                mResponse = "The device is in Airplane mode or has no sim card or outside of cellular service range";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // do something
                mResponse = "No sim present";
                break;
            case TelephonyManager.SIM_STATE_READY:
                mResponse = GlobalCommonValues.SUCCESS;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                mResponse = "No sim present";
                break;
        }
        return mResponse;
    }

    /**
     * Method to clear temp username
     * @param mContext
     */
    public static void clearTempUserName(Context mContext){
        SharedPreference saveState = new SharedPreference();
        //clear temp username
        if(!saveState.getUSER_NAME_TEMP(mContext).equalsIgnoreCase("")){
            saveState.setUSER_NAME_TEMP(mContext, "");
        }
    }

    //Get Category Id from the String Value
    public static String getCategoryIdFromString(Context mContext, String mCategory){

        String mCategoryID = "";

        if(mCategory.equalsIgnoreCase(GlobalCommonValues.RecentCalls) || mCategory.equalsIgnoreCase("0")){

            mCategoryID = GlobalCommonValues.ButtonTypeRecentCalls;

        }else if(mCategory.equalsIgnoreCase("All")){

            mCategoryID = GlobalCommonValues.ButtonTypeAll;

        }else if(mCategory.equalsIgnoreCase("Friend")){

            mCategoryID = GlobalCommonValues.ButtonTypeFriend;

        }else if(mCategory.equalsIgnoreCase("Family")){

            mCategoryID = GlobalCommonValues.ButtonTypeFamily;

        }else if(mCategory.equalsIgnoreCase("Business")){

            mCategoryID = GlobalCommonValues.ButtonTypeBusiness;

        }else{

            // check if category exists in the database and fetch it's ID
            mCategoryID = DBQuery.getCategoryIDFromName(mContext, mCategory);
            if(mCategoryID == null || mCategoryID.trim().equalsIgnoreCase("")){
                mCategoryID = GlobalCommonValues.ButtonTypeAll;
            }
        }
        return  mCategoryID;
    }

    //Get Category String Value from the ID
    public static String getCategoryNameFromId(Context mContext, String mCategoryID){

        String mCategoryName = "";

        if(mCategoryID.equalsIgnoreCase("0")){

            mCategoryName = GlobalCommonValues.ButtonTypeRecentCalls;

        }else if(mCategoryID.equalsIgnoreCase("All")){

            mCategoryName = GlobalCommonValues.ButtonTypeAll;

        }else if(mCategoryID.equalsIgnoreCase("2")){

            mCategoryName = "Friend";

        }else if(mCategoryID.equalsIgnoreCase("3")){

            mCategoryName = "Family";

        }else if(mCategoryID.equalsIgnoreCase("4")){

            mCategoryName = "Business";

        }else{
            // check if category exists in the database and fetch it's ID
            mCategoryName = DBQuery.getCategoryNameFromID(mContext, mCategoryID);
            if(mCategoryName == null || mCategoryName.trim().equalsIgnoreCase("")){
                mCategoryName = "Friend";
            }
        }
        return  mCategoryName;
    }

    /**
     * Method to enable/disable elderly mode options
     */
    public static void ConfigureElderlyModeOptions(Context mContext, boolean isDisableLongTap,
                                                   boolean isAutoSpeakerMode,
                                                   boolean isEmergencyContactNotification){
        SharedPreference mSaveState = new SharedPreference();

        mSaveState.setIsDisableLongTap(mContext, isDisableLongTap);

        mSaveState.setIsAutoSpeakerMode(mContext, isAutoSpeakerMode);

        mSaveState.setIsEmergencyContactNotification(mContext, isEmergencyContactNotification);
    }


    /**
     * Method to make phone calls
     */
    public static void makePhoneCall(Context mContext, String mDialledNumber){

        SharedPreference saveState = new SharedPreference();

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mDialledNumber));
        //update boolean value in preference as number is dialled from the app
        saveState.setIS_NUMBER_DIALLED(mContext, true);
        mContext.startActivity(callIntent);
    }


    /**
     * Method to fetch list of unread calls form the phone and insert in local DB
     */
    public static ArrayList<UserCallLogDataBean> getMissedCallsUnread(Context mContext, ContentResolver cr){

        ArrayList<UserCallLogDataBean> mListCallLogs = new ArrayList<UserCallLogDataBean>();

        // reading all data in descending order according to DATE

        // To get only unread missed calls
        String where_missed_calls = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + "=1";

        // For all unread calls
        String where_all_calls = CallLog.Calls.NEW + "=1";

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.query(callUri, null, where_all_calls, null, strOrder);

        // loop through cursor
        while (cur.moveToNext()) {

            UserCallLogDataBean mUserCallLogDataBean = new UserCallLogDataBean();

            String callNumber = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
            //system.out.println("callNumber>>" + callNumber);
            String callName = cur
                    .getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));

            //system.out.println("callName>>" + callName);
            String callDate = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.DATE));
            //system.out.println("<<<<<callDate>>>>>>    " + callDate);

            long millis = cur.getLong(cur.getColumnIndexOrThrow("date"));

            int time_format = 0;

            String mDisplayDate = "";

            try {
                time_format = Settings.System.getInt(mContext.getContentResolver(), Settings.System.TIME_12_24);
            }catch(Exception e){
                e.getMessage();
            }

            SimpleDateFormat dateFormat;

            if(time_format == 12){
                dateFormat = new SimpleDateFormat(
                        "MMM dd yyyy, KK:mm a");
            }else{
                dateFormat = new SimpleDateFormat(
                        "MMM dd yyyy, HH:mm");
            }

            String date = dateFormat.format(millis);
            //system.out.println("<<<<< converted callDate>>>>>>    " + date);

            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "MM-dd-yyyy HH:mm");

            String dateNow = formatter.format(currentDate.getTime());
            //system.out.println("dateNow>>>" + dateNow);
            String cal22 = "04-19-2014";
//			String cal11 = "07-19-2013";
			/*
			 * 07-08 02:58:53.039: I/System.out(1542): dateNow>>>2014-07-08
			 * 02:58:53
			 */

            /*int num = CalculateDays.number_Of_Days(dateNow, cal22);
            //system.out.println("num of days" + num);*/

            String callType = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.TYPE));

            //system.out.println("callType>>" + callType);

            String isCallNew = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.NEW));
            //system.out.println("callNew>>" + isCallNew);

            String duration = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.DURATION));
            long duration1 = Long.parseLong(duration) * 1000;
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(tz);
            duration = df.format(new Date(duration1));
            //system.out.println(duration);

            //system.out.println("callduration" + duration);

            if (callName != null && !callName.isEmpty()) {
                mUserCallLogDataBean.set_name(callName);
                System.out
                        .println("callName get data>>" + mUserCallLogDataBean.get_name());
            } else {
                mUserCallLogDataBean.set_name("Unknown");
                //mUserCallLogDataBean.set_name(mUserCallLogDataBean.get_phone());

                System.out
                        .println("callName get data  " + mUserCallLogDataBean.get_name());
            }

            mUserCallLogDataBean.set_phone(callNumber);

            mUserCallLogDataBean.set_date(date);
            mUserCallLogDataBean.set_type(callType);
            mUserCallLogDataBean.set_duration(duration);

            mListCallLogs.add(mUserCallLogDataBean);

            //DBQuery.insertAllUserCallLogFromPhone(cxt, user_data);

            // process log data...
        }
        return mListCallLogs;
    }

    /**
     * Update missed call status to read
     * @param mContext
     */
    public static void updateMissedCallsStatusToRead(Context mContext){
        try{
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NEW, Integer.valueOf(0));
            if (android.os.Build.VERSION.SDK_INT >= 14){
                //values.put(CallLog.Calls.IS_READ, Integer.valueOf(1));
                values.put(CallLog.Calls.IS_READ, true);
            }

            StringBuilder where = new StringBuilder();
            where.append(CallLog.Calls.NEW);
            where.append(" = 1 AND ");
            where.append(CallLog.Calls.TYPE);
            where.append(" = ?");
            ContentResolver cr = mContext.getContentResolver();
            Uri baseUri = Uri.parse("content://call_log/calls");
            int numUpdated = cr.update(baseUri, values, where.toString(),new String[]{ Integer.toString(CallLog.Calls.MISSED_TYPE) });
        }catch(Exception e){
            e.getMessage();
        }
    }

    /**
     * Method to check if device's gps is enabled or not
     * @param mContext
     * @return
     */
    public static boolean isGPSEnabled(Context mContext) {
        boolean isGpsEnabled = true;

        LocationManager locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGpsEnabled = false;
        }

        return isGpsEnabled;
    }

    /**
     * Method to insert call logs
     * @param mcontext
     * @param mPhonenumber
     * @param callType
     */

    public static long insertCallLog(Context mcontext, String mPhonenumber, int callType){

        long mCount = 0L;
        SharedPreference saveState = new SharedPreference();

        try{
    /*        Toast.makeText(mcontext,
                    "insertCallLog() : " + iCount,
                    Toast.LENGTH_LONG).show();
            iCount++ ;*/
            long milliSeconds = System.currentTimeMillis();

            SimpleDateFormat dateFormat;

            dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            //2016-12-13 05:11:01

            String date = dateFormat.format(milliSeconds);

            CallDetailsBean mCallDetailsBean = new CallDetailsBean();

            mCallDetailsBean.setCallName("");
            mCallDetailsBean.setPrefix("");
            mCallDetailsBean.setCountryCode("");
            mCallDetailsBean.setCallingNumber(mPhonenumber);
            mCallDetailsBean.setCallTime(date);
            mCallDetailsBean.setCallType(callType);
            if(callType == GlobalCommonValues.CallTypeMissed){
                // set Status as un - read for missed calls
                mCallDetailsBean.setStatus(1);
            }else{
                // set Status as read for rest of the calls i.e. incoming & outgoing
                mCallDetailsBean.setStatus(2);
            }

            if(saveState.getIS_EMERGENCY_NUMBER_DIALLED(mcontext)){
                mCallDetailsBean.setEmergencyCall(true);
            }else{
                mCallDetailsBean.setEmergencyCall(false);
            }

            saveState.setIS_EMERGENCY_NUMBER_DIALLED(mcontext, false);

            mCount = DBQuery.insertCallDetails(mcontext, mCallDetailsBean);
        }catch(Exception e){
            e.getMessage();
            mCount = 0;
        }
        return mCount;
    }

    /**
     * Not in Use
     * @param mcontext
     * @param mPhonenumber
     * @param callType
     */
    public static void insertCallLogDummy(Context mcontext, String mPhonenumber, int callType){
        try{
            SharedPreference saveState = new SharedPreference();
            String mUserName = "";
            ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();

            // split the number as per splitting rule algorithm
            String strNumber = "", numberRegisteredUser = "";

            /*String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
            boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;*/
            String []arrayUserDetails = new String[80];

            try {
                strNumber = GlobalConfig_Methods.getBBNumberToCheck(mcontext, mPhonenumber);
                arrayUserDetails = strNumber.split(",");
                numberRegisteredUser=arrayUserDetails[1];
            } catch (Exception e) {
                e.getMessage();
            }


            if(new SharedPreference().isRegistered(mcontext)){
                try {
                    /*strNumber = GlobalConfig_Methods.getBBNumberToCheck(mcontext, mPhonenumber);
                    numberRegisteredUser=arrayUserDetails[1];*/
                    if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase("")){
                        // in cas enumber successfully splitted from the algorithm
                        // check in Tiles table for existence of contacts
                        // fetch details from tile table using phonenumber
                        listContactTiles=DBQuery.getTileFromPhoneNumber(mcontext,
                                GlobalConfig_Methods.trimSpecialCharactersFromString(numberRegisteredUser));
                    }else{
                        // in case unable to find number from splitting algorithm
                        // check in Tiles table for existence of contacts
                        // fetch details from tile table using phonenumber
                        listContactTiles=DBQuery.getTileFromPhoneNumber(mcontext,
                                GlobalConfig_Methods.trimSpecialCharactersFromString(mPhonenumber));
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }else{
                // In case of un-registered user
                /*strNumber = GlobalConfig_Methods.getBBNumberToCheck(mcontext, mPhonenumber);
                numberRegisteredUser=arrayUserDetails[1];*/

                // check in Tiles table for existence of contacts
                // fetch details from tile table using phonenumber
                listContactTiles=DBQuery.getTileFromPhoneNumber(mcontext,
                        GlobalConfig_Methods.trimSpecialCharactersFromString(numberRegisteredUser));
            }

            // check in Tiles table for existence of contacts
            // fetch details from tile table using phonenumber
            /*listContactTiles=DBQuery.getTileFromPhoneNumber(mcontext,
                    GlobalConfig_Methods.trimSpecialCharactersFromString(mPhonenumber));*/

            if(listContactTiles!=null && listContactTiles.size()>0){
                mUserName = listContactTiles.get(0).getName();
            }else if((mUserName == null || mUserName.trim().equalsIgnoreCase(""))){
                int BBID;

                if((saveState.isRegistered(mcontext))){
                    // get BBID from phone number and country Code
                    BBID = DBQuery.getBBIDFromPhoneNumber(mcontext, numberRegisteredUser);

                    // fetch name from BBContacts Table on the basis of BBID
                    if(BBID > 0){
                        // check in BBContacts table for existence of contacts
                        mUserName = DBQuery.getContactTileNameFromBBID(mcontext,
                                BBID, false);
                    }else{
                        // fetch contact name from phone
                        mUserName = GlobalConfig_Methods.getContactName(mcontext, mPhonenumber);
                        if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                            mUserName = "Unknown";
                        }
                    }
                }else{
                    // fetch the contact name from the phone contact list
                    // fetch contact name from phone
                    mUserName = GlobalConfig_Methods.getContactName(mcontext, mPhonenumber);
                    if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                        mUserName = "Unknown";
                    }
                }
            }

            if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                // fetch contact name from phone
                mUserName = GlobalConfig_Methods.getContactName(mcontext, mPhonenumber);
            }else {
                if(mUserName == null || mUserName.trim().equalsIgnoreCase("Unknown"))
                    mUserName = "Unknown";
            }

            if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                mUserName = "Unknown";
            }

            long milliSeconds = System.currentTimeMillis();

            SimpleDateFormat dateFormat;

            dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            //2016-12-13 05:11:01

            String date = dateFormat.format(milliSeconds);

            CallDetailsBean mCallDetailsBean = new CallDetailsBean();

            mCallDetailsBean.setCallName(mUserName);
            mCallDetailsBean.setPrefix("");
            mCallDetailsBean.setCountryCode("");
            mCallDetailsBean.setCallingNumber(mPhonenumber);
            mCallDetailsBean.setCallTime(date);
            mCallDetailsBean.setCallType(callType);
            mCallDetailsBean.setStatus(1);
            DBQuery.insertCallDetails(mcontext, mCallDetailsBean);
        }catch(Exception e){
            e.getMessage();
        }
    }

    /**
     * Change Date Display
     * @param time
     * @return
     */
    public static String changeDateFormatToMMMddyyyy(Context mContext, String time) {

        int time_format = 0;
        try {
            time_format = Settings.System.getInt(mContext.getContentResolver(), Settings.System.TIME_12_24);
        }catch(Exception e){
            e.getMessage();
        }

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "";
        if(time_format == 12){
            outputPattern = "MMM dd yyyy, KK:mm a";
        }else{
            outputPattern = "MMM dd yyyy, HH:mm";
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     *
     * @param mContext
     * @param mCountryCode
     * @param mPhoneNumber
     */
    public static String getUserNameFromAlgorithm(Context mContext, String mCountryCode,
                                                  String mPhoneNumber,
                                                  INotifySetImageBitmapUrl mINotifySetImageBitmapUrl){

        String mUserNameReturn                       = "";

        ArrayList<ContactTilesBean> listContactTiles = new ArrayList<ContactTilesBean>();
        ArrayList<BBContactsBean> listBBContacts     = new ArrayList<BBContactsBean>();

        String mUserName                             = "", mDisplayImageFromBBID = "";
        Bitmap displayImageFromTile                  = null, displayImageFromPhoneContact = null;

        int BBID                                     =  -1;
        boolean isTncUser                            = false;
        SharedPreference saveState                   = new SharedPreference();

        // split the number as per splitting rule algorithm
        String strNumber = "", numberRegisteredUser = "";
        String []arrayUserDetails = new String[80];

        try {
            strNumber = GlobalConfig_Methods.getBBNumberToCheck(mContext, mPhoneNumber);
            arrayUserDetails = strNumber.split(",");
            numberRegisteredUser=arrayUserDetails[1];
            if(arrayUserDetails[5]!=null && !arrayUserDetails[5].trim().equalsIgnoreCase(""))
                if(Boolean.parseBoolean(arrayUserDetails[5]) == true)
                    isTncUser = true;
        } catch (Exception e) {
            e.getMessage();
        }


        try{
            if(numberRegisteredUser!=null && !numberRegisteredUser.trim().equalsIgnoreCase("")){
                // in case number successfully splitted from the algorithm
                // check in Tiles table for existence of contacts
                // fetch details from tile table using phonenumber
                listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,
                        GlobalConfig_Methods.trimSpecialCharactersFromString(numberRegisteredUser));
            }else{
                // in case unable to find number from splitting algorithm
                // check in Tiles table for existence of contacts
                // fetch details from tile table using phonenumber
                listContactTiles=DBQuery.getTileFromPhoneNumber(mContext,
                        GlobalConfig_Methods.trimSpecialCharactersFromString(mPhoneNumber));
            }
        }catch(Exception e){
            e.getMessage();
        }

        // In case of Tile Exists for the user number
        if(listContactTiles!=null && listContactTiles.size()>0){
            mUserName = listContactTiles.get(0).getName();

            //set image array if tile table has an image array

            if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0) {
                byte arrayImage[] = listContactTiles.get(0).getImage();
                if (arrayImage != null && arrayImage.length > 0) {
                    displayImageFromTile = BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length);
                }
            }

            // Check if it is a TnC user
            if(listContactTiles.get(0).isIsTncUser()){
                isTncUser = true;
            }

        }else if((mUserName == null || mUserName.trim().equalsIgnoreCase(""))){

            if((saveState.isRegistered(mContext))){
                // get BBID from phone number and country Code
                BBID = DBQuery.getBBIDFromPhoneNumber(mContext, numberRegisteredUser);

                // fetch name from BBContacts Table on the basis of BBID
                if(BBID > 0){

                    // Set it as a TnC user
                    isTncUser = true;

                    // fetch image from bbContacts table if doesn't exists in tile table
                    listBBContacts=DBQuery.checkBBContactExistence(mContext,BBID);
                    if(mUserName!= null && listBBContacts.size() > 0){
                        if(listBBContacts.get(0).getName()!=null &&
                                !listBBContacts.get(0).getName().trim().equalsIgnoreCase("")){
                            mUserName = listBBContacts.get(0).getName();
                        }

                        if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equalsIgnoreCase("")){
                            mDisplayImageFromBBID = listBBContacts.get(0).getImage();
                        }
                    }
                    // check in BBContacts table for existence of contacts
                        /*mUserName = DBQuery.getContactTileNameFromBBID(mContext,
                                BBID, false);*/
                }else{
                    // fetch contact name from phone
                    mUserName = GlobalConfig_Methods.getContactName(mContext, mPhoneNumber);
                    if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                        mUserName = "Unknown";
                    }
                    // fetch User Image from Phone Contact
                    displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mPhoneNumber);
                }
            }else{
                // fetch the contact name from the phone contact list
                // fetch contact name from phone
                mUserName = GlobalConfig_Methods.getContactName(mContext, mPhoneNumber);
                if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
                    mUserName = "Unknown";
                }

                // fetch User Image from Phone Contact
                displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mPhoneNumber);
            }
        }

        if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
            // fetch contact name from phone
            mUserName = GlobalConfig_Methods.getContactName(mContext, mPhoneNumber);

            // fetch User Image from Phone Contact
            displayImageFromPhoneContact = GlobalConfig_Methods.getContactBitmap(mContext, mPhoneNumber);

        }else {
            if(mUserName == null || mUserName.trim().equalsIgnoreCase("Unknown"))
                mUserName = "Unknown";
        }

        if(mUserName == null || mUserName.trim().equalsIgnoreCase("")){
            mUserName = "Unknown";
        }


        if(mUserName == null ||
                mUserName.equalsIgnoreCase("") ||
                mUserName.equalsIgnoreCase("Unknown")){
            mUserNameReturn = "";
        }else{
            mUserNameReturn = mUserName;
        }

        if(mINotifySetImageBitmapUrl!=null){

            //Set Contact Display Image
            if(displayImageFromTile!=null){
                // Set Tile Image
                mINotifySetImageBitmapUrl.setBitmap(displayImageFromTile);
            }else if(mDisplayImageFromBBID!=null && mDisplayImageFromBBID.length() > 0){
                // Set Image from BBContacts Table
                mINotifySetImageBitmapUrl.setUrl(mDisplayImageFromBBID);
                //holder.imViewContactImage.setImageBitmap(displayImageFromTile);
            }else if(displayImageFromPhoneContact!=null){
                // Set Phone Contact Image
                mINotifySetImageBitmapUrl.setBitmap(displayImageFromPhoneContact);

            }else{
                // Set Default Image
                mINotifySetImageBitmapUrl.setBitmap(((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap());
                //holder.imViewContactImage.setImageResource(R.drawable.no_image);
            }

        }

        return mUserNameReturn;
    }

    /**
     * Method to Get Formatted DateTime In MilliSeconds
     * @param mTimeStamp
     * @return
     */
    public static long getFormattedDateTimeInMilliSeconds(String mTimeStamp){
        long timeInMilliseconds = 0l;

        String givenDateString = mTimeStamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
            //system.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (Exception e) {
            e.printStackTrace();
            timeInMilliseconds = 0l;
        }

        return  timeInMilliseconds;
    }


    /**
     *  Method to Get Formatted Date From MilliSeconds
     * @param context
     * @param smsTimeInMilis
     * @return
     */
    public static String getFormattedDateFromMilliSeconds(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "KK:mm a";
        String dateTimeFormatString = "", dateTimeFormatStringForTodayYesterday;

        int time_format = 0;

        try {
            time_format = Settings.System.getInt(context.getContentResolver(), Settings.System.TIME_12_24);
        }catch(Exception e){
            e.getMessage();
        }

        if(time_format == 12){
            dateTimeFormatString                  = "MMMM dd, hh:mm a";  // hh:mm a;
            dateTimeFormatStringForTodayYesterday = "hh:mm a";
        }else{
            dateTimeFormatString                  = "MMMM dd, HH:mm";
            dateTimeFormatStringForTodayYesterday = "HH:mm";
        }


        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return GlobalCommonValues.DAY_TODAY + ", " + new SimpleDateFormat(dateTimeFormatStringForTodayYesterday).format(new Date(smsTimeInMilis)).toString();   //"Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return GlobalCommonValues.DAY_YESTERDAY + ", " + new SimpleDateFormat(dateTimeFormatStringForTodayYesterday).format(new Date(smsTimeInMilis)).toString();  //"Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } /*else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return new SimpleDateFormat(dateTimeFormatString).format(new Date(smsTimeInMilis)).toString(); //DateFormat.format(dateTimeFormatString, smsTime).toString();
        }*/ else {
            return new SimpleDateFormat(dateTimeFormatString).format(new Date(smsTimeInMilis)).toString(); //return DateFormat.format("MMMM dd, KK:mm a", smsTime).toString();
        }
    }

    public static void showMap(Context mContext, double mLatitude, double mLongitude, String mPlaceName) {

        boolean installedMaps = false;

        // CHECK IF GOOGLE MAPS IS INSTALLED
        PackageManager pkManager = mContext.getPackageManager();
        try {
            @SuppressWarnings("unused")
            PackageInfo pkInfo = pkManager.getPackageInfo("com.google.android.apps.maps", 0);
            installedMaps = true;
        } catch (Exception e) {
            e.printStackTrace();
            installedMaps = false;
        }

        // SHOW THE MAP USING CO-ORDINATES FROM THE CHECKIN
        if (installedMaps == true) {
            String geoCode = "geo:0,0?q=" + mLatitude + ","
                    + mLongitude + "(" + mPlaceName + ")";
            Intent sendLocationToMap = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(geoCode));
            mContext.startActivity(sendLocationToMap);
        } else if (installedMaps == false) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mContext);

            // SET THE ICON
            alertDialogBuilder.setIcon(R.drawable.appicon_96);

            // SET THE TITLE
            alertDialogBuilder.setTitle("Google Maps Not Found");

            // SET THE MESSAGE
            alertDialogBuilder
                    .setMessage("No Map Application is installed")
                    .setCancelable(false)
                    .setNeutralButton("Got It",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                }
                            });

            // CREATE THE ALERT DIALOG
            AlertDialog alertDialog = alertDialogBuilder.create();

            // SHOW THE ALERT DIALOG
            alertDialog.show();
        }
    }

    public static void clearSelectedcategoryValueFromPreferences(Context mContext){
        SharedPreference saveState = new SharedPreference();
        saveState.setSELECTED_CATEGORY(mContext, "");
    }

    public static boolean isMyLauncherDefault(Context mContext) {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = mContext.getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) mContext.getPackageManager();

        // You can use name of your package here as third argument
        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to clear defaults of an app if any is set
     * @param mActivity
     */
    public static void clearDefaults(Activity mActivity){
        mActivity.getPackageManager().clearPackagePreferredActivities("com.tnc");
    }

    /**
     * Method to Disconnect Call
     */
    public static void disconnectCall(){
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            Log.i("CallScreenActivity",
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.i("CallScreenActivity", "Exception object: " + e);
        }

        /*try {
					Class c = Class.forName(tm.getClass().getName());
					Method m = c.getDeclaredMethod("getITelephony");
					m.setAccessible(true);
					Object telephonyService = m.invoke(tm);

					c = Class.forName(telephonyService.getClass().getName());
					m = c.getDeclaredMethod("endCall");
					m.setAccessible(true);
					m.invoke(telephonyService);

				} catch (Exception e) {
					e.printStackTrace();
				}
		*/
    }

    /**
     * Method to enable LoudSpeaker for call
     */
    public static void enableDisableLoudSpeakerCall(Context mContext){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // check if the autospeaker mode is ON
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        // Enable LoudSpeaker if disabled otherwise disable it if enabled
        if(audioManager.isSpeakerphoneOn()){
            audioManager.setSpeakerphoneOn(false);
        }else{
            audioManager.setSpeakerphoneOn(true);
        }
    }

    /**
     * Method to enable LoudSpeaker for normal mode
     */
    public static void enableDisableLoudSpeakerNormal(Context mContext){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // check if the autospeaker mode is ON
        audioManager.setMode(AudioManager.MODE_NORMAL);

        // Enable LoudSpeaker if disabled otherwise disable it if enabled
        if(audioManager.isSpeakerphoneOn()){
            audioManager.setSpeakerphoneOn(false);
        }else{
            audioManager.setSpeakerphoneOn(true);
        }
    }

    /**
     * Method to enable / disbale Microphone Mute
     */
    public static void enableDisableMicroPhoneMute(Context mContext){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // check if the microphone is mute or not
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        // Enable microphone if disabled otherwise disable it if enabled
        if(audioManager.isMicrophoneMute()){
            audioManager.setMicrophoneMute(false);
        }else{
            audioManager.setMicrophoneMute(true);
        }
    }

    /**
     * Check for Bluetooth
     * @return True if Bluetooth is available.
     */
    public static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isBluetoothEnabled = false;

        if(bluetoothAdapter == null){
            isBluetoothEnabled = false;
        }else{
            isBluetoothEnabled = (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
        }
        return isBluetoothEnabled;
    }

    /**
     * Method to check LoudSpeaker enabled / disabled
     */
    public static boolean isLoudSpeakerEnabled(Context mContext){
        boolean isLoudSpeakerEnabled = false;

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // check if the Auto-Speaker mode is ON
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(audioManager.isSpeakerphoneOn()){
            isLoudSpeakerEnabled = true;
        }else{
            isLoudSpeakerEnabled = false;
        }
        return isLoudSpeakerEnabled;
    }

    /**
     * Method to check Mute enabled / disabled
     */
    public static boolean isMicroPhoneMute(Context mContext){
        boolean isMicroPhoneMute = false;

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // check if the Auto-Speaker mode is ON
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(audioManager.isMicrophoneMute()){
            isMicroPhoneMute = true;
        }else{
            isMicroPhoneMute = false;
        }
        return isMicroPhoneMute;
    }

    /**
     * Method to detect if call is active / not
     * @param context
     * @return
     */
    public boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }
        else{
            return false;
        }
    }

    public final static String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    public final static String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    public final static String AUDIO_RECORDER_FILE_EXT_AMR = ".amr";
    //    final static String AUDIO_RECORDER_FOLDER = R.string.app_name + File.pathSeparator + "AudioRecorder";
    static MediaRecorder recorder = null;

    /**
     * Method to Get the file name
     */
    private static String getFilename(Context mContext) {

        String AUDIO_RECORDER_FOLDER = mContext.getResources().getString(R.string.app_name)
                + File.separator + "AudioRecorder";

        String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP,
                AUDIO_RECORDER_FILE_EXT_AMR};
        int currentFormat = 0;

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        try {
            FileUtils.deleteDirectory(new File(file.getAbsolutePath()));
        }catch(Exception e){
            e.getMessage();
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        String bbid = new SharedPreference().getBBID(mContext);

        return (file.getAbsolutePath() + "/" + bbid + file_exts[currentFormat]);
//        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }

    /**
     * Method to Record Audio
     */

    public static void startRecording(final Context mContext) {

        recorder = null;

//        int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };

        MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
            }
        };

        MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
            }
        };
        recorder = new MediaRecorder();
//        recorder.setMaxDuration(5000);  for 5 seconds
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename(mContext));
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pauseRecording() {
        if (null != recorder) {
            recorder.pause();
        }
    }

    public static void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    /**
     * Method to check greeting message existence
     * @param mContext
     * @return
     */
    public static boolean isGreetingMessageExist(Context mContext){

        boolean isGreetingExist = false;

        try{

            File file = getGreetingMessageFilePath(mContext);

            if(file!=null && file.exists() && file.canRead()){
                isGreetingExist = true;
            }

        }catch(Exception e){
            e.getMessage();
        }

        return isGreetingExist;
    }

    public static File getGreetingMessageFilePath(Context mContext){

        SharedPreference saveState = new SharedPreference();

        String mFileName = saveState.getBBID(mContext) + GlobalConfig_Methods.AUDIO_RECORDER_FILE_EXT_MP4;

        String AUDIO_RECORDER_FOLDER = mContext.getResources().getString(R.string.app_name)
                + File.separator + "AudioRecorder";

        // get File storage path
        String filepath = Environment.getExternalStorageDirectory().getPath() + File.separator +
                AUDIO_RECORDER_FOLDER;
        File file = new File(filepath, mFileName);

        return  file;
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    /**
     * go back one screen
     * @param mContext
     */
    public static void goToBackScreen(Context mContext){
        if(mContext instanceof MainBaseActivity){
            ((MainBaseActivity)mContext).fragmentManager.popBackStack();
        }else if(mContext instanceof HomeScreenActivity){
            ((HomeScreenActivity)mContext).fragmentManager.popBackStack();
        }
    }

    /**
     * go to google play store to download Premium Version of the app
     */
    public static void gotoPremiumVersionPlayStore(Context mContext){
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse("market://details?id=com.tnc"));
            mContext.startActivity(viewIntent);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to Set Enable / Disable Airplane Mode
     * @param isAirplaneMode
     */
    /*public static void toggleAirplaneMode(boolean isAirplaneMode) {

        String mCommandEnableFirst  = "";
        String mCommandEnableSecond = "";

        if(isAirplaneMode){
            mCommandEnableFirst = "settings put global airplane_mode_on 0";
            mCommandEnableSecond = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
        }else{
            mCommandEnableFirst = "settings put global airplane_mode_on 1";
            mCommandEnableSecond = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
        }

        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(mCommandEnableFirst);
            runtime.exec(mCommandEnableSecond);
        } catch (Exception e) {
            Log.e("Error2", e.getMessage());
        }
    }*/

    /**
     * Method to check valid string
     * @param mOption
     * @return
     */
    public static boolean isValidString(String mOption){
        boolean isValidString = true;
        if((mOption == null) || (mOption.trim().equalsIgnoreCase("")) ||
                (mOption.trim().equalsIgnoreCase("null"))) {
            isValidString = false;
        }
        return isValidString;
    }



    /**
     * Method to check valid list
     * @param :listData
     * @return
     */
    public static boolean isValidList(ArrayList listData){
        boolean isValidList = true;
        if((listData == null) || (listData.size() == 0)) {
            isValidList = false;
        }
        return isValidList;
    }

    public static boolean isValidArrayList(ArrayList<?> listData){
        boolean isValid =  true;
        if((listData == null) || (listData.size() == 0)){
            isValid = false;
        }
        return isValid;
    }


    public static Date getDateFromStringDate(String date){
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//        DateFormat df = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss a");
        Date startDate = null;
        try {
            startDate = df.parse(date);
//            String newDateString = df.format(startDate);
            //system.out.println(newDateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    /**
     * Method to display alert message, dismiss with user comfirmation
     * @param messageToDisplay
     */
    public  static void  displayAlertDialog(String messageToDisplay, FragmentActivity activity){
        TileUpdateSuccessDialog dialog = new TileUpdateSuccessDialog();
        dialog.setCancelable(false);
        dialog.newInstance("", activity, messageToDisplay);
        dialog.show(activity.getSupportFragmentManager(), "test");
    }

    public  static void  displayAlertDialog(String messageToDisplay, FragmentActivity activity, INotifyAction iNotifyAction){
        TileUpdateSuccessDialog dialog = new TileUpdateSuccessDialog();
        dialog.setCancelable(false);
        dialog.newInstance("", activity, messageToDisplay,iNotifyAction);
        dialog.show(activity.getSupportFragmentManager(), "test");
    }

    /**
     * Method to cancel registration
     * @param mContext
     * @param passCode
     */
    public static void cancelRegistration(Context mContext, String passCode){
        if (mContext instanceof MainBaseActivity) {
            SharedPreference saveState = new SharedPreference();
            CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
            objCancelRegistration.setPasscode(saveState.getPassCode(mContext));
            ((MainBaseActivity) mContext)
                    .cancelRegistration(objCancelRegistration);
            mContext.startActivity(new Intent(((MainBaseActivity) mContext),
                    HomeScreenActivity.class));
            ((MainBaseActivity) mContext).finish();
        } else if (mContext instanceof HomeScreenActivity) {
            SharedPreference saveState = new SharedPreference();
            CancelRegistrationRequestBean objCancelRegistration = new CancelRegistrationRequestBean();
            objCancelRegistration.setPasscode(saveState.getPassCode(mContext));
            ((HomeScreenActivity) mContext)
                    .cancelRegistration(objCancelRegistration);
            mContext.startActivity(new Intent(((HomeScreenActivity) mContext),
                    HomeScreenActivity.class));
            ((HomeScreenActivity) mContext).finish();
        }
    }


    public static void displayWelcomeBackDialog(Context mContext){
        String _title="";
        String _message = mContext.getResources().getString(R.string.txtReturningUserWelcome)+" "+mContext.getResources().getString(R.string.app_name)+" "+ mContext.getResources().getString(R.string.txtRecoverCloudBackupMessage);
        WelcomeBackReg_RestoreDialog welcomeBackRegistrationDialog=
                new WelcomeBackReg_RestoreDialog();
        welcomeBackRegistrationDialog.setCancelable(false);
        welcomeBackRegistrationDialog.newInstance(_title, mContext,_message,"");
        if (mContext instanceof MainBaseActivity)
        {
            welcomeBackRegistrationDialog.show(((MainBaseActivity)mContext).getSupportFragmentManager(),"test");
        }
        else if (mContext instanceof HomeScreenActivity)
        {
            welcomeBackRegistrationDialog.show(((HomeScreenActivity)mContext).getSupportFragmentManager(),"test");
        }
    }

    /**
     * method to underline partial text
     * @param textView
     * @param stringToUpdate
     * @param startIndex
     * @param endIndex
     */
    public static void underLinePartialText(TextView textView, String stringToUpdate, int startIndex, int endIndex, boolean isDoubleStyle,
                                            int startIndex2, int endIndex2){
        SpannableString content = new SpannableString(stringToUpdate);
        content.setSpan(new UnderlineSpan(), startIndex, endIndex, 0);//where first 0 shows the starting and stringToUpdate.length() shows the ending span.if you want to span only part of it than you can change these values like 5,8 then it will underline part of it.
        if(isDoubleStyle) {
            content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(content);
    }

    //    static String strStyledText = "";
    static SpannableString content = null;

    /**
     * Method to set text style
     * @param textView
     * @param stringToUpdate
     * @param startIndex
     * @param endIndex
     * @param isDoubleStyle
     * @param startIndex2
     * @param endIndex2
     */
    public static void setTextStyleThread(final TextView textView, final String stringToUpdate, final int startIndex, final int endIndex, final boolean isDoubleStyle,
                                          final int startIndex2, final int endIndex2) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                /*try {
                    Thread.sleep(1000);*/
                content = new SpannableString(stringToUpdate);
                content.setSpan(new UnderlineSpan(), startIndex, endIndex, 0);//where first 0 shows the starting and stringToUpdate.length() shows the ending span.if you want to span only part of it than you can change these values like 5,8 then it will underline part of it.
                if(isDoubleStyle) {
                    content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
//                    strStyledText = content.toString();
                /*}
                catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                handler.post(new Runnable(){
                    public void run() {
                        textView.setText(content);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    /**
     * Set RobotoBoldFont for textView and EditText
     * @param mContext
     * @param editText
     */
    public static void setRobotoBoldFontStyle(Context mContext, EditText editText, TextView textView){
        if(editText!=null){
            CustomFonts.setFontOfEditText(mContext, editText,
                    "fonts/Roboto-Bold_1.ttf");
        }
        if(textView!=null){
            CustomFonts.setFontOfTextView(mContext, textView,
                    "fonts/Roboto-Bold_1.ttf");
        }

    }
}
