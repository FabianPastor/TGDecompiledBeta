package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class LoopingMediaSource
  implements MediaSource
{
  private int childPeriodCount;
  private final MediaSource childSource;
  private final int loopCount;
  private boolean wasPrepareSourceCalled;
  
  public LoopingMediaSource(MediaSource paramMediaSource)
  {
    this(paramMediaSource, Integer.MAX_VALUE);
  }
  
  public LoopingMediaSource(MediaSource paramMediaSource, int paramInt)
  {
    if (paramInt > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.childSource = paramMediaSource;
      this.loopCount = paramInt;
      return;
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if (this.loopCount != Integer.MAX_VALUE) {}
    for (paramMediaPeriodId = this.childSource.createPeriod(paramMediaPeriodId.copyWithPeriodIndex(paramMediaPeriodId.periodIndex % this.childPeriodCount), paramAllocator);; paramMediaPeriodId = this.childSource.createPeriod(paramMediaPeriodId, paramAllocator)) {
      return paramMediaPeriodId;
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.childSource.maybeThrowSourceInfoRefreshError();
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, final MediaSource.Listener paramListener)
  {
    if (!this.wasPrepareSourceCalled) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.wasPrepareSourceCalled = true;
      this.childSource.prepareSource(paramExoPlayer, false, new MediaSource.Listener()
      {
        public void onSourceInfoRefreshed(MediaSource paramAnonymousMediaSource, Timeline paramAnonymousTimeline, Object paramAnonymousObject)
        {
          LoopingMediaSource.access$002(LoopingMediaSource.this, paramAnonymousTimeline.getPeriodCount());
          if (LoopingMediaSource.this.loopCount != Integer.MAX_VALUE) {}
          for (paramAnonymousMediaSource = new LoopingMediaSource.LoopingTimeline(paramAnonymousTimeline, LoopingMediaSource.this.loopCount);; paramAnonymousMediaSource = new LoopingMediaSource.InfinitelyLoopingTimeline(paramAnonymousTimeline))
          {
            paramListener.onSourceInfoRefreshed(LoopingMediaSource.this, paramAnonymousMediaSource, paramAnonymousObject);
            return;
          }
        }
      });
      return;
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    this.childSource.releasePeriod(paramMediaPeriod);
  }
  
  public void releaseSource()
  {
    this.childSource.releaseSource();
  }
  
  private static final class InfinitelyLoopingTimeline
    extends ForwardingTimeline
  {
    public InfinitelyLoopingTimeline(Timeline paramTimeline)
    {
      super();
    }
    
    public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      paramInt2 = this.timeline.getNextWindowIndex(paramInt1, paramInt2, paramBoolean);
      paramInt1 = paramInt2;
      if (paramInt2 == -1) {
        paramInt1 = getFirstWindowIndex(paramBoolean);
      }
      return paramInt1;
    }
    
    public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      paramInt2 = this.timeline.getPreviousWindowIndex(paramInt1, paramInt2, paramBoolean);
      paramInt1 = paramInt2;
      if (paramInt2 == -1) {
        paramInt1 = getLastWindowIndex(paramBoolean);
      }
      return paramInt1;
    }
  }
  
  private static final class LoopingTimeline
    extends AbstractConcatenatedTimeline
  {
    private final int childPeriodCount;
    private final Timeline childTimeline;
    private final int childWindowCount;
    private final int loopCount;
    
    public LoopingTimeline(Timeline paramTimeline, int paramInt)
    {
      super();
      this.childTimeline = paramTimeline;
      this.childPeriodCount = paramTimeline.getPeriodCount();
      this.childWindowCount = paramTimeline.getWindowCount();
      this.loopCount = paramInt;
      if (this.childPeriodCount > 0) {
        if (paramInt > Integer.MAX_VALUE / this.childPeriodCount) {
          break label65;
        }
      }
      label65:
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool, "LoopingMediaSource contains too many periods");
        return;
      }
    }
    
    protected int getChildIndexByChildUid(Object paramObject)
    {
      if (!(paramObject instanceof Integer)) {}
      for (int i = -1;; i = ((Integer)paramObject).intValue()) {
        return i;
      }
    }
    
    protected int getChildIndexByPeriodIndex(int paramInt)
    {
      return paramInt / this.childPeriodCount;
    }
    
    protected int getChildIndexByWindowIndex(int paramInt)
    {
      return paramInt / this.childWindowCount;
    }
    
    protected Object getChildUidByChildIndex(int paramInt)
    {
      return Integer.valueOf(paramInt);
    }
    
    protected int getFirstPeriodIndexByChildIndex(int paramInt)
    {
      return this.childPeriodCount * paramInt;
    }
    
    protected int getFirstWindowIndexByChildIndex(int paramInt)
    {
      return this.childWindowCount * paramInt;
    }
    
    public int getPeriodCount()
    {
      return this.childPeriodCount * this.loopCount;
    }
    
    protected Timeline getTimelineByChildIndex(int paramInt)
    {
      return this.childTimeline;
    }
    
    public int getWindowCount()
    {
      return this.childWindowCount * this.loopCount;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/LoopingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */