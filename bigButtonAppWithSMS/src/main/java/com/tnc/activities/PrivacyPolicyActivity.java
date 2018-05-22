package com.tnc.activities;

import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.utility.Logs;
import com.tnc.webresponse.PrivacyTermsReponseBean;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PrivacyPolicyActivity extends FragmentActivity {//implements OnPageChangeListener{

	private TextView tvTitle,tvPrivacyPolicy;//,tvContent;
	private Context mActivity;
	private Button btnBack;
	private FrameLayout flBackArrow;
	private String content = "";
	private String title = "";
	private InputStream input;
	private TransparentProgressDialog progress;
	private WebView wvDisplayData;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.privacy_policy);
		mActivity=PrivacyPolicyActivity.this;
		idInitialization();
	}

	//method for initialization of views/widgets
	private void idInitialization(){
		progress = new TransparentProgressDialog(mActivity, R.drawable.customspinner);
		flBackArrow=(FrameLayout)findViewById(R.id.flBackArrow);
		tvTitle=(TextView) findViewById(R.id.tvTitle);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack=(Button)findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
		tvPrivacyPolicy=(TextView) findViewById(R.id.tvPrivacyPolicy);
		//tvContent=(TextView) findViewById(R.id.tvContent);
		wvDisplayData=(WebView) findViewById(R.id.wvDisplayData);
		//		pdfView=(PDFView) findViewById(R.id.pdfView);

		CustomFonts.setFontOfTextView(mActivity, tvTitle,
				"fonts/comic_sans_ms_regular.ttf");
		//CustomFonts.setFontOfTextView(mActivity,tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(mActivity,tvPrivacyPolicy, "fonts/Roboto-Bold_1.ttf");

		if(getIntent()!=null && getIntent().getExtras()!=null && getIntent().getExtras().getString("title")!=null){
			title = getIntent().getExtras().getString("title");
		}else{
			title = getResources().getString(R.string.txtPrivacyPolicyCamelCase);
		}

		tvPrivacyPolicy.setText(title);
		tvPrivacyPolicy.setAllCaps(true);

		wvDisplayData.getSettings().setJavaScriptEnabled(true);
		wvDisplayData.getSettings().setPluginState(PluginState.ON);

		wvDisplayData.setWebViewClient(new Callback());

		try {
			checkInternetConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//tvContent.setText(content);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * check availability of internet connection for calling how To Web service
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			getPrivacyPolicyRequest();
		}
		else{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
		}
	}

	//method to create request to get & display terms of use & privacy policy
	private void getPrivacyPolicyRequest()
	{
		if(title.toLowerCase().contains("privacy")){
			MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_PRIVACYPOLICY,
					mActivity.getResources().getString(R.string.private_key),privacyTermsResponsehandler);
		}else if(title.toLowerCase().contains("terms")){
			MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_TERMSOFUSAGE,
					mActivity.getResources().getString(R.string.private_key),privacyTermsResponsehandler);
		}
	}

	// async task to to request for displaying privacy policy/ terms of use
	AsyncHttpResponseHandler privacyTermsResponsehandler = new JsonHttpResponseHandler() {
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
				if(response!=null){
					Logs.writeLog("PrivacyTermsWebService", "OnSuccess",response.toString());
					getResponsePrivacyTerms(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("PrivacyTermsWebService", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/* Handling Response from the Server for the request being sent to get clip art images */

	private void getResponsePrivacyTerms(String response) {
		Gson gson = new Gson();
		String pdfURL = "";
		try {
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)){
				PrivacyTermsReponseBean get_Response = gson.fromJson(response, PrivacyTermsReponseBean.class);
				if (get_Response.response_code.equals(
						GlobalCommonValues.SUCCESS_CODE)){
					pdfURL = get_Response.getData.get(0).name;
					wvDisplayData.loadUrl("http://docs.google.com/gview?embedded=true&url="+pdfURL);

				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private class Callback extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(
				WebView view, String url) {
			return(false);
		}
	}
}
