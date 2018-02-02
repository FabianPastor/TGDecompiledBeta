package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.offline.Downloader.ProgressListener;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public abstract class SegmentDownloader<M, K> implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheDataSource dataSource;
    private volatile long downloadedBytes;
    private volatile int downloadedSegments;
    private K[] keys;
    private M manifest;
    private final Uri manifestUri;
    private final CacheDataSource offlineDataSource;
    private final PriorityTaskManager priorityTaskManager;
    private volatile int totalSegments;

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

    protected abstract List<Segment> getAllSegments(DataSource dataSource, M m, boolean z) throws InterruptedException, IOException;

    protected abstract M getManifest(DataSource dataSource, Uri uri) throws IOException;

    protected abstract List<Segment> getSegments(DataSource dataSource, M m, K[] kArr, boolean z) throws InterruptedException, IOException;

    public SegmentDownloader(Uri manifestUri, DownloaderConstructorHelper constructorHelper) {
        this.manifestUri = manifestUri;
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.buildCacheDataSource(false);
        this.offlineDataSource = constructorHelper.buildCacheDataSource(true);
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
        resetCounters();
    }

    public final M getManifest() throws IOException {
        return getManifestIfNeeded(false);
    }

    public final void selectRepresentations(K[] keys) {
        this.keys = keys != null ? (Object[]) keys.clone() : null;
        resetCounters();
    }

    public final void init() throws InterruptedException, IOException {
        try {
            Exception e;
            getManifestIfNeeded(true);
            try {
                initStatus(true);
                return;
            } catch (IOException e2) {
                e = e2;
            } catch (InterruptedException e3) {
                e = e3;
            }
            resetCounters();
            throw e;
        } catch (IOException e4) {
        }
    }

    public final synchronized void download(ProgressListener listener) throws IOException, InterruptedException {
        this.priorityTaskManager.add(C.PRIORITY_DOWNLOAD);
        try {
            getManifestIfNeeded(false);
            List<Segment> segments = initStatus(false);
            notifyListener(listener);
            Collections.sort(segments);
            byte[] buffer = new byte[131072];
            CachingCounters cachingCounters = new CachingCounters();
            for (int i = 0; i < segments.size(); i++) {
                CacheUtil.cache(((Segment) segments.get(i)).dataSpec, this.cache, this.dataSource, buffer, this.priorityTaskManager, C.PRIORITY_DOWNLOAD, cachingCounters, true);
                this.downloadedBytes += cachingCounters.newlyCachedBytes;
                this.downloadedSegments++;
                notifyListener(listener);
            }
            this.priorityTaskManager.remove(C.PRIORITY_DOWNLOAD);
        } catch (Throwable th) {
            this.priorityTaskManager.remove(C.PRIORITY_DOWNLOAD);
        }
    }

    public final int getTotalSegments() {
        return this.totalSegments;
    }

    public final int getDownloadedSegments() {
        return this.downloadedSegments;
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public float getDownloadPercentage() {
        int totalSegments = this.totalSegments;
        int downloadedSegments = this.downloadedSegments;
        if (totalSegments == -1 || downloadedSegments == -1) {
            return Float.NaN;
        }
        if (totalSegments != 0) {
            return (100.0f * ((float) downloadedSegments)) / ((float) totalSegments);
        }
        return 100.0f;
    }

    public final void remove() throws InterruptedException {
        try {
            getManifestIfNeeded(true);
        } catch (IOException e) {
        }
        resetCounters();
        if (this.manifest != null) {
            List<Segment> segments = null;
            try {
                segments = getAllSegments(this.offlineDataSource, this.manifest, true);
            } catch (IOException e2) {
            }
            if (segments != null) {
                for (int i = 0; i < segments.size(); i++) {
                    remove(((Segment) segments.get(i)).dataSpec.uri);
                }
            }
            this.manifest = null;
        }
        remove(this.manifestUri);
    }

    private void resetCounters() {
        this.totalSegments = -1;
        this.downloadedSegments = -1;
        this.downloadedBytes = -1;
    }

    private void remove(Uri uri) {
        CacheUtil.remove(this.cache, CacheUtil.generateKey(uri));
    }

    private void notifyListener(ProgressListener listener) {
        if (listener != null) {
            listener.onDownloadProgress(this, getDownloadPercentage(), this.downloadedBytes);
        }
    }

    private synchronized List<Segment> initStatus(boolean offline) throws IOException, InterruptedException {
        List<Segment> segments;
        DataSource dataSource = getDataSource(offline);
        if (this.keys == null || this.keys.length <= 0) {
            segments = getAllSegments(dataSource, this.manifest, offline);
        } else {
            segments = getSegments(dataSource, this.manifest, this.keys, offline);
        }
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

    private M getManifestIfNeeded(boolean offline) throws IOException {
        if (this.manifest == null) {
            this.manifest = getManifest(getDataSource(offline), this.manifestUri);
        }
        return this.manifest;
    }

    private DataSource getDataSource(boolean offline) {
        return offline ? this.offlineDataSource : this.dataSource;
    }
}
