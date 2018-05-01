package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0542C;
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
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
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
    private final Runnable maybeFinishPrepareRunnable = new C06151();
    private final ArrayList<HlsMediaChunk> mediaChunks = new ArrayList();
    private final int minLoadableRetryCount;
    private final Format muxedAudioFormat;
    private final HlsChunkHolder nextChunkHolder = new HlsChunkHolder();
    private final Runnable onTracksEndedRunnable = new C06162();
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

    /* renamed from: org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper$1 */
    class C06151 implements Runnable {
        C06151() {
        }

        public void run() {
            HlsSampleStreamWrapper.this.maybeFinishPrepare();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper$2 */
    class C06162 implements Runnable {
        C06162() {
        }

        public void run() {
            HlsSampleStreamWrapper.this.onTracksEnded();
        }
    }

    public interface Callback extends org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback<HlsSampleStreamWrapper> {
        void onPlaylistRefreshRequired(HlsUrl hlsUrl);

        void onPrepared();
    }

    public void reevaluateBuffer(long j) {
    }

    public void seekMap(SeekMap seekMap) {
    }

    public HlsSampleStreamWrapper(int i, Callback callback, HlsChunkSource hlsChunkSource, Allocator allocator, long j, Format format, int i2, EventDispatcher eventDispatcher) {
        this.trackType = i;
        this.callback = callback;
        this.chunkSource = hlsChunkSource;
        this.allocator = allocator;
        this.muxedAudioFormat = format;
        this.minLoadableRetryCount = i2;
        this.eventDispatcher = eventDispatcher;
        this.lastSeekPositionUs = j;
        this.pendingResetPositionUs = j;
    }

    public void continuePreparing() {
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        }
    }

    public void prepareWithMasterPlaylistInfo(TrackGroupArray trackGroupArray, int i) {
        this.prepared = true;
        this.trackGroups = trackGroupArray;
        this.primaryTrackGroupIndex = i;
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

    public int bindSampleQueueToSampleStream(int i) {
        if (!isMappingFinished()) {
            return -1;
        }
        i = this.trackGroupToSampleQueueIndex[i];
        if (i == -1 || this.sampleQueuesEnabledStates[i]) {
            return -1;
        }
        this.sampleQueuesEnabledStates[i] = true;
        return i;
    }

    public void unbindSampleQueue(int i) {
        i = this.trackGroupToSampleQueueIndex[i];
        Assertions.checkState(this.sampleQueuesEnabledStates[i]);
        this.sampleQueuesEnabledStates[i] = false;
    }

    public boolean selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j, boolean z) {
        boolean z2;
        TrackSelection trackSelection;
        boolean z3;
        TrackSelection trackSelection2;
        TrackSelection trackSelection3;
        int indexOf;
        SampleQueue[] sampleQueueArr;
        int length;
        long j2;
        TrackSelection trackSelection4;
        int i;
        boolean z4;
        TrackSelection[] trackSelectionArr2 = trackSelectionArr;
        SampleStream[] sampleStreamArr2 = sampleStreamArr;
        long j3 = j;
        Assertions.checkState(this.prepared);
        int i2 = this.enabledTrackGroupCount;
        int i3 = 0;
        int i4 = 0;
        while (i4 < trackSelectionArr2.length) {
            if (sampleStreamArr2[i4] != null && (trackSelectionArr2[i4] == null || !zArr[i4])) {
                r0.enabledTrackGroupCount--;
                ((HlsSampleStream) sampleStreamArr2[i4]).unbindSampleQueue();
                sampleStreamArr2[i4] = null;
            }
            i4++;
        }
        if (!z) {
            if (r0.seenFirstTrackSelection) {
                if (i2 == 0) {
                }
            } else if (j3 != r0.lastSeekPositionUs) {
            }
            z2 = false;
            trackSelection = r0.chunkSource.getTrackSelection();
            z3 = z2;
            trackSelection2 = trackSelection;
            i2 = 0;
            while (i2 < trackSelectionArr2.length) {
                if (sampleStreamArr2[i2] == null && trackSelectionArr2[i2] != null) {
                    r0.enabledTrackGroupCount++;
                    trackSelection3 = trackSelectionArr2[i2];
                    indexOf = r0.trackGroups.indexOf(trackSelection3.getTrackGroup());
                    if (indexOf == r0.primaryTrackGroupIndex) {
                        r0.chunkSource.selectTracks(trackSelection3);
                        trackSelection2 = trackSelection3;
                    }
                    sampleStreamArr2[i2] = new HlsSampleStream(r0, indexOf);
                    zArr2[i2] = true;
                    if (r0.sampleQueuesBuilt && !r16) {
                        SampleQueue sampleQueue = r0.sampleQueues[r0.trackGroupToSampleQueueIndex[indexOf]];
                        sampleQueue.rewind();
                        z3 = (sampleQueue.advanceTo(j3, true, true) == -1 || sampleQueue.getReadIndex() == 0) ? false : true;
                    }
                }
                i2++;
            }
            if (r0.enabledTrackGroupCount != 0) {
                r0.chunkSource.reset();
                r0.downstreamTrackFormat = null;
                r0.mediaChunks.clear();
                if (r0.loader.isLoading()) {
                    resetSampleQueues();
                } else {
                    if (r0.sampleQueuesBuilt) {
                        sampleQueueArr = r0.sampleQueues;
                        length = sampleQueueArr.length;
                        while (i3 < length) {
                            sampleQueueArr[i3].discardToEnd();
                            i3++;
                        }
                    }
                    r0.loader.cancelLoading();
                }
            } else {
                if (!(r0.mediaChunks.isEmpty() || Util.areEqual(trackSelection2, trackSelection))) {
                    if (!r0.seenFirstTrackSelection) {
                        j2 = 0;
                        if (j3 < 0) {
                            j2 = -j3;
                        }
                        trackSelection4 = trackSelection2;
                        trackSelection2.updateSelectedTrack(j3, j2, C0542C.TIME_UNSET);
                        if (trackSelection4.getSelectedIndexInTrackGroup() != r0.chunkSource.getTrackGroup().indexOf(getLastMediaChunk().trackFormat)) {
                            i = 0;
                            if (i != 0) {
                                r0.pendingResetUpstreamFormats = true;
                                z4 = true;
                                z3 = z4;
                                if (z3) {
                                    seekToUs(j3, z4);
                                    while (i3 < sampleStreamArr2.length) {
                                        if (sampleStreamArr2[i3] == null) {
                                            zArr2[i3] = true;
                                        }
                                        i3++;
                                    }
                                }
                            }
                        }
                    }
                    i = true;
                    if (i != 0) {
                        r0.pendingResetUpstreamFormats = true;
                        z4 = true;
                        z3 = z4;
                        if (z3) {
                            seekToUs(j3, z4);
                            while (i3 < sampleStreamArr2.length) {
                                if (sampleStreamArr2[i3] == null) {
                                    zArr2[i3] = true;
                                }
                                i3++;
                            }
                        }
                    }
                }
                z4 = z;
                if (z3) {
                    seekToUs(j3, z4);
                    while (i3 < sampleStreamArr2.length) {
                        if (sampleStreamArr2[i3] == null) {
                            zArr2[i3] = true;
                        }
                        i3++;
                    }
                }
            }
            r0.seenFirstTrackSelection = true;
            return z3;
        }
        z2 = true;
        trackSelection = r0.chunkSource.getTrackSelection();
        z3 = z2;
        trackSelection2 = trackSelection;
        i2 = 0;
        while (i2 < trackSelectionArr2.length) {
            r0.enabledTrackGroupCount++;
            trackSelection3 = trackSelectionArr2[i2];
            indexOf = r0.trackGroups.indexOf(trackSelection3.getTrackGroup());
            if (indexOf == r0.primaryTrackGroupIndex) {
                r0.chunkSource.selectTracks(trackSelection3);
                trackSelection2 = trackSelection3;
            }
            sampleStreamArr2[i2] = new HlsSampleStream(r0, indexOf);
            zArr2[i2] = true;
            SampleQueue sampleQueue2 = r0.sampleQueues[r0.trackGroupToSampleQueueIndex[indexOf]];
            sampleQueue2.rewind();
            if (sampleQueue2.advanceTo(j3, true, true) == -1) {
            }
            i2++;
        }
        if (r0.enabledTrackGroupCount != 0) {
            if (r0.seenFirstTrackSelection) {
                j2 = 0;
                if (j3 < 0) {
                    j2 = -j3;
                }
                trackSelection4 = trackSelection2;
                trackSelection2.updateSelectedTrack(j3, j2, C0542C.TIME_UNSET);
                if (trackSelection4.getSelectedIndexInTrackGroup() != r0.chunkSource.getTrackGroup().indexOf(getLastMediaChunk().trackFormat)) {
                    i = 0;
                    if (i != 0) {
                        r0.pendingResetUpstreamFormats = true;
                        z4 = true;
                        z3 = z4;
                        if (z3) {
                            seekToUs(j3, z4);
                            while (i3 < sampleStreamArr2.length) {
                                if (sampleStreamArr2[i3] == null) {
                                    zArr2[i3] = true;
                                }
                                i3++;
                            }
                        }
                    }
                    z4 = z;
                    if (z3) {
                        seekToUs(j3, z4);
                        while (i3 < sampleStreamArr2.length) {
                            if (sampleStreamArr2[i3] == null) {
                                zArr2[i3] = true;
                            }
                            i3++;
                        }
                    }
                }
            }
            i = true;
            if (i != 0) {
                r0.pendingResetUpstreamFormats = true;
                z4 = true;
                z3 = z4;
                if (z3) {
                    seekToUs(j3, z4);
                    while (i3 < sampleStreamArr2.length) {
                        if (sampleStreamArr2[i3] == null) {
                            zArr2[i3] = true;
                        }
                        i3++;
                    }
                }
            }
            z4 = z;
            if (z3) {
                seekToUs(j3, z4);
                while (i3 < sampleStreamArr2.length) {
                    if (sampleStreamArr2[i3] == null) {
                        zArr2[i3] = true;
                    }
                    i3++;
                }
            }
        } else {
            r0.chunkSource.reset();
            r0.downstreamTrackFormat = null;
            r0.mediaChunks.clear();
            if (r0.loader.isLoading()) {
                resetSampleQueues();
            } else {
                if (r0.sampleQueuesBuilt) {
                    sampleQueueArr = r0.sampleQueues;
                    length = sampleQueueArr.length;
                    while (i3 < length) {
                        sampleQueueArr[i3].discardToEnd();
                        i3++;
                    }
                }
                r0.loader.cancelLoading();
            }
        }
        r0.seenFirstTrackSelection = true;
        return z3;
    }

    public void discardBuffer(long j, boolean z) {
        if (this.sampleQueuesBuilt) {
            int length = this.sampleQueues.length;
            for (int i = 0; i < length; i++) {
                this.sampleQueues[i].discardTo(j, z, this.sampleQueuesEnabledStates[i]);
            }
        }
    }

    public boolean seekToUs(long j, boolean z) {
        this.lastSeekPositionUs = j;
        if (this.sampleQueuesBuilt && !z && !isPendingReset() && seekInsideBufferUs(j)) {
            return false;
        }
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading() != null) {
            this.loader.cancelLoading();
        } else {
            resetSampleQueues();
        }
        return 1;
    }

    public void release() {
        boolean release = this.loader.release(this);
        if (this.prepared && !release) {
            for (SampleQueue discardToEnd : this.sampleQueues) {
                discardToEnd.discardToEnd();
            }
        }
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
    }

    public void onLoaderReleased() {
        resetSampleQueues();
    }

    public void setIsTimestampMaster(boolean z) {
        this.chunkSource.setIsTimestampMaster(z);
    }

    public void onPlaylistBlacklisted(HlsUrl hlsUrl, long j) {
        this.chunkSource.onPlaylistBlacklisted(hlsUrl, j);
    }

    public boolean isReady(int i) {
        if (!this.loadingFinished) {
            if (isPendingReset() || this.sampleQueues[i].hasNextSample() == 0) {
                return false;
            }
        }
        return true;
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    public int readData(int i, FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (isPendingReset()) {
            return -3;
        }
        if (!this.mediaChunks.isEmpty()) {
            int i2 = 0;
            while (i2 < this.mediaChunks.size() - 1 && finishedReadingChunk((HlsMediaChunk) this.mediaChunks.get(i2))) {
                i2++;
            }
            if (i2 > 0) {
                Util.removeRange(this.mediaChunks, 0, i2);
            }
            HlsMediaChunk hlsMediaChunk = (HlsMediaChunk) this.mediaChunks.get(0);
            Format format = hlsMediaChunk.trackFormat;
            if (!format.equals(this.downstreamTrackFormat)) {
                this.eventDispatcher.downstreamFormatChanged(this.trackType, format, hlsMediaChunk.trackSelectionReason, hlsMediaChunk.trackSelectionData, hlsMediaChunk.startTimeUs);
            }
            this.downstreamTrackFormat = format;
        }
        return this.sampleQueues[i].read(formatHolder, decoderInputBuffer, z, this.loadingFinished, this.lastSeekPositionUs);
    }

    public int skipData(int i, long j) {
        if (isPendingReset()) {
            return 0;
        }
        i = this.sampleQueues[i];
        if (this.loadingFinished && j > i.getLargestQueuedTimestampUs()) {
            return i.advanceToEnd();
        }
        i = i.advanceTo(j, true, true);
        if (i == -1) {
            i = 0;
        }
        return i;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long j = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = getLastMediaChunk();
        if (!lastMediaChunk.isLoadCompleted()) {
            lastMediaChunk = this.mediaChunks.size() > 1 ? (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        }
        if (lastMediaChunk != null) {
            j = Math.max(j, lastMediaChunk.endTimeUs);
        }
        if (this.sampleQueuesBuilt) {
            for (SampleQueue largestQueuedTimestampUs : this.sampleQueues) {
                j = Math.max(j, largestQueuedTimestampUs.getLargestQueuedTimestampUs());
            }
        }
        return j;
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : getLastMediaChunk().endTimeUs;
    }

    public boolean continueLoading(long j) {
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                HlsMediaChunk hlsMediaChunk;
                long j2;
                if (isPendingReset()) {
                    hlsMediaChunk = null;
                    j2 = r0.pendingResetPositionUs;
                } else {
                    hlsMediaChunk = getLastMediaChunk();
                    j2 = hlsMediaChunk.endTimeUs;
                }
                r0.chunkSource.getNextChunk(hlsMediaChunk, j, j2, r0.nextChunkHolder);
                boolean z = r0.nextChunkHolder.endOfStream;
                Chunk chunk = r0.nextChunkHolder.chunk;
                HlsUrl hlsUrl = r0.nextChunkHolder.playlist;
                r0.nextChunkHolder.clear();
                if (z) {
                    r0.pendingResetPositionUs = C0542C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (chunk == null) {
                    if (hlsUrl != null) {
                        r0.callback.onPlaylistRefreshRequired(hlsUrl);
                    }
                    return false;
                } else {
                    if (isMediaChunk(chunk)) {
                        r0.pendingResetPositionUs = C0542C.TIME_UNSET;
                        hlsMediaChunk = (HlsMediaChunk) chunk;
                        hlsMediaChunk.init(r0);
                        r0.mediaChunks.add(hlsMediaChunk);
                    }
                    long startLoading = r0.loader.startLoading(chunk, r0, r0.minLoadableRetryCount);
                    r0.eventDispatcher.loadStarted(chunk.dataSpec, chunk.type, r0.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, startLoading);
                    return true;
                }
            }
        }
        return false;
    }

    public void onLoadCompleted(Chunk chunk, long j, long j2) {
        Chunk chunk2 = chunk;
        this.chunkSource.onChunkLoadCompleted(chunk2);
        this.eventDispatcher.loadCompleted(chunk2.dataSpec, chunk2.type, this.trackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, chunk.bytesLoaded());
        if (this.prepared) {
            r0.callback.onContinueLoadingRequested(r0);
        } else {
            continueLoading(r0.lastSeekPositionUs);
        }
    }

    public void onLoadCanceled(Chunk chunk, long j, long j2, boolean z) {
        Chunk chunk2 = chunk;
        EventDispatcher eventDispatcher = this.eventDispatcher;
        DataSpec dataSpec = chunk2.dataSpec;
        int i = chunk2.type;
        int i2 = this.trackType;
        Format format = chunk2.trackFormat;
        int i3 = chunk2.trackSelectionReason;
        Object obj = chunk2.trackSelectionData;
        long j3 = chunk2.startTimeUs;
        long j4 = chunk2.endTimeUs;
        eventDispatcher.loadCanceled(dataSpec, i, i2, format, i3, obj, j3, j4, j, j2, chunk.bytesLoaded());
        if (!z) {
            resetSampleQueues();
            if (r0.enabledTrackGroupCount > 0) {
                r0.callback.onContinueLoadingRequested(r0);
            }
        }
    }

    public int onLoadError(Chunk chunk, long j, long j2, IOException iOException) {
        boolean z;
        boolean z2;
        HlsSampleStreamWrapper hlsSampleStreamWrapper = this;
        Chunk chunk2 = chunk;
        IOException iOException2 = iOException;
        long bytesLoaded = chunk.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(chunk);
        int i = 0;
        if (isMediaChunk) {
            if (bytesLoaded != 0) {
                z = false;
                if (hlsSampleStreamWrapper.chunkSource.onChunkLoadError(chunk2, z, iOException2)) {
                    z2 = false;
                } else {
                    if (isMediaChunk) {
                        Assertions.checkState(((HlsMediaChunk) hlsSampleStreamWrapper.mediaChunks.remove(hlsSampleStreamWrapper.mediaChunks.size() - 1)) != chunk2);
                        if (hlsSampleStreamWrapper.mediaChunks.isEmpty()) {
                            hlsSampleStreamWrapper.pendingResetPositionUs = hlsSampleStreamWrapper.lastSeekPositionUs;
                        }
                    }
                    z2 = true;
                }
                hlsSampleStreamWrapper.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, hlsSampleStreamWrapper.trackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, chunk.bytesLoaded(), iOException, z2);
                if (z2) {
                    if (iOException instanceof ParserException) {
                        i = 3;
                    }
                    return i;
                }
                if (hlsSampleStreamWrapper.prepared) {
                    continueLoading(hlsSampleStreamWrapper.lastSeekPositionUs);
                } else {
                    hlsSampleStreamWrapper.callback.onContinueLoadingRequested(hlsSampleStreamWrapper);
                }
                return 2;
            }
        }
        z = true;
        if (hlsSampleStreamWrapper.chunkSource.onChunkLoadError(chunk2, z, iOException2)) {
            z2 = false;
        } else {
            if (isMediaChunk) {
                if (((HlsMediaChunk) hlsSampleStreamWrapper.mediaChunks.remove(hlsSampleStreamWrapper.mediaChunks.size() - 1)) != chunk2) {
                }
                Assertions.checkState(((HlsMediaChunk) hlsSampleStreamWrapper.mediaChunks.remove(hlsSampleStreamWrapper.mediaChunks.size() - 1)) != chunk2);
                if (hlsSampleStreamWrapper.mediaChunks.isEmpty()) {
                    hlsSampleStreamWrapper.pendingResetPositionUs = hlsSampleStreamWrapper.lastSeekPositionUs;
                }
            }
            z2 = true;
        }
        hlsSampleStreamWrapper.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, hlsSampleStreamWrapper.trackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, chunk.bytesLoaded(), iOException, z2);
        if (z2) {
            if (iOException instanceof ParserException) {
                i = 3;
            }
            return i;
        }
        if (hlsSampleStreamWrapper.prepared) {
            hlsSampleStreamWrapper.callback.onContinueLoadingRequested(hlsSampleStreamWrapper);
        } else {
            continueLoading(hlsSampleStreamWrapper.lastSeekPositionUs);
        }
        return 2;
    }

    public void init(int i, boolean z, boolean z2) {
        boolean z3 = false;
        if (!z2) {
            this.audioSampleQueueMappingDone = false;
            this.videoSampleQueueMappingDone = false;
        }
        for (SampleQueue sourceId : this.sampleQueues) {
            sourceId.sourceId(i);
        }
        if (z) {
            i = this.sampleQueues;
            z = i.length;
            while (z3 < z) {
                i[z3].splice();
                z3++;
            }
        }
    }

    public TrackOutput track(int i, int i2) {
        boolean z = false;
        int length = this.sampleQueues.length;
        if (i2 == 1) {
            if (this.audioSampleQueueIndex != -1) {
                if (this.audioSampleQueueMappingDone) {
                    if (this.sampleQueueTrackIds[this.audioSampleQueueIndex] == i) {
                        i = this.sampleQueues[this.audioSampleQueueIndex];
                    } else {
                        i = createDummyTrackOutput(i, i2);
                    }
                    return i;
                }
                this.audioSampleQueueMappingDone = true;
                this.sampleQueueTrackIds[this.audioSampleQueueIndex] = i;
                return this.sampleQueues[this.audioSampleQueueIndex];
            } else if (this.tracksEnded) {
                return createDummyTrackOutput(i, i2);
            }
        } else if (i2 != 2) {
            for (int i3 = 0; i3 < length; i3++) {
                if (this.sampleQueueTrackIds[i3] == i) {
                    return this.sampleQueues[i3];
                }
            }
            if (this.tracksEnded) {
                return createDummyTrackOutput(i, i2);
            }
        } else if (this.videoSampleQueueIndex != -1) {
            if (this.videoSampleQueueMappingDone) {
                if (this.sampleQueueTrackIds[this.videoSampleQueueIndex] == i) {
                    i = this.sampleQueues[this.videoSampleQueueIndex];
                } else {
                    i = createDummyTrackOutput(i, i2);
                }
                return i;
            }
            this.videoSampleQueueMappingDone = true;
            this.sampleQueueTrackIds[this.videoSampleQueueIndex] = i;
            return this.sampleQueues[this.videoSampleQueueIndex];
        } else if (this.tracksEnded) {
            return createDummyTrackOutput(i, i2);
        }
        TrackOutput sampleQueue = new SampleQueue(this.allocator);
        sampleQueue.setSampleOffsetUs(this.sampleOffsetUs);
        sampleQueue.setUpstreamFormatChangeListener(this);
        int i4 = length + 1;
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, i4);
        this.sampleQueueTrackIds[length] = i;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, i4);
        this.sampleQueues[length] = sampleQueue;
        this.sampleQueueIsAudioVideoFlags = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, i4);
        i = this.sampleQueueIsAudioVideoFlags;
        if (i2 == 1 || i2 == 2) {
            z = true;
        }
        i[length] = z;
        this.haveAudioVideoSampleQueues |= this.sampleQueueIsAudioVideoFlags[length];
        if (i2 == 1) {
            this.audioSampleQueueMappingDone = true;
            this.audioSampleQueueIndex = length;
        } else if (i2 == 2) {
            this.videoSampleQueueMappingDone = true;
            this.videoSampleQueueIndex = length;
        }
        this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, i4);
        return sampleQueue;
    }

    public void endTracks() {
        this.tracksEnded = true;
        this.handler.post(this.onTracksEndedRunnable);
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void setSampleOffsetUs(long j) {
        this.sampleOffsetUs = j;
        for (SampleQueue sampleOffsetUs : this.sampleQueues) {
            sampleOffsetUs.setSampleOffsetUs(j);
        }
    }

    private boolean finishedReadingChunk(HlsMediaChunk hlsMediaChunk) {
        hlsMediaChunk = hlsMediaChunk.uid;
        int length = this.sampleQueues.length;
        int i = 0;
        while (i < length) {
            if (this.sampleQueuesEnabledStates[i] && this.sampleQueues[i].peekSourceId() == hlsMediaChunk) {
                return false;
            }
            i++;
        }
        return true;
    }

    private void resetSampleQueues() {
        for (SampleQueue reset : this.sampleQueues) {
            reset.reset(this.pendingResetUpstreamFormats);
        }
        this.pendingResetUpstreamFormats = false;
    }

    private void onTracksEnded() {
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    private void maybeFinishPrepare() {
        if (!this.released && this.trackGroupToSampleQueueIndex == null) {
            if (this.sampleQueuesBuilt) {
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
                } else {
                    buildTracks();
                    this.prepared = true;
                    this.callback.onPrepared();
                }
            }
        }
    }

    private void mapSampleQueuesToMatchTrackGroups() {
        int i = this.trackGroups.length;
        this.trackGroupToSampleQueueIndex = new int[i];
        Arrays.fill(this.trackGroupToSampleQueueIndex, -1);
        for (int i2 = 0; i2 < i; i2++) {
            for (int i3 = 0; i3 < this.sampleQueues.length; i3++) {
                if (formatsMatch(this.sampleQueues[i3].getUpstreamFormat(), this.trackGroups.get(i2).getFormat(0))) {
                    this.trackGroupToSampleQueueIndex[i2] = i3;
                    break;
                }
            }
        }
    }

    private void buildTracks() {
        int length = this.sampleQueues.length;
        int i = -1;
        int i2 = 0;
        int i3 = i2;
        while (true) {
            boolean z = true;
            if (i2 >= length) {
                break;
            }
            String str = this.sampleQueues[i2].getUpstreamFormat().sampleMimeType;
            if (!MimeTypes.isVideo(str)) {
                z = MimeTypes.isAudio(str) ? true : MimeTypes.isText(str);
            }
            if (z > i3) {
                i = i2;
                i3 = z;
            } else if (z == i3 && i != -1) {
                i = -1;
            }
            i2++;
        }
        TrackGroup trackGroup = this.chunkSource.getTrackGroup();
        int i4 = trackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.trackGroupToSampleQueueIndex = new int[length];
        for (int i5 = 0; i5 < length; i5++) {
            this.trackGroupToSampleQueueIndex[i5] = i5;
        }
        TrackGroup[] trackGroupArr = new TrackGroup[length];
        for (int i6 = 0; i6 < length; i6++) {
            Format upstreamFormat = this.sampleQueues[i6].getUpstreamFormat();
            if (i6 == i) {
                Format[] formatArr = new Format[i4];
                for (int i7 = 0; i7 < i4; i7++) {
                    formatArr[i7] = deriveFormat(trackGroup.getFormat(i7), upstreamFormat, true);
                }
                trackGroupArr[i6] = new TrackGroup(formatArr);
                this.primaryTrackGroupIndex = i6;
            } else {
                Format format = (i3 == 3 && MimeTypes.isAudio(upstreamFormat.sampleMimeType)) ? this.muxedAudioFormat : null;
                trackGroupArr[i6] = new TrackGroup(deriveFormat(format, upstreamFormat, false));
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroupArr);
    }

    private HlsMediaChunk getLastMediaChunk() {
        return (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C0542C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long j) {
        int length = this.sampleQueues.length;
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= length) {
                return true;
            }
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(j, true, false) == -1) {
                z = false;
            }
            if (z || (!this.sampleQueueIsAudioVideoFlags[i] && this.haveAudioVideoSampleQueues)) {
                i++;
            }
        }
        return false;
    }

    private static Format deriveFormat(Format format, Format format2, boolean z) {
        if (format == null) {
            return format2;
        }
        boolean z2 = z ? format.bitrate : true;
        String codecsOfType = Util.getCodecsOfType(format.codecs, MimeTypes.getTrackType(format2.sampleMimeType));
        z = MimeTypes.getMediaMimeType(codecsOfType);
        if (!z) {
            z = format2.sampleMimeType;
        }
        return format2.copyWithContainerInfo(format.id, z, codecsOfType, z2, format.width, format.height, format.selectionFlags, format.language);
    }

    private static boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private static boolean formatsMatch(Format format, Format format2) {
        String str = format.sampleMimeType;
        String str2 = format2.sampleMimeType;
        int trackType = MimeTypes.getTrackType(str);
        boolean z = false;
        if (trackType != 3) {
            if (trackType == MimeTypes.getTrackType(str2)) {
                z = true;
            }
            return z;
        } else if (!Util.areEqual(str, str2)) {
            return false;
        } else {
            if (!MimeTypes.APPLICATION_CEA608.equals(str)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(str)) {
                    return true;
                }
            }
            if (format.accessibilityChannel == format2.accessibilityChannel) {
                z = true;
            }
            return z;
        }
    }

    private static DummyTrackOutput createDummyTrackOutput(int i, int i2) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unmapped track with id ");
        stringBuilder.append(i);
        stringBuilder.append(" of type ");
        stringBuilder.append(i2);
        Log.w(str, stringBuilder.toString());
        return new DummyTrackOutput();
    }
}
