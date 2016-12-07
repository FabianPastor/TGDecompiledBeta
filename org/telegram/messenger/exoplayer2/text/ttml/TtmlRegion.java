package org.telegram.messenger.exoplayer2.text.ttml;

import org.telegram.messenger.exoplayer2.text.Cue;

final class TtmlRegion {
    public final float line;
    public final int lineType;
    public final float position;
    public final float width;

    public TtmlRegion() {
        this(Cue.DIMEN_UNSET, Cue.DIMEN_UNSET, Integer.MIN_VALUE, Cue.DIMEN_UNSET);
    }

    public TtmlRegion(float position, float line, int lineType, float width) {
        this.position = position;
        this.line = line;
        this.lineType = lineType;
        this.width = width;
    }
}
