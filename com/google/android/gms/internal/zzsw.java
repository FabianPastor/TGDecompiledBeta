package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzaa;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class zzsw
  implements ThreadFactory
{
  private final String GM;
  private final AtomicInteger GN = new AtomicInteger();
  private final ThreadFactory GO = Executors.defaultThreadFactory();
  private final int mPriority;
  
  public zzsw(String paramString)
  {
    this(paramString, 0);
  }
  
  public zzsw(String paramString, int paramInt)
  {
    this.GM = ((String)zzaa.zzb(paramString, "Name must not be null"));
    this.mPriority = paramInt;
  }
  
  public Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = this.GO.newThread(new zzsx(paramRunnable, this.mPriority));
    String str = this.GM;
    int i = this.GN.getAndIncrement();
    paramRunnable.setName(String.valueOf(str).length() + 13 + str + "[" + i + "]");
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */