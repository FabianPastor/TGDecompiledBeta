package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public class SubtitleInputBuffer extends DecoderInputBuffer {
    public long subsampleOffsetUs;

    public SubtitleInputBuffer() {
        super(1);
    }
}
