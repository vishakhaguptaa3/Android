package com.tnc.activities;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.bean.ContactUsRequestBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ContactUsSuccessfulDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ContactUsResponseBean;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

public class ContacUsActivity extends FragmentActivity{

	private TextView tvTitle,tvContactUs,tvVersion,tvAttachment;//tvMessage
	private Context mActivity;
	private Button btnAddAttachment,btnBack,btnSendMessage,btnDisableOverlay,btnCancel;
	private FrameLayout flBackArrow;
	private EditText etMessage;
	private int requestCodeGallery=200;
	private String imageBase64="";
	private Gson gson;
	private TransparentProgressDialog progress;
	private SharedPreference saveState;
	private Uri selectedImage =null;
	private ScrollView svContactUsForm;
	private WebView wvContactUsForm;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_us_fragment);
		mActivity=ContacUsActivity.this;
		idInitialization();
	}

	/**
	 * Method to initialize views/widgets
	 */
	private void idInitialization(){
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivity, R.drawable.customspinner);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvContactUs=(TextView)findViewById(R.id.tvContactUs);

		svContactUsForm = (ScrollView) findViewById(R.id.svContactUsForm);
		wvContactUsForm	= (WebView) findViewById(R.id.wvContactUsForm);

		tvVersion=(TextView)findViewById(R.id.tvVersion);
		//		tvMessage=(TextView)findViewById(R.id.tvMessage);
		tvAttachment=(TextView)findViewById(R.id.tvAttachment);
		btnAddAttachment=(Button)findViewById(R.id.btnAddAttachment);
		flBackArrow=(FrameLayout)findViewById(R.id.flBackArrow);
		etMessage=(EditText)findViewById(R.id.etMessage);
		btnBack=(Button)findViewById(R.id.btnBack);
		btnSendMessage=(Button)findViewById(R.id.btnSendMessage);
		btnDisableOverlay=(Button)findViewById(R.id.btnDisableOverlay);
		btnCancel=(Button)findViewById(R.id.btnCancel);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(mActivity, tvTitle,
				"fonts/comic_sans_ms_regular.ttf");
		//CustomFonts.setFontOfTextView(mActivity,tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(mActivity,tvContactUs, "fonts/Roboto-Bold_1.ttf");
		btnSendMessage.setEnabled(true);
		btnDisableOverlay.setVisibility(View.GONE);
		String version="";

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		//check if the app is registered
		if(saveState.isRegistered(mActivity)){
			// if the app is registered
			svContactUsForm.setVisibility(View.VISIBLE);
			wvContactUsForm.setVisibility(View.GONE);
			try {
				version=mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(),0).versionName;
			} catch (Exception e) {
				e.getMessage();
			}
			tvVersion.setText("v"+version);

			btnAddAttachment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intentImages=new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					/*intentImages.setAction(android.content.Intent.ACTION_GET_CONTENT);  
					intentImages.setType("image/*");
					intentImages.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
					startActivityForResult(intentImages,requestCodeGallery);

				}
			});
			tvAttachment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intentImages=new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					/*intentImages.setAction(android.content.Intent.ACTION_GET_CONTENT);  
					intentImages.setType("image/*");
					intentImages.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
					startActivityForResult(intentImages,requestCodeGallery);

				}
			});

			etMessage.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					//enable/disable send button if data is contacined in message textbox
					if(s.length()==0)
					{
						btnSendMessage.setEnabled(false);
						btnDisableOverlay.setVisibility(View.VISIBLE);
					}
					else if(s.length()>0)
					{
						btnSendMessage.setEnabled(true);
						btnDisableOverlay.setVisibility(View.GONE);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}else{
			// if the app is not registered then redirect to the webpage
			svContactUsForm.setVisibility(View.GONE);
			wvContactUsForm.setVisibility(View.VISIBLE);
			wvContactUsForm.getSettings().setJavaScriptEnabled(true);
			wvContactUsForm.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			wvContactUsForm.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				public void onPageFinished(WebView view, String url) {
					if (progress!=null && progress.isShowing()) {
						progress.dismiss();
					}
				}

				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					if (progress!=null && progress.isShowing()) {
						progress.dismiss();
					}
				}
			});
			if(progress!=null && !progress.isShowing()){
				progress.show();
				progress.setCancelable(false);	
			}
			wvContactUsForm.loadUrl("http://chatstasy.com/contactus/");
		}

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSendMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//check if the message textbox is not empty 

				if(etMessage.getText().toString().trim().equals(""))
				{
					ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
					dialogErrorMessage.setCancelable(false);
					dialogErrorMessage.newInstance("",((ContacUsActivity) mActivity),
							getResources().getString(R.string.txtPleaseEnterMessage), "", null);
					dialogErrorMessage.show(((ContacUsActivity) mActivity).getSupportFragmentManager(), "test");

				}
				else if(!etMessage.getText().toString().trim().equals(""))
				{
					//call web service to post query
					checkInternetConnection();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * check internet availability
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			if(etMessage.getText().toString().trim().equals(""))
			{
				ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				dialogErrorMessage.newInstance("",((ContacUsActivity) mActivity),
						getResources().getString(R.string.txtPleaseEnterMessage), "", null);
				dialogErrorMessage.show(((ContacUsActivity) mActivity).getSupportFragmentManager(), "test");

			}
			else if(!etMessage.getText().toString().trim().equals(""))
			{
				String message =etMessage.getText().toString();
				String version = tvVersion.getText().toString();
				ContactUsRequestBean objContactUsRequestBean = new ContactUsRequestBean(message,
						version,saveState.getUserMailID(ContacUsActivity.this), imageBase64);
				contactUsRequest(objContactUsRequestBean);	
			}
		} else {
			GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
		}
	}

	
	//sending data to the server
	private void contactUsRequest(ContactUsRequestBean objContactUsRequestBean)
	{
		try {
			gson = new Gson();
			String stingGson = gson.toJson(objContactUsRequestBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivity,
					GlobalCommonValues.CONTACTUS, stringEntity,
					contactUsResponseHandler,
					mActivity.getString(R.string.private_key),saveState.getPublicKey(mActivity));
		} catch (Exception e) {
			e.getMessage();
		}		
	}

	//Async task to handle request made to the server
	AsyncHttpResponseHandler contactUsResponseHandler = new JsonHttpResponseHandler() {
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
				if (response != null) {
					Logs.writeLog("Contact Us", "OnSuccess", response.toString());
					getResponseContactUs(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("Contact Us", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	public void onStop() {
		super.onStop();
	}

	/**
	 * handle response for the request being made for the contact us
	 * 
	 * @param response
	 */
	private void getResponseContactUs(String response)
	{
		try {
			if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response))
			{
				gson = new Gson();
				ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
				dialogErrorMessage.setCancelable(false);
				ContactUsResponseBean get_Response = gson.fromJson(response,ContactUsResponseBean.class);
				if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) 
				{
					String message = getResources().getString(R.string.txtContactUsRequestSubmitMessage);
					ContactUsSuccessfulDialog dialog = new ContactUsSuccessfulDialog();
					dialog.setCancelable(false);
					dialog.newInstance("",mActivity,message,"",iAction);
					dialog.show(getSupportFragmentManager(), "test");

				} else if (get_Response.getResponse_code().equals(
						GlobalCommonValues.FAILURE_CODE)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_1)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_5)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_2)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_3)
						|| get_Response.getResponse_code().equals(
								GlobalCommonValues.FAILURE_CODE_4)) {
					dialogErrorMessage.newInstance("",
							((ContacUsActivity) mActivity),
							get_Response.getResponse_message(), "", null);
					dialogErrorMessage.show(
							((ContacUsActivity) mActivity)
							.getSupportFragmentManager(), "test");
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//interface to handle finish of screen if successfully sent data to the server
	INotifyAction iAction = new INotifyAction() {
		@Override
		public void setAction(String action) {
			if(action.equals("finish"))
				finish();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == requestCodeGallery && resultCode == RESULT_OK  && null != data) {
				// Get the Image from data
				selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String imgDecodableString = cursor.getString(columnIndex);
				cursor.close();
				Bitmap _bitmap=BitmapFactory.decodeFile(imgDecodableString);
				imageBase64=GlobalConfig_Methods.encodeTobase64(_bitmap);
				tvAttachment.setText(getResources().getString(R.string.txtAttached));			
			} 
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
