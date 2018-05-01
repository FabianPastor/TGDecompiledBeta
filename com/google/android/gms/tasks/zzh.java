package com.google.android.gms.tasks;

final class zzh
  implements Runnable
{
  zzh(zzg paramzzg) {}
  
  public final void run()
  {
    synchronized (zzg.zza(this.zzafr))
    {
      if (zzg.zzb(this.zzafr) != null) {
        zzg.zzb(this.zzafr).onCanceled();
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */