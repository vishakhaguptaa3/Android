package com.tnc.model;


public class Message {



    public static String reImageURl;
    public String idSender;
    public String idReceiver;



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String text;
    public long timestamp;
    public String imageUrl;
    public String lat;
    public String lng;



    public String getConvertdate() {
        return convertdate;
    }

    public void setConvertdate(String convertdate) {
        this.convertdate = convertdate;
    }

    public String convertdate;
//    public Message(String idSender, String idReceiver,String text) {
//        this.idReceiver = idSender;
//        this.idReceiver = idReceiver;
//        this.text = text;
//
//        // Initialize to current time
//        timestamp = new Date().getTime();
//    }
//
public static String getReImageURl() {
    return reImageURl;
}

    public static void setReImageURl(String reImageURl) {
        Message.reImageURl = reImageURl;
    }

    public Message(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Message() {

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }




}