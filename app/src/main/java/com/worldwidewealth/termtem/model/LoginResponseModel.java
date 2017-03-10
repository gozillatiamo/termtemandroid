package com.worldwidewealth.termtem.model;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by MyNet on 9/11/2559.
 */

public class LoginResponseModel {
    private boolean CASHSERVICE;
    private String AGENTID;
    private String USERID;
    private String BALANCE;
    private String MSGREAD;
    private String FirstName;
    private String LastName;
    private String TelNo;
    private String AgentCode;

    public String getMSGREAD() {
        return MSGREAD;
    }

    private List<UserMenuModel> usermenu;

    public String getBALANCE() {
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

    public String getAgentCode() {
        return AgentCode;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getTelNo() {
        return TelNo;
    }
}
