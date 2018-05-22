package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotificationPushReponseBean 
{
	@SerializedName("message")
	String message;

	@SerializedName("to_user_id")
	String to_user_id;

	@SerializedName("from_user_id")
	String from_user_id;

	@SerializedName("notification_id")
	String notification_id;

	@SerializedName("datetime")
	String datetime;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the to_user_id
	 */
	public String getTo_user_id() {
		return to_user_id;
	}

	/**
	 * @param to_user_id the to_user_id to set
	 */
	public void setTo_user_id(String to_user_id) {
		this.to_user_id = to_user_id;
	}

	/**
	 * @return the from_user_id
	 */
	public String getFrom_user_id() {
		return from_user_id;
	}

	/**
	 * @param from_user_id the from_user_id to set
	 */
	public void setFrom_user_id(String from_user_id) {
		this.from_user_id = from_user_id;
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
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}


}
