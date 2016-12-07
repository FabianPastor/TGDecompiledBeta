package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper.SingleTrackMetadataOutput;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;

public class ContainerMediaChunk extends BaseMediaChunk implements SingleTrackMetadataOutput {
    private volatile int bytesLoaded;
    private final int chunkCount;
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private final Format sampleFormat;
    private final long sampleOffsetUs;

    public ContainerMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex, int chunkCount, long sampleOffsetUs, ChunkExtractorWrapper extractorWrapper, Format sampleFormat) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.chunkCount = chunkCount;
        this.sampleOffsetUs = sampleOffsetUs;
        this.extractorWrapper = extractorWrapper;
        this.sampleFormat = sampleFormat;
    }

    public int getNextChunkIndex() {
        return this.chunkIndex + this.chunkCount;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public final long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public final void seekMap(SeekMap seekMap) {
    }

    public final void cancelLoad() {
        this.loadCanceled = true;
    }

    public final boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public final void load() throws IOException, InterruptedException {
        DataSpec loadDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
        ExtractorInput input;
        try {
            input = new DefaultExtractorInput(this.dataSource, loadDataSpec.absoluteStreamPosition, this.dataSource.open(loadDataSpec));
            if (this.bytesLoaded == 0) {
                DefaultTrackOutput trackOutput = getTrackOutput();
                trackOutput.formatWithOffset(this.sampleFormat, this.sampleOffsetUs);
                this.extractorWrapper.init(this, trackOutput);
            }
            int result = 0;
            while (result == 0) {
                if (!this.loadCanceled) {
                    result = this.extractorWrapper.read(input);
                }
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
            this.loadCompleted = true;
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }
}
