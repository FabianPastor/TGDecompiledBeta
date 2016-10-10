package org.telegram.messenger.exoplayer.dash.mpd;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.util.ManifestFetcher.RedirectingManifest;

public class MediaPresentationDescription
  implements ManifestFetcher.RedirectingManifest
{
  public final long availabilityStartTime;
  public final long duration;
  public final boolean dynamic;
  public final String location;
  public final long minBufferTime;
  public final long minUpdatePeriod;
  private final List<Period> periods;
  public final long timeShiftBufferDepth;
  public final UtcTimingElement utcTiming;
  
  public MediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, UtcTimingElement paramUtcTimingElement, String paramString, List<Period> paramList)
  {
    this.availabilityStartTime = paramLong1;
    this.duration = paramLong2;
    this.minBufferTime = paramLong3;
    this.dynamic = paramBoolean;
    this.minUpdatePeriod = paramLong4;
    this.timeShiftBufferDepth = paramLong5;
    this.utcTiming = paramUtcTimingElement;
    this.location = paramString;
    paramUtcTimingElement = paramList;
    if (paramList == null) {
      paramUtcTimingElement = Collections.emptyList();
    }
    this.periods = paramUtcTimingElement;
  }
  
  public final String getNextManifestUri()
  {
    return this.location;
  }
  
  public final Period getPeriod(int paramInt)
  {
    return (Period)this.periods.get(paramInt);
  }
  
  public final int getPeriodCount()
  {
    return this.periods.size();
  }
  
  public final long getPeriodDuration(int paramInt)
  {
    if (paramInt == this.periods.size() - 1)
    {
      if (this.duration == -1L) {
        return -1L;
      }
      return this.duration - ((Period)this.periods.get(paramInt)).startMs;
    }
    return ((Period)this.periods.get(paramInt + 1)).startMs - ((Period)this.periods.get(paramInt)).startMs;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/MediaPresentationDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */