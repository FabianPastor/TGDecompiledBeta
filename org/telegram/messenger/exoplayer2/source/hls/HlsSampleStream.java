package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;

final class HlsSampleStream
  implements SampleStream
{
  private int sampleQueueIndex;
  private final HlsSampleStreamWrapper sampleStreamWrapper;
  private final int trackGroupIndex;
  
  public HlsSampleStream(HlsSampleStreamWrapper paramHlsSampleStreamWrapper, int paramInt)
  {
    this.sampleStreamWrapper = paramHlsSampleStreamWrapper;
    this.trackGroupIndex = paramInt;
    this.sampleQueueIndex = -1;
  }
  
  private boolean ensureBoundSampleQueue()
  {
    boolean bool = true;
    if (this.sampleQueueIndex != -1) {}
    for (;;)
    {
      return bool;
      this.sampleQueueIndex = this.sampleStreamWrapper.bindSampleQueueToSampleStream(this.trackGroupIndex);
      if (this.sampleQueueIndex == -1) {
        bool = false;
      }
    }
  }
  
  public boolean isReady()
  {
    if ((ensureBoundSampleQueue()) && (this.sampleStreamWrapper.isReady(this.sampleQueueIndex))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if ((!ensureBoundSampleQueue()) && (this.sampleStreamWrapper.isMappingFinished())) {
      throw new SampleQueueMappingException(this.sampleStreamWrapper.getTrackGroups().get(this.trackGroupIndex).getFormat(0).sampleMimeType);
    }
    this.sampleStreamWrapper.maybeThrowError();
  }
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    if (!ensureBoundSampleQueue()) {}
    for (int i = -3;; i = this.sampleStreamWrapper.readData(this.sampleQueueIndex, paramFormatHolder, paramDecoderInputBuffer, paramBoolean)) {
      return i;
    }
  }
  
  public int skipData(long paramLong)
  {
    if (!ensureBoundSampleQueue()) {}
    for (int i = 0;; i = this.sampleStreamWrapper.skipData(this.sampleQueueIndex, paramLong)) {
      return i;
    }
  }
  
  public void unbindSampleQueue()
  {
    if (this.sampleQueueIndex != -1)
    {
      this.sampleStreamWrapper.unbindSampleQueue(this.trackGroupIndex);
      this.sampleQueueIndex = -1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsSampleStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */