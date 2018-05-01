package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbhb
  implements ThreadFactory
{
  private final int mPriority;
  private final String zzgfb;
  private final AtomicInteger zzgfc = new AtomicInteger();
  private final ThreadFactory zzgfd = Executors.defaultThreadFactory();
  
  public zzbhb(String paramString)
  {
    this(paramString, 0);
  }
  
  private zzbhb(String paramString, int paramInt)
  {
    this.zzgfb = ((String)zzbq.checkNotNull(paramString, "Name must not be null"));
    this.mPriority = 0;
  }
  
  public final Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = this.zzgfd.newThread(new zzbhc(paramRunnable, 0));
    String str = this.zzgfb;
    int i = this.zzgfc.getAndIncrement();
    paramRunnable.setName(String.valueOf(str).length() + 13 + str + "[" + i + "]");
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */