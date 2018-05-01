package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import java.util.concurrent.locks.Lock;

abstract class zzbcn
  implements Runnable
{
  private zzbcn(zzbcd paramzzbcd) {}
  
  @WorkerThread
  public void run()
  {
    zzbcd.zzc(this.zzaDp).lock();
    try
    {
      boolean bool = Thread.interrupted();
      if (bool) {
        return;
      }
      zzpV();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      zzbcd.zzd(this.zzaDp).zza(localRuntimeException);
      return;
    }
    finally
    {
      zzbcd.zzc(this.zzaDp).unlock();
    }
  }
  
  @WorkerThread
  protected abstract void zzpV();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */