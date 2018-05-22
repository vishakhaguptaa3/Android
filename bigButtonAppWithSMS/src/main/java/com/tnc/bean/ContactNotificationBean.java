package com.tnc.bean;

public class ContactNotificationBean {

	String notification_id="";
//	String is_accept="";

	public ContactNotificationBean(String notification_id) {
		super();
		this.notification_id = notification_id;
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
}
