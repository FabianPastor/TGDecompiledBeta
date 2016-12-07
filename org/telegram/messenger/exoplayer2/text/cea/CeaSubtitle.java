package org.telegram.messenger.exoplayer2.text.cea;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;

final class CeaSubtitle implements Subtitle {
    private final List<Cue> cues;

    public CeaSubtitle(Cue cue) {
        if (cue == null) {
            this.cues = Collections.emptyList();
        } else {
            this.cues = Collections.singletonList(cue);
        }
    }

    public int getNextEventTimeIndex(long timeUs) {
        return 0;
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
