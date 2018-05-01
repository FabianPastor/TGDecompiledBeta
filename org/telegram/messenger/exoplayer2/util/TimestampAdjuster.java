package org.telegram.messenger.exoplayer2.util;

public final class TimestampAdjuster
{
  public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
  private static final long MAX_PTS_PLUS_ONE = 8589934592L;
  private long firstSampleTimestampUs;
  private volatile long lastSampleTimestamp = -9223372036854775807L;
  private long timestampOffsetUs;
  
  public TimestampAdjuster(long paramLong)
  {
    setFirstSampleTimestampUs(paramLong);
  }
  
  public static long ptsToUs(long paramLong)
  {
    return 1000000L * paramLong / 90000L;
  }
  
  public static long usToPts(long paramLong)
  {
    return 90000L * paramLong / 1000000L;
  }
  
  public long adjustSampleTimestamp(long paramLong)
  {
    long l = -9223372036854775807L;
    if (paramLong == -9223372036854775807L)
    {
      paramLong = l;
      return paramLong;
    }
    if (this.lastSampleTimestamp != -9223372036854775807L) {
      this.lastSampleTimestamp = paramLong;
    }
    for (;;)
    {
      paramLong = this.timestampOffsetUs + paramLong;
      break;
      if (this.firstSampleTimestampUs != Long.MAX_VALUE) {
        this.timestampOffsetUs = (this.firstSampleTimestampUs - paramLong);
      }
      try
      {
        this.lastSampleTimestamp = paramLong;
        notifyAll();
      }
      finally {}
    }
  }
  
  public long adjustTsTimestamp(long paramLong)
  {
    if (paramLong == -9223372036854775807L)
    {
      paramLong = -9223372036854775807L;
      return paramLong;
    }
    long l1 = paramLong;
    if (this.lastSampleTimestamp != -9223372036854775807L)
    {
      long l2 = usToPts(this.lastSampleTimestamp);
      long l3 = (4294967296L + l2) / 8589934592L;
      l1 = paramLong + 8589934592L * (l3 - 1L);
      paramLong += 8589934592L * l3;
      if (Math.abs(l1 - l2) >= Math.abs(paramLong - l2)) {
        break label98;
      }
    }
    for (;;)
    {
      paramLong = adjustSampleTimestamp(ptsToUs(l1));
      break;
      label98:
      l1 = paramLong;
    }
  }
  
  public long getFirstSampleTimestampUs()
  {
    return this.firstSampleTimestampUs;
  }
  
  public long getLastAdjustedTimestampUs()
  {
    long l = -9223372036854775807L;
    if (this.lastSampleTimestamp != -9223372036854775807L) {
      l = this.lastSampleTimestamp;
    }
    for (;;)
    {
      return l;
      if (this.firstSampleTimestampUs != Long.MAX_VALUE) {
        l = this.firstSampleTimestampUs;
      }
    }
  }
  
  public long getTimestampOffsetUs()
  {
    long l = -9223372036854775807L;
    if (this.firstSampleTimestampUs == Long.MAX_VALUE) {
      l = 0L;
    }
    for (;;)
    {
      return l;
      if (this.lastSampleTimestamp != -9223372036854775807L) {
        l = this.timestampOffsetUs;
      }
    }
  }
  
  public void reset()
  {
    this.lastSampleTimestamp = -9223372036854775807L;
  }
  
  /* Error */
  public void setFirstSampleTimestampUs(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 23	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:lastSampleTimestamp	J
    //   6: ldc2_w 20
    //   9: lcmp
    //   10: ifne +17 -> 27
    //   13: iconst_1
    //   14: istore_3
    //   15: iload_3
    //   16: invokestatic 67	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(Z)V
    //   19: aload_0
    //   20: lload_1
    //   21: putfield 39	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:firstSampleTimestampUs	J
    //   24: aload_0
    //   25: monitorexit
    //   26: return
    //   27: iconst_0
    //   28: istore_3
    //   29: goto -14 -> 15
    //   32: astore 4
    //   34: aload_0
    //   35: monitorexit
    //   36: aload 4
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	TimestampAdjuster
    //   0	39	1	paramLong	long
    //   14	15	3	bool	boolean
    //   32	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	13	32	finally
    //   15	24	32	finally
  }
  
  /* Error */
  public void waitUntilInitialized()
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 23	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:lastSampleTimestamp	J
    //   6: ldc2_w 20
    //   9: lcmp
    //   10: ifne +15 -> 25
    //   13: aload_0
    //   14: invokevirtual 73	java/lang/Object:wait	()V
    //   17: goto -15 -> 2
    //   20: astore_1
    //   21: aload_0
    //   22: monitorexit
    //   23: aload_1
    //   24: athrow
    //   25: aload_0
    //   26: monitorexit
    //   27: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	TimestampAdjuster
    //   20	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	17	20	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/TimestampAdjuster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */