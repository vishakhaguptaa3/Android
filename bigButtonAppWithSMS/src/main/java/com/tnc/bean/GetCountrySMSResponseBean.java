package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

public class GetCountrySMSResponseBean {

	@SerializedName("response_code")
	String response_code;
	
	@SerializedName("response_message")
	String response_message;
	
	@SerializedName("public_key")
	String public_key;
	
	@SerializedName("data")
	GetCountrySMSResponseDataBean data;

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
	 * @return the public_key
	 */
	public String getPublic_key() {
		return public_key;
	}

	/**
	 * @param public_key the public_key to set
	 */
	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	/**
	 * @return the data
	 */
	public GetCountrySMSResponseDataBean getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(GetCountrySMSResponseDataBean data) {
		this.data = data;
	}		
}
