package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DummyTrackOutput
  implements TrackOutput
{
  public void format(Format paramFormat) {}
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    int i = paramExtractorInput.skip(paramInt);
    paramInt = i;
    if (i == -1)
    {
      if (paramBoolean) {
        paramInt = -1;
      }
    }
    else {
      return paramInt;
    }
    throw new EOFException();
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(paramInt);
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, TrackOutput.CryptoData paramCryptoData) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/DummyTrackOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */