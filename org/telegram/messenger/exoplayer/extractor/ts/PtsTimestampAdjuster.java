package org.telegram.messenger.exoplayer.extractor.ts;

public final class PtsTimestampAdjuster
{
  public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
  private static final long MAX_PTS_PLUS_ONE = 8589934592L;
  private final long firstSampleTimestampUs;
  private volatile long lastPts;
  private long timestampOffsetUs;
  
  public PtsTimestampAdjuster(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.lastPts = Long.MIN_VALUE;
  }
  
  public static long ptsToUs(long paramLong)
  {
    return 1000000L * paramLong / 90000L;
  }
  
  public static long usToPts(long paramLong)
  {
    return 90000L * paramLong / 1000000L;
  }
  
  public long adjustTimestamp(long paramLong)
  {
    long l1 = paramLong;
    if (this.lastPts != Long.MIN_VALUE)
    {
      long l2 = (this.lastPts + 4294967296L) / 8589934592L;
      l1 = paramLong + 8589934592L * (l2 - 1L);
      paramLong += 8589934592L * l2;
      if (Math.abs(l1 - this.lastPts) >= Math.abs(paramLong - this.lastPts)) {
        break label118;
      }
    }
    for (;;)
    {
      paramLong = ptsToUs(l1);
      if ((this.firstSampleTimestampUs != Long.MAX_VALUE) && (this.lastPts == Long.MIN_VALUE)) {
        this.timestampOffsetUs = (this.firstSampleTimestampUs - paramLong);
      }
      this.lastPts = l1;
      return this.timestampOffsetUs + paramLong;
      label118:
      l1 = paramLong;
    }
  }
  
  public boolean isInitialized()
  {
    return this.lastPts != Long.MIN_VALUE;
  }
  
  public void reset()
  {
    this.lastPts = Long.MIN_VALUE;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/PtsTimestampAdjuster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */