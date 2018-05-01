package org.telegram.messenger.voip;

import android.os.SystemClock;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.tgnet.TLRPC.TL_phoneConnection;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPController {
    public static final int DATA_SAVING_ALWAYS = 2;
    public static final int DATA_SAVING_MOBILE = 1;
    public static final int DATA_SAVING_NEVER = 0;
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

    public static class Stats {
        public long bytesRecvdMobile;
        public long bytesRecvdWifi;
        public long bytesSentMobile;
        public long bytesSentWifi;

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Stats{bytesRecvdMobile=");
            stringBuilder.append(this.bytesRecvdMobile);
            stringBuilder.append(", bytesSentWifi=");
            stringBuilder.append(this.bytesSentWifi);
            stringBuilder.append(", bytesRecvdWifi=");
            stringBuilder.append(this.bytesRecvdWifi);
            stringBuilder.append(", bytesSentMobile=");
            stringBuilder.append(this.bytesSentMobile);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }
    }

    private native void nativeConnect(long j);

    private native void nativeDebugCtl(long j, int i, int i2);

    private native String nativeGetDebugLog(long j);

    private native String nativeGetDebugString(long j);

    private native int nativeGetLastError(long j);

    private native int nativeGetPeerCapabilities(long j);

    private native long nativeGetPreferredRelayID(long j);

    private native void nativeGetStats(long j, Stats stats);

    private static native String nativeGetVersion();

    private native long nativeInit();

    private native void nativeRelease(long j);

    private native void nativeRequestCallUpgrade(long j);

    private native void nativeSendGroupCallKey(long j, byte[] bArr);

    private native void nativeSetAudioOutputGainControlEnabled(long j, boolean z);

    private native void nativeSetConfig(long j, double d, double d2, int i, boolean z, boolean z2, boolean z3, String str, String str2);

    private native void nativeSetEchoCancellationStrength(long j, int i);

    private native void nativeSetEncryptionKey(long j, byte[] bArr, boolean z);

    private native void nativeSetMicMute(long j, boolean z);

    private static native void nativeSetNativeBufferSize(int i);

    private native void nativeSetNetworkType(long j, int i);

    private native void nativeSetProxy(long j, String str, int i, String str2, String str3);

    private native void nativeSetRemoteEndpoints(long j, TL_phoneConnection[] tL_phoneConnectionArr, boolean z, boolean z2, int i);

    private native void nativeStart(long j);

    public VoIPController() {
        this.nativeInst = 0;
        this.nativeInst = nativeInit();
    }

    public void start() {
        ensureNativeInstance();
        nativeStart(this.nativeInst);
    }

    public void connect() {
        ensureNativeInstance();
        nativeConnect(this.nativeInst);
    }

    public void setRemoteEndpoints(TL_phoneConnection[] tL_phoneConnectionArr, boolean z, boolean z2, int i) {
        if (tL_phoneConnectionArr.length == 0) {
            throw new IllegalArgumentException("endpoints size is 0");
        }
        int i2 = 0;
        while (i2 < tL_phoneConnectionArr.length) {
            TL_phoneConnection tL_phoneConnection = tL_phoneConnectionArr[i2];
            if (tL_phoneConnection.ip != null) {
                if (tL_phoneConnection.ip.length() != 0) {
                    if (tL_phoneConnection.peer_tag == null || tL_phoneConnection.peer_tag.length == 16) {
                        i2++;
                    } else {
                        z = new StringBuilder();
                        z.append("endpoint ");
                        z.append(tL_phoneConnection);
                        z.append(" has peer_tag of wrong length");
                        throw new IllegalArgumentException(z.toString());
                    }
                }
            }
            z = new StringBuilder();
            z.append("endpoint ");
            z.append(tL_phoneConnection);
            z.append(" has empty/null ipv4");
            throw new IllegalArgumentException(z.toString());
        }
        ensureNativeInstance();
        nativeSetRemoteEndpoints(this.nativeInst, tL_phoneConnectionArr, z, z2, i);
    }

    public void setEncryptionKey(byte[] bArr, boolean z) {
        if (bArr.length != 256) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("key length must be exactly 256 bytes but is ");
            stringBuilder.append(bArr.length);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        ensureNativeInstance();
        nativeSetEncryptionKey(this.nativeInst, bArr, z);
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

    protected void ensureNativeInstance() {
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
        if (this.listener != null) {
            this.listener.onConnectionStateChanged(i);
        }
    }

    private void handleSignalBarsChange(int i) {
        if (this.listener != null) {
            this.listener.onSignalBarCountChanged(i);
        }
    }

    private void groupCallKeyReceived(byte[] bArr) {
        if (this.listener != null) {
            this.listener.onGroupCallKeyReceived(bArr);
        }
    }

    private void groupCallKeySent() {
        if (this.listener != null) {
            this.listener.onGroupCallKeySent();
        }
    }

    private void callUpgradeRequestReceived() {
        if (this.listener != null) {
            this.listener.onCallUpgradeRequestReceived();
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

    public void setConfig(double r15, double r17, int r19, long r20) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r14 = this;
        r13 = r14;
        r13.ensureNativeInstance();
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 0;
        r2 = 16;
        if (r0 < r2) goto L_0x0017;
    L_0x000b:
        r0 = android.media.audiofx.AcousticEchoCanceler.isAvailable();	 Catch:{ Throwable -> 0x0014 }
        r2 = android.media.audiofx.AcousticEchoCanceler.isAvailable();	 Catch:{ Throwable -> 0x0015 }
        goto L_0x0019;
    L_0x0014:
        r0 = r1;
    L_0x0015:
        r2 = r1;
        goto L_0x0019;
    L_0x0017:
        r0 = r1;
        r2 = r0;
    L_0x0019:
        r3 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r4 = "dbg_dump_call_stats";
        r3.getBoolean(r4, r1);
        r3 = r13.nativeInst;
        r5 = 1;
        if (r0 == 0) goto L_0x0032;
    L_0x0027:
        r0 = "use_system_aec";
        r0 = org.telegram.messenger.voip.VoIPServerConfig.getBoolean(r0, r5);
        if (r0 != 0) goto L_0x0030;
    L_0x002f:
        goto L_0x0032;
    L_0x0030:
        r8 = r1;
        goto L_0x0033;
    L_0x0032:
        r8 = r5;
    L_0x0033:
        if (r2 == 0) goto L_0x0040;
    L_0x0035:
        r0 = "use_system_ns";
        r0 = org.telegram.messenger.voip.VoIPServerConfig.getBoolean(r0, r5);
        if (r0 != 0) goto L_0x003e;
    L_0x003d:
        goto L_0x0040;
    L_0x003e:
        r9 = r1;
        goto L_0x0041;
    L_0x0040:
        r9 = r5;
    L_0x0041:
        r10 = 1;
        r0 = r20;
        r11 = r13.getLogFilePath(r0);
        r12 = 0;
        r0 = r13;
        r1 = r3;
        r3 = r15;
        r5 = r17;
        r7 = r19;
        r0.nativeSetConfig(r1, r3, r5, r7, r8, r9, r10, r11, r12);
        return;
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
        if (stats == null) {
            throw new NullPointerException("You're not supposed to pass null here");
        }
        nativeGetStats(this.nativeInst, stats);
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    private String getLogFilePath(String str) {
        Calendar instance = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(instance.get(5)), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(1)), Integer.valueOf(instance.get(11)), Integer.valueOf(instance.get(12)), Integer.valueOf(instance.get(13)), str})).getAbsolutePath();
    }

    private String getLogFilePath(long j) {
        File logsDir = VoIPHelper.getLogsDir();
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j);
        stringBuilder.append(".log");
        return new File(logsDir, stringBuilder.toString()).getAbsolutePath();
    }

    public String getDebugLog() {
        ensureNativeInstance();
        return nativeGetDebugLog(this.nativeInst);
    }

    public void setProxy(String str, int i, String str2, String str3) {
        ensureNativeInstance();
        if (str == null) {
            throw new NullPointerException("address can't be null");
        }
        nativeSetProxy(this.nativeInst, str, i, str2, str3);
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
        } else if (bArr.length != 256) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("key must be 256 bytes long, got ");
            stringBuilder.append(bArr.length);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else {
            ensureNativeInstance();
            nativeSendGroupCallKey(this.nativeInst, bArr);
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
}
