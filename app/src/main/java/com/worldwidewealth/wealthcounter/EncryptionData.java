package com.worldwidewealth.wealthcounter;

import android.util.Base64;
import android.util.Log;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by MyNet on 1/11/2559.
 */

public class EncryptionData {

    private static final String DEFAULTKEY = "WWW$2oi6-0o1www";
    private static final String OTPKEY = "OTP$2oi6-0o1otp";

    public static final String OTP = "otp";

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

        Log.e("strKey", key);

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
            Log.e("ivSize", iv.getIV().length+"");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encoded = cipher.doFinal(rbData);

            String strEncoded = Base64.encodeToString(encoded, Base64.NO_WRAP);
            String convertPlus = strEncoded.replace("+", "%2B");
            Log.e("Encoded", strEncoded);
            Log.e("EncodedSize", encoded.length+"");
            Log.e("convertPlus", convertPlus);
            return convertPlus;

        } catch (Exception e){
            e.printStackTrace();
            Log.e("Encoded", "null");
            return  null;
        }

    }

    static public String DecryptData(String strData, String key) {
        String strResult = strData.replace("%2B", "+");

        byte[] bb = key.getBytes(characterSet);
        String strKeyBase64 = Base64.encodeToString(bb, Base64.NO_WRAP);

        if (!InitKey(strKeyBase64))
        {
            strResult = "";
            return strResult;
        }

        int nReturn = 0;
        byte[] rbData = Base64.decode(strResult.getBytes(characterSet), Base64.NO_WRAP);

        try {

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            //IvParameterSpec iv = new IvParameterSpec(org.apache.commons.codec.binary.Hex.decodeHex(plainIV.toCharArray()));
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decoded = cipher.doFinal(rbData);
            String strDecode = new String(decoded, Charset.forName("UTF8"));
            String convertDecode = strDecode.concat(";");
            Log.e("Decoded", strDecode);
            Log.e("convertDecode", convertDecode);
            return strDecode;
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Decoded", "null");
            return  null;
        }
    }

    static private boolean InitKey(String strKey) {
        try
        {
/*
            m_Key = new byte[24];
            m_IV = new byte[24];
*/

/*
            char[] strArrayBase64 = strKeyBase64.toCharArray();
            Log.e("strKeyBase64Size", strArrayBase64.length+"");
            for (int i = 0; i < strArrayBase64.length; i++){

                Log.e("strKeyBase64"+i, strArrayBase64[i]+"");
                if (i == 12){
                    Log.e("13", "13");
                }
            }
*/

//            byte[] bp = strKey.getBytes(characterSet);
//            Log.e("BPSize", bp.length+"");
//            for (int i = 0; i < bp.length; i++){
//                Log.e("BP"+i, bp[i]+"");
//            }

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(strKey.getBytes(characterSet), 0, strKey.length());
            byte[] sha512hash = md.digest();
/*
            String sha512 = Base64.encodeToString(sha512hash, Base64.NO_WRAP);
            sha512hash = sha512.getBytes(characterSet);
*/
            Log.e("sha1hash", Base64.encodeToString(sha512hash, Base64.NO_WRAP));
            Log.e("sha1", sha512hash.length+"");
            for (int i = 0; i < sha512hash.length; i++){
                Log.e("sha512_"+i, sha512hash[i]+"");
            }

            m_Key = Arrays.copyOf(sha512hash, 8);
            m_IV = Arrays.copyOfRange(sha512hash, 8, 16);

            for (int i = 0; i < m_Key.length; i++)
                Log.e("m_Key_"+i, m_Key[i]+"");

            for (int i = 0; i < m_IV.length; i++)
                Log.e("m_IV_"+i, m_IV[i]+"");



/*
            int i;
            for (i = 0; i < 24; i++)
                m_Key[i] = sha512hash[i];

            for (i = 24; i < 48; i++)
                m_IV[i - 24] = sha512hash[i];

            Log.e("m_Key", Base64.encodeToString(m_Key, Base64.DEFAULT));
            Log.e("m_IV", Base64.encodeToString(m_IV, Base64.DEFAULT));
*/
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
