package com.google.android.gms.tasks;

import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

final class zzg<TResult>
  implements zzq<TResult>
{
  private final Object mLock = new Object();
  private final Executor zzafk;
  @GuardedBy("mLock")
  private OnCanceledListener zzafq;
  
  public zzg(Executor paramExecutor, OnCanceledListener paramOnCanceledListener)
  {
    this.zzafk = paramExecutor;
    this.zzafq = paramOnCanceledListener;
  }
  
  public final void onComplete(Task paramTask)
  {
    if (paramTask.isCanceled()) {}
    synchronized (this.mLock)
    {
      if (this.zzafq == null) {
        return;
      }
      this.zzafk.execute(new zzh(this));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */