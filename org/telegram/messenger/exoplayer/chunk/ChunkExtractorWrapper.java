package org.telegram.messenger.exoplayer.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public class ChunkExtractorWrapper
  implements ExtractorOutput, TrackOutput
{
  private final Extractor extractor;
  private boolean extractorInitialized;
  private SingleTrackOutput output;
  private boolean seenTrack;
  
  public ChunkExtractorWrapper(Extractor paramExtractor)
  {
    this.extractor = paramExtractor;
  }
  
  public void drmInitData(DrmInitData paramDrmInitData)
  {
    this.output.drmInitData(paramDrmInitData);
  }
  
  public void endTracks()
  {
    Assertions.checkState(this.seenTrack);
  }
  
  public void format(MediaFormat paramMediaFormat)
  {
    this.output.format(paramMediaFormat);
  }
  
  public void init(SingleTrackOutput paramSingleTrackOutput)
  {
    this.output = paramSingleTrackOutput;
    if (!this.extractorInitialized)
    {
      this.extractor.init(this);
      this.extractorInitialized = true;
      return;
    }
    this.extractor.seek();
  }
  
  public int read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool = true;
    int i = this.extractor.read(paramExtractorInput, null);
    if (i != 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      return i;
      bool = false;
    }
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    return this.output.sampleData(paramExtractorInput, paramInt, paramBoolean);
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    this.output.sampleData(paramParsableByteArray, paramInt);
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.output.sampleMetadata(paramLong, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
  }
  
  public void seekMap(SeekMap paramSeekMap)
  {
    this.output.seekMap(paramSeekMap);
  }
  
  public TrackOutput track(int paramInt)
  {
    if (!this.seenTrack) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.seenTrack = true;
      return this;
    }
  }
  
  public static abstract interface SingleTrackOutput
    extends TrackOutput
  {
    public abstract void drmInitData(DrmInitData paramDrmInitData);
    
    public abstract void seekMap(SeekMap paramSeekMap);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/ChunkExtractorWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */