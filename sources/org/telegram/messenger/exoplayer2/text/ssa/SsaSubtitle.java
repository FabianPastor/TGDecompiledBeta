package org.telegram.messenger.exoplayer2.text.ssa;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SsaSubtitle implements Subtitle {
    private final long[] cueTimesUs;
    private final Cue[] cues;

    public SsaSubtitle(Cue[] cueArr, long[] jArr) {
        this.cues = cueArr;
        this.cueTimesUs = jArr;
    }

    public int getNextEventTimeIndex(long j) {
        j = Util.binarySearchCeil(this.cueTimesUs, j, false, false);
        return j < this.cueTimesUs.length ? j : -1;
    }

    public int getEventTimeCount() {
        return this.cueTimesUs.length;
    }

    public long getEventTime(int i) {
        boolean z = false;
        Assertions.checkArgument(i >= 0);
        if (i < this.cueTimesUs.length) {
            z = true;
        }
        Assertions.checkArgument(z);
        return this.cueTimesUs[i];
    }

    public List<Cue> getCues(long j) {
        j = Util.binarySearchFloor(this.cueTimesUs, j, true, false);
        if (j != -1) {
            if (this.cues[j] != null) {
                return Collections.singletonList(this.cues[j]);
            }
        }
        return Collections.emptyList();
    }
}
