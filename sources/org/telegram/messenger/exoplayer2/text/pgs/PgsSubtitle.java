package org.telegram.messenger.exoplayer2.text.pgs;

import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;

final class PgsSubtitle implements Subtitle {
    private final List<Cue> cues;

    public PgsSubtitle(List<Cue> cues) {
        this.cues = cues;
    }

    public int getNextEventTimeIndex(long timeUs) {
        return -1;
    }

    public int getEventTimeCount() {
        return 1;
    }

    public long getEventTime(int index) {
        return 0;
    }

    public List<Cue> getCues(long timeUs) {
        return this.cues;
    }
}
