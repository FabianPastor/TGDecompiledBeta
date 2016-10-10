package com.google.android.gms.internal;

import android.os.Process;

class zzsg
  implements Runnable
{
  private final int mPriority;
  private final Runnable zzw;
  
  public zzsg(Runnable paramRunnable, int paramInt)
  {
    this.zzw = paramRunnable;
    this.mPriority = paramInt;
  }
  
  public void run()
  {
    Process.setThreadPriority(this.mPriority);
    this.zzw.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */