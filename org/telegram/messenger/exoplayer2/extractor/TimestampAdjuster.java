package org.telegram.messenger.exoplayer2.extractor;

public final class TimestampAdjuster
{
  public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
  private static final long MAX_PTS_PLUS_ONE = 8589934592L;
  private final long firstSampleTimestampUs;
  private volatile long lastSampleTimestamp;
  private long timestampOffsetUs;
  
  public TimestampAdjuster(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.lastSampleTimestamp = -9223372036854775807L;
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
    if (this.lastSampleTimestamp != -9223372036854775807L) {
      this.lastSampleTimestamp = paramLong;
    }
    for (;;)
    {
      return this.timestampOffsetUs + paramLong;
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
    long l1 = paramLong;
    if (this.lastSampleTimestamp != -9223372036854775807L)
    {
      long l2 = usToPts(this.lastSampleTimestamp);
      long l3 = (4294967296L + l2) / 8589934592L;
      l1 = paramLong + 8589934592L * (l3 - 1L);
      paramLong += 8589934592L * l3;
      if (Math.abs(l1 - l2) >= Math.abs(paramLong - l2)) {
        break label81;
      }
    }
    for (;;)
    {
      return adjustSampleTimestamp(ptsToUs(l1));
      label81:
      l1 = paramLong;
    }
  }
  
  public void reset()
  {
    this.lastSampleTimestamp = -9223372036854775807L;
  }
  
  /* Error */
  public void waitUntilInitialized()
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 25	org/telegram/messenger/exoplayer2/extractor/TimestampAdjuster:lastSampleTimestamp	J
    //   6: ldc2_w 22
    //   9: lcmp
    //   10: ifne +15 -> 25
    //   13: aload_0
    //   14: invokevirtual 60	java/lang/Object:wait	()V
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/TimestampAdjuster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */