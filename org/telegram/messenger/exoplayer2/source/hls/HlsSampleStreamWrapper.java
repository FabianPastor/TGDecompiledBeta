package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
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
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsSampleStreamWrapper implements ExtractorOutput, UpstreamFormatChangedListener, SequenceableLoader, org.telegram.messenger.exoplayer2.upstream.Loader.Callback<Chunk>, ReleaseCallback {
    private static final int PRIMARY_TYPE_AUDIO = 2;
    private static final int PRIMARY_TYPE_NONE = 0;
    private static final int PRIMARY_TYPE_TEXT = 1;
    private static final int PRIMARY_TYPE_VIDEO = 3;
    private static final String TAG = "HlsSampleStreamWrapper";
    private final Allocator allocator;
    private int audioSampleQueueIndex = -1;
    private boolean audioSampleQueueMappingDone;
    private final Callback callback;
    private final HlsChunkSource chunkSource;
    private Format downstreamTrackFormat;
    private int enabledTrackGroupCount;
    private final EventDispatcher eventDispatcher;
    private final Handler handler = new Handler();
    private boolean haveAudioVideoSampleQueues;
    private long lastSeekPositionUs;
    private final Loader loader = new Loader("Loader:HlsSampleStreamWrapper");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable = new Runnable() {
        public void run() {
            HlsSampleStreamWrapper.this.maybeFinishPrepare();
        }
    };
    private final ArrayList<HlsMediaChunk> mediaChunks = new ArrayList();
    private final int minLoadableRetryCount;
    private final Format muxedAudioFormat;
    private final HlsChunkHolder nextChunkHolder = new HlsChunkHolder();
    private final Runnable onTracksEndedRunnable = new Runnable() {
        public void run() {
            HlsSampleStreamWrapper.this.onTracksEnded();
        }
    };
    private long pendingResetPositionUs;
    private boolean pendingResetUpstreamFormats;
    private boolean prepared;
    private int primaryTrackGroupIndex;
    private boolean released;
    private long sampleOffsetUs;
    private boolean[] sampleQueueIsAudioVideoFlags = new boolean[0];
    private int[] sampleQueueTrackIds = new int[0];
    private SampleQueue[] sampleQueues = new SampleQueue[0];
    private boolean sampleQueuesBuilt;
    private boolean[] sampleQueuesEnabledStates = new boolean[0];
    private boolean seenFirstTrackSelection;
    private int[] trackGroupToSampleQueueIndex;
    private TrackGroupArray trackGroups;
    private final int trackType;
    private boolean tracksEnded;
    private int videoSampleQueueIndex = -1;
    private boolean videoSampleQueueMappingDone;

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

    public void prepareWithMasterPlaylistInfo(TrackGroupArray trackGroups, int primaryTrackGroupIndex) {
        this.prepared = true;
        this.trackGroups = trackGroups;
        this.primaryTrackGroupIndex = primaryTrackGroupIndex;
        this.callback.onPrepared();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public boolean isMappingFinished() {
        return this.trackGroupToSampleQueueIndex != null;
    }

    public int bindSampleQueueToSampleStream(int trackGroupIndex) {
        if (!isMappingFinished()) {
            return -1;
        }
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        if (sampleQueueIndex == -1) {
            return -1;
        }
        if (this.sampleQueuesEnabledStates[sampleQueueIndex]) {
            return -1;
        }
        this.sampleQueuesEnabledStates[sampleQueueIndex] = true;
        return sampleQueueIndex;
    }

    public void unbindSampleQueue(int trackGroupIndex) {
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        Assertions.checkState(this.sampleQueuesEnabledStates[sampleQueueIndex]);
        this.sampleQueuesEnabledStates[sampleQueueIndex] = false;
    }

    public boolean selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, boolean forceReset) {
        boolean seekRequired;
        SampleQueue sampleQueue;
        Assertions.checkState(this.prepared);
        int oldEnabledTrackGroupCount = this.enabledTrackGroupCount;
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                this.enabledTrackGroupCount--;
                ((HlsSampleStream) streams[i]).unbindSampleQueue();
                streams[i] = null;
            }
            i++;
        }
        if (forceReset || (this.seenFirstTrackSelection ? oldEnabledTrackGroupCount == 0 : positionUs != this.lastSeekPositionUs)) {
            seekRequired = true;
        } else {
            seekRequired = false;
        }
        TrackSelection oldPrimaryTrackSelection = this.chunkSource.getTrackSelection();
        TrackSelection primaryTrackSelection = oldPrimaryTrackSelection;
        i = 0;
        while (i < selections.length) {
            if (streams[i] == null && selections[i] != null) {
                this.enabledTrackGroupCount++;
                TrackSelection selection = selections[i];
                int trackGroupIndex = this.trackGroups.indexOf(selection.getTrackGroup());
                if (trackGroupIndex == this.primaryTrackGroupIndex) {
                    primaryTrackSelection = selection;
                    this.chunkSource.selectTracks(selection);
                }
                streams[i] = new HlsSampleStream(this, trackGroupIndex);
                streamResetFlags[i] = true;
                if (this.sampleQueuesBuilt && !seekRequired) {
                    sampleQueue = this.sampleQueues[this.trackGroupToSampleQueueIndex[trackGroupIndex]];
                    sampleQueue.rewind();
                    if (sampleQueue.advanceTo(positionUs, true, true) != -1 || sampleQueue.getReadIndex() == 0) {
                        seekRequired = false;
                    } else {
                        seekRequired = true;
                    }
                }
            }
            i++;
        }
        if (this.enabledTrackGroupCount == 0) {
            this.chunkSource.reset();
            this.downstreamTrackFormat = null;
            this.mediaChunks.clear();
            if (this.loader.isLoading()) {
                if (this.sampleQueuesBuilt) {
                    for (SampleQueue sampleQueue2 : this.sampleQueues) {
                        sampleQueue2.discardToEnd();
                    }
                }
                this.loader.cancelLoading();
            } else {
                resetSampleQueues();
            }
        } else {
            if (!(this.mediaChunks.isEmpty() || Util.areEqual(primaryTrackSelection, oldPrimaryTrackSelection))) {
                boolean primarySampleQueueDirty = false;
                if (this.seenFirstTrackSelection) {
                    primarySampleQueueDirty = true;
                } else {
                    primaryTrackSelection.updateSelectedTrack(positionUs, positionUs < 0 ? -positionUs : 0, C.TIME_UNSET);
                    if (primaryTrackSelection.getSelectedIndexInTrackGroup() != this.chunkSource.getTrackGroup().indexOf(getLastMediaChunk().trackFormat)) {
                        primarySampleQueueDirty = true;
                    }
                }
                if (primarySampleQueueDirty) {
                    forceReset = true;
                    seekRequired = true;
                    this.pendingResetUpstreamFormats = true;
                }
            }
            if (seekRequired) {
                seekToUs(positionUs, forceReset);
                for (i = 0; i < streams.length; i++) {
                    if (streams[i] != null) {
                        streamResetFlags[i] = true;
                    }
                }
            }
        }
        this.seenFirstTrackSelection = true;
        return seekRequired;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (this.sampleQueuesBuilt) {
            int sampleQueueCount = this.sampleQueues.length;
            for (int i = 0; i < sampleQueueCount; i++) {
                this.sampleQueues[i].discardTo(positionUs, toKeyframe, this.sampleQueuesEnabledStates[i]);
            }
        }
    }

    public boolean seekToUs(long positionUs, boolean forceReset) {
        this.lastSeekPositionUs = positionUs;
        if (this.sampleQueuesBuilt && !forceReset && !isPendingReset() && seekInsideBufferUs(positionUs)) {
            return false;
        }
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
        } else {
            resetSampleQueues();
        }
        return true;
    }

    public void release() {
        boolean releasedSynchronously = this.loader.release(this);
        if (this.prepared && !releasedSynchronously) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.discardToEnd();
            }
        }
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
    }

    public void onLoaderReleased() {
        resetSampleQueues();
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.chunkSource.setIsTimestampMaster(isTimestampMaster);
    }

    public void onPlaylistBlacklisted(HlsUrl url, long blacklistMs) {
        this.chunkSource.onPlaylistBlacklisted(url, blacklistMs);
    }

    public boolean isReady(int sampleQueueIndex) {
        return this.loadingFinished || (!isPendingReset() && this.sampleQueues[sampleQueueIndex].hasNextSample());
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    public int readData(int sampleQueueIndex, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
        if (isPendingReset()) {
            return -3;
        }
        if (!this.mediaChunks.isEmpty()) {
            int discardToMediaChunkIndex = 0;
            while (discardToMediaChunkIndex < this.mediaChunks.size() - 1 && finishedReadingChunk((HlsMediaChunk) this.mediaChunks.get(discardToMediaChunkIndex))) {
                discardToMediaChunkIndex++;
            }
            if (discardToMediaChunkIndex > 0) {
                Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
            }
            HlsMediaChunk currentChunk = (HlsMediaChunk) this.mediaChunks.get(0);
            Format trackFormat = currentChunk.trackFormat;
            if (!trackFormat.equals(this.downstreamTrackFormat)) {
                this.eventDispatcher.downstreamFormatChanged(this.trackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
            }
            this.downstreamTrackFormat = trackFormat;
        }
        return this.sampleQueues[sampleQueueIndex].read(formatHolder, buffer, requireFormat, this.loadingFinished, this.lastSeekPositionUs);
    }

    public int skipData(int sampleQueueIndex, long positionUs) {
        if (isPendingReset()) {
            return 0;
        }
        SampleQueue sampleQueue = this.sampleQueues[sampleQueueIndex];
        if (this.loadingFinished && positionUs > sampleQueue.getLargestQueuedTimestampUs()) {
            return sampleQueue.advanceToEnd();
        }
        int skipCount = sampleQueue.advanceTo(positionUs, true, true);
        if (skipCount == -1) {
            skipCount = 0;
        }
        return skipCount;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long bufferedPositionUs = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = getLastMediaChunk();
        HlsMediaChunk lastCompletedMediaChunk = lastMediaChunk.isLoadCompleted() ? lastMediaChunk : this.mediaChunks.size() > 1 ? (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        if (!this.sampleQueuesBuilt) {
            return bufferedPositionUs;
        }
        for (SampleQueue sampleQueue : this.sampleQueues) {
            bufferedPositionUs = Math.max(bufferedPositionUs, sampleQueue.getLargestQueuedTimestampUs());
        }
        return bufferedPositionUs;
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : getLastMediaChunk().endTimeUs;
    }

    public boolean continueLoading(long positionUs) {
        if (this.loadingFinished || this.loader.isLoading()) {
            return false;
        }
        HlsMediaChunk previousChunk;
        long loadPositionUs;
        if (isPendingReset()) {
            previousChunk = null;
            loadPositionUs = this.pendingResetPositionUs;
        } else {
            previousChunk = getLastMediaChunk();
            loadPositionUs = previousChunk.endTimeUs;
        }
        this.chunkSource.getNextChunk(previousChunk, positionUs, loadPositionUs, this.nextChunkHolder);
        boolean endOfStream = this.nextChunkHolder.endOfStream;
        Chunk loadable = this.nextChunkHolder.chunk;
        HlsUrl playlistToLoad = this.nextChunkHolder.playlist;
        this.nextChunkHolder.clear();
        if (endOfStream) {
            this.pendingResetPositionUs = C.TIME_UNSET;
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

    public void reevaluateBuffer(long positionUs) {
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
            resetSampleQueues();
            if (this.enabledTrackGroupCount > 0) {
                this.callback.onContinueLoadingRequested(this);
            }
        }
    }

    public int onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        boolean cancelable = !isMediaChunk || bytesLoaded == 0;
        boolean canceled = false;
        if (this.chunkSource.onChunkLoadError(loadable, cancelable, error)) {
            if (isMediaChunk) {
                Assertions.checkState(((HlsMediaChunk) this.mediaChunks.remove(this.mediaChunks.size() + -1)) == loadable);
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
            canceled = true;
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, canceled);
        if (!canceled) {
            return error instanceof ParserException ? 3 : 0;
        } else {
            if (this.prepared) {
                this.callback.onContinueLoadingRequested(this);
            } else {
                continueLoading(this.lastSeekPositionUs);
            }
            return 2;
        }
    }

    public void init(int chunkUid, boolean shouldSpliceIn, boolean reusingExtractor) {
        int i = 0;
        if (!reusingExtractor) {
            this.audioSampleQueueMappingDone = false;
            this.videoSampleQueueMappingDone = false;
        }
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.sourceId(chunkUid);
        }
        if (shouldSpliceIn) {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].splice();
                i++;
            }
        }
    }

    public TrackOutput track(int id, int type) {
        int trackCount = this.sampleQueues.length;
        if (type == 1) {
            if (this.audioSampleQueueIndex != -1) {
                if (!this.audioSampleQueueMappingDone) {
                    this.audioSampleQueueMappingDone = true;
                    this.sampleQueueTrackIds[this.audioSampleQueueIndex] = id;
                    return this.sampleQueues[this.audioSampleQueueIndex];
                } else if (this.sampleQueueTrackIds[this.audioSampleQueueIndex] == id) {
                    return this.sampleQueues[this.audioSampleQueueIndex];
                } else {
                    return createDummyTrackOutput(id, type);
                }
            } else if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
        } else if (type != 2) {
            for (int i = 0; i < trackCount; i++) {
                if (this.sampleQueueTrackIds[i] == id) {
                    return this.sampleQueues[i];
                }
            }
            if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
        } else if (this.videoSampleQueueIndex != -1) {
            if (!this.videoSampleQueueMappingDone) {
                this.videoSampleQueueMappingDone = true;
                this.sampleQueueTrackIds[this.videoSampleQueueIndex] = id;
                return this.sampleQueues[this.videoSampleQueueIndex];
            } else if (this.sampleQueueTrackIds[this.videoSampleQueueIndex] == id) {
                return this.sampleQueues[this.videoSampleQueueIndex];
            } else {
                return createDummyTrackOutput(id, type);
            }
        } else if (this.tracksEnded) {
            return createDummyTrackOutput(id, type);
        }
        SampleQueue trackOutput = new SampleQueue(this.allocator);
        trackOutput.setSampleOffsetUs(this.sampleOffsetUs);
        trackOutput.setUpstreamFormatChangeListener(this);
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds[trackCount] = id;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        this.sampleQueues[trackCount] = trackOutput;
        this.sampleQueueIsAudioVideoFlags = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, trackCount + 1);
        boolean[] zArr = this.sampleQueueIsAudioVideoFlags;
        boolean z = type == 1 || type == 2;
        zArr[trackCount] = z;
        this.haveAudioVideoSampleQueues |= this.sampleQueueIsAudioVideoFlags[trackCount];
        if (type == 1) {
            this.audioSampleQueueMappingDone = true;
            this.audioSampleQueueIndex = trackCount;
        } else if (type == 2) {
            this.videoSampleQueueMappingDone = true;
            this.videoSampleQueueIndex = trackCount;
        }
        this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, trackCount + 1);
        return trackOutput;
    }

    public void endTracks() {
        this.tracksEnded = true;
        this.handler.post(this.onTracksEndedRunnable);
    }

    public void seekMap(SeekMap seekMap) {
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        this.sampleOffsetUs = sampleOffsetUs;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.setSampleOffsetUs(sampleOffsetUs);
        }
    }

    private boolean finishedReadingChunk(HlsMediaChunk chunk) {
        int chunkUid = chunk.uid;
        int sampleQueueCount = this.sampleQueues.length;
        int i = 0;
        while (i < sampleQueueCount) {
            if (this.sampleQueuesEnabledStates[i] && this.sampleQueues[i].peekSourceId() == chunkUid) {
                return false;
            }
            i++;
        }
        return true;
    }

    private void resetSampleQueues() {
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset(this.pendingResetUpstreamFormats);
        }
        this.pendingResetUpstreamFormats = false;
    }

    private void onTracksEnded() {
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    private void maybeFinishPrepare() {
        if (!this.released && this.trackGroupToSampleQueueIndex == null && this.sampleQueuesBuilt) {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            int i = 0;
            while (i < length) {
                if (sampleQueueArr[i].getUpstreamFormat() != null) {
                    i++;
                } else {
                    return;
                }
            }
            if (this.trackGroups != null) {
                mapSampleQueuesToMatchTrackGroups();
                return;
            }
            buildTracks();
            this.prepared = true;
            this.callback.onPrepared();
        }
    }

    private void mapSampleQueuesToMatchTrackGroups() {
        int trackGroupCount = this.trackGroups.length;
        this.trackGroupToSampleQueueIndex = new int[trackGroupCount];
        Arrays.fill(this.trackGroupToSampleQueueIndex, -1);
        for (int i = 0; i < trackGroupCount; i++) {
            for (int queueIndex = 0; queueIndex < this.sampleQueues.length; queueIndex++) {
                if (formatsMatch(this.sampleQueues[queueIndex].getUpstreamFormat(), this.trackGroups.get(i).getFormat(0))) {
                    this.trackGroupToSampleQueueIndex[i] = queueIndex;
                    break;
                }
            }
        }
    }

    private void buildTracks() {
        int i;
        int primaryExtractorTrackType = 0;
        int primaryExtractorTrackIndex = -1;
        int extractorTrackCount = this.sampleQueues.length;
        for (i = 0; i < extractorTrackCount; i++) {
            int trackType;
            String sampleMimeType = this.sampleQueues[i].getUpstreamFormat().sampleMimeType;
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
        this.trackGroupToSampleQueueIndex = new int[extractorTrackCount];
        for (i = 0; i < extractorTrackCount; i++) {
            this.trackGroupToSampleQueueIndex[i] = i;
        }
        TrackGroup[] trackGroups = new TrackGroup[extractorTrackCount];
        for (i = 0; i < extractorTrackCount; i++) {
            Format sampleFormat = this.sampleQueues[i].getUpstreamFormat();
            if (i == primaryExtractorTrackIndex) {
                Format[] formats = new Format[chunkSourceTrackCount];
                for (int j = 0; j < chunkSourceTrackCount; j++) {
                    formats[j] = deriveFormat(chunkSourceTrackGroup.getFormat(j), sampleFormat, true);
                }
                trackGroups[i] = new TrackGroup(formats);
                this.primaryTrackGroupIndex = i;
            } else {
                Format trackFormat = (primaryExtractorTrackType == 3 && MimeTypes.isAudio(sampleFormat.sampleMimeType)) ? this.muxedAudioFormat : null;
                trackGroups[i] = new TrackGroup(deriveFormat(trackFormat, sampleFormat, false));
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroups);
    }

    private HlsMediaChunk getLastMediaChunk() {
        return (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long positionUs) {
        int sampleQueueCount = this.sampleQueues.length;
        int i = 0;
        while (i < sampleQueueCount) {
            boolean seekInsideQueue;
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(positionUs, true, false) != -1) {
                seekInsideQueue = true;
            } else {
                seekInsideQueue = false;
            }
            if (!seekInsideQueue && (this.sampleQueueIsAudioVideoFlags[i] || !this.haveAudioVideoSampleQueues)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static Format deriveFormat(Format playlistFormat, Format sampleFormat, boolean propagateBitrate) {
        if (playlistFormat == null) {
            return sampleFormat;
        }
        int bitrate = propagateBitrate ? playlistFormat.bitrate : -1;
        String codecs = Util.getCodecsOfType(playlistFormat.codecs, MimeTypes.getTrackType(sampleFormat.sampleMimeType));
        String mimeType = MimeTypes.getMediaMimeType(codecs);
        if (mimeType == null) {
            mimeType = sampleFormat.sampleMimeType;
        }
        return sampleFormat.copyWithContainerInfo(playlistFormat.id, mimeType, codecs, bitrate, playlistFormat.width, playlistFormat.height, playlistFormat.selectionFlags, playlistFormat.language);
    }

    private static boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private static boolean formatsMatch(Format manifestFormat, Format sampleFormat) {
        String manifestFormatMimeType = manifestFormat.sampleMimeType;
        String sampleFormatMimeType = sampleFormat.sampleMimeType;
        int manifestFormatTrackType = MimeTypes.getTrackType(manifestFormatMimeType);
        if (manifestFormatTrackType != 3) {
            if (manifestFormatTrackType == MimeTypes.getTrackType(sampleFormatMimeType)) {
                return true;
            }
            return false;
        } else if (!Util.areEqual(manifestFormatMimeType, sampleFormatMimeType)) {
            return false;
        } else {
            if ((MimeTypes.APPLICATION_CEA608.equals(manifestFormatMimeType) || MimeTypes.APPLICATION_CEA708.equals(manifestFormatMimeType)) && manifestFormat.accessibilityChannel != sampleFormat.accessibilityChannel) {
                return false;
            }
            return true;
        }
    }

    private static DummyTrackOutput createDummyTrackOutput(int id, int type) {
        Log.w(TAG, "Unmapped track with id " + id + " of type " + type);
        return new DummyTrackOutput();
    }
}
