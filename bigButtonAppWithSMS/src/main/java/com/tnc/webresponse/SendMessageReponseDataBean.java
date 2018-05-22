package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class SendMessageReponseDataBean 
{
	@SerializedName("message_id")
	public String message_id;

	@SerializedName("from_user_id")
	public String from_user_id;
	
	@SerializedName("from_user_phone")
	public String from_user_phone;

	@SerializedName("to_user_id")
	public String to_user_id;

	@SerializedName("message")
	public String message;

	@SerializedName("status")
	public String status;

	@SerializedName("datetime")
	public String datatime;
	
	@SerializedName("name")
	public String name;

	public String unread_count;

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String imageurl;
	/**
	 * @return the message_id
	 */
	public String getMessage_id() {
		return message_id;
	}

	/**
	 * @param message_id the message_id to set
	 */
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
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

	/**
	 * @return the datatime
	 */
	public String getDatatime() {
		return datatime;
	}

	/**
	 * @param datatime the datatime to set
	 */
	public void setDatatime(String datatime) {
		this.datatime = datatime;
	}

	/**
	 * @return the from_user_phone
	 */
	public String getFrom_user_phone() {
		return from_user_phone;
	}

	/**
	 * @param from_user_phone the from_user_phone to set
	 */
	public void setFrom_user_phone(String from_user_phone) {
		this.from_user_phone = from_user_phone;
	}

	/**
	 * @return the unread_count
	 */
	public String getUnread_count() {
		return unread_count;
	}

	/**
	 * @param unread_count the unread_count to set
	 */
	public void setUnread_count(String unread_count) {
		this.unread_count = unread_count;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}	
}
