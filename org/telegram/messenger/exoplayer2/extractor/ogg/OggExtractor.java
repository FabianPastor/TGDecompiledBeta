package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public class OggExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new OggExtractor()};
        }
    };
    private static final int MAX_VERIFICATION_BYTES = 8;
    private StreamReader streamReader;

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        try {
            OggPageHeader header = new OggPageHeader();
            if (!header.populate(input, true) || (header.type & 2) != 2) {
                return false;
            }
            int length = Math.min(header.bodySize, 8);
            ParsableByteArray scratch = new ParsableByteArray(length);
            input.peekFully(scratch.data, 0, length);
            if (FlacReader.verifyBitstreamType(resetPosition(scratch))) {
                this.streamReader = new FlacReader();
            } else if (VorbisReader.verifyBitstreamType(resetPosition(scratch))) {
                this.streamReader = new VorbisReader();
            } else if (!OpusReader.verifyBitstreamType(resetPosition(scratch))) {
                return false;
            } else {
                this.streamReader = new OpusReader();
            }
            return true;
        } catch (ParserException e) {
            return false;
        }
    }

    public void init(ExtractorOutput output) {
        TrackOutput trackOutput = output.track(0, 1);
        output.endTracks();
        this.streamReader.init(output, trackOutput);
    }

    public void seek(long position, long timeUs) {
        this.streamReader.seek(position, timeUs);
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        return this.streamReader.read(input, seekPosition);
    }

    StreamReader getStreamReader() {
        return this.streamReader;
    }

    private static ParsableByteArray resetPosition(ParsableByteArray scratch) {
        scratch.setPosition(0);
        return scratch;
    }
}
