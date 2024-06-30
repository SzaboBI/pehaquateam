package com.example.pe_haquapp.controller.Utils;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class NetworkUtils {
    public static boolean isConnected(ConnectivityManager manager){
        NetworkCapabilities capabilities = null;
        if (manager != null){
            capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        }
        return manager != null && manager.getActiveNetwork() != null &&
                capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
