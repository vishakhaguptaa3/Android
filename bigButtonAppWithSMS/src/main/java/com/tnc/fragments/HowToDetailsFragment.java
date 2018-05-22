package com.tnc.fragments;

//import com.facebook.android.WebViewClient;
import com.tnc.R;
import com.tnc.activities.InfoActivity;
import com.tnc.common.CustomFonts;
import com.tnc.webresponse.HowtoReponseDataBean;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * class to display howto/faq details 
 *  @author a3logics
 */
public class HowToDetailsFragment extends Fragment{

	private TextView tvTitle,tvQuestion,tvTime;
	private WebView tvAnswer;
	private FrameLayout flBack;
	private Button btnBack;	
	private HowtoReponseDataBean objHowToResponseDataBean;
	private String position = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.howtodetailsfragment,container, false);
		idInitialization(view);
		return view;
	}

	public HowToDetailsFragment newInstance(String position,
			HowtoReponseDataBean objHowToResponseDataBean){
		HowToDetailsFragment frag = new HowToDetailsFragment();
		this.position=position;
		this.objHowToResponseDataBean=objHowToResponseDataBean;
		@SuppressWarnings("unused")
		Bundle args = new Bundle();
		return frag;
	}

	private void idInitialization(View view) {
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvQuestion=(TextView) view.findViewById(R.id.tvQuestion);
		tvAnswer=(WebView) view.findViewById(R.id.tvAnswer);
		tvTime=(TextView) view.findViewById(R.id.tvTime);
		flBack=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);	
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");

		flBack.setVisibility(View.VISIBLE);
		tvQuestion.setTypeface(null,Typeface.BOLD);
		
		tvQuestion.setText(position+Html.fromHtml(objHowToResponseDataBean.getQuestion()).toString());
		//tvAnswer.setText(Html.fromHtml(objHowToResponseDataBean.getAnswer()).toString());
		tvAnswer.getSettings().setJavaScriptEnabled(true);
		tvAnswer.loadDataWithBaseURL("", objHowToResponseDataBean.getAnswer().toString(), "text/html", "UTF-8", "");
		//redirecting web view in same window
		
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		tvAnswer.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            return false;
	        }
	    });
		
		////system.out.println("Answer Key Url " +objHowToResponseDataBean.getAnswer());
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InfoActivity)getActivity()).fragmentManager.popBackStack();
			}
		});
	}

}
