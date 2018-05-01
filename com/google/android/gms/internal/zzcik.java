package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

final class zzcik<V>
  extends FutureTask<V>
  implements Comparable<zzcik>
{
  private final String zzjep;
  private final long zzjer;
  final boolean zzjes;
  
  zzcik(zzcih paramzzcih, Runnable paramRunnable, boolean paramBoolean, String paramString)
  {
    super(paramRunnable, null);
    zzbq.checkNotNull(paramString);
    this.zzjer = zzcih.zzazu().getAndIncrement();
    this.zzjep = paramString;
    this.zzjes = false;
    if (this.zzjer == Long.MAX_VALUE) {
      paramzzcih.zzawy().zzazd().log("Tasks index overflow");
    }
  }
  
  zzcik(Callable<V> paramCallable, boolean paramBoolean, String paramString)
  {
    super(paramBoolean);
    Object localObject;
    zzbq.checkNotNull(localObject);
    this.zzjer = zzcih.zzazu().getAndIncrement();
    this.zzjep = ((String)localObject);
    this.zzjes = paramString;
    if (this.zzjer == Long.MAX_VALUE) {
      paramCallable.zzawy().zzazd().log("Tasks index overflow");
    }
  }
  
  protected final void setException(Throwable paramThrowable)
  {
    this.zzjeq.zzawy().zzazd().zzj(this.zzjep, paramThrowable);
    if ((paramThrowable instanceof zzcii)) {
      Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), paramThrowable);
    }
    super.setException(paramThrowable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcik.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */