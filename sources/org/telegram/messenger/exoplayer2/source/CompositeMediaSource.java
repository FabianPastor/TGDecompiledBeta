package org.telegram.messenger.exoplayer2.source;

import android.os.Handler;
import java.io.IOException;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSource.SourceInfoRefreshListener;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class CompositeMediaSource<T> extends BaseMediaSource {
    private final HashMap<T, MediaSourceAndListener> childSources = new HashMap();
    private Handler eventHandler;
    private ExoPlayer player;

    private static final class MediaSourceAndListener {
        public final MediaSourceEventListener eventListener;
        public final SourceInfoRefreshListener listener;
        public final MediaSource mediaSource;

        public MediaSourceAndListener(MediaSource mediaSource, SourceInfoRefreshListener listener, MediaSourceEventListener eventListener) {
            this.mediaSource = mediaSource;
            this.listener = listener;
            this.eventListener = eventListener;
        }
    }

    private final class ForwardingEventListener implements MediaSourceEventListener {
        private EventDispatcher eventDispatcher;
        private final T id;

        public ForwardingEventListener(T id) {
            this.eventDispatcher = CompositeMediaSource.this.createEventDispatcher(null);
            this.id = id;
        }

        public void onMediaPeriodCreated(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.mediaPeriodCreated();
            }
        }

        public void onMediaPeriodReleased(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.mediaPeriodReleased();
            }
        }

        public void onLoadStarted(int windowIndex, MediaPeriodId mediaPeriodId, LoadEventInfo loadEventData, MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadStarted(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadCompleted(int windowIndex, MediaPeriodId mediaPeriodId, LoadEventInfo loadEventData, MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadCompleted(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadCanceled(int windowIndex, MediaPeriodId mediaPeriodId, LoadEventInfo loadEventData, MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadCanceled(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadError(int windowIndex, MediaPeriodId mediaPeriodId, LoadEventInfo loadEventData, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadError(loadEventData, maybeUpdateMediaLoadData(mediaLoadData), error, wasCanceled);
            }
        }

        public void onReadingStarted(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.readingStarted();
            }
        }

        public void onUpstreamDiscarded(int windowIndex, MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.upstreamDiscarded(maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onDownstreamFormatChanged(int windowIndex, MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.downstreamFormatChanged(maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        private boolean maybeUpdateEventDispatcher(int childWindowIndex, MediaPeriodId childMediaPeriodId) {
            MediaPeriodId mediaPeriodId = null;
            if (childMediaPeriodId != null) {
                mediaPeriodId = CompositeMediaSource.this.getMediaPeriodIdForChildMediaPeriodId(this.id, childMediaPeriodId);
                if (mediaPeriodId == null) {
                    return false;
                }
            }
            int windowIndex = CompositeMediaSource.this.getWindowIndexForChildWindowIndex(this.id, childWindowIndex);
            if (!(this.eventDispatcher.windowIndex == windowIndex && Util.areEqual(this.eventDispatcher.mediaPeriodId, mediaPeriodId))) {
                this.eventDispatcher = CompositeMediaSource.this.createEventDispatcher(windowIndex, mediaPeriodId, 0);
            }
            return true;
        }

        private MediaLoadData maybeUpdateMediaLoadData(MediaLoadData mediaLoadData) {
            long mediaStartTimeMs = CompositeMediaSource.this.getMediaTimeForChildMediaTime(this.id, mediaLoadData.mediaStartTimeMs);
            long mediaEndTimeMs = CompositeMediaSource.this.getMediaTimeForChildMediaTime(this.id, mediaLoadData.mediaEndTimeMs);
            return (mediaStartTimeMs == mediaLoadData.mediaStartTimeMs && mediaEndTimeMs == mediaLoadData.mediaEndTimeMs) ? mediaLoadData : new MediaLoadData(mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat, mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaStartTimeMs, mediaEndTimeMs);
        }
    }

    protected abstract void onChildSourceInfoRefreshed(T t, MediaSource mediaSource, Timeline timeline, Object obj);

    protected CompositeMediaSource() {
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource) {
        this.player = player;
        this.eventHandler = new Handler();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (MediaSourceAndListener childSource : this.childSources.values()) {
            childSource.mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public void releaseSourceInternal() {
        for (MediaSourceAndListener childSource : this.childSources.values()) {
            childSource.mediaSource.releaseSource(childSource.listener);
            childSource.mediaSource.removeEventListener(childSource.eventListener);
        }
        this.childSources.clear();
        this.player = null;
    }

    protected final void prepareChildSource(final T id, MediaSource mediaSource) {
        boolean z;
        if (this.childSources.containsKey(id)) {
            z = false;
        } else {
            z = true;
        }
        Assertions.checkArgument(z);
        SourceInfoRefreshListener sourceListener = new SourceInfoRefreshListener() {
            public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                CompositeMediaSource.this.onChildSourceInfoRefreshed(id, source, timeline, manifest);
            }
        };
        MediaSourceEventListener eventListener = new ForwardingEventListener(id);
        this.childSources.put(id, new MediaSourceAndListener(mediaSource, sourceListener, eventListener));
        mediaSource.addEventListener(this.eventHandler, eventListener);
        mediaSource.prepareSource(this.player, false, sourceListener);
    }

    protected final void releaseChildSource(T id) {
        MediaSourceAndListener removedChild = (MediaSourceAndListener) this.childSources.remove(id);
        removedChild.mediaSource.releaseSource(removedChild.listener);
        removedChild.mediaSource.removeEventListener(removedChild.eventListener);
    }

    protected int getWindowIndexForChildWindowIndex(T t, int windowIndex) {
        return windowIndex;
    }

    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(T t, MediaPeriodId mediaPeriodId) {
        return mediaPeriodId;
    }

    protected long getMediaTimeForChildMediaTime(T t, long mediaTimeMs) {
        return mediaTimeMs;
    }
}
