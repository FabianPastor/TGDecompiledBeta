package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class VbriSeeker implements Seeker {
    private static final String TAG = "VbriSeeker";
    private final long durationUs;
    private final long[] positions;
    private final long[] timesUs;

    public static VbriSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        frame.skipBytes(10);
        int numFrames = frame.readInt();
        if (numFrames <= 0) {
            return null;
        }
        int sampleRate = mpegAudioHeader.sampleRate;
        long durationUs = Util.scaleLargeTimestamp((long) numFrames, ((long) (sampleRate >= 32000 ? 1152 : 576)) * C.MICROS_PER_SECOND, (long) sampleRate);
        int entryCount = frame.readUnsignedShort();
        int scale = frame.readUnsignedShort();
        int entrySize = frame.readUnsignedShort();
        frame.skipBytes(2);
        long minPosition = position + ((long) mpegAudioHeader.frameSize);
        long[] timesUs = new long[entryCount];
        long[] positions = new long[entryCount];
        for (int index = 0; index < entryCount; index++) {
            int segmentSize;
            timesUs[index] = (((long) index) * durationUs) / ((long) entryCount);
            positions[index] = Math.max(position, minPosition);
            switch (entrySize) {
                case 1:
                    segmentSize = frame.readUnsignedByte();
                    break;
                case 2:
                    segmentSize = frame.readUnsignedShort();
                    break;
                case 3:
                    segmentSize = frame.readUnsignedInt24();
                    break;
                case 4:
                    segmentSize = frame.readUnsignedIntToInt();
                    break;
                default:
                    return null;
            }
            position += (long) (segmentSize * scale);
        }
        if (!(inputLength == -1 || inputLength == position)) {
            Log.w(TAG, "VBRI data size mismatch: " + inputLength + ", " + position);
        }
        return new VbriSeeker(timesUs, positions, durationUs);
    }

    private VbriSeeker(long[] timesUs, long[] positions, long durationUs) {
        this.timesUs = timesUs;
        this.positions = positions;
        this.durationUs = durationUs;
    }

    public boolean isSeekable() {
        return true;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        int tableIndex = Util.binarySearchFloor(this.timesUs, timeUs, true, true);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[tableIndex], this.positions[tableIndex]);
        if (seekPoint.timeUs >= timeUs || tableIndex == this.timesUs.length - 1) {
            return new SeekPoints(seekPoint);
        }
        return new SeekPoints(seekPoint, new SeekPoint(this.timesUs[tableIndex + 1], this.positions[tableIndex + 1]));
    }

    public long getTimeUs(long position) {
        return this.timesUs[Util.binarySearchFloor(this.positions, position, true, true)];
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
