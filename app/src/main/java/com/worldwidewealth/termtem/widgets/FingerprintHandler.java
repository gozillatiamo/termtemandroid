package com.worldwidewealth.termtem.widgets;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

/**
 * Created by gozillatiamo on 12/14/17.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler {

    private CancellationSignal cancellationSignal;
    private Context mContext;

    public FingerprintHandler(Context context){
        this.mContext = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, FingerprintManager.AuthenticationCallback callback){
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        manager.authenticate(cryptoObject, cancellationSignal, 0, callback, null);

    }


}
