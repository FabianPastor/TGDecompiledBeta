package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult>
  implements zzf<TResult>
{
  private final Executor aBG;
  private OnSuccessListener<? super TResult> aJD;
  private final Object zzakd = new Object();
  
  public zze(@NonNull Executor paramExecutor, @NonNull OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    this.aBG = paramExecutor;
    this.aJD = paramOnSuccessListener;
  }
  
  public void cancel()
  {
    synchronized (this.zzakd)
    {
      this.aJD = null;
      return;
    }
  }
  
  public void onComplete(@NonNull final Task<TResult> paramTask)
  {
    if (paramTask.isSuccessful()) {
      synchronized (this.zzakd)
      {
        if (this.aJD == null) {
          return;
        }
        this.aBG.execute(new Runnable()
        {
          public void run()
          {
            synchronized (zze.zza(zze.this))
            {
              if (zze.zzb(zze.this) != null) {
                zze.zzb(zze.this).onSuccess(paramTask.getResult());
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */