package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class H262Reader implements ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES = new double[]{23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private final CsdBuffer csdBuffer = new CsdBuffer(128);
    private String formatId;
    private long frameDurationUs;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final boolean[] prefixFlags = new boolean[4];
    private boolean sampleHasPicture;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean startedFirstSample;
    private long totalBytesWritten;

    private static final class CsdBuffer {
        private static final byte[] START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 1};
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int initialCapacity) {
            this.data = new byte[initialCapacity];
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }

        public boolean onStartCode(int startCodeValue, int bytesAlreadyPassed) {
            if (this.isFilling) {
                this.length -= bytesAlreadyPassed;
                if (this.sequenceExtensionPosition == 0 && startCodeValue == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = this.length;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (startCodeValue == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            onData(START_CODE, 0, START_CODE.length);
            return false;
        }

        public void onData(byte[] newData, int offset, int limit) {
            if (this.isFilling) {
                int readLength = limit - offset;
                if (this.data.length < this.length + readLength) {
                    this.data = Arrays.copyOf(this.data, (this.length + readLength) * 2);
                }
                System.arraycopy(newData, offset, this.data, this.length, readLength);
                this.length += readLength;
            }
        }
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        this.totalBytesWritten = 0;
        this.startedFirstSample = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.pesTimeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        ParsableByteArray parsableByteArray = data;
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = parsableByteArray.data;
        this.totalBytesWritten += (long) data.bytesLeft();
        this.output.sampleData(parsableByteArray, data.bytesLeft());
        while (true) {
            int startCodeOffset = NalUnitUtil.findNalUnit(dataArray, offset, limit, r0.prefixFlags);
            if (startCodeOffset == limit) {
                break;
            }
            int lengthToStartCode;
            int startCodeValue = parsableByteArray.data[startCodeOffset + 3] & 255;
            boolean z = false;
            if (!r0.hasOutputFormat) {
                lengthToStartCode = startCodeOffset - offset;
                if (lengthToStartCode > 0) {
                    r0.csdBuffer.onData(dataArray, offset, startCodeOffset);
                }
                if (r0.csdBuffer.onStartCode(startCodeValue, lengthToStartCode < 0 ? -lengthToStartCode : 0)) {
                    Pair<Format, Long> result = parseCsdBuffer(r0.csdBuffer, r0.formatId);
                    r0.output.format((Format) result.first);
                    r0.frameDurationUs = ((Long) result.second).longValue();
                    r0.hasOutputFormat = true;
                }
            }
            if (startCodeValue != 0) {
                if (startCodeValue != START_SEQUENCE_HEADER) {
                    if (startCodeValue == START_GROUP) {
                        r0.sampleIsKeyframe = true;
                    }
                    offset = startCodeOffset + 3;
                    parsableByteArray = data;
                }
            }
            lengthToStartCode = limit - startCodeOffset;
            if (r0.startedFirstSample && r0.sampleHasPicture && r0.hasOutputFormat) {
                int i = offset;
                r0.output.sampleMetadata(r0.sampleTimeUs, r0.sampleIsKeyframe, ((int) (r0.totalBytesWritten - r0.samplePosition)) - lengthToStartCode, lengthToStartCode, null);
            }
            if (!r0.startedFirstSample || r0.sampleHasPicture) {
                r0.samplePosition = r0.totalBytesWritten - ((long) lengthToStartCode);
                long j = r0.pesTimeUs != C0542C.TIME_UNSET ? r0.pesTimeUs : r0.startedFirstSample ? r0.sampleTimeUs + r0.frameDurationUs : 0;
                r0.sampleTimeUs = j;
                r0.sampleIsKeyframe = false;
                r0.pesTimeUs = C0542C.TIME_UNSET;
                r0.startedFirstSample = true;
            }
            if (startCodeValue == 0) {
                z = true;
            }
            r0.sampleHasPicture = z;
            offset = startCodeOffset + 3;
            parsableByteArray = data;
        }
        if (!r0.hasOutputFormat) {
            r0.csdBuffer.onData(dataArray, offset, limit);
        }
    }

    public void packetFinished() {
    }

    private static Pair<Format, Long> parseCsdBuffer(CsdBuffer csdBuffer, String formatId) {
        float pixelWidthHeightRatio;
        CsdBuffer csdBuffer2 = csdBuffer;
        byte[] csdData = Arrays.copyOf(csdBuffer2.data, csdBuffer2.length);
        int firstByte = csdData[4] & 255;
        int secondByte = csdData[5] & 255;
        int width = (firstByte << 4) | (secondByte >> 4);
        int height = ((secondByte & 15) << 8) | (csdData[6] & 255);
        switch ((csdData[7] & PsExtractor.VIDEO_STREAM_MASK) >> 4) {
            case 2:
                pixelWidthHeightRatio = ((float) (4 * height)) / ((float) (3 * width));
                break;
            case 3:
                pixelWidthHeightRatio = ((float) (16 * height)) / ((float) (9 * width));
                break;
            case 4:
                pixelWidthHeightRatio = ((float) (121 * height)) / ((float) (100 * width));
                break;
            default:
                pixelWidthHeightRatio = 1.0f;
                break;
        }
        Format format = Format.createVideoSampleFormat(formatId, MimeTypes.VIDEO_MPEG2, null, -1, -1, width, height, -1.0f, Collections.singletonList(csdData), -1, pixelWidthHeightRatio, null);
        long frameDurationUs = 0;
        int frameRateCodeMinusOne = (csdData[7] & 15) - 1;
        float f;
        int i;
        if (frameRateCodeMinusOne < 0 || frameRateCodeMinusOne >= FRAME_RATE_VALUES.length) {
            f = pixelWidthHeightRatio;
            i = firstByte;
        } else {
            double frameRate = FRAME_RATE_VALUES[frameRateCodeMinusOne];
            int sequenceExtensionPosition = csdBuffer2.sequenceExtensionPosition;
            int frameRateExtensionN = (csdData[sequenceExtensionPosition + 9] & 96) >> 5;
            int frameRateExtensionD = csdData[sequenceExtensionPosition + 9] & 31;
            if (frameRateExtensionN != frameRateExtensionD) {
                frameRate *= (((double) frameRateExtensionN) + 1.0d) / ((double) (frameRateExtensionD + 1));
            } else {
                f = pixelWidthHeightRatio;
                i = firstByte;
                int i2 = frameRateExtensionN;
            }
            frameDurationUs = (long) (1000000.0d / frameRate);
        }
        return Pair.create(format, Long.valueOf(frameDurationUs));
    }
}
