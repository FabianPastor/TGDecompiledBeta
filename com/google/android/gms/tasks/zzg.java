package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

class zzg<TResult>
{
  private Queue<zzf<TResult>> aJF;
  private boolean aJG;
  private final Object zzakd = new Object();
  
  public void zza(@NonNull Task<TResult> paramTask)
  {
    for (;;)
    {
      zzf localzzf;
      synchronized (this.zzakd)
      {
        if ((this.aJF == null) || (this.aJG)) {
          return;
        }
        this.aJG = true;
        synchronized (this.zzakd)
        {
          localzzf = (zzf)this.aJF.poll();
          if (localzzf == null)
          {
            this.aJG = false;
            return;
          }
        }
      }
      localzzf.onComplete(paramTask);
    }
  }
  
  public void zza(@NonNull zzf<TResult> paramzzf)
  {
    synchronized (this.zzakd)
    {
      if (this.aJF == null) {
        this.aJF = new ArrayDeque();
      }
      this.aJF.add(paramzzf);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */