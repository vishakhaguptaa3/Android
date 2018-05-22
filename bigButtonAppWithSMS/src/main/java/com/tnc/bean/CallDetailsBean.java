package com.tnc.bean;

/**
 * Created by a3logics on 2/12/16.
 */

public class CallDetailsBean {

    String CallName;

    String Prefix;

    String CountryCode;

    String CallingNumber;

    String CallTime;
    //1 - incoming, 2 - outgoing, 3 - missed
    int callType;

    int Status;

    int callCount;

    boolean isEmergencyCall;

    boolean isTncUser = false;

    public CallDetailsBean(){

    }

    public CallDetailsBean(String callName, String prefix, String countryCode, String callingNumber,
                           String callTime, int callType, int Status, int callCount,boolean isEmergencyCall,
                           boolean isTncUser) {
        this.CallName = callName;
        this.Prefix = prefix;
        this.CountryCode = countryCode;
        this.CallingNumber = callingNumber;
        this.CallTime = callTime;
        this.callType = callType;
        this.Status = Status;
        this.callCount = callCount;
        this.isEmergencyCall = isEmergencyCall;
        this.isTncUser = isTncUser;
    }

    public String getCallName() {
        return CallName;
    }

    public void setCallName(String callName) {
        CallName = callName;
    }

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String prefix) {
        Prefix = prefix;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCallingNumber() {
        return CallingNumber;
    }

    public void setCallingNumber(String callingNumber) {
        CallingNumber = callingNumber;
    }

    public String getCallTime() {
        return CallTime;
    }

    public void setCallTime(String callTime) {
        CallTime = callTime;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public boolean isEmergencyCall() {
        return isEmergencyCall;
    }

    public void setEmergencyCall(boolean emergencyCall) {
        isEmergencyCall = emergencyCall;
    }

    public boolean isTncUser() {
        return isTncUser;
    }

    public void setTncUser(boolean tncUser) {
        isTncUser = tncUser;
    }
}
