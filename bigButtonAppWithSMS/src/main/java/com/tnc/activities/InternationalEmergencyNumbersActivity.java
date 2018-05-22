package com.tnc.activities;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.InternationalEmergencyAdapter;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.EmergencyNumberRespnseBean;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InternationalEmergencyNumbersActivity extends FragmentActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

	private TextView tvTitle,tvInfo,tvCountryHeader,tvEmergencyNumberHeader,tvDialoNowHeader;
	private ListView lvCountries,lvAlphabets;
	private FrameLayout flBackArrow;
	private Button btnBack;
	private Context mActivity;
	private ArrayList<CountryDetailsBean> listCountries;
	private InternationalEmergencyAdapter adapter;
	private ArrayList<String> listAlphabets;
	private AlphabetAdapter adapterAlphabet;
	private SearchView searchViewCountries;
	private SharedPreference saveState;
	private TransparentProgressDialog progress;
	private Gson gson;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.international_emergency_number);
		mActivity=InternationalEmergencyNumbersActivity.this;
		idInitialization();
	}

	//method for initialization of views/widgets
	private void idInitialization()
	{
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivity, R.drawable.customspinner);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvInfo=(TextView)findViewById(R.id.tvInfo);
		tvCountryHeader=(TextView)findViewById(R.id.tvCountryHeader);
		tvEmergencyNumberHeader=(TextView)findViewById(R.id.tvEmergencyNumberHeader);
		tvDialoNowHeader=(TextView)findViewById(R.id.tvDialoNowHeader);
		lvCountries=(ListView)findViewById(R.id.lvCountries);
		lvAlphabets=(ListView)findViewById(R.id.lvAlphabets);
		searchViewCountries=(SearchView)findViewById(R.id.searchViewCountries);
		flBackArrow = (FrameLayout)findViewById(R.id.flBackArrow);
		btnBack = (Button)findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(mActivity, tvTitle,
				"fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(mActivity,tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(mActivity,tvInfo, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(mActivity,tvCountryHeader, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(mActivity,tvEmergencyNumberHeader,"fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(mActivity,tvDialoNowHeader,"fonts/Roboto-Bold_1.ttf");
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		try {
			SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);
			searchViewCountries.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchViewCountries.setQueryHint(getResources().getString(R.string.txtSearchfromCountryListHint));
		} catch (Exception e) {
			e.getMessage();
		}
		searchViewCountries.setOnQueryTextListener(this);
		searchViewCountries.setOnCloseListener(this);
		int searchPlateId = searchViewCountries.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlateView = searchViewCountries.findViewById(searchPlateId);
		if (searchPlateView != null) {
			searchPlateView.setBackgroundColor(Color.WHITE);
		}
		try{
			int id = searchViewCountries.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchViewCountries.findViewById(id);
			textView.setTextColor(Color.BLACK);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		listCountries=new ArrayList<CountryDetailsBean>();

		//check if emergency numbers version is updated then call the web service to fetch the updated list

		/**
		 * Devanshu Nath Tripathi
		 * As per client UAT-442 i comment this code
		 * now every user can get Update Dynamic Content 
		 */

		if(saveState.getIS_EMERGENCY_NUMBER_VERSION_UPDATED(mActivity) || 
				(DBQuery.getAllCountryEmergencyNumbers(mActivity).size()==0)){
			checkInternetConnection();
		}else{//fetch all emergency numbers from the database
			displayEmergencyNumberList();
		}


		// get updated EMERGENCY_NUMBER
		//		checkInternetConnection();

		listAlphabets=new ArrayList<String>();
		for(int i=65;i<=90;i++){
			listAlphabets.add(String.valueOf((char)(i)));
		}
		adapterAlphabet=new AlphabetAdapter(mActivity,listAlphabets);
		adapterAlphabet.notifyDataSetChanged();
		lvAlphabets.setAdapter(adapterAlphabet);
		searchViewCountries.setFocusable(false);
		lvAlphabets.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3)
			{
				for(int i=0;i<listCountries.size();i++)
				{
					if(listCountries.get(i).getCountryName().toLowerCase().startsWith(listAlphabets.get(position).toLowerCase()))
					{
						lvCountries.setSelection(i);
						break;
					}
				}
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// method to display the emergency numbers list
	private void displayEmergencyNumberList(){
		listCountries=DBQuery.getAllCountryEmergencyNumbers(mActivity);
		adapter=new InternationalEmergencyAdapter(mActivity, listCountries);
		lvCountries.setAdapter(adapter);
	}

	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			getEmergencyNumbersRequest();
		}
		else{			
			displayEmergencyNumberList();
		}		
	}

	// method to call web service to get update emergency numbers from the web service
	private void getEmergencyNumbersRequest()
	{
		MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_EMERGENCY_NUMBERS,
				mActivity.getResources().getString(R.string.private_key),emergencyNumbersResponsehandler);
	}

	// Async task to call web service to get Emergency Numbers
	AsyncHttpResponseHandler emergencyNumbersResponsehandler = new JsonHttpResponseHandler() {
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
					Logs.writeLog("Emergency Numbers", "OnSuccess",response.toString());
					getResponse(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if(response!=null)
				Logs.writeLog("Emergency Numbers", "OnFailure",response);
			displayEmergencyNumberList();
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};

	/*
	 * Handling Response from the Server for the request being sent to get Emergency Numbers
	 */
	private void getResponse(String response) {
		try {
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				listCountries=new ArrayList<CountryDetailsBean>();
				try {
					gson = new Gson();
					EmergencyNumberRespnseBean get_Response = gson.fromJson(response,EmergencyNumberRespnseBean.class);
					if(get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
						DBQuery.deleteTable("EmergencyNumbers", "", null, getApplicationContext());
						if(get_Response!=null && get_Response.getData()!=null && get_Response.getData().size()>0)
						{		
							CountryDetailsBean mObjCountryDetailBean;
							for(int i=0;i<get_Response.getData().size();i++){
								mObjCountryDetailBean= new CountryDetailsBean();
								mObjCountryDetailBean.setCountryName(get_Response.getData().get(i).getCountry());
								mObjCountryDetailBean.setEmergency(get_Response.getData().get(i).getEmergency());
								listCountries.add(mObjCountryDetailBean);
							}
							saveState.setIS_EMERGENCY_NUMBER_VERSION_UPDATED(mActivity, false);
							// Insert Emergency Number in the Database
							DBQuery.insertAllCountryEmergencyNumbers(mActivity,listCountries);
							displayEmergencyNumberList();
						}
					}
					else if(get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) || 
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) || 
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) || 
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) || 
							get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)){
						displayEmergencyNumberList();

					}
				}
				catch (Exception e){
					e.getMessage();
				}

			} else {
				displayEmergencyNumberList();
				//ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onPause() {
		InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchViewCountries.getWindowToken(), 0);
		super.onPause();
	}

	@Override
	public boolean onClose() {
		adapter.filterData("");
		searchViewCountries.setQuery("",false);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if(adapter!=null){
			adapter.filterData(newText);
			adapter.notifyDataSetChanged();
			lvCountries.setAdapter(adapter);	
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(adapter!=null){
			adapter.filterData(query);
			adapter.notifyDataSetChanged();
			lvCountries.setAdapter(adapter);	
		}
		return false;
	}
}
