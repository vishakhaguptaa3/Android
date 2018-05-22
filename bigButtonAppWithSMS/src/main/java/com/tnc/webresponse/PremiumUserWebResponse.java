package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a3logics on 9/8/16.
 */
public class PremiumUserWebResponse {

    @SerializedName("response_code")
    public String response_code;

    @SerializedName("response_message")
    public String response_message;

    @SerializedName("public_key")
    public String public_key;


    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }
}
