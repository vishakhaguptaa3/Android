package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class GetAllMessageResponseData {
	@SerializedName("message_id")
	String message_id;
	@SerializedName("from_user_id")
	String from_user_id;
	@SerializedName("to_user_id")
	String to_user_id;
	@SerializedName("message")
	String message;
	@SerializedName("status")
	String status;
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
