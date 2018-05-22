package com.tnc.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by a3logics on 2/4/18.
 */

@IgnoreExtraProperties
public class ChatInfo {

    public String chatType;
    public String email;
    public String name;

    public ChatInfo(String chatType, String email, String name) {
        this.chatType = chatType;
        this.email = email;
        this.name = name;
    }

    public ChatInfo(String chatType) {
        this.chatType = chatType;
    }

    public ChatInfo() {
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
