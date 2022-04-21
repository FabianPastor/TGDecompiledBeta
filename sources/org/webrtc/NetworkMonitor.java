package org.webrtc;

import android.content.Context;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import org.webrtc.NetworkChangeDetector;

public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private volatile NetworkChangeDetector.ConnectionType currentConnectionType;
    private final ArrayList<Long> nativeNetworkObservers;
    private NetworkChangeDetector networkChangeDetector;
    private NetworkChangeDetectorFactory networkChangeDetectorFactory;
    private final Object networkChangeDetectorLock;
    private final ArrayList<NetworkObserver> networkObservers;
    private int numObservers;

    public interface NetworkObserver {
        void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType connectionType);
    }

    private native void nativeNotifyConnectionTypeChanged(long j);

    private native void nativeNotifyOfActiveNetworkList(long j, NetworkChangeDetector.NetworkInformation[] networkInformationArr);

    private native void nativeNotifyOfNetworkConnect(long j, NetworkChangeDetector.NetworkInformation networkInformation);

    private native void nativeNotifyOfNetworkDisconnect(long j, long j2);

    private native void nativeNotifyOfNetworkPreference(long j, NetworkChangeDetector.ConnectionType connectionType, int i);

    private static class InstanceHolder {
        static final NetworkMonitor instance = new NetworkMonitor();

        private InstanceHolder() {
        }
    }

    private NetworkMonitor() {
        this.networkChangeDetectorFactory = new NetworkChangeDetectorFactory() {
            public NetworkChangeDetector create(NetworkChangeDetector.Observer observer, Context context) {
                return new NetworkMonitorAutoDetect(observer, context);
            }
        };
        this.networkChangeDetectorLock = new Object();
        this.nativeNetworkObservers = new ArrayList<>();
        this.networkObservers = new ArrayList<>();
        this.numObservers = 0;
        this.currentConnectionType = NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN;
    }

    public void setNetworkChangeDetectorFactory(NetworkChangeDetectorFactory factory) {
        assertIsTrue(this.numObservers == 0);
        this.networkChangeDetectorFactory = factory;
    }

    @Deprecated
    public static void init(Context context) {
    }

    public static NetworkMonitor getInstance() {
        return InstanceHolder.instance;
    }

    private static void assertIsTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected to be true");
        }
    }

    public void startMonitoring(Context applicationContext) {
        synchronized (this.networkChangeDetectorLock) {
            this.numObservers++;
            if (this.networkChangeDetector == null) {
                this.networkChangeDetector = createNetworkChangeDetector(applicationContext);
            }
            this.currentConnectionType = this.networkChangeDetector.getCurrentConnectionType();
        }
    }

    @Deprecated
    public void startMonitoring() {
        startMonitoring(ContextUtils.getApplicationContext());
    }

    private void startMonitoring(Context applicationContext, long nativeObserver) {
        Logging.d("NetworkMonitor", "Start monitoring with native observer " + nativeObserver);
        startMonitoring(applicationContext != null ? applicationContext : ContextUtils.getApplicationContext());
        synchronized (this.nativeNetworkObservers) {
            this.nativeNetworkObservers.add(Long.valueOf(nativeObserver));
        }
        updateObserverActiveNetworkList(nativeObserver);
        notifyObserversOfConnectionTypeChange(this.currentConnectionType);
    }

    public void stopMonitoring() {
        synchronized (this.networkChangeDetectorLock) {
            int i = this.numObservers - 1;
            this.numObservers = i;
            if (i == 0) {
                this.networkChangeDetector.destroy();
                this.networkChangeDetector = null;
            }
        }
    }

    private void stopMonitoring(long nativeObserver) {
        Logging.d("NetworkMonitor", "Stop monitoring with native observer " + nativeObserver);
        stopMonitoring();
        synchronized (this.nativeNetworkObservers) {
            this.nativeNetworkObservers.remove(Long.valueOf(nativeObserver));
        }
    }

    private boolean networkBindingSupported() {
        boolean z;
        synchronized (this.networkChangeDetectorLock) {
            NetworkChangeDetector networkChangeDetector2 = this.networkChangeDetector;
            z = networkChangeDetector2 != null && networkChangeDetector2.supportNetworkCallback();
        }
        return z;
    }

    private static int androidSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    private NetworkChangeDetector.ConnectionType getCurrentConnectionType() {
        return this.currentConnectionType;
    }

    private NetworkChangeDetector createNetworkChangeDetector(Context appContext) {
        return this.networkChangeDetectorFactory.create(new NetworkChangeDetector.Observer() {
            public void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType newConnectionType) {
                NetworkMonitor.this.updateCurrentConnectionType(newConnectionType);
            }

            public void onNetworkConnect(NetworkChangeDetector.NetworkInformation networkInfo) {
                NetworkMonitor.this.notifyObserversOfNetworkConnect(networkInfo);
            }

            public void onNetworkDisconnect(long networkHandle) {
                NetworkMonitor.this.notifyObserversOfNetworkDisconnect(networkHandle);
            }

            public void onNetworkPreference(List<NetworkChangeDetector.ConnectionType> types, int preference) {
                NetworkMonitor.this.notifyObserversOfNetworkPreference(types, preference);
            }
        }, appContext);
    }

    /* access modifiers changed from: private */
    public void updateCurrentConnectionType(NetworkChangeDetector.ConnectionType newConnectionType) {
        this.currentConnectionType = newConnectionType;
        notifyObserversOfConnectionTypeChange(newConnectionType);
    }

    private void notifyObserversOfConnectionTypeChange(NetworkChangeDetector.ConnectionType newConnectionType) {
        List<NetworkObserver> javaObservers;
        for (Long nativeObserver : getNativeNetworkObserversSync()) {
            nativeNotifyConnectionTypeChanged(nativeObserver.longValue());
        }
        synchronized (this.networkObservers) {
            javaObservers = new ArrayList<>(this.networkObservers);
        }
        for (NetworkObserver observer : javaObservers) {
            observer.onConnectionTypeChanged(newConnectionType);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkConnect(NetworkChangeDetector.NetworkInformation networkInfo) {
        for (Long nativeObserver : getNativeNetworkObserversSync()) {
            nativeNotifyOfNetworkConnect(nativeObserver.longValue(), networkInfo);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkDisconnect(long networkHandle) {
        for (Long nativeObserver : getNativeNetworkObserversSync()) {
            nativeNotifyOfNetworkDisconnect(nativeObserver.longValue(), networkHandle);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkPreference(List<NetworkChangeDetector.ConnectionType> types, int preference) {
        List<Long> nativeObservers = getNativeNetworkObserversSync();
        for (NetworkChangeDetector.ConnectionType type : types) {
            for (Long nativeObserver : nativeObservers) {
                nativeNotifyOfNetworkPreference(nativeObserver.longValue(), type, preference);
            }
        }
    }

    private void updateObserverActiveNetworkList(long nativeObserver) {
        List<NetworkChangeDetector.NetworkInformation> networkInfoList;
        synchronized (this.networkChangeDetectorLock) {
            NetworkChangeDetector networkChangeDetector2 = this.networkChangeDetector;
            networkInfoList = networkChangeDetector2 == null ? null : networkChangeDetector2.getActiveNetworkList();
        }
        if (networkInfoList != null && networkInfoList.size() != 0) {
            nativeNotifyOfActiveNetworkList(nativeObserver, (NetworkChangeDetector.NetworkInformation[]) networkInfoList.toArray(new NetworkChangeDetector.NetworkInformation[networkInfoList.size()]));
        }
    }

    private List<Long> getNativeNetworkObserversSync() {
        ArrayList arrayList;
        synchronized (this.nativeNetworkObservers) {
            arrayList = new ArrayList(this.nativeNetworkObservers);
        }
        return arrayList;
    }

    @Deprecated
    public static void addNetworkObserver(NetworkObserver observer) {
        getInstance().addObserver(observer);
    }

    public void addObserver(NetworkObserver observer) {
        synchronized (this.networkObservers) {
            this.networkObservers.add(observer);
        }
    }

    @Deprecated
    public static void removeNetworkObserver(NetworkObserver observer) {
        getInstance().removeObserver(observer);
    }

    public void removeObserver(NetworkObserver observer) {
        synchronized (this.networkObservers) {
            this.networkObservers.remove(observer);
        }
    }

    public static boolean isOnline() {
        return getInstance().getCurrentConnectionType() != NetworkChangeDetector.ConnectionType.CONNECTION_NONE;
    }

    /* access modifiers changed from: package-private */
    public NetworkChangeDetector getNetworkChangeDetector() {
        NetworkChangeDetector networkChangeDetector2;
        synchronized (this.networkChangeDetectorLock) {
            networkChangeDetector2 = this.networkChangeDetector;
        }
        return networkChangeDetector2;
    }

    /* access modifiers changed from: package-private */
    public int getNumObservers() {
        int i;
        synchronized (this.networkChangeDetectorLock) {
            i = this.numObservers;
        }
        return i;
    }

    static NetworkMonitorAutoDetect createAndSetAutoDetectForTest(Context context) {
        NetworkMonitor networkMonitor = getInstance();
        NetworkChangeDetector networkChangeDetector2 = networkMonitor.createNetworkChangeDetector(context);
        networkMonitor.networkChangeDetector = networkChangeDetector2;
        return (NetworkMonitorAutoDetect) networkChangeDetector2;
    }
}
