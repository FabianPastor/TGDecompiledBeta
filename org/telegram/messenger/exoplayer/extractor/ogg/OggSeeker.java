package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class OggSeeker
{
  private static final int MATCH_RANGE = 72000;
  private long audioDataLength = -1L;
  private final ParsableByteArray headerArray = new ParsableByteArray(282);
  private final OggUtil.PageHeader pageHeader = new OggUtil.PageHeader();
  private long totalSamples;
  
  public long getNextSeekPosition(long paramLong, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool;
    int j;
    int k;
    if ((this.audioDataLength != -1L) && (this.totalSamples != 0L))
    {
      bool = true;
      Assertions.checkState(bool);
      OggUtil.populatePageHeader(paramExtractorInput, this.pageHeader, this.headerArray, false);
      paramLong -= this.pageHeader.granulePosition;
      if ((paramLong > 0L) && (paramLong <= 72000L)) {
        break label138;
      }
      j = this.pageHeader.bodySize;
      k = this.pageHeader.headerSize;
      if (paramLong > 0L) {
        break label132;
      }
    }
    label132:
    for (int i = 2;; i = 1)
    {
      long l = i * (k + j);
      return paramExtractorInput.getPosition() - l + this.audioDataLength * paramLong / this.totalSamples;
      bool = false;
      break;
    }
    label138:
    paramExtractorInput.resetPeekPosition();
    return -1L;
  }
  
  public void setup(long paramLong1, long paramLong2)
  {
    if ((paramLong1 > 0L) && (paramLong2 > 0L)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.audioDataLength = paramLong1;
      this.totalSamples = paramLong2;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/OggSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */