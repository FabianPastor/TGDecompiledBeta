package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbuw
{
  public static final int[] zzcsi = new int[0];
  public static final long[] zzcsj = new long[0];
  public static final float[] zzcsk = new float[0];
  public static final double[] zzcsl = new double[0];
  public static final boolean[] zzcsm = new boolean[0];
  public static final String[] zzcsn = new String[0];
  public static final byte[][] zzcso = new byte[0][];
  public static final byte[] zzcsp = new byte[0];
  
  public static int zzK(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static boolean zzb(zzbul paramzzbul, int paramInt)
    throws IOException
  {
    return paramzzbul.zzqh(paramInt);
  }
  
  public static final int zzc(zzbul paramzzbul, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzbul.getPosition();
    paramzzbul.zzqh(paramInt);
    while (paramzzbul.zzacu() == paramInt)
    {
      paramzzbul.zzqh(paramInt);
      i += 1;
    }
    paramzzbul.zzql(j);
    return i;
  }
  
  static int zzqA(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  public static int zzqB(int paramInt)
  {
    return paramInt >>> 3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbuw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */