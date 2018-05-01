package com.google.android.gms.common.util.concurrent;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory
  implements ThreadFactory
{
  private final String name;
  private final int priority;
  private final ThreadFactory zzaau = Executors.defaultThreadFactory();
  
  public NamedThreadFactory(String paramString)
  {
    this(paramString, 0);
  }
  
  public NamedThreadFactory(String paramString, int paramInt)
  {
    this.name = ((String)Preconditions.checkNotNull(paramString, "Name must not be null"));
    this.priority = paramInt;
  }
  
  public Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = this.zzaau.newThread(new zza(paramRunnable, this.priority));
    paramRunnable.setName(this.name);
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/concurrent/NamedThreadFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */