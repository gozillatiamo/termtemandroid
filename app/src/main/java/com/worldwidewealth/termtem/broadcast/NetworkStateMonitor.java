package com.worldwidewealth.termtem.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.worldwidewealth.termtem.R;

/**
 * Created by gozillatiamo on 6/1/17.
 */

public class NetworkStateMonitor extends BroadcastReceiver {

    public interface Linstener {
        void onNetworkStateChange(boolean isConnect);
    }

    private Context mContext;
    private boolean mConnectUp;

    public NetworkStateMonitor(Context context){
        mContext = context;

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, intentFilter);

        mConnectUp  = isConnectUp();
    }

    public boolean isConnectUp(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null &&networkInfo.isConnectedOrConnecting();

    }

    public void unregisterReceiver(){
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnectNow = isConnectUp();
        if (isConnectNow == mConnectUp) return;

        mConnectUp = isConnectNow;

        if (!mConnectUp)
            Toast.makeText(context, context.getString(R.string.network_disconnect), Toast.LENGTH_SHORT).show();

        try {
            ((Linstener) mContext).onNetworkStateChange(mConnectUp);
        } catch (ClassCastException e){
            e.printStackTrace();
        }

    }
}
