package com.google.android.gms.internal;

import java.util.concurrent.BlockingQueue;

public final class zzl
  extends Thread
{
  private final zzb zzi;
  private final zzw zzj;
  private volatile boolean zzk = false;
  private final BlockingQueue<zzp<?>> zzw;
  private final zzk zzx;
  
  public zzl(BlockingQueue<zzp<?>> paramBlockingQueue, zzk paramzzk, zzb paramzzb, zzw paramzzw)
  {
    this.zzw = paramBlockingQueue;
    this.zzx = paramzzk;
    this.zzi = paramzzb;
    this.zzj = paramzzw;
  }
  
  public final void quit()
  {
    this.zzk = true;
    interrupt();
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: bipush 10
    //   2: invokestatic 50	android/os/Process:setThreadPriority	(I)V
    //   5: invokestatic 56	android/os/SystemClock:elapsedRealtime	()J
    //   8: lstore_1
    //   9: aload_0
    //   10: getfield 24	com/google/android/gms/internal/zzl:zzw	Ljava/util/concurrent/BlockingQueue;
    //   13: invokeinterface 62 1 0
    //   18: checkcast 64	com/google/android/gms/internal/zzp
    //   21: astore_3
    //   22: aload_3
    //   23: ldc 66
    //   25: invokevirtual 70	com/google/android/gms/internal/zzp:zzb	(Ljava/lang/String;)V
    //   28: aload_3
    //   29: invokevirtual 74	com/google/android/gms/internal/zzp:zzc	()I
    //   32: invokestatic 79	android/net/TrafficStats:setThreadStatsTag	(I)V
    //   35: aload_0
    //   36: getfield 26	com/google/android/gms/internal/zzl:zzx	Lcom/google/android/gms/internal/zzk;
    //   39: aload_3
    //   40: invokeinterface 85 2 0
    //   45: astore 4
    //   47: aload_3
    //   48: ldc 87
    //   50: invokevirtual 70	com/google/android/gms/internal/zzp:zzb	(Ljava/lang/String;)V
    //   53: aload 4
    //   55: getfield 92	com/google/android/gms/internal/zzn:zzz	Z
    //   58: ifeq +55 -> 113
    //   61: aload_3
    //   62: invokevirtual 96	com/google/android/gms/internal/zzp:zzl	()Z
    //   65: ifeq +48 -> 113
    //   68: aload_3
    //   69: ldc 98
    //   71: invokevirtual 100	com/google/android/gms/internal/zzp:zzc	(Ljava/lang/String;)V
    //   74: goto -69 -> 5
    //   77: astore 4
    //   79: aload 4
    //   81: invokestatic 56	android/os/SystemClock:elapsedRealtime	()J
    //   84: lload_1
    //   85: lsub
    //   86: invokevirtual 103	com/google/android/gms/internal/zzaa:zza	(J)V
    //   89: aload_0
    //   90: getfield 30	com/google/android/gms/internal/zzl:zzj	Lcom/google/android/gms/internal/zzw;
    //   93: aload_3
    //   94: aload 4
    //   96: invokeinterface 108 3 0
    //   101: goto -96 -> 5
    //   104: astore_3
    //   105: aload_0
    //   106: getfield 22	com/google/android/gms/internal/zzl:zzk	Z
    //   109: ifeq -104 -> 5
    //   112: return
    //   113: aload_3
    //   114: aload 4
    //   116: invokevirtual 111	com/google/android/gms/internal/zzp:zza	(Lcom/google/android/gms/internal/zzn;)Lcom/google/android/gms/internal/zzt;
    //   119: astore 4
    //   121: aload_3
    //   122: ldc 113
    //   124: invokevirtual 70	com/google/android/gms/internal/zzp:zzb	(Ljava/lang/String;)V
    //   127: aload_3
    //   128: invokevirtual 116	com/google/android/gms/internal/zzp:zzh	()Z
    //   131: ifeq +35 -> 166
    //   134: aload 4
    //   136: getfield 122	com/google/android/gms/internal/zzt:zzae	Lcom/google/android/gms/internal/zzc;
    //   139: ifnull +27 -> 166
    //   142: aload_0
    //   143: getfield 28	com/google/android/gms/internal/zzl:zzi	Lcom/google/android/gms/internal/zzb;
    //   146: aload_3
    //   147: invokevirtual 126	com/google/android/gms/internal/zzp:getUrl	()Ljava/lang/String;
    //   150: aload 4
    //   152: getfield 122	com/google/android/gms/internal/zzt:zzae	Lcom/google/android/gms/internal/zzc;
    //   155: invokeinterface 131 3 0
    //   160: aload_3
    //   161: ldc -123
    //   163: invokevirtual 70	com/google/android/gms/internal/zzp:zzb	(Ljava/lang/String;)V
    //   166: aload_3
    //   167: invokevirtual 135	com/google/android/gms/internal/zzp:zzk	()V
    //   170: aload_0
    //   171: getfield 30	com/google/android/gms/internal/zzl:zzj	Lcom/google/android/gms/internal/zzw;
    //   174: aload_3
    //   175: aload 4
    //   177: invokeinterface 138 3 0
    //   182: goto -177 -> 5
    //   185: astore 4
    //   187: aload 4
    //   189: ldc -116
    //   191: iconst_1
    //   192: anewarray 142	java/lang/Object
    //   195: dup
    //   196: iconst_0
    //   197: aload 4
    //   199: invokevirtual 145	java/lang/Exception:toString	()Ljava/lang/String;
    //   202: aastore
    //   203: invokestatic 150	com/google/android/gms/internal/zzab:zza	(Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)V
    //   206: new 42	com/google/android/gms/internal/zzaa
    //   209: dup
    //   210: aload 4
    //   212: invokespecial 153	com/google/android/gms/internal/zzaa:<init>	(Ljava/lang/Throwable;)V
    //   215: astore 4
    //   217: aload 4
    //   219: invokestatic 56	android/os/SystemClock:elapsedRealtime	()J
    //   222: lload_1
    //   223: lsub
    //   224: invokevirtual 103	com/google/android/gms/internal/zzaa:zza	(J)V
    //   227: aload_0
    //   228: getfield 30	com/google/android/gms/internal/zzl:zzj	Lcom/google/android/gms/internal/zzw;
    //   231: aload_3
    //   232: aload 4
    //   234: invokeinterface 108 3 0
    //   239: goto -234 -> 5
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	242	0	this	zzl
    //   8	215	1	l	long
    //   21	73	3	localzzp	zzp
    //   104	128	3	localInterruptedException	InterruptedException
    //   45	9	4	localzzn	zzn
    //   77	38	4	localzzaa1	zzaa
    //   119	57	4	localzzt	zzt
    //   185	26	4	localException	Exception
    //   215	18	4	localzzaa2	zzaa
    // Exception table:
    //   from	to	target	type
    //   22	74	77	com/google/android/gms/internal/zzaa
    //   113	166	77	com/google/android/gms/internal/zzaa
    //   166	182	77	com/google/android/gms/internal/zzaa
    //   9	22	104	java/lang/InterruptedException
    //   22	74	185	java/lang/Exception
    //   113	166	185	java/lang/Exception
    //   166	182	185	java/lang/Exception
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */