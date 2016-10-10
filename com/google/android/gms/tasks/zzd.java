package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult>
  implements zzf<TResult>
{
  private final Executor aBG;
  private OnFailureListener aJB;
  private final Object zzakd = new Object();
  
  public zzd(@NonNull Executor paramExecutor, @NonNull OnFailureListener paramOnFailureListener)
  {
    this.aBG = paramExecutor;
    this.aJB = paramOnFailureListener;
  }
  
  public void cancel()
  {
    synchronized (this.zzakd)
    {
      this.aJB = null;
      return;
    }
  }
  
  public void onComplete(@NonNull final Task<TResult> paramTask)
  {
    if (!paramTask.isSuccessful()) {
      synchronized (this.zzakd)
      {
        if (this.aJB == null) {
          return;
        }
        this.aBG.execute(new Runnable()
        {
          public void run()
          {
            synchronized (zzd.zza(zzd.this))
            {
              if (zzd.zzb(zzd.this) != null) {
                zzd.zzb(zzd.this).onFailure(paramTask.getException());
              }
              return;
            }
          }
        });
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */