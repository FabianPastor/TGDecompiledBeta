package com.google.android.gms.common.api.internal;

import java.util.concurrent.locks.Lock;

abstract class zzat
  implements Runnable
{
  private zzat(zzaj paramzzaj) {}
  
  public void run()
  {
    zzaj.zzc(this.zzhv).lock();
    for (;;)
    {
      try
      {
        boolean bool = Thread.interrupted();
        if (bool) {
          return;
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        zzaj.zzd(this.zzhv).zzb(localRuntimeException);
        zzaj.zzc(this.zzhv).unlock();
        continue;
      }
      finally
      {
        zzaj.zzc(this.zzhv).unlock();
      }
      zzaq();
      zzaj.zzc(this.zzhv).unlock();
    }
  }
  
  protected abstract void zzaq();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */