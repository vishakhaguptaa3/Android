package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;
import com.tnc.bean.PremiumFeaturesBean;

import java.util.ArrayList;

/**
 * Created by a3logics on 23/10/17.
 */

public class PremiumFeatureResponseBean {

    @SerializedName("response_code")
    String response_code;

    @SerializedName("response_message")
    String response_message="";

    @SerializedName("data")
    PremiumFeaturesBean mListFeatures = new PremiumFeaturesBean();

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public PremiumFeaturesBean getmListFeatures() {
        return mListFeatures;
    }

    public void setmListFeatures(PremiumFeaturesBean mListFeatures) {
        this.mListFeatures = mListFeatures;
    }
}
