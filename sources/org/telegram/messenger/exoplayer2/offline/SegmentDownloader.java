package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public Segment(long j, DataSpec dataSpec) {
            this.startTimeUs = j;
            this.dataSpec = dataSpec;
        }

        public int compareTo(Segment segment) {
            long j = this.startTimeUs - segment.startTimeUs;
            if (j == 0) {
                return null;
            }
            return j < 0 ? -1 : 1;
        }
    }

    protected abstract List<Segment> getAllSegments(DataSource dataSource, M m, boolean z) throws InterruptedException, IOException;

    protected abstract M getManifest(DataSource dataSource, Uri uri) throws IOException;

    protected abstract List<Segment> getSegments(DataSource dataSource, M m, K[] kArr, boolean z) throws InterruptedException, IOException;

    public SegmentDownloader(Uri uri, DownloaderConstructorHelper downloaderConstructorHelper) {
        this.manifestUri = uri;
        this.cache = downloaderConstructorHelper.getCache();
        this.dataSource = downloaderConstructorHelper.buildCacheDataSource(null);
        this.offlineDataSource = downloaderConstructorHelper.buildCacheDataSource(true);
        this.priorityTaskManager = downloaderConstructorHelper.getPriorityTaskManager();
        resetCounters();
    }

    public final M getManifest() throws IOException {
        return getManifestIfNeeded(false);
    }

    public final void selectRepresentations(K[] kArr) {
        this.keys = kArr != null ? (Object[]) kArr.clone() : null;
        resetCounters();
    }

    public final void init() throws java.lang.InterruptedException, java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r1 = this;
        r0 = 1;
        r1.getManifestIfNeeded(r0);	 Catch:{ IOException -> 0x000d }
        r1.initStatus(r0);	 Catch:{ IOException -> 0x0008, IOException -> 0x0008 }
        return;
    L_0x0008:
        r0 = move-exception;
        r1.resetCounters();
        throw r0;
    L_0x000d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.offline.SegmentDownloader.init():void");
    }

    public final synchronized void download(ProgressListener progressListener) throws IOException, InterruptedException {
        this.priorityTaskManager.add(C0542C.PRIORITY_DOWNLOAD);
        int i = 0;
        try {
            getManifestIfNeeded(false);
            List initStatus = initStatus(false);
            notifyListener(progressListener);
            Collections.sort(initStatus);
            byte[] bArr = new byte[131072];
            CachingCounters cachingCounters = new CachingCounters();
            while (i < initStatus.size()) {
                CacheUtil.cache(((Segment) initStatus.get(i)).dataSpec, this.cache, this.dataSource, bArr, this.priorityTaskManager, C0542C.PRIORITY_DOWNLOAD, cachingCounters, true);
                this.downloadedBytes += cachingCounters.newlyCachedBytes;
                this.downloadedSegments++;
                notifyListener(progressListener);
                i++;
            }
        } finally {
            this.priorityTaskManager.remove(C0542C.PRIORITY_DOWNLOAD);
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
        int i = this.totalSegments;
        int i2 = this.downloadedSegments;
        if (i != -1) {
            if (i2 != -1) {
                float f = 100.0f;
                if (i != 0) {
                    f = (((float) i2) * 100.0f) / ((float) i);
                }
                return f;
            }
        }
        return Float.NaN;
    }

    public final void remove() throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r4 = this;
        r0 = 1;
        r4.getManifestIfNeeded(r0);	 Catch:{ IOException -> 0x0004 }
    L_0x0004:
        r4.resetCounters();
        r1 = r4.manifest;
        if (r1 == 0) goto L_0x0031;
    L_0x000b:
        r1 = 0;
        r2 = r4.offlineDataSource;	 Catch:{ IOException -> 0x0015 }
        r3 = r4.manifest;	 Catch:{ IOException -> 0x0015 }
        r0 = r4.getAllSegments(r2, r3, r0);	 Catch:{ IOException -> 0x0015 }
        goto L_0x0016;
    L_0x0015:
        r0 = r1;
    L_0x0016:
        if (r0 == 0) goto L_0x002f;
    L_0x0018:
        r2 = 0;
    L_0x0019:
        r3 = r0.size();
        if (r2 >= r3) goto L_0x002f;
    L_0x001f:
        r3 = r0.get(r2);
        r3 = (org.telegram.messenger.exoplayer2.offline.SegmentDownloader.Segment) r3;
        r3 = r3.dataSpec;
        r3 = r3.uri;
        r4.remove(r3);
        r2 = r2 + 1;
        goto L_0x0019;
    L_0x002f:
        r4.manifest = r1;
    L_0x0031:
        r0 = r4.manifestUri;
        r4.remove(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.offline.SegmentDownloader.remove():void");
    }

    private void resetCounters() {
        this.totalSegments = -1;
        this.downloadedSegments = -1;
        this.downloadedBytes = -1;
    }

    private void remove(Uri uri) {
        CacheUtil.remove(this.cache, CacheUtil.generateKey(uri));
    }

    private void notifyListener(ProgressListener progressListener) {
        if (progressListener != null) {
            progressListener.onDownloadProgress(this, getDownloadPercentage(), this.downloadedBytes);
        }
    }

    private synchronized List<Segment> initStatus(boolean z) throws IOException, InterruptedException {
        DataSource dataSource = getDataSource(z);
        if (this.keys == null || this.keys.length <= 0) {
            z = getAllSegments(dataSource, this.manifest, z);
        } else {
            z = getSegments(dataSource, this.manifest, this.keys, z);
        }
        CachingCounters cachingCounters = new CachingCounters();
        this.totalSegments = z.size();
        this.downloadedSegments = 0;
        this.downloadedBytes = 0;
        for (int size = z.size() - 1; size >= 0; size--) {
            CacheUtil.getCached(((Segment) z.get(size)).dataSpec, this.cache, cachingCounters);
            this.downloadedBytes += cachingCounters.alreadyCachedBytes;
            if (cachingCounters.alreadyCachedBytes == cachingCounters.contentLength) {
                this.downloadedSegments++;
                z.remove(size);
            }
        }
        return z;
    }

    private M getManifestIfNeeded(boolean z) throws IOException {
        if (this.manifest == null) {
            this.manifest = getManifest(getDataSource(z), this.manifestUri);
        }
        return this.manifest;
    }

    private DataSource getDataSource(boolean z) {
        return z ? this.offlineDataSource : this.dataSource;
    }
}
