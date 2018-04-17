package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public class ChunkSampleStream<T extends ChunkSource> implements SampleStream, SequenceableLoader, Callback<Chunk>, org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback {
    private static final String TAG = "ChunkSampleStream";
    private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
    private final T chunkSource;
    long decodeOnlyUntilPositionUs;
    private final SampleQueue[] embeddedSampleQueues;
    private final Format[] embeddedTrackFormats;
    private final int[] embeddedTrackTypes;
    private final boolean[] embeddedTracksSelected;
    private final EventDispatcher eventDispatcher;
    private long lastSeekPositionUs;
    private final Loader loader = new Loader("Loader:ChunkSampleStream");
    boolean loadingFinished;
    private final BaseMediaChunkOutput mediaChunkOutput;
    private final ArrayList<BaseMediaChunk> mediaChunks = new ArrayList();
    private final int minLoadableRetryCount;
    private final ChunkHolder nextChunkHolder = new ChunkHolder();
    private long pendingResetPositionUs;
    private Format primaryDownstreamTrackFormat;
    private final SampleQueue primarySampleQueue;
    public final int primaryTrackType;
    private final List<BaseMediaChunk> readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    private ReleaseCallback<T> releaseCallback;

    public interface ReleaseCallback<T extends ChunkSource> {
        void onSampleStreamReleased(ChunkSampleStream<T> chunkSampleStream);
    }

    public final class EmbeddedSampleStream implements SampleStream {
        private boolean formatNotificationSent;
        private final int index;
        public final ChunkSampleStream<T> parent;
        private final SampleQueue sampleQueue;

        public EmbeddedSampleStream(ChunkSampleStream<T> parent, SampleQueue sampleQueue, int index) {
            this.parent = parent;
            this.sampleQueue = sampleQueue;
            this.index = index;
        }

        public boolean isReady() {
            if (!ChunkSampleStream.this.loadingFinished) {
                if (ChunkSampleStream.this.isPendingReset() || !this.sampleQueue.hasNextSample()) {
                    return false;
                }
            }
            return true;
        }

        public int skipData(long positionUs) {
            int skipCount;
            if (!ChunkSampleStream.this.loadingFinished || positionUs <= this.sampleQueue.getLargestQueuedTimestampUs()) {
                skipCount = this.sampleQueue.advanceTo(positionUs, true, true);
                if (skipCount == -1) {
                    skipCount = 0;
                }
            } else {
                skipCount = this.sampleQueue.advanceToEnd();
            }
            if (skipCount > 0) {
                maybeNotifyTrackFormatChanged();
            }
            return skipCount;
        }

        public void maybeThrowError() throws IOException {
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            int result = this.sampleQueue.read(formatHolder, buffer, formatRequired, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
            if (result == -4) {
                maybeNotifyTrackFormatChanged();
            }
            return result;
        }

        public void release() {
            Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
            ChunkSampleStream.this.embeddedTracksSelected[this.index] = false;
        }

        private void maybeNotifyTrackFormatChanged() {
            if (!this.formatNotificationSent) {
                ChunkSampleStream.this.eventDispatcher.downstreamFormatChanged(ChunkSampleStream.this.embeddedTrackTypes[this.index], ChunkSampleStream.this.embeddedTrackFormats[this.index], 0, null, ChunkSampleStream.this.lastSeekPositionUs);
                this.formatNotificationSent = true;
            }
        }
    }

    public ChunkSampleStream(int primaryTrackType, int[] embeddedTrackTypes, Format[] embeddedTrackFormats, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, int minLoadableRetryCount, EventDispatcher eventDispatcher) {
        int i = primaryTrackType;
        int[] iArr = embeddedTrackTypes;
        Allocator allocator2 = allocator;
        long j = positionUs;
        this.primaryTrackType = i;
        this.embeddedTrackTypes = iArr;
        this.embeddedTrackFormats = embeddedTrackFormats;
        this.chunkSource = chunkSource;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.minLoadableRetryCount = minLoadableRetryCount;
        int i2 = 0;
        int embeddedTrackCount = iArr == null ? 0 : iArr.length;
        r0.embeddedSampleQueues = new SampleQueue[embeddedTrackCount];
        r0.embeddedTracksSelected = new boolean[embeddedTrackCount];
        int[] trackTypes = new int[(1 + embeddedTrackCount)];
        SampleQueue[] sampleQueues = new SampleQueue[(1 + embeddedTrackCount)];
        r0.primarySampleQueue = new SampleQueue(allocator2);
        trackTypes[0] = i;
        sampleQueues[0] = r0.primarySampleQueue;
        while (i2 < embeddedTrackCount) {
            SampleQueue sampleQueue = new SampleQueue(allocator2);
            r0.embeddedSampleQueues[i2] = sampleQueue;
            sampleQueues[i2 + 1] = sampleQueue;
            trackTypes[i2 + 1] = iArr[i2];
            i2++;
            i = primaryTrackType;
        }
        r0.mediaChunkOutput = new BaseMediaChunkOutput(trackTypes, sampleQueues);
        r0.pendingResetPositionUs = j;
        r0.lastSeekPositionUs = j;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        int oldFirstIndex = this.primarySampleQueue.getFirstIndex();
        this.primarySampleQueue.discardTo(positionUs, toKeyframe, true);
        int newFirstIndex = this.primarySampleQueue.getFirstIndex();
        if (newFirstIndex > oldFirstIndex) {
            long discardToUs = this.primarySampleQueue.getFirstTimestampUs();
            for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
                this.embeddedSampleQueues[i].discardTo(discardToUs, toKeyframe, this.embeddedTracksSelected[i]);
            }
            discardDownstreamMediaChunks(newFirstIndex);
        }
    }

    public EmbeddedSampleStream selectEmbeddedTrack(long positionUs, int trackType) {
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            if (this.embeddedTrackTypes[i] == trackType) {
                Assertions.checkState(this.embeddedTracksSelected[i] ^ true);
                this.embeddedTracksSelected[i] = true;
                this.embeddedSampleQueues[i].rewind();
                this.embeddedSampleQueues[i].advanceTo(positionUs, true, true);
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
        BaseMediaChunk lastMediaChunk = getLastMediaChunk();
        BaseMediaChunk lastCompletedMediaChunk = lastMediaChunk.isLoadCompleted() ? lastMediaChunk : this.mediaChunks.size() > 1 ? (BaseMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        return Math.max(bufferedPositionUs, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.chunkSource.getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public void seekToUs(long positionUs) {
        boolean seekInsideBuffer;
        this.lastSeekPositionUs = positionUs;
        this.primarySampleQueue.rewind();
        int i = 0;
        if (isPendingReset()) {
            seekInsideBuffer = false;
        } else {
            BaseMediaChunk seekToMediaChunk = null;
            int i2 = 0;
            while (i2 < this.mediaChunks.size()) {
                BaseMediaChunk mediaChunk = (BaseMediaChunk) this.mediaChunks.get(i2);
                long mediaChunkStartTimeUs = mediaChunk.startTimeUs;
                if (mediaChunkStartTimeUs == positionUs) {
                    seekToMediaChunk = mediaChunk;
                    break;
                } else if (mediaChunkStartTimeUs > positionUs) {
                    break;
                } else {
                    i2++;
                }
            }
            if (seekToMediaChunk != null) {
                seekInsideBuffer = this.primarySampleQueue.setReadPosition(seekToMediaChunk.getFirstSampleIndex(0));
                this.decodeOnlyUntilPositionUs = Long.MIN_VALUE;
            } else {
                seekInsideBuffer = this.primarySampleQueue.advanceTo(positionUs, true, (positionUs > getNextLoadPositionUs() ? 1 : (positionUs == getNextLoadPositionUs() ? 0 : -1)) < 0) != -1;
                this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
            }
        }
        if (seekInsideBuffer) {
            for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
                embeddedSampleQueue.rewind();
                embeddedSampleQueue.advanceTo(positionUs, true, false);
            }
            return;
        }
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        this.primarySampleQueue.reset();
        SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
        int length = sampleQueueArr.length;
        while (i < length) {
            sampleQueueArr[i].reset();
            i++;
        }
    }

    public void release() {
        release(null);
    }

    public void release(ReleaseCallback<T> callback) {
        this.releaseCallback = callback;
        if (!this.loader.release(this)) {
            this.primarySampleQueue.discardToEnd();
            for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
                embeddedSampleQueue.discardToEnd();
            }
        }
    }

    public void onLoaderReleased() {
        this.primarySampleQueue.reset();
        for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.reset();
        }
        if (this.releaseCallback != null) {
            this.releaseCallback.onSampleStreamReleased(this);
        }
    }

    public boolean isReady() {
        if (!this.loadingFinished) {
            if (isPendingReset() || !this.primarySampleQueue.hasNextSample()) {
                return false;
            }
        }
        return true;
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
        int result = this.primarySampleQueue.read(formatHolder, buffer, formatRequired, this.loadingFinished, this.decodeOnlyUntilPositionUs);
        if (result == -4) {
            maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), 1);
        }
        return result;
    }

    public int skipData(long positionUs) {
        if (isPendingReset()) {
            return 0;
        }
        int skipCount;
        if (!this.loadingFinished || positionUs <= this.primarySampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = this.primarySampleQueue.advanceTo(positionUs, true, true);
            if (skipCount == -1) {
                skipCount = 0;
            }
        } else {
            skipCount = this.primarySampleQueue.advanceToEnd();
        }
        if (skipCount > 0) {
            maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), skipCount);
        }
        return skipCount;
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        Chunk chunk = loadable;
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        Chunk chunk = loadable;
        this.eventDispatcher.loadCanceled(chunk.dataSpec, chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            r0.primarySampleQueue.reset();
            for (SampleQueue embeddedSampleQueue : r0.embeddedSampleQueues) {
                embeddedSampleQueue.reset();
            }
            r0.callback.onContinueLoadingRequested(r0);
        }
    }

    public int onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean z;
        boolean cancelable;
        boolean canceled;
        Chunk chunk = loadable;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        boolean z2 = true;
        int lastChunkIndex = this.mediaChunks.size() - 1;
        if (bytesLoaded != 0 && isMediaChunk) {
            if (haveReadFromMediaChunk(lastChunkIndex)) {
                z = false;
                cancelable = z;
                z = false;
                if (r0.chunkSource.onChunkLoadError(chunk, cancelable, error)) {
                    if (cancelable) {
                        Log.w(TAG, "Ignoring attempt to cancel non-cancelable load.");
                    } else {
                        z = true;
                        if (isMediaChunk) {
                            if (discardUpstreamMediaChunksFromIndex(lastChunkIndex) == chunk) {
                                z2 = false;
                            }
                            Assertions.checkState(z2);
                            if (r0.mediaChunks.isEmpty()) {
                                r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                            }
                        }
                    }
                }
                canceled = z;
                r0.eventDispatcher.loadError(chunk.dataSpec, chunk.type, r0.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
                if (canceled) {
                    return 0;
                }
                r0.callback.onContinueLoadingRequested(r0);
                return 2;
            }
        }
        z = true;
        cancelable = z;
        z = false;
        if (r0.chunkSource.onChunkLoadError(chunk, cancelable, error)) {
            if (cancelable) {
                z = true;
                if (isMediaChunk) {
                    if (discardUpstreamMediaChunksFromIndex(lastChunkIndex) == chunk) {
                        z2 = false;
                    }
                    Assertions.checkState(z2);
                    if (r0.mediaChunks.isEmpty()) {
                        r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                    }
                }
            } else {
                Log.w(TAG, "Ignoring attempt to cancel non-cancelable load.");
            }
        }
        canceled = z;
        r0.eventDispatcher.loadError(chunk.dataSpec, chunk.type, r0.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
        if (canceled) {
            return 0;
        }
        r0.callback.onContinueLoadingRequested(r0);
        return 2;
    }

    public boolean continueLoading(long positionUs) {
        boolean resetToMediaChunk = false;
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                MediaChunk previousChunk;
                long j;
                boolean pendingReset = isPendingReset();
                if (pendingReset) {
                    previousChunk = null;
                    j = r0.pendingResetPositionUs;
                } else {
                    previousChunk = getLastMediaChunk();
                    j = previousChunk.endTimeUs;
                }
                r0.chunkSource.getNextChunk(previousChunk, positionUs, j, r0.nextChunkHolder);
                boolean endOfStream = r0.nextChunkHolder.endOfStream;
                Chunk loadable = r0.nextChunkHolder.chunk;
                r0.nextChunkHolder.clear();
                if (endOfStream) {
                    r0.pendingResetPositionUs = C0539C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (loadable == null) {
                    return false;
                } else {
                    if (isMediaChunk(loadable)) {
                        BaseMediaChunk mediaChunk = (BaseMediaChunk) loadable;
                        if (pendingReset) {
                            if (mediaChunk.startTimeUs == r0.pendingResetPositionUs) {
                                resetToMediaChunk = true;
                            }
                            r0.decodeOnlyUntilPositionUs = resetToMediaChunk ? Long.MIN_VALUE : r0.pendingResetPositionUs;
                            r0.pendingResetPositionUs = C0539C.TIME_UNSET;
                        }
                        mediaChunk.init(r0.mediaChunkOutput);
                        r0.mediaChunks.add(mediaChunk);
                    }
                    r0.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, r0.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, r0.loader.startLoading(loadable, r0, r0.minLoadableRetryCount));
                    return true;
                }
            }
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : getLastMediaChunk().endTimeUs;
    }

    public void reevaluateBuffer(long positionUs) {
        if (!this.loader.isLoading()) {
            if (!isPendingReset()) {
                int currentQueueSize = this.mediaChunks.size();
                int preferredQueueSize = this.chunkSource.getPreferredQueueSize(positionUs, this.readOnlyMediaChunks);
                if (currentQueueSize > preferredQueueSize) {
                    int newQueueSize = currentQueueSize;
                    for (int i = preferredQueueSize; i < currentQueueSize; i++) {
                        if (!haveReadFromMediaChunk(i)) {
                            newQueueSize = i;
                            break;
                        }
                    }
                    if (newQueueSize != currentQueueSize) {
                        long endTimeUs = getLastMediaChunk().endTimeUs;
                        BaseMediaChunk firstRemovedChunk = discardUpstreamMediaChunksFromIndex(newQueueSize);
                        if (this.mediaChunks.isEmpty()) {
                            this.pendingResetPositionUs = this.lastSeekPositionUs;
                        }
                        this.loadingFinished = false;
                        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, firstRemovedChunk.startTimeUs, endTimeUs);
                    }
                }
            }
        }
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean haveReadFromMediaChunk(int mediaChunkIndex) {
        BaseMediaChunk mediaChunk = (BaseMediaChunk) this.mediaChunks.get(mediaChunkIndex);
        if (this.primarySampleQueue.getReadIndex() > mediaChunk.getFirstSampleIndex(0)) {
            return true;
        }
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            if (this.embeddedSampleQueues[i].getReadIndex() > mediaChunk.getFirstSampleIndex(i + 1)) {
                return true;
            }
        }
        return false;
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C0539C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int discardToPrimaryStreamIndex) {
        int discardToMediaChunkIndex = primaryStreamIndexToMediaChunkIndex(discardToPrimaryStreamIndex, 0);
        if (discardToMediaChunkIndex > 0) {
            Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int toPrimaryStreamReadIndex, int readCount) {
        int fromMediaChunkIndex = primaryStreamIndexToMediaChunkIndex(toPrimaryStreamReadIndex - readCount, 0);
        int toMediaChunkIndexInclusive = readCount == 1 ? fromMediaChunkIndex : primaryStreamIndexToMediaChunkIndex(toPrimaryStreamReadIndex - 1, fromMediaChunkIndex);
        for (int i = fromMediaChunkIndex; i <= toMediaChunkIndexInclusive; i++) {
            maybeNotifyPrimaryTrackFormatChanged(i);
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int mediaChunkReadIndex) {
        BaseMediaChunk currentChunk = (BaseMediaChunk) this.mediaChunks.get(mediaChunkReadIndex);
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = trackFormat;
    }

    private int primaryStreamIndexToMediaChunkIndex(int primaryStreamIndex, int minChunkIndex) {
        for (int i = minChunkIndex + 1; i < this.mediaChunks.size(); i++) {
            if (((BaseMediaChunk) this.mediaChunks.get(i)).getFirstSampleIndex(0) > primaryStreamIndex) {
                return i - 1;
            }
        }
        return this.mediaChunks.size() - 1;
    }

    private BaseMediaChunk getLastMediaChunk() {
        return (BaseMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 1);
    }

    private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int chunkIndex) {
        BaseMediaChunk firstRemovedChunk = (BaseMediaChunk) this.mediaChunks.get(chunkIndex);
        Util.removeRange(this.mediaChunks, chunkIndex, this.mediaChunks.size());
        int i = 0;
        this.primarySampleQueue.discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(0));
        while (true) {
            int i2 = i;
            if (i2 >= this.embeddedSampleQueues.length) {
                return firstRemovedChunk;
            }
            this.embeddedSampleQueues[i2].discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(i2 + 1));
            i = i2 + 1;
        }
    }
}
