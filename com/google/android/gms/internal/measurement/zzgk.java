package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.BlockingQueue;

final class zzgk
  extends Thread
{
  private final Object zzama;
  private final BlockingQueue<zzgj<?>> zzamb;
  
  public zzgk(String paramString, BlockingQueue<zzgj<?>> paramBlockingQueue)
  {
    Preconditions.checkNotNull(paramBlockingQueue);
    Object localObject;
    Preconditions.checkNotNull(localObject);
    this.zzama = new Object();
    this.zzamb = ((BlockingQueue)localObject);
    setName(paramBlockingQueue);
  }
  
  private final void zza(InterruptedException paramInterruptedException)
  {
    this.zzalx.zzgg().zzin().zzg(String.valueOf(getName()).concat(" was interrupted"), paramInterruptedException);
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iload_1
    //   3: ifne +27 -> 30
    //   6: aload_0
    //   7: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   10: invokestatic 82	com/google/android/gms/internal/measurement/zzgg:zza	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/util/concurrent/Semaphore;
    //   13: invokevirtual 87	java/util/concurrent/Semaphore:acquire	()V
    //   16: iconst_1
    //   17: istore_1
    //   18: goto -16 -> 2
    //   21: astore_2
    //   22: aload_0
    //   23: aload_2
    //   24: invokespecial 89	com/google/android/gms/internal/measurement/zzgk:zza	(Ljava/lang/InterruptedException;)V
    //   27: goto -25 -> 2
    //   30: invokestatic 95	android/os/Process:myTid	()I
    //   33: invokestatic 99	android/os/Process:getThreadPriority	(I)I
    //   36: istore_3
    //   37: aload_0
    //   38: getfield 31	com/google/android/gms/internal/measurement/zzgk:zzamb	Ljava/util/concurrent/BlockingQueue;
    //   41: invokeinterface 105 1 0
    //   46: checkcast 107	com/google/android/gms/internal/measurement/zzgj
    //   49: astore_2
    //   50: aload_2
    //   51: ifnull +86 -> 137
    //   54: aload_2
    //   55: getfield 111	com/google/android/gms/internal/measurement/zzgj:zzalz	Z
    //   58: ifeq +73 -> 131
    //   61: iload_3
    //   62: istore_1
    //   63: iload_1
    //   64: invokestatic 115	android/os/Process:setThreadPriority	(I)V
    //   67: aload_2
    //   68: invokevirtual 117	com/google/android/gms/internal/measurement/zzgj:run	()V
    //   71: goto -34 -> 37
    //   74: astore 4
    //   76: aload_0
    //   77: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   80: invokestatic 121	com/google/android/gms/internal/measurement/zzgg:zzc	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/lang/Object;
    //   83: astore_2
    //   84: aload_2
    //   85: monitorenter
    //   86: aload_0
    //   87: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   90: invokestatic 82	com/google/android/gms/internal/measurement/zzgg:zza	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/util/concurrent/Semaphore;
    //   93: invokevirtual 124	java/util/concurrent/Semaphore:release	()V
    //   96: aload_0
    //   97: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   100: invokestatic 121	com/google/android/gms/internal/measurement/zzgg:zzc	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/lang/Object;
    //   103: invokevirtual 127	java/lang/Object:notifyAll	()V
    //   106: aload_0
    //   107: aload_0
    //   108: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   111: invokestatic 131	com/google/android/gms/internal/measurement/zzgg:zzd	(Lcom/google/android/gms/internal/measurement/zzgg;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   114: if_acmpne +228 -> 342
    //   117: aload_0
    //   118: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   121: aconst_null
    //   122: invokestatic 134	com/google/android/gms/internal/measurement/zzgg:zza	(Lcom/google/android/gms/internal/measurement/zzgg;Lcom/google/android/gms/internal/measurement/zzgk;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   125: pop
    //   126: aload_2
    //   127: monitorexit
    //   128: aload 4
    //   130: athrow
    //   131: bipush 10
    //   133: istore_1
    //   134: goto -71 -> 63
    //   137: aload_0
    //   138: getfield 29	com/google/android/gms/internal/measurement/zzgk:zzama	Ljava/lang/Object;
    //   141: astore_2
    //   142: aload_2
    //   143: monitorenter
    //   144: aload_0
    //   145: getfield 31	com/google/android/gms/internal/measurement/zzgk:zzamb	Ljava/util/concurrent/BlockingQueue;
    //   148: invokeinterface 137 1 0
    //   153: ifnonnull +27 -> 180
    //   156: aload_0
    //   157: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   160: invokestatic 141	com/google/android/gms/internal/measurement/zzgg:zzb	(Lcom/google/android/gms/internal/measurement/zzgg;)Z
    //   163: istore 5
    //   165: iload 5
    //   167: ifne +13 -> 180
    //   170: aload_0
    //   171: getfield 29	com/google/android/gms/internal/measurement/zzgk:zzama	Ljava/lang/Object;
    //   174: ldc2_w 142
    //   177: invokevirtual 147	java/lang/Object:wait	(J)V
    //   180: aload_2
    //   181: monitorexit
    //   182: aload_0
    //   183: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   186: invokestatic 121	com/google/android/gms/internal/measurement/zzgg:zzc	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/lang/Object;
    //   189: astore 4
    //   191: aload 4
    //   193: monitorenter
    //   194: aload_0
    //   195: getfield 31	com/google/android/gms/internal/measurement/zzgk:zzamb	Ljava/util/concurrent/BlockingQueue;
    //   198: invokeinterface 137 1 0
    //   203: ifnonnull +127 -> 330
    //   206: aload 4
    //   208: monitorexit
    //   209: aload_0
    //   210: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   213: invokestatic 121	com/google/android/gms/internal/measurement/zzgg:zzc	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/lang/Object;
    //   216: astore 4
    //   218: aload 4
    //   220: monitorenter
    //   221: aload_0
    //   222: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   225: invokestatic 82	com/google/android/gms/internal/measurement/zzgg:zza	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/util/concurrent/Semaphore;
    //   228: invokevirtual 124	java/util/concurrent/Semaphore:release	()V
    //   231: aload_0
    //   232: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   235: invokestatic 121	com/google/android/gms/internal/measurement/zzgg:zzc	(Lcom/google/android/gms/internal/measurement/zzgg;)Ljava/lang/Object;
    //   238: invokevirtual 127	java/lang/Object:notifyAll	()V
    //   241: aload_0
    //   242: aload_0
    //   243: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   246: invokestatic 131	com/google/android/gms/internal/measurement/zzgg:zzd	(Lcom/google/android/gms/internal/measurement/zzgg;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   249: if_acmpne +34 -> 283
    //   252: aload_0
    //   253: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   256: aconst_null
    //   257: invokestatic 134	com/google/android/gms/internal/measurement/zzgg:zza	(Lcom/google/android/gms/internal/measurement/zzgg;Lcom/google/android/gms/internal/measurement/zzgk;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   260: pop
    //   261: aload 4
    //   263: monitorexit
    //   264: return
    //   265: astore 4
    //   267: aload_0
    //   268: aload 4
    //   270: invokespecial 89	com/google/android/gms/internal/measurement/zzgk:zza	(Ljava/lang/InterruptedException;)V
    //   273: goto -93 -> 180
    //   276: astore 4
    //   278: aload_2
    //   279: monitorexit
    //   280: aload 4
    //   282: athrow
    //   283: aload_0
    //   284: aload_0
    //   285: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   288: invokestatic 150	com/google/android/gms/internal/measurement/zzgg:zze	(Lcom/google/android/gms/internal/measurement/zzgg;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   291: if_acmpne +21 -> 312
    //   294: aload_0
    //   295: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   298: aconst_null
    //   299: invokestatic 152	com/google/android/gms/internal/measurement/zzgg:zzb	(Lcom/google/android/gms/internal/measurement/zzgg;Lcom/google/android/gms/internal/measurement/zzgk;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   302: pop
    //   303: goto -42 -> 261
    //   306: astore_2
    //   307: aload 4
    //   309: monitorexit
    //   310: aload_2
    //   311: athrow
    //   312: aload_0
    //   313: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   316: invokevirtual 46	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   319: invokevirtual 155	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   322: ldc -99
    //   324: invokevirtual 160	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   327: goto -66 -> 261
    //   330: aload 4
    //   332: monitorexit
    //   333: goto -296 -> 37
    //   336: astore_2
    //   337: aload 4
    //   339: monitorexit
    //   340: aload_2
    //   341: athrow
    //   342: aload_0
    //   343: aload_0
    //   344: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   347: invokestatic 150	com/google/android/gms/internal/measurement/zzgg:zze	(Lcom/google/android/gms/internal/measurement/zzgg;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   350: if_acmpne +22 -> 372
    //   353: aload_0
    //   354: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   357: aconst_null
    //   358: invokestatic 152	com/google/android/gms/internal/measurement/zzgg:zzb	(Lcom/google/android/gms/internal/measurement/zzgg;Lcom/google/android/gms/internal/measurement/zzgk;)Lcom/google/android/gms/internal/measurement/zzgk;
    //   361: pop
    //   362: goto -236 -> 126
    //   365: astore 4
    //   367: aload_2
    //   368: monitorexit
    //   369: aload 4
    //   371: athrow
    //   372: aload_0
    //   373: getfield 15	com/google/android/gms/internal/measurement/zzgk:zzalx	Lcom/google/android/gms/internal/measurement/zzgg;
    //   376: invokevirtual 46	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   379: invokevirtual 155	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   382: ldc -99
    //   384: invokevirtual 160	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   387: goto -261 -> 126
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	390	0	this	zzgk
    //   1	133	1	i	int
    //   21	3	2	localInterruptedException1	InterruptedException
    //   49	230	2	localObject1	Object
    //   306	5	2	localObject2	Object
    //   336	32	2	localObject3	Object
    //   36	26	3	j	int
    //   74	55	4	localObject4	Object
    //   265	4	4	localInterruptedException2	InterruptedException
    //   276	62	4	localObject6	Object
    //   365	5	4	localObject7	Object
    //   163	3	5	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   6	16	21	java/lang/InterruptedException
    //   30	37	74	finally
    //   37	50	74	finally
    //   54	61	74	finally
    //   63	71	74	finally
    //   137	144	74	finally
    //   182	194	74	finally
    //   280	283	74	finally
    //   340	342	74	finally
    //   170	180	265	java/lang/InterruptedException
    //   144	165	276	finally
    //   170	180	276	finally
    //   180	182	276	finally
    //   267	273	276	finally
    //   278	280	276	finally
    //   221	261	306	finally
    //   261	264	306	finally
    //   283	303	306	finally
    //   307	310	306	finally
    //   312	327	306	finally
    //   194	209	336	finally
    //   330	333	336	finally
    //   337	340	336	finally
    //   86	126	365	finally
    //   126	128	365	finally
    //   342	362	365	finally
    //   367	369	365	finally
    //   372	387	365	finally
  }
  
  public final void zzjj()
  {
    synchronized (this.zzama)
    {
      this.zzama.notifyAll();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzgk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */