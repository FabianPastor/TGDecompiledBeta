package com.google.android.gms.common.api.internal;

abstract class zzbe
{
  private final zzbc zzjg;
  
  protected zzbe(zzbc paramzzbc)
  {
    this.zzjg = paramzzbc;
  }
  
  protected abstract void zzaq();
  
  /* Error */
  public final void zzc(zzbd paramzzbd)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 23	com/google/android/gms/common/api/internal/zzbd:zza	(Lcom/google/android/gms/common/api/internal/zzbd;)Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 28 1 0
    //   9: aload_1
    //   10: invokestatic 32	com/google/android/gms/common/api/internal/zzbd:zzb	(Lcom/google/android/gms/common/api/internal/zzbd;)Lcom/google/android/gms/common/api/internal/zzbc;
    //   13: astore_2
    //   14: aload_0
    //   15: getfield 13	com/google/android/gms/common/api/internal/zzbe:zzjg	Lcom/google/android/gms/common/api/internal/zzbc;
    //   18: astore_3
    //   19: aload_2
    //   20: aload_3
    //   21: if_acmpeq +13 -> 34
    //   24: aload_1
    //   25: invokestatic 23	com/google/android/gms/common/api/internal/zzbd:zza	(Lcom/google/android/gms/common/api/internal/zzbd;)Ljava/util/concurrent/locks/Lock;
    //   28: invokeinterface 35 1 0
    //   33: return
    //   34: aload_0
    //   35: invokevirtual 37	com/google/android/gms/common/api/internal/zzbe:zzaq	()V
    //   38: aload_1
    //   39: invokestatic 23	com/google/android/gms/common/api/internal/zzbd:zza	(Lcom/google/android/gms/common/api/internal/zzbd;)Ljava/util/concurrent/locks/Lock;
    //   42: invokeinterface 35 1 0
    //   47: goto -14 -> 33
    //   50: astore_3
    //   51: aload_1
    //   52: invokestatic 23	com/google/android/gms/common/api/internal/zzbd:zza	(Lcom/google/android/gms/common/api/internal/zzbd;)Ljava/util/concurrent/locks/Lock;
    //   55: invokeinterface 35 1 0
    //   60: aload_3
    //   61: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	zzbe
    //   0	62	1	paramzzbd	zzbd
    //   13	7	2	localzzbc1	zzbc
    //   18	3	3	localzzbc2	zzbc
    //   50	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	19	50	finally
    //   34	38	50	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */