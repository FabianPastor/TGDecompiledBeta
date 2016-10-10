package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult>
  implements zzf<TResult>
{
  private final Executor aBG;
  private OnCompleteListener<TResult> aJz;
  private final Object zzakd = new Object();
  
  public zzc(@NonNull Executor paramExecutor, @NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    this.aBG = paramExecutor;
    this.aJz = paramOnCompleteListener;
  }
  
  public void cancel()
  {
    synchronized (this.zzakd)
    {
      this.aJz = null;
      return;
    }
  }
  
  public void onComplete(@NonNull final Task<TResult> paramTask)
  {
    synchronized (this.zzakd)
    {
      if (this.aJz == null) {
        return;
      }
      this.aBG.execute(new Runnable()
      {
        public void run()
        {
          synchronized (zzc.zza(zzc.this))
          {
            if (zzc.zzb(zzc.this) != null) {
              zzc.zzb(zzc.this).onComplete(paramTask);
            }
            return;
          }
        }
      });
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */