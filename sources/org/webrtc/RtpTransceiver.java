package org.webrtc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RtpParameters;

public class RtpTransceiver {
    private RtpReceiver cachedReceiver;
    private RtpSender cachedSender;
    private long nativeRtpTransceiver;

    private static native RtpTransceiverDirection nativeCurrentDirection(long j);

    private static native RtpTransceiverDirection nativeDirection(long j);

    private static native MediaStreamTrack.MediaType nativeGetMediaType(long j);

    private static native String nativeGetMid(long j);

    private static native RtpReceiver nativeGetReceiver(long j);

    private static native RtpSender nativeGetSender(long j);

    private static native boolean nativeSetDirection(long j, RtpTransceiverDirection rtpTransceiverDirection);

    private static native void nativeStopInternal(long j);

    private static native void nativeStopStandard(long j);

    private static native boolean nativeStopped(long j);

    public enum RtpTransceiverDirection {
        SEND_RECV(0),
        SEND_ONLY(1),
        RECV_ONLY(2),
        INACTIVE(3);
        
        private final int nativeIndex;

        private RtpTransceiverDirection(int nativeIndex2) {
            this.nativeIndex = nativeIndex2;
        }

        /* access modifiers changed from: package-private */
        public int getNativeIndex() {
            return this.nativeIndex;
        }

        static RtpTransceiverDirection fromNativeIndex(int nativeIndex2) {
            for (RtpTransceiverDirection type : values()) {
                if (type.getNativeIndex() == nativeIndex2) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Uknown native RtpTransceiverDirection type" + nativeIndex2);
        }
    }

    public static final class RtpTransceiverInit {
        private final RtpTransceiverDirection direction;
        private final List<RtpParameters.Encoding> sendEncodings;
        private final List<String> streamIds;

        public RtpTransceiverInit() {
            this(RtpTransceiverDirection.SEND_RECV);
        }

        public RtpTransceiverInit(RtpTransceiverDirection direction2) {
            this(direction2, Collections.emptyList(), Collections.emptyList());
        }

        public RtpTransceiverInit(RtpTransceiverDirection direction2, List<String> streamIds2) {
            this(direction2, streamIds2, Collections.emptyList());
        }

        public RtpTransceiverInit(RtpTransceiverDirection direction2, List<String> streamIds2, List<RtpParameters.Encoding> sendEncodings2) {
            this.direction = direction2;
            this.streamIds = new ArrayList(streamIds2);
            this.sendEncodings = new ArrayList(sendEncodings2);
        }

        /* access modifiers changed from: package-private */
        public int getDirectionNativeIndex() {
            return this.direction.getNativeIndex();
        }

        /* access modifiers changed from: package-private */
        public List<String> getStreamIds() {
            return new ArrayList(this.streamIds);
        }

        /* access modifiers changed from: package-private */
        public List<RtpParameters.Encoding> getSendEncodings() {
            return new ArrayList(this.sendEncodings);
        }
    }

    protected RtpTransceiver(long nativeRtpTransceiver2) {
        this.nativeRtpTransceiver = nativeRtpTransceiver2;
        this.cachedSender = nativeGetSender(nativeRtpTransceiver2);
        this.cachedReceiver = nativeGetReceiver(nativeRtpTransceiver2);
    }

    public MediaStreamTrack.MediaType getMediaType() {
        checkRtpTransceiverExists();
        return nativeGetMediaType(this.nativeRtpTransceiver);
    }

    public String getMid() {
        checkRtpTransceiverExists();
        return nativeGetMid(this.nativeRtpTransceiver);
    }

    public RtpSender getSender() {
        return this.cachedSender;
    }

    public RtpReceiver getReceiver() {
        return this.cachedReceiver;
    }

    public boolean isStopped() {
        checkRtpTransceiverExists();
        return nativeStopped(this.nativeRtpTransceiver);
    }

    public RtpTransceiverDirection getDirection() {
        checkRtpTransceiverExists();
        return nativeDirection(this.nativeRtpTransceiver);
    }

    public RtpTransceiverDirection getCurrentDirection() {
        checkRtpTransceiverExists();
        return nativeCurrentDirection(this.nativeRtpTransceiver);
    }

    public boolean setDirection(RtpTransceiverDirection rtpTransceiverDirection) {
        checkRtpTransceiverExists();
        return nativeSetDirection(this.nativeRtpTransceiver, rtpTransceiverDirection);
    }

    public void stop() {
        checkRtpTransceiverExists();
        nativeStopInternal(this.nativeRtpTransceiver);
    }

    public void stopInternal() {
        checkRtpTransceiverExists();
        nativeStopInternal(this.nativeRtpTransceiver);
    }

    public void stopStandard() {
        checkRtpTransceiverExists();
        nativeStopStandard(this.nativeRtpTransceiver);
    }

    public void dispose() {
        checkRtpTransceiverExists();
        this.cachedSender.dispose();
        this.cachedReceiver.dispose();
        JniCommon.nativeReleaseRef(this.nativeRtpTransceiver);
        this.nativeRtpTransceiver = 0;
    }

    private void checkRtpTransceiverExists() {
        if (this.nativeRtpTransceiver == 0) {
            throw new IllegalStateException("RtpTransceiver has been disposed.");
        }
    }
}
