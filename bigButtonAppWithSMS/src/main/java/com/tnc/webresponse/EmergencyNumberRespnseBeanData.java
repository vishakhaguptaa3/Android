package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class EmergencyNumberRespnseBeanData {
	
	@SerializedName("country")
	public String country;
	
	@SerializedName("emergency")
	public String emergency;
	
	@SerializedName("version")
	public String version;

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the emergency
	 */
	public String getEmergency() {
		return emergency;
	}

	/**
	 * @param emergency the emergency to set
	 */
	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}
