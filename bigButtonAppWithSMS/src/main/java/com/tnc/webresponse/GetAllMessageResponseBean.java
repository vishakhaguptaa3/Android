package com.tnc.webresponse;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GetAllMessageResponseBean 
{
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public ArrayList<SendMessageReponseDataBean> data;
	
	public ArrayList<SendMessageReponseDataBean> getData()
	{
		return data;
	}
}
