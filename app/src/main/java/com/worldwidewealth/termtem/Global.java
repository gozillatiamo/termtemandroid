package com.worldwidewealth.termtem;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MyNet on 11/10/2559.
 */

public class Global {
    private static Global mGlobal;
    private static final String KEY_GLOBAL = Global.class.getSimpleName();
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static final String PAGE = "page";
    private static final String DEVICEID = "deviceid";
    private static final String USERID = "userid";
    private static final String AGENTID = "agentid";
    private static final String TOKEN = "token";
    private static final String TXID = "txid";
    private static final String BALANCE = "balance";
    private static final String MSGREAD = "msg_read";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String PHONENO = "phoneno";
    private static final String AGENTCODE = "agentcode";

    public static Global getInstance(){
        if (mGlobal == null){
            mGlobal = new Global();
            mPreferences = MyApplication.getContext().getSharedPreferences(KEY_GLOBAL, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
        }

        return mGlobal;
    }

    public Global() {
    }

    public float getBALANCE() {
        return mPreferences.getFloat(BALANCE, 0);
    }

    public void setBALANCE(float balance) {
        mEditor.putFloat(BALANCE, balance);
        mEditor.commit();
    }

    public String getMSGREAD() {
        return mPreferences.getString(MSGREAD, "0");
    }

    public void setMSGREAD(String msgread) {
        mEditor.putString(MSGREAD, msgread);
        mEditor.commit();
    }

    public String getUSERID() {
        return mPreferences.getString(USERID, null);
    }

    public void setUSERID(String userid) {
        if ("".equals(userid)) userid = null;
        mEditor.putString(USERID, userid);
        mEditor.commit();
    }

    public String getAGENTID() {
        return mPreferences.getString(AGENTID, null);
    }

    public void setAGENTID(String agentid) {
        if ("".equals(agentid)) agentid = null;
        mEditor.putString(AGENTID, agentid);
        mEditor.commit();
    }

    public String getTXID() {
        return mPreferences.getString(TXID, null);
    }

    public void setTXID(String txid) {
        if ("".equals(txid)) txid = null;
        mEditor.putString(TXID, txid);
        mEditor.commit();
    }

    public String getDEVICEID() {
        return mPreferences.getString(DEVICEID, null);
    }

    public void setDEVICEID(String deviceid) {
        if ("".equals(getUSERID())) deviceid = null;
        mEditor.putString(DEVICEID, deviceid);
        mEditor.commit();
    }

    public String getTOKEN() {
        return mPreferences.getString(TOKEN, null);
    }

    public void setTOKEN(String token) {
        if ("".equals(token)) token = null;
        mEditor.putString(TOKEN, token);
        mEditor.commit();
    }

    public String getAGENTCODE() {
        return mPreferences.getString(AGENTCODE, null);
    }

    public String getPHONENO() {
        return mPreferences.getString(PHONENO, null);
    }

    public String getFIRSTNAME() {
        return mPreferences.getString(FIRSTNAME, null);
    }

    public String getLASTNAME() {
        return mPreferences.getString(LASTNAME, null);
    }

    public void setAGENTCODE(String agentcode) {
        if ("".equals(agentcode)) agentcode = null;
        mEditor.putString(AGENTCODE, agentcode);
        mEditor.commit();
    }

    public void setPHONENO(String phoneno) {
        if ("".equals(phoneno)) phoneno = null;
        mEditor.putString(PHONENO, phoneno);
        mEditor.commit();
    }

    public void setFIRSTNAME(String firstname) {
        if ("".equals(firstname)) firstname = null;
        mEditor.putString(FIRSTNAME, firstname);
        mEditor.commit();
    }

    public void setLASTNAME(String lastname) {
        if ("".equals(lastname)) lastname = null;
        mEditor.putString(LASTNAME, lastname);
        mEditor.commit();
    }


}
