package com.tnc.bean;

public class CheckEmailBean
{
	String email;

	public CheckEmailBean(String email)
	{
		this.email=email;
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
}
