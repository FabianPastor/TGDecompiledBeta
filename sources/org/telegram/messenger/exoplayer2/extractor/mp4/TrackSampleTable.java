package org.telegram.messenger.exoplayer2.extractor.mp4;

import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class TrackSampleTable {
    public final int[] flags;
    public final int maximumSize;
    public final long[] offsets;
    public final int sampleCount;
    public final int[] sizes;
    public final long[] timestampsUs;

    public TrackSampleTable(long[] jArr, int[] iArr, int i, long[] jArr2, int[] iArr2) {
        boolean z = false;
        Assertions.checkArgument(iArr.length == jArr2.length);
        Assertions.checkArgument(jArr.length == jArr2.length);
        if (iArr2.length == jArr2.length) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.offsets = jArr;
        this.sizes = iArr;
        this.maximumSize = i;
        this.timestampsUs = jArr2;
        this.flags = iArr2;
        this.sampleCount = jArr.length;
    }

    public int getIndexOfEarlierOrEqualSynchronizationSample(long j) {
        for (j = Util.binarySearchFloor(this.timestampsUs, j, true, false); j >= null; j--) {
            if ((this.flags[j] & 1) != 0) {
                return j;
            }
        }
        return -1;
    }

    public int getIndexOfLaterOrEqualSynchronizationSample(long j) {
        for (j = Util.binarySearchCeil(this.timestampsUs, j, true, false); j < this.timestampsUs.length; j++) {
            if ((this.flags[j] & 1) != 0) {
                return j;
            }
        }
        return -1;
    }
}
