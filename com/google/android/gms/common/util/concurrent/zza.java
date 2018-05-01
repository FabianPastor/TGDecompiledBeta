package com.google.android.gms.common.util.concurrent;

import android.os.Process;

final class zza
  implements Runnable
{
  private final int priority;
  private final Runnable zzaax;
  
  public zza(Runnable paramRunnable, int paramInt)
  {
    this.zzaax = paramRunnable;
    this.priority = paramInt;
  }
  
  public final void run()
  {
    Process.setThreadPriority(this.priority);
    this.zzaax.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/concurrent/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */