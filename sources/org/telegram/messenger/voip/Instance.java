package org.telegram.messenger.voip;

import android.os.Build;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.NativeInstance;
import org.webrtc.ContextUtils;
import org.webrtc.VideoSink;

public final class Instance {
    public static final int AUDIO_STATE_ACTIVE = 1;
    public static final int AUDIO_STATE_MUTED = 0;
    public static final List<String> AVAILABLE_VERSIONS;
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
        void onStateUpdated(int i, boolean z);
    }

    static {
        List<String> list;
        if (Build.VERSION.SDK_INT >= 18) {
            list = Arrays.asList(new String[]{"3.0.0", "2.7.7", "2.4.4"});
        } else {
            list = Arrays.asList(new String[]{"2.4.4"});
        }
        AVAILABLE_VERSIONS = list;
    }

    private Instance() {
    }

    public static ServerConfig getGlobalServerConfig() {
        return globalServerConfig;
    }

    public static void setGlobalServerConfig(String serverConfigJson) {
        try {
            globalServerConfig = new ServerConfig(new JSONObject(serverConfigJson));
            NativeInstance nativeInstance = instance;
            if (nativeInstance != null) {
                nativeInstance.setGlobalServerConfig(serverConfigJson);
            }
        } catch (JSONException e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("failed to parse tgvoip server config", (Throwable) e);
            }
        }
    }

    public static void destroyInstance() {
        instance = null;
    }

    public static NativeInstance makeInstance(String version, Config config, String persistentStateFilePath, Endpoint[] endpoints, Proxy proxy, int networkType, EncryptionKey encryptionKey, VideoSink remoteSink, long videoCapturer, NativeInstance.AudioLevelsCallback audioLevelsCallback) {
        if (!"2.4.4".equals(version)) {
            ContextUtils.initialize(ApplicationLoader.applicationContext);
        }
        instance = NativeInstance.make(version, config, persistentStateFilePath, endpoints, proxy, networkType, encryptionKey, remoteSink, videoCapturer, audioLevelsCallback);
        setGlobalServerConfig(globalServerConfig.jsonObject.toString());
        setBufferSize(bufferSize);
        return instance;
    }

    public static void setBufferSize(int size) {
        bufferSize = size;
        NativeInstance nativeInstance = instance;
        if (nativeInstance != null) {
            nativeInstance.setBufferSize(size);
        }
    }

    public static int getConnectionMaxLayer() {
        return 92;
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
        public final boolean enableSm;
        public final double initializationTimeout;
        public final String logPath;
        public final int maxApiLayer;
        public final double receiveTimeout;
        public final String statsLogPath;

        public Config(double initializationTimeout2, double receiveTimeout2, int dataSaving2, boolean enableP2p2, boolean enableAec2, boolean enableNs2, boolean enableAgc2, boolean enableCallUpgrade2, boolean enableSm2, String logPath2, String statsLogPath2, int maxApiLayer2) {
            this.initializationTimeout = initializationTimeout2;
            this.receiveTimeout = receiveTimeout2;
            this.dataSaving = dataSaving2;
            this.enableP2p = enableP2p2;
            this.enableAec = enableAec2;
            this.enableNs = enableNs2;
            this.enableAgc = enableAgc2;
            this.enableCallUpgrade = enableCallUpgrade2;
            this.logPath = logPath2;
            this.statsLogPath = statsLogPath2;
            this.maxApiLayer = maxApiLayer2;
            this.enableSm = enableSm2;
        }

        public String toString() {
            return "Config{initializationTimeout=" + this.initializationTimeout + ", receiveTimeout=" + this.receiveTimeout + ", dataSaving=" + this.dataSaving + ", enableP2p=" + this.enableP2p + ", enableAec=" + this.enableAec + ", enableNs=" + this.enableNs + ", enableAgc=" + this.enableAgc + ", enableCallUpgrade=" + this.enableCallUpgrade + ", logPath='" + this.logPath + '\'' + ", statsLogPath='" + this.statsLogPath + '\'' + ", maxApiLayer=" + this.maxApiLayer + ", enableSm=" + this.enableSm + '}';
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

        public Endpoint(boolean isRtc2, long id2, String ipv42, String ipv62, int port2, int type2, byte[] peerTag2, boolean turn2, boolean stun2, String username2, String password2) {
            this.isRtc = isRtc2;
            this.id = id2;
            this.ipv4 = ipv42;
            this.ipv6 = ipv62;
            this.port = port2;
            this.type = type2;
            this.peerTag = peerTag2;
            this.turn = turn2;
            this.stun = stun2;
            this.username = username2;
            this.password = password2;
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

        public Proxy(String host2, int port2, String login2, String password2) {
            this.host = host2;
            this.port = port2;
            this.login = login2;
            this.password = password2;
        }

        public String toString() {
            return "Proxy{host='" + this.host + '\'' + ", port=" + this.port + ", login='" + this.login + '\'' + ", password='" + this.password + '\'' + '}';
        }
    }

    public static final class EncryptionKey {
        public final boolean isOutgoing;
        public final byte[] value;

        public EncryptionKey(byte[] value2, boolean isOutgoing2) {
            this.value = value2;
            this.isOutgoing = isOutgoing2;
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

        public FinalState(byte[] persistentState2, String debugLog2, TrafficStats trafficStats2, boolean isRatingSuggested2) {
            this.persistentState = persistentState2;
            this.debugLog = debugLog2;
            this.trafficStats = trafficStats2;
            this.isRatingSuggested = isRatingSuggested2;
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

        public TrafficStats(long bytesSentWifi2, long bytesReceivedWifi2, long bytesSentMobile2, long bytesReceivedMobile2) {
            this.bytesSentWifi = bytesSentWifi2;
            this.bytesReceivedWifi = bytesReceivedWifi2;
            this.bytesSentMobile = bytesSentMobile2;
            this.bytesReceivedMobile = bytesReceivedMobile2;
        }

        public String toString() {
            return "TrafficStats{bytesSentWifi=" + this.bytesSentWifi + ", bytesReceivedWifi=" + this.bytesReceivedWifi + ", bytesSentMobile=" + this.bytesSentMobile + ", bytesReceivedMobile=" + this.bytesReceivedMobile + '}';
        }
    }

    public static final class Fingerprint {
        public final String fingerprint;
        public final String hash;
        public final String setup;

        public Fingerprint(String hash2, String setup2, String fingerprint2) {
            this.hash = hash2;
            this.setup = setup2;
            this.fingerprint = fingerprint2;
        }

        public String toString() {
            return "Fingerprint{hash=" + this.hash + ", setup=" + this.setup + ", fingerprint=" + this.fingerprint + '}';
        }
    }

    public static final class Candidate {
        public final String component;
        public final String foundation;
        public final String generation;
        public final String id;
        public final String ip;
        public final String network;
        public final String port;
        public final String priority;
        public final String protocol;
        public final String relAddr;
        public final String relPort;
        public final String tcpType;
        public final String type;

        public Candidate(String port2, String protocol2, String network2, String generation2, String id2, String component2, String foundation2, String priority2, String ip2, String type2, String tcpType2, String relAddr2, String relPort2) {
            this.port = port2;
            this.protocol = protocol2;
            this.network = network2;
            this.generation = generation2;
            this.id = id2;
            this.component = component2;
            this.foundation = foundation2;
            this.priority = priority2;
            this.ip = ip2;
            this.type = type2;
            this.tcpType = tcpType2;
            this.relAddr = relAddr2;
            this.relPort = relPort2;
        }

        public String toString() {
            return "Candidate{port=" + this.port + ", protocol=" + this.protocol + ", network=" + this.network + ", generation=" + this.generation + ", id=" + this.id + ", component=" + this.component + ", foundation=" + this.foundation + ", priority=" + this.priority + ", ip=" + this.ip + ", type=" + this.type + ", tcpType=" + this.tcpType + ", relAddr=" + this.relAddr + ", relPort=" + this.relPort + '}';
        }
    }

    public static final class ServerConfig {
        public final boolean enableStunMarking;
        public final boolean enable_h264_decoder;
        public final boolean enable_h264_encoder;
        public final boolean enable_h265_decoder;
        public final boolean enable_h265_encoder;
        public final boolean enable_vp8_decoder;
        public final boolean enable_vp8_encoder;
        public final boolean enable_vp9_decoder;
        public final boolean enable_vp9_encoder;
        public final double hangupUiTimeout;
        /* access modifiers changed from: private */
        public final JSONObject jsonObject;
        public final boolean useSystemAec;
        public final boolean useSystemNs;

        private ServerConfig(JSONObject jsonObject2) {
            this.jsonObject = jsonObject2;
            this.useSystemNs = jsonObject2.optBoolean("use_system_ns", true);
            this.useSystemAec = jsonObject2.optBoolean("use_system_aec", true);
            this.enableStunMarking = jsonObject2.optBoolean("voip_enable_stun_marking", false);
            this.hangupUiTimeout = jsonObject2.optDouble("hangup_ui_timeout", 5.0d);
            this.enable_vp8_encoder = jsonObject2.optBoolean("enable_vp8_encoder", true);
            this.enable_vp8_decoder = jsonObject2.optBoolean("enable_vp8_decoder", true);
            this.enable_vp9_encoder = jsonObject2.optBoolean("enable_vp9_encoder", true);
            this.enable_vp9_decoder = jsonObject2.optBoolean("enable_vp9_decoder", true);
            this.enable_h265_encoder = jsonObject2.optBoolean("enable_h265_encoder", true);
            this.enable_h265_decoder = jsonObject2.optBoolean("enable_h265_decoder", true);
            this.enable_h264_encoder = jsonObject2.optBoolean("enable_h264_encoder", true);
            this.enable_h264_decoder = jsonObject2.optBoolean("enable_h264_decoder", true);
        }

        public String getString(String key) {
            return getString(key, "");
        }

        public String getString(String key, String fallback) {
            return this.jsonObject.optString(key, fallback);
        }
    }
}
