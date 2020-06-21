package vnu.uet.mobilecourse.assistant.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int STATUS_NOT_CONNECTED = 0;
    public static final int STATUS_WIFI = 1;
    public static final int STATUS_MOBILE = 2;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);

        int status = STATUS_NOT_CONNECTED;

        if (conn == TYPE_WIFI) {
            status = STATUS_WIFI;
        } else if (conn == TYPE_MOBILE) {
            status = STATUS_MOBILE;
        }

        return status;
    }
}