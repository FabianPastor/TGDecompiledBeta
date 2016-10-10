package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.text.eia608.Eia608Parser;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class SeiReader
{
  private final TrackOutput output;
  
  public SeiReader(TrackOutput paramTrackOutput)
  {
    this.output = paramTrackOutput;
    paramTrackOutput.format(MediaFormat.createTextFormat(null, "application/eia-608", -1, -1L, null));
  }
  
  public void consume(long paramLong, ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 1)
    {
      int i = 0;
      int k;
      int j;
      do
      {
        k = paramParsableByteArray.readUnsignedByte();
        j = i + k;
        i = j;
      } while (k == 255);
      i = 0;
      int m;
      do
      {
        m = paramParsableByteArray.readUnsignedByte();
        k = i + m;
        i = k;
      } while (m == 255);
      if (Eia608Parser.isSeiMessageEia608(j, k, paramParsableByteArray))
      {
        this.output.sampleData(paramParsableByteArray, k);
        this.output.sampleMetadata(paramLong, 1, k, 0, null);
      }
      else
      {
        paramParsableByteArray.skipBytes(k);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/SeiReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */