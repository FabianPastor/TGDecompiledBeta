package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;

class zzatn
{
  final String mAppId;
  final String mName;
  final long zzbrB;
  final long zzbrC;
  final long zzbrD;
  
  zzatn(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzac.zzaw(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzaw(bool1);
      this.mAppId = paramString1;
      this.mName = paramString2;
      this.zzbrB = paramLong1;
      this.zzbrC = paramLong2;
      this.zzbrD = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  zzatn zzLV()
  {
    return new zzatn(this.mAppId, this.mName, this.zzbrB + 1L, this.zzbrC + 1L, this.zzbrD);
  }
  
  zzatn zzap(long paramLong)
  {
    return new zzatn(this.mAppId, this.mName, this.zzbrB, this.zzbrC, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */