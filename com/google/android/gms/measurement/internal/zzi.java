package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;

class zzi
{
  final long aot;
  final long aou;
  final long aov;
  final String mName;
  final String zzcpe;
  
  zzi(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzac.zzhz(paramString1);
    zzac.zzhz(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzac.zzbs(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzbs(bool1);
      this.zzcpe = paramString1;
      this.mName = paramString2;
      this.aot = paramLong1;
      this.aou = paramLong2;
      this.aov = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  zzi zzbm(long paramLong)
  {
    return new zzi(this.zzcpe, this.mName, this.aot, this.aou, paramLong);
  }
  
  zzi zzbvy()
  {
    return new zzi(this.zzcpe, this.mName, this.aot + 1L, this.aou + 1L, this.aov);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */