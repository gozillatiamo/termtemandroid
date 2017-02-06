package com.worldwidewealth.termtem.model;

import java.util.List;

/**
 * Created by MyNet on 9/11/2559.
 */

public class LoginResponseModel {
    private boolean CASHSERVICE;
    private String AGENTID;
    private String USERID;
    private float BALANCE;
    private List<UserMenuModel> usermenu;

    public float getBALANCE() {
        return BALANCE;
    }

    public void setBALANCE(float BALANCE) {
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

    public List<UserMenuModel> getUsermenu() {
        return usermenu;
    }
}
