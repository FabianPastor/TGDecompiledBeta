package org.telegram.messenger.exoplayer2.source.hls;

import android.util.SparseArray;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;

public final class TimestampAdjusterProvider {
    private final SparseArray<TimestampAdjuster> timestampAdjusters = new SparseArray();

    public TimestampAdjuster getAdjuster(int discontinuitySequence, long startTimeUs) {
        TimestampAdjuster adjuster = (TimestampAdjuster) this.timestampAdjusters.get(discontinuitySequence);
        if (adjuster != null) {
            return adjuster;
        }
        adjuster = new TimestampAdjuster(startTimeUs);
        this.timestampAdjusters.put(discontinuitySequence, adjuster);
        return adjuster;
    }

    public void reset() {
        this.timestampAdjusters.clear();
    }
}
