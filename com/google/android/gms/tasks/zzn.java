package com.google.android.gms.tasks;

final class zzn
  implements Runnable
{
  zzn(zzm paramzzm, Task paramTask) {}
  
  public final void run()
  {
    synchronized (zzm.zza(this.zzafx))
    {
      if (zzm.zzb(this.zzafx) != null) {
        zzm.zzb(this.zzafx).onSuccess(this.zzafn.getResult());
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */