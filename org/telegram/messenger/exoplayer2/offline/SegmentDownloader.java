package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public abstract class SegmentDownloader<M, K>
  implements Downloader
{
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
  
  public SegmentDownloader(Uri paramUri, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    this.manifestUri = paramUri;
    this.cache = paramDownloaderConstructorHelper.getCache();
    this.dataSource = paramDownloaderConstructorHelper.buildCacheDataSource(false);
    this.offlineDataSource = paramDownloaderConstructorHelper.buildCacheDataSource(true);
    this.priorityTaskManager = paramDownloaderConstructorHelper.getPriorityTaskManager();
    resetCounters();
  }
  
  private DataSource getDataSource(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (CacheDataSource localCacheDataSource = this.offlineDataSource;; localCacheDataSource = this.dataSource) {
      return localCacheDataSource;
    }
  }
  
  private M getManifestIfNeeded(boolean paramBoolean)
    throws IOException
  {
    if (this.manifest == null) {
      this.manifest = getManifest(getDataSource(paramBoolean), this.manifestUri);
    }
    return (M)this.manifest;
  }
  
  private List<Segment> initStatus(boolean paramBoolean)
    throws IOException, InterruptedException
  {
    try
    {
      Object localObject1 = getDataSource(paramBoolean);
      if ((this.keys != null) && (this.keys.length > 0)) {}
      for (localObject1 = getSegments((DataSource)localObject1, this.manifest, this.keys, paramBoolean);; localObject1 = getAllSegments((DataSource)localObject1, this.manifest, paramBoolean))
      {
        CacheUtil.CachingCounters localCachingCounters = new org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil$CachingCounters;
        localCachingCounters.<init>();
        this.totalSegments = ((List)localObject1).size();
        this.downloadedSegments = 0;
        this.downloadedBytes = 0L;
        for (int i = ((List)localObject1).size() - 1; i >= 0; i--)
        {
          CacheUtil.getCached(((Segment)((List)localObject1).get(i)).dataSpec, this.cache, localCachingCounters);
          this.downloadedBytes += localCachingCounters.alreadyCachedBytes;
          if (localCachingCounters.alreadyCachedBytes == localCachingCounters.contentLength)
          {
            this.downloadedSegments += 1;
            ((List)localObject1).remove(i);
          }
        }
      }
      return (List<Segment>)localObject1;
    }
    finally {}
  }
  
  private void notifyListener(Downloader.ProgressListener paramProgressListener)
  {
    if (paramProgressListener != null) {
      paramProgressListener.onDownloadProgress(this, getDownloadPercentage(), this.downloadedBytes);
    }
  }
  
  private void remove(Uri paramUri)
  {
    CacheUtil.remove(this.cache, CacheUtil.generateKey(paramUri));
  }
  
  private void resetCounters()
  {
    this.totalSegments = -1;
    this.downloadedSegments = -1;
    this.downloadedBytes = -1L;
  }
  
  /* Error */
  public final void download(Downloader.ProgressListener paramProgressListener)
    throws IOException, InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 61	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:priorityTaskManager	Lorg/telegram/messenger/exoplayer2/util/PriorityTaskManager;
    //   6: sipush 64536
    //   9: invokevirtual 164	org/telegram/messenger/exoplayer2/util/PriorityTaskManager:add	(I)V
    //   12: aload_0
    //   13: iconst_0
    //   14: invokespecial 166	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:getManifestIfNeeded	(Z)Ljava/lang/Object;
    //   17: pop
    //   18: aload_0
    //   19: iconst_0
    //   20: invokespecial 168	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:initStatus	(Z)Ljava/util/List;
    //   23: astore_2
    //   24: aload_0
    //   25: aload_1
    //   26: invokespecial 170	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:notifyListener	(Lorg/telegram/messenger/exoplayer2/offline/Downloader$ProgressListener;)V
    //   29: aload_2
    //   30: invokestatic 176	java/util/Collections:sort	(Ljava/util/List;)V
    //   33: ldc 13
    //   35: newarray <illegal type>
    //   37: astore_3
    //   38: new 94	org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil$CachingCounters
    //   41: astore 4
    //   43: aload 4
    //   45: invokespecial 95	org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil$CachingCounters:<init>	()V
    //   48: iconst_0
    //   49: istore 5
    //   51: iload 5
    //   53: aload_2
    //   54: invokeinterface 101 1 0
    //   59: if_icmpge +74 -> 133
    //   62: aload_2
    //   63: iload 5
    //   65: invokeinterface 111 2 0
    //   70: checkcast 9	org/telegram/messenger/exoplayer2/offline/SegmentDownloader$Segment
    //   73: getfield 115	org/telegram/messenger/exoplayer2/offline/SegmentDownloader$Segment:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   76: aload_0
    //   77: getfield 47	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:cache	Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;
    //   80: aload_0
    //   81: getfield 53	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource;
    //   84: aload_3
    //   85: aload_0
    //   86: getfield 61	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:priorityTaskManager	Lorg/telegram/messenger/exoplayer2/util/PriorityTaskManager;
    //   89: sipush 64536
    //   92: aload 4
    //   94: iconst_1
    //   95: invokestatic 179	org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil:cache	(Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource;[BLorg/telegram/messenger/exoplayer2/util/PriorityTaskManager;ILorg/telegram/messenger/exoplayer2/upstream/cache/CacheUtil$CachingCounters;Z)V
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 107	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:downloadedBytes	J
    //   103: aload 4
    //   105: getfield 182	org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil$CachingCounters:newlyCachedBytes	J
    //   108: ladd
    //   109: putfield 107	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:downloadedBytes	J
    //   112: aload_0
    //   113: aload_0
    //   114: getfield 105	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:downloadedSegments	I
    //   117: iconst_1
    //   118: iadd
    //   119: putfield 105	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:downloadedSegments	I
    //   122: aload_0
    //   123: aload_1
    //   124: invokespecial 170	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:notifyListener	(Lorg/telegram/messenger/exoplayer2/offline/Downloader$ProgressListener;)V
    //   127: iinc 5 1
    //   130: goto -79 -> 51
    //   133: aload_0
    //   134: getfield 61	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:priorityTaskManager	Lorg/telegram/messenger/exoplayer2/util/PriorityTaskManager;
    //   137: sipush 64536
    //   140: invokevirtual 184	org/telegram/messenger/exoplayer2/util/PriorityTaskManager:remove	(I)V
    //   143: aload_0
    //   144: monitorexit
    //   145: return
    //   146: astore_1
    //   147: aload_0
    //   148: getfield 61	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:priorityTaskManager	Lorg/telegram/messenger/exoplayer2/util/PriorityTaskManager;
    //   151: sipush 64536
    //   154: invokevirtual 184	org/telegram/messenger/exoplayer2/util/PriorityTaskManager:remove	(I)V
    //   157: aload_1
    //   158: athrow
    //   159: astore_1
    //   160: aload_0
    //   161: monitorexit
    //   162: aload_1
    //   163: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	SegmentDownloader
    //   0	164	1	paramProgressListener	Downloader.ProgressListener
    //   23	40	2	localList	List
    //   37	48	3	arrayOfByte	byte[]
    //   41	63	4	localCachingCounters	CacheUtil.CachingCounters
    //   49	79	5	i	int
    // Exception table:
    //   from	to	target	type
    //   12	48	146	finally
    //   51	127	146	finally
    //   2	12	159	finally
    //   133	143	159	finally
    //   147	159	159	finally
  }
  
  protected abstract List<Segment> getAllSegments(DataSource paramDataSource, M paramM, boolean paramBoolean)
    throws InterruptedException, IOException;
  
  public float getDownloadPercentage()
  {
    float f = 100.0F;
    int i = this.totalSegments;
    int j = this.downloadedSegments;
    if ((i == -1) || (j == -1)) {
      f = NaN.0F;
    }
    for (;;)
    {
      return f;
      if (i != 0) {
        f = 100.0F * j / i;
      }
    }
  }
  
  public final long getDownloadedBytes()
  {
    return this.downloadedBytes;
  }
  
  public final int getDownloadedSegments()
  {
    return this.downloadedSegments;
  }
  
  public final M getManifest()
    throws IOException
  {
    return (M)getManifestIfNeeded(false);
  }
  
  protected abstract M getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException;
  
  protected abstract List<Segment> getSegments(DataSource paramDataSource, M paramM, K[] paramArrayOfK, boolean paramBoolean)
    throws InterruptedException, IOException;
  
  public final int getTotalSegments()
  {
    return this.totalSegments;
  }
  
  /* Error */
  public final void init()
    throws InterruptedException, IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: invokespecial 166	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:getManifestIfNeeded	(Z)Ljava/lang/Object;
    //   5: pop
    //   6: aload_0
    //   7: iconst_1
    //   8: invokespecial 168	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:initStatus	(Z)Ljava/util/List;
    //   11: pop
    //   12: return
    //   13: astore_1
    //   14: goto -2 -> 12
    //   17: astore_1
    //   18: aload_0
    //   19: invokespecial 64	org/telegram/messenger/exoplayer2/offline/SegmentDownloader:resetCounters	()V
    //   22: aload_1
    //   23: athrow
    //   24: astore_1
    //   25: goto -7 -> 18
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	SegmentDownloader
    //   13	1	1	localIOException1	IOException
    //   17	6	1	localIOException2	IOException
    //   24	1	1	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   0	6	13	java/io/IOException
    //   6	12	17	java/io/IOException
    //   6	12	24	java/lang/InterruptedException
  }
  
  public final void remove()
    throws InterruptedException
  {
    try
    {
      getManifestIfNeeded(true);
      resetCounters();
      Object localObject;
      if (this.manifest != null) {
        localObject = null;
      }
      try
      {
        List localList = getAllSegments(this.offlineDataSource, this.manifest, true);
        localObject = localList;
      }
      catch (IOException localIOException2)
      {
        int i;
        for (;;) {}
      }
      if (localObject != null) {
        for (i = 0; i < ((List)localObject).size(); i++) {
          remove(((Segment)((List)localObject).get(i)).dataSpec.uri);
        }
      }
      this.manifest = null;
      remove(this.manifestUri);
      return;
    }
    catch (IOException localIOException1)
    {
      for (;;) {}
    }
  }
  
  public final void selectRepresentations(K[] paramArrayOfK)
  {
    if (paramArrayOfK != null) {}
    for (paramArrayOfK = (Object[])paramArrayOfK.clone();; paramArrayOfK = null)
    {
      this.keys = paramArrayOfK;
      resetCounters();
      return;
    }
  }
  
  protected static class Segment
    implements Comparable<Segment>
  {
    public final DataSpec dataSpec;
    public final long startTimeUs;
    
    public Segment(long paramLong, DataSpec paramDataSpec)
    {
      this.startTimeUs = paramLong;
      this.dataSpec = paramDataSpec;
    }
    
    public int compareTo(Segment paramSegment)
    {
      long l = this.startTimeUs - paramSegment.startTimeUs;
      int i;
      if (l == 0L) {
        i = 0;
      }
      for (;;)
      {
        return i;
        if (l < 0L) {
          i = -1;
        } else {
          i = 1;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/offline/SegmentDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */