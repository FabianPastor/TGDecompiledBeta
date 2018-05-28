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
    class C06221 implements Runnable {
        C06221() {
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
            Assertions.checkState(!this.isCreateCalled);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs) {
            Assertions.checkState(!this.isCreateCalled);
            this.livePresentationDelayMs = livePresentationDelayMs;
            return this;
        }

        public Factory setManifestParser(Parser<? extends SsManifest> manifestParser) {
            Assertions.checkState(!this.isCreateCalled);
            this.manifestParser = (Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(!this.isCreateCalled);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public SsMediaSource createMediaSource(SsManifest manifest, Handler eventHandler, MediaSourceEventListener eventListener) {
            Assertions.checkArgument(!manifest.isLive);
            this.isCreateCalled = true;
            return new SsMediaSource(manifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, eventHandler, eventListener);
        }

        public SsMediaSource createMediaSource(Uri manifestUri) {
            return createMediaSource(manifestUri, null, null);
        }

        public SsMediaSource createMediaSource(Uri manifestUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                this.manifestParser = new SsManifestParser();
            }
            return new SsMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, eventHandler, eventListener);
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
        boolean z = manifest == null || !manifest.isLive;
        Assertions.checkState(z);
        this.manifest = manifest;
        if (manifestUri == null) {
            manifestUri = null;
        } else if (!Util.toLowerInvariant(manifestUri.getLastPathSegment()).matches("manifest(\\(.+\\))?")) {
            manifestUri = Uri.withAppendedPath(manifestUri, "Manifest");
        }
        this.manifestUri = manifestUri;
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
        SsMediaPeriod period = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.eventDispatcher, this.manifestLoaderErrorThrower, allocator);
        this.mediaPeriods.add(period);
        return period;
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
        boolean isFatal = error instanceof ParserException;
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? 3 : 0;
    }

    private void processManifest() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r5_2 'timeline' org.telegram.messenger.exoplayer2.Timeline) in PHI: PHI: (r5_1 'timeline' org.telegram.messenger.exoplayer2.Timeline) = (r5_0 'timeline' org.telegram.messenger.exoplayer2.Timeline), (r5_2 'timeline' org.telegram.messenger.exoplayer2.Timeline), (r5_3 'timeline' org.telegram.messenger.exoplayer2.Timeline) binds: {(r5_0 'timeline' org.telegram.messenger.exoplayer2.Timeline)=B:15:0x007f, (r5_2 'timeline' org.telegram.messenger.exoplayer2.Timeline)=B:29:0x00f7, (r5_3 'timeline' org.telegram.messenger.exoplayer2.Timeline)=B:33:0x011c}
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
        r33 = this;
        r32 = 0;
    L_0x0002:
        r0 = r33;
        r8 = r0.mediaPeriods;
        r8 = r8.size();
        r0 = r32;
        if (r0 >= r8) goto L_0x0024;
    L_0x000e:
        r0 = r33;
        r8 = r0.mediaPeriods;
        r0 = r32;
        r8 = r8.get(r0);
        r8 = (org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaPeriod) r8;
        r0 = r33;
        r9 = r0.manifest;
        r8.updateManifest(r9);
        r32 = r32 + 1;
        goto L_0x0002;
    L_0x0024:
        r14 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r30 = -922337203NUM;
        r0 = r33;
        r8 = r0.manifest;
        r9 = r8.streamElements;
        r10 = r9.length;
        r8 = 0;
        r24 = r14;
    L_0x0035:
        if (r8 >= r10) goto L_0x0069;
    L_0x0037:
        r4 = r9[r8];
        r11 = r4.chunkCount;
        if (r11 <= 0) goto L_0x0137;
    L_0x003d:
        r11 = 0;
        r18 = r4.getStartTimeUs(r11);
        r0 = r24;
        r2 = r18;
        r14 = java.lang.Math.min(r0, r2);
        r11 = r4.chunkCount;
        r11 = r11 + -1;
        r18 = r4.getStartTimeUs(r11);
        r11 = r4.chunkCount;
        r11 = r11 + -1;
        r20 = r4.getChunkDurationUs(r11);
        r18 = r18 + r20;
        r0 = r30;
        r2 = r18;
        r30 = java.lang.Math.max(r0, r2);
    L_0x0064:
        r8 = r8 + 1;
        r24 = r14;
        goto L_0x0035;
    L_0x0069:
        r8 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r8 = (r24 > r8 ? 1 : (r24 == r8 ? 0 : -1));
        if (r8 != 0) goto L_0x00a8;
    L_0x0072:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.isLive;
        if (r8 == 0) goto L_0x00a5;
    L_0x007a:
        r6 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
    L_0x007f:
        r5 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r8 = 0;
        r10 = 0;
        r12 = 0;
        r14 = 1;
        r0 = r33;
        r0 = r0.manifest;
        r18 = r0;
        r0 = r18;
        r15 = r0.isLive;
        r5.<init>(r6, r8, r10, r12, r14, r15);
        r14 = r24;
    L_0x0097:
        r0 = r33;
        r8 = r0.sourceListener;
        r0 = r33;
        r9 = r0.manifest;
        r0 = r33;
        r8.onSourceInfoRefreshed(r0, r5, r9);
        return;
    L_0x00a5:
        r6 = 0;
        goto L_0x007f;
    L_0x00a8:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.isLive;
        if (r8 == 0) goto L_0x0107;
    L_0x00b0:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.dvrWindowLengthUs;
        r10 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 == 0) goto L_0x0134;
    L_0x00bf:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.dvrWindowLengthUs;
        r10 = 0;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 <= 0) goto L_0x0134;
    L_0x00cb:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.dvrWindowLengthUs;
        r8 = r30 - r8;
        r0 = r24;
        r14 = java.lang.Math.max(r0, r8);
    L_0x00d9:
        r12 = r30 - r14;
        r0 = r33;
        r8 = r0.livePresentationDelayMs;
        r8 = org.telegram.messenger.exoplayer2.C0546C.msToUs(r8);
        r16 = r12 - r8;
        r8 = 5000000; // 0x4c4b40 float:7.006492E-39 double:2.470328E-317;
        r8 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1));
        if (r8 >= 0) goto L_0x00f7;
    L_0x00ec:
        r8 = 5000000; // 0x4c4b40 float:7.006492E-39 double:2.470328E-317;
        r10 = 2;
        r10 = r12 / r10;
        r16 = java.lang.Math.min(r8, r10);
    L_0x00f7:
        r5 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r10 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r18 = 1;
        r19 = 1;
        r9 = r5;
        r9.<init>(r10, r12, r14, r16, r18, r19);
        goto L_0x0097;
    L_0x0107:
        r0 = r33;
        r8 = r0.manifest;
        r8 = r8.durationUs;
        r10 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 == 0) goto L_0x0131;
    L_0x0116:
        r0 = r33;
        r8 = r0.manifest;
        r12 = r8.durationUs;
    L_0x011c:
        r5 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r20 = r24 + r12;
        r26 = 0;
        r28 = 1;
        r29 = 0;
        r19 = r5;
        r22 = r12;
        r19.<init>(r20, r22, r24, r26, r28, r29);
        r14 = r24;
        goto L_0x0097;
    L_0x0131:
        r12 = r30 - r24;
        goto L_0x011c;
    L_0x0134:
        r14 = r24;
        goto L_0x00d9;
    L_0x0137:
        r14 = r24;
        goto L_0x0064;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource.processManifest():void");
    }

    private void scheduleManifestRefresh() {
        if (this.manifest.isLive) {
            this.manifestRefreshHandler.postDelayed(new C06221(), Math.max(0, (this.manifestLoadStartTimestamp + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) - SystemClock.elapsedRealtime()));
        }
    }

    private void startLoadingManifest() {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.manifestLoader.startLoading(loadable, this, this.minLoadableRetryCount));
    }
}
