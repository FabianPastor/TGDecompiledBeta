package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class OggParser
{
  public static final int OGG_MAX_SEGMENT_SIZE = 255;
  private int currentSegmentIndex = -1;
  private long elapsedSamples;
  private final ParsableByteArray headerArray = new ParsableByteArray(282);
  private final OggUtil.PacketInfoHolder holder = new OggUtil.PacketInfoHolder();
  private final OggUtil.PageHeader pageHeader = new OggUtil.PageHeader();
  
  public OggUtil.PageHeader getPageHeader()
  {
    return this.pageHeader;
  }
  
  public long readGranuleOfLastPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (paramExtractorInput.getLength() != -1L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      OggUtil.skipToNextPage(paramExtractorInput);
      this.pageHeader.reset();
      while (((this.pageHeader.type & 0x4) != 4) && (paramExtractorInput.getPosition() < paramExtractorInput.getLength()))
      {
        OggUtil.populatePageHeader(paramExtractorInput, this.pageHeader, this.headerArray, false);
        paramExtractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
      }
    }
    return this.pageHeader.granulePosition;
  }
  
  public boolean readPacket(ExtractorInput paramExtractorInput, ParsableByteArray paramParsableByteArray)
    throws IOException, InterruptedException
  {
    if ((paramExtractorInput != null) && (paramParsableByteArray != null)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      i = 0;
      if (i != 0) {
        break label279;
      }
      if (this.currentSegmentIndex >= 0) {
        break label150;
      }
      if (OggUtil.populatePageHeader(paramExtractorInput, this.pageHeader, this.headerArray, true)) {
        break;
      }
      return false;
    }
    int n = 0;
    int m = this.pageHeader.headerSize;
    int k = m;
    int j = n;
    if ((this.pageHeader.type & 0x1) == 1)
    {
      k = m;
      j = n;
      if (paramParsableByteArray.limit() == 0)
      {
        OggUtil.calculatePacketSize(this.pageHeader, 0, this.holder);
        j = 0 + this.holder.segmentCount;
        k = m + this.holder.size;
      }
    }
    paramExtractorInput.skipFully(k);
    this.currentSegmentIndex = j;
    label150:
    OggUtil.calculatePacketSize(this.pageHeader, this.currentSegmentIndex, this.holder);
    k = this.currentSegmentIndex + this.holder.segmentCount;
    if (this.holder.size > 0)
    {
      paramExtractorInput.readFully(paramParsableByteArray.data, paramParsableByteArray.limit(), this.holder.size);
      paramParsableByteArray.setLimit(paramParsableByteArray.limit() + this.holder.size);
      if (this.pageHeader.laces[(k - 1)] == 255) {
        break label274;
      }
    }
    label274:
    for (int i = 1;; i = 0)
    {
      j = k;
      if (k == this.pageHeader.pageSegmentCount) {
        j = -1;
      }
      this.currentSegmentIndex = j;
      break;
    }
    label279:
    return true;
  }
  
  public void reset()
  {
    this.pageHeader.reset();
    this.headerArray.reset();
    this.currentSegmentIndex = -1;
  }
  
  public long skipToPageOfGranule(ExtractorInput paramExtractorInput, long paramLong)
    throws IOException, InterruptedException
  {
    OggUtil.skipToNextPage(paramExtractorInput);
    OggUtil.populatePageHeader(paramExtractorInput, this.pageHeader, this.headerArray, false);
    while (this.pageHeader.granulePosition < paramLong)
    {
      paramExtractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
      this.elapsedSamples = this.pageHeader.granulePosition;
      OggUtil.populatePageHeader(paramExtractorInput, this.pageHeader, this.headerArray, false);
    }
    if (this.elapsedSamples == 0L) {
      throw new ParserException();
    }
    paramExtractorInput.resetPeekPosition();
    paramLong = this.elapsedSamples;
    this.elapsedSamples = 0L;
    this.currentSegmentIndex = -1;
    return paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/OggParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */