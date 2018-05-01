package com.google.android.gms.tasks;

import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

final class zzi<TResult>
  implements zzq<TResult>
{
  private final Object mLock = new Object();
  private final Executor zzafk;
  @GuardedBy("mLock")
  private OnCompleteListener<TResult> zzafs;
  
  public zzi(Executor paramExecutor, OnCompleteListener<TResult> paramOnCompleteListener)
  {
    this.zzafk = paramExecutor;
    this.zzafs = paramOnCompleteListener;
  }
  
  public final void onComplete(Task<TResult> paramTask)
  {
    synchronized (this.mLock)
    {
      if (this.zzafs == null) {
        return;
      }
      this.zzafk.execute(new zzj(this, paramTask));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */