package org.telegram.messenger.exoplayer2.extractor.wav;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;

public final class WavExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new WavExtractor() };
    }
  };
  private static final int MAX_INPUT_SIZE = 32768;
  private int bytesPerFrame;
  private ExtractorOutput extractorOutput;
  private int pendingBytes;
  private TrackOutput trackOutput;
  private WavHeader wavHeader;
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramExtractorOutput.track(0, 1);
    this.wavHeader = null;
    paramExtractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (this.wavHeader == null)
    {
      this.wavHeader = WavHeaderReader.peek(paramExtractorInput);
      if (this.wavHeader == null) {
        throw new ParserException("Unsupported or unrecognized wav header.");
      }
      paramPositionHolder = Format.createAudioSampleFormat(null, "audio/raw", null, this.wavHeader.getBitrate(), 32768, this.wavHeader.getNumChannels(), this.wavHeader.getSampleRateHz(), this.wavHeader.getEncoding(), null, null, 0, null);
      this.trackOutput.format(paramPositionHolder);
      this.bytesPerFrame = this.wavHeader.getBytesPerFrame();
    }
    if (!this.wavHeader.hasDataBounds())
    {
      WavHeaderReader.skipToData(paramExtractorInput, this.wavHeader);
      this.extractorOutput.seekMap(this.wavHeader);
    }
    int i = this.trackOutput.sampleData(paramExtractorInput, 32768 - this.pendingBytes, true);
    if (i != -1) {
      this.pendingBytes += i;
    }
    int j = this.pendingBytes / this.bytesPerFrame;
    if (j > 0)
    {
      long l = this.wavHeader.getTimeUs(paramExtractorInput.getPosition() - this.pendingBytes);
      j *= this.bytesPerFrame;
      this.pendingBytes -= j;
      this.trackOutput.sampleMetadata(l, 1, j, this.pendingBytes, null);
    }
    if (i == -1) {}
    for (i = -1;; i = 0) {
      return i;
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.pendingBytes = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (WavHeaderReader.peek(paramExtractorInput) != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/wav/WavExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */