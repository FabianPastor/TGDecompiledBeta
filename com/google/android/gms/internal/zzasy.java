package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;

class zzasy
{
  final String mName;
  final String zzVQ;
  final long zzbqJ;
  final long zzbqK;
  final long zzbqL;
  
  zzasy(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzac.zzdv(paramString1);
    zzac.zzdv(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzac.zzas(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzas(bool1);
      this.zzVQ = paramString1;
      this.mName = paramString2;
      this.zzbqJ = paramLong1;
      this.zzbqK = paramLong2;
      this.zzbqL = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  zzasy zzKX()
  {
    return new zzasy(this.zzVQ, this.mName, this.zzbqJ + 1L, this.zzbqK + 1L, this.zzbqL);
  }
  
  zzasy zzan(long paramLong)
  {
    return new zzasy(this.zzVQ, this.mName, this.zzbqJ, this.zzbqK, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */