package org.telegram.messenger.voip;

import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build.VERSION;
import android.os.SystemClock;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.tgnet.TLRPC.TL_phoneConnection;

public class VoIPController {
    public static final int DATA_SAVING_ALWAYS = 2;
    public static final int DATA_SAVING_MOBILE = 1;
    public static final int DATA_SAVING_NEVER = 0;
    public static final int ERROR_AUDIO_IO = 3;
    public static final int ERROR_INCOMPATIBLE = 1;
    public static final int ERROR_LOCALIZED = -3;
    public static final int ERROR_PEER_OUTDATED = -1;
    public static final int ERROR_PRIVACY = -2;
    public static final int ERROR_TIMEOUT = 2;
    public static final int ERROR_UNKNOWN = 0;
    public static final int NET_TYPE_3G = 3;
    public static final int NET_TYPE_DIALUP = 10;
    public static final int NET_TYPE_EDGE = 2;
    public static final int NET_TYPE_ETHERNET = 7;
    public static final int NET_TYPE_GPRS = 1;
    public static final int NET_TYPE_HSPA = 4;
    public static final int NET_TYPE_LTE = 5;
    public static final int NET_TYPE_OTHER_HIGH_SPEED = 8;
    public static final int NET_TYPE_OTHER_LOW_SPEED = 9;
    public static final int NET_TYPE_OTHER_MOBILE = 11;
    public static final int NET_TYPE_UNKNOWN = 0;
    public static final int NET_TYPE_WIFI = 6;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_FAILED = 4;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    private long callStartTime;
    private ConnectionStateListener listener;
    private long nativeInst;

    public interface ConnectionStateListener {
        void onConnectionStateChanged(int i);
    }

    public static class Stats {
        public long bytesRecvdMobile;
        public long bytesRecvdWifi;
        public long bytesSentMobile;
        public long bytesSentWifi;

        public String toString() {
            return "Stats{bytesRecvdMobile=" + this.bytesRecvdMobile + ", bytesSentWifi=" + this.bytesSentWifi + ", bytesRecvdWifi=" + this.bytesRecvdWifi + ", bytesSentMobile=" + this.bytesSentMobile + '}';
        }
    }

    private native void nativeConnect(long j);

    private native void nativeDebugCtl(long j, int i, int i2);

    private native String nativeGetDebugLog(long j);

    private native String nativeGetDebugString(long j);

    private native int nativeGetLastError(long j);

    private native long nativeGetPreferredRelayID(long j);

    private native void nativeGetStats(long j, Stats stats);

    private static native String nativeGetVersion();

    private native long nativeInit(int i);

    private native void nativeRelease(long j);

    private native void nativeSetConfig(long j, double d, double d2, int i, boolean z, boolean z2, boolean z3, String str, String str2);

    private native void nativeSetEncryptionKey(long j, byte[] bArr, boolean z);

    private native void nativeSetMicMute(long j, boolean z);

    private static native void nativeSetNativeBufferSize(int i);

    private native void nativeSetNetworkType(long j, int i);

    private native void nativeSetRemoteEndpoints(long j, TL_phoneConnection[] tL_phoneConnectionArr, boolean z);

    private native void nativeStart(long j);

    public VoIPController() {
        this.nativeInst = 0;
        this.nativeInst = nativeInit(VERSION.SDK_INT);
    }

    public void start() {
        ensureNativeInstance();
        nativeStart(this.nativeInst);
    }

    public void connect() {
        ensureNativeInstance();
        nativeConnect(this.nativeInst);
    }

    public void setRemoteEndpoints(TL_phoneConnection[] endpoints, boolean allowP2p) {
        if (endpoints.length == 0) {
            throw new IllegalArgumentException("endpoints size is 0");
        }
        int a = 0;
        while (a < endpoints.length) {
            TL_phoneConnection endpoint = endpoints[a];
            if (endpoint.ip == null || endpoint.ip.length() == 0) {
                throw new IllegalArgumentException("endpoint " + endpoint + " has empty/null ipv4");
            } else if (endpoint.peer_tag == null || endpoint.peer_tag.length == 16) {
                a++;
            } else {
                throw new IllegalArgumentException("endpoint " + endpoint + " has peer_tag of wrong length");
            }
        }
        ensureNativeInstance();
        nativeSetRemoteEndpoints(this.nativeInst, endpoints, allowP2p);
    }

    public void setEncryptionKey(byte[] key, boolean isOutgoing) {
        if (key.length != 256) {
            throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + key.length);
        }
        ensureNativeInstance();
        nativeSetEncryptionKey(this.nativeInst, key, isOutgoing);
    }

    public static void setNativeBufferSize(int size) {
        nativeSetNativeBufferSize(size);
    }

    public void release() {
        ensureNativeInstance();
        nativeRelease(this.nativeInst);
        this.nativeInst = 0;
    }

    public String getDebugString() {
        ensureNativeInstance();
        return nativeGetDebugString(this.nativeInst);
    }

    private void ensureNativeInstance() {
        if (this.nativeInst == 0) {
            throw new IllegalStateException("Native instance is not valid");
        }
    }

    public void setConnectionStateListener(ConnectionStateListener connectionStateListener) {
        this.listener = connectionStateListener;
    }

    private void handleStateChange(int state) {
        this.callStartTime = SystemClock.elapsedRealtime();
        if (this.listener != null) {
            this.listener.onConnectionStateChanged(state);
        }
    }

    public void setNetworkType(int type) {
        ensureNativeInstance();
        nativeSetNetworkType(this.nativeInst, type);
    }

    public long getCallDuration() {
        return SystemClock.elapsedRealtime() - this.callStartTime;
    }

    public void setMicMute(boolean mute) {
        ensureNativeInstance();
        nativeSetMicMute(this.nativeInst, mute);
    }

    public void setConfig(double recvTimeout, double initTimeout, int dataSavingOption) {
        String logFilePath;
        ensureNativeInstance();
        boolean sysAecAvailable = false;
        boolean sysNsAvailable = false;
        if (VERSION.SDK_INT >= 16) {
            try {
                sysAecAvailable = AcousticEchoCanceler.isAvailable();
                sysNsAvailable = AcousticEchoCanceler.isAvailable();
            } catch (Throwable th) {
            }
        }
        boolean dump = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("dbg_dump_call_stats", false);
        long j = this.nativeInst;
        boolean z = (VERSION.SDK_INT >= 16 && sysAecAvailable && VoIPServerConfig.getBoolean("use_system_aec", true)) ? false : true;
        boolean z2 = (VERSION.SDK_INT >= 16 && sysNsAvailable && VoIPServerConfig.getBoolean("use_system_ns", true)) ? false : true;
        String logFilePath2 = BuildConfig.DEBUG ? getLogFilePath("voip") : null;
        if (BuildConfig.DEBUG && dump) {
            logFilePath = getLogFilePath("voipStats");
        } else {
            logFilePath = null;
        }
        nativeSetConfig(j, recvTimeout, initTimeout, dataSavingOption, z, z2, true, logFilePath2, logFilePath);
    }

    public void debugCtl(int request, int param) {
        ensureNativeInstance();
        nativeDebugCtl(this.nativeInst, request, param);
    }

    public long getPreferredRelayID() {
        ensureNativeInstance();
        return nativeGetPreferredRelayID(this.nativeInst);
    }

    public int getLastError() {
        ensureNativeInstance();
        return nativeGetLastError(this.nativeInst);
    }

    public void getStats(Stats stats) {
        ensureNativeInstance();
        if (stats == null) {
            throw new NullPointerException("You're not supposed to pass null here");
        }
        nativeGetStats(this.nativeInst, stats);
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    private String getLogFilePath(String name) {
        Calendar c = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(c.get(5)), Integer.valueOf(c.get(2) + 1), Integer.valueOf(c.get(1)), Integer.valueOf(c.get(11)), Integer.valueOf(c.get(12)), Integer.valueOf(c.get(13)), name})).getAbsolutePath();
    }

    public String getDebugLog() {
        ensureNativeInstance();
        return nativeGetDebugLog(this.nativeInst);
    }
}
