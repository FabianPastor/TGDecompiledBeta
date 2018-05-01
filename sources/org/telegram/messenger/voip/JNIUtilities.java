package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import org.telegram.messenger.ApplicationLoader;

public class JNIUtilities {
    @TargetApi(23)
    public static String getCurrentNetworkInterfaceName() {
        ConnectivityManager cm = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
        Network net = cm.getActiveNetwork();
        if (net == null) {
            return null;
        }
        LinkProperties props = cm.getLinkProperties(net);
        if (props != null) {
            return props.getInterfaceName();
        }
        return null;
    }
}
