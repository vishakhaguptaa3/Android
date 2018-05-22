package com.tnc.webresponse;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class DefaultMessagesResponse {

	@SerializedName("response_code")
	public String response_code;
	
	@SerializedName("response_message")
	public String response_message;
	
	@SerializedName("data")
	public ArrayList<DefaultMessagesResponseData> data;

	/**
	 * @return the response_code
	 */
	public String getResponse_code() {
		return response_code;
	}

	/**
	 * @param response_code the response_code to set
	 */
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	/**
	 * @return the response_message
	 */
	public String getResponse_message() {
		return response_message;
	}

	/**
	 * @param response_message the response_message to set
	 */
	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}

	/**
	 * @return the data
	 */
	public ArrayList<DefaultMessagesResponseData> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<DefaultMessagesResponseData> data) {
		this.data = data;
	}	
}
