package org.telegram.messenger.exoplayer2.upstream;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.upstream.BandwidthMeter.EventListener;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.SlidingPercentile;

public final class DefaultBandwidthMeter implements BandwidthMeter, TransferListener<Object> {
    private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
    public static final long DEFAULT_INITIAL_BITRATE_ESTIMATE = 1000000;
    public static final int DEFAULT_SLIDING_WINDOW_MAX_WEIGHT = 2000;
    private static final int ELAPSED_MILLIS_FOR_ESTIMATE = 2000;
    private long bitrateEstimate;
    private final Clock clock;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private long sampleBytesTransferred;
    private long sampleStartTimeMs;
    private final SlidingPercentile slidingPercentile;
    private int streamCount;
    private long totalBytesTransferred;
    private long totalElapsedTimeMs;

    public static final class Builder {
        private Clock clock = Clock.DEFAULT;
        private Handler eventHandler;
        private EventListener eventListener;
        private long initialBitrateEstimate = 1000000;
        private int slidingWindowMaxWeight = 2000;

        public Builder setEventListener(Handler eventHandler, EventListener eventListener) {
            boolean z = (eventHandler == null || eventListener == null) ? false : true;
            Assertions.checkArgument(z);
            this.eventHandler = eventHandler;
            this.eventListener = eventListener;
            return this;
        }

        public Builder setSlidingWindowMaxWeight(int slidingWindowMaxWeight) {
            this.slidingWindowMaxWeight = slidingWindowMaxWeight;
            return this;
        }

        public Builder setInitialBitrateEstimate(long initialBitrateEstimate) {
            this.initialBitrateEstimate = initialBitrateEstimate;
            return this;
        }

        public Builder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public DefaultBandwidthMeter build() {
            return new DefaultBandwidthMeter(this.eventHandler, this.eventListener, this.initialBitrateEstimate, this.slidingWindowMaxWeight, this.clock);
        }
    }

    public DefaultBandwidthMeter() {
        this(null, null, 1000000, 2000, Clock.DEFAULT);
    }

    @Deprecated
    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener) {
        this(eventHandler, eventListener, 1000000, 2000, Clock.DEFAULT);
    }

    @Deprecated
    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener, int maxWeight) {
        this(eventHandler, eventListener, 1000000, maxWeight, Clock.DEFAULT);
    }

    private DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener, long initialBitrateEstimate, int maxWeight, Clock clock) {
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.slidingPercentile = new SlidingPercentile(maxWeight);
        this.clock = clock;
        this.bitrateEstimate = initialBitrateEstimate;
    }

    public synchronized long getBitrateEstimate() {
        return this.bitrateEstimate;
    }

    public synchronized void onTransferStart(Object source, DataSpec dataSpec) {
        if (this.streamCount == 0) {
            this.sampleStartTimeMs = this.clock.elapsedRealtime();
        }
        this.streamCount++;
    }

    public synchronized void onBytesTransferred(Object source, int bytes) {
        this.sampleBytesTransferred += (long) bytes;
    }

    public synchronized void onTransferEnd(Object source) {
        Assertions.checkState(this.streamCount > 0);
        long nowMs = this.clock.elapsedRealtime();
        int sampleElapsedTimeMs = (int) (nowMs - this.sampleStartTimeMs);
        this.totalElapsedTimeMs += (long) sampleElapsedTimeMs;
        this.totalBytesTransferred += this.sampleBytesTransferred;
        if (sampleElapsedTimeMs > 0) {
            this.slidingPercentile.addSample((int) Math.sqrt((double) this.sampleBytesTransferred), (float) ((this.sampleBytesTransferred * 8000) / ((long) sampleElapsedTimeMs)));
            if (this.totalElapsedTimeMs >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS || this.totalBytesTransferred >= 524288) {
                this.bitrateEstimate = (long) this.slidingPercentile.getPercentile(0.5f);
            }
        }
        notifyBandwidthSample(sampleElapsedTimeMs, this.sampleBytesTransferred, this.bitrateEstimate);
        int i = this.streamCount - 1;
        this.streamCount = i;
        if (i > 0) {
            this.sampleStartTimeMs = nowMs;
        }
        this.sampleBytesTransferred = 0;
    }

    private void notifyBandwidthSample(int elapsedMs, long bytes, long bitrate) {
        if (this.eventHandler != null && this.eventListener != null) {
            final int i = elapsedMs;
            final long j = bytes;
            final long j2 = bitrate;
            this.eventHandler.post(new Runnable() {
                public void run() {
                    DefaultBandwidthMeter.this.eventListener.onBandwidthSample(i, j, j2);
                }
            });
        }
    }
}
