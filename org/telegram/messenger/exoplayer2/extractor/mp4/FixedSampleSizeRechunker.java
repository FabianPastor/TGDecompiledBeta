package org.telegram.messenger.exoplayer2.extractor.mp4;

import org.telegram.messenger.exoplayer2.util.Util;

final class FixedSampleSizeRechunker
{
  private static final int MAX_SAMPLE_SIZE = 8192;
  
  public static Results rechunk(int paramInt, long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong)
  {
    int i = 8192 / paramInt;
    int j = 0;
    int k = paramArrayOfInt.length;
    for (int m = 0; m < k; m++) {
      j += Util.ceilDivide(paramArrayOfInt[m], i);
    }
    long[] arrayOfLong1 = new long[j];
    int[] arrayOfInt1 = new int[j];
    int n = 0;
    long[] arrayOfLong2 = new long[j];
    int[] arrayOfInt2 = new int[j];
    k = 0;
    m = 0;
    for (j = 0; j < paramArrayOfInt.length; j++)
    {
      int i1 = paramArrayOfInt[j];
      long l = paramArrayOfLong[j];
      while (i1 > 0)
      {
        int i2 = Math.min(i, i1);
        arrayOfLong1[m] = l;
        arrayOfInt1[m] = (paramInt * i2);
        n = Math.max(n, arrayOfInt1[m]);
        arrayOfLong2[m] = (k * paramLong);
        arrayOfInt2[m] = 1;
        l += arrayOfInt1[m];
        k += i2;
        i1 -= i2;
        m++;
      }
    }
    return new Results(arrayOfLong1, arrayOfInt1, n, arrayOfLong2, arrayOfInt2, null);
  }
  
  public static final class Results
  {
    public final int[] flags;
    public final int maximumSize;
    public final long[] offsets;
    public final int[] sizes;
    public final long[] timestamps;
    
    private Results(long[] paramArrayOfLong1, int[] paramArrayOfInt1, int paramInt, long[] paramArrayOfLong2, int[] paramArrayOfInt2)
    {
      this.offsets = paramArrayOfLong1;
      this.sizes = paramArrayOfInt1;
      this.maximumSize = paramInt;
      this.timestamps = paramArrayOfLong2;
      this.flags = paramArrayOfInt2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/FixedSampleSizeRechunker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */