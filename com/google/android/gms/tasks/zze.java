package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

final class zze<TResult>
  implements zzk<TResult>
{
  private final Object mLock = new Object();
  private final Executor zzkev;
  private OnCompleteListener<TResult> zzkud;
  
  public zze(Executor paramExecutor, OnCompleteListener<TResult> paramOnCompleteListener)
  {
    this.zzkev = paramExecutor;
    this.zzkud = paramOnCompleteListener;
  }
  
  public final void onComplete(Task<TResult> paramTask)
  {
    synchronized (this.mLock)
    {
      if (this.zzkud == null) {
        return;
      }
      this.zzkev.execute(new zzf(this, paramTask));
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */