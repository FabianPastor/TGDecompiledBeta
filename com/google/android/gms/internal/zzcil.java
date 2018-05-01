package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.BlockingQueue;

final class zzcil
  extends Thread
{
  private final Object zzjet;
  private final BlockingQueue<zzcik<?>> zzjeu;
  
  public zzcil(String paramString, BlockingQueue<zzcik<?>> paramBlockingQueue)
  {
    zzbq.checkNotNull(paramBlockingQueue);
    Object localObject;
    zzbq.checkNotNull(localObject);
    this.zzjet = new Object();
    this.zzjeu = ((BlockingQueue)localObject);
    setName(paramBlockingQueue);
  }
  
  private final void zza(InterruptedException paramInterruptedException)
  {
    this.zzjeq.zzawy().zzazf().zzj(String.valueOf(getName()).concat(" was interrupted"), paramInterruptedException);
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iload_1
    //   3: ifne +29 -> 32
    //   6: aload_0
    //   7: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   10: invokestatic 82	com/google/android/gms/internal/zzcih:zza	(Lcom/google/android/gms/internal/zzcih;)Ljava/util/concurrent/Semaphore;
    //   13: invokevirtual 87	java/util/concurrent/Semaphore:acquire	()V
    //   16: iconst_1
    //   17: istore_1
    //   18: goto -16 -> 2
    //   21: astore 4
    //   23: aload_0
    //   24: aload 4
    //   26: invokespecial 89	com/google/android/gms/internal/zzcil:zza	(Ljava/lang/InterruptedException;)V
    //   29: goto -27 -> 2
    //   32: invokestatic 95	android/os/Process:myTid	()I
    //   35: invokestatic 99	android/os/Process:getThreadPriority	(I)I
    //   38: istore_2
    //   39: aload_0
    //   40: getfield 31	com/google/android/gms/internal/zzcil:zzjeu	Ljava/util/concurrent/BlockingQueue;
    //   43: invokeinterface 105 1 0
    //   48: checkcast 107	com/google/android/gms/internal/zzcik
    //   51: astore 4
    //   53: aload 4
    //   55: ifnull +91 -> 146
    //   58: aload 4
    //   60: getfield 111	com/google/android/gms/internal/zzcik:zzjes	Z
    //   63: ifeq +77 -> 140
    //   66: iload_2
    //   67: istore_1
    //   68: iload_1
    //   69: invokestatic 115	android/os/Process:setThreadPriority	(I)V
    //   72: aload 4
    //   74: invokevirtual 117	com/google/android/gms/internal/zzcik:run	()V
    //   77: goto -38 -> 39
    //   80: astore 5
    //   82: aload_0
    //   83: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   86: invokestatic 121	com/google/android/gms/internal/zzcih:zzc	(Lcom/google/android/gms/internal/zzcih;)Ljava/lang/Object;
    //   89: astore 4
    //   91: aload 4
    //   93: monitorenter
    //   94: aload_0
    //   95: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   98: invokestatic 82	com/google/android/gms/internal/zzcih:zza	(Lcom/google/android/gms/internal/zzcih;)Ljava/util/concurrent/Semaphore;
    //   101: invokevirtual 124	java/util/concurrent/Semaphore:release	()V
    //   104: aload_0
    //   105: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   108: invokestatic 121	com/google/android/gms/internal/zzcih:zzc	(Lcom/google/android/gms/internal/zzcih;)Ljava/lang/Object;
    //   111: invokevirtual 127	java/lang/Object:notifyAll	()V
    //   114: aload_0
    //   115: aload_0
    //   116: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   119: invokestatic 131	com/google/android/gms/internal/zzcih:zzd	(Lcom/google/android/gms/internal/zzcih;)Lcom/google/android/gms/internal/zzcil;
    //   122: if_acmpne +235 -> 357
    //   125: aload_0
    //   126: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   129: aconst_null
    //   130: invokestatic 134	com/google/android/gms/internal/zzcih:zza	(Lcom/google/android/gms/internal/zzcih;Lcom/google/android/gms/internal/zzcil;)Lcom/google/android/gms/internal/zzcil;
    //   133: pop
    //   134: aload 4
    //   136: monitorexit
    //   137: aload 5
    //   139: athrow
    //   140: bipush 10
    //   142: istore_1
    //   143: goto -75 -> 68
    //   146: aload_0
    //   147: getfield 29	com/google/android/gms/internal/zzcil:zzjet	Ljava/lang/Object;
    //   150: astore 4
    //   152: aload 4
    //   154: monitorenter
    //   155: aload_0
    //   156: getfield 31	com/google/android/gms/internal/zzcil:zzjeu	Ljava/util/concurrent/BlockingQueue;
    //   159: invokeinterface 137 1 0
    //   164: ifnonnull +25 -> 189
    //   167: aload_0
    //   168: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   171: invokestatic 141	com/google/android/gms/internal/zzcih:zzb	(Lcom/google/android/gms/internal/zzcih;)Z
    //   174: istore_3
    //   175: iload_3
    //   176: ifne +13 -> 189
    //   179: aload_0
    //   180: getfield 29	com/google/android/gms/internal/zzcil:zzjet	Ljava/lang/Object;
    //   183: ldc2_w 142
    //   186: invokevirtual 147	java/lang/Object:wait	(J)V
    //   189: aload 4
    //   191: monitorexit
    //   192: aload_0
    //   193: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   196: invokestatic 121	com/google/android/gms/internal/zzcih:zzc	(Lcom/google/android/gms/internal/zzcih;)Ljava/lang/Object;
    //   199: astore 4
    //   201: aload 4
    //   203: monitorenter
    //   204: aload_0
    //   205: getfield 31	com/google/android/gms/internal/zzcil:zzjeu	Ljava/util/concurrent/BlockingQueue;
    //   208: invokeinterface 137 1 0
    //   213: ifnonnull +130 -> 343
    //   216: aload 4
    //   218: monitorexit
    //   219: aload_0
    //   220: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   223: invokestatic 121	com/google/android/gms/internal/zzcih:zzc	(Lcom/google/android/gms/internal/zzcih;)Ljava/lang/Object;
    //   226: astore 4
    //   228: aload 4
    //   230: monitorenter
    //   231: aload_0
    //   232: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   235: invokestatic 82	com/google/android/gms/internal/zzcih:zza	(Lcom/google/android/gms/internal/zzcih;)Ljava/util/concurrent/Semaphore;
    //   238: invokevirtual 124	java/util/concurrent/Semaphore:release	()V
    //   241: aload_0
    //   242: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   245: invokestatic 121	com/google/android/gms/internal/zzcih:zzc	(Lcom/google/android/gms/internal/zzcih;)Ljava/lang/Object;
    //   248: invokevirtual 127	java/lang/Object:notifyAll	()V
    //   251: aload_0
    //   252: aload_0
    //   253: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   256: invokestatic 131	com/google/android/gms/internal/zzcih:zzd	(Lcom/google/android/gms/internal/zzcih;)Lcom/google/android/gms/internal/zzcil;
    //   259: if_acmpne +35 -> 294
    //   262: aload_0
    //   263: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   266: aconst_null
    //   267: invokestatic 134	com/google/android/gms/internal/zzcih:zza	(Lcom/google/android/gms/internal/zzcih;Lcom/google/android/gms/internal/zzcil;)Lcom/google/android/gms/internal/zzcil;
    //   270: pop
    //   271: aload 4
    //   273: monitorexit
    //   274: return
    //   275: astore 5
    //   277: aload_0
    //   278: aload 5
    //   280: invokespecial 89	com/google/android/gms/internal/zzcil:zza	(Ljava/lang/InterruptedException;)V
    //   283: goto -94 -> 189
    //   286: astore 5
    //   288: aload 4
    //   290: monitorexit
    //   291: aload 5
    //   293: athrow
    //   294: aload_0
    //   295: aload_0
    //   296: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   299: invokestatic 150	com/google/android/gms/internal/zzcih:zze	(Lcom/google/android/gms/internal/zzcih;)Lcom/google/android/gms/internal/zzcil;
    //   302: if_acmpne +23 -> 325
    //   305: aload_0
    //   306: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   309: aconst_null
    //   310: invokestatic 152	com/google/android/gms/internal/zzcih:zzb	(Lcom/google/android/gms/internal/zzcih;Lcom/google/android/gms/internal/zzcil;)Lcom/google/android/gms/internal/zzcil;
    //   313: pop
    //   314: goto -43 -> 271
    //   317: astore 5
    //   319: aload 4
    //   321: monitorexit
    //   322: aload 5
    //   324: athrow
    //   325: aload_0
    //   326: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   329: invokevirtual 46	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   332: invokevirtual 155	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   335: ldc -99
    //   337: invokevirtual 160	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   340: goto -69 -> 271
    //   343: aload 4
    //   345: monitorexit
    //   346: goto -307 -> 39
    //   349: astore 5
    //   351: aload 4
    //   353: monitorexit
    //   354: aload 5
    //   356: athrow
    //   357: aload_0
    //   358: aload_0
    //   359: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   362: invokestatic 150	com/google/android/gms/internal/zzcih:zze	(Lcom/google/android/gms/internal/zzcih;)Lcom/google/android/gms/internal/zzcil;
    //   365: if_acmpne +23 -> 388
    //   368: aload_0
    //   369: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   372: aconst_null
    //   373: invokestatic 152	com/google/android/gms/internal/zzcih:zzb	(Lcom/google/android/gms/internal/zzcih;Lcom/google/android/gms/internal/zzcil;)Lcom/google/android/gms/internal/zzcil;
    //   376: pop
    //   377: goto -243 -> 134
    //   380: astore 5
    //   382: aload 4
    //   384: monitorexit
    //   385: aload 5
    //   387: athrow
    //   388: aload_0
    //   389: getfield 15	com/google/android/gms/internal/zzcil:zzjeq	Lcom/google/android/gms/internal/zzcih;
    //   392: invokevirtual 46	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   395: invokevirtual 155	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   398: ldc -99
    //   400: invokevirtual 160	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   403: goto -269 -> 134
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	406	0	this	zzcil
    //   1	142	1	i	int
    //   38	29	2	j	int
    //   174	2	3	bool	boolean
    //   21	4	4	localInterruptedException1	InterruptedException
    //   80	58	5	localObject2	Object
    //   275	4	5	localInterruptedException2	InterruptedException
    //   286	6	5	localObject3	Object
    //   317	6	5	localObject4	Object
    //   349	6	5	localObject5	Object
    //   380	6	5	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   6	16	21	java/lang/InterruptedException
    //   32	39	80	finally
    //   39	53	80	finally
    //   58	66	80	finally
    //   68	77	80	finally
    //   146	155	80	finally
    //   192	204	80	finally
    //   291	294	80	finally
    //   354	357	80	finally
    //   179	189	275	java/lang/InterruptedException
    //   155	175	286	finally
    //   179	189	286	finally
    //   189	192	286	finally
    //   277	283	286	finally
    //   288	291	286	finally
    //   231	271	317	finally
    //   271	274	317	finally
    //   294	314	317	finally
    //   319	322	317	finally
    //   325	340	317	finally
    //   204	219	349	finally
    //   343	346	349	finally
    //   351	354	349	finally
    //   94	134	380	finally
    //   134	137	380	finally
    //   357	377	380	finally
    //   382	385	380	finally
    //   388	403	380	finally
  }
  
  public final void zzrk()
  {
    synchronized (this.zzjet)
    {
      this.zzjet.notifyAll();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */