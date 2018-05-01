package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

final class zzc<TResult, TContinuationResult>
  implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzk<TResult>
{
  private final Executor zzbEo;
  private final Continuation<TResult, Task<TContinuationResult>> zzbLR;
  private final zzn<TContinuationResult> zzbLS;
  
  public zzc(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation, @NonNull zzn<TContinuationResult> paramzzn)
  {
    this.zzbEo = paramExecutor;
    this.zzbLR = paramContinuation;
    this.zzbLS = paramzzn;
  }
  
  public final void cancel()
  {
    throw new UnsupportedOperationException();
  }
  
  public final void onComplete(@NonNull Task<TResult> paramTask)
  {
    this.zzbEo.execute(new zzd(this, paramTask));
  }
  
  public final void onFailure(@NonNull Exception paramException)
  {
    this.zzbLS.setException(paramException);
  }
  
  public final void onSuccess(TContinuationResult paramTContinuationResult)
  {
    this.zzbLS.setResult(paramTContinuationResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */