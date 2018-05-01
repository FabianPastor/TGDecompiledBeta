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

        public CsdBuffer(int i) {
            this.data = new byte[i];
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }

        public boolean onStartCode(int i, int i2) {
            if (this.isFilling) {
                this.length -= i2;
                if (this.sequenceExtensionPosition == 0 && i == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = this.length;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (i == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            onData(START_CODE, 0, START_CODE.length);
            return false;
        }

        public void onData(byte[] bArr, int i, int i2) {
            if (this.isFilling) {
                i2 -= i;
                if (this.data.length < this.length + i2) {
                    this.data = Arrays.copyOf(this.data, (this.length + i2) * 2);
                }
                System.arraycopy(bArr, i, this.data, this.length, i2);
                this.length += i2;
            }
        }
    }

    public void packetFinished() {
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        this.totalBytesWritten = 0;
        this.startedFirstSample = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 2);
    }

    public void packetStarted(long j, boolean z) {
        this.pesTimeUs = j;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        byte[] bArr = parsableByteArray2.data;
        this.totalBytesWritten += (long) parsableByteArray.bytesLeft();
        this.output.sampleData(parsableByteArray2, parsableByteArray.bytesLeft());
        while (true) {
            int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, r0.prefixFlags);
            if (findNalUnit == limit) {
                break;
            }
            int i;
            byte[] bArr2;
            boolean z;
            boolean z2;
            int i2 = findNalUnit + 3;
            int i3 = parsableByteArray2.data[i2] & 255;
            if (!r0.hasOutputFormat) {
                int i4 = findNalUnit - position;
                if (i4 > 0) {
                    r0.csdBuffer.onData(bArr, position, findNalUnit);
                }
                if (r0.csdBuffer.onStartCode(i3, i4 < 0 ? -i4 : 0)) {
                    Pair parseCsdBuffer = parseCsdBuffer(r0.csdBuffer, r0.formatId);
                    r0.output.format((Format) parseCsdBuffer.first);
                    r0.frameDurationUs = ((Long) parseCsdBuffer.second).longValue();
                    r0.hasOutputFormat = true;
                }
            }
            if (i3 != 0) {
                if (i3 != START_SEQUENCE_HEADER) {
                    if (i3 == START_GROUP) {
                        r0.sampleIsKeyframe = true;
                    }
                    i = limit;
                    bArr2 = bArr;
                    position = i2;
                    limit = i;
                    bArr = bArr2;
                }
            }
            position = limit - findNalUnit;
            if (r0.startedFirstSample && r0.sampleHasPicture && r0.hasOutputFormat) {
                i = limit;
                bArr2 = bArr;
                r0.output.sampleMetadata(r0.sampleTimeUs, r0.sampleIsKeyframe, ((int) (r0.totalBytesWritten - r0.samplePosition)) - position, position, null);
            } else {
                i = limit;
                bArr2 = bArr;
            }
            if (r0.startedFirstSample) {
                if (!r0.sampleHasPicture) {
                    z = false;
                    z2 = true;
                    if (i3 == 0) {
                        z = z2;
                    }
                    r0.sampleHasPicture = z;
                    position = i2;
                    limit = i;
                    bArr = bArr2;
                }
            }
            r0.samplePosition = r0.totalBytesWritten - ((long) position);
            long j = r0.pesTimeUs != C0542C.TIME_UNSET ? r0.pesTimeUs : r0.startedFirstSample ? r0.sampleTimeUs + r0.frameDurationUs : 0;
            r0.sampleTimeUs = j;
            z = false;
            r0.sampleIsKeyframe = false;
            r0.pesTimeUs = C0542C.TIME_UNSET;
            z2 = true;
            r0.startedFirstSample = true;
            if (i3 == 0) {
                z = z2;
            }
            r0.sampleHasPicture = z;
            position = i2;
            limit = i;
            bArr = bArr2;
        }
        if (!r0.hasOutputFormat) {
            r0.csdBuffer.onData(bArr, position, limit);
        }
    }

    private static Pair<Format, Long> parseCsdBuffer(CsdBuffer csdBuffer, String str) {
        float f;
        CsdBuffer csdBuffer2 = csdBuffer;
        Object copyOf = Arrays.copyOf(csdBuffer2.data, csdBuffer2.length);
        int i = copyOf[5] & 255;
        int i2 = ((copyOf[4] & 255) << 4) | (i >> 4);
        int i3 = ((i & 15) << 8) | (copyOf[6] & 255);
        switch ((copyOf[7] & PsExtractor.VIDEO_STREAM_MASK) >> 4) {
            case 2:
                f = ((float) (4 * i3)) / ((float) (3 * i2));
                break;
            case 3:
                f = ((float) (16 * i3)) / ((float) (9 * i2));
                break;
            case 4:
                f = ((float) (121 * i3)) / ((float) (100 * i2));
                break;
            default:
                f = 1.0f;
                break;
        }
        Format createVideoSampleFormat = Format.createVideoSampleFormat(str, MimeTypes.VIDEO_MPEG2, null, -1, -1, i2, i3, -1.0f, Collections.singletonList(copyOf), -1, f, null);
        long j = 0;
        int i4 = (copyOf[7] & 15) - 1;
        if (i4 >= 0 && i4 < FRAME_RATE_VALUES.length) {
            double d = FRAME_RATE_VALUES[i4];
            int i5 = csdBuffer2.sequenceExtensionPosition + 9;
            i4 = (copyOf[i5] & 96) >> 5;
            i5 = copyOf[i5] & 31;
            if (i4 != i5) {
                d *= (((double) i4) + 1.0d) / ((double) (i5 + 1));
            }
            j = (long) (1000000.0d / d);
        }
        return Pair.create(createVideoSampleFormat, Long.valueOf(j));
    }
}
