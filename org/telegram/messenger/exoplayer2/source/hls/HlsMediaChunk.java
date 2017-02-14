package org.telegram.messenger.exoplayer2.source.hls;

import android.text.TextUtils;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsMediaChunk extends MediaChunk {
    private static final String AAC_FILE_EXTENSION = ".aac";
    private static final String AC3_FILE_EXTENSION = ".ac3";
    private static final String EC3_FILE_EXTENSION = ".ec3";
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final String MP4_FILE_EXTENSION = ".mp4";
    private static final AtomicInteger UID_SOURCE = new AtomicInteger();
    private static final String VTT_FILE_EXTENSION = ".vtt";
    private static final String WEBVTT_FILE_EXTENSION = ".webvtt";
    private long adjustedEndTimeUs;
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    private Extractor extractor;
    private HlsSampleStreamWrapper extractorOutput;
    public final HlsUrl hlsUrl;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private boolean initLoadCompleted;
    private int initSegmentBytesLoaded;
    private final boolean isEncrypted = (this.dataSource instanceof Aes128DataSource);
    private final boolean isMasterTimestampSource;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private final HlsMediaChunk previousChunk;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;

    public HlsMediaChunk(DataSource dataSource, DataSpec dataSpec, DataSpec initDataSpec, HlsUrl hlsUrl, int trackSelectionReason, Object trackSelectionData, Segment segment, int chunkIndex, boolean isMasterTimestampSource, TimestampAdjuster timestampAdjuster, HlsMediaChunk previousChunk, byte[] encryptionKey, byte[] encryptionIv) {
        super(buildDataSource(dataSource, encryptionKey, encryptionIv), dataSpec, hlsUrl.format, trackSelectionReason, trackSelectionData, segment.startTimeUs, segment.durationUs + segment.startTimeUs, chunkIndex);
        this.initDataSpec = initDataSpec;
        this.hlsUrl = hlsUrl;
        this.isMasterTimestampSource = isMasterTimestampSource;
        this.timestampAdjuster = timestampAdjuster;
        this.previousChunk = previousChunk;
        this.initDataSource = dataSource;
        this.discontinuitySequenceNumber = segment.discontinuitySequenceNumber;
        this.adjustedEndTimeUs = this.endTimeUs;
        this.uid = UID_SOURCE.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper output) {
        this.extractorOutput = output;
        int i = this.uid;
        boolean z = (this.previousChunk == null || this.previousChunk.hlsUrl == this.hlsUrl) ? false : true;
        output.init(i, z);
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
        if (this.extractor == null) {
            this.extractor = buildExtractor();
        }
        maybeLoadInitData();
        if (!this.loadCanceled) {
            loadMedia();
        }
    }

    private Extractor buildExtractor() {
        boolean needNewExtractor;
        Extractor extractor;
        if (this.previousChunk != null && this.previousChunk.discontinuitySequenceNumber == this.discontinuitySequenceNumber && this.trackFormat == this.previousChunk.trackFormat) {
            needNewExtractor = false;
        } else {
            needNewExtractor = true;
        }
        boolean usingNewExtractor = true;
        String lastPathSegment = this.dataSpec.uri.getLastPathSegment();
        if (lastPathSegment.endsWith(AAC_FILE_EXTENSION)) {
            extractor = new AdtsExtractor(this.startTimeUs);
        } else if (lastPathSegment.endsWith(AC3_FILE_EXTENSION) || lastPathSegment.endsWith(EC3_FILE_EXTENSION)) {
            extractor = new Ac3Extractor(this.startTimeUs);
        } else if (lastPathSegment.endsWith(MP3_FILE_EXTENSION)) {
            extractor = new Mp3Extractor(this.startTimeUs);
        } else if (lastPathSegment.endsWith(WEBVTT_FILE_EXTENSION) || lastPathSegment.endsWith(VTT_FILE_EXTENSION)) {
            extractor = new WebvttExtractor(this.trackFormat.language, this.timestampAdjuster);
        } else if (!needNewExtractor) {
            usingNewExtractor = false;
            extractor = this.previousChunk.extractor;
        } else if (lastPathSegment.endsWith(MP4_FILE_EXTENSION)) {
            extractor = new FragmentedMp4Extractor(0, this.timestampAdjuster);
        } else {
            int esReaderFactoryFlags = 0;
            String codecs = this.trackFormat.codecs;
            if (!TextUtils.isEmpty(codecs)) {
                if (!MimeTypes.AUDIO_AAC.equals(MimeTypes.getAudioMediaMimeType(codecs))) {
                    esReaderFactoryFlags = 0 | 2;
                }
                if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(codecs))) {
                    esReaderFactoryFlags |= 4;
                }
            }
            extractor = new TsExtractor(this.timestampAdjuster, new DefaultTsPayloadReaderFactory(esReaderFactoryFlags), true);
        }
        if (usingNewExtractor) {
            extractor.init(this.extractorOutput);
        }
        return extractor;
    }

    private void maybeLoadInitData() throws IOException, InterruptedException {
        ExtractorInput input;
        if (this.previousChunk != null && this.previousChunk.extractor == this.extractor && !this.initLoadCompleted && this.initDataSpec != null) {
            DataSpec initSegmentDataSpec = Util.getRemainderDataSpec(this.initDataSpec, this.initSegmentBytesLoaded);
            try {
                input = new DefaultExtractorInput(this.initDataSource, initSegmentDataSpec.absoluteStreamPosition, this.initDataSource.open(initSegmentDataSpec));
                int result = 0;
                while (result == 0) {
                    if (!this.loadCanceled) {
                        result = this.extractor.read(input, null);
                    }
                }
                this.initSegmentBytesLoaded += (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
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
        ExtractorInput input;
        if (this.isEncrypted) {
            loadDataSpec = this.dataSpec;
            skipLoadedBytes = this.bytesLoaded != 0;
        } else {
            loadDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
            skipLoadedBytes = false;
        }
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
            Util.closeQuietly(this.dataSource);
            this.loadCompleted = true;
        } catch (Throwable th) {
            Util.closeQuietly(this.dataSource);
        }
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] encryptionKey, byte[] encryptionIv) {
        return (encryptionKey == null || encryptionIv == null) ? dataSource : new Aes128DataSource(dataSource, encryptionKey, encryptionIv);
    }
}
