package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ogg.OggUtil.PageHeader;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class OggSeeker {
    private static final int MATCH_RANGE = 72000;
    private long audioDataLength = -1;
    private final ParsableByteArray headerArray = new ParsableByteArray(282);
    private final PageHeader pageHeader = new PageHeader();
    private long totalSamples;

    OggSeeker() {
    }

    public void setup(long audioDataLength, long totalSamples) {
        boolean z = audioDataLength > 0 && totalSamples > 0;
        Assertions.checkArgument(z);
        this.audioDataLength = audioDataLength;
        this.totalSamples = totalSamples;
    }

    public long getNextSeekPosition(long targetGranule, ExtractorInput input) throws IOException, InterruptedException {
        boolean z = (this.audioDataLength == -1 || this.totalSamples == 0) ? false : true;
        Assertions.checkState(z);
        OggUtil.populatePageHeader(input, this.pageHeader, this.headerArray, false);
        long granuleDistance = targetGranule - this.pageHeader.granulePosition;
        if (granuleDistance <= 0 || granuleDistance > 72000) {
            return (input.getPosition() - ((long) ((granuleDistance <= 0 ? 2 : 1) * (this.pageHeader.headerSize + this.pageHeader.bodySize)))) + ((this.audioDataLength * granuleDistance) / this.totalSamples);
        }
        input.resetPeekPosition();
        return -1;
    }
}
