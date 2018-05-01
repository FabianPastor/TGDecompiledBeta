package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbxw
{
  static final int zzcuS = zzO(1, 3);
  static final int zzcuT = zzO(1, 4);
  static final int zzcuU = zzO(2, 0);
  static final int zzcuV = zzO(3, 2);
  public static final int[] zzcuW = new int[0];
  public static final long[] zzcuX = new long[0];
  public static final float[] zzcuY = new float[0];
  public static final double[] zzcuZ = new double[0];
  public static final boolean[] zzcva = new boolean[0];
  public static final String[] zzcvb = new String[0];
  public static final byte[][] zzcvc = new byte[0][];
  public static final byte[] zzcvd = new byte[0];
  
  public static int zzO(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static final int zzb(zzbxl paramzzbxl, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzbxl.getPosition();
    paramzzbxl.zzqY(paramInt);
    while (paramzzbxl.zzaeo() == paramInt)
    {
      paramzzbxl.zzqY(paramInt);
      i += 1;
    }
    paramzzbxl.zzrc(j);
    return i;
  }
  
  static int zzrr(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  public static int zzrs(int paramInt)
  {
    return paramInt >>> 3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */