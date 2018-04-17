package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SectionReader implements TsPayloadReader {
    private static final int DEFAULT_SECTION_BUFFER_LENGTH = 32;
    private static final int MAX_SECTION_LENGTH = 4098;
    private static final int SECTION_HEADER_LENGTH = 3;
    private int bytesRead;
    private final SectionPayloadReader reader;
    private final ParsableByteArray sectionData = new ParsableByteArray(32);
    private boolean sectionSyntaxIndicator;
    private int totalSectionLength;
    private boolean waitingForPayloadStart;

    public SectionReader(SectionPayloadReader reader) {
        this.reader = reader;
    }

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        this.reader.init(timestampAdjuster, extractorOutput, idGenerator);
        this.waitingForPayloadStart = true;
    }

    public void seek() {
        this.waitingForPayloadStart = true;
    }

    public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator) {
        int payloadStartPosition = -1;
        if (payloadUnitStartIndicator) {
            payloadStartPosition = data.getPosition() + data.readUnsignedByte();
        }
        if (this.waitingForPayloadStart) {
            if (payloadUnitStartIndicator) {
                this.waitingForPayloadStart = false;
                data.setPosition(payloadStartPosition);
                this.bytesRead = 0;
            } else {
                return;
            }
        }
        while (data.bytesLeft() > 0) {
            boolean z = true;
            int tableId;
            if (this.bytesRead < 3) {
                if (this.bytesRead == 0) {
                    tableId = data.readUnsignedByte();
                    data.setPosition(data.getPosition() - 1);
                    if (tableId == 255) {
                        this.waitingForPayloadStart = true;
                        return;
                    }
                }
                tableId = Math.min(data.bytesLeft(), 3 - this.bytesRead);
                data.readBytes(this.sectionData.data, this.bytesRead, tableId);
                this.bytesRead += tableId;
                if (this.bytesRead == 3) {
                    this.sectionData.reset(3);
                    this.sectionData.skipBytes(1);
                    int secondHeaderByte = this.sectionData.readUnsignedByte();
                    int thirdHeaderByte = this.sectionData.readUnsignedByte();
                    if ((secondHeaderByte & 128) == 0) {
                        z = false;
                    }
                    this.sectionSyntaxIndicator = z;
                    this.totalSectionLength = (((secondHeaderByte & 15) << 8) | thirdHeaderByte) + 3;
                    if (this.sectionData.capacity() < this.totalSectionLength) {
                        byte[] bytes = this.sectionData.data;
                        this.sectionData.reset(Math.min(MAX_SECTION_LENGTH, Math.max(this.totalSectionLength, bytes.length * 2)));
                        System.arraycopy(bytes, 0, this.sectionData.data, 0, 3);
                    }
                }
            } else {
                tableId = Math.min(data.bytesLeft(), this.totalSectionLength - this.bytesRead);
                data.readBytes(this.sectionData.data, this.bytesRead, tableId);
                this.bytesRead += tableId;
                if (this.bytesRead == this.totalSectionLength) {
                    if (!this.sectionSyntaxIndicator) {
                        this.sectionData.reset(this.totalSectionLength);
                    } else if (Util.crc(this.sectionData.data, 0, this.totalSectionLength, -1) != 0) {
                        this.waitingForPayloadStart = true;
                        return;
                    } else {
                        this.sectionData.reset(this.totalSectionLength - 4);
                    }
                    this.reader.consume(this.sectionData);
                    this.bytesRead = 0;
                }
            }
        }
    }
}
