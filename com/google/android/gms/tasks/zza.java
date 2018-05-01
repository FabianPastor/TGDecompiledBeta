package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

final class zza<TResult, TContinuationResult>
  implements zzk<TResult>
{
  private final Executor zzbEo;
  private final Continuation<TResult, TContinuationResult> zzbLR;
  private final zzn<TContinuationResult> zzbLS;
  
  public zza(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, TContinuationResult> paramContinuation, @NonNull zzn<TContinuationResult> paramzzn)
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
    this.zzbEo.execute(new zzb(this, paramTask));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */