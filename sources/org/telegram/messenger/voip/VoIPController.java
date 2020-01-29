package org.telegram.messenger.voip;

import android.os.SystemClock;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.tgnet.TLRPC;
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
    public static final int PEER_CAP_GROUP_CALLS = 1;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_FAILED = 4;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    protected long callStartTime;
    protected ConnectionStateListener listener;
    protected long nativeInst;

    public interface ConnectionStateListener {
        void onCallUpgradeRequestReceived();

        void onConnectionStateChanged(int i);

        void onGroupCallKeyReceived(byte[] bArr);

        void onGroupCallKeySent();

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

    private native void nativeSendGroupCallKey(long j, byte[] bArr);

    private native void nativeSetAudioOutputGainControlEnabled(long j, boolean z);

    private native void nativeSetConfig(long j, double d, double d2, int i, boolean z, boolean z2, boolean z3, String str, String str2, boolean z4);

    private native void nativeSetEchoCancellationStrength(long j, int i);

    private native void nativeSetEncryptionKey(long j, byte[] bArr, boolean z);

    private native void nativeSetMicMute(long j, boolean z);

    private static native void nativeSetNativeBufferSize(int i);

    private native void nativeSetNetworkType(long j, int i);

    private native void nativeSetProxy(long j, String str, int i, String str2, String str3);

    private native void nativeSetRemoteEndpoints(long j, TLRPC.TL_phoneConnection[] tL_phoneConnectionArr, boolean z, boolean z2, int i);

    public static native void nativeSetVideoRenderer(long j, long j2);

    public static native void nativeSetVideoSource(long j, long j2);

    private native void nativeStart(long j);

    public VoIPController() {
        this.nativeInst = 0;
        this.nativeInst = nativeInit(new File(ApplicationLoader.applicationContext.getFilesDir(), "voip_persistent_state.json").getAbsolutePath());
    }

    public void start() {
        ensureNativeInstance();
        nativeStart(this.nativeInst);
    }

    public void connect() {
        ensureNativeInstance();
        nativeConnect(this.nativeInst);
    }

    public void setRemoteEndpoints(TLRPC.TL_phoneConnection[] tL_phoneConnectionArr, boolean z, boolean z2, int i) {
        if (tL_phoneConnectionArr.length != 0) {
            int i2 = 0;
            while (i2 < tL_phoneConnectionArr.length) {
                TLRPC.TL_phoneConnection tL_phoneConnection = tL_phoneConnectionArr[i2];
                String str = tL_phoneConnection.ip;
                if (str == null || str.length() == 0) {
                    throw new IllegalArgumentException("endpoint " + tL_phoneConnection + " has empty/null ipv4");
                }
                byte[] bArr = tL_phoneConnection.peer_tag;
                if (bArr == null || bArr.length == 16) {
                    i2++;
                } else {
                    throw new IllegalArgumentException("endpoint " + tL_phoneConnection + " has peer_tag of wrong length");
                }
            }
            ensureNativeInstance();
            nativeSetRemoteEndpoints(this.nativeInst, tL_phoneConnectionArr, z, z2, i);
            return;
        }
        throw new IllegalArgumentException("endpoints size is 0");
    }

    public void setEncryptionKey(byte[] bArr, boolean z) {
        if (bArr.length == 256) {
            ensureNativeInstance();
            nativeSetEncryptionKey(this.nativeInst, bArr, z);
            return;
        }
        throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + bArr.length);
    }

    public static void setNativeBufferSize(int i) {
        nativeSetNativeBufferSize(i);
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

    private void handleStateChange(int i) {
        if (i == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onConnectionStateChanged(i);
        }
    }

    private void handleSignalBarsChange(int i) {
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onSignalBarCountChanged(i);
        }
    }

    private void groupCallKeyReceived(byte[] bArr) {
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onGroupCallKeyReceived(bArr);
        }
    }

    private void groupCallKeySent() {
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onGroupCallKeySent();
        }
    }

    private void callUpgradeRequestReceived() {
        ConnectionStateListener connectionStateListener = this.listener;
        if (connectionStateListener != null) {
            connectionStateListener.onCallUpgradeRequestReceived();
        }
    }

    public void setNetworkType(int i) {
        ensureNativeInstance();
        nativeSetNetworkType(this.nativeInst, i);
    }

    public long getCallDuration() {
        return SystemClock.elapsedRealtime() - this.callStartTime;
    }

    public void setMicMute(boolean z) {
        ensureNativeInstance();
        nativeSetMicMute(this.nativeInst, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setConfig(double r17, double r19, int r21, long r22) {
        /*
            r16 = this;
            r14 = r16
            r0 = r22
            r16.ensureNativeInstance()
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 0
            r4 = 16
            if (r2 < r4) goto L_0x0017
            boolean r2 = android.media.audiofx.AcousticEchoCanceler.isAvailable()     // Catch:{ all -> 0x0017 }
            boolean r4 = android.media.audiofx.NoiseSuppressor.isAvailable()     // Catch:{ all -> 0x0018 }
            goto L_0x0019
        L_0x0017:
            r2 = 0
        L_0x0018:
            r4 = 0
        L_0x0019:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r6 = "dbg_dump_call_stats"
            boolean r5 = r5.getBoolean(r6, r3)
            long r6 = r14.nativeInst
            r8 = 1
            if (r2 == 0) goto L_0x0034
            java.lang.String r2 = "use_system_aec"
            boolean r2 = org.telegram.messenger.voip.VoIPServerConfig.getBoolean(r2, r8)
            if (r2 != 0) goto L_0x0032
            goto L_0x0034
        L_0x0032:
            r9 = 0
            goto L_0x0035
        L_0x0034:
            r9 = 1
        L_0x0035:
            if (r4 == 0) goto L_0x0043
            java.lang.String r2 = "use_system_ns"
            boolean r2 = org.telegram.messenger.voip.VoIPServerConfig.getBoolean(r2, r8)
            if (r2 != 0) goto L_0x0041
            goto L_0x0043
        L_0x0041:
            r10 = 0
            goto L_0x0044
        L_0x0043:
            r10 = 1
        L_0x0044:
            r11 = 1
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x0060
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "voip"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.String r0 = r14.getLogFilePath((java.lang.String) r0)
            goto L_0x0064
        L_0x0060:
            java.lang.String r0 = r14.getLogFilePath((long) r0)
        L_0x0064:
            r12 = r0
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0073
            if (r5 == 0) goto L_0x0073
            java.lang.String r0 = "voipStats"
            java.lang.String r0 = r14.getLogFilePath((java.lang.String) r0)
            goto L_0x0074
        L_0x0073:
            r0 = 0
        L_0x0074:
            r13 = r0
            boolean r15 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            r0 = r16
            r1 = r6
            r3 = r17
            r5 = r19
            r7 = r21
            r8 = r9
            r9 = r10
            r10 = r11
            r11 = r12
            r12 = r13
            r13 = r15
            r0.nativeSetConfig(r1, r3, r5, r7, r8, r9, r10, r11, r12, r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPController.setConfig(double, double, int, long):void");
    }

    public void debugCtl(int i, int i2) {
        ensureNativeInstance();
        nativeDebugCtl(this.nativeInst, i, i2);
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

    private String getLogFilePath(String str) {
        Calendar instance = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(instance.get(5)), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(1)), Integer.valueOf(instance.get(11)), Integer.valueOf(instance.get(12)), Integer.valueOf(instance.get(13)), str})).getAbsolutePath();
    }

    private String getLogFilePath(long j) {
        File logsDir = VoIPHelper.getLogsDir();
        if (!BuildVars.DEBUG_VERSION) {
            File[] listFiles = logsDir.listFiles();
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(Arrays.asList(listFiles));
            while (arrayList.size() > 20) {
                File file = (File) arrayList.get(0);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    File file2 = (File) it.next();
                    if (file2.getName().endsWith(".log") && file2.lastModified() < file.lastModified()) {
                        file = file2;
                    }
                }
                file.delete();
                arrayList.remove(file);
            }
        }
        return new File(logsDir, j + ".log").getAbsolutePath();
    }

    public String getDebugLog() {
        ensureNativeInstance();
        return nativeGetDebugLog(this.nativeInst);
    }

    public void setProxy(String str, int i, String str2, String str3) {
        ensureNativeInstance();
        if (str != null) {
            nativeSetProxy(this.nativeInst, str, i, str2, str3);
            return;
        }
        throw new NullPointerException("address can't be null");
    }

    public void setAudioOutputGainControlEnabled(boolean z) {
        ensureNativeInstance();
        nativeSetAudioOutputGainControlEnabled(this.nativeInst, z);
    }

    public int getPeerCapabilities() {
        ensureNativeInstance();
        return nativeGetPeerCapabilities(this.nativeInst);
    }

    public void sendGroupCallKey(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("key can not be null");
        } else if (bArr.length == 256) {
            ensureNativeInstance();
            nativeSendGroupCallKey(this.nativeInst, bArr);
        } else {
            throw new IllegalArgumentException("key must be 256 bytes long, got " + bArr.length);
        }
    }

    public void requestCallUpgrade() {
        ensureNativeInstance();
        nativeRequestCallUpgrade(this.nativeInst);
    }

    public void setEchoCancellationStrength(int i) {
        ensureNativeInstance();
        nativeSetEchoCancellationStrength(this.nativeInst, i);
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
