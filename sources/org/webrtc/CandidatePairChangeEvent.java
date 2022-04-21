package org.webrtc;

public final class CandidatePairChangeEvent {
    public final int estimatedDisconnectedTimeMs;
    public final int lastDataReceivedMs;
    public final IceCandidate local;
    public final String reason;
    public final IceCandidate remote;

    CandidatePairChangeEvent(IceCandidate local2, IceCandidate remote2, int lastDataReceivedMs2, String reason2, int estimatedDisconnectedTimeMs2) {
        this.local = local2;
        this.remote = remote2;
        this.lastDataReceivedMs = lastDataReceivedMs2;
        this.reason = reason2;
        this.estimatedDisconnectedTimeMs = estimatedDisconnectedTimeMs2;
    }
}
