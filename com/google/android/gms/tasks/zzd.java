package com.google.android.gms.tasks;

final class zzd
  implements Runnable
{
  zzd(zzc paramzzc, Task paramTask) {}
  
  public final void run()
  {
    try
    {
      Task localTask = (Task)zzc.zza(this.zzbLV).then(this.zzbLT);
      if (localTask == null)
      {
        this.zzbLV.onFailure(new NullPointerException("Continuation returned null"));
        return;
      }
    }
    catch (RuntimeExecutionException localRuntimeExecutionException)
    {
      if ((localRuntimeExecutionException.getCause() instanceof Exception))
      {
        zzc.zzb(this.zzbLV).setException((Exception)localRuntimeExecutionException.getCause());
        return;
      }
      zzc.zzb(this.zzbLV).setException(localRuntimeExecutionException);
      return;
    }
    catch (Exception localException)
    {
      zzc.zzb(this.zzbLV).setException(localException);
      return;
    }
    localException.addOnSuccessListener(TaskExecutors.zzbMf, this.zzbLV);
    localException.addOnFailureListener(TaskExecutors.zzbMf, this.zzbLV);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */