package com.google.android.gms.tasks;

import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

final class zzk<TResult>
  implements zzq<TResult>
{
  private final Object mLock = new Object();
  private final Executor zzafk;
  @GuardedBy("mLock")
  private OnFailureListener zzafu;
  
  public zzk(Executor paramExecutor, OnFailureListener paramOnFailureListener)
  {
    this.zzafk = paramExecutor;
    this.zzafu = paramOnFailureListener;
  }
  
  public final void onComplete(Task<TResult> paramTask)
  {
    if ((!paramTask.isSuccessful()) && (!paramTask.isCanceled())) {}
    synchronized (this.mLock)
    {
      if (this.zzafu == null) {
        return;
      }
      this.zzafk.execute(new zzl(this, paramTask));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */