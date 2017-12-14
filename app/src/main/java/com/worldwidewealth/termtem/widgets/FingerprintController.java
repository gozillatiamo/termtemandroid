package com.worldwidewealth.termtem.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.worldwidewealth.termtem.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by gozillatiamo on 12/13/17.
 */

public class FingerprintController {

    private static final String KEY_NAME = "termtemfingerprint";
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private static FingerprintManager fingerprintManager;
    private static Context mContext;

    public static FingerprintController getInstance(Context context){
        mContext = context;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

            if (!fingerprintManager.isHardwareDetected()){
                return null;
            }

            return new FingerprintController();

        } else return null;

    }


    @TargetApi(Build.VERSION_CODES.M)
    public void initController(FingerprintManager.AuthenticationCallback callback){
        keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

        if (!fingerprintManager.hasEnrolledFingerprints()){
            AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.MyAlertDialogError)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_invalid_finger_in_device)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((Activity)mContext).finish();
                        }
                    }).show();
            return;
        }

        if (!keyguardManager.isKeyguardSecure()){
            AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.MyAlertDialogError)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_invalid_lockscreen_secure)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((Activity)mContext).finish();
                        }
                    }).show();
            return;

        }

        try{
            generateKey();
        } catch (FingerprintException e){
            e.printStackTrace();
        }

        if (initCipher()){
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler fingerprintHandler = new FingerprintHandler(mContext);
            fingerprintHandler.startAuth(fingerprintManager, cryptoObject, callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException{
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);

            keyGenerator.init( new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc){
            exc.printStackTrace();
            throw new FingerprintException(exc);

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher(){
        try{
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e){
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e){

            return false;
        } catch (KeyStoreException | CertificateException
                    | UnrecoverableKeyException | IOException
                    | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e){
            super(e);
        }
    }

}
