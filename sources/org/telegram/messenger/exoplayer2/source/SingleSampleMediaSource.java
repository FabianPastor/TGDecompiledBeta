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

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory) {
            this.dataSourceFactory = (org.telegram.messenger.exoplayer2.upstream.DataSource.Factory) Assertions.checkNotNull(factory);
        }

        public Factory setMinLoadableRetryCount(int i) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = i;
            return this;
        }

        public Factory setTreatLoadErrorsAsEndOfStream(boolean z) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.treatLoadErrorsAsEndOfStream = z;
            return this;
        }

        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long j) {
            return createMediaSource(uri, format, j, null, null);
        }

        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            this.isCreateCalled = true;
            return new SingleSampleMediaSource(uri, this.dataSourceFactory, format, j, this.minLoadableRetryCount, handler, mediaSourceEventListener, this.treatLoadErrorsAsEndOfStream);
        }
    }

    private static final class EventListenerWrapper implements MediaSourceEventListener {
        private final EventListener eventListener;
        private final int eventSourceId;

        public void onDownstreamFormatChanged(int i, Format format, int i2, Object obj, long j) {
        }

        public void onLoadCanceled(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
        }

        public void onLoadCompleted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
        }

        public void onLoadStarted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3) {
        }

        public void onUpstreamDiscarded(int i, long j, long j2) {
        }

        public EventListenerWrapper(EventListener eventListener, int i) {
            this.eventListener = (EventListener) Assertions.checkNotNull(eventListener);
            this.eventSourceId = i;
        }

        public void onLoadError(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5, IOException iOException, boolean z) {
            this.eventListener.onLoadError(this.eventSourceId, iOException);
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public void releaseSource() {
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Format format, long j) {
        this(uri, factory, format, j, 3);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Format format, long j, int i) {
        this(uri, factory, format, j, i, null, null, false);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Format format, long j, int i, Handler handler, EventListener eventListener, int i2, boolean z) {
        EventListener eventListener2 = eventListener;
        this(uri, factory, format, j, i, handler, eventListener2 == null ? null : new EventListenerWrapper(eventListener2, i2), z);
    }

    private SingleSampleMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Format format, long j, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener, boolean z) {
        this.dataSourceFactory = factory;
        this.format = format;
        this.durationUs = j;
        this.minLoadableRetryCount = i;
        this.treatLoadErrorsAsEndOfStream = z;
        this.eventDispatcher = new EventDispatcher(handler, mediaSourceEventListener);
        this.dataSpec = new DataSpec(uri);
        this.timeline = new SinglePeriodTimeline(j, true, null);
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(this.isPrepared ^ 1, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.isPrepared = true;
        listener.onSourceInfoRefreshed(this, this.timeline, false);
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        Assertions.checkArgument(mediaPeriodId.periodIndex == null ? true : null);
        return new SingleSampleMediaPeriod(this.dataSpec, this.dataSourceFactory, this.format, this.durationUs, this.minLoadableRetryCount, this.eventDispatcher, this.treatLoadErrorsAsEndOfStream);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((SingleSampleMediaPeriod) mediaPeriod).release();
    }
}
