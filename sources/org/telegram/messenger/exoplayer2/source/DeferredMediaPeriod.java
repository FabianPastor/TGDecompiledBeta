package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public final class DeferredMediaPeriod implements MediaPeriod, Callback {
    private final Allocator allocator;
    private Callback callback;
    private final MediaPeriodId id;
    private MediaPeriod mediaPeriod;
    public final MediaSource mediaSource;
    private long preparePositionUs;

    public DeferredMediaPeriod(MediaSource mediaSource, MediaPeriodId id, Allocator allocator) {
        this.id = id;
        this.allocator = allocator;
        this.mediaSource = mediaSource;
    }

    public void createPeriod() {
        this.mediaPeriod = this.mediaSource.createPeriod(this.id, this.allocator);
        if (this.callback != null) {
            this.mediaPeriod.prepare(this, this.preparePositionUs);
        }
    }

    public void releasePeriod() {
        if (this.mediaPeriod != null) {
            this.mediaSource.releasePeriod(this.mediaPeriod);
        }
    }

    public void prepare(Callback callback, long preparePositionUs) {
        this.callback = callback;
        this.preparePositionUs = preparePositionUs;
        if (this.mediaPeriod != null) {
            this.mediaPeriod.prepare(this, preparePositionUs);
        }
    }

    public void maybeThrowPrepareError() throws IOException {
        if (this.mediaPeriod != null) {
            this.mediaPeriod.maybeThrowPrepareError();
        } else {
            this.mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.mediaPeriod.getTrackGroups();
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        return this.mediaPeriod.selectTracks(selections, mayRetainStreamFlags, streams, streamResetFlags, positionUs);
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        this.mediaPeriod.discardBuffer(positionUs, toKeyframe);
    }

    public long readDiscontinuity() {
        return this.mediaPeriod.readDiscontinuity();
    }

    public long getBufferedPositionUs() {
        return this.mediaPeriod.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        return this.mediaPeriod.seekToUs(positionUs);
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.mediaPeriod.getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public long getNextLoadPositionUs() {
        return this.mediaPeriod.getNextLoadPositionUs();
    }

    public void reevaluateBuffer(long positionUs) {
        this.mediaPeriod.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.mediaPeriod != null && this.mediaPeriod.continueLoading(positionUs);
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.callback.onContinueLoadingRequested(this);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        this.callback.onPrepared(this);
    }
}
