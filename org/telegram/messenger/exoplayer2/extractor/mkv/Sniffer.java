package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class Sniffer
{
  private static final int ID_EBML = 440786851;
  private static final int SEARCH_LENGTH = 1024;
  private int peekLength;
  private final ParsableByteArray scratch = new ParsableByteArray(8);
  
  private long readUint(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.peekFully(this.scratch.data, 0, 1);
    int i = this.scratch.data[0] & 0xFF;
    if (i == 0) {}
    int j;
    for (long l = Long.MIN_VALUE;; l = j)
    {
      return l;
      j = 128;
      for (int k = 0; (i & j) == 0; k++) {
        j >>= 1;
      }
      j = i & (j ^ 0xFFFFFFFF);
      paramExtractorInput.peekFully(this.scratch.data, 1, k);
      for (i = 0; i < k; i++) {
        j = (j << 8) + (this.scratch.data[(i + 1)] & 0xFF);
      }
      this.peekLength += k + 1;
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    long l1 = paramExtractorInput.getLength();
    long l2;
    label62:
    boolean bool;
    if ((l1 == -1L) || (l1 > 1024L))
    {
      l2 = 1024L;
      int i = (int)l2;
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      l2 = this.scratch.readUnsignedInt();
      this.peekLength = 4;
      if (l2 == 440786851L) {
        break label148;
      }
      int j = this.peekLength + 1;
      this.peekLength = j;
      if (j != i) {
        break label104;
      }
      bool = false;
    }
    for (;;)
    {
      return bool;
      l2 = l1;
      break;
      label104:
      paramExtractorInput.peekFully(this.scratch.data, 0, 1);
      l2 = l2 << 8 & 0xFFFFFFFFFFFFFF00 | this.scratch.data[0] & 0xFF;
      break label62;
      label148:
      l2 = readUint(paramExtractorInput);
      long l3 = this.peekLength;
      if ((l2 == Long.MIN_VALUE) || ((l1 != -1L) && (l3 + l2 >= l1)))
      {
        bool = false;
      }
      else
      {
        do
        {
          if (l1 != 0L)
          {
            paramExtractorInput.advancePeekPosition((int)l1);
            this.peekLength = ((int)(this.peekLength + l1));
          }
          if (this.peekLength >= l3 + l2) {
            break label279;
          }
          if (readUint(paramExtractorInput) == Long.MIN_VALUE)
          {
            bool = false;
            break;
          }
          l1 = readUint(paramExtractorInput);
        } while ((l1 >= 0L) && (l1 <= 2147483647L));
        bool = false;
        continue;
        label279:
        if (this.peekLength == l3 + l2) {
          bool = true;
        } else {
          bool = false;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mkv/Sniffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */