package com.google.android.gms.common.util.concurrent;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;

public class HandlerExecutor
  implements Executor
{
  private final Handler handler;
  
  public HandlerExecutor(Looper paramLooper)
  {
    this.handler = new Handler(paramLooper);
  }
  
  public void execute(Runnable paramRunnable)
  {
    this.handler.post(paramRunnable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/concurrent/HandlerExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */