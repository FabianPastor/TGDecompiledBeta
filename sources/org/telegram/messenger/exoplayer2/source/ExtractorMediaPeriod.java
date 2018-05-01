package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ConditionVariable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExtractorMediaPeriod implements ExtractorOutput, MediaPeriod, UpstreamFormatChangedListener, Callback<ExtractingLoadable>, ReleaseCallback {
    private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000;
    private int actualMinLoadableRetryCount;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final long continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final DataSource dataSource;
    private long durationUs;
    private int enabledTrackCount;
    private final EventDispatcher eventDispatcher;
    private int extractedSamplesCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private final Handler handler;
    private boolean haveAudioVideoTracks;
    private long lastSeekPositionUs;
    private long length;
    private final Listener listener;
    private final ConditionVariable loadCondition;
    private final Loader loader = new Loader("Loader:ExtractorMediaPeriod");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private final int minLoadableRetryCount;
    private boolean notifyDiscontinuity;
    private final Runnable onContinueLoadingRequestedRunnable;
    private boolean pendingDeferredRetry;
    private long pendingResetPositionUs;
    private boolean prepared;
    private boolean released;
    private int[] sampleQueueTrackIds;
    private SampleQueue[] sampleQueues;
    private boolean sampleQueuesBuilt;
    private SeekMap seekMap;
    private boolean seenFirstTrackSelection;
    private boolean[] trackEnabledStates;
    private boolean[] trackFormatNotificationSent;
    private boolean[] trackIsAudioVideoFlags;
    private TrackGroupArray tracks;
    private final Uri uri;

    /* renamed from: org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod$1 */
    class C05931 implements Runnable {
        C05931() {
        }

        public void run() {
            ExtractorMediaPeriod.this.maybeFinishPrepare();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod$2 */
    class C05942 implements Runnable {
        C05942() {
        }

        public void run() {
            if (!ExtractorMediaPeriod.this.released) {
                ExtractorMediaPeriod.this.callback.onContinueLoadingRequested(ExtractorMediaPeriod.this);
            }
        }
    }

    private static final class ExtractorHolder {
        private Extractor extractor;
        private final ExtractorOutput extractorOutput;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractorArr, ExtractorOutput extractorOutput) {
            this.extractors = extractorArr;
            this.extractorOutput = extractorOutput;
        }

        public org.telegram.messenger.exoplayer2.extractor.Extractor selectExtractor(org.telegram.messenger.exoplayer2.extractor.ExtractorInput r6, android.net.Uri r7) throws java.io.IOException, java.lang.InterruptedException {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r5 = this;
            r0 = r5.extractor;
            if (r0 == 0) goto L_0x0007;
        L_0x0004:
            r6 = r5.extractor;
            return r6;
        L_0x0007:
            r0 = r5.extractors;
            r1 = r0.length;
            r2 = 0;
        L_0x000b:
            if (r2 >= r1) goto L_0x0026;
        L_0x000d:
            r3 = r0[r2];
            r4 = r3.sniff(r6);	 Catch:{ EOFException -> 0x0020, all -> 0x001b }
            if (r4 == 0) goto L_0x0020;	 Catch:{ EOFException -> 0x0020, all -> 0x001b }
        L_0x0015:
            r5.extractor = r3;	 Catch:{ EOFException -> 0x0020, all -> 0x001b }
            r6.resetPeekPosition();
            goto L_0x0026;
        L_0x001b:
            r7 = move-exception;
            r6.resetPeekPosition();
            throw r7;
        L_0x0020:
            r6.resetPeekPosition();
            r2 = r2 + 1;
            goto L_0x000b;
        L_0x0026:
            r6 = r5.extractor;
            if (r6 != 0) goto L_0x004c;
        L_0x002a:
            r6 = new org.telegram.messenger.exoplayer2.source.UnrecognizedInputFormatException;
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r1 = "None of the available extractors (";
            r0.append(r1);
            r1 = r5.extractors;
            r1 = org.telegram.messenger.exoplayer2.util.Util.getCommaDelimitedSimpleClassNames(r1);
            r0.append(r1);
            r1 = ") could read the stream.";
            r0.append(r1);
            r0 = r0.toString();
            r6.<init>(r0, r7);
            throw r6;
        L_0x004c:
            r6 = r5.extractor;
            r7 = r5.extractorOutput;
            r6.init(r7);
            r6 = r5.extractor;
            return r6;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.ExtractorHolder.selectExtractor(org.telegram.messenger.exoplayer2.extractor.ExtractorInput, android.net.Uri):org.telegram.messenger.exoplayer2.extractor.Extractor");
        }

        public void release() {
            if (this.extractor != null) {
                this.extractor.release();
                this.extractor = null;
            }
        }
    }

    interface Listener {
        void onSourceInfoRefreshed(long j, boolean z);
    }

    final class ExtractingLoadable implements Loadable {
        private long bytesLoaded;
        private final DataSource dataSource;
        private DataSpec dataSpec;
        private final ExtractorHolder extractorHolder;
        private long length = -1;
        private volatile boolean loadCanceled;
        private final ConditionVariable loadCondition;
        private boolean pendingExtractorSeek = true;
        private final PositionHolder positionHolder = new PositionHolder();
        private long seekTimeUs;
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, ConditionVariable conditionVariable) {
            this.uri = (Uri) Assertions.checkNotNull(uri);
            this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
            this.extractorHolder = (ExtractorHolder) Assertions.checkNotNull(extractorHolder);
            this.loadCondition = conditionVariable;
        }

        public void setLoadPosition(long j, long j2) {
            this.positionHolder.position = j;
            this.seekTimeUs = j2;
            this.pendingExtractorSeek = 1;
        }

        public void cancelLoad() {
            this.loadCanceled = true;
        }

        public boolean isLoadCanceled() {
            return this.loadCanceled;
        }

        public void load() throws java.io.IOException, java.lang.InterruptedException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r4_6 org.telegram.messenger.exoplayer2.extractor.ExtractorInput) in PHI: PHI: (r4_9 org.telegram.messenger.exoplayer2.extractor.ExtractorInput) = (r4_6 org.telegram.messenger.exoplayer2.extractor.ExtractorInput), (r4_6 org.telegram.messenger.exoplayer2.extractor.ExtractorInput), (r4_10 org.telegram.messenger.exoplayer2.extractor.ExtractorInput) binds: {(r4_6 org.telegram.messenger.exoplayer2.extractor.ExtractorInput)=B:24:0x0097, (r4_6 org.telegram.messenger.exoplayer2.extractor.ExtractorInput)=B:31:0x00bb, (r4_10 org.telegram.messenger.exoplayer2.extractor.ExtractorInput)=B:33:0x00bd}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r14 = this;
            r0 = 0;
            r1 = r0;
        L_0x0002:
            if (r1 != 0) goto L_0x00dd;
        L_0x0004:
            r2 = r14.loadCanceled;
            if (r2 != 0) goto L_0x00dd;
        L_0x0008:
            r2 = 0;
            r3 = 1;
            r4 = r14.positionHolder;	 Catch:{ all -> 0x00bc }
            r12 = r4.position;	 Catch:{ all -> 0x00bc }
            r4 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;	 Catch:{ all -> 0x00bc }
            r6 = r14.uri;	 Catch:{ all -> 0x00bc }
            r9 = -1;	 Catch:{ all -> 0x00bc }
            r5 = org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.this;	 Catch:{ all -> 0x00bc }
            r11 = r5.customCacheKey;	 Catch:{ all -> 0x00bc }
            r5 = r4;	 Catch:{ all -> 0x00bc }
            r7 = r12;	 Catch:{ all -> 0x00bc }
            r5.<init>(r6, r7, r9, r11);	 Catch:{ all -> 0x00bc }
            r14.dataSpec = r4;	 Catch:{ all -> 0x00bc }
            r4 = r14.dataSource;	 Catch:{ all -> 0x00bc }
            r5 = r14.dataSpec;	 Catch:{ all -> 0x00bc }
            r4 = r4.open(r5);	 Catch:{ all -> 0x00bc }
            r14.length = r4;	 Catch:{ all -> 0x00bc }
            r4 = r14.length;	 Catch:{ all -> 0x00bc }
            r6 = -1;	 Catch:{ all -> 0x00bc }
            r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ all -> 0x00bc }
            if (r8 == 0) goto L_0x0039;	 Catch:{ all -> 0x00bc }
        L_0x0033:
            r4 = r14.length;	 Catch:{ all -> 0x00bc }
            r6 = r4 + r12;	 Catch:{ all -> 0x00bc }
            r14.length = r6;	 Catch:{ all -> 0x00bc }
        L_0x0039:
            r4 = new org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x00bc }
            r6 = r14.dataSource;	 Catch:{ all -> 0x00bc }
            r9 = r14.length;	 Catch:{ all -> 0x00bc }
            r5 = r4;	 Catch:{ all -> 0x00bc }
            r7 = r12;	 Catch:{ all -> 0x00bc }
            r5.<init>(r6, r7, r9);	 Catch:{ all -> 0x00bc }
            r2 = r14.extractorHolder;	 Catch:{ all -> 0x00ba }
            r5 = r14.dataSource;	 Catch:{ all -> 0x00ba }
            r5 = r5.getUri();	 Catch:{ all -> 0x00ba }
            r2 = r2.selectExtractor(r4, r5);	 Catch:{ all -> 0x00ba }
            r5 = r14.pendingExtractorSeek;	 Catch:{ all -> 0x00ba }
            if (r5 == 0) goto L_0x005b;	 Catch:{ all -> 0x00ba }
        L_0x0054:
            r5 = r14.seekTimeUs;	 Catch:{ all -> 0x00ba }
            r2.seek(r12, r5);	 Catch:{ all -> 0x00ba }
            r14.pendingExtractorSeek = r0;	 Catch:{ all -> 0x00ba }
        L_0x005b:
            if (r1 != 0) goto L_0x0099;	 Catch:{ all -> 0x00ba }
        L_0x005d:
            r5 = r14.loadCanceled;	 Catch:{ all -> 0x00ba }
            if (r5 != 0) goto L_0x0099;	 Catch:{ all -> 0x00ba }
        L_0x0061:
            r5 = r14.loadCondition;	 Catch:{ all -> 0x00ba }
            r5.block();	 Catch:{ all -> 0x00ba }
            r5 = r14.positionHolder;	 Catch:{ all -> 0x00ba }
            r5 = r2.read(r4, r5);	 Catch:{ all -> 0x00ba }
            r6 = r4.getPosition();	 Catch:{ all -> 0x0096 }
            r1 = org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.this;	 Catch:{ all -> 0x0096 }
            r8 = r1.continueLoadingCheckIntervalBytes;	 Catch:{ all -> 0x0096 }
            r10 = r12 + r8;	 Catch:{ all -> 0x0096 }
            r1 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));	 Catch:{ all -> 0x0096 }
            if (r1 <= 0) goto L_0x0094;	 Catch:{ all -> 0x0096 }
        L_0x007c:
            r12 = r4.getPosition();	 Catch:{ all -> 0x0096 }
            r1 = r14.loadCondition;	 Catch:{ all -> 0x0096 }
            r1.close();	 Catch:{ all -> 0x0096 }
            r1 = org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.this;	 Catch:{ all -> 0x0096 }
            r1 = r1.handler;	 Catch:{ all -> 0x0096 }
            r6 = org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.this;	 Catch:{ all -> 0x0096 }
            r6 = r6.onContinueLoadingRequestedRunnable;	 Catch:{ all -> 0x0096 }
            r1.post(r6);	 Catch:{ all -> 0x0096 }
        L_0x0094:
            r1 = r5;
            goto L_0x005b;
        L_0x0096:
            r0 = move-exception;
            r1 = r5;
            goto L_0x00be;
        L_0x0099:
            if (r1 != r3) goto L_0x009d;
        L_0x009b:
            r1 = r0;
            goto L_0x00b3;
        L_0x009d:
            if (r4 == 0) goto L_0x00b3;
        L_0x009f:
            r2 = r14.positionHolder;
            r3 = r4.getPosition();
            r2.position = r3;
            r2 = r14.positionHolder;
            r2 = r2.position;
            r4 = r14.dataSpec;
            r4 = r4.absoluteStreamPosition;
            r6 = r2 - r4;
            r14.bytesLoaded = r6;
        L_0x00b3:
            r2 = r14.dataSource;
            org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r2);
            goto L_0x0002;
        L_0x00ba:
            r0 = move-exception;
            goto L_0x00be;
        L_0x00bc:
            r0 = move-exception;
            r4 = r2;
        L_0x00be:
            if (r1 != r3) goto L_0x00c1;
        L_0x00c0:
            goto L_0x00d7;
        L_0x00c1:
            if (r4 == 0) goto L_0x00d7;
        L_0x00c3:
            r1 = r14.positionHolder;
            r2 = r4.getPosition();
            r1.position = r2;
            r1 = r14.positionHolder;
            r1 = r1.position;
            r3 = r14.dataSpec;
            r3 = r3.absoluteStreamPosition;
            r5 = r1 - r3;
            r14.bytesLoaded = r5;
        L_0x00d7:
            r1 = r14.dataSource;
            org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r1);
            throw r0;
        L_0x00dd:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.ExtractorMediaPeriod.ExtractingLoadable.load():void");
        }
    }

    private final class SampleStreamImpl implements SampleStream {
        private final int track;

        public SampleStreamImpl(int i) {
            this.track = i;
        }

        public boolean isReady() {
            return ExtractorMediaPeriod.this.isReady(this.track);
        }

        public void maybeThrowError() throws IOException {
            ExtractorMediaPeriod.this.maybeThrowError();
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            return ExtractorMediaPeriod.this.readData(this.track, formatHolder, decoderInputBuffer, z);
        }

        public int skipData(long j) {
            return ExtractorMediaPeriod.this.skipData(this.track, j);
        }
    }

    public void reevaluateBuffer(long j) {
    }

    public ExtractorMediaPeriod(Uri uri, DataSource dataSource, Extractor[] extractorArr, int i, EventDispatcher eventDispatcher, Listener listener, Allocator allocator, String str, int i2) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.minLoadableRetryCount = i;
        this.eventDispatcher = eventDispatcher;
        this.listener = listener;
        this.allocator = allocator;
        this.customCacheKey = str;
        this.continueLoadingCheckIntervalBytes = (long) i2;
        this.extractorHolder = new ExtractorHolder(extractorArr, this);
        this.loadCondition = new ConditionVariable();
        this.maybeFinishPrepareRunnable = new C05931();
        this.onContinueLoadingRequestedRunnable = new C05942();
        this.handler = new Handler();
        this.sampleQueueTrackIds = new int[0];
        this.sampleQueues = new SampleQueue[0];
        this.pendingResetPositionUs = C0542C.TIME_UNSET;
        this.length = -1;
        this.durationUs = C0542C.TIME_UNSET;
        if (i == -1) {
            i = 3;
        }
        this.actualMinLoadableRetryCount = i;
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
        this.extractorHolder.release();
        for (SampleQueue reset : this.sampleQueues) {
            reset.reset();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long j) {
        this.callback = callback;
        this.loadCondition.open();
        startLoading();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        boolean[] zArr3;
        Assertions.checkState(this.prepared);
        int i = this.enabledTrackCount;
        int i2 = 0;
        int i3 = 0;
        while (i3 < trackSelectionArr.length) {
            if (sampleStreamArr[i3] != null && (trackSelectionArr[i3] == null || !zArr[i3])) {
                int access$300 = ((SampleStreamImpl) sampleStreamArr[i3]).track;
                Assertions.checkState(this.trackEnabledStates[access$300]);
                this.enabledTrackCount--;
                this.trackEnabledStates[access$300] = false;
                sampleStreamArr[i3] = null;
            }
            i3++;
        }
        if (this.seenFirstTrackSelection == null) {
            if (j != 0) {
            }
            zArr = null;
            zArr3 = zArr;
            zArr = null;
            while (zArr < trackSelectionArr.length) {
                if (sampleStreamArr[zArr] == null && trackSelectionArr[zArr] != null) {
                    TrackSelection trackSelection = trackSelectionArr[zArr];
                    Assertions.checkState(trackSelection.length() != 1);
                    Assertions.checkState(trackSelection.getIndexInTrackGroup(0) != 0);
                    i3 = this.tracks.indexOf(trackSelection.getTrackGroup());
                    Assertions.checkState(this.trackEnabledStates[i3] ^ true);
                    this.enabledTrackCount++;
                    this.trackEnabledStates[i3] = true;
                    sampleStreamArr[zArr] = new SampleStreamImpl(i3);
                    zArr2[zArr] = true;
                    if (zArr3 == null) {
                        SampleQueue sampleQueue = this.sampleQueues[i3];
                        sampleQueue.rewind();
                        zArr3 = (sampleQueue.advanceTo(j, true, true) == -1 || sampleQueue.getReadIndex() == 0) ? 0 : true;
                    }
                }
                zArr++;
            }
            if (this.enabledTrackCount == null) {
                this.pendingDeferredRetry = false;
                this.notifyDiscontinuity = false;
                if (this.loader.isLoading() == null) {
                    trackSelectionArr = this.sampleQueues;
                    zArr = trackSelectionArr.length;
                    while (i2 < zArr) {
                        trackSelectionArr[i2].discardToEnd();
                        i2++;
                    }
                    this.loader.cancelLoading();
                } else {
                    trackSelectionArr = this.sampleQueues;
                    zArr = trackSelectionArr.length;
                    while (i2 < zArr) {
                        trackSelectionArr[i2].reset();
                        i2++;
                    }
                }
            } else if (zArr3 != null) {
                j = seekToUs(j);
                while (i2 < sampleStreamArr.length) {
                    if (sampleStreamArr[i2] != null) {
                        zArr2[i2] = true;
                    }
                    i2++;
                }
            }
            this.seenFirstTrackSelection = true;
            return j;
        }
        zArr = 1;
        zArr3 = zArr;
        zArr = null;
        while (zArr < trackSelectionArr.length) {
            TrackSelection trackSelection2 = trackSelectionArr[zArr];
            if (trackSelection2.length() != 1) {
            }
            Assertions.checkState(trackSelection2.length() != 1);
            if (trackSelection2.getIndexInTrackGroup(0) != 0) {
            }
            Assertions.checkState(trackSelection2.getIndexInTrackGroup(0) != 0);
            i3 = this.tracks.indexOf(trackSelection2.getTrackGroup());
            Assertions.checkState(this.trackEnabledStates[i3] ^ true);
            this.enabledTrackCount++;
            this.trackEnabledStates[i3] = true;
            sampleStreamArr[zArr] = new SampleStreamImpl(i3);
            zArr2[zArr] = true;
            if (zArr3 == null) {
                SampleQueue sampleQueue2 = this.sampleQueues[i3];
                sampleQueue2.rewind();
                if (sampleQueue2.advanceTo(j, true, true) == -1) {
                }
            }
            zArr++;
        }
        if (this.enabledTrackCount == null) {
            this.pendingDeferredRetry = false;
            this.notifyDiscontinuity = false;
            if (this.loader.isLoading() == null) {
                trackSelectionArr = this.sampleQueues;
                zArr = trackSelectionArr.length;
                while (i2 < zArr) {
                    trackSelectionArr[i2].reset();
                    i2++;
                }
            } else {
                trackSelectionArr = this.sampleQueues;
                zArr = trackSelectionArr.length;
                while (i2 < zArr) {
                    trackSelectionArr[i2].discardToEnd();
                    i2++;
                }
                this.loader.cancelLoading();
            }
        } else if (zArr3 != null) {
            j = seekToUs(j);
            while (i2 < sampleStreamArr.length) {
                if (sampleStreamArr[i2] != null) {
                    zArr2[i2] = true;
                }
                i2++;
            }
        }
        this.seenFirstTrackSelection = true;
        return j;
    }

    public void discardBuffer(long j, boolean z) {
        int length = this.sampleQueues.length;
        for (int i = 0; i < length; i++) {
            this.sampleQueues[i].discardTo(j, z, this.trackEnabledStates[i]);
        }
    }

    public boolean continueLoading(long j) {
        if (this.loadingFinished == null && this.pendingDeferredRetry == null) {
            if (this.prepared == null || this.enabledTrackCount != null) {
                j = this.loadCondition.open();
                if (!this.loader.isLoading()) {
                    startLoading();
                    j = 1;
                }
                return j;
            }
        }
        return 0;
    }

    public long getNextLoadPositionUs() {
        return this.enabledTrackCount == 0 ? Long.MIN_VALUE : getBufferedPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifyDiscontinuity || (!this.loadingFinished && getExtractedSamplesCount() <= this.extractedSamplesCountAtStartOfLoad)) {
            return C0542C.TIME_UNSET;
        }
        this.notifyDiscontinuity = false;
        return this.lastSeekPositionUs;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long j;
        if (this.haveAudioVideoTracks) {
            j = Long.MAX_VALUE;
            int length = this.sampleQueues.length;
            for (int i = 0; i < length; i++) {
                if (this.trackIsAudioVideoFlags[i]) {
                    j = Math.min(j, this.sampleQueues[i].getLargestQueuedTimestampUs());
                }
            }
        } else {
            j = getLargestQueuedTimestampUs();
        }
        if (j == Long.MIN_VALUE) {
            j = this.lastSeekPositionUs;
        }
        return j;
    }

    public long seekToUs(long j) {
        if (!this.seekMap.isSeekable()) {
            j = 0;
        }
        this.lastSeekPositionUs = j;
        int i = 0;
        this.notifyDiscontinuity = false;
        if (!isPendingReset() && seekInsideBufferUs(j)) {
            return j;
        }
        this.pendingDeferredRetry = false;
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
        } else {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].reset();
                i++;
            }
        }
        return j;
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        if (!this.seekMap.isSeekable()) {
            return 0;
        }
        SeekPoints seekPoints = this.seekMap.getSeekPoints(j);
        return Util.resolveSeekPositionUs(j, seekParameters, seekPoints.first.timeUs, seekPoints.second.timeUs);
    }

    boolean isReady(int i) {
        return !suppressRead() && (this.loadingFinished || this.sampleQueues[i].hasNextSample() != 0);
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError(this.actualMinLoadableRetryCount);
    }

    int readData(int i, FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (suppressRead()) {
            return -3;
        }
        formatHolder = this.sampleQueues[i].read(formatHolder, decoderInputBuffer, z, this.loadingFinished, this.lastSeekPositionUs);
        if (formatHolder == -4) {
            maybeNotifyTrackFormat(i);
        } else if (formatHolder == -3) {
            maybeStartDeferredRetry(i);
        }
        return formatHolder;
    }

    int skipData(int i, long j) {
        int i2 = 0;
        if (suppressRead()) {
            return 0;
        }
        SampleQueue sampleQueue = this.sampleQueues[i];
        if (!this.loadingFinished || j <= sampleQueue.getLargestQueuedTimestampUs()) {
            j = sampleQueue.advanceTo(j, true, true);
            if (j != -1) {
                i2 = j;
            }
        } else {
            i2 = sampleQueue.advanceToEnd();
        }
        if (i2 > 0) {
            maybeNotifyTrackFormat(i);
        } else {
            maybeStartDeferredRetry(i);
        }
        return i2;
    }

    private void maybeNotifyTrackFormat(int i) {
        if (!this.trackFormatNotificationSent[i]) {
            Format format = this.tracks.get(i).getFormat(0);
            this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(format.sampleMimeType), format, 0, null, this.lastSeekPositionUs);
            this.trackFormatNotificationSent[i] = true;
        }
    }

    private void maybeStartDeferredRetry(int i) {
        if (this.pendingDeferredRetry && this.trackIsAudioVideoFlags[i]) {
            if (this.sampleQueues[i].hasNextSample() == 0) {
                this.pendingResetPositionUs = 0;
                i = 0;
                this.pendingDeferredRetry = false;
                this.notifyDiscontinuity = true;
                this.lastSeekPositionUs = 0;
                this.extractedSamplesCountAtStartOfLoad = 0;
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                while (i < length) {
                    sampleQueueArr[i].reset();
                    i++;
                }
                this.callback.onContinueLoadingRequested(this);
            }
        }
    }

    private boolean suppressRead() {
        if (!this.notifyDiscontinuity) {
            if (!isPendingReset()) {
                return false;
            }
        }
        return true;
    }

    public void onLoadCompleted(ExtractingLoadable extractingLoadable, long j, long j2) {
        if (this.durationUs == C0542C.TIME_UNSET) {
            long largestQueuedTimestampUs = getLargestQueuedTimestampUs();
            r0.durationUs = largestQueuedTimestampUs == Long.MIN_VALUE ? 0 : largestQueuedTimestampUs + DEFAULT_LAST_SAMPLE_DURATION_US;
            r0.listener.onSourceInfoRefreshed(r0.durationUs, r0.seekMap.isSeekable());
        }
        r0.eventDispatcher.loadCompleted(extractingLoadable.dataSpec, 1, -1, null, 0, null, extractingLoadable.seekTimeUs, r0.durationUs, j, j2, extractingLoadable.bytesLoaded);
        copyLengthFromLoader(extractingLoadable);
        r0.loadingFinished = true;
        r0.callback.onContinueLoadingRequested(r0);
    }

    public void onLoadCanceled(ExtractingLoadable extractingLoadable, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(extractingLoadable.dataSpec, 1, -1, null, 0, null, extractingLoadable.seekTimeUs, this.durationUs, j, j2, extractingLoadable.bytesLoaded);
        if (!z) {
            copyLengthFromLoader(extractingLoadable);
            for (SampleQueue reset : r0.sampleQueues) {
                reset.reset();
            }
            if (r0.enabledTrackCount > 0) {
                r0.callback.onContinueLoadingRequested(r0);
            }
        }
    }

    public int onLoadError(ExtractingLoadable extractingLoadable, long j, long j2, IOException iOException) {
        boolean isLoadableExceptionFatal = isLoadableExceptionFatal(iOException);
        this.eventDispatcher.loadError(extractingLoadable.dataSpec, 1, -1, null, 0, null, extractingLoadable.seekTimeUs, this.durationUs, j, j2, extractingLoadable.bytesLoaded, iOException, isLoadableExceptionFatal);
        copyLengthFromLoader(extractingLoadable);
        if (isLoadableExceptionFatal) {
            return 3;
        }
        ExtractingLoadable extractingLoadable2;
        int i;
        int extractedSamplesCount = getExtractedSamplesCount();
        int i2 = 0;
        if (extractedSamplesCount > r0.extractedSamplesCountAtStartOfLoad) {
            extractingLoadable2 = extractingLoadable;
            i = 1;
        } else {
            extractingLoadable2 = extractingLoadable;
            i = 0;
        }
        if (!configureRetry(extractingLoadable2, extractedSamplesCount)) {
            i2 = 2;
        } else if (i != 0) {
            i2 = 1;
        }
        return i2;
    }

    public TrackOutput track(int i, int i2) {
        i2 = this.sampleQueues.length;
        for (int i3 = 0; i3 < i2; i3++) {
            if (this.sampleQueueTrackIds[i3] == i) {
                return this.sampleQueues[i3];
            }
        }
        TrackOutput sampleQueue = new SampleQueue(this.allocator);
        sampleQueue.setUpstreamFormatChangeListener(this);
        int i4 = i2 + 1;
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, i4);
        this.sampleQueueTrackIds[i2] = i;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, i4);
        this.sampleQueues[i2] = sampleQueue;
        return sampleQueue;
    }

    public void endTracks() {
        this.sampleQueuesBuilt = true;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    private void maybeFinishPrepare() {
        if (!(this.released || this.prepared || this.seekMap == null)) {
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
                this.loadCondition.close();
                int length2 = this.sampleQueues.length;
                TrackGroup[] trackGroupArr = new TrackGroup[length2];
                this.trackIsAudioVideoFlags = new boolean[length2];
                this.trackEnabledStates = new boolean[length2];
                this.trackFormatNotificationSent = new boolean[length2];
                this.durationUs = this.seekMap.getDurationUs();
                i = 0;
                while (true) {
                    boolean z = true;
                    if (i >= length2) {
                        break;
                    }
                    trackGroupArr[i] = new TrackGroup(this.sampleQueues[i].getUpstreamFormat());
                    String str = r5.sampleMimeType;
                    if (!MimeTypes.isVideo(str)) {
                        if (!MimeTypes.isAudio(str)) {
                            z = false;
                        }
                    }
                    this.trackIsAudioVideoFlags[i] = z;
                    this.haveAudioVideoTracks = z | this.haveAudioVideoTracks;
                    i++;
                }
                this.tracks = new TrackGroupArray(trackGroupArr);
                if (this.minLoadableRetryCount == -1 && this.length == -1 && this.seekMap.getDurationUs() == C0542C.TIME_UNSET) {
                    this.actualMinLoadableRetryCount = 6;
                }
                this.prepared = true;
                this.listener.onSourceInfoRefreshed(this.durationUs, this.seekMap.isSeekable());
                this.callback.onPrepared(this);
            }
        }
    }

    private void copyLengthFromLoader(ExtractingLoadable extractingLoadable) {
        if (this.length == -1) {
            this.length = extractingLoadable.length;
        }
    }

    private void startLoading() {
        ExtractingLoadable extractingLoadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.loadCondition);
        if (this.prepared) {
            Assertions.checkState(isPendingReset());
            if (r6.durationUs == C0542C.TIME_UNSET || r6.pendingResetPositionUs < r6.durationUs) {
                extractingLoadable.setLoadPosition(r6.seekMap.getSeekPoints(r6.pendingResetPositionUs).first.position, r6.pendingResetPositionUs);
                r6.pendingResetPositionUs = C0542C.TIME_UNSET;
            } else {
                r6.loadingFinished = true;
                r6.pendingResetPositionUs = C0542C.TIME_UNSET;
                return;
            }
        }
        r6.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        r6.eventDispatcher.loadStarted(extractingLoadable.dataSpec, 1, -1, null, 0, null, extractingLoadable.seekTimeUs, r6.durationUs, r6.loader.startLoading(extractingLoadable, r6, r6.actualMinLoadableRetryCount));
    }

    private boolean configureRetry(ExtractingLoadable extractingLoadable, int i) {
        if (this.length == -1) {
            if (this.seekMap == null || this.seekMap.getDurationUs() == C0542C.TIME_UNSET) {
                int i2 = 0;
                if (this.prepared == 0 || suppressRead() != 0) {
                    this.notifyDiscontinuity = this.prepared;
                    this.lastSeekPositionUs = 0;
                    this.extractedSamplesCountAtStartOfLoad = 0;
                    i = this.sampleQueues;
                    int length = i.length;
                    while (i2 < length) {
                        i[i2].reset();
                        i2++;
                    }
                    extractingLoadable.setLoadPosition(0, 0);
                    return true;
                }
                this.pendingDeferredRetry = true;
                return false;
            }
        }
        this.extractedSamplesCountAtStartOfLoad = i;
        return true;
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
            if (z || (!this.trackIsAudioVideoFlags[i] && this.haveAudioVideoTracks)) {
                i++;
            }
        }
        return false;
    }

    private int getExtractedSamplesCount() {
        SampleQueue[] sampleQueueArr = this.sampleQueues;
        int i = 0;
        int i2 = 0;
        while (i < sampleQueueArr.length) {
            i2 += sampleQueueArr[i].getWriteIndex();
            i++;
        }
        return i2;
    }

    private long getLargestQueuedTimestampUs() {
        long j = Long.MIN_VALUE;
        for (SampleQueue largestQueuedTimestampUs : this.sampleQueues) {
            j = Math.max(j, largestQueuedTimestampUs.getLargestQueuedTimestampUs());
        }
        return j;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C0542C.TIME_UNSET;
    }

    private static boolean isLoadableExceptionFatal(IOException iOException) {
        return iOException instanceof UnrecognizedInputFormatException;
    }
}
