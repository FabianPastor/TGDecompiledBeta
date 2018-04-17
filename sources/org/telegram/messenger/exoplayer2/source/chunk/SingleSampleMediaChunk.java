package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private volatile int bytesLoaded;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private final Format sampleFormat;
    private final int trackType;

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex, int trackType, Format sampleFormat) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.trackType = trackType;
        this.sampleFormat = sampleFormat;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() throws IOException, InterruptedException {
        Throwable th;
        DataSpec loadDataSpec = this.dataSpec.subrange((long) this.bytesLoaded);
        DataSpec dataSpec;
        try {
            long length = r1.dataSource.open(loadDataSpec);
            if (length != -1) {
                try {
                    length += (long) r1.bytesLoaded;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            ExtractorInput defaultExtractorInput = new DefaultExtractorInput(r1.dataSource, (long) r1.bytesLoaded, length);
            BaseMediaChunkOutput output = getOutput();
            output.setSampleOffsetUs(0);
            int result = 0;
            TrackOutput trackOutput = output.track(0, r1.trackType);
            trackOutput.format(r1.sampleFormat);
            while (result != -1) {
                r1.bytesLoaded += result;
                result = trackOutput.sampleData(defaultExtractorInput, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
            }
            dataSpec = loadDataSpec;
            boolean z = true;
            try {
                trackOutput.sampleMetadata(r1.startTimeUs, 1, r1.bytesLoaded, 0, null);
                Util.closeQuietly(r1.dataSource);
                r1.loadCompleted = z;
            } catch (Throwable th3) {
                th = th3;
                loadDataSpec = th;
                Util.closeQuietly(r1.dataSource);
                throw loadDataSpec;
            }
        } catch (Throwable th4) {
            dataSpec = loadDataSpec;
            loadDataSpec = th4;
            Util.closeQuietly(r1.dataSource);
            throw loadDataSpec;
        }
    }
}
