package com.worldwidewealth.termtem;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
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
    private static final String LAST_USER_LOGIN = "lastuserlogin";

    private static final String CARRIER = "carrier";
    private static final String AMT = "amt";
    private static final String SUBMIT_PHONENO = "submitphoneno";
    private static final String TRANID = "tranid";
    private static final String AGENTIDCASHIN = "agentidcashid";
    private static final String BUTTONID = "buttonid";
    private static final String SUBMIT_PG_ID = "submitpgid";
    private static final String SUBMIT_PG_NAME = "submitpgname";
    private static final String SUBMIT_DEVICEID = "submitdeviceid";
    private static final String SUBMIT_TXID = "submittxid";
    private static final String SUBMIT_AGENTID = "submitagentid";
    private static final String SUBMIT_USERID = "submituserid";

    private static final String SUBMIT_ACTION = "submitaction";
    private static final String SUBMIT_STATUS = "submitstatus";
    private static final String SUBMIT_IS_FAV = "submitisfav";




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
//        if ("".equals(getUSERID())) deviceid = null;
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


    public void  setUserData(ContentValues values){

        String responseStr = values.getAsString(USERDATA);

        Log.e(TAG, "ResponseLogin: "+responseStr);
        LoginResponseModel loginResponseModel = new Gson().fromJson(responseStr, LoginResponseModel.class);
        mEditor.putString(USERNAME, values.getAsString(USERNAME));
        mEditor.putString(PASSWORD, values.getAsString(PASSWORD));
        mEditor.putString(USERID, EncryptionData.DecryptData(loginResponseModel.getUSERID(), Global.getInstance().getTXID()));
        mEditor.putString(AGENTID, EncryptionData.DecryptData(loginResponseModel.getAGENTID(), Global.getInstance().getTXID()));
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
//        mEditor.putString(SERVICE_TRAN_ID, null);
        mEditor.commit();
    }

    public void clearUserData(){

/*
        if (clearall) {
            clearUserName();
            mEditor.putString(TXID, null);

        }
*/

        clearUserName();
        mEditor.putString(TXID, null);

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

//        if (!setUser.contains(username)){
            setUser.add(username);
            mEditor.putStringSet(CACHEUSER, setUser);
            mEditor.commit();

        setLastUserLogin(username);
//        }

    }

    public void setLastUserLogin(String userLogin){
        mEditor.putString(LAST_USER_LOGIN, userLogin);
        mEditor.commit();
    }

    public String getLastUserLogin(){
        return mPreferences.getString(LAST_USER_LOGIN, null);
    }

    public void clearAll(){
        mEditor.clear();
        mEditor.commit();
    }

    public void setLastSubmit(RequestModel model, boolean isFav){
        if (model == null){
            mEditor.putString(SUBMIT_ACTION, null);
            mEditor.putString(SUBMIT_AGENTID, null);
            mEditor.putString(SUBMIT_DEVICEID, null);
            mEditor.putString(SUBMIT_TXID, null);
            mEditor.putString(SUBMIT_USERID, null);
            mEditor.putString(BUTTONID, null);
            mEditor.putString(AGENTIDCASHIN, null);
            mEditor.putString(TRANID, null);
            mEditor.putString(SUBMIT_PHONENO, null);
            mEditor.putString(AMT, null);
            mEditor.putString(CARRIER, null);
            mEditor.putString(SUBMIT_PG_ID, null);
            mEditor.putString(SUBMIT_PG_NAME, null);
            mEditor.putBoolean(SUBMIT_IS_FAV, false);
            mEditor.commit();
            setSubmitStatus(null);
            return;
        }


        SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel)model.getData();

        mEditor.putString(SUBMIT_ACTION, model.getAction());
        mEditor.putString(SUBMIT_AGENTID, submitTopupRequestModel.getAGENTID());
        mEditor.putString(SUBMIT_DEVICEID, submitTopupRequestModel.getDEVICEID());
        mEditor.putString(SUBMIT_TXID, submitTopupRequestModel.getTXID());
        mEditor.putString(SUBMIT_USERID, submitTopupRequestModel.getUSERID());
        mEditor.putString(BUTTONID, submitTopupRequestModel.getBUTTONID());
        mEditor.putString(AGENTIDCASHIN, submitTopupRequestModel.getAGENTIDCASHIN());
        mEditor.putString(TRANID, submitTopupRequestModel.getTRANID());
        mEditor.putString(SUBMIT_PHONENO, submitTopupRequestModel.getPHONENO());
        mEditor.putString(AMT, submitTopupRequestModel.getAMT());
        mEditor.putString(CARRIER, submitTopupRequestModel.getCARRIER());
        mEditor.putString(SUBMIT_PG_ID, submitTopupRequestModel.getPGID());
        mEditor.putString(SUBMIT_PG_NAME, submitTopupRequestModel.getPGNAME());
        mEditor.putBoolean(SUBMIT_IS_FAV, isFav);
        mEditor.commit();
    }

    public void setSubmitStatus(String status){
        mEditor.putString(SUBMIT_STATUS, status);
        mEditor.commit();

    }

    public boolean getSubmitStatus(){
        String status = mPreferences.getString(SUBMIT_STATUS, null);
        if (status == null || !status.equals("Success")) return false;

        return true;
    }

    public boolean getSubmitIsFav(){
        return mPreferences.getBoolean(SUBMIT_IS_FAV, false);
    }

    public RequestModel getLastSubmit(){

        if (mPreferences.getString(TRANID, null) == null) return null;

        SubmitTopupRequestModel submitTopupRequestModel = new SubmitTopupRequestModel(
                mPreferences.getString(AMT, null),
                mPreferences.getString(CARRIER, null),
                mPreferences.getString(SUBMIT_PHONENO, null),
                mPreferences.getString(TRANID, null),
                mPreferences.getString(BUTTONID, null),
                mPreferences.getString(AGENTIDCASHIN, null),
                mPreferences.getString(SUBMIT_PG_ID, null),
                mPreferences.getString(SUBMIT_PG_NAME, null)
        );
        submitTopupRequestModel.setAGENTID(mPreferences.getString(SUBMIT_AGENTID, null));
        submitTopupRequestModel.setDEVICEID(mPreferences.getString(SUBMIT_DEVICEID, null));
        submitTopupRequestModel.setTXID(mPreferences.getString(SUBMIT_TXID, null));
        submitTopupRequestModel.setUSERID(mPreferences.getString(SUBMIT_USERID, null));

        return new RequestModel(mPreferences.getString(SUBMIT_ACTION, null), submitTopupRequestModel);
    }

    public String getLastTranId(){
        return mPreferences.getString(TRANID, null);
    }

    public String getLastSubmitAction(){
        return mPreferences.getString(SUBMIT_ACTION, null);
    }

    public String getLastSubmitPhoneNo(){
        return mPreferences.getString(SUBMIT_PHONENO, null);
    }

    public String getLastSubmitCarrier(){
        return mPreferences.getString(CARRIER, null);
    }

    public String getLastSubmitAmt(){
        return mPreferences.getString(AMT, null);
    }


/*
    public String getProcessSubmit(){
        return mPreferences.getString(SERVICE_TRAN_ID, null);
    }

    public String getProcessType(){
        return mPreferences.getString(SERVICE_TOPUP_TYPE, null);
    }
*/


}
