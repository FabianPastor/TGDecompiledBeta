package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsMediaChunk extends MediaChunk {
    private static final AtomicInteger UID_SOURCE = new AtomicInteger();
    private long adjustedEndTimeUs;
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    public final Extractor extractor;
    private final boolean extractorNeedsInit;
    private HlsSampleStreamWrapper extractorOutput;
    private final boolean isEncrypted = (this.dataSource instanceof Aes128DataSource);
    private final boolean isMasterTimestampSource;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid = UID_SOURCE.getAndIncrement();

    public HlsMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex, int discontinuitySequenceNumber, boolean isMasterTimestampSource, TimestampAdjuster timestampAdjuster, Extractor extractor, boolean extractorNeedsInit, boolean shouldSpliceIn, byte[] encryptionKey, byte[] encryptionIv) {
        super(buildDataSource(dataSource, encryptionKey, encryptionIv), dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.discontinuitySequenceNumber = discontinuitySequenceNumber;
        this.isMasterTimestampSource = isMasterTimestampSource;
        this.timestampAdjuster = timestampAdjuster;
        this.extractor = extractor;
        this.extractorNeedsInit = extractorNeedsInit;
        this.shouldSpliceIn = shouldSpliceIn;
        this.adjustedEndTimeUs = startTimeUs;
    }

    public void init(HlsSampleStreamWrapper output) {
        this.extractorOutput = output;
        output.init(this.uid, this.shouldSpliceIn);
        if (this.extractorNeedsInit) {
            this.extractor.init(output);
        }
    }

    public long getAdjustedStartTimeUs() {
        return this.adjustedEndTimeUs - getDurationUs();
    }

    public long getAdjustedEndTimeUs() {
        return this.adjustedEndTimeUs;
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
            if (!(this.isMasterTimestampSource || this.timestampAdjuster == null)) {
                this.timestampAdjuster.waitUntilInitialized();
            }
            while (result == 0 && !this.loadCanceled) {
                result = this.extractor.read(input, null);
            }
            long adjustedEndTimeUs = this.extractorOutput.getLargestQueuedTimestampUs();
            if (adjustedEndTimeUs != Long.MIN_VALUE) {
                this.adjustedEndTimeUs = adjustedEndTimeUs;
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
            this.loadCompleted = true;
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] encryptionKey, byte[] encryptionIv) {
        return (encryptionKey == null || encryptionIv == null) ? dataSource : new Aes128DataSource(dataSource, encryptionKey, encryptionIv);
    }
}
