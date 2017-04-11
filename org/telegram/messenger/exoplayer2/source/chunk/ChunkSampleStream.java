package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.util.Assertions;

public class ChunkSampleStream<T extends ChunkSource> implements SampleStream, SequenceableLoader, Callback<Chunk> {
    private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
    private final T chunkSource;
    private final DefaultTrackOutput[] embeddedSampleQueues;
    private final int[] embeddedTrackTypes;
    private final boolean[] embeddedTracksSelected;
    private final EventDispatcher eventDispatcher;
    long lastSeekPositionUs;
    private final Loader loader = new Loader("Loader:ChunkSampleStream");
    boolean loadingFinished;
    private final BaseMediaChunkOutput mediaChunkOutput;
    private final LinkedList<BaseMediaChunk> mediaChunks = new LinkedList();
    private final int minLoadableRetryCount;
    private final ChunkHolder nextChunkHolder = new ChunkHolder();
    private long pendingResetPositionUs;
    private Format primaryDownstreamTrackFormat;
    private final DefaultTrackOutput primarySampleQueue;
    private final int primaryTrackType;
    private final List<BaseMediaChunk> readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);

    public final class EmbeddedSampleStream implements SampleStream {
        private final int index;
        public final ChunkSampleStream<T> parent;
        private final DefaultTrackOutput sampleQueue;

        public EmbeddedSampleStream(ChunkSampleStream<T> parent, DefaultTrackOutput sampleQueue, int index) {
            this.parent = parent;
            this.sampleQueue = sampleQueue;
            this.index = index;
        }

        public boolean isReady() {
            return ChunkSampleStream.this.loadingFinished || !(ChunkSampleStream.this.isPendingReset() || this.sampleQueue.isEmpty());
        }

        public void skipToKeyframeBefore(long timeUs) {
            this.sampleQueue.skipToKeyframeBefore(timeUs);
        }

        public void maybeThrowError() throws IOException {
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            return this.sampleQueue.readData(formatHolder, buffer, formatRequired, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.lastSeekPositionUs);
        }

        public void release() {
            Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
            ChunkSampleStream.this.embeddedTracksSelected[this.index] = false;
        }
    }

    public ChunkSampleStream(int primaryTrackType, int[] embeddedTrackTypes, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, int minLoadableRetryCount, EventDispatcher eventDispatcher) {
        this.primaryTrackType = primaryTrackType;
        this.embeddedTrackTypes = embeddedTrackTypes;
        this.chunkSource = chunkSource;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.minLoadableRetryCount = minLoadableRetryCount;
        int embeddedTrackCount = embeddedTrackTypes == null ? 0 : embeddedTrackTypes.length;
        this.embeddedSampleQueues = new DefaultTrackOutput[embeddedTrackCount];
        this.embeddedTracksSelected = new boolean[embeddedTrackCount];
        int[] trackTypes = new int[(embeddedTrackCount + 1)];
        DefaultTrackOutput[] sampleQueues = new DefaultTrackOutput[(embeddedTrackCount + 1)];
        this.primarySampleQueue = new DefaultTrackOutput(allocator);
        trackTypes[0] = primaryTrackType;
        sampleQueues[0] = this.primarySampleQueue;
        for (int i = 0; i < embeddedTrackCount; i++) {
            DefaultTrackOutput trackOutput = new DefaultTrackOutput(allocator);
            this.embeddedSampleQueues[i] = trackOutput;
            sampleQueues[i + 1] = trackOutput;
            trackTypes[i + 1] = embeddedTrackTypes[i];
        }
        this.mediaChunkOutput = new BaseMediaChunkOutput(trackTypes, sampleQueues);
        this.pendingResetPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
    }

    public void discardUnselectedEmbeddedTracksTo(long positionUs) {
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            if (!this.embeddedTracksSelected[i]) {
                this.embeddedSampleQueues[i].skipToKeyframeBefore(positionUs, true);
            }
        }
    }

    public EmbeddedSampleStream selectEmbeddedTrack(long positionUs, int trackType) {
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            if (this.embeddedTrackTypes[i] == trackType) {
                Assertions.checkState(!this.embeddedTracksSelected[i]);
                this.embeddedTracksSelected[i] = true;
                this.embeddedSampleQueues[i].skipToKeyframeBefore(positionUs, true);
                return new EmbeddedSampleStream(this, this.embeddedSampleQueues[i], i);
            }
        }
        throw new IllegalStateException();
    }

    public T getChunkSource() {
        return this.chunkSource;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long bufferedPositionUs = this.lastSeekPositionUs;
        BaseMediaChunk lastMediaChunk = (BaseMediaChunk) this.mediaChunks.getLast();
        BaseMediaChunk lastCompletedMediaChunk = lastMediaChunk.isLoadCompleted() ? lastMediaChunk : this.mediaChunks.size() > 1 ? (BaseMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        return Math.max(bufferedPositionUs, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public void seekToUs(long positionUs) {
        boolean seekInsideBuffer;
        DefaultTrackOutput[] defaultTrackOutputArr;
        int length;
        int length2;
        int i = 0;
        this.lastSeekPositionUs = positionUs;
        if (!isPendingReset()) {
            boolean z;
            DefaultTrackOutput defaultTrackOutput = this.primarySampleQueue;
            if (positionUs < getNextLoadPositionUs()) {
                z = true;
            } else {
                z = false;
            }
            if (defaultTrackOutput.skipToKeyframeBefore(positionUs, z)) {
                seekInsideBuffer = true;
                if (seekInsideBuffer) {
                    this.pendingResetPositionUs = positionUs;
                    this.loadingFinished = false;
                    this.mediaChunks.clear();
                    if (this.loader.isLoading()) {
                        this.primarySampleQueue.reset(true);
                        defaultTrackOutputArr = this.embeddedSampleQueues;
                        length = defaultTrackOutputArr.length;
                        while (i < length) {
                            defaultTrackOutputArr[i].reset(true);
                            i++;
                        }
                        return;
                    }
                    this.loader.cancelLoading();
                    return;
                }
                while (this.mediaChunks.size() > 1 && ((BaseMediaChunk) this.mediaChunks.get(1)).getFirstSampleIndex(0) <= this.primarySampleQueue.getReadIndex()) {
                    this.mediaChunks.removeFirst();
                }
                defaultTrackOutputArr = this.embeddedSampleQueues;
                length2 = defaultTrackOutputArr.length;
                while (i < length2) {
                    defaultTrackOutputArr[i].skipToKeyframeBefore(positionUs);
                    i++;
                }
            }
        }
        seekInsideBuffer = false;
        if (seekInsideBuffer) {
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            this.mediaChunks.clear();
            if (this.loader.isLoading()) {
                this.primarySampleQueue.reset(true);
                defaultTrackOutputArr = this.embeddedSampleQueues;
                length = defaultTrackOutputArr.length;
                while (i < length) {
                    defaultTrackOutputArr[i].reset(true);
                    i++;
                }
                return;
            }
            this.loader.cancelLoading();
            return;
        }
        while (this.mediaChunks.size() > 1) {
            this.mediaChunks.removeFirst();
        }
        defaultTrackOutputArr = this.embeddedSampleQueues;
        length2 = defaultTrackOutputArr.length;
        while (i < length2) {
            defaultTrackOutputArr[i].skipToKeyframeBefore(positionUs);
            i++;
        }
    }

    public void release() {
        this.primarySampleQueue.disable();
        for (DefaultTrackOutput embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.disable();
        }
        this.loader.release();
    }

    public boolean isReady() {
        return this.loadingFinished || !(isPendingReset() || this.primarySampleQueue.isEmpty());
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        if (!this.loader.isLoading()) {
            this.chunkSource.maybeThrowError();
        }
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (isPendingReset()) {
            return -3;
        }
        discardDownstreamMediaChunks(this.primarySampleQueue.getReadIndex());
        return this.primarySampleQueue.readData(formatHolder, buffer, formatRequired, this.loadingFinished, this.lastSeekPositionUs);
    }

    public void skipToKeyframeBefore(long timeUs) {
        this.primarySampleQueue.skipToKeyframeBefore(timeUs);
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.chunkSource.onChunkLoadCompleted(loadable);
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            this.primarySampleQueue.reset(true);
            for (DefaultTrackOutput embeddedSampleQueue : this.embeddedSampleQueues) {
                embeddedSampleQueue.reset(true);
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public int onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        boolean cancelable = !isMediaChunk || bytesLoaded == 0 || this.mediaChunks.size() > 1;
        boolean canceled = false;
        if (this.chunkSource.onChunkLoadError(loadable, cancelable, error)) {
            canceled = true;
            if (isMediaChunk) {
                Chunk removed = (BaseMediaChunk) this.mediaChunks.removeLast();
                Assertions.checkState(removed == loadable);
                this.primarySampleQueue.discardUpstreamSamples(removed.getFirstSampleIndex(0));
                for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
                    this.embeddedSampleQueues[i].discardUpstreamSamples(removed.getFirstSampleIndex(i + 1));
                }
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
        if (!canceled) {
            return 0;
        }
        this.callback.onContinueLoadingRequested(this);
        return 2;
    }

    public boolean continueLoading(long positionUs) {
        if (this.loadingFinished || this.loader.isLoading()) {
            return false;
        }
        MediaChunk mediaChunk;
        ChunkSource chunkSource = this.chunkSource;
        if (this.mediaChunks.isEmpty()) {
            mediaChunk = null;
        } else {
            BaseMediaChunk baseMediaChunk = (BaseMediaChunk) this.mediaChunks.getLast();
        }
        if (this.pendingResetPositionUs != C.TIME_UNSET) {
            positionUs = this.pendingResetPositionUs;
        }
        chunkSource.getNextChunk(mediaChunk, positionUs, this.nextChunkHolder);
        boolean endOfStream = this.nextChunkHolder.endOfStream;
        Chunk loadable = this.nextChunkHolder.chunk;
        this.nextChunkHolder.clear();
        if (endOfStream) {
            this.loadingFinished = true;
            return true;
        } else if (loadable == null) {
            return false;
        } else {
            if (isMediaChunk(loadable)) {
                this.pendingResetPositionUs = C.TIME_UNSET;
                BaseMediaChunk mediaChunk2 = (BaseMediaChunk) loadable;
                mediaChunk2.init(this.mediaChunkOutput);
                this.mediaChunks.add(mediaChunk2);
            }
            this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, this.loader.startLoading(loadable, this, this.minLoadableRetryCount));
            return true;
        }
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : ((BaseMediaChunk) this.mediaChunks.getLast()).endTimeUs;
    }

    private void maybeDiscardUpstream(long positionUs) {
        discardUpstreamMediaChunks(Math.max(1, this.chunkSource.getPreferredQueueSize(positionUs, this.readOnlyMediaChunks)));
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int primaryStreamReadIndex) {
        while (this.mediaChunks.size() > 1 && ((BaseMediaChunk) this.mediaChunks.get(1)).getFirstSampleIndex(0) <= primaryStreamReadIndex) {
            this.mediaChunks.removeFirst();
        }
        BaseMediaChunk currentChunk = (BaseMediaChunk) this.mediaChunks.getFirst();
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = trackFormat;
    }

    private boolean discardUpstreamMediaChunks(int queueLength) {
        if (this.mediaChunks.size() <= queueLength) {
            return false;
        }
        long startTimeUs = 0;
        long endTimeUs = ((BaseMediaChunk) this.mediaChunks.getLast()).endTimeUs;
        BaseMediaChunk removed = null;
        while (this.mediaChunks.size() > queueLength) {
            removed = (BaseMediaChunk) this.mediaChunks.removeLast();
            startTimeUs = removed.startTimeUs;
            this.loadingFinished = false;
        }
        this.primarySampleQueue.discardUpstreamSamples(removed.getFirstSampleIndex(0));
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            this.embeddedSampleQueues[i].discardUpstreamSamples(removed.getFirstSampleIndex(i + 1));
        }
        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, startTimeUs, endTimeUs);
        return true;
    }
}
