package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceInfoSectionReader implements SectionPayloadReader {
    private TrackOutput output;

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        this.output = extractorOutput.track(idGenerator.getNextId());
        this.output.format(Format.createSampleFormat(null, MimeTypes.APPLICATION_SCTE35, null, -1, null));
    }

    public void consume(ParsableByteArray sectionData) {
        int sampleSize = sectionData.bytesLeft();
        this.output.sampleData(sectionData, sampleSize);
        this.output.sampleMetadata(0, 1, sampleSize, 0, null);
    }
}
