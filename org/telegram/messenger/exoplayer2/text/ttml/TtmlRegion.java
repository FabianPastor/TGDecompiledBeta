package org.telegram.messenger.exoplayer2.text.ttml;

import org.telegram.messenger.exoplayer2.text.Cue;

final class TtmlRegion {
    public final String id;
    public final float line;
    public final int lineAnchor;
    public final int lineType;
    public final float position;
    public final float width;

    public TtmlRegion(String id) {
        this(id, Cue.DIMEN_UNSET, Cue.DIMEN_UNSET, Integer.MIN_VALUE, Integer.MIN_VALUE, Cue.DIMEN_UNSET);
    }

    public TtmlRegion(String id, float position, float line, int lineType, int lineAnchor, float width) {
        this.id = id;
        this.position = position;
        this.line = line;
        this.lineType = lineType;
        this.lineAnchor = lineAnchor;
        this.width = width;
    }
}
