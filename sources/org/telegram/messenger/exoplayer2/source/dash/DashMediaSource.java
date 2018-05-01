package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifestParser;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.source.dash.manifest.UtcTimingElement;
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

public final class DashMediaSource implements MediaSource {
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000;
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
    private static final String TAG = "DashMediaSource";
    private final org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private DataSource dataSource;
    private boolean dynamicMediaPresentationEnded;
    private long elapsedRealtimeOffsetMs;
    private final EventDispatcher eventDispatcher;
    private long expiredManifestPublishTimeUs;
    private int firstPeriodId;
    private Handler handler;
    private final long livePresentationDelayMs;
    private Loader loader;
    private DashManifest manifest;
    private final ManifestCallback manifestCallback;
    private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
    private IOException manifestFatalError;
    private long manifestLoadEndTimestampMs;
    private LoaderErrorThrower manifestLoadErrorThrower;
    private boolean manifestLoadPending;
    private long manifestLoadStartTimestampMs;
    private final Parser<? extends DashManifest> manifestParser;
    private Uri manifestUri;
    private final Object manifestUriLock;
    private final int minLoadableRetryCount;
    private final SparseArray<DashMediaPeriod> periodsById;
    private final PlayerEmsgCallback playerEmsgCallback;
    private final Runnable refreshManifestRunnable;
    private final boolean sideloadedManifest;
    private final Runnable simulateManifestRefreshRunnable;
    private Listener sourceListener;
    private int staleManifestReloadAttempt;

    /* renamed from: org.telegram.messenger.exoplayer2.source.dash.DashMediaSource$1 */
    class C06101 implements Runnable {
        C06101() {
        }

        public void run() {
            DashMediaSource.this.startLoadingManifest();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.source.dash.DashMediaSource$2 */
    class C06112 implements Runnable {
        C06112() {
        }

        public void run() {
            DashMediaSource.this.processManifest(false);
        }
    }

    private static final class PeriodSeekInfo {
        public final long availableEndTimeUs;
        public final long availableStartTimeUs;
        public final boolean isIndexExplicit;

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long j) {
            Period period2 = period;
            long j2 = j;
            int size = period2.adaptationSets.size();
            int i = 0;
            boolean z = false;
            long j3 = Long.MAX_VALUE;
            long j4 = 0;
            int i2 = z;
            boolean z2 = i2;
            while (i2 < size) {
                DashSegmentIndex index = ((Representation) ((AdaptationSet) period2.adaptationSets.get(i2)).representations.get(i)).getIndex();
                if (index == null) {
                    return new PeriodSeekInfo(true, 0, j2);
                }
                int i3;
                z |= index.isExplicit();
                int segmentCount = index.getSegmentCount(j2);
                if (segmentCount == 0) {
                    i3 = i2;
                    z2 = true;
                    j4 = 0;
                    j3 = 0;
                } else if (z2) {
                    i3 = i2;
                } else {
                    int firstSegmentNum = index.getFirstSegmentNum();
                    i3 = i2;
                    long max = Math.max(j4, index.getTimeUs(firstSegmentNum));
                    if (segmentCount != -1) {
                        firstSegmentNum = (firstSegmentNum + segmentCount) - 1;
                        j3 = Math.min(j3, index.getTimeUs(firstSegmentNum) + index.getDurationUs(firstSegmentNum, j2));
                    }
                    j4 = max;
                }
                i2 = i3 + 1;
                i = 0;
            }
            return new PeriodSeekInfo(z, j4, j3);
        }

        private PeriodSeekInfo(boolean z, long j, long j2) {
            this.isIndexExplicit = z;
            this.availableStartTimeUs = j;
            this.availableEndTimeUs = j2;
        }
    }

    private static final class DashTimeline extends Timeline {
        private final int firstPeriodId;
        private final DashManifest manifest;
        private final long offsetInFirstPeriodUs;
        private final long presentationStartTimeMs;
        private final long windowDefaultStartPositionUs;
        private final long windowDurationUs;
        private final long windowStartTimeMs;

        public int getWindowCount() {
            return 1;
        }

        public DashTimeline(long j, long j2, int i, long j3, long j4, long j5, DashManifest dashManifest) {
            this.presentationStartTimeMs = j;
            this.windowStartTimeMs = j2;
            this.firstPeriodId = i;
            this.offsetInFirstPeriodUs = j3;
            this.windowDurationUs = j4;
            this.windowDefaultStartPositionUs = j5;
            this.manifest = dashManifest;
        }

        public int getPeriodCount() {
            return this.manifest.getPeriodCount();
        }

        public Timeline.Period getPeriod(int i, Timeline.Period period, boolean z) {
            Assertions.checkIndex(i, 0, this.manifest.getPeriodCount());
            Integer num = null;
            Object obj = z ? this.manifest.getPeriod(i).id : null;
            if (z) {
                num = Integer.valueOf(this.firstPeriodId + Assertions.checkIndex(i, 0, this.manifest.getPeriodCount()));
            }
            return period.set(obj, num, 0, this.manifest.getPeriodDurationUs(i), C0542C.msToUs(this.manifest.getPeriod(i).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        public Window getWindow(int i, Window window, boolean z, long j) {
            Assertions.checkIndex(i, 0, 1);
            return window.set(null, this.presentationStartTimeMs, this.windowStartTimeMs, true, this.manifest.dynamic, getAdjustedWindowDefaultStartPositionUs(j), this.windowDurationUs, 0, this.manifest.getPeriodCount() - 1, this.offsetInFirstPeriodUs);
        }

        public int getIndexOfPeriod(Object obj) {
            int i = -1;
            if (!(obj instanceof Integer)) {
                return -1;
            }
            obj = ((Integer) obj).intValue();
            if (obj >= this.firstPeriodId) {
                if (obj < this.firstPeriodId + getPeriodCount()) {
                    i = obj - this.firstPeriodId;
                }
            }
            return i;
        }

        private long getAdjustedWindowDefaultStartPositionUs(long j) {
            long j2 = this.windowDefaultStartPositionUs;
            if (!this.manifest.dynamic) {
                return j2;
            }
            long j3;
            if (j > 0) {
                j3 = j2 + j;
                if (j3 > this.windowDurationUs) {
                    return C0542C.TIME_UNSET;
                }
                j2 = j3;
            }
            j3 = this.offsetInFirstPeriodUs + j2;
            long periodDurationUs = this.manifest.getPeriodDurationUs(0);
            j = 0;
            while (j < this.manifest.getPeriodCount() - 1 && j3 >= periodDurationUs) {
                long j4 = j3 - periodDurationUs;
                j++;
                periodDurationUs = this.manifest.getPeriodDurationUs(j);
                j3 = j4;
            }
            j = this.manifest.getPeriod(j);
            int adaptationSetIndex = j.getAdaptationSetIndex(2);
            if (adaptationSetIndex == -1) {
                return j2;
            }
            j = ((Representation) ((AdaptationSet) j.adaptationSets.get(adaptationSetIndex)).representations.get(0)).getIndex();
            if (j != null) {
                if (j.getSegmentCount(periodDurationUs) != 0) {
                    return (j2 + j.getTimeUs(j.getSegmentNum(j3, periodDurationUs))) - j3;
                }
            }
            return j2;
        }
    }

    private final class DefaultPlayerEmsgCallback implements PlayerEmsgCallback {
        private DefaultPlayerEmsgCallback() {
        }

        public void onDashManifestRefreshRequested() {
            DashMediaSource.this.onDashManifestRefreshRequested();
        }

        public void onDashManifestPublishTimeExpired(long j) {
            DashMediaSource.this.onDashManifestPublishTimeExpired(j);
        }

        public void onDashLiveMediaPresentationEndSignalEncountered() {
            DashMediaSource.this.onDashLiveMediaPresentationEndSignalEncountered();
        }
    }

    public static final class Factory implements MediaSourceFactory {
        private final org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        private boolean isCreateCalled;
        private long livePresentationDelayMs = -1;
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
        private Parser<? extends DashManifest> manifestParser;
        private int minLoadableRetryCount = 3;

        public Factory(org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory2) {
            this.chunkSourceFactory = (org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory) Assertions.checkNotNull(factory);
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

        public Factory setManifestParser(Parser<? extends DashManifest> parser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(parser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public DashMediaSource createMediaSource(DashManifest dashManifest, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            DashManifest dashManifest2 = dashManifest;
            Assertions.checkArgument(dashManifest2.dynamic ^ true);
            this.isCreateCalled = true;
            return new DashMediaSource(dashManifest2, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, handler, mediaSourceEventListener);
        }

        public DashMediaSource createMediaSource(Uri uri) {
            return createMediaSource(uri, null, null);
        }

        public DashMediaSource createMediaSource(Uri uri, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                r0.manifestParser = new DashManifestParser();
            }
            return new DashMediaSource(null, (Uri) Assertions.checkNotNull(uri), r0.manifestDataSourceFactory, r0.manifestParser, r0.chunkSourceFactory, r0.compositeSequenceableLoaderFactory, r0.minLoadableRetryCount, r0.livePresentationDelayMs, handler, mediaSourceEventListener);
        }

        public int[] getSupportedTypes() {
            return new int[]{0};
        }
    }

    static final class Iso8601Parser implements Parser<Long> {
        private static final Pattern TIMESTAMP_WITH_TIMEZONE_PATTERN = Pattern.compile("(.+?)(Z|((\\+|-|\u2212)(\\d\\d)(:?(\\d\\d))?))");

        Iso8601Parser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            uri = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                inputStream = TIMESTAMP_WITH_TIMEZONE_PATTERN.matcher(uri);
                if (inputStream.matches()) {
                    uri = inputStream.group(1);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    long time = simpleDateFormat.parse(uri).getTime();
                    if ("Z".equals(inputStream.group(2)) != null) {
                        uri = time;
                    } else {
                        long j = "+".equals(inputStream.group(4)) != null ? 1 : -1;
                        long parseLong = Long.parseLong(inputStream.group(5));
                        uri = inputStream.group(7);
                        uri = time - (j * ((((parseLong * 60) + (TextUtils.isEmpty(uri) != null ? 0 : Long.parseLong(uri))) * 60) * 1000));
                    }
                    return Long.valueOf(uri);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't parse timestamp: ");
                stringBuilder.append(uri);
                throw new ParserException(stringBuilder.toString());
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private final class ManifestCallback implements Callback<ParsingLoadable<DashManifest>> {
        private ManifestCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<DashManifest> parsingLoadable, long j, long j2) {
            DashMediaSource.this.onManifestLoadCompleted(parsingLoadable, j, j2);
        }

        public void onLoadCanceled(ParsingLoadable<DashManifest> parsingLoadable, long j, long j2, boolean z) {
            DashMediaSource.this.onLoadCanceled(parsingLoadable, j, j2);
        }

        public int onLoadError(ParsingLoadable<DashManifest> parsingLoadable, long j, long j2, IOException iOException) {
            return DashMediaSource.this.onManifestLoadError(parsingLoadable, j, j2, iOException);
        }
    }

    final class ManifestLoadErrorThrower implements LoaderErrorThrower {
        ManifestLoadErrorThrower() {
        }

        public void maybeThrowError() throws IOException {
            DashMediaSource.this.loader.maybeThrowError();
            maybeThrowManifestError();
        }

        public void maybeThrowError(int i) throws IOException {
            DashMediaSource.this.loader.maybeThrowError(i);
            maybeThrowManifestError();
        }

        private void maybeThrowManifestError() throws IOException {
            if (DashMediaSource.this.manifestFatalError != null) {
                throw DashMediaSource.this.manifestFatalError;
            }
        }
    }

    private final class UtcTimestampCallback implements Callback<ParsingLoadable<Long>> {
        private UtcTimestampCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<Long> parsingLoadable, long j, long j2) {
            DashMediaSource.this.onUtcTimestampLoadCompleted(parsingLoadable, j, j2);
        }

        public void onLoadCanceled(ParsingLoadable<Long> parsingLoadable, long j, long j2, boolean z) {
            DashMediaSource.this.onLoadCanceled(parsingLoadable, j, j2);
        }

        public int onLoadError(ParsingLoadable<Long> parsingLoadable, long j, long j2, IOException iOException) {
            return DashMediaSource.this.onUtcTimestampLoadError(parsingLoadable, j, j2, iOException);
        }
    }

    private static final class XsDateTimeParser implements Parser<Long> {
        private XsDateTimeParser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.dash");
    }

    @Deprecated
    public DashMediaSource(DashManifest dashManifest, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(dashManifest, factory, 3, handler, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(DashManifest dashManifest, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(dashManifest, null, null, null, factory, new DefaultCompositeSequenceableLoaderFactory(), i, -1, handler, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory2, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, factory2, 3, -1, handler, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory2, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, new DashManifestParser(), factory2, i, j, handler, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Parser<? extends DashManifest> parser, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory2, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(null, uri, factory, parser, factory2, new DefaultCompositeSequenceableLoaderFactory(), i, j, handler, mediaSourceEventListener);
    }

    private DashMediaSource(DashManifest dashManifest, Uri uri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, Parser<? extends DashManifest> parser, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory factory2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int i, long j, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this.manifest = dashManifest;
        this.manifestUri = uri;
        this.manifestDataSourceFactory = factory;
        this.manifestParser = parser;
        this.chunkSourceFactory = factory2;
        this.minLoadableRetryCount = i;
        this.livePresentationDelayMs = j;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.sideloadedManifest = dashManifest != null ? 1 : null;
        this.eventDispatcher = new EventDispatcher(handler, mediaSourceEventListener);
        this.manifestUriLock = new Object();
        this.periodsById = new SparseArray();
        this.playerEmsgCallback = new DefaultPlayerEmsgCallback();
        this.expiredManifestPublishTimeUs = 1;
        if (this.sideloadedManifest != null) {
            Assertions.checkState(dashManifest.dynamic ^ 1);
            this.manifestCallback = null;
            this.refreshManifestRunnable = null;
            this.simulateManifestRefreshRunnable = null;
            return;
        }
        this.manifestCallback = new ManifestCallback();
        this.refreshManifestRunnable = new C06101();
        this.simulateManifestRefreshRunnable = new C06112();
    }

    public void replaceManifestUri(Uri uri) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = uri;
        }
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(this.sourceListener == null ? true : null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.sourceListener = listener;
        if (this.sideloadedManifest != null) {
            this.manifestLoadErrorThrower = new Dummy();
            processManifest(false);
            return;
        }
        this.dataSource = this.manifestDataSourceFactory.createDataSource();
        this.loader = new Loader("Loader:DashMediaSource");
        this.manifestLoadErrorThrower = new ManifestLoadErrorThrower();
        this.handler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoadErrorThrower.maybeThrowError();
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        int i = mediaPeriodId.periodIndex;
        MediaPeriodId dashMediaPeriod = new DashMediaPeriod(this.firstPeriodId + i, this.manifest, i, this.chunkSourceFactory, this.minLoadableRetryCount, this.eventDispatcher.copyWithMediaTimeOffsetMs(this.manifest.getPeriod(i).startMs), this.elapsedRealtimeOffsetMs, this.manifestLoadErrorThrower, allocator, this.compositeSequenceableLoaderFactory, this.playerEmsgCallback);
        this.periodsById.put(dashMediaPeriod.id, dashMediaPeriod);
        return dashMediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DashMediaPeriod dashMediaPeriod = (DashMediaPeriod) mediaPeriod;
        dashMediaPeriod.release();
        this.periodsById.remove(dashMediaPeriod.id);
    }

    public void releaseSource() {
        this.manifestLoadPending = false;
        this.dataSource = null;
        this.manifestLoadErrorThrower = null;
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
        this.manifestLoadStartTimestampMs = 0;
        this.manifestLoadEndTimestampMs = 0;
        this.manifest = null;
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages(null);
            this.handler = null;
        }
        this.elapsedRealtimeOffsetMs = 0;
        this.periodsById.clear();
    }

    void onDashManifestRefreshRequested() {
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        startLoadingManifest();
    }

    void onDashLiveMediaPresentationEndSignalEncountered() {
        this.dynamicMediaPresentationEnded = true;
    }

    void onDashManifestPublishTimeExpired(long j) {
        if (this.expiredManifestPublishTimeUs == C0542C.TIME_UNSET || this.expiredManifestPublishTimeUs < j) {
            this.expiredManifestPublishTimeUs = j;
        }
    }

    void onManifestLoadCompleted(ParsingLoadable<DashManifest> parsingLoadable, long j, long j2) {
        this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, parsingLoadable.type, j, j2, parsingLoadable.bytesLoaded());
        DashManifest dashManifest = (DashManifest) parsingLoadable.getResult();
        int periodCount = this.manifest == null ? 0 : this.manifest.getPeriodCount();
        long j3 = dashManifest.getPeriod(0).startMs;
        int i = 0;
        while (i < periodCount && this.manifest.getPeriod(i).startMs < j3) {
            i++;
        }
        if (dashManifest.dynamic) {
            boolean z;
            if (periodCount - i > dashManifest.getPeriodCount()) {
                Log.w(TAG, "Loaded out of sync manifest");
            } else {
                if (!this.dynamicMediaPresentationEnded) {
                    if (dashManifest.publishTimeMs > this.expiredManifestPublishTimeUs) {
                        z = false;
                        if (z) {
                            this.staleManifestReloadAttempt = 0;
                        } else {
                            parsingLoadable = this.staleManifestReloadAttempt;
                            this.staleManifestReloadAttempt = parsingLoadable + 1;
                            if (parsingLoadable >= this.minLoadableRetryCount) {
                                scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                            } else {
                                this.manifestFatalError = new DashManifestStaleException();
                            }
                            return;
                        }
                    }
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Loaded stale dynamic manifest: ");
                stringBuilder.append(dashManifest.publishTimeMs);
                stringBuilder.append(", ");
                stringBuilder.append(this.dynamicMediaPresentationEnded);
                stringBuilder.append(", ");
                stringBuilder.append(this.expiredManifestPublishTimeUs);
                Log.w(str, stringBuilder.toString());
            }
            z = true;
            if (z) {
                this.staleManifestReloadAttempt = 0;
            } else {
                parsingLoadable = this.staleManifestReloadAttempt;
                this.staleManifestReloadAttempt = parsingLoadable + 1;
                if (parsingLoadable >= this.minLoadableRetryCount) {
                    this.manifestFatalError = new DashManifestStaleException();
                } else {
                    scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                }
                return;
            }
        }
        this.manifest = dashManifest;
        this.manifestLoadPending &= this.manifest.dynamic;
        this.manifestLoadStartTimestampMs = j - j2;
        this.manifestLoadEndTimestampMs = j;
        if (this.manifest.location != null) {
            synchronized (this.manifestUriLock) {
                if (parsingLoadable.dataSpec.uri == this.manifestUri) {
                    this.manifestUri = this.manifest.location;
                }
            }
        }
        if (periodCount != 0) {
            this.firstPeriodId += i;
            processManifest(true);
        } else if (this.manifest.utcTiming != null) {
            resolveUtcTimingElement(this.manifest.utcTiming);
        } else {
            processManifest(true);
        }
    }

    int onManifestLoadError(ParsingLoadable<DashManifest> parsingLoadable, long j, long j2, IOException iOException) {
        ParsingLoadable<DashManifest> parsingLoadable2 = parsingLoadable;
        IOException iOException2 = iOException;
        boolean z = iOException2 instanceof ParserException;
        this.eventDispatcher.loadError(parsingLoadable2.dataSpec, parsingLoadable2.type, j, j2, parsingLoadable2.bytesLoaded(), iOException2, z);
        return z ? 3 : 0;
    }

    void onUtcTimestampLoadCompleted(ParsingLoadable<Long> parsingLoadable, long j, long j2) {
        this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, parsingLoadable.type, j, j2, parsingLoadable.bytesLoaded());
        onUtcTimestampResolved(((Long) parsingLoadable.getResult()).longValue() - j);
    }

    int onUtcTimestampLoadError(ParsingLoadable<Long> parsingLoadable, long j, long j2, IOException iOException) {
        ParsingLoadable<Long> parsingLoadable2 = parsingLoadable;
        this.eventDispatcher.loadError(parsingLoadable2.dataSpec, parsingLoadable2.type, j, j2, parsingLoadable2.bytesLoaded(), iOException, true);
        onUtcTimestampResolutionError(iOException);
        return 2;
    }

    void onLoadCanceled(ParsingLoadable<?> parsingLoadable, long j, long j2) {
        this.eventDispatcher.loadCanceled(parsingLoadable.dataSpec, parsingLoadable.type, j, j2, parsingLoadable.bytesLoaded());
    }

    private void resolveUtcTimingElement(UtcTimingElement utcTimingElement) {
        String str = utcTimingElement.schemeIdUri;
        if (!Util.areEqual(str, "urn:mpeg:dash:utc:direct:2014")) {
            if (!Util.areEqual(str, "urn:mpeg:dash:utc:direct:2012")) {
                if (!Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2014")) {
                    if (!Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2012")) {
                        if (!Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2014")) {
                            if (!Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2012")) {
                                onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
                                return;
                            }
                        }
                        resolveUtcTimingElementHttp(utcTimingElement, new XsDateTimeParser());
                        return;
                    }
                }
                resolveUtcTimingElementHttp(utcTimingElement, new Iso8601Parser());
                return;
            }
        }
        resolveUtcTimingElementDirect(utcTimingElement);
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement utcTimingElement) {
        try {
            onUtcTimestampResolved(Util.parseXsDateTime(utcTimingElement.value) - this.manifestLoadEndTimestampMs);
        } catch (UtcTimingElement utcTimingElement2) {
            onUtcTimestampResolutionError(utcTimingElement2);
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement utcTimingElement, Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(utcTimingElement.value), 5, (Parser) parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long j) {
        this.elapsedRealtimeOffsetMs = j;
        processManifest(1);
    }

    private void onUtcTimestampResolutionError(IOException iOException) {
        Log.e(TAG, "Failed to resolve UtcTiming element.", iOException);
        processManifest(true);
    }

    private void processManifest(boolean z) {
        int i;
        long min;
        DashMediaSource dashMediaSource = this;
        for (i = 0; i < dashMediaSource.periodsById.size(); i++) {
            int keyAt = dashMediaSource.periodsById.keyAt(i);
            if (keyAt >= dashMediaSource.firstPeriodId) {
                ((DashMediaPeriod) dashMediaSource.periodsById.valueAt(i)).updateManifest(dashMediaSource.manifest, keyAt - dashMediaSource.firstPeriodId);
            }
        }
        i = dashMediaSource.manifest.getPeriodCount() - 1;
        PeriodSeekInfo createPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(0), dashMediaSource.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo createPeriodSeekInfo2 = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(i), dashMediaSource.manifest.getPeriodDurationUs(i));
        long j = createPeriodSeekInfo.availableStartTimeUs;
        long j2 = createPeriodSeekInfo2.availableEndTimeUs;
        if (!dashMediaSource.manifest.dynamic || createPeriodSeekInfo2.isIndexExplicit) {
            i = 0;
        } else {
            j2 = Math.min((getNowUnixTimeUs() - C0542C.msToUs(dashMediaSource.manifest.availabilityStartTimeMs)) - C0542C.msToUs(dashMediaSource.manifest.getPeriod(i).startMs), j2);
            if (dashMediaSource.manifest.timeShiftBufferDepthMs != C0542C.TIME_UNSET) {
                long max;
                long msToUs = j2 - C0542C.msToUs(dashMediaSource.manifest.timeShiftBufferDepthMs);
                while (msToUs < 0 && i > 0) {
                    i--;
                    msToUs += dashMediaSource.manifest.getPeriodDurationUs(i);
                }
                if (i == 0) {
                    max = Math.max(j, msToUs);
                } else {
                    max = dashMediaSource.manifest.getPeriodDurationUs(0);
                }
                j = max;
            }
            i = 1;
        }
        long j3 = j2 - j;
        int i2 = 0;
        while (i2 < dashMediaSource.manifest.getPeriodCount() - 1) {
            i2++;
            j3 += dashMediaSource.manifest.getPeriodDurationUs(i2);
        }
        if (dashMediaSource.manifest.dynamic) {
            long j4 = dashMediaSource.livePresentationDelayMs;
            if (j4 == -1) {
                j4 = dashMediaSource.manifest.suggestedPresentationDelayMs != C0542C.TIME_UNSET ? dashMediaSource.manifest.suggestedPresentationDelayMs : 30000;
            }
            j2 = j3 - C0542C.msToUs(j4);
            min = j2 < MIN_LIVE_DEFAULT_START_POSITION_US ? Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, j3 / 2) : j2;
        } else {
            min = 0;
        }
        dashMediaSource.sourceListener.onSourceInfoRefreshed(dashMediaSource, new DashTimeline(dashMediaSource.manifest.availabilityStartTimeMs, (dashMediaSource.manifest.availabilityStartTimeMs + dashMediaSource.manifest.getPeriod(0).startMs) + C0542C.usToMs(j), dashMediaSource.firstPeriodId, j, j3, min, dashMediaSource.manifest), dashMediaSource.manifest);
        if (!dashMediaSource.sideloadedManifest) {
            dashMediaSource.handler.removeCallbacks(dashMediaSource.simulateManifestRefreshRunnable);
            if (i != 0) {
                dashMediaSource.handler.postDelayed(dashMediaSource.simulateManifestRefreshRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
            if (dashMediaSource.manifestLoadPending) {
                startLoadingManifest();
            } else if (z && dashMediaSource.manifest.dynamic) {
                long j5 = dashMediaSource.manifest.minUpdatePeriodMs;
                if (j5 == 0) {
                    j5 = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                }
                scheduleManifestRefresh(Math.max(0, (dashMediaSource.manifestLoadStartTimestampMs + j5) - SystemClock.elapsedRealtime()));
            }
        }
    }

    private void scheduleManifestRefresh(long j) {
        this.handler.postDelayed(this.refreshManifestRunnable, j);
    }

    private void startLoadingManifest() {
        this.handler.removeCallbacks(this.refreshManifestRunnable);
        if (this.loader.isLoading()) {
            this.manifestLoadPending = true;
            return;
        }
        Uri uri;
        synchronized (this.manifestUriLock) {
            uri = this.manifestUri;
        }
        this.manifestLoadPending = false;
        startLoading(new ParsingLoadable(this.dataSource, uri, 4, this.manifestParser), this.manifestCallback, this.minLoadableRetryCount);
    }

    private long getManifestLoadRetryDelayMillis() {
        return (long) Math.min((this.staleManifestReloadAttempt - 1) * 1000, 5000);
    }

    private <T> void startLoading(ParsingLoadable<T> parsingLoadable, Callback<ParsingLoadable<T>> callback, int i) {
        this.eventDispatcher.loadStarted(parsingLoadable.dataSpec, parsingLoadable.type, this.loader.startLoading(parsingLoadable, callback, i));
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C0542C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C0542C.msToUs(System.currentTimeMillis());
    }
}
