package com.google.android.gms.internal.measurement;

import java.util.concurrent.atomic.AtomicReference;

final class zzia
  implements Runnable
{
  zzia(zzhm paramzzhm, AtomicReference paramAtomicReference) {}
  
  public final void run()
  {
    try
    {
      AtomicReference localAtomicReference = this.zzaoo;
      zzeh localzzeh = this.zzaop.zzgi();
      localAtomicReference.set(Double.valueOf(localzzeh.zzc(localzzeh.zzfv().zzah(), zzew.zzahw)));
      return;
    }
    finally
    {
      this.zzaoo.notify();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */