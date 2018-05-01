package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0542C;
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

    public boolean isSeekable() {
        return true;
    }

    public static VbriSeeker create(long j, long j2, MpegAudioHeader mpegAudioHeader, ParsableByteArray parsableByteArray) {
        long j3 = j;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        parsableByteArray2.skipBytes(10);
        int readInt = parsableByteArray.readInt();
        if (readInt <= 0) {
            return null;
        }
        long j4;
        int i = mpegAudioHeader2.sampleRate;
        long scaleLargeTimestamp = Util.scaleLargeTimestamp((long) readInt, C0542C.MICROS_PER_SECOND * ((long) (i >= 32000 ? 1152 : 576)), (long) i);
        readInt = parsableByteArray.readUnsignedShort();
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
        parsableByteArray2.skipBytes(2);
        long j5 = j2 + ((long) mpegAudioHeader2.frameSize);
        long[] jArr = new long[readInt];
        long[] jArr2 = new long[readInt];
        int i2 = 0;
        long j6 = j2;
        while (i2 < readInt) {
            int readUnsignedByte;
            j4 = scaleLargeTimestamp;
            jArr[i2] = (((long) i2) * scaleLargeTimestamp) / ((long) readInt);
            jArr2[i2] = Math.max(j6, j5);
            switch (readUnsignedShort2) {
                case 1:
                    readUnsignedByte = parsableByteArray.readUnsignedByte();
                    break;
                case 2:
                    readUnsignedByte = parsableByteArray.readUnsignedShort();
                    break;
                case 3:
                    readUnsignedByte = parsableByteArray.readUnsignedInt24();
                    break;
                case 4:
                    readUnsignedByte = parsableByteArray.readUnsignedIntToInt();
                    break;
                default:
                    return null;
            }
            i2++;
            j6 += (long) (readUnsignedByte * readUnsignedShort);
            scaleLargeTimestamp = j4;
            j3 = j;
        }
        j4 = scaleLargeTimestamp;
        long j7 = j;
        if (!(j7 == -1 || j7 == j6)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VBRI data size mismatch: ");
            stringBuilder.append(j7);
            stringBuilder.append(", ");
            stringBuilder.append(j6);
            Log.w(str, stringBuilder.toString());
        }
        return new VbriSeeker(jArr, jArr2, j4);
    }

    private VbriSeeker(long[] jArr, long[] jArr2, long j) {
        this.timesUs = jArr;
        this.positions = jArr2;
        this.durationUs = j;
    }

    public SeekPoints getSeekPoints(long j) {
        int binarySearchFloor = Util.binarySearchFloor(this.timesUs, j, true, true);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[binarySearchFloor], this.positions[binarySearchFloor]);
        if (seekPoint.timeUs < j) {
            if (binarySearchFloor != this.timesUs.length - 1) {
                binarySearchFloor++;
                return new SeekPoints(seekPoint, new SeekPoint(this.timesUs[binarySearchFloor], this.positions[binarySearchFloor]));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getTimeUs(long j) {
        return this.timesUs[Util.binarySearchFloor(this.positions, j, true, true)];
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
