package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza
{
  private static int aKD = 0;
  private static final Object zzaok = new Object();
  private SparseArray<Integer> aKE = new SparseArray();
  private SparseArray<Integer> aKF = new SparseArray();
  
  public int zzabb(int paramInt)
  {
    synchronized (zzaok)
    {
      Integer localInteger = (Integer)this.aKE.get(paramInt);
      if (localInteger != null)
      {
        paramInt = localInteger.intValue();
        return paramInt;
      }
      int i = aKD;
      aKD += 1;
      this.aKE.append(paramInt, Integer.valueOf(i));
      this.aKF.append(i, Integer.valueOf(paramInt));
      return i;
    }
  }
  
  public int zzabc(int paramInt)
  {
    synchronized (zzaok)
    {
      paramInt = ((Integer)this.aKF.get(paramInt)).intValue();
      return paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */