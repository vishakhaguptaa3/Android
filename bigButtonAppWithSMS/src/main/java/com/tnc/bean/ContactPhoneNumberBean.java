package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

public class ContactPhoneNumberBean 
{
	@SerializedName("countryCode")
	String countryCode="";

	@SerializedName("number")
	String number="";

	@SerializedName("bbid")
	String bbid="";
	
	@SerializedName("name")
	String name="";

	@SerializedName("image")
	String image="";

	public ContactPhoneNumberBean()
	{

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
	 * @return the bbid
	 */
	public String getBbid() {
		return bbid;
	}

	/**
	 * @param bbid the bbid to set
	 */
	public void setBbid(String bbid) {
		this.bbid = bbid;
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
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
}
