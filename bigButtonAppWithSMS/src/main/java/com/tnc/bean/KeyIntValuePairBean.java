package com.tnc.bean;

/**
 * Created by a3logics on 22/12/16.
 */

public class KeyIntValuePairBean {

    int position;

    String value;

    public KeyIntValuePairBean() {
    }

    public KeyIntValuePairBean(int position, String value) {
        this.position = position;
        this.value = value;
    }

    public KeyIntValuePairBean(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
