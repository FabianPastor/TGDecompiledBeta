package org.telegram.messenger.exoplayer2.source.hls;

import android.util.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.C0542C;
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
        DataSpec dataSpec2 = initDataSpec;
        HlsUrl hlsUrl2 = hlsUrl;
        int i = discontinuitySequenceNumber;
        HlsMediaChunk hlsMediaChunk = previousChunk;
        super(buildDataSource(dataSource, fullSegmentEncryptionKey, encryptionIv), dataSpec, hlsUrl2.format, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.discontinuitySequenceNumber = i;
        this.initDataSpec = dataSpec2;
        this.hlsUrl = hlsUrl2;
        this.isMasterTimestampSource = isMasterTimestampSource;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        this.timestampAdjuster = timestampAdjuster2;
        Extractor previousExtractor = null;
        if (hlsMediaChunk != null) {
            Extractor extractor;
            r11.shouldSpliceIn = hlsMediaChunk.hlsUrl != hlsUrl2;
            if (hlsMediaChunk.discontinuitySequenceNumber == i) {
                if (!r11.shouldSpliceIn) {
                    extractor = hlsMediaChunk.extractor;
                    previousExtractor = extractor;
                }
            }
            extractor = null;
            previousExtractor = extractor;
        } else {
            r11.shouldSpliceIn = false;
        }
        Extractor previousExtractor2 = previousExtractor;
        Extractor previousExtractor3 = previousExtractor2;
        boolean z = false;
        Pair<Extractor, Boolean> extractorData = extractorFactory.createExtractor(previousExtractor2, dataSpec.uri, r11.trackFormat, muxedCaptionFormats, drmInitData, timestampAdjuster2);
        r11.extractor = (Extractor) extractorData.first;
        r11.isPackedAudioExtractor = ((Boolean) extractorData.second).booleanValue();
        r11.reusingExtractor = r11.extractor == previousExtractor3 ? true : z;
        boolean z2 = (!r11.reusingExtractor || dataSpec2 == null) ? z : true;
        r11.initLoadCompleted = z2;
        if (!r11.isPackedAudioExtractor) {
            r11.id3Decoder = null;
            r11.id3Data = null;
        } else if (hlsMediaChunk == null || hlsMediaChunk.id3Data == null) {
            r11.id3Decoder = new Id3Decoder();
            r11.id3Data = new ParsableByteArray(10);
        } else {
            r11.id3Decoder = hlsMediaChunk.id3Decoder;
            r11.id3Data = hlsMediaChunk.id3Data;
        }
        r11.initDataSource = dataSource;
        r11.uid = uidSource.getAndIncrement();
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
        if (!this.initLoadCompleted) {
            if (this.initDataSpec != null) {
                DataSpec initSegmentDataSpec = this.initDataSpec.subrange((long) this.initSegmentBytesLoaded);
                DefaultExtractorInput input;
                try {
                    input = new DefaultExtractorInput(this.initDataSource, initSegmentDataSpec.absoluteStreamPosition, this.initDataSource.open(initSegmentDataSpec));
                    int result = 0;
                    while (result == 0) {
                        if (this.loadCanceled) {
                            break;
                        }
                        result = this.extractor.read(input, null);
                    }
                    this.initSegmentBytesLoaded = (int) (input.getPosition() - this.initDataSpec.absoluteStreamPosition);
                    Util.closeQuietly(this.dataSource);
                    this.initLoadCompleted = true;
                } catch (Throwable th) {
                    Util.closeQuietly(this.dataSource);
                }
            }
        }
    }

    private void loadMedia() throws IOException, InterruptedException {
        DataSpec loadDataSpec;
        boolean skipLoadedBytes;
        int result = 0;
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
        ExtractorInput defaultExtractorInput;
        try {
            defaultExtractorInput = new DefaultExtractorInput(this.dataSource, loadDataSpec.absoluteStreamPosition, this.dataSource.open(loadDataSpec));
            if (this.isPackedAudioExtractor && !this.id3TimestampPeeked) {
                long id3Timestamp = peekId3PrivTimestamp(defaultExtractorInput);
                this.id3TimestampPeeked = true;
                this.output.setSampleOffsetUs(id3Timestamp != C0542C.TIME_UNSET ? this.timestampAdjuster.adjustTsTimestamp(id3Timestamp) : this.startTimeUs);
            }
            if (skipLoadedBytes) {
                defaultExtractorInput.skipFully(this.bytesLoaded);
            }
            while (result == 0) {
                if (this.loadCanceled) {
                    break;
                }
                result = this.extractor.read(defaultExtractorInput, null);
            }
            this.bytesLoaded = (int) (defaultExtractorInput.getPosition() - this.dataSpec.absoluteStreamPosition);
            Util.closeQuietly(this.dataSource);
            this.loadCompleted = true;
        } catch (Throwable th) {
            Util.closeQuietly(this.dataSource);
        }
    }

    private long peekId3PrivTimestamp(ExtractorInput input) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        input.resetPeekPosition();
        if (!extractorInput.peekFully(this.id3Data.data, 0, 10, true)) {
            return C0542C.TIME_UNSET;
        }
        r0.id3Data.reset(10);
        if (r0.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
            return C0542C.TIME_UNSET;
        }
        r0.id3Data.skipBytes(3);
        int id3Size = r0.id3Data.readSynchSafeInt();
        int requiredCapacity = id3Size + 10;
        if (requiredCapacity > r0.id3Data.capacity()) {
            byte[] data = r0.id3Data.data;
            r0.id3Data.reset(requiredCapacity);
            System.arraycopy(data, 0, r0.id3Data.data, 0, 10);
        }
        if (!extractorInput.peekFully(r0.id3Data.data, 10, id3Size, true)) {
            return C0542C.TIME_UNSET;
        }
        Metadata metadata = r0.id3Decoder.decode(r0.id3Data.data, id3Size);
        if (metadata == null) {
            return C0542C.TIME_UNSET;
        }
        int metadataLength = metadata.length();
        for (int i = 0; i < metadataLength; i++) {
            Entry frame = metadata.get(i);
            if (frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) frame;
                if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                    System.arraycopy(privFrame.privateData, 0, r0.id3Data.data, 0, 8);
                    r0.id3Data.reset(8);
                    return r0.id3Data.readLong() & 8589934591L;
                }
            }
        }
        return C0542C.TIME_UNSET;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        if (fullSegmentEncryptionKey != null) {
            return new Aes128DataSource(dataSource, fullSegmentEncryptionKey, encryptionIv);
        }
        return dataSource;
    }
}
