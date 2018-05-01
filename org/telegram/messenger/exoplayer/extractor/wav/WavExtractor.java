package org.telegram.messenger.exoplayer.extractor.wav;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;

public final class WavExtractor
  implements Extractor, SeekMap
{
  private static final int MAX_INPUT_SIZE = 32768;
  private int bytesPerFrame;
  private ExtractorOutput extractorOutput;
  private int pendingBytes;
  private TrackOutput trackOutput;
  private WavHeader wavHeader;
  
  public long getPosition(long paramLong)
  {
    return this.wavHeader.getPosition(paramLong);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramExtractorOutput.track(0);
    this.wavHeader = null;
    paramExtractorOutput.endTracks();
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (this.wavHeader == null)
    {
      this.wavHeader = WavHeaderReader.peek(paramExtractorInput);
      if (this.wavHeader == null) {
        throw new ParserException("Error initializing WavHeader. Did you sniff first?");
      }
      this.bytesPerFrame = this.wavHeader.getBytesPerFrame();
    }
    if (!this.wavHeader.hasDataBounds())
    {
      WavHeaderReader.skipToData(paramExtractorInput, this.wavHeader);
      this.trackOutput.format(MediaFormat.createAudioFormat(null, "audio/raw", this.wavHeader.getBitrate(), 32768, this.wavHeader.getDurationUs(), this.wavHeader.getNumChannels(), this.wavHeader.getSampleRateHz(), null, null, this.wavHeader.getEncoding()));
      this.extractorOutput.seekMap(this);
    }
    int i = this.trackOutput.sampleData(paramExtractorInput, 32768 - this.pendingBytes, true);
    if (i != -1) {
      this.pendingBytes += i;
    }
    int j = this.pendingBytes / this.bytesPerFrame * this.bytesPerFrame;
    if (j > 0)
    {
      long l1 = paramExtractorInput.getPosition();
      long l2 = this.pendingBytes;
      this.pendingBytes -= j;
      this.trackOutput.sampleMetadata(this.wavHeader.getTimeUs(l1 - l2), 1, j, this.pendingBytes, null);
    }
    if (i == -1) {
      return -1;
    }
    return 0;
  }
  
  public void release() {}
  
  public void seek()
  {
    this.pendingBytes = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return WavHeaderReader.peek(paramExtractorInput) != null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/wav/WavExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */