package com.google.android.gms.internal.measurement;

import java.util.concurrent.atomic.AtomicReference;

final class zzhy
  implements Runnable
{
  zzhy(zzhm paramzzhm, AtomicReference paramAtomicReference) {}
  
  public final void run()
  {
    try
    {
      AtomicReference localAtomicReference = this.zzaoo;
      zzeh localzzeh = this.zzaop.zzgi();
      localAtomicReference.set(Long.valueOf(localzzeh.zza(localzzeh.zzfv().zzah(), zzew.zzahu)));
      return;
    }
    finally
    {
      this.zzaoo.notify();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzhy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */