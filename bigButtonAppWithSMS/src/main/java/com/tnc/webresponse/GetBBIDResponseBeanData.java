package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class GetBBIDResponseBeanData {

	@SerializedName("user_id")
	public String user_id; 

	@SerializedName("email")
	public String email;
	
	@SerializedName("image")
	public String image; 

	@SerializedName("is_default_image")
	public String is_default_image; 

	@SerializedName("name")
	public String name; 

	@SerializedName("country_code")
	public String country_code;

	@SerializedName("country_idd")
	public String country_idd;
	
	@SerializedName("country_name")
	public String country_name;

	@SerializedName("country_emergency")
	public String country_emergency;	

	@SerializedName("number")
	public String number; 

	@SerializedName("is_activate")
	public int is_activate;

	@SerializedName("is_email_verified")
	public int is_email_verified;

	@SerializedName("is_verified")
	public String is_verified;
	
	@SerializedName("is_premium_user")
	public String is_premium_user;

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the is_default_image
	 */
	public String getIs_default_image() {
		return is_default_image;
	}

	/**
	 * @param is_default_image the is_default_image to set
	 */
	public void setIs_default_image(String is_default_image) {
		this.is_default_image = is_default_image;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the is_email_verified
	 */
	public int getIs_email_verified() {
		return is_email_verified;
	}

	/**
	 * @param is_email_verified the is_email_verified to set
	 */
	public void setIs_email_verified(int is_email_verified) {
		this.is_email_verified = is_email_verified;
	}

	/**
	 * @return the is_verified
	 */
	public String getIs_verified() {
		return is_verified;
	}

	/**
	 * @param is_verified the is_verified to set
	 */
	public void setIs_verified(String is_verified) {
		this.is_verified = is_verified;
	}

	/**
	 * @return the is_activate
	 */
	public int getIs_activate() {
		return is_activate;
	}

	/**
	 * @param is_activate the is_activate to set
	 */
	public void setIs_activate(int is_activate) {
		this.is_activate = is_activate;
	}

	/**
	 * @return the country_idd
	 */
	public String getCountry_idd() {
		return country_idd;
	}

	/**
	 * @param country_idd the country_idd to set
	 */
	public void setCountry_idd(String country_idd) {
		this.country_idd = country_idd;
	}

	/**
	 * @return the country_emergency
	 */
	public String getCountry_emergency() {
		return country_emergency;
	}

	/**
	 * @param country_emergency the country_emergency to set
	 */
	public void setCountry_emergency(String country_emergency) {
		this.country_emergency = country_emergency;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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
