package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
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
    private ClippingSampleStream[] sampleStreams = new ClippingSampleStream[0];
    long startUs;

    private final class ClippingSampleStream implements SampleStream {
        public final SampleStream childStream;
        private boolean sentEos;

        public ClippingSampleStream(SampleStream childStream) {
            this.childStream = childStream;
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

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            if (this.sentEos) {
                buffer.setFlags(4);
                return -4;
            }
            int result = this.childStream.readData(formatHolder, buffer, requireFormat);
            if (result == -5) {
                Format format = formatHolder.format;
                formatHolder.format = format.copyWithGaplessInfo(ClippingMediaPeriod.this.startUs != 0 ? 0 : format.encoderDelay, ClippingMediaPeriod.this.endUs != Long.MIN_VALUE ? 0 : format.encoderPadding);
                return -5;
            } else if (ClippingMediaPeriod.this.endUs != Long.MIN_VALUE && ((result == -4 && buffer.timeUs >= ClippingMediaPeriod.this.endUs) || (result == -3 && ClippingMediaPeriod.this.getBufferedPositionUs() == Long.MIN_VALUE))) {
                buffer.clear();
                buffer.setFlags(4);
                this.sentEos = true;
                return -4;
            } else if (result != -4 || buffer.isEndOfStream()) {
                return result;
            } else {
                buffer.timeUs -= ClippingMediaPeriod.this.startUs;
                return result;
            }
        }

        public int skipData(long positionUs) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            return this.childStream.skipData(ClippingMediaPeriod.this.startUs + positionUs);
        }
    }

    public ClippingMediaPeriod(MediaPeriod mediaPeriod, boolean enableInitialDiscontinuity) {
        this.mediaPeriod = mediaPeriod;
        this.pendingInitialDiscontinuityPositionUs = enableInitialDiscontinuity ? 0 : C.TIME_UNSET;
        this.startUs = C.TIME_UNSET;
        this.endUs = C.TIME_UNSET;
    }

    public void setClipping(long startUs, long endUs) {
        this.startUs = startUs;
        this.endUs = endUs;
    }

    public void prepare(Callback callback, long positionUs) {
        this.callback = callback;
        this.mediaPeriod.prepare(this, this.startUs + positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.mediaPeriod.maybeThrowPrepareError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.mediaPeriod.getTrackGroups();
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        this.sampleStreams = new ClippingSampleStream[streams.length];
        SampleStream[] childStreams = new SampleStream[streams.length];
        for (i = 0; i < streams.length; i++) {
            this.sampleStreams[i] = (ClippingSampleStream) streams[i];
            childStreams[i] = this.sampleStreams[i] != null ? this.sampleStreams[i].childStream : null;
        }
        long enablePositionUs = this.mediaPeriod.selectTracks(selections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs + this.startUs) - this.startUs;
        long j = (isPendingInitialDiscontinuity() && positionUs == 0 && shouldKeepInitialDiscontinuity(this.startUs, selections)) ? enablePositionUs : C.TIME_UNSET;
        this.pendingInitialDiscontinuityPositionUs = j;
        boolean z = enablePositionUs == positionUs || (enablePositionUs >= 0 && (this.endUs == Long.MIN_VALUE || this.startUs + enablePositionUs <= this.endUs));
        Assertions.checkState(z);
        i = 0;
        while (i < streams.length) {
            if (childStreams[i] == null) {
                this.sampleStreams[i] = null;
            } else if (streams[i] == null || this.sampleStreams[i].childStream != childStreams[i]) {
                this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
            }
            streams[i] = this.sampleStreams[i];
            i++;
        }
        return enablePositionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        this.mediaPeriod.discardBuffer(this.startUs + positionUs, toKeyframe);
    }

    public void reevaluateBuffer(long positionUs) {
        this.mediaPeriod.reevaluateBuffer(this.startUs + positionUs);
    }

    public long readDiscontinuity() {
        boolean z = false;
        if (isPendingInitialDiscontinuity()) {
            long initialDiscontinuityUs = this.pendingInitialDiscontinuityPositionUs;
            this.pendingInitialDiscontinuityPositionUs = C.TIME_UNSET;
            long childDiscontinuityUs = readDiscontinuity();
            return childDiscontinuityUs != C.TIME_UNSET ? childDiscontinuityUs : initialDiscontinuityUs;
        } else {
            long discontinuityUs = this.mediaPeriod.readDiscontinuity();
            if (discontinuityUs == C.TIME_UNSET) {
                return C.TIME_UNSET;
            }
            boolean z2;
            if (discontinuityUs >= this.startUs) {
                z2 = true;
            } else {
                z2 = false;
            }
            Assertions.checkState(z2);
            if (this.endUs == Long.MIN_VALUE || discontinuityUs <= this.endUs) {
                z = true;
            }
            Assertions.checkState(z);
            return discontinuityUs - this.startUs;
        }
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = this.mediaPeriod.getBufferedPositionUs();
        if (bufferedPositionUs == Long.MIN_VALUE) {
            return Long.MIN_VALUE;
        }
        if (this.endUs == Long.MIN_VALUE || bufferedPositionUs < this.endUs) {
            return Math.max(0, bufferedPositionUs - this.startUs);
        }
        return Long.MIN_VALUE;
    }

    public long seekToUs(long positionUs) {
        boolean z = false;
        this.pendingInitialDiscontinuityPositionUs = C.TIME_UNSET;
        for (ClippingSampleStream sampleStream : this.sampleStreams) {
            if (sampleStream != null) {
                sampleStream.clearSentEos();
            }
        }
        long offsetPositionUs = positionUs + this.startUs;
        long seekUs = this.mediaPeriod.seekToUs(offsetPositionUs);
        if (seekUs == offsetPositionUs || (seekUs >= this.startUs && (this.endUs == Long.MIN_VALUE || seekUs <= this.endUs))) {
            z = true;
        }
        Assertions.checkState(z);
        return seekUs - this.startUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        if (positionUs == this.startUs) {
            return 0;
        }
        long offsetPositionUs = positionUs + this.startUs;
        return this.mediaPeriod.getAdjustedSeekPositionUs(offsetPositionUs, clipSeekParameters(offsetPositionUs, seekParameters)) - this.startUs;
    }

    public long getNextLoadPositionUs() {
        long nextLoadPositionUs = this.mediaPeriod.getNextLoadPositionUs();
        if (nextLoadPositionUs == Long.MIN_VALUE) {
            return Long.MIN_VALUE;
        }
        if (this.endUs == Long.MIN_VALUE || nextLoadPositionUs < this.endUs) {
            return nextLoadPositionUs - this.startUs;
        }
        return Long.MIN_VALUE;
    }

    public boolean continueLoading(long positionUs) {
        return this.mediaPeriod.continueLoading(this.startUs + positionUs);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        boolean z = (this.startUs == C.TIME_UNSET || this.endUs == C.TIME_UNSET) ? false : true;
        Assertions.checkState(z);
        this.callback.onPrepared(this);
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.callback.onContinueLoadingRequested(this);
    }

    boolean isPendingInitialDiscontinuity() {
        return this.pendingInitialDiscontinuityPositionUs != C.TIME_UNSET;
    }

    private SeekParameters clipSeekParameters(long offsetPositionUs, SeekParameters seekParameters) {
        long toleranceAfterMs;
        long toleranceBeforeMs = Math.min(offsetPositionUs - this.startUs, seekParameters.toleranceBeforeUs);
        if (this.endUs == Long.MIN_VALUE) {
            toleranceAfterMs = seekParameters.toleranceAfterUs;
        } else {
            toleranceAfterMs = Math.min(this.endUs - offsetPositionUs, seekParameters.toleranceAfterUs);
        }
        return (toleranceBeforeMs == seekParameters.toleranceBeforeUs && toleranceAfterMs == seekParameters.toleranceAfterUs) ? seekParameters : new SeekParameters(toleranceBeforeMs, toleranceAfterMs);
    }

    private static boolean shouldKeepInitialDiscontinuity(long startUs, TrackSelection[] selections) {
        if (startUs == 0) {
            return false;
        }
        for (TrackSelection trackSelection : selections) {
            if (trackSelection != null && !MimeTypes.isAudio(trackSelection.getSelectedFormat().sampleMimeType)) {
                return true;
            }
        }
        return false;
    }
}
