package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a3logics on 20/11/17.
 */

public class NotificationResponseTimer {

    @SerializedName("timer")
    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
