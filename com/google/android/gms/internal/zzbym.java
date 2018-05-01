package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbym
{
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  static final int zzcwM = zzO(1, 3);
  static final int zzcwN = zzO(1, 4);
  static final int zzcwO = zzO(2, 0);
  static final int zzcwP = zzO(3, 2);
  public static final int[] zzcwQ = new int[0];
  public static final long[] zzcwR = new long[0];
  public static final float[] zzcwS = new float[0];
  public static final double[] zzcwT = new double[0];
  public static final boolean[] zzcwU = new boolean[0];
  public static final byte[][] zzcwV = new byte[0][];
  public static final byte[] zzcwW = new byte[0];
  
  public static int zzO(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static final int zzb(zzbyb paramzzbyb, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzbyb.getPosition();
    paramzzbyb.zzrd(paramInt);
    while (paramzzbyb.zzaeW() == paramInt)
    {
      paramzzbyb.zzrd(paramInt);
      i += 1;
    }
    paramzzbyb.zzrh(j);
    return i;
  }
  
  static int zzrw(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  public static int zzrx(int paramInt)
  {
    return paramInt >>> 3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbym.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */