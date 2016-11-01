package com.worldwidewealth.wealthcounter;

import android.util.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.cert.Extension;
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

    private static byte[] keyGenerate = new byte[16];

    private static final Charset characterSet = Charset.forName("US-ASCII");


    static public String EncryptData(String strKey, String strData)
    {
        String strResult;
        if (strData.length() > 92160)
        {
            strResult = strData;
            return strResult;
        }

        if (!InitKey(strKey))
        {
            strResult = strData;
            return strResult;
        }
//        strData = String.format("{0,5:00000}" + strData, strData.length());

//        byte[] rbData = new byte[strData.length()];
//        ASCIIEncoding aEnc = new ASCIIEncoding();
//        aEnc.GetBytes(strData, 0, strData.Length, rbData, 0);

        byte[] rbData = strData.getBytes(characterSet);

        try {

            KeySpec keySpec = new DESKeySpec(keyGenerate);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            //IvParameterSpec iv = new IvParameterSpec(org.apache.commons.codec.binary.Hex.decodeHex(plainIV.toCharArray()));
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encoded = cipher.doFinal(rbData);
            return Base64.encodeToString(encoded, Base64.DEFAULT);
        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }

/*        DESCryptoServiceProvider descsp = new DESCryptoServiceProvider();

        ICryptoTransform desEncrypt = descsp.CreateEncryptor(m_Key, m_IV);

        MemoryStream mStream = new MemoryStream(rbData);
        CryptoStream cs = new CryptoStream(mStream, desEncrypt, CryptoStreamMode.Read);
        MemoryStream mOut = new MemoryStream();

        int bytesRead;
        byte[] output = new byte[1024];
        do
        {
            bytesRead = cs.Read(output, 0, 1024);
            if (bytesRead != 0)
                mOut.Write(output, 0, bytesRead);
        } while (bytesRead > 0);

        if (mOut.Length == 0)
            strResult = "";
        else
            strResult = Convert.ToBase64String(mOut.GetBuffer(), 0, (int)mOut.Length);

        return strResult;*/
    }

    static public String DecryptData(String strKey, String strData)
    {
        String strResult;

        if (!InitKey(strKey))
        {
            strResult = "";
            return strResult;
        }

        int nReturn = 0;
        byte[] rbData = Base64.decode(strData.getBytes(characterSet), Base64.DEFAULT);

        try {

            KeySpec keySpec = new DESKeySpec(keyGenerate);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            //IvParameterSpec iv = new IvParameterSpec(org.apache.commons.codec.binary.Hex.decodeHex(plainIV.toCharArray()));
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decoded = cipher.doFinal(rbData);
            return new String(decoded, Charset.forName("UTF8"));
        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }
/*

        DESCryptoServiceProvider descsp = new DESCryptoServiceProvider();
        ICryptoTransform desDecrypt = descsp.CreateDecryptor(m_Key, m_IV);

        MemoryStream mOut = new MemoryStream();
        CryptoStream cs = new CryptoStream(mOut, desDecrypt, CryptoStreamMode.Write);

        byte[] bPlain = new byte[strData.Length];
        try
        {
            bPlain = Convert.FromBase64CharArray(strData.ToCharArray(), 0, strData.Length);
        }
        catch (Exception ex)
        {
            strResult = "";
            return strResult;
        }

        long lRead = 0;
        long lTotal = strData.Length;
*/



//        try
//        {
//            while (lTotal >= lRead)
//            {
//                cs.Write(bPlain, 0, (int)bPlain.Length);
//                lRead = mOut.Length + Convert.ToInt64(Convert.ToUInt32((bPlain.Length / descsp.BlockSize) * descsp.BlockSize));
//            };
//
//            ASCIIEncoding aEnc = new ASCIIEncoding();
//            strResult = aEnc.GetString(mOut.GetBuffer(), 0, (int)mOut.Length);
//
//            String strLen = strResult.Substring(0, 5);
//            int nLen = Convert.ToInt32(strLen);
//            strResult = strResult.Substring(5, nLen);
//            nReturn = (int)mOut.Length;
//
//            return strResult;
//        }
//        catch (Exception ex)
//        {
//            strResult = "";
//
//            return strResult;
//        }
    }

    static private boolean InitKey(String strKey)
    {
        try
        {
//            byte[] bp = new byte[strKey.length()];
//            ASCIIEncoding aEnc = new ASCIIEncoding();
//            aEnc.GetBytes(strKey, 0, strKey.Length, bp, 0);
            byte[] bp = strKey.getBytes(characterSet);

//            SHA1CryptoServiceProvider sha = new SHA1CryptoServiceProvider();
//            byte[] bpHash = sha.ComputeHash(bp);

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[strKey.length()];
            md.update(bp, 0, strKey.length());
            sha1hash = md.digest();

            keyGenerate = sha1hash;
//            int i;
//            for (i = 0; i < 8; i++)
//                m_Key[i] = sha1hash[i];
//
//            for (i = 8; i < 16; i++)
//                m_IV[i - 8] = sha1hash[i];

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
