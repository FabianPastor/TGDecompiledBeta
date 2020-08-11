package org.webrtc;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.webrtc.NetworkMonitorAutoDetect;

public class NetworkMonitorAutoDetect extends BroadcastReceiver {
    static final long INVALID_NET_ID = -1;
    private static final String TAG = "NetworkMonitorAutoDetect";
    private final ConnectivityManager.NetworkCallback allNetworkCallback;
    private ConnectionType connectionType;
    /* access modifiers changed from: private */
    public ConnectivityManagerDelegate connectivityManagerDelegate;
    private final Context context;
    private final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private boolean isRegistered;
    private final ConnectivityManager.NetworkCallback mobileNetworkCallback;
    /* access modifiers changed from: private */
    public final Observer observer;
    private WifiDirectManagerDelegate wifiDirectManagerDelegate;
    private WifiManagerDelegate wifiManagerDelegate;
    private String wifiSSID;

    public enum ConnectionType {
        CONNECTION_UNKNOWN,
        CONNECTION_ETHERNET,
        CONNECTION_WIFI,
        CONNECTION_4G,
        CONNECTION_3G,
        CONNECTION_2G,
        CONNECTION_UNKNOWN_CELLULAR,
        CONNECTION_BLUETOOTH,
        CONNECTION_VPN,
        CONNECTION_NONE
    }

    public interface Observer {
        void onConnectionTypeChanged(ConnectionType connectionType);

        void onNetworkConnect(NetworkInformation networkInformation);

        void onNetworkDisconnect(long j);
    }

    public static class IPAddress {
        public final byte[] address;

        public IPAddress(byte[] bArr) {
            this.address = bArr;
        }

        @CalledByNative("IPAddress")
        private byte[] getAddress() {
            return this.address;
        }
    }

    public static class NetworkInformation {
        public final long handle;
        public final IPAddress[] ipAddresses;
        public final String name;
        public final ConnectionType type;
        public final ConnectionType underlyingTypeForVpn;

        public NetworkInformation(String str, ConnectionType connectionType, ConnectionType connectionType2, long j, IPAddress[] iPAddressArr) {
            this.name = str;
            this.type = connectionType;
            this.underlyingTypeForVpn = connectionType2;
            this.handle = j;
            this.ipAddresses = iPAddressArr;
        }

        @CalledByNative("NetworkInformation")
        private IPAddress[] getIpAddresses() {
            return this.ipAddresses;
        }

        @CalledByNative("NetworkInformation")
        private ConnectionType getConnectionType() {
            return this.type;
        }

        @CalledByNative("NetworkInformation")
        private ConnectionType getUnderlyingConnectionTypeForVpn() {
            return this.underlyingTypeForVpn;
        }

        @CalledByNative("NetworkInformation")
        private long getHandle() {
            return this.handle;
        }

        @CalledByNative("NetworkInformation")
        private String getName() {
            return this.name;
        }
    }

    static class NetworkState {
        private final boolean connected;
        private final int subtype;
        private final int type;
        private final int underlyingNetworkSubtypeForVpn;
        private final int underlyingNetworkTypeForVpn;

        public NetworkState(boolean z, int i, int i2, int i3, int i4) {
            this.connected = z;
            this.type = i;
            this.subtype = i2;
            this.underlyingNetworkTypeForVpn = i3;
            this.underlyingNetworkSubtypeForVpn = i4;
        }

        public boolean isConnected() {
            return this.connected;
        }

        public int getNetworkType() {
            return this.type;
        }

        public int getNetworkSubType() {
            return this.subtype;
        }

        public int getUnderlyingNetworkTypeForVpn() {
            return this.underlyingNetworkTypeForVpn;
        }

        public int getUnderlyingNetworkSubtypeForVpn() {
            return this.underlyingNetworkSubtypeForVpn;
        }
    }

    @SuppressLint({"NewApi"})
    private class SimpleNetworkCallback extends ConnectivityManager.NetworkCallback {
        private SimpleNetworkCallback() {
        }

        public void onAvailable(Network network) {
            Logging.d("NetworkMonitorAutoDetect", "Network becomes available: " + network.toString());
            onNetworkChanged(network);
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            Logging.d("NetworkMonitorAutoDetect", "capabilities changed: " + networkCapabilities.toString());
            onNetworkChanged(network);
        }

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            Logging.d("NetworkMonitorAutoDetect", "link properties changed: " + linkProperties.toString());
            onNetworkChanged(network);
        }

        public void onLosing(Network network, int i) {
            Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " is about to lose in " + i + "ms");
        }

        public void onLost(Network network) {
            Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " is disconnected");
            NetworkMonitorAutoDetect.this.observer.onNetworkDisconnect(NetworkMonitorAutoDetect.networkToNetId(network));
        }

        private void onNetworkChanged(Network network) {
            NetworkInformation access$300 = NetworkMonitorAutoDetect.this.connectivityManagerDelegate.networkToInfo(network);
            if (access$300 != null) {
                NetworkMonitorAutoDetect.this.observer.onNetworkConnect(access$300);
            }
        }
    }

    static class ConnectivityManagerDelegate {
        private final ConnectivityManager connectivityManager;

        ConnectivityManagerDelegate(Context context) {
            this.connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        }

        ConnectivityManagerDelegate() {
            this.connectivityManager = null;
        }

        /* access modifiers changed from: package-private */
        public NetworkState getNetworkState() {
            ConnectivityManager connectivityManager2 = this.connectivityManager;
            if (connectivityManager2 == null) {
                return new NetworkState(false, -1, -1, -1, -1);
            }
            return getNetworkState(connectivityManager2.getActiveNetworkInfo());
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"NewApi"})
        public NetworkState getNetworkState(Network network) {
            ConnectivityManager connectivityManager2;
            NetworkInfo activeNetworkInfo;
            Network network2 = network;
            if (network2 == null || (connectivityManager2 = this.connectivityManager) == null) {
                return new NetworkState(false, -1, -1, -1, -1);
            }
            NetworkInfo networkInfo = connectivityManager2.getNetworkInfo(network2);
            if (networkInfo == null) {
                Logging.w("NetworkMonitorAutoDetect", "Couldn't retrieve information from network " + network.toString());
                return new NetworkState(false, -1, -1, -1, -1);
            } else if (networkInfo.getType() != 17) {
                NetworkCapabilities networkCapabilities = this.connectivityManager.getNetworkCapabilities(network2);
                return (networkCapabilities == null || !networkCapabilities.hasTransport(4)) ? getNetworkState(networkInfo) : new NetworkState(networkInfo.isConnected(), 17, -1, networkInfo.getType(), networkInfo.getSubtype());
            } else if (networkInfo.getType() != 17) {
                return getNetworkState(networkInfo);
            } else {
                if (Build.VERSION.SDK_INT < 23 || !network2.equals(this.connectivityManager.getActiveNetwork()) || (activeNetworkInfo = this.connectivityManager.getActiveNetworkInfo()) == null || activeNetworkInfo.getType() == 17) {
                    return new NetworkState(networkInfo.isConnected(), 17, -1, -1, -1);
                }
                return new NetworkState(networkInfo.isConnected(), 17, -1, activeNetworkInfo.getType(), activeNetworkInfo.getSubtype());
            }
        }

        private NetworkState getNetworkState(NetworkInfo networkInfo) {
            if (networkInfo == null || !networkInfo.isConnected()) {
                return new NetworkState(false, -1, -1, -1, -1);
            }
            return new NetworkState(true, networkInfo.getType(), networkInfo.getSubtype(), -1, -1);
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"NewApi"})
        public Network[] getAllNetworks() {
            ConnectivityManager connectivityManager2 = this.connectivityManager;
            if (connectivityManager2 == null) {
                return new Network[0];
            }
            return connectivityManager2.getAllNetworks();
        }

        /* access modifiers changed from: package-private */
        public List<NetworkInformation> getActiveNetworkList() {
            if (!supportNetworkCallback()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (Network networkToInfo : getAllNetworks()) {
                NetworkInformation networkToInfo2 = networkToInfo(networkToInfo);
                if (networkToInfo2 != null) {
                    arrayList.add(networkToInfo2);
                }
            }
            return arrayList;
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"NewApi"})
        public long getDefaultNetId() {
            NetworkInfo activeNetworkInfo;
            NetworkInfo networkInfo;
            if (!supportNetworkCallback() || (activeNetworkInfo = this.connectivityManager.getActiveNetworkInfo()) == null) {
                return -1;
            }
            long j = -1;
            for (Network network : getAllNetworks()) {
                if (hasInternetCapability(network) && (networkInfo = this.connectivityManager.getNetworkInfo(network)) != null && networkInfo.getType() == activeNetworkInfo.getType()) {
                    if (j == -1) {
                        j = NetworkMonitorAutoDetect.networkToNetId(network);
                    } else {
                        throw new RuntimeException("Multiple connected networks of same type are not supported.");
                    }
                }
            }
            return j;
        }

        /* access modifiers changed from: private */
        @SuppressLint({"NewApi"})
        public NetworkInformation networkToInfo(Network network) {
            ConnectivityManager connectivityManager2;
            if (network == null || (connectivityManager2 = this.connectivityManager) == null) {
                return null;
            }
            LinkProperties linkProperties = connectivityManager2.getLinkProperties(network);
            if (linkProperties == null) {
                Logging.w("NetworkMonitorAutoDetect", "Detected unknown network: " + network.toString());
                return null;
            } else if (linkProperties.getInterfaceName() == null) {
                Logging.w("NetworkMonitorAutoDetect", "Null interface name for network " + network.toString());
                return null;
            } else {
                NetworkState networkState = getNetworkState(network);
                ConnectionType connectionType = NetworkMonitorAutoDetect.getConnectionType(networkState);
                if (connectionType == ConnectionType.CONNECTION_NONE) {
                    Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " is disconnected");
                    return null;
                }
                if (connectionType == ConnectionType.CONNECTION_UNKNOWN || connectionType == ConnectionType.CONNECTION_UNKNOWN_CELLULAR) {
                    Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " connection type is " + connectionType + " because it has type " + networkState.getNetworkType() + " and subtype " + networkState.getNetworkSubType());
                }
                return new NetworkInformation(linkProperties.getInterfaceName(), connectionType, NetworkMonitorAutoDetect.getUnderlyingConnectionTypeForVpn(networkState), NetworkMonitorAutoDetect.networkToNetId(network), getIPAddresses(linkProperties));
            }
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"NewApi"})
        public boolean hasInternetCapability(Network network) {
            NetworkCapabilities networkCapabilities;
            ConnectivityManager connectivityManager2 = this.connectivityManager;
            if (connectivityManager2 == null || (networkCapabilities = connectivityManager2.getNetworkCapabilities(network)) == null || !networkCapabilities.hasCapability(12)) {
                return false;
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        public void registerNetworkCallback(ConnectivityManager.NetworkCallback networkCallback) {
            this.connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addCapability(12).build(), networkCallback);
        }

        @SuppressLint({"NewApi"})
        public void requestMobileNetwork(ConnectivityManager.NetworkCallback networkCallback) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addCapability(12).addTransportType(0);
            this.connectivityManager.requestNetwork(builder.build(), networkCallback);
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"NewApi"})
        public IPAddress[] getIPAddresses(LinkProperties linkProperties) {
            IPAddress[] iPAddressArr = new IPAddress[linkProperties.getLinkAddresses().size()];
            int i = 0;
            for (LinkAddress address : linkProperties.getLinkAddresses()) {
                iPAddressArr[i] = new IPAddress(address.getAddress().getAddress());
                i++;
            }
            return iPAddressArr;
        }

        @SuppressLint({"NewApi"})
        public void releaseCallback(ConnectivityManager.NetworkCallback networkCallback) {
            if (supportNetworkCallback()) {
                Logging.d("NetworkMonitorAutoDetect", "Unregister network callback");
                this.connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }

        public boolean supportNetworkCallback() {
            return Build.VERSION.SDK_INT >= 21 && this.connectivityManager != null;
        }
    }

    static class WifiManagerDelegate {
        private final Context context;

        WifiManagerDelegate(Context context2) {
            this.context = context2;
        }

        WifiManagerDelegate() {
            this.context = null;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x001a, code lost:
            r0 = (r0 = (android.net.wifi.WifiInfo) r0.getParcelableExtra("wifiInfo")).getSSID();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String getWifiSSID() {
            /*
                r3 = this;
                android.content.Context r0 = r3.context
                android.content.IntentFilter r1 = new android.content.IntentFilter
                java.lang.String r2 = "android.net.wifi.STATE_CHANGE"
                r1.<init>(r2)
                r2 = 0
                android.content.Intent r0 = r0.registerReceiver(r2, r1)
                if (r0 == 0) goto L_0x0021
                java.lang.String r1 = "wifiInfo"
                android.os.Parcelable r0 = r0.getParcelableExtra(r1)
                android.net.wifi.WifiInfo r0 = (android.net.wifi.WifiInfo) r0
                if (r0 == 0) goto L_0x0021
                java.lang.String r0 = r0.getSSID()
                if (r0 == 0) goto L_0x0021
                return r0
            L_0x0021:
                java.lang.String r0 = ""
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.NetworkMonitorAutoDetect.WifiManagerDelegate.getWifiSSID():java.lang.String");
        }
    }

    static class WifiDirectManagerDelegate extends BroadcastReceiver {
        private static final int WIFI_P2P_NETWORK_HANDLE = 0;
        private final Context context;
        private final Observer observer;
        private NetworkInformation wifiP2pNetworkInfo;

        WifiDirectManagerDelegate(Observer observer2, Context context2) {
            this.context = context2;
            this.observer = observer2;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
            context2.registerReceiver(this, intentFilter);
            if (Build.VERSION.SDK_INT > 28) {
                WifiP2pManager wifiP2pManager = (WifiP2pManager) context2.getSystemService("wifip2p");
                wifiP2pManager.requestGroupInfo(wifiP2pManager.initialize(context2, context2.getMainLooper(), (WifiP2pManager.ChannelListener) null), new WifiP2pManager.GroupInfoListener() {
                    public final void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                        NetworkMonitorAutoDetect.WifiDirectManagerDelegate.this.lambda$new$0$NetworkMonitorAutoDetect$WifiDirectManagerDelegate(wifiP2pGroup);
                    }
                });
            }
        }

        @SuppressLint({"InlinedApi"})
        public void onReceive(Context context2, Intent intent) {
            if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(intent.getAction())) {
                lambda$new$0$NetworkMonitorAutoDetect$WifiDirectManagerDelegate((WifiP2pGroup) intent.getParcelableExtra("p2pGroupInfo"));
            } else if ("android.net.wifi.p2p.STATE_CHANGED".equals(intent.getAction())) {
                onWifiP2pStateChange(intent.getIntExtra("wifi_p2p_state", 0));
            }
        }

        public void release() {
            this.context.unregisterReceiver(this);
        }

        public List<NetworkInformation> getActiveNetworkList() {
            NetworkInformation networkInformation = this.wifiP2pNetworkInfo;
            if (networkInformation != null) {
                return Collections.singletonList(networkInformation);
            }
            return Collections.emptyList();
        }

        /* access modifiers changed from: private */
        /* renamed from: onWifiP2pGroupChange */
        public void lambda$new$0$NetworkMonitorAutoDetect$WifiDirectManagerDelegate(WifiP2pGroup wifiP2pGroup) {
            if (wifiP2pGroup != null && wifiP2pGroup.getInterface() != null) {
                try {
                    ArrayList<T> list = Collections.list(NetworkInterface.getByName(wifiP2pGroup.getInterface()).getInetAddresses());
                    IPAddress[] iPAddressArr = new IPAddress[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        iPAddressArr[i] = new IPAddress(((InetAddress) list.get(i)).getAddress());
                    }
                    NetworkInformation networkInformation = new NetworkInformation(wifiP2pGroup.getInterface(), ConnectionType.CONNECTION_WIFI, ConnectionType.CONNECTION_NONE, 0, iPAddressArr);
                    this.wifiP2pNetworkInfo = networkInformation;
                    this.observer.onNetworkConnect(networkInformation);
                } catch (SocketException e) {
                    Logging.e("NetworkMonitorAutoDetect", "Unable to get WifiP2p network interface", e);
                }
            }
        }

        private void onWifiP2pStateChange(int i) {
            if (i == 1) {
                this.wifiP2pNetworkInfo = null;
                this.observer.onNetworkDisconnect(0);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public NetworkMonitorAutoDetect(Observer observer2, Context context2) {
        this.observer = observer2;
        this.context = context2;
        this.connectivityManagerDelegate = new ConnectivityManagerDelegate(context2);
        this.wifiManagerDelegate = new WifiManagerDelegate(context2);
        NetworkState networkState = this.connectivityManagerDelegate.getNetworkState();
        this.connectionType = getConnectionType(networkState);
        this.wifiSSID = getWifiSSID(networkState);
        if (PeerConnectionFactory.fieldTrialsFindFullName("IncludeWifiDirect").equals("Enabled")) {
            this.wifiDirectManagerDelegate = new WifiDirectManagerDelegate(observer2, context2);
        }
        registerReceiver();
        if (this.connectivityManagerDelegate.supportNetworkCallback()) {
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback();
            try {
                this.connectivityManagerDelegate.requestMobileNetwork(networkCallback);
            } catch (SecurityException unused) {
                Logging.w("NetworkMonitorAutoDetect", "Unable to obtain permission to request a cellular network.");
                networkCallback = null;
            }
            this.mobileNetworkCallback = networkCallback;
            SimpleNetworkCallback simpleNetworkCallback = new SimpleNetworkCallback();
            this.allNetworkCallback = simpleNetworkCallback;
            this.connectivityManagerDelegate.registerNetworkCallback(simpleNetworkCallback);
            return;
        }
        this.mobileNetworkCallback = null;
        this.allNetworkCallback = null;
    }

    public boolean supportNetworkCallback() {
        return this.connectivityManagerDelegate.supportNetworkCallback();
    }

    /* access modifiers changed from: package-private */
    public void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate connectivityManagerDelegate2) {
        this.connectivityManagerDelegate = connectivityManagerDelegate2;
    }

    /* access modifiers changed from: package-private */
    public void setWifiManagerDelegateForTests(WifiManagerDelegate wifiManagerDelegate2) {
        this.wifiManagerDelegate = wifiManagerDelegate2;
    }

    /* access modifiers changed from: package-private */
    public boolean isReceiverRegisteredForTesting() {
        return this.isRegistered;
    }

    /* access modifiers changed from: package-private */
    public List<NetworkInformation> getActiveNetworkList() {
        List<NetworkInformation> activeNetworkList = this.connectivityManagerDelegate.getActiveNetworkList();
        if (activeNetworkList == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(activeNetworkList);
        WifiDirectManagerDelegate wifiDirectManagerDelegate2 = this.wifiDirectManagerDelegate;
        if (wifiDirectManagerDelegate2 != null) {
            arrayList.addAll(wifiDirectManagerDelegate2.getActiveNetworkList());
        }
        return arrayList;
    }

    public void destroy() {
        ConnectivityManager.NetworkCallback networkCallback = this.allNetworkCallback;
        if (networkCallback != null) {
            this.connectivityManagerDelegate.releaseCallback(networkCallback);
        }
        ConnectivityManager.NetworkCallback networkCallback2 = this.mobileNetworkCallback;
        if (networkCallback2 != null) {
            this.connectivityManagerDelegate.releaseCallback(networkCallback2);
        }
        WifiDirectManagerDelegate wifiDirectManagerDelegate2 = this.wifiDirectManagerDelegate;
        if (wifiDirectManagerDelegate2 != null) {
            wifiDirectManagerDelegate2.release();
        }
        unregisterReceiver();
    }

    private void registerReceiver() {
        if (!this.isRegistered) {
            this.isRegistered = true;
            this.context.registerReceiver(this, this.intentFilter);
        }
    }

    private void unregisterReceiver() {
        if (this.isRegistered) {
            this.isRegistered = false;
            this.context.unregisterReceiver(this);
        }
    }

    public NetworkState getCurrentNetworkState() {
        return this.connectivityManagerDelegate.getNetworkState();
    }

    public long getDefaultNetId() {
        return this.connectivityManagerDelegate.getDefaultNetId();
    }

    private static ConnectionType getConnectionType(boolean z, int i, int i2) {
        if (!z) {
            return ConnectionType.CONNECTION_NONE;
        }
        if (i == 0) {
            switch (i2) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                    return ConnectionType.CONNECTION_2G;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    return ConnectionType.CONNECTION_3G;
                case 13:
                    return ConnectionType.CONNECTION_4G;
                default:
                    return ConnectionType.CONNECTION_UNKNOWN_CELLULAR;
            }
        } else if (i == 1) {
            return ConnectionType.CONNECTION_WIFI;
        } else {
            if (i == 6) {
                return ConnectionType.CONNECTION_4G;
            }
            if (i == 7) {
                return ConnectionType.CONNECTION_BLUETOOTH;
            }
            if (i == 9) {
                return ConnectionType.CONNECTION_ETHERNET;
            }
            if (i != 17) {
                return ConnectionType.CONNECTION_UNKNOWN;
            }
            return ConnectionType.CONNECTION_VPN;
        }
    }

    public static ConnectionType getConnectionType(NetworkState networkState) {
        return getConnectionType(networkState.isConnected(), networkState.getNetworkType(), networkState.getNetworkSubType());
    }

    /* access modifiers changed from: private */
    public static ConnectionType getUnderlyingConnectionTypeForVpn(NetworkState networkState) {
        if (networkState.getNetworkType() != 17) {
            return ConnectionType.CONNECTION_NONE;
        }
        return getConnectionType(networkState.isConnected(), networkState.getUnderlyingNetworkTypeForVpn(), networkState.getUnderlyingNetworkSubtypeForVpn());
    }

    private String getWifiSSID(NetworkState networkState) {
        if (getConnectionType(networkState) != ConnectionType.CONNECTION_WIFI) {
            return "";
        }
        return this.wifiManagerDelegate.getWifiSSID();
    }

    public void onReceive(Context context2, Intent intent) {
        NetworkState currentNetworkState = getCurrentNetworkState();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            connectionTypeChanged(currentNetworkState);
        }
    }

    private void connectionTypeChanged(NetworkState networkState) {
        ConnectionType connectionType2 = getConnectionType(networkState);
        String wifiSSID2 = getWifiSSID(networkState);
        if (connectionType2 != this.connectionType || !wifiSSID2.equals(this.wifiSSID)) {
            this.connectionType = connectionType2;
            this.wifiSSID = wifiSSID2;
            Logging.d("NetworkMonitorAutoDetect", "Network connectivity changed, type is: " + this.connectionType);
            this.observer.onConnectionTypeChanged(connectionType2);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public static long networkToNetId(Network network) {
        if (Build.VERSION.SDK_INT >= 23) {
            return network.getNetworkHandle();
        }
        return (long) Integer.parseInt(network.toString());
    }
}
