package org.telegram.messenger.exoplayer;

import java.io.IOException;
import java.util.Arrays;

public abstract class SampleSourceTrackRenderer
  extends TrackRenderer
{
  private long durationUs;
  private SampleSource.SampleSourceReader enabledSource;
  private int enabledSourceTrackIndex;
  private int[] handledSourceIndices;
  private int[] handledSourceTrackIndices;
  private final SampleSource.SampleSourceReader[] sources;
  
  public SampleSourceTrackRenderer(SampleSource... paramVarArgs)
  {
    this.sources = new SampleSource.SampleSourceReader[paramVarArgs.length];
    int i = 0;
    while (i < paramVarArgs.length)
    {
      this.sources[i] = paramVarArgs[i].register();
      i += 1;
    }
  }
  
  private long checkForDiscontinuity(long paramLong)
    throws ExoPlaybackException
  {
    long l = this.enabledSource.readDiscontinuity(this.enabledSourceTrackIndex);
    if (l != Long.MIN_VALUE)
    {
      onDiscontinuity(l);
      return l;
    }
    return paramLong;
  }
  
  private void maybeThrowError(SampleSource.SampleSourceReader paramSampleSourceReader)
    throws ExoPlaybackException
  {
    try
    {
      paramSampleSourceReader.maybeThrowError();
      return;
    }
    catch (IOException paramSampleSourceReader)
    {
      throw new ExoPlaybackException(paramSampleSourceReader);
    }
  }
  
  protected final boolean doPrepare(long paramLong)
    throws ExoPlaybackException
  {
    int j = 1;
    int i = 0;
    while (i < this.sources.length)
    {
      j &= this.sources[i].prepare(paramLong);
      i += 1;
    }
    if (j == 0) {
      return false;
    }
    i = 0;
    j = 0;
    while (j < this.sources.length)
    {
      i += this.sources[j].getTrackCount();
      j += 1;
    }
    long l1 = 0L;
    int k = 0;
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int i1 = this.sources.length;
    i = 0;
    while (i < i1)
    {
      SampleSource.SampleSourceReader localSampleSourceReader = this.sources[i];
      int i2 = localSampleSourceReader.getTrackCount();
      int m = 0;
      if (m < i2)
      {
        MediaFormat localMediaFormat = localSampleSourceReader.getFormat(m);
        for (;;)
        {
          int n;
          try
          {
            boolean bool = handlesTrack(localMediaFormat);
            paramLong = l1;
            n = k;
            if (bool)
            {
              arrayOfInt1[k] = i;
              arrayOfInt2[k] = m;
              k += 1;
              if (l1 == -1L)
              {
                n = k;
                paramLong = l1;
              }
            }
            else
            {
              m += 1;
              l1 = paramLong;
              k = n;
            }
          }
          catch (MediaCodecUtil.DecoderQueryException localDecoderQueryException)
          {
            throw new ExoPlaybackException(localDecoderQueryException);
          }
          long l2 = localMediaFormat.durationUs;
          if (l2 == -1L)
          {
            paramLong = -1L;
            n = k;
          }
          else
          {
            paramLong = l1;
            n = k;
            if (l2 != -2L)
            {
              paramLong = Math.max(l1, l2);
              n = k;
            }
          }
        }
      }
      i += 1;
    }
    this.durationUs = l1;
    this.handledSourceIndices = Arrays.copyOf(localDecoderQueryException, k);
    this.handledSourceTrackIndices = Arrays.copyOf(arrayOfInt2, k);
    return true;
  }
  
  protected final void doSomeWork(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    paramLong1 = shiftInputPosition(paramLong1);
    boolean bool = this.enabledSource.continueBuffering(this.enabledSourceTrackIndex, paramLong1);
    doSomeWork(checkForDiscontinuity(paramLong1), paramLong2, bool);
  }
  
  protected abstract void doSomeWork(long paramLong1, long paramLong2, boolean paramBoolean)
    throws ExoPlaybackException;
  
  protected long getBufferedPositionUs()
  {
    return this.enabledSource.getBufferedPositionUs();
  }
  
  protected long getDurationUs()
  {
    return this.durationUs;
  }
  
  protected final MediaFormat getFormat(int paramInt)
  {
    return this.sources[this.handledSourceIndices[paramInt]].getFormat(this.handledSourceTrackIndices[paramInt]);
  }
  
  protected final int getTrackCount()
  {
    return this.handledSourceTrackIndices.length;
  }
  
  protected abstract boolean handlesTrack(MediaFormat paramMediaFormat)
    throws MediaCodecUtil.DecoderQueryException;
  
  protected void maybeThrowError()
    throws ExoPlaybackException
  {
    if (this.enabledSource != null) {
      maybeThrowError(this.enabledSource);
    }
    for (;;)
    {
      return;
      int j = this.sources.length;
      int i = 0;
      while (i < j)
      {
        maybeThrowError(this.sources[i]);
        i += 1;
      }
    }
  }
  
  protected void onDisabled()
    throws ExoPlaybackException
  {
    this.enabledSource.disable(this.enabledSourceTrackIndex);
    this.enabledSource = null;
  }
  
  protected abstract void onDiscontinuity(long paramLong)
    throws ExoPlaybackException;
  
  protected void onEnabled(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    paramLong = shiftInputPosition(paramLong);
    this.enabledSource = this.sources[this.handledSourceIndices[paramInt]];
    this.enabledSourceTrackIndex = this.handledSourceTrackIndices[paramInt];
    this.enabledSource.enable(this.enabledSourceTrackIndex, paramLong);
    onDiscontinuity(paramLong);
  }
  
  protected void onReleased()
    throws ExoPlaybackException
  {
    int j = this.sources.length;
    int i = 0;
    while (i < j)
    {
      this.sources[i].release();
      i += 1;
    }
  }
  
  protected final int readSource(long paramLong, MediaFormatHolder paramMediaFormatHolder, SampleHolder paramSampleHolder)
  {
    return this.enabledSource.readData(this.enabledSourceTrackIndex, paramLong, paramMediaFormatHolder, paramSampleHolder);
  }
  
  protected final void seekTo(long paramLong)
    throws ExoPlaybackException
  {
    paramLong = shiftInputPosition(paramLong);
    this.enabledSource.seekToUs(paramLong);
    checkForDiscontinuity(paramLong);
  }
  
  protected long shiftInputPosition(long paramLong)
  {
    return paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/SampleSourceTrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */