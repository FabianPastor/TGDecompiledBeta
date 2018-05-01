package com.google.android.gms.common.util.concurrent;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberedThreadFactory
  implements ThreadFactory
{
  private final int priority;
  private final ThreadFactory zzaau = Executors.defaultThreadFactory();
  private final String zzaav;
  private final AtomicInteger zzaaw = new AtomicInteger();
  
  public NumberedThreadFactory(String paramString)
  {
    this(paramString, 0);
  }
  
  public NumberedThreadFactory(String paramString, int paramInt)
  {
    this.zzaav = ((String)Preconditions.checkNotNull(paramString, "Name must not be null"));
    this.priority = paramInt;
  }
  
  public Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = this.zzaau.newThread(new zza(paramRunnable, this.priority));
    String str = this.zzaav;
    int i = this.zzaaw.getAndIncrement();
    paramRunnable.setName(String.valueOf(str).length() + 13 + str + "[" + i + "]");
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/concurrent/NumberedThreadFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */