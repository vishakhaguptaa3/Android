package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.TileContactsAdapterChangeButton;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
 * class to display the list Tiles on the home screen 
 *  @author a3logics
 */
public class TileContactListFragment extends BaseFragmentTabs{

	/*
	 *In case of change button screen
	 * */

	private TextView tvTitle,tvStep,tvFavoritesList;
	private Button btnBack,btnHome;
	private FrameLayout flBackArrow,flInformation;
	private ListView lvButtonContacts,lvAlphabets;
	private int adapterSelected_position;
	private TileContactsAdapterChangeButton adapterTileContacts;
	private ArrayList<ContactTilesBean> listTiles;
	private SharedPreference saveState;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.tilecontactfragment, container, false);
		idInitialization(view);
		return view;
	}

	//method for initialization of views/widgets
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvFavoritesList=(TextView) view.findViewById(R.id.tvFavoritesList);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		flInformation=(FrameLayout) view.findViewById(R.id.flInformationButton);
		flInformation.setVisibility(View.VISIBLE);
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		btnHome.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		lvButtonContacts=(ListView) view.findViewById(R.id.lvButtonContacts);
		lvAlphabets=(ListView) view.findViewById(R.id.lvAlphabets);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvFavoritesList, "fonts/Roboto-Bold_1.ttf");
		tvStep.setText("SELECT CHAT BUTTON");
		tvStep.setTextColor(Color.parseColor("#1a649f"));
		tvFavoritesList.setVisibility(View.GONE);
		lvAlphabets.setVisibility(View.GONE);
		listTiles=new ArrayList<ContactTilesBean>();
		listTiles=DBQuery.getAllTiles((HomeScreenActivity)mActivityTabs);
		adapterTileContacts=new TileContactsAdapterChangeButton(mActivityTabs,listTiles,false,null,false);
		
		//For Emergency Button
		ContactTilesBean objContactTile =new ContactTilesBean();
		objContactTile.setName("Emergency");
		objContactTile.setPhoneNumber(saveState.getEmergency(mActivityTabs));

		Bitmap _bitmap=((BitmapDrawable)(mActivityTabs.getResources().getDrawable(R.drawable.add_btn))).getBitmap();
		ByteArrayOutputStream blob = new ByteArrayOutputStream();
		_bitmap.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, blob);
		byte[] arrayImage = blob.toByteArray();

		objContactTile.setImage(arrayImage);
		adapterTileContacts.listTiles.add(objContactTile);
		adapterTileContacts.notifyDataSetChanged();

		lvButtonContacts.setAdapter(adapterTileContacts);
		lvButtonContacts.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View childViewList, int position,
					long arg3) {
				MainBaseActivity.selectedPrefixCodeForTileDetails="";
				MainBaseActivity.selectedCountryCodeForTileDetails="";
				MainBaseActivity.isIsdCodeFlagChecked=false;
				MainBaseActivity.contactNameForTile="";
				MainBaseActivity.contactNumberForTile="";
				adapterSelected_position=position;
				adapterTileContacts.setRowColor(adapterSelected_position,true);
				if(listTiles.get(position).getName().equalsIgnoreCase("Emergency"))
				{
					EmergencyNumberConfigureFragment objEmergencyNumberFragment=
							new EmergencyNumberConfigureFragment();
					objEmergencyNumberFragment.newInstance(mActivityTabs);
					((HomeScreenActivity) mActivityTabs).setFragment(objEmergencyNumberFragment);
				}
				else{
					MainBaseActivity.objTileEdit=new ContactTilesBean();
					TileContactDetailsFragment objTileContactDetailsFragment;

					MainBaseActivity.objTileEdit=listTiles.get(position);

					if(mActivityTabs instanceof MainBaseActivity)
					{
						objTileContactDetailsFragment=new TileContactDetailsFragment();
						objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivityTabs),false,null,MainBaseActivity.objTileEdit.getPrefix()+MainBaseActivity.objTileEdit.getCountryCode()+MainBaseActivity.objTileEdit.getPhoneNumber(),MainBaseActivity.objTileEdit.getName().trim(),
								MainBaseActivity.objTileEdit.getPrefix()+MainBaseActivity.objTileEdit.getCountryCode()+MainBaseActivity.objTileEdit.getPhoneNumber(),null,true);
						((MainBaseActivity) mActivityTabs).setFragment(objTileContactDetailsFragment);
					}
					else if(mActivityTabs instanceof HomeScreenActivity)
					{
						objTileContactDetailsFragment=new TileContactDetailsFragment();
						objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivityTabs),false,null,MainBaseActivity.objTileEdit.getPrefix()+MainBaseActivity.objTileEdit.getCountryCode()+MainBaseActivity.objTileEdit.getPhoneNumber(),MainBaseActivity.objTileEdit.getName().trim(),
								MainBaseActivity.objTileEdit.getPrefix()+MainBaseActivity.objTileEdit.getCountryCode()+MainBaseActivity.objTileEdit.getPhoneNumber(),null,true);
						((HomeScreenActivity) mActivityTabs).setFragment(objTileContactDetailsFragment);
					}
				}
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();				
			}
		});
		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((HomeScreenActivity)getActivity()).startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				((HomeScreenActivity)getActivity()).finish();
			}
		});
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
	}
}
