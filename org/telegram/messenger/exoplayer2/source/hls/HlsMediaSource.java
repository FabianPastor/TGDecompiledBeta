package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
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
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class HlsMediaSource
  implements MediaSource, HlsPlaylistTracker.PrimaryPlaylistListener
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private final boolean allowChunklessPreparation;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final HlsDataSourceFactory dataSourceFactory;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final HlsExtractorFactory extractorFactory;
  private final Uri manifestUri;
  private final int minLoadableRetryCount;
  private final ParsingLoadable.Parser<HlsPlaylist> playlistParser;
  private HlsPlaylistTracker playlistTracker;
  private MediaSource.Listener sourceListener;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
  }
  
  @Deprecated
  public HlsMediaSource(Uri paramUri, HlsDataSourceFactory paramHlsDataSourceFactory, HlsExtractorFactory paramHlsExtractorFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener, ParsingLoadable.Parser<HlsPlaylist> paramParser)
  {
    this(paramUri, paramHlsDataSourceFactory, paramHlsExtractorFactory, new DefaultCompositeSequenceableLoaderFactory(), paramInt, paramHandler, paramMediaSourceEventListener, paramParser, false);
  }
  
  private HlsMediaSource(Uri paramUri, HlsDataSourceFactory paramHlsDataSourceFactory, HlsExtractorFactory paramHlsExtractorFactory, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener, ParsingLoadable.Parser<HlsPlaylist> paramParser, boolean paramBoolean)
  {
    this.manifestUri = paramUri;
    this.dataSourceFactory = paramHlsDataSourceFactory;
    this.extractorFactory = paramHlsExtractorFactory;
    this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    this.minLoadableRetryCount = paramInt;
    this.playlistParser = paramParser;
    this.allowChunklessPreparation = paramBoolean;
    this.eventDispatcher = new MediaSourceEventListener.EventDispatcher(paramHandler, paramMediaSourceEventListener);
  }
  
  @Deprecated
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, new DefaultHlsDataSourceFactory(paramFactory), HlsExtractorFactory.DEFAULT, paramInt, paramHandler, paramMediaSourceEventListener, new HlsPlaylistParser());
  }
  
  @Deprecated
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
  {
    this(paramUri, paramFactory, 3, paramHandler, paramMediaSourceEventListener);
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if (paramMediaPeriodId.periodIndex == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      return new HlsMediaPeriod(this.extractorFactory, this.playlistTracker, this.dataSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, paramAllocator, this.compositeSequenceableLoaderFactory, this.allowChunklessPreparation);
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
  }
  
  public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    long l1;
    long l2;
    label25:
    long l3;
    long l4;
    label59:
    long l5;
    label91:
    long l6;
    boolean bool;
    if (paramHlsMediaPlaylist.hasProgramDateTime)
    {
      l1 = 0L;
      if (!paramHlsMediaPlaylist.hasProgramDateTime) {
        break label171;
      }
      l2 = C.usToMs(paramHlsMediaPlaylist.startTimeUs);
      l3 = paramHlsMediaPlaylist.startOffsetUs;
      if (!this.playlistTracker.isLive()) {
        break label224;
      }
      if (!paramHlsMediaPlaylist.hasEndTag) {
        break label179;
      }
      l4 = paramHlsMediaPlaylist.startTimeUs + paramHlsMediaPlaylist.durationUs;
      localObject = paramHlsMediaPlaylist.segments;
      l5 = l3;
      if (l3 == -9223372036854775807L)
      {
        if (!((List)localObject).isEmpty()) {
          break label187;
        }
        l5 = 0L;
      }
      l3 = paramHlsMediaPlaylist.durationUs;
      l6 = paramHlsMediaPlaylist.startTimeUs;
      if (paramHlsMediaPlaylist.hasEndTag) {
        break label218;
      }
      bool = true;
    }
    label113:
    for (Object localObject = new SinglePeriodTimeline(l1, l2, l4, l3, l6, l5, true, bool);; localObject = new SinglePeriodTimeline(l1, l2, paramHlsMediaPlaylist.startTimeUs + paramHlsMediaPlaylist.durationUs, paramHlsMediaPlaylist.durationUs, paramHlsMediaPlaylist.startTimeUs, l5, true, false))
    {
      this.sourceListener.onSourceInfoRefreshed(this, (Timeline)localObject, new HlsManifest(this.playlistTracker.getMasterPlaylist(), paramHlsMediaPlaylist));
      return;
      l1 = -9223372036854775807L;
      break;
      label171:
      l2 = -9223372036854775807L;
      break label25;
      label179:
      l4 = -9223372036854775807L;
      break label59;
      label187:
      l5 = ((HlsMediaPlaylist.Segment)((List)localObject).get(Math.max(0, ((List)localObject).size() - 3))).relativeStartTimeUs;
      break label91;
      label218:
      bool = false;
      break label113;
      label224:
      l5 = l3;
      if (l3 == -9223372036854775807L) {
        l5 = 0L;
      }
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (this.sourceListener == null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.playlistTracker = new HlsPlaylistTracker(this.manifestUri, this.dataSourceFactory, this.eventDispatcher, this.minLoadableRetryCount, this, this.playlistParser);
      this.sourceListener = paramListener;
      this.playlistTracker.start();
      return;
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((HlsMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSource()
  {
    if (this.playlistTracker != null)
    {
      this.playlistTracker.release();
      this.playlistTracker = null;
    }
  }
  
  public static final class Factory
    implements AdsMediaSource.MediaSourceFactory
  {
    private boolean allowChunklessPreparation;
    private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private HlsExtractorFactory extractorFactory;
    private final HlsDataSourceFactory hlsDataSourceFactory;
    private boolean isCreateCalled;
    private int minLoadableRetryCount;
    private ParsingLoadable.Parser<HlsPlaylist> playlistParser;
    
    public Factory(HlsDataSourceFactory paramHlsDataSourceFactory)
    {
      this.hlsDataSourceFactory = ((HlsDataSourceFactory)Assertions.checkNotNull(paramHlsDataSourceFactory));
      this.extractorFactory = HlsExtractorFactory.DEFAULT;
      this.minLoadableRetryCount = 3;
      this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
    }
    
    public Factory(DataSource.Factory paramFactory)
    {
      this(new DefaultHlsDataSourceFactory(paramFactory));
    }
    
    public HlsMediaSource createMediaSource(Uri paramUri)
    {
      return createMediaSource(paramUri, null, null);
    }
    
    public HlsMediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      this.isCreateCalled = true;
      if (this.playlistParser == null) {
        this.playlistParser = new HlsPlaylistParser();
      }
      return new HlsMediaSource(paramUri, this.hlsDataSourceFactory, this.extractorFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, paramHandler, paramMediaSourceEventListener, this.playlistParser, this.allowChunklessPreparation, null);
    }
    
    public int[] getSupportedTypes()
    {
      return new int[] { 2 };
    }
    
    public Factory setAllowChunklessPreparation(boolean paramBoolean)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.allowChunklessPreparation = paramBoolean;
        return this;
      }
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
    
    public Factory setExtractorFactory(HlsExtractorFactory paramHlsExtractorFactory)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.extractorFactory = ((HlsExtractorFactory)Assertions.checkNotNull(paramHlsExtractorFactory));
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
    
    public Factory setPlaylistParser(ParsingLoadable.Parser<HlsPlaylist> paramParser)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.playlistParser = ((ParsingLoadable.Parser)Assertions.checkNotNull(paramParser));
        return this;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */