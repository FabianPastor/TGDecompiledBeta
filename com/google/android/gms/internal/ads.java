package com.google.android.gms.internal;

import java.io.IOException;

public final class ads
{
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static int zzcsA;
  private static int zzcsB;
  public static final int[] zzcsC;
  public static final long[] zzcsD;
  public static final float[] zzcsE;
  private static double[] zzcsF;
  public static final boolean[] zzcsG;
  public static final byte[][] zzcsH = new byte[0][];
  public static final byte[] zzcsI = new byte[0];
  private static int zzcsy = 11;
  private static int zzcsz = 12;
  
  static
  {
    zzcsA = 16;
    zzcsB = 26;
    zzcsC = new int[0];
    zzcsD = new long[0];
    zzcsE = new float[0];
    zzcsF = new double[0];
    zzcsG = new boolean[0];
  }
  
  public static final int zzb(adg paramadg, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramadg.getPosition();
    paramadg.zzcm(paramInt);
    while (paramadg.zzLA() == paramInt)
    {
      paramadg.zzcm(paramInt);
      i += 1;
    }
    paramadg.zzq(j, paramInt);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ads.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */