package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotificationUpdateResponseDatBean 
{
	@SerializedName("notification_id")
	public String notification_id;

	@SerializedName("status")
	public String status;

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
