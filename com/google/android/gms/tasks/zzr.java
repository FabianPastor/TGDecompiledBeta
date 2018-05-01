package com.google.android.gms.tasks;

import java.util.ArrayDeque;
import java.util.Queue;
import javax.annotation.concurrent.GuardedBy;

final class zzr<TResult>
{
  private final Object mLock = new Object();
  @GuardedBy("mLock")
  private Queue<zzq<TResult>> zzaga;
  @GuardedBy("mLock")
  private boolean zzagb;
  
  public final void zza(Task<TResult> paramTask)
  {
    for (;;)
    {
      zzq localzzq;
      synchronized (this.mLock)
      {
        if ((this.zzaga == null) || (this.zzagb)) {
          return;
        }
        this.zzagb = true;
        synchronized (this.mLock)
        {
          localzzq = (zzq)this.zzaga.poll();
          if (localzzq == null) {
            this.zzagb = false;
          }
        }
      }
      localzzq.onComplete(paramTask);
    }
  }
  
  public final void zza(zzq<TResult> paramzzq)
  {
    synchronized (this.mLock)
    {
      if (this.zzaga == null)
      {
        ArrayDeque localArrayDeque = new java/util/ArrayDeque;
        localArrayDeque.<init>();
        this.zzaga = localArrayDeque;
      }
      this.zzaga.add(paramzzq);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */