package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzabm
{
  private static final int zzbzt = 11;
  private static final int zzbzu = 12;
  private static final int zzbzv = 16;
  private static final int zzbzw = 26;
  public static final int[] zzbzx = new int[0];
  public static final long[] zzbzy = new long[0];
  private static final float[] zzbzz = new float[0];
  private static final double[] zzcaa = new double[0];
  private static final boolean[] zzcab = new boolean[0];
  public static final String[] zzcac = new String[0];
  private static final byte[][] zzcad = new byte[0][];
  public static final byte[] zzcae = new byte[0];
  
  public static final int zzb(zzaba paramzzaba, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzaba.getPosition();
    paramzzaba.zzam(paramInt);
    while (paramzzaba.zzvo() == paramInt)
    {
      paramzzaba.zzam(paramInt);
      i++;
    }
    paramzzaba.zzd(j, paramInt);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */