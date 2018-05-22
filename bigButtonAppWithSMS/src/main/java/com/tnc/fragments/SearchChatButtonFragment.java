package com.tnc.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.AlphabetAdapter;
import com.tnc.adapter.SearchButtonAdapter;
import com.tnc.adapter.SearchButtonAdapter.ContactsAdapterListener;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import com.tnc.widgets.MyDividerItemDecoration;
import java.util.ArrayList;

/**
 * Created by a3logics on 16/3/18.
 */

public class SearchChatButtonFragment extends BaseFragmentTabs implements
        View.OnClickListener, AdapterView.OnItemClickListener, ContactsAdapterListener{

    private TextView                    tvTitle,tvHeading,textViewSearcBar;
    private Button                      btnBack,btnHome;
    private FrameLayout                 flBackArrow,flInformationButton;
    private SearchView                  searchViewContacts;
    private RecyclerView                mRecyclerViewContacts;
    private ListView                    lvAlphabets;
    private LinearLayout                llSearchView;
    public TransparentProgressDialog    dialogProgress                   = null;
    private int                         adapterSelected_position;
    private ArrayList<String>           listEmptyMessage;
    private boolean                     isSearchResultFirstTime          = false;
    private ArrayList<String>           listAlphabets                    = null;
    private AlphabetAdapter             adapterAlphabet;
    private int                         mFirstVisiblePositionList        = -1;
    private int                         mLastVisiblePositionList         = -1;
    private String                      mSearchtext                      = "";
    private ArrayList<ContactTilesBean> mListTiles                       = new ArrayList<>();
    private SearchButtonAdapter         mAdapterSearchButton             = null;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.search_chat_button, container, false);
        idInitialization(view);
        return view;
    }

    /*
	 * Initialization of widgets/views
	 */
    private void idInitialization(View view) {
        saveState            = new SharedPreference();
        dialogProgress       = new TransparentProgressDialog(getActivity(),R.drawable.customspinner);

        ///////// Text Views
        tvTitle                = view.findViewById(R.id.tvTitle);
        tvHeading              = view.findViewById(R.id.tvHeading);

        flBackArrow            = view.findViewById(R.id.flBackArrow);
        llSearchView           = view.findViewById(R.id.llSearchView);

        // Search View
        searchViewContacts     = view.findViewById(R.id.searchViewContacts);
        // listViews/recyclerviews
        mRecyclerViewContacts  = view.findViewById(R.id.recycler_view_contacts);
        lvAlphabets            = view.findViewById(R.id.lvAlphabets);
        // Buttons
        btnBack                = view.findViewById(R.id.btnBack);
        flInformationButton    = view.findViewById(R.id.flInformationButton);
        btnHome                = view.findViewById(R.id.btnHome);

        // Configure/Set/Display refresh button
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
        btnHome.setBackgroundResource(R.drawable.refresh);
        btnHome.setEnabled(true);
        btnHome.setClickable(true);
        flBackArrow.setVisibility(View.VISIBLE);

        try {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchViewContacts.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchViewContacts.setQueryHint(getResources().getString(R.string.txtHintContactName));

            searchViewContacts.setMaxWidth(Integer.MAX_VALUE);

            // listening to search query text change
            searchViewContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    mAdapterSearchButton.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // filter recycler view when text is changed
                    mAdapterSearchButton.getFilter().filter(query);
                    return false;
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }

        // load tiles data
        loadTilesData();

        // Set Click Listeners or trextchange listeners of searchview
        /*searchViewContacts.setOnQueryTextListener(this);
        searchViewContacts.setOnCloseListener(this);*/
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        setAlphabetsAdapter();

        // Configure searchview
        int searchPlateId = searchViewContacts.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchViewContacts.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.WHITE);
        }
        try{
            int id = searchViewContacts.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            textViewSearcBar = (TextView) searchViewContacts.findViewById(id);
            textViewSearcBar.setTextColor(Color.BLACK);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        lvAlphabets.setOnItemClickListener(this);
    }

    /**
     * Method to set alphabet adapter
     */
    private void setAlphabetsAdapter(){
        listAlphabets=new ArrayList<String>();
        for(int i=65;i<=90;i++)
        {
            listAlphabets.add(String.valueOf((char)(i)));
        }
        adapterAlphabet=new AlphabetAdapter(getActivity(),listAlphabets);
        adapterAlphabet.notifyDataSetChanged();
        lvAlphabets.setAdapter(adapterAlphabet);
    }

    /**
     * Method to load tiles data
     */
    public void loadTilesData(){
        new LoadTiles().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int mSelectedPosition, long l) {
        switch (adapterView.getId()){
            case R.id.lvAlphabets:
                //Method to set list selection as per alphabet selection
                setAlphabetSelection(mSelectedPosition);
                break;

            default:
                break;
        }
    }

    @Override
    public void onContactSelected(ContactTilesBean contact) {
        CallLogDetailsScreenFragment mCallLogDetailsScreenFragment = new CallLogDetailsScreenFragment();
        mCallLogDetailsScreenFragment.newInstance(getActivity(), contact.getPhoneNumber(), contact.isIsTncUser());
        ((HomeScreenActivity) getActivity()).setFragment(mCallLogDetailsScreenFragment);
    }

    // async task to setup the display tiles
    class LoadTiles extends AsyncTask<Void, Void, ArrayList<ContactTilesBean>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(dialogProgress!=null && !dialogProgress.isShowing()){
                dialogProgress.show();
            }
        }

        @Override
        protected ArrayList<ContactTilesBean> doInBackground(Void... voids) {
            mListTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(),
                    GlobalCommonValues.ButtonTypeAll, GlobalCommonValues.ButtonModecall);
            return mListTiles;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactTilesBean> mListData) {
            super.onPostExecute(mListData);
            if(dialogProgress!=null && dialogProgress.isShowing()){
                dialogProgress.dismiss();
            }
            setTilesAdapter();
        }
    }

    /**
     * Method to set tiles/ button list
     */
    private void setTilesAdapter(){
        mAdapterSearchButton = new SearchButtonAdapter(getActivity(), mListTiles, this);
        // Configure recycler View
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerViewContacts.setLayoutManager(mLayoutManager);
        mRecyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewContacts.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));
        mRecyclerViewContacts.setAdapter(mAdapterSearchButton);
        mAdapterSearchButton.notifyDataSetChanged();
    }

    /**
     * Method to set list selection as per alphabet selection
     */
    private void setAlphabetSelection(int mSelectedPosition){
        iterator: for(int mPosition = 0; mPosition<mListTiles.size(); mPosition++){
            if(mListTiles.get(mPosition).getName().toLowerCase().startsWith(listAlphabets.get(mSelectedPosition).toLowerCase())){
                mRecyclerViewContacts.scrollToPosition(mPosition);
                break iterator;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                ((HomeScreenActivity)getActivity()).fragmentManager.popBackStack();
                break;
            case R.id.btnHome: // for refresh
                saveState.setRefrehContactList(getActivity(),false);
                saveState.setIS_FROM_HOME(getActivity(),true);
                if(dialogProgress!=null && !dialogProgress.isShowing()){
                    dialogProgress.show();
                }
                ((HomeScreenActivity)getActivity()).sendContactsToServer();

                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        // Handle back press of hardware button on the fragment
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    // close search view on back button pressed
                    if (!searchViewContacts.isIconified()) {
                        searchViewContacts.setIconified(true);
                    }
                    return false;
                }
                return false;
            }
        });
    }
}
