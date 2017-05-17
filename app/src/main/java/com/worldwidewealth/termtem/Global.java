package com.worldwidewealth.termtem;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by MyNet on 11/10/2559.
 */

public class Global {
    private static Global mGlobal;
    private static final String KEY_GLOBAL = Global.class.getSimpleName();
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static List<UserMenuModel> mUserMenuList;
    private static final String PAGE = "page";
    private static final String DEVICEID = "deviceid";
    private static final String VERSIONCODE = "versioncode";
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
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USERDATA = "userdata";
    private static final String CACHEUSER = "cacheuser";

    public static final String TAG = Global.class.getSimpleName();

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

    public static String getKeyUSERNAME() {
        return USERNAME;
    }

    public static String getKeyPASSWORD() {
        return PASSWORD;
    }

    public static String getKeyUSERDATA() {
        return USERDATA;
    }

    public static int getVERSIONCODE() {
        return mPreferences.getInt(VERSIONCODE, -1);
    }

    public void setVERSIONCODE(int versioncode){
        mEditor.putInt(VERSIONCODE, versioncode);
        mEditor.commit();
    }

    public double getBALANCE() {
        String strBalance = mPreferences.getString(BALANCE, null);
        double balance = (strBalance == null) ? 0:Double.parseDouble(strBalance);
        return balance;
    }

    public void setBALANCE(String balance) {
        mEditor.putString(BALANCE, balance);
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

/*
    public void setUSERID(String userid) {
        if ("".equals(userid)) userid = null;
        mEditor.putString(USERID, userid);
        mEditor.commit();
    }
*/

    public String getAGENTID() {
        return mPreferences.getString(AGENTID, null);
    }

/*
    public void setAGENTID(String agentid) {
        if ("".equals(agentid)) agentid = null;
        mEditor.putString(AGENTID, agentid);
        mEditor.commit();
    }
*/

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

    public String getUSERNAME() {
        return mPreferences.getString(USERNAME, null);
    }

    public String getPASSWORD() {
        return mPreferences.getString(PASSWORD, null);
    }


    public void setUserData(ContentValues values){

        String responseStr = values.getAsString(USERDATA);

        Log.e(TAG, "ResponseLogin: "+responseStr);
        LoginResponseModel loginResponseModel = new Gson().fromJson(responseStr, LoginResponseModel.class);
        mEditor.putString(USERNAME, values.getAsString(USERNAME));
        mEditor.putString(PASSWORD, values.getAsString(PASSWORD));
        mEditor.putString(USERID, loginResponseModel.getUSERID());
        mEditor.putString(AGENTID, loginResponseModel.getAGENTID());
        mEditor.putString(AGENTCODE, loginResponseModel.getAgentCode());
        mEditor.putString(FIRSTNAME, loginResponseModel.getFirstName());
        mEditor.putString(LASTNAME, loginResponseModel.getLastName());
        mEditor.putString(PHONENO, loginResponseModel.getTelNo());
        mEditor.putString(BALANCE, loginResponseModel.getBALANCE());
        mEditor.putString(MSGREAD, loginResponseModel.getMSGREAD());
        mUserMenuList = loginResponseModel.getUsermenu();

        mEditor.commit();
    }

    public void clearUserName(){
        mEditor.putString(USERNAME, null);
        mEditor.putString(PASSWORD, null);
        mEditor.commit();
    }

    public void clearUserData(boolean clearall){

        if (clearall) {
            mEditor.putString(USERNAME, null);
            mEditor.putString(PASSWORD, null);
            mEditor.putString(TXID, null);
        }
        mEditor.putString(USERID, null);
        mEditor.putString(AGENTID, null);
        mEditor.putString(AGENTCODE, null);
        mEditor.putString(FIRSTNAME, null);
        mEditor.putString(LASTNAME, null);
        mEditor.putString(PHONENO, null);
        mEditor.putFloat(BALANCE, 0);
        mEditor.putString(MSGREAD, null);
        mUserMenuList = null;

        mEditor.commit();

    }

    public List<UserMenuModel> getUserMenuList() {
        return mUserMenuList;
    }

    public String[] getCacheUser(){
        Set<String> setUser = mPreferences.getStringSet(CACHEUSER, new HashSet<String>());
        return setUser.toArray(new String[setUser.size()]);
    }

    public void setCacheUser(String username){
        Set<String> setUser = mPreferences.getStringSet(CACHEUSER, new HashSet<String>());

        if (!setUser.contains(username)){
            setUser.add(username);

            mEditor.putStringSet(CACHEUSER, setUser);
            mEditor.commit();
        }

    }

    public void clearAll(){
        mEditor.clear();
        mEditor.commit();
    }
}
