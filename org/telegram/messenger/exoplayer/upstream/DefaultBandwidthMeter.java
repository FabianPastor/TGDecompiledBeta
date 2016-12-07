package org.telegram.messenger.exoplayer.upstream;

import android.os.Handler;
import org.telegram.messenger.exoplayer.upstream.BandwidthMeter.EventListener;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Clock;
import org.telegram.messenger.exoplayer.util.SlidingPercentile;
import org.telegram.messenger.exoplayer.util.SystemClock;

public final class DefaultBandwidthMeter implements BandwidthMeter {
    public static final int DEFAULT_MAX_WEIGHT = 2000;
    private long bitrateEstimate;
    private long bytesAccumulator;
    private final Clock clock;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final SlidingPercentile slidingPercentile;
    private long startTimeMs;
    private int streamCount;

    public DefaultBandwidthMeter() {
        this(null, null);
    }

    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener) {
        this(eventHandler, eventListener, new SystemClock());
    }

    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener, Clock clock) {
        this(eventHandler, eventListener, clock, 2000);
    }

    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener, int maxWeight) {
        this(eventHandler, eventListener, new SystemClock(), maxWeight);
    }

    public DefaultBandwidthMeter(Handler eventHandler, EventListener eventListener, Clock clock, int maxWeight) {
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.clock = clock;
        this.slidingPercentile = new SlidingPercentile(maxWeight);
        this.bitrateEstimate = -1;
    }

    public synchronized long getBitrateEstimate() {
        return this.bitrateEstimate;
    }

    public synchronized void onTransferStart() {
        if (this.streamCount == 0) {
            this.startTimeMs = this.clock.elapsedRealtime();
        }
        this.streamCount++;
    }

    public synchronized void onBytesTransferred(int bytes) {
        this.bytesAccumulator += (long) bytes;
    }

    public synchronized void onTransferEnd() {
        Assertions.checkState(this.streamCount > 0);
        long nowMs = this.clock.elapsedRealtime();
        int elapsedMs = (int) (nowMs - this.startTimeMs);
        if (elapsedMs > 0) {
            this.slidingPercentile.addSample((int) Math.sqrt((double) this.bytesAccumulator), (float) ((this.bytesAccumulator * 8000) / ((long) elapsedMs)));
            float bandwidthEstimateFloat = this.slidingPercentile.getPercentile(0.5f);
            this.bitrateEstimate = Float.isNaN(bandwidthEstimateFloat) ? -1 : (long) bandwidthEstimateFloat;
            notifyBandwidthSample(elapsedMs, this.bytesAccumulator, this.bitrateEstimate);
        }
        this.streamCount--;
        if (this.streamCount > 0) {
            this.startTimeMs = nowMs;
        }
        this.bytesAccumulator = 0;
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
