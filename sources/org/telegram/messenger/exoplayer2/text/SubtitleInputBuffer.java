package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public final class SubtitleInputBuffer extends DecoderInputBuffer implements Comparable<SubtitleInputBuffer> {
    public long subsampleOffsetUs;

    public SubtitleInputBuffer() {
        super(1);
    }

    public int compareTo(SubtitleInputBuffer subtitleInputBuffer) {
        int i = -1;
        if (isEndOfStream() != subtitleInputBuffer.isEndOfStream()) {
            if (isEndOfStream() != null) {
                i = 1;
            }
            return i;
        }
        long j = this.timeUs - subtitleInputBuffer.timeUs;
        if (j == 0) {
            return null;
        }
        if (j > 0) {
            i = 1;
        }
        return i;
    }
}
