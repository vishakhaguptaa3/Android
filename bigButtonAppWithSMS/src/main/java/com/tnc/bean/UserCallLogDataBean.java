package com.tnc.bean;

/**
 * Created by a3logics on 5/12/16.
 */

public class UserCallLogDataBean {

    //private variables
    int _id;
    String _name;
    String _phone;
    String _imei;
    String _date;
    String _type;
    String _duration;
    Boolean _ischeck;

    public Boolean get_ischeck() {
        return _ischeck;
    }

    public void set_ischeck(Boolean _ischeck) {
        this._ischeck = _ischeck;
    }

    // Empty constructor
    public UserCallLogDataBean(){

    }

    // constructor
    public UserCallLogDataBean(int id, String name, String phone,String imei,String type,String duration){
        this._id = id;
        this._name = name;
        this._phone = phone;
        this._imei = imei;

        this._type = type;
        this._duration = duration;

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
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

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_duration() {
        return _duration;
    }

    public void set_duration(String _duration) {
        this._duration = _duration;
    }

}
