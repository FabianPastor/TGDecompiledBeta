package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class ClippingMediaPeriod implements MediaPeriod, Callback {
    private Callback callback;
    long endUs;
    public final MediaPeriod mediaPeriod;
    private long pendingInitialDiscontinuityPositionUs;
    private ClippingSampleStream[] sampleStreams = new ClippingSampleStream[null];
    long startUs;

    private final class ClippingSampleStream implements SampleStream {
        public final SampleStream childStream;
        private boolean sentEos;

        public ClippingSampleStream(SampleStream sampleStream) {
            this.childStream = sampleStream;
        }

        public void clearSentEos() {
            this.sentEos = false;
        }

        public boolean isReady() {
            return !ClippingMediaPeriod.this.isPendingInitialDiscontinuity() && this.childStream.isReady();
        }

        public void maybeThrowError() throws IOException {
            this.childStream.maybeThrowError();
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            if (this.sentEos) {
                decoderInputBuffer.setFlags(4);
                return -4;
            }
            z = this.childStream.readData(formatHolder, decoderInputBuffer, z);
            if (z) {
                decoderInputBuffer = formatHolder.format;
                int i = 0;
                z = ClippingMediaPeriod.this.startUs != 0 ? false : decoderInputBuffer.encoderDelay;
                if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE) {
                    i = decoderInputBuffer.encoderPadding;
                }
                formatHolder.format = decoderInputBuffer.copyWithGaplessInfo(z, i);
                return -5;
            } else if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE || ((!z || decoderInputBuffer.timeUs < ClippingMediaPeriod.this.endUs) && !(z && ClippingMediaPeriod.this.getBufferedPositionUs() == Long.MIN_VALUE))) {
                if (z && decoderInputBuffer.isEndOfStream() == null) {
                    decoderInputBuffer.timeUs -= ClippingMediaPeriod.this.startUs;
                }
                return z;
            } else {
                decoderInputBuffer.clear();
                decoderInputBuffer.setFlags(4);
                this.sentEos = true;
                return -4;
            }
        }

        public int skipData(long j) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            return this.childStream.skipData(ClippingMediaPeriod.this.startUs + j);
        }
    }

    public ClippingMediaPeriod(MediaPeriod mediaPeriod, boolean z) {
        this.mediaPeriod = mediaPeriod;
        this.pendingInitialDiscontinuityPositionUs = z ? 0 : -922337203NUM;
        this.startUs = C0542C.TIME_UNSET;
        this.endUs = C0542C.TIME_UNSET;
    }

    public void setClipping(long j, long j2) {
        this.startUs = j;
        this.endUs = j2;
    }

    public void prepare(Callback callback, long j) {
        this.callback = callback;
        this.mediaPeriod.prepare(this, this.startUs + j);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.mediaPeriod.maybeThrowPrepareError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.mediaPeriod.getTrackGroups();
    }

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        boolean z;
        SampleStream[] sampleStreamArr2 = sampleStreamArr;
        int i = 0;
        this.sampleStreams = new ClippingSampleStream[sampleStreamArr2.length];
        SampleStream[] sampleStreamArr3 = new SampleStream[sampleStreamArr2.length];
        int i2 = 0;
        while (true) {
            SampleStream sampleStream = null;
            if (i2 >= sampleStreamArr2.length) {
                break;
            }
            r0.sampleStreams[i2] = (ClippingSampleStream) sampleStreamArr2[i2];
            if (r0.sampleStreams[i2] != null) {
                sampleStream = r0.sampleStreams[i2].childStream;
            }
            sampleStreamArr3[i2] = sampleStream;
            i2++;
        }
        long selectTracks = r0.mediaPeriod.selectTracks(trackSelectionArr, zArr, sampleStreamArr3, zArr2, j + r0.startUs) - r0.startUs;
        long j2 = (isPendingInitialDiscontinuity() && j == 0 && shouldKeepInitialDiscontinuity(r0.startUs, trackSelectionArr)) ? selectTracks : C0542C.TIME_UNSET;
        r0.pendingInitialDiscontinuityPositionUs = j2;
        if (selectTracks != j) {
            if (selectTracks >= 0) {
                if (r0.endUs != Long.MIN_VALUE) {
                    if (r0.startUs + selectTracks <= r0.endUs) {
                    }
                }
            }
            z = false;
            Assertions.checkState(z);
            while (i < sampleStreamArr2.length) {
                if (sampleStreamArr3[i] != null) {
                    r0.sampleStreams[i] = null;
                } else if (sampleStreamArr2[i] != null || r0.sampleStreams[i].childStream != sampleStreamArr3[i]) {
                    r0.sampleStreams[i] = new ClippingSampleStream(sampleStreamArr3[i]);
                }
                sampleStreamArr2[i] = r0.sampleStreams[i];
                i++;
            }
            return selectTracks;
        }
        z = true;
        Assertions.checkState(z);
        while (i < sampleStreamArr2.length) {
            if (sampleStreamArr3[i] != null) {
                if (sampleStreamArr2[i] != null) {
                }
                r0.sampleStreams[i] = new ClippingSampleStream(sampleStreamArr3[i]);
            } else {
                r0.sampleStreams[i] = null;
            }
            sampleStreamArr2[i] = r0.sampleStreams[i];
            i++;
        }
        return selectTracks;
    }

    public void discardBuffer(long j, boolean z) {
        this.mediaPeriod.discardBuffer(j + this.startUs, z);
    }

    public void reevaluateBuffer(long j) {
        this.mediaPeriod.reevaluateBuffer(j + this.startUs);
    }

    public long readDiscontinuity() {
        long j;
        if (isPendingInitialDiscontinuity()) {
            j = this.pendingInitialDiscontinuityPositionUs;
            this.pendingInitialDiscontinuityPositionUs = C0542C.TIME_UNSET;
            long readDiscontinuity = readDiscontinuity();
            if (readDiscontinuity != C0542C.TIME_UNSET) {
                j = readDiscontinuity;
            }
            return j;
        }
        j = this.mediaPeriod.readDiscontinuity();
        if (j == C0542C.TIME_UNSET) {
            return C0542C.TIME_UNSET;
        }
        boolean z = false;
        Assertions.checkState(j >= this.startUs);
        if (this.endUs == Long.MIN_VALUE || j <= this.endUs) {
            z = true;
        }
        Assertions.checkState(z);
        return j - this.startUs;
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = this.mediaPeriod.getBufferedPositionUs();
        if (bufferedPositionUs != Long.MIN_VALUE) {
            if (this.endUs == Long.MIN_VALUE || bufferedPositionUs < this.endUs) {
                return Math.max(0, bufferedPositionUs - this.startUs);
            }
        }
        return Long.MIN_VALUE;
    }

    public long seekToUs(long j) {
        this.pendingInitialDiscontinuityPositionUs = C0542C.TIME_UNSET;
        boolean z = false;
        for (ClippingSampleStream clippingSampleStream : this.sampleStreams) {
            if (clippingSampleStream != null) {
                clippingSampleStream.clearSentEos();
            }
        }
        long j2 = j + this.startUs;
        j = this.mediaPeriod.seekToUs(j2);
        if (j == j2 || (j >= this.startUs && (this.endUs == Long.MIN_VALUE || j <= this.endUs))) {
            z = true;
        }
        Assertions.checkState(z);
        return j - this.startUs;
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        if (j == this.startUs) {
            return 0;
        }
        long j2 = j + this.startUs;
        return this.mediaPeriod.getAdjustedSeekPositionUs(j2, clipSeekParameters(j2, seekParameters)) - this.startUs;
    }

    public long getNextLoadPositionUs() {
        long nextLoadPositionUs = this.mediaPeriod.getNextLoadPositionUs();
        if (nextLoadPositionUs != Long.MIN_VALUE) {
            if (this.endUs == Long.MIN_VALUE || nextLoadPositionUs < this.endUs) {
                return nextLoadPositionUs - this.startUs;
            }
        }
        return Long.MIN_VALUE;
    }

    public boolean continueLoading(long j) {
        return this.mediaPeriod.continueLoading(j + this.startUs);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        mediaPeriod = (this.startUs == C0542C.TIME_UNSET || this.endUs == C0542C.TIME_UNSET) ? null : true;
        Assertions.checkState(mediaPeriod);
        this.callback.onPrepared(this);
    }

    public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
        this.callback.onContinueLoadingRequested(this);
    }

    boolean isPendingInitialDiscontinuity() {
        return this.pendingInitialDiscontinuityPositionUs != C0542C.TIME_UNSET;
    }

    private SeekParameters clipSeekParameters(long j, SeekParameters seekParameters) {
        long min = Math.min(j - this.startUs, seekParameters.toleranceBeforeUs);
        if (this.endUs == Long.MIN_VALUE) {
            j = seekParameters.toleranceAfterUs;
        } else {
            j = Math.min(this.endUs - j, seekParameters.toleranceAfterUs);
        }
        if (min == seekParameters.toleranceBeforeUs && j == seekParameters.toleranceAfterUs) {
            return seekParameters;
        }
        return new SeekParameters(min, j);
    }

    private static boolean shouldKeepInitialDiscontinuity(long j, TrackSelection[] trackSelectionArr) {
        if (j != 0) {
            for (TrackSelection trackSelection : trackSelectionArr) {
                if (trackSelection != null && !MimeTypes.isAudio(trackSelection.getSelectedFormat().sampleMimeType)) {
                    return 1;
                }
            }
        }
        return false;
    }
}
