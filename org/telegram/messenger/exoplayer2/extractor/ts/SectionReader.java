package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SectionReader implements TsPayloadReader {
    private static final int SECTION_HEADER_LENGTH = 3;
    private final ParsableBitArray headerScratch = new ParsableBitArray(new byte[3]);
    private final SectionPayloadReader reader;
    private int sectionBytesRead;
    private final ParsableByteArray sectionData = new ParsableByteArray();
    private int sectionLength;

    public SectionReader(SectionPayloadReader reader) {
        this.reader = reader;
    }

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        this.reader.init(timestampAdjuster, extractorOutput, idGenerator);
    }

    public void seek() {
    }

    public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator) {
        if (payloadUnitStartIndicator) {
            data.skipBytes(data.readUnsignedByte());
            data.readBytes(this.headerScratch, 3);
            data.setPosition(data.getPosition() - 3);
            this.headerScratch.skipBits(12);
            this.sectionLength = this.headerScratch.readBits(12) + 3;
            this.sectionBytesRead = 0;
            this.sectionData.reset(this.sectionLength);
        }
        int bytesToRead = Math.min(data.bytesLeft(), this.sectionLength - this.sectionBytesRead);
        data.readBytes(this.sectionData.data, this.sectionBytesRead, bytesToRead);
        this.sectionBytesRead += bytesToRead;
        if (this.sectionBytesRead >= this.sectionLength && Util.crc(this.sectionData.data, 0, this.sectionLength, -1) == 0) {
            this.sectionData.setLimit(this.sectionData.limit() - 4);
            this.reader.consume(this.sectionData);
        }
    }
}
