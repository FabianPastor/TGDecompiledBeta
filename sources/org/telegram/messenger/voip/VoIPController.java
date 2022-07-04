package org.telegram.messenger.voip;

import android.content.SharedPreferences;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.SystemClock;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPController {
    public static final int DATA_SAVING_ALWAYS = 2;
    public static final int DATA_SAVING_MOBILE = 1;
    public static final int DATA_SAVING_NEVER = 0;
    public static final int DATA_SAVING_ROAMING = 3;
    public static final int ERROR_AUDIO_IO = 3;
    public static final int ERROR_CONNECTION_SERVICE = -5;
    public static final int ERROR_INCOMPATIBLE = 1;
    public static final int ERROR_INSECURE_UPGRADE = -4;
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
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    protected long callStartTime;
    protected ConnectionStateListener listener;
    protected long nativeInst = nativeInit(new File(ApplicationLoader.applicationContext.getFilesDir(), "voip_persistent_state.json").getAbsolutePath());

    public interface ConnectionStateListener {
        void onConnectionStateChanged(int i, boolean z);

        void onSignalBarCountChanged(int i);
    }

    public static native int getConnectionMaxLayer();

    private native void nativeConnect(long j);

    private native void nativeDebugCtl(long j, int i, int i2);

    private native String nativeGetDebugLog(long j);

    private native String nativeGetDebugString(long j);

    private native int nativeGetLastError(long j);

    private native int nativeGetPeerCapabilities(long j);

    private native long nativeGetPreferredRelayID(long j);

    private native void nativeGetStats(long j, Stats stats);

    private static native String nativeGetVersion();

    private native long nativeInit(String str);

    private static native boolean nativeNeedRate(long j);

    private native void nativeRelease(long j);

    private native void nativeRequestCallUpgrade(long j);

    private native void nativeSetAudioOutputGainControlEnabled(long j, boolean z);

    private native void nativeSetConfig(long j, double d, double d2, int i, boolean z, boolean z2, boolean z3, String str, String str2, boolean z4);

    private native void nativeSetEchoCancellationStrength(long j, int i);

    private native void nativeSetEncryptionKey(long j, byte[] bArr, boolean z);

    private native void nativeSetMicMute(long j, boolean z);

    private static native void nativeSetNativeBufferSize(int i);

    private native void nativeSetNetworkType(long j, int i);

    private native void nativeSetProxy(long j, String str, int i, String str2, String str3);

    private native void nativeStart(long j);

    public void start() {
        ensureNativeInstance();
        nativeStart(this.nativeInst);
    }

    public void connect() {
        ensureNativeInstance();
        nativeConnect(this.nativeInst);
    }

    public void setEncryptionKey(byte[] key, boolean isOutgoing) {
        if (key.length == 256) {
            ensureNativeInstance();
            nativeSetEncryptionKey(this.nativeInst, key, isOutgoing);
            return;
        }
        throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + key.length);
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

    /* access modifiers changed from: protected */
    public void ensureNativeInstance() {
        if (this.nativeInst == 0) {
            throw new IllegalStateException("Native instance is not valid");
        }
    }

    public void setConnectionStateListener(ConnectionStateListener connectionStateListener) {
        this.listener = connectionStateListener;
    }

    private void handleStateChange(int state) {
        if (state == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onConnectionStateChanged(state, false);
        }
    }

    private void handleSignalBarsChange(int count) {
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onSignalBarCountChanged(count);
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

    public void setConfig(double recvTimeout, double initTimeout, int dataSavingOption, long callID) {
        boolean sysNsAvailable;
        boolean sysAecAvailable;
        String str;
        long j = callID;
        ensureNativeInstance();
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                sysAecAvailable = AcousticEchoCanceler.isAvailable();
                sysNsAvailable = NoiseSuppressor.isAvailable();
            } catch (Throwable th) {
                sysAecAvailable = false;
                sysNsAvailable = false;
            }
        } else {
            sysAecAvailable = false;
            sysNsAvailable = false;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean dump = preferences.getBoolean("dbg_dump_call_stats", false);
        long j2 = this.nativeInst;
        boolean z = !sysAecAvailable || !VoIPServerConfig.getBoolean("use_system_aec", true);
        boolean z2 = !sysNsAvailable || !VoIPServerConfig.getBoolean("use_system_ns", true);
        if (BuildVars.DEBUG_VERSION) {
            str = getLogFilePath("voip" + j);
        } else {
            str = getLogFilePath(j);
        }
        SharedPreferences sharedPreferences = preferences;
        nativeSetConfig(j2, recvTimeout, initTimeout, dataSavingOption, z, z2, true, str, (!BuildVars.DEBUG_VERSION || !dump) ? null : getLogFilePath("voipStats"), BuildVars.DEBUG_VERSION);
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
        if (stats != null) {
            nativeGetStats(this.nativeInst, stats);
            return;
        }
        throw new NullPointerException("You're not supposed to pass null here");
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    private String getLogFilePath(String name) {
        Calendar c = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(c.get(5)), Integer.valueOf(c.get(2) + 1), Integer.valueOf(c.get(1)), Integer.valueOf(c.get(11)), Integer.valueOf(c.get(12)), Integer.valueOf(c.get(13)), name})).getAbsolutePath();
    }

    private String getLogFilePath(long callID) {
        File dir = VoIPHelper.getLogsDir();
        if (!BuildVars.DEBUG_VERSION) {
            ArrayList<File> logs = new ArrayList<>(Arrays.asList(dir.listFiles()));
            while (logs.size() > 20) {
                File oldest = logs.get(0);
                Iterator<File> it = logs.iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    if (file.getName().endsWith(".log") && file.lastModified() < oldest.lastModified()) {
                        oldest = file;
                    }
                }
                oldest.delete();
                logs.remove(oldest);
            }
        }
        return new File(dir, callID + ".log").getAbsolutePath();
    }

    public String getDebugLog() {
        ensureNativeInstance();
        return nativeGetDebugLog(this.nativeInst);
    }

    public void setProxy(String address, int port, String username, String password) {
        ensureNativeInstance();
        if (address != null) {
            nativeSetProxy(this.nativeInst, address, port, username, password);
            return;
        }
        throw new NullPointerException("address can't be null");
    }

    public void setAudioOutputGainControlEnabled(boolean enabled) {
        ensureNativeInstance();
        nativeSetAudioOutputGainControlEnabled(this.nativeInst, enabled);
    }

    public int getPeerCapabilities() {
        ensureNativeInstance();
        return nativeGetPeerCapabilities(this.nativeInst);
    }

    public void requestCallUpgrade() {
        ensureNativeInstance();
        nativeRequestCallUpgrade(this.nativeInst);
    }

    public void setEchoCancellationStrength(int strength) {
        ensureNativeInstance();
        nativeSetEchoCancellationStrength(this.nativeInst, strength);
    }

    public boolean needRate() {
        ensureNativeInstance();
        return nativeNeedRate(this.nativeInst);
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
}
