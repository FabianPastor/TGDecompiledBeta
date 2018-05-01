package org.telegram.messenger.exoplayer2.upstream;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.upstream.BandwidthMeter.EventListener;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.SlidingPercentile;

public final class DefaultBandwidthMeter implements BandwidthMeter, TransferListener<Object> {
    private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
    public static final int DEFAULT_MAX_WEIGHT = 2000;
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

    public DefaultBandwidthMeter() {
        this(null, null);
    }

    public DefaultBandwidthMeter(Handler handler, EventListener eventListener) {
        this(handler, eventListener, 2000);
    }

    public DefaultBandwidthMeter(Handler handler, EventListener eventListener, int i) {
        this(handler, eventListener, i, Clock.DEFAULT);
    }

    public DefaultBandwidthMeter(Handler handler, EventListener eventListener, int i, Clock clock) {
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.slidingPercentile = new SlidingPercentile(i);
        this.clock = clock;
        this.bitrateEstimate = -1;
    }

    public synchronized long getBitrateEstimate() {
        return this.bitrateEstimate;
    }

    public synchronized void onTransferStart(Object obj, DataSpec dataSpec) {
        if (this.streamCount == null) {
            this.sampleStartTimeMs = this.clock.elapsedRealtime();
        }
        this.streamCount++;
    }

    public synchronized void onBytesTransferred(Object obj, int i) {
        this.sampleBytesTransferred += (long) i;
    }

    public synchronized void onTransferEnd(Object obj) {
        Assertions.checkState(this.streamCount > null ? 1 : null);
        long elapsedRealtime = this.clock.elapsedRealtime();
        int i = (int) (elapsedRealtime - this.sampleStartTimeMs);
        long j = (long) i;
        this.totalElapsedTimeMs += j;
        this.totalBytesTransferred += this.sampleBytesTransferred;
        if (i > 0) {
            this.slidingPercentile.addSample((int) Math.sqrt((double) this.sampleBytesTransferred), (float) ((this.sampleBytesTransferred * 8000) / j));
            if (this.totalElapsedTimeMs >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS || this.totalBytesTransferred >= 524288) {
                obj = this.slidingPercentile.getPercentile(0.5f);
                this.bitrateEstimate = Float.isNaN(obj) ? -1 : (long) obj;
            }
        }
        notifyBandwidthSample(i, this.sampleBytesTransferred, this.bitrateEstimate);
        obj = this.streamCount - 1;
        this.streamCount = obj;
        if (obj > null) {
            this.sampleStartTimeMs = elapsedRealtime;
        }
        this.sampleBytesTransferred = 0;
    }

    private void notifyBandwidthSample(int i, long j, long j2) {
        if (this.eventHandler != null && this.eventListener != null) {
            final int i2 = i;
            final long j3 = j;
            final long j4 = j2;
            this.eventHandler.post(new Runnable() {
                public void run() {
                    DefaultBandwidthMeter.this.eventListener.onBandwidthSample(i2, j3, j4);
                }
            });
        }
    }
}
