package com.sam_chordas.android.stockhawk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sam_chordas.android.stockhawk.rest.NetworkUtil;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by MA on 5/29/16.
 * Referenced from: http://stackoverflow.com/questions/25678216/android-internet-connectivity-change-listener
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    MyStocksActivity main = null;


    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) && main != null) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                    main.showInternetMsg();
            }else{
                    main.hideInternetMsg();
            }

        }
    }

    public void setMainActivityHandler(MyStocksActivity main){
        this.main=main;
    }
}