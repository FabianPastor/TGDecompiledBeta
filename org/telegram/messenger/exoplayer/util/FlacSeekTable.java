package org.telegram.messenger.exoplayer.util;

import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.extractor.SeekMap;

public final class FlacSeekTable {
    private static final int METADATA_LENGTH_OFFSET = 1;
    private static final int SEEK_POINT_SIZE = 18;
    private final long[] offsets;
    private final long[] sampleNumbers;

    public static FlacSeekTable parseSeekTable(ParsableByteArray data) {
        data.skipBytes(1);
        int numberOfSeekPoints = data.readUnsignedInt24() / 18;
        long[] sampleNumbers = new long[numberOfSeekPoints];
        long[] offsets = new long[numberOfSeekPoints];
        for (int i = 0; i < numberOfSeekPoints; i++) {
            sampleNumbers[i] = data.readLong();
            offsets[i] = data.readLong();
            data.skipBytes(2);
        }
        return new FlacSeekTable(sampleNumbers, offsets);
    }

    private FlacSeekTable(long[] sampleNumbers, long[] offsets) {
        this.sampleNumbers = sampleNumbers;
        this.offsets = offsets;
    }

    public SeekMap createSeekMap(long firstFrameOffset, long sampleRate) {
        final long j = sampleRate;
        final long j2 = firstFrameOffset;
        return new SeekMap() {
            public boolean isSeekable() {
                return true;
            }

            public long getPosition(long timeUs) {
                return j2 + FlacSeekTable.this.offsets[Util.binarySearchFloor(FlacSeekTable.this.sampleNumbers, (j * timeUs) / C.MICROS_PER_SECOND, true, true)];
            }
        };
    }
}
