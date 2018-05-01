package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class XingSeeker implements Seeker {
    private static final String TAG = "XingSeeker";
    private final long dataSize;
    private final long dataStartPosition;
    private final long durationUs;
    private final long[] tableOfContents;
    private final int xingFrameSize;

    public static XingSeeker create(long j, long j2, MpegAudioHeader mpegAudioHeader, ParsableByteArray parsableByteArray) {
        long j3 = j;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        int i = mpegAudioHeader2.samplesPerFrame;
        int i2 = mpegAudioHeader2.sampleRate;
        int readInt = parsableByteArray.readInt();
        if ((readInt & 1) == 1) {
            int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
            if (readUnsignedIntToInt != 0) {
                long scaleLargeTimestamp = Util.scaleLargeTimestamp((long) readUnsignedIntToInt, ((long) i) * C0542C.MICROS_PER_SECOND, (long) i2);
                if ((readInt & 6) != 6) {
                    return new XingSeeker(j2, mpegAudioHeader2.frameSize, scaleLargeTimestamp);
                }
                long readUnsignedIntToInt2 = (long) parsableByteArray.readUnsignedIntToInt();
                long[] jArr = new long[100];
                for (int i3 = 0; i3 < 100; i3++) {
                    jArr[i3] = (long) parsableByteArray.readUnsignedByte();
                }
                if (j3 != -1) {
                    long j4 = j2 + readUnsignedIntToInt2;
                    if (j3 != j4) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("XING data size mismatch: ");
                        stringBuilder.append(j3);
                        stringBuilder.append(", ");
                        stringBuilder.append(j4);
                        Log.w(str, stringBuilder.toString());
                    }
                }
                return new XingSeeker(j2, mpegAudioHeader2.frameSize, scaleLargeTimestamp, readUnsignedIntToInt2, jArr);
            }
        }
        return null;
    }

    private XingSeeker(long j, int i, long j2) {
        this(j, i, j2, -1, null);
    }

    private XingSeeker(long j, int i, long j2, long j3, long[] jArr) {
        this.dataStartPosition = j;
        this.xingFrameSize = i;
        this.durationUs = j2;
        this.dataSize = j3;
        this.tableOfContents = jArr;
    }

    public boolean isSeekable() {
        return this.tableOfContents != null;
    }

    public SeekPoints getSeekPoints(long j) {
        XingSeeker xingSeeker = this;
        if (!isSeekable()) {
            return new SeekPoints(new SeekPoint(0, xingSeeker.dataStartPosition + ((long) xingSeeker.xingFrameSize)));
        }
        long constrainValue = Util.constrainValue(j, 0, xingSeeker.durationUs);
        double d = (((double) constrainValue) * 100.0d) / ((double) xingSeeker.durationUs);
        double d2 = 0.0d;
        if (d > 0.0d) {
            if (d >= 100.0d) {
                d2 = 256.0d;
            } else {
                double d3;
                int i = (int) d;
                double d4 = (double) xingSeeker.tableOfContents[i];
                if (i == 99) {
                    d3 = 256.0d;
                } else {
                    d3 = (double) xingSeeker.tableOfContents[i + 1];
                }
                d2 = d4 + ((d - ((double) i)) * (d3 - d4));
            }
        }
        return new SeekPoints(new SeekPoint(constrainValue, xingSeeker.dataStartPosition + Util.constrainValue(Math.round((d2 / 256.0d) * ((double) xingSeeker.dataSize)), (long) xingSeeker.xingFrameSize, xingSeeker.dataSize - 1)));
    }

    public long getTimeUs(long j) {
        long j2 = j - this.dataStartPosition;
        if (isSeekable() != null) {
            if (j2 > ((long) this.xingFrameSize)) {
                long j3;
                j = (((double) j2) * 4643211215818981376L) / ((double) this.dataSize);
                int binarySearchFloor = Util.binarySearchFloor(this.tableOfContents, (long) j, true, true);
                long timeUsForTableIndex = getTimeUsForTableIndex(binarySearchFloor);
                long j4 = this.tableOfContents[binarySearchFloor];
                int i = binarySearchFloor + 1;
                long timeUsForTableIndex2 = getTimeUsForTableIndex(i);
                if (binarySearchFloor == 99) {
                    j3 = 256;
                } else {
                    j3 = this.tableOfContents[i];
                }
                return timeUsForTableIndex + Math.round((j4 == j3 ? 0 : (j - ((double) j4)) / ((double) (j3 - j4))) * ((double) (timeUsForTableIndex2 - timeUsForTableIndex)));
            }
        }
        return 0;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    private long getTimeUsForTableIndex(int i) {
        return (this.durationUs * ((long) i)) / 100;
    }
}
