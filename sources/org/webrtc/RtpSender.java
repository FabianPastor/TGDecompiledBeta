package org.webrtc;

import java.util.List;

public class RtpSender {
    private MediaStreamTrack cachedTrack;
    private final DtmfSender dtmfSender;
    private long nativeRtpSender;
    private boolean ownsTrack = true;

    private static native long nativeGetDtmfSender(long j);

    private static native String nativeGetId(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native List<String> nativeGetStreams(long j);

    private static native long nativeGetTrack(long j);

    private static native void nativeSetFrameEncryptor(long j, long j2);

    private static native boolean nativeSetParameters(long j, RtpParameters rtpParameters);

    private static native void nativeSetStreams(long j, List<String> list);

    private static native boolean nativeSetTrack(long j, long j2);

    public RtpSender(long nativeRtpSender2) {
        this.nativeRtpSender = nativeRtpSender2;
        this.cachedTrack = MediaStreamTrack.createMediaStreamTrack(nativeGetTrack(nativeRtpSender2));
        long nativeDtmfSender = nativeGetDtmfSender(nativeRtpSender2);
        this.dtmfSender = nativeDtmfSender != 0 ? new DtmfSender(nativeDtmfSender) : null;
    }

    public boolean setTrack(MediaStreamTrack track, boolean takeOwnership) {
        checkRtpSenderExists();
        if (!nativeSetTrack(this.nativeRtpSender, track == null ? 0 : track.getNativeMediaStreamTrack())) {
            return false;
        }
        MediaStreamTrack mediaStreamTrack = this.cachedTrack;
        if (mediaStreamTrack != null && this.ownsTrack) {
            mediaStreamTrack.dispose();
        }
        this.cachedTrack = track;
        this.ownsTrack = takeOwnership;
        return true;
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public void setStreams(List<String> streamIds) {
        checkRtpSenderExists();
        nativeSetStreams(this.nativeRtpSender, streamIds);
    }

    public List<String> getStreams() {
        checkRtpSenderExists();
        return nativeGetStreams(this.nativeRtpSender);
    }

    public boolean setParameters(RtpParameters parameters) {
        checkRtpSenderExists();
        return nativeSetParameters(this.nativeRtpSender, parameters);
    }

    public RtpParameters getParameters() {
        checkRtpSenderExists();
        return nativeGetParameters(this.nativeRtpSender);
    }

    public String id() {
        checkRtpSenderExists();
        return nativeGetId(this.nativeRtpSender);
    }

    public DtmfSender dtmf() {
        return this.dtmfSender;
    }

    public void setFrameEncryptor(FrameEncryptor frameEncryptor) {
        checkRtpSenderExists();
        nativeSetFrameEncryptor(this.nativeRtpSender, frameEncryptor.getNativeFrameEncryptor());
    }

    public void dispose() {
        checkRtpSenderExists();
        DtmfSender dtmfSender2 = this.dtmfSender;
        if (dtmfSender2 != null) {
            dtmfSender2.dispose();
        }
        MediaStreamTrack mediaStreamTrack = this.cachedTrack;
        if (mediaStreamTrack != null && this.ownsTrack) {
            mediaStreamTrack.dispose();
        }
        JniCommon.nativeReleaseRef(this.nativeRtpSender);
        this.nativeRtpSender = 0;
    }

    /* access modifiers changed from: package-private */
    public long getNativeRtpSender() {
        checkRtpSenderExists();
        return this.nativeRtpSender;
    }

    private void checkRtpSenderExists() {
        if (this.nativeRtpSender == 0) {
            throw new IllegalStateException("RtpSender has been disposed.");
        }
    }
}
