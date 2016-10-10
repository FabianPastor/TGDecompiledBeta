package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzb<TResult, TContinuationResult>
  implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzf<TResult>
{
  private final Executor aBG;
  private final Continuation<TResult, Task<TContinuationResult>> aJu;
  private final zzh<TContinuationResult> aJv;
  
  public zzb(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation, @NonNull zzh<TContinuationResult> paramzzh)
  {
    this.aBG = paramExecutor;
    this.aJu = paramContinuation;
    this.aJv = paramzzh;
  }
  
  public void cancel()
  {
    throw new UnsupportedOperationException();
  }
  
  public void onComplete(@NonNull final Task<TResult> paramTask)
  {
    this.aBG.execute(new Runnable()
    {
      public void run()
      {
        try
        {
          Task localTask = (Task)zzb.zza(zzb.this).then(paramTask);
          if (localTask == null)
          {
            zzb.this.onFailure(new NullPointerException("Continuation returned null"));
            return;
          }
        }
        catch (RuntimeExecutionException localRuntimeExecutionException)
        {
          if ((localRuntimeExecutionException.getCause() instanceof Exception))
          {
            zzb.zzb(zzb.this).setException((Exception)localRuntimeExecutionException.getCause());
            return;
          }
          zzb.zzb(zzb.this).setException(localRuntimeExecutionException);
          return;
        }
        catch (Exception localException)
        {
          zzb.zzb(zzb.this).setException(localException);
          return;
        }
        localException.addOnSuccessListener(TaskExecutors.aJI, zzb.this);
        localException.addOnFailureListener(TaskExecutors.aJI, zzb.this);
      }
    });
  }
  
  public void onFailure(@NonNull Exception paramException)
  {
    this.aJv.setException(paramException);
  }
  
  public void onSuccess(TContinuationResult paramTContinuationResult)
  {
    this.aJv.setResult(paramTContinuationResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */