package org.telegram.messenger.exoplayer2;

import android.util.Log;
import org.telegram.messenger.exoplayer2.source.ClippingMediaPeriod;
import org.telegram.messenger.exoplayer2.source.EmptySampleStream;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodHolder {
    private static final String TAG = "MediaPeriodHolder";
    public boolean hasEnabledTracks;
    public MediaPeriodInfo info;
    public final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    public MediaPeriodHolder next;
    private TrackSelectorResult periodTrackSelectorResult;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    public long rendererPositionOffsetUs;
    public final SampleStream[] sampleStreams;
    private final TrackSelector trackSelector;
    public TrackSelectorResult trackSelectorResult;
    public final Object uid;

    public MediaPeriodHolder(RendererCapabilities[] rendererCapabilitiesArr, long j, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, Object obj, MediaPeriodInfo mediaPeriodInfo) {
        this.rendererCapabilities = rendererCapabilitiesArr;
        this.rendererPositionOffsetUs = j - mediaPeriodInfo.startPositionUs;
        this.trackSelector = trackSelector;
        this.mediaSource = mediaSource;
        this.uid = Assertions.checkNotNull(obj);
        this.info = mediaPeriodInfo;
        this.sampleStreams = new SampleStream[rendererCapabilitiesArr.length];
        this.mayRetainStreamFlags = new boolean[rendererCapabilitiesArr.length];
        rendererCapabilitiesArr = mediaSource.createPeriod(mediaPeriodInfo.id, allocator);
        if (mediaPeriodInfo.endPositionUs != Long.MIN_VALUE) {
            j = new ClippingMediaPeriod(rendererCapabilitiesArr, true);
            j.setClipping(0, mediaPeriodInfo.endPositionUs);
            rendererCapabilitiesArr = j;
        }
        this.mediaPeriod = rendererCapabilitiesArr;
    }

    public long toRendererTime(long j) {
        return j + getRendererOffset();
    }

    public long toPeriodTime(long j) {
        return j - getRendererOffset();
    }

    public long getRendererOffset() {
        return this.rendererPositionOffsetUs;
    }

    public boolean isFullyBuffered() {
        return this.prepared && (!this.hasEnabledTracks || this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE);
    }

    public long getDurationUs() {
        return this.info.durationUs;
    }

    public long getBufferedPositionUs(boolean z) {
        if (!this.prepared) {
            return this.info.startPositionUs;
        }
        long bufferedPositionUs = this.mediaPeriod.getBufferedPositionUs();
        if (bufferedPositionUs == Long.MIN_VALUE && z) {
            bufferedPositionUs = this.info.durationUs;
        }
        return bufferedPositionUs;
    }

    public long getNextLoadPositionUs() {
        return !this.prepared ? 0 : this.mediaPeriod.getNextLoadPositionUs();
    }

    public TrackSelectorResult handlePrepared(float f) throws ExoPlaybackException {
        this.prepared = true;
        selectTracks(f);
        long applyTrackSelection = applyTrackSelection(this.info.startPositionUs, 0.0f);
        this.rendererPositionOffsetUs += this.info.startPositionUs - applyTrackSelection;
        this.info = this.info.copyWithStartPositionUs(applyTrackSelection);
        return this.trackSelectorResult;
    }

    public void reevaluateBuffer(long j) {
        if (this.prepared) {
            this.mediaPeriod.reevaluateBuffer(toPeriodTime(j));
        }
    }

    public void continueLoading(long j) {
        this.mediaPeriod.continueLoading(toPeriodTime(j));
    }

    public boolean selectTracks(float f) throws ExoPlaybackException {
        TrackSelectorResult selectTracks = this.trackSelector.selectTracks(this.rendererCapabilities, this.mediaPeriod.getTrackGroups());
        int i = 0;
        if (selectTracks.isEquivalent(this.periodTrackSelectorResult)) {
            return false;
        }
        this.trackSelectorResult = selectTracks;
        TrackSelection[] all = this.trackSelectorResult.selections.getAll();
        int length = all.length;
        while (i < length) {
            TrackSelection trackSelection = all[i];
            if (trackSelection != null) {
                trackSelection.onPlaybackSpeed(f);
            }
            i++;
        }
        return true;
    }

    public long applyTrackSelection(long j, boolean z) {
        return applyTrackSelection(j, z, new boolean[this.rendererCapabilities.length]);
    }

    public long applyTrackSelection(long j, boolean z, boolean[] zArr) {
        TrackSelectionArray trackSelectionArray = this.trackSelectorResult.selections;
        int i = 0;
        while (true) {
            boolean z2 = true;
            if (i >= trackSelectionArray.length) {
                break;
            }
            boolean[] zArr2 = r0.mayRetainStreamFlags;
            if (z || !r0.trackSelectorResult.isEquivalent(r0.periodTrackSelectorResult, i)) {
                z2 = false;
            }
            zArr2[i] = z2;
            i++;
        }
        disassociateNoSampleRenderersWithEmptySampleStream(r0.sampleStreams);
        updatePeriodTrackSelectorResult(r0.trackSelectorResult);
        long selectTracks = r0.mediaPeriod.selectTracks(trackSelectionArray.getAll(), r0.mayRetainStreamFlags, r0.sampleStreams, zArr, j);
        associateNoSampleRenderersWithEmptySampleStream(r0.sampleStreams);
        r0.hasEnabledTracks = false;
        for (int i2 = 0; i2 < r0.sampleStreams.length; i2++) {
            if (r0.sampleStreams[i2] != null) {
                Assertions.checkState(r0.trackSelectorResult.renderersEnabled[i2]);
                if (r0.rendererCapabilities[i2].getTrackType() != 5) {
                    r0.hasEnabledTracks = true;
                }
            } else {
                Assertions.checkState(trackSelectionArray.get(i2) == null);
            }
        }
        return selectTracks;
    }

    public void release() {
        updatePeriodTrackSelectorResult(null);
        try {
            if (this.info.endPositionUs != Long.MIN_VALUE) {
                this.mediaSource.releasePeriod(((ClippingMediaPeriod) this.mediaPeriod).mediaPeriod);
            } else {
                this.mediaSource.releasePeriod(this.mediaPeriod);
            }
        } catch (Throwable e) {
            Log.e(TAG, "Period release failed.", e);
        }
    }

    private void updatePeriodTrackSelectorResult(TrackSelectorResult trackSelectorResult) {
        if (this.periodTrackSelectorResult != null) {
            disableTrackSelectionsInResult(this.periodTrackSelectorResult);
        }
        this.periodTrackSelectorResult = trackSelectorResult;
        if (this.periodTrackSelectorResult != null) {
            enableTrackSelectionsInResult(this.periodTrackSelectorResult);
        }
    }

    private void enableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult) {
        for (int i = 0; i < trackSelectorResult.renderersEnabled.length; i++) {
            boolean z = trackSelectorResult.renderersEnabled[i];
            TrackSelection trackSelection = trackSelectorResult.selections.get(i);
            if (z && trackSelection != null) {
                trackSelection.enable();
            }
        }
    }

    private void disableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult) {
        for (int i = 0; i < trackSelectorResult.renderersEnabled.length; i++) {
            boolean z = trackSelectorResult.renderersEnabled[i];
            TrackSelection trackSelection = trackSelectorResult.selections.get(i);
            if (z && trackSelection != null) {
                trackSelection.disable();
            }
        }
    }

    private void disassociateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreamArr) {
        for (int i = 0; i < this.rendererCapabilities.length; i++) {
            if (this.rendererCapabilities[i].getTrackType() == 5) {
                sampleStreamArr[i] = null;
            }
        }
    }

    private void associateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreamArr) {
        int i = 0;
        while (i < this.rendererCapabilities.length) {
            if (this.rendererCapabilities[i].getTrackType() == 5 && this.trackSelectorResult.renderersEnabled[i]) {
                sampleStreamArr[i] = new EmptySampleStream();
            }
            i++;
        }
    }
}
