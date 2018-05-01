package com.google.android.gms.tasks;

import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

final class zzm<TResult>
  implements zzq<TResult>
{
  private final Object mLock = new Object();
  private final Executor zzafk;
  @GuardedBy("mLock")
  private OnSuccessListener<? super TResult> zzafw;
  
  public zzm(Executor paramExecutor, OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    this.zzafk = paramExecutor;
    this.zzafw = paramOnSuccessListener;
  }
  
  public final void onComplete(Task<TResult> paramTask)
  {
    if (paramTask.isSuccessful()) {}
    synchronized (this.mLock)
    {
      if (this.zzafw == null) {
        return;
      }
      this.zzafk.execute(new zzn(this, paramTask));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */