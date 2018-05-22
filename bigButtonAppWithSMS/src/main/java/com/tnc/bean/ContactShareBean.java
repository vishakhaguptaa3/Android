package com.tnc.bean;

public class ContactShareBean {

	String to_id="";

	/**
	 * @return the to_id
	 */
	public String getTo_id() {
		return to_id;
	}

	/**
	 * @param to_id the to_id to set
	 */
	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}

	public ContactShareBean(String to_id) {
		super();
		this.to_id = to_id;
	}

}
