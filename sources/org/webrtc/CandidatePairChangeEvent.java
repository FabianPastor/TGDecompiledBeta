package org.webrtc;

public final class CandidatePairChangeEvent {
    public final int lastDataReceivedMs;
    public final IceCandidate local;
    public final String reason;
    public final IceCandidate remote;

    @CalledByNative
    CandidatePairChangeEvent(IceCandidate iceCandidate, IceCandidate iceCandidate2, int i, String str) {
        this.local = iceCandidate;
        this.remote = iceCandidate2;
        this.lastDataReceivedMs = i;
        this.reason = str;
    }
}
