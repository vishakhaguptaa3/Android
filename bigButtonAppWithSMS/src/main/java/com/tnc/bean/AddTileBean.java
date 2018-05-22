package com.tnc.bean;

public class AddTileBean
{
	String contact_name,contact_phone,contact_image,international_access_code;
	public AddTileBean(String contact_name,String contact_phone,String contact_image,String international_access_code)
	{
		this.contact_name=contact_name;
		this.contact_phone=contact_phone;
		this.contact_image=contact_image;
		this.international_access_code=international_access_code;
	}
	/**
	 * @return the contact_name
	 */
	public String getContact_name() {
		return contact_name;
	}
	/**
	 * @param contact_name the contact_name to set
	 */
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	/**
	 * @return the contact_phone
	 */
	public String getContact_phone() {
		return contact_phone;
	}
	/**
	 * @param contact_phone the contact_phone to set
	 */
	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}
	/**
	 * @return the contact_image
	 */
	public String getContact_image() {
		return contact_image;
	}
	/**
	 * @param contact_image the contact_image to set
	 */
	public void setContact_image(String contact_image) {
		this.contact_image = contact_image;
	}
	/**
	 * @return the international_access_code
	 */
	public String getInternational_access_code() {
		return international_access_code;
	}
	/**
	 * @param international_access_code the international_access_code to set
	 */
	public void setInternational_access_code(String international_access_code) {
		this.international_access_code = international_access_code;
	}
}
