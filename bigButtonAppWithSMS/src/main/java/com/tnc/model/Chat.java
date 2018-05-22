package com.tnc.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Author: Kartik Sharma
 * Created on: 9/4/2016 , 12:43 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class Chat  implements Comparable<Chat> {
    public String displayName;
    public String email;
    public String senderId;
    public String receiverUid;
    public String phoneNumber;
    public String text;
    public String timestamp;
    public String photoURL_a;
    public String photoURL_i;
    public String fuid;
    public String deviceToken;
    public String createdOn;
    public String firebaseUId;


    public String getFirebaseUId() {
        return firebaseUId;
    }

    public void setFirebaseUId(String firebaseUId) {
        this.firebaseUId = firebaseUId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoURL_a() {
        return photoURL_a;
    }

    public void setPhotoURL_a(String photoURL_a) {
        this.photoURL_a = photoURL_a;
    }

    public String getPhotoURL_i() {
        return photoURL_i;
    }

    public void setPhotoURL_i(String photoURL_i) {
        this.photoURL_i = photoURL_i;
    }


    public Chat(String sender, String message) {
        this.displayName = sender;
        this.text = message;
    }

    public Chat() {}


        public Chat(String sender, String email, String senderUid, String receiverUid, String timestamp, String imageA, String imageI) {
            this.displayName = sender;
            this.email = email;
            this.senderId = senderUid;
            this.receiverUid = receiverUid;

            this.timestamp = timestamp;
            this.photoURL_a = imageA;
            this.photoURL_i = imageI;
        }
    public Chat(String displayName, String email, String senderUid, String receiverUid, String message, String createdOn, String deviceToken, String firebaseUId) {
        this.displayName = displayName;
        this.email = email;
        this.senderId = senderUid;
        this.receiverUid = receiverUid;
        this.text = message;
        this.createdOn = createdOn;
        this.deviceToken = deviceToken;
        this.firebaseUId = firebaseUId;
    }

    @Override
    public int compareTo(@NonNull Chat o) {
        //System.out.println("this-->"+timestamp+"and object --->"+o.timestamp+"and their equal value--->"+(timestamp == o.getTimeStamp())+","+(timeStamp == o.timeStamp));
        if(this.timestamp == o.getTimestamp()){
            return 0;
        } else if(this.timestamp == o.getTimestamp()) {
            return 1;
        }else {
            return -1;
        }
    }
}