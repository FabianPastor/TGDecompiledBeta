package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SingleSampleMediaSource implements MediaSource {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private final EventDispatcher eventDispatcher;
    private final Format format;
    private boolean isPrepared;
    private final int minLoadableRetryCount;
    private final Timeline timeline;
    private final boolean treatLoadErrorsAsEndOfStream;

    @Deprecated
    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    public static final class Factory {
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private boolean isCreateCalled;
        private int minLoadableRetryCount = 3;
        private boolean treatLoadErrorsAsEndOfStream;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = (org.telegram.messenger.exoplayer2.upstream.DataSource.Factory) Assertions.checkNotNull(dataSourceFactory);
        }

        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setTreatLoadErrorsAsEndOfStream(boolean treatLoadErrorsAsEndOfStream) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
            return this;
        }

        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long durationUs) {
            return createMediaSource(uri, format, durationUs, null, null);
        }

        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long durationUs, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            return new SingleSampleMediaSource(uri, this.dataSourceFactory, format, durationUs, this.minLoadableRetryCount, eventHandler, eventListener, this.treatLoadErrorsAsEndOfStream);
        }
    }

    private static final class EventListenerWrapper implements MediaSourceEventListener {
        private final EventListener eventListener;
        private final int eventSourceId;

        public EventListenerWrapper(EventListener eventListener, int eventSourceId) {
            this.eventListener = (EventListener) Assertions.checkNotNull(eventListener);
            this.eventSourceId = eventSourceId;
        }

        public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        }

        public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        }

        public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        }

        public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            this.eventListener.onLoadError(this.eventSourceId, error);
        }

        public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
        }

        public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
        }
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs) {
        this(uri, dataSourceFactory, format, durationUs, 3);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount) {
        this(uri, dataSourceFactory, format, durationUs, minLoadableRetryCount, null, null, false);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, int eventSourceId, boolean treatLoadErrorsAsEndOfStream) {
        EventListenerWrapper eventListenerWrapper;
        EventListener eventListener2 = eventListener;
        if (eventListener2 == null) {
            eventListenerWrapper = null;
            int i = eventSourceId;
        } else {
            eventListenerWrapper = new EventListenerWrapper(eventListener2, eventSourceId);
        }
        this(uri, dataSourceFactory, format, durationUs, minLoadableRetryCount, eventHandler, eventListenerWrapper, treatLoadErrorsAsEndOfStream);
    }

    private SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener, boolean treatLoadErrorsAsEndOfStream) {
        this.dataSourceFactory = dataSourceFactory;
        this.format = format;
        this.durationUs = durationUs;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.dataSpec = new DataSpec(uri);
        this.timeline = new SinglePeriodTimeline(durationUs, true, false);
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        Assertions.checkState(this.isPrepared ^ true, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.isPrepared = true;
        listener.onSourceInfoRefreshed(this, this.timeline, null);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        Assertions.checkArgument(id.periodIndex == 0);
        return new SingleSampleMediaPeriod(this.dataSpec, this.dataSourceFactory, this.format, this.durationUs, this.minLoadableRetryCount, this.eventDispatcher, this.treatLoadErrorsAsEndOfStream);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((SingleSampleMediaPeriod) mediaPeriod).release();
    }

    public void releaseSource() {
    }
}
