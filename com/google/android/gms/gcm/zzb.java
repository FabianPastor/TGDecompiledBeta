package com.google.android.gms.gcm;

import android.support.annotation.NonNull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class zzb
  implements ThreadFactory
{
  private final AtomicInteger zzbfH = new AtomicInteger(1);
  
  zzb(GcmTaskService paramGcmTaskService) {}
  
  public final Thread newThread(@NonNull Runnable paramRunnable)
  {
    int i = this.zzbfH.getAndIncrement();
    paramRunnable = new Thread(paramRunnable, 20 + "gcm-task#" + i);
    paramRunnable.setPriority(4);
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */