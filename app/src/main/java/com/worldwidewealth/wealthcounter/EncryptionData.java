package com.worldwidewealth.wealthcounter;

import android.util.Base64;
import android.util.Log;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by MyNet on 1/11/2559.
 */

public class EncryptionData {

    private static final String key = "WWW$2oi6-0o1www";

    private static byte[] m_Key = new byte[8];
    private static byte[] m_IV = new byte[8];

    private static final Charset characterSet = Charset.forName("US-ASCII");


    static public String EncryptData(String strData)
    {
        String strResult;
        if (strData.length() > 92160)
        {
            strResult = strData;
            return strResult;
        }

        if (!InitKey(key))
        {
            strResult = strData;
            return strResult;
        }

        byte[] rbData = strData.getBytes(characterSet);

        try {

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encoded = cipher.doFinal(rbData);

            String strEncoded = Base64.encodeToString(encoded, Base64.DEFAULT);
            Log.e("Encoded", strEncoded);
            return strEncoded;
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Encoded", "null");
            return  null;
        }

    }

    static public String DecryptData(String strData)
    {
        String strResult;

        if (!InitKey(key))
        {
            strResult = "";
            return strResult;
        }

        int nReturn = 0;
        byte[] rbData = Base64.decode(strData.getBytes(characterSet), Base64.DEFAULT);

        try {

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            //IvParameterSpec iv = new IvParameterSpec(org.apache.commons.codec.binary.Hex.decodeHex(plainIV.toCharArray()));
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decoded = cipher.doFinal(rbData);
            String strDecode = new String(decoded, Charset.forName("UTF8"));
            Log.e("Decoded", strDecode);
            return strDecode;
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Decoded", "null");
            return  null;
        }
    }

    static private boolean InitKey(String strKey)
    {
        try
        {
            byte[] bp = strKey.getBytes(characterSet);

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[strKey.length()];
            md.update(bp, 0, strKey.length());
            sha1hash = md.digest();

            int i;
            for (i = 0; i < 8; i++)
                m_Key[i] = sha1hash[i];

            for (i = 8; i < 16; i++)
                m_IV[i - 8] = sha1hash[i];

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
