package com.tnc.bean;

public class CountryDetailsBean {

	public String CountryName="";
	public String CountryCode="";
	public String IDDCode="";
	public String Emergency="";
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return CountryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		CountryName = countryName;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return CountryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
	/**
	 * @return the iDDCode
	 */
	public String getIDDCode() {
		return IDDCode;
	}
	/**
	 * @param iDDCode the iDDCode to set
	 */
	public void setIDDCode(String iDDCode) {
		IDDCode = iDDCode;
	}
	/**
	 * @return the emergency
	 */
	public String getEmergency() {
		return Emergency;
	}
	/**
	 * @param emergency the emergency to set
	 */
	public void setEmergency(String emergency) {
		Emergency = emergency;
	}

	public CountryDetailsBean() {
		super();
	}
	public CountryDetailsBean(String countryName, String countryCode,
			String iDDCode, String emergency) {
		super();
		CountryName = countryName;
		CountryCode = countryCode;
		IDDCode = iDDCode;
		Emergency = emergency;
	}
}
