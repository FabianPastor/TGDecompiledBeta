package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.util.Clock;

public abstract interface TimeRange
{
  public abstract long[] getCurrentBoundsMs(long[] paramArrayOfLong);
  
  public abstract long[] getCurrentBoundsUs(long[] paramArrayOfLong);
  
  public abstract boolean isStatic();
  
  public static final class DynamicTimeRange
    implements TimeRange
  {
    private final long bufferDepthUs;
    private final long elapsedRealtimeAtStartUs;
    private final long maxEndTimeUs;
    private final long minStartTimeUs;
    private final Clock systemClock;
    
    public DynamicTimeRange(long paramLong1, long paramLong2, long paramLong3, long paramLong4, Clock paramClock)
    {
      this.minStartTimeUs = paramLong1;
      this.maxEndTimeUs = paramLong2;
      this.elapsedRealtimeAtStartUs = paramLong3;
      this.bufferDepthUs = paramLong4;
      this.systemClock = paramClock;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (DynamicTimeRange)paramObject;
      } while ((((DynamicTimeRange)paramObject).minStartTimeUs == this.minStartTimeUs) && (((DynamicTimeRange)paramObject).maxEndTimeUs == this.maxEndTimeUs) && (((DynamicTimeRange)paramObject).elapsedRealtimeAtStartUs == this.elapsedRealtimeAtStartUs) && (((DynamicTimeRange)paramObject).bufferDepthUs == this.bufferDepthUs));
      return false;
    }
    
    public long[] getCurrentBoundsMs(long[] paramArrayOfLong)
    {
      paramArrayOfLong = getCurrentBoundsUs(paramArrayOfLong);
      paramArrayOfLong[0] /= 1000L;
      paramArrayOfLong[1] /= 1000L;
      return paramArrayOfLong;
    }
    
    public long[] getCurrentBoundsUs(long[] paramArrayOfLong)
    {
      long[] arrayOfLong;
      if (paramArrayOfLong != null)
      {
        arrayOfLong = paramArrayOfLong;
        if (paramArrayOfLong.length >= 2) {}
      }
      else
      {
        arrayOfLong = new long[2];
      }
      long l3 = Math.min(this.maxEndTimeUs, this.systemClock.elapsedRealtime() * 1000L - this.elapsedRealtimeAtStartUs);
      long l2 = this.minStartTimeUs;
      long l1 = l2;
      if (this.bufferDepthUs != -1L) {
        l1 = Math.max(l2, l3 - this.bufferDepthUs);
      }
      arrayOfLong[0] = l1;
      arrayOfLong[1] = l3;
      return arrayOfLong;
    }
    
    public int hashCode()
    {
      return ((((int)this.minStartTimeUs + 527) * 31 + (int)this.maxEndTimeUs) * 31 + (int)this.elapsedRealtimeAtStartUs) * 31 + (int)this.bufferDepthUs;
    }
    
    public boolean isStatic()
    {
      return false;
    }
  }
  
  public static final class StaticTimeRange
    implements TimeRange
  {
    private final long endTimeUs;
    private final long startTimeUs;
    
    public StaticTimeRange(long paramLong1, long paramLong2)
    {
      this.startTimeUs = paramLong1;
      this.endTimeUs = paramLong2;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (StaticTimeRange)paramObject;
      } while ((((StaticTimeRange)paramObject).startTimeUs == this.startTimeUs) && (((StaticTimeRange)paramObject).endTimeUs == this.endTimeUs));
      return false;
    }
    
    public long[] getCurrentBoundsMs(long[] paramArrayOfLong)
    {
      paramArrayOfLong = getCurrentBoundsUs(paramArrayOfLong);
      paramArrayOfLong[0] /= 1000L;
      paramArrayOfLong[1] /= 1000L;
      return paramArrayOfLong;
    }
    
    public long[] getCurrentBoundsUs(long[] paramArrayOfLong)
    {
      long[] arrayOfLong;
      if (paramArrayOfLong != null)
      {
        arrayOfLong = paramArrayOfLong;
        if (paramArrayOfLong.length >= 2) {}
      }
      else
      {
        arrayOfLong = new long[2];
      }
      arrayOfLong[0] = this.startTimeUs;
      arrayOfLong[1] = this.endTimeUs;
      return arrayOfLong;
    }
    
    public int hashCode()
    {
      return ((int)this.startTimeUs + 527) * 31 + (int)this.endTimeUs;
    }
    
    public boolean isStatic()
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/TimeRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */