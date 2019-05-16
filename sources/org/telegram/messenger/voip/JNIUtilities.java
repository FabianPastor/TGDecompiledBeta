package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

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

    public static String[] getLocalNetworkAddressesAndInterfaceName() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
        String[] strArr = null;
        String str;
        if (VERSION.SDK_INT >= 23) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return null;
            }
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties == null) {
                return null;
            }
            str = null;
            for (LinkAddress address : linkProperties.getLinkAddresses()) {
                InetAddress address2 = address.getAddress();
                if (address2 instanceof Inet4Address) {
                    if (!address2.isLinkLocalAddress()) {
                        strArr = address2.getHostAddress();
                    }
                } else if (!(!(address2 instanceof Inet6Address) || address2.isLinkLocalAddress() || (address2.getAddress()[0] & 240) == 240)) {
                    str = address2.getHostAddress();
                }
            }
            return new String[]{linkProperties.getInterfaceName(), strArr, str};
        }
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                return null;
            }
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                if (!networkInterface.isLoopback()) {
                    if (networkInterface.isUp()) {
                        networkInterfaces = networkInterface.getInetAddresses();
                        str = null;
                        String str2 = str;
                        while (networkInterfaces.hasMoreElements()) {
                            InetAddress inetAddress = (InetAddress) networkInterfaces.nextElement();
                            if (inetAddress instanceof Inet4Address) {
                                if (!inetAddress.isLinkLocalAddress()) {
                                    str = inetAddress.getHostAddress();
                                }
                            } else if (!(!(inetAddress instanceof Inet6Address) || inetAddress.isLinkLocalAddress() || (inetAddress.getAddress()[0] & 240) == 240)) {
                                str2 = inetAddress.getHostAddress();
                            }
                        }
                        return new String[]{networkInterface.getName(), str, str2};
                    }
                }
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String[] getCarrierInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        if (VERSION.SDK_INT >= 24) {
            telephonyManager = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        }
        if (TextUtils.isEmpty(telephonyManager.getNetworkOperatorName())) {
            return null;
        }
        String networkOperator = telephonyManager.getNetworkOperator();
        String str = "";
        if (networkOperator == null || networkOperator.length() <= 3) {
            networkOperator = str;
        } else {
            str = networkOperator.substring(0, 3);
            networkOperator = networkOperator.substring(3);
        }
        return new String[]{telephonyManager.getNetworkOperatorName(), telephonyManager.getNetworkCountryIso().toUpperCase(), str, networkOperator};
    }

    public static int[] getWifiInfo() {
        try {
            WifiInfo connectionInfo = ((WifiManager) ApplicationLoader.applicationContext.getSystemService("wifi")).getConnectionInfo();
            return new int[]{connectionInfo.getRssi(), connectionInfo.getLinkSpeed()};
        } catch (Exception unused) {
            return null;
        }
    }
}
