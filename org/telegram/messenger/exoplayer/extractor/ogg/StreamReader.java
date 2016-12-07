package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

abstract class StreamReader {
    protected ExtractorOutput extractorOutput;
    protected final OggParser oggParser = new OggParser();
    protected final ParsableByteArray scratch = new ParsableByteArray(new byte[65025], 0);
    protected TrackOutput trackOutput;

    abstract int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException;

    StreamReader() {
    }

    void init(ExtractorOutput output, TrackOutput trackOutput) {
        this.extractorOutput = output;
        this.trackOutput = trackOutput;
    }

    void seek() {
        this.oggParser.reset();
        this.scratch.reset();
    }
}
