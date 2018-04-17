package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0539C;
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
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        ParsableByteArray parsableByteArray = frame;
        parsableByteArray.skipBytes(10);
        int numFrames = frame.readInt();
        if (numFrames <= 0) {
            return null;
        }
        int sampleRate = mpegAudioHeader2.sampleRate;
        long durationUs = Util.scaleLargeTimestamp((long) numFrames, C0539C.MICROS_PER_SECOND * ((long) (sampleRate >= 32000 ? 1152 : 576)), (long) sampleRate);
        int entryCount = frame.readUnsignedShort();
        int scale = frame.readUnsignedShort();
        int entrySize = frame.readUnsignedShort();
        parsableByteArray.skipBytes(2);
        long minPosition = position + ((long) mpegAudioHeader2.frameSize);
        long[] timesUs = new long[entryCount];
        long[] positions = new long[entryCount];
        int index = 0;
        long position2 = position;
        while (true) {
            int index2 = index;
            long durationUs2;
            if (index2 < entryCount) {
                int segmentSize;
                durationUs2 = durationUs;
                timesUs[index2] = (((long) index2) * durationUs) / ((long) entryCount);
                positions[index2] = Math.max(position2, minPosition);
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
                index = index2 + 1;
                position2 += (long) (segmentSize * scale);
                durationUs = durationUs2;
                j = inputLength;
                mpegAudioHeader2 = mpegAudioHeader;
            } else {
                durationUs2 = durationUs;
                durationUs = inputLength;
                if (!(durationUs == -1 || durationUs == position2)) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("VBRI data size mismatch: ");
                    stringBuilder.append(durationUs);
                    stringBuilder.append(", ");
                    stringBuilder.append(position2);
                    Log.w(str, stringBuilder.toString());
                }
                return new VbriSeeker(timesUs, positions, durationUs2);
            }
        }
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
        if (seekPoint.timeUs < timeUs) {
            if (tableIndex != this.timesUs.length - 1) {
                return new SeekPoints(seekPoint, new SeekPoint(this.timesUs[tableIndex + 1], this.positions[tableIndex + 1]));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getTimeUs(long position) {
        return this.timesUs[Util.binarySearchFloor(this.positions, position, true, true)];
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
