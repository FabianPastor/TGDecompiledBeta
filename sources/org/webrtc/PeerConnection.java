package org.webrtc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webrtc.DataChannel;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RtpTransceiver;

public class PeerConnection {
    private final List<MediaStream> localStreams;
    private final long nativePeerConnection;
    private List<RtpReceiver> receivers;
    private List<RtpSender> senders;
    private List<RtpTransceiver> transceivers;

    public enum BundlePolicy {
        BALANCED,
        MAXBUNDLE,
        MAXCOMPAT
    }

    public enum CandidateNetworkPolicy {
        ALL,
        LOW_COST
    }

    public enum ContinualGatheringPolicy {
        GATHER_ONCE,
        GATHER_CONTINUALLY
    }

    public enum IceTransportsType {
        NONE,
        RELAY,
        NOHOST,
        ALL
    }

    public enum KeyType {
        RSA,
        ECDSA
    }

    public enum PortPrunePolicy {
        NO_PRUNE,
        PRUNE_BASED_ON_PRIORITY,
        KEEP_FIRST_READY
    }

    public enum RtcpMuxPolicy {
        NEGOTIATE,
        REQUIRE
    }

    public enum SdpSemantics {
        PLAN_B,
        UNIFIED_PLAN
    }

    public enum TcpCandidatePolicy {
        ENABLED,
        DISABLED
    }

    public enum TlsCertPolicy {
        TLS_CERT_POLICY_SECURE,
        TLS_CERT_POLICY_INSECURE_NO_CHECK
    }

    private native boolean nativeAddIceCandidate(String str, int i, String str2);

    private native boolean nativeAddLocalStream(long j);

    private native RtpSender nativeAddTrack(long j, List<String> list);

    private native RtpTransceiver nativeAddTransceiverOfType(MediaStreamTrack.MediaType mediaType, RtpTransceiver.RtpTransceiverInit rtpTransceiverInit);

    private native RtpTransceiver nativeAddTransceiverWithTrack(long j, RtpTransceiver.RtpTransceiverInit rtpTransceiverInit);

    private native void nativeClose();

    private native PeerConnectionState nativeConnectionState();

    private native void nativeCreateAnswer(SdpObserver sdpObserver, MediaConstraints mediaConstraints);

    private native DataChannel nativeCreateDataChannel(String str, DataChannel.Init init);

    private native void nativeCreateOffer(SdpObserver sdpObserver, MediaConstraints mediaConstraints);

    private static native long nativeCreatePeerConnectionObserver(Observer observer);

    private native RtpSender nativeCreateSender(String str, String str2);

    private static native void nativeFreeOwnedPeerConnection(long j);

    private native RtcCertificatePem nativeGetCertificate();

    private native SessionDescription nativeGetLocalDescription();

    private native long nativeGetNativePeerConnection();

    private native List<RtpReceiver> nativeGetReceivers();

    private native SessionDescription nativeGetRemoteDescription();

    private native List<RtpSender> nativeGetSenders();

    private native List<RtpTransceiver> nativeGetTransceivers();

    private native IceConnectionState nativeIceConnectionState();

    private native IceGatheringState nativeIceGatheringState();

    private native void nativeNewGetStats(RTCStatsCollectorCallback rTCStatsCollectorCallback);

    private native boolean nativeOldGetStats(StatsObserver statsObserver, long j);

    private native boolean nativeRemoveIceCandidates(IceCandidate[] iceCandidateArr);

    private native void nativeRemoveLocalStream(long j);

    private native boolean nativeRemoveTrack(long j);

    private native void nativeSetAudioPlayout(boolean z);

    private native void nativeSetAudioRecording(boolean z);

    private native boolean nativeSetBitrate(Integer num, Integer num2, Integer num3);

    private native boolean nativeSetConfiguration(RTCConfiguration rTCConfiguration);

    private native void nativeSetLocalDescription(SdpObserver sdpObserver, SessionDescription sessionDescription);

    private native void nativeSetRemoteDescription(SdpObserver sdpObserver, SessionDescription sessionDescription);

    private native SignalingState nativeSignalingState();

    private native boolean nativeStartRtcEventLog(int i, int i2);

    private native void nativeStopRtcEventLog();

    public enum IceGatheringState {
        NEW,
        GATHERING,
        COMPLETE;

        static IceGatheringState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public enum IceConnectionState {
        NEW,
        CHECKING,
        CONNECTED,
        COMPLETED,
        FAILED,
        DISCONNECTED,
        CLOSED;

        static IceConnectionState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public enum PeerConnectionState {
        NEW,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        FAILED,
        CLOSED;

        static PeerConnectionState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public enum SignalingState {
        STABLE,
        HAVE_LOCAL_OFFER,
        HAVE_LOCAL_PRANSWER,
        HAVE_REMOTE_OFFER,
        HAVE_REMOTE_PRANSWER,
        CLOSED;

        static SignalingState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public interface Observer {
        void onAddStream(MediaStream mediaStream);

        void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreamArr);

        void onConnectionChange(PeerConnectionState peerConnectionState);

        void onDataChannel(DataChannel dataChannel);

        void onIceCandidate(IceCandidate iceCandidate);

        void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr);

        void onIceConnectionChange(IceConnectionState iceConnectionState);

        void onIceConnectionReceivingChange(boolean z);

        void onIceGatheringChange(IceGatheringState iceGatheringState);

        void onRemoveStream(MediaStream mediaStream);

        void onRenegotiationNeeded();

        void onSelectedCandidatePairChanged(CandidatePairChangeEvent candidatePairChangeEvent);

        void onSignalingChange(SignalingState signalingState);

        void onStandardizedIceConnectionChange(IceConnectionState iceConnectionState);

        void onTrack(RtpTransceiver rtpTransceiver);

        /* renamed from: org.webrtc.PeerConnection$Observer$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onStandardizedIceConnectionChange(Observer _this, IceConnectionState newState) {
            }

            public static void $default$onConnectionChange(Observer _this, PeerConnectionState newState) {
            }

            public static void $default$onSelectedCandidatePairChanged(Observer _this, CandidatePairChangeEvent event) {
            }

            public static void $default$onTrack(Observer _this, RtpTransceiver transceiver) {
            }
        }
    }

    public static class IceServer {
        public final String hostname;
        public final String password;
        public final List<String> tlsAlpnProtocols;
        public final TlsCertPolicy tlsCertPolicy;
        public final List<String> tlsEllipticCurves;
        @Deprecated
        public final String uri;
        public final List<String> urls;
        public final String username;

        @Deprecated
        public IceServer(String uri2) {
            this(uri2, "", "");
        }

        @Deprecated
        public IceServer(String uri2, String username2, String password2) {
            this(uri2, username2, password2, TlsCertPolicy.TLS_CERT_POLICY_SECURE);
        }

        @Deprecated
        public IceServer(String uri2, String username2, String password2, TlsCertPolicy tlsCertPolicy2) {
            this(uri2, username2, password2, tlsCertPolicy2, "");
        }

        @Deprecated
        public IceServer(String uri2, String username2, String password2, TlsCertPolicy tlsCertPolicy2, String hostname2) {
            this(uri2, Collections.singletonList(uri2), username2, password2, tlsCertPolicy2, hostname2, (List<String>) null, (List<String>) null);
        }

        private IceServer(String uri2, List<String> urls2, String username2, String password2, TlsCertPolicy tlsCertPolicy2, String hostname2, List<String> tlsAlpnProtocols2, List<String> tlsEllipticCurves2) {
            if (uri2 == null || urls2 == null || urls2.isEmpty()) {
                throw new IllegalArgumentException("uri == null || urls == null || urls.isEmpty()");
            }
            for (String it : urls2) {
                if (it == null) {
                    throw new IllegalArgumentException("urls element is null: " + urls2);
                }
            }
            if (username2 == null) {
                throw new IllegalArgumentException("username == null");
            } else if (password2 == null) {
                throw new IllegalArgumentException("password == null");
            } else if (hostname2 != null) {
                this.uri = uri2;
                this.urls = urls2;
                this.username = username2;
                this.password = password2;
                this.tlsCertPolicy = tlsCertPolicy2;
                this.hostname = hostname2;
                this.tlsAlpnProtocols = tlsAlpnProtocols2;
                this.tlsEllipticCurves = tlsEllipticCurves2;
            } else {
                throw new IllegalArgumentException("hostname == null");
            }
        }

        public String toString() {
            return this.urls + " [" + this.username + ":" + this.password + "] [" + this.tlsCertPolicy + "] [" + this.hostname + "] [" + this.tlsAlpnProtocols + "] [" + this.tlsEllipticCurves + "]";
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof IceServer)) {
                return false;
            }
            IceServer other = (IceServer) obj;
            if (!this.uri.equals(other.uri) || !this.urls.equals(other.urls) || !this.username.equals(other.username) || !this.password.equals(other.password) || !this.tlsCertPolicy.equals(other.tlsCertPolicy) || !this.hostname.equals(other.hostname) || !this.tlsAlpnProtocols.equals(other.tlsAlpnProtocols) || !this.tlsEllipticCurves.equals(other.tlsEllipticCurves)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.uri, this.urls, this.username, this.password, this.tlsCertPolicy, this.hostname, this.tlsAlpnProtocols, this.tlsEllipticCurves});
        }

        public static Builder builder(String uri2) {
            return new Builder(Collections.singletonList(uri2));
        }

        public static Builder builder(List<String> urls2) {
            return new Builder(urls2);
        }

        public static class Builder {
            private String hostname;
            private String password;
            private List<String> tlsAlpnProtocols;
            private TlsCertPolicy tlsCertPolicy;
            private List<String> tlsEllipticCurves;
            private final List<String> urls;
            private String username;

            private Builder(List<String> urls2) {
                this.username = "";
                this.password = "";
                this.tlsCertPolicy = TlsCertPolicy.TLS_CERT_POLICY_SECURE;
                this.hostname = "";
                if (urls2 == null || urls2.isEmpty()) {
                    throw new IllegalArgumentException("urls == null || urls.isEmpty(): " + urls2);
                }
                this.urls = urls2;
            }

            public Builder setUsername(String username2) {
                this.username = username2;
                return this;
            }

            public Builder setPassword(String password2) {
                this.password = password2;
                return this;
            }

            public Builder setTlsCertPolicy(TlsCertPolicy tlsCertPolicy2) {
                this.tlsCertPolicy = tlsCertPolicy2;
                return this;
            }

            public Builder setHostname(String hostname2) {
                this.hostname = hostname2;
                return this;
            }

            public Builder setTlsAlpnProtocols(List<String> tlsAlpnProtocols2) {
                this.tlsAlpnProtocols = tlsAlpnProtocols2;
                return this;
            }

            public Builder setTlsEllipticCurves(List<String> tlsEllipticCurves2) {
                this.tlsEllipticCurves = tlsEllipticCurves2;
                return this;
            }

            public IceServer createIceServer() {
                return new IceServer(this.urls.get(0), this.urls, this.username, this.password, this.tlsCertPolicy, this.hostname, this.tlsAlpnProtocols, this.tlsEllipticCurves);
            }
        }

        /* access modifiers changed from: package-private */
        public List<String> getUrls() {
            return this.urls;
        }

        /* access modifiers changed from: package-private */
        public String getUsername() {
            return this.username;
        }

        /* access modifiers changed from: package-private */
        public String getPassword() {
            return this.password;
        }

        /* access modifiers changed from: package-private */
        public TlsCertPolicy getTlsCertPolicy() {
            return this.tlsCertPolicy;
        }

        /* access modifiers changed from: package-private */
        public String getHostname() {
            return this.hostname;
        }

        /* access modifiers changed from: package-private */
        public List<String> getTlsAlpnProtocols() {
            return this.tlsAlpnProtocols;
        }

        /* access modifiers changed from: package-private */
        public List<String> getTlsEllipticCurves() {
            return this.tlsEllipticCurves;
        }
    }

    public enum AdapterType {
        UNKNOWN(0),
        ETHERNET(1),
        WIFI(2),
        CELLULAR(4),
        VPN(8),
        LOOPBACK(16),
        ADAPTER_TYPE_ANY(32),
        CELLULAR_2G(64),
        CELLULAR_3G(128),
        CELLULAR_4G(256),
        CELLULAR_5G(512);
        
        private static final Map<Integer, AdapterType> BY_BITMASK = null;
        public final Integer bitMask;

        static {
            int i;
            BY_BITMASK = new HashMap();
            for (AdapterType t : values()) {
                BY_BITMASK.put(t.bitMask, t);
            }
        }

        private AdapterType(Integer bitMask2) {
            this.bitMask = bitMask2;
        }

        static AdapterType fromNativeIndex(int nativeIndex) {
            return BY_BITMASK.get(Integer.valueOf(nativeIndex));
        }
    }

    public static class RTCConfiguration {
        public boolean activeResetSrtpParams;
        public Boolean allowCodecSwitching;
        public boolean audioJitterBufferFastAccelerate;
        public int audioJitterBufferMaxPackets;
        public BundlePolicy bundlePolicy = BundlePolicy.BALANCED;
        public CandidateNetworkPolicy candidateNetworkPolicy = CandidateNetworkPolicy.ALL;
        public RtcCertificatePem certificate;
        public Boolean combinedAudioVideoBwe;
        public ContinualGatheringPolicy continualGatheringPolicy;
        public CryptoOptions cryptoOptions;
        public boolean disableIPv6OnWifi;
        public boolean disableIpv6;
        public boolean enableCpuOveruseDetection;
        public boolean enableDscp;
        public Boolean enableDtlsSrtp;
        public boolean enableRtpDataChannel;
        public int iceBackupCandidatePairPingInterval;
        public int iceCandidatePoolSize;
        public Integer iceCheckIntervalStrongConnectivityMs;
        public Integer iceCheckIntervalWeakConnectivityMs;
        public Integer iceCheckMinInterval;
        public int iceConnectionReceivingTimeout;
        public List<IceServer> iceServers;
        public IceTransportsType iceTransportsType = IceTransportsType.ALL;
        public Integer iceUnwritableMinChecks;
        public Integer iceUnwritableTimeMs;
        public KeyType keyType;
        public int maxIPv6Networks;
        public AdapterType networkPreference;
        public boolean presumeWritableWhenFullyRelayed;
        @Deprecated
        public boolean pruneTurnPorts;
        public RtcpMuxPolicy rtcpMuxPolicy = RtcpMuxPolicy.REQUIRE;
        public Integer screencastMinBitrate;
        public SdpSemantics sdpSemantics;
        public Integer stunCandidateKeepaliveIntervalMs;
        public boolean surfaceIceCandidatesOnIceTransportTypeChanged;
        public boolean suspendBelowMinBitrate;
        public TcpCandidatePolicy tcpCandidatePolicy = TcpCandidatePolicy.ENABLED;
        public TurnCustomizer turnCustomizer;
        public String turnLoggingId;
        public PortPrunePolicy turnPortPrunePolicy;

        public RTCConfiguration(List<IceServer> iceServers2) {
            this.iceServers = iceServers2;
            this.audioJitterBufferMaxPackets = 50;
            this.audioJitterBufferFastAccelerate = false;
            this.iceConnectionReceivingTimeout = -1;
            this.iceBackupCandidatePairPingInterval = -1;
            this.keyType = KeyType.ECDSA;
            this.continualGatheringPolicy = ContinualGatheringPolicy.GATHER_ONCE;
            this.iceCandidatePoolSize = 0;
            this.pruneTurnPorts = false;
            this.turnPortPrunePolicy = PortPrunePolicy.NO_PRUNE;
            this.presumeWritableWhenFullyRelayed = false;
            this.surfaceIceCandidatesOnIceTransportTypeChanged = false;
            this.iceCheckIntervalStrongConnectivityMs = null;
            this.iceCheckIntervalWeakConnectivityMs = null;
            this.iceCheckMinInterval = null;
            this.iceUnwritableTimeMs = null;
            this.iceUnwritableMinChecks = null;
            this.stunCandidateKeepaliveIntervalMs = null;
            this.disableIPv6OnWifi = false;
            this.maxIPv6Networks = 5;
            this.disableIpv6 = false;
            this.enableDscp = false;
            this.enableCpuOveruseDetection = true;
            this.enableRtpDataChannel = false;
            this.suspendBelowMinBitrate = false;
            this.screencastMinBitrate = null;
            this.combinedAudioVideoBwe = null;
            this.enableDtlsSrtp = null;
            this.networkPreference = AdapterType.UNKNOWN;
            this.sdpSemantics = SdpSemantics.PLAN_B;
            this.activeResetSrtpParams = false;
            this.cryptoOptions = null;
            this.turnLoggingId = null;
            this.allowCodecSwitching = null;
        }

        /* access modifiers changed from: package-private */
        public IceTransportsType getIceTransportsType() {
            return this.iceTransportsType;
        }

        /* access modifiers changed from: package-private */
        public List<IceServer> getIceServers() {
            return this.iceServers;
        }

        /* access modifiers changed from: package-private */
        public BundlePolicy getBundlePolicy() {
            return this.bundlePolicy;
        }

        /* access modifiers changed from: package-private */
        public PortPrunePolicy getTurnPortPrunePolicy() {
            return this.turnPortPrunePolicy;
        }

        /* access modifiers changed from: package-private */
        public RtcCertificatePem getCertificate() {
            return this.certificate;
        }

        /* access modifiers changed from: package-private */
        public RtcpMuxPolicy getRtcpMuxPolicy() {
            return this.rtcpMuxPolicy;
        }

        /* access modifiers changed from: package-private */
        public TcpCandidatePolicy getTcpCandidatePolicy() {
            return this.tcpCandidatePolicy;
        }

        /* access modifiers changed from: package-private */
        public CandidateNetworkPolicy getCandidateNetworkPolicy() {
            return this.candidateNetworkPolicy;
        }

        /* access modifiers changed from: package-private */
        public int getAudioJitterBufferMaxPackets() {
            return this.audioJitterBufferMaxPackets;
        }

        /* access modifiers changed from: package-private */
        public boolean getAudioJitterBufferFastAccelerate() {
            return this.audioJitterBufferFastAccelerate;
        }

        /* access modifiers changed from: package-private */
        public int getIceConnectionReceivingTimeout() {
            return this.iceConnectionReceivingTimeout;
        }

        /* access modifiers changed from: package-private */
        public int getIceBackupCandidatePairPingInterval() {
            return this.iceBackupCandidatePairPingInterval;
        }

        /* access modifiers changed from: package-private */
        public KeyType getKeyType() {
            return this.keyType;
        }

        /* access modifiers changed from: package-private */
        public ContinualGatheringPolicy getContinualGatheringPolicy() {
            return this.continualGatheringPolicy;
        }

        /* access modifiers changed from: package-private */
        public int getIceCandidatePoolSize() {
            return this.iceCandidatePoolSize;
        }

        /* access modifiers changed from: package-private */
        public boolean getPruneTurnPorts() {
            return this.pruneTurnPorts;
        }

        /* access modifiers changed from: package-private */
        public boolean getPresumeWritableWhenFullyRelayed() {
            return this.presumeWritableWhenFullyRelayed;
        }

        /* access modifiers changed from: package-private */
        public boolean getSurfaceIceCandidatesOnIceTransportTypeChanged() {
            return this.surfaceIceCandidatesOnIceTransportTypeChanged;
        }

        /* access modifiers changed from: package-private */
        public Integer getIceCheckIntervalStrongConnectivity() {
            return this.iceCheckIntervalStrongConnectivityMs;
        }

        /* access modifiers changed from: package-private */
        public Integer getIceCheckIntervalWeakConnectivity() {
            return this.iceCheckIntervalWeakConnectivityMs;
        }

        /* access modifiers changed from: package-private */
        public Integer getIceCheckMinInterval() {
            return this.iceCheckMinInterval;
        }

        /* access modifiers changed from: package-private */
        public Integer getIceUnwritableTimeout() {
            return this.iceUnwritableTimeMs;
        }

        /* access modifiers changed from: package-private */
        public Integer getIceUnwritableMinChecks() {
            return this.iceUnwritableMinChecks;
        }

        /* access modifiers changed from: package-private */
        public Integer getStunCandidateKeepaliveInterval() {
            return this.stunCandidateKeepaliveIntervalMs;
        }

        /* access modifiers changed from: package-private */
        public boolean getDisableIPv6OnWifi() {
            return this.disableIPv6OnWifi;
        }

        /* access modifiers changed from: package-private */
        public int getMaxIPv6Networks() {
            return this.maxIPv6Networks;
        }

        /* access modifiers changed from: package-private */
        public TurnCustomizer getTurnCustomizer() {
            return this.turnCustomizer;
        }

        /* access modifiers changed from: package-private */
        public boolean getDisableIpv6() {
            return this.disableIpv6;
        }

        /* access modifiers changed from: package-private */
        public boolean getEnableDscp() {
            return this.enableDscp;
        }

        /* access modifiers changed from: package-private */
        public boolean getEnableCpuOveruseDetection() {
            return this.enableCpuOveruseDetection;
        }

        /* access modifiers changed from: package-private */
        public boolean getEnableRtpDataChannel() {
            return this.enableRtpDataChannel;
        }

        /* access modifiers changed from: package-private */
        public boolean getSuspendBelowMinBitrate() {
            return this.suspendBelowMinBitrate;
        }

        /* access modifiers changed from: package-private */
        public Integer getScreencastMinBitrate() {
            return this.screencastMinBitrate;
        }

        /* access modifiers changed from: package-private */
        public Boolean getCombinedAudioVideoBwe() {
            return this.combinedAudioVideoBwe;
        }

        /* access modifiers changed from: package-private */
        public Boolean getEnableDtlsSrtp() {
            return this.enableDtlsSrtp;
        }

        /* access modifiers changed from: package-private */
        public AdapterType getNetworkPreference() {
            return this.networkPreference;
        }

        /* access modifiers changed from: package-private */
        public SdpSemantics getSdpSemantics() {
            return this.sdpSemantics;
        }

        /* access modifiers changed from: package-private */
        public boolean getActiveResetSrtpParams() {
            return this.activeResetSrtpParams;
        }

        /* access modifiers changed from: package-private */
        public Boolean getAllowCodecSwitching() {
            return this.allowCodecSwitching;
        }

        /* access modifiers changed from: package-private */
        public CryptoOptions getCryptoOptions() {
            return this.cryptoOptions;
        }

        /* access modifiers changed from: package-private */
        public String getTurnLoggingId() {
            return this.turnLoggingId;
        }
    }

    public PeerConnection(NativePeerConnectionFactory factory) {
        this(factory.createNativePeerConnection());
    }

    PeerConnection(long nativePeerConnection2) {
        this.localStreams = new ArrayList();
        this.senders = new ArrayList();
        this.receivers = new ArrayList();
        this.transceivers = new ArrayList();
        this.nativePeerConnection = nativePeerConnection2;
    }

    public SessionDescription getLocalDescription() {
        return nativeGetLocalDescription();
    }

    public SessionDescription getRemoteDescription() {
        return nativeGetRemoteDescription();
    }

    public RtcCertificatePem getCertificate() {
        return nativeGetCertificate();
    }

    public DataChannel createDataChannel(String label, DataChannel.Init init) {
        return nativeCreateDataChannel(label, init);
    }

    public void createOffer(SdpObserver observer, MediaConstraints constraints) {
        nativeCreateOffer(observer, constraints);
    }

    public void createAnswer(SdpObserver observer, MediaConstraints constraints) {
        nativeCreateAnswer(observer, constraints);
    }

    public void setLocalDescription(SdpObserver observer, SessionDescription sdp) {
        nativeSetLocalDescription(observer, sdp);
    }

    public void setRemoteDescription(SdpObserver observer, SessionDescription sdp) {
        nativeSetRemoteDescription(observer, sdp);
    }

    public void setAudioPlayout(boolean playout) {
        nativeSetAudioPlayout(playout);
    }

    public void setAudioRecording(boolean recording) {
        nativeSetAudioRecording(recording);
    }

    public boolean setConfiguration(RTCConfiguration config) {
        return nativeSetConfiguration(config);
    }

    public boolean addIceCandidate(IceCandidate candidate) {
        return nativeAddIceCandidate(candidate.sdpMid, candidate.sdpMLineIndex, candidate.sdp);
    }

    public boolean removeIceCandidates(IceCandidate[] candidates) {
        return nativeRemoveIceCandidates(candidates);
    }

    public boolean addStream(MediaStream stream) {
        if (!nativeAddLocalStream(stream.getNativeMediaStream())) {
            return false;
        }
        this.localStreams.add(stream);
        return true;
    }

    public void removeStream(MediaStream stream) {
        nativeRemoveLocalStream(stream.getNativeMediaStream());
        this.localStreams.remove(stream);
    }

    public RtpSender createSender(String kind, String stream_id) {
        RtpSender newSender = nativeCreateSender(kind, stream_id);
        if (newSender != null) {
            this.senders.add(newSender);
        }
        return newSender;
    }

    public List<RtpSender> getSenders() {
        for (RtpSender sender : this.senders) {
            sender.dispose();
        }
        List<RtpSender> nativeGetSenders = nativeGetSenders();
        this.senders = nativeGetSenders;
        return Collections.unmodifiableList(nativeGetSenders);
    }

    public List<RtpReceiver> getReceivers() {
        for (RtpReceiver receiver : this.receivers) {
            receiver.dispose();
        }
        List<RtpReceiver> nativeGetReceivers = nativeGetReceivers();
        this.receivers = nativeGetReceivers;
        return Collections.unmodifiableList(nativeGetReceivers);
    }

    public List<RtpTransceiver> getTransceivers() {
        for (RtpTransceiver transceiver : this.transceivers) {
            transceiver.dispose();
        }
        List<RtpTransceiver> nativeGetTransceivers = nativeGetTransceivers();
        this.transceivers = nativeGetTransceivers;
        return Collections.unmodifiableList(nativeGetTransceivers);
    }

    public RtpSender addTrack(MediaStreamTrack track) {
        return addTrack(track, Collections.emptyList());
    }

    public RtpSender addTrack(MediaStreamTrack track, List<String> streamIds) {
        if (track == null || streamIds == null) {
            throw new NullPointerException("No MediaStreamTrack specified in addTrack.");
        }
        RtpSender newSender = nativeAddTrack(track.getNativeMediaStreamTrack(), streamIds);
        if (newSender != null) {
            this.senders.add(newSender);
            return newSender;
        }
        throw new IllegalStateException("C++ addTrack failed.");
    }

    public boolean removeTrack(RtpSender sender) {
        if (sender != null) {
            return nativeRemoveTrack(sender.getNativeRtpSender());
        }
        throw new NullPointerException("No RtpSender specified for removeTrack.");
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack track) {
        return addTransceiver(track, new RtpTransceiver.RtpTransceiverInit());
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack track, RtpTransceiver.RtpTransceiverInit init) {
        if (track != null) {
            if (init == null) {
                init = new RtpTransceiver.RtpTransceiverInit();
            }
            RtpTransceiver newTransceiver = nativeAddTransceiverWithTrack(track.getNativeMediaStreamTrack(), init);
            if (newTransceiver != null) {
                this.transceivers.add(newTransceiver);
                return newTransceiver;
            }
            throw new IllegalStateException("C++ addTransceiver failed.");
        }
        throw new NullPointerException("No MediaStreamTrack specified for addTransceiver.");
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack.MediaType mediaType) {
        return addTransceiver(mediaType, new RtpTransceiver.RtpTransceiverInit());
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack.MediaType mediaType, RtpTransceiver.RtpTransceiverInit init) {
        if (mediaType != null) {
            if (init == null) {
                init = new RtpTransceiver.RtpTransceiverInit();
            }
            RtpTransceiver newTransceiver = nativeAddTransceiverOfType(mediaType, init);
            if (newTransceiver != null) {
                this.transceivers.add(newTransceiver);
                return newTransceiver;
            }
            throw new IllegalStateException("C++ addTransceiver failed.");
        }
        throw new NullPointerException("No MediaType specified for addTransceiver.");
    }

    @Deprecated
    public boolean getStats(StatsObserver observer, MediaStreamTrack track) {
        return nativeOldGetStats(observer, track == null ? 0 : track.getNativeMediaStreamTrack());
    }

    public void getStats(RTCStatsCollectorCallback callback) {
        nativeNewGetStats(callback);
    }

    public boolean setBitrate(Integer min, Integer current, Integer max) {
        return nativeSetBitrate(min, current, max);
    }

    public boolean startRtcEventLog(int file_descriptor, int max_size_bytes) {
        return nativeStartRtcEventLog(file_descriptor, max_size_bytes);
    }

    public void stopRtcEventLog() {
        nativeStopRtcEventLog();
    }

    public SignalingState signalingState() {
        return nativeSignalingState();
    }

    public IceConnectionState iceConnectionState() {
        return nativeIceConnectionState();
    }

    public PeerConnectionState connectionState() {
        return nativeConnectionState();
    }

    public IceGatheringState iceGatheringState() {
        return nativeIceGatheringState();
    }

    public void close() {
        nativeClose();
    }

    public void dispose() {
        close();
        for (MediaStream stream : this.localStreams) {
            nativeRemoveLocalStream(stream.getNativeMediaStream());
            stream.dispose();
        }
        this.localStreams.clear();
        for (RtpSender sender : this.senders) {
            sender.dispose();
        }
        this.senders.clear();
        for (RtpReceiver receiver : this.receivers) {
            receiver.dispose();
        }
        for (RtpTransceiver transceiver : this.transceivers) {
            transceiver.dispose();
        }
        this.transceivers.clear();
        this.receivers.clear();
        nativeFreeOwnedPeerConnection(this.nativePeerConnection);
    }

    public long getNativePeerConnection() {
        return nativeGetNativePeerConnection();
    }

    /* access modifiers changed from: package-private */
    public long getNativeOwnedPeerConnection() {
        return this.nativePeerConnection;
    }

    public static long createNativePeerConnectionObserver(Observer observer) {
        return nativeCreatePeerConnectionObserver(observer);
    }
}
