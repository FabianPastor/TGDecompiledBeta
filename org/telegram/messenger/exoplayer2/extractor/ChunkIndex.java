package org.telegram.messenger.exoplayer2.extractor;

import org.telegram.messenger.exoplayer2.util.Util;

public final class ChunkIndex
  implements SeekMap
{
  private final long durationUs;
  public final long[] durationsUs;
  public final int length;
  public final long[] offsets;
  public final int[] sizes;
  public final long[] timesUs;
  
  public ChunkIndex(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    this.sizes = paramArrayOfInt;
    this.offsets = paramArrayOfLong1;
    this.durationsUs = paramArrayOfLong2;
    this.timesUs = paramArrayOfLong3;
    this.length = paramArrayOfInt.length;
    if (this.length > 0) {}
    for (this.durationUs = (paramArrayOfLong2[(this.length - 1)] + paramArrayOfLong3[(this.length - 1)]);; this.durationUs = 0L) {
      return;
    }
  }
  
  public int getChunkIndex(long paramLong)
  {
    return Util.binarySearchFloor(this.timesUs, paramLong, true, true);
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    int i = getChunkIndex(paramLong);
    Object localObject = new SeekPoint(this.timesUs[i], this.offsets[i]);
    if ((((SeekPoint)localObject).timeUs >= paramLong) || (i == this.length - 1)) {}
    for (localObject = new SeekMap.SeekPoints((SeekPoint)localObject);; localObject = new SeekMap.SeekPoints((SeekPoint)localObject, new SeekPoint(this.timesUs[(i + 1)], this.offsets[(i + 1)]))) {
      return (SeekMap.SeekPoints)localObject;
    }
  }
  
  public boolean isSeekable()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ChunkIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */