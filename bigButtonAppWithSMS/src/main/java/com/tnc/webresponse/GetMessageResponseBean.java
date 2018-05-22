package com.tnc.webresponse;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GetMessageResponseBean {
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public ArrayList<GetMessageResponseDataBean> getData;

	public ArrayList<GetMessageResponseDataBean> getData()
	{
		return getData;
	}
}
