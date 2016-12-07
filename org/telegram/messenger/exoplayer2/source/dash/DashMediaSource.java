package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory;
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
    private final Factory chunkSourceFactory;
    private DataSource dataSource;
    private long elapsedRealtimeOffsetMs;
    private final EventDispatcher eventDispatcher;
    private int firstPeriodId;
    private Handler handler;
    private final long livePresentationDelayMs;
    private Loader loader;
    private DashManifest manifest;
    private final ManifestCallback manifestCallback;
    private final DataSource.Factory manifestDataSourceFactory;
    private long manifestLoadEndTimestamp;
    private long manifestLoadStartTimestamp;
    private final DashManifestParser manifestParser;
    private Uri manifestUri;
    private final Object manifestUriLock;
    private final int minLoadableRetryCount;
    private final SparseArray<DashMediaPeriod> periodsById;
    private final Runnable refreshManifestRunnable;
    private final Runnable simulateManifestRefreshRunnable;
    private Listener sourceListener;

    private static final class PeriodSeekInfo {
        public final long availableEndTimeUs;
        public final long availableStartTimeUs;
        public final boolean isIndexExplicit;

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long durationUs) {
            int adaptationSetCount = period.adaptationSets.size();
            long availableStartTimeUs = 0;
            long availableEndTimeUs = Long.MAX_VALUE;
            boolean isIndexExplicit = false;
            for (int i = 0; i < adaptationSetCount; i++) {
                DashSegmentIndex index = ((Representation) ((AdaptationSet) period.adaptationSets.get(i)).representations.get(0)).getIndex();
                if (index == null) {
                    return new PeriodSeekInfo(true, 0, durationUs);
                }
                int firstSegmentNum = index.getFirstSegmentNum();
                int lastSegmentNum = index.getLastSegmentNum(durationUs);
                isIndexExplicit |= index.isExplicit();
                availableStartTimeUs = Math.max(availableStartTimeUs, index.getTimeUs(firstSegmentNum));
                if (lastSegmentNum != -1) {
                    availableEndTimeUs = Math.min(availableEndTimeUs, index.getTimeUs(lastSegmentNum) + index.getDurationUs(lastSegmentNum, durationUs));
                }
            }
            return new PeriodSeekInfo(isIndexExplicit, availableStartTimeUs, availableEndTimeUs);
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
            String id;
            Integer uid = null;
            Assertions.checkIndex(periodIndex, 0, this.manifest.getPeriodCount());
            if (setIdentifiers) {
                id = this.manifest.getPeriod(periodIndex).id;
            } else {
                id = null;
            }
            if (setIdentifiers) {
                uid = Integer.valueOf(this.firstPeriodId + Assertions.checkIndex(periodIndex, 0, this.manifest.getPeriodCount()));
            }
            return period.set(id, uid, 0, this.manifest.getPeriodDurationUs(periodIndex), C.msToUs(this.manifest.getPeriod(periodIndex).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        public int getWindowCount() {
            return 1;
        }

        public Window getWindow(int windowIndex, Window window, boolean setIdentifier) {
            Assertions.checkIndex(windowIndex, 0, 1);
            return window.set(null, this.presentationStartTimeMs, this.windowStartTimeMs, true, this.manifest.dynamic, this.windowDefaultStartPositionUs, this.windowDurationUs, 0, this.manifest.getPeriodCount() - 1, this.offsetInFirstPeriodUs);
        }

        public int getIndexOfPeriod(Object uid) {
            if (!(uid instanceof Integer)) {
                return -1;
            }
            int periodId = ((Integer) uid).intValue();
            if (periodId < this.firstPeriodId || periodId >= this.firstPeriodId + getPeriodCount()) {
                return -1;
            }
            return periodId - this.firstPeriodId;
        }
    }

    private static final class Iso8601Parser implements Parser<Long> {
        private Iso8601Parser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                return Long.valueOf(format.parse(firstLine).getTime());
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
            try {
                return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    public DashMediaSource(Uri manifestUri, DataSource.Factory manifestDataSourceFactory, Factory chunkSourceFactory, Handler eventHandler, AdaptiveMediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, -1, eventHandler, eventListener);
    }

    public DashMediaSource(Uri manifestUri, DataSource.Factory manifestDataSourceFactory, Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, AdaptiveMediaSourceEventListener eventListener) {
        this.manifestUri = manifestUri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.chunkSourceFactory = chunkSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.manifestParser = new DashManifestParser(generateContentId());
        this.manifestCallback = new ManifestCallback();
        this.manifestUriLock = new Object();
        this.periodsById = new SparseArray();
        this.refreshManifestRunnable = new Runnable() {
            public void run() {
                DashMediaSource.this.startLoadingManifest();
            }
        };
        this.simulateManifestRefreshRunnable = new Runnable() {
            public void run() {
                DashMediaSource.this.processManifest();
            }
        };
    }

    public void replaceManifestUri(Uri manifestUri) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = manifestUri;
        }
    }

    public void prepareSource(Listener listener) {
        this.sourceListener = listener;
        this.dataSource = this.manifestDataSourceFactory.createDataSource();
        this.loader = new Loader("Loader:DashMediaSource");
        this.handler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.loader.maybeThrowError();
    }

    public MediaPeriod createPeriod(int index, Allocator allocator, long positionUs) {
        DashMediaPeriod mediaPeriod = new DashMediaPeriod(this.firstPeriodId + index, this.manifest, index, this.chunkSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, this.elapsedRealtimeOffsetMs, this.loader, allocator);
        this.periodsById.put(mediaPeriod.id, mediaPeriod);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DashMediaPeriod dashMediaPeriod = (DashMediaPeriod) mediaPeriod;
        dashMediaPeriod.release();
        this.periodsById.remove(dashMediaPeriod.id);
    }

    public void releaseSource() {
        this.dataSource = null;
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
        this.manifestLoadStartTimestamp = 0;
        this.manifestLoadEndTimestamp = 0;
        this.manifest = null;
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages(null);
            this.handler = null;
        }
        this.elapsedRealtimeOffsetMs = 0;
        this.periodsById.clear();
    }

    void onManifestLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        int periodCount;
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        DashManifest newManifest = (DashManifest) loadable.getResult();
        if (this.manifest == null) {
            periodCount = 0;
        } else {
            periodCount = this.manifest.getPeriodCount();
        }
        int removedPeriodCount = 0;
        long newFirstPeriodStartTimeMs = newManifest.getPeriod(0).startMs;
        while (removedPeriodCount < periodCount && this.manifest.getPeriod(removedPeriodCount).startMs < newFirstPeriodStartTimeMs) {
            removedPeriodCount++;
        }
        if (periodCount - removedPeriodCount > newManifest.getPeriodCount()) {
            Log.w(TAG, "Out of sync manifest");
            scheduleManifestRefresh();
            return;
        }
        this.manifest = newManifest;
        this.manifestLoadStartTimestamp = elapsedRealtimeMs - loadDurationMs;
        this.manifestLoadEndTimestamp = elapsedRealtimeMs;
        if (this.manifest.location != null) {
            synchronized (this.manifestUriLock) {
                if (loadable.dataSpec.uri == this.manifestUri) {
                    this.manifestUri = this.manifest.location;
                }
            }
        }
        if (periodCount != 0) {
            this.firstPeriodId += removedPeriodCount;
            processManifestAndScheduleRefresh();
        } else if (this.manifest.utcTiming != null) {
            resolveUtcTimingElement(this.manifest.utcTiming);
        } else {
            processManifestAndScheduleRefresh();
        }
    }

    int onManifestLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean isFatal = error instanceof ParserException;
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? 3 : 0;
    }

    void onUtcTimestampLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        onUtcTimestampResolved(((Long) loadable.getResult()).longValue() - elapsedRealtimeMs);
    }

    int onUtcTimestampLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, true);
        onUtcTimestampResolutionError(error);
        return 2;
    }

    void onLoadCanceled(ParsingLoadable<?> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    private void startLoadingManifest() {
        Uri manifestUri;
        synchronized (this.manifestUriLock) {
            manifestUri = this.manifestUri;
        }
        startLoading(new ParsingLoadable(this.dataSource, manifestUri, 4, this.manifestParser), this.manifestCallback, this.minLoadableRetryCount);
    }

    private void resolveUtcTimingElement(UtcTimingElement timingElement) {
        String scheme = timingElement.schemeIdUri;
        if (Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
            resolveUtcTimingElementDirect(timingElement);
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014")) {
            resolveUtcTimingElementHttp(timingElement, new Iso8601Parser());
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014")) {
            resolveUtcTimingElementHttp(timingElement, new XsDateTimeParser());
        } else {
            onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
        }
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement timingElement) {
        try {
            onUtcTimestampResolved(Util.parseXsDateTime(timingElement.value) - this.manifestLoadEndTimestamp);
        } catch (Throwable e) {
            onUtcTimestampResolutionError(new ParserException(e));
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement timingElement, Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(timingElement.value), 5, parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long elapsedRealtimeOffsetMs) {
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        processManifestAndScheduleRefresh();
    }

    private void onUtcTimestampResolutionError(IOException error) {
        Log.e(TAG, "Failed to resolve UtcTiming element.", error);
        processManifestAndScheduleRefresh();
    }

    private void processManifestAndScheduleRefresh() {
        processManifest();
        scheduleManifestRefresh();
    }

    private void processManifest() {
        int i;
        int periodIndex;
        for (i = 0; i < this.periodsById.size(); i++) {
            int id = this.periodsById.keyAt(i);
            if (id >= this.firstPeriodId) {
                ((DashMediaPeriod) this.periodsById.valueAt(i)).updateManifest(this.manifest, id - this.firstPeriodId);
            }
        }
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        int lastPeriodIndex = this.manifest.getPeriodCount() - 1;
        PeriodSeekInfo firstPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(0), this.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo lastPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(lastPeriodIndex), this.manifest.getPeriodDurationUs(lastPeriodIndex));
        long currentStartTimeUs = firstPeriodSeekInfo.availableStartTimeUs;
        long currentEndTimeUs = lastPeriodSeekInfo.availableEndTimeUs;
        if (this.manifest.dynamic && !lastPeriodSeekInfo.isIndexExplicit) {
            currentEndTimeUs = Math.min((getNowUnixTimeUs() - C.msToUs(this.manifest.availabilityStartTime)) - C.msToUs(this.manifest.getPeriod(lastPeriodIndex).startMs), currentEndTimeUs);
            if (this.manifest.timeShiftBufferDepth != C.TIME_UNSET) {
                long offsetInPeriodUs = currentEndTimeUs - C.msToUs(this.manifest.timeShiftBufferDepth);
                periodIndex = lastPeriodIndex;
                while (offsetInPeriodUs < 0 && periodIndex > 0) {
                    periodIndex--;
                    offsetInPeriodUs += this.manifest.getPeriodDurationUs(periodIndex);
                }
                if (periodIndex == 0) {
                    currentStartTimeUs = Math.max(currentStartTimeUs, offsetInPeriodUs);
                } else {
                    currentStartTimeUs = this.manifest.getPeriodDurationUs(0);
                }
            }
            this.handler.postDelayed(this.simulateManifestRefreshRunnable, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
        long windowDurationUs = currentEndTimeUs - currentStartTimeUs;
        for (i = 0; i < this.manifest.getPeriodCount() - 1; i++) {
            windowDurationUs += this.manifest.getPeriodDurationUs(i);
        }
        long windowDefaultStartPositionUs = 0;
        if (this.manifest.dynamic) {
            long presentationDelayForManifestMs = this.livePresentationDelayMs;
            if (presentationDelayForManifestMs == -1) {
                presentationDelayForManifestMs = this.manifest.suggestedPresentationDelay != C.TIME_UNSET ? this.manifest.suggestedPresentationDelay : 30000;
            }
            long defaultStartPositionUs = windowDurationUs - C.msToUs(presentationDelayForManifestMs);
            if (defaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                defaultStartPositionUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, windowDurationUs / 2);
            }
            periodIndex = 0;
            long defaultStartPositionInPeriodUs = currentStartTimeUs + defaultStartPositionUs;
            long periodDurationUs = this.manifest.getPeriodDurationUs(0);
            while (periodIndex < this.manifest.getPeriodCount() - 1 && defaultStartPositionInPeriodUs >= periodDurationUs) {
                defaultStartPositionInPeriodUs -= periodDurationUs;
                periodIndex++;
                periodDurationUs = this.manifest.getPeriodDurationUs(periodIndex);
            }
            Period period = this.manifest.getPeriod(periodIndex);
            int videoAdaptationSetIndex = period.getAdaptationSetIndex(2);
            if (videoAdaptationSetIndex != -1) {
                DashSegmentIndex index = ((Representation) ((AdaptationSet) period.adaptationSets.get(videoAdaptationSetIndex)).representations.get(0)).getIndex();
                windowDefaultStartPositionUs = (defaultStartPositionUs - defaultStartPositionInPeriodUs) + index.getTimeUs(index.getSegmentNum(defaultStartPositionInPeriodUs, periodDurationUs));
            } else {
                windowDefaultStartPositionUs = defaultStartPositionUs;
            }
        }
        this.sourceListener.onSourceInfoRefreshed(new DashTimeline(this.manifest.availabilityStartTime, (this.manifest.availabilityStartTime + this.manifest.getPeriod(0).startMs) + C.usToMs(currentStartTimeUs), this.firstPeriodId, currentStartTimeUs, windowDurationUs, windowDefaultStartPositionUs, this.manifest), this.manifest);
    }

    private void scheduleManifestRefresh() {
        if (this.manifest.dynamic) {
            long minUpdatePeriod = this.manifest.minUpdatePeriod;
            if (minUpdatePeriod == 0) {
                minUpdatePeriod = ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            }
            this.handler.postDelayed(this.refreshManifestRunnable, Math.max(0, (this.manifestLoadStartTimestamp + minUpdatePeriod) - SystemClock.elapsedRealtime()));
        }
    }

    private <T> void startLoading(ParsingLoadable<T> loadable, Callback<ParsingLoadable<T>> callback, int minRetryCount) {
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.loader.startLoading(loadable, callback, minRetryCount));
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C.msToUs(System.currentTimeMillis());
    }

    private String generateContentId() {
        return Util.sha1(this.manifestUri.toString());
    }
}
