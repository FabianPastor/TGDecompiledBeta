package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;

final class zzu
  implements zzbq
{
  private zzu(zzr paramzzr) {}
  
  /* Error */
  public final void zzb(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   4: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   7: invokeinterface 32 1 0
    //   12: aload_0
    //   13: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   16: invokestatic 36	com/google/android/gms/common/api/internal/zzr:zzc	(Lcom/google/android/gms/common/api/internal/zzr;)Z
    //   19: ifeq +34 -> 53
    //   22: aload_0
    //   23: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   26: iconst_0
    //   27: invokestatic 39	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;Z)Z
    //   30: pop
    //   31: aload_0
    //   32: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   35: iload_1
    //   36: iload_2
    //   37: invokestatic 42	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;IZ)V
    //   40: aload_0
    //   41: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   44: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   47: invokeinterface 45 1 0
    //   52: return
    //   53: aload_0
    //   54: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   57: iconst_1
    //   58: invokestatic 39	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;Z)Z
    //   61: pop
    //   62: aload_0
    //   63: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   66: invokestatic 49	com/google/android/gms/common/api/internal/zzr:zzf	(Lcom/google/android/gms/common/api/internal/zzr;)Lcom/google/android/gms/common/api/internal/zzbd;
    //   69: iload_1
    //   70: invokevirtual 55	com/google/android/gms/common/api/internal/zzbd:onConnectionSuspended	(I)V
    //   73: aload_0
    //   74: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   77: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   80: invokeinterface 45 1 0
    //   85: goto -33 -> 52
    //   88: astore_3
    //   89: aload_0
    //   90: getfield 12	com/google/android/gms/common/api/internal/zzu:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   93: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   96: invokeinterface 45 1 0
    //   101: aload_3
    //   102: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	zzu
    //   0	103	1	paramInt	int
    //   0	103	2	paramBoolean	boolean
    //   88	14	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	40	88	finally
    //   53	73	88	finally
  }
  
  public final void zzb(Bundle paramBundle)
  {
    zzr.zza(this.zzgc).lock();
    try
    {
      zzr.zzb(this.zzgc, ConnectionResult.RESULT_SUCCESS);
      zzr.zzb(this.zzgc);
      return;
    }
    finally
    {
      zzr.zza(this.zzgc).unlock();
    }
  }
  
  public final void zzc(ConnectionResult paramConnectionResult)
  {
    zzr.zza(this.zzgc).lock();
    try
    {
      zzr.zzb(this.zzgc, paramConnectionResult);
      zzr.zzb(this.zzgc);
      return;
    }
    finally
    {
      zzr.zza(this.zzgc).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */