package org.telegram.messenger.exoplayer.util;

import org.telegram.messenger.exoplayer.C;

public final class FlacUtil {
    private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;

    private FlacUtil() {
    }

    public static long extractSampleTimestamp(FlacStreamInfo streamInfo, ParsableByteArray frameData) {
        frameData.skipBytes(4);
        long sampleNumber = frameData.readUTF8EncodedLong();
        if (streamInfo.minBlockSize == streamInfo.maxBlockSize) {
            sampleNumber *= (long) streamInfo.minBlockSize;
        }
        return (C.MICROS_PER_SECOND * sampleNumber) / ((long) streamInfo.sampleRate);
    }
}
