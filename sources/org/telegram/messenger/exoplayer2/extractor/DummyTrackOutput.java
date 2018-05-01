package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DummyTrackOutput implements TrackOutput {
    public void format(Format format) {
    }

    public void sampleMetadata(long j, int i, int i2, int i3, CryptoData cryptoData) {
    }

    public int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException {
        extractorInput = extractorInput.skip(i);
        if (extractorInput != -1) {
            return extractorInput;
        }
        if (z) {
            return -1;
        }
        throw new EOFException();
    }

    public void sampleData(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.skipBytes(i);
    }
}
