package org.telegram.messenger.exoplayer2.source.hls;

import android.util.SparseArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class TimestampAdjusterProvider
{
  private final SparseArray<TimestampAdjuster> timestampAdjusters = new SparseArray();
  
  public TimestampAdjuster getAdjuster(int paramInt)
  {
    TimestampAdjuster localTimestampAdjuster1 = (TimestampAdjuster)this.timestampAdjusters.get(paramInt);
    TimestampAdjuster localTimestampAdjuster2 = localTimestampAdjuster1;
    if (localTimestampAdjuster1 == null)
    {
      localTimestampAdjuster2 = new TimestampAdjuster(Long.MAX_VALUE);
      this.timestampAdjusters.put(paramInt, localTimestampAdjuster2);
    }
    return localTimestampAdjuster2;
  }
  
  public void reset()
  {
    this.timestampAdjusters.clear();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/TimestampAdjusterProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */