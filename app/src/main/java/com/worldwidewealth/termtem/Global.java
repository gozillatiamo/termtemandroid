package com.worldwidewealth.termtem;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.util.ParcelableUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by MyNet on 11/10/2559.
 */

public class Global {
    private static Global mGlobal;
    public static final String KEY_GLOBAL = Global.class.getSimpleName();
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

    private static final String SUBMIT_ACTION = "submitaction";
    private static final String SUBMIT_STATUS = "submitstatus";
    private static final String SUBMIT_IS_FAV = "submitisfav";

    private static final String AGENTIDCASHIN = "agentidcashid";
    private static final String BUTTONID = "buttonid";
    private static final String SUBMIT_PG_ID = "submitpgid";
    private static final String SUBMIT_PG_NAME = "submitpgname";
    private static final String SUBMIT_DEVICEID = "submitdeviceid";
    private static final String SUBMIT_TXID = "submittxid";
    private static final String SUBMIT_AGENTID = "submitagentid";
    private static final String SUBMIT_USERID = "submituserid";
    private static final String SUBMIT_BILLSERVICE = "submitbillservice";

    private static final String LAST_SUBMIT = "lastsubmit";




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


    public String getAGENTID() {
        return mPreferences.getString(AGENTID, null);
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
            clearLastSubmit();
            return;
        }

        byte[] bytesModel = ParcelableUtil.marshall(model);
        String encryptionSubmit = EncryptionData.EncryptWithBytes(bytesModel, getDEVICEID());
        mEditor.putString(LAST_SUBMIT, encryptionSubmit);
        mEditor.putBoolean(SUBMIT_IS_FAV, isFav);
        mEditor.commit();

    }

    public void setLastBillService(LoadBillServiceResponse billService){

        byte[] bytesModel = ParcelableUtil.marshall(billService);
        String encryptionSubmit = EncryptionData.EncryptWithBytes(bytesModel, getDEVICEID());
        mEditor.putString(SUBMIT_BILLSERVICE, encryptionSubmit);
        mEditor.commit();

    }

    public LoadBillServiceResponse getLastBillService(){
        try {
            byte[] bytesLastSubmit = EncryptionData.DecrptWithByte(mPreferences.getString(SUBMIT_BILLSERVICE, null), getDEVICEID());
            LoadBillServiceResponse billServiceResponse = ParcelableUtil.unmarshall(bytesLastSubmit, LoadBillServiceResponse.CREATOR);
            return billServiceResponse;

        } catch (NullPointerException e){
            clearLastSubmit();
            return null;
        }

    }


    public void setSubmitStatus(String status){
        mEditor.putString(SUBMIT_STATUS, status);
        mEditor.commit();

    }

    public void clearLastSubmit(){
        mEditor.putString(SUBMIT_ACTION, null);
        mEditor.putString(TRANID, null);
        mEditor.putString(SUBMIT_PHONENO, null);
        mEditor.putString(AMT, null);
        mEditor.putString(CARRIER, null);
        mEditor.putBoolean(SUBMIT_IS_FAV, false);
        mEditor.putString(LAST_SUBMIT, null);

        mEditor.putString(SUBMIT_PG_ID, null);
        mEditor.putString(SUBMIT_PG_NAME, null);
        mEditor.putString(SUBMIT_AGENTID, null);
        mEditor.putString(SUBMIT_DEVICEID, null);
        mEditor.putString(SUBMIT_TXID, null);
        mEditor.putString(SUBMIT_USERID, null);
        mEditor.putString(BUTTONID, null);
        mEditor.putString(AGENTIDCASHIN, null);
        mEditor.putString(SUBMIT_BILLSERVICE, null);


        mEditor.putString(SUBMIT_STATUS, null);

        mEditor.commit();


    }
    public boolean getSubmitStatus(){
        String status = mPreferences.getString(SUBMIT_STATUS, null);
        if (status == null || !status.equals("Success")) return false;

        return true;
    }

    public String getStrSubmitStatus(){
        String status = mPreferences.getString(SUBMIT_STATUS, null);
        return status;
    }

    public boolean getSubmitIsFav(){
        return mPreferences.getBoolean(SUBMIT_IS_FAV, false);
    }

    public void setSubmitIsFav(boolean isFav){

        mEditor.putBoolean(SUBMIT_IS_FAV, isFav);
        mEditor.commit();
    }

    public boolean hasSubmit(){
        return mPreferences.getString(LAST_SUBMIT, null) != null;
    }

    public RequestModel getLastSubmit(){

        if (!hasSubmit()) return null;

        byte[] bytesLastSubmit = EncryptionData.DecrptWithByte(mPreferences.getString(LAST_SUBMIT, null), getDEVICEID());
        RequestModel requestModel = ParcelableUtil.unmarshall(bytesLastSubmit, RequestModel.CREATOR);

        return requestModel;
    }

    public String getLastTranId(){
        return mPreferences.getString(TRANID, null);
    }


}
