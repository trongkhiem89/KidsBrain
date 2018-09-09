package com.kid.brain.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by khiemnt on 9/12/2015.
 */
public class NetworkUtil {

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connMgr.getActiveNetworkInfo();
        return (activeNetInfo != null && activeNetInfo.isAvailable() && activeNetInfo.isConnected());
    }
}
