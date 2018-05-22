package com.tnc.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tnc.R;
import com.tnc.adapter.CategoryListAdapter;
import com.tnc.adapter.HowtoAdapter;
import com.tnc.adapter.TncUserNotifyRegistrationAdapter;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.interfaces.INotifyImageNotifyDialog;
import com.tnc.preferences.SharedPreference;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by a3logics on 14/11/17.
 */

public class NotifyImageDialog  extends DialogFragment implements View.OnClickListener {
        private TextView tvTitle;
        private ListView groupListView;
        public Button btnYes,btnNo;
        private Context mAct;
        private String title="";
        private File fileDatabase;
        private Gson gson;
        private TransparentProgressDialog progress;
        private SharedPreference saveState;
        private ArrayList<ContactTilesBean> listBackedup=null;
        private INotifyImageNotifyDialog iNotifySetDefault;
        ArrayList<Boolean> checkState ;
        ArrayList<CategoryBean> listCategories;

        public NotifyImageDialog newInstance(String title, Activity mAct, INotifyImageNotifyDialog iNotifySetDefault)
        {
            this.mAct = mAct;
            this.title=title;
            NotifyImageDialog frag = new NotifyImageDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            this.iNotifySetDefault=iNotifySetDefault;
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        }

        @Override
        public void onStart()
        {
            super.onStart();
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.6f;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.notify_image_update, container);
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            btnYes = (Button) view.findViewById(R.id.btnYes);
            btnNo= (Button) view.findViewById(R.id.btnNo);
            groupListView = (ListView) view.findViewById(R.id.groupListView);
            CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
            CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");

            btnYes.setAllCaps(true);
            btnNo.setAllCaps(true);

            btnYes.setOnClickListener(this);
            btnNo.setOnClickListener(this);

            //fetch categories from database
            listCategories = displayCategoriesFromDB();
            ListViewAdapter adapter = new ListViewAdapter(listCategories);
            groupListView.setAdapter(adapter);
            updateView();
            return view;
        }

        private void updateView()
        {
            btnYes.setText(getActivity().getResources().getString(R.string.txtBroadCastPopUpButton));
            tvTitle.setText(title);
            if(title.trim().equals(""))
            {
                tvTitle.setVisibility(View.GONE);
            }
            else{
                tvTitle.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View v)
        {
            if(v.getId()==R.id.btnYes)
            {
                dismiss();
                String categoryListCommaSeperated = "";
                if(iNotifySetDefault!=null) {
                    for(int i=0;i<checkState.size();i++){
                        if(checkState.get(i)) {
                            if(categoryListCommaSeperated.length()==0){
                                categoryListCommaSeperated = categoryListCommaSeperated + listCategories.get(i).getCategoryID();
                            }else{
                                categoryListCommaSeperated = categoryListCommaSeperated +","+ listCategories.get(i).getCategoryID();
                            }

                        }
                    }
                    iNotifySetDefault.yes(categoryListCommaSeperated);
                }
            }
            else if(v.getId()==R.id.btnNo)
            {
                dismiss();
                if(iNotifySetDefault!=null)
                    iNotifySetDefault.no();
            }
        }

        class ListViewAdapter extends BaseAdapter {

           ViewHolder holder;
           ArrayList<CategoryBean> mGroupList;


            public ListViewAdapter(ArrayList<CategoryBean> arrayList) {
                mGroupList  = arrayList;
                checkState = new ArrayList<Boolean>();
                for(int i=0;i<arrayList.size();i++){
                    if(i==0) {
                        checkState.add(true);
                    }else{
                        checkState.add(false);
                    }
                }
            }

            @Override
            public int getCount() {
                return mGroupList.size();
            }

            @Override
            public CategoryBean getItem(int position) {
                return mGroupList!=null ? mGroupList.get(position):null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.notify_image_adapter_layout, null);
                    // configure view holder
                    holder = new ViewHolder();
                    holder.image_adapter_checkbox = (CheckBox) convertView.findViewById(R.id.image_adapter_checkbox);
                    holder.image_adapter_text = (TextView) convertView.findViewById(R.id.image_adapter_text);
                    convertView.setTag(holder);
                }else {
                    // fill data
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.image_adapter_text.setText(getItem(position).getCategoryName());
                holder.image_adapter_checkbox.setChecked(checkState.get(position));
                holder.image_adapter_checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position==0){
                            for(int i=0;i<getCount();i++){
                                if(i==0) {
                                    checkState.set(i,true);
                                }else{
                                    checkState.set(i,false);
                                }
                            }
                        }else{
                            checkState.set(0,false);
                            if(checkState.get(position)) {
                                checkState.set(position, false);
                            }else{
                                checkState.set(position, true);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                return convertView;
            }

            class ViewHolder  {
                CheckBox image_adapter_checkbox;
                TextView image_adapter_text;
            }

        }

    /**
     * Method to display pre-configured messages
     */
    private ArrayList<CategoryBean> displayCategoriesFromDB(){
        ArrayList<CategoryBean> listCategories = new ArrayList<CategoryBean>();

        //fetch categories from database
        listCategories = DBQuery.getCategoriesForTiles(getActivity());

        if(listCategories!=null && !listCategories.isEmpty()){
            // remove Recent Calls and 'all' option from the list
            listCategories.remove(0);
        }
        return listCategories;
    }



}

