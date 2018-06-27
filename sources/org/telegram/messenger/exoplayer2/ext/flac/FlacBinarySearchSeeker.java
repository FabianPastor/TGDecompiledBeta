package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.ext.flac.FlacDecoderJni.FlacFrameDecodeException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;
import org.telegram.messenger.exoplayer2.util.Util;

final class FlacBinarySearchSeeker {
    private static final long MAX_SKIP_BYTES = 262144;
    private final long approxBytesPerFrame;
    private final FlacDecoderJni decoderJni;
    private final long firstFramePosition;
    private final long inputLength;
    private SeekOperationParams pendingSeekOperationParams = null;
    private final FlacBinarySearchSeekMap seekMap;
    private final FlacStreamInfo streamInfo;

    private static final class SeekOperationParams {
        private final long approxBytesPerFrame;
        private long ceilingPosition;
        private long ceilingSample;
        private long floorPosition;
        private long floorSample;
        private long nextSearchPosition;
        private final long seekTimeUs;
        private final long targetSample;

        private SeekOperationParams(long seekTimeUs, long targetSample, long floorSample, long ceilingSample, long floorPosition, long ceilingPosition, long approxBytesPerFrame) {
            this.seekTimeUs = seekTimeUs;
            this.floorSample = floorSample;
            this.ceilingSample = ceilingSample;
            this.floorPosition = floorPosition;
            this.ceilingPosition = ceilingPosition;
            this.targetSample = targetSample;
            this.approxBytesPerFrame = approxBytesPerFrame;
            updateNextSearchPosition();
        }

        private void updateSeekFloor(long floorSample, long floorPosition) {
            this.floorSample = floorSample;
            this.floorPosition = floorPosition;
            updateNextSearchPosition();
        }

        private void updateSeekCeiling(long ceilingSample, long ceilingPosition) {
            this.ceilingSample = ceilingSample;
            this.ceilingPosition = ceilingPosition;
            updateNextSearchPosition();
        }

        private void updateNextSearchPosition() {
            this.nextSearchPosition = getNextSearchPosition(this.targetSample, this.floorSample, this.ceilingSample, this.floorPosition, this.ceilingPosition, this.approxBytesPerFrame);
        }

        private static long getNextSearchPosition(long targetSample, long floorSample, long ceilingSample, long floorPosition, long ceilingPosition, long approxBytesPerFrame) {
            if (1 + floorPosition >= ceilingPosition || 1 + floorSample >= ceilingSample) {
                return floorPosition;
            }
            long bytesToSkip = (targetSample - floorSample) * Math.max(1, (ceilingPosition - floorPosition) / (ceilingSample - floorSample));
            return Util.constrainValue(((floorPosition + bytesToSkip) - (approxBytesPerFrame - 1)) - (bytesToSkip / 20), floorPosition, ceilingPosition - 1);
        }
    }

    private static final class FlacBinarySearchSeekMap implements SeekMap {
        private final long approxBytesPerFrame;
        private final long durationUs;
        private final long firstFramePosition;
        private final long inputLength;
        private final FlacStreamInfo streamInfo;

        private FlacBinarySearchSeekMap(FlacStreamInfo streamInfo, long firstFramePosition, long inputLength, long durationUs, long approxBytesPerFrame) {
            this.streamInfo = streamInfo;
            this.firstFramePosition = firstFramePosition;
            this.inputLength = inputLength;
            this.approxBytesPerFrame = approxBytesPerFrame;
            this.durationUs = durationUs;
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            return new SeekPoints(new SeekPoint(timeUs, SeekOperationParams.getNextSearchPosition(this.streamInfo.getSampleIndex(timeUs), 0, this.streamInfo.totalSamples, this.firstFramePosition, this.inputLength, this.approxBytesPerFrame)));
        }

        public long getDurationUs() {
            return this.durationUs;
        }
    }

    public FlacBinarySearchSeeker(FlacStreamInfo streamInfo, long firstFramePosition, long inputLength, FlacDecoderJni decoderJni) {
        this.streamInfo = (FlacStreamInfo) Assertions.checkNotNull(streamInfo);
        this.decoderJni = (FlacDecoderJni) Assertions.checkNotNull(decoderJni);
        this.firstFramePosition = firstFramePosition;
        this.inputLength = inputLength;
        this.approxBytesPerFrame = streamInfo.getApproxBytesPerFrame();
        this.seekMap = new FlacBinarySearchSeekMap(streamInfo, firstFramePosition, inputLength, streamInfo.durationUs(), this.approxBytesPerFrame);
    }

    public SeekMap getSeekMap() {
        return this.seekMap;
    }

    public void setSeekTargetUs(long timeUs) {
        if (this.pendingSeekOperationParams == null || this.pendingSeekOperationParams.seekTimeUs != timeUs) {
            this.pendingSeekOperationParams = new SeekOperationParams(timeUs, this.streamInfo.getSampleIndex(timeUs), 0, this.streamInfo.totalSamples, this.firstFramePosition, this.inputLength, this.approxBytesPerFrame);
        }
    }

    public boolean hasPendingSeek() {
        return this.pendingSeekOperationParams != null;
    }

    public int handlePendingSeek(ExtractorInput input, PositionHolder seekPositionHolder, ByteBuffer outputBuffer) throws InterruptedException, IOException {
        outputBuffer.position(0);
        outputBuffer.limit(0);
        while (true) {
            long floorPosition = this.pendingSeekOperationParams.floorPosition;
            long ceilingPosition = this.pendingSeekOperationParams.ceilingPosition;
            long searchPosition = this.pendingSeekOperationParams.nextSearchPosition;
            if (((long) Math.max(1, this.streamInfo.minFrameSize)) + floorPosition >= ceilingPosition) {
                this.pendingSeekOperationParams = null;
                this.decoderJni.reset(floorPosition);
                return seekToPosition(input, floorPosition, seekPositionHolder);
            } else if (!skipInputUntilPosition(input, searchPosition)) {
                return seekToPosition(input, searchPosition, seekPositionHolder);
            } else {
                this.decoderJni.reset(searchPosition);
                try {
                    this.decoderJni.decodeSampleWithBacktrackPosition(outputBuffer, searchPosition);
                    if (outputBuffer.limit() == 0) {
                        return -1;
                    }
                    long lastFrameSampleIndex = this.decoderJni.getLastFrameFirstSampleIndex();
                    long nextFrameSampleIndex = this.decoderJni.getNextFrameFirstSampleIndex();
                    long nextFrameSamplePosition = this.decoderJni.getDecodePosition();
                    boolean targetSampleInLastFrame = lastFrameSampleIndex <= this.pendingSeekOperationParams.targetSample && nextFrameSampleIndex > this.pendingSeekOperationParams.targetSample;
                    if (targetSampleInLastFrame) {
                        this.pendingSeekOperationParams = null;
                        return 0;
                    } else if (nextFrameSampleIndex <= this.pendingSeekOperationParams.targetSample) {
                        this.pendingSeekOperationParams.updateSeekFloor(nextFrameSampleIndex, nextFrameSamplePosition);
                    } else {
                        this.pendingSeekOperationParams.updateSeekCeiling(lastFrameSampleIndex, searchPosition);
                    }
                } catch (FlacFrameDecodeException e) {
                    this.pendingSeekOperationParams = null;
                    throw new IOException("Cannot read frame at position " + searchPosition, e);
                }
            }
        }
    }

    private boolean skipInputUntilPosition(ExtractorInput input, long position) throws IOException, InterruptedException {
        long bytesToSkip = position - input.getPosition();
        if (bytesToSkip < 0 || bytesToSkip > MAX_SKIP_BYTES) {
            return false;
        }
        input.skipFully((int) bytesToSkip);
        return true;
    }

    private int seekToPosition(ExtractorInput input, long position, PositionHolder seekPositionHolder) {
        if (position == input.getPosition()) {
            return 0;
        }
        seekPositionHolder.position = position;
        return 1;
    }
}
