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

        public Factory(org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory2) {
            this.chunkSourceFactory = (org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) Assertions.checkNotNull(factory);
            this.manifestDataSourceFactory = factory2;
        }

        public Factory setMinLoadableRetryCount(int i) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = i;
            return this;
        }

        public Factory setLivePresentationDelayMs(long j) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.livePresentationDelayMs = j;
            return this;
        }

        public Factory setManifestParser(Parser<? extends SsManifest> parser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(parser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public SsMediaSource createMediaSource(SsManifest ssManifest, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            SsManifest ssManifest2 = ssManifest;
            Assertions.checkArgument(ssManifest2.isLive ^ true);
            this.isCreateCalled = true;
            return new SsMediaSource(ssManifest2, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, handler, mediaSourceEventListener);
        }

        public SsMediaSource createMediaSource(Uri uri) {
            return createMediaSource(uri, null, null);
        }

        public SsMediaSource createMediaSource(Uri uri, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                r0.manifestParser = new SsManifestParser();
            }
            return new SsMediaSource(null, (Uri) Assertions.checkNotNull(uri), r0.manifestDataSourceFactory, r0.manifestParser, r0.chunkSourceFactory, r0.compositeSequenceableLoaderFactory, r0.minLoadableRetryCount, r0.livePresentationDelayMs, handler, mediaSourceEventListener);
        }

        public int[] getSupportedTypes() {
            return new int[]{1};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.smoothstreaming");
    }

    @Deprecated
    public SsMediaSource(SsManifest ssManifest, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(ssManifest, factory, 3, handler, mediaSourceEventListener);
    }

    @Deprecated
    public SsMediaSource(SsManifest ssManifest, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(ssManifest, null, null, null, factory, new DefaultCompositeSequenceableLoaderFactory(), i, 30000, handler, mediaSourceEventListener);
    }

    @Deprecated
    public SsMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory2, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, factory2, 3, 30000, handler, mediaSourceEventListener);
    }

    @Deprecated
    public SsMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory2, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, new SsManifestParser(), factory2, i, j, handler, mediaSourceEventListener);
    }

    @Deprecated
    public SsMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Parser<? extends SsManifest> parser, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory2, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(null, uri, factory, parser, factory2, new DefaultCompositeSequenceableLoaderFactory(), i, j, handler, mediaSourceEventListener);
    }

    private SsMediaSource(SsManifest ssManifest, Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Parser<? extends SsManifest> parser, org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory factory2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        boolean z;
        if (ssManifest != null) {
            if (ssManifest.isLive) {
                z = false;
                Assertions.checkState(z);
                this.manifest = ssManifest;
                if (uri == null) {
                    uri = null;
                } else if (Util.toLowerInvariant(uri.getLastPathSegment()).matches("manifest(\\(.+\\))?") != null) {
                    uri = Uri.withAppendedPath(uri, "Manifest");
                }
                this.manifestUri = uri;
                this.manifestDataSourceFactory = factory;
                this.manifestParser = parser;
                this.chunkSourceFactory = factory2;
                this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
                this.minLoadableRetryCount = i;
                this.livePresentationDelayMs = j;
                this.eventDispatcher = new EventDispatcher(handler, mediaSourceEventListener);
                this.mediaPeriods = new ArrayList();
            }
        }
        z = true;
        Assertions.checkState(z);
        this.manifest = ssManifest;
        if (uri == null) {
            uri = null;
        } else if (Util.toLowerInvariant(uri.getLastPathSegment()).matches("manifest(\\(.+\\))?") != null) {
            uri = Uri.withAppendedPath(uri, "Manifest");
        }
        this.manifestUri = uri;
        this.manifestDataSourceFactory = factory;
        this.manifestParser = parser;
        this.chunkSourceFactory = factory2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.minLoadableRetryCount = i;
        this.livePresentationDelayMs = j;
        this.eventDispatcher = new EventDispatcher(handler, mediaSourceEventListener);
        this.mediaPeriods = new ArrayList();
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(this.sourceListener == null ? true : null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
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

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        Assertions.checkArgument(mediaPeriodId.periodIndex == null ? true : null);
        MediaPeriodId ssMediaPeriod = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.eventDispatcher, this.manifestLoaderErrorThrower, allocator);
        this.mediaPeriods.add(ssMediaPeriod);
        return ssMediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((SsMediaPeriod) mediaPeriod).release();
        this.mediaPeriods.remove(mediaPeriod);
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

    public void onLoadCompleted(ParsingLoadable<SsManifest> parsingLoadable, long j, long j2) {
        this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, parsingLoadable.type, j, j2, parsingLoadable.bytesLoaded());
        this.manifest = (SsManifest) parsingLoadable.getResult();
        this.manifestLoadStartTimestamp = j - j2;
        processManifest();
        scheduleManifestRefresh();
    }

    public void onLoadCanceled(ParsingLoadable<SsManifest> parsingLoadable, long j, long j2, boolean z) {
        this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, parsingLoadable.type, j, j2, parsingLoadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<SsManifest> parsingLoadable, long j, long j2, IOException iOException) {
        ParsingLoadable<SsManifest> parsingLoadable2 = parsingLoadable;
        IOException iOException2 = iOException;
        boolean z = iOException2 instanceof ParserException;
        this.eventDispatcher.loadError(parsingLoadable2.dataSpec, parsingLoadable2.type, j, j2, parsingLoadable2.bytesLoaded(), iOException2, z);
        return z ? 3 : 0;
    }

    private void processManifest() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r1_4 org.telegram.messenger.exoplayer2.Timeline) in PHI: PHI: (r1_25 org.telegram.messenger.exoplayer2.Timeline) = (r1_4 org.telegram.messenger.exoplayer2.Timeline), (r1_17 org.telegram.messenger.exoplayer2.Timeline), (r1_23 org.telegram.messenger.exoplayer2.Timeline) binds: {(r1_4 org.telegram.messenger.exoplayer2.Timeline)=B:16:0x006e, (r1_17 org.telegram.messenger.exoplayer2.Timeline)=B:28:0x00c3, (r1_23 org.telegram.messenger.exoplayer2.Timeline)=B:34:0x00e4}
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
        r2 = -922337203NUM;
        r4 = r0.manifest;
        r4 = r4.streamElements;
        r7 = r4.length;
        r8 = r2;
        r13 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r2 = r1;
    L_0x002a:
        if (r2 >= r7) goto L_0x0055;
    L_0x002c:
        r3 = r4[r2];
        r10 = r3.chunkCount;
        if (r10 <= 0) goto L_0x0052;
    L_0x0032:
        r10 = r3.getStartTimeUs(r1);
        r10 = java.lang.Math.min(r13, r10);
        r12 = r3.chunkCount;
        r12 = r12 + -1;
        r12 = r3.getStartTimeUs(r12);
        r14 = r3.chunkCount;
        r14 = r14 + -1;
        r14 = r3.getChunkDurationUs(r14);
        r5 = r12 + r14;
        r5 = java.lang.Math.max(r8, r5);
        r8 = r5;
        r13 = r10;
    L_0x0052:
        r2 = r2 + 1;
        goto L_0x002a;
    L_0x0055:
        r2 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r1 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        r2 = 0;
        r4 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r1 != 0) goto L_0x0083;
    L_0x0065:
        r1 = r0.manifest;
        r1 = r1.isLive;
        if (r1 == 0) goto L_0x006d;
    L_0x006b:
        r7 = r4;
        goto L_0x006e;
    L_0x006d:
        r7 = r2;
    L_0x006e:
        r1 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r9 = 0;
        r11 = 0;
        r13 = 0;
        r15 = 1;
        r2 = r0.manifest;
        r2 = r2.isLive;
        r6 = r1;
        r16 = r2;
        r6.<init>(r7, r9, r11, r13, r15, r16);
        goto L_0x00f2;
    L_0x0083:
        r1 = r0.manifest;
        r1 = r1.isLive;
        if (r1 == 0) goto L_0x00d3;
    L_0x0089:
        r1 = r0.manifest;
        r6 = r1.dvrWindowLengthUs;
        r1 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r1 == 0) goto L_0x00a3;
    L_0x0091:
        r1 = r0.manifest;
        r4 = r1.dvrWindowLengthUs;
        r1 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r1 <= 0) goto L_0x00a3;
    L_0x0099:
        r1 = r0.manifest;
        r1 = r1.dvrWindowLengthUs;
        r3 = r8 - r1;
        r13 = java.lang.Math.max(r13, r3);
    L_0x00a3:
        r20 = r13;
        r18 = r8 - r20;
        r1 = r0.livePresentationDelayMs;
        r1 = org.telegram.messenger.exoplayer2.C0542C.msToUs(r1);
        r3 = r18 - r1;
        r1 = 5000000; // 0x4c4b40 float:7.006492E-39 double:2.470328E-317;
        r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1));
        if (r5 >= 0) goto L_0x00c1;
    L_0x00b6:
        r3 = 2;
        r3 = r18 / r3;
        r1 = java.lang.Math.min(r1, r3);
        r22 = r1;
        goto L_0x00c3;
    L_0x00c1:
        r22 = r3;
    L_0x00c3:
        r1 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r16 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r24 = 1;
        r25 = 1;
        r15 = r1;
        r15.<init>(r16, r18, r20, r22, r24, r25);
        goto L_0x00f2;
    L_0x00d3:
        r1 = r0.manifest;
        r1 = r1.durationUs;
        r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x00e1;
    L_0x00db:
        r1 = r0.manifest;
        r1 = r1.durationUs;
    L_0x00df:
        r11 = r1;
        goto L_0x00e4;
    L_0x00e1:
        r1 = r8 - r13;
        goto L_0x00df;
    L_0x00e4:
        r1 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r9 = r13 + r11;
        r15 = 0;
        r17 = 1;
        r18 = 0;
        r8 = r1;
        r8.<init>(r9, r11, r13, r15, r17, r18);
    L_0x00f2:
        r2 = r0.sourceListener;
        r3 = r0.manifest;
        r2.onSourceInfoRefreshed(r0, r1, r3);
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
        ParsingLoadable parsingLoadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
        this.eventDispatcher.loadStarted(parsingLoadable.dataSpec, parsingLoadable.type, this.manifestLoader.startLoading(parsingLoadable, this, this.minLoadableRetryCount));
    }
}
