package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class TrackFragment {
    public long atomPosition;
    public long auxiliaryDataPosition;
    public long dataPosition;
    public boolean definesEncryptionData;
    public DefaultSampleValues header;
    public long nextFragmentDecodeTime;
    public int[] sampleCompositionTimeOffsetTable;
    public int sampleCount;
    public long[] sampleDecodingTimeTable;
    public ParsableByteArray sampleEncryptionData;
    public int sampleEncryptionDataLength;
    public boolean sampleEncryptionDataNeedsFill;
    public boolean[] sampleHasSubsampleEncryptionTable;
    public boolean[] sampleIsSyncFrameTable;
    public int[] sampleSizeTable;
    public TrackEncryptionBox trackEncryptionBox;
    public int trunCount;
    public long[] trunDataPosition;
    public int[] trunLength;

    TrackFragment() {
    }

    public void reset() {
        this.trunCount = 0;
        this.nextFragmentDecodeTime = 0;
        this.definesEncryptionData = false;
        this.sampleEncryptionDataNeedsFill = false;
        this.trackEncryptionBox = null;
    }

    public void initTables(int i, int i2) {
        this.trunCount = i;
        this.sampleCount = i2;
        if (this.trunLength == null || this.trunLength.length < i) {
            this.trunDataPosition = new long[i];
            this.trunLength = new int[i];
        }
        if (this.sampleSizeTable == 0 || this.sampleSizeTable.length < i2) {
            i2 = (i2 * 125) / 100;
            this.sampleSizeTable = new int[i2];
            this.sampleCompositionTimeOffsetTable = new int[i2];
            this.sampleDecodingTimeTable = new long[i2];
            this.sampleIsSyncFrameTable = new boolean[i2];
            this.sampleHasSubsampleEncryptionTable = new boolean[i2];
        }
    }

    public void initEncryptionData(int i) {
        if (this.sampleEncryptionData == null || this.sampleEncryptionData.limit() < i) {
            this.sampleEncryptionData = new ParsableByteArray(i);
        }
        this.sampleEncryptionDataLength = i;
        this.definesEncryptionData = true;
        this.sampleEncryptionDataNeedsFill = true;
    }

    public void fillEncryptionData(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public void fillEncryptionData(ParsableByteArray parsableByteArray) {
        parsableByteArray.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public long getSamplePresentationTime(int i) {
        return this.sampleDecodingTimeTable[i] + ((long) this.sampleCompositionTimeOffsetTable[i]);
    }
}
