package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class HlsMediaSource implements MediaSource {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private final Factory dataSourceFactory;
    private final EventDispatcher eventDispatcher;
    private final Uri manifestUri;
    private final int minLoadableRetryCount;
    private Listener sourceListener;

    public HlsMediaSource(Uri manifestUri, Factory dataSourceFactory, Handler eventHandler, AdaptiveMediaSourceEventListener eventListener) {
        this(manifestUri, dataSourceFactory, 3, eventHandler, eventListener);
    }

    public HlsMediaSource(Uri manifestUri, Factory dataSourceFactory, int minLoadableRetryCount, Handler eventHandler, AdaptiveMediaSourceEventListener eventListener) {
        this.manifestUri = manifestUri;
        this.dataSourceFactory = dataSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
    }

    public void prepareSource(Listener listener) {
        this.sourceListener = listener;
        listener.onSourceInfoRefreshed(new SinglePeriodTimeline(C.TIME_UNSET, false), null);
    }

    public void maybeThrowSourceInfoRefreshError() {
    }

    public MediaPeriod createPeriod(int index, Allocator allocator, long positionUs) {
        Assertions.checkArgument(index == 0);
        return new HlsMediaPeriod(this.manifestUri, this.dataSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, this.sourceListener, allocator, positionUs);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((HlsMediaPeriod) mediaPeriod).release();
    }

    public void releaseSource() {
        this.sourceListener = null;
    }
}
