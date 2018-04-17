package org.telegram.messenger.exoplayer2.extractor.mp4;

import org.telegram.messenger.exoplayer2.util.Util;

final class FixedSampleSizeRechunker {
    private static final int MAX_SAMPLE_SIZE = 8192;

    public static final class Results {
        public final int[] flags;
        public final int maximumSize;
        public final long[] offsets;
        public final int[] sizes;
        public final long[] timestamps;

        private Results(long[] offsets, int[] sizes, int maximumSize, long[] timestamps, int[] flags) {
            this.offsets = offsets;
            this.sizes = sizes;
            this.maximumSize = maximumSize;
            this.timestamps = timestamps;
            this.flags = flags;
        }
    }

    FixedSampleSizeRechunker() {
    }

    public static Results rechunk(int fixedSampleSize, long[] chunkOffsets, int[] chunkSampleCounts, long timestampDeltaInTimeUnits) {
        int[] iArr = chunkSampleCounts;
        int maxSampleCount = 8192 / fixedSampleSize;
        int chunkIndex = 0;
        int rechunkedSampleCount = 0;
        for (int chunkSampleCount : iArr) {
            int chunkSampleCount2;
            rechunkedSampleCount += Util.ceilDivide(chunkSampleCount2, maxSampleCount);
        }
        long[] offsets = new long[rechunkedSampleCount];
        int[] sizes = new int[rechunkedSampleCount];
        long[] timestamps = new long[rechunkedSampleCount];
        int[] flags = new int[rechunkedSampleCount];
        int maximumSize = 0;
        int originalSampleIndex = 0;
        int newSampleIndex = 0;
        while (chunkIndex < iArr.length) {
            chunkSampleCount2 = iArr[chunkIndex];
            long sampleOffset = chunkOffsets[chunkIndex];
            int maximumSize2 = maximumSize;
            int originalSampleIndex2 = originalSampleIndex;
            while (chunkSampleCount2 > 0) {
                int bufferSampleCount = Math.min(maxSampleCount, chunkSampleCount2);
                offsets[newSampleIndex] = sampleOffset;
                sizes[newSampleIndex] = fixedSampleSize * bufferSampleCount;
                maximumSize2 = Math.max(maximumSize2, sizes[newSampleIndex]);
                timestamps[newSampleIndex] = ((long) originalSampleIndex2) * timestampDeltaInTimeUnits;
                flags[newSampleIndex] = 1;
                originalSampleIndex2 += bufferSampleCount;
                chunkSampleCount2 -= bufferSampleCount;
                newSampleIndex++;
                sampleOffset += (long) sizes[newSampleIndex];
                iArr = chunkSampleCounts;
            }
            chunkIndex++;
            maximumSize = maximumSize2;
            originalSampleIndex = originalSampleIndex2;
            iArr = chunkSampleCounts;
        }
        return new Results(offsets, sizes, maximumSize, timestamps, flags);
    }
}
