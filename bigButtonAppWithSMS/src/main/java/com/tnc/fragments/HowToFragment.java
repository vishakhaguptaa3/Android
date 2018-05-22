package com.tnc.fragments;

import java.util.ArrayList;

import com.tnc.R;
import com.tnc.activities.InfoActivity;
import com.tnc.adapter.HowtoAdapter;
import com.tnc.common.CustomFonts;
import com.tnc.preferences.SharedPreference;
import com.tnc.webresponse.HowtoReponseDataBean;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * class to display howto/faq list 
 *  @author a3logics
 */

public class HowToFragment extends Fragment implements OnClickListener,OnItemClickListener{

	private TextView tvTitle,tvhowTo;
	private FrameLayout flBack;
	private Button btnBack;
	private ListView lvhowTo;
	private ArrayList<HowtoReponseDataBean> listHowToResponseDataBean = 
			new ArrayList<HowtoReponseDataBean>();
	private SharedPreference saveState;
	private HowtoAdapter adapterHowTo;
	private int adapterSelected_position;
	private String title;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.howtofragment,container, false);
		idInitialization(view);
		return view;
	}

	public HowToFragment newInstance(String title,ArrayList<HowtoReponseDataBean> listHowToResponseDataBean){
		HowToFragment frag = new HowToFragment();
		this.title=title;
		this.listHowToResponseDataBean=listHowToResponseDataBean;
		@SuppressWarnings("unused")
		Bundle args = new Bundle();
		return frag;
	}

	private void idInitialization(View view) {
		saveState = new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvhowTo=(TextView) view.findViewById(R.id.tvhowTo);
		flBack=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		lvhowTo=(ListView) view.findViewById(R.id.lvhowTo);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvhowTo, "fonts/Roboto-Bold_1.ttf");
		tvhowTo.setText(title);
		flBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		lvhowTo.setOnItemClickListener(this);

		adapterHowTo = new HowtoAdapter(getActivity(), listHowToResponseDataBean);
		lvhowTo.setAdapter(adapterHowTo);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View rowView, int position, long arg3) {
		if(arg0.getId()==R.id.lvhowTo){
			adapterSelected_position = position;
			adapterHowTo.setRowColor(adapterSelected_position, true);
			HowToDetailsFragment objHowToDetailsFragment = 
					new HowToDetailsFragment();
			objHowToDetailsFragment.newInstance(String.valueOf(position+1)+". ",listHowToResponseDataBean.get(position));
			((InfoActivity)getActivity()).setFragment(objHowToDetailsFragment);
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnBack){
			((InfoActivity)getActivity()).fragmentManager.popBackStack();
		}
	}
}
