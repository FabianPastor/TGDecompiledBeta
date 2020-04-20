package org.telegram.messenger.voip;

import android.util.Base64;
import androidx.collection.ArrayMap;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public final class TgVoip {
    private static final List<String> AVAILABLE_VERSIONS = Arrays.asList(new String[]{"2.4.4", "2.7"});
    private static final List<Integer> AVAILABLE_VERSIONS_IDS = Arrays.asList(new Integer[]{1, 3});
    public static final int DATA_SAVING_ALWAYS = 2;
    public static final int DATA_SAVING_MOBILE = 1;
    public static final int DATA_SAVING_NEVER = 0;
    public static final int DATA_SAVING_ROAMING = 3;
    public static final int ENDPOINT_TYPE_INET = 0;
    public static final int ENDPOINT_TYPE_LAN = 1;
    public static final int ENDPOINT_TYPE_TCP_RELAY = 3;
    public static final int ENDPOINT_TYPE_UDP_RELAY = 2;
    public static final String ERROR_AUDIO_IO = "ERROR_AUDIO_IO";
    public static final String ERROR_CONNECTION_SERVICE = "ERROR_CONNECTION_SERVICE";
    public static final String ERROR_INCOMPATIBLE = "ERROR_INCOMPATIBLE";
    public static final String ERROR_INSECURE_UPGRADE = "ERROR_INSECURE_UPGRADE";
    public static final String ERROR_LOCALIZED = "ERROR_LOCALIZED";
    public static final String ERROR_PEER_OUTDATED = "ERROR_PEER_OUTDATED";
    public static final String ERROR_PRIVACY = "ERROR_PRIVACY";
    public static final String ERROR_TIMEOUT = "ERROR_TIMEOUT";
    public static final String ERROR_UNKNOWN = "ERROR_UNKNOWN";
    private static final int LIB_REVISION = 1;
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
    private static int bufferSize;
    private static Delegate delegate;
    private static Map<String, Delegate> delegateByVersionArray = new ArrayMap(AVAILABLE_VERSIONS.size());
    private static ServerConfig globalServerConfig = new ServerConfig(new JSONObject());

    public interface Delegate {
        int getConnectionMaxLayer();

        String getVersion();

        Instance makeInstance(Config config, String str, Endpoint[] endpointArr, Proxy proxy, int i, EncryptionKey encryptionKey);

        void setBufferSize(int i);

        void setGlobalServerConfig(String str);
    }

    public interface Instance {
        String getDebugInfo();

        String getLastError();

        int getPeerCapabilities();

        byte[] getPersistentState();

        long getPreferredRelayId();

        TrafficStats getTrafficStats();

        void requestCallUpgrade();

        void sendGroupCallKey(byte[] bArr);

        void setAudioOutputGainControlEnabled(boolean z);

        void setEchoCancellationStrength(int i);

        void setMuteMicrophone(boolean z);

        void setNetworkType(int i);

        void setOnSignalBarsUpdatedListener(OnSignalBarsUpdatedListener onSignalBarsUpdatedListener);

        void setOnStateUpdatedListener(OnStateUpdatedListener onStateUpdatedListener);

        FinalState stop();
    }

    public interface OnSignalBarsUpdatedListener {
        void onSignalBarsUpdated(int i);
    }

    public interface OnStateUpdatedListener {
        void onStateUpdated(int i);
    }

    public static int getConnectionMaxLayer() {
        return 92;
    }

    private TgVoip() {
    }

    public static List<String> getAvailableVersions() {
        return AVAILABLE_VERSIONS;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.messenger.voip.TgVoip$Delegate} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0088 A[SYNTHETIC, Splitter:B:42:0x0088] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0090 A[Catch:{ IOException -> 0x008c }] */
    @android.annotation.SuppressLint({"DefaultLocale"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void setNativeVersion(android.content.Context r12, java.lang.String r13) {
        /*
            java.util.Map<java.lang.String, org.telegram.messenger.voip.TgVoip$Delegate> r0 = delegateByVersionArray
            java.lang.Object r0 = r0.get(r13)
            org.telegram.messenger.voip.TgVoip$Delegate r0 = (org.telegram.messenger.voip.TgVoip.Delegate) r0
            if (r0 != 0) goto L_0x014e
            java.util.List<java.lang.String> r0 = AVAILABLE_VERSIONS
            int r0 = r0.indexOf(r13)
            r1 = -1
            r2 = 1
            r3 = 2
            r4 = 0
            if (r0 == r1) goto L_0x0134
            java.lang.String r1 = "dex"
            java.io.File r1 = r12.getDir(r1, r4)
            java.io.File r5 = new java.io.File
            java.lang.String r6 = "libtgvoip.dex"
            r5.<init>(r1, r6)
            boolean r7 = checkDexFile(r5)
            r8 = 0
            if (r7 != 0) goto L_0x0098
            r5.delete()
            java.io.BufferedInputStream r7 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x007b, all -> 0x0078 }
            android.content.res.AssetManager r9 = r12.getAssets()     // Catch:{ IOException -> 0x007b, all -> 0x0078 }
            java.io.InputStream r6 = r9.open(r6)     // Catch:{ IOException -> 0x007b, all -> 0x0078 }
            r7.<init>(r6)     // Catch:{ IOException -> 0x007b, all -> 0x0078 }
            java.io.BufferedOutputStream r6 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0074, all -> 0x0070 }
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0074, all -> 0x0070 }
            r9.<init>(r5)     // Catch:{ IOException -> 0x0074, all -> 0x0070 }
            r6.<init>(r9)     // Catch:{ IOException -> 0x0074, all -> 0x0070 }
            r9 = 4096(0x1000, float:5.74E-42)
            byte[] r10 = new byte[r9]     // Catch:{ IOException -> 0x006e, all -> 0x006c }
        L_0x0048:
            int r11 = r7.read(r10, r4, r9)     // Catch:{ IOException -> 0x006e, all -> 0x006c }
            if (r11 < 0) goto L_0x0052
            r6.write(r10, r4, r11)     // Catch:{ IOException -> 0x006e, all -> 0x006c }
            goto L_0x0048
        L_0x0052:
            r7.close()     // Catch:{ IOException -> 0x0059 }
            r6.close()     // Catch:{ IOException -> 0x0059 }
            goto L_0x005d
        L_0x0059:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x005d:
            boolean r6 = checkDexFile(r5)
            if (r6 == 0) goto L_0x0064
            goto L_0x0098
        L_0x0064:
            java.lang.IllegalStateException r12 = new java.lang.IllegalStateException
            java.lang.String r13 = "incorrect libtgvoip.dex checksum after copying from assets"
            r12.<init>(r13)
            throw r12
        L_0x006c:
            r12 = move-exception
            goto L_0x0072
        L_0x006e:
            r12 = move-exception
            goto L_0x0076
        L_0x0070:
            r12 = move-exception
            r6 = r8
        L_0x0072:
            r8 = r7
            goto L_0x0086
        L_0x0074:
            r12 = move-exception
            r6 = r8
        L_0x0076:
            r8 = r7
            goto L_0x007d
        L_0x0078:
            r12 = move-exception
            r6 = r8
            goto L_0x0086
        L_0x007b:
            r12 = move-exception
            r6 = r8
        L_0x007d:
            java.lang.IllegalStateException r13 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0085 }
            java.lang.String r0 = "failed to copy libtgvoip.dex"
            r13.<init>(r0, r12)     // Catch:{ all -> 0x0085 }
            throw r13     // Catch:{ all -> 0x0085 }
        L_0x0085:
            r12 = move-exception
        L_0x0086:
            if (r8 == 0) goto L_0x008e
            r8.close()     // Catch:{ IOException -> 0x008c }
            goto L_0x008e
        L_0x008c:
            r13 = move-exception
            goto L_0x0094
        L_0x008e:
            if (r6 == 0) goto L_0x0097
            r6.close()     // Catch:{ IOException -> 0x008c }
            goto L_0x0097
        L_0x0094:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x0097:
            throw r12
        L_0x0098:
            java.io.File r6 = new java.io.File
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "tgvoip_cache"
            r7.append(r9)
            r7.append(r13)
            java.lang.String r7 = r7.toString()
            r6.<init>(r1, r7)
            boolean r1 = r6.exists()
            if (r1 == 0) goto L_0x00c1
            boolean r1 = r6.isDirectory()
            if (r1 != 0) goto L_0x00c4
            r6.delete()
            r6.mkdirs()
            goto L_0x00c4
        L_0x00c1:
            r6.mkdirs()
        L_0x00c4:
            dalvik.system.DexClassLoader r1 = new dalvik.system.DexClassLoader
            java.lang.String r5 = r5.getAbsolutePath()
            java.lang.String r6 = r6.getAbsolutePath()
            android.content.pm.ApplicationInfo r7 = r12.getApplicationInfo()
            java.lang.String r7 = r7.nativeLibraryDir
            java.lang.ClassLoader r9 = r12.getClassLoader()
            r1.<init>(r5, r6, r7, r9)
            java.lang.String r5 = "org.telegram.messenger.voip.TgVoipNativeLoader"
            java.lang.Class r5 = r1.loadClass(r5)     // Catch:{ Exception -> 0x011c }
            java.lang.String r6 = "initNativeLib"
            java.lang.Class[] r7 = new java.lang.Class[r3]     // Catch:{ Exception -> 0x011c }
            java.lang.Class<android.content.Context> r9 = android.content.Context.class
            r7[r4] = r9     // Catch:{ Exception -> 0x011c }
            java.lang.Class r9 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x011c }
            r7[r2] = r9     // Catch:{ Exception -> 0x011c }
            java.lang.reflect.Method r5 = r5.getDeclaredMethod(r6, r7)     // Catch:{ Exception -> 0x011c }
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x011c }
            r3[r4] = r12     // Catch:{ Exception -> 0x011c }
            java.util.List<java.lang.Integer> r12 = AVAILABLE_VERSIONS_IDS     // Catch:{ Exception -> 0x011c }
            java.lang.Object r12 = r12.get(r0)     // Catch:{ Exception -> 0x011c }
            r3[r2] = r12     // Catch:{ Exception -> 0x011c }
            r5.invoke(r8, r3)     // Catch:{ Exception -> 0x011c }
            java.lang.String r12 = "org.telegram.messenger.voip.NativeTgVoipDelegate"
            java.lang.Class r12 = r1.loadClass(r12)     // Catch:{ Exception -> 0x0113 }
            java.lang.Object r12 = r12.newInstance()     // Catch:{ Exception -> 0x0113 }
            r0 = r12
            org.telegram.messenger.voip.TgVoip$Delegate r0 = (org.telegram.messenger.voip.TgVoip.Delegate) r0     // Catch:{ Exception -> 0x0113 }
            java.util.Map<java.lang.String, org.telegram.messenger.voip.TgVoip$Delegate> r12 = delegateByVersionArray
            r12.put(r13, r0)
            goto L_0x014e
        L_0x0113:
            r12 = move-exception
            java.lang.IllegalStateException r13 = new java.lang.IllegalStateException
            java.lang.String r0 = "failed to create new instance of tgvoip delegate"
            r13.<init>(r0, r12)
            throw r13
        L_0x011c:
            r12 = move-exception
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "failed to load tgvoip native library version "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            r0.<init>(r13, r12)
            throw r0
        L_0x0134:
            java.lang.IllegalArgumentException r12 = new java.lang.IllegalArgumentException
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r4] = r13
            java.util.List<java.lang.String> r13 = AVAILABLE_VERSIONS
            java.lang.String r1 = ", "
            java.lang.String r13 = android.text.TextUtils.join(r1, r13)
            r0[r2] = r13
            java.lang.String r13 = "tgvoip version %s is not available (available versions = %s)"
            java.lang.String r13 = java.lang.String.format(r13, r0)
            r12.<init>(r13)
            throw r12
        L_0x014e:
            org.telegram.messenger.voip.TgVoip$Delegate r12 = delegate
            if (r12 == r0) goto L_0x017e
            delegate = r0
            org.telegram.messenger.voip.TgVoip$ServerConfig r12 = globalServerConfig
            org.json.JSONObject r12 = r12.jsonObject
            java.lang.String r12 = r12.toString()
            r0.setGlobalServerConfig(r12)
            int r12 = bufferSize
            r0.setBufferSize(r12)
            boolean r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r12 == 0) goto L_0x017e
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r0 = "tgvoip version changed to "
            r12.append(r0)
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            org.telegram.messenger.FileLog.d(r12)
        L_0x017e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.TgVoip.setNativeVersion(android.content.Context, java.lang.String):void");
    }

    private static boolean checkDexFile(File file) {
        if (file != null && file.exists()) {
            try {
                MessageDigest instance = MessageDigest.getInstance("SHA1");
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read > 0) {
                        instance.update(bArr, 0, read);
                    } else {
                        return TgVoipDex.getChecksum().equals(new String(Base64.encode(instance.digest(), 0)).trim());
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        return false;
    }

    public static ServerConfig getGlobalServerConfig() {
        return globalServerConfig;
    }

    public static void setGlobalServerConfig(String str) {
        try {
            globalServerConfig = new ServerConfig(new JSONObject(str));
            if (delegate != null) {
                delegate.setGlobalServerConfig(str);
            }
        } catch (JSONException e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("failed to parse tgvoip server config", e);
            }
        }
    }

    public static Instance makeInstance(Config config, String str, Endpoint[] endpointArr, Proxy proxy, int i, EncryptionKey encryptionKey) {
        checkHasDelegate();
        return delegate.makeInstance(config, str, endpointArr, proxy, i, encryptionKey);
    }

    public static void setBufferSize(int i) {
        bufferSize = i;
        Delegate delegate2 = delegate;
        if (delegate2 != null) {
            delegate2.setBufferSize(i);
        }
    }

    public static String getVersion() {
        Delegate delegate2 = delegate;
        if (delegate2 != null) {
            return delegate2.getVersion();
        }
        return null;
    }

    private static void checkHasDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("tgvoip version is not set");
        }
    }

    public static final class Config {
        public final int dataSaving;
        public final boolean enableAec;
        public final boolean enableAgc;
        public final boolean enableCallUpgrade;
        public final boolean enableNs;
        public final boolean enableP2p;
        public final double initializationTimeout;
        public final String logPath;
        public final int maxApiLayer;
        public final double receiveTimeout;

        public Config(double d, double d2, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, String str, int i2) {
            this.initializationTimeout = d;
            this.receiveTimeout = d2;
            this.dataSaving = i;
            this.enableP2p = z;
            this.enableAec = z2;
            this.enableNs = z3;
            this.enableAgc = z4;
            this.enableCallUpgrade = z5;
            this.logPath = str;
            this.maxApiLayer = i2;
        }

        public String toString() {
            return "Config{initializationTimeout=" + this.initializationTimeout + ", receiveTimeout=" + this.receiveTimeout + ", dataSaving=" + this.dataSaving + ", enableP2p=" + this.enableP2p + ", enableAec=" + this.enableAec + ", enableNs=" + this.enableNs + ", enableAgc=" + this.enableAgc + ", enableCallUpgrade=" + this.enableCallUpgrade + ", logPath='" + this.logPath + '\'' + ", maxApiLayer=" + this.maxApiLayer + '}';
        }
    }

    public static final class Endpoint {
        public final long id;
        public final String ipv4;
        public final String ipv6;
        public final byte[] peerTag;
        public final int port;
        public final int type;

        public Endpoint(long j, String str, String str2, int i, int i2, byte[] bArr) {
            this.id = j;
            this.ipv4 = str;
            this.ipv6 = str2;
            this.port = i;
            this.type = i2;
            this.peerTag = bArr;
        }

        public String toString() {
            return "Endpoint{id=" + this.id + ", ipv4='" + this.ipv4 + '\'' + ", ipv6='" + this.ipv6 + '\'' + ", port=" + this.port + ", type=" + this.type + ", peerTag=" + Arrays.toString(this.peerTag) + '}';
        }
    }

    public static final class Proxy {
        public final String host;
        public final String login;
        public final String password;
        public final int port;

        public Proxy(String str, int i, String str2, String str3) {
            this.host = str;
            this.port = i;
            this.login = str2;
            this.password = str3;
        }

        public String toString() {
            return "Proxy{host='" + this.host + '\'' + ", port=" + this.port + ", login='" + this.login + '\'' + ", password='" + this.password + '\'' + '}';
        }
    }

    public static final class EncryptionKey {
        public final boolean isOutgoing;
        public final byte[] value;

        public EncryptionKey(byte[] bArr, boolean z) {
            this.value = bArr;
            this.isOutgoing = z;
        }

        public String toString() {
            return "EncryptionKey{value=" + Arrays.toString(this.value) + ", isOutgoing=" + this.isOutgoing + '}';
        }
    }

    public static final class FinalState {
        public final String debugLog;
        public final boolean isRatingSuggested;
        public final byte[] persistentState;
        public final TrafficStats trafficStats;

        public FinalState(byte[] bArr, String str, TrafficStats trafficStats2, boolean z) {
            this.persistentState = bArr;
            this.debugLog = str;
            this.trafficStats = trafficStats2;
            this.isRatingSuggested = z;
        }

        public String toString() {
            return "FinalState{persistentState=" + Arrays.toString(this.persistentState) + ", debugLog='" + this.debugLog + '\'' + ", trafficStats=" + this.trafficStats + ", isRatingSuggested=" + this.isRatingSuggested + '}';
        }
    }

    public static final class TrafficStats {
        public final long bytesReceivedMobile;
        public final long bytesReceivedWifi;
        public final long bytesSentMobile;
        public final long bytesSentWifi;

        public TrafficStats(long j, long j2, long j3, long j4) {
            this.bytesSentWifi = j;
            this.bytesReceivedWifi = j2;
            this.bytesSentMobile = j3;
            this.bytesReceivedMobile = j4;
        }

        public String toString() {
            return "TrafficStats{bytesSentWifi=" + this.bytesSentWifi + ", bytesReceivedWifi=" + this.bytesReceivedWifi + ", bytesSentMobile=" + this.bytesSentMobile + ", bytesReceivedMobile=" + this.bytesReceivedMobile + '}';
        }
    }

    public static final class ServerConfig {
        public final double hangupUiTimeout;
        /* access modifiers changed from: private */
        public final JSONObject jsonObject;
        public final boolean useSystemAec;
        public final boolean useSystemNs;

        private ServerConfig(JSONObject jSONObject) {
            this.jsonObject = jSONObject;
            this.useSystemNs = jSONObject.optBoolean("use_system_ns", true);
            this.useSystemAec = jSONObject.optBoolean("use_system_aec", true);
            this.hangupUiTimeout = jSONObject.optDouble("hangup_ui_timeout", 5.0d);
        }

        public String getString(String str) {
            return getString(str, "");
        }

        public String getString(String str, String str2) {
            return this.jsonObject.optString(str, str2);
        }
    }
}
