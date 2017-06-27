package com.fabhurtado.mycontacts.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This util class is used to check device connectivity
 *
 * @author FabHurtado
 */

public class NetworkDetector {

    /**
     * Check device connectivity.
     *
     * @param context application context
     * @return true if device is connected to internet
     */
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
