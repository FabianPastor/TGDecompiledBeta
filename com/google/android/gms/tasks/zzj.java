package com.google.android.gms.tasks;

final class zzj
  implements Runnable
{
  zzj(zzi paramzzi, Task paramTask) {}
  
  public final void run()
  {
    synchronized (zzi.zza(this.zzaft))
    {
      if (zzi.zzb(this.zzaft) != null) {
        zzi.zzb(this.zzaft).onComplete(this.zzafn);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */