package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public class zzsv
  implements Executor
{
  private final Handler mHandler;
  
  public zzsv(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper);
  }
  
  public void execute(@NonNull Runnable paramRunnable)
  {
    this.mHandler.post(paramRunnable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */