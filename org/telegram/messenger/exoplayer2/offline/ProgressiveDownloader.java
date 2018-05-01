package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public final class ProgressiveDownloader
  implements Downloader
{
  private static final int BUFFER_SIZE_BYTES = 131072;
  private final Cache cache;
  private final CacheUtil.CachingCounters cachingCounters;
  private final CacheDataSource dataSource;
  private final DataSpec dataSpec;
  private final PriorityTaskManager priorityTaskManager;
  
  public ProgressiveDownloader(String paramString1, String paramString2, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    this.dataSpec = new DataSpec(Uri.parse(paramString1), 0L, -1L, paramString2, 0);
    this.cache = paramDownloaderConstructorHelper.getCache();
    this.dataSource = paramDownloaderConstructorHelper.buildCacheDataSource(false);
    this.priorityTaskManager = paramDownloaderConstructorHelper.getPriorityTaskManager();
    this.cachingCounters = new CacheUtil.CachingCounters();
  }
  
  public void download(Downloader.ProgressListener paramProgressListener)
    throws InterruptedException, IOException
  {
    this.priorityTaskManager.add(64536);
    try
    {
      byte[] arrayOfByte = new byte[131072];
      CacheUtil.cache(this.dataSpec, this.cache, this.dataSource, arrayOfByte, this.priorityTaskManager, 64536, this.cachingCounters, true);
      if (paramProgressListener != null) {
        paramProgressListener.onDownloadProgress(this, 100.0F, this.cachingCounters.contentLength);
      }
      return;
    }
    finally
    {
      this.priorityTaskManager.remove(64536);
    }
  }
  
  public float getDownloadPercentage()
  {
    long l = this.cachingCounters.contentLength;
    if (l == -1L) {}
    for (float f = NaN.0F;; f = (float)this.cachingCounters.totalCachedBytes() * 100.0F / (float)l) {
      return f;
    }
  }
  
  public long getDownloadedBytes()
  {
    return this.cachingCounters.totalCachedBytes();
  }
  
  public void init()
  {
    CacheUtil.getCached(this.dataSpec, this.cache, this.cachingCounters);
  }
  
  public void remove()
  {
    CacheUtil.remove(this.cache, CacheUtil.getKey(this.dataSpec));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/offline/ProgressiveDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */