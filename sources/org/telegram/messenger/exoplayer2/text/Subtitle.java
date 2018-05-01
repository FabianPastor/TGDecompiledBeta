package org.telegram.messenger.exoplayer2.text;

import java.util.List;

public interface Subtitle {
    List<Cue> getCues(long j);

    long getEventTime(int i);

    int getEventTimeCount();

    int getNextEventTimeIndex(long j);
}
