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
import java.util.regex.Matcher;
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

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long durationUs) {
            Period period2 = period;
            long j = durationUs;
            int adaptationSetCount = period2.adaptationSets.size();
            long availableStartTimeUs = 0;
            long availableEndTimeUs = Long.MAX_VALUE;
            boolean isIndexExplicit = false;
            boolean seenEmptyIndex = false;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < adaptationSetCount) {
                    DashSegmentIndex index = ((Representation) ((AdaptationSet) period2.adaptationSets.get(i2)).representations.get(0)).getIndex();
                    if (index == null) {
                        return new PeriodSeekInfo(true, 0, j);
                    }
                    DashSegmentIndex index2 = index;
                    isIndexExplicit |= index2.isExplicit();
                    i = index2.getSegmentCount(j);
                    if (i == 0) {
                        seenEmptyIndex = true;
                        availableStartTimeUs = 0;
                        availableEndTimeUs = 0;
                    } else if (!seenEmptyIndex) {
                        int firstSegmentNum = index2.getFirstSegmentNum();
                        long availableStartTimeUs2 = Math.max(availableStartTimeUs, index2.getTimeUs(firstSegmentNum));
                        if (i != -1) {
                            int lastSegmentNum = (firstSegmentNum + i) - 1;
                            availableEndTimeUs = Math.min(availableEndTimeUs, index2.getTimeUs(lastSegmentNum) + index2.getDurationUs(lastSegmentNum, j));
                        }
                        availableStartTimeUs = availableStartTimeUs2;
                    }
                    i = i2 + 1;
                    period2 = period;
                } else {
                    long availableStartTimeUs3 = availableStartTimeUs;
                    return new PeriodSeekInfo(isIndexExplicit, availableStartTimeUs, availableEndTimeUs);
                }
            }
        }

        private PeriodSeekInfo(boolean isIndexExplicit, long availableStartTimeUs, long availableEndTimeUs) {
            this.isIndexExplicit = isIndexExplicit;
            this.availableStartTimeUs = availableStartTimeUs;
            this.availableEndTimeUs = availableEndTimeUs;
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

        public DashTimeline(long presentationStartTimeMs, long windowStartTimeMs, int firstPeriodId, long offsetInFirstPeriodUs, long windowDurationUs, long windowDefaultStartPositionUs, DashManifest manifest) {
            this.presentationStartTimeMs = presentationStartTimeMs;
            this.windowStartTimeMs = windowStartTimeMs;
            this.firstPeriodId = firstPeriodId;
            this.offsetInFirstPeriodUs = offsetInFirstPeriodUs;
            this.windowDurationUs = windowDurationUs;
            this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
            this.manifest = manifest;
        }

        public int getPeriodCount() {
            return this.manifest.getPeriodCount();
        }

        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIdentifiers) {
            Assertions.checkIndex(periodIndex, 0, this.manifest.getPeriodCount());
            Integer num = null;
            Object id = setIdentifiers ? this.manifest.getPeriod(periodIndex).id : null;
            if (setIdentifiers) {
                num = Integer.valueOf(this.firstPeriodId + Assertions.checkIndex(periodIndex, 0, this.manifest.getPeriodCount()));
            }
            return period.set(id, num, 0, this.manifest.getPeriodDurationUs(periodIndex), C0542C.msToUs(this.manifest.getPeriod(periodIndex).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        public int getWindowCount() {
            return 1;
        }

        public Window getWindow(int windowIndex, Window window, boolean setIdentifier, long defaultPositionProjectionUs) {
            Assertions.checkIndex(windowIndex, 0, 1);
            long windowDefaultStartPositionUs = getAdjustedWindowDefaultStartPositionUs(defaultPositionProjectionUs);
            return window.set(null, this.presentationStartTimeMs, this.windowStartTimeMs, true, this.manifest.dynamic, windowDefaultStartPositionUs, this.windowDurationUs, 0, this.manifest.getPeriodCount() - 1, this.offsetInFirstPeriodUs);
        }

        public int getIndexOfPeriod(Object uid) {
            int i = -1;
            if (!(uid instanceof Integer)) {
                return -1;
            }
            int periodId = ((Integer) uid).intValue();
            if (periodId >= this.firstPeriodId) {
                if (periodId < this.firstPeriodId + getPeriodCount()) {
                    i = periodId - this.firstPeriodId;
                }
            }
            return i;
        }

        private long getAdjustedWindowDefaultStartPositionUs(long defaultPositionProjectionUs) {
            long windowDefaultStartPositionUs = this.windowDefaultStartPositionUs;
            if (!this.manifest.dynamic) {
                return windowDefaultStartPositionUs;
            }
            if (defaultPositionProjectionUs > 0) {
                long windowDefaultStartPositionUs2 = windowDefaultStartPositionUs + defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs2 > r0.windowDurationUs) {
                    return C0542C.TIME_UNSET;
                }
                windowDefaultStartPositionUs = windowDefaultStartPositionUs2;
            }
            int periodIndex = 0;
            long defaultStartPositionInPeriodUs = r0.offsetInFirstPeriodUs + windowDefaultStartPositionUs;
            long periodDurationUs = r0.manifest.getPeriodDurationUs(0);
            while (periodIndex < r0.manifest.getPeriodCount() - 1 && defaultStartPositionInPeriodUs >= periodDurationUs) {
                long defaultStartPositionInPeriodUs2 = defaultStartPositionInPeriodUs - periodDurationUs;
                periodIndex++;
                periodDurationUs = r0.manifest.getPeriodDurationUs(periodIndex);
                defaultStartPositionInPeriodUs = defaultStartPositionInPeriodUs2;
            }
            Period period = r0.manifest.getPeriod(periodIndex);
            int videoAdaptationSetIndex = period.getAdaptationSetIndex(2);
            if (videoAdaptationSetIndex == -1) {
                return windowDefaultStartPositionUs;
            }
            DashSegmentIndex snapIndex = ((Representation) ((AdaptationSet) period.adaptationSets.get(videoAdaptationSetIndex)).representations.get(0)).getIndex();
            if (snapIndex != null) {
                if (snapIndex.getSegmentCount(periodDurationUs) != 0) {
                    return (windowDefaultStartPositionUs + snapIndex.getTimeUs(snapIndex.getSegmentNum(defaultStartPositionInPeriodUs, periodDurationUs))) - defaultStartPositionInPeriodUs;
                }
            }
            return windowDefaultStartPositionUs;
        }
    }

    private final class DefaultPlayerEmsgCallback implements PlayerEmsgCallback {
        private DefaultPlayerEmsgCallback() {
        }

        public void onDashManifestRefreshRequested() {
            DashMediaSource.this.onDashManifestRefreshRequested();
        }

        public void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
            DashMediaSource.this.onDashManifestPublishTimeExpired(expiredManifestPublishTimeUs);
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

        public Factory(org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory) {
            this.chunkSourceFactory = (org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory);
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

        public Factory setManifestParser(Parser<? extends DashManifest> manifestParser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public DashMediaSource createMediaSource(DashManifest manifest, Handler eventHandler, MediaSourceEventListener eventListener) {
            DashManifest dashManifest = manifest;
            Assertions.checkArgument(dashManifest.dynamic ^ true);
            this.isCreateCalled = true;
            return new DashMediaSource(dashManifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, eventHandler, eventListener);
        }

        public DashMediaSource createMediaSource(Uri manifestUri) {
            return createMediaSource(manifestUri, null, null);
        }

        public DashMediaSource createMediaSource(Uri manifestUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                r0.manifestParser = new DashManifestParser();
            }
            return new DashMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), r0.manifestDataSourceFactory, r0.manifestParser, r0.chunkSourceFactory, r0.compositeSequenceableLoaderFactory, r0.minLoadableRetryCount, r0.livePresentationDelayMs, eventHandler, eventListener);
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
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                Matcher matcher = TIMESTAMP_WITH_TIMEZONE_PATTERN.matcher(firstLine);
                if (matcher.matches()) {
                    String timestampWithoutTimezone = matcher.group(1);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    long timestampMs = format.parse(timestampWithoutTimezone).getTime();
                    if (!"Z".equals(matcher.group(2))) {
                        long sign = "+".equals(matcher.group(4)) ? 1 : -1;
                        long hours = Long.parseLong(matcher.group(5));
                        String minutesString = matcher.group(7);
                        timestampMs -= ((((hours * 60) + (TextUtils.isEmpty(minutesString) ? 0 : Long.parseLong(minutesString))) * 60) * 1000) * sign;
                    }
                    return Long.valueOf(timestampMs);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't parse timestamp: ");
                stringBuilder.append(firstLine);
                throw new ParserException(stringBuilder.toString());
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private final class ManifestCallback implements Callback<ParsingLoadable<DashManifest>> {
        private ManifestCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onManifestLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public int onLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
            return DashMediaSource.this.onManifestLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    final class ManifestLoadErrorThrower implements LoaderErrorThrower {
        ManifestLoadErrorThrower() {
        }

        public void maybeThrowError() throws IOException {
            DashMediaSource.this.loader.maybeThrowError();
            maybeThrowManifestError();
        }

        public void maybeThrowError(int minRetryCount) throws IOException {
            DashMediaSource.this.loader.maybeThrowError(minRetryCount);
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

        public void onLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onUtcTimestampLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public int onLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
            return DashMediaSource.this.onUtcTimestampLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
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
    public DashMediaSource(DashManifest manifest, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, chunkSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, null, null, null, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), minLoadableRetryCount, -1, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, -1, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, new DashManifestParser(), chunkSourceFactory, minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends DashManifest> manifestParser, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(null, manifestUri, manifestDataSourceFactory, manifestParser, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    private DashMediaSource(DashManifest manifest, Uri manifestUri, org.telegram.messenger.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends DashManifest> manifestParser, org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        DashManifest dashManifest = manifest;
        this.manifest = dashManifest;
        this.manifestUri = manifestUri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.manifestParser = manifestParser;
        this.chunkSourceFactory = chunkSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        r0.sideloadedManifest = dashManifest != null;
        r0.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        r0.manifestUriLock = new Object();
        r0.periodsById = new SparseArray();
        r0.playerEmsgCallback = new DefaultPlayerEmsgCallback();
        r0.expiredManifestPublishTimeUs = C0542C.TIME_UNSET;
        if (r0.sideloadedManifest) {
            Assertions.checkState(true ^ dashManifest.dynamic);
            r0.manifestCallback = null;
            r0.refreshManifestRunnable = null;
            r0.simulateManifestRefreshRunnable = null;
            return;
        }
        r0.manifestCallback = new ManifestCallback();
        r0.refreshManifestRunnable = new C06101();
        r0.simulateManifestRefreshRunnable = new C06112();
    }

    public void replaceManifestUri(Uri manifestUri) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = manifestUri;
        }
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        Assertions.checkState(this.sourceListener == null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.sourceListener = listener;
        if (this.sideloadedManifest) {
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

    public MediaPeriod createPeriod(MediaPeriodId periodId, Allocator allocator) {
        int periodIndex = periodId.periodIndex;
        DashMediaPeriod mediaPeriod = new DashMediaPeriod(this.firstPeriodId + periodIndex, this.manifest, periodIndex, this.chunkSourceFactory, this.minLoadableRetryCount, this.eventDispatcher.copyWithMediaTimeOffsetMs(this.manifest.getPeriod(periodIndex).startMs), this.elapsedRealtimeOffsetMs, this.manifestLoadErrorThrower, allocator, this.compositeSequenceableLoaderFactory, this.playerEmsgCallback);
        this.periodsById.put(mediaPeriod.id, mediaPeriod);
        return mediaPeriod;
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

    void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
        if (this.expiredManifestPublishTimeUs == C0542C.TIME_UNSET || this.expiredManifestPublishTimeUs < expiredManifestPublishTimeUs) {
            this.expiredManifestPublishTimeUs = expiredManifestPublishTimeUs;
        }
    }

    void onManifestLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        long j = elapsedRealtimeMs;
        this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, parsingLoadable.type, j, loadDurationMs, loadable.bytesLoaded());
        DashManifest newManifest = (DashManifest) loadable.getResult();
        int periodCount = this.manifest == null ? 0 : r1.manifest.getPeriodCount();
        int removedPeriodCount = 0;
        long newFirstPeriodStartTimeMs = newManifest.getPeriod(0).startMs;
        while (removedPeriodCount < periodCount && r1.manifest.getPeriod(removedPeriodCount).startMs < newFirstPeriodStartTimeMs) {
            removedPeriodCount++;
        }
        if (newManifest.dynamic) {
            boolean isManifestStale = false;
            if (periodCount - removedPeriodCount > newManifest.getPeriodCount()) {
                Log.w(TAG, "Loaded out of sync manifest");
                isManifestStale = true;
            } else if (r1.dynamicMediaPresentationEnded || newManifest.publishTimeMs <= r1.expiredManifestPublishTimeUs) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Loaded stale dynamic manifest: ");
                stringBuilder.append(newManifest.publishTimeMs);
                stringBuilder.append(", ");
                stringBuilder.append(r1.dynamicMediaPresentationEnded);
                stringBuilder.append(", ");
                stringBuilder.append(r1.expiredManifestPublishTimeUs);
                Log.w(str, stringBuilder.toString());
                isManifestStale = true;
            }
            if (isManifestStale) {
                int i = r1.staleManifestReloadAttempt;
                r1.staleManifestReloadAttempt = i + 1;
                if (i < r1.minLoadableRetryCount) {
                    scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                } else {
                    r1.manifestFatalError = new DashManifestStaleException();
                }
                return;
            }
            r1.staleManifestReloadAttempt = 0;
        }
        r1.manifest = newManifest;
        r1.manifestLoadPending &= r1.manifest.dynamic;
        r1.manifestLoadStartTimestampMs = j - loadDurationMs;
        r1.manifestLoadEndTimestampMs = j;
        if (r1.manifest.location != null) {
            synchronized (r1.manifestUriLock) {
                try {
                    if (parsingLoadable.dataSpec.uri == r1.manifestUri) {
                        r1.manifestUri = r1.manifest.location;
                    }
                } catch (Throwable th) {
                    Throwable th2 = th;
                }
            }
        }
        if (periodCount != 0) {
            r1.firstPeriodId += removedPeriodCount;
            processManifest(true);
        } else if (r1.manifest.utcTiming != null) {
            resolveUtcTimingElement(r1.manifest.utcTiming);
        } else {
            processManifest(true);
        }
    }

    int onManifestLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        IOException iOException = error;
        boolean isFatal = iOException instanceof ParserException;
        this.eventDispatcher.loadError(parsingLoadable.dataSpec, parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), iOException, isFatal);
        return isFatal ? 3 : 0;
    }

    void onUtcTimestampLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        onUtcTimestampResolved(((Long) loadable.getResult()).longValue() - elapsedRealtimeMs);
    }

    int onUtcTimestampLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<Long> parsingLoadable = loadable;
        this.eventDispatcher.loadError(parsingLoadable.dataSpec, parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, parsingLoadable.bytesLoaded(), error, true);
        onUtcTimestampResolutionError(error);
        return 2;
    }

    void onLoadCanceled(ParsingLoadable<?> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    private void resolveUtcTimingElement(UtcTimingElement timingElement) {
        String scheme = timingElement.schemeIdUri;
        if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2014")) {
            if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
                if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014")) {
                    if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2012")) {
                        if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014")) {
                            if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012")) {
                                onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
                                return;
                            }
                        }
                        resolveUtcTimingElementHttp(timingElement, new XsDateTimeParser());
                        return;
                    }
                }
                resolveUtcTimingElementHttp(timingElement, new Iso8601Parser());
                return;
            }
        }
        resolveUtcTimingElementDirect(timingElement);
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement timingElement) {
        try {
            onUtcTimestampResolved(Util.parseXsDateTime(timingElement.value) - this.manifestLoadEndTimestampMs);
        } catch (ParserException e) {
            onUtcTimestampResolutionError(e);
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement timingElement, Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(timingElement.value), 5, (Parser) parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long elapsedRealtimeOffsetMs) {
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        processManifest(true);
    }

    private void onUtcTimestampResolutionError(IOException error) {
        Log.e(TAG, "Failed to resolve UtcTiming element.", error);
        processManifest(true);
    }

    private void processManifest(boolean scheduleRefresh) {
        int id;
        boolean windowChangingImplicitly;
        long timeShiftBufferDepthUs;
        long presentationDelayForManifestMs;
        DashTimeline dashTimeline;
        DashMediaSource dashMediaSource = this;
        for (int i = 0; i < dashMediaSource.periodsById.size(); i++) {
            id = dashMediaSource.periodsById.keyAt(i);
            if (id >= dashMediaSource.firstPeriodId) {
                ((DashMediaPeriod) dashMediaSource.periodsById.valueAt(i)).updateManifest(dashMediaSource.manifest, id - dashMediaSource.firstPeriodId);
            }
        }
        id = dashMediaSource.manifest.getPeriodCount() - 1;
        PeriodSeekInfo firstPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(0), dashMediaSource.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo lastPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(id), dashMediaSource.manifest.getPeriodDurationUs(id));
        long currentStartTimeUs = firstPeriodSeekInfo.availableStartTimeUs;
        long currentEndTimeUs = lastPeriodSeekInfo.availableEndTimeUs;
        if (!dashMediaSource.manifest.dynamic || lastPeriodSeekInfo.isIndexExplicit) {
            windowChangingImplicitly = false;
        } else {
            windowChangingImplicitly = false;
            currentEndTimeUs = Math.min((getNowUnixTimeUs() - C0542C.msToUs(dashMediaSource.manifest.availabilityStartTimeMs)) - C0542C.msToUs(dashMediaSource.manifest.getPeriod(id).startMs), currentEndTimeUs);
            if (dashMediaSource.manifest.timeShiftBufferDepthMs != C0542C.TIME_UNSET) {
                timeShiftBufferDepthUs = C0542C.msToUs(dashMediaSource.manifest.timeShiftBufferDepthMs);
                int periodIndex = id;
                long offsetInPeriodUs = currentEndTimeUs - timeShiftBufferDepthUs;
                while (offsetInPeriodUs < 0 && periodIndex > 0) {
                    periodIndex--;
                    offsetInPeriodUs += dashMediaSource.manifest.getPeriodDurationUs(periodIndex);
                }
                if (periodIndex == 0) {
                    currentStartTimeUs = Math.max(currentStartTimeUs, offsetInPeriodUs);
                } else {
                    long j = timeShiftBufferDepthUs;
                    currentStartTimeUs = dashMediaSource.manifest.getPeriodDurationUs(0);
                }
            }
            windowChangingImplicitly = true;
        }
        long windowDurationUs = currentEndTimeUs - currentStartTimeUs;
        int i2 = 0;
        while (i2 < dashMediaSource.manifest.getPeriodCount() - 1) {
            i2++;
            windowDurationUs += dashMediaSource.manifest.getPeriodDurationUs(i2);
        }
        timeShiftBufferDepthUs = 0;
        if (dashMediaSource.manifest.dynamic) {
            presentationDelayForManifestMs = dashMediaSource.livePresentationDelayMs;
            if (presentationDelayForManifestMs == -1) {
                presentationDelayForManifestMs = dashMediaSource.manifest.suggestedPresentationDelayMs != C0542C.TIME_UNSET ? dashMediaSource.manifest.suggestedPresentationDelayMs : 30000;
            }
            timeShiftBufferDepthUs = windowDurationUs - C0542C.msToUs(presentationDelayForManifestMs);
            if (timeShiftBufferDepthUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                timeShiftBufferDepthUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, windowDurationUs / 2);
            } else {
                long j2 = timeShiftBufferDepthUs;
            }
        }
        DashTimeline timeline = new DashTimeline(dashMediaSource.manifest.availabilityStartTimeMs, (dashMediaSource.manifest.availabilityStartTimeMs + dashMediaSource.manifest.getPeriod(0).startMs) + C0542C.usToMs(currentStartTimeUs), dashMediaSource.firstPeriodId, currentStartTimeUs, windowDurationUs, timeShiftBufferDepthUs, dashMediaSource.manifest);
        dashMediaSource.sourceListener.onSourceInfoRefreshed(dashMediaSource, timeline, dashMediaSource.manifest);
        if (!dashMediaSource.sideloadedManifest) {
            dashMediaSource.handler.removeCallbacks(dashMediaSource.simulateManifestRefreshRunnable);
            if (windowChangingImplicitly) {
                dashMediaSource.handler.postDelayed(dashMediaSource.simulateManifestRefreshRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
            if (dashMediaSource.manifestLoadPending) {
                startLoadingManifest();
                long j3 = timeShiftBufferDepthUs;
                dashTimeline = timeline;
                return;
            } else if (scheduleRefresh && dashMediaSource.manifest.dynamic) {
                presentationDelayForManifestMs = dashMediaSource.manifest.minUpdatePeriodMs;
                if (presentationDelayForManifestMs == 0) {
                    presentationDelayForManifestMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                }
                scheduleManifestRefresh(Math.max(0, (dashMediaSource.manifestLoadStartTimestampMs + presentationDelayForManifestMs) - SystemClock.elapsedRealtime()));
                return;
            }
        }
        dashTimeline = timeline;
    }

    private void scheduleManifestRefresh(long delayUntilNextLoadMs) {
        this.handler.postDelayed(this.refreshManifestRunnable, delayUntilNextLoadMs);
    }

    private void startLoadingManifest() {
        this.handler.removeCallbacks(this.refreshManifestRunnable);
        if (this.loader.isLoading()) {
            this.manifestLoadPending = true;
            return;
        }
        Uri manifestUri;
        synchronized (this.manifestUriLock) {
            manifestUri = this.manifestUri;
        }
        this.manifestLoadPending = false;
        startLoading(new ParsingLoadable(this.dataSource, manifestUri, 4, this.manifestParser), this.manifestCallback, this.minLoadableRetryCount);
    }

    private long getManifestLoadRetryDelayMillis() {
        return (long) Math.min((this.staleManifestReloadAttempt - 1) * 1000, 5000);
    }

    private <T> void startLoading(ParsingLoadable<T> loadable, Callback<ParsingLoadable<T>> callback, int minRetryCount) {
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.loader.startLoading(loadable, callback, minRetryCount));
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C0542C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C0542C.msToUs(System.currentTimeMillis());
    }
}
