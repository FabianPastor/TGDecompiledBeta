package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

final class zzgj<V>
  extends FutureTask<V>
  implements Comparable<zzgj>
{
  private final String zzalw;
  private final long zzaly;
  final boolean zzalz;
  
  zzgj(zzgg paramzzgg, Runnable paramRunnable, boolean paramBoolean, String paramString)
  {
    super(paramRunnable, null);
    Preconditions.checkNotNull(paramString);
    this.zzaly = zzgg.zzji().getAndIncrement();
    this.zzalw = paramString;
    this.zzalz = false;
    if (this.zzaly == Long.MAX_VALUE) {
      paramzzgg.zzgg().zzil().log("Tasks index overflow");
    }
  }
  
  protected final void setException(Throwable paramThrowable)
  {
    this.zzalx.zzgg().zzil().zzg(this.zzalw, paramThrowable);
    if ((paramThrowable instanceof zzgh)) {
      Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), paramThrowable);
    }
    super.setException(paramThrowable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzgj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */