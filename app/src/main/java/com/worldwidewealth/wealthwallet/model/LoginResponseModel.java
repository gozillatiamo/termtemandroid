package com.worldwidewealth.wealthwallet.model;

/**
 * Created by MyNet on 9/11/2559.
 */

public class LoginResponseModel {
    private boolean CASHSERVICE;
    private String AGENTID;
    private String USERID;
    private Double BALANCE;

    public Double getBALANCE() {
        return BALANCE;
    }

    public void setBALANCE(Double BALANCE) {
        this.BALANCE = BALANCE;
    }

    public boolean isCASHSERVICE() {
        return CASHSERVICE;
    }

    public void setCASHSERVICE(boolean CASHSERVICE) {
        this.CASHSERVICE = CASHSERVICE;
    }

    public String getAGENTID() {
        return AGENTID;
    }

    public void setAGENTID(String AGENTID) {
        this.AGENTID = AGENTID;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }
}
