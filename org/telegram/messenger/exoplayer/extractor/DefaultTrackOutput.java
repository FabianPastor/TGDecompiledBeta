package org.telegram.messenger.exoplayer.extractor;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public class DefaultTrackOutput
  implements TrackOutput
{
  private volatile MediaFormat format;
  private volatile long largestParsedTimestampUs;
  private long lastReadTimeUs;
  private boolean needKeyframe;
  private final RollingSampleBuffer rollingBuffer;
  private final SampleHolder sampleInfoHolder;
  private long spliceOutTimeUs;
  
  public DefaultTrackOutput(Allocator paramAllocator)
  {
    this.rollingBuffer = new RollingSampleBuffer(paramAllocator);
    this.sampleInfoHolder = new SampleHolder(0);
    this.needKeyframe = true;
    this.lastReadTimeUs = Long.MIN_VALUE;
    this.spliceOutTimeUs = Long.MIN_VALUE;
    this.largestParsedTimestampUs = Long.MIN_VALUE;
  }
  
  private boolean advanceToEligibleSample()
  {
    boolean bool1 = this.rollingBuffer.peekSample(this.sampleInfoHolder);
    boolean bool2 = bool1;
    if (this.needKeyframe) {
      for (;;)
      {
        bool2 = bool1;
        if (!bool1) {
          break;
        }
        bool2 = bool1;
        if (this.sampleInfoHolder.isSyncFrame()) {
          break;
        }
        this.rollingBuffer.skipSample();
        bool1 = this.rollingBuffer.peekSample(this.sampleInfoHolder);
      }
    }
    if (!bool2) {}
    while ((this.spliceOutTimeUs != Long.MIN_VALUE) && (this.sampleInfoHolder.timeUs >= this.spliceOutTimeUs)) {
      return false;
    }
    return true;
  }
  
  public void clear()
  {
    this.rollingBuffer.clear();
    this.needKeyframe = true;
    this.lastReadTimeUs = Long.MIN_VALUE;
    this.spliceOutTimeUs = Long.MIN_VALUE;
    this.largestParsedTimestampUs = Long.MIN_VALUE;
  }
  
  public boolean configureSpliceTo(DefaultTrackOutput paramDefaultTrackOutput)
  {
    if (this.spliceOutTimeUs != Long.MIN_VALUE) {
      return true;
    }
    if (this.rollingBuffer.peekSample(this.sampleInfoHolder)) {}
    for (long l = this.sampleInfoHolder.timeUs;; l = this.lastReadTimeUs + 1L)
    {
      paramDefaultTrackOutput = paramDefaultTrackOutput.rollingBuffer;
      while ((paramDefaultTrackOutput.peekSample(this.sampleInfoHolder)) && ((this.sampleInfoHolder.timeUs < l) || (!this.sampleInfoHolder.isSyncFrame()))) {
        paramDefaultTrackOutput.skipSample();
      }
    }
    if (paramDefaultTrackOutput.peekSample(this.sampleInfoHolder))
    {
      this.spliceOutTimeUs = this.sampleInfoHolder.timeUs;
      return true;
    }
    return false;
  }
  
  public void discardUntil(long paramLong)
  {
    while ((this.rollingBuffer.peekSample(this.sampleInfoHolder)) && (this.sampleInfoHolder.timeUs < paramLong))
    {
      this.rollingBuffer.skipSample();
      this.needKeyframe = true;
    }
    this.lastReadTimeUs = Long.MIN_VALUE;
  }
  
  public void discardUpstreamSamples(int paramInt)
  {
    this.rollingBuffer.discardUpstreamSamples(paramInt);
    if (this.rollingBuffer.peekSample(this.sampleInfoHolder)) {}
    for (long l = this.sampleInfoHolder.timeUs;; l = Long.MIN_VALUE)
    {
      this.largestParsedTimestampUs = l;
      return;
    }
  }
  
  public void format(MediaFormat paramMediaFormat)
  {
    this.format = paramMediaFormat;
  }
  
  public MediaFormat getFormat()
  {
    return this.format;
  }
  
  public long getLargestParsedTimestampUs()
  {
    return this.largestParsedTimestampUs;
  }
  
  public int getReadIndex()
  {
    return this.rollingBuffer.getReadIndex();
  }
  
  public boolean getSample(SampleHolder paramSampleHolder)
  {
    if (!advanceToEligibleSample()) {
      return false;
    }
    this.rollingBuffer.readSample(paramSampleHolder);
    this.needKeyframe = false;
    this.lastReadTimeUs = paramSampleHolder.timeUs;
    return true;
  }
  
  public int getWriteIndex()
  {
    return this.rollingBuffer.getWriteIndex();
  }
  
  public boolean hasFormat()
  {
    return this.format != null;
  }
  
  public boolean isEmpty()
  {
    return !advanceToEligibleSample();
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    return this.rollingBuffer.appendData(paramExtractorInput, paramInt, paramBoolean);
  }
  
  public int sampleData(DataSource paramDataSource, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return this.rollingBuffer.appendData(paramDataSource, paramInt, paramBoolean);
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    this.rollingBuffer.appendData(paramParsableByteArray, paramInt);
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.largestParsedTimestampUs = Math.max(this.largestParsedTimestampUs, paramLong);
    this.rollingBuffer.commitSample(paramLong, paramInt1, this.rollingBuffer.getWritePosition() - paramInt2 - paramInt3, paramInt2, paramArrayOfByte);
  }
  
  public boolean skipToKeyframeBefore(long paramLong)
  {
    return this.rollingBuffer.skipToKeyframeBefore(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/DefaultTrackOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */