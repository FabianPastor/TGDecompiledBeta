package org.telegram.messenger.voip;

import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.webrtc.ContextUtils;
import org.webrtc.VideoSink;

public final class Instance {
    public static final int AUDIO_STATE_ACTIVE = 1;
    public static final int AUDIO_STATE_MUTED = 0;
    public static final List<String> AVAILABLE_VERSIONS = Arrays.asList(new String[]{"2.7.7", "2.4.4"});
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
    public static final int VIDEO_STATE_ACTIVE = 2;
    public static final int VIDEO_STATE_INACTIVE = 0;
    public static final int VIDEO_STATE_PAUSED = 1;
    private static int bufferSize;
    private static ServerConfig globalServerConfig = new ServerConfig(new JSONObject());
    private static NativeInstance instance;

    public interface OnRemoteMediaStateUpdatedListener {
        void onMediaStateUpdated(int i, int i2);
    }

    public interface OnSignalBarsUpdatedListener {
        void onSignalBarsUpdated(int i);
    }

    public interface OnSignalingDataListener {
        void onSignalingData(byte[] bArr);
    }

    public interface OnStateUpdatedListener {
        void onStateUpdated(int i);
    }

    public static int getConnectionMaxLayer() {
        return 92;
    }

    private Instance() {
    }

    public static ServerConfig getGlobalServerConfig() {
        return globalServerConfig;
    }

    public static void setGlobalServerConfig(String str) {
        try {
            globalServerConfig = new ServerConfig(new JSONObject(str));
            if (instance != null) {
                instance.setGlobalServerConfig(str);
            }
        } catch (JSONException e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("failed to parse tgvoip server config", e);
            }
        }
    }

    public static void destroyInstance() {
        instance = null;
    }

    public static NativeInstance makeInstance(String str, Config config, String str2, Endpoint[] endpointArr, Proxy proxy, int i, EncryptionKey encryptionKey, VideoSink videoSink, long j) {
        if (!"2.4.4".equals(str)) {
            ContextUtils.initialize(ApplicationLoader.applicationContext);
        }
        instance = NativeInstance.make(str, config, str2, endpointArr, proxy, i, encryptionKey, videoSink, j);
        setGlobalServerConfig(globalServerConfig.jsonObject.toString());
        setBufferSize(bufferSize);
        return instance;
    }

    public static void setBufferSize(int i) {
        bufferSize = i;
        NativeInstance nativeInstance = instance;
        if (nativeInstance != null) {
            nativeInstance.setBufferSize(i);
        }
    }

    public static String getVersion() {
        NativeInstance nativeInstance = instance;
        if (nativeInstance != null) {
            return nativeInstance.getVersion();
        }
        return null;
    }

    private static void checkHasDelegate() {
        if (instance == null) {
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
        public final boolean isRtc;
        public final String password;
        public final byte[] peerTag;
        public final int port;
        public final boolean stun;
        public final boolean turn;
        public final int type;
        public final String username;

        public Endpoint(boolean z, long j, String str, String str2, int i, int i2, byte[] bArr, boolean z2, boolean z3, String str3, String str4) {
            this.isRtc = z;
            this.id = j;
            this.ipv4 = str;
            this.ipv6 = str2;
            this.port = i;
            this.type = i2;
            this.peerTag = bArr;
            this.turn = z2;
            this.stun = z3;
            this.username = str3;
            this.password = str4;
        }

        public String toString() {
            return "Endpoint{id=" + this.id + ", ipv4='" + this.ipv4 + '\'' + ", ipv6='" + this.ipv6 + '\'' + ", port=" + this.port + ", type=" + this.type + ", peerTag=" + Arrays.toString(this.peerTag) + ", turn=" + this.turn + ", stun=" + this.stun + ", username=" + this.username + ", password=" + this.password + '}';
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
