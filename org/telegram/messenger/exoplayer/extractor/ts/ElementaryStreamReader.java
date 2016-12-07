package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

abstract class ElementaryStreamReader {
    protected final TrackOutput output;

    public abstract void consume(ParsableByteArray parsableByteArray);

    public abstract void packetFinished();

    public abstract void packetStarted(long j, boolean z);

    public abstract void seek();

    protected ElementaryStreamReader(TrackOutput output) {
        this.output = output;
    }
}
