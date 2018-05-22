package com.tnc.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.ClipArtsAdapter;
import com.tnc.adapter.ClipArtsAdapterImageResponse;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ClipArtBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to display clipart images during responding to the image request
 *  @author a3logics
 */
public class ClipArtsFragmentImageResponse extends BaseFragment
{
	private TextView tvTitle,tvContent,tvGallery;
	private GridView gvClipArts;
	private Button btnBack;
	private FrameLayout flBackArrow;
	private ClipArtsAdapterImageResponse adapterClipArts;
	private TransparentProgressDialog progress;
	private Gson gson=null;
	private Display display;
	private int height=0,width=0;
	private int view_item_width=0;
	private ArrayList<String> listClipArtsString=null;
	private ArrayList<ClipArtBean> listClipArts =
			new ArrayList<ClipArtBean>();
	private String strResponseCode="",strResponseMessage="";
	private String[] imagesArray = {"1455001002_Grandpa1.jpg","1455001002_Grandpa2.jpg","1455001002_Grandpa3.jpg","1455001002_Man1.jpg","1455001002_Man2.jpg","1455001002_Man3.jpg","1455001002_Man5.jpg","1455001002_Man6.jpg","1455001002_Man7.jpg","1455001002_Man8.jpg","1455001002_Man9.jpg","1455001002_Man10.jpg","1455001048_Man11.jpg","1455001048_Man12.jpg","1455001048_Man13.jpg","1455001048_Man14.jpg","1455001048_Man15.jpg","1455001048_Man16.jpg","1455001048_Man17.jpg","1455001048_Man18.jpg","1455001048_Man19.jpg","1455001048_Man20.jpg","1455001048_Man21.jpg","1455001048_Man22.jpg","1455001069_Man23.jpg","1455001069_Man24.jpg","1455001069_Man25.jpg","1455001069_Man26.jpg","1455001069_Man27.jpg","1455001069_Man28.jpg","1455001069_Man29.jpg","1455001069_Man30.jpg","1455001165_Doctor7.jpg","1455001165_Grandma1.jpg","1455001165_Grandma2.jpg","1455001165_Grandma3.jpg","1455001165_Grandma4.jpg","1455001165_Grandma5.jpg","1455001165_Woman1.jpg","1455001165_Woman2.jpg","1455001165_Woman3.jpg","1455001165_Woman4.jpg","1455001165_Woman5.jpg","1455001189_Women6.jpg","1455001189_Women7.jpg","1455001189_Women8.jpg","1455001189_Women9.jpg","1455001189_Women11.jpg","1455001189_Women12.jpg","1455001189_Women13.jpg","1455001189_Women14.jpg","1455001226_Boy1.jpg","1455001226_Boy2.jpg","1455001226_Boy3.jpg","1455001226_Boy4.jpg","1455001226_Boy5.jpg","1455001226_Boy6.jpg","1455001226_Boy7.jpg","1455001338_Dentist1.jpg","1455001338_Dentist2.jpg","1455001338_Doctor1.jpg","1455001338_Doctor2.jpg","1455001338_Doctor3.jpg","1455001338_Doctor4.jpg","1455001338_Doctor6.jpg","1455001338_Professional1.jpg","1455001338_Professional2.jpg","1455001338_Professional3.jpg","1455001338_Professional4.jpg","1455001477_ConferenceCaller.jpg","1455001477_ConferenceCalling.jpg","1455002336_Girl1.jpg","1455002336_Girl2.jpg","1455002336_Girl3.jpg","1455002336_Girl4.jpg","1455002336_Girl5.jpg","1455002336_Girl6.jpg","1455002336_Girl7.jpg","1455002336_IndianGirl1.jpg","1455002367_BarberShop1.jpg","1455002367_Hospital.jpg","1455002367_House1.jpg","1455002367_House2.jpg","1455002367_House3.jpg","1455002367_House4.jpg","1455002367_House5.jpg","1455002367_House6.jpg","1455002367_Library1.jpg","1455002385_Office1.jpg","1455002385_Office2.jpg","1455002385_Office3.jpg","1455002385_Office4.jpg","1455002385_Office5.jpg","1455002385_OfficeBuilding1.jpg","1455002385_OfficeBuilding2.jpg","1455002385_School.jpg","1455002385_Supermarket1.jpg","1456263870_Man31.jpg","1460129904_Ganesha.jpg"};

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.clipartsfragment, container, false);
		idInitialization(view);
		return view;
	} 

	private void idInitialization(View view)
	{
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvContent=(TextView) view.findViewById(R.id.tvContent);
		tvGallery=(TextView) view.findViewById(R.id.tvGallery);
		gvClipArts=(GridView) view.findViewById(R.id.gvGallery);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/StencilStd.otf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvContent, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvGallery, "fonts/Roboto-Bold_1.ttf");
		tvContent.setText("Choose Button Image from Clip Art");
		tvGallery.setText("CLIP ART");
		progress=new TransparentProgressDialog(mActivity,R.drawable.customspinner);
		gson=new Gson();
		setDimensions();
		//		checkInternetConnection();

		//fetch Clipart Images
		listClipArts = new ArrayList<ClipArtBean>();
		listClipArts = DBQuery.getAllClipArts(getActivity());

		if(saveState == null){
			saveState = new SharedPreference();
		}
		
		if(saveState.getIS_INITIAL_CLIPARTS(getActivity()) || listClipArts==null ||
				(listClipArts!=null && listClipArts.isEmpty())){
			listClipArts = new ArrayList<ClipArtBean>();
			ClipArtBean mObjClipArtBean = null;
			for(int imagesLoop = 0;imagesLoop<imagesArray.length;imagesLoop++){
				mObjClipArtBean = new ClipArtBean();
				mObjClipArtBean.setClipArtName(imagesArray[imagesLoop]);
				listClipArts.add(mObjClipArtBean);
			}
		}

		//Set Cliparts Display Adapter for the Gridview
		adapterClipArts=new ClipArtsAdapterImageResponse(mActivity, listClipArts, view_item_width);
		gvClipArts.setAdapter(adapterClipArts);


		//		adapterClipArts=new ClipArtsAdapterImageResponse(mActivity,imageId,view_item_width);
		//		gvClipArts.setAdapter(adapterClipArts);
		MainBaseActivity.selectedImagepath="";
		MainBaseActivity.isImageSelected=false;
		MainBaseActivity._bitmap=null;
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//				MainBaseActivity.isImageSelected=false;
				if(mActivity instanceof MainBaseActivity)
					((MainBaseActivity)mActivity).fragmentManager.popBackStack();
				else if(mActivity instanceof HomeScreenActivity)
					((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
			}
		});
	}

	/**
	 * check availabitiy of internet connection
	 */
	public void checkInternetConnection() {
		if (NetworkConnection.isNetworkAvailable(mActivity)) {
			clipArtsRequest();
		} else
		{
			GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
		}
	}

	//Method to call web service to get clipart images
	private void clipArtsRequest()
	{
		MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_CLIPARTS,
				mActivity.getResources().getString(R.string.private_key),clipArtsResponsehandler);
	}

	//async task to handle request made to get the clipart images
	AsyncHttpResponseHandler clipArtsResponsehandler = new JsonHttpResponseHandler() {
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
					Logs.writeLog("ClipArtsImage", "OnSuccess",response.toString());
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
				Logs.writeLog("ClipArtsImage", "OnFailure",response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			if (progress.isShowing())
				progress.dismiss();
		}
	};


	// Handling Response from the Server for the request being sent to get clip art images

	private void getResponse(String response) {
		try 
		{
			if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
				listClipArtsString=new ArrayList<String>();
				try 
				{
					JSONObject jobj=new JSONObject(response);
					strResponseCode=String.valueOf(jobj.get("response_code"));
					strResponseMessage=String.valueOf(jobj.get("response_message"));
					if(strResponseCode.equals(GlobalCommonValues.SUCCESS_CODE))
					{
						JSONArray arrCat = jobj.getJSONArray("data");
						for (int i = 0; i < arrCat.length(); i++) {
							// String animalVideo="";
							listClipArtsString.add(String.valueOf(arrCat.get(i)));
						}
						//						adapterClipArts=new ClipArtsAdapterImageResponse(mActivity, listClipArtsString, view_item_width);
						//						gvClipArts.setAdapter(adapterClipArts);
					}
					else if(strResponseCode.equals(GlobalCommonValues.FAILURE_CODE_1))
					{
						ShowDialog.alert(mActivity, "", strResponseMessage);
					}
				}
				catch (Exception e) 
				{
					e.getMessage();
				}

			} else {
				//	ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/*
	 * calculation of screen width to adjust gridview column width
	 * */
	@SuppressWarnings("deprecation")
	private void setDimensions()
	{
		display=mActivity.getWindow().getWindowManager().getDefaultDisplay(); 
		width = display.getWidth();
		height = display.getHeight();
		int display_width = width- 24;
		if (display_width > 0) 
		{
			int item_width = display_width / 3;
			if (item_width > 0)
			{
				view_item_width = item_width;
			}
		} 
		else 
		{
			view_item_width = (int) mActivity.getResources().getDimension(
					R.dimen.photo_side);
		}
		gvClipArts.setColumnWidth(view_item_width);
	}
}

