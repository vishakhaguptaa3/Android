package com.tnc.dialog;

/**
 * Created by a3logics on 13/10/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.bean.PremiumFeaturesBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.utility.Logs;
import com.tnc.webresponse.PremiumFeatureResponseBean;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PremiumFeaturesDialog extends DialogFragment {

    private Context mContext;
    private TextView tvTitle;
    private Button   btnOk;
    private LinearLayout layoutDots;

    private ViewPager viewPageFeatures;
    private PremiumFeaturesPagerAdapter adapter;

    private TransparentProgressDialog progress;

    private Gson gson;

    private PremiumFeaturesBean mListFeatures = new PremiumFeaturesBean();

    private TextView[] dots;

    public PremiumFeaturesDialog newInstance(Context mContext)
    {
        this.mContext = mContext;
        PremiumFeaturesDialog frag = new PremiumFeaturesDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        //this line is what you need:
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        params.dimAmount = 0.6f;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.premium_features_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        tvTitle            = (TextView) view.findViewById(R.id.tvTitle);
        btnOk              = (Button) view.findViewById(R.id.btnOk);
        layoutDots         = (LinearLayout) view.findViewById(R.id.layoutDots);

        // Locate the ViewPager in premium_features_dialog.xml
        viewPageFeatures   = (ViewPager) view.findViewById(R.id.viewPageFeatures);

        tvTitle.setText(mContext.getResources().getString(R.string.txtPremiumFeatureTitle));

        CustomFonts.setFontOfTextView(getActivity(), tvTitle,
                "fonts/comic_sans_ms_regular.ttf");

        progress=new TransparentProgressDialog(getActivity(),R.drawable.customspinner);
        gson=new Gson();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // load premium features data from the server
        checkInternetConnection();

        viewPageFeatures.setOnPageChangeListener(viewPagerPageChangeListener);

        return view;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
                //addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * check availability of internet connection
     */
    public void checkInternetConnection() {
        if (NetworkConnection.isNetworkAvailable(getActivity())) {
            premiumFeaturesRequest();
        }
        else
        {
            ShowDialog.alert(getActivity(), "", getActivity().getResources()
                    .getString(R.string.no_internet_abvailable), new AlertCallBack() {
                @Override
                public void alertAction(boolean select) {
                    dismiss();
                }
            });
        }
    }

    //Method to call web service to get clipart images
    private void premiumFeaturesRequest()
    {
        MyHttpConnection.getWithoutPara(getActivity(), GlobalCommonValues.GET_PREMIUM_FEATURES,
                getActivity().getResources().getString(R.string.private_key),premiumFeaturesResponsehandler);
    }

    //async task to handle request made to get the PremiumFeatures
    AsyncHttpResponseHandler premiumFeaturesResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if ((!progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null)
                {
                    Logs.writeLog("PremiumFeatures", "OnSuccess",response.toString());
                    getResponse(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("PremiumFeatures", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /*
	 * Handling Response from the Server for the request being sent to get clip art images
	 */
    private void getResponse(String response) {
        try {
            if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
                try
                {
                    gson = new Gson();
                    PremiumFeatureResponseBean get_Response = gson.fromJson(response,PremiumFeatureResponseBean.class);
                    if (get_Response.getResponse_code() .equalsIgnoreCase(GlobalCommonValues.SUCCESS_CODE)){
                       if(get_Response.getmListFeatures()!=null ){


                           mListFeatures = get_Response.getmListFeatures();

                           // adding bottom dots
                           //addBottomDots(0);

                           // Pass results to ViewPagerAdapter Class
                           adapter = new PremiumFeaturesPagerAdapter(getActivity(), mListFeatures);
                           // Binds the Adapter to the ViewPager
                           viewPageFeatures.setAdapter(adapter);
                       }
                    } else{
                        ShowDialog.alert(getActivity(), "", get_Response.getResponse_message());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            } else {
                ShowDialog.alert(getActivity(), "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void addBottomDots(int currentPage) {



        dots = new TextView[mListFeatures.size()];

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(getActivity().getResources().getDimension(R.dimen.txtSizeMedium));
            dots[i].setTextColor(getActivity().getResources().getColor(R.color.dot_inactive_color)); //(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getActivity().getResources().getColor(R.color.dot_active_color)); //(colorsActive[currentPage]);
    }*/
}
