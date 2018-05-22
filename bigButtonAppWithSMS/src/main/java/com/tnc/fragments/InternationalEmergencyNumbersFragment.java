package com.tnc.fragments;
/*package com.bigbutton.fragments;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.bigbutton.R;
import com.bigbutton.adapter.AlphabetAdapter;
import com.bigbutton.adapter.InternationalEmergencyAdapter;
import com.bigbutton.base.BaseFragmentTabs;
import com.bigbutton.bean.CountryDetailsBean;
import com.bigbutton.common.CustomFonts;
import com.bigbutton.database.DBQuery;
import com.bigbutton.homescreen.HomeScreenActivity;

public class InternationalEmergencyNumbersFragment extends BaseFragmentTabs implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

	TextView tvTitle,tvInfo,tvCountryHeader,tvEmergencyNumberHeader,tvDialoNowHeader;
	ListView lvCountries,lvAlphabets;
	FrameLayout flBackArrow;
	Button btnBack;
	Context mActivity;
	ArrayList<CountryDetailsBean> listCountries;
	InternationalEmergencyAdapter adapter;
	ArrayList<String> listAlphabets;
	AlphabetAdapter adapterAlphabet;
	SearchView searchViewCountries;

	public InternationalEmergencyNumbersFragment newInstance(Context mActivity)
	{
		InternationalEmergencyNumbersFragment frag = new InternationalEmergencyNumbersFragment();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.international_emergency_number,container, false);
		idInitialization(view);
		return view;
	}

	private void idInitialization(View view)
	{
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvInfo=(TextView) view.findViewById(R.id.tvInfo);
		tvCountryHeader=(TextView) view.findViewById(R.id.tvCountryHeader);
		tvEmergencyNumberHeader=(TextView) view.findViewById(R.id.tvEmergencyNumberHeader);
		tvDialoNowHeader=(TextView)view.findViewById(R.id.tvDialoNowHeader);
		lvCountries=(ListView) view.findViewById(R.id.lvCountries);
		lvAlphabets=(ListView) view.findViewById(R.id.lvAlphabets);
		searchViewCountries=(SearchView) view.findViewById(R.id.searchViewCountries);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvInfo, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvCountryHeader, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvEmergencyNumberHeader,"fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvDialoNowHeader,"fonts/Helvetica-Bold.otf");
		try {
			SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);
			searchViewCountries.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
			searchViewCountries.setQueryHint("Search from Country list...");
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
		listCountries=DBQuery.getAllCountryDetails(mActivity);
		adapter=new InternationalEmergencyAdapter(mActivity, listCountries);
		lvCountries.setAdapter(adapter);
		listAlphabets=new ArrayList<String>();
		for(int i=65;i<=90;i++)
		{
			listAlphabets.add(String.valueOf((char)(i)));
		}
		adapterAlphabet=new AlphabetAdapter(mActivity,listAlphabets);
		adapterAlphabet.notifyDataSetChanged();
		lvAlphabets.setAdapter(adapterAlphabet);

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
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
			}
		});
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
		if(adapter!=null)
		{
			adapter.filterData(newText);
			adapter.notifyDataSetChanged();
			lvCountries.setAdapter(adapter);	
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(adapter!=null)
		{
			adapter.filterData(query);
			adapter.notifyDataSetChanged();
			lvCountries.setAdapter(adapter);	
		}
		return false;
	}
}
*/