package com.tnc.bean;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ContactDetailsBean 
{
	public ContactDetailsBean() 
	{
	}
	
	String _id="";
	String _name="";
	ArrayList<String> _phone=new ArrayList<String>();
	String _imei="";
	String _date="";
	String _type="";
	String _duration="";
	String _emailid="";
	String Orgination="";
	String imageUri="";
	Bitmap _imgpeople=null;
	boolean isCheck=false;
	ArrayList<String> sType=new ArrayList<String>();

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
	}
	
	/**
	 * @return the _phone
	 */
	public ArrayList<String> get_phone() {
		return _phone;
	}
	
	
	/**
	 * @param _phone the _phone to set
	 */
	public void set_phone(ArrayList<String> _phone) {
		this._phone = _phone;
	}	
	
	public String get_imei() {
		return _imei;
	}
	public void set_imei(String _imei) {
		this._imei = _imei;
	}
	public String get_date() {
		return _date;
	}
	public void set_date(String _date) {
		this._date = _date;
	}
	
	public String get_duration() {
		return _duration;
	}
	public void set_duration(String _duration) {
		this._duration = _duration;
	}
	public String get_emailid() {
		return _emailid;
	}
	public void set_emailid(String _emailid) {
		this._emailid = _emailid;
	}
	public String getOrgination() {
		return Orgination;
	}
	public void setOrgination(String orgination) {
		Orgination = orgination;
	}
	public Bitmap get_imgpeople() {
		return _imgpeople;
	}
	public void set_imgpeople(Bitmap _imgpeople) {
		this._imgpeople = _imgpeople;
	}
	/**
	 * @return the isCheck
	 */
	public boolean isCheck() {
		return isCheck;
	}
	/**
	 * @param isCheck the isCheck to set
	 */
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	/**
	 * @return the sType
	 */
	public ArrayList<String> getsType() {
		return sType;
	}
	/**
	 * @param sType the sType to set
	 */
	public void setsType(ArrayList<String> sType) {
		this.sType = sType;
	}
	/**
	 * @return the _type
	 */
	public String get_type() {
		return _type;
	}
	/**
	 * @param _type the _type to set
	 */
	public void set_type(String _type) {
		this._type = _type;
	}
	/**
	 * @return the imageUri
	 */
	public String getImageUri() {
		return imageUri;
	}
	/**
	 * @param imageUri the imageUri to set
	 */
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	
}
