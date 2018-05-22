package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;


public class GetBBIDUserDetailsBean 
{
	@SerializedName("user_id")
	public String user_id;

	@SerializedName("image")
	public String image;

	@SerializedName("is_default_image")
	public String is_default_image;

	@SerializedName("name")
	public String name;

	@SerializedName("number")
	public String number;
	
	@SerializedName("country_code")
	public String country_code;

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
}
