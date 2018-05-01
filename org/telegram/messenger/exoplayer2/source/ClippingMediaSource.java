package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ClippingMediaSource
  extends CompositeMediaSource<Void>
{
  private IllegalClippingException clippingError;
  private final boolean enableInitialDiscontinuity;
  private final long endUs;
  private final ArrayList<ClippingMediaPeriod> mediaPeriods;
  private final MediaSource mediaSource;
  private MediaSource.Listener sourceListener;
  private final long startUs;
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2)
  {
    this(paramMediaSource, paramLong1, paramLong2, true);
  }
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    if (paramLong1 >= 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.mediaSource = ((MediaSource)Assertions.checkNotNull(paramMediaSource));
      this.startUs = paramLong1;
      this.endUs = paramLong2;
      this.enableInitialDiscontinuity = paramBoolean;
      this.mediaPeriods = new ArrayList();
      return;
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    paramMediaPeriodId = new ClippingMediaPeriod(this.mediaSource.createPeriod(paramMediaPeriodId, paramAllocator), this.enableInitialDiscontinuity);
    this.mediaPeriods.add(paramMediaPeriodId);
    paramMediaPeriodId.setClipping(this.startUs, this.endUs);
    return paramMediaPeriodId;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    if (this.clippingError != null) {
      throw this.clippingError;
    }
    super.maybeThrowSourceInfoRefreshError();
  }
  
  protected void onChildSourceInfoRefreshed(Void paramVoid, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    if (this.clippingError != null) {}
    for (;;)
    {
      return;
      try
      {
        paramVoid = new org/telegram/messenger/exoplayer2/source/ClippingMediaSource$ClippingTimeline;
        paramVoid.<init>(paramTimeline, this.startUs, this.endUs);
        this.sourceListener.onSourceInfoRefreshed(this, paramVoid, paramObject);
        int i = this.mediaPeriods.size();
        for (int j = 0; j < i; j++) {
          ((ClippingMediaPeriod)this.mediaPeriods.get(j)).setClipping(this.startUs, this.endUs);
        }
      }
      catch (IllegalClippingException paramVoid)
      {
        this.clippingError = paramVoid;
      }
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    super.prepareSource(paramExoPlayer, paramBoolean, paramListener);
    this.sourceListener = paramListener;
    prepareChildSource(null, this.mediaSource);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    Assertions.checkState(this.mediaPeriods.remove(paramMediaPeriod));
    this.mediaSource.releasePeriod(((ClippingMediaPeriod)paramMediaPeriod).mediaPeriod);
  }
  
  public void releaseSource()
  {
    super.releaseSource();
    this.clippingError = null;
    this.sourceListener = null;
  }
  
  private static final class ClippingTimeline
    extends ForwardingTimeline
  {
    private final long endUs;
    private final long startUs;
    
    public ClippingTimeline(Timeline paramTimeline, long paramLong1, long paramLong2)
      throws ClippingMediaSource.IllegalClippingException
    {
      super();
      if (paramTimeline.getPeriodCount() != 1) {
        throw new ClippingMediaSource.IllegalClippingException(0);
      }
      if (paramTimeline.getPeriod(0, new Timeline.Period()).getPositionInWindowUs() != 0L) {
        throw new ClippingMediaSource.IllegalClippingException(1);
      }
      paramTimeline = paramTimeline.getWindow(0, new Timeline.Window(), false);
      if (paramLong2 == Long.MIN_VALUE) {
        paramLong2 = paramTimeline.durationUs;
      }
      long l2;
      for (;;)
      {
        l1 = paramLong2;
        if (paramTimeline.durationUs == -9223372036854775807L) {
          break label160;
        }
        l2 = paramLong2;
        if (paramLong2 > paramTimeline.durationUs) {
          l2 = paramTimeline.durationUs;
        }
        if ((paramLong1 == 0L) || (paramTimeline.isSeekable)) {
          break;
        }
        throw new ClippingMediaSource.IllegalClippingException(2);
      }
      long l1 = l2;
      if (paramLong1 > l2) {
        throw new ClippingMediaSource.IllegalClippingException(3);
      }
      label160:
      this.startUs = paramLong1;
      this.endUs = l1;
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      long l = -9223372036854775807L;
      paramPeriod = this.timeline.getPeriod(0, paramPeriod, paramBoolean);
      if (this.endUs != -9223372036854775807L) {
        l = this.endUs - this.startUs;
      }
      paramPeriod.durationUs = l;
      return paramPeriod;
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      paramWindow = this.timeline.getWindow(0, paramWindow, paramBoolean, paramLong);
      if (this.endUs != -9223372036854775807L)
      {
        paramLong = this.endUs - this.startUs;
        paramWindow.durationUs = paramLong;
        if (paramWindow.defaultPositionUs != -9223372036854775807L)
        {
          paramWindow.defaultPositionUs = Math.max(paramWindow.defaultPositionUs, this.startUs);
          if (this.endUs != -9223372036854775807L) {
            break label166;
          }
        }
      }
      label166:
      for (paramLong = paramWindow.defaultPositionUs;; paramLong = Math.min(paramWindow.defaultPositionUs, this.endUs))
      {
        paramWindow.defaultPositionUs = paramLong;
        paramWindow.defaultPositionUs -= this.startUs;
        paramLong = C.usToMs(this.startUs);
        if (paramWindow.presentationStartTimeMs != -9223372036854775807L) {
          paramWindow.presentationStartTimeMs += paramLong;
        }
        if (paramWindow.windowStartTimeMs != -9223372036854775807L) {
          paramWindow.windowStartTimeMs += paramLong;
        }
        return paramWindow;
        paramLong = -9223372036854775807L;
        break;
      }
    }
  }
  
  public static final class IllegalClippingException
    extends IOException
  {
    public static final int REASON_INVALID_PERIOD_COUNT = 0;
    public static final int REASON_NOT_SEEKABLE_TO_START = 2;
    public static final int REASON_PERIOD_OFFSET_IN_WINDOW = 1;
    public static final int REASON_START_EXCEEDS_END = 3;
    public final int reason;
    
    public IllegalClippingException(int paramInt)
    {
      this.reason = paramInt;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Reason {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ClippingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */