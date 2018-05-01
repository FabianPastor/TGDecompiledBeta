package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;

final class zzeq
{
  final String name;
  final long zzafp;
  final long zzafq;
  final long zzafr;
  final long zzafs;
  final Long zzaft;
  final Long zzafu;
  final Boolean zzafv;
  final String zztd;
  
  zzeq(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, Long paramLong5, Long paramLong6, Boolean paramBoolean)
  {
    Preconditions.checkNotEmpty(paramString1);
    Preconditions.checkNotEmpty(paramString2);
    if (paramLong1 >= 0L)
    {
      bool = true;
      Preconditions.checkArgument(bool);
      if (paramLong2 < 0L) {
        break label116;
      }
      bool = true;
      label38:
      Preconditions.checkArgument(bool);
      if (paramLong4 < 0L) {
        break label122;
      }
    }
    label116:
    label122:
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool);
      this.zztd = paramString1;
      this.name = paramString2;
      this.zzafp = paramLong1;
      this.zzafq = paramLong2;
      this.zzafr = paramLong3;
      this.zzafs = paramLong4;
      this.zzaft = paramLong5;
      this.zzafu = paramLong6;
      this.zzafv = paramBoolean;
      return;
      bool = false;
      break;
      bool = false;
      break label38;
    }
  }
  
  final zzeq zza(Long paramLong1, Long paramLong2, Boolean paramBoolean)
  {
    if ((paramBoolean != null) && (!paramBoolean.booleanValue())) {
      paramBoolean = null;
    }
    for (;;)
    {
      return new zzeq(this.zztd, this.name, this.zzafp, this.zzafq, this.zzafr, this.zzafs, paramLong1, paramLong2, paramBoolean);
    }
  }
  
  final zzeq zzad(long paramLong)
  {
    return new zzeq(this.zztd, this.name, this.zzafp, this.zzafq, this.zzafr, paramLong, this.zzaft, this.zzafu, this.zzafv);
  }
  
  final zzeq zzie()
  {
    return new zzeq(this.zztd, this.name, this.zzafp + 1L, this.zzafq + 1L, this.zzafr, this.zzafs, this.zzaft, this.zzafu, this.zzafv);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzeq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */