package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

final class zzcgw
{
  final String mAppId;
  final String mName;
  final long zzizk;
  final long zzizl;
  final long zzizm;
  final long zzizn;
  final Long zzizo;
  final Long zzizp;
  final Boolean zzizq;
  
  zzcgw(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, Long paramLong5, Long paramLong6, Boolean paramBoolean)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString2);
    if (paramLong1 >= 0L)
    {
      bool = true;
      zzbq.checkArgument(bool);
      if (paramLong2 < 0L) {
        break label116;
      }
      bool = true;
      label38:
      zzbq.checkArgument(bool);
      if (paramLong4 < 0L) {
        break label122;
      }
    }
    label116:
    label122:
    for (boolean bool = true;; bool = false)
    {
      zzbq.checkArgument(bool);
      this.mAppId = paramString1;
      this.mName = paramString2;
      this.zzizk = paramLong1;
      this.zzizl = paramLong2;
      this.zzizm = paramLong3;
      this.zzizn = paramLong4;
      this.zzizo = paramLong5;
      this.zzizp = paramLong6;
      this.zzizq = paramBoolean;
      return;
      bool = false;
      break;
      bool = false;
      break label38;
    }
  }
  
  final zzcgw zza(Long paramLong1, Long paramLong2, Boolean paramBoolean)
  {
    if ((paramBoolean != null) && (!paramBoolean.booleanValue())) {
      paramBoolean = null;
    }
    for (;;)
    {
      return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, this.zzizm, this.zzizn, paramLong1, paramLong2, paramBoolean);
    }
  }
  
  final zzcgw zzayw()
  {
    return new zzcgw(this.mAppId, this.mName, this.zzizk + 1L, this.zzizl + 1L, this.zzizm, this.zzizn, this.zzizo, this.zzizp, this.zzizq);
  }
  
  final zzcgw zzbb(long paramLong)
  {
    return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, paramLong, this.zzizn, this.zzizo, this.zzizp, this.zzizq);
  }
  
  final zzcgw zzbc(long paramLong)
  {
    return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, this.zzizm, paramLong, this.zzizo, this.zzizp, this.zzizq);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */