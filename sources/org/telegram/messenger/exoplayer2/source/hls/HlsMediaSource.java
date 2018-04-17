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

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(new DefaultHlsDataSourceFactory(dataSourceFactory));
        }

        public Factory(HlsDataSourceFactory hlsDataSourceFactory) {
            this.hlsDataSourceFactory = (HlsDataSourceFactory) Assertions.checkNotNull(hlsDataSourceFactory);
            this.extractorFactory = HlsExtractorFactory.DEFAULT;
            this.minLoadableRetryCount = 3;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setExtractorFactory(HlsExtractorFactory extractorFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.extractorFactory = (HlsExtractorFactory) Assertions.checkNotNull(extractorFactory);
            return this;
        }

        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setPlaylistParser(Parser<HlsPlaylist> playlistParser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.playlistParser = (Parser) Assertions.checkNotNull(playlistParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public Factory setAllowChunklessPreparation(boolean allowChunklessPreparation) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.allowChunklessPreparation = allowChunklessPreparation;
            return this;
        }

        public HlsMediaSource createMediaSource(Uri playlistUri) {
            return createMediaSource(playlistUri, null, null);
        }

        public HlsMediaSource createMediaSource(Uri playlistUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            if (this.playlistParser == null) {
                this.playlistParser = new HlsPlaylistParser();
            }
            return new HlsMediaSource(playlistUri, this.hlsDataSourceFactory, this.extractorFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, eventHandler, eventListener, this.playlistParser, this.allowChunklessPreparation);
        }

        public int[] getSupportedTypes() {
            return new int[]{2};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, dataSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, new DefaultHlsDataSourceFactory(dataSourceFactory), HlsExtractorFactory.DEFAULT, minLoadableRetryCount, eventHandler, eventListener, new HlsPlaylistParser());
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, HlsDataSourceFactory dataSourceFactory, HlsExtractorFactory extractorFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener, Parser<HlsPlaylist> playlistParser) {
        this(manifestUri, dataSourceFactory, extractorFactory, new DefaultCompositeSequenceableLoaderFactory(), minLoadableRetryCount, eventHandler, eventListener, playlistParser, false);
    }

    private HlsMediaSource(Uri manifestUri, HlsDataSourceFactory dataSourceFactory, HlsExtractorFactory extractorFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener, Parser<HlsPlaylist> playlistParser, boolean allowChunklessPreparation) {
        this.manifestUri = manifestUri;
        this.dataSourceFactory = dataSourceFactory;
        this.extractorFactory = extractorFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.playlistParser = playlistParser;
        this.allowChunklessPreparation = allowChunklessPreparation;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        Assertions.checkState(this.sourceListener == null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.playlistTracker = new HlsPlaylistTracker(this.manifestUri, this.dataSourceFactory, this.eventDispatcher, this.minLoadableRetryCount, this, this.playlistParser);
        this.sourceListener = listener;
        this.playlistTracker.start();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        Assertions.checkArgument(id.periodIndex == 0);
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

    public void onPrimaryPlaylistRefreshed(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist r32) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r2_9 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) in PHI: PHI: (r2_10 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) = (r2_7 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline), (r2_9 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) binds: {(r2_7 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline)=B:22:0x0059, (r2_9 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline)=B:26:0x0079}
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
        r31 = this;
        r0 = r31;
        r1 = r32;
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
        r14 = r1.startOffsetUs;
        r2 = r0.playlistTracker;
        r2 = r2.isLive();
        if (r2 == 0) goto L_0x0073;
    L_0x0027:
        r2 = r1.hasEndTag;
        if (r2 == 0) goto L_0x0034;
    L_0x002b:
        r12 = r1.startTimeUs;
        r3 = r1.durationUs;
        r16 = r12 + r3;
        r12 = r16;
        goto L_0x0035;
    L_0x0034:
        r12 = r5;
    L_0x0035:
        r2 = r1.segments;
        r3 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));
        if (r3 != 0) goto L_0x0058;
    L_0x003b:
        r3 = r2.isEmpty();
        if (r3 == 0) goto L_0x0044;
    L_0x0041:
        r3 = 0;
        goto L_0x0057;
    L_0x0044:
        r3 = 0;
        r4 = r2.size();
        r4 = r4 + -3;
        r3 = java.lang.Math.max(r3, r4);
        r3 = r2.get(r3);
        r3 = (org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment) r3;
        r3 = r3.relativeStartTimeUs;
    L_0x0057:
        goto L_0x0059;
    L_0x0058:
        r3 = r14;
    L_0x0059:
        r5 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r14 = r1.durationUs;
        r6 = r1.startTimeUs;
        r20 = 1;
        r24 = r2;
        r2 = r1.hasEndTag;
        r21 = r2 ^ 1;
        r16 = r6;
        r7 = r5;
        r18 = r3;
        r7.<init>(r8, r10, r12, r14, r16, r18, r20, r21);
        r2 = r5;
        r14 = r3;
        goto L_0x0098;
    L_0x0073:
        r2 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));
        if (r2 != 0) goto L_0x0079;
    L_0x0077:
        r14 = 0;
    L_0x0079:
        r2 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r3 = r1.startTimeUs;
        r5 = r1.durationUs;
        r21 = r3 + r5;
        r3 = r1.durationUs;
        r5 = r1.startTimeUs;
        r29 = 1;
        r30 = 0;
        r16 = r2;
        r17 = r8;
        r19 = r10;
        r23 = r3;
        r25 = r5;
        r27 = r14;
        r16.<init>(r17, r19, r21, r23, r25, r27, r29, r30);
    L_0x0098:
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
