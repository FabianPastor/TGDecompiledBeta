package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0539C;
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
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.hls.HlsChunkSource.HlsChunkHolder;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
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
    private final Runnable maybeFinishPrepareRunnable = new C06121();
    private final ArrayList<HlsMediaChunk> mediaChunks = new ArrayList();
    private final int minLoadableRetryCount;
    private final Format muxedAudioFormat;
    private final HlsChunkHolder nextChunkHolder = new HlsChunkHolder();
    private final Runnable onTracksEndedRunnable = new C06132();
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
    class C06121 implements Runnable {
        C06121() {
        }

        public void run() {
            HlsSampleStreamWrapper.this.maybeFinishPrepare();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper$2 */
    class C06132 implements Runnable {
        C06132() {
        }

        public void run() {
            HlsSampleStreamWrapper.this.onTracksEnded();
        }
    }

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
        if (sampleQueueIndex == -1 || this.sampleQueuesEnabledStates[sampleQueueIndex]) {
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

    public boolean selectTracks(org.telegram.messenger.exoplayer2.trackselection.TrackSelection[] r22, boolean[] r23, org.telegram.messenger.exoplayer2.source.SampleStream[] r24, boolean[] r25, long r26, boolean r28) {
        /* JADX: method processing error */
/*
Error: java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
	at java.util.BitSet.get(BitSet.java:623)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.usedArgAssign(CodeShrinker.java:138)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.access$300(CodeShrinker.java:43)
	at jadx.core.dex.visitors.CodeShrinker.canMoveBetweenBlocks(CodeShrinker.java:282)
	at jadx.core.dex.visitors.CodeShrinker.shrinkBlock(CodeShrinker.java:232)
	at jadx.core.dex.visitors.CodeShrinker.shrinkMethod(CodeShrinker.java:38)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkArrayForEach(LoopRegionVisitor.java:196)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkForIndexedLoop(LoopRegionVisitor.java:119)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.processLoopRegion(LoopRegionVisitor.java:65)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.enterRegion(LoopRegionVisitor.java:52)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:56)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.visit(LoopRegionVisitor.java:46)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r21 = this;
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r10 = r26;
        r3 = r0.prepared;
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r3);
        r12 = r0.enabledTrackGroupCount;
        r3 = 0;
    L_0x0010:
        r4 = 0;
        r14 = 1;
        r5 = r1.length;
        if (r3 >= r5) goto L_0x0032;
    L_0x0015:
        r5 = r2[r3];
        if (r5 == 0) goto L_0x002f;
    L_0x0019:
        r5 = r1[r3];
        if (r5 == 0) goto L_0x0021;
    L_0x001d:
        r5 = r23[r3];
        if (r5 != 0) goto L_0x002f;
    L_0x0021:
        r5 = r0.enabledTrackGroupCount;
        r5 = r5 - r14;
        r0.enabledTrackGroupCount = r5;
        r5 = r2[r3];
        r5 = (org.telegram.messenger.exoplayer2.source.hls.HlsSampleStream) r5;
        r5.unbindSampleQueue();
        r2[r3] = r4;
    L_0x002f:
        r3 = r3 + 1;
        goto L_0x0010;
    L_0x0032:
        if (r28 != 0) goto L_0x0044;
    L_0x0034:
        r3 = r0.seenFirstTrackSelection;
        if (r3 == 0) goto L_0x003b;
    L_0x0038:
        if (r12 != 0) goto L_0x0042;
    L_0x003a:
        goto L_0x0044;
    L_0x003b:
        r5 = r0.lastSeekPositionUs;
        r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1));
        if (r3 == 0) goto L_0x0042;
    L_0x0041:
        goto L_0x0044;
    L_0x0042:
        r3 = 0;
        goto L_0x0045;
    L_0x0044:
        r3 = r14;
    L_0x0045:
        r5 = r0.chunkSource;
        r8 = r5.getTrackSelection();
        r5 = r8;
        r16 = r3;
        r9 = r5;
        r3 = 0;
    L_0x0050:
        r5 = r1.length;
        if (r3 >= r5) goto L_0x00a8;
    L_0x0053:
        r5 = r2[r3];
        if (r5 != 0) goto L_0x00a4;
    L_0x0057:
        r5 = r1[r3];
        if (r5 == 0) goto L_0x00a4;
    L_0x005b:
        r5 = r0.enabledTrackGroupCount;
        r5 = r5 + r14;
        r0.enabledTrackGroupCount = r5;
        r5 = r1[r3];
        r6 = r0.trackGroups;
        r7 = r5.getTrackGroup();
        r6 = r6.indexOf(r7);
        r7 = r0.primaryTrackGroupIndex;
        if (r6 != r7) goto L_0x0077;
    L_0x0070:
        r7 = r5;
        r9 = r0.chunkSource;
        r9.selectTracks(r5);
        r9 = r7;
    L_0x0077:
        r7 = new org.telegram.messenger.exoplayer2.source.hls.HlsSampleStream;
        r7.<init>(r0, r6);
        r2[r3] = r7;
        r25[r3] = r14;
        r7 = r0.sampleQueuesBuilt;
        if (r7 == 0) goto L_0x00a4;
    L_0x0084:
        if (r16 != 0) goto L_0x00a4;
    L_0x0086:
        r7 = r0.sampleQueues;
        r13 = r0.trackGroupToSampleQueueIndex;
        r13 = r13[r6];
        r7 = r7[r13];
        r7.rewind();
        r13 = r7.advanceTo(r10, r14, r14);
        r14 = -1;
        if (r13 != r14) goto L_0x00a0;
    L_0x0098:
        r13 = r7.getReadIndex();
        if (r13 == 0) goto L_0x00a0;
    L_0x009e:
        r13 = 1;
        goto L_0x00a1;
    L_0x00a0:
        r13 = 0;
    L_0x00a1:
        r5 = r13;
        r16 = r5;
    L_0x00a4:
        r3 = r3 + 1;
        r14 = 1;
        goto L_0x0050;
    L_0x00a8:
        r3 = r0.enabledTrackGroupCount;
        if (r3 != 0) goto L_0x00e2;
    L_0x00ac:
        r3 = r0.chunkSource;
        r3.reset();
        r0.downstreamTrackFormat = r4;
        r3 = r0.mediaChunks;
        r3.clear();
        r3 = r0.loader;
        r3 = r3.isLoading();
        if (r3 == 0) goto L_0x00d8;
    L_0x00c0:
        r3 = r0.sampleQueuesBuilt;
        if (r3 == 0) goto L_0x00d2;
    L_0x00c4:
        r3 = r0.sampleQueues;
        r4 = r3.length;
        r5 = 0;
    L_0x00c8:
        if (r5 >= r4) goto L_0x00d2;
    L_0x00ca:
        r6 = r3[r5];
        r6.discardToEnd();
        r5 = r5 + 1;
        goto L_0x00c8;
    L_0x00d2:
        r3 = r0.loader;
        r3.cancelLoading();
        goto L_0x00db;
    L_0x00d8:
        r21.resetSampleQueues();
    L_0x00db:
        r15 = r28;
        r14 = r8;
        r1 = r9;
        r5 = 1;
        goto L_0x0150;
    L_0x00e2:
        r3 = r0.mediaChunks;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0132;
    L_0x00ea:
        r3 = org.telegram.messenger.exoplayer2.util.Util.areEqual(r9, r8);
        if (r3 != 0) goto L_0x0132;
    L_0x00f0:
        r13 = 0;
        r3 = r0.seenFirstTrackSelection;
        if (r3 != 0) goto L_0x0125;
    L_0x00f5:
        r3 = 0;
        r5 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x00fe;
    L_0x00fb:
        r3 = -r10;
    L_0x00fc:
        r6 = r3;
        goto L_0x00ff;
    L_0x00fe:
        goto L_0x00fc;
    L_0x00ff:
        r18 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r3 = r9;
        r4 = r10;
        r14 = r8;
        r1 = r9;
        r8 = r18;
        r3.updateSelectedTrack(r4, r6, r8);
        r3 = r0.chunkSource;
        r3 = r3.getTrackGroup();
        r4 = r21.getLastMediaChunk();
        r4 = r4.trackFormat;
        r3 = r3.indexOf(r4);
        r4 = r1.getSelectedIndexInTrackGroup();
        if (r4 == r3) goto L_0x0124;
    L_0x0123:
        r13 = 1;
    L_0x0124:
        goto L_0x0128;
    L_0x0125:
        r14 = r8;
        r1 = r9;
        r13 = 1;
    L_0x0128:
        if (r13 == 0) goto L_0x0134;
    L_0x012a:
        r3 = 1;
        r4 = 1;
        r5 = 1;
        r0.pendingResetUpstreamFormats = r5;
        r16 = r4;
        goto L_0x0136;
    L_0x0132:
        r14 = r8;
        r1 = r9;
    L_0x0134:
        r3 = r28;
    L_0x0136:
        if (r16 == 0) goto L_0x014e;
    L_0x0138:
        r0.seekToUs(r10, r3);
        r17 = 0;
    L_0x013d:
        r4 = r17;
        r5 = r2.length;
        if (r4 >= r5) goto L_0x014e;
    L_0x0142:
        r5 = r2[r4];
        if (r5 == 0) goto L_0x014a;
    L_0x0146:
        r5 = 1;
        r25[r4] = r5;
        goto L_0x014b;
    L_0x014a:
        r5 = 1;
    L_0x014b:
        r17 = r4 + 1;
        goto L_0x013d;
    L_0x014e:
        r5 = 1;
        r15 = r3;
    L_0x0150:
        r0.seenFirstTrackSelection = r5;
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper.selectTracks(org.telegram.messenger.exoplayer2.trackselection.TrackSelection[], boolean[], org.telegram.messenger.exoplayer2.source.SampleStream[], boolean[], long, boolean):boolean");
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
        if (!this.loadingFinished) {
            if (isPendingReset() || !this.sampleQueues[sampleQueueIndex].hasNextSample()) {
                return false;
            }
        }
        return true;
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
        int i = 0;
        if (isPendingReset()) {
            return 0;
        }
        SampleQueue sampleQueue = this.sampleQueues[sampleQueueIndex];
        if (this.loadingFinished && positionUs > sampleQueue.getLargestQueuedTimestampUs()) {
            return sampleQueue.advanceToEnd();
        }
        int skipCount = sampleQueue.advanceTo(positionUs, true, true);
        if (skipCount != -1) {
            i = skipCount;
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
        long bufferedPositionUs = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = getLastMediaChunk();
        HlsMediaChunk lastCompletedMediaChunk = lastMediaChunk.isLoadCompleted() ? lastMediaChunk : this.mediaChunks.size() > 1 ? (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 2) : null;
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        if (this.sampleQueuesBuilt) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                bufferedPositionUs = Math.max(bufferedPositionUs, sampleQueue.getLargestQueuedTimestampUs());
            }
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
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                HlsMediaChunk previousChunk;
                long j;
                if (isPendingReset()) {
                    previousChunk = null;
                    j = r0.pendingResetPositionUs;
                } else {
                    previousChunk = getLastMediaChunk();
                    j = previousChunk.endTimeUs;
                }
                long loadPositionUs = j;
                r0.chunkSource.getNextChunk(previousChunk, positionUs, loadPositionUs, r0.nextChunkHolder);
                boolean endOfStream = r0.nextChunkHolder.endOfStream;
                Chunk loadable = r0.nextChunkHolder.chunk;
                HlsUrl playlistToLoad = r0.nextChunkHolder.playlist;
                r0.nextChunkHolder.clear();
                if (endOfStream) {
                    r0.pendingResetPositionUs = C0539C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (loadable == null) {
                    if (playlistToLoad != null) {
                        r0.callback.onPlaylistRefreshRequired(playlistToLoad);
                    }
                    return false;
                } else {
                    if (isMediaChunk(loadable)) {
                        r0.pendingResetPositionUs = C0539C.TIME_UNSET;
                        HlsMediaChunk mediaChunk = (HlsMediaChunk) loadable;
                        mediaChunk.init(r0);
                        r0.mediaChunks.add(mediaChunk);
                    }
                    long elapsedRealtimeMs = r0.loader.startLoading(loadable, r0, r0.minLoadableRetryCount);
                    r0.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, r0.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs);
                    return true;
                }
            }
        }
        return false;
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        Chunk chunk = loadable;
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (this.prepared) {
            r0.callback.onContinueLoadingRequested(r0);
        } else {
            continueLoading(r0.lastSeekPositionUs);
        }
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        Chunk chunk = loadable;
        this.eventDispatcher.loadCanceled(chunk.dataSpec, chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            resetSampleQueues();
            if (r0.enabledTrackGroupCount > 0) {
                r0.callback.onContinueLoadingRequested(r0);
            }
        }
    }

    public int onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean z;
        boolean cancelable;
        boolean canceled;
        HlsSampleStreamWrapper hlsSampleStreamWrapper = this;
        Chunk chunk = loadable;
        IOException iOException = error;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        boolean z2 = true;
        int i = 0;
        if (isMediaChunk) {
            if (bytesLoaded != 0) {
                z = false;
                cancelable = z;
                if (hlsSampleStreamWrapper.chunkSource.onChunkLoadError(chunk, cancelable, iOException)) {
                    canceled = false;
                } else {
                    if (isMediaChunk) {
                        if (((HlsMediaChunk) hlsSampleStreamWrapper.mediaChunks.remove(hlsSampleStreamWrapper.mediaChunks.size() - 1)) == chunk) {
                            z2 = false;
                        }
                        Assertions.checkState(z2);
                        if (hlsSampleStreamWrapper.mediaChunks.isEmpty()) {
                            hlsSampleStreamWrapper.pendingResetPositionUs = hlsSampleStreamWrapper.lastSeekPositionUs;
                        }
                    }
                    canceled = true;
                }
                hlsSampleStreamWrapper.eventDispatcher.loadError(chunk.dataSpec, chunk.type, hlsSampleStreamWrapper.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, canceled);
                if (canceled) {
                    if (error instanceof ParserException) {
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
        cancelable = z;
        if (hlsSampleStreamWrapper.chunkSource.onChunkLoadError(chunk, cancelable, iOException)) {
            canceled = false;
        } else {
            if (isMediaChunk) {
                if (((HlsMediaChunk) hlsSampleStreamWrapper.mediaChunks.remove(hlsSampleStreamWrapper.mediaChunks.size() - 1)) == chunk) {
                    z2 = false;
                }
                Assertions.checkState(z2);
                if (hlsSampleStreamWrapper.mediaChunks.isEmpty()) {
                    hlsSampleStreamWrapper.pendingResetPositionUs = hlsSampleStreamWrapper.lastSeekPositionUs;
                }
            }
            canceled = true;
        }
        hlsSampleStreamWrapper.eventDispatcher.loadError(chunk.dataSpec, chunk.type, hlsSampleStreamWrapper.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, canceled);
        if (canceled) {
            if (error instanceof ParserException) {
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

    public void init(int chunkUid, boolean shouldSpliceIn, boolean reusingExtractor) {
        int length;
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
            length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].splice();
                i++;
            }
        }
    }

    public TrackOutput track(int id, int type) {
        boolean z = false;
        int trackCount = this.sampleQueues.length;
        TrackOutput trackOutput;
        if (type == 1) {
            if (this.audioSampleQueueIndex != -1) {
                if (this.audioSampleQueueMappingDone) {
                    if (this.sampleQueueTrackIds[this.audioSampleQueueIndex] == id) {
                        trackOutput = this.sampleQueues[this.audioSampleQueueIndex];
                    } else {
                        trackOutput = createDummyTrackOutput(id, type);
                    }
                    return trackOutput;
                }
                this.audioSampleQueueMappingDone = true;
                this.sampleQueueTrackIds[this.audioSampleQueueIndex] = id;
                return this.sampleQueues[this.audioSampleQueueIndex];
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
            if (this.videoSampleQueueMappingDone) {
                if (this.sampleQueueTrackIds[this.videoSampleQueueIndex] == id) {
                    trackOutput = this.sampleQueues[this.videoSampleQueueIndex];
                } else {
                    trackOutput = createDummyTrackOutput(id, type);
                }
                return trackOutput;
            }
            this.videoSampleQueueMappingDone = true;
            this.sampleQueueTrackIds[this.videoSampleQueueIndex] = id;
            return this.sampleQueues[this.videoSampleQueueIndex];
        } else if (this.tracksEnded) {
            return createDummyTrackOutput(id, type);
        }
        SampleQueue trackOutput2 = new SampleQueue(this.allocator);
        trackOutput2.setSampleOffsetUs(this.sampleOffsetUs);
        trackOutput2.setUpstreamFormatChangeListener(this);
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds[trackCount] = id;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        this.sampleQueues[trackCount] = trackOutput2;
        this.sampleQueueIsAudioVideoFlags = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, trackCount + 1);
        boolean[] zArr = this.sampleQueueIsAudioVideoFlags;
        if (type != 1) {
            if (type != 2) {
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
                return trackOutput2;
            }
        }
        z = true;
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
        return trackOutput2;
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
        int extractorTrackCount = this.sampleQueues.length;
        int primaryExtractorTrackIndex = -1;
        int primaryExtractorTrackType = 0;
        for (int i = 0; i < extractorTrackCount; i++) {
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
                if (trackType <= primaryExtractorTrackType) {
                    primaryExtractorTrackType = trackType;
                    primaryExtractorTrackIndex = i;
                } else if (trackType == primaryExtractorTrackType && primaryExtractorTrackIndex != -1) {
                    primaryExtractorTrackIndex = -1;
                }
            }
            if (trackType <= primaryExtractorTrackType) {
                primaryExtractorTrackIndex = -1;
            } else {
                primaryExtractorTrackType = trackType;
                primaryExtractorTrackIndex = i;
            }
        }
        TrackGroup chunkSourceTrackGroup = this.chunkSource.getTrackGroup();
        int chunkSourceTrackCount = chunkSourceTrackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.trackGroupToSampleQueueIndex = new int[extractorTrackCount];
        for (int i2 = 0; i2 < extractorTrackCount; i2++) {
            this.trackGroupToSampleQueueIndex[i2] = i2;
        }
        TrackGroup[] trackGroups = new TrackGroup[extractorTrackCount];
        for (trackType = 0; trackType < extractorTrackCount; trackType++) {
            Format sampleFormat = this.sampleQueues[trackType].getUpstreamFormat();
            if (trackType == primaryExtractorTrackIndex) {
                Format[] formats = new Format[chunkSourceTrackCount];
                for (int j = 0; j < chunkSourceTrackCount; j++) {
                    formats[j] = deriveFormat(chunkSourceTrackGroup.getFormat(j), sampleFormat, true);
                }
                trackGroups[trackType] = new TrackGroup(formats);
                this.primaryTrackGroupIndex = trackType;
            } else {
                Format trackFormat = (primaryExtractorTrackType == 3 && MimeTypes.isAudio(sampleFormat.sampleMimeType)) ? this.muxedAudioFormat : null;
                trackGroups[trackType] = new TrackGroup(deriveFormat(trackFormat, sampleFormat, false));
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroups);
    }

    private HlsMediaChunk getLastMediaChunk() {
        return (HlsMediaChunk) this.mediaChunks.get(this.mediaChunks.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C0539C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long positionUs) {
        int sampleQueueCount = this.sampleQueues.length;
        int i = 0;
        while (true) {
            boolean seekInsideQueue = true;
            if (i >= sampleQueueCount) {
                return true;
            }
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(positionUs, true, false) == -1) {
                seekInsideQueue = false;
            }
            if (seekInsideQueue || (!this.sampleQueueIsAudioVideoFlags[i] && this.haveAudioVideoSampleQueues)) {
                i++;
            }
        }
        return false;
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
        boolean z = false;
        if (manifestFormatTrackType != 3) {
            if (manifestFormatTrackType == MimeTypes.getTrackType(sampleFormatMimeType)) {
                z = true;
            }
            return z;
        } else if (!Util.areEqual(manifestFormatMimeType, sampleFormatMimeType)) {
            return false;
        } else {
            if (!MimeTypes.APPLICATION_CEA608.equals(manifestFormatMimeType)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(manifestFormatMimeType)) {
                    return true;
                }
            }
            if (manifestFormat.accessibilityChannel == sampleFormat.accessibilityChannel) {
                z = true;
            }
            return z;
        }
    }

    private static DummyTrackOutput createDummyTrackOutput(int id, int type) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unmapped track with id ");
        stringBuilder.append(id);
        stringBuilder.append(" of type ");
        stringBuilder.append(type);
        Log.w(str, stringBuilder.toString());
        return new DummyTrackOutput();
    }
}
