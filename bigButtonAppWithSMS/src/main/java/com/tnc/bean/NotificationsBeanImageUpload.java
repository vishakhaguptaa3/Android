package com.tnc.bean;

public class NotificationsBeanImageUpload
{
	String notifications_id;

	public NotificationsBeanImageUpload(String notifications_id) {
		super();
		this.notifications_id = notifications_id;
	}

	/**
	 * @return the notifications_id
	 */
	public String getNotifications_id() {
		return notifications_id;
	}

	/**
	 * @param notifications_id the notifications_id to set
	 */
	public void setNotifications_id(String notifications_id) {
		this.notifications_id = notifications_id;
	}
}
