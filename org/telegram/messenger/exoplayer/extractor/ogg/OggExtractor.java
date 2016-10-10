package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public class OggExtractor
  implements Extractor
{
  private StreamReader streamReader;
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    TrackOutput localTrackOutput = paramExtractorOutput.track(0);
    paramExtractorOutput.endTracks();
    this.streamReader.init(paramExtractorOutput, localTrackOutput);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    return this.streamReader.read(paramExtractorInput, paramPositionHolder);
  }
  
  public void release() {}
  
  public void seek()
  {
    this.streamReader.seek();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    try
    {
      ParsableByteArray localParsableByteArray = new ParsableByteArray(new byte[27], 0);
      OggUtil.PageHeader localPageHeader = new OggUtil.PageHeader();
      if ((OggUtil.populatePageHeader(paramExtractorInput, localPageHeader, localParsableByteArray, true)) && ((localPageHeader.type & 0x2) == 2))
      {
        if (localPageHeader.bodySize < 7) {
          return false;
        }
        localParsableByteArray.reset();
        paramExtractorInput.peekFully(localParsableByteArray.data, 0, 7);
        if (FlacReader.verifyBitstreamType(localParsableByteArray))
        {
          this.streamReader = new FlacReader();
          break label123;
        }
        localParsableByteArray.reset();
        if (VorbisReader.verifyBitstreamType(localParsableByteArray))
        {
          this.streamReader = new VorbisReader();
          break label123;
        }
      }
    }
    catch (ParserException paramExtractorInput)
    {
      paramExtractorInput = paramExtractorInput;
      return false;
    }
    finally {}
    return false;
    label123:
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/OggExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */