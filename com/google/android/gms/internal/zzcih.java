package com.google.android.gms.internal;

import android.os.Looper;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class zzcih
  extends zzcjl
{
  private static final AtomicLong zzjeo = new AtomicLong(Long.MIN_VALUE);
  private ExecutorService zzieo;
  private zzcil zzjef;
  private zzcil zzjeg;
  private final PriorityBlockingQueue<zzcik<?>> zzjeh = new PriorityBlockingQueue();
  private final BlockingQueue<zzcik<?>> zzjei = new LinkedBlockingQueue();
  private final Thread.UncaughtExceptionHandler zzjej = new zzcij(this, "Thread death: Uncaught exception on worker thread");
  private final Thread.UncaughtExceptionHandler zzjek = new zzcij(this, "Thread death: Uncaught exception on network thread");
  private final Object zzjel = new Object();
  private final Semaphore zzjem = new Semaphore(2);
  private volatile boolean zzjen;
  
  zzcih(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final void zza(zzcik<?> paramzzcik)
  {
    synchronized (this.zzjel)
    {
      this.zzjeh.add(paramzzcik);
      if (this.zzjef == null)
      {
        this.zzjef = new zzcil(this, "Measurement Worker", this.zzjeh);
        this.zzjef.setUncaughtExceptionHandler(this.zzjej);
        this.zzjef.start();
        return;
      }
      this.zzjef.zzrk();
    }
  }
  
  public static boolean zzau()
  {
    return Looper.myLooper() == Looper.getMainLooper();
  }
  
  public final void zzawj()
  {
    if (Thread.currentThread() != this.zzjeg) {
      throw new IllegalStateException("Call expected from network thread");
    }
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  public final boolean zzazs()
  {
    return Thread.currentThread() == this.zzjef;
  }
  
  final ExecutorService zzazt()
  {
    synchronized (this.zzjel)
    {
      if (this.zzieo == null) {
        this.zzieo = new ThreadPoolExecutor(0, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
      }
      ExecutorService localExecutorService = this.zzieo;
      return localExecutorService;
    }
  }
  
  public final <V> Future<V> zzc(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzxf();
    zzbq.checkNotNull(paramCallable);
    paramCallable = new zzcik(this, paramCallable, false, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzjef)
    {
      if (!this.zzjeh.isEmpty()) {
        zzawy().zzazf().log("Callable skipped the worker queue.");
      }
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public final <V> Future<V> zzd(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzxf();
    zzbq.checkNotNull(paramCallable);
    paramCallable = new zzcik(this, paramCallable, true, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzjef)
    {
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public final void zzg(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzxf();
    zzbq.checkNotNull(paramRunnable);
    zza(new zzcik(this, paramRunnable, false, "Task exception on worker thread"));
  }
  
  public final void zzh(Runnable arg1)
    throws IllegalStateException
  {
    zzxf();
    zzbq.checkNotNull(???);
    zzcik localzzcik = new zzcik(this, ???, false, "Task exception on network thread");
    synchronized (this.zzjel)
    {
      this.zzjei.add(localzzcik);
      if (this.zzjeg == null)
      {
        this.zzjeg = new zzcil(this, "Measurement Network", this.zzjei);
        this.zzjeg.setUncaughtExceptionHandler(this.zzjek);
        this.zzjeg.start();
        return;
      }
      this.zzjeg.zzrk();
    }
  }
  
  public final void zzve()
  {
    if (Thread.currentThread() != this.zzjef) {
      throw new IllegalStateException("Call expected from worker thread");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcih.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */