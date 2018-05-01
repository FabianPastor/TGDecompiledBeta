package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

final class OggUtil
{
  public static final int PAGE_HEADER_SIZE = 27;
  private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");
  
  public static void calculatePacketSize(PageHeader paramPageHeader, int paramInt, PacketInfoHolder paramPacketInfoHolder)
  {
    paramPacketInfoHolder.segmentCount = 0;
    paramPacketInfoHolder.size = 0;
    int i;
    do
    {
      if (paramPacketInfoHolder.segmentCount + paramInt >= paramPageHeader.pageSegmentCount) {
        break;
      }
      int[] arrayOfInt = paramPageHeader.laces;
      i = paramPacketInfoHolder.segmentCount;
      paramPacketInfoHolder.segmentCount = (i + 1);
      i = arrayOfInt[(i + paramInt)];
      paramPacketInfoHolder.size += i;
    } while (i == 255);
  }
  
  public static boolean populatePageHeader(ExtractorInput paramExtractorInput, PageHeader paramPageHeader, ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    paramParsableByteArray.reset();
    paramPageHeader.reset();
    if ((paramExtractorInput.getLength() == -1L) || (paramExtractorInput.getLength() - paramExtractorInput.getPeekPosition() >= 27L))
    {
      i = 1;
      if ((i != 0) && (paramExtractorInput.peekFully(paramParsableByteArray.data, 0, 27, true))) {
        break label86;
      }
      if (!paramBoolean) {
        break label78;
      }
    }
    label78:
    label86:
    label112:
    do
    {
      do
      {
        return false;
        i = 0;
        break;
        throw new EOFException();
        if (paramParsableByteArray.readUnsignedInt() == TYPE_OGGS) {
          break label112;
        }
      } while (paramBoolean);
      throw new ParserException("expected OggS capture pattern at begin of page");
      paramPageHeader.revision = paramParsableByteArray.readUnsignedByte();
      if (paramPageHeader.revision == 0) {
        break label141;
      }
    } while (paramBoolean);
    throw new ParserException("unsupported bit stream revision");
    label141:
    paramPageHeader.type = paramParsableByteArray.readUnsignedByte();
    paramPageHeader.granulePosition = paramParsableByteArray.readLittleEndianLong();
    paramPageHeader.streamSerialNumber = paramParsableByteArray.readLittleEndianUnsignedInt();
    paramPageHeader.pageSequenceNumber = paramParsableByteArray.readLittleEndianUnsignedInt();
    paramPageHeader.pageChecksum = paramParsableByteArray.readLittleEndianUnsignedInt();
    paramPageHeader.pageSegmentCount = paramParsableByteArray.readUnsignedByte();
    paramParsableByteArray.reset();
    paramPageHeader.headerSize = (paramPageHeader.pageSegmentCount + 27);
    paramExtractorInput.peekFully(paramParsableByteArray.data, 0, paramPageHeader.pageSegmentCount);
    int i = 0;
    while (i < paramPageHeader.pageSegmentCount)
    {
      paramPageHeader.laces[i] = paramParsableByteArray.readUnsignedByte();
      paramPageHeader.bodySize += paramPageHeader.laces[i];
      i += 1;
    }
    return true;
  }
  
  public static int readBits(byte paramByte, int paramInt1, int paramInt2)
  {
    return paramByte >> paramInt2 & 255 >>> 8 - paramInt1;
  }
  
  public static void skipToNextPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = new byte['ࠀ'];
    int i;
    for (int j = arrayOfByte.length;; j = i)
    {
      i = j;
      if (paramExtractorInput.getLength() != -1L)
      {
        i = j;
        if (paramExtractorInput.getPosition() + j > paramExtractorInput.getLength())
        {
          j = (int)(paramExtractorInput.getLength() - paramExtractorInput.getPosition());
          i = j;
          if (j < 4) {
            throw new EOFException();
          }
        }
      }
      paramExtractorInput.peekFully(arrayOfByte, 0, i, false);
      j = 0;
      while (j < i - 3)
      {
        if ((arrayOfByte[j] == 79) && (arrayOfByte[(j + 1)] == 103) && (arrayOfByte[(j + 2)] == 103) && (arrayOfByte[(j + 3)] == 83))
        {
          paramExtractorInput.skipFully(j);
          return;
        }
        j += 1;
      }
      paramExtractorInput.skipFully(i - 3);
    }
  }
  
  public static class PacketInfoHolder
  {
    public int segmentCount;
    public int size;
  }
  
  public static final class PageHeader
  {
    public int bodySize;
    public long granulePosition;
    public int headerSize;
    public final int[] laces = new int['ÿ'];
    public long pageChecksum;
    public int pageSegmentCount;
    public long pageSequenceNumber;
    public int revision;
    public long streamSerialNumber;
    public int type;
    
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/OggUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */