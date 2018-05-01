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

    public SectionReader(SectionPayloadReader sectionPayloadReader) {
        this.reader = sectionPayloadReader;
    }

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        this.reader.init(timestampAdjuster, extractorOutput, trackIdGenerator);
        this.waitingForPayloadStart = true;
    }

    public void seek() {
        this.waitingForPayloadStart = true;
    }

    public void consume(ParsableByteArray parsableByteArray, boolean z) {
        int readUnsignedByte = z ? parsableByteArray.readUnsignedByte() + parsableByteArray.getPosition() : -1;
        if (this.waitingForPayloadStart) {
            if (z) {
                this.waitingForPayloadStart = false;
                parsableByteArray.setPosition(readUnsignedByte);
                this.bytesRead = 0;
            } else {
                return;
            }
        }
        while (parsableByteArray.bytesLeft() <= false) {
            boolean z2 = true;
            if (this.bytesRead < true) {
                if (!this.bytesRead) {
                    z = parsableByteArray.readUnsignedByte();
                    parsableByteArray.setPosition(parsableByteArray.getPosition() - 1);
                    if (z) {
                        this.waitingForPayloadStart = true;
                        return;
                    }
                }
                z = Math.min(parsableByteArray.bytesLeft(), 3 - this.bytesRead);
                parsableByteArray.readBytes(this.sectionData.data, this.bytesRead, z);
                this.bytesRead += z;
                if (this.bytesRead) {
                    this.sectionData.reset(3);
                    this.sectionData.skipBytes(1);
                    z = this.sectionData.readUnsignedByte();
                    int readUnsignedByte2 = this.sectionData.readUnsignedByte();
                    if ((z & 128) == 0) {
                        z2 = false;
                    }
                    this.sectionSyntaxIndicator = z2;
                    this.totalSectionLength = (((z & 15) << 8) | readUnsignedByte2) + true;
                    if (this.sectionData.capacity() < this.totalSectionLength) {
                        z = this.sectionData.data;
                        this.sectionData.reset(Math.min(MAX_SECTION_LENGTH, Math.max(this.totalSectionLength, z.length * 2)));
                        System.arraycopy(z, 0, this.sectionData.data, 0, 3);
                    }
                }
            } else {
                z = Math.min(parsableByteArray.bytesLeft(), this.totalSectionLength - this.bytesRead);
                parsableByteArray.readBytes(this.sectionData.data, this.bytesRead, z);
                this.bytesRead += z;
                if (this.bytesRead != this.totalSectionLength) {
                    continue;
                } else {
                    if (!this.sectionSyntaxIndicator) {
                        this.sectionData.reset(this.totalSectionLength);
                    } else if (Util.crc(this.sectionData.data, 0, this.totalSectionLength, -1)) {
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
