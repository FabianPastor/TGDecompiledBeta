package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public abstract class SegmentDownloader<M extends FilterableManifest<M, K>, K> implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheDataSource dataSource;
    private volatile long downloadedBytes;
    private volatile int downloadedSegments;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final Uri manifestUri;
    private final CacheDataSource offlineDataSource;
    private final PriorityTaskManager priorityTaskManager;
    private final ArrayList<K> streamKeys;
    private volatile int totalSegments = -1;

    protected static class Segment implements Comparable<Segment> {
        public final DataSpec dataSpec;
        public final long startTimeUs;

        public Segment(long startTimeUs, DataSpec dataSpec) {
            this.startTimeUs = startTimeUs;
            this.dataSpec = dataSpec;
        }

        public int compareTo(Segment other) {
            long startOffsetDiff = this.startTimeUs - other.startTimeUs;
            if (startOffsetDiff == 0) {
                return 0;
            }
            return startOffsetDiff < 0 ? -1 : 1;
        }
    }

    protected abstract M getManifest(DataSource dataSource, Uri uri) throws IOException;

    protected abstract List<Segment> getSegments(DataSource dataSource, M m, boolean z) throws InterruptedException, IOException;

    public SegmentDownloader(Uri manifestUri, List<K> streamKeys, DownloaderConstructorHelper constructorHelper) {
        this.manifestUri = manifestUri;
        this.streamKeys = new ArrayList(streamKeys);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.buildCacheDataSource(false);
        this.offlineDataSource = constructorHelper.buildCacheDataSource(true);
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
    }

    public final void download() throws IOException, InterruptedException {
        this.priorityTaskManager.add(-1000);
        List<Segment> segments = initDownload();
        Collections.sort(segments);
        byte[] buffer = new byte[131072];
        CachingCounters cachingCounters = new CachingCounters();
        int i = 0;
        while (i < segments.size()) {
            try {
                CacheUtil.cache(((Segment) segments.get(i)).dataSpec, this.cache, this.dataSource, buffer, this.priorityTaskManager, -1000, cachingCounters, this.isCanceled, true);
                this.downloadedSegments++;
                this.downloadedBytes += cachingCounters.newlyCachedBytes;
                i++;
            } catch (Throwable th) {
                this.priorityTaskManager.remove(-1000);
            }
        }
        this.priorityTaskManager.remove(-1000);
    }

    public void cancel() {
        this.isCanceled.set(true);
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public final float getDownloadPercentage() {
        int totalSegments = this.totalSegments;
        int downloadedSegments = this.downloadedSegments;
        if (totalSegments == -1 || downloadedSegments == -1) {
            return -1.0f;
        }
        if (totalSegments != 0) {
            return (100.0f * ((float) downloadedSegments)) / ((float) totalSegments);
        }
        return 100.0f;
    }

    public final void remove() throws InterruptedException {
        try {
            List<Segment> segments = getSegments(this.offlineDataSource, getManifest(this.offlineDataSource, this.manifestUri), true);
            for (int i = 0; i < segments.size(); i++) {
                removeUri(((Segment) segments.get(i)).dataSpec.uri);
            }
        } catch (IOException e) {
        } finally {
            removeUri(this.manifestUri);
        }
    }

    private List<Segment> initDownload() throws IOException, InterruptedException {
        M manifest = getManifest(this.dataSource, this.manifestUri);
        if (!this.streamKeys.isEmpty()) {
            manifest = (FilterableManifest) manifest.copy(this.streamKeys);
        }
        List<Segment> segments = getSegments(this.dataSource, manifest, false);
        CachingCounters cachingCounters = new CachingCounters();
        this.totalSegments = segments.size();
        this.downloadedSegments = 0;
        this.downloadedBytes = 0;
        for (int i = segments.size() - 1; i >= 0; i--) {
            CacheUtil.getCached(((Segment) segments.get(i)).dataSpec, this.cache, cachingCounters);
            this.downloadedBytes += cachingCounters.alreadyCachedBytes;
            if (cachingCounters.alreadyCachedBytes == cachingCounters.contentLength) {
                this.downloadedSegments++;
                segments.remove(i);
            }
        }
        return segments;
    }

    private void removeUri(Uri uri) {
        CacheUtil.remove(this.cache, CacheUtil.generateKey(uri));
    }
}
