package org.telegram.messenger.exoplayer.extractor;

import org.telegram.messenger.exoplayer.util.Util;

public final class ChunkIndex
  implements SeekMap
{
  public final long[] durationsUs;
  public final int length;
  public final long[] offsets;
  public final int[] sizes;
  public final long[] timesUs;
  
  public ChunkIndex(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    this.length = paramArrayOfInt.length;
    this.sizes = paramArrayOfInt;
    this.offsets = paramArrayOfLong1;
    this.durationsUs = paramArrayOfLong2;
    this.timesUs = paramArrayOfLong3;
  }
  
  public int getChunkIndex(long paramLong)
  {
    return Util.binarySearchFloor(this.timesUs, paramLong, true, true);
  }
  
  public long getPosition(long paramLong)
  {
    return this.offsets[getChunkIndex(paramLong)];
  }
  
  public boolean isSeekable()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ChunkIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */