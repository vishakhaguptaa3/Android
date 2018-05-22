package com.tnc.bean;

public class GetCountrySMSRequestBean {

	String country_name="";

	String number_type="";

	public GetCountrySMSRequestBean(){		
	}
	
	public GetCountrySMSRequestBean(String country_name, String number_type) {
		super();
		this.country_name = country_name;
		this.number_type = number_type;
	}

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
	 * @return the number_type
	 */
	public String getNumber_type() {
		return number_type;
	}

	/**
	 * @param number_type the number_type to set
	 */
	public void setNumber_type(String number_type) {
		this.number_type = number_type;
	}	
}
