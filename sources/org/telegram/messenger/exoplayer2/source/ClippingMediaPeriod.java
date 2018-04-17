package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
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
                int encoderPadding = 0;
                int encoderDelay = ClippingMediaPeriod.this.startUs != 0 ? 0 : format.encoderDelay;
                if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE) {
                    encoderPadding = format.encoderPadding;
                }
                formatHolder.format = format.copyWithGaplessInfo(encoderDelay, encoderPadding);
                return -5;
            } else if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE || ((result != -4 || buffer.timeUs < ClippingMediaPeriod.this.endUs) && !(result == -3 && ClippingMediaPeriod.this.getBufferedPositionUs() == Long.MIN_VALUE))) {
                if (result == -4 && !buffer.isEndOfStream()) {
                    buffer.timeUs -= ClippingMediaPeriod.this.startUs;
                }
                return result;
            } else {
                buffer.clear();
                buffer.setFlags(4);
                this.sentEos = true;
                return -4;
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
        this.pendingInitialDiscontinuityPositionUs = enableInitialDiscontinuity ? 0 : C0542C.TIME_UNSET;
        this.startUs = C0542C.TIME_UNSET;
        this.endUs = C0542C.TIME_UNSET;
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
        long j;
        boolean z;
        SampleStream[] sampleStreamArr = streams;
        int i = 0;
        this.sampleStreams = new ClippingSampleStream[sampleStreamArr.length];
        SampleStream[] childStreams = new SampleStream[sampleStreamArr.length];
        int i2 = 0;
        while (true) {
            SampleStream sampleStream = null;
            if (i2 >= sampleStreamArr.length) {
                break;
            }
            r0.sampleStreams[i2] = (ClippingSampleStream) sampleStreamArr[i2];
            if (r0.sampleStreams[i2] != null) {
                sampleStream = r0.sampleStreams[i2].childStream;
            }
            childStreams[i2] = sampleStream;
            i2++;
        }
        long enablePositionUs = r0.mediaPeriod.selectTracks(selections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs + r0.startUs) - r0.startUs;
        if (!isPendingInitialDiscontinuity() || positionUs != 0) {
            TrackSelection[] trackSelectionArr = selections;
        } else if (shouldKeepInitialDiscontinuity(r0.startUs, selections)) {
            j = enablePositionUs;
            r0.pendingInitialDiscontinuityPositionUs = j;
            if (enablePositionUs != positionUs) {
                if (enablePositionUs >= 0) {
                    if (r0.endUs != Long.MIN_VALUE) {
                        if (r0.startUs + enablePositionUs <= r0.endUs) {
                        }
                    }
                }
                z = false;
                Assertions.checkState(z);
                while (i < sampleStreamArr.length) {
                    if (childStreams[i] != null) {
                        r0.sampleStreams[i] = null;
                    } else if (sampleStreamArr[i] != null || r0.sampleStreams[i].childStream != childStreams[i]) {
                        r0.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                    }
                    sampleStreamArr[i] = r0.sampleStreams[i];
                    i++;
                }
                return enablePositionUs;
            }
            z = true;
            Assertions.checkState(z);
            while (i < sampleStreamArr.length) {
                if (childStreams[i] != null) {
                    if (sampleStreamArr[i] != null) {
                    }
                    r0.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                } else {
                    r0.sampleStreams[i] = null;
                }
                sampleStreamArr[i] = r0.sampleStreams[i];
                i++;
            }
            return enablePositionUs;
        }
        j = C0542C.TIME_UNSET;
        r0.pendingInitialDiscontinuityPositionUs = j;
        if (enablePositionUs != positionUs) {
            if (enablePositionUs >= 0) {
                if (r0.endUs != Long.MIN_VALUE) {
                    if (r0.startUs + enablePositionUs <= r0.endUs) {
                    }
                }
            }
            z = false;
            Assertions.checkState(z);
            while (i < sampleStreamArr.length) {
                if (childStreams[i] != null) {
                    r0.sampleStreams[i] = null;
                } else {
                    if (sampleStreamArr[i] != null) {
                    }
                    r0.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                }
                sampleStreamArr[i] = r0.sampleStreams[i];
                i++;
            }
            return enablePositionUs;
        }
        z = true;
        Assertions.checkState(z);
        while (i < sampleStreamArr.length) {
            if (childStreams[i] != null) {
                if (sampleStreamArr[i] != null) {
                }
                r0.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
            } else {
                r0.sampleStreams[i] = null;
            }
            sampleStreamArr[i] = r0.sampleStreams[i];
            i++;
        }
        return enablePositionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        this.mediaPeriod.discardBuffer(positionUs + this.startUs, toKeyframe);
    }

    public void reevaluateBuffer(long positionUs) {
        this.mediaPeriod.reevaluateBuffer(positionUs + this.startUs);
    }

    public long readDiscontinuity() {
        if (isPendingInitialDiscontinuity()) {
            long initialDiscontinuityUs = this.pendingInitialDiscontinuityPositionUs;
            this.pendingInitialDiscontinuityPositionUs = C0542C.TIME_UNSET;
            long childDiscontinuityUs = readDiscontinuity();
            return childDiscontinuityUs != C0542C.TIME_UNSET ? childDiscontinuityUs : initialDiscontinuityUs;
        }
        initialDiscontinuityUs = this.mediaPeriod.readDiscontinuity();
        if (initialDiscontinuityUs == C0542C.TIME_UNSET) {
            return C0542C.TIME_UNSET;
        }
        boolean z = false;
        Assertions.checkState(initialDiscontinuityUs >= this.startUs);
        if (this.endUs != Long.MIN_VALUE) {
            if (initialDiscontinuityUs > this.endUs) {
                Assertions.checkState(z);
                return initialDiscontinuityUs - this.startUs;
            }
        }
        z = true;
        Assertions.checkState(z);
        return initialDiscontinuityUs - this.startUs;
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

    public long seekToUs(long positionUs) {
        this.pendingInitialDiscontinuityPositionUs = C0542C.TIME_UNSET;
        boolean z = false;
        for (ClippingSampleStream sampleStream : this.sampleStreams) {
            if (sampleStream != null) {
                sampleStream.clearSentEos();
            }
        }
        long offsetPositionUs = positionUs + this.startUs;
        long seekUs = this.mediaPeriod.seekToUs(offsetPositionUs);
        if (seekUs != offsetPositionUs) {
            if (seekUs >= this.startUs) {
                if (this.endUs != Long.MIN_VALUE) {
                    if (seekUs <= this.endUs) {
                    }
                }
            }
            Assertions.checkState(z);
            return seekUs - this.startUs;
        }
        z = true;
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
        if (nextLoadPositionUs != Long.MIN_VALUE) {
            if (this.endUs == Long.MIN_VALUE || nextLoadPositionUs < this.endUs) {
                return nextLoadPositionUs - this.startUs;
            }
        }
        return Long.MIN_VALUE;
    }

    public boolean continueLoading(long positionUs) {
        return this.mediaPeriod.continueLoading(positionUs + this.startUs);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        boolean z = (this.startUs == C0542C.TIME_UNSET || this.endUs == C0542C.TIME_UNSET) ? false : true;
        Assertions.checkState(z);
        this.callback.onPrepared(this);
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.callback.onContinueLoadingRequested(this);
    }

    boolean isPendingInitialDiscontinuity() {
        return this.pendingInitialDiscontinuityPositionUs != C0542C.TIME_UNSET;
    }

    private SeekParameters clipSeekParameters(long offsetPositionUs, SeekParameters seekParameters) {
        long toleranceAfterMs;
        long toleranceBeforeMs = Math.min(offsetPositionUs - this.startUs, seekParameters.toleranceBeforeUs);
        if (this.endUs == Long.MIN_VALUE) {
            toleranceAfterMs = seekParameters.toleranceAfterUs;
        } else {
            toleranceAfterMs = Math.min(this.endUs - offsetPositionUs, seekParameters.toleranceAfterUs);
        }
        if (toleranceBeforeMs == seekParameters.toleranceBeforeUs && toleranceAfterMs == seekParameters.toleranceAfterUs) {
            return seekParameters;
        }
        return new SeekParameters(toleranceBeforeMs, toleranceAfterMs);
    }

    private static boolean shouldKeepInitialDiscontinuity(long startUs, TrackSelection[] selections) {
        if (startUs != 0) {
            for (TrackSelection trackSelection : selections) {
                if (trackSelection != null && !MimeTypes.isAudio(trackSelection.getSelectedFormat().sampleMimeType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
