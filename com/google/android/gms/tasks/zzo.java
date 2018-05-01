package com.google.android.gms.tasks;

import java.util.concurrent.Callable;

final class zzo
  implements Runnable
{
  zzo(zzn paramzzn, Callable paramCallable) {}
  
  public final void run()
  {
    try
    {
      this.zzkur.setResult(this.val$callable.call());
      return;
    }
    catch (Exception localException)
    {
      this.zzkur.setException(localException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */