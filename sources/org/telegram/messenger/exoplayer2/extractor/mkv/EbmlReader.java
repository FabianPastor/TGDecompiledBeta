package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;

interface EbmlReader {
    public static final int TYPE_BINARY = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_MASTER = 1;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_UNSIGNED_INT = 2;

    void init(EbmlReaderOutput ebmlReaderOutput);

    boolean read(ExtractorInput extractorInput) throws IOException, InterruptedException;

    void reset();
}
