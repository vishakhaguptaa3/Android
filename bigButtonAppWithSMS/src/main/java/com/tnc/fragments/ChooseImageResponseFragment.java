package com.tnc.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.ContactListAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.NotificationsBeanImageUpload;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.BackupRestoreSuccessDialog;
import com.tnc.dialog.DefaultImageConfirmation;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ImageUploadConfirmation;
import com.tnc.dialog.ImageUploadSuccessDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.SetDefaultImageResponse;
import com.tnc.webresponse.UploadImageResponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
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

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * class to choose image to be send as a response for the requestor who made request to send him/her the tile image 
 *  @author a3logics
 */

public class ChooseImageResponseFragment extends BaseFragmentTabs implements
OnClickListener {
	private FrameLayout flBackArrow, flInformationButton;
	private TextView tvTitle, tvStep, tvChooseButton;
	private ListView lvImageMenu;
	private Button btnBack, btnHome, btnSkip;
	private String title;
	private ArrayList<String> listMenu;
	private ContactListAdapter adapterMenu = null;
	private String path = "";
	private Bitmap image = null;
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
	private final int PICK_IMAGE_REQUEST = 210;
	private Uri fileUri;
	private String picturePath = "";
	private ContactDetailsBean selectedContactBean = null;
	private Context mActivityTabs;
	private int adapterSelected_position = 0;
	private String notifications_id = "", NotificationStatus = "";
	private Gson gson = null;
	private TransparentProgressDialog progress;
	boolean selectedDefault = false;

	public ChooseImageResponseFragment newInstance(Context mActivityTabs,
			String NotificationId, String NotificationStatus) {
		ChooseImageResponseFragment frag = new ChooseImageResponseFragment();
		this.mActivityTabs = mActivityTabs;
		this.notifications_id = NotificationId;
		this.NotificationStatus = NotificationStatus;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chooseimagefragment, container,
				false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view) {
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivityTabs,
				R.drawable.customspinner);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		flInformationButton = (FrameLayout) view
				.findViewById(R.id.flInformationButton);
		tvStep = (TextView) view.findViewById(R.id.tvStep);
		tvChooseButton = (TextView) view.findViewById(R.id.tvChooseButton);
		lvImageMenu = (ListView) view.findViewById(R.id.lvImageMenu);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnHome = (Button) view.findViewById(R.id.btnHome);
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		flInformationButton.setVisibility(View.VISIBLE);
		btnHome.setVisibility(View.VISIBLE);
		tvStep.setVisibility(View.GONE);

		tvChooseButton.setAllCaps(true);

		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvStep,
				"fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvChooseButton,
				"fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnSkip,
				"fonts/Roboto-Regular_1.ttf");
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnSkip.setVisibility(View.GONE);
		listMenu = new ArrayList<String>();
		try {
			if (saveState.getDisplayDEFAULTIMAGEString(getActivity()).equalsIgnoreCase("true"))
				listMenu.add(getActivity().getResources().getString(R.string.txtSendProfileImage));
		} catch (Exception e) {
			e.getMessage();
		}
		listMenu.add(getActivity().getResources().getString(R.string.txtChooseFromGallery));
		listMenu.add(getActivity().getResources().getString(R.string.txtTakeNewPhoto));
		listMenu.add(getActivity().getResources().getString(R.string.txtClipArt));
		flBackArrow.setVisibility(View.VISIBLE);
		adapterMenu = new ContactListAdapter(mActivityTabs, null, listMenu,
				true);
		adapterMenu.notifyDataSetChanged();
		lvImageMenu.setAdapter(adapterMenu);
		btnHome.setOnClickListener(this);
		lvImageMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				adapterSelected_position = position;
				adapterMenu.setRowColor(adapterSelected_position, true);
				// in case of send default image
				if (listMenu.get(position).equalsIgnoreCase(
						getActivity().getResources().getString(R.string.txtSendProfileImage))) // Send Default Image
				{
					selectedDefault = true;
					NotificationsBeanImageUpload objNotificationBean = new NotificationsBeanImageUpload(
							notifications_id);
					uploadImage(objNotificationBean, null);
				}
				// in case of choose image from gallery
				if (listMenu.get(position).equalsIgnoreCase(
						getActivity().getResources().getString(R.string.txtChooseFromGallery))) // From Gallery
				{
					selectedDefault = false;
					//					GlobalCommonValues.selectedClipArtUrl = "";
					MainBaseActivity.isImageRequested = false;
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(
							Intent.createChooser(intent, "Complete action using"),
							PICK_IMAGE_REQUEST);

				}// in case of click camera image 
				else if (listMenu.get(position).equalsIgnoreCase(
						getActivity().getResources().getString(R.string.txtTakeNewPhoto))) // Camera Click
				{
					selectedDefault = false;
					MainBaseActivity._bitmap = null;
					MainBaseActivity.isImageSelected = false;
					MainBaseActivity.isImageRequested = false;
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputImageUri(); // create a file to save the
					// image in specific folder
					if (fileUri != null) {
						intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					}
					startActivityForResult(intent,
							REQUEST_CODE_TAKE_PICTURE);
				}// in case of cliparts
				else if (listMenu.get(position).equalsIgnoreCase(
						getActivity().getResources().getString(R.string.txtClipArt)))
				{
					selectedDefault = false;
					MainBaseActivity.isImageRequested = false;
					ClipArtsFragmentImageResponse objClipArts = new ClipArtsFragmentImageResponse();
					((HomeScreenActivity) mActivityTabs)
					.setFragment(objClipArts);

				}
			}
		});
		btnBack.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
	}

	// interface to call webservice to make default Image
	INotifyGalleryDialog iNotifyDialogDefault = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			DefaultImageConfirmation dialogImageConfirmation = new DefaultImageConfirmation();
			dialogImageConfirmation.setCancelable(false);
			dialogImageConfirmation.newInstance("", mActivityTabs,
					"Do you want to make this as you default image?", "",
					iNotifySetDefault);
			dialogImageConfirmation.show(getChildFragmentManager(), "test");
			selectedDefault = false;
		}

		@Override
		public void no() {
		}
	};

	INotifyGalleryDialog iNotifySetDefault = new INotifyGalleryDialog() {

		@Override
		public void yes() {// in case of, to make an image as default image
			sendDefaultImageRequest();
			selectedDefault = false;
		}

		@Override
		public void no() { // in case of not to make an image as default image
			if(mActivityTabs instanceof MainBaseActivity)
			{
				((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
			}
			else if(mActivityTabs instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
			}
		}
	};

	/**
	 * Method to send default image to the user who made us request for contact image 
	 */
	private void sendDefaultImageRequest() {
		gson = new Gson();
		try {
			gson = new Gson();
			MyHttpConnection.postFileWithJsonEntityHeaderSetDeafultImage(
					mActivityTabs, GlobalCommonValues.SET_DEFAULT_IMAGE,
					setDefaultResponseHandler,
					saveState.getPrivateKey(mActivityTabs),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to handle web service call for the request made to send the default image
	AsyncHttpResponseHandler setDefaultResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null)
				{
					Logs.writeLog("Set Default Image Response", "OnSuccess", response.toString());
					getResponseSetDefaultImage(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		/**
		 *  Handle response from the server for the request being made to send the default image to the requestor
		 * @param response
		 */
		private void getResponseSetDefaultImage(String response) {
			try {

				if (!TextUtils.isEmpty(response)
						&& GlobalConfig_Methods.isJsonString(response)) {
					gson = new Gson();
					SetDefaultImageResponse get_Response = gson.fromJson(response,
							SetDefaultImageResponse.class);
					if (get_Response.response_code
							.equals(GlobalCommonValues.SUCCESS_CODE)) {
						BackupRestoreSuccessDialog objImageUploadSuccess = new BackupRestoreSuccessDialog();

						objImageUploadSuccess.setCancelable(false);
						objImageUploadSuccess.newInstance("", mActivityTabs,
								"An image has been sent successfully", "");
						objImageUploadSuccess.show(getChildFragmentManager(), "test");
						selectedDefault = false;
					}
				}

			} catch (Exception e) {
				e.getMessage();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Set Default Image Response", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	INotifyGalleryDialog iNotifyExitToHomeScreen = new INotifyGalleryDialog() {
		@Override
		public void yes() {
		}

		@Override
		public void no() {
			adapterMenu.setRowColor(adapterSelected_position, false);
		}
	};

	/**
	 * Method to send the selected image to the requestor
	 * @param objNotificationBean
	 * @param fileToUpload
	 */
	private void uploadImage(NotificationsBeanImageUpload objNotificationBean,
			File fileToUpload) {
		try {
			gson = new Gson();			
			MyHttpConnection.postFileWithJsonEntityHeaderRequestImage(
					mActivityTabs, GlobalCommonValues.UPLOAD_REQUESTED_IMAGE,
					fileToUpload, "",
					objNotificationBean.getNotifications_id(),
					uploadImageResponseHandler,
					saveState.getPrivateKey(mActivityTabs),
					saveState.getPublicKey(mActivityTabs));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to handle web service call to upload image and send it to the requestor
	AsyncHttpResponseHandler uploadImageResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if(response!=null)
				{
					Logs.writeLog("Choose Image Response", "OnSuccess", response.toString());
					getResponseUploadImage(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Choose Image Response", "OnFailure", response);
			// GlobalCommonValues.imageResponsePathSaved="";
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	public void onDestroy() {
		super.onDestroy();
		// GlobalCommonValues.imageResponsePathSaved="";
	};


/**
 *  Handle response from the server for the request being made to upload/send the image to the requestor
 * @param response
 */
	private void getResponseUploadImage(String response) {
		try 
		{
			String response2 = "";
			if (response.contains("application/octet-stream")
					|| response.contains("type") || response.contains("Array")) {
				response2 = response.substring(
						response.indexOf("response_code") - 2, response.length());
			} else {
				response2 = response;
			}

			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response2)) {
				gson = new Gson();
				ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				UploadImageResponse get_Response = gson.fromJson(response2,
						UploadImageResponse.class);
				if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) 
				{
					try 
					{
						if (!selectedDefault) 
						{
							ImageUploadSuccessDialog objImageUploadSuccess = new ImageUploadSuccessDialog();
							objImageUploadSuccess.setCancelable(false);
							objImageUploadSuccess.newInstance("", mActivityTabs,
									"An image has been sent successfully", "");
							objImageUploadSuccess.show(getChildFragmentManager(),
									"test");
							GlobalCommonValues._bitmap = null;
							GlobalCommonValues.isImageSelected = false;
							GlobalCommonValues.selectedImagepath = "";
							//							GlobalCommonValues.selectedClipArtUrl = "";
						} 
						else if (selectedDefault) 
						{
							iNotifyDialogDefault.yes();
						}
					}
					catch (Exception e) 
					{
						e.getMessage();
					}

				} else if (get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_1)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_2)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_3)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_4)
						|| get_Response.response_code
						.equals(GlobalCommonValues.FAILURE_CODE_5)) {
					dialogErrorMessage.newInstance("",
							((HomeScreenActivity) mActivityTabs),
							get_Response.response_message, "", null,
							iNotifyExitToHomeScreen);
					dialogErrorMessage.show(((HomeScreenActivity) mActivityTabs)
							.getSupportFragmentManager(), "test");
					GlobalCommonValues._bitmap = null;
					GlobalCommonValues.isImageSelected = false;
					GlobalCommonValues.selectedImagepath = "";
				}
			}
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	/**
	 * Method to set the save path of camera clicked image
	 * @return
	 */
	private static Uri getOutputImageUri() {
		if (Environment.getExternalStorageState() == null) {
			return null;
		}

		File mediaStorage = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
				"TNC_IMAGES_RESPONSE");
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
				Uri selectedImage = fileUri;
				picturePath = selectedImage.getPath();
				GlobalCommonValues.selectedImagepath = picturePath;
				cropImage(true);
			}else if(requestCode==PICK_IMAGE_REQUEST) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				@SuppressWarnings("unused")
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				MainBaseActivity.selectedImagepath=picturePath;
				Intent cropIntent=new Intent(getActivity(),CropImage.class);
				cropIntent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
				cropIntent.putExtra(CropImage.SCALE, true);
				cropIntent.putExtra(CropImage.ASPECT_X, 1);
				cropIntent.putExtra(CropImage.ASPECT_Y, 1);
				getActivity().startActivity(cropIntent);

				//MainBaseActivity._bitmap = decodeFile(new File(picturePath));
			}

		}
	}

	/**
	 * navigate to the cropping tool screen and allow us to crop the image
	 * 
	 * @param isCamera
	 */
	private void cropImage(boolean isCamera) {
		if (!GlobalCommonValues.selectedImagepath.equals("")) {
			GlobalCommonValues._bitmap = null;
			Intent intent = new Intent(getActivity(), CropImage.class);
			if (isCamera)
				intent.putExtra(CropImage.IMAGE_PATH, picturePath);
			else
				intent.putExtra(CropImage.IMAGE_PATH,
						GlobalCommonValues.selectedImagepath);
			intent.putExtra(CropImage.SCALE, true);
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		lvImageMenu.setEnabled(false);
		lvImageMenu.setClickable(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		lvImageMenu.setEnabled(true);
		lvImageMenu.setClickable(true);
		adapterMenu.setRowColor(adapterSelected_position,false);
		if (MainBaseActivity.isCameraCanceled) {
			MainBaseActivity._bitmap = null;
			MainBaseActivity.isImageSelected = false;
			MainBaseActivity.isImageRequested = false;
			GlobalCommonValues.selectedImagepath = "";
			GlobalCommonValues._bitmap = null;
			GlobalCommonValues.isImageSelected = false;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			if (fileUri != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			}
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			MainBaseActivity.isCameraCanceled = false;
		}
		lvImageMenu.setAdapter(adapterMenu);
		if (GlobalCommonValues._bitmap != null && GlobalCommonValues.isImageSelected)
		{
			GlobalCommonValues._bitmap = GlobalConfig_Methods.getResizedBitmap(
					GlobalCommonValues._bitmap, 200, 200);
			GlobalConfig_Methods.saveImageToExternalStorage(GlobalCommonValues._bitmap);
			File fileToUpload = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/",
					"photo1.jpg");
			NotificationsBeanImageUpload objNotificationBean = new NotificationsBeanImageUpload(
					notifications_id);
			uploadImage(objNotificationBean, fileToUpload);
			//			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			if (mActivityTabs instanceof MainBaseActivity)
				((MainBaseActivity) mActivityTabs).fragmentManager
				.popBackStack();
			if (mActivityTabs instanceof HomeScreenActivity)
				((HomeScreenActivity) mActivityTabs).fragmentManager
				.popBackStack();
		}
		if (v.getId() == R.id.btnHome) {
			if (mActivityTabs instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivityTabs).startActivity(new Intent(
						mActivityTabs, HomeScreenActivity.class));
				((HomeScreenActivity) mActivityTabs).finish();
			}
		}
	}
}
