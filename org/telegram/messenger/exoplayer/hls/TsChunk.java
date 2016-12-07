package org.telegram.messenger.exoplayer.hls;

import java.io.IOException;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Util;

public final class TsChunk extends MediaChunk {
    private long adjustedEndTimeUs;
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    public final HlsExtractorWrapper extractorWrapper;
    private final boolean isEncrypted = (this.dataSource instanceof Aes128DataSource);
    private volatile boolean loadCanceled;

    public TsChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, long startTimeUs, long endTimeUs, int chunkIndex, int discontinuitySequenceNumber, HlsExtractorWrapper extractorWrapper, byte[] encryptionKey, byte[] encryptionIv) {
        super(buildDataSource(dataSource, encryptionKey, encryptionIv), dataSpec, trigger, format, startTimeUs, endTimeUs, chunkIndex);
        this.discontinuitySequenceNumber = discontinuitySequenceNumber;
        this.extractorWrapper = extractorWrapper;
        this.adjustedEndTimeUs = startTimeUs;
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
        DataSpec loadDataSpec;
        boolean skipLoadedBytes;
        if (this.isEncrypted) {
            loadDataSpec = this.dataSpec;
            skipLoadedBytes = this.bytesLoaded != 0;
        } else {
            loadDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
            skipLoadedBytes = false;
        }
        ExtractorInput input;
        try {
            input = new DefaultExtractorInput(this.dataSource, loadDataSpec.absoluteStreamPosition, this.dataSource.open(loadDataSpec));
            if (skipLoadedBytes) {
                input.skipFully(this.bytesLoaded);
            }
            int result = 0;
            while (result == 0) {
                if (!this.loadCanceled) {
                    result = this.extractorWrapper.read(input);
                }
            }
            long tsChunkEndTimeUs = this.extractorWrapper.getAdjustedEndTimeUs();
            if (tsChunkEndTimeUs != Long.MIN_VALUE) {
                this.adjustedEndTimeUs = tsChunkEndTimeUs;
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }

    public long getAdjustedEndTimeUs() {
        return this.adjustedEndTimeUs;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] encryptionKey, byte[] encryptionIv) {
        return (encryptionKey == null || encryptionIv == null) ? dataSource : new Aes128DataSource(dataSource, encryptionKey, encryptionIv);
    }
}
