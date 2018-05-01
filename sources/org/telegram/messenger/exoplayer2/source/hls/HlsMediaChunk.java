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

    public HlsMediaChunk(HlsExtractorFactory hlsExtractorFactory, DataSource dataSource, DataSpec dataSpec, DataSpec dataSpec2, HlsUrl hlsUrl, List<Format> list, int i, Object obj, long j, long j2, int i2, int i3, boolean z, TimestampAdjuster timestampAdjuster, HlsMediaChunk hlsMediaChunk, DrmInitData drmInitData, byte[] bArr, byte[] bArr2) {
        Extractor extractor;
        DataSpec dataSpec3;
        DataSpec dataSpec4 = dataSpec2;
        HlsUrl hlsUrl2 = hlsUrl;
        int i4 = i3;
        HlsMediaChunk hlsMediaChunk2 = hlsMediaChunk;
        super(buildDataSource(dataSource, bArr, bArr2), dataSpec, hlsUrl2.format, i, obj, j, j2, i2);
        this.discontinuitySequenceNumber = i4;
        this.initDataSpec = dataSpec4;
        this.hlsUrl = hlsUrl2;
        this.isMasterTimestampSource = z;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        this.timestampAdjuster = timestampAdjuster2;
        boolean z2 = true;
        if (hlsMediaChunk2 != null) {
            Extractor extractor2;
            r11.shouldSpliceIn = hlsMediaChunk2.hlsUrl != hlsUrl2;
            if (hlsMediaChunk2.discontinuitySequenceNumber == i4) {
                if (!r11.shouldSpliceIn) {
                    extractor2 = hlsMediaChunk2.extractor;
                    extractor = extractor2;
                    dataSpec3 = dataSpec;
                }
            }
            extractor2 = null;
            extractor = extractor2;
            dataSpec3 = dataSpec;
        } else {
            r11.shouldSpliceIn = false;
            dataSpec3 = dataSpec;
            extractor = null;
        }
        Pair createExtractor = hlsExtractorFactory.createExtractor(extractor, dataSpec3.uri, r11.trackFormat, list, drmInitData, timestampAdjuster2);
        r11.extractor = (Extractor) createExtractor.first;
        r11.isPackedAudioExtractor = ((Boolean) createExtractor.second).booleanValue();
        r11.reusingExtractor = r11.extractor == extractor;
        if (!r11.reusingExtractor || dataSpec4 == null) {
            z2 = false;
        }
        r11.initLoadCompleted = z2;
        if (!r11.isPackedAudioExtractor) {
            r11.id3Decoder = null;
            r11.id3Data = null;
        } else if (hlsMediaChunk2 == null || hlsMediaChunk2.id3Data == null) {
            r11.id3Decoder = new Id3Decoder();
            r11.id3Data = new ParsableByteArray(10);
        } else {
            r11.id3Decoder = hlsMediaChunk2.id3Decoder;
            r11.id3Data = hlsMediaChunk2.id3Data;
        }
        r11.initDataSource = dataSource;
        r11.uid = uidSource.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.output = hlsSampleStreamWrapper;
        hlsSampleStreamWrapper.init(this.uid, this.shouldSpliceIn, this.reusingExtractor);
        if (!this.reusingExtractor) {
            this.extractor.init(hlsSampleStreamWrapper);
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
                DataSpec subrange = this.initDataSpec.subrange((long) this.initSegmentBytesLoaded);
                ExtractorInput defaultExtractorInput;
                try {
                    defaultExtractorInput = new DefaultExtractorInput(this.initDataSource, subrange.absoluteStreamPosition, this.initDataSource.open(subrange));
                    int i = 0;
                    while (i == 0) {
                        if (this.loadCanceled) {
                            break;
                        }
                        i = this.extractor.read(defaultExtractorInput, null);
                    }
                    this.initSegmentBytesLoaded = (int) (defaultExtractorInput.getPosition() - this.initDataSpec.absoluteStreamPosition);
                    Util.closeQuietly(this.dataSource);
                    this.initLoadCompleted = true;
                } catch (Throwable th) {
                    Util.closeQuietly(this.dataSource);
                }
            }
        }
    }

    private void loadMedia() throws IOException, InterruptedException {
        boolean z;
        int i = 0;
        DataSpec dataSpec;
        ExtractorInput defaultExtractorInput;
        if (this.isEncrypted) {
            dataSpec = this.dataSpec;
            if (this.bytesLoaded != 0) {
                z = true;
                if (!this.isMasterTimestampSource) {
                    this.timestampAdjuster.waitUntilInitialized();
                } else if (this.timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
                    this.timestampAdjuster.setFirstSampleTimestampUs(this.startTimeUs);
                }
                defaultExtractorInput = new DefaultExtractorInput(this.dataSource, dataSpec.absoluteStreamPosition, this.dataSource.open(dataSpec));
                if (this.isPackedAudioExtractor && !this.id3TimestampPeeked) {
                    long peekId3PrivTimestamp = peekId3PrivTimestamp(defaultExtractorInput);
                    this.id3TimestampPeeked = true;
                    this.output.setSampleOffsetUs(peekId3PrivTimestamp == C0542C.TIME_UNSET ? this.timestampAdjuster.adjustTsTimestamp(peekId3PrivTimestamp) : this.startTimeUs);
                }
                if (z) {
                    defaultExtractorInput.skipFully(this.bytesLoaded);
                }
                while (i == 0) {
                    if (!this.loadCanceled) {
                        break;
                    }
                    i = this.extractor.read(defaultExtractorInput, null);
                }
                this.bytesLoaded = (int) (defaultExtractorInput.getPosition() - this.dataSpec.absoluteStreamPosition);
                Util.closeQuietly(this.dataSource);
                this.loadCompleted = true;
            }
        }
        dataSpec = this.dataSpec.subrange((long) this.bytesLoaded);
        z = false;
        if (!this.isMasterTimestampSource) {
            this.timestampAdjuster.waitUntilInitialized();
        } else if (this.timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
            this.timestampAdjuster.setFirstSampleTimestampUs(this.startTimeUs);
        }
        try {
            defaultExtractorInput = new DefaultExtractorInput(this.dataSource, dataSpec.absoluteStreamPosition, this.dataSource.open(dataSpec));
            long peekId3PrivTimestamp2 = peekId3PrivTimestamp(defaultExtractorInput);
            this.id3TimestampPeeked = true;
            if (peekId3PrivTimestamp2 == C0542C.TIME_UNSET) {
            }
            this.output.setSampleOffsetUs(peekId3PrivTimestamp2 == C0542C.TIME_UNSET ? this.timestampAdjuster.adjustTsTimestamp(peekId3PrivTimestamp2) : this.startTimeUs);
            if (z) {
                defaultExtractorInput.skipFully(this.bytesLoaded);
            }
            while (i == 0) {
                if (!this.loadCanceled) {
                    break;
                }
                i = this.extractor.read(defaultExtractorInput, null);
            }
            this.bytesLoaded = (int) (defaultExtractorInput.getPosition() - this.dataSpec.absoluteStreamPosition);
            Util.closeQuietly(this.dataSource);
            this.loadCompleted = true;
        } catch (Throwable th) {
            Util.closeQuietly(this.dataSource);
        }
    }

    private long peekId3PrivTimestamp(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.resetPeekPosition();
        if (!extractorInput.peekFully(this.id3Data.data, 0, 10, true)) {
            return C0542C.TIME_UNSET;
        }
        this.id3Data.reset(10);
        if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
            return C0542C.TIME_UNSET;
        }
        this.id3Data.skipBytes(3);
        int readSynchSafeInt = this.id3Data.readSynchSafeInt();
        int i = readSynchSafeInt + 10;
        if (i > this.id3Data.capacity()) {
            Object obj = this.id3Data.data;
            this.id3Data.reset(i);
            System.arraycopy(obj, 0, this.id3Data.data, 0, 10);
        }
        if (extractorInput.peekFully(this.id3Data.data, 10, readSynchSafeInt, true) == null) {
            return C0542C.TIME_UNSET;
        }
        extractorInput = this.id3Decoder.decode(this.id3Data.data, readSynchSafeInt);
        if (extractorInput == null) {
            return C0542C.TIME_UNSET;
        }
        readSynchSafeInt = extractorInput.length();
        for (int i2 = 0; i2 < readSynchSafeInt; i2++) {
            Entry entry = extractorInput.get(i2);
            if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                    System.arraycopy(privFrame.privateData, 0, this.id3Data.data, 0, 8);
                    this.id3Data.reset(8);
                    return this.id3Data.readLong() & 8589934591L;
                }
            }
        }
        return C0542C.TIME_UNSET;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] bArr, byte[] bArr2) {
        return bArr != null ? new Aes128DataSource(dataSource, bArr, bArr2) : dataSource;
    }
}
