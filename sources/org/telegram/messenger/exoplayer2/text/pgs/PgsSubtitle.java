package org.telegram.messenger.exoplayer2.text.pgs;

import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;

final class PgsSubtitle implements Subtitle {
    private final List<Cue> cues;

    public long getEventTime(int i) {
        return 0;
    }

    public int getEventTimeCount() {
        return 1;
    }

    public int getNextEventTimeIndex(long j) {
        return -1;
    }

    public PgsSubtitle(List<Cue> list) {
        this.cues = list;
    }

    public List<Cue> getCues(long j) {
        return this.cues;
    }
}
