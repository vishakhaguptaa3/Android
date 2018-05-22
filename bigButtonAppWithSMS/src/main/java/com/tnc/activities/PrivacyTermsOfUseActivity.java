package com.tnc.activities;

import java.io.File;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.DefaultImageConfirmation;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import static com.tnc.common.GlobalCommonValues.NO;
import static com.tnc.common.GlobalCommonValues.YES;

public class PrivacyTermsOfUseActivity extends FragmentActivity implements OnClickListener{

    private Context mAct;
    private SharedPreference saveState;
    private TextView tvTitle,tvThankYou,tvAgreementTitle;
    private Button btnAgeConfirmation,btnQuestion,btnCancel,btnOk;
    private boolean isChecked = false;
    private boolean isOkClicked=false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.terms_of_use);
        mAct = PrivacyTermsOfUseActivity.this;
        saveState = new SharedPreference();

        //initialization of views/widgets
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvThankYou = (TextView) findViewById(R.id.tvThankYou);
        tvAgreementTitle = (TextView) findViewById(R.id.tvAgreementTitle);
        btnAgeConfirmation = (Button) findViewById(R.id.btnAgeConfirmation);
        btnQuestion = (Button) findViewById(R.id.btnQuestion);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnQuestion.setOnClickListener(this);
        btnAgeConfirmation.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnAgeConfirmation.setBackgroundResource(R.drawable.unchecked_white);
        btnOk.setBackgroundResource(R.drawable.button_bg_greyed_out);
        btnOk.setEnabled(false);
        CustomFonts.setFontOfTextView(mAct, tvTitle,
                "fonts/comic_sans_ms_regular.ttf");

        //		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        //		CustomFonts.setFontOfTextView(mAct,tvTitle, "fonts/Helvetica-Bold.otf");


        //open elderly Mode Pop-up if user hasn't taken action yet
        if(saveState.getIsElderlyMode(PrivacyTermsOfUseActivity.this).trim().equalsIgnoreCase("")){
            openElderlyModePopup();
        }

        String msg = getResources().getString(R.string.txtAppInstallAgreementMessage);

        int i1 = msg.indexOf("Terms");
        int i2 = msg.indexOf("and")-1;
        int i3 = msg.indexOf("Privacy");
        int i4 = msg.length()-1;
        tvAgreementTitle.setMovementMethod(LinkMovementMethod.getInstance());

        tvAgreementTitle.setText(msg, BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)tvAgreementTitle.getText();
        ClickableSpan myClickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent myIntent = new Intent(mAct,PrivacyPolicyActivity.class);
                myIntent.putExtra("title",getResources().getString(R.string.txtTermsOfUseCamelCase));
                startActivity(myIntent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#ffffff"));//set text color
                ds.setUnderlineText(true); // set to false to remove underline
            }
        };
        ClickableSpan myClickableSpan2 = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent myIntent = new Intent(mAct,PrivacyPolicyActivity.class);
                myIntent.putExtra("title",getResources().getString(R.string.txtPrivacyPolicyCamelCase));
                startActivity(myIntent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#ffffff"));//set text color
                ds.setUnderlineText(true); // set to false to remove underline
            }
        };

        mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(myClickableSpan2, i3, i4 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        /**
         * "By installing this App, you are agreeing to our "+
         linkTermsOfUse+" and "+linkPrivacyPolicy
         */

        //Clear Image/DatabaseDirectory if any exists earlier

        String userImageFile = String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNC/");

        String tncImages= String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNCImages/");

        String tncTest= String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNCTest/");

        String userDatabase= String.valueOf(
                Environment.getExternalStorageDirectory()+File.separator+"TNCDatabase/");

        String recordingPath = Environment.getExternalStorageDirectory().getPath() + File.separator +
                getResources().getString(R.string.app_name); // + File.pathSeparator + "AudioRecorder";

        //Delete Files from App Directories
        GlobalConfig_Methods.deleteFiles(userImageFile);
        GlobalConfig_Methods.deleteFiles(tncImages);
        GlobalConfig_Methods.deleteFiles(tncTest);
        GlobalConfig_Methods.deleteFiles(userDatabase);
        GlobalConfig_Methods.deleteFiles(recordingPath);
    }

    @SuppressWarnings("unused")
    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @SuppressWarnings("unused")
    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener)                 {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return link;
    }

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    /**
     * Method to set elderly mode on/off
     */
    private void openElderlyModePopup(){
        DefaultImageConfirmation mDialog = new DefaultImageConfirmation();
        mDialog.setCancelable(false);
        mDialog.newInstance("", PrivacyTermsOfUseActivity.this,getResources().getString(R.string.txtActivateElderlyMode),
                "", mActionElderlyMode);
        mDialog.show(getSupportFragmentManager(), "Test");
    }

    /**
     * Interface to handle action taken to enable/disable elderly mode
     */
    INotifyGalleryDialog mActionElderlyMode = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // in case user clicks yes to activate elderly mode
            saveState.setIsElderlyMode(PrivacyTermsOfUseActivity.this,
                    YES);
            GlobalConfig_Methods.ConfigureElderlyModeOptions(PrivacyTermsOfUseActivity.this,true, true, true);
        }

        @Override
        public void no() {
           // in case user clicks no to de-activate elderly mode
            saveState.setIsElderlyMode(PrivacyTermsOfUseActivity.this,
                    NO);
            GlobalConfig_Methods.ConfigureElderlyModeOptions(PrivacyTermsOfUseActivity.this,false, false, false);
        }
    };

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btnQuestion){
            ImageRequestDialog dialog = new ImageRequestDialog();
            dialog.setCancelable(false);
            dialog.newInstance(getResources().getString(R.string.app_name),mAct,getResources().getString(R.string.txtAgeConfirmation18Years),"",null);
            dialog.show((((PrivacyTermsOfUseActivity)mAct).getSupportFragmentManager()), "test");

        }else if(v.getId()==R.id.btnOk){
            //			saveState.setCLIPARTS_VERSION(mAct,"1.2");
            isOkClicked=true;
            Intent myIntent=new Intent(((PrivacyTermsOfUseActivity)mAct),MainBaseActivity.class);
            startActivity(myIntent);
            ((PrivacyTermsOfUseActivity)mAct).finish();

        }else if(v.getId()==R.id.btnCancel){
            saveState.setFirstTime(mAct, true);
            ImageRequestDialog dialog = new ImageRequestDialog();
            dialog.setCancelable(false);
            dialog.newInstance(getResources().getString(R.string.app_name),mAct,getResources().getString(R.string.txtRunAppWhenready),"",mAlertAction);
            dialog.show((((PrivacyTermsOfUseActivity)mAct).getSupportFragmentManager()), "test");
        }
        else if(v.getId()==R.id.btnAgeConfirmation){
            if(isChecked){
                isChecked=false;
                btnAgeConfirmation.setBackgroundResource(R.drawable.unchecked_white);
                btnOk.setBackgroundResource(R.drawable.button_bg_greyed_out);
                btnOk.setEnabled(false);

            }else{
                isChecked=true;
                btnAgeConfirmation.setBackgroundResource(R.drawable.checked_white);
                btnOk.setBackgroundResource(R.drawable.button_bg_skip);
                btnOk.setEnabled(true);
            }
        }
    }

    /**
     * interface to finish the app screen
     */
    AlertCallAction mAlertAction = new AlertCallAction() {

        @Override
        public void isAlert(boolean isOkClikced) {
            if(isOkClikced){
                finish();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode == KeyEvent.KEYCODE_BACK)){
            if(saveState.isFirst(mAct))
                saveState.setFirstTime(mAct,true);
        }
        return super.onKeyDown(keyCode, event);
    }
}
