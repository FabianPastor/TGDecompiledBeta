package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import org.telegram.messenger.ApplicationLoader;

public class JNIUtilities {
    @TargetApi(23)
    public static String getCurrentNetworkInterfaceName() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return null;
        }
        LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
        if (linkProperties == null) {
            return null;
        }
        return linkProperties.getInterfaceName();
    }
}
