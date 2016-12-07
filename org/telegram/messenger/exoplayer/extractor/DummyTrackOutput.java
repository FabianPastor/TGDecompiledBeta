package org.telegram.messenger.exoplayer.extractor;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public class DummyTrackOutput implements TrackOutput {
    public void format(MediaFormat format) {
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        return input.skip(length);
    }

    public void sampleData(ParsableByteArray data, int length) {
        data.skipBytes(length);
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, byte[] encryptionKey) {
    }
}
