package com.google.android.gms.vision;

import android.util.SparseArray;
import javax.annotation.concurrent.GuardedBy;

public final class zzc
{
  private static final Object sLock = new Object();
  @GuardedBy("sLock")
  private static int zzas = 0;
  @GuardedBy("sLock")
  private SparseArray<Integer> zzat = new SparseArray();
  @GuardedBy("sLock")
  private SparseArray<Integer> zzau = new SparseArray();
  
  public final int zzb(int paramInt)
  {
    synchronized (sLock)
    {
      Integer localInteger = (Integer)this.zzat.get(paramInt);
      if (localInteger != null)
      {
        paramInt = localInteger.intValue();
        return paramInt;
      }
      int i = zzas;
      zzas += 1;
      this.zzat.append(paramInt, Integer.valueOf(i));
      this.zzau.append(i, Integer.valueOf(paramInt));
      paramInt = i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */