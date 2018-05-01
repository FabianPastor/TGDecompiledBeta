package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class DefaultOggSeeker
  implements OggSeeker
{
  private static final int DEFAULT_OFFSET = 30000;
  public static final int MATCH_BYTE_RANGE = 100000;
  public static final int MATCH_RANGE = 72000;
  private static final int STATE_IDLE = 3;
  private static final int STATE_READ_LAST_PAGE = 1;
  private static final int STATE_SEEK = 2;
  private static final int STATE_SEEK_TO_END = 0;
  private long end;
  private long endGranule;
  private final long endPosition;
  private final OggPageHeader pageHeader = new OggPageHeader();
  private long positionBeforeSeekToEnd;
  private long start;
  private long startGranule;
  private final long startPosition;
  private int state;
  private final StreamReader streamReader;
  private long targetGranule;
  private long totalGranules;
  
  public DefaultOggSeeker(long paramLong1, long paramLong2, StreamReader paramStreamReader, int paramInt, long paramLong3)
  {
    boolean bool;
    if ((paramLong1 >= 0L) && (paramLong2 > paramLong1))
    {
      bool = true;
      Assertions.checkArgument(bool);
      this.streamReader = paramStreamReader;
      this.startPosition = paramLong1;
      this.endPosition = paramLong2;
      if (paramInt != paramLong2 - paramLong1) {
        break label79;
      }
      this.totalGranules = paramLong3;
    }
    label79:
    for (this.state = 3;; this.state = 0)
    {
      return;
      bool = false;
      break;
    }
  }
  
  private long getEstimatedPosition(long paramLong1, long paramLong2, long paramLong3)
  {
    paramLong2 = paramLong1 + ((this.endPosition - this.startPosition) * paramLong2 / this.totalGranules - paramLong3);
    paramLong1 = paramLong2;
    if (paramLong2 < this.startPosition) {
      paramLong1 = this.startPosition;
    }
    paramLong2 = paramLong1;
    if (paramLong1 >= this.endPosition) {
      paramLong2 = this.endPosition - 1L;
    }
    return paramLong2;
  }
  
  public OggSeekMap createSeekMap()
  {
    if (this.totalGranules != 0L) {}
    for (OggSeekMap localOggSeekMap = new OggSeekMap(null);; localOggSeekMap = null) {
      return localOggSeekMap;
    }
  }
  
  public long getNextSeekPosition(long paramLong, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.start == this.end) {
      paramLong = -(this.startGranule + 2L);
    }
    for (;;)
    {
      return paramLong;
      long l1 = paramExtractorInput.getPosition();
      if (!skipToNextPage(paramExtractorInput, this.end))
      {
        if (this.start == l1) {
          throw new IOException("No ogg page can be found.");
        }
        paramLong = this.start;
      }
      else
      {
        this.pageHeader.populate(paramExtractorInput, false);
        paramExtractorInput.resetPeekPosition();
        long l2 = paramLong - this.pageHeader.granulePosition;
        int i = this.pageHeader.headerSize + this.pageHeader.bodySize;
        if ((l2 < 0L) || (l2 > 72000L))
        {
          if (l2 < 0L)
          {
            this.end = l1;
            this.endGranule = this.pageHeader.granulePosition;
          }
          for (;;)
          {
            if (this.end - this.start < 100000L)
            {
              this.end = this.start;
              paramLong = this.start;
              break;
              this.start = (paramExtractorInput.getPosition() + i);
              this.startGranule = this.pageHeader.granulePosition;
              if (this.end - this.start + i < 100000L)
              {
                paramExtractorInput.skipFully(i);
                paramLong = -(this.startGranule + 2L);
                break;
              }
            }
          }
          l1 = i;
          if (l2 <= 0L) {}
          for (paramLong = 2L;; paramLong = 1L)
          {
            paramLong = Math.min(Math.max(paramExtractorInput.getPosition() - l1 * paramLong + (this.end - this.start) * l2 / (this.endGranule - this.startGranule), this.start), this.end - 1L);
            break;
          }
        }
        paramExtractorInput.skipFully(i);
        paramLong = -(this.pageHeader.granulePosition + 2L);
      }
    }
  }
  
  public long read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    switch (this.state)
    {
    default: 
      throw new IllegalStateException();
    case 3: 
      l1 = -1L;
    case 0: 
    case 1: 
      for (;;)
      {
        return l1;
        this.positionBeforeSeekToEnd = paramExtractorInput.getPosition();
        this.state = 1;
        long l2 = this.endPosition - 65307L;
        l1 = l2;
        if (l2 <= this.positionBeforeSeekToEnd)
        {
          this.totalGranules = readGranuleOfLastPage(paramExtractorInput);
          this.state = 3;
          l1 = this.positionBeforeSeekToEnd;
        }
      }
    }
    if (this.targetGranule == 0L) {}
    for (long l1 = 0L;; l1 = skipToPageOfGranule(paramExtractorInput, this.targetGranule, -(2L + l1)))
    {
      this.state = 3;
      l1 = -(2L + l1);
      break;
      l1 = getNextSeekPosition(this.targetGranule, paramExtractorInput);
      if (l1 >= 0L) {
        break;
      }
    }
  }
  
  long readGranuleOfLastPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    skipToNextPage(paramExtractorInput);
    this.pageHeader.reset();
    while (((this.pageHeader.type & 0x4) != 4) && (paramExtractorInput.getPosition() < this.endPosition))
    {
      this.pageHeader.populate(paramExtractorInput, false);
      paramExtractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
    }
    return this.pageHeader.granulePosition;
  }
  
  public void resetSeeking()
  {
    this.start = this.startPosition;
    this.end = this.endPosition;
    this.startGranule = 0L;
    this.endGranule = this.totalGranules;
  }
  
  void skipToNextPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (!skipToNextPage(paramExtractorInput, this.endPosition)) {
      throw new EOFException();
    }
  }
  
  boolean skipToNextPage(ExtractorInput paramExtractorInput, long paramLong)
    throws IOException, InterruptedException
  {
    boolean bool = false;
    paramLong = Math.min(3L + paramLong, this.endPosition);
    byte[] arrayOfByte = new byte['ࠀ'];
    int j;
    for (int i = arrayOfByte.length;; i = j)
    {
      j = i;
      if (paramExtractorInput.getPosition() + i > paramLong)
      {
        i = (int)(paramLong - paramExtractorInput.getPosition());
        j = i;
        if (i < 4) {
          return bool;
        }
      }
      paramExtractorInput.peekFully(arrayOfByte, 0, j, false);
      for (i = 0;; i++)
      {
        if (i >= j - 3) {
          break label162;
        }
        if ((arrayOfByte[i] == 79) && (arrayOfByte[(i + 1)] == 103) && (arrayOfByte[(i + 2)] == 103) && (arrayOfByte[(i + 3)] == 83))
        {
          paramExtractorInput.skipFully(i);
          bool = true;
          break;
        }
      }
      label162:
      paramExtractorInput.skipFully(j - 3);
    }
  }
  
  long skipToPageOfGranule(ExtractorInput paramExtractorInput, long paramLong1, long paramLong2)
    throws IOException, InterruptedException
  {
    this.pageHeader.populate(paramExtractorInput, false);
    while (this.pageHeader.granulePosition < paramLong1)
    {
      paramExtractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
      paramLong2 = this.pageHeader.granulePosition;
      this.pageHeader.populate(paramExtractorInput, false);
    }
    paramExtractorInput.resetPeekPosition();
    return paramLong2;
  }
  
  public long startSeek(long paramLong)
  {
    boolean bool;
    if ((this.state == 3) || (this.state == 2))
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (paramLong != 0L) {
        break label54;
      }
    }
    label54:
    for (paramLong = 0L;; paramLong = this.streamReader.convertTimeToGranule(paramLong))
    {
      this.targetGranule = paramLong;
      this.state = 2;
      resetSeeking();
      return this.targetGranule;
      bool = false;
      break;
    }
  }
  
  private class OggSeekMap
    implements SeekMap
  {
    private OggSeekMap() {}
    
    public long getDurationUs()
    {
      return DefaultOggSeeker.this.streamReader.convertGranuleToTime(DefaultOggSeeker.this.totalGranules);
    }
    
    public SeekMap.SeekPoints getSeekPoints(long paramLong)
    {
      if (paramLong == 0L) {}
      long l;
      for (SeekMap.SeekPoints localSeekPoints = new SeekMap.SeekPoints(new SeekPoint(0L, DefaultOggSeeker.this.startPosition));; localSeekPoints = new SeekMap.SeekPoints(new SeekPoint(paramLong, DefaultOggSeeker.this.getEstimatedPosition(DefaultOggSeeker.this.startPosition, l, 30000L))))
      {
        return localSeekPoints;
        l = DefaultOggSeeker.this.streamReader.convertTimeToGranule(paramLong);
      }
    }
    
    public boolean isSeekable()
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/DefaultOggSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */