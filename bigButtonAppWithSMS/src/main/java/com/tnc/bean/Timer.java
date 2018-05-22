package com.tnc.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by a3logics on 17/11/17.
 */

public class Timer {

    @SerializedName("timer")
    private String timer;

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

}