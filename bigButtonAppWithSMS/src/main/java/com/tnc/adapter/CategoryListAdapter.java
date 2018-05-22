package com.tnc.adapter;

/**
 * Created by a3logics on 2/1/17.
 */

import java.util.ArrayList;
import com.tnc.R;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.InitResponseMessageBean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class CategoryListAdapter extends BaseAdapter
{
    private Context mContext;

    public ArrayList<CategoryBean> listCategories = new ArrayList<CategoryBean>();

    private ViewHolder holder=null;
    private int positionListItem;
    private int selectedPosition;
    private boolean isColorSet;

    public CategoryListAdapter(Context mContext,ArrayList<CategoryBean> listCategories){
        this.listCategories = new ArrayList<CategoryBean>();
        this.listCategories = listCategories;
        this.mContext       = mContext;
    }

    public void setRowColor(int position, boolean isColorSet) {
        selectedPosition = position;
        this.isColorSet = isColorSet;
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return listCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {
        TextView tvMessage;
        LinearLayout llInitResponseMessageHolder;
        ImageView imViewArrow;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //reuse views
        if ( convertView== null)
        {
            LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.init_response_message_adapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.llInitResponseMessageHolder=(LinearLayout) convertView.findViewById(R.id.llInitResponseMessageHolder);
            holder.tvMessage=(TextView) convertView.findViewById(R.id.tvMessage);
            holder.imViewArrow=(ImageView) convertView.findViewById(R.id.imViewArrow);
            convertView.setTag(holder);
        }else{
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMessage.setTag(R.id.tvMessage,position);

        // Set the visibility of directional arrow
        int mCategoryID = -1;
        mCategoryID = Integer.parseInt(listCategories.get(position).getCategoryID());

        if(mCategoryID <=4){
            holder.imViewArrow.setVisibility(View.GONE);
        }else{
            holder.imViewArrow.setVisibility(View.VISIBLE);
        }

        if (isColorSet && position == selectedPosition){
            ((LinearLayout) convertView).setBackgroundColor(mContext
                    .getResources().getColor(R.color.stripDarkBlueColor));
            holder.tvMessage.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_white));
        }
        else{
            ((LinearLayout) convertView).setBackgroundColor(mContext
                    .getResources().getColor(R.color.white));

            if(mCategoryID <= 4){ // For system categories like Friend, Family, Business
                holder.tvMessage.setTextColor(Color.parseColor("#C0C0C0"));
            }else{
                holder.tvMessage.setTextColor(Color.parseColor("#000000"));
            }

            holder.imViewArrow.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.arrow_blue));
        }

        holder.tvMessage.setTypeface(null,Typeface.BOLD);

        holder.tvMessage.setText(listCategories.get(position).getCategoryName());
        return convertView;
    }
}
