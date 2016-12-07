package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public interface TsPayloadReader {

    public static final class EsInfo {
        public final byte[] descriptorBytes;
        public final String language;
        public final int streamType;

        public EsInfo(int streamType, String language, byte[] descriptorBytes) {
            this.streamType = streamType;
            this.language = language;
            this.descriptorBytes = descriptorBytes;
        }
    }

    public interface Factory {
        SparseArray<TsPayloadReader> createInitialPayloadReaders();

        TsPayloadReader createPayloadReader(int i, EsInfo esInfo);
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

    void consume(ParsableByteArray parsableByteArray, boolean z);

    void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator);

    void seek();
}
