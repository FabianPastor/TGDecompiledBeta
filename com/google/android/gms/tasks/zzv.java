package com.google.android.gms.tasks;

import java.util.concurrent.Callable;

final class zzv
  implements Runnable
{
  zzv(zzu paramzzu, Callable paramCallable) {}
  
  public final void run()
  {
    try
    {
      this.zzagj.setResult(this.val$callable.call());
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        this.zzagj.setException(localException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */