package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

final class zzcij
  implements Thread.UncaughtExceptionHandler
{
  private final String zzjep;
  
  public zzcij(zzcih paramzzcih, String paramString)
  {
    zzbq.checkNotNull(paramString);
    this.zzjep = paramString;
  }
  
  public final void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    try
    {
      this.zzjeq.zzawy().zzazd().zzj(this.zzjep, paramThrowable);
      return;
    }
    finally
    {
      paramThread = finally;
      throw paramThread;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcij.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */