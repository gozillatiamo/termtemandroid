package com.worldwidewealth.termtem;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.until.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.until.Until;

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
//        String key = null;
//        if (type == null){
//            key = DEFAULTKEY;
//        } else {
//            key = OTPKEY;
//        }

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
        if (strData == null) return null;
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


    public static final Object getModel(Context context, Call call, ResponseBody response, Callback callback){

        ResponseModel responseModel = null;
        Gson gson = new Gson();
        String msg = null;
        String strRespone = null;
        try {
            strRespone = response.string();
            responseModel = gson.fromJson(strRespone, ResponseModel.class);

            if (responseModel.getStatus() != APIServices.SUCCESS) {
                DialogCounterAlert.DialogProgress.dismiss();
                if (context instanceof ActivityTopup) {
                    Fragment currentFragment = ((AppCompatActivity) context).getSupportFragmentManager()
                            .findFragmentById(R.id.container_topup)
                            .getChildFragmentManager()
                            .findFragmentById(R.id.container_topup_package);
                    if (currentFragment instanceof FragmentTopupPreview){
                        msg = context.getString(R.string.alert_topup_fail);
                    }
/*
                    Log.e(TAG, "Fragmet: "+currentFragment.toString());
                    if (currentFragment instanceof FragmentTopupPackage){
                        currentFragment = currentFragment.getChildFragmentManager().findFragmentById(R.id.container_topup_package);
                        Log.e(TAG, "Fragmet: "+currentFragment.toString());

                    }
*/
                }

                new ErrorNetworkThrowable(null).networkError(context,
                        msg+"\n"+responseModel.getMsg(), call, callback, true);
            } else{
                return responseModel;
            }

        } catch (JsonSyntaxException e){
//            String converted = Until.ConvertJsonEncode(strRespone);
            String responDecode = Until.decode(strRespone);
            return responDecode;

        } catch (IOException e) {}

        return null;
    }
}
