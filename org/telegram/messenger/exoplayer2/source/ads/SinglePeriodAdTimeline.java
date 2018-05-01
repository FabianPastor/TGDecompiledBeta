package org.telegram.messenger.exoplayer2.source.ads;

import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.ForwardingTimeline;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class SinglePeriodAdTimeline
  extends ForwardingTimeline
{
  private final AdPlaybackState adPlaybackState;
  
  public SinglePeriodAdTimeline(Timeline paramTimeline, AdPlaybackState paramAdPlaybackState)
  {
    super(paramTimeline);
    if (paramTimeline.getPeriodCount() == 1)
    {
      bool2 = true;
      Assertions.checkState(bool2);
      if (paramTimeline.getWindowCount() != 1) {
        break label51;
      }
    }
    label51:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Assertions.checkState(bool2);
      this.adPlaybackState = paramAdPlaybackState;
      return;
      bool2 = false;
      break;
    }
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    this.timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
    paramPeriod.set(paramPeriod.id, paramPeriod.uid, paramPeriod.windowIndex, paramPeriod.durationUs, paramPeriod.getPositionInWindowUs(), this.adPlaybackState);
    return paramPeriod;
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    paramWindow = super.getWindow(paramInt, paramWindow, paramBoolean, paramLong);
    if (paramWindow.durationUs == -9223372036854775807L) {
      paramWindow.durationUs = this.adPlaybackState.contentDurationUs;
    }
    return paramWindow;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ads/SinglePeriodAdTimeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */