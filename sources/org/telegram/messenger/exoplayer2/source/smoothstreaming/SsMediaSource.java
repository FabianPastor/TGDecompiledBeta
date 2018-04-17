package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SsMediaSource implements MediaSource, Callback<ParsingLoadable<SsManifest>> {
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private final org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final EventDispatcher eventDispatcher;
    private final long livePresentationDelayMs;
    private SsManifest manifest;
    private DataSource manifestDataSource;
    private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
    private long manifestLoadStartTimestamp;
    private Loader manifestLoader;
    private LoaderErrorThrower manifestLoaderErrorThrower;
    private final Parser<? extends SsManifest> manifestParser;
    private Handler manifestRefreshHandler;
    private final Uri manifestUri;
    private final ArrayList<SsMediaPeriod> mediaPeriods;
    private final int minLoadableRetryCount;
    private Listener sourceListener;

    /* renamed from: org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource$1 */
    class C06181 implements Runnable {
        C06181() {
        }

        public void run() {
            SsMediaSource.this.startLoadingManifest();
        }
    }

    public static final class Factory implements MediaSourceFactory {
        private final org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        private boolean isCreateCalled;
        private long livePresentationDelayMs = 30000;
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
        private Parser<? extends SsManifest> manifestParser;
        private int minLoadableRetryCount = 3;

        public Factory(org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory) {
            this.chunkSourceFactory = (org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory);
            this.manifestDataSourceFactory = manifestDataSourceFactory;
        }

        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.livePresentationDelayMs = livePresentationDelayMs;
            return this;
        }

        public Factory setManifestParser(Parser<? extends SsManifest> manifestParser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public SsMediaSource createMediaSource(SsManifest manifest, Handler eventHandler, MediaSourceEventListener eventListener) {
            SsManifest ssManifest = manifest;
            Assertions.checkArgument(ssManifest.isLive ^ true);
            this.isCreateCalled = true;
            return new SsMediaSource(ssManifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, eventHandler, eventListener);
        }

        public SsMediaSource createMediaSource(Uri manifestUri) {
            return createMediaSource(manifestUri, null, null);
        }

        public SsMediaSource createMediaSource(Uri manifestUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                r0.manifestParser = new SsManifestParser();
            }
            return new SsMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), r0.manifestDataSourceFactory, r0.manifestParser, r0.chunkSourceFactory, r0.compositeSequenceableLoaderFactory, r0.minLoadableRetryCount, r0.livePresentationDelayMs, eventHandler, eventListener);
        }

        public int[] getSupportedTypes() {
            return new int[]{1};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.smoothstreaming");
    }

    @Deprecated
    public SsMediaSource(SsManifest manifest, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, chunkSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(SsManifest manifest, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, null, null, null, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), minLoadableRetryCount, 30000, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, 30000, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, new SsManifestParser(), chunkSourceFactory, minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends SsManifest> manifestParser, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(null, manifestUri, manifestDataSourceFactory, manifestParser, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    private SsMediaSource(SsManifest manifest, Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends SsManifest> manifestParser, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        boolean z;
        Uri withAppendedPath;
        if (manifest != null) {
            if (manifest.isLive) {
                z = false;
                Assertions.checkState(z);
                this.manifest = manifest;
                withAppendedPath = manifestUri != null ? null : Util.toLowerInvariant(manifestUri.getLastPathSegment()).matches("manifest(\\(.+\\))?") ? manifestUri : Uri.withAppendedPath(manifestUri, "Manifest");
                this.manifestUri = withAppendedPath;
                this.manifestDataSourceFactory = manifestDataSourceFactory;
                this.manifestParser = manifestParser;
                this.chunkSourceFactory = chunkSourceFactory;
                this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
                this.minLoadableRetryCount = minLoadableRetryCount;
                this.livePresentationDelayMs = livePresentationDelayMs;
                this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
                this.mediaPeriods = new ArrayList();
            }
        }
        z = true;
        Assertions.checkState(z);
        this.manifest = manifest;
        if (manifestUri != null) {
            if (Util.toLowerInvariant(manifestUri.getLastPathSegment()).matches("manifest(\\(.+\\))?")) {
            }
        }
        this.manifestUri = withAppendedPath;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.manifestParser = manifestParser;
        this.chunkSourceFactory = chunkSourceFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.mediaPeriods = new ArrayList();
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        Assertions.checkState(this.sourceListener == null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.sourceListener = listener;
        if (this.manifest != null) {
            this.manifestLoaderErrorThrower = new Dummy();
            processManifest();
            return;
        }
        this.manifestDataSource = this.manifestDataSourceFactory.createDataSource();
        this.manifestLoader = new Loader("Loader:Manifest");
        this.manifestLoaderErrorThrower = this.manifestLoader;
        this.manifestRefreshHandler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        Assertions.checkArgument(id.periodIndex == 0);
        SsMediaPeriod ssMediaPeriod = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.eventDispatcher, this.manifestLoaderErrorThrower, allocator);
        this.mediaPeriods.add(ssMediaPeriod);
        return ssMediaPeriod;
    }

    public void releasePeriod(MediaPeriod period) {
        ((SsMediaPeriod) period).release();
        this.mediaPeriods.remove(period);
    }

    public void releaseSource() {
        this.manifest = null;
        this.manifestDataSource = null;
        this.manifestLoadStartTimestamp = 0;
        if (this.manifestLoader != null) {
            this.manifestLoader.release();
            this.manifestLoader = null;
        }
        if (this.manifestRefreshHandler != null) {
            this.manifestRefreshHandler.removeCallbacksAndMessages(null);
            this.manifestRefreshHandler = null;
        }
    }

    public void onLoadCompleted(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.manifest = (SsManifest) loadable.getResult();
        this.manifestLoadStartTimestamp = elapsedRealtimeMs - loadDurationMs;
        processManifest();
        scheduleManifestRefresh();
    }

    public void onLoadCanceled(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<SsManifest> parsingLoadable = loadable;
        IOException iOException = error;
        boolean isFatal = iOException instanceof ParserException;
        this.eventDispatcher.loadError(parsingLoadable.dataSpec, parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), iOException, isFatal);
        return isFatal ? 3 : 0;
    }

    private void processManifest() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r1_7 'timeline' org.telegram.messenger.exoplayer2.Timeline) in PHI: PHI: (r1_8 'timeline' org.telegram.messenger.exoplayer2.Timeline) = (r1_7 'timeline' org.telegram.messenger.exoplayer2.Timeline), (r1_12 'timeline' org.telegram.messenger.exoplayer2.Timeline) binds: {(r1_7 'timeline' org.telegram.messenger.exoplayer2.Timeline)=B:17:0x0076, (r1_12 'timeline' org.telegram.messenger.exoplayer2.Timeline)=B:30:0x00c9}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r26 = this;
        r0 = r26;
        r1 = 0;
        r2 = r1;
    L_0x0004:
        r3 = r0.mediaPeriods;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x001c;
    L_0x000c:
        r3 = r0.mediaPeriods;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaPeriod) r3;
        r4 = r0.manifest;
        r3.updateManifest(r4);
        r2 = r2 + 1;
        goto L_0x0004;
    L_0x001c:
        r2 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r4 = -922337203NUM;
        r6 = r0.manifest;
        r6 = r6.streamElements;
        r7 = r6.length;
        r13 = r4;
        r3 = r2;
        r2 = r1;
    L_0x002b:
        if (r2 >= r7) goto L_0x005b;
    L_0x002d:
        r5 = r6[r2];
        r8 = r5.chunkCount;
        if (r8 <= 0) goto L_0x0055;
    L_0x0033:
        r8 = r5.getStartTimeUs(r1);
        r3 = java.lang.Math.min(r3, r8);
        r8 = r5.chunkCount;
        r8 = r8 + -1;
        r8 = r5.getStartTimeUs(r8);
        r10 = r5.chunkCount;
        r10 = r10 + -1;
        r10 = r5.getChunkDurationUs(r10);
        r20 = r2;
        r1 = r8 + r10;
        r1 = java.lang.Math.max(r13, r1);
        r13 = r1;
        goto L_0x0057;
    L_0x0055:
        r20 = r2;
    L_0x0057:
        r2 = r20 + 1;
        r1 = 0;
        goto L_0x002b;
    L_0x005b:
        r1 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1));
        r1 = 0;
        r6 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r5 != 0) goto L_0x008e;
    L_0x006b:
        r5 = r0.manifest;
        r5 = r5.isLive;
        if (r5 == 0) goto L_0x0074;
    L_0x0071:
        r16 = r6;
        goto L_0x0076;
    L_0x0074:
        r16 = r1;
    L_0x0076:
        r1 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r18 = 0;
        r20 = 0;
        r22 = 0;
        r24 = 1;
        r2 = r0.manifest;
        r2 = r2.isLive;
        r15 = r1;
        r25 = r2;
        r15.<init>(r16, r18, r20, r22, r24, r25);
    L_0x008b:
        r6 = r13;
        goto L_0x0101;
    L_0x008e:
        r5 = r0.manifest;
        r5 = r5.isLive;
        if (r5 == 0) goto L_0x00e0;
    L_0x0094:
        r5 = r0.manifest;
        r8 = r5.dvrWindowLengthUs;
        r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x00ae;
    L_0x009c:
        r5 = r0.manifest;
        r5 = r5.dvrWindowLengthUs;
        r7 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1));
        if (r7 <= 0) goto L_0x00ae;
    L_0x00a4:
        r1 = r0.manifest;
        r1 = r1.dvrWindowLengthUs;
        r5 = r13 - r1;
        r3 = java.lang.Math.max(r3, r5);
    L_0x00ae:
        r1 = r13 - r3;
        r5 = r0.livePresentationDelayMs;
        r5 = org.telegram.messenger.exoplayer2.C0542C.msToUs(r5);
        r7 = r1 - r5;
        r5 = 5000000; // 0x4c4b40 float:7.006492E-39 double:2.470328E-317;
        r9 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));
        if (r9 >= 0) goto L_0x00c8;
    L_0x00bf:
        r9 = 2;
        r9 = r1 / r9;
        r5 = java.lang.Math.min(r5, r9);
        goto L_0x00c9;
    L_0x00c8:
        r5 = r7;
    L_0x00c9:
        r7 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r16 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r24 = 1;
        r25 = 1;
        r15 = r7;
        r18 = r1;
        r20 = r3;
        r22 = r5;
        r15.<init>(r16, r18, r20, r22, r24, r25);
        r1 = r7;
        goto L_0x008b;
    L_0x00e0:
        r1 = r0.manifest;
        r1 = r1.durationUs;
        r5 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x00ed;
    L_0x00e8:
        r1 = r0.manifest;
        r1 = r1.durationUs;
        goto L_0x00ef;
    L_0x00ed:
        r1 = r13 - r3;
    L_0x00ef:
        r5 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r9 = r3 + r1;
        r15 = 0;
        r17 = 1;
        r18 = 0;
        r8 = r5;
        r11 = r1;
        r6 = r13;
        r13 = r3;
        r8.<init>(r9, r11, r13, r15, r17, r18);
        r1 = r5;
    L_0x0101:
        r2 = r0.sourceListener;
        r5 = r0.manifest;
        r2.onSourceInfoRefreshed(r0, r1, r5);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource.processManifest():void");
    }

    private void scheduleManifestRefresh() {
        if (this.manifest.isLive) {
            this.manifestRefreshHandler.postDelayed(new C06181(), Math.max(0, (this.manifestLoadStartTimestamp + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) - SystemClock.elapsedRealtime()));
        }
    }

    private void startLoadingManifest() {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.manifestLoader.startLoading(loadable, this, this.minLoadableRetryCount));
    }
}
