package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class OggPacket
{
  private int currentSegmentIndex = -1;
  private final ParsableByteArray packetArray = new ParsableByteArray(new byte[65025], 0);
  private final OggPageHeader pageHeader = new OggPageHeader();
  private boolean populated;
  private int segmentCount;
  
  private int calculatePacketSize(int paramInt)
  {
    this.segmentCount = 0;
    int i = 0;
    int j;
    int k;
    do
    {
      j = i;
      if (this.segmentCount + paramInt >= this.pageHeader.pageSegmentCount) {
        break;
      }
      int[] arrayOfInt = this.pageHeader.laces;
      j = this.segmentCount;
      this.segmentCount = (j + 1);
      k = arrayOfInt[(j + paramInt)];
      j = i + k;
      i = j;
    } while (k == 255);
    return j;
  }
  
  public OggPageHeader getPageHeader()
  {
    return this.pageHeader;
  }
  
  public ParsableByteArray getPayload()
  {
    return this.packetArray;
  }
  
  public boolean populate(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool1 = false;
    if (paramExtractorInput != null)
    {
      bool2 = true;
      Assertions.checkState(bool2);
      if (this.populated)
      {
        this.populated = false;
        this.packetArray.reset();
      }
      if (this.populated) {
        break label319;
      }
      if (this.currentSegmentIndex >= 0) {
        break label149;
      }
      if (this.pageHeader.populate(paramExtractorInput, true)) {
        break label66;
      }
    }
    label66:
    label149:
    label314:
    label319:
    for (boolean bool2 = bool1;; bool2 = true)
    {
      return bool2;
      bool2 = false;
      break;
      int i = 0;
      int j = this.pageHeader.headerSize;
      int k = j;
      int m = i;
      if ((this.pageHeader.type & 0x1) == 1)
      {
        k = j;
        m = i;
        if (this.packetArray.limit() == 0)
        {
          k = j + calculatePacketSize(0);
          m = 0 + this.segmentCount;
        }
      }
      paramExtractorInput.skipFully(k);
      this.currentSegmentIndex = m;
      m = calculatePacketSize(this.currentSegmentIndex);
      k = this.currentSegmentIndex + this.segmentCount;
      if (m > 0)
      {
        if (this.packetArray.capacity() < this.packetArray.limit() + m) {
          this.packetArray.data = Arrays.copyOf(this.packetArray.data, this.packetArray.limit() + m);
        }
        paramExtractorInput.readFully(this.packetArray.data, this.packetArray.limit(), m);
        this.packetArray.setLimit(this.packetArray.limit() + m);
        if (this.pageHeader.laces[(k - 1)] == 255) {
          break label314;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        this.populated = bool2;
        m = k;
        if (k == this.pageHeader.pageSegmentCount) {
          m = -1;
        }
        this.currentSegmentIndex = m;
        break;
      }
    }
  }
  
  public void reset()
  {
    this.pageHeader.reset();
    this.packetArray.reset();
    this.currentSegmentIndex = -1;
    this.populated = false;
  }
  
  public void trimPayload()
  {
    if (this.packetArray.data.length == 65025) {}
    for (;;)
    {
      return;
      this.packetArray.data = Arrays.copyOf(this.packetArray.data, Math.max(65025, this.packetArray.limit()));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/OggPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */