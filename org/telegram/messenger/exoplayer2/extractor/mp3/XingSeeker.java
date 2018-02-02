package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C;
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
        int samplesPerFrame = mpegAudioHeader.samplesPerFrame;
        int sampleRate = mpegAudioHeader.sampleRate;
        int flags = frame.readInt();
        if ((flags & 1) == 1) {
            int frameCount = frame.readUnsignedIntToInt();
            if (frameCount != 0) {
                long durationUs = Util.scaleLargeTimestamp((long) frameCount, ((long) samplesPerFrame) * C.MICROS_PER_SECOND, (long) sampleRate);
                if ((flags & 6) != 6) {
                    return new XingSeeker(position, mpegAudioHeader.frameSize, durationUs);
                }
                long dataSize = (long) frame.readUnsignedIntToInt();
                long[] tableOfContents = new long[100];
                for (int i = 0; i < 100; i++) {
                    tableOfContents[i] = (long) frame.readUnsignedByte();
                }
                if (!(inputLength == -1 || inputLength == position + dataSize)) {
                    Log.w(TAG, "XING data size mismatch: " + inputLength + ", " + (position + dataSize));
                }
                return new XingSeeker(position, mpegAudioHeader.frameSize, durationUs, dataSize, tableOfContents);
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
        if (!isSeekable()) {
            return new SeekPoints(new SeekPoint(0, this.dataStartPosition + ((long) this.xingFrameSize)));
        }
        double scaledPosition;
        timeUs = Util.constrainValue(timeUs, 0, this.durationUs);
        double percent = (((double) timeUs) * 100.0d) / ((double) this.durationUs);
        if (percent <= 0.0d) {
            scaledPosition = 0.0d;
        } else if (percent >= 100.0d) {
            scaledPosition = 256.0d;
        } else {
            int prevTableIndex = (int) percent;
            double prevScaledPosition = (double) this.tableOfContents[prevTableIndex];
            scaledPosition = prevScaledPosition + (((prevTableIndex == 99 ? 256.0d : (double) this.tableOfContents[prevTableIndex + 1]) - prevScaledPosition) * (percent - ((double) prevTableIndex)));
        }
        return new SeekPoints(new SeekPoint(timeUs, this.dataStartPosition + Util.constrainValue(Math.round((scaledPosition / 256.0d) * ((double) this.dataSize)), (long) this.xingFrameSize, this.dataSize - 1)));
    }

    public long getTimeUs(long position) {
        long positionOffset = position - this.dataStartPosition;
        if (!isSeekable() || positionOffset <= ((long) this.xingFrameSize)) {
            return 0;
        }
        double scaledPosition = (((double) positionOffset) * 256.0d) / ((double) this.dataSize);
        int prevTableIndex = Util.binarySearchFloor(this.tableOfContents, (long) scaledPosition, true, true);
        long prevTimeUs = getTimeUsForTableIndex(prevTableIndex);
        long prevScaledPosition = this.tableOfContents[prevTableIndex];
        long nextTimeUs = getTimeUsForTableIndex(prevTableIndex + 1);
        long nextScaledPosition = prevTableIndex == 99 ? 256 : this.tableOfContents[prevTableIndex + 1];
        return Math.round(((double) (nextTimeUs - prevTimeUs)) * (prevScaledPosition == nextScaledPosition ? 0.0d : (scaledPosition - ((double) prevScaledPosition)) / ((double) (nextScaledPosition - prevScaledPosition)))) + prevTimeUs;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    private long getTimeUsForTableIndex(int tableIndex) {
        return (this.durationUs * ((long) tableIndex)) / 100;
    }
}
