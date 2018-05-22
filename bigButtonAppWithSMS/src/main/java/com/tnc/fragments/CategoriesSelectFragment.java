package com.tnc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tnc.R;
import com.tnc.adapter.CategoriesSelectAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CategoryBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.interfaces.INotifyAction;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

/**
 * Created by a3logics on 5/5/17.
 */

public class CategoriesSelectFragment extends BaseFragmentTabs implements View.OnClickListener{

    private RelativeLayout llParentLayout;
    private FrameLayout flBackArrow;
    private TextView tvTitle,tvHeading;
    private Button btnBack, btnCancel, btnSelectCategories;
    private ListView lvCategories;
    private ArrayList<CategoryBean> mListCategoryBean = new ArrayList<CategoryBean>();
    private ArrayList<CategoryBean> mListCategoryFromDB = new ArrayList<CategoryBean>();
    private CategoriesSelectAdapter mAdapterCategoryList;
    ArrayList<String> mListButtonType = new ArrayList<String>();
    private INotifyAction mActionSelectedcategories;
    private String mButtonType = "";
    boolean isCancelClicked = false;


    public CategoriesSelectFragment newInstance(Context mContext, INotifyAction mActionSelectedcategories, String mButtonType){
        CategoriesSelectFragment frag = new CategoriesSelectFragment();
        this.mActionSelectedcategories = mActionSelectedcategories;
        this.mButtonType = mButtonType;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_select_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();

        llParentLayout=(RelativeLayout) view.findViewById(R.id.llParentLayout);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);

        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);

        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSelectCategories = (Button) view.findViewById(R.id.btnSelectCategories);


        lvCategories = (ListView) view.findViewById(R.id.lvCategories);

        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvHeading, "fonts/Roboto-Bold_1.ttf");

        //fetch category from database to display it in the dropdown
        mListCategoryFromDB = DBQuery.getCategoriesForTiles(getActivity());


        // set button types in a list
        if(mButtonType!=null && !mButtonType.isEmpty()){
            if(mButtonType.contains(",")){
                // in case of multiple value for the buttontype

                String mButtonTypeArray[] = mButtonType.split(",");

                // iterate through array for each value to be added in the list
                for(int i=0;i<mButtonTypeArray.length;i++){
                    mListButtonType.add(mButtonTypeArray[i]);
                }
            }else{
                // in case of single value for the buttontype
                mListButtonType.add(mButtonType);
            }
        }

        if(mListCategoryFromDB!=null && mListCategoryFromDB.size()>0){
            mListCategoryFromDB.remove(0);
            mListCategoryFromDB.remove(0);

            //set List Adapter
            mAdapterCategoryList = new CategoriesSelectAdapter(getActivity(), mListCategoryFromDB, mListButtonType);
            lvCategories.setAdapter(mAdapterCategoryList);
        }

        btnBack.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSelectCategories.setOnClickListener(this);
        llParentLayout.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        //call method to select categories and go to back screen
//        if(!isCancelClicked){
//            selectCategories();
//        }
        super.onDestroy();
    }

    /**
     * method to select categories and go to back screen
     */
    private void selectCategories(){
        // send selected categories id to the back screen
        if(mActionSelectedcategories!=null){
            if(mAdapterCategoryList!=null && mAdapterCategoryList.mListselectedCategory!=null && mAdapterCategoryList.mListselectedCategory.size() == 0){
                mActionSelectedcategories.setAction("");
            }else if(mAdapterCategoryList!=null && mAdapterCategoryList.mListselectedCategory!=null && mAdapterCategoryList.mListselectedCategory.size() > 0){

                // if only one category selected then no  comma separation values required
                if(mAdapterCategoryList.mListselectedCategory.size() == 1){
                    mActionSelectedcategories.setAction(mAdapterCategoryList.mListselectedCategory.get(0));
                }else{
                    // if more than one category selected then comma separation values required by iterating through each value in the loop

                    String selectedCategories = "";

                    for(int i=0; i< mAdapterCategoryList.mListselectedCategory.size(); i++){
                        selectedCategories += mAdapterCategoryList.mListselectedCategory.get(i);

                        if(!(i == mAdapterCategoryList.mListselectedCategory.size()-1)){
                            selectedCategories += ",";
                        }
                    }
                    mActionSelectedcategories.setAction(selectedCategories);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            isCancelClicked = true;
            GlobalConfig_Methods.goToBackScreen(getActivity());
        }else if(v.getId() == R.id.btnCancel){
            isCancelClicked = true;
            GlobalConfig_Methods.goToBackScreen(getActivity());
        }else if(v.getId() == R.id.btnSelectCategories){
            isCancelClicked = false;
            //call method to select categories and go to back screen
            selectCategories();

            // Go back one screen
            GlobalConfig_Methods.goToBackScreen(getActivity());
        }else if(v.getId() == R.id.llParentLayout){

        }
    }
}
