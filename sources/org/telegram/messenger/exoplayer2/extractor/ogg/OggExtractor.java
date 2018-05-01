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
    public static final ExtractorsFactory FACTORY = new C18421();
    private static final int MAX_VERIFICATION_BYTES = 8;
    private ExtractorOutput output;
    private StreamReader streamReader;
    private boolean streamReaderInitialized;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.ogg.OggExtractor$1 */
    static class C18421 implements ExtractorsFactory {
        C18421() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new OggExtractor()};
        }
    }

    public void release() {
    }

    public boolean sniff(org.telegram.messenger.exoplayer2.extractor.ExtractorInput r1) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = this;
        r1 = r0.sniffInternal(r1);	 Catch:{ ParserException -> 0x0005 }
        return r1;
    L_0x0005:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.ogg.OggExtractor.sniff(org.telegram.messenger.exoplayer2.extractor.ExtractorInput):boolean");
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
    }

    public void seek(long j, long j2) {
        if (this.streamReader != null) {
            this.streamReader.seek(j, j2);
        }
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        if (this.streamReader == null) {
            if (sniffInternal(extractorInput)) {
                extractorInput.resetPeekPosition();
            } else {
                throw new ParserException("Failed to determine bitstream type");
            }
        }
        if (!this.streamReaderInitialized) {
            TrackOutput track = this.output.track(0, 1);
            this.output.endTracks();
            this.streamReader.init(this.output, track);
            this.streamReaderInitialized = true;
        }
        return this.streamReader.read(extractorInput, positionHolder);
    }

    private boolean sniffInternal(ExtractorInput extractorInput) throws IOException, InterruptedException {
        OggPageHeader oggPageHeader = new OggPageHeader();
        if (oggPageHeader.populate(extractorInput, true)) {
            if ((oggPageHeader.type & 2) == 2) {
                int min = Math.min(oggPageHeader.bodySize, 8);
                ParsableByteArray parsableByteArray = new ParsableByteArray(min);
                extractorInput.peekFully(parsableByteArray.data, 0, min);
                if (FlacReader.verifyBitstreamType(resetPosition(parsableByteArray)) != null) {
                    this.streamReader = new FlacReader();
                } else if (VorbisReader.verifyBitstreamType(resetPosition(parsableByteArray)) != null) {
                    this.streamReader = new VorbisReader();
                } else if (OpusReader.verifyBitstreamType(resetPosition(parsableByteArray)) == null) {
                    return false;
                } else {
                    this.streamReader = new OpusReader();
                }
                return true;
            }
        }
        return false;
    }

    private static ParsableByteArray resetPosition(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(0);
        return parsableByteArray;
    }
}
