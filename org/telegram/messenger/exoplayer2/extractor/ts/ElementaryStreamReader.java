package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public abstract class ElementaryStreamReader {

    public static final class EsInfo {
        public byte[] descriptorBytes;
        public String language;
        public final int streamType;

        public EsInfo(int streamType, String language, byte[] descriptorBytes) {
            this.streamType = streamType;
            this.language = language;
            this.descriptorBytes = descriptorBytes;
        }
    }

    public interface Factory {
        ElementaryStreamReader createStreamReader(int i, EsInfo esInfo);
    }

    public static final class TrackIdGenerator {
        private final int firstId;
        private int generatedIdCount;
        private final int idIncrement;

        public TrackIdGenerator(int firstId, int idIncrement) {
            this.firstId = firstId;
            this.idIncrement = idIncrement;
        }

        public int getNextId() {
            int i = this.firstId;
            int i2 = this.idIncrement;
            int i3 = this.generatedIdCount;
            this.generatedIdCount = i3 + 1;
            return i + (i2 * i3);
        }
    }

    public abstract void consume(ParsableByteArray parsableByteArray);

    public abstract void init(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator);

    public abstract void packetFinished();

    public abstract void packetStarted(long j, boolean z);

    public abstract void seek();
}
