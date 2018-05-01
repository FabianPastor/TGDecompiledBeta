package com.google.android.gms.internal;

import java.util.concurrent.locks.Lock;

abstract class zzbcy
{
  private final zzbcw zzaDZ;
  
  protected zzbcy(zzbcw paramzzbcw)
  {
    this.zzaDZ = paramzzbcw;
  }
  
  public final void zzc(zzbcx paramzzbcx)
  {
    zzbcx.zza(paramzzbcx).lock();
    try
    {
      zzbcw localzzbcw1 = zzbcx.zzb(paramzzbcx);
      zzbcw localzzbcw2 = this.zzaDZ;
      if (localzzbcw1 != localzzbcw2) {
        return;
      }
      zzpV();
      return;
    }
    finally
    {
      zzbcx.zza(paramzzbcx).unlock();
    }
  }
  
  protected abstract void zzpV();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */