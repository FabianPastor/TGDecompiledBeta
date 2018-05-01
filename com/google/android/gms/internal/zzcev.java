package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

final class zzcev
{
  final String mAppId;
  final String mName;
  final long zzbpG;
  final long zzbpH;
  final long zzbpI;
  
  zzcev(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzbo.zzcF(paramString1);
    zzbo.zzcF(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzbo.zzaf(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zzaf(bool1);
      this.mAppId = paramString1;
      this.mName = paramString2;
      this.zzbpG = paramLong1;
      this.zzbpH = paramLong2;
      this.zzbpI = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  final zzcev zzab(long paramLong)
  {
    return new zzcev(this.mAppId, this.mName, this.zzbpG, this.zzbpH, paramLong);
  }
  
  final zzcev zzys()
  {
    return new zzcev(this.mAppId, this.mName, this.zzbpG + 1L, this.zzbpH + 1L, this.zzbpI);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcev.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */