package com.tnc.dialog;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tnc.R;
import com.tnc.bean.CloudRecoverBackupListingBean;
import com.tnc.bean.PremiumFeaturesBean;

import java.util.ArrayList;

/**
 * Created by a3logics on 23/10/17.
 */

public class PremiumFeaturesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private PremiumFeaturesBean mListFeatures = new PremiumFeaturesBean();

    public PremiumFeaturesPagerAdapter(Context mContext,PremiumFeaturesBean mListFeatures)
    {
        this.mContext=mContext;
        this.inflater = (LayoutInflater)this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);
        this.mListFeatures = mListFeatures;
    }


    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = this.inflater.inflate(R.layout.pager_list_items, container, false);

        ImageView displayImage = (ImageView)view.findViewById(R.id.feature_image);

        TextView imageText = (TextView)view.findViewById(R.id.feature_name);

        if(mListFeatures.getImage()!=null && !mListFeatures.getImage().trim().equals(""))
            Picasso.with(mContext).load(mListFeatures.getImage()).into(displayImage);

        imageText.setText(mListFeatures.getCaption());
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
