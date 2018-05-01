package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline
  extends Timeline
{
  private static final Object ID = new Object();
  private final boolean isDynamic;
  private final boolean isSeekable;
  private final long periodDurationUs;
  private final long presentationStartTimeMs;
  private final long windowDefaultStartPositionUs;
  private final long windowDurationUs;
  private final long windowPositionInPeriodUs;
  private final long windowStartTimeMs;
  
  public SinglePeriodTimeline(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.presentationStartTimeMs = paramLong1;
    this.windowStartTimeMs = paramLong2;
    this.periodDurationUs = paramLong3;
    this.windowDurationUs = paramLong4;
    this.windowPositionInPeriodUs = paramLong5;
    this.windowDefaultStartPositionUs = paramLong6;
    this.isSeekable = paramBoolean1;
    this.isDynamic = paramBoolean2;
  }
  
  public SinglePeriodTimeline(long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(-9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3, paramLong4, paramBoolean1, paramBoolean2);
  }
  
  public SinglePeriodTimeline(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramLong, paramLong, 0L, 0L, paramBoolean1, paramBoolean2);
  }
  
  public int getIndexOfPeriod(Object paramObject)
  {
    if (ID.equals(paramObject)) {}
    for (int i = 0;; i = -1) {
      return i;
    }
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    if (paramBoolean) {}
    for (Object localObject = ID;; localObject = null) {
      return paramPeriod.set(localObject, localObject, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }
  }
  
  public int getPeriodCount()
  {
    return 1;
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    Object localObject;
    long l1;
    long l2;
    if (paramBoolean)
    {
      localObject = ID;
      l1 = this.windowDefaultStartPositionUs;
      l2 = l1;
      if (this.isDynamic)
      {
        l2 = l1;
        if (paramLong != 0L)
        {
          if (this.windowDurationUs != -9223372036854775807L) {
            break label101;
          }
          l2 = -9223372036854775807L;
        }
      }
    }
    for (;;)
    {
      return paramWindow.set(localObject, this.presentationStartTimeMs, this.windowStartTimeMs, this.isSeekable, this.isDynamic, l2, this.windowDurationUs, 0, 0, this.windowPositionInPeriodUs);
      localObject = null;
      break;
      label101:
      paramLong = l1 + paramLong;
      l2 = paramLong;
      if (paramLong > this.windowDurationUs) {
        l2 = -9223372036854775807L;
      }
    }
  }
  
  public int getWindowCount()
  {
    return 1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/SinglePeriodTimeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */