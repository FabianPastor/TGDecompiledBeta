package org.telegram.messenger.exoplayer.text;

import java.util.List;

final class PlayableSubtitle implements Subtitle {
    private final long offsetUs;
    public final long startTimeUs;
    private final Subtitle subtitle;

    public PlayableSubtitle(Subtitle subtitle, boolean isRelative, long startTimeUs, long offsetUs) {
        this.subtitle = subtitle;
        this.startTimeUs = startTimeUs;
        if (!isRelative) {
            startTimeUs = 0;
        }
        this.offsetUs = startTimeUs + offsetUs;
    }

    public int getEventTimeCount() {
        return this.subtitle.getEventTimeCount();
    }

    public long getEventTime(int index) {
        return this.subtitle.getEventTime(index) + this.offsetUs;
    }

    public long getLastEventTime() {
        return this.subtitle.getLastEventTime() + this.offsetUs;
    }

    public int getNextEventTimeIndex(long timeUs) {
        return this.subtitle.getNextEventTimeIndex(timeUs - this.offsetUs);
    }

    public List<Cue> getCues(long timeUs) {
        return this.subtitle.getCues(timeUs - this.offsetUs);
    }
}
