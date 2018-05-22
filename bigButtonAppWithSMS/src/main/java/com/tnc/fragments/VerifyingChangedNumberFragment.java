package com.tnc.fragments;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.NotifyFriendsConfirmationDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.ChangeNumberResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * class to perform verification of number change 
 *  @author a3logics
 */
public class VerifyingChangedNumberFragment extends BaseFragment implements
OnClickListener {

	private Context mAct;
	private 	TransparentProgressDialog progress;
	private Gson gson;
	private Button btnRetry, btnSkip;
	private TextView tvTitle, tvVerify;
	private SharedPreference saveState;
	private boolean isNewRegistration=false;
	private 	int count =1;
	public boolean isFinished=false;

	public VerifyingChangedNumberFragment newInstance(Context mAct) {
		this.mAct = mAct;
		VerifyingChangedNumberFragment frag = new VerifyingChangedNumberFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.verifyingregistrationprogressbar,
				container,false);
		try {
			progress = new TransparentProgressDialog(mAct, R.drawable.customspinner);
		} catch (Exception e) {
			e.getMessage();
		}
		saveState = new SharedPreference();
		idInitialization(view);
		checkInternetConnection();
		return view;
	}


	// Method to initialize views/widgets
	private void idInitialization(View view) {
		btnRetry = (Button) view.findViewById(R.id.btnRetry);
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvVerify = (TextView) view.findViewById(R.id.tvVerify);
		btnRetry.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
		tvVerify.setVisibility(View.VISIBLE);
		btnRetry.setVisibility(View.GONE);
		btnSkip.setVisibility(View.GONE);
		tvVerify.setText("Please wait...");
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}

	/**
	 * check internet availability
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mAct)) {
			changeMyNumber();
		} else { 
			GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
		}
	}

	//method to call web service to check number change
	private void changeMyNumber()
	{
		try {
			MyHttpConnection.getWithoutHeaderWithoutJsonEntity(mAct,
					GlobalCommonValues.CHANGEMYUMBER, getChangeNumberResponseHandler,
					mAct.getString(R.string.private_key),
					saveState.getPublicKey(mAct));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	//async task to check number change
	AsyncHttpResponseHandler getChangeNumberResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			if(progress!=null && !progress.isShowing())
			{
				progress.show();
			}
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("getChangeNumberResponseHandler", "OnSuccess",
							response.toString());
					getResponseChangeMyNumber(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(progress!=null && progress.isShowing())
				progress.dismiss();
			if (response != null)
				Logs.writeLog("getChangeNumberResponseHandler", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			if(progress!=null && isFinished)
				progress.dismiss();
			// Completed the request (either success or failure)
		}
	};

	/**
	 * handle response for the request being made to change the number
	 * @param response
	 */

	private void getResponseChangeMyNumber(String response)
	{
		String response2="";
		if(response.contains("</div>") || response.contains("<h4>") || response.contains("php"))
		{
			response2=response.substring(response.indexOf("response_code")-2,response.length());
		}
		else{
			response2=response;
		}
		if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
			gson = new Gson();
			ChangeNumberResponse get_Response = gson.fromJson(response2,ChangeNumberResponse.class);
			if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) 
			{
				NotifyFriendsConfirmationDialog dialog=new NotifyFriendsConfirmationDialog();
				if(get_Response.getData.is_number_change.equalsIgnoreCase("yes"))
				{
					saveState.setIS_NUMBER_CHANGED(mAct, true);
					saveState.setUPDATE_EMERGENCY(mAct, true);
					saveState.setIS_RecentRegistration(mAct, true);
					dialog.newInstance("",mAct,"Phone number Changed. Do you like to inform your contacts?","",iNotify);
					dialog.setCancelable(false);
					dialog.show(getChildFragmentManager(), "test");
					isFinished=true;
					if(progress!=null && progress.isShowing())
					{
						progress.dismiss();
					}

				}
				else if(get_Response.getData.is_number_change.equalsIgnoreCase("no"))
				{
					if (count<=120) {
						checkInternetConnection();
						count++;
					}else{
						tvVerify.setText("Unable to verify your number change");
						btnRetry.setVisibility(View.VISIBLE);
						btnSkip.setVisibility(View.VISIBLE);
						isFinished=true;
						if(progress!=null && progress.isShowing())
						{
							progress.dismiss();
						}
					}
				}
			}
		}
	}

	//Interface to handle ,whether user want to notify users for the availability of him on TnC/not
	INotifyGalleryDialog iNotify = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			// In case user selected yes to notify friends for the number changed
			SharedPreference saveState = new SharedPreference();
			/*if(saveState.getISPREMIUMUSER(getActivity())){
				//if the user is a premium user
				((HomeScreenActivity)mAct).setFragment(new TncUsers_NotifyFragment());
			}else{
				//if the user is not a premium user
				((HomeScreenActivity)mAct).setFragment(new PremiumFeaturesFragment());
			}*/
			((HomeScreenActivity)mAct).setFragment(new TncUsers_NotifyFragment());
		}

		@Override
		public void no() {
			// In case user selected no for not being to notified to friends for the number changed
			if (mAct instanceof MainBaseActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((MainBaseActivity) mAct).finish();
			} else if (mAct instanceof HomeScreenActivity) {
				startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((HomeScreenActivity) mAct).finish();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRetry:
			tvVerify.setText("Please wait...");
			btnRetry.setVisibility(View.GONE);
			btnSkip.setVisibility(View.GONE);
			if(mAct instanceof MainBaseActivity)
			{
				((MainBaseActivity)mAct).fragmentManager.popBackStack();
			}
			else if(mAct instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mAct).fragmentManager.popBackStack();
			}
			break;

		case R.id.btnSkip:

			if(mAct instanceof HomeScreenActivity)
			{
				Intent myIntent=new Intent(mAct,HomeScreenActivity.class);
				startActivity(myIntent);
				((HomeScreenActivity)mAct).finish();
			}
			break;

		default:
			break;
		}
	}
}
