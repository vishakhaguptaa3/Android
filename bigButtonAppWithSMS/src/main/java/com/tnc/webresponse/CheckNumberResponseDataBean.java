package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class CheckNumberResponseDataBean {

	@SerializedName("is_number_change")
	public String is_number_change;

	/**
	 * @return the is_number_change
	 */
	public String getIs_number_change() {
		return is_number_change;
	}

	/**
	 * @param is_number_change the is_number_change to set
	 */
	public void setIs_number_change(String is_number_change) {
		this.is_number_change = is_number_change;
	}
}
