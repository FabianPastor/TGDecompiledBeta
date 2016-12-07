package org.telegram.messenger.exoplayer.extractor.mp4;

import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

final class TrackSampleTable {
    public static final int NO_SAMPLE = -1;
    public final int[] flags;
    public final int maximumSize;
    public final long[] offsets;
    public final int sampleCount;
    public final int[] sizes;
    public final long[] timestampsUs;

    TrackSampleTable(long[] offsets, int[] sizes, int maximumSize, long[] timestampsUs, int[] flags) {
        boolean z;
        boolean z2 = true;
        Assertions.checkArgument(sizes.length == timestampsUs.length);
        if (offsets.length == timestampsUs.length) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkArgument(z);
        if (flags.length != timestampsUs.length) {
            z2 = false;
        }
        Assertions.checkArgument(z2);
        this.offsets = offsets;
        this.sizes = sizes;
        this.maximumSize = maximumSize;
        this.timestampsUs = timestampsUs;
        this.flags = flags;
        this.sampleCount = offsets.length;
    }

    public int getIndexOfEarlierOrEqualSynchronizationSample(long timeUs) {
        for (int i = Util.binarySearchFloor(this.timestampsUs, timeUs, true, false); i >= 0; i--) {
            if ((this.flags[i] & 1) != 0) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfLaterOrEqualSynchronizationSample(long timeUs) {
        for (int i = Util.binarySearchCeil(this.timestampsUs, timeUs, true, false); i < this.timestampsUs.length; i++) {
            if ((this.flags[i] & 1) != 0) {
                return i;
            }
        }
        return -1;
    }
}
