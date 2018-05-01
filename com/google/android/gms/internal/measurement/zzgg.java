package com.google.android.gms.internal.measurement;

import android.os.Looper;
import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class zzgg
  extends zzhk
{
  private static final AtomicLong zzalv = new AtomicLong(Long.MIN_VALUE);
  private ExecutorService zzall;
  private zzgk zzalm;
  private zzgk zzaln;
  private final PriorityBlockingQueue<zzgj<?>> zzalo = new PriorityBlockingQueue();
  private final BlockingQueue<zzgj<?>> zzalp = new LinkedBlockingQueue();
  private final Thread.UncaughtExceptionHandler zzalq = new zzgi(this, "Thread death: Uncaught exception on worker thread");
  private final Thread.UncaughtExceptionHandler zzalr = new zzgi(this, "Thread death: Uncaught exception on network thread");
  private final Object zzals = new Object();
  private final Semaphore zzalt = new Semaphore(2);
  private volatile boolean zzalu;
  
  zzgg(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  public static boolean isMainThread()
  {
    if (Looper.myLooper() == Looper.getMainLooper()) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final void zza(zzgj<?> paramzzgj)
  {
    synchronized (this.zzals)
    {
      this.zzalo.add(paramzzgj);
      if (this.zzalm == null)
      {
        paramzzgj = new com/google/android/gms/internal/measurement/zzgk;
        paramzzgj.<init>(this, "Measurement Worker", this.zzalo);
        this.zzalm = paramzzgj;
        this.zzalm.setUncaughtExceptionHandler(this.zzalq);
        this.zzalm.start();
        return;
      }
      this.zzalm.zzjj();
    }
  }
  
  public final void zzab()
  {
    if (Thread.currentThread() != this.zzalm) {
      throw new IllegalStateException("Call expected from worker thread");
    }
  }
  
  public final void zzc(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzch();
    Preconditions.checkNotNull(paramRunnable);
    zza(new zzgj(this, paramRunnable, false, "Task exception on worker thread"));
  }
  
  public final void zzd(Runnable arg1)
    throws IllegalStateException
  {
    zzch();
    Preconditions.checkNotNull(???);
    Object localObject1 = new zzgj(this, ???, false, "Task exception on network thread");
    synchronized (this.zzals)
    {
      this.zzalp.add(localObject1);
      if (this.zzaln == null)
      {
        localObject1 = new com/google/android/gms/internal/measurement/zzgk;
        ((zzgk)localObject1).<init>(this, "Measurement Network", this.zzalp);
        this.zzaln = ((zzgk)localObject1);
        this.zzaln.setUncaughtExceptionHandler(this.zzalr);
        this.zzaln.start();
        return;
      }
      this.zzaln.zzjj();
    }
  }
  
  public final void zzfr()
  {
    if (Thread.currentThread() != this.zzaln) {
      throw new IllegalStateException("Call expected from network thread");
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  public final boolean zzjg()
  {
    if (Thread.currentThread() == this.zzalm) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  final ExecutorService zzjh()
  {
    synchronized (this.zzals)
    {
      if (this.zzall == null)
      {
        ThreadPoolExecutor localThreadPoolExecutor = new java/util/concurrent/ThreadPoolExecutor;
        TimeUnit localTimeUnit = TimeUnit.SECONDS;
        localObject2 = new java/util/concurrent/ArrayBlockingQueue;
        ((ArrayBlockingQueue)localObject2).<init>(100);
        localThreadPoolExecutor.<init>(0, 1, 30L, localTimeUnit, (BlockingQueue)localObject2);
        this.zzall = localThreadPoolExecutor;
      }
      Object localObject2 = this.zzall;
      return (ExecutorService)localObject2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzgg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */