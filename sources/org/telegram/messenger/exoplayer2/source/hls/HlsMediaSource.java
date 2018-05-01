package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class HlsMediaSource implements MediaSource, PrimaryPlaylistListener {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private final boolean allowChunklessPreparation;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final Uri manifestUri;
    private final int minLoadableRetryCount;
    private final Parser<HlsPlaylist> playlistParser;
    private HlsPlaylistTracker playlistTracker;
    private Listener sourceListener;

    public static final class Factory implements MediaSourceFactory {
        private boolean allowChunklessPreparation;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private HlsExtractorFactory extractorFactory;
        private final HlsDataSourceFactory hlsDataSourceFactory;
        private boolean isCreateCalled;
        private int minLoadableRetryCount;
        private Parser<HlsPlaylist> playlistParser;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory) {
            this(new DefaultHlsDataSourceFactory(factory));
        }

        public Factory(HlsDataSourceFactory hlsDataSourceFactory) {
            this.hlsDataSourceFactory = (HlsDataSourceFactory) Assertions.checkNotNull(hlsDataSourceFactory);
            this.extractorFactory = HlsExtractorFactory.DEFAULT;
            this.minLoadableRetryCount = 3;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setExtractorFactory(HlsExtractorFactory hlsExtractorFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.extractorFactory = (HlsExtractorFactory) Assertions.checkNotNull(hlsExtractorFactory);
            return this;
        }

        public Factory setMinLoadableRetryCount(int i) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = i;
            return this;
        }

        public Factory setPlaylistParser(Parser<HlsPlaylist> parser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.playlistParser = (Parser) Assertions.checkNotNull(parser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public Factory setAllowChunklessPreparation(boolean z) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.allowChunklessPreparation = z;
            return this;
        }

        public HlsMediaSource createMediaSource(Uri uri) {
            return createMediaSource(uri, null, null);
        }

        public HlsMediaSource createMediaSource(Uri uri, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            this.isCreateCalled = true;
            if (this.playlistParser == null) {
                this.playlistParser = new HlsPlaylistParser();
            }
            return new HlsMediaSource(uri, this.hlsDataSourceFactory, this.extractorFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, handler, mediaSourceEventListener, this.playlistParser, this.allowChunklessPreparation);
        }

        public int[] getSupportedTypes() {
            return new int[]{2};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
    }

    @Deprecated
    public HlsMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, 3, handler, mediaSourceEventListener);
    }

    @Deprecated
    public HlsMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, new DefaultHlsDataSourceFactory(factory), HlsExtractorFactory.DEFAULT, i, handler, mediaSourceEventListener, new HlsPlaylistParser());
    }

    @Deprecated
    public HlsMediaSource(Uri uri, HlsDataSourceFactory hlsDataSourceFactory, HlsExtractorFactory hlsExtractorFactory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener, Parser<HlsPlaylist> parser) {
        this(uri, hlsDataSourceFactory, hlsExtractorFactory, new DefaultCompositeSequenceableLoaderFactory(), i, handler, mediaSourceEventListener, parser, false);
    }

    private HlsMediaSource(Uri uri, HlsDataSourceFactory hlsDataSourceFactory, HlsExtractorFactory hlsExtractorFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener, Parser<HlsPlaylist> parser, boolean z) {
        this.manifestUri = uri;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.extractorFactory = hlsExtractorFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.minLoadableRetryCount = i;
        this.playlistParser = parser;
        this.allowChunklessPreparation = z;
        this.eventDispatcher = new EventDispatcher(handler, mediaSourceEventListener);
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(this.sourceListener == null ? true : null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.playlistTracker = new HlsPlaylistTracker(this.manifestUri, this.dataSourceFactory, this.eventDispatcher, this.minLoadableRetryCount, this, this.playlistParser);
        this.sourceListener = listener;
        this.playlistTracker.start();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        Assertions.checkArgument(mediaPeriodId.periodIndex == null ? true : null);
        return new HlsMediaPeriod(this.extractorFactory, this.playlistTracker, this.dataSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, allocator, this.compositeSequenceableLoaderFactory, this.allowChunklessPreparation);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((HlsMediaPeriod) mediaPeriod).release();
    }

    public void releaseSource() {
        if (this.playlistTracker != null) {
            this.playlistTracker.release();
            this.playlistTracker = null;
        }
    }

    public void onPrimaryPlaylistRefreshed(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist r24) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r2_8 org.telegram.messenger.exoplayer2.Timeline) in PHI: PHI: (r2_11 org.telegram.messenger.exoplayer2.Timeline) = (r2_8 org.telegram.messenger.exoplayer2.Timeline), (r2_10 org.telegram.messenger.exoplayer2.Timeline) binds: {(r2_8 org.telegram.messenger.exoplayer2.Timeline)=B:22:0x005b, (r2_10 org.telegram.messenger.exoplayer2.Timeline)=B:27:0x0079}
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
        r23 = this;
        r0 = r23;
        r1 = r24;
        r2 = r1.hasProgramDateTime;
        r5 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r2 == 0) goto L_0x0010;
    L_0x000d:
        r8 = 0;
        goto L_0x0011;
    L_0x0010:
        r8 = r5;
    L_0x0011:
        r2 = r1.hasProgramDateTime;
        if (r2 == 0) goto L_0x001c;
    L_0x0015:
        r10 = r1.startTimeUs;
        r10 = org.telegram.messenger.exoplayer2.C0542C.usToMs(r10);
        goto L_0x001d;
    L_0x001c:
        r10 = r5;
    L_0x001d:
        r12 = r1.startOffsetUs;
        r2 = r0.playlistTracker;
        r2 = r2.isLive();
        if (r2 == 0) goto L_0x0070;
    L_0x0027:
        r2 = r1.hasEndTag;
        if (r2 == 0) goto L_0x0032;
    L_0x002b:
        r14 = r1.startTimeUs;
        r3 = r1.durationUs;
        r16 = r14 + r3;
        goto L_0x0034;
    L_0x0032:
        r16 = r5;
    L_0x0034:
        r2 = r1.segments;
        r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1));
        if (r3 != 0) goto L_0x0059;
    L_0x003a:
        r3 = r2.isEmpty();
        if (r3 == 0) goto L_0x0043;
    L_0x0040:
        r3 = 0;
        goto L_0x0056;
    L_0x0043:
        r3 = 0;
        r4 = r2.size();
        r4 = r4 + -3;
        r3 = java.lang.Math.max(r3, r4);
        r2 = r2.get(r3);
        r2 = (org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment) r2;
        r3 = r2.relativeStartTimeUs;
    L_0x0056:
        r18 = r3;
        goto L_0x005b;
    L_0x0059:
        r18 = r12;
    L_0x005b:
        r2 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r14 = r1.durationUs;
        r3 = r1.startTimeUs;
        r20 = 1;
        r5 = r1.hasEndTag;
        r21 = r5 ^ 1;
        r7 = r2;
        r12 = r16;
        r16 = r3;
        r7.<init>(r8, r10, r12, r14, r16, r18, r20, r21);
        goto L_0x008f;
    L_0x0070:
        r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1));
        if (r2 != 0) goto L_0x0077;
    L_0x0074:
        r18 = 0;
        goto L_0x0079;
    L_0x0077:
        r18 = r12;
    L_0x0079:
        r2 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r3 = r1.startTimeUs;
        r5 = r1.durationUs;
        r12 = r3 + r5;
        r14 = r1.durationUs;
        r3 = r1.startTimeUs;
        r20 = 1;
        r21 = 0;
        r7 = r2;
        r16 = r3;
        r7.<init>(r8, r10, r12, r14, r16, r18, r20, r21);
    L_0x008f:
        r3 = r0.sourceListener;
        r4 = new org.telegram.messenger.exoplayer2.source.hls.HlsManifest;
        r5 = r0.playlistTracker;
        r5 = r5.getMasterPlaylist();
        r4.<init>(r5, r1);
        r3.onSourceInfoRefreshed(r0, r2, r4);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource.onPrimaryPlaylistRefreshed(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist):void");
    }
}
