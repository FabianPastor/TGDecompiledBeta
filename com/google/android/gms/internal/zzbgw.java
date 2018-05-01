package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbgw
  implements ThreadFactory
{
  private final int mPriority;
  private final String zzaKe;
  private final AtomicInteger zzaKf = new AtomicInteger();
  private final ThreadFactory zzaKg = Executors.defaultThreadFactory();
  
  public zzbgw(String paramString)
  {
    this(paramString, 0);
  }
  
  private zzbgw(String paramString, int paramInt)
  {
    this.zzaKe = ((String)zzbo.zzb(paramString, "Name must not be null"));
    this.mPriority = 0;
  }
  
  public final Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = this.zzaKg.newThread(new zzbgx(paramRunnable, 0));
    String str = this.zzaKe;
    int i = this.zzaKf.getAndIncrement();
    paramRunnable.setName(String.valueOf(str).length() + 13 + str + "[" + i + "]");
    return paramRunnable;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */