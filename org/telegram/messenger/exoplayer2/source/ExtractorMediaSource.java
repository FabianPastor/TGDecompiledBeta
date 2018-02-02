package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ExtractorMediaSource implements Listener, MediaSource {
    public static final int DEFAULT_LOADING_CHECK_INTERVAL_BYTES = 1048576;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
    public static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
    private final int continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
    private final EventDispatcher eventDispatcher;
    private final ExtractorsFactory extractorsFactory;
    private final int minLoadableRetryCount;
    private Listener sourceListener;
    private long timelineDurationUs;
    private boolean timelineIsSeekable;
    private final Uri uri;

    @Deprecated
    public interface EventListener {
        void onLoadError(IOException iOException);
    }

    private static final class EventListenerWrapper implements MediaSourceEventListener {
        private final EventListener eventListener;

        public EventListenerWrapper(EventListener eventListener) {
            this.eventListener = (EventListener) Assertions.checkNotNull(eventListener);
        }

        public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        }

        public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        }

        public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        }

        public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            this.eventListener.onLoadError(error);
        }

        public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
        }

        public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
        }
    }

    public static final class Factory implements MediaSourceFactory {
        private int continueLoadingCheckIntervalBytes = ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES;
        private String customCacheKey;
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private ExtractorsFactory extractorsFactory;
        private boolean isCreateCalled;
        private int minLoadableRetryCount = -1;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
        }

        public Factory setExtractorsFactory(ExtractorsFactory extractorsFactory) {
            Assertions.checkState(!this.isCreateCalled);
            this.extractorsFactory = extractorsFactory;
            return this;
        }

        public Factory setCustomCacheKey(String customCacheKey) {
            Assertions.checkState(!this.isCreateCalled);
            this.customCacheKey = customCacheKey;
            return this;
        }

        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(!this.isCreateCalled);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setContinueLoadingCheckIntervalBytes(int continueLoadingCheckIntervalBytes) {
            Assertions.checkState(!this.isCreateCalled);
            this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes;
            return this;
        }

        public ExtractorMediaSource createMediaSource(Uri uri) {
            return createMediaSource(uri, null, null);
        }

        public ExtractorMediaSource createMediaSource(Uri uri, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            if (this.extractorsFactory == null) {
                this.extractorsFactory = new DefaultExtractorsFactory();
            }
            return new ExtractorMediaSource(uri, this.dataSourceFactory, this.extractorsFactory, this.minLoadableRetryCount, eventHandler, eventListener, this.customCacheKey, this.continueLoadingCheckIntervalBytes);
        }

        public int[] getSupportedTypes() {
            return new int[]{3};
        }
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory, Handler eventHandler, EventListener eventListener) {
        this(uri, dataSourceFactory, extractorsFactory, eventHandler, eventListener, null);
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory, Handler eventHandler, EventListener eventListener, String customCacheKey) {
        this(uri, dataSourceFactory, extractorsFactory, -1, eventHandler, eventListener, customCacheKey, (int) DEFAULT_LOADING_CHECK_INTERVAL_BYTES);
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, String customCacheKey, int continueLoadingCheckIntervalBytes) {
        this(uri, dataSourceFactory, extractorsFactory, minLoadableRetryCount, eventHandler, eventListener == null ? null : new EventListenerWrapper(eventListener), customCacheKey, continueLoadingCheckIntervalBytes);
    }

    private ExtractorMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener, String customCacheKey, int continueLoadingCheckIntervalBytes) {
        this.uri = uri;
        this.dataSourceFactory = dataSourceFactory;
        this.extractorsFactory = extractorsFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.customCacheKey = customCacheKey;
        this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes;
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        boolean z;
        if (this.sourceListener == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.sourceListener = listener;
        notifySourceInfoRefreshed(C.TIME_UNSET, false);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        Assertions.checkArgument(id.periodIndex == 0);
        return new ExtractorMediaPeriod(this.uri, this.dataSourceFactory.createDataSource(), this.extractorsFactory.createExtractors(), this.minLoadableRetryCount, this.eventDispatcher, this, allocator, this.customCacheKey, this.continueLoadingCheckIntervalBytes);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((ExtractorMediaPeriod) mediaPeriod).release();
    }

    public void releaseSource() {
    }

    public void onSourceInfoRefreshed(long durationUs, boolean isSeekable) {
        if (durationUs == C.TIME_UNSET) {
            durationUs = this.timelineDurationUs;
        }
        if (this.timelineDurationUs != durationUs || this.timelineIsSeekable != isSeekable) {
            notifySourceInfoRefreshed(durationUs, isSeekable);
        }
    }

    private void notifySourceInfoRefreshed(long durationUs, boolean isSeekable) {
        this.timelineDurationUs = durationUs;
        this.timelineIsSeekable = isSeekable;
        this.sourceListener.onSourceInfoRefreshed(this, new SinglePeriodTimeline(this.timelineDurationUs, this.timelineIsSeekable, false), null);
    }
}
