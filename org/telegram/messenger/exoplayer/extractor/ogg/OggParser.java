package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ogg.OggUtil.PacketInfoHolder;
import org.telegram.messenger.exoplayer.extractor.ogg.OggUtil.PageHeader;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class OggParser {
    public static final int OGG_MAX_SEGMENT_SIZE = 255;
    private int currentSegmentIndex = -1;
    private long elapsedSamples;
    private final ParsableByteArray headerArray = new ParsableByteArray(282);
    private final PacketInfoHolder holder = new PacketInfoHolder();
    private final PageHeader pageHeader = new PageHeader();

    OggParser() {
    }

    public void reset() {
        this.pageHeader.reset();
        this.headerArray.reset();
        this.currentSegmentIndex = -1;
    }

    public boolean readPacket(ExtractorInput input, ParsableByteArray packetArray) throws IOException, InterruptedException {
        boolean z;
        if (input == null || packetArray == null) {
            z = false;
        } else {
            z = true;
        }
        Assertions.checkState(z);
        boolean z2 = false;
        while (!z2) {
            int segmentIndex;
            if (this.currentSegmentIndex < 0) {
                if (!OggUtil.populatePageHeader(input, this.pageHeader, this.headerArray, true)) {
                    return false;
                }
                segmentIndex = 0;
                int bytesToSkip = this.pageHeader.headerSize;
                if ((this.pageHeader.type & 1) == 1 && packetArray.limit() == 0) {
                    OggUtil.calculatePacketSize(this.pageHeader, 0, this.holder);
                    segmentIndex = 0 + this.holder.segmentCount;
                    bytesToSkip += this.holder.size;
                }
                input.skipFully(bytesToSkip);
                this.currentSegmentIndex = segmentIndex;
            }
            OggUtil.calculatePacketSize(this.pageHeader, this.currentSegmentIndex, this.holder);
            segmentIndex = this.currentSegmentIndex + this.holder.segmentCount;
            if (this.holder.size > 0) {
                input.readFully(packetArray.data, packetArray.limit(), this.holder.size);
                packetArray.setLimit(packetArray.limit() + this.holder.size);
                if (this.pageHeader.laces[segmentIndex - 1] != 255) {
                    z2 = true;
                } else {
                    z2 = false;
                }
            }
            if (segmentIndex == this.pageHeader.pageSegmentCount) {
                segmentIndex = -1;
            }
            this.currentSegmentIndex = segmentIndex;
        }
        return true;
    }

    public long readGranuleOfLastPage(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkArgument(input.getLength() != -1);
        OggUtil.skipToNextPage(input);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && input.getPosition() < input.getLength()) {
            OggUtil.populatePageHeader(input, this.pageHeader, this.headerArray, false);
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    public long skipToPageOfGranule(ExtractorInput input, long targetGranule) throws IOException, InterruptedException {
        OggUtil.skipToNextPage(input);
        OggUtil.populatePageHeader(input, this.pageHeader, this.headerArray, false);
        while (this.pageHeader.granulePosition < targetGranule) {
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            this.elapsedSamples = this.pageHeader.granulePosition;
            OggUtil.populatePageHeader(input, this.pageHeader, this.headerArray, false);
        }
        if (this.elapsedSamples == 0) {
            throw new ParserException();
        }
        input.resetPeekPosition();
        long returnValue = this.elapsedSamples;
        this.elapsedSamples = 0;
        this.currentSegmentIndex = -1;
        return returnValue;
    }

    public PageHeader getPageHeader() {
        return this.pageHeader;
    }
}
