package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

public class GetCountrySMSResponseDataBean {

	@SerializedName("country_name")
	String country_name;
	
	@SerializedName("number")
	String number;
	
	@SerializedName("type")
	String type;

	/**
	 * @return the country_name
	 */
	public String getCountry_name() {
		return country_name;
	}

	/**
	 * @param country_name the country_name to set
	 */
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}	
}
