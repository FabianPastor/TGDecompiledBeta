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
    private Format downstreamTrackFormat;
    private final EventDispatcher eventDispatcher;
    private long lastSeekPositionUs;
    private final Loader loader = new Loader("Loader:ChunkSampleStream");
    private boolean loadingFinished;
    private final LinkedList<BaseMediaChunk> mediaChunks = new LinkedList();
    private final int minLoadableRetryCount;
    private final ChunkHolder nextChunkHolder = new ChunkHolder();
    private long pendingResetPositionUs;
    private final List<BaseMediaChunk> readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    private final DefaultTrackOutput sampleQueue;
    private final int trackType;

    public ChunkSampleStream(int trackType, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, int minLoadableRetryCount, EventDispatcher eventDispatcher) {
        this.trackType = trackType;
        this.chunkSource = chunkSource;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.sampleQueue = new DefaultTrackOutput(allocator);
        this.lastSeekPositionUs = positionUs;
        this.pendingResetPositionUs = positionUs;
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
        return Math.max(bufferedPositionUs, this.sampleQueue.getLargestQueuedTimestampUs());
    }

    public void seekToUs(long positionUs) {
        boolean seekInsideBuffer;
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset() || !this.sampleQueue.skipToKeyframeBefore(positionUs)) {
            seekInsideBuffer = false;
        } else {
            seekInsideBuffer = true;
        }
        if (seekInsideBuffer) {
            while (this.mediaChunks.size() > 1 && ((BaseMediaChunk) this.mediaChunks.get(1)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()) {
                this.mediaChunks.removeFirst();
            }
            return;
        }
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
        } else {
            this.sampleQueue.reset(true);
        }
    }

    public void release() {
        this.sampleQueue.disable();
        this.loader.release();
    }

    public boolean isReady() {
        return this.loadingFinished || !(isPendingReset() || this.sampleQueue.isEmpty());
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        if (!this.loader.isLoading()) {
            this.chunkSource.maybeThrowError();
        }
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer) {
        if (isPendingReset()) {
            return -3;
        }
        while (this.mediaChunks.size() > 1 && ((BaseMediaChunk) this.mediaChunks.get(1)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()) {
            this.mediaChunks.removeFirst();
        }
        BaseMediaChunk currentChunk = (BaseMediaChunk) this.mediaChunks.getFirst();
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.downstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.trackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.downstreamTrackFormat = trackFormat;
        return this.sampleQueue.readData(formatHolder, buffer, this.loadingFinished, this.lastSeekPositionUs);
    }

    public void skipToKeyframeBefore(long timeUs) {
        this.sampleQueue.skipToKeyframeBefore(timeUs);
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.chunkSource.onChunkLoadCompleted(loadable);
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            this.sampleQueue.reset(true);
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
                this.sampleQueue.discardUpstreamSamples(removed.getFirstSampleIndex());
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
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
                mediaChunk2.init(this.sampleQueue);
                this.mediaChunks.add(mediaChunk2);
            }
            this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, this.loader.startLoading(loadable, this, this.minLoadableRetryCount));
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

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
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
        this.sampleQueue.discardUpstreamSamples(removed.getFirstSampleIndex());
        this.eventDispatcher.upstreamDiscarded(this.trackType, startTimeUs, endTimeUs);
        return true;
    }
}
