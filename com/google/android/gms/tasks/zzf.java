package com.google.android.gms.tasks;

final class zzf
  implements Runnable
{
  zzf(zze paramzze, Task paramTask) {}
  
  public final void run()
  {
    synchronized (zze.zza(this.zzkue))
    {
      if (zze.zzb(this.zzkue) != null) {
        zze.zzb(this.zzkue).onComplete(this.zzkua);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */