package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

/*
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
*/
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
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
import java.io.SyncFailedException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

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

    //EncyptData
    static public String EncryptData(String strData, String key) {

        if (strData == null || strData.equals("")) return "";

        //เอา value มาต่อ ด้วย ;
        String strResult = strData + ";" ;


        //แปลงเป็น Byte
        byte[] rbData = strResult.getBytes(characterSet);

        return EncryptWithBytes(rbData, key);
    }

    static public String DecryptData(String strData, String key) {
        byte[] decoded = DecrptWithByte(strData, key);

        if (decoded == null) return "";

        String strDecode = new String(decoded, Charset.forName("UTF8"));
        String[] convertDecode = strDecode.split(";");
        return convertDecode[0];
    }

    static public String EncryptWithBytes(byte[] bytesData, String key){

        byte[] bb = key.getBytes(characterSet);
        String strKeyBase64 = Base64.encodeToString(bb, Base64.NO_WRAP);

        if (!InitKey(strKeyBase64))
        {
            return "";
        }

        try {

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encoded = cipher.doFinal(bytesData);

            String strEncoded = Base64.encodeToString(encoded, Base64.NO_WRAP);
            String convertPlus = strEncoded.replace("+", "%2B");
            return convertPlus;

        } catch (Exception e){
            Log.e(TAG, "EncryptFail");
            return  "";
        }


    }

    static public byte[] DecrptWithByte(String strData, String key){

        if (strData == null || strData.equals("")) return null;
        if (key == null){
            Util.backToSignIn(MyApplication.LeavingOrEntering.currentActivity);
            return null;
        }

        String strResult = strData.replace("%2B", "+");

        byte[] bb = key.getBytes(characterSet);
        String strKeyBase64 = Base64.encodeToString(bb, Base64.NO_WRAP);

        if (!InitKey(strKeyBase64))
        {
            return null;
        }


        try {
            byte[] rbData = Base64.decode(strResult.getBytes(characterSet), Base64.NO_WRAP);

            KeySpec keySpec = new DESKeySpec(m_Key);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(m_IV);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decoded = cipher.doFinal(rbData);
            return decoded;
        } catch (Exception e){
            Log.e(TAG, "DecryptFail");
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

    public static final Object getModel(final Context context, final Call call, ResponseBody response, final Callback callback){
        Trace mTrace = null;

        final ResponseModel responseModel;
        RequestModel requestModel = null;
        Gson gson = new Gson();
        String msg = null;
        String dialogTitle;
        String strRespone = null;
        //ดึง Model Request เพื่อเซ็คการแสดงผล
        String strRequest = Util.convertToStringRequest(call.request().body());

        //แปลง string json เป็น model gson request
        if (strRequest != null){
            requestModel = new Gson().fromJson(strRequest, RequestModel.class);
            mTrace = MyApplication.mPerformance.startTrace(requestModel.getAction());
//            mTrace.start();
        }

        try {
            if (requestModel.getAction().equals(APIServices.ACTION_SUBMIT_BILL) && response == null){
                responseModel = new ResponseModel();
                responseModel.setStatus(-2);
                responseModel.setMsg("Fail");

            } else {
                // แปลง response เป็น model response
                strRespone = response.string();
                responseModel = gson.fromJson(strRespone, ResponseModel.class);
                Log.e(TAG, "Response: " + strRespone);
                if (responseModel == null) return null;

                dialogTitle = null;
                msg = responseModel.getAppdisplay();
            }

            // check  pending สำหรับ recall submit
            switch (requestModel.getAction()){
                case APIServices.ACTIONSUBMITTOPUP:
                case APIServices.ACTION_GET_OTP_EPIN:
                case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                case APIServices.ACTION_SUBMIT_VAS:
                case APIServices.ACTION_SUBMIT_BILL:
                    Global.getInstance().setSubmitStatus(responseModel.getMsg());
                    if (responseModel.getMsg().contains(APIServices.MSG_WAIT)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                APIHelper.enqueueWithRetry(call.clone(), callback);
                            }
                        }, 3000);

                        if (mTrace != null){
                            mTrace.incrementCounter("PENDING");
//                            mTrace.stop();
                        }
                        return APIServices.MSG_WAIT;
                    }

                    break;
            }


            // response Fail
            if (responseModel.getStatus() != APIServices.SUCCESS) {
                if (mTrace != null){
                    mTrace.incrementCounter("FAIL");
//                    mTrace.stop();
                }

                try {
                    switch (requestModel.getAction()) {

                        case APIServices.ACTIONGETOTP:
                        case APIServices.ACTIONSUBMITTOPUP:
                        case APIServices.ACTION_GET_OTP_EPIN:
                        case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                        case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                        case APIServices.ACTION_SUBMIT_VAS:
                        case APIServices.ACTION_SUBMIT_BILL:
                        case APIServices.ACTION_PREVIEW_VAS:
                        case APIServices.ACTION_PREVIEW_BILL:


/*
                            if (responseModel.getMsg().contains(APIServices.MSG_FAIL) || responseModel.getMsg().equals("Fail")) {
                                Global.getInstance().setLastSubmit(null, false);
                            }
*/
                            if (msg != null && msg.contains("Success")) msg = null;

                            if (msg == null || msg.isEmpty()){
                                msg = MyApplication.getContext().getString(R.string.alert_topup_fail);
                                dialogTitle = MyApplication.getContext().getString(R.string.error);
                                new DialogCounterAlert(context, dialogTitle, msg, null);
                            }

                            showErrorMSG(context, responseModel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((Activity) context).finish();
                                }
                            });

                            DialogCounterAlert.DialogProgress.dismiss();
                            return null;

                        case APIServices.ACTIONLOGIN:
                        case APIServices.ACTIONNOTIPAY:
                            DialogCounterAlert.DialogProgress.dismiss();

                            return responseModel;

                        case APIServices.ACTIONLOGOUT:
                        case APIServices.ACTIONGETBALANCE:
                            DialogCounterAlert.DialogProgress.dismiss();

                            return null;

/*
                        case APIServices.ACTION_GET_LISTPG:
                            APIHelper.enqueueWithRetry(call.clone(), callback);
                            return null;
*/

                        default:
                            DialogCounterAlert.DialogProgress.dismiss();

                            if (MyApplication.canUseLeaving(MyApplication.LeavingOrEntering.currentActivity) &&
                                    Global.getInstance().getUSERNAME() == null) {
                                try{
                                    Util.backToSignIn((Activity) context);
                                } catch (ClassCastException e){
                                    e.printStackTrace();
                                }
                                return null;
                            }

//                            msg = responseModel.getMsg();
                            msg = MyApplication.getContext().getString(R.string.error_msg);

                            switch (requestModel.getAction()){
                                case APIServices.ACTION_GET_LISTPG:
                                    msg = MyApplication.getContext().getString(R.string.error_msg_data);
                                    break;
                            }

                            if (msg.substring(0, 3).equals(APIServices.MSG_FAIL)) msg = "Fail";
                            // show Dialog Error
                            new ErrorNetworkThrowable(null).networkError(context, msg
                                    , call, callback, true);
                    }
                } catch (NullPointerException e){
                    DialogCounterAlert.DialogProgress.dismiss();

                    e.printStackTrace();
                    return null;
                }
            } else{
                // Response Success
                if (mTrace != null){
                    mTrace.incrementCounter("SUCCESS");
//                    mTrace.stop();
                }

                return responseModel;
            }

        } catch (JsonSyntaxException e){
            // Catch จาก การแปลง json เป็น model จะได้เป็น String encoded แทน
            String responDecode = Util.decode(strRespone);
            Log.e(TAG, "ResponDecode: "+ responDecode);
            if (mTrace != null){
                mTrace.incrementCounter("SUCCESS");
//                mTrace.stop();
            }
            return responDecode;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    // แสดง Error กรณีบังคับแสดง
    public static boolean showErrorMSG(Context context, ResponseModel responseModel, DialogInterface.OnClickListener listener) {
        Activity activity = null;
        try {
            activity = (Activity) context;
            Activity currentActivity = MyApplication.LeavingOrEntering.currentActivity;
            if (activity.getLocalClassName().equals(currentActivity.getLocalClassName())) {
                String msg = responseModel.getAppdisplay();
                if (msg != null && msg.contains("Success")) msg = null;

                if (msg != null && !msg.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity)
                            .setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton(MyApplication.getContext().getString(R.string.confirm), listener).show();
                    return true;
                }
            }

        } catch (ClassCastException e){}

        if (responseModel.getStatus() != APIServices.SUCCESS) {
            MyApplication.uploadFail(MyApplication.NOTITOPUP, responseModel.getAppdisplay());
        }
        return false;
    }


}
