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
    private String MSGREAD;

    public String getMSGREAD() {
        return MSGREAD;
    }

    private List<UserMenuModel> usermenu;

    public float getBALANCE() {
        return BALANCE;
    }

    public boolean isCASHSERVICE() {
        return CASHSERVICE;
    }

    public String getAGENTID() {
        return AGENTID;
    }

    public String getUSERID() {
        return USERID;
    }

    public List<UserMenuModel> getUsermenu() {
        return usermenu;
    }
}
