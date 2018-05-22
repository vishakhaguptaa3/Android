package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class VerifyRegistrationResponseDataBean {

	@SerializedName("backup_key")
	public String backup_key;

	@SerializedName("public_key")
	public String public_key;

	@SerializedName("country_code")
	public String country_code;

	@SerializedName("number")
	public String number; 

	@SerializedName("guest_user")
	public String guest_user; 
	
	@SerializedName("is_premium_user")
	public String is_premium_user;

	/**
	 * @return the backup_key
	 */
	public String getBackup_key() {
		return backup_key;
	}

	/**
	 * @param backup_key the backup_key to set
	 */
	public void setBackup_key(String backup_key) {
		this.backup_key = backup_key;
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
	 * @return the country_code
	 */
	public String getCountry_code() {
		return country_code;
	}

	/**
	 * @param country_code the country_code to set
	 */
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
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
	 * @return the guest_user
	 */
	public String getGuest_user() {
		return guest_user;
	}

	/**
	 * @param guest_user the guest_user to set
	 */
	public void setGuest_user(String guest_user) {
		this.guest_user = guest_user;
	}

	/**
	 * @return the is_premium_user
	 */
	public String getIs_premium_user() {
		return is_premium_user;
	}

	/**
	 * @param is_premium_user the is_premium_user to set
	 */
	public void setIs_premium_user(String is_premium_user) {
		this.is_premium_user = is_premium_user;
	}	
}
