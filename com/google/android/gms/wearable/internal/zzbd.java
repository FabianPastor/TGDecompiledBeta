package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzbd
  extends zzdh
{
  private final Object mLock = new Object();
  private zzah zzbSr;
  private zzbe zzbSv;
  
  public final void zza(zzbe paramzzbe)
  {
    synchronized (this.mLock)
    {
      this.zzbSv = ((zzbe)zzbo.zzu(paramzzbe));
      zzah localzzah = this.zzbSr;
      if (localzzah != null) {
        paramzzbe.zzb(localzzah);
      }
      return;
    }
  }
  
  public final void zzm(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      zzbe localzzbe = this.zzbSv;
      zzah localzzah = new zzah(paramInt1, paramInt2);
      this.zzbSr = localzzah;
      if (localzzbe != null) {
        localzzbe.zzb(localzzah);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */