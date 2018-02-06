package org.telegram.messenger.exoplayer2.source.hls;

import android.util.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.id3.PrivFrame;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsMediaChunk extends MediaChunk {
    private static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
    private static final AtomicInteger uidSource = new AtomicInteger();
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    private final Extractor extractor;
    public final HlsUrl hlsUrl;
    private final ParsableByteArray id3Data;
    private final Id3Decoder id3Decoder;
    private boolean id3TimestampPeeked;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private boolean initLoadCompleted;
    private int initSegmentBytesLoaded;
    private final boolean isEncrypted = (this.dataSource instanceof Aes128DataSource);
    private final boolean isMasterTimestampSource;
    private final boolean isPackedAudioExtractor;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private HlsSampleStreamWrapper output;
    private final boolean reusingExtractor;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;

    public HlsMediaChunk(HlsExtractorFactory extractorFactory, DataSource dataSource, DataSpec dataSpec, DataSpec initDataSpec, HlsUrl hlsUrl, List<Format> muxedCaptionFormats, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex, int discontinuitySequenceNumber, boolean isMasterTimestampSource, TimestampAdjuster timestampAdjuster, HlsMediaChunk previousChunk, DrmInitData drmInitData, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        super(buildDataSource(dataSource, fullSegmentEncryptionKey, encryptionIv), dataSpec, hlsUrl.format, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.discontinuitySequenceNumber = discontinuitySequenceNumber;
        this.initDataSpec = initDataSpec;
        this.hlsUrl = hlsUrl;
        this.isMasterTimestampSource = isMasterTimestampSource;
        this.timestampAdjuster = timestampAdjuster;
        Extractor previousExtractor = null;
        if (previousChunk != null) {
            this.shouldSpliceIn = previousChunk.hlsUrl != hlsUrl;
            previousExtractor = (previousChunk.discontinuitySequenceNumber != discontinuitySequenceNumber || this.shouldSpliceIn) ? null : previousChunk.extractor;
        } else {
            this.shouldSpliceIn = false;
        }
        Pair<Extractor, Boolean> extractorData = extractorFactory.createExtractor(previousExtractor, dataSpec.uri, this.trackFormat, muxedCaptionFormats, drmInitData, timestampAdjuster);
        this.extractor = (Extractor) extractorData.first;
        this.isPackedAudioExtractor = ((Boolean) extractorData.second).booleanValue();
        this.reusingExtractor = this.extractor == previousExtractor;
        boolean z = this.reusingExtractor && initDataSpec != null;
        this.initLoadCompleted = z;
        if (!this.isPackedAudioExtractor) {
            this.id3Decoder = null;
            this.id3Data = null;
        } else if (previousChunk == null || previousChunk.id3Data == null) {
            this.id3Decoder = new Id3Decoder();
            this.id3Data = new ParsableByteArray(10);
        } else {
            this.id3Decoder = previousChunk.id3Decoder;
            this.id3Data = previousChunk.id3Data;
        }
        this.initDataSource = dataSource;
        this.uid = uidSource.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper output) {
        this.output = output;
        output.init(this.uid, this.shouldSpliceIn, this.reusingExtractor);
        if (!this.reusingExtractor) {
            this.extractor.init(output);
        }
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
        maybeLoadInitData();
        if (!this.loadCanceled) {
            loadMedia();
        }
    }

    private void maybeLoadInitData() throws IOException, InterruptedException {
        if (!this.initLoadCompleted && this.initDataSpec != null) {
            DataSpec initSegmentDataSpec = this.initDataSpec.subrange((long) this.initSegmentBytesLoaded);
            ExtractorInput input;
            try {
                input = new DefaultExtractorInput(this.initDataSource, initSegmentDataSpec.absoluteStreamPosition, this.initDataSource.open(initSegmentDataSpec));
                int result = 0;
                while (result == 0) {
                    if (!this.loadCanceled) {
                        result = this.extractor.read(input, null);
                    }
                }
                this.initSegmentBytesLoaded = (int) (input.getPosition() - this.initDataSpec.absoluteStreamPosition);
                Util.closeQuietly(this.dataSource);
                this.initLoadCompleted = true;
            } catch (Throwable th) {
                Util.closeQuietly(this.dataSource);
            }
        }
    }

    private void loadMedia() throws IOException, InterruptedException {
        DataSpec loadDataSpec;
        boolean skipLoadedBytes;
        if (this.isEncrypted) {
            loadDataSpec = this.dataSpec;
            skipLoadedBytes = this.bytesLoaded != 0;
        } else {
            loadDataSpec = this.dataSpec.subrange((long) this.bytesLoaded);
            skipLoadedBytes = false;
        }
        if (!this.isMasterTimestampSource) {
            this.timestampAdjuster.waitUntilInitialized();
        } else if (this.timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
            this.timestampAdjuster.setFirstSampleTimestampUs(this.startTimeUs);
        }
        ExtractorInput input;
        try {
            input = new DefaultExtractorInput(this.dataSource, loadDataSpec.absoluteStreamPosition, this.dataSource.open(loadDataSpec));
            if (this.isPackedAudioExtractor && !this.id3TimestampPeeked) {
                long id3Timestamp = peekId3PrivTimestamp(input);
                this.id3TimestampPeeked = true;
                this.output.setSampleOffsetUs(id3Timestamp != C.TIME_UNSET ? this.timestampAdjuster.adjustTsTimestamp(id3Timestamp) : this.startTimeUs);
            }
            if (skipLoadedBytes) {
                input.skipFully(this.bytesLoaded);
            }
            int result = 0;
            while (result == 0) {
                if (this.loadCanceled) {
                    break;
                }
                result = this.extractor.read(input, null);
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            Util.closeQuietly(this.dataSource);
            this.loadCompleted = true;
        } catch (Throwable th) {
            Util.closeQuietly(this.dataSource);
        }
    }

    private long peekId3PrivTimestamp(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        if (!input.peekFully(this.id3Data.data, 0, 10, true)) {
            return C.TIME_UNSET;
        }
        this.id3Data.reset(10);
        if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
            return C.TIME_UNSET;
        }
        this.id3Data.skipBytes(3);
        int id3Size = this.id3Data.readSynchSafeInt();
        int requiredCapacity = id3Size + 10;
        if (requiredCapacity > this.id3Data.capacity()) {
            byte[] data = this.id3Data.data;
            this.id3Data.reset(requiredCapacity);
            System.arraycopy(data, 0, this.id3Data.data, 0, 10);
        }
        if (!input.peekFully(this.id3Data.data, 10, id3Size, true)) {
            return C.TIME_UNSET;
        }
        Metadata metadata = this.id3Decoder.decode(this.id3Data.data, id3Size);
        if (metadata == null) {
            return C.TIME_UNSET;
        }
        int metadataLength = metadata.length();
        for (int i = 0; i < metadataLength; i++) {
            Entry frame = metadata.get(i);
            if (frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) frame;
                if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                    System.arraycopy(privFrame.privateData, 0, this.id3Data.data, 0, 8);
                    this.id3Data.reset(8);
                    return this.id3Data.readLong() & 8589934591L;
                }
            }
        }
        return C.TIME_UNSET;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        if (fullSegmentEncryptionKey != null) {
            return new Aes128DataSource(dataSource, fullSegmentEncryptionKey, encryptionIv);
        }
        return dataSource;
    }
}
