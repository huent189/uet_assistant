package vnu.uet.mobilecourse.assistant.network.error_detector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class NetworkUtil {
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities netInfo = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return (netInfo != null && netInfo.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
    }
}
