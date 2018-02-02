package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
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
        Timeline timeline;
        for (int i = 0; i < this.mediaPeriods.size(); i++) {
            ((SsMediaPeriod) this.mediaPeriods.get(i)).updateManifest(this.manifest);
        }
        long endTimeUs = Long.MIN_VALUE;
        StreamElement[] streamElementArr = this.manifest.streamElements;
        int length = streamElementArr.length;
        int i2 = 0;
        long startTimeUs = Long.MAX_VALUE;
        while (i2 < length) {
            long startTimeUs2;
            StreamElement element = streamElementArr[i2];
            if (element.chunkCount > 0) {
                startTimeUs2 = Math.min(startTimeUs, element.getStartTimeUs(0));
                endTimeUs = Math.max(endTimeUs, element.getStartTimeUs(element.chunkCount - 1) + element.getChunkDurationUs(element.chunkCount - 1));
            } else {
                startTimeUs2 = startTimeUs;
            }
            i2++;
            startTimeUs = startTimeUs2;
        }
        if (startTimeUs == Long.MAX_VALUE) {
            timeline = new SinglePeriodTimeline(this.manifest.isLive ? C.TIME_UNSET : 0, 0, 0, 0, true, this.manifest.isLive);
            startTimeUs2 = startTimeUs;
        } else if (this.manifest.isLive) {
            if (this.manifest.dvrWindowLengthUs == C.TIME_UNSET || this.manifest.dvrWindowLengthUs <= 0) {
                startTimeUs2 = startTimeUs;
            } else {
                startTimeUs2 = Math.max(startTimeUs, endTimeUs - this.manifest.dvrWindowLengthUs);
            }
            durationUs = endTimeUs - startTimeUs2;
            long defaultStartPositionUs = durationUs - C.msToUs(this.livePresentationDelayMs);
            if (defaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                defaultStartPositionUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, durationUs / 2);
            }
            Timeline singlePeriodTimeline = new SinglePeriodTimeline(C.TIME_UNSET, durationUs, startTimeUs2, defaultStartPositionUs, true, true);
        } else {
            durationUs = this.manifest.durationUs != C.TIME_UNSET ? this.manifest.durationUs : endTimeUs - startTimeUs;
            Timeline singlePeriodTimeline2 = new SinglePeriodTimeline(startTimeUs + durationUs, durationUs, startTimeUs, 0, true, false);
            startTimeUs2 = startTimeUs;
        }
        this.sourceListener.onSourceInfoRefreshed(this, timeline, this.manifest);
    }

    private void scheduleManifestRefresh() {
        if (this.manifest.isLive) {
            this.manifestRefreshHandler.postDelayed(new Runnable() {
                public void run() {
                    SsMediaSource.this.startLoadingManifest();
                }
            }, Math.max(0, (this.manifestLoadStartTimestamp + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) - SystemClock.elapsedRealtime()));
        }
    }

    private void startLoadingManifest() {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.manifestLoader.startLoading(loadable, this, this.minLoadableRetryCount));
    }
}
