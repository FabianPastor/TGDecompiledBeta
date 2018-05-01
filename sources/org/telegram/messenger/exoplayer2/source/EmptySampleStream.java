package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public final class EmptySampleStream implements SampleStream {
    public boolean isReady() {
        return true;
    }

    public void maybeThrowError() throws IOException {
    }

    public int skipData(long j) {
        return 0;
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        decoderInputBuffer.setFlags(4);
        return -4;
    }
}
