package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public final class SubtitleInputBuffer extends DecoderInputBuffer implements Comparable<SubtitleInputBuffer> {
    public long subsampleOffsetUs;

    public SubtitleInputBuffer() {
        super(1);
    }

    public int compareTo(SubtitleInputBuffer other) {
        int i = -1;
        if (isEndOfStream() != other.isEndOfStream()) {
            if (isEndOfStream()) {
                i = 1;
            }
            return i;
        }
        long delta = this.timeUs - other.timeUs;
        if (delta == 0) {
            return 0;
        }
        if (delta > 0) {
            i = 1;
        }
        return i;
    }
}
