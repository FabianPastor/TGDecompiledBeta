package android.support.v4.net;

import android.net.ConnectivityManager;

class ConnectivityManagerCompatApi24 {
    ConnectivityManagerCompatApi24() {
    }

    public static int getRestrictBackgroundStatus(ConnectivityManager cm) {
        return cm.getRestrictBackgroundStatus();
    }
}
