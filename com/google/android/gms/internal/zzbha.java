package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;

public final class zzbha
  implements Executor
{
  private final Handler mHandler;
  
  public zzbha(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper);
  }
  
  public final void execute(Runnable paramRunnable)
  {
    this.mHandler.post(paramRunnable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbha.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */