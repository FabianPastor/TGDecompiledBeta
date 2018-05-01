package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class zzaud
  extends zzauh
{
  private static final AtomicLong zzbtU = new AtomicLong(Long.MIN_VALUE);
  private ExecutorService zzbtK;
  private zzd zzbtL;
  private zzd zzbtM;
  private final PriorityBlockingQueue<FutureTask<?>> zzbtN = new PriorityBlockingQueue();
  private final BlockingQueue<FutureTask<?>> zzbtO = new LinkedBlockingQueue();
  private final Thread.UncaughtExceptionHandler zzbtP = new zzb("Thread death: Uncaught exception on worker thread");
  private final Thread.UncaughtExceptionHandler zzbtQ = new zzb("Thread death: Uncaught exception on network thread");
  private final Object zzbtR = new Object();
  private final Semaphore zzbtS = new Semaphore(2);
  private volatile boolean zzbtT;
  
  zzaud(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  private void zza(zzc<?> paramzzc)
  {
    synchronized (this.zzbtR)
    {
      this.zzbtN.add(paramzzc);
      if (this.zzbtL == null)
      {
        this.zzbtL = new zzd("Measurement Worker", this.zzbtN);
        this.zzbtL.setUncaughtExceptionHandler(this.zzbtP);
        this.zzbtL.start();
        return;
      }
      this.zzbtL.zzhA();
    }
  }
  
  private void zza(FutureTask<?> paramFutureTask)
  {
    synchronized (this.zzbtR)
    {
      this.zzbtO.add(paramFutureTask);
      if (this.zzbtM == null)
      {
        this.zzbtM = new zzd("Measurement Network", this.zzbtO);
        this.zzbtM.setUncaughtExceptionHandler(this.zzbtQ);
        this.zzbtM.start();
        return;
      }
      this.zzbtM.zzhA();
    }
  }
  
  public void zzJX()
  {
    if (Thread.currentThread() != this.zzbtM) {
      throw new IllegalStateException("Call expected from network thread");
    }
  }
  
  public boolean zzMr()
  {
    return Thread.currentThread() == this.zzbtL;
  }
  
  ExecutorService zzMs()
  {
    synchronized (this.zzbtR)
    {
      if (this.zzbtK == null) {
        this.zzbtK = new ThreadPoolExecutor(0, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
      }
      ExecutorService localExecutorService = this.zzbtK;
      return localExecutorService;
    }
  }
  
  public boolean zzbc()
  {
    return Looper.myLooper() == Looper.getMainLooper();
  }
  
  public <V> Future<V> zzd(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzob();
    zzac.zzw(paramCallable);
    paramCallable = new zzc(paramCallable, false, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzbtL)
    {
      if (!this.zzbtN.isEmpty()) {
        zzKl().zzMb().log("Callable skipped the worker queue.");
      }
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public <V> Future<V> zze(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zzob();
    zzac.zzw(paramCallable);
    paramCallable = new zzc(paramCallable, true, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzbtL)
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
    zzob();
    zzac.zzw(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on worker thread"));
  }
  
  public void zzmR()
  {
    if (Thread.currentThread() != this.zzbtL) {
      throw new IllegalStateException("Call expected from worker thread");
    }
  }
  
  protected void zzmS() {}
  
  public void zzn(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzob();
    zzac.zzw(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on network thread"));
  }
  
  static class zza
    extends RuntimeException
  {}
  
  private final class zzb
    implements Thread.UncaughtExceptionHandler
  {
    private final String zzbtV;
    
    public zzb(String paramString)
    {
      zzac.zzw(paramString);
      this.zzbtV = paramString;
    }
    
    public void uncaughtException(Thread paramThread, Throwable paramThrowable)
    {
      try
      {
        zzaud.this.zzKl().zzLZ().zzj(this.zzbtV, paramThrowable);
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
    private final String zzbtV;
    private final long zzbtX;
    private final boolean zzbtY;
    
    zzc(Runnable paramRunnable, boolean paramBoolean, String paramString)
    {
      super(null);
      zzac.zzw(paramString);
      this.zzbtX = zzaud.zzMt().getAndIncrement();
      this.zzbtV = paramString;
      this.zzbtY = paramBoolean;
      if (this.zzbtX == Long.MAX_VALUE) {
        zzaud.this.zzKl().zzLZ().log("Tasks index overflow");
      }
    }
    
    zzc(boolean paramBoolean, String paramString)
    {
      super();
      Object localObject;
      zzac.zzw(localObject);
      this.zzbtX = zzaud.zzMt().getAndIncrement();
      this.zzbtV = ((String)localObject);
      this.zzbtY = paramString;
      if (this.zzbtX == Long.MAX_VALUE) {
        zzaud.this.zzKl().zzLZ().log("Tasks index overflow");
      }
    }
    
    protected void setException(Throwable paramThrowable)
    {
      zzaud.this.zzKl().zzLZ().zzj(this.zzbtV, paramThrowable);
      if ((paramThrowable instanceof zzaud.zza)) {
        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), paramThrowable);
      }
      super.setException(paramThrowable);
    }
    
    public int zzb(@NonNull zzc paramzzc)
    {
      if (this.zzbtY != paramzzc.zzbtY) {
        if (!this.zzbtY) {}
      }
      while (this.zzbtX < paramzzc.zzbtX)
      {
        return -1;
        return 1;
      }
      if (this.zzbtX > paramzzc.zzbtX) {
        return 1;
      }
      zzaud.this.zzKl().zzMa().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbtX));
      return 0;
    }
  }
  
  private final class zzd
    extends Thread
  {
    private final Object zzbtZ;
    private final BlockingQueue<FutureTask<?>> zzbua;
    
    public zzd(BlockingQueue<FutureTask<?>> paramBlockingQueue)
    {
      zzac.zzw(paramBlockingQueue);
      Object localObject;
      zzac.zzw(localObject);
      this.zzbtZ = new Object();
      this.zzbua = ((BlockingQueue)localObject);
      setName(paramBlockingQueue);
    }
    
    private void zza(InterruptedException paramInterruptedException)
    {
      zzaud.this.zzKl().zzMb().zzj(String.valueOf(getName()).concat(" was interrupted"), paramInterruptedException);
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
      //   7: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   10: invokestatic 81	com/google/android/gms/internal/zzaud:zza	(Lcom/google/android/gms/internal/zzaud;)Ljava/util/concurrent/Semaphore;
      //   13: invokevirtual 86	java/util/concurrent/Semaphore:acquire	()V
      //   16: iconst_1
      //   17: istore_1
      //   18: goto -16 -> 2
      //   21: astore_3
      //   22: aload_0
      //   23: aload_3
      //   24: invokespecial 88	com/google/android/gms/internal/zzaud$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   27: goto -25 -> 2
      //   30: aload_0
      //   31: getfield 34	com/google/android/gms/internal/zzaud$zzd:zzbua	Ljava/util/concurrent/BlockingQueue;
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
      //   57: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   60: invokestatic 102	com/google/android/gms/internal/zzaud:zzc	(Lcom/google/android/gms/internal/zzaud;)Ljava/lang/Object;
      //   63: astore_3
      //   64: aload_3
      //   65: monitorenter
      //   66: aload_0
      //   67: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   70: invokestatic 81	com/google/android/gms/internal/zzaud:zza	(Lcom/google/android/gms/internal/zzaud;)Ljava/util/concurrent/Semaphore;
      //   73: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   76: aload_0
      //   77: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   80: invokestatic 102	com/google/android/gms/internal/zzaud:zzc	(Lcom/google/android/gms/internal/zzaud;)Ljava/lang/Object;
      //   83: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   86: aload_0
      //   87: aload_0
      //   88: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   91: invokestatic 111	com/google/android/gms/internal/zzaud:zzd	(Lcom/google/android/gms/internal/zzaud;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   94: if_acmpne +215 -> 309
      //   97: aload_0
      //   98: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   101: aconst_null
      //   102: invokestatic 114	com/google/android/gms/internal/zzaud:zza	(Lcom/google/android/gms/internal/zzaud;Lcom/google/android/gms/internal/zzaud$zzd;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   105: pop
      //   106: aload_3
      //   107: monitorexit
      //   108: aload 4
      //   110: athrow
      //   111: aload_0
      //   112: getfield 32	com/google/android/gms/internal/zzaud$zzd:zzbtZ	Ljava/lang/Object;
      //   115: astore_3
      //   116: aload_3
      //   117: monitorenter
      //   118: aload_0
      //   119: getfield 34	com/google/android/gms/internal/zzaud$zzd:zzbua	Ljava/util/concurrent/BlockingQueue;
      //   122: invokeinterface 117 1 0
      //   127: ifnonnull +25 -> 152
      //   130: aload_0
      //   131: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   134: invokestatic 121	com/google/android/gms/internal/zzaud:zzb	(Lcom/google/android/gms/internal/zzaud;)Z
      //   137: istore_2
      //   138: iload_2
      //   139: ifne +13 -> 152
      //   142: aload_0
      //   143: getfield 32	com/google/android/gms/internal/zzaud$zzd:zzbtZ	Ljava/lang/Object;
      //   146: ldc2_w 122
      //   149: invokevirtual 127	java/lang/Object:wait	(J)V
      //   152: aload_3
      //   153: monitorexit
      //   154: aload_0
      //   155: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   158: invokestatic 102	com/google/android/gms/internal/zzaud:zzc	(Lcom/google/android/gms/internal/zzaud;)Ljava/lang/Object;
      //   161: astore_3
      //   162: aload_3
      //   163: monitorenter
      //   164: aload_0
      //   165: getfield 34	com/google/android/gms/internal/zzaud$zzd:zzbua	Ljava/util/concurrent/BlockingQueue;
      //   168: invokeinterface 117 1 0
      //   173: ifnonnull +124 -> 297
      //   176: aload_3
      //   177: monitorexit
      //   178: aload_0
      //   179: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   182: invokestatic 102	com/google/android/gms/internal/zzaud:zzc	(Lcom/google/android/gms/internal/zzaud;)Ljava/lang/Object;
      //   185: astore_3
      //   186: aload_3
      //   187: monitorenter
      //   188: aload_0
      //   189: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   192: invokestatic 81	com/google/android/gms/internal/zzaud:zza	(Lcom/google/android/gms/internal/zzaud;)Ljava/util/concurrent/Semaphore;
      //   195: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   198: aload_0
      //   199: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   202: invokestatic 102	com/google/android/gms/internal/zzaud:zzc	(Lcom/google/android/gms/internal/zzaud;)Ljava/lang/Object;
      //   205: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   208: aload_0
      //   209: aload_0
      //   210: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   213: invokestatic 111	com/google/android/gms/internal/zzaud:zzd	(Lcom/google/android/gms/internal/zzaud;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   216: if_acmpne +33 -> 249
      //   219: aload_0
      //   220: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   223: aconst_null
      //   224: invokestatic 114	com/google/android/gms/internal/zzaud:zza	(Lcom/google/android/gms/internal/zzaud;Lcom/google/android/gms/internal/zzaud$zzd;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   227: pop
      //   228: aload_3
      //   229: monitorexit
      //   230: return
      //   231: astore 4
      //   233: aload_0
      //   234: aload 4
      //   236: invokespecial 88	com/google/android/gms/internal/zzaud$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   239: goto -87 -> 152
      //   242: astore 4
      //   244: aload_3
      //   245: monitorexit
      //   246: aload 4
      //   248: athrow
      //   249: aload_0
      //   250: aload_0
      //   251: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   254: invokestatic 130	com/google/android/gms/internal/zzaud:zze	(Lcom/google/android/gms/internal/zzaud;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   257: if_acmpne +22 -> 279
      //   260: aload_0
      //   261: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   264: aconst_null
      //   265: invokestatic 132	com/google/android/gms/internal/zzaud:zzb	(Lcom/google/android/gms/internal/zzaud;Lcom/google/android/gms/internal/zzaud$zzd;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   268: pop
      //   269: goto -41 -> 228
      //   272: astore 4
      //   274: aload_3
      //   275: monitorexit
      //   276: aload 4
      //   278: athrow
      //   279: aload_0
      //   280: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   283: invokevirtual 47	com/google/android/gms/internal/zzaud:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   286: invokevirtual 135	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
      //   289: ldc -119
      //   291: invokevirtual 140	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
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
      //   311: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   314: invokestatic 130	com/google/android/gms/internal/zzaud:zze	(Lcom/google/android/gms/internal/zzaud;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   317: if_acmpne +22 -> 339
      //   320: aload_0
      //   321: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   324: aconst_null
      //   325: invokestatic 132	com/google/android/gms/internal/zzaud:zzb	(Lcom/google/android/gms/internal/zzaud;Lcom/google/android/gms/internal/zzaud$zzd;)Lcom/google/android/gms/internal/zzaud$zzd;
      //   328: pop
      //   329: goto -223 -> 106
      //   332: astore 4
      //   334: aload_3
      //   335: monitorexit
      //   336: aload 4
      //   338: athrow
      //   339: aload_0
      //   340: getfield 18	com/google/android/gms/internal/zzaud$zzd:zzbtW	Lcom/google/android/gms/internal/zzaud;
      //   343: invokevirtual 47	com/google/android/gms/internal/zzaud:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   346: invokevirtual 135	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
      //   349: ldc -119
      //   351: invokevirtual 140	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
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
    
    public void zzhA()
    {
      synchronized (this.zzbtZ)
      {
        this.zzbtZ.notifyAll();
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaud.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */