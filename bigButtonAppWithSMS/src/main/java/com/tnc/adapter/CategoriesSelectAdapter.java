package com.tnc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.tnc.R;
import com.tnc.bean.CategoryBean;
import java.util.ArrayList;

/**
 * Created by a3logics on 5/5/17.
 */

public class CategoriesSelectAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CategoryBean> mListCategoryFromDB;
    public ArrayList<String> mListselectedCategory = new ArrayList<String>();
    ArrayList<String> mListButtonType = new ArrayList<String>();

    public CategoriesSelectAdapter(Activity mContext, ArrayList<CategoryBean> mListCategoryFromDB, ArrayList<String> mListButtonType){
        this.mContext = mContext;
        this.mListCategoryFromDB = new ArrayList<CategoryBean>();
        this.mListCategoryFromDB = mListCategoryFromDB;
        this.mListButtonType = mListButtonType;
    }

    @Override
    public int getCount() {
        return mListCategoryFromDB.size();
    }

    @Override
    public Object getItem(int position) {
        return mListCategoryFromDB.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView tvCategory;
        CheckBox mCheckBoxCategory;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder            = null;

        //reuse views
        if ( convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elderly_mode_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tvElderModeOption);
            holder.mCheckBoxCategory=(CheckBox) convertView.findViewById(R.id.chkBoxElderlyModeOption);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mCheckBoxCategory.setChecked(false);

        holder.mCheckBoxCategory.setTag(position);

        holder.tvCategory.setText(mListCategoryFromDB.get(position).getCategoryName());

        // in case list item is previously selected then make te selectable
        if(mListButtonType!=null && !mListButtonType.isEmpty()){
            if(mListButtonType.contains(mListCategoryFromDB.get(position).getCategoryID())){
                holder.mCheckBoxCategory.setChecked(true);
                if(!(mListselectedCategory.contains(mListCategoryFromDB.get(position).getCategoryID())))
                    mListselectedCategory.add(mListCategoryFromDB.get(position).getCategoryID());
            }
        }

        // Toggle checkbox state
        holder.mCheckBoxCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox mChkBox = ((CheckBox)v);

                int mIdCheckBox = Integer.parseInt(String.valueOf(mChkBox.getTag()));

                if(mChkBox.isChecked()){
//                    mChkBox.setChecked(false);

                    // add an item to the list
                    if((mListselectedCategory!=null) &&
                            !(mListselectedCategory.contains(mListCategoryFromDB.get(mIdCheckBox).getCategoryID()))){
                        mListselectedCategory.add(mListCategoryFromDB.get(mIdCheckBox).getCategoryID());
                    }
                }else{
//                    mChkBox.setChecked(true);

                    // remove item from the list
                    if((mListselectedCategory!=null) &&
                            (mListselectedCategory.contains(mListCategoryFromDB.get(mIdCheckBox).getCategoryID()))){
                        mListselectedCategory.remove(mListCategoryFromDB.get(mIdCheckBox).getCategoryID());
                    }

                }
            }
        });

        return convertView;
    }
}
