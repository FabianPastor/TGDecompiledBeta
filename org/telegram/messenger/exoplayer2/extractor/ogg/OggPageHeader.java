package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class OggPageHeader
{
  public static final int EMPTY_PAGE_HEADER_SIZE = 27;
  public static final int MAX_PAGE_PAYLOAD = 65025;
  public static final int MAX_PAGE_SIZE = 65307;
  public static final int MAX_SEGMENT_COUNT = 255;
  private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");
  public int bodySize;
  public long granulePosition;
  public int headerSize;
  public final int[] laces = new int['ÿ'];
  public long pageChecksum;
  public int pageSegmentCount;
  public long pageSequenceNumber;
  public int revision;
  private final ParsableByteArray scratch = new ParsableByteArray(255);
  public long streamSerialNumber;
  public int type;
  
  public boolean populate(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    boolean bool = false;
    this.scratch.reset();
    reset();
    int i;
    if ((paramExtractorInput.getLength() == -1L) || (paramExtractorInput.getLength() - paramExtractorInput.getPeekPosition() >= 27L))
    {
      i = 1;
      if ((i != 0) && (paramExtractorInput.peekFully(this.scratch.data, 0, 27, true))) {
        break label94;
      }
      if (!paramBoolean) {
        break label86;
      }
    }
    for (;;)
    {
      return bool;
      i = 0;
      break;
      label86:
      throw new EOFException();
      label94:
      if (this.scratch.readUnsignedInt() != TYPE_OGGS)
      {
        if (!paramBoolean) {
          throw new ParserException("expected OggS capture pattern at begin of page");
        }
      }
      else
      {
        this.revision = this.scratch.readUnsignedByte();
        if (this.revision != 0)
        {
          if (!paramBoolean) {
            throw new ParserException("unsupported bit stream revision");
          }
        }
        else
        {
          this.type = this.scratch.readUnsignedByte();
          this.granulePosition = this.scratch.readLittleEndianLong();
          this.streamSerialNumber = this.scratch.readLittleEndianUnsignedInt();
          this.pageSequenceNumber = this.scratch.readLittleEndianUnsignedInt();
          this.pageChecksum = this.scratch.readLittleEndianUnsignedInt();
          this.pageSegmentCount = this.scratch.readUnsignedByte();
          this.headerSize = (this.pageSegmentCount + 27);
          this.scratch.reset();
          paramExtractorInput.peekFully(this.scratch.data, 0, this.pageSegmentCount);
          for (i = 0; i < this.pageSegmentCount; i++)
          {
            this.laces[i] = this.scratch.readUnsignedByte();
            this.bodySize += this.laces[i];
          }
          bool = true;
        }
      }
    }
  }
  
  public void reset()
  {
    this.revision = 0;
    this.type = 0;
    this.granulePosition = 0L;
    this.streamSerialNumber = 0L;
    this.pageSequenceNumber = 0L;
    this.pageChecksum = 0L;
    this.pageSegmentCount = 0;
    this.headerSize = 0;
    this.bodySize = 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/OggPageHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */