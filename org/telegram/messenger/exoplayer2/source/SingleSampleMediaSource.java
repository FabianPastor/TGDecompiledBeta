package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SingleSampleMediaSource
  implements MediaSource
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private final DataSource.Factory dataSourceFactory;
  private final DataSpec dataSpec;
  private final long durationUs;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Format format;
  private boolean isPrepared;
  private final int minLoadableRetryCount;
  private final Timeline timeline;
  private final boolean treatLoadErrorsAsEndOfStream;
  
  @Deprecated
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, 3);
  }
  
  @Deprecated
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, paramInt, null, null, false);
  }
  
  private SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener, boolean paramBoolean)
  {
    this.dataSourceFactory = paramFactory;
    this.format = paramFormat;
    this.durationUs = paramLong;
    this.minLoadableRetryCount = paramInt;
    this.treatLoadErrorsAsEndOfStream = paramBoolean;
    this.eventDispatcher = new MediaSourceEventListener.EventDispatcher(paramHandler, paramMediaSourceEventListener);
    this.dataSpec = new DataSpec(paramUri);
    this.timeline = new SinglePeriodTimeline(paramLong, true, false);
  }
  
  @Deprecated
  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2, boolean paramBoolean) {}
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if (paramMediaPeriodId.periodIndex == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      return new SingleSampleMediaPeriod(this.dataSpec, this.dataSourceFactory, this.format, this.durationUs, this.minLoadableRetryCount, this.eventDispatcher, this.treatLoadErrorsAsEndOfStream);
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {}
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (!this.isPrepared) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.isPrepared = true;
      paramListener.onSourceInfoRefreshed(this, this.timeline, null);
      return;
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SingleSampleMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSource() {}
  
  @Deprecated
  public static abstract interface EventListener
  {
    public abstract void onLoadError(int paramInt, IOException paramIOException);
  }
  
  private static final class EventListenerWrapper
    implements MediaSourceEventListener
  {
    private final SingleSampleMediaSource.EventListener eventListener;
    private final int eventSourceId;
    
    public EventListenerWrapper(SingleSampleMediaSource.EventListener paramEventListener, int paramInt)
    {
      this.eventListener = ((SingleSampleMediaSource.EventListener)Assertions.checkNotNull(paramEventListener));
      this.eventSourceId = paramInt;
    }
    
    public void onDownstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong) {}
    
    public void onLoadCanceled(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {}
    
    public void onLoadCompleted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {}
    
    public void onLoadError(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, IOException paramIOException, boolean paramBoolean)
    {
      this.eventListener.onLoadError(this.eventSourceId, paramIOException);
    }
    
    public void onLoadStarted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3) {}
    
    public void onUpstreamDiscarded(int paramInt, long paramLong1, long paramLong2) {}
  }
  
  public static final class Factory
  {
    private final DataSource.Factory dataSourceFactory;
    private boolean isCreateCalled;
    private int minLoadableRetryCount;
    private boolean treatLoadErrorsAsEndOfStream;
    
    public Factory(DataSource.Factory paramFactory)
    {
      this.dataSourceFactory = ((DataSource.Factory)Assertions.checkNotNull(paramFactory));
      this.minLoadableRetryCount = 3;
    }
    
    public SingleSampleMediaSource createMediaSource(Uri paramUri, Format paramFormat, long paramLong)
    {
      return createMediaSource(paramUri, paramFormat, paramLong, null, null);
    }
    
    public SingleSampleMediaSource createMediaSource(Uri paramUri, Format paramFormat, long paramLong, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener)
    {
      this.isCreateCalled = true;
      return new SingleSampleMediaSource(paramUri, this.dataSourceFactory, paramFormat, paramLong, this.minLoadableRetryCount, paramHandler, paramMediaSourceEventListener, this.treatLoadErrorsAsEndOfStream, null);
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
    
    public Factory setTreatLoadErrorsAsEndOfStream(boolean paramBoolean)
    {
      if (!this.isCreateCalled) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.treatLoadErrorsAsEndOfStream = paramBoolean;
        return this;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/SingleSampleMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */