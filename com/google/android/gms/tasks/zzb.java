package com.google.android.gms.tasks;

final class zzb
  implements Runnable
{
  zzb(zza paramzza, Task paramTask) {}
  
  public final void run()
  {
    try
    {
      Object localObject = zza.zza(this.zzbLU).then(this.zzbLT);
      zza.zzb(this.zzbLU).setResult(localObject);
      return;
    }
    catch (RuntimeExecutionException localRuntimeExecutionException)
    {
      if ((localRuntimeExecutionException.getCause() instanceof Exception))
      {
        zza.zzb(this.zzbLU).setException((Exception)localRuntimeExecutionException.getCause());
        return;
      }
      zza.zzb(this.zzbLU).setException(localRuntimeExecutionException);
      return;
    }
    catch (Exception localException)
    {
      zza.zzb(this.zzbLU).setException(localException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */