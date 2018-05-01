package com.google.android.gms.internal;

import android.os.Process;

final class zzbhc
  implements Runnable
{
  private final int mPriority;
  private final Runnable zzz;
  
  public zzbhc(Runnable paramRunnable, int paramInt)
  {
    this.zzz = paramRunnable;
    this.mPriority = paramInt;
  }
  
  public final void run()
  {
    Process.setThreadPriority(this.mPriority);
    this.zzz.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */