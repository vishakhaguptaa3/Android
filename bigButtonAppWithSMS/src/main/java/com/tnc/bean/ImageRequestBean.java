package com.tnc.bean;

public class ImageRequestBean
{
	String to_id="";

	int is_image_requested;

	int is_emergency;

	int is_image_broadcast;

	public ImageRequestBean(String to_id, int is_image_requested, int is_emergency, int is_image_broadcast)
	{
		this.to_id=to_id;
		this.is_image_requested = is_image_requested;
		this.is_emergency = is_emergency;
		this.is_image_broadcast = is_image_broadcast;
	}

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

	public int getIs_image_requested() {
		return is_image_requested;
	}

	public void setIs_image_requested(int is_image_requested) {
		this.is_image_requested = is_image_requested;
	}

	public int getIs_emergency() {
		return is_emergency;
	}

	public void setIs_emergency(int is_emergency) {
		this.is_emergency = is_emergency;
	}

	public int getIs_image_broadcast() {
		return is_image_broadcast;
	}

	public void setIs_image_broadcast(int is_image_broadcast) {
		this.is_image_broadcast = is_image_broadcast;
	}
}
