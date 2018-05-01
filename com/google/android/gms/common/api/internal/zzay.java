package com.google.android.gms.common.api.internal;

import java.util.concurrent.locks.Lock;

abstract class zzay
  implements Runnable
{
  private zzay(zzao paramzzao) {}
  
  public void run()
  {
    zzao.zzc(this.zzfrl).lock();
    try
    {
      boolean bool = Thread.interrupted();
      if (bool) {
        return;
      }
      zzaib();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      zzao.zzd(this.zzfrl).zza(localRuntimeException);
      return;
    }
    finally
    {
      zzao.zzc(this.zzfrl).unlock();
    }
  }
  
  protected abstract void zzaib();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */