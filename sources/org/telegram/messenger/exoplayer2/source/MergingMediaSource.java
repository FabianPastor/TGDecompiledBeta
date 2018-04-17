package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class MergingMediaSource implements MediaSource {
    private static final int PERIOD_COUNT_UNSET = -1;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private Listener listener;
    private final MediaSource[] mediaSources;
    private IllegalMergeException mergeError;
    private final ArrayList<MediaSource> pendingTimelineSources;
    private int periodCount;
    private Object primaryManifest;
    private Timeline primaryTimeline;

    public static final class IllegalMergeException extends IOException {
        public static final int REASON_PERIOD_COUNT_MISMATCH = 0;
        public final int reason;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Reason {
        }

        public IllegalMergeException(int reason) {
            this.reason = reason;
        }
    }

    public MergingMediaSource(MediaSource... mediaSources) {
        this(new DefaultCompositeSequenceableLoaderFactory(), mediaSources);
    }

    public MergingMediaSource(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, MediaSource... mediaSources) {
        this.mediaSources = mediaSources;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.pendingTimelineSources = new ArrayList(Arrays.asList(mediaSources));
        this.periodCount = -1;
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        boolean z;
        if (this.listener == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.listener = listener;
        for (int i = 0; i < this.mediaSources.length; i++) {
            final int sourceIndex = i;
            this.mediaSources[sourceIndex].prepareSource(player, false, new Listener() {
                public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                    MergingMediaSource.this.handleSourceInfoRefreshed(sourceIndex, timeline, manifest);
                }
            });
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        if (this.mergeError != null) {
            throw this.mergeError;
        }
        for (MediaSource mediaSource : this.mediaSources) {
            mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        MediaPeriod[] periods = new MediaPeriod[this.mediaSources.length];
        for (int i = 0; i < periods.length; i++) {
            periods[i] = this.mediaSources[i].createPeriod(id, allocator);
        }
        return new MergingMediaPeriod(this.compositeSequenceableLoaderFactory, periods);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        MergingMediaPeriod mergingPeriod = (MergingMediaPeriod) mediaPeriod;
        for (int i = 0; i < this.mediaSources.length; i++) {
            this.mediaSources[i].releasePeriod(mergingPeriod.periods[i]);
        }
    }

    public void releaseSource() {
        for (MediaSource mediaSource : this.mediaSources) {
            mediaSource.releaseSource();
        }
    }

    private void handleSourceInfoRefreshed(int sourceIndex, Timeline timeline, Object manifest) {
        if (this.mergeError == null) {
            this.mergeError = checkTimelineMerges(timeline);
        }
        if (this.mergeError == null) {
            this.pendingTimelineSources.remove(this.mediaSources[sourceIndex]);
            if (sourceIndex == 0) {
                this.primaryTimeline = timeline;
                this.primaryManifest = manifest;
            }
            if (this.pendingTimelineSources.isEmpty()) {
                this.listener.onSourceInfoRefreshed(this, this.primaryTimeline, this.primaryManifest);
            }
        }
    }

    private IllegalMergeException checkTimelineMerges(Timeline timeline) {
        if (this.periodCount == -1) {
            this.periodCount = timeline.getPeriodCount();
        } else if (timeline.getPeriodCount() != this.periodCount) {
            return new IllegalMergeException(0);
        }
        return null;
    }
}
