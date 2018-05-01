package org.telegram.messenger.exoplayer2.extractor.ts;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Ac3Extractor
  implements Extractor
{
  private static final int AC3_SYNC_WORD = 2935;
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Ac3Extractor() };
    }
  };
  private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int MAX_SNIFF_BYTES = 8192;
  private static final int MAX_SYNC_FRAME_SIZE = 2786;
  private final long firstSampleTimestampUs;
  private final Ac3Reader reader;
  private final ParsableByteArray sampleData;
  private boolean startedPacket;
  
  public Ac3Extractor()
  {
    this(0L);
  }
  
  public Ac3Extractor(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.reader = new Ac3Reader();
    this.sampleData = new ParsableByteArray(2786);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.reader.createTracks(paramExtractorOutput, new TsPayloadReader.TrackIdGenerator(0, 1));
    paramExtractorOutput.endTracks();
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = -1;
    int j = paramExtractorInput.read(this.sampleData.data, 0, 2786);
    if (j == -1) {}
    for (;;)
    {
      return i;
      this.sampleData.setPosition(0);
      this.sampleData.setLimit(j);
      if (!this.startedPacket)
      {
        this.reader.packetStarted(this.firstSampleTimestampUs, true);
        this.startedPacket = true;
      }
      this.reader.consume(this.sampleData);
      i = 0;
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.startedPacket = false;
    this.reader.seek();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool1 = false;
    ParsableByteArray localParsableByteArray = new ParsableByteArray(10);
    int i = 0;
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, 10);
    localParsableByteArray.setPosition(0);
    int j;
    int k;
    if (localParsableByteArray.readUnsignedInt24() != ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      j = i;
      k = 0;
    }
    for (;;)
    {
      label64:
      paramExtractorInput.peekFully(localParsableByteArray.data, 0, 5);
      localParsableByteArray.setPosition(0);
      boolean bool2;
      if (localParsableByteArray.readUnsignedShort() != 2935)
      {
        k = 0;
        paramExtractorInput.resetPeekPosition();
        j++;
        if (j - i >= 8192) {
          bool2 = bool1;
        }
      }
      label178:
      int m;
      do
      {
        for (;;)
        {
          return bool2;
          localParsableByteArray.skipBytes(3);
          k = localParsableByteArray.readSynchSafeInt();
          i += k + 10;
          paramExtractorInput.advancePeekPosition(k);
          break;
          paramExtractorInput.advancePeekPosition(j);
          break label64;
          k++;
          if (k < 4) {
            break label178;
          }
          bool2 = true;
        }
        m = Ac3Util.parseAc3SyncframeSize(localParsableByteArray.data);
        bool2 = bool1;
      } while (m == -1);
      paramExtractorInput.advancePeekPosition(m - 5);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/Ac3Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */