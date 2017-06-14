package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MyNet on 1/11/2559.
 */

public class EncryptionData {

    public static final String ASRESPONSEMODEL = "asresponsemodel";
    public static final String STRMODEL = "strmodel";
    public static final String TAG = EncryptionData.class.getSimpleName();



    private static byte[] m_Key;
    private static byte[] m_IV;

    private static final Charset characterSet = Charset.forName("ASCII");

    static public String EncryptData(String strData, String key) {

        if (strData == null || strData.equals("")) return "";

        String strResult = strData + ";" ;
//        if (strData.length() > 92160)
//        {
//            strResult = strData;
//            return strResult;
//        }


//        byte[] bb = key.getBytes(characterSet);
//        key = Base64.encodeToString(bb, Base64.DEFAULT);

        byte[] bb = key.getBytes(characterSet);
        String strKeyBase64 = Base64.encodeToString(bb, Base64.NO_WRAP);
//

        if (!InitKey(strKeyBase64))
        {
            strResult = strData;
            return strResult;
        }

        byte[] rbData = strResult.getBytes(characterSet);

        try {

//            SecretKey secretKey = new SecretKeySpec(m_Key, "DESede");

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encoded = cipher.doFinal(rbData);

            String strEncoded = Base64.encodeToString(encoded, Base64.NO_WRAP);
            String convertPlus = strEncoded.replace("+", "%2B");
            return convertPlus;

        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }

    static public String DecryptData(String strData, String key) {
        if (strData == null || strData.equals("")) return "";

        String strResult = strData.replace("%2B", "+");

        byte[] bb = key.getBytes(characterSet);
        String strKeyBase64 = Base64.encodeToString(bb, Base64.NO_WRAP);

        if (!InitKey(strKeyBase64))
        {
            strResult = "";
            return strResult;
        }

        byte[] rbData = Base64.decode(strResult.getBytes(characterSet), Base64.NO_WRAP);

        try {

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decoded = cipher.doFinal(rbData);
            String strDecode = new String(decoded, Charset.forName("UTF8"));
            String[] convertDecode = strDecode.split(";");
            return convertDecode[0];
        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    static private boolean InitKey(String strKey) {
        try
        {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(strKey.getBytes(characterSet), 0, strKey.length());
            byte[] sha1hash = md.digest();
            m_Key = Arrays.copyOf(sha1hash, 8);
            m_IV = Arrays.copyOfRange(sha1hash, 8, 16);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private static int retry = 0;
    public static final Object getModel(Context context, final Call call, ResponseBody response, final Callback callback){

        ResponseModel responseModel;
        RequestModel requestModel = null;
        Gson gson = new Gson();
        String msg;
        String strRespone = null;
        String strRequest = Util.convertToStringRequest(call.request().body());
        if (strRequest != null){
            requestModel = new Gson().fromJson(strRequest, RequestModel.class);
        }
        try {
            strRespone = response.string();
            responseModel = gson.fromJson(strRespone, ResponseModel.class);
            Log.e(TAG, "Response: "+ strRespone);
            if (responseModel == null) return null;

            switch (requestModel.getAction()){
                case APIServices.ACTIONSUBMITTOPUP:
                case APIServices.ACTION_GET_OTP_EPIN:
                case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                case APIServices.ACTION_SUBMIT_VAS:
                    Global.getInstance().setSubmitStatus(responseModel.getMsg());
                    break;
            }

            if (responseModel.getStatus() != APIServices.SUCCESS) {
                DialogCounterAlert.DialogProgress.dismiss();
                try {
                    switch (requestModel.getAction()) {

                        case APIServices.ACTIONGETOTP:
                        case APIServices.ACTIONSUBMITTOPUP:
                        case APIServices.ACTION_GET_OTP_EPIN:
                        case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                        case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                        case APIServices.ACTION_SUBMIT_VAS:

                            if (responseModel.getMsg().equals(APIServices.MSG_FAIL)) {
                                Global.getInstance().setLastSubmit(null);
                            }

                            if (responseModel.getMsg().contains(APIServices.MSG_WAIT)) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        call.cancel();
                                        APIHelper.enqueueWithRetry(call.clone(), callback);
                                    }
                                }, 3000);

                                return APIServices.MSG_WAIT;
                            }

                            msg = MyApplication.getContext().getString(R.string.alert_topup_fail);
                            new DialogCounterAlert(context, context.getString(R.string.error), msg, null);

                            return null;

                        case APIServices.ACTIONLOGIN:
                        case APIServices.ACTIONNOTIPAY:
                            return responseModel;

                        case APIServices.ACTIONLOGOUT:
                        case APIServices.ACTIONGETBALANCE:
                            return null;

                        default:
                            if (MyApplication.canUseLeaving(MyApplication.LeavingOrEntering.currentActivity) &&
                                    Global.getInstance().getAGENTID() == null) {
                                Util.backToSignIn((Activity) context);
                                return null;
                            }
                            new ErrorNetworkThrowable(null).networkError(context,
                                    responseModel.getMsg(), call, callback, true);
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                    return null;
                }
            } else{
                return responseModel;
            }

        } catch (JsonSyntaxException e){
//            String converted = Util.ConvertJsonEncode(strRespone);
            String responDecode = Util.decode(strRespone);
            Log.e(TAG, "ResponDecode: "+ responDecode);
            return responDecode;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
