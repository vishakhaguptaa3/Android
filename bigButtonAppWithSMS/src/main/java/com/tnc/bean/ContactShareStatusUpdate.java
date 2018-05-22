package com.tnc.bean;

public class ContactShareStatusUpdate {

	String notification_id="";
	String status="";


	public ContactShareStatusUpdate(String notification_id, String status) {
		super();
		this.notification_id = notification_id;
		this.status = status;
	}

	/**
	 * @return the notification_id
	 */
	public String getNotification_id() {
		return notification_id;
	}

	/**
	 * @param notification_id the notification_id to set
	 */
	public void setNotification_id(String notification_id) {
		this.notification_id = notification_id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
