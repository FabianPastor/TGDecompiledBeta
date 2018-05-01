package org.telegram.messenger.exoplayer.util;

import org.telegram.messenger.exoplayer.extractor.SeekMap;

public final class FlacSeekTable
{
  private static final int METADATA_LENGTH_OFFSET = 1;
  private static final int SEEK_POINT_SIZE = 18;
  private final long[] offsets;
  private final long[] sampleNumbers;
  
  private FlacSeekTable(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    this.sampleNumbers = paramArrayOfLong1;
    this.offsets = paramArrayOfLong2;
  }
  
  public static FlacSeekTable parseSeekTable(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(1);
    int j = paramParsableByteArray.readUnsignedInt24() / 18;
    long[] arrayOfLong1 = new long[j];
    long[] arrayOfLong2 = new long[j];
    int i = 0;
    while (i < j)
    {
      arrayOfLong1[i] = paramParsableByteArray.readLong();
      arrayOfLong2[i] = paramParsableByteArray.readLong();
      paramParsableByteArray.skipBytes(2);
      i += 1;
    }
    return new FlacSeekTable(arrayOfLong1, arrayOfLong2);
  }
  
  public SeekMap createSeekMap(long paramLong1, final long paramLong2)
  {
    new SeekMap()
    {
      public long getPosition(long paramAnonymousLong)
      {
        paramAnonymousLong = paramLong2 * paramAnonymousLong / 1000000L;
        int i = Util.binarySearchFloor(FlacSeekTable.this.sampleNumbers, paramAnonymousLong, true, true);
        return this.val$firstFrameOffset + FlacSeekTable.this.offsets[i];
      }
      
      public boolean isSeekable()
      {
        return true;
      }
    };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/FlacSeekTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */