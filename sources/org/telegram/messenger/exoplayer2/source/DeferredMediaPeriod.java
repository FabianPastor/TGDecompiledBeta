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

    public DeferredMediaPeriod(MediaSource mediaSource, MediaPeriodId mediaPeriodId, Allocator allocator) {
        this.id = mediaPeriodId;
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

    public void prepare(Callback callback, long j) {
        this.callback = callback;
        this.preparePositionUs = j;
        if (this.mediaPeriod != null) {
            this.mediaPeriod.prepare(this, j);
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

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        return this.mediaPeriod.selectTracks(trackSelectionArr, zArr, sampleStreamArr, zArr2, j);
    }

    public void discardBuffer(long j, boolean z) {
        this.mediaPeriod.discardBuffer(j, z);
    }

    public long readDiscontinuity() {
        return this.mediaPeriod.readDiscontinuity();
    }

    public long getBufferedPositionUs() {
        return this.mediaPeriod.getBufferedPositionUs();
    }

    public long seekToUs(long j) {
        return this.mediaPeriod.seekToUs(j);
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return this.mediaPeriod.getAdjustedSeekPositionUs(j, seekParameters);
    }

    public long getNextLoadPositionUs() {
        return this.mediaPeriod.getNextLoadPositionUs();
    }

    public void reevaluateBuffer(long j) {
        this.mediaPeriod.reevaluateBuffer(j);
    }

    public boolean continueLoading(long j) {
        return (this.mediaPeriod == null || this.mediaPeriod.continueLoading(j) == null) ? 0 : 1;
    }

    public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
        this.callback.onContinueLoadingRequested(this);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        this.callback.onPrepared(this);
    }
}
