package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public void maybeThrowError() throws IOException {
        }

        public EmbeddedSampleStream(ChunkSampleStream<T> chunkSampleStream, SampleQueue sampleQueue, int i) {
            this.parent = chunkSampleStream;
            this.sampleQueue = sampleQueue;
            this.index = i;
        }

        public boolean isReady() {
            if (!ChunkSampleStream.this.loadingFinished) {
                if (ChunkSampleStream.this.isPendingReset() || !this.sampleQueue.hasNextSample()) {
                    return false;
                }
            }
            return true;
        }

        public int skipData(long j) {
            if (!ChunkSampleStream.this.loadingFinished || j <= this.sampleQueue.getLargestQueuedTimestampUs()) {
                j = this.sampleQueue.advanceTo(j, true, true);
                if (j == -1) {
                    j = null;
                }
            } else {
                j = this.sampleQueue.advanceToEnd();
            }
            if (j > null) {
                maybeNotifyTrackFormatChanged();
            }
            return j;
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            formatHolder = this.sampleQueue.read(formatHolder, decoderInputBuffer, z, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
            if (formatHolder == -4) {
                maybeNotifyTrackFormatChanged();
            }
            return formatHolder;
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

    public ChunkSampleStream(int i, int[] iArr, Format[] formatArr, T t, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long j, int i2, EventDispatcher eventDispatcher) {
        this.primaryTrackType = i;
        this.embeddedTrackTypes = iArr;
        this.embeddedTrackFormats = formatArr;
        this.chunkSource = t;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.minLoadableRetryCount = i2;
        formatArr = null;
        if (iArr == null) {
            t = null;
        } else {
            t = iArr.length;
        }
        this.embeddedSampleQueues = new SampleQueue[t];
        this.embeddedTracksSelected = new boolean[t];
        callback = 1 + t;
        i2 = new int[callback];
        callback = new SampleQueue[callback];
        this.primarySampleQueue = new SampleQueue(allocator);
        i2[0] = i;
        callback[0] = this.primarySampleQueue;
        while (formatArr < t) {
            i = new SampleQueue(allocator);
            this.embeddedSampleQueues[formatArr] = i;
            eventDispatcher = formatArr + 1;
            callback[eventDispatcher] = i;
            i2[eventDispatcher] = iArr[formatArr];
            formatArr = eventDispatcher;
        }
        this.mediaChunkOutput = new BaseMediaChunkOutput(i2, callback);
        this.pendingResetPositionUs = j;
        this.lastSeekPositionUs = j;
    }

    public void discardBuffer(long j, boolean z) {
        int firstIndex = this.primarySampleQueue.getFirstIndex();
        this.primarySampleQueue.discardTo(j, z, true);
        j = this.primarySampleQueue.getFirstIndex();
        if (j > firstIndex) {
            long firstTimestampUs = this.primarySampleQueue.getFirstTimestampUs();
            for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
                this.embeddedSampleQueues[i].discardTo(firstTimestampUs, z, this.embeddedTracksSelected[i]);
            }
            discardDownstreamMediaChunks(j);
        }
    }

    public EmbeddedSampleStream selectEmbeddedTrack(long j, int i) {
        for (int i2 = 0; i2 < this.embeddedSampleQueues.length; i2++) {
            if (this.embeddedTrackTypes[i2] == i) {
                Assertions.checkState(this.embeddedTracksSelected[i2] ^ 1);
                this.embeddedTracksSelected[i2] = 1;
                this.embeddedSampleQueues[i2].rewind();
                this.embeddedSampleQueues[i2].advanceTo(j, true, true);
                return new EmbeddedSampleStream(this, this.embeddedSampleQueues[i2], i2);
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
        long j = this.lastSeekPositionUs;
        BaseMediaChunk lastMediaChunk = getLastMediaChunk();
        if (!lastMediaChunk.isLoadCompleted()) {
            lastMediaChunk = this.mediaChunks.size() > 1 ? (BaseMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        }
        if (lastMediaChunk != null) {
            j = Math.max(j, lastMediaChunk.endTimeUs);
        }
        return Math.max(j, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return this.chunkSource.getAdjustedSeekPositionUs(j, seekParameters);
    }

    public void seekToUs(long j) {
        boolean z;
        int i;
        this.lastSeekPositionUs = j;
        this.primarySampleQueue.rewind();
        int i2 = 0;
        if (isPendingReset()) {
            z = false;
        } else {
            BaseMediaChunk baseMediaChunk = null;
            i = 0;
            while (i < this.mediaChunks.size()) {
                BaseMediaChunk baseMediaChunk2 = (BaseMediaChunk) this.mediaChunks.get(i);
                long j2 = baseMediaChunk2.startTimeUs;
                if (j2 == j) {
                    baseMediaChunk = baseMediaChunk2;
                    break;
                } else if (j2 > j) {
                    break;
                } else {
                    i++;
                }
            }
            if (baseMediaChunk != null) {
                z = this.primarySampleQueue.setReadPosition(baseMediaChunk.getFirstSampleIndex(0));
                this.decodeOnlyUntilPositionUs = Long.MIN_VALUE;
            } else {
                z = this.primarySampleQueue.advanceTo(j, true, (j > getNextLoadPositionUs() ? 1 : (j == getNextLoadPositionUs() ? 0 : -1)) < 0) != -1;
                this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
            }
        }
        if (z) {
            for (SampleQueue sampleQueue : this.embeddedSampleQueues) {
                sampleQueue.rewind();
                sampleQueue.advanceTo(j, true, false);
            }
            return;
        }
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading() != null) {
            this.loader.cancelLoading();
            return;
        }
        this.primarySampleQueue.reset();
        j = this.embeddedSampleQueues;
        int length = j.length;
        while (i2 < length) {
            j[i2].reset();
            i2++;
        }
    }

    public void release() {
        release(null);
    }

    public void release(ReleaseCallback<T> releaseCallback) {
        this.releaseCallback = releaseCallback;
        if (this.loader.release(this) == null) {
            this.primarySampleQueue.discardToEnd();
            for (SampleQueue discardToEnd : this.embeddedSampleQueues) {
                discardToEnd.discardToEnd();
            }
        }
    }

    public void onLoaderReleased() {
        this.primarySampleQueue.reset();
        for (SampleQueue reset : this.embeddedSampleQueues) {
            reset.reset();
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

    public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (isPendingReset()) {
            return -3;
        }
        formatHolder = this.primarySampleQueue.read(formatHolder, decoderInputBuffer, z, this.loadingFinished, this.decodeOnlyUntilPositionUs);
        if (formatHolder == -4) {
            maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), true);
        }
        return formatHolder;
    }

    public int skipData(long j) {
        int i = 0;
        if (isPendingReset()) {
            return 0;
        }
        if (!this.loadingFinished || j <= this.primarySampleQueue.getLargestQueuedTimestampUs()) {
            j = this.primarySampleQueue.advanceTo(j, true, true);
            if (j != -1) {
                i = j;
            }
        } else {
            i = this.primarySampleQueue.advanceToEnd();
        }
        if (i > 0) {
            maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), i);
        }
        return i;
    }

    public void onLoadCompleted(Chunk chunk, long j, long j2) {
        Chunk chunk2 = chunk;
        this.chunkSource.onChunkLoadCompleted(chunk2);
        this.eventDispatcher.loadCompleted(chunk2.dataSpec, chunk2.type, this.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, chunk.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk chunk, long j, long j2, boolean z) {
        Chunk chunk2 = chunk;
        this.eventDispatcher.loadCanceled(chunk2.dataSpec, chunk2.type, this.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, chunk.bytesLoaded());
        if (!z) {
            r0.primarySampleQueue.reset();
            for (SampleQueue reset : r0.embeddedSampleQueues) {
                reset.reset();
            }
            r0.callback.onContinueLoadingRequested(r0);
        }
    }

    public int onLoadError(Chunk chunk, long j, long j2, IOException iOException) {
        boolean z;
        boolean z2;
        Chunk chunk2 = chunk;
        long bytesLoaded = chunk.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(chunk);
        int size = this.mediaChunks.size() - 1;
        if (bytesLoaded != 0 && isMediaChunk) {
            if (haveReadFromMediaChunk(size)) {
                z = false;
                if (r0.chunkSource.onChunkLoadError(chunk2, z, iOException)) {
                    if (z) {
                        Log.w(TAG, "Ignoring attempt to cancel non-cancelable load.");
                    } else {
                        if (isMediaChunk) {
                            Assertions.checkState(discardUpstreamMediaChunksFromIndex(size) != chunk2);
                            if (r0.mediaChunks.isEmpty()) {
                                r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                            }
                        }
                        z2 = true;
                        r0.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, r0.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, bytesLoaded, iOException, z2);
                        if (!z2) {
                            return 0;
                        }
                        r0.callback.onContinueLoadingRequested(r0);
                        return 2;
                    }
                }
                z2 = false;
                r0.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, r0.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, bytesLoaded, iOException, z2);
                if (!z2) {
                    return 0;
                }
                r0.callback.onContinueLoadingRequested(r0);
                return 2;
            }
        }
        z = true;
        if (r0.chunkSource.onChunkLoadError(chunk2, z, iOException)) {
            if (z) {
                if (isMediaChunk) {
                    if (discardUpstreamMediaChunksFromIndex(size) != chunk2) {
                    }
                    Assertions.checkState(discardUpstreamMediaChunksFromIndex(size) != chunk2);
                    if (r0.mediaChunks.isEmpty()) {
                        r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                    }
                }
                z2 = true;
                r0.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, r0.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, bytesLoaded, iOException, z2);
                if (!z2) {
                    return 0;
                }
                r0.callback.onContinueLoadingRequested(r0);
                return 2;
            }
            Log.w(TAG, "Ignoring attempt to cancel non-cancelable load.");
        }
        z2 = false;
        r0.eventDispatcher.loadError(chunk2.dataSpec, chunk2.type, r0.primaryTrackType, chunk2.trackFormat, chunk2.trackSelectionReason, chunk2.trackSelectionData, chunk2.startTimeUs, chunk2.endTimeUs, j, j2, bytesLoaded, iOException, z2);
        if (!z2) {
            return 0;
        }
        r0.callback.onContinueLoadingRequested(r0);
        return 2;
    }

    public boolean continueLoading(long j) {
        boolean z = false;
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                MediaChunk mediaChunk;
                long j2;
                boolean isPendingReset = isPendingReset();
                if (isPendingReset) {
                    mediaChunk = null;
                    j2 = r0.pendingResetPositionUs;
                } else {
                    mediaChunk = getLastMediaChunk();
                    j2 = mediaChunk.endTimeUs;
                }
                r0.chunkSource.getNextChunk(mediaChunk, j, j2, r0.nextChunkHolder);
                boolean z2 = r0.nextChunkHolder.endOfStream;
                Chunk chunk = r0.nextChunkHolder.chunk;
                r0.nextChunkHolder.clear();
                if (z2) {
                    r0.pendingResetPositionUs = C0542C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (chunk == null) {
                    return false;
                } else {
                    if (isMediaChunk(chunk)) {
                        BaseMediaChunk baseMediaChunk = (BaseMediaChunk) chunk;
                        if (isPendingReset) {
                            long j3;
                            if (baseMediaChunk.startTimeUs == r0.pendingResetPositionUs) {
                                z = true;
                            }
                            if (z) {
                                j3 = Long.MIN_VALUE;
                            } else {
                                j3 = r0.pendingResetPositionUs;
                            }
                            r0.decodeOnlyUntilPositionUs = j3;
                            r0.pendingResetPositionUs = C0542C.TIME_UNSET;
                        }
                        baseMediaChunk.init(r0.mediaChunkOutput);
                        r0.mediaChunks.add(baseMediaChunk);
                    }
                    long startLoading = r0.loader.startLoading(chunk, r0, r0.minLoadableRetryCount);
                    r0.eventDispatcher.loadStarted(chunk.dataSpec, chunk.type, r0.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, startLoading);
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

    public void reevaluateBuffer(long j) {
        if (!this.loader.isLoading()) {
            if (!isPendingReset()) {
                int size = this.mediaChunks.size();
                j = this.chunkSource.getPreferredQueueSize(j, this.readOnlyMediaChunks);
                if (size > j) {
                    while (j < size) {
                        if (!haveReadFromMediaChunk(j)) {
                            break;
                        }
                        j++;
                    }
                    j = size;
                    if (j != size) {
                        long j2 = getLastMediaChunk().endTimeUs;
                        j = discardUpstreamMediaChunksFromIndex(j);
                        if (this.mediaChunks.isEmpty()) {
                            this.pendingResetPositionUs = this.lastSeekPositionUs;
                        }
                        this.loadingFinished = false;
                        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, j.startTimeUs, j2);
                    }
                }
            }
        }
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean haveReadFromMediaChunk(int i) {
        BaseMediaChunk baseMediaChunk = (BaseMediaChunk) this.mediaChunks.get(i);
        if (this.primarySampleQueue.getReadIndex() > baseMediaChunk.getFirstSampleIndex(0)) {
            return true;
        }
        int i2 = 0;
        while (i2 < this.embeddedSampleQueues.length) {
            int readIndex = this.embeddedSampleQueues[i2].getReadIndex();
            i2++;
            if (readIndex > baseMediaChunk.getFirstSampleIndex(i2)) {
                return true;
            }
        }
        return false;
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C0542C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int i) {
        i = primaryStreamIndexToMediaChunkIndex(i, 0);
        if (i > 0) {
            Util.removeRange(this.mediaChunks, 0, i);
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int i, int i2) {
        int primaryStreamIndexToMediaChunkIndex = primaryStreamIndexToMediaChunkIndex(i - i2, 0);
        if (i2 == 1) {
            i = primaryStreamIndexToMediaChunkIndex;
        } else {
            i = primaryStreamIndexToMediaChunkIndex(i - 1, primaryStreamIndexToMediaChunkIndex);
        }
        while (primaryStreamIndexToMediaChunkIndex <= i) {
            maybeNotifyPrimaryTrackFormatChanged(primaryStreamIndexToMediaChunkIndex);
            primaryStreamIndexToMediaChunkIndex++;
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int i) {
        BaseMediaChunk baseMediaChunk = (BaseMediaChunk) this.mediaChunks.get(i);
        Format format = baseMediaChunk.trackFormat;
        if (!format.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, format, baseMediaChunk.trackSelectionReason, baseMediaChunk.trackSelectionData, baseMediaChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = format;
    }

    private int primaryStreamIndexToMediaChunkIndex(int i, int i2) {
        do {
            i2++;
            if (i2 >= this.mediaChunks.size()) {
                return this.mediaChunks.size() - 1;
            }
        } while (((BaseMediaChunk) this.mediaChunks.get(i2)).getFirstSampleIndex(0) <= i);
        return i2 - 1;
    }

    private BaseMediaChunk getLastMediaChunk() {
        return (BaseMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 1);
    }

    private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int i) {
        BaseMediaChunk baseMediaChunk = (BaseMediaChunk) this.mediaChunks.get(i);
        Util.removeRange(this.mediaChunks, i, this.mediaChunks.size());
        int i2 = 0;
        this.primarySampleQueue.discardUpstreamSamples(baseMediaChunk.getFirstSampleIndex(0));
        while (i2 < this.embeddedSampleQueues.length) {
            i = this.embeddedSampleQueues[i2];
            i2++;
            i.discardUpstreamSamples(baseMediaChunk.getFirstSampleIndex(i2));
        }
        return baseMediaChunk;
    }
}
