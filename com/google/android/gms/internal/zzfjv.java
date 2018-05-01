package com.google.android.gms.internal;

import java.io.IOException;

public final class zzfjv
{
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static int zzpnl = 11;
  private static int zzpnm = 12;
  private static int zzpnn = 16;
  private static int zzpno = 26;
  public static final int[] zzpnp = new int[0];
  public static final long[] zzpnq = new long[0];
  public static final float[] zzpnr = new float[0];
  public static final double[] zzpns = new double[0];
  public static final boolean[] zzpnt = new boolean[0];
  public static final byte[][] zzpnu = new byte[0][];
  public static final byte[] zzpnv = new byte[0];
  
  public static final int zzb(zzfjj paramzzfjj, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzfjj.getPosition();
    paramzzfjj.zzkq(paramInt);
    while (paramzzfjj.zzcvt() == paramInt)
    {
      paramzzfjj.zzkq(paramInt);
      i += 1;
    }
    paramzzfjj.zzam(j, paramInt);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */