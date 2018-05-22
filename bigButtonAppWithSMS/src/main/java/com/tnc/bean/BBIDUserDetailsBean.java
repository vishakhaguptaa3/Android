package com.tnc.bean;

public class BBIDUserDetailsBean {

	public String user_id;

	public BBIDUserDetailsBean(String user_id) {
		super();
		this.user_id = user_id;
	}

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
}
