package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzaa;

class zzi
{
  final long arD;
  final long arE;
  final long arF;
  final String mName;
  final String zzctj;
  
  zzi(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzaa.zzib(paramString1);
    zzaa.zzib(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzaa.zzbt(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzaa.zzbt(bool1);
      this.zzctj = paramString1;
      this.mName = paramString2;
      this.arD = paramLong1;
      this.arE = paramLong2;
      this.arF = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  zzi zzbl(long paramLong)
  {
    return new zzi(this.zzctj, this.mName, this.arD, this.arE, paramLong);
  }
  
  zzi zzbwv()
  {
    return new zzi(this.zzctj, this.mName, this.arD + 1L, this.arE + 1L, this.arF);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */