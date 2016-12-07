package org.telegram.messenger.exoplayer.upstream;

public interface BandwidthMeter extends TransferListener {
    public static final long NO_ESTIMATE = -1;

    public interface EventListener {
        void onBandwidthSample(int i, long j, long j2);
    }

    long getBitrateEstimate();
}
