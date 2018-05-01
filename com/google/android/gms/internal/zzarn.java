package com.google.android.gms.internal;

import java.io.IOException;

public final class zzarn
{
  public static final int[] bqF = new int[0];
  public static final long[] bqG = new long[0];
  public static final float[] bqH = new float[0];
  public static final double[] bqI = new double[0];
  public static final boolean[] bqJ = new boolean[0];
  public static final String[] bqK = new String[0];
  public static final byte[][] bqL = new byte[0][];
  public static final byte[] bqM = new byte[0];
  
  static int zzaht(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  public static int zzahu(int paramInt)
  {
    return paramInt >>> 3;
  }
  
  public static int zzaj(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static boolean zzb(zzarc paramzzarc, int paramInt)
    throws IOException
  {
    return paramzzarc.zzaha(paramInt);
  }
  
  public static final int zzc(zzarc paramzzarc, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzarc.getPosition();
    paramzzarc.zzaha(paramInt);
    while (paramzzarc.cw() == paramInt)
    {
      paramzzarc.zzaha(paramInt);
      i += 1;
    }
    paramzzarc.zzahe(j);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzarn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */