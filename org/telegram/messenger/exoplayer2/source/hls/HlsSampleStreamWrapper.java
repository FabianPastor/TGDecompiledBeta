package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.IOException;
import java.util.LinkedList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput.UpstreamFormatChangedListener;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.hls.HlsChunkSource.HlsChunkHolder;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

final class HlsSampleStreamWrapper implements org.telegram.messenger.exoplayer2.upstream.Loader.Callback<Chunk>, SequenceableLoader, ExtractorOutput, UpstreamFormatChangedListener {
    private static final int PRIMARY_TYPE_AUDIO = 2;
    private static final int PRIMARY_TYPE_NONE = 0;
    private static final int PRIMARY_TYPE_TEXT = 1;
    private static final int PRIMARY_TYPE_VIDEO = 3;
    private final Allocator allocator;
    private final Callback callback;
    private final HlsChunkSource chunkSource;
    private Format downstreamTrackFormat;
    private int enabledTrackCount;
    private final EventDispatcher eventDispatcher;
    private boolean[] groupEnabledStates;
    private final Handler handler = new Handler();
    private long lastSeekPositionUs;
    private final Loader loader = new Loader("Loader:HlsSampleStreamWrapper");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable = new Runnable() {
        public void run() {
            HlsSampleStreamWrapper.this.maybeFinishPrepare();
        }
    };
    private final LinkedList<HlsMediaChunk> mediaChunks = new LinkedList();
    private final int minLoadableRetryCount;
    private final Format muxedAudioFormat;
    private final HlsChunkHolder nextChunkHolder = new HlsChunkHolder();
    private long pendingResetPositionUs;
    private boolean prepared;
    private int primaryTrackGroupIndex;
    private boolean released;
    private final SparseArray<DefaultTrackOutput> sampleQueues = new SparseArray();
    private boolean sampleQueuesBuilt;
    private TrackGroupArray trackGroups;
    private final int trackType;
    private int upstreamChunkUid;

    public interface Callback extends org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback<HlsSampleStreamWrapper> {
        void onPlaylistRefreshRequired(HlsUrl hlsUrl);

        void onPrepared();
    }

    public HlsSampleStreamWrapper(int trackType, Callback callback, HlsChunkSource chunkSource, Allocator allocator, long positionUs, Format muxedAudioFormat, int minLoadableRetryCount, EventDispatcher eventDispatcher) {
        this.trackType = trackType;
        this.callback = callback;
        this.chunkSource = chunkSource;
        this.allocator = allocator;
        this.muxedAudioFormat = muxedAudioFormat;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.lastSeekPositionUs = positionUs;
        this.pendingResetPositionUs = positionUs;
    }

    public void continuePreparing() {
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        }
    }

    public void prepareSingleTrack(Format format) {
        track(0, -1).format(format);
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public boolean selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, boolean isFirstTrackSelection) {
        Assertions.checkState(this.prepared);
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                int group = ((HlsSampleStream) streams[i]).group;
                setTrackGroupEnabledState(group, false);
                ((DefaultTrackOutput) this.sampleQueues.valueAt(group)).disable();
                streams[i] = null;
            }
            i++;
        }
        TrackSelection primaryTrackSelection = null;
        boolean selectedNewTracks = false;
        i = 0;
        while (i < selections.length) {
            if (streams[i] == null && selections[i] != null) {
                TrackSelection selection = selections[i];
                group = this.trackGroups.indexOf(selection.getTrackGroup());
                setTrackGroupEnabledState(group, true);
                if (group == this.primaryTrackGroupIndex) {
                    primaryTrackSelection = selection;
                    this.chunkSource.selectTracks(selection);
                }
                streams[i] = new HlsSampleStream(this, group);
                streamResetFlags[i] = true;
                selectedNewTracks = true;
            }
            i++;
        }
        if (isFirstTrackSelection) {
            int sampleQueueCount = this.sampleQueues.size();
            for (i = 0; i < sampleQueueCount; i++) {
                if (!this.groupEnabledStates[i]) {
                    ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).disable();
                }
            }
            if (!(primaryTrackSelection == null || this.mediaChunks.isEmpty())) {
                primaryTrackSelection.updateSelectedTrack(0);
                if (primaryTrackSelection.getSelectedIndexInTrackGroup() != this.chunkSource.getTrackGroup().indexOf(((HlsMediaChunk) this.mediaChunks.getLast()).trackFormat)) {
                    seekTo(this.lastSeekPositionUs);
                }
            }
        }
        if (this.enabledTrackCount == 0) {
            this.chunkSource.reset();
            this.downstreamTrackFormat = null;
            this.mediaChunks.clear();
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            }
        }
        return selectedNewTracks;
    }

    public void seekTo(long positionUs) {
        this.lastSeekPositionUs = positionUs;
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        int sampleQueueCount = this.sampleQueues.size();
        for (int i = 0; i < sampleQueueCount; i++) {
            ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).reset(this.groupEnabledStates[i]);
        }
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long bufferedPositionUs = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = (HlsMediaChunk) this.mediaChunks.getLast();
        HlsMediaChunk lastCompletedMediaChunk = lastMediaChunk.isLoadCompleted() ? lastMediaChunk : this.mediaChunks.size() > 1 ? (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        int sampleQueueCount = this.sampleQueues.size();
        for (int i = 0; i < sampleQueueCount; i++) {
            bufferedPositionUs = Math.max(bufferedPositionUs, ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
        }
        return bufferedPositionUs;
    }

    public void release() {
        int sampleQueueCount = this.sampleQueues.size();
        for (int i = 0; i < sampleQueueCount; i++) {
            ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).disable();
        }
        this.loader.release();
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.chunkSource.setIsTimestampMaster(isTimestampMaster);
    }

    public void onPlaylistBlacklisted(HlsUrl url, long blacklistMs) {
        this.chunkSource.onPlaylistBlacklisted(url, blacklistMs);
    }

    boolean isReady(int group) {
        return this.loadingFinished || !(isPendingReset() || ((DefaultTrackOutput) this.sampleQueues.valueAt(group)).isEmpty());
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    int readData(int group, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
        if (isPendingReset()) {
            return -3;
        }
        while (this.mediaChunks.size() > 1 && finishedReadingChunk((HlsMediaChunk) this.mediaChunks.getFirst())) {
            this.mediaChunks.removeFirst();
        }
        HlsMediaChunk currentChunk = (HlsMediaChunk) this.mediaChunks.getFirst();
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.downstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.trackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.downstreamTrackFormat = trackFormat;
        return ((DefaultTrackOutput) this.sampleQueues.valueAt(group)).readData(formatHolder, buffer, requireFormat, this.loadingFinished, this.lastSeekPositionUs);
    }

    void skipToKeyframeBefore(int group, long timeUs) {
        ((DefaultTrackOutput) this.sampleQueues.valueAt(group)).skipToKeyframeBefore(timeUs);
    }

    private boolean finishedReadingChunk(HlsMediaChunk chunk) {
        int chunkUid = chunk.uid;
        int i = 0;
        while (i < this.sampleQueues.size()) {
            if (this.groupEnabledStates[i] && ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).peekSourceId() == chunkUid) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean continueLoading(long positionUs) {
        if (this.loadingFinished || this.loader.isLoading()) {
            return false;
        }
        HlsMediaChunk hlsMediaChunk;
        HlsChunkSource hlsChunkSource = this.chunkSource;
        if (this.mediaChunks.isEmpty()) {
            hlsMediaChunk = null;
        } else {
            hlsMediaChunk = (HlsMediaChunk) this.mediaChunks.getLast();
        }
        if (this.pendingResetPositionUs != C.TIME_UNSET) {
            positionUs = this.pendingResetPositionUs;
        }
        hlsChunkSource.getNextChunk(hlsMediaChunk, positionUs, this.nextChunkHolder);
        boolean endOfStream = this.nextChunkHolder.endOfStream;
        Chunk loadable = this.nextChunkHolder.chunk;
        HlsUrl playlistToLoad = this.nextChunkHolder.playlist;
        this.nextChunkHolder.clear();
        if (endOfStream) {
            this.loadingFinished = true;
            return true;
        } else if (loadable == null) {
            if (playlistToLoad != null) {
                this.callback.onPlaylistRefreshRequired(playlistToLoad);
            }
            return false;
        } else {
            if (isMediaChunk(loadable)) {
                this.pendingResetPositionUs = C.TIME_UNSET;
                HlsMediaChunk mediaChunk = (HlsMediaChunk) loadable;
                mediaChunk.init(this);
                this.mediaChunks.add(mediaChunk);
            }
            this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, this.loader.startLoading(loadable, this, this.minLoadableRetryCount));
            return true;
        }
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : ((HlsMediaChunk) this.mediaChunks.getLast()).endTimeUs;
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.chunkSource.onChunkLoadCompleted(loadable);
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (this.prepared) {
            this.callback.onContinueLoadingRequested(this);
            return;
        }
        continueLoading(this.lastSeekPositionUs);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            int sampleQueueCount = this.sampleQueues.size();
            for (int i = 0; i < sampleQueueCount; i++) {
                ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).reset(this.groupEnabledStates[i]);
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public int onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        boolean cancelable = !isMediaChunk || bytesLoaded == 0;
        boolean canceled = false;
        if (this.chunkSource.onChunkLoadError(loadable, cancelable, error)) {
            if (isMediaChunk) {
                Assertions.checkState(((HlsMediaChunk) this.mediaChunks.removeLast()) == loadable);
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
            canceled = true;
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, canceled);
        if (!canceled) {
            return 0;
        }
        if (this.prepared) {
            this.callback.onContinueLoadingRequested(this);
        } else {
            continueLoading(this.lastSeekPositionUs);
        }
        return 2;
    }

    public void init(int chunkUid, boolean shouldSpliceIn) {
        int i;
        this.upstreamChunkUid = chunkUid;
        for (i = 0; i < this.sampleQueues.size(); i++) {
            ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).sourceId(chunkUid);
        }
        if (shouldSpliceIn) {
            for (i = 0; i < this.sampleQueues.size(); i++) {
                ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).splice();
            }
        }
    }

    public DefaultTrackOutput track(int id, int type) {
        if (this.sampleQueues.indexOfKey(id) >= 0) {
            return (DefaultTrackOutput) this.sampleQueues.get(id);
        }
        DefaultTrackOutput trackOutput = new DefaultTrackOutput(this.allocator);
        trackOutput.setUpstreamFormatChangeListener(this);
        trackOutput.sourceId(this.upstreamChunkUid);
        this.sampleQueues.put(id, trackOutput);
        return trackOutput;
    }

    public void endTracks() {
        this.sampleQueuesBuilt = true;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void seekMap(SeekMap seekMap) {
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    private void maybeFinishPrepare() {
        if (!this.released && !this.prepared && this.sampleQueuesBuilt) {
            int sampleQueueCount = this.sampleQueues.size();
            int i = 0;
            while (i < sampleQueueCount) {
                if (((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getUpstreamFormat() != null) {
                    i++;
                } else {
                    return;
                }
            }
            buildTracks();
            this.prepared = true;
            this.callback.onPrepared();
        }
    }

    private void buildTracks() {
        int i;
        int primaryExtractorTrackType = 0;
        int primaryExtractorTrackIndex = -1;
        int extractorTrackCount = this.sampleQueues.size();
        for (i = 0; i < extractorTrackCount; i++) {
            int trackType;
            String sampleMimeType = ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getUpstreamFormat().sampleMimeType;
            if (MimeTypes.isVideo(sampleMimeType)) {
                trackType = 3;
            } else if (MimeTypes.isAudio(sampleMimeType)) {
                trackType = 2;
            } else if (MimeTypes.isText(sampleMimeType)) {
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
        TrackGroup chunkSourceTrackGroup = this.chunkSource.getTrackGroup();
        int chunkSourceTrackCount = chunkSourceTrackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.groupEnabledStates = new boolean[extractorTrackCount];
        TrackGroup[] trackGroups = new TrackGroup[extractorTrackCount];
        for (i = 0; i < extractorTrackCount; i++) {
            Format sampleFormat = ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getUpstreamFormat();
            if (i == primaryExtractorTrackIndex) {
                Format[] formats = new Format[chunkSourceTrackCount];
                for (int j = 0; j < chunkSourceTrackCount; j++) {
                    formats[j] = deriveFormat(chunkSourceTrackGroup.getFormat(j), sampleFormat);
                }
                trackGroups[i] = new TrackGroup(formats);
                this.primaryTrackGroupIndex = i;
            } else {
                Format trackFormat = (primaryExtractorTrackType == 3 && MimeTypes.isAudio(sampleFormat.sampleMimeType)) ? this.muxedAudioFormat : null;
                trackGroups[i] = new TrackGroup(deriveFormat(trackFormat, sampleFormat));
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroups);
    }

    private void setTrackGroupEnabledState(int group, boolean enabledState) {
        int i = 1;
        Assertions.checkState(this.groupEnabledStates[group] != enabledState);
        this.groupEnabledStates[group] = enabledState;
        int i2 = this.enabledTrackCount;
        if (!enabledState) {
            i = -1;
        }
        this.enabledTrackCount = i2 + i;
    }

    private static Format deriveFormat(Format containerFormat, Format sampleFormat) {
        if (containerFormat == null) {
            return sampleFormat;
        }
        String codecs = null;
        int sampleTrackType = MimeTypes.getTrackType(sampleFormat.sampleMimeType);
        if (sampleTrackType == 1) {
            codecs = getAudioCodecs(containerFormat.codecs);
        } else if (sampleTrackType == 2) {
            codecs = getVideoCodecs(containerFormat.codecs);
        }
        return sampleFormat.copyWithContainerInfo(containerFormat.id, codecs, containerFormat.bitrate, containerFormat.width, containerFormat.height, containerFormat.selectionFlags, containerFormat.language);
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private static String getAudioCodecs(String codecs) {
        return getCodecsOfType(codecs, 1);
    }

    private static String getVideoCodecs(String codecs) {
        return getCodecsOfType(codecs, 2);
    }

    private static String getCodecsOfType(String codecs, int trackType) {
        if (TextUtils.isEmpty(codecs)) {
            return null;
        }
        String[] codecArray = codecs.split("(\\s*,\\s*)|(\\s*$)");
        StringBuilder builder = new StringBuilder();
        for (String codec : codecArray) {
            if (trackType == MimeTypes.getTrackTypeOfCodec(codec)) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(codec);
            }
        }
        if (builder.length() > 0) {
            return builder.toString();
        }
        return null;
    }
}
