package com.google.android.gms.tasks;

final class zzl
  implements Runnable
{
  zzl(zzk paramzzk, Task paramTask) {}
  
  public final void run()
  {
    synchronized (zzk.zza(this.zzafv))
    {
      if (zzk.zzb(this.zzafv) != null) {
        zzk.zzb(this.zzafv).onFailure(this.zzafn.getException());
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */