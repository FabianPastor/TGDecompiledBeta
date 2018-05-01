package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzbk
{
  private static final int zzct = 11;
  private static final int zzcu = 12;
  private static final int zzcv = 16;
  private static final int zzcw = 26;
  private static final int[] zzcx = new int[0];
  private static final long[] zzcy = new long[0];
  private static final float[] zzcz = new float[0];
  private static final double[] zzda = new double[0];
  private static final boolean[] zzdb = new boolean[0];
  private static final String[] zzdc = new String[0];
  public static final byte[][] zzdd = new byte[0][];
  public static final byte[] zzde = new byte[0];
  
  public static final int zzb(zzay paramzzay, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzay.getPosition();
    paramzzay.zzh(paramInt);
    while (paramzzay.zzy() == paramInt)
    {
      paramzzay.zzh(paramInt);
      i++;
    }
    paramzzay.zzb(j, paramInt);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */