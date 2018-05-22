package com.tnc.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CategoryListAdapter;
import com.tnc.adapter.InitResponseMessageEditableAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;

/**
 * Created by a3logics on 2/1/17.
 */

public class ChatButtonCategoryListFragment extends BaseFragmentTabs implements View.OnClickListener{

    private LinearLayout llTopView;
    private Context mActivity;
    private FrameLayout flBackArrow,flInformationButton;
    private Button btnBack,btnHome;

    private TextView tvTitle,tvNew,tvChatButtonCategoryTitle,tvHeaderTitle;

    private ListView lvEditableCategoriesFromDB;
    private ArrayList<CategoryBean> listCategories = null;
    private CategoryListAdapter adapterCategoryList = null;
    private int adapterSelected_position;
    private TransparentProgressDialog progress;
    private  SharedPreference saveState;


    public ChatButtonCategoryListFragment newInstance(Context mActivity){
        ChatButtonCategoryListFragment frag = new ChatButtonCategoryListFragment();
        this.mActivity=mActivity;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.chat_button_category_list, container, false);
        idInitialization(view);

        // display categories from the database
        displayCategoriesFromDB();

        return view;
    }

    private void idInitialization(View view) {
        saveState = new SharedPreference();
        progress = new TransparentProgressDialog(getActivity(), R.drawable.customspinner);
        newInstance(mActivityTabs);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvNew = (TextView) view.findViewById(R.id.tvNew);

        tvChatButtonCategoryTitle = (TextView) view.findViewById(R.id.tvChatButtonCategoryTitle);

        tvChatButtonCategoryTitle.setTextColor(Color.parseColor("#3692DB"));
        tvChatButtonCategoryTitle.setAllCaps(true);

        lvEditableCategoriesFromDB = (ListView) view.findViewById(R.id.lvEditableMessagesFromDB);

        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        flInformationButton = (FrameLayout) view.findViewById(R.id.flInformationButton);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        llTopView = (LinearLayout) view.findViewById(R.id.llTopView);
        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");

        CustomFonts.setFontOfTextView(getActivity(), tvChatButtonCategoryTitle, "fonts/Roboto-Bold_1.ttf");
        HomeScreenActivity.btnHome.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        llTopView.setOnClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
            }
        });

        lvEditableCategoriesFromDB.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View childView, int position,
                                    long arg3) {
                if(adapterCategoryList.listCategories!=null &&
                        !adapterCategoryList.listCategories.isEmpty()){

                    try {

                        int mCategoryID = Integer.parseInt(listCategories.get(position).getCategoryID());

                        if(mCategoryID > 4){


                            adapterSelected_position=position;
                            adapterCategoryList.setRowColor(adapterSelected_position, true);

                            AddEditChatButtonCategory objAddEditChatButtonCategory = new AddEditChatButtonCategory();
                            objAddEditChatButtonCategory.newInstance(mActivityTabs, mCategoryID,
                                    listCategories.get(position).getCategoryName(),
                                    iNotifyGalleryDialog);
                            ((HomeScreenActivity)mActivityTabs).setFragment(objAddEditChatButtonCategory);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }

                }
            }
        });
    }

    /**
     * Method to display pre-configured messages
     */
    private void displayCategoriesFromDB(){
        listCategories = new ArrayList<CategoryBean>();

        //fetch categories from database
        listCategories = DBQuery.getCategoriesForTiles(getActivity());

        if(listCategories!=null && !listCategories.isEmpty()){

            // remove Recent Calls and 'all' option from the list
            listCategories.remove(0);
            listCategories.remove(0);

            // Set the list adapter and display it in a listview
            adapterCategoryList = new CategoryListAdapter(getActivity(), listCategories);

            lvEditableCategoriesFromDB.setAdapter(adapterCategoryList);
        }
    }

    /**
     * interface to handle list refresh after coming back to this screen from add/edit category screen
     */
    INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            displayCategoriesFromDB();
            adapterCategoryList.setRowColor(adapterSelected_position, false);
        }

        @Override
        public void no() {
            displayCategoriesFromDB();
            adapterCategoryList.setRowColor(adapterSelected_position, false);
        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.llTopView){

        }else if(view.getId()==R.id.btnHome){
            if(mActivityTabs instanceof MainBaseActivity){
                ((MainBaseActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
                ((MainBaseActivity)mActivityTabs).finish();
            }else if(mActivityTabs instanceof HomeScreenActivity){
                ((HomeScreenActivity)mActivityTabs).startActivity(new Intent(mActivityTabs,HomeScreenActivity.class));
                ((HomeScreenActivity)mActivityTabs).finish();
            }
        }else if(view.getId()==R.id.tvNew){

            if(!saveState.isRegistered(mActivityTabs)){
                ImageRequestDialog dialog=new ImageRequestDialog();
                dialog.setCancelable(false);
                dialog.newInstance("", mActivityTabs, Html.fromHtml("Please create profile <br>"+
                        "to use this feature").toString(),"",null,iNotifyGalleryDialog);
                dialog.show(getChildFragmentManager(),"test");
            }else{
                AddEditChatButtonCategory objAddEditChatButtonCategory = new AddEditChatButtonCategory();
                objAddEditChatButtonCategory.newInstance(mActivityTabs,-1,null,iNotifyGalleryDialog);
                ((HomeScreenActivity)mActivityTabs).setFragment(objAddEditChatButtonCategory);
            }
        }
    }
}
