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

    public static XingSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        int samplesPerFrame = mpegAudioHeader2.samplesPerFrame;
        int sampleRate = mpegAudioHeader2.sampleRate;
        int flags = frame.readInt();
        if ((flags & 1) == 1) {
            int readUnsignedIntToInt = frame.readUnsignedIntToInt();
            int frameCount = readUnsignedIntToInt;
            if (readUnsignedIntToInt != 0) {
                long durationUs = Util.scaleLargeTimestamp((long) frameCount, ((long) samplesPerFrame) * C0542C.MICROS_PER_SECOND, (long) sampleRate);
                if ((flags & 6) != 6) {
                    return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs);
                }
                long readUnsignedIntToInt2 = (long) frame.readUnsignedIntToInt();
                long[] tableOfContents = new long[100];
                for (int i = 0; i < 100; i++) {
                    tableOfContents[i] = (long) frame.readUnsignedByte();
                }
                if (!(j == -1 || j == position + readUnsignedIntToInt2)) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("XING data size mismatch: ");
                    stringBuilder.append(j);
                    stringBuilder.append(", ");
                    stringBuilder.append(position + readUnsignedIntToInt2);
                    Log.w(str, stringBuilder.toString());
                }
                long dataSize = readUnsignedIntToInt2;
                return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs, readUnsignedIntToInt2, tableOfContents);
            }
        }
        return null;
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs) {
        this(dataStartPosition, xingFrameSize, durationUs, -1, null);
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs, long dataSize, long[] tableOfContents) {
        this.dataStartPosition = dataStartPosition;
        this.xingFrameSize = xingFrameSize;
        this.durationUs = durationUs;
        this.dataSize = dataSize;
        this.tableOfContents = tableOfContents;
    }

    public boolean isSeekable() {
        return this.tableOfContents != null;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        XingSeeker xingSeeker = this;
        if (!isSeekable()) {
            return new SeekPoints(new SeekPoint(0, xingSeeker.dataStartPosition + ((long) xingSeeker.xingFrameSize)));
        }
        double scaledPosition;
        long timeUs2 = Util.constrainValue(timeUs, 0, xingSeeker.durationUs);
        double percent = (((double) timeUs2) * 100.0d) / ((double) xingSeeker.durationUs);
        if (percent <= 0.0d) {
            scaledPosition = 0.0d;
        } else if (percent >= 100.0d) {
            scaledPosition = 256.0d;
        } else {
            int prevTableIndex = (int) percent;
            double prevScaledPosition = (double) xingSeeker.tableOfContents[prevTableIndex];
            scaledPosition = prevScaledPosition + (((prevTableIndex == 99 ? 256.0d : (double) xingSeeker.tableOfContents[prevTableIndex + 1]) - prevScaledPosition) * (percent - ((double) prevTableIndex)));
            return new SeekPoints(new SeekPoint(timeUs2, xingSeeker.dataStartPosition + Util.constrainValue(Math.round((scaledPosition / 256.0d) * ((double) xingSeeker.dataSize)), (long) xingSeeker.xingFrameSize, xingSeeker.dataSize - 1)));
        }
        return new SeekPoints(new SeekPoint(timeUs2, xingSeeker.dataStartPosition + Util.constrainValue(Math.round((scaledPosition / 256.0d) * ((double) xingSeeker.dataSize)), (long) xingSeeker.xingFrameSize, xingSeeker.dataSize - 1)));
    }

    public long getTimeUs(long position) {
        long positionOffset = position - this.dataStartPosition;
        if (isSeekable()) {
            if (positionOffset > ((long) r0.xingFrameSize)) {
                double d;
                double scaledPosition = (((double) positionOffset) * 256.0d) / ((double) r0.dataSize);
                int prevTableIndex = Util.binarySearchFloor(r0.tableOfContents, (long) scaledPosition, true, true);
                long prevTimeUs = getTimeUsForTableIndex(prevTableIndex);
                long prevScaledPosition = r0.tableOfContents[prevTableIndex];
                long nextTimeUs = getTimeUsForTableIndex(prevTableIndex + 1);
                long nextScaledPosition = prevTableIndex == 99 ? 256 : r0.tableOfContents[prevTableIndex + 1];
                if (prevScaledPosition == nextScaledPosition) {
                    d = 0.0d;
                    double d2 = scaledPosition;
                } else {
                    d = (scaledPosition - ((double) prevScaledPosition)) / ((double) (nextScaledPosition - prevScaledPosition));
                }
                return prevTimeUs + Math.round(((double) (nextTimeUs - prevTimeUs)) * d);
            }
        }
        return 0;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    private long getTimeUsForTableIndex(int tableIndex) {
        return (this.durationUs * ((long) tableIndex)) / 100;
    }
}
