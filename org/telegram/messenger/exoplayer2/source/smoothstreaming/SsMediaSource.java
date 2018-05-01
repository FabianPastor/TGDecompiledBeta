package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C;
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
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SsMediaSource
  implements MediaSource, Loader.Callback<ParsingLoadable<SsManifest>>
{
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000L;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
  private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000L;
  private final SsChunkSource.Factory chunkSourceFactory;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final long livePresentationDelayMs;
  private SsManifest manifest;
  private DataSource manifestDataSource;
  private final DataSource.Factory manifestDataSourceFactory;
  private long manifestLoadStartTimestamp;
  private Loader manifestLoader;
  private LoaderErrorThrower manifestLoaderErrorThrower;
  private final ParsingLoadable.Parser<? extends SsManifest> manifestParser;
  private Handler manifestRefreshHandler;
  private final Uri manifestUri;
  private final ArrayList<SsMediaPeriod> mediaPeriods;
  private final int minLoadableRetryCount;
  private MediaSource.Listener sourceListener;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.smoothstreaming");
  }
  
  @Deprecated
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, new SsManifestParser(), paramFactory1, paramInt, paramLong, paramHandler, paramMediaSourceEventListener);
  }
  
  @Deprecated
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, paramFactory1, 3, 30000L, paramHandler, paramMediaSourceEventListener);
  }
  
  @Deprecated
  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser<? extends SsManifest> paramParser, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(null, paramUri, paramFactory, paramParser, paramFactory1, new DefaultCompositeSequenceableLoaderFactory(), paramInt, paramLong, paramHandler, paramMediaSourceEventListener);
  }
  
  private SsMediaSource(SsManifest paramSsManifest, Uri paramUri, DataSource.Factory paramFactory, ParsingLoadable.Parser<? extends SsManifest> paramParser, SsChunkSource.Factory paramFactory1, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, int paramInt, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    boolean bool;
    if ((paramSsManifest == null) || (!paramSsManifest.isLive))
    {
      bool = true;
      Assertions.checkState(bool);
      this.manifest = paramSsManifest;
      if (paramUri != null) {
        break label107;
      }
      paramSsManifest = null;
    }
    for (;;)
    {
      this.manifestUri = paramSsManifest;
      this.manifestDataSourceFactory = paramFactory;
      this.manifestParser = paramParser;
      this.chunkSourceFactory = paramFactory1;
      this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
      this.minLoadableRetryCount = paramInt;
      this.livePresentationDelayMs = paramLong;
      this.eventDispatcher = new MediaSourceEventListener.EventDispatcher(paramHandler, paramMediaSourceEventListener);
      this.mediaPeriods = new ArrayList();
      return;
      bool = false;
      break;
      label107:
      paramSsManifest = paramUri;
      if (!Util.toLowerInvariant(paramUri.getLastPathSegment()).matches("manifest(\\(.+\\))?")) {
        paramSsManifest = Uri.withAppendedPath(paramUri, "Manifest");
      }
    }
  }
  
  @Deprecated
  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramSsManifest, null, null, null, paramFactory, new DefaultCompositeSequenceableLoaderFactory(), paramInt, 30000L, paramHandler, paramMediaSourceEventListener);
  }
  
  @Deprecated
  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramSsManifest, paramFactory, 3, paramHandler, paramMediaSourceEventListener);
  }
  
  private void processManifest()
  {
    for (int i = 0; i < this.mediaPeriods.size(); i++) {
      ((SsMediaPeriod)this.mediaPeriods.get(i)).updateManifest(this.manifest);
    }
    long l1 = Long.MIN_VALUE;
    SsManifest.StreamElement[] arrayOfStreamElement = this.manifest.streamElements;
    int j = arrayOfStreamElement.length;
    i = 0;
    long l2 = Long.MAX_VALUE;
    Object localObject;
    if (i < j)
    {
      localObject = arrayOfStreamElement[i];
      if (((SsManifest.StreamElement)localObject).chunkCount <= 0) {
        break label372;
      }
      l2 = Math.min(l2, ((SsManifest.StreamElement)localObject).getStartTimeUs(0));
      l1 = Math.max(l1, ((SsManifest.StreamElement)localObject).getStartTimeUs(((SsManifest.StreamElement)localObject).chunkCount - 1) + ((SsManifest.StreamElement)localObject).getChunkDurationUs(((SsManifest.StreamElement)localObject).chunkCount - 1));
    }
    label369:
    label372:
    for (;;)
    {
      i++;
      break;
      if (l2 == Long.MAX_VALUE)
      {
        if (this.manifest.isLive) {}
        for (l2 = -9223372036854775807L;; l2 = 0L)
        {
          localObject = new SinglePeriodTimeline(l2, 0L, 0L, 0L, true, this.manifest.isLive);
          this.sourceListener.onSourceInfoRefreshed(this, (Timeline)localObject, this.manifest);
          return;
        }
      }
      if (this.manifest.isLive)
      {
        if ((this.manifest.dvrWindowLengthUs == -9223372036854775807L) || (this.manifest.dvrWindowLengthUs <= 0L)) {
          break label369;
        }
        l2 = Math.max(l2, l1 - this.manifest.dvrWindowLengthUs);
      }
      for (;;)
      {
        long l3 = l1 - l2;
        long l4 = l3 - C.msToUs(this.livePresentationDelayMs);
        l1 = l4;
        if (l4 < 5000000L) {
          l1 = Math.min(5000000L, l3 / 2L);
        }
        localObject = new SinglePeriodTimeline(-9223372036854775807L, l3, l2, l1, true, true);
        break;
        if (this.manifest.durationUs != -9223372036854775807L) {}
        for (l1 = this.manifest.durationUs;; l1 -= l2)
        {
          localObject = new SinglePeriodTimeline(l2 + l1, l1, l2, 0L, true, false);
          break;
        }
      }
    }
  }
  
  private void scheduleManifestRefresh()
  {
    if (!this.manifest.isLive) {}
    for (;;)
    {
      return;
      long l = Math.max(0L, this.manifestLoadStartTimestamp + 5000L - SystemClock.elapsedRealtime());
      this.manifestRefreshHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          SsMediaSource.this.startLoadingManifest();
        }
      }, l);
    }
  }
  
  private void startLoadingManifest()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
    long l = this.manifestLoader.startLoading(localParsingLoadable, this, this.minLoadableRetryCount);
    this.eventDispatcher.loadStarted(localParsingLoadable.dataSpec, localParsingLoadable.type, l);
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if (paramMediaPeriodId.periodIndex == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      paramMediaPeriodId = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.eventDispatcher, this.manifestLoaderErrorThrower, paramAllocator);
      this.mediaPeriods.add(paramMediaPeriodId);
      return paramMediaPeriodId;
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onLoadCanceled(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public void onLoadCompleted(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    this.manifest = ((SsManifest)paramParsingLoadable.getResult());
    this.manifestLoadStartTimestamp = (paramLong1 - paramLong2);
    processManifest();
    scheduleManifestRefresh();
  }
  
  public int onLoadError(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {}
    for (int i = 3;; i = 0) {
      return i;
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (this.sourceListener == null)
    {
      paramBoolean = true;
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.sourceListener = paramListener;
      if (this.manifest == null) {
        break label49;
      }
      this.manifestLoaderErrorThrower = new LoaderErrorThrower.Dummy();
      processManifest();
    }
    for (;;)
    {
      return;
      paramBoolean = false;
      break;
      label49:
      this.manifestDataSource = this.manifestDataSourceFactory.createDataSource();
      this.manifestLoader = new Loader("Loader:Manifest");
      this.manifestLoaderErrorThrower = this.manifestLoader;
      this.manifestRefreshHandler = new Handler();
      startLoadingManifest();
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SsMediaPeriod)paramMediaPeriod).release();
    this.mediaPeriods.remove(paramMediaPeriod);
  }
  
  public void releaseSource()
  {
    this.manifest = null;
    this.manifestDataSource = null;
    this.manifestLoadStartTimestamp = 0L;
    if (this.manifestLoader != null)
    {
      this.manifestLoader.release();
      this.manifestLoader = null;
    }
    if (this.manifestRefreshHandler != null)
    {
      this.manifestRefreshHandler.removeCallbacksAndMessages(null);
      this.manifestRefreshHandler = null;
    }
  }
  
  public static final class Factory
    implements AdsMediaSource.MediaSourceFactory
  {
    private final SsChunkSource.Factory chunkSourceFactory;
    private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private boolean isCreateCalled;
    private long livePresentationDelayMs;
    private final DataSource.Factory manifestDataSourceFactory;
    private ParsingLoadable.Parser<? extends SsManifest> manifestParser;
    private int minLoadableRetryCount;
    
    public Factory(SsChunkSource.Factory paramFactory, DataSource.Factory paramFactory1)
    {
      this.chunkSourceFactory = ((SsChunkSource.Factory)Assertions.checkNotNull(paramFactory));
      this.manifestDataSourceFactory = paramFactory1;
      this.minLoadableRetryCount = 3;
      this.livePresentationDelayMs = 30000L;
      this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
    }
    
    public SsMediaSource createMediaSource(Uri paramUri)
    {
      return createMediaSource(paramUri, null, null);
    }
    
    public SsMediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      this.isCreateCalled = true;
      if (this.manifestParser == null) {
        this.manifestParser = new SsManifestParser();
      }
      return new SsMediaSource(null, (Uri)Assertions.checkNotNull(paramUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, paramHandler, paramMediaSourceEventListener, null);
    }
    
    public SsMediaSource createMediaSource(SsManifest paramSsManifest, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      if (!paramSsManifest.isLive) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        this.isCreateCalled = true;
        return new SsMediaSource(paramSsManifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.livePresentationDelayMs, paramHandler, paramMediaSourceEventListener, null);
      }
    }
    
    public int[] getSupportedTypes()
    {
      return new int[] { 1 };
    }
    
    public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.compositeSequenceableLoaderFactory = ((CompositeSequenceableLoaderFactory)Assertions.checkNotNull(paramCompositeSequenceableLoaderFactory));
        return this;
      }
    }
    
    public Factory setLivePresentationDelayMs(long paramLong)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.livePresentationDelayMs = paramLong;
        return this;
      }
    }
    
    public Factory setManifestParser(ParsingLoadable.Parser<? extends SsManifest> paramParser)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.manifestParser = ((ParsingLoadable.Parser)Assertions.checkNotNull(paramParser));
        return this;
      }
    }
    
    public Factory setMinLoadableRetryCount(int paramInt)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.minLoadableRetryCount = paramInt;
        return this;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/SsMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */