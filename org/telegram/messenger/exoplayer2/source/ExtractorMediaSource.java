package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ExtractorMediaSource implements MediaSource, Listener {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
    public static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
    private final String customCacheKey;
    private final Factory dataSourceFactory;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final ExtractorsFactory extractorsFactory;
    private final int minLoadableRetryCount;
    private final Period period;
    private Listener sourceListener;
    private Timeline timeline;
    private boolean timelineHasDuration;
    private final Uri uri;

    public interface EventListener {
        void onLoadError(IOException iOException);
    }

    public ExtractorMediaSource(Uri uri, Factory dataSourceFactory, ExtractorsFactory extractorsFactory, Handler eventHandler, EventListener eventListener) {
        this(uri, dataSourceFactory, extractorsFactory, -1, eventHandler, eventListener, null);
    }

    public ExtractorMediaSource(Uri uri, Factory dataSourceFactory, ExtractorsFactory extractorsFactory, Handler eventHandler, EventListener eventListener, String customCacheKey) {
        this(uri, dataSourceFactory, extractorsFactory, -1, eventHandler, eventListener, customCacheKey);
    }

    public ExtractorMediaSource(Uri uri, Factory dataSourceFactory, ExtractorsFactory extractorsFactory, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, String customCacheKey) {
        this.uri = uri;
        this.dataSourceFactory = dataSourceFactory;
        this.extractorsFactory = extractorsFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.customCacheKey = customCacheKey;
        this.period = new Period();
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        this.sourceListener = listener;
        this.timeline = new SinglePeriodTimeline(C.TIME_UNSET, false);
        listener.onSourceInfoRefreshed(this.timeline, null);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MediaPeriod createPeriod(int index, Allocator allocator, long positionUs) {
        Assertions.checkArgument(index == 0);
        return new ExtractorMediaPeriod(this.uri, this.dataSourceFactory.createDataSource(), this.extractorsFactory.createExtractors(), this.minLoadableRetryCount, this.eventHandler, this.eventListener, this, allocator, this.customCacheKey);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((ExtractorMediaPeriod) mediaPeriod).release();
    }

    public void releaseSource() {
        this.sourceListener = null;
    }

    public void onSourceInfoRefreshed(Timeline newTimeline, Object manifest) {
        boolean newTimelineHasDuration = false;
        if (newTimeline.getPeriod(0, this.period).getDurationUs() != C.TIME_UNSET) {
            newTimelineHasDuration = true;
        }
        if (!this.timelineHasDuration || newTimelineHasDuration) {
            this.timeline = newTimeline;
            this.timelineHasDuration = newTimelineHasDuration;
            this.sourceListener.onSourceInfoRefreshed(this.timeline, null);
        }
    }
}
