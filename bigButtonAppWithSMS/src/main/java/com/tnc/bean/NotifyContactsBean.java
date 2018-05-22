package com.tnc.bean;

public class NotifyContactsBean {

	public String type="";
	public String tncids="";
	
	public NotifyContactsBean(String type, String tncids) {
		super();
		this.type = type;
		this.tncids = tncids;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the tncids
	 */
	public String getTncids() {
		return tncids;
	}
	/**
	 * @param tncids the tncids to set
	 */
	public void setTncids(String tncids) {
		this.tncids = tncids;
	}
}
