package org.telegram.messenger.exoplayer.hls;

import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.LoadControl;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.chunk.BaseChunkSampleSourceEventListener;
import org.telegram.messenger.exoplayer.chunk.Chunk;
import org.telegram.messenger.exoplayer.chunk.ChunkOperationHolder;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class HlsSampleSource implements SampleSource, SampleSourceReader, Callback {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final long NO_RESET_PENDING = Long.MIN_VALUE;
    private static final int PRIMARY_TYPE_AUDIO = 2;
    private static final int PRIMARY_TYPE_NONE = 0;
    private static final int PRIMARY_TYPE_TEXT = 1;
    private static final int PRIMARY_TYPE_VIDEO = 3;
    private final int bufferSizeContribution;
    private final ChunkOperationHolder chunkOperationHolder;
    private final HlsChunkSource chunkSource;
    private int[] chunkSourceTrackIndices;
    private long currentLoadStartTimeMs;
    private Chunk currentLoadable;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private TsChunk currentTsLoadable;
    private Format downstreamFormat;
    private MediaFormat[] downstreamMediaFormats;
    private long downstreamPositionUs;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private boolean[] extractorTrackEnabledStates;
    private int[] extractorTrackIndices;
    private final LinkedList<HlsExtractorWrapper> extractors;
    private long lastSeekPositionUs;
    private final LoadControl loadControl;
    private boolean loadControlRegistered;
    private Loader loader;
    private boolean loadingFinished;
    private final int minLoadableRetryCount;
    private boolean[] pendingDiscontinuities;
    private long pendingResetPositionUs;
    private boolean prepared;
    private TsChunk previousTsLoadable;
    private int remainingReleaseCount;
    private int trackCount;
    private boolean[] trackEnabledStates;
    private MediaFormat[] trackFormats;

    public interface EventListener extends BaseChunkSampleSourceEventListener {
    }

    public HlsSampleSource(HlsChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution) {
        this(chunkSource, loadControl, bufferSizeContribution, null, null, 0);
    }

    public HlsSampleSource(HlsChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this(chunkSource, loadControl, bufferSizeContribution, eventHandler, eventListener, eventSourceId, 3);
    }

    public HlsSampleSource(HlsChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution, Handler eventHandler, EventListener eventListener, int eventSourceId, int minLoadableRetryCount) {
        this.chunkSource = chunkSource;
        this.loadControl = loadControl;
        this.bufferSizeContribution = bufferSizeContribution;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.eventSourceId = eventSourceId;
        this.pendingResetPositionUs = Long.MIN_VALUE;
        this.extractors = new LinkedList();
        this.chunkOperationHolder = new ChunkOperationHolder();
    }

    public SampleSourceReader register() {
        this.remainingReleaseCount++;
        return this;
    }

    public boolean prepare(long positionUs) {
        if (this.prepared) {
            return true;
        }
        if (!this.chunkSource.prepare()) {
            return false;
        }
        if (!this.extractors.isEmpty()) {
            while (true) {
                HlsExtractorWrapper extractor = (HlsExtractorWrapper) this.extractors.getFirst();
                if (!extractor.isPrepared()) {
                    if (this.extractors.size() <= 1) {
                        break;
                    }
                    ((HlsExtractorWrapper) this.extractors.removeFirst()).clear();
                } else {
                    buildTracks(extractor);
                    this.prepared = true;
                    maybeStartLoading();
                    return true;
                }
            }
        }
        if (this.loader == null) {
            this.loader = new Loader("Loader:HLS");
            this.loadControl.register(this, this.bufferSizeContribution);
            this.loadControlRegistered = true;
        }
        if (!this.loader.isLoading()) {
            this.pendingResetPositionUs = positionUs;
            this.downstreamPositionUs = positionUs;
        }
        maybeStartLoading();
        return false;
    }

    public int getTrackCount() {
        Assertions.checkState(this.prepared);
        return this.trackCount;
    }

    public MediaFormat getFormat(int track) {
        Assertions.checkState(this.prepared);
        return this.trackFormats[track];
    }

    public void enable(int track, long positionUs) {
        Assertions.checkState(this.prepared);
        setTrackEnabledState(track, true);
        this.downstreamMediaFormats[track] = null;
        this.pendingDiscontinuities[track] = false;
        this.downstreamFormat = null;
        boolean wasLoadControlRegistered = this.loadControlRegistered;
        if (!this.loadControlRegistered) {
            this.loadControl.register(this, this.bufferSizeContribution);
            this.loadControlRegistered = true;
        }
        if (this.chunkSource.isLive()) {
            positionUs = 0;
        }
        int chunkSourceTrack = this.chunkSourceTrackIndices[track];
        if (chunkSourceTrack != -1 && chunkSourceTrack != this.chunkSource.getSelectedTrackIndex()) {
            this.chunkSource.selectTrack(chunkSourceTrack);
            seekToInternal(positionUs);
        } else if (this.enabledTrackCount == 1) {
            this.lastSeekPositionUs = positionUs;
            if (wasLoadControlRegistered && this.downstreamPositionUs == positionUs) {
                maybeStartLoading();
                return;
            }
            this.downstreamPositionUs = positionUs;
            restartFrom(positionUs);
        }
    }

    public void disable(int track) {
        Assertions.checkState(this.prepared);
        setTrackEnabledState(track, false);
        if (this.enabledTrackCount == 0) {
            this.chunkSource.reset();
            this.downstreamPositionUs = Long.MIN_VALUE;
            if (this.loadControlRegistered) {
                this.loadControl.unregister(this);
                this.loadControlRegistered = false;
            }
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
                return;
            }
            clearState();
            this.loadControl.trimAllocator();
        }
    }

    public boolean continueBuffering(int track, long playbackPositionUs) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[track]);
        this.downstreamPositionUs = playbackPositionUs;
        if (!this.extractors.isEmpty()) {
            discardSamplesForDisabledTracks(getCurrentExtractor(), this.downstreamPositionUs);
        }
        maybeStartLoading();
        if (this.loadingFinished) {
            return true;
        }
        if (isPendingReset() || this.extractors.isEmpty()) {
            return false;
        }
        int extractorIndex = 0;
        while (extractorIndex < this.extractors.size()) {
            HlsExtractorWrapper extractor = (HlsExtractorWrapper) this.extractors.get(extractorIndex);
            if (!extractor.isPrepared()) {
                break;
            } else if (extractor.hasSamples(this.extractorTrackIndices[track])) {
                return true;
            } else {
                extractorIndex++;
            }
        }
        return false;
    }

    public long readDiscontinuity(int track) {
        if (!this.pendingDiscontinuities[track]) {
            return Long.MIN_VALUE;
        }
        this.pendingDiscontinuities[track] = false;
        return this.lastSeekPositionUs;
    }

    public int readData(int track, long playbackPositionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder) {
        Assertions.checkState(this.prepared);
        this.downstreamPositionUs = playbackPositionUs;
        if (this.pendingDiscontinuities[track] || isPendingReset()) {
            return -2;
        }
        HlsExtractorWrapper extractor = getCurrentExtractor();
        if (!extractor.isPrepared()) {
            return -2;
        }
        Format format = extractor.format;
        if (!format.equals(this.downstreamFormat)) {
            notifyDownstreamFormatChanged(format, extractor.trigger, extractor.startTimeUs);
        }
        this.downstreamFormat = format;
        if (this.extractors.size() > 1) {
            extractor.configureSpliceTo((HlsExtractorWrapper) this.extractors.get(1));
        }
        int extractorTrack = this.extractorTrackIndices[track];
        int extractorIndex = 0;
        while (this.extractors.size() > extractorIndex + 1 && !extractor.hasSamples(extractorTrack)) {
            extractorIndex++;
            extractor = (HlsExtractorWrapper) this.extractors.get(extractorIndex);
            if (!extractor.isPrepared()) {
                return -2;
            }
        }
        MediaFormat mediaFormat = extractor.getMediaFormat(extractorTrack);
        if (mediaFormat != null) {
            if (mediaFormat.equals(this.downstreamMediaFormats[track])) {
                this.downstreamMediaFormats[track] = mediaFormat;
            } else {
                formatHolder.format = mediaFormat;
                this.downstreamMediaFormats[track] = mediaFormat;
                return -4;
            }
        }
        if (extractor.getSample(extractorTrack, sampleHolder)) {
            boolean decodeOnly = sampleHolder.timeUs < this.lastSeekPositionUs;
            sampleHolder.flags = (decodeOnly ? C.SAMPLE_FLAG_DECODE_ONLY : 0) | sampleHolder.flags;
            return -3;
        } else if (this.loadingFinished) {
            return -1;
        } else {
            return -2;
        }
    }

    public void maybeThrowError() throws IOException {
        if (this.currentLoadableException != null && this.currentLoadableExceptionCount > this.minLoadableRetryCount) {
            throw this.currentLoadableException;
        } else if (this.currentLoadable == null) {
            this.chunkSource.maybeThrowError();
        }
    }

    public void seekToUs(long positionUs) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.enabledTrackCount > 0);
        if (this.chunkSource.isLive()) {
            positionUs = 0;
        }
        long currentPositionUs = isPendingReset() ? this.pendingResetPositionUs : this.downstreamPositionUs;
        this.downstreamPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
        if (currentPositionUs != positionUs) {
            seekToInternal(positionUs);
        }
    }

    public long getBufferedPositionUs() {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.enabledTrackCount > 0);
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (this.loadingFinished) {
            return -3;
        }
        long largestParsedTimestampUs = ((HlsExtractorWrapper) this.extractors.getLast()).getLargestParsedTimestampUs();
        if (this.extractors.size() > 1) {
            largestParsedTimestampUs = Math.max(largestParsedTimestampUs, ((HlsExtractorWrapper) this.extractors.get(this.extractors.size() - 2)).getLargestParsedTimestampUs());
        }
        return largestParsedTimestampUs == Long.MIN_VALUE ? this.downstreamPositionUs : largestParsedTimestampUs;
    }

    public void release() {
        Assertions.checkState(this.remainingReleaseCount > 0);
        int i = this.remainingReleaseCount - 1;
        this.remainingReleaseCount = i;
        if (i == 0 && this.loader != null) {
            if (this.loadControlRegistered) {
                this.loadControl.unregister(this);
                this.loadControlRegistered = false;
            }
            this.loader.release();
            this.loader = null;
        }
    }

    public void onLoadCompleted(Loadable loadable) {
        boolean z;
        boolean z2 = true;
        if (loadable == this.currentLoadable) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        long now = SystemClock.elapsedRealtime();
        long loadDurationMs = now - this.currentLoadStartTimeMs;
        this.chunkSource.onChunkLoadCompleted(this.currentLoadable);
        if (isTsChunk(this.currentLoadable)) {
            if (this.currentLoadable != this.currentTsLoadable) {
                z2 = false;
            }
            Assertions.checkState(z2);
            this.previousTsLoadable = this.currentTsLoadable;
            notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentTsLoadable.type, this.currentTsLoadable.trigger, this.currentTsLoadable.format, this.currentTsLoadable.startTimeUs, this.currentTsLoadable.endTimeUs, now, loadDurationMs);
        } else {
            notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1, -1, now, loadDurationMs);
        }
        clearCurrentLoadable();
        maybeStartLoading();
    }

    public void onLoadCanceled(Loadable loadable) {
        notifyLoadCanceled(this.currentLoadable.bytesLoaded());
        if (this.enabledTrackCount > 0) {
            restartFrom(this.pendingResetPositionUs);
            return;
        }
        clearState();
        this.loadControl.trimAllocator();
    }

    public void onLoadError(Loadable loadable, IOException e) {
        if (this.chunkSource.onChunkLoadError(this.currentLoadable, e)) {
            if (this.previousTsLoadable == null && !isPendingReset()) {
                this.pendingResetPositionUs = this.lastSeekPositionUs;
            }
            clearCurrentLoadable();
        } else {
            this.currentLoadableException = e;
            this.currentLoadableExceptionCount++;
            this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        }
        notifyLoadError(e);
        maybeStartLoading();
    }

    private void buildTracks(HlsExtractorWrapper extractor) {
        int i;
        int primaryExtractorTrackType = 0;
        int primaryExtractorTrackIndex = -1;
        int extractorTrackCount = extractor.getTrackCount();
        for (i = 0; i < extractorTrackCount; i++) {
            int trackType;
            String mimeType = extractor.getMediaFormat(i).mimeType;
            if (MimeTypes.isVideo(mimeType)) {
                trackType = 3;
            } else if (MimeTypes.isAudio(mimeType)) {
                trackType = 2;
            } else if (MimeTypes.isText(mimeType)) {
                trackType = 1;
            } else {
                trackType = 0;
            }
            if (trackType > primaryExtractorTrackType) {
                primaryExtractorTrackType = trackType;
                primaryExtractorTrackIndex = i;
            } else if (trackType == primaryExtractorTrackType && primaryExtractorTrackIndex != -1) {
                primaryExtractorTrackIndex = -1;
            }
        }
        int chunkSourceTrackCount = this.chunkSource.getTrackCount();
        boolean expandPrimaryExtractorTrack = primaryExtractorTrackIndex != -1;
        this.trackCount = extractorTrackCount;
        if (expandPrimaryExtractorTrack) {
            this.trackCount += chunkSourceTrackCount - 1;
        }
        this.trackFormats = new MediaFormat[this.trackCount];
        this.trackEnabledStates = new boolean[this.trackCount];
        this.pendingDiscontinuities = new boolean[this.trackCount];
        this.downstreamMediaFormats = new MediaFormat[this.trackCount];
        this.chunkSourceTrackIndices = new int[this.trackCount];
        this.extractorTrackIndices = new int[this.trackCount];
        this.extractorTrackEnabledStates = new boolean[extractorTrackCount];
        long durationUs = this.chunkSource.getDurationUs();
        i = 0;
        int trackIndex = 0;
        while (i < extractorTrackCount) {
            int trackIndex2;
            MediaFormat format = extractor.getMediaFormat(i).copyWithDurationUs(durationUs);
            String language = null;
            if (MimeTypes.isAudio(format.mimeType)) {
                language = this.chunkSource.getMuxedAudioLanguage();
            } else if (MimeTypes.APPLICATION_EIA608.equals(format.mimeType)) {
                language = this.chunkSource.getMuxedCaptionLanguage();
            }
            if (i == primaryExtractorTrackIndex) {
                int j = 0;
                while (j < chunkSourceTrackCount) {
                    MediaFormat copyAsAdaptive;
                    this.extractorTrackIndices[trackIndex] = i;
                    this.chunkSourceTrackIndices[trackIndex] = j;
                    Variant fixedTrackVariant = this.chunkSource.getFixedTrackVariant(j);
                    MediaFormat[] mediaFormatArr = this.trackFormats;
                    trackIndex2 = trackIndex + 1;
                    if (fixedTrackVariant == null) {
                        copyAsAdaptive = format.copyAsAdaptive(null);
                    } else {
                        copyAsAdaptive = copyWithFixedTrackInfo(format, fixedTrackVariant.format, language);
                    }
                    mediaFormatArr[trackIndex] = copyAsAdaptive;
                    j++;
                    trackIndex = trackIndex2;
                }
                trackIndex2 = trackIndex;
            } else {
                this.extractorTrackIndices[trackIndex] = i;
                this.chunkSourceTrackIndices[trackIndex] = -1;
                trackIndex2 = trackIndex + 1;
                this.trackFormats[trackIndex] = format.copyWithLanguage(language);
            }
            i++;
            trackIndex = trackIndex2;
        }
    }

    private void setTrackEnabledState(int track, boolean enabledState) {
        boolean z;
        boolean z2 = false;
        int i = 1;
        if (this.trackEnabledStates[track] != enabledState) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        int extractorTrack = this.extractorTrackIndices[track];
        if (this.extractorTrackEnabledStates[extractorTrack] != enabledState) {
            z2 = true;
        }
        Assertions.checkState(z2);
        this.trackEnabledStates[track] = enabledState;
        this.extractorTrackEnabledStates[extractorTrack] = enabledState;
        int i2 = this.enabledTrackCount;
        if (!enabledState) {
            i = -1;
        }
        this.enabledTrackCount = i2 + i;
    }

    private static MediaFormat copyWithFixedTrackInfo(MediaFormat format, Format fixedTrackFormat, String languageOverride) {
        String language;
        int width = fixedTrackFormat.width == -1 ? -1 : fixedTrackFormat.width;
        int height = fixedTrackFormat.height == -1 ? -1 : fixedTrackFormat.height;
        if (languageOverride == null) {
            language = fixedTrackFormat.language;
        } else {
            language = languageOverride;
        }
        return format.copyWithFixedTrackInfo(fixedTrackFormat.id, fixedTrackFormat.bitrate, width, height, language);
    }

    private void seekToInternal(long positionUs) {
        this.lastSeekPositionUs = positionUs;
        this.downstreamPositionUs = positionUs;
        Arrays.fill(this.pendingDiscontinuities, true);
        this.chunkSource.seek();
        restartFrom(positionUs);
    }

    private HlsExtractorWrapper getCurrentExtractor() {
        HlsExtractorWrapper extractor = (HlsExtractorWrapper) this.extractors.getFirst();
        while (this.extractors.size() > 1 && !haveSamplesForEnabledTracks(extractor)) {
            ((HlsExtractorWrapper) this.extractors.removeFirst()).clear();
            extractor = (HlsExtractorWrapper) this.extractors.getFirst();
        }
        return extractor;
    }

    private void discardSamplesForDisabledTracks(HlsExtractorWrapper extractor, long timeUs) {
        if (extractor.isPrepared()) {
            for (int i = 0; i < this.extractorTrackEnabledStates.length; i++) {
                if (!this.extractorTrackEnabledStates[i]) {
                    extractor.discardUntil(i, timeUs);
                }
            }
        }
    }

    private boolean haveSamplesForEnabledTracks(HlsExtractorWrapper extractor) {
        if (!extractor.isPrepared()) {
            return false;
        }
        int i = 0;
        while (i < this.extractorTrackEnabledStates.length) {
            if (this.extractorTrackEnabledStates[i] && extractor.hasSamples(i)) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void restartFrom(long positionUs) {
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        clearState();
        maybeStartLoading();
    }

    private void clearState() {
        for (int i = 0; i < this.extractors.size(); i++) {
            ((HlsExtractorWrapper) this.extractors.get(i)).clear();
        }
        this.extractors.clear();
        clearCurrentLoadable();
        this.previousTsLoadable = null;
    }

    private void clearCurrentLoadable() {
        this.currentTsLoadable = null;
        this.currentLoadable = null;
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = 0;
    }

    private void maybeStartLoading() {
        long now = SystemClock.elapsedRealtime();
        long nextLoadPositionUs = getNextLoadPositionUs();
        boolean isBackedOff = this.currentLoadableException != null;
        boolean loadingOrBackedOff = this.loader.isLoading() || isBackedOff;
        boolean nextLoader = this.loadControl.update(this, this.downstreamPositionUs, nextLoadPositionUs, loadingOrBackedOff);
        if (isBackedOff) {
            if (now - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                this.currentLoadableException = null;
                this.loader.startLoading(this.currentLoadable, this);
            }
        } else if (!this.loader.isLoading() && nextLoader) {
            if (!this.prepared || this.enabledTrackCount != 0) {
                long j;
                HlsChunkSource hlsChunkSource = this.chunkSource;
                TsChunk tsChunk = this.previousTsLoadable;
                if (this.pendingResetPositionUs != Long.MIN_VALUE) {
                    j = this.pendingResetPositionUs;
                } else {
                    j = this.downstreamPositionUs;
                }
                hlsChunkSource.getChunkOperation(tsChunk, j, this.chunkOperationHolder);
                boolean endOfStream = this.chunkOperationHolder.endOfStream;
                Chunk nextLoadable = this.chunkOperationHolder.chunk;
                this.chunkOperationHolder.clear();
                if (endOfStream) {
                    this.loadingFinished = true;
                    this.loadControl.update(this, this.downstreamPositionUs, -1, false);
                } else if (nextLoadable != null) {
                    this.currentLoadStartTimeMs = now;
                    this.currentLoadable = nextLoadable;
                    if (isTsChunk(this.currentLoadable)) {
                        TsChunk tsChunk2 = (TsChunk) this.currentLoadable;
                        if (isPendingReset()) {
                            this.pendingResetPositionUs = Long.MIN_VALUE;
                        }
                        HlsExtractorWrapper extractorWrapper = tsChunk2.extractorWrapper;
                        if (this.extractors.isEmpty() || this.extractors.getLast() != extractorWrapper) {
                            extractorWrapper.init(this.loadControl.getAllocator());
                            this.extractors.addLast(extractorWrapper);
                        }
                        notifyLoadStarted(tsChunk2.dataSpec.length, tsChunk2.type, tsChunk2.trigger, tsChunk2.format, tsChunk2.startTimeUs, tsChunk2.endTimeUs);
                        this.currentTsLoadable = tsChunk2;
                    } else {
                        notifyLoadStarted(this.currentLoadable.dataSpec.length, this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1, -1);
                    }
                    this.loader.startLoading(this.currentLoadable, this);
                }
            }
        }
    }

    private long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (this.loadingFinished || (this.prepared && this.enabledTrackCount == 0)) {
            return -1;
        }
        return this.currentTsLoadable != null ? this.currentTsLoadable.endTimeUs : this.previousTsLoadable.endTimeUs;
    }

    private boolean isTsChunk(Chunk chunk) {
        return chunk instanceof TsChunk;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != Long.MIN_VALUE;
    }

    private long getRetryDelayMillis(long errorCount) {
        return Math.min((errorCount - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    long usToMs(long timeUs) {
        return timeUs / 1000;
    }

    private void notifyLoadStarted(long length, int type, int trigger, Format format, long mediaStartTimeUs, long mediaEndTimeUs) {
        if (this.eventHandler != null && this.eventListener != null) {
            final long j = length;
            final int i = type;
            final int i2 = trigger;
            final Format format2 = format;
            final long j2 = mediaStartTimeUs;
            final long j3 = mediaEndTimeUs;
            this.eventHandler.post(new Runnable() {
                public void run() {
                    HlsSampleSource.this.eventListener.onLoadStarted(HlsSampleSource.this.eventSourceId, j, i, i2, format2, HlsSampleSource.this.usToMs(j2), HlsSampleSource.this.usToMs(j3));
                }
            });
        }
    }

    private void notifyLoadCompleted(long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs) {
        if (this.eventHandler != null && this.eventListener != null) {
            final long j = bytesLoaded;
            final int i = type;
            final int i2 = trigger;
            final Format format2 = format;
            final long j2 = mediaStartTimeUs;
            final long j3 = mediaEndTimeUs;
            final long j4 = elapsedRealtimeMs;
            final long j5 = loadDurationMs;
            this.eventHandler.post(new Runnable() {
                public void run() {
                    HlsSampleSource.this.eventListener.onLoadCompleted(HlsSampleSource.this.eventSourceId, j, i, i2, format2, HlsSampleSource.this.usToMs(j2), HlsSampleSource.this.usToMs(j3), j4, j5);
                }
            });
        }
    }

    private void notifyLoadCanceled(final long bytesLoaded) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    HlsSampleSource.this.eventListener.onLoadCanceled(HlsSampleSource.this.eventSourceId, bytesLoaded);
                }
            });
        }
    }

    private void notifyLoadError(final IOException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    HlsSampleSource.this.eventListener.onLoadError(HlsSampleSource.this.eventSourceId, e);
                }
            });
        }
    }

    private void notifyDownstreamFormatChanged(Format format, int trigger, long positionUs) {
        if (this.eventHandler != null && this.eventListener != null) {
            final Format format2 = format;
            final int i = trigger;
            final long j = positionUs;
            this.eventHandler.post(new Runnable() {
                public void run() {
                    HlsSampleSource.this.eventListener.onDownstreamFormatChanged(HlsSampleSource.this.eventSourceId, format2, i, HlsSampleSource.this.usToMs(j));
                }
            });
        }
    }
}
