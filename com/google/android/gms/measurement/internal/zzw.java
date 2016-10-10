package com.google.android.gms.measurement.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class zzw
  extends zzaa
{
  private static final AtomicLong aqI = new AtomicLong(Long.MIN_VALUE);
  private zzd aqA;
  private final PriorityBlockingQueue<FutureTask<?>> aqB = new PriorityBlockingQueue();
  private final BlockingQueue<FutureTask<?>> aqC = new LinkedBlockingQueue();
  private final Thread.UncaughtExceptionHandler aqD = new zzb("Thread death: Uncaught exception on worker thread");
  private final Thread.UncaughtExceptionHandler aqE = new zzb("Thread death: Uncaught exception on network thread");
  private final Object aqF = new Object();
  private final Semaphore aqG = new Semaphore(2);
  private volatile boolean aqH;
  private zzd aqz;
  
  zzw(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private void zza(zzc<?> paramzzc)
  {
    synchronized (this.aqF)
    {
      this.aqB.add(paramzzc);
      if (this.aqz == null)
      {
        this.aqz = new zzd("Measurement Worker", this.aqB);
        this.aqz.setUncaughtExceptionHandler(this.aqD);
        this.aqz.start();
        return;
      }
      this.aqz.zzoi();
    }
  }
  
  private void zza(FutureTask<?> paramFutureTask)
  {
    synchronized (this.aqF)
    {
      this.aqC.add(paramFutureTask);
      if (this.aqA == null)
      {
        this.aqA = new zzd("Measurement Network", this.aqC);
        this.aqA.setUncaughtExceptionHandler(this.aqE);
        this.aqA.start();
        return;
      }
      this.aqA.zzoi();
    }
  }
  
  public void zzbuv()
  {
    if (Thread.currentThread() != this.aqA) {
      throw new IllegalStateException("Call expected from network thread");
    }
  }
  
  public <V> Future<V> zzd(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzaax();
    zzac.zzy(paramCallable);
    paramCallable = new zzc(paramCallable, false, "Task exception on worker thread");
    if (Thread.currentThread() == this.aqz)
    {
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public <V> Future<V> zze(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzaax();
    zzac.zzy(paramCallable);
    paramCallable = new zzc(paramCallable, true, "Task exception on worker thread");
    if (Thread.currentThread() == this.aqz)
    {
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public void zzm(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzaax();
    zzac.zzy(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on worker thread"));
  }
  
  public void zzn(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzaax();
    zzac.zzy(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on network thread"));
  }
  
  public void zzyl()
  {
    if (Thread.currentThread() != this.aqz) {
      throw new IllegalStateException("Call expected from worker thread");
    }
  }
  
  protected void zzym() {}
  
  static class zza
    extends RuntimeException
  {}
  
  private final class zzb
    implements Thread.UncaughtExceptionHandler
  {
    private final String aqJ;
    
    public zzb(String paramString)
    {
      zzac.zzy(paramString);
      this.aqJ = paramString;
    }
    
    public void uncaughtException(Thread paramThread, Throwable paramThrowable)
    {
      try
      {
        zzw.this.zzbvg().zzbwc().zzj(this.aqJ, paramThrowable);
        return;
      }
      finally
      {
        paramThread = finally;
        throw paramThread;
      }
    }
  }
  
  private final class zzc<V>
    extends FutureTask<V>
    implements Comparable<zzc>
  {
    private final String aqJ;
    private final long aqL;
    private final boolean aqM;
    
    zzc(Runnable paramRunnable, boolean paramBoolean, String paramString)
    {
      super(null);
      zzac.zzy(paramString);
      this.aqL = zzw.zzbwu().getAndIncrement();
      this.aqJ = paramString;
      this.aqM = paramBoolean;
      if (this.aqL == Long.MAX_VALUE) {
        zzw.this.zzbvg().zzbwc().log("Tasks index overflow");
      }
    }
    
    zzc(boolean paramBoolean, String paramString)
    {
      super();
      Object localObject;
      zzac.zzy(localObject);
      this.aqL = zzw.zzbwu().getAndIncrement();
      this.aqJ = ((String)localObject);
      this.aqM = paramString;
      if (this.aqL == Long.MAX_VALUE) {
        zzw.this.zzbvg().zzbwc().log("Tasks index overflow");
      }
    }
    
    protected void setException(Throwable paramThrowable)
    {
      zzw.this.zzbvg().zzbwc().zzj(this.aqJ, paramThrowable);
      if ((paramThrowable instanceof zzw.zza)) {
        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), paramThrowable);
      }
      super.setException(paramThrowable);
    }
    
    public int zzb(@NonNull zzc paramzzc)
    {
      if (this.aqM != paramzzc.aqM) {
        if (!this.aqM) {}
      }
      while (this.aqL < paramzzc.aqL)
      {
        return -1;
        return 1;
      }
      if (this.aqL > paramzzc.aqL) {
        return 1;
      }
      zzw.this.zzbvg().zzbwd().zzj("Two tasks share the same index. index", Long.valueOf(this.aqL));
      return 0;
    }
  }
  
  private final class zzd
    extends Thread
  {
    private final Object aqN;
    private final BlockingQueue<FutureTask<?>> aqO;
    
    public zzd(BlockingQueue<FutureTask<?>> paramBlockingQueue)
    {
      zzac.zzy(paramBlockingQueue);
      Object localObject;
      zzac.zzy(localObject);
      this.aqN = new Object();
      this.aqO = ((BlockingQueue)localObject);
      setName(paramBlockingQueue);
    }
    
    private void zza(InterruptedException paramInterruptedException)
    {
      zzw.this.zzbvg().zzbwe().zzj(String.valueOf(getName()).concat(" was interrupted"), paramInterruptedException);
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_1
      //   2: iload_1
      //   3: ifne +27 -> 30
      //   6: aload_0
      //   7: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   10: invokestatic 81	com/google/android/gms/measurement/internal/zzw:zza	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/util/concurrent/Semaphore;
      //   13: invokevirtual 86	java/util/concurrent/Semaphore:acquire	()V
      //   16: iconst_1
      //   17: istore_1
      //   18: goto -16 -> 2
      //   21: astore_3
      //   22: aload_0
      //   23: aload_3
      //   24: invokespecial 88	com/google/android/gms/measurement/internal/zzw$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   27: goto -25 -> 2
      //   30: aload_0
      //   31: getfield 34	com/google/android/gms/measurement/internal/zzw$zzd:aqO	Ljava/util/concurrent/BlockingQueue;
      //   34: invokeinterface 94 1 0
      //   39: checkcast 96	java/util/concurrent/FutureTask
      //   42: astore_3
      //   43: aload_3
      //   44: ifnull +67 -> 111
      //   47: aload_3
      //   48: invokevirtual 98	java/util/concurrent/FutureTask:run	()V
      //   51: goto -21 -> 30
      //   54: astore 4
      //   56: aload_0
      //   57: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   60: invokestatic 102	com/google/android/gms/measurement/internal/zzw:zzc	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/lang/Object;
      //   63: astore_3
      //   64: aload_3
      //   65: monitorenter
      //   66: aload_0
      //   67: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   70: invokestatic 81	com/google/android/gms/measurement/internal/zzw:zza	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/util/concurrent/Semaphore;
      //   73: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   76: aload_0
      //   77: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   80: invokestatic 102	com/google/android/gms/measurement/internal/zzw:zzc	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/lang/Object;
      //   83: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   86: aload_0
      //   87: aload_0
      //   88: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   91: invokestatic 111	com/google/android/gms/measurement/internal/zzw:zzd	(Lcom/google/android/gms/measurement/internal/zzw;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   94: if_acmpne +215 -> 309
      //   97: aload_0
      //   98: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   101: aconst_null
      //   102: invokestatic 114	com/google/android/gms/measurement/internal/zzw:zza	(Lcom/google/android/gms/measurement/internal/zzw;Lcom/google/android/gms/measurement/internal/zzw$zzd;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   105: pop
      //   106: aload_3
      //   107: monitorexit
      //   108: aload 4
      //   110: athrow
      //   111: aload_0
      //   112: getfield 32	com/google/android/gms/measurement/internal/zzw$zzd:aqN	Ljava/lang/Object;
      //   115: astore_3
      //   116: aload_3
      //   117: monitorenter
      //   118: aload_0
      //   119: getfield 34	com/google/android/gms/measurement/internal/zzw$zzd:aqO	Ljava/util/concurrent/BlockingQueue;
      //   122: invokeinterface 117 1 0
      //   127: ifnonnull +25 -> 152
      //   130: aload_0
      //   131: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   134: invokestatic 121	com/google/android/gms/measurement/internal/zzw:zzb	(Lcom/google/android/gms/measurement/internal/zzw;)Z
      //   137: istore_2
      //   138: iload_2
      //   139: ifne +13 -> 152
      //   142: aload_0
      //   143: getfield 32	com/google/android/gms/measurement/internal/zzw$zzd:aqN	Ljava/lang/Object;
      //   146: ldc2_w 122
      //   149: invokevirtual 127	java/lang/Object:wait	(J)V
      //   152: aload_3
      //   153: monitorexit
      //   154: aload_0
      //   155: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   158: invokestatic 102	com/google/android/gms/measurement/internal/zzw:zzc	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/lang/Object;
      //   161: astore_3
      //   162: aload_3
      //   163: monitorenter
      //   164: aload_0
      //   165: getfield 34	com/google/android/gms/measurement/internal/zzw$zzd:aqO	Ljava/util/concurrent/BlockingQueue;
      //   168: invokeinterface 117 1 0
      //   173: ifnonnull +124 -> 297
      //   176: aload_3
      //   177: monitorexit
      //   178: aload_0
      //   179: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   182: invokestatic 102	com/google/android/gms/measurement/internal/zzw:zzc	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/lang/Object;
      //   185: astore_3
      //   186: aload_3
      //   187: monitorenter
      //   188: aload_0
      //   189: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   192: invokestatic 81	com/google/android/gms/measurement/internal/zzw:zza	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/util/concurrent/Semaphore;
      //   195: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   198: aload_0
      //   199: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   202: invokestatic 102	com/google/android/gms/measurement/internal/zzw:zzc	(Lcom/google/android/gms/measurement/internal/zzw;)Ljava/lang/Object;
      //   205: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   208: aload_0
      //   209: aload_0
      //   210: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   213: invokestatic 111	com/google/android/gms/measurement/internal/zzw:zzd	(Lcom/google/android/gms/measurement/internal/zzw;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   216: if_acmpne +33 -> 249
      //   219: aload_0
      //   220: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   223: aconst_null
      //   224: invokestatic 114	com/google/android/gms/measurement/internal/zzw:zza	(Lcom/google/android/gms/measurement/internal/zzw;Lcom/google/android/gms/measurement/internal/zzw$zzd;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   227: pop
      //   228: aload_3
      //   229: monitorexit
      //   230: return
      //   231: astore 4
      //   233: aload_0
      //   234: aload 4
      //   236: invokespecial 88	com/google/android/gms/measurement/internal/zzw$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   239: goto -87 -> 152
      //   242: astore 4
      //   244: aload_3
      //   245: monitorexit
      //   246: aload 4
      //   248: athrow
      //   249: aload_0
      //   250: aload_0
      //   251: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   254: invokestatic 130	com/google/android/gms/measurement/internal/zzw:zze	(Lcom/google/android/gms/measurement/internal/zzw;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   257: if_acmpne +22 -> 279
      //   260: aload_0
      //   261: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   264: aconst_null
      //   265: invokestatic 132	com/google/android/gms/measurement/internal/zzw:zzb	(Lcom/google/android/gms/measurement/internal/zzw;Lcom/google/android/gms/measurement/internal/zzw$zzd;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   268: pop
      //   269: goto -41 -> 228
      //   272: astore 4
      //   274: aload_3
      //   275: monitorexit
      //   276: aload 4
      //   278: athrow
      //   279: aload_0
      //   280: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   283: invokevirtual 47	com/google/android/gms/measurement/internal/zzw:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   286: invokevirtual 135	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   289: ldc -119
      //   291: invokevirtual 140	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
      //   294: goto -66 -> 228
      //   297: aload_3
      //   298: monitorexit
      //   299: goto -269 -> 30
      //   302: astore 4
      //   304: aload_3
      //   305: monitorexit
      //   306: aload 4
      //   308: athrow
      //   309: aload_0
      //   310: aload_0
      //   311: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   314: invokestatic 130	com/google/android/gms/measurement/internal/zzw:zze	(Lcom/google/android/gms/measurement/internal/zzw;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   317: if_acmpne +22 -> 339
      //   320: aload_0
      //   321: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   324: aconst_null
      //   325: invokestatic 132	com/google/android/gms/measurement/internal/zzw:zzb	(Lcom/google/android/gms/measurement/internal/zzw;Lcom/google/android/gms/measurement/internal/zzw$zzd;)Lcom/google/android/gms/measurement/internal/zzw$zzd;
      //   328: pop
      //   329: goto -223 -> 106
      //   332: astore 4
      //   334: aload_3
      //   335: monitorexit
      //   336: aload 4
      //   338: athrow
      //   339: aload_0
      //   340: getfield 18	com/google/android/gms/measurement/internal/zzw$zzd:aqK	Lcom/google/android/gms/measurement/internal/zzw;
      //   343: invokevirtual 47	com/google/android/gms/measurement/internal/zzw:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   346: invokevirtual 135	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   349: ldc -119
      //   351: invokevirtual 140	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
      //   354: goto -248 -> 106
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	357	0	this	zzd
      //   1	17	1	i	int
      //   137	2	2	bool	boolean
      //   21	3	3	localInterruptedException1	InterruptedException
      //   54	55	4	localObject2	Object
      //   231	4	4	localInterruptedException2	InterruptedException
      //   242	5	4	localObject3	Object
      //   272	5	4	localObject4	Object
      //   302	5	4	localObject5	Object
      //   332	5	4	localObject6	Object
      // Exception table:
      //   from	to	target	type
      //   6	16	21	java/lang/InterruptedException
      //   30	43	54	finally
      //   47	51	54	finally
      //   111	118	54	finally
      //   154	164	54	finally
      //   246	249	54	finally
      //   306	309	54	finally
      //   142	152	231	java/lang/InterruptedException
      //   118	138	242	finally
      //   142	152	242	finally
      //   152	154	242	finally
      //   233	239	242	finally
      //   244	246	242	finally
      //   188	228	272	finally
      //   228	230	272	finally
      //   249	269	272	finally
      //   274	276	272	finally
      //   279	294	272	finally
      //   164	178	302	finally
      //   297	299	302	finally
      //   304	306	302	finally
      //   66	106	332	finally
      //   106	108	332	finally
      //   309	329	332	finally
      //   334	336	332	finally
      //   339	354	332	finally
    }
    
    public void zzoi()
    {
      synchronized (this.aqN)
      {
        this.aqN.notifyAll();
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */