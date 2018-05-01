package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public class OggExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new OggExtractor() };
    }
  };
  private static final int MAX_VERIFICATION_BYTES = 8;
  private ExtractorOutput output;
  private StreamReader streamReader;
  private boolean streamReaderInitialized;
  
  private static ParsableByteArray resetPosition(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(0);
    return paramParsableByteArray;
  }
  
  private boolean sniffInternal(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool1 = false;
    Object localObject = new OggPageHeader();
    boolean bool2 = bool1;
    if (((OggPageHeader)localObject).populate(paramExtractorInput, true))
    {
      if ((((OggPageHeader)localObject).type & 0x2) != 2) {
        bool2 = bool1;
      }
    }
    else {
      return bool2;
    }
    int i = Math.min(((OggPageHeader)localObject).bodySize, 8);
    localObject = new ParsableByteArray(i);
    paramExtractorInput.peekFully(((ParsableByteArray)localObject).data, 0, i);
    if (FlacReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject))) {
      this.streamReader = new FlacReader();
    }
    for (;;)
    {
      bool2 = true;
      break;
      if (VorbisReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
      {
        this.streamReader = new VorbisReader();
      }
      else
      {
        bool2 = bool1;
        if (!OpusReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject))) {
          break;
        }
        this.streamReader = new OpusReader();
      }
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (this.streamReader == null)
    {
      if (!sniffInternal(paramExtractorInput)) {
        throw new ParserException("Failed to determine bitstream type");
      }
      paramExtractorInput.resetPeekPosition();
    }
    if (!this.streamReaderInitialized)
    {
      TrackOutput localTrackOutput = this.output.track(0, 1);
      this.output.endTracks();
      this.streamReader.init(this.output, localTrackOutput);
      this.streamReaderInitialized = true;
    }
    return this.streamReader.read(paramExtractorInput, paramPositionHolder);
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    if (this.streamReader != null) {
      this.streamReader.seek(paramLong1, paramLong2);
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    try
    {
      bool = sniffInternal(paramExtractorInput);
      return bool;
    }
    catch (ParserException paramExtractorInput)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/OggExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */