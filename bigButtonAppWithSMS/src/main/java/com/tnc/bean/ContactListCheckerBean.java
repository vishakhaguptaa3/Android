package com.tnc.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ContactListCheckerBean
{
	@SerializedName("contact_id")
	String contact_id="";
	
	@SerializedName("phonenumber")
	public ArrayList<ContactPhoneNumberBean> phonenumber;
	
	public ContactListCheckerBean()
	{
	}

	/**
	 * @return the contact_id
	 */
	public String getContact_id() 
	{
		return contact_id;
	}

	/**
	 * @param contact_id the contact_id to set
	 */
	public void setContact_id(String contact_id) 
	{
		this.contact_id = contact_id;
	}
	
	public ArrayList<ContactPhoneNumberBean> getPhoneData() 
	{
		return phonenumber;
	}
}
