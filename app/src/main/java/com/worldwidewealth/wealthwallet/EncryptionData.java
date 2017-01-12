package com.worldwidewealth.wealthwallet;

import android.content.ContentValues;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.until.Until;

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
        Log.e(TAG, "strData: " + strData);
        Log.e(TAG, "Key: " + key);
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
            Log.e(TAG, "encoded: " + convertPlus);
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
            Log.e(TAG, "First strKeyBase64: " + strKey);

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(strKey.getBytes(characterSet), 0, strKey.length());
            byte[] sha512hash = md.digest();
            Log.e(TAG, "SHA-1 size: " + sha512hash.length);
            for (byte bytestr:sha512hash){
                Log.e(TAG, "byte: "+ bytestr);
            }
            m_Key = Arrays.copyOf(sha512hash, 8);
            Log.e(TAG, m_Key.toString());
            for (byte bytestr:m_Key){
                Log.e(TAG, "byte m_Key: "+ bytestr);
            }
            m_IV = Arrays.copyOfRange(sha512hash, 8, 16);
            Log.e(TAG, m_IV.toString());
            for (byte bytestr:m_IV){
                Log.e(TAG, "byte m_Key: "+ bytestr);
            }

            String encodeBase64 = Base64.encodeToString(sha512hash, Base64.DEFAULT);
            Log.e(TAG, "EncodeFormSHA-1: "+ encodeBase64);


            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static ContentValues getModel(ResponseBody response){
        ContentValues resultvalues = new ContentValues();
        ResponseModel responseModel = null;
        Gson gson = new Gson();
        String strRespone = null;

        try {
            strRespone = response.string();
            responseModel = gson.fromJson(strRespone, ResponseModel.class);
        }
        catch (JsonSyntaxException e){}
        catch (IOException e) {}

        if (responseModel != null){
            resultvalues.put(ASRESPONSEMODEL, true);
            resultvalues.put(STRMODEL, strRespone);
        } else {
            String converted = Until.ConvertJsonEncode(strRespone);
            String responDecode = Until.decode(converted);
            resultvalues.put(ASRESPONSEMODEL, false);
            resultvalues.put(STRMODEL, responDecode);

        }


        return resultvalues;
    }
}
