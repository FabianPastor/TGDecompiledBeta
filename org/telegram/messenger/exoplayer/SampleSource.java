package org.telegram.messenger.exoplayer;

import java.io.IOException;

public abstract interface SampleSource
{
  public static final int END_OF_STREAM = -1;
  public static final int FORMAT_READ = -4;
  public static final int NOTHING_READ = -2;
  public static final long NO_DISCONTINUITY = Long.MIN_VALUE;
  public static final int SAMPLE_READ = -3;
  
  public abstract SampleSourceReader register();
  
  public static abstract interface SampleSourceReader
  {
    public abstract boolean continueBuffering(int paramInt, long paramLong);
    
    public abstract void disable(int paramInt);
    
    public abstract void enable(int paramInt, long paramLong);
    
    public abstract long getBufferedPositionUs();
    
    public abstract MediaFormat getFormat(int paramInt);
    
    public abstract int getTrackCount();
    
    public abstract void maybeThrowError()
      throws IOException;
    
    public abstract boolean prepare(long paramLong);
    
    public abstract int readData(int paramInt, long paramLong, MediaFormatHolder paramMediaFormatHolder, SampleHolder paramSampleHolder);
    
    public abstract long readDiscontinuity(int paramInt);
    
    public abstract void release();
    
    public abstract void seekToUs(long paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/SampleSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */