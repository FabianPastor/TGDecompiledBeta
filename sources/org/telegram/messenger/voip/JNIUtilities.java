package org.telegram.messenger.voip;

import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
    public static String getCurrentNetworkInterfaceName() {
        LinkProperties props;
        ConnectivityManager cm = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
        Network net = cm.getActiveNetwork();
        if (net == null || (props = cm.getLinkProperties(net)) == null) {
            return null;
        }
        return props.getInterfaceName();
    }

    public static String[] getLocalNetworkAddressesAndInterfaceName() {
        LinkProperties linkProps;
        ConnectivityManager cm = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 23) {
            Network net = cm.getActiveNetwork();
            if (net == null || (linkProps = cm.getLinkProperties(net)) == null) {
                return null;
            }
            String ipv4 = null;
            String ipv6 = null;
            for (LinkAddress addr : linkProps.getLinkAddresses()) {
                InetAddress a = addr.getAddress();
                if (a instanceof Inet4Address) {
                    if (!a.isLinkLocalAddress()) {
                        ipv4 = a.getHostAddress();
                    }
                } else if ((a instanceof Inet6Address) && !a.isLinkLocalAddress() && (a.getAddress()[0] & 240) != 240) {
                    ipv6 = a.getHostAddress();
                }
            }
            return new String[]{linkProps.getInterfaceName(), ipv4, ipv6};
        }
        try {
            Enumeration<NetworkInterface> itfs = NetworkInterface.getNetworkInterfaces();
            if (itfs == null) {
                return null;
            }
            while (itfs.hasMoreElements()) {
                NetworkInterface itf = itfs.nextElement();
                if (!itf.isLoopback()) {
                    if (itf.isUp()) {
                        Enumeration<InetAddress> addrs = itf.getInetAddresses();
                        String ipv42 = null;
                        String ipv62 = null;
                        while (addrs.hasMoreElements()) {
                            InetAddress a2 = addrs.nextElement();
                            if (a2 instanceof Inet4Address) {
                                if (!a2.isLinkLocalAddress()) {
                                    ipv42 = a2.getHostAddress();
                                }
                            } else if ((a2 instanceof Inet6Address) && !a2.isLinkLocalAddress() && (a2.getAddress()[0] & 240) != 240) {
                                ipv62 = a2.getHostAddress();
                            }
                        }
                        return new String[]{itf.getName(), ipv42, ipv62};
                    }
                }
            }
            return null;
        } catch (Exception x) {
            FileLog.e((Throwable) x);
            return null;
        }
    }

    public static String[] getCarrierInfo() {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        if (Build.VERSION.SDK_INT >= 24) {
            tm = tm.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        }
        if (TextUtils.isEmpty(tm.getNetworkOperatorName())) {
            return null;
        }
        String mnc = "";
        String mcc = "";
        String carrierID = tm.getNetworkOperator();
        if (carrierID != null && carrierID.length() > 3) {
            mcc = carrierID.substring(0, 3);
            mnc = carrierID.substring(3);
        }
        return new String[]{tm.getNetworkOperatorName(), tm.getNetworkCountryIso().toUpperCase(), mcc, mnc};
    }

    public static int[] getWifiInfo() {
        try {
            WifiInfo info = ((WifiManager) ApplicationLoader.applicationContext.getSystemService("wifi")).getConnectionInfo();
            return new int[]{info.getRssi(), info.getLinkSpeed()};
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSupportedVideoCodecs() {
        return "";
    }

    public static int getMaxVideoResolution() {
        return 320;
    }
}
