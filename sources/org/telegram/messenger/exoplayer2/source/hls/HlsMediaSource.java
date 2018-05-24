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
            Assertions.checkState(!this.isCreateCalled);
            this.extractorFactory = (HlsExtractorFactory) Assertions.checkNotNull(extractorFactory);
            return this;
        }

        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(!this.isCreateCalled);
            this.minLoadableRetryCount = minLoadableRetryCount;
            return this;
        }

        public Factory setPlaylistParser(Parser<HlsPlaylist> playlistParser) {
            Assertions.checkState(!this.isCreateCalled);
            this.playlistParser = (Parser) Assertions.checkNotNull(playlistParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(!this.isCreateCalled);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public Factory setAllowChunklessPreparation(boolean allowChunklessPreparation) {
            Assertions.checkState(!this.isCreateCalled);
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

    public void onPrimaryPlaylistRefreshed(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist r33) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r3_2 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) in PHI: PHI: (r3_1 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) = (r3_0 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline), (r3_2 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline) binds: {(r3_0 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline)=B:19:0x005f, (r3_2 'timeline' org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline)=B:30:0x00af}
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
        r32 = this;
        r0 = r33;
        r10 = r0.hasProgramDateTime;
        if (r10 == 0) goto L_0x007b;
    L_0x0006:
        r4 = 0;
    L_0x0008:
        r0 = r33;
        r10 = r0.hasProgramDateTime;
        if (r10 == 0) goto L_0x0081;
    L_0x000e:
        r0 = r33;
        r10 = r0.startTimeUs;
        r6 = org.telegram.messenger.exoplayer2.C0600C.usToMs(r10);
    L_0x0016:
        r0 = r33;
        r14 = r0.startOffsetUs;
        r0 = r32;
        r10 = r0.playlistTracker;
        r10 = r10.isLive();
        if (r10 == 0) goto L_0x00a4;
    L_0x0024:
        r0 = r33;
        r10 = r0.hasEndTag;
        if (r10 == 0) goto L_0x0087;
    L_0x002a:
        r0 = r33;
        r10 = r0.startTimeUs;
        r0 = r33;
        r12 = r0.durationUs;
        r8 = r10 + r12;
    L_0x0034:
        r0 = r33;
        r2 = r0.segments;
        r10 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r10 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r10 != 0) goto L_0x0049;
    L_0x0041:
        r10 = r2.isEmpty();
        if (r10 == 0) goto L_0x008d;
    L_0x0047:
        r14 = 0;
    L_0x0049:
        r3 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r0 = r33;
        r10 = r0.durationUs;
        r0 = r33;
        r12 = r0.startTimeUs;
        r16 = 1;
        r0 = r33;
        r0 = r0.hasEndTag;
        r17 = r0;
        if (r17 != 0) goto L_0x00a1;
    L_0x005d:
        r17 = 1;
    L_0x005f:
        r3.<init>(r4, r6, r8, r10, r12, r14, r16, r17);
    L_0x0062:
        r0 = r32;
        r10 = r0.sourceListener;
        r11 = new org.telegram.messenger.exoplayer2.source.hls.HlsManifest;
        r0 = r32;
        r12 = r0.playlistTracker;
        r12 = r12.getMasterPlaylist();
        r0 = r33;
        r11.<init>(r12, r0);
        r0 = r32;
        r10.onSourceInfoRefreshed(r0, r3, r11);
        return;
    L_0x007b:
        r4 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        goto L_0x0008;
    L_0x0081:
        r6 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        goto L_0x0016;
    L_0x0087:
        r8 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        goto L_0x0034;
    L_0x008d:
        r10 = 0;
        r11 = r2.size();
        r11 = r11 + -3;
        r10 = java.lang.Math.max(r10, r11);
        r10 = r2.get(r10);
        r10 = (org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment) r10;
        r14 = r10.relativeStartTimeUs;
        goto L_0x0049;
    L_0x00a1:
        r17 = 0;
        goto L_0x005f;
    L_0x00a4:
        r10 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r10 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r10 != 0) goto L_0x00af;
    L_0x00ad:
        r14 = 0;
    L_0x00af:
        r3 = new org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
        r0 = r33;
        r10 = r0.startTimeUs;
        r0 = r33;
        r12 = r0.durationUs;
        r22 = r10 + r12;
        r0 = r33;
        r0 = r0.durationUs;
        r24 = r0;
        r0 = r33;
        r0 = r0.startTimeUs;
        r26 = r0;
        r30 = 1;
        r31 = 0;
        r17 = r3;
        r18 = r4;
        r20 = r6;
        r28 = r14;
        r17.<init>(r18, r20, r22, r24, r26, r28, r30, r31);
        goto L_0x0062;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource.onPrimaryPlaylistRefreshed(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist):void");
    }
}
