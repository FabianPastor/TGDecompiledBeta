package org.telegram.messenger.exoplayer.extractor.mp4;

import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

final class TrackSampleTable
{
  public static final int NO_SAMPLE = -1;
  public final int[] flags;
  public final int maximumSize;
  public final long[] offsets;
  public final int sampleCount;
  public final int[] sizes;
  public final long[] timestampsUs;
  
  TrackSampleTable(long[] paramArrayOfLong1, int[] paramArrayOfInt1, int paramInt, long[] paramArrayOfLong2, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1.length == paramArrayOfLong2.length)
    {
      bool1 = true;
      Assertions.checkArgument(bool1);
      if (paramArrayOfLong1.length != paramArrayOfLong2.length) {
        break label97;
      }
      bool1 = true;
      label34:
      Assertions.checkArgument(bool1);
      if (paramArrayOfInt2.length != paramArrayOfLong2.length) {
        break label103;
      }
    }
    label97:
    label103:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      this.offsets = paramArrayOfLong1;
      this.sizes = paramArrayOfInt1;
      this.maximumSize = paramInt;
      this.timestampsUs = paramArrayOfLong2;
      this.flags = paramArrayOfInt2;
      this.sampleCount = paramArrayOfLong1.length;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label34;
    }
  }
  
  public int getIndexOfEarlierOrEqualSynchronizationSample(long paramLong)
  {
    int i = Util.binarySearchFloor(this.timestampsUs, paramLong, true, false);
    while (i >= 0)
    {
      if ((this.flags[i] & 0x1) != 0) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  public int getIndexOfLaterOrEqualSynchronizationSample(long paramLong)
  {
    int i = Util.binarySearchCeil(this.timestampsUs, paramLong, true, false);
    while (i < this.timestampsUs.length)
    {
      if ((this.flags[i] & 0x1) != 0) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp4/TrackSampleTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */